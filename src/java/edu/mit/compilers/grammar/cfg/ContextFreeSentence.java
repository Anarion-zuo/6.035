package edu.mit.compilers.grammar.cfg;

import java.util.*;

public class ContextFreeSentence {
    private final List<ContextFreeSymbol> symbols;

    public ContextFreeSentence(List<ContextFreeSymbol> symbols) {
        this.symbols = symbols;
    }

    public ContextFreeSentence(ContextFreeSymbol... symbols) {
        this(Arrays.stream(symbols).toList());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        for (var symbol : symbols) {
            builder.append(symbol.toString());
            builder.append(", ");
        }
        builder.append(']');
        return builder.toString();
    }

    public void setSymbol(int index, ContextFreeSymbol symbol) {
        symbols.set(index, symbol);
    }

    public class Iterator implements java.util.Iterator<ContextFreeSymbol> {
        private int index;

        public Iterator(int index) {
            this.index = index;
        }

        public Iterator clone() throws CloneNotSupportedException {
            return new Iterator(index);
        }

        public boolean hasNext() {
            return index < symbols.size();
        }

        public ContextFreeSymbol next() {
            int oldIndex = index;
            index++;
            return symbols.get(oldIndex);
        }

        public boolean hasPrevious() {
            return index >= 0;
        }

        public ContextFreeSymbol previous() {
            int oldIndex = index;
            index--;
            return symbols.get(oldIndex);
        }
    }

    public Iterator iterator() {
        return new Iterator(0);
    }
}
