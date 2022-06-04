package edu.mit.compilers.grammar.token.decaf;

public class SinglelineComment extends Comment {
    public SinglelineComment(String matchedText) {
        super(matchedText);
    }

    @Override
    protected String inspect() {
        if (matchedText.length() < 2) {
            matched = false;
            return "matched length too short";
        }
        if (matchedText.charAt(0) != '/' || matchedText.charAt(1) != '/') {
            matched = false;
            return "not started with //";
        }
        if (matchedText.charAt(matchedText.length() - 1) != '\n' &&
                matchedText.charAt(matchedText.length() - 1) != '\r') {
            matched = false;
            return "not ended with a new line";
        }
        matched = true;
        return "";
    }
}
