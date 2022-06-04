// YOU CAN MODIFY ANYTHING IN THIS FILE, THIS IS JUST A SUGGESTED CLASS

package edu.mit.compilers.grammar;
import edu.mit.compilers.grammar.regular.RegularTokenSet;
import edu.mit.compilers.grammar.token.Token;

import java.io.*;
import java.util.HashSet;
import java.util.List;


public class DecafScanner {

    private final Reader reader;
    private RegularTokenSet tokenSet;
    private static HashSet<Character> whitespaces = new HashSet<>(List.of(' ', '\n', '\r', '\t', '\0'));
    StringBuffer buffer = new StringBuffer();

    public DecafScanner(InputStream in) {
        reader = new BufferedReader(new InputStreamReader(in));
        tokenSet = RegularTokenSet.newByConfigFile(new File("resources/grammar/tokens.txt"));
    }


    /** might find this useful **/
    public Token nextToken() {
        char curChar = 0;
        try {
            curChar = (char) reader.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //if (curChar)
        return null;
    }

    /** Whether to display debug information. */
    public void setTrace(boolean shouldTrace) {

    }

}
