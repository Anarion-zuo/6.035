package edu.mit.compilers.grammar.token.decaf;

public class BooleanLiteral extends DecafToken {
    private boolean matchedOption = false;

    public BooleanLiteral(String matchedText) {
        super(matchedText);
    }

    @Override
    protected String inspect() {
        if (matchedText.equals("true")) {
            matched = true;
            matchedOption = true;
            return "";
        }
        if (matchedText.equals("false")) {
            matched = true;
            matchedOption = false;
            return "";
        }
        matched = false;
        return "expecting boolean literal, got " + matchedText;
    }

    @Override
    protected String getTokenName() {
        return "BOOLEANLITERAL";
    }

    @Override
    protected String getTokenAttributeContent() {
        return matchedText;
    }
}
