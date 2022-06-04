package edu.mit.compilers.grammar.token.decaf;

import java.util.HashSet;
import java.util.List;

public class Whitespace extends DecafToken {
    public Whitespace(String matchedText) {
        super(matchedText);
    }

    private static final HashSet<Character> whitespaceCharacters = new HashSet<>(List.of(
            ' ', '\t', '\n', '\r'
    ));

    @Override
    protected String inspect() {
        for (int i = 0; i < matchedText.length(); ++i) {
            if (!whitespaceCharacters.contains(matchedText.charAt(i))) {
                matched = false;
                return String.format("unexpected char 0x%x", (int) matchedText.charAt(i));
            }
        }
        matched = true;
        return "";
    }

    @Override
    protected String getTokenName() {
        return "WHITESPACE";
    }

    @Override
    protected String getTokenAttributeContent() {
        return "EMPTY_SPACES";
    }
}
