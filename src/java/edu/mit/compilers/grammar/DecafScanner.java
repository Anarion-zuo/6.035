// YOU CAN MODIFY ANYTHING IN THIS FILE, THIS IS JUST A SUGGESTED CLASS

package edu.mit.compilers.grammar;
import edu.mit.compilers.grammar.regular.RegularTokenSet;
import edu.mit.compilers.grammar.token.Token;
import edu.mit.compilers.grammar.token.decaf.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


public class DecafScanner {

    private final ByteBuffer buffer;
    private final RegularTokenSet tokenSet;
    private static HashSet<Character> whitespaces = new HashSet<>(List.of(' ', '\n', '\r', '\t', '\0'));
    private RegularTokenSet.StreamIterator streamIterator = null;
    private int curRow = 1;

    public DecafScanner(RandomAccessFile file) {
        var fileChannel = file.getChannel();
        try {
            buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tokenSet = RegularTokenSet.newByConfigFile(new File("resources/grammar/tokens.txt"));
    }

    /*protected void processMightMatchToken() throws TokenCannotMatchException {
        var tokenList = streamIterator.getPossibleMatched();
        int nonEmptyTokenCount = 0;
        for (var token : tokenList) {
            if (token.getMatchedLength() > 0) {
                System.out.println(token.getText());
                nonEmptyTokenCount++;
            }
        }
        if (nonEmptyTokenCount > 0) {
            throw new TokenCannotMatchException();
        }
    }*/

    /** might find this useful **/
    /*public Token nextToken() throws TokenCannotMatchException, TokenAmbiguousException {
        if (streamIterator == null) {
            streamIterator = tokenSet.streamIterator(0, buffer.limit(), buffer);
        }
        while (true) {
            boolean hasNext = streamIterator.next();
            if (!hasNext) {
                streamIterator.next();
                processMightMatchToken();
                return null;
            }
            var tokenList = streamIterator.getMatched();
            var matchedTokenList = new LinkedList<Token>();
            if (!tokenList.isEmpty()) {
                for (var token : tokenList) {
                    token.setTextRow(curRow);
                    if (token.isMatched()) {
                        matchedTokenList.add(token);
                    }
                }
            } else {
                processMightMatchToken();
            }
            if (!matchedTokenList.isEmpty()) {
                streamIterator.acceptMatched();
                if (matchedTokenList.size() > 1) {
                    throw new TokenAmbiguousException();
                }
                var token = matchedTokenList.getLast();
                if (!token.shouldIgnore()) {
                    token.setTextRow(curRow);
                    return token;
                }
                if (token.getClass() == Newline.class
                        || token.getClass() == SinglelineComment.class) {
                    ++curRow;
                }
            }
        }
        //throw new TokenCannotMatchException();
    }*/

    public Token nextToken() throws TokenCannotMatchException {
        if (streamIterator == null) {
            streamIterator = tokenSet.streamIterator(0, buffer.limit(), buffer);
        }
        while (true) {
            if (!streamIterator.hasNext()) {
                return null;
            }
            Token token = null;
            try {
                token = streamIterator.nextToken();
            } catch (TokenAmbiguousException | UndefinedTokenException e) {
                throw new RuntimeException(e);
            }
            if (token == null || !token.isMatched()) {
                throw new TokenCannotMatchException();
            }
            if (token.getClass() == Newline.class
                    || token.getClass() == SinglelineComment.class) {
                ++curRow;
            }
            if (!token.shouldIgnore()) {
                token.setTextRow(curRow);
                return token;
            }
        }
    }

    /** Whether to display debug information. */
    public void setTrace(boolean shouldTrace) {

    }

}
