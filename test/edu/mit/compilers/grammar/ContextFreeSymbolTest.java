package edu.mit.compilers.grammar;

import edu.mit.compilers.grammar.cfg.ContextFreeSentence;
import edu.mit.compilers.grammar.cfg.ContextFreeSymbol;
import edu.mit.compilers.grammar.cfg.ContextFreeTerminalSymbol;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ContextFreeSymbolTest {

    class CharSymbol extends ContextFreeTerminalSymbol {

        private char ch;

        @Override
        public String toString() {
            return "" + ch;
        }

        public CharSymbol(char c) {
            ch = c;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CharSymbol)) {
                return false;
            }
            return ((CharSymbol) obj).ch == ch;
        }
    }

    class MultipleTerminalSymbol extends ContextFreeSymbol {

        public MultipleTerminalSymbol(List<ContextFreeSentence> sentences) {
            super(sentences);
        }

        public MultipleTerminalSymbol(ContextFreeSentence... sentences) {
            super(sentences);
        }
    }

    @Test
    public void multipleCharMatchTest() {
        var root = new MultipleTerminalSymbol(
                new ContextFreeSentence(new CharSymbol('a'), new CharSymbol('b'), new CharSymbol('c')),
                new ContextFreeSentence(new CharSymbol('a'), new CharSymbol('b'), new CharSymbol('d')),
                new ContextFreeSentence(new CharSymbol('a'), new CharSymbol('b'), new CharSymbol('e'))
        );
        // perfect match
        System.out.println("==========================");
        var stream1 = new ContextFreeSentence(new CharSymbol('a'), new CharSymbol('b'), new CharSymbol('c'));
        var nextIterator = root.match(stream1.iterator()).nextIterator;
        Assert.assertFalse(nextIterator.hasNext());

        System.out.println("==========================");
        stream1 = new ContextFreeSentence(new CharSymbol('a'), new CharSymbol('b'), new CharSymbol('d'));
        nextIterator = root.match(stream1.iterator()).nextIterator;
        Assert.assertFalse(nextIterator.hasNext());

        System.out.println("==========================");
        stream1 = new ContextFreeSentence(new CharSymbol('a'), new CharSymbol('b'), new CharSymbol('e'));
        nextIterator = root.match(stream1.iterator()).nextIterator;
        Assert.assertFalse(nextIterator.hasNext());

        // incorrect match
        System.out.println("==========================");
        stream1 = new ContextFreeSentence(new CharSymbol('a'), new CharSymbol('b'), new CharSymbol('a'));
        nextIterator = root.match(stream1.iterator()).nextIterator;
        Assert.assertNull(nextIterator);

        // short match
        System.out.println("==========================");
        stream1 = new ContextFreeSentence(new CharSymbol('a'), new CharSymbol('b'));
        nextIterator = root.match(stream1.iterator()).nextIterator;
        Assert.assertNull(nextIterator);
    }

    class CharListSymbol extends ContextFreeSymbol {

        public String toString() {
            return "CharList";
        }
        public CharListSymbol(char c) {
            super(Arrays.asList(new ContextFreeSentence(Arrays.asList(new CharSymbol(c))), new ContextFreeSentence(Arrays.asList(new CharSymbol(c), null))));
            sentences.get(1).setSymbol(1, this);
        }
    }

    @Test
    public void listCharMatchTest() {
        var listSymbol = new CharListSymbol('a');
        var stream = new ContextFreeSentence(new CharSymbol('a'));
        var nextIterator = listSymbol.match(stream.iterator()).nextIterator;
        Assert.assertFalse(nextIterator.hasNext());

        stream = new ContextFreeSentence(new CharSymbol('a'), new CharSymbol('a'));
        nextIterator = listSymbol.match(stream.iterator()).nextIterator;
        Assert.assertFalse(nextIterator.hasNext());

        stream = new ContextFreeSentence(new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'));
        nextIterator = listSymbol.match(stream.iterator()).nextIterator;
        Assert.assertFalse(nextIterator.hasNext());
    }

    @Test
    public void manyAlternativeTest() {
        var alt1 = new MultipleTerminalSymbol(
                new ContextFreeSentence(new CharSymbol('a'), new CharSymbol('b'), new CharSymbol('c')),
                new ContextFreeSentence(new CharSymbol('a'), new CharSymbol('b'), new CharSymbol('d')),
                new ContextFreeSentence(new CharSymbol('a'), new CharSymbol('b'), new CharSymbol('e'))
        );
        var alt2 = new CharListSymbol('a');
        var root = new ContextFreeSymbol(new ContextFreeSentence(alt1), new ContextFreeSentence(alt2));
        // perfect match
        System.out.println("==========================");
        var stream = new ContextFreeSentence(new CharSymbol('a'), new CharSymbol('b'), new CharSymbol('c'));
        var nextIterator = root.match(stream.iterator()).nextIterator;
        Assert.assertFalse(nextIterator.hasNext());

        System.out.println("==========================");
        stream = new ContextFreeSentence(new CharSymbol('a'), new CharSymbol('b'), new CharSymbol('d'));
        nextIterator = root.match(stream.iterator()).nextIterator;
        Assert.assertFalse(nextIterator.hasNext());

        System.out.println("==========================");
        stream = new ContextFreeSentence(new CharSymbol('a'), new CharSymbol('b'), new CharSymbol('e'));
        nextIterator = root.match(stream.iterator()).nextIterator;
        Assert.assertFalse(nextIterator.hasNext());

        // incorrect match
        System.out.println("==========================");
        stream = new ContextFreeSentence(new CharSymbol('a'), new CharSymbol('b'), new CharSymbol('a'));
        nextIterator = root.match(stream.iterator()).nextIterator;
        Assert.assertNull(nextIterator);

        // short match
        System.out.println("==========================");
        stream = new ContextFreeSentence(new CharSymbol('a'), new CharSymbol('b'));
        nextIterator = root.match(stream.iterator()).nextIterator;
        Assert.assertNull(nextIterator);

        System.out.println("==========================");
        stream = new ContextFreeSentence(new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'), new CharSymbol('a'));
        nextIterator = root.match(stream.iterator()).nextIterator;
        Assert.assertFalse(nextIterator.hasNext());
    }
}
