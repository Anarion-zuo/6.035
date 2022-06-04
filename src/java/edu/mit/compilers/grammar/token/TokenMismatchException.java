package edu.mit.compilers.grammar.token;

public class TokenMismatchException extends Exception {
    protected final String message;

    public TokenMismatchException(String message) {
        this.message = message;
    }

    @Override
    public String getLocalizedMessage() {
        return super.getLocalizedMessage() + ": " + message;
    }
}
