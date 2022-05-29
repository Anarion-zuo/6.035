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

        char[] incompleteLeftBracket = "(a".toCharArray();
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

    private void contructionTestTemplate(String regexString, String inputString, boolean shouldMatch) {
        char[] regex = regexString.toCharArray(), input = inputString.toCharArray();
        var info = regularSymbolUtil.rootMatchExaust(regex);
        Assert.assertTrue(regularSymbolUtil.isFormatCorrect(regex));
        RegularGraph graph = (RegularGraph) info.afterAttribute;
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
        contructionTestTemplate("a", "a", true);
        contructionTestTemplate("a", "b", false);
        contructionTestTemplate("123456", "123456", true);
        contructionTestTemplate("123456", "1234567", false);
        contructionTestTemplate("123456", "12", false);

        contructionTestTemplate("(123456)", "123456", true);
        contructionTestTemplate("(123456)", "1234567", false);
        contructionTestTemplate("(123456)", "1234", false);

        contructionTestTemplate("a|b", "a", true);
        contructionTestTemplate("a|b", "b", true);
        contructionTestTemplate("a|b", "c", false);
        contructionTestTemplate("(ab)|b", "ab", true);
        contructionTestTemplate("(ab)|b", "b", true);
        contructionTestTemplate("(ab)|b", "abc", false);
        contructionTestTemplate("(ab)|b|(cdefg)", "cdefg", true);
        contructionTestTemplate("(ab)|b|(cdefg)", "cd", false);


        contructionTestTemplate("a*", "a", true);
        contructionTestTemplate("a*", "aa", true);
        contructionTestTemplate("a*", "aaa", true);
        contructionTestTemplate("a*", "aaaaaaaaaaaaaa", true);
        contructionTestTemplate("a*", "aaaaaaaaafaaa", false);
        contructionTestTemplate("(abc)*", "abc", true);
        contructionTestTemplate("(abc)*", "abcabcabc", true);
        contructionTestTemplate("(abc)*", "abcadcabc", false);

        contructionTestTemplate("(abc)*|(k*)", "kkkkkabc", false);
        contructionTestTemplate("(abc)*|(k*)", "kkkkk", true);
        contructionTestTemplate("(abc)*|(k*)", "abcabc", true);

    }

    @Test
    public void ambuguityTest() {
        contructionTestTemplate("ab|c", "ac", true);
        contructionTestTemplate("ab|c", "ab", true);
        contructionTestTemplate("ab|c", "a", false);
        contructionTestTemplate("ab|c", "c", false);
        contructionTestTemplate("ab|c", "b", false);
        contructionTestTemplate("ab|cd", "ab", true);
        contructionTestTemplate("ab|cd", "abd", false);
        contructionTestTemplate("ab|cd", "acd", true);

        contructionTestTemplate("abc*", "abc", true);
        contructionTestTemplate("abc*", "abcccc", true);
        contructionTestTemplate("abc*", "abcabc", false);
        contructionTestTemplate("ab*c", "abc", true);
        contructionTestTemplate("ab*c", "abbbbbbbc", true);
        contructionTestTemplate("ab*c", "ababc", false);
    }

    @Test
    public void enumTest() {
        contructionTestTemplate("[a]", "a", true);
        contructionTestTemplate("[ab]", "a", true);
        contructionTestTemplate("[ab]", "b", true);
        contructionTestTemplate("[ab]", "c", false);

        contructionTestTemplate("[a-c]", "a", true);
        contructionTestTemplate("[a-c]", "b", true);
        contructionTestTemplate("[a-c]", "c", true);
        contructionTestTemplate("[a-c]", "9", false);
        contructionTestTemplate("[a-c0-3]", "1", true);
        contructionTestTemplate("[a-c0-3]", "3", true);
        contructionTestTemplate("[a-c0-3]", ";", false);

        contructionTestTemplate("[a]|[bc]", "b", true);
        contructionTestTemplate("[a]|[bc]", "a", true);
        contructionTestTemplate("[a]|[bc]", "c", true);
        contructionTestTemplate("[a]|[bc]", "d", false);
        contructionTestTemplate("[bc]*", "bbccccbbc", true);
        contructionTestTemplate("[bc]*", "bbcaccbbc", false);
//
    }


    @Test
    public void exceptTest() {
        contructionTestTemplate("[^abc]", "a", false);
        contructionTestTemplate("[^abc]", "b", false);
        contructionTestTemplate("[^abc]", "c", false);
        contructionTestTemplate("[^abc]", "1", true);
        contructionTestTemplate("[^abc]", "2", true);

        contructionTestTemplate("[^a-c]", "a", false);
        contructionTestTemplate("[^a-c]", "b", false);
        contructionTestTemplate("[^a-c]", "c", false);
        contructionTestTemplate("[^a-c]", "9", true);
        contructionTestTemplate("[^a-c0-3]", "1", false);
        contructionTestTemplate("[^a-c0-3]", "3", false);
        contructionTestTemplate("[^a-c0-3]", ";", true);
        contructionTestTemplate("[^ab\\|]", "|", false);
        contructionTestTemplate("[^bc]*", "bbccb", false);
        contructionTestTemplate("[^bc]*", "bbacb", false);
        contructionTestTemplate("[^b]*", "aalle", true);
        contructionTestTemplate("[^b]*", "baaaa", false);

        contructionTestTemplate("[^b][^a]", "12", true);
        contructionTestTemplate("[^b][^a]", "ba", false);
        contructionTestTemplate("[^b][^a]", "b1", false);
        contructionTestTemplate("[^b][^a]", "1a", false);

    }

    @Test
    public void specialCharacterTest() {
        // single special chars
        contructionTestTemplate("\\|", "|", true);
        contructionTestTemplate("\\*", "*", true);
        contructionTestTemplate("\\(", "(", true);
        contructionTestTemplate("\\)", ")", true);
        contructionTestTemplate("(a)", "a)", false);
        contructionTestTemplate("\\\\", "\\", true);

        // integrated
        contructionTestTemplate("(\\()", "(", true);
        contructionTestTemplate("(\\))", ")", true);
        contructionTestTemplate("(\\))", "(", false);
        contructionTestTemplate("(\\()", ")", false);
        contructionTestTemplate("(\\|)", "|", true);
        contructionTestTemplate("(\\*)", "*", true);
        contructionTestTemplate("(\\**)", "*****", true);
        contructionTestTemplate("(\\**)", "", true);
        contructionTestTemplate("(ab)|(c\\|d)", "c", false);
        contructionTestTemplate("(ab)|(c\\|d)", "ab", true);
        contructionTestTemplate("(ab)|(c\\|d)", "c|d", true);
        contructionTestTemplate("(ab)|(c\\|d)", "c|", false);
        contructionTestTemplate("\\\\a", "\\a", true);
        contructionTestTemplate("\\\\*", "\\\\\\\\\\\\\\\\", true);

        contructionTestTemplate("\\n\\t\\r", "\n\t\r", true);
    }

    @Test
    public void wildcardTest() {
        contructionTestTemplate(".", "a", true);
        contructionTestTemplate(".", "b", true);
        contructionTestTemplate(".", "p", true);
        contructionTestTemplate("...", "123", true);
        contructionTestTemplate("...", "123", true);
        contructionTestTemplate("...", "1234", false);
        contructionTestTemplate("...", "12", false);

        contructionTestTemplate("\\.", ".", true);
        contructionTestTemplate("\\.", "a", false);
        contructionTestTemplate("\\.*", "......", true);

        contructionTestTemplate(".a.", "1a3", true);
        contructionTestTemplate(".a.", "1a9", true);
        contructionTestTemplate(".a.", "1b9", false);

        contructionTestTemplate("(..)|.", "ac", true);
        contructionTestTemplate("(..)|.", "8", true);
        contructionTestTemplate("(..)|.", "85", true);
        contructionTestTemplate("(..)|.", "85a", false);

        contructionTestTemplate(".*", "", true);
        contructionTestTemplate(".*", "1", true);
        contructionTestTemplate(".*", "123", true);
        contructionTestTemplate(".*", "68768f4a68s4ef8sv8274r9(^&*^*&", true);
        contructionTestTemplate("a.*", "a68768f4a68s4ef8sv8274r9(^&*^*&", true);
        contructionTestTemplate("ab.*", "ab68768f4a68s4ef8sv8274r9(^&*^*&", true);
        contructionTestTemplate("ab.*", "ac68768f4a68s4ef8sv8274r9(^&*^*&", false);
    }

    @Test
    public void optionalTest() {
        contructionTestTemplate("?", "1", true);
        contructionTestTemplate("?", "", true);
        contructionTestTemplate("??", "", true);
        contructionTestTemplate("??", "1", true);
        contructionTestTemplate("??", "12", true);
        contructionTestTemplate("??", "123", false);
        contructionTestTemplate("1234?", "1234", true);
        contructionTestTemplate("1234?", "12345", true);
        contructionTestTemplate("1234?", "123456", false);
        contructionTestTemplate("1234?", "1234567", false);
    }

}
