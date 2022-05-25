package edu.mit.compilers.grammar.regular;

public class RegularExceptGraph extends RegularGraph {
    public RegularExceptGraph(RegularGraph innerGraph) {
        super(innerGraph.source, innerGraph.error, innerGraph.dest);
    }

    @Override
    public Iterator iterator() {
        return super.exceptIterator();
    }
}
