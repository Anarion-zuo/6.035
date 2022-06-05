package edu.mit.compilers.grammar.token.decaf;

import com.google.common.base.Utf8;
import edu.mit.compilers.grammar.token.Token;

import java.util.HashSet;
import java.util.List;

public class DecafToken extends Token {
    private static final HashSet<String> reservedWords = new HashSet<>(List.of(
        "bool", "break", "import", "continue", "else", "false", "for",
            "while", "if", "int", "return", "len", "true", "void", "boolean", "callout", "class"
    ));

    protected static char escapeCharacter(char ch) {
        switch (ch) {
            case 'r' -> {
                return '\r';
            }
            case 'n' -> {
                return '\n';
            }
            case 't' -> {
                return '\t';
            }
            case '\\' -> {
                return '\\';
            }
            default -> {
                return ch;
            }
        }
    }

    protected static String reverseEscapeCharacter(char ch) {
        switch (ch) {
            case '\r' -> {
                return "\\r";
            }
            case '\n' -> {
                return "\\n";
            }
            case '\t' -> {
                return "\\t";
            }
            case '\\' -> {
                return "\\\\";
            }
            default -> {
                return "" + ch;
            }
        }
    }

    public DecafToken(String matchedText) {
        super(matchedText);
    }

    public static boolean isReservedWord(String word) {
        return reservedWords.contains(word);
    }

    protected static boolean isUtf8Viewable(char ch) {
        return !(
                (ch >= 0 && ch <= 0x1f) ||
                        (ch >= 0x7f && ch <= 0x9f)
                );
    }
}
