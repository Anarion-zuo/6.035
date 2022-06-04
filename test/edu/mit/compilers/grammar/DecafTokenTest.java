package edu.mit.compilers.grammar;

import edu.mit.compilers.grammar.regular.RegularTokenSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class DecafTokenTest {
    RegularTokenSet tokenSet;
    @Before
    public void before() {
        tokenSet = RegularTokenSet.newByConfigFile(new File("resources/grammar/tokens.txt"));
    }

    @Test
    public void simpleTest() throws IOException {

        var fileChannel = new RandomAccessFile("resources/provided/scanner/char1", "rw").getChannel();
        MappedByteBuffer out = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
        var iter = tokenSet.streamIterator(0, (int) fileChannel.size(), out);
        while (true) {
            boolean hasNext = iter.next();
            if (!hasNext) {
                break;
            }
            var tokenList = iter.getMatched();
            if (!tokenList.isEmpty()) {
                System.out.println("matched tokens: ");
                for (var token : tokenList) {
                    System.out.println(token.getText());
                }
                iter.acceptMatched();
            }
        }
    }
}
