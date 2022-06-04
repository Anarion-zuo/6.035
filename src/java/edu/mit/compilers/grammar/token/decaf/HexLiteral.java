package edu.mit.compilers.grammar.token.decaf;

public class HexLiteral extends IntLiteral {
    public HexLiteral(String matchedText) {
        super(matchedText);
    }

    @Override
    protected long parseNumber() {
        if (!matchedText.startsWith("0x")) {
            throw new NumberFormatException();
        }
        if (matchedText.length() <= 2) {
            throw new NumberFormatException();
        }
        String unwrappedText = matchedText.substring(2);
        return Long.parseLong(unwrappedText, 16);
    }

    @Override
    protected String getTokenAttributeContent() {
        return matchedText;
    }
}
