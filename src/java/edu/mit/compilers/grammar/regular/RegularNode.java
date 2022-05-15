package edu.mit.compilers.grammar.regular;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class RegularNode {
    protected final HashMap<Character, RegularNode> determMap = new HashMap<>();
    protected final List<RegularNode> nondeterList = new ArrayList<>();

    public RegularNode getDetermined(char ch) {
        return determMap.get(ch);
    }

    public Iterator<RegularNode> nonDeterminedIterator() {
        return nondeterList.iterator();
    }
}
