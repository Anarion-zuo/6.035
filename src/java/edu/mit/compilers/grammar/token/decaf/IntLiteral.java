package edu.mit.compilers.grammar.token.decaf;

public class IntLiteral extends DecafToken {
    private int parsedNumber;

    public IntLiteral(String matchedText) {
        super(matchedText);
        try {
            parsedNumber = Integer.parseInt(matchedText);
            matched = true;
        } catch (NumberFormatException e) {
            matched = false;
        }
    }

    @Override
    public String inspect() {
        if (matched) {
            return "";
        }
        return String.format("cannot recognize %s, do you mean an integer?", matchedText);
    }
}
