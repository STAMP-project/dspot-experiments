package com.hankcs.hanlp.model.hmm;


import com.hankcs.hanlp.corpus.PKU;
import com.hankcs.hanlp.model.perceptron.PerceptronSegmenter;
import com.hankcs.hanlp.tokenizer.lexical.AbstractLexicalAnalyzer;
import java.util.Arrays;
import junit.framework.TestCase;


public class HMMPOSTaggerTest extends TestCase {
    public void testTrain() throws Exception {
        HMMPOSTagger tagger = new HMMPOSTagger();// ???????

        // HMMPOSTagger tagger = new HMMPOSTagger(new SecondOrderHiddenMarkovModel()); // ?????
        tagger.train(PKU.PKU199801);// ??

        System.out.println(Arrays.toString(tagger.tag("?", "?", "??", "?", "??", "??")));// ??

        AbstractLexicalAnalyzer analyzer = new AbstractLexicalAnalyzer(new PerceptronSegmenter(), tagger);// ???????

        System.out.println(analyzer.analyze("?????????").translateLabels());// ??+????

    }
}

