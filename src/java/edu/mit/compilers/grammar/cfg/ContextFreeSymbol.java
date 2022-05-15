package edu.mit.compilers.grammar.cfg;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ContextFreeSymbol {
    protected final List<ContextFreeSentence> sentences;

    public static class MatchInfo {
        public final ContextFreeSentence.Iterator nextIterator;
        public final ContextFreeSentence matchedSentence;

        public MatchInfo(ContextFreeSentence.Iterator nextIterator, ContextFreeSentence matchedSentence) {
            this.nextIterator = nextIterator;
            this.matchedSentence = matchedSentence;
        }
    }

    public ContextFreeSymbol(List<ContextFreeSentence> sentences) {
        this.sentences = sentences;
    }

    public ContextFreeSymbol(ContextFreeSentence... sentences) {
        this(Arrays.stream(sentences).toList());
    }

    public ContextFreeSentence getSentence(int index) {
        return sentences.get(index);
    }

    public MatchInfo match(ContextFreeSentence.Iterator symbolIterator) {
        if (!symbolIterator.hasNext()) {
            return new MatchInfo(null, null);
        }
        ContextFreeSentence.Iterator resultNext = null;
        ContextFreeSentence resultSentence = null;
        for (var sentence : sentences) {
            System.out.println("Matching: trying sentence " + sentence.toString());
            ContextFreeSentence.Iterator curIterator = null;
            try {
                curIterator = symbolIterator.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
            var sentenceIterator = sentence.iterator();
            while (sentenceIterator.hasNext()) {
                var symbol = sentenceIterator.next();
                var info = symbol.match(curIterator);
                if (info.nextIterator == null) {
                    curIterator = null;
                    break;
                }
                curIterator = info.nextIterator;
            }
            if (curIterator != null && !curIterator.hasNext()) {
                resultNext = curIterator;
                resultSentence = sentence;
                break;
            }
        }
        return new MatchInfo(resultNext, resultSentence);
    }
}
