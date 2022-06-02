package edu.mit.compilers.grammar;

import edu.mit.compilers.grammar.regular.RegularTokenSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class DecafTokenTest {
    RegularTokenSet tokenSet;
    @Before
    public void before() {
        tokenSet = RegularTokenSet.newByConfigFile(new File("resources/grammar/tokens.txt"));
    }

    @Test
    public void simpleTest() {
        var matchedList = tokenSet.matchLongest("\"a\"");
        for (var info : matchedList) {
            System.out.println(info.tokenName + " " + info.length);
        }
        matchedList = tokenSet.matchLongest("'a'");
        for (var info : matchedList) {
            System.out.println(info.tokenName + " " + info.length);
        }
        matchedList = tokenSet.matchLongest("/*a*/");
        for (var info : matchedList) {
            System.out.println(info.tokenName + " " + info.length);
        }
        matchedList = tokenSet.matchLongest("//\n");
        for (var info : matchedList) {
            System.out.println(info.tokenName + " " + info.length);
        }
        //Assert.assertEquals(1, matchedList.size());
    }
}
