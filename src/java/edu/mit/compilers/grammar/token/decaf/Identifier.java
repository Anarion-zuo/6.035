package edu.mit.compilers.grammar.token.decaf;

import java.util.List;

public class Identifier extends DecafToken {
    public Identifier(String matchedText) {
        super(matchedText);
    }

    @Override
    public String inspect() {
        String msg = "invalid identifier";

        // check first char
        char firstChar = matchedText.charAt(0);
        boolean isIdentifier = true;
        // not a number
        if (firstChar <= '9' && firstChar >= '0') {
            isIdentifier = false;
        }
        // not special char
        if (List.of('+', '-', '*', '/', ',', ';', '?', '^').contains(firstChar)) {
            isIdentifier = false;
        }

        if (isIdentifier) {
            matched = true;
            return "";
        }
        matched = false;
        return "invalid identifier";
    }
}
