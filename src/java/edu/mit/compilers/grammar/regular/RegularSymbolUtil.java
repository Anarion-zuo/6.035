package edu.mit.compilers.grammar.regular;

import edu.mit.compilers.grammar.cfg.ContextFreeEpsilonSymbol;
import edu.mit.compilers.grammar.cfg.ContextFreeSentence;
import edu.mit.compilers.grammar.cfg.ContextFreeSymbol;
import edu.mit.compilers.grammar.cfg.ContextFreeTerminalSymbol;
import edu.mit.compilers.tools.Logger;

import java.security.InvalidAlgorithmParameterException;
import java.util.*;

/**
 * Concat:
 * Expr := AtomExpr
 *      := AtomExpr Expr
 *      := AlternateExpr
 *
 * AtomExpr := char
 *          := EnumIncludeExpr
 *          := EnumExcludeExpr
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
 *             := EnumIncludeExpr *
 *             := EnumExcludeExpr *
 *      where a** is not allowed
 *
 * Then, some extension or grammar sugar
 *
 * char := SingleChar
 *      := \ SingleChar
 * SingleChar := wildcard
 *            := optional
 *
 * CharList := char
 *          := char CharList
 *
 * CharRange := char - char
 * CharRangeList := CharRange
 *               := CharRange CharRangeList
 *
 * EnumExpr := CharList
 *          := CharRangeList
 *
 * EnumIncludeExpr := [ EnumExpr ]
 *
 * EnumExceptExpr := [ ^ EnumExpr ]
 */
public class RegularSymbolUtil {
    private Expr exprNode = new Expr();
    private AtomExpr atomExprNode = new AtomExpr();
    private AlternateExpr alternateExprNode = new AlternateExpr();
    private BracketExpr bracketExprNode = new BracketExpr();
    //private ClosureExpr closureExprNode = new ClosureExpr();
    private ClosureExpr closureNode = new ClosureExpr();
    //private Closure2Expr closure2Node = new Closure2Expr();
    //private EpsilonExpr epsilonExpr = new EpsilonExpr();

    private SingleCharExpr wildcardSingleCharExpr = new SingleCharExpr(wildcardCharVal);
    private SingleCharExpr wildcardEscapeSingleCharExpr = new SingleCharExpr(wildcardCharVal, true);
    private CharExpr charExprNode = new CharExpr();
    private SingleCharExpr starSingleCharExpr = new SingleCharExpr('*', true);
    private CharListExpr charListExpr = new CharListExpr();
    private CharRange charRangeExpr = new CharRange();
    private CharRangeList charRangeListExpr = new CharRangeList();
    private EnumExpr enumExpr = new EnumExpr();
    private EnumIncludeExpr enumIncludeExpr = new EnumIncludeExpr();
    private EnumExcludeExpr enumExcludeExpr = new EnumExcludeExpr();

    static HashSet<Character> specialChars = new HashSet<>(Arrays.asList('*', '(', ')', '|', '[', ']', '^'));

    private static final char wildcardCharVal = 0;

