package edu.mit.compilers.grammar.token.decaf;

public class HexLiteral extends IntLiteral {
    public HexLiteral(String matchedText) {
        super(matchedText);
    }

    @Override
    protected int parseNumber() {
        if (!matchedText.startsWith("0x")) {
            throw new NumberFormatException();
        }
        if (matchedText.length() <= 2) {
            throw new NumberFormatException();
        }
        String unwrappedText = matchedText.substring(2);
        return Integer.parseInt(unwrappedText, 16);
    }

    @Override
    protected String getTokenAttributeContent() {
        return String.format("0x%x", parsedNumber);
    }
}
