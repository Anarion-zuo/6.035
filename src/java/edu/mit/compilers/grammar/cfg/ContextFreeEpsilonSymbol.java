package edu.mit.compilers.grammar.cfg;

public class ContextFreeEpsilonSymbol extends ContextFreeSymbol {
    @Override
    public MatchInfo match(ContextFreeSentence.Iterator symbolIterator) {
        return new MatchInfo(symbolIterator, null, 0);
    }
}
