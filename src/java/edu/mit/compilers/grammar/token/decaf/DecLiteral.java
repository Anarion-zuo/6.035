package edu.mit.compilers.grammar.token.decaf;

public class DecLiteral extends IntLiteral {
    public DecLiteral(String matchedText) {
        super(matchedText);
    }

    @Override
    protected int parseNumber() {
        return Integer.parseInt(matchedText, 10);
    }

    @Override
    protected String getTokenAttributeContent() {
        return String.format("%d", parsedNumber);
    }
}
