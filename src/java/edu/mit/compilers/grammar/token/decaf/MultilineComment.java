package edu.mit.compilers.grammar.token.decaf;

public class MultilineComment extends Comment {
    public MultilineComment(String matchedText) {
        super(matchedText);
    }

    @Override
    protected String inspect() {
        if (matchedText.length() < 4) {
            matched = false;
            return "matched length too short";
        }
        if (matchedText.charAt(0) != '/' ||
                matchedText.charAt(1) != '*' ||
                matchedText.charAt(matchedText.length() - 1) != '/' ||
                matchedText.charAt(matchedText.length() - 2) != '*') {
            matched = false;
            return "not a multilne comment with /* */";
        }
        matched = true;
        return "";
    }
}
