package edu.mit.compilers.grammar.regular;

import java.security.InvalidAlgorithmParameterException;
import java.util.*;

public class RegularNode {
    private static class IdGenerator {
        private int curVal = 0;

        public int make() {
            int result = curVal;
            ++curVal;
            return result;
        }
    }

    private static final IdGenerator idGenerator = new IdGenerator();

    protected int id;
    protected final HashMap<Character, RegularNode> determMap = new HashMap<>();
    protected final HashSet<RegularNode> nondeterminedSet = new HashSet<>();
    protected final RegularNode errorNode;

    public RegularNode(RegularNode errorNode) {
        id = idGenerator.make();
        this.errorNode = errorNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegularNode that = (RegularNode) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public int getId() {
        return id;
    }

    public RegularNode getDetermined(char ch) {
        var next = determMap.get(ch);
        if (next == null) {
            return errorNode;
        }
        return next;
    }

    public void addDetermined(char ch, RegularNode node) throws InvalidAlgorithmParameterException {
        determMap.put(ch, node);
    }

    public Iterator<RegularNode> nonDeterminedIterator() {
        return nondeterminedSet.iterator();
    }

    public Set<RegularNode> getNondetermined() {
        return nondeterminedSet;
    }

    public void removeNondetermined(RegularNode node) {
        nondeterminedSet.remove(node);
    }

    public void addNonDetermined(RegularNode node) {
        nondeterminedSet.add(node);
    }
}
