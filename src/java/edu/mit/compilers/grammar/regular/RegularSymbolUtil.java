package edu.mit.compilers.grammar.regular;

import edu.mit.compilers.grammar.cfg.ContextFreeEpsilonSymbol;
import edu.mit.compilers.grammar.cfg.ContextFreeSentence;
import edu.mit.compilers.grammar.cfg.ContextFreeSymbol;
import edu.mit.compilers.grammar.cfg.ContextFreeTerminalSymbol;

import java.util.*;

/**
 * Concat:
 * Expr := AtomExpr
 *      := AlternateExpr
 *      := AtomExpr Expr
 *
 * AtomExpr := char
 *          := BracketExpr
 *          := ClosureExpr
 *
 * AlternateExpr := AtomExpr
 *               := AtomExpr | Expr
 *
 * BracketExpr := ( Expr )
 *
 * This causes left recursion:
 * ClosureExpr := AtomExpr *
 * Therefore it is rewritten to this:
 * ClosureExpr := char *
 *             := BracketExpr *
 *      where a** is not allowed
 */
public class RegularSymbolUtil {
    private Expr exprNode = new Expr();
    private AtomExpr atomExprNode = new AtomExpr();
    private AlternateExpr alternateExprNode = new AlternateExpr();
    private BracketExpr bracketExprNode = new BracketExpr();
    //private ClosureExpr closureExprNode = new ClosureExpr();
    private ClosureExpr closureNode = new ClosureExpr();
    //private Closure2Expr closure2Node = new Closure2Expr();
    private EpsilonExpr epsilonExpr = new EpsilonExpr();

    private CharExpr wildcardCharExpr = new CharExpr(wildcardCharVal);
    private CharExpr starCharExpr = new CharExpr('*');

    static HashSet<Character> specialChars = new HashSet<>(Arrays.asList('*', '(', ')', '|'));

    private static final char wildcardCharVal = 0;

    public RegularSymbolUtil() {
        exprNode.addSentences(3);
        exprNode.getSentence(Expr.atomSentenceIndex).addSymbol(atomExprNode);
        exprNode.getSentence(Expr.alternateSentenceIndex).addSymbol(alternateExprNode);
        exprNode.getSentence(Expr.atomListSentenceIndex).addSymbol(atomExprNode);
        exprNode.getSentence(Expr.atomListSentenceIndex).addSymbol(exprNode);

        atomExprNode.addSentences(3);
        atomExprNode.getSentence(0).addSymbol(wildcardCharExpr);
        atomExprNode.getSentence(1).addSymbol(bracketExprNode);
        atomExprNode.getSentence(2).addSymbol(closureNode);

        alternateExprNode.addSentences(2);
        alternateExprNode.getSentence(0).addSymbol(atomExprNode);
        alternateExprNode.getSentence(1).addSymbol(atomExprNode);
        alternateExprNode.getSentence(1).addSymbol(new CharExpr('|'));
        alternateExprNode.getSentence(1).addSymbol(exprNode);

        bracketExprNode.addSentences(1);
        bracketExprNode.getSentence(0).addSymbol(new CharExpr('('));
        bracketExprNode.getSentence(0).addSymbol(exprNode);
        bracketExprNode.getSentence(0).addSymbol(new CharExpr(')'));

        closureNode.addSentences(2);
        closureNode.getSentence(0).addSymbol(wildcardCharExpr);
        closureNode.getSentence(0).addSymbol(starCharExpr);
        closureNode.getSentence(1).addSymbol(bracketExprNode);
        closureNode.getSentence(1).addSymbol(starCharExpr);
    }

    public boolean isFormatCorrect(char[] input) {
        ContextFreeSentence inputStream = new ContextFreeSentence();
        for (char c : input) {
            inputStream.addSymbol(new CharExpr(c));
        }
        var info = exprNode.matchExaust(inputStream.iterator());
        return info.nextIterator != null;
    }

    class Expr extends ContextFreeSymbol {
        static final int atomSentenceIndex = 0;
        static final int alternateSentenceIndex = 1;
        static final int atomListSentenceIndex = 2;
        @Override
        public String toString() {
            return "Expr";
        }

        @Override
        public void afterMatch(int sentenceIndex, ContextFreeSentence matchedSentence) {

        }
    }

    class AtomExpr extends ContextFreeSymbol {
        @Override
        public String toString() {
            return "AtomExpr";
        }
    }

    class AlternateExpr extends ContextFreeSymbol {
        @Override
        public String toString() {
            return "AlternateExpr";
        }
    }

    class BracketExpr extends ContextFreeSymbol {
        @Override
        public String toString() {
            return "BracketExpr";
        }
    }

    class ClosureExpr extends ContextFreeSymbol {
        @Override
        public String toString() {
            return "Closure";
        }
    }

    class EpsilonExpr extends ContextFreeEpsilonSymbol {
        @Override
        public String toString() {
            return "EpsilonExpr";
        }
    }

    class CharExpr extends ContextFreeTerminalSymbol {
        private final char ch;

        public CharExpr(char ch) {
            this.ch = ch;
        }

        @Override
        public String toString() {
            return "CharExpr{" +
                    ch +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CharExpr charExpr = (CharExpr) o;
            if (ch == wildcardCharVal) {
                if (charExpr.ch == wildcardCharVal) {
                    System.out.print("(char match wildcard)");
                    return true;
                }
                System.out.print("(char match wildcard)");
                return !specialChars.contains(charExpr.ch);
            }
            if (charExpr.ch == wildcardCharVal) {
                System.out.print("(char match wildcard)");
                return !specialChars.contains(this.ch);
            }
            System.out.printf("(char match %c %c)", this.ch, charExpr.ch);
            return charExpr.ch == this.ch;
        }

        @Override
        public int hashCode() {
            return Objects.hash(ch);
        }
    }
}
