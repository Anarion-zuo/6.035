package edu.mit.compilers.grammar;

import edu.mit.compilers.grammar.regular.RegularTokenSet;
import edu.mit.compilers.grammar.token.Token;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static java.util.Map.entry;

public class RegularTokenTest {
    private RegularTokenSet regularTokenSet = null;

    @Test
    public void simpleTokenTest() {
        regularTokenSet = RegularTokenSet.newByRegexList(List.of(
                entry("A", "A"),
                entry("B", "B")
        ));
        var iter = regularTokenSet.iterator();
        Assert.assertEquals(iter.next('A').getFirst(), "A");
        iter = regularTokenSet.iterator();
        Assert.assertEquals(iter.next('B').getFirst(), "B");
        iter = regularTokenSet.iterator();
        Assert.assertTrue(iter.next('C').isEmpty());
    }

    @Test
    public void multipleRegexTest() {
        String positiveDigits = "1|2|3|4|5|6|7|8|9";
        String digits = "0|" + positiveDigits;
        regularTokenSet = RegularTokenSet.newByRegexList(List.of(
                entry("AB", "AB"),
                entry("A|B", "A|B"),
                entry("A*", "A*"),
                entry(digits, "digits"),
                entry("(" + positiveDigits + ")" + "(" + digits + ")*", "positive integer")
        ));

        var iter = regularTokenSet.iterator();
        Assert.assertEquals("A|B", iter.next('A').getFirst());
        Assert.assertEquals("A*", iter.next('A').getFirst());
        Assert.assertEquals("A*", iter.next('A').getFirst());

        iter = regularTokenSet.iterator();
        iter.next('A');
        Assert.assertEquals("AB", iter.next('B').getFirst());

        iter = regularTokenSet.iterator();
        Assert.assertEquals("A|B", iter.next('B').getFirst());

        String digitString = "digits";
        iter = regularTokenSet.iterator();
        Assert.assertEquals(digitString, iter.next('0').getFirst());
        iter = regularTokenSet.iterator();
        Assert.assertEquals(digitString, iter.next('1').getFirst());
        iter = regularTokenSet.iterator();
        Assert.assertEquals(digitString, iter.next('2').getFirst());
        iter = regularTokenSet.iterator();
        Assert.assertEquals(digitString, iter.next('3').getFirst());
        iter = regularTokenSet.iterator();
        Assert.assertEquals(digitString, iter.next('4').getFirst());
        iter = regularTokenSet.iterator();
        Assert.assertEquals(digitString, iter.next('5').getFirst());
        iter = regularTokenSet.iterator();
        Assert.assertEquals(digitString, iter.next('6').getFirst());
        iter = regularTokenSet.iterator();
        Assert.assertEquals(digitString, iter.next('7').getFirst());
        iter = regularTokenSet.iterator();
        Assert.assertEquals(digitString, iter.next('8').getFirst());
        iter = regularTokenSet.iterator();
        Assert.assertEquals(digitString, iter.next('9').getFirst());

        regularTokenSet = RegularTokenSet.newByRegexList(List.of(
                entry("AB", "AB"),
                entry("A|B", "A|B"),
                entry("A*", "A*"),
                entry("(" + positiveDigits + ")" + "(" + digits + ")*", "positive integer")
        ));
        Assert.assertEquals("positive integer", regularTokenSet.match("123".toCharArray()).getFirst());
        Assert.assertEquals("positive integer", regularTokenSet.match("111111111".toCharArray()).getFirst());
        Assert.assertEquals("positive integer", regularTokenSet.match("9988".toCharArray()).getFirst());
        Assert.assertEquals("positive integer", regularTokenSet.match("9000".toCharArray()).getFirst());
    }

    @Test
    public void specialCharRegexTest() {
        regularTokenSet = RegularTokenSet.newByRegexList(List.of(
                entry("a*\\n", "a star"),
                entry("abc.*\\n", "abc.star"),
                entry("abc\t.*\\n", "abc.star escape t")
        ));
        Assert.assertEquals("a star", regularTokenSet.match("aaaa\n".toCharArray()).getFirst());
        Assert.assertEquals("abc.star", regularTokenSet.match("abckkkk\n".toCharArray()).getFirst());
        Assert.assertEquals("abc.star", regularTokenSet.match("abc545468\n".toCharArray()).getFirst());
        Assert.assertEquals("abc.star", regularTokenSet.match("abc\\t123abc\n".toCharArray()).getFirst());
    }

    @Test
    public void matchLengthTest() {
        regularTokenSet = RegularTokenSet.newByRegexList(List.of(
                entry("aaab", "3a1b"),
                entry("aaac", "3a1c")
        ));
        var matchedList = regularTokenSet.matchLongest("aaad".toCharArray());
        Assert.assertEquals(2, matchedList.size());
        matchedList = regularTokenSet.matchLongest("aaab".toCharArray());
        Assert.assertEquals(1, matchedList.size());
        Assert.assertEquals("3a1b", matchedList.getFirst().tokenName);
        regularTokenSet = RegularTokenSet.newByRegexList(List.of(
                entry("'.'", "char literal"),
                entry(".", "single char")
        ));
        matchedList = regularTokenSet.matchLongest("'ab'".toCharArray());
        Assert.assertEquals(1, matchedList.size());
        Assert.assertEquals("char literal", matchedList.getFirst().tokenName);
    }
}
