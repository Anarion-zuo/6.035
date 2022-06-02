// YOU CAN MODIFY ANYTHING IN THIS FILE, THIS IS JUST A SUGGESTED CLASS

package edu.mit.compilers.grammar.token;

public class Token {
    protected final String matchedText;
    protected boolean matched = false;
    protected String inspectMessage;

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

    public String getText() {
        return "EMPTY_TOKEN";
    }

}
