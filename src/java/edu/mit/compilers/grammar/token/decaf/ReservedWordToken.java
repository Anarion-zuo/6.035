package edu.mit.compilers.grammar.token.decaf;

public class ReservedWordToken extends DecafToken {
    protected String word;

    public ReservedWordToken(String matchedText, String word) {
        super(matchedText);
        this.word = word;
    }

    @Override
    protected String inspect() {
        if (word.equals(matchedText)) {
            matched = true;
            return "";
        }
        matched = false;
        return String.format("expected %s, got %s", matchedText, word);
    }
}
