package edu.mit.compilers.grammar.regular;

import java.security.InvalidAlgorithmParameterException;

public class RegularSingleCharGraph extends RegularGraph {
    private final char ch;

    public RegularSingleCharGraph(char ch) {
        breakSourceAndDest();
        try {
            source.addDetermined(ch, dest);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
        this.ch = ch;
    }

    public char getChar() {
        return ch;
    }
}
