package edu.mit.compilers.grammar.cfg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class ContextFreeTerminalSymbol extends ContextFreeSymbol {
    public ContextFreeTerminalSymbol() {
        super((List<ContextFreeSentence>) null);
    }
    
    @Override
    public MatchInfo match(ContextFreeSentence.Iterator symbolIterator) {
        if (symbolIterator.hasNext()) {
            var rhs = symbolIterator.next();
            if (this.equals(rhs)) {
                return new MatchInfo(symbolIterator, new ContextFreeSentence(Arrays.asList(this)));
            }
        }
        return new MatchInfo(null, null);
    }
}
