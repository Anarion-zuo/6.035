// YOU CAN MODIFY ANYTHING IN THIS FILE, THIS IS JUST A SUGGESTED CLASS

package edu.mit.compilers.grammar.token;

public class Token {
    protected final String matchedText;
    protected boolean matched = false;
    protected String inspectMessage;
    protected int textRow = -1, textCol = -1;

    public Token(String matchedText) {
        this.matchedText = matchedText;
    }

    protected String inspect() {
        return "";
    }

    protected final void callInspect() {
        inspectMessage = inspect();
    }

    public boolean isEOF() {
        return false;
    }

    protected String getTokenName() {
        return "UNDEFINED_TOKEN";
    }

    protected String getTokenAttributeContent() {
        return "";
    }

    public final String getText() {
        String prefix = textRow + " " + getTokenName() + " " + getTokenAttributeContent();
        if (matched) {
            return prefix;
        }
        return prefix + ": " + inspectMessage;
    }

    public final void afterMatching() throws TokenMismatchException {
        inspectMessage = inspect();
        if (!matched) {
            throw new TokenMismatchException(inspectMessage);
        }
    }

    public final boolean isMatched() {
        return matched;
    }
}
