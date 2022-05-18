package edu.mit.compilers.grammar.regular;

import edu.mit.compilers.grammar.token.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegularTokenSet {
    private final List<RegularGraph> regularGraphList;
    private final Map<Integer, Token> tokenMap;

    public RegularTokenSet(Map<Integer, Token> tokenMap, List<RegularGraph> graphList) {
        this.tokenMap = tokenMap;
        this.regularGraphList = graphList;
    }

    public static RegularTokenSet newByDestNodeMap(Map<RegularNode, Token> destNodeMap, List<RegularGraph> graphList) {
        Map<Integer, Token> tokenMap = new HashMap<>();
        for (var pair : destNodeMap.entrySet()) {
            tokenMap.put(pair.getKey().getId(), pair.getValue());
        }
        return new RegularTokenSet(tokenMap, graphList);
    }

    public static RegularTokenSet newByRegexList(List<Map.Entry<String, Token>> tokenEntryList) {
        RegularSymbolUtil regularSymbolUtil = new RegularSymbolUtil();
        HashMap<RegularNode, Token> destTokenMap = new HashMap<>();
        ArrayList<RegularGraph> graphList = new ArrayList<>();
        for (var pair : tokenEntryList) {
            char[] regex = pair.getKey().toCharArray();
            var info = regularSymbolUtil.rootMatchExaust(regex);
            RegularGraph graph = (RegularGraph) info.object;
            destTokenMap.put(graph.getDest(), pair.getValue());
            graphList.add(graph);
        }
        return newByDestNodeMap(destTokenMap, graphList);
    }

    /*public void addGraph(RegularGraph graph) {
        regularGraphList.add(graph);
    }*/

    public class Iterator {
        private final List<RegularGraph.Iterator> iteratorList = new ArrayList<>();

        public Iterator() {
            for (var graph : regularGraphList) {
                iteratorList.add(graph.iterator());
            }
        }

        public Token next(char ch) {
            for (var iter : iteratorList) {
                if (iter.invalid()) {
                    continue;
                }
                iter.next(ch);
                if (iter.hasMatch()) {
                    var result = tokenMap.get(iter.getDestId());
                    assert result != null;
                    return result;
                }
            }
            return null;
        }
    }

    public Iterator iterator() {
        return new Iterator();
    }

    public Token match(char[] input) {
        var iter = iterator();
        Token token = null;
        for (char c : input) {
            token = iter.next(c);
        }
        if (token != null) {
            token.setAttribute(input);
        }
        return token;
    }
}
