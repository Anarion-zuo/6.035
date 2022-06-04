package edu.mit.compilers.grammar.regular;

import edu.mit.compilers.grammar.token.DecafTokenFactory;
import edu.mit.compilers.grammar.token.Token;
import edu.mit.compilers.grammar.token.decaf.DecafToken;
import edu.mit.compilers.grammar.token.decaf.UndefinedTokenException;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

public class RegularTokenSet {
    private final List<RegularGraph> regularGraphList;
    private final Map<Integer, String> tokenMap;

    public RegularTokenSet(Map<Integer, String> tokenMap, List<RegularGraph> graphList) {
        this.tokenMap = tokenMap;
        this.regularGraphList = graphList;
    }

    /**
     * Build token set by list of node mapping
     * @param destNodeMap mapping from destination node to token name
     * @param graphList regex graphs
     * @return
     */
    public static RegularTokenSet newByDestNodeMap(Map<RegularNode, String> destNodeMap, List<RegularGraph> graphList) {
        Map<Integer, String> tokenMap = new HashMap<>();
        for (var pair : destNodeMap.entrySet()) {
            tokenMap.put(pair.getKey().getId(), pair.getValue());
        }
        return new RegularTokenSet(tokenMap, graphList);
    }

    /**
     * Build token set by list of mapping
     * @param tokenEntryList mapping from regex to token name
     * @return
     */
    public static RegularTokenSet newByRegexList(List<Map.Entry<String, String>> tokenEntryList) {
        RegularSymbolUtil regularSymbolUtil = new RegularSymbolUtil();
        HashMap<RegularNode, String> destTokenMap = new HashMap<>();
        ArrayList<RegularGraph> graphList = new ArrayList<>();
        for (var pair : tokenEntryList) {
            char[] regex = pair.getKey().toCharArray();
            var info = regularSymbolUtil.rootMatchExaust(regex);
            RegularGraph graph = (RegularGraph) info.afterAttribute;
            destTokenMap.put(graph.getDest(), pair.getValue());
            graphList.add(graph);
        }
        return newByDestNodeMap(destTokenMap, graphList);
    }

    /*public void addGraph(RegularGraph graph) {
        regularGraphList.add(graph);
    }*/

    public static class MatchInfo {
        public final int length;
        public final String tokenName;

        public MatchInfo(int length, String tokenName) {
            this.length = length;
            this.tokenName = tokenName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MatchInfo matchInfo = (MatchInfo) o;
            return tokenName.equals(matchInfo.tokenName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tokenName);
        }
    }

    public class SingleTokenIterator {
        private final List<RegularGraph.Iterator> iteratorList;

        public SingleTokenIterator() {
            iteratorList = new ArrayList<>();
            for (var graph : regularGraphList) {
                iteratorList.add(graph.iterator());
            }
        }

        private SingleTokenIterator(List<RegularGraph.Iterator> iteratorList) {
            this.iteratorList = iteratorList;
        }

        public LinkedList<String> next(char ch) {
            LinkedList<String> result = new LinkedList<>();
            for (var iter : iteratorList) {
                if (iter.invalid()) {
                    continue;
                }
                iter.next(ch);
                if (iter.hasMatch()) {
                    var tokenName = tokenMap.get(iter.getDestId());
                    assert tokenName != null;
                    result.add(tokenName);
                }
            }
            return result;
        }

        public LinkedList<MatchInfo> mightMatch() {
            LinkedList<MatchInfo> result = new LinkedList<>();
            for (var iter : iteratorList) {
                iter.moveNodesThroughEpsilon();
                if (iter.invalid()) {
                    continue;
                }
                var tokenName = tokenMap.get(iter.getDestId());
                result.add(new MatchInfo(iter.getNextCount(), tokenName));
            }
            return result;
        }

