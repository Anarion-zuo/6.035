package edu.mit.compilers.grammar.token.decaf;

public abstract class IntLiteral extends DecafToken {
    protected int parsedNumber;

    public IntLiteral(String matchedText) {
        super(matchedText);
    }

    @Override
    protected final String getTokenName() {
        return "INTLITERAL";
    }

    protected abstract int parseNumber() throws NumberFormatException;

    @Override
    protected final String inspect() {
        try {
            parsedNumber = parseNumber();
            matched = true;
        } catch (NumberFormatException e) {
            matched = false;
        }
        if (matched) {
            return "";
        }
        return String.format("cannot recognize %s, do you want an integer?", matchedText);
    }
}
