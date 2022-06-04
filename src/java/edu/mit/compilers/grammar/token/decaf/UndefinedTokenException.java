package edu.mit.compilers.grammar.token.decaf;

public class UndefinedTokenException extends Exception {
    private final String tokenName;

    public UndefinedTokenException(String tokenName) {
        this.tokenName = tokenName;
    }

    @Override
    public String getLocalizedMessage() {
        return super.getLocalizedMessage() + ": undefined token [" + tokenName + "]";
    }
}