        public String mostLikely() {
            int maxNextCount = 0;
            String result = null;
            for (var iter : iteratorList) {
                if (maxNextCount < iter.getNextCount()) {
                    maxNextCount = iter.getNextCount();
                    result = tokenMap.get(iter.getDestId());
                }
            }
            return result;
        }

        public SingleTokenIterator clone() {
            ArrayList<RegularGraph.Iterator> newIteratorList = new ArrayList<>();
            for (var graphIterator : iteratorList) {
                newIteratorList.add(graphIterator.clone());
            }
            return new SingleTokenIterator(newIteratorList);
        }
    }

    public SingleTokenIterator iterator() {
        return new SingleTokenIterator();
    }

    public LinkedList<String> match(char[] input) {
        var iter = iterator();
        LinkedList<String> tokenName = new LinkedList<>();
        for (char c : input) {
            tokenName.addAll(iter.next(c));
        }
        return tokenName;
    }

    private HashSet<MatchInfo> matchLongest(char[] input, SingleTokenIterator iterator, int cur) {
        if (cur == input.length) {
            return new HashSet<>();
        }
        // feed more characters to the algorithm
        // see if there are longer match
        var noCur = matchLongest(input, iterator.clone(), cur + 1);
        var cloneIterator = iterator.clone();
        cloneIterator.next(input[cur]);
        var matched = cloneIterator.mightMatch();
        var takeCur = matchLongest(input, cloneIterator, cur + 1);

        int maxLength = 0;
        for (MatchInfo info : matched) {
            maxLength = Math.max(maxLength, info.length);
        }
        for (MatchInfo info : noCur) {
            maxLength = Math.max(maxLength, info.length);
        }
        for (MatchInfo info : takeCur) {
            maxLength = Math.max(maxLength, info.length);
        }
        HashSet<MatchInfo> result = new HashSet<>();
        for (MatchInfo info : matched) {
            if (info.length == maxLength) {
                result.add(info);
            }
        }
        for (MatchInfo info : noCur) {
            if (info.length == maxLength) {
                result.add(info);
            }
        }
        for (MatchInfo info : takeCur) {
            if (info.length == maxLength) {
                result.add(info);
            }
        }

        return result;
    }

    public LinkedList<MatchInfo> matchLongest(char[] input) {
        return new LinkedList<>(matchLongest(input, iterator(), 0));
    }

    public LinkedList<MatchInfo> matchLongest(String input) {
        return matchLongest(input.toCharArray());
    }

    public class StreamIterator {
        private final int leftIndex, limitIndex;
        private final ByteBuffer buffer;
        private int curIndex = -1;
        private static class IteratorInfo {
            final SingleTokenIterator singleIterator;
            final int beginIndex;

            public IteratorInfo(SingleTokenIterator singleIterator, int beginIndex) {
                this.singleIterator = singleIterator;
                this.beginIndex = beginIndex;
            }
        }
        private LinkedList<IteratorInfo> iterators = new LinkedList<>();

        private StreamIterator(int leftIndex, int limitIndex, ByteBuffer buffer) {
            this.leftIndex = leftIndex;
            this.limitIndex = limitIndex;
            this.buffer = buffer;
            this.curIndex = leftIndex;
            this.iterators.add(new IteratorInfo(
                    new SingleTokenIterator(), this.curIndex
            ));
        }

        protected void next(char ch) {
            var newIterators = new LinkedList<IteratorInfo>();
            for (var info : iterators) {
                //newIterators.add(new IteratorInfo(iter, info.beginIndex));
                var nextIter = info.singleIterator.clone();
                nextIter.next(ch);
                newIterators.add(new IteratorInfo(nextIter, info.beginIndex));
            }
            iterators = newIterators;
            ++curIndex;
            if (iterators.isEmpty()) {
                iterators.add(new IteratorInfo(new SingleTokenIterator(), curIndex));
            }
        }

        public boolean next() {
            if (curIndex >= limitIndex) {
                return false;
            }
            // absolute get
            char ch = (char) buffer.get(curIndex);
            next(ch);
            return true;
        }

