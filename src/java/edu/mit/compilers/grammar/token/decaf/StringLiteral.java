package edu.mit.compilers.grammar.token.decaf;

public class StringLiteral extends DecafToken {
    public StringLiteral(String matchedText) {
        super(matchedText);
    }

    private String getMatchedLiteral() {
        return matchedText.substring(1, matchedText.length() - 1);
    }

    @Override
    public String inspect() {
        matched = true;
        return "";
    }

    @Override
    public String getText() {
        return "STRINGLITERAL " + getMatchedLiteral();
    }
}
