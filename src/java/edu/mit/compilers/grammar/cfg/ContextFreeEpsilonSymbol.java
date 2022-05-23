package edu.mit.compilers.grammar.cfg;

import java.security.InvalidAlgorithmParameterException;
import java.util.List;

public class ContextFreeEpsilonSymbol extends ContextFreeSymbol {
    @Override
    public Object afterMatch(int sentenceIndex, ContextFreeSentence matchedSentence, List<Object> childAttributes) throws InvalidAlgorithmParameterException {
        return null;
    }

    @Override
    public MatchInfo match(ContextFreeSentence.Iterator symbolIterator) {
        try {
            return new MatchInfo(symbolIterator, null, 0, afterMatch(-1, null, null));
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }
}
