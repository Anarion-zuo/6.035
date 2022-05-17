package edu.mit.compilers.grammar;

import edu.mit.compilers.grammar.regular.RegularGraph;
import edu.mit.compilers.grammar.regular.RegularSymbolUtil;
import org.junit.Assert;
import org.junit.Test;

public class RegularExprParseTest {
    RegularSymbolUtil regularSymbolUtil = new RegularSymbolUtil();

    @Test
    public void formatTest() {
        char[] aChar = "a".toCharArray();
        char[] manyChars = "abc".toCharArray();
        char[] aBracket = "(ab)".toCharArray();
        char[] aClosure = "a*".toCharArray();
        char[] aConcat = "a|b".toCharArray();
        char[] concatClosure = "abc*".toCharArray();
        Assert.assertTrue(regularSymbolUtil.isFormatCorrect(aChar));
        Assert.assertTrue(regularSymbolUtil.isFormatCorrect(manyChars));
        Assert.assertTrue(regularSymbolUtil.isFormatCorrect(aBracket));
        Assert.assertTrue(regularSymbolUtil.isFormatCorrect(aClosure));
        Assert.assertTrue(regularSymbolUtil.isFormatCorrect(aConcat));
        Assert.assertTrue(regularSymbolUtil.isFormatCorrect(concatClosure));

        char[] bracketClosure = "(abc)*".toCharArray();
        char[] bracketAlter = "(abcd)|(ae)|c".toCharArray();
        char[] bracketClosureAlter1 = "(a|b)*".toCharArray();
        char[] bracketClosureAlter2 = "a|b*".toCharArray();
        Assert.assertTrue(regularSymbolUtil.isFormatCorrect(bracketClosure));
        Assert.assertTrue(regularSymbolUtil.isFormatCorrect(bracketAlter));
        Assert.assertTrue(regularSymbolUtil.isFormatCorrect(bracketClosureAlter1));
        Assert.assertTrue(regularSymbolUtil.isFormatCorrect(bracketClosureAlter2));

        char[] int1 = "(a|(bc*))".toCharArray();
        char[] int2 = "(a*b*c*|(ef)*f)|k*|j12*".toCharArray();
        Assert.assertTrue(regularSymbolUtil.isFormatCorrect(int1));
        Assert.assertTrue(regularSymbolUtil.isFormatCorrect(int2));

        char[] incompleteLeftBracket = "(abc".toCharArray();
        char[] incompleteRightBracket = "aaa)".toCharArray();
        char[] unpairedBracket = ")(".toCharArray();
        char[] incompleteAlter = "|a".toCharArray();
        char[] incompleteClosure = "*f".toCharArray();
        Assert.assertFalse(regularSymbolUtil.isFormatCorrect(incompleteLeftBracket));
        Assert.assertFalse(regularSymbolUtil.isFormatCorrect(incompleteRightBracket));
        Assert.assertFalse(regularSymbolUtil.isFormatCorrect(unpairedBracket));
        Assert.assertFalse(regularSymbolUtil.isFormatCorrect(incompleteAlter));
        Assert.assertFalse(regularSymbolUtil.isFormatCorrect(incompleteClosure));

        char[] wrong1 = "))((ff**".toCharArray();
        Assert.assertFalse(regularSymbolUtil.isFormatCorrect(wrong1));
    }

    private void contructionTestTemplate(char[] regex, char []input, boolean shouldMatch) {
        var info = regularSymbolUtil.rootMatchExaust(regex);
        Assert.assertTrue(regularSymbolUtil.isFormatCorrect(regex));
        RegularGraph graph = (RegularGraph) info.object;
        var iter = graph.iterator();
        if (shouldMatch) {
            for (char c : input) {
                Assert.assertTrue(iter.hasNext());
                iter.next(c);
                Assert.assertFalse(iter.invalid());
            }
            Assert.assertTrue(iter.hasMatch());
        } else {
            for (char c : input) {
                iter.next(c);
            }
            if (!iter.invalid()) {
                Assert.assertTrue(iter.hasNext());
            }
        }
    }

    @Test
    public void constructionTest() {
        contructionTestTemplate("a".toCharArray(), "a".toCharArray(), true);
        contructionTestTemplate("a".toCharArray(), "b".toCharArray(), false);
        contructionTestTemplate("123456".toCharArray(), "123456".toCharArray(), true);
        contructionTestTemplate("123456".toCharArray(), "1234567".toCharArray(), false);
        contructionTestTemplate("123456".toCharArray(), "12".toCharArray(), false);

        contructionTestTemplate("(123456)".toCharArray(), "123456".toCharArray(), true);
        contructionTestTemplate("(123456)".toCharArray(), "1234567".toCharArray(), false);
        contructionTestTemplate("(123456)".toCharArray(), "1234".toCharArray(), false);

        contructionTestTemplate("a|b".toCharArray(), "a".toCharArray(), true);
        contructionTestTemplate("a|b".toCharArray(), "b".toCharArray(), true);
        contructionTestTemplate("a|b".toCharArray(), "c".toCharArray(), false);
        contructionTestTemplate("(ab)|b".toCharArray(), "ab".toCharArray(), true);
        contructionTestTemplate("(ab)|b".toCharArray(), "b".toCharArray(), true);
        contructionTestTemplate("(ab)|b".toCharArray(), "abc".toCharArray(), false);
        contructionTestTemplate("(ab)|b|(cdefg)".toCharArray(), "cdefg".toCharArray(), true);
        contructionTestTemplate("(ab)|b|(cdefg)".toCharArray(), "cd".toCharArray(), false);


        contructionTestTemplate("a*".toCharArray(), "a".toCharArray(), true);
        contructionTestTemplate("a*".toCharArray(), "aa".toCharArray(), true);
        contructionTestTemplate("a*".toCharArray(), "aaa".toCharArray(), true);
        contructionTestTemplate("a*".toCharArray(), "aaaaaaaaaaaaaa".toCharArray(), true);
        contructionTestTemplate("a*".toCharArray(), "aaaaaaaaafaaa".toCharArray(), false);
        contructionTestTemplate("(abc)*".toCharArray(), "abc".toCharArray(), true);
        contructionTestTemplate("(abc)*".toCharArray(), "abcabcabc".toCharArray(), true);
        contructionTestTemplate("(abc)*".toCharArray(), "abcadcabc".toCharArray(), false);

        contructionTestTemplate("(abc)*|(k*)".toCharArray(), "kkkkkabc".toCharArray(), false);
        contructionTestTemplate("(abc)*|(k*)".toCharArray(), "kkkkk".toCharArray(), true);
        contructionTestTemplate("(abc)*|(k*)".toCharArray(), "abcabc".toCharArray(), true);

    }
}
