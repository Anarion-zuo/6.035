// YOU CAN MODIFY ANYTHING IN THIS FILE, THIS IS JUST A SUGGESTED CLASS

package edu.mit.compilers.grammar.token;

public class Token {

    Object attribute = null;

    public void setAttribute(char[] input) {
        this.attribute = makeAttribute(input);
    }

    protected Object makeAttribute(char[] input) {
        return null;
    }

    public Object getAttribute() {
        return attribute;
    }

    public boolean isEOF() {
        return false;
    }

    public String getText() {
        return "";
    }
}
