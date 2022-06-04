package edu.mit.compilers.grammar.token.decaf;

public class Comment extends DecafToken {
    public Comment(String matchedText) {
        super(matchedText);
    }

    @Override
    protected final String getTokenName() {
        return "COMMENT";
    }

    @Override
    protected final String getTokenAttributeContent() {
        return matchedText;
    }
}