    public RegularSymbolUtil() {
        exprNode.addSentences(3);
        exprNode.getSentence(Expr.atomSentenceIndex).addSymbol(atomExprNode);
        exprNode.getSentence(Expr.alternateSentenceIndex).addSymbol(alternateExprNode);
        exprNode.getSentence(Expr.atomListSentenceIndex).addSymbol(atomExprNode);
        exprNode.getSentence(Expr.atomListSentenceIndex).addSymbol(exprNode);

        atomExprNode.addSentences(5);
        atomExprNode.getSentence(0).addSymbol(charExprNode);
        atomExprNode.getSentence(1).addSymbol(bracketExprNode);
        atomExprNode.getSentence(2).addSymbol(closureNode);
        atomExprNode.getSentence(3).addSymbol(enumIncludeExpr);
        atomExprNode.getSentence(4).addSymbol(enumExcludeExpr);

        alternateExprNode.addSentences(2);
        alternateExprNode.getSentence(0).addSymbol(atomExprNode);
        alternateExprNode.getSentence(1).addSymbol(atomExprNode);
        alternateExprNode.getSentence(1).addSymbol(new SingleCharExpr('|', true));
        alternateExprNode.getSentence(1).addSymbol(exprNode);

        bracketExprNode.addSentences(1);
        bracketExprNode.getSentence(0).addSymbol(new SingleCharExpr('(', true));
        bracketExprNode.getSentence(0).addSymbol(exprNode);
        bracketExprNode.getSentence(0).addSymbol(new SingleCharExpr(')', true));

        closureNode.addSentences(4);
        closureNode.getSentence(0).addSymbol(charExprNode);
        closureNode.getSentence(0).addSymbol(starSingleCharExpr);
        closureNode.getSentence(1).addSymbol(bracketExprNode);
        closureNode.getSentence(1).addSymbol(starSingleCharExpr);
        closureNode.getSentence(2).addSymbol(enumIncludeExpr);
        closureNode.getSentence(2).addSymbol(starSingleCharExpr);
        closureNode.getSentence(3).addSymbol(enumExcludeExpr);
        closureNode.getSentence(3).addSymbol(starSingleCharExpr);

        charExprNode.addSentences(2);
        charExprNode.getSentence(CharExpr.escapeIndex).addSymbol(new SingleCharExpr('\\', true));
        charExprNode.getSentence(CharExpr.escapeIndex).addSymbol(wildcardEscapeSingleCharExpr);
        charExprNode.getSentence(CharExpr.singleCharIndex).addSymbol(wildcardSingleCharExpr);

        charListExpr.addSentences(2);
        charListExpr.getSentence(CharListExpr.charTerminalIndex).addSymbol(charExprNode);
        charListExpr.getSentence(CharListExpr.charListIndex).addSymbol(charExprNode);
        charListExpr.getSentence(CharListExpr.charListIndex).addSymbol(charListExpr);

        charRangeExpr.addSentences(1);
        charRangeExpr.getSentence(0).addSymbol(charExprNode);
        charRangeExpr.getSentence(0).addSymbol(new SingleCharExpr('-', true));
        charRangeExpr.getSentence(0).addSymbol(charExprNode);

        charRangeListExpr.addSentences(2);
        charRangeListExpr.getSentence(CharRangeList.rangeTerminalIndex).addSymbol(charRangeExpr);
        charRangeListExpr.getSentence(CharRangeList.rangeListIndex).addSymbol(charRangeExpr);
        charRangeListExpr.getSentence(CharRangeList.rangeListIndex).addSymbol(charRangeListExpr);

        enumExpr.addSentences(2);
        enumExpr.getSentence(0).addSymbol(charRangeListExpr);
        enumExpr.getSentence(1).addSymbol(charListExpr);

        enumIncludeExpr.addSentences(1);
        enumIncludeExpr.getSentence(0).addSymbol(new SingleCharExpr('[', true));
        enumIncludeExpr.getSentence(0).addSymbol(enumExpr);
        enumIncludeExpr.getSentence(0).addSymbol(new SingleCharExpr(']', true));

        enumExcludeExpr.addSentences(1);
        enumExcludeExpr.getSentence(0).addSymbol(new SingleCharExpr('[', true));
        enumExcludeExpr.getSentence(0).addSymbol(new SingleCharExpr('^', true));
        enumExcludeExpr.getSentence(0).addSymbol(enumExpr);
        enumExcludeExpr.getSentence(0).addSymbol(new SingleCharExpr(']', true));
    }

    public ContextFreeSymbol.MatchInfo rootMatchExaust(char[] input) {
        ContextFreeSentence inputStream = new ContextFreeSentence();
        for (char c : input) {
            inputStream.addSymbol(new SingleCharExpr(c));
        }
        var info = exprNode.matchExaust(inputStream.iterator());
        if (info.nextIterator != null) {
            Logger.getInstance().printf("RegularParse", "Matched!!: " + info.matchedSentence);
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
            atomGraph.getError().addNonDetermined(graph.getError());
            rightGraph.getError().addNonDetermined(graph.getError());
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

        public Object onAlterList(RegularGraph atomGraph, RegularGraph listGraph) {
            RegularGraph graph = new RegularGraph();
            graph.breakSourceAndDest();
            graph.getSource().addNonDetermined(atomGraph.getSource());
            graph.getSource().addNonDetermined(listGraph.getSource());
            atomGraph.getDest().addNonDetermined(graph.getDest());
            listGraph.getDest().addNonDetermined(graph.getDest());
            atomGraph.getError().addNonDetermined(graph.getError());
            listGraph.getError().addNonDetermined(graph.getError());
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
            closuredGraph.getError().addNonDetermined(graph.getError());
            return graph;
        }
    }

    /*public class EpsilonExpr extends ContextFreeEpsilonSymbol {
        @Override
        public String toString() {
            return "EpsilonExpr";
        }

        @Override
        public Object afterMatch(int sentenceIndex, ContextFreeSentence matchedSentence, List<Object> childAttributes) throws InvalidAlgorithmParameterException {
            return new RegularGraph();
        }
    }*/

    public class CharExpr extends ContextFreeSymbol {

        private static final int singleCharIndex = 1;
        private static final int escapeIndex = 0;

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
                case 'r' -> {
                    escapeChar = '\r';
                }
                default -> {
                    escapeChar = childChar;
                }
            }
            return escapeChar;
        }

