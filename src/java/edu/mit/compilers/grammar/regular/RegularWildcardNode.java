package edu.mit.compilers.grammar.regular;

import java.security.InvalidAlgorithmParameterException;

public class RegularWildcardNode extends RegularNode {
    private RegularNode nextNode = null;

    public RegularWildcardNode() {

    }

    public RegularWildcardNode(RegularNode nextNode) {
        this.nextNode = nextNode;
    }

    public void setNextNode(RegularNode nextNode) {
        this.nextNode = nextNode;
    }

    @Override
    public RegularNode getDetermined(char ch) {
        return nextNode;
    }

    @Override
    public void addDetermined(char ch, RegularNode node) throws InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException();
    }
}
