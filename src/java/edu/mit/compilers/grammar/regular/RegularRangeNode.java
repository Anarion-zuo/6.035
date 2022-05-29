package edu.mit.compilers.grammar.regular;

import java.security.InvalidAlgorithmParameterException;

public class RegularRangeNode extends RegularNode {

    private final char lower, higher;
    private final RegularNode next;

    public RegularRangeNode(char lower, char higher, RegularNode next, RegularNode errorNode) {
        super(errorNode);
        if (lower > higher) {
            try {
                throw new InvalidAlgorithmParameterException();
            } catch (InvalidAlgorithmParameterException e) {
                throw new RuntimeException(e);
            }
        }
        this.lower = lower;
        this.higher = higher;
        this.next = next;
    }

    @Override
    public void addDetermined(char ch, RegularNode node) throws InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException();
    }

    @Override
    public RegularNode getDetermined(char ch) {
        if (ch <= higher && ch >= lower) {
            return next;
        }
        return errorNode;
    }
}
