package edu.mit.compilers.grammar.token.decaf;

import java.util.HashSet;
import java.util.List;

public class Newline extends DecafToken {
    public Newline(String matchedText) {
        super(matchedText);
    }

    private static final HashSet<Character> newLineCharacters = new HashSet<>(List.of('\n', '\r'));

    @Override
    protected String inspect() {
        if (matchedText.length() != 1) {
            matched = false;
            return String.format("expecting new line, got %d characters", matchedText.length());
        }
        if (!newLineCharacters.contains(matchedText.charAt(0))) {
            matched = false;
            return String.format("unexpected char 0x%x", (int) matchedText.charAt(0));
        }
        matched = true;
        return "";
    }

    @Override
    protected String getTokenName() {
        return "NEWLINE";
    }

    @Override
    protected String getTokenAttributeContent() {
        return "'\\n'";
    }

    @Override
    public boolean shouldIgnore() {
        return true;
    }
}
