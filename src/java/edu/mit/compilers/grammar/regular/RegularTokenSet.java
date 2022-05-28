package edu.mit.compilers.grammar.regular;

import java.io.*;
import java.util.*;

public class RegularTokenSet {
    private final List<RegularGraph> regularGraphList;
    private final Map<Integer, String> tokenMap;

    public RegularTokenSet(Map<Integer, String> tokenMap, List<RegularGraph> graphList) {
        this.tokenMap = tokenMap;
        this.regularGraphList = graphList;
    }

    public static RegularTokenSet newByDestNodeMap(Map<RegularNode, String> destNodeMap, List<RegularGraph> graphList) {
        Map<Integer, String> tokenMap = new HashMap<>();
        for (var pair : destNodeMap.entrySet()) {
            tokenMap.put(pair.getKey().getId(), pair.getValue());
        }
        return new RegularTokenSet(tokenMap, graphList);
    }

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

    public class Iterator {
        private final List<RegularGraph.Iterator> iteratorList;

        public Iterator() {
            iteratorList = new ArrayList<>();
            for (var graph : regularGraphList) {
                iteratorList.add(graph.iterator());
            }
        }

        private Iterator(List<RegularGraph.Iterator> iteratorList) {
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

        public Iterator clone() {
            ArrayList<RegularGraph.Iterator> newIteratorList = new ArrayList<>();
            for (var graphIterator : iteratorList) {
                newIteratorList.add(graphIterator.clone());
            }
            return new Iterator(newIteratorList);
        }
    }

    public Iterator iterator() {
        return new Iterator();
    }

    public LinkedList<String> match(char[] input) {
        var iter = iterator();
        LinkedList<String> tokenName = new LinkedList<>();
        for (char c : input) {
            tokenName.addAll(iter.next(c));
        }
        return tokenName;
    }

    private HashSet<MatchInfo> matchLongest(char[] input, Iterator iterator, int cur) {
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
            throw new InputMismatchException();
        }
        int right = left;
        while (right < line.length()) {
            if (line.charAt(left) != ' ') {
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
            if (line.charAt(left) != ' ') {
                ++right;
            } else {
                break;
            }
        }
        if (line.charAt(right) != '}') {
            throw new InputMismatchException();
        }
        return Map.entry(tokenName, line.substring(left + 1, right - 1));
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
            result.add(parseMappingLine(line));
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
