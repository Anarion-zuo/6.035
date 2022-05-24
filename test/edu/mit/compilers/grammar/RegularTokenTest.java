package edu.mit.compilers.grammar;

import edu.mit.compilers.grammar.regular.RegularTokenSet;
import edu.mit.compilers.grammar.token.Token;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static java.util.Map.entry;

public class RegularTokenTest {
    class TokenA extends Token {
        @Override
        public String getText() {
            return "A";
        }
    }

    class TokenB extends Token {
        @Override
        public String getText() {
            return "B";
        }
    }

    private RegularTokenSet regularTokenSet = null;

    @Test
    public void simpleTokenTest() {
        regularTokenSet = RegularTokenSet.newByRegexList(List.of(
                entry("A", new TokenA()),
                entry("B", new TokenB())
        ));
        var iter = regularTokenSet.iterator();
        Assert.assertEquals(iter.next('A').getText(), "A");
        iter = regularTokenSet.iterator();
        Assert.assertEquals(iter.next('B').getText(), "B");
        iter = regularTokenSet.iterator();
        Assert.assertNull(iter.next('C'));
    }

    class TokenAB extends Token {
        @Override
        public String getText() {
            return "AB";
        }
    }

    class TokenAlternate extends Token {
        @Override
        public String getText() {
            return "Alternate";
        }
    }

    class TokenClosure extends Token {
        @Override
        public String getText() {
            return "Closure";
        }
    }

    class TokenDigit extends Token {
        @Override
        public String getText() {
            return "Digit";
        }
    }

    class TokenPositiveInteger extends Token {
        @Override
        public String getText() {
            return "PositiveInteger";
        }

        @Override
        protected Object makeAttribute(char[] input) {
            return Integer.parseInt(new String(input));
        }
    }

    @Test
    public void multipleRegexTest() {
        String positiveDigits = "1|2|3|4|5|6|7|8|9";
        String digits = "0|" + positiveDigits;
        regularTokenSet = RegularTokenSet.newByRegexList(List.of(
                entry("AB", new TokenAB()),
                entry("A|B", new TokenAlternate()),
                entry("A*", new TokenClosure()),
                entry(digits, new TokenDigit()),
                entry("(" + positiveDigits + ")" + "(" + digits + ")*", new TokenPositiveInteger())
        ));

        var iter = regularTokenSet.iterator();
        Assert.assertEquals("Alternate", iter.next('A').getText());
        Assert.assertEquals("Closure", iter.next('A').getText());
        Assert.assertEquals("Closure", iter.next('A').getText());

        iter = regularTokenSet.iterator();
        iter.next('A');
        Assert.assertEquals("AB", iter.next('B').getText());

        iter = regularTokenSet.iterator();
        Assert.assertEquals("Alternate", iter.next('B').getText());

        String digitString = new TokenDigit().getText();
        iter = regularTokenSet.iterator();
        Assert.assertEquals(digitString, iter.next('0').getText());
        iter = regularTokenSet.iterator();
        Assert.assertEquals(digitString, iter.next('1').getText());
        iter = regularTokenSet.iterator();
        Assert.assertEquals(digitString, iter.next('2').getText());
        iter = regularTokenSet.iterator();
        Assert.assertEquals(digitString, iter.next('3').getText());
        iter = regularTokenSet.iterator();
        Assert.assertEquals(digitString, iter.next('4').getText());
        iter = regularTokenSet.iterator();
        Assert.assertEquals(digitString, iter.next('5').getText());
        iter = regularTokenSet.iterator();
        Assert.assertEquals(digitString, iter.next('6').getText());
        iter = regularTokenSet.iterator();
        Assert.assertEquals(digitString, iter.next('7').getText());
        iter = regularTokenSet.iterator();
        Assert.assertEquals(digitString, iter.next('8').getText());
        iter = regularTokenSet.iterator();
        Assert.assertEquals(digitString, iter.next('9').getText());

        regularTokenSet = RegularTokenSet.newByRegexList(List.of(
                entry("AB", new TokenAB()),
                entry("A|B", new TokenAlternate()),
                entry("A*", new TokenClosure()),
                entry("(" + positiveDigits + ")" + "(" + digits + ")*", new TokenPositiveInteger())
        ));
        Assert.assertEquals("PositiveInteger", regularTokenSet.match("123".toCharArray()).getText());
        Assert.assertEquals(123, regularTokenSet.match("123".toCharArray()).getAttribute());
        Assert.assertEquals("PositiveInteger", regularTokenSet.match("111111111".toCharArray()).getText());
        Assert.assertEquals(111111111, regularTokenSet.match("111111111".toCharArray()).getAttribute());
        Assert.assertEquals("PositiveInteger", regularTokenSet.match("9988".toCharArray()).getText());
        Assert.assertEquals(9988, regularTokenSet.match("9988".toCharArray()).getAttribute());
        Assert.assertEquals("PositiveInteger", regularTokenSet.match("9000".toCharArray()).getText());
        Assert.assertEquals(9000, regularTokenSet.match("9000".toCharArray()).getAttribute());
        Assert.assertNull(regularTokenSet.match("0123".toCharArray()));
        Assert.assertNull(regularTokenSet.match("0".toCharArray()));
        Assert.assertNull(regularTokenSet.match("000".toCharArray()));
    }

    class TokenLineEnd extends Token {
        private String text;

        public TokenLineEnd(String text) {
            this.text = text;
        }

        @Override
        public String getText() {
            return text;
        }
    }

    @Test
    public void specialCharRegexTest() {
        regularTokenSet = RegularTokenSet.newByRegexList(List.of(
                entry("a*\\n", new TokenLineEnd("a star")),
                entry("abc.*\\n", new TokenLineEnd("abc.star")),
                entry("abc\t.*\\n", new TokenLineEnd("abc.star escape t"))
        ));
        Assert.assertEquals("a star", regularTokenSet.match("aaaa\n".toCharArray()).getText());
        Assert.assertEquals("abc.star", regularTokenSet.match("abckkkk\n".toCharArray()).getText());
        Assert.assertEquals("abc.star", regularTokenSet.match("abc545468\n".toCharArray()).getText());
        Assert.assertEquals("abc.star", regularTokenSet.match("abc\\t123abc\n".toCharArray()).getText());
    }
}
