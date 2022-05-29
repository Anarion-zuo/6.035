package edu.mit.compilers.grammar.regular;

import java.util.*;

public class RegularGraph {
    protected final RegularNode source;
    protected final RegularNode dest;
    protected final RegularNode error;

    public RegularGraph() {
        error = new RegularNode(null);
        dest = new RegularNode(error);
        source = new RegularNode(error);
        source.addNonDetermined(dest);
    }

    public RegularGraph(RegularNode source, RegularNode dest, RegularNode error) {
        this.dest = dest;
        this.source = source;
        this.error = error;
    }

    public static RegularGraph parseRegex(char[] input) {
        var regularSymbolUtil = new RegularSymbolUtil();
        var info = regularSymbolUtil.rootMatchExaust(input);
        if (info.nextIterator == null) {
            return null;
        }
        return (RegularGraph) info.afterAttribute;
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

    public RegularNode getError() {
        return error;
    }

    public void breakSourceAndDest() {
        source.removeNondetermined(dest);
    }

    public int getDestId() {
        return dest.getId();
    }

    public class Iterator {
        protected HashSet<RegularNode> nodes = new HashSet<>();
        private int nextCount = 0;

        public Iterator(RegularNode source) {
            nodes.add(source);
        }

        public int getDestId() {
            return dest.getId();
        }

        private Iterator(HashSet<RegularNode> nodes, int nextCount) {
            this.nodes = nodes;
            this.nextCount = nextCount;
        }

        public Iterator clone() {
            var newNodes = new HashSet<RegularNode>(nodes);
            var newNextCount = nextCount;
            return new Iterator(newNodes, newNextCount);
        }

        protected void moveNodesThroughEpsilon() {
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
                } else {
                    l2.add(error);
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
            return nodes.isEmpty() || (nodes.size() == 1 && nodes.contains(error));
        }

        public void next(char ch) {
            ++nextCount;
            moveNodesWithCharInput(ch);
        }
    }

    public class ExceptIterator extends Iterator {

        public ExceptIterator(RegularNode source) {
            super(source);
        }

        @Override
        public boolean hasMatch() {
            moveNodesThroughEpsilon();
            return nodes.contains(error);
        }

        @Override
        public boolean hasNext() {
            moveNodesThroughEpsilon();
            return !(nodes.size() == 1 && nodes.contains(error));
        }

        @Override
        public boolean invalid() {
            return nodes.isEmpty() || (nodes.size() == 1 && nodes.contains(dest));
        }
    }

    public Iterator iterator() {
        return new Iterator(source);
    }
    public ExceptIterator exceptIterator() {
        return new ExceptIterator(source);
    }
}
