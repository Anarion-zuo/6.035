package edu.mit.compilers.grammar;

import edu.mit.compilers.grammar.regular.RegularSymbolUtil;
import org.junit.Assert;
import org.junit.Test;

public class RegularExprParseTest {
    @Test
    public void formatTest() {
        RegularSymbolUtil regularSymbolUtil = new RegularSymbolUtil();
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
}
