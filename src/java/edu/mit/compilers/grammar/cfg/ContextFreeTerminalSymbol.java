package edu.mit.compilers.grammar.cfg;

import java.security.InvalidAlgorithmParameterException;
import java.util.List;

public abstract class ContextFreeTerminalSymbol extends ContextFreeSymbol {
    public ContextFreeTerminalSymbol() {
        super((List<ContextFreeSentence>) null);
    }

    public abstract Object getTerminalObject();
    
    @Override
    public MatchInfo match(ContextFreeSentence.Iterator symbolIterator) {
        if (symbolIterator.hasNext()) {
            var rhs = (ContextFreeTerminalSymbol) symbolIterator.next();
            if (this.equals(rhs)) {
                try {
                    return new MatchInfo(symbolIterator, new ContextFreeSentence(List.of(this)), 1, afterMatch(-1, null, List.of(rhs.getTerminalObject())));
                } catch (InvalidAlgorithmParameterException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return new MatchInfo(null, null, 0, null);
    }
}
