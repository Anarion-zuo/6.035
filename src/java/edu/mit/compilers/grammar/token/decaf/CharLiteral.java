package edu.mit.compilers.grammar.token.decaf;

public class CharLiteral extends DecafToken {
    private char matchedChar = 0;

    public CharLiteral(String matchedText) {
        super(matchedText);
    }

    @Override
    protected String inspect() {
        if (matchedText.length() < 3) {
            matched = false;
            return "expecting char literal, found mismatched quotes";
        }
        String tooLongMessage = String.format("expecting char literal, found expression %s length %d too long", matchedText, matchedText.length());
        if (matchedText.length() == 4) {
            if (matchedText.charAt(1) == '\\') {
                matchedChar = escapeCharacter(matchedText.charAt(2));
                matched = true;
                return "";
            }
            matched = false;
            return tooLongMessage;
        }
        if (matchedText.length() > 4) {
            matched = false;
            return tooLongMessage;
        }
        if (matchedText.charAt(0) != '\'' || matchedText.charAt(2) != '\'' ) {
            matched = false;
            return "expecting char literal, found miss matched quotes";
        }
        char midChar = matchedText.charAt(1);
        if (midChar == '\'' ||
                midChar == '\n' ||
                midChar == '\"' ||
                midChar == '\\') {
            matched = false;
            return "unexpected char "
                    + String.format("0x%x", (int) midChar);
        }
        matchedChar = midChar;
        matched = true;
        return "";
    }

    @Override
    protected String getTokenName() {
        return "CHARLITERAL";
    }

    @Override
    protected String getTokenAttributeContent() {
        return "'" + reverseEscapeCharacter(matchedChar) + "'";
    }
}
