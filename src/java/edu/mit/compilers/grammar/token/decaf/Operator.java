package edu.mit.compilers.grammar.token.decaf;

public class Operator extends DecafToken {
    private final String word;

    public Operator(String matchedText, String word) {
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
        return "expecting " + word + " got " + matchedText;
    }

    @Override
    public String getText() {
        return textRow + " " + word;
    }
}
