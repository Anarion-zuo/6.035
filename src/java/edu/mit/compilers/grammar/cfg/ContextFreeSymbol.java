package edu.mit.compilers.grammar.cfg;

import javax.naming.OperationNotSupportedException;
import java.security.InvalidAlgorithmParameterException;
import java.util.*;

public class ContextFreeSymbol {
    protected final List<ContextFreeSentence> sentences;

    public static class MatchInfo {
        public final ContextFreeSentence.Iterator nextIterator;
        public final ContextFreeSentence matchedSentence;
        public final int symbolCount;
        public final Object afterAttribute;

        public MatchInfo(ContextFreeSentence.Iterator nextIterator, ContextFreeSentence matchedSentence, int symbolCount, Object afterAttribute) {
            this.nextIterator = nextIterator;
            this.matchedSentence = matchedSentence;
            this.symbolCount = symbolCount;
            this.afterAttribute = afterAttribute;
        }
    }

    public ContextFreeSymbol() {
        this.sentences = new ArrayList<>();
    }

    public ContextFreeSymbol(List<ContextFreeSentence> sentences) {
        this.sentences = sentences;
    }

    public ContextFreeSymbol(ContextFreeSentence... sentences) {
        this(Arrays.stream(sentences).toList());
    }

    public void addSentence(ContextFreeSentence sentence) {
        sentences.add(sentence);
    }

    public void addSentence() {
        sentences.add(new ContextFreeSentence());
    }

    public void addSentences(int count) {
        for (int i = 0; i < count; ++i) {
            addSentence();
        }
    }

    public ContextFreeSentence getSentence(int index) {
        return sentences.get(index);
    }

    public Object afterMatch(int sentenceIndex, ContextFreeSentence matchedSentence, List<Object> childAttributes) throws InvalidAlgorithmParameterException {
        return null;
    }

    protected MatchInfo matchMaxLength(ContextFreeSentence.Iterator symbolIterator) {
        if (!symbolIterator.hasNext()) {
            return new MatchInfo(null, null, 0, null);
        }
        ContextFreeSentence.Iterator resultNext = null;
        ContextFreeSentence resultSentence = null;
        int resultCount = 0;
        int resultSentenceIndex = -1;
        var sentencesIterator = sentences.listIterator();
        Object resultObj = null;
        while (sentencesIterator.hasNext()) {
            int sentenceIndex = sentencesIterator.nextIndex();
            var sentence = sentencesIterator.next();
            System.out.print("Matching: trying sentence " + sentence.toString() + "... ");
            ContextFreeSentence.Iterator curIterator = null;
            try {
                curIterator = symbolIterator.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
            var sentenceIterator = sentence.iterator();
            List<Object> matchedObjects = new LinkedList<>();
            while (sentenceIterator.hasNext()) {
                var symbol = sentenceIterator.next();
                var info = symbol.match(curIterator);
                matchedObjects.add(info.afterAttribute);
                if (info.nextIterator == null) {
                    curIterator = null;
                    break;
                }
                curIterator = info.nextIterator;
            }
            if (curIterator != null) {
                int localDifference = 0;
                try {
                    localDifference = curIterator.difference(symbolIterator);
                } catch (OperationNotSupportedException e) {
                    throw new RuntimeException(e);
                }
                if (localDifference > resultCount) {
                    resultNext = curIterator;
                    resultSentence = sentence;
                    resultCount = localDifference;
                    resultSentenceIndex = sentenceIndex;
                    try {
                        resultObj = afterMatch(resultSentenceIndex, resultSentence, matchedObjects);
                    } catch (InvalidAlgorithmParameterException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.printf("matched count %d!\n", localDifference);
            } else {
                System.out.println("failed!");
            }
        }
        return new MatchInfo(resultNext, resultSentence, resultCount, resultObj);
    }

    protected MatchInfo matchBySentenceOrder(ContextFreeSentence.Iterator symbolIterator) {
        if (!symbolIterator.hasNext()) {
            return new MatchInfo(null, null, 0, null);
        }
        ContextFreeSentence.Iterator resultNext = null;
        ContextFreeSentence resultSentence = null;
        int resultCount = 0;
        int resultSentenceIndex = -1;
        var sentencesIterator = sentences.listIterator();
        Object resultObj = null;
        while (sentencesIterator.hasNext()) {
            int sentenceIndex = sentencesIterator.nextIndex();
            var sentence = sentencesIterator.next();
            System.out.print("Matching: trying sentence " + sentence.toString() + "... ");
            ContextFreeSentence.Iterator curIterator = null;
            try {
                curIterator = symbolIterator.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
            var sentenceIterator = sentence.iterator();
            List<Object> matchedObjects = new LinkedList<>();
            while (sentenceIterator.hasNext()) {
                var symbol = sentenceIterator.next();
                var info = symbol.match(curIterator);
                matchedObjects.add(info.afterAttribute);
                if (info.nextIterator == null) {
                    curIterator = null;
                    break;
                }
                curIterator = info.nextIterator;
            }
            if (curIterator != null) {
                int localDifference = 0;
                try {
                    localDifference = curIterator.difference(symbolIterator);
                } catch (OperationNotSupportedException e) {
                    throw new RuntimeException(e);
                }
                resultNext = curIterator;
                resultSentence = sentence;
                resultCount = localDifference;
                resultSentenceIndex = sentenceIndex;
                try {
                    resultObj = afterMatch(resultSentenceIndex, resultSentence, matchedObjects);
                } catch (InvalidAlgorithmParameterException e) {
                    throw new RuntimeException(e);
                }
                System.out.printf("matched count %d!\n", localDifference);
                break;
            } else {
                System.out.println("failed!");
            }
        }
        return new MatchInfo(resultNext, resultSentence, resultCount, resultObj);
    }

    public MatchInfo match(ContextFreeSentence.Iterator symbolIterator) {
        return matchMaxLength(symbolIterator);
    }

    public MatchInfo matchExaust(ContextFreeSentence.Iterator symbolIterator) {
        while (true) {
            var info  = match(symbolIterator);
            if (info.nextIterator == null) {
                return new MatchInfo(null, null, 0, null);
            }
            if (!info.nextIterator.hasNext()) {
                return info;
            }
            System.out.println("input not exausted, continue...");
            symbolIterator = info.nextIterator;
        }
    }
}