        private RegularGraph onDotWildCard() {
            RegularGraph graph = new RegularGraph();
            graph.breakSourceAndDest();
            RegularNode dotNode = new RegularWildcardNode(graph.getDest(), graph.getError());
            graph.getSource().addNonDetermined(dotNode);
            return graph;
        }

        private RegularGraph onOptional() {
            RegularGraph graph = new RegularGraph();
            RegularNode dotNode = new RegularWildcardNode(graph.getDest(), graph.getError());
            graph.getSource().addNonDetermined(dotNode);
            return graph;
        }

        @Override
        public Object afterMatch(int sentenceIndex, ContextFreeSentence matchedSentence, List<Object> childAttributes) throws InvalidAlgorithmParameterException {
            switch (sentenceIndex) {
                case singleCharIndex -> {
                    assert childAttributes.size() == 1;
                    var attr = (SingleCharExpr.Attribute) childAttributes.get(0);
                    if (attr.matchedChar == '.') {
                        return onDotWildCard();
                    }
                    if (attr.matchedChar == '?') {
                        return onOptional();
                    }
                    return attr.graph;
                }
                case escapeIndex -> {
                    assert childAttributes.size() == 2;
                    var backSlashAttribute = (SingleCharExpr.Attribute) childAttributes.get(0);
                    var escapeAttribute = (SingleCharExpr.Attribute) childAttributes.get(1);
                    assert backSlashAttribute.matchedChar == '\\';
                    char escapeChar = escapeChar(escapeAttribute.matchedChar);
                    return new RegularSingleCharGraph(escapeChar);
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
            Character rhsCh = (Character) childAttributes.get(0);
            return new Attribute(new RegularSingleCharGraph(rhsCh), rhsCh);
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
                    Logger.getInstance().printf("RegularParse", "(char match reserved : escape)");
                    return false;
                }
                // rhs is not an escape symbol
            }
            if (ch == wildcardCharVal) {
                if (singleCharExpr.ch == wildcardCharVal) {
                    Logger.getInstance().printf("RegularParse", "(char match both wildcard)");
                    return true;
                }
                if (specialChars.contains(singleCharExpr.ch)) {
                    if (!isSpecial) {
                        Logger.getInstance().printf("RegularParse", "(char not match wildcard rhs with special char %c)", singleCharExpr.ch);
                        return false;
                    } else {
                        Logger.getInstance().printf("RegularParse", "(char match wildcard escape rhs %c)", singleCharExpr.ch);
                        return true;
                    }
                }
                return true;
            }
            Logger.getInstance().printf("RegularParse", "(char match %c %c)", this.ch, singleCharExpr.ch);
            return singleCharExpr.ch == this.ch;
        }

