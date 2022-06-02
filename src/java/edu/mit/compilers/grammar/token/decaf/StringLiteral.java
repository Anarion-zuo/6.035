package edu.mit.compilers.grammar.token.decaf;

public class StringLiteral extends DecafToken {
    private String textWithoutEscape;

    private static String transformEscape(String input) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < input.length(); ++i) {
            if (input.charAt(i) == '\\') {
                ++i;
                builder.append(escapeCharacter(input.charAt(i)));
            } else {
                builder.append(input.charAt(i));
            }
        }
        return builder.toString();
    }

    private void transformEscapeInMatchedText() {
        textWithoutEscape = transformEscape(matchedText);
    }

    public StringLiteral(String matchedText) {
        super(matchedText);
    }

    private String getMatchedLiteral() {
        return textWithoutEscape.substring(1, matchedText.length() - 1);
    }

    @Override
    protected String inspect() {
        if (matchedText.charAt(0) != '\"' || matchedText.charAt(matchedText.length() - 1) != '\"') {
            matched = false;
            return "mismatched quotes in string literal";
        }
        for (int i = 1; i < matchedText.length() - 1; ++i) {
            if (matchedText.charAt(i) == '\"') {
                if (matchedText.charAt(i - 1) != '\\') {
                    matched = false;
                    return "mismatched quotes in string literal";
                }
            }
        }
        transformEscapeInMatchedText();
        matched = true;
        return "";
    }

    @Override
    public String getText() {
        if (!matched) {
            return "STRINGLITERAL: ";
        }
        return "STRINGLITERAL " + getMatchedLiteral();
    }
}
