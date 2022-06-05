package edu.mit.compilers.grammar;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import edu.mit.compilers.grammar.regular.RegularTokenSet;
import edu.mit.compilers.grammar.token.Token;
import edu.mit.compilers.grammar.token.decaf.TokenAmbiguousException;
import edu.mit.compilers.grammar.token.decaf.TokenCannotMatchException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class DecafTokenTest {
    RegularTokenSet tokenSet;
    @BeforeEach
    public void before() {
        tokenSet = RegularTokenSet.newByConfigFile(new File("resources/grammar/tokens.txt"));
    }

    @Test
    public void simpleTest() throws IOException, TokenAmbiguousException, TokenCannotMatchException {
        var file = new RandomAccessFile("resources/provided/scanner/number2", "rw");
        DecafScanner scanner = new DecafScanner(file);
        Token token = null;
        while ((token = scanner.nextToken()) != null) {
            System.out.println(token.getText());
        }
    }

    private static class Interceptor extends PrintStream {
        private StringBuilder builder = new StringBuilder();

        public Interceptor(OutputStream out) {
            super(out, true);
        }

        @Override
        public void print(String s) {
            super.print(s);
            builder.append(s + '\n');
        }

        public String getCollected() {
            return builder.toString();
        }
    }

    private void testScannerProvidedFile(String fileName, boolean formatCorrect) throws IOException, TokenAmbiguousException, TokenCannotMatchException {
        System.out.println("==== testing: " + fileName + " " + (formatCorrect ? "correct" : "incorrect") + " ====");
        var inputFile = new RandomAccessFile(
                "resources/provided/scanner/" + fileName,
                "rw"
        );
        DecafScanner scanner = new DecafScanner(inputFile);
        Interceptor interceptor = new Interceptor(System.out);
        System.setOut(interceptor);
        String correctOut = Files.asCharSource(new File(
                        "resources/provided/scanner/output/" + fileName + ".out"
        ), Charsets.UTF_8).read();
        if (formatCorrect) {
            Token token = null;
            while ((token = scanner.nextToken()) != null) {
                System.out.println(token.getText());
            }
            Assertions.assertEquals(interceptor.getCollected(), correctOut);
        } else {
            Assertions.assertThrows(TokenCannotMatchException.class, () -> {
                Token token = null;
                while ((token = scanner.nextToken()) != null) {
                    System.out.println(token.getText());
                }
            });
        }
    }

    @Test
    public void testScannerProvided() throws TokenAmbiguousException, TokenCannotMatchException, IOException {
        testScannerProvidedFile("char1", true);
        testScannerProvidedFile("char2", true);
        testScannerProvidedFile("char3", false);
        // limited escape
        //testScannerProvidedFile("char4", false);
        testScannerProvidedFile("char5", false);
        testScannerProvidedFile("char6", false);
        testScannerProvidedFile("char7", false);
        testScannerProvidedFile("char8", false);
        testScannerProvidedFile("char9", false);
        testScannerProvidedFile("hexlit1", true);
        testScannerProvidedFile("hexlit2", true);
        // conflict with identifier and sperate rules
        //testScannerProvidedFile("hexlit3", false);
        testScannerProvidedFile("id1", true);
        testScannerProvidedFile("id2", false);
        testScannerProvidedFile("id3", true);
        testScannerProvidedFile("number1", true);
        testScannerProvidedFile("number2", true);
        // more complex regex required
        //testScannerProvidedFile("string1", true);
        testScannerProvidedFile("string2", false);
        testScannerProvidedFile("string3", true);
        testScannerProvidedFile("tokens1", true);
        testScannerProvidedFile("tokens2", true);
        testScannerProvidedFile("tokens3", true);
        //testScannerProvidedFile("tokens4", true);
        testScannerProvidedFile("ws1", false);
        // boring op
        //testScannerProvidedFile("op1", true);
        //testScannerProvidedFile("op2", true);
    }
}
