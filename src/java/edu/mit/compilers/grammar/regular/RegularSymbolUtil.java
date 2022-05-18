package edu.mit.compilers.grammar.regular;

import edu.mit.compilers.grammar.cfg.ContextFreeEpsilonSymbol;
import edu.mit.compilers.grammar.cfg.ContextFreeSentence;
import edu.mit.compilers.grammar.cfg.ContextFreeSymbol;
import edu.mit.compilers.grammar.cfg.ContextFreeTerminalSymbol;

import java.security.InvalidAlgorithmParameterException;
import java.util.*;

/**
 * Concat:
 * Expr := AtomExpr
 *      := AtomExpr Expr
 *      := AlternateExpr
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

    public ContextFreeSymbol.MatchInfo rootMatchExaust(char[] input) {
        ContextFreeSentence inputStream = new ContextFreeSentence();
        for (char c : input) {
            inputStream.addSymbol(new CharExpr(c));
        }
        return exprNode.matchExaust(inputStream.iterator());
    }

    public boolean isFormatCorrect(char[] input) {
        var info = rootMatchExaust(input);
        return info.nextIterator != null;
    }

    public Expr getExprNode() {
        return exprNode;
    }

    public class Expr extends ContextFreeSymbol {
        static final int atomSentenceIndex = 0;
        static final int alternateSentenceIndex = 2;
        static final int atomListSentenceIndex = 1;

        @Override
        public String toString() {
            return "Expr";
        }

        private Object onAtomList(RegularGraph atomGraph, RegularGraph rightGraph) {
            // concat
            var graph = new RegularGraph();
            graph.breakSourceAndDest();
            atomGraph.getDest().addNonDetermined(rightGraph.getSource());
            rightGraph.getDest().addNonDetermined(graph.getDest());
            graph.getSource().addNonDetermined(atomGraph.getSource());
            return graph;
        }

        @Override
        public Object afterMatch(int sentenceIndex, ContextFreeSentence matchedSentence, List<Object> childObjects) throws InvalidAlgorithmParameterException {
            switch (sentenceIndex) {
                case atomSentenceIndex -> {
                    assert childObjects.size() == 1;
                    return childObjects.get(0);
                }
                case alternateSentenceIndex -> {
                    assert childObjects.size() == 1;
                    return childObjects.get(0);
                }
                case atomListSentenceIndex -> {
                    assert childObjects.size() == 2;
                    return onAtomList((RegularGraph) childObjects.get(0), (RegularGraph) childObjects.get(1));
                }
                default -> {
                    throw new InvalidAlgorithmParameterException();
                }
            }
        }
    }

    public class AtomExpr extends ContextFreeSymbol {
        @Override
        public String toString() {
            return "AtomExpr";
        }

        @Override
        public Object afterMatch(int sentenceIndex, ContextFreeSentence matchedSentence, List<Object> childObjects) throws InvalidAlgorithmParameterException {
            assert childObjects.size() == 1;
            return childObjects.get(0);
        }
    }

    public class AlternateExpr extends ContextFreeSymbol {
        private static final int atomIndex = 0;
        private static final int atomListIndex = 1;

        @Override
        public String toString() {
            return "AlternateExpr";
        }

        public Object onAlterList(RegularGraph atomNode, RegularGraph listNode) {
            RegularGraph graph = new RegularGraph();
            graph.breakSourceAndDest();
            graph.getSource().addNonDetermined(atomNode.getSource());
            graph.getSource().addNonDetermined(listNode.getSource());
            atomNode.getDest().addNonDetermined(graph.getDest());
            listNode.getDest().addNonDetermined(graph.getDest());
            return graph;
        }

        @Override
        public Object afterMatch(int sentenceIndex, ContextFreeSentence matchedSentence, List<Object> childObjects) throws InvalidAlgorithmParameterException {
            switch (sentenceIndex) {
                case atomIndex -> {
                    assert childObjects.size() == 1;
                    return childObjects.get(0);
                }
                case atomListIndex -> {
                    assert childObjects.size() == 3;
                    return onAlterList((RegularGraph) childObjects.get(0), (RegularGraph) childObjects.get(2));
                }
                default -> {
                    throw new InvalidAlgorithmParameterException();
                }
            }
        }
    }

    public class BracketExpr extends ContextFreeSymbol {
        @Override
        public String toString() {
            return "BracketExpr";
        }

        @Override
        public Object afterMatch(int sentenceIndex, ContextFreeSentence matchedSentence, List<Object> childObjects) throws InvalidAlgorithmParameterException {
            assert childObjects.size() == 3;
            return childObjects.get(1);
        }
    }

    public class ClosureExpr extends ContextFreeSymbol {
        @Override
        public String toString() {
            return "Closure";
        }

        @Override
        public Object afterMatch(int sentenceIndex, ContextFreeSentence matchedSentence, List<Object> childObjects) throws InvalidAlgorithmParameterException {
            assert childObjects.size() == 2;
            var closuredGraph = (RegularGraph) childObjects.get(0);
            var graph = new RegularGraph();
            // does not break source to dest here
            closuredGraph.getDest().addNonDetermined(closuredGraph.getSource());
            graph.getSource().addNonDetermined(closuredGraph.getSource());
            closuredGraph.getDest().addNonDetermined(graph.getDest());
            return graph;
        }
    }

    public class EpsilonExpr extends ContextFreeEpsilonSymbol {
        @Override
        public String toString() {
            return "EpsilonExpr";
        }

        @Override
        public Object afterMatch(int sentenceIndex, ContextFreeSentence matchedSentence, List<Object> childObjects) throws InvalidAlgorithmParameterException {
            return new RegularGraph();
        }
    }

    public class CharExpr extends ContextFreeTerminalSymbol {
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
        protected Object getTerminalObject() {
            return ch;
        }

        @Override
        public Object afterMatch(int sentenceIndex, ContextFreeSentence matchedSentence, List<Object> childObjects) throws InvalidAlgorithmParameterException {
            assert childObjects.size() == 1;
            var graph = new RegularGraph();
            graph.breakSourceAndDest();
            Character rhsCh = (Character) childObjects.get(0);
            graph.getSource().addDetermined(rhsCh, graph.getDest());
            return graph;
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
