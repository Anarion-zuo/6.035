package edu.mit.compilers.grammar.token.decaf;

public class ReservedWord extends DecafToken {
    protected final String word;

    public ReservedWord(String matchedText, String word) {
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

    @Override
    public String getText() {
        return textRow + " " + word;
    }
}
