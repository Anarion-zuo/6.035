package edu.mit.compilers.grammar.regular;

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

    public RegularNode() {
        id = idGenerator.make();
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
        return determMap.get(ch);
    }

    public void addDetermined(char ch, RegularNode node) {
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
