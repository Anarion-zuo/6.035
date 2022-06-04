// YOU CAN MODIFY ANYTHING IN THIS FILE, THIS IS JUST A SUGGESTED CLASS

package edu.mit.compilers.grammar.token;

public class Token {
    protected final String matchedText;
    protected boolean matched = false;
    protected String inspectMessage;
    protected int textRow = -1, textCol = -1;

    public final int getTextRow() {
        return textRow;
    }

    public final void setTextRow(int textRow) {
        this.textRow = textRow;
    }

    public final int getTextCol() {
        return textCol;
    }

    public final void setTextCol(int textCol) {
        this.textCol = textCol;
    }

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

    public String getText() {
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

    public boolean shouldIgnore() {
        return false;
    }

    public final int getMatchedLength() {
        return matchedText.length();
    }
}
