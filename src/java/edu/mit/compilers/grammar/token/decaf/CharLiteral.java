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
            return "expecting char literal, found mismatched single quote";
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
        matchedChar = matchedText.charAt(1);
        matched = true;
        return "";
    }

    @Override
    public String getText() {
        return super.getText();
    }
}
