package edu.mit.compilers.grammar.regular;

import java.util.*;

public class RegularGraph {
    private final RegularNode source;
    private final RegularNode dest;

    public RegularGraph() {
        dest = new RegularNode();
        source = new RegularNode();
        source.addNonDetermined(dest);
    }

    public static RegularGraph parseRegex(char[] input) {
        var regularSymbolUtil = new RegularSymbolUtil();
        var info = regularSymbolUtil.rootMatchExaust(input);
        if (info.nextIterator == null) {
            return null;
        }
        return (RegularGraph) info.object;
    }

    public boolean isDest(RegularNode node) {
        return node == dest;
    }

    public RegularNode getSource() {
        return source;
    }

    public RegularNode getDest() {
        return dest;
    }

    public void breakSourceAndDest() {
        source.removeNondetermined(dest);
    }

    public int getDestId() {
        return dest.getId();
    }

    public class Iterator {
        private HashSet<RegularNode> nodes = new HashSet<>();
        private int nextCount = 0;

        public Iterator(RegularNode source) {
            nodes.add(source);
        }

        public int getDestId() {
            return dest.getId();
        }

        private void moveNodesThroughEpsilon() {
            HashSet<RegularNode> l1 = nodes, l2 = new HashSet<>();
            while (true) {
                boolean ended = true;
                for (var curNode : l1) {
                    var nonters = curNode.getNondetermined();
                    if (nonters.isEmpty()) {
                        l2.add(curNode);
                    } else {
                        ended = false;
                        l2.addAll(nonters);
                    }
                }
                l1.clear();
                var lt = l1;
                l1 = l2;
                l2 = lt;
                if (ended) {
                    nodes = l1;
                    return;
                }
            }
        }

        private void moveNodesWithCharInput(char ch) {
            moveNodesThroughEpsilon();
            HashSet<RegularNode> l2 = new HashSet<>();
            for (var curNode : nodes) {
                var nextNode = curNode.getDetermined(ch);
                if (nextNode != null) {
                    l2.add(nextNode);
                }
            }
            nodes = l2;
            moveNodesThroughEpsilon();
        }

        public final int getNextCount() {
            return nextCount;
        }

        public boolean hasMatch() {
            moveNodesThroughEpsilon();
            return nodes.contains(dest);
        }

        public RegularNode getDest() {
            return dest;
        }

        public boolean hasNext() {
            moveNodesThroughEpsilon();
            return !(nodes.size() == 1 && nodes.contains(dest));
        }

        public boolean invalid() {
            return nodes.isEmpty();
        }

        public void next(char ch) {
            ++nextCount;
            moveNodesWithCharInput(ch);
        }
    }

    public Iterator iterator() {
        return new Iterator(source);
    }
}
