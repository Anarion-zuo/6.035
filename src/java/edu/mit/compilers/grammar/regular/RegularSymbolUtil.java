package edu.mit.compilers.grammar.regular;

import edu.mit.compilers.grammar.cfg.ContextFreeSentence;
import edu.mit.compilers.grammar.cfg.ContextFreeSymbol;
import edu.mit.compilers.grammar.cfg.ContextFreeTerminalSymbol;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/**
 * Concat:
 * Expr := AtomExpr Expr
 *      := AtomExpr
 *      := AlternateExpr
 *
 * AtomExpr := BracketExpr
 *          := ClosureExpr
 *          := char
 *
 * AlternateExpr := AtomExpr | Expr
 *               := AtomExpr
 *
 * BracketExpr := ( Expr )
 *
 * ClosureExpr := AtomExpr *
 */
public class RegularSymbolUtil {
    private Expr exprNode = new Expr();
    private AtomExpr atomExprNode = new AtomExpr();
    private AlternateExpr alternateExprNode = new AlternateExpr();
    private BracketExpr bracketExprNode = new BracketExpr();
    private ClosureExpr closureExprNode = new ClosureExpr();

    static HashSet<Character> specialChars = new HashSet<>(Arrays.asList('*', '(', ')', '|'));

    private static final char wildcardCharVal = 0;

    public RegularSymbolUtil() {
        exprNode.addSentences(3);
        exprNode.getSentence(0).addSymbol(atomExprNode);
        exprNode.getSentence(0).addSymbol(exprNode);
        exprNode.getSentence(1).addSymbol(atomExprNode);
        exprNode.getSentence(2).addSymbol(alternateExprNode);

        atomExprNode.addSentences(3);
        atomExprNode.getSentence(0).addSymbol(bracketExprNode);
        atomExprNode.getSentence(1).addSymbol(closureExprNode);
        atomExprNode.getSentence(2).addSymbol(new CharExpr(wildcardCharVal));

        alternateExprNode.addSentences(2);
        alternateExprNode.getSentence(0).addSymbol(atomExprNode);
        alternateExprNode.getSentence(0).addSymbol(new CharExpr('|'));
        alternateExprNode.getSentence(1).addSymbol(atomExprNode);

        bracketExprNode.addSentences(1);
        bracketExprNode.getSentence(0).addSymbol(new CharExpr('('));
        bracketExprNode.getSentence(0).addSymbol(exprNode);
        bracketExprNode.getSentence(0).addSymbol(new CharExpr(')'));

        closureExprNode.addSentences(1);
        closureExprNode.getSentence(0).addSymbol(atomExprNode);
        closureExprNode.getSentence(0).addSymbol(new CharExpr('*'));
    }

    class Expr extends ContextFreeSymbol {}

    class AtomExpr extends ContextFreeSymbol {}

    class AlternateExpr extends ContextFreeSymbol {}

    class BracketExpr extends ContextFreeSymbol {}

    class ClosureExpr extends ContextFreeSymbol {}

    class CharExpr extends ContextFreeTerminalSymbol {
        private final char ch;

        public CharExpr(char ch) {
            this.ch = ch;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CharExpr charExpr = (CharExpr) o;
            if (ch == wildcardCharVal) {
                if (charExpr.ch == wildcardCharVal) {
                    return true;
                }
                return !specialChars.contains(charExpr.ch);
            }
            if (charExpr.ch == wildcardCharVal) {
                return !specialChars.contains(this.ch);
            }
            return charExpr.ch == this.ch;
        }

        @Override
        public int hashCode() {
            return Objects.hash(ch);
        }
    }
}
