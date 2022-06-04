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
        var file = new RandomAccessFile("resources/provided/scanner/hexlit1", "rw");
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
            builder.append(s);
        }

        public String getCollected() {
            return builder.toString();
        }
    }

    private void testScannerProvidedFile(String fileName, boolean formatCorrect) throws IOException, TokenAmbiguousException, TokenCannotMatchException {
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
    }
}