        private static class IteratorMatchInfo {
            final String tokenName;
            final int beginIndex;

            public IteratorMatchInfo(String tokenName, int beginIndex) {
                this.tokenName = tokenName;
                this.beginIndex = beginIndex;
            }
        }
        public LinkedList<Token> getMatched() {
            LinkedList<IteratorMatchInfo> candidates = new LinkedList<>();
            int maxLength = 0;
            for (var info : iterators) {
                var singleIter = info.singleIterator;
                for (var graphIter : singleIter.mightMatch()) {
                    if (maxLength < graphIter.length) {
                        candidates.clear();
                        maxLength = graphIter.length;
                        candidates.add(new IteratorMatchInfo(graphIter.tokenName, info.beginIndex));
                    } else if (maxLength == graphIter.length) {
                        candidates.add(new IteratorMatchInfo(graphIter.tokenName, info.beginIndex));
                    } else {
                        // do nothing
                    }
                }
            }
            LinkedList<Token> result = new LinkedList<>();
            for (var info : candidates) {
                byte[] matchedChar = new byte[curIndex - info.beginIndex];
                // relative get
                buffer.position(info.beginIndex).get(matchedChar, 0, curIndex - info.beginIndex);
                DecafToken token = null;
                try {
                    token = DecafTokenFactory.getInstance().makeToken(
                            info.tokenName,
                            new String(matchedChar)
                            );
                    if (token.isMatched()) {
                        result.add(token);
                    }
                } catch (UndefinedTokenException ignore) {

                }
            }
            if (!result.isEmpty()) {
                //iterators.clear();
                iterators.add(new IteratorInfo(new SingleTokenIterator(), curIndex));
            }
            return result;
        }

        public void acceptMatched() {
            iterators.clear();
            iterators.add(new IteratorInfo(new SingleTokenIterator(), curIndex));
        }
    }


    public StreamIterator streamIterator(int leftIndex, int limitIndex, ByteBuffer buffer) {
        return new StreamIterator(leftIndex, limitIndex, buffer);
    }

    private static Map.Entry<String, String> parseMappingLine(String line) {
        int left = 0;
        while (left < line.length()) {
            if (line.charAt(left) == ' ') {
                ++left;
            } else {
                break;
            }
        }
        if (left == line.length()) {
            // empty line
            return null;
            //throw new InputMismatchException();
        }
        if (line.charAt(left) == '#') {
            // comment line
            return null;
        }
        int right = left;
        while (right < line.length()) {
            if (line.charAt(right) != ' ') {
                ++right;
            } else {
                break;
            }
        }
        if (right == line.length()) {
            throw new InputMismatchException();
        }
        String tokenName = line.substring(left, right);
        left = right;
        while (left < line.length()) {
            if (line.charAt(left) == ' ') {
                ++left;
            } else {
                break;
            }
        }
        if (left == line.length()) {
            throw new InputMismatchException();
        }
        if (line.charAt(left) != '{') {
            throw new InputMismatchException();
        }
        right = left;
        while (right < line.length()) {
            if (line.charAt(right) != '}') {
                ++right;
            } else {
                break;
            }
        }
        if (line.charAt(right) != '}') {
            throw new InputMismatchException();
        }
        return Map.entry(line.substring(left + 1, right), tokenName);
    }
    private static List<Map.Entry<String, String>> getRegexTokenListFromFile(File file) throws IOException {
        ArrayList<Map.Entry<String, String>> result = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        while (true) {
            line = reader.readLine();
            if (line == null) {
                break;
            }
            var parsedLine = parseMappingLine(line);
            if (parsedLine != null) {
                result.add(parsedLine);
            }
        }
        reader.close();
        return result;
    }

    public static RegularTokenSet newByConfigFile(File file) {
        List<Map.Entry<String, String>> list = null;
        try {
            list = getRegexTokenListFromFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return newByRegexList(list);
    }
}
