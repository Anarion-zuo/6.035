package edu.mit.compilers.grammar;

import com.sun.source.tree.AssertTree;
import edu.mit.compilers.grammar.regular.RegularGraph;
import edu.mit.compilers.grammar.regular.RegularNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.InvalidAlgorithmParameterException;

public class RegularMatchTest {

    @Test
    public void emptyInputTest() {
        RegularGraph graph = new RegularGraph();
        var iter = graph.iterator();
        var iterator = graph.iterator();
        Assertions.assertFalse(iterator.hasNext());
        iterator.next('a');
        Assertions.assertTrue(iterator.invalid());
        iterator.next('a');
        iterator.next('a');
        iterator.next('a');
        iterator.next('a');
        Assertions.assertTrue(iterator.invalid());
    }

    @Test
    public void charMatchTest() throws InvalidAlgorithmParameterException {
        RegularGraph graph = new RegularGraph();
        graph.getSource().addDetermined('a', graph.getDest());
        graph.getSource().removeNondetermined(graph.getDest());
        var iter = graph.iterator();
        Assertions.assertTrue(iter.hasNext());
        iter.next('a');
        Assertions.assertFalse(iter.hasNext());
    }

    @Test
    public void alternateCharMatchTest() throws InvalidAlgorithmParameterException {
        // Construct a|b
        RegularNode m1 = new RegularNode(null), m2 = new RegularNode(null);
        RegularGraph graph = new RegularGraph();
        graph.getSource().addDetermined('a', m1);
        graph.getSource().addDetermined('b', m2);
        m1.addNonDetermined(graph.getDest());
        m2.addNonDetermined(graph.getDest());
        graph.getSource().removeNondetermined(graph.getDest());

        // match
        var iter1 = graph.iterator();
        Assertions.assertTrue(iter1.hasNext());
        iter1.next('a');
        Assertions.assertFalse(iter1.hasNext());

        var iter2 = graph.iterator();
        Assertions.assertTrue(iter2.hasNext());
        iter2.next('b');
        Assertions.assertFalse(iter2.hasNext());

        var iter3 = graph.iterator();
        iter3.next('9');
        Assertions.assertTrue(iter3.invalid());
    }
}