        @Override
        public int hashCode() {
            return Objects.hash(ch);
        }
    }

    public class CharListExpr extends ContextFreeSymbol {
        private static final int charTerminalIndex = 0;
        private static final int charListIndex = 1;

        @Override
        public String toString() {
            return "CharListExpr";
        }

        private RegularGraph onCharList(RegularGraph graphChar, RegularGraph graphCharList) {
            RegularGraph graph = new RegularGraph();
            graph.breakSourceAndDest();
            graph.getSource().addNonDetermined(graphChar.getSource());
            graph.getSource().addNonDetermined(graphCharList.getSource());
            graphChar.getDest().addNonDetermined(graph.getDest());
            graphCharList.getDest().addNonDetermined(graph.getDest());
            graphChar.getError().addNonDetermined(graph.getError());
            graphCharList.getError().addNonDetermined(graph.getError());
            return graph;
        }

        @Override
        public Object afterMatch(int sentenceIndex, ContextFreeSentence matchedSentence, List<Object> childAttributes) throws InvalidAlgorithmParameterException {
            switch (sentenceIndex) {
                case charTerminalIndex -> {
                    assert childAttributes.size() == 1;
                    return childAttributes.get(0);
                }
                case charListIndex -> {
                    assert childAttributes.size() == 2;
                    return onCharList(
                            (RegularGraph) childAttributes.get(0),
                            (RegularGraph) childAttributes.get(1)
                    );
                }
                default -> {
                    throw new InvalidAlgorithmParameterException();
                }
            }
        }
    }

    public class CharRange extends ContextFreeSymbol {
        @Override
        public String toString() {
            return "CharRange";
        }

        @Override
        public Object afterMatch(int sentenceIndex, ContextFreeSentence matchedSentence, List<Object> childAttributes) throws InvalidAlgorithmParameterException {
            if (sentenceIndex == 0) {
                assert childAttributes.size() == 3;
                assert ((SingleCharExpr.Attribute) childAttributes.get(1)).matchedChar == '-';
                RegularSingleCharGraph left = (RegularSingleCharGraph) childAttributes.get(0), right = (RegularSingleCharGraph) childAttributes.get(2);
                assert left.getChar() < right.getChar();
                RegularGraph graph = new RegularGraph();
                graph.breakSourceAndDest();
                var rangeNode = new RegularRangeNode(left.getChar(), right.getChar(), graph.getDest(), graph.getError());
                graph.getSource().addNonDetermined(rangeNode);
                return graph;
            } else {
                throw new InvalidAlgorithmParameterException();
            }
        }
    }

    public class CharRangeList extends ContextFreeSymbol {
        private static final int rangeTerminalIndex = 0;
        private static final int rangeListIndex = 1;

        @Override
        public String toString() {
            return "CharRangeList";
        }

        private RegularGraph onRangeList(RegularGraph graphChar, RegularGraph graphRangeList) {
            RegularGraph graph = new RegularGraph();
            graph.breakSourceAndDest();
            graph.getSource().addNonDetermined(graphChar.getSource());
            graph.getSource().addNonDetermined(graphRangeList.getSource());
            graphChar.getDest().addNonDetermined(graph.getDest());
            graphRangeList.getDest().addNonDetermined(graph.getDest());
            graphChar.getError().addNonDetermined(graph.getError());
            graphRangeList.getError().addNonDetermined(graph.getError());
            return graph;
        }

        @Override
        public Object afterMatch(int sentenceIndex, ContextFreeSentence matchedSentence, List<Object> childAttributes) throws InvalidAlgorithmParameterException {
            switch (sentenceIndex) {
                case rangeTerminalIndex -> {
                    assert childAttributes.size() == 1;
                    return childAttributes.get(0);
                }
                case rangeListIndex -> {
                    assert childAttributes.size() == 2;
                    return onRangeList(
                            (RegularGraph) childAttributes.get(0),
                            (RegularGraph) childAttributes.get(1)
                    );
                }
                default -> {
                    throw new InvalidAlgorithmParameterException();
                }
            }
        }
    }

    public class EnumExpr extends ContextFreeSymbol {
        @Override
        public String toString() {
            return "EnumExpr";
        }

        @Override
        public Object afterMatch(int sentenceIndex, ContextFreeSentence matchedSentence, List<Object> childAttributes) throws InvalidAlgorithmParameterException {
            assert childAttributes.size() == 1;
            return childAttributes.get(0);
        }
    }

    public class EnumIncludeExpr extends ContextFreeSymbol {
        @Override
        public String toString() {
            return "EnumIncludeExpr";
        }

        @Override
        public Object afterMatch(int sentenceIndex, ContextFreeSentence matchedSentence, List<Object> childAttributes) throws InvalidAlgorithmParameterException {
            assert childAttributes.size() == 3;
            return childAttributes.get(1);
        }
    }

    public class EnumExcludeExpr extends ContextFreeSymbol {
        @Override
        public String toString() {
            return "EnumExcludeExpr";
        }

        @Override
        public Object afterMatch(int sentenceIndex, ContextFreeSentence matchedSentence, List<Object> childAttributes) throws InvalidAlgorithmParameterException {
            assert childAttributes.size() == 4;
            RegularGraph graph = new RegularGraph();
            graph.breakSourceAndDest();
            RegularGraph exceptedGraph = (RegularGraph) childAttributes.get(2);
            exceptedGraph.getError().addNonDetermined(graph.getDest());
            exceptedGraph.getDest().addNonDetermined(graph.getError());
            graph.getSource().addNonDetermined(exceptedGraph.getSource());
            return graph;
        }
    }
}
