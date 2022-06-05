package edu.mit.compilers.grammar.token.decaf;

import java.util.HashSet;
import java.util.List;

public class Identifier extends DecafToken {
    public Identifier(String matchedText) {
        super(matchedText);
    }

    private static final HashSet<Character> unAllowedCharacters = new HashSet<>(List.of(
            '+', '-', '*', '/', ',', ';', '?', '^', ' ', '\'', '\"', '\r', '\n', '.', '\\', '\t'
    ));

    @Override
    protected String inspect() {
        String msg = "invalid identifier";

        if (matchedText.length() == 0) {
            matched = false;
            return "expecting identifier, got empty string";
        }
        // check first char
        char firstChar = matchedText.charAt(0);
        boolean isIdentifier = true;
        // not a number
        if (firstChar <= '9' && firstChar >= '0') {
            isIdentifier = false;
        }
        // the rest of the chars
        for (int i = 0; i < matchedText.length(); ++i) {
            // no special char
            if (unAllowedCharacters.contains(matchedText.charAt(i))) {
                isIdentifier = false;
                break;
            }
            if (!isUtf8Viewable(matchedText.charAt(i))) {
                isIdentifier = false;
                break;
            }
        }

        // reserved word
        if (isReservedWord(matchedText)) {
            matched = false;
            return matchedText + " is a reserved word, cannot be an identifier";
        }

        if (isIdentifier) {
            matched = true;
            return "";
        }
        matched = false;
        return msg;
    }

    @Override
    protected String getTokenName() {
        return "IDENTIFIER";
    }

    @Override
    protected String getTokenAttributeContent() {
        return matchedText;
    }
}
