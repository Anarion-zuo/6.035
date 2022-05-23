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
 *
 * Then, some extension or grammar sugar
 *
 * char := SingleChar
 *      := \ SingleChar
 *
 * CharList := char CharList
 *          := char
 *
 * CharRange := char - char
 * CharRangeList := CharRange
 *               := CharRange CharRangeList
 *
 * BlockBracket := [ CharList ]
 *              := [ ]
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

    private SingleCharExpr wildcardSingleCharExpr = new SingleCharExpr(wildcardCharVal);
    private SingleCharExpr wildcardEscapeSingleCharExpr = new SingleCharExpr(wildcardCharVal, true);
    private CharExpr charExprNode = new CharExpr();
    private SingleCharExpr starSingleCharExpr = new SingleCharExpr('*', true);

    static HashSet<Character> specialChars = new HashSet<>(Arrays.asList('*', '(', ')', '|'));

    private static final char wildcardCharVal = 0;

    public RegularSymbolUtil() {
        exprNode.addSentences(3);
        exprNode.getSentence(Expr.atomSentenceIndex).addSymbol(atomExprNode);
        exprNode.getSentence(Expr.alternateSentenceIndex).addSymbol(alternateExprNode);
        exprNode.getSentence(Expr.atomListSentenceIndex).addSymbol(atomExprNode);
        exprNode.getSentence(Expr.atomListSentenceIndex).addSymbol(exprNode);

        atomExprNode.addSentences(3);
        atomExprNode.getSentence(0).addSymbol(charExprNode);
        atomExprNode.getSentence(1).addSymbol(bracketExprNode);
        atomExprNode.getSentence(2).addSymbol(closureNode);

        alternateExprNode.addSentences(2);
        alternateExprNode.getSentence(0).addSymbol(atomExprNode);
        alternateExprNode.getSentence(1).addSymbol(atomExprNode);
        alternateExprNode.getSentence(1).addSymbol(new SingleCharExpr('|', true));
        alternateExprNode.getSentence(1).addSymbol(exprNode);

        bracketExprNode.addSentences(1);
        bracketExprNode.getSentence(0).addSymbol(new SingleCharExpr('(', true));
        bracketExprNode.getSentence(0).addSymbol(exprNode);
        bracketExprNode.getSentence(0).addSymbol(new SingleCharExpr(')', true));

        closureNode.addSentences(2);
        closureNode.getSentence(0).addSymbol(charExprNode);
        closureNode.getSentence(0).addSymbol(starSingleCharExpr);
        closureNode.getSentence(1).addSymbol(bracketExprNode);
        closureNode.getSentence(1).addSymbol(starSingleCharExpr);

        charExprNode.addSentences(2);
        charExprNode.getSentence(CharExpr.escapeIndex).addSymbol(new SingleCharExpr('\\', true));
        charExprNode.getSentence(CharExpr.escapeIndex).addSymbol(wildcardEscapeSingleCharExpr);
        charExprNode.getSentence(CharExpr.singleCharIndex).addSymbol(wildcardSingleCharExpr);
    }

    public ContextFreeSymbol.MatchInfo rootMatchExaust(char[] input) {
        ContextFreeSentence inputStream = new ContextFreeSentence();
        for (char c : input) {
            inputStream.addSymbol(new SingleCharExpr(c));
        }
        var info = exprNode.matchExaust(inputStream.iterator());
        if (info.nextIterator != null) {
            System.out.println("Matched!!: " + info.matchedSentence);
        }
        return info;
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
        static final int atomListSentenceIndex = 1;
        static final int alternateSentenceIndex = 2;

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
        public Object afterMatch(int sentenceIndex, ContextFreeSentence matchedSentence, List<Object> childAttributes) throws InvalidAlgorithmParameterException {
            switch (sentenceIndex) {
                case atomSentenceIndex -> {
                    assert childAttributes.size() == 1;
                    return childAttributes.get(0);
                }
                case alternateSentenceIndex -> {
                    assert childAttributes.size() == 1;
                    return childAttributes.get(0);
                }
                case atomListSentenceIndex -> {
                    assert childAttributes.size() == 2;
                    return onAtomList((RegularGraph) childAttributes.get(0), (RegularGraph) childAttributes.get(1));
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
        public Object afterMatch(int sentenceIndex, ContextFreeSentence matchedSentence, List<Object> childAttributes) throws InvalidAlgorithmParameterException {
            assert childAttributes.size() == 1;
            return childAttributes.get(0);
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
        public Object afterMatch(int sentenceIndex, ContextFreeSentence matchedSentence, List<Object> childAttributes) throws InvalidAlgorithmParameterException {
            switch (sentenceIndex) {
                case atomIndex -> {
                    assert childAttributes.size() == 1;
                    return childAttributes.get(0);
                }
                case atomListIndex -> {
                    assert childAttributes.size() == 3;
                    return onAlterList((RegularGraph) childAttributes.get(0), (RegularGraph) childAttributes.get(2));
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
        public Object afterMatch(int sentenceIndex, ContextFreeSentence matchedSentence, List<Object> childAttributes) throws InvalidAlgorithmParameterException {
            assert childAttributes.size() == 3;
            return childAttributes.get(1);
        }
    }

    public class ClosureExpr extends ContextFreeSymbol {
        @Override
        public String toString() {
            return "Closure";
        }

        @Override
        public Object afterMatch(int sentenceIndex, ContextFreeSentence matchedSentence, List<Object> childAttributes) throws InvalidAlgorithmParameterException {
            assert childAttributes.size() == 2;
            var closuredGraph = (RegularGraph) childAttributes.get(0);
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
        public Object afterMatch(int sentenceIndex, ContextFreeSentence matchedSentence, List<Object> childAttributes) throws InvalidAlgorithmParameterException {
            return new RegularGraph();
        }
    }

    public class CharExpr extends ContextFreeSymbol {

        private static final int singleCharIndex = 0;
        private static final int escapeIndex = 1;

        @Override
        public String toString() {
            return "CharExpr";
        }

        private char escapeChar(char childChar) {
            char escapeChar = childChar;
            switch (childChar) {
                case 'n' -> {
                    escapeChar = '\n';
                }
                case 't' -> {
                    escapeChar = '\t';
                }
                default -> {
                    escapeChar = childChar;
                }
            }
            return escapeChar;
        }

        @Override
        public Object afterMatch(int sentenceIndex, ContextFreeSentence matchedSentence, List<Object> childAttributes) throws InvalidAlgorithmParameterException {
            switch (sentenceIndex) {
                case singleCharIndex -> {
                    assert childAttributes.size() == 1;
                    var attr = (SingleCharExpr.Attribute) childAttributes.get(0);
                    return attr.graph;
                }
                case escapeIndex -> {
                    assert childAttributes.size() == 2;
                    var backSlashAttribute = (SingleCharExpr.Attribute) childAttributes.get(0);
                    var escapeAttribute = (SingleCharExpr.Attribute) childAttributes.get(1);
                    assert backSlashAttribute.matchedChar == '\\';
                    char escapeChar = escapeChar(escapeAttribute.matchedChar);
                    RegularGraph graph = new RegularGraph();
                    graph.breakSourceAndDest();
                    graph.getSource().addDetermined(escapeChar, graph.getDest());
                    return graph;
                }
                default -> { throw new InvalidAlgorithmParameterException(); }
            }
        }

        @Override
        public MatchInfo match(ContextFreeSentence.Iterator symbolIterator) {
            return matchBySentenceOrder(symbolIterator);
        }
    }

    public class SingleCharExpr extends ContextFreeTerminalSymbol {
        private final char ch;

        /**
         * Call input the right hand side, grammar the left hand side.
         * Both sides generate symbols for comparison.
         * If a symbol on the left hand side is marked special,
         *      the symbol represents a character that is reserved in regular grammar.
         * If a symbol on the right hand side is marked special,
         *      the symbol is an escape character.
         */
        private final boolean isSpecial;

        public SingleCharExpr(char ch) {
            this.ch = ch;
            this.isSpecial = false;
        }
        public SingleCharExpr(char ch, boolean isSpecial) {
            this.ch = ch;
            this.isSpecial = isSpecial;
        }

        @Override
        public String toString() {
            return "SingleCharExpr{" +
                    ch +
                    '}';
        }

        @Override
        public Object getTerminalObject() {
            return ch;
        }

        class Attribute {
            final RegularGraph graph;
            final char matchedChar;

            public Attribute(RegularGraph graph, char matchedChar) {
                this.graph = graph;
                this.matchedChar = matchedChar;
            }
        }

        @Override
        public Object afterMatch(int sentenceIndex, ContextFreeSentence matchedSentence, List<Object> childAttributes) throws InvalidAlgorithmParameterException {
            assert childAttributes.size() == 1;
            var graph = new RegularGraph();
            graph.breakSourceAndDest();
            Character rhsCh = (Character) childAttributes.get(0);
            graph.getSource().addDetermined(rhsCh, graph.getDest());
            return new Attribute(graph, rhsCh);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SingleCharExpr singleCharExpr = (SingleCharExpr) o;
            if (isSpecial) {
                // this is a reserved symbol
                if (singleCharExpr.isSpecial) {
                    // an escape symbol cannot be a reserved symbol
                    System.out.println("(char match reserved : escape)");
                    return false;
                }
                // rhs is not an escape symbol
            }
            if (ch == wildcardCharVal) {
                if (singleCharExpr.ch == wildcardCharVal) {
                    System.out.print("(char match both wildcard)");
                    return true;
                }
                if (specialChars.contains(singleCharExpr.ch)) {
                    if (!isSpecial) {
                        System.out.printf("(char not match wildcard rhs with special char %c)", singleCharExpr.ch);
                        return false;
                    } else {
                        System.out.printf("(char match wildcard escape rhs %c)", singleCharExpr.ch);
                        return true;
                    }
                }
                return singleCharExpr.ch != '\\';
            }
            System.out.printf("(char match %c %c)", this.ch, singleCharExpr.ch);
            return singleCharExpr.ch == this.ch;
        }

        @Override
        public int hashCode() {
            return Objects.hash(ch);
        }
    }
}
