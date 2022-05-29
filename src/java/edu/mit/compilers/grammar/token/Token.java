// YOU CAN MODIFY ANYTHING IN THIS FILE, THIS IS JUST A SUGGESTED CLASS

package edu.mit.compilers.grammar.token;

public class Token {
    protected final String matchedText;
    protected boolean matched = false;

    public Token(String matchedText) {
        this.matchedText = matchedText;
    }

    public String inspect() {
        return "";
    }

    public boolean isEOF() {
        return false;
    }

    public String getText() {
        return "EMPTY_TOKEN";
    }

    public class Report {

    }
}
