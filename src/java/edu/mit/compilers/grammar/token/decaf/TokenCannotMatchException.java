package edu.mit.compilers.grammar.token.decaf;

import java.util.LinkedList;

public class TokenCannotMatchException extends Exception {
    public TokenCannotMatchException() {

    }

    @Override
    public String getLocalizedMessage() {
        return super.getLocalizedMessage() + ": cannot find a token to match this";
    }
}
