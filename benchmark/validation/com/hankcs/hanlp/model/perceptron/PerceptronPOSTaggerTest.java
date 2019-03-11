package com.hankcs.hanlp.model.perceptron;


import HanLP.Config;
import com.hankcs.hanlp.corpus.PKU;
import com.hankcs.hanlp.tokenizer.lexical.AbstractLexicalAnalyzer;
import java.util.Arrays;
import junit.framework.TestCase;


public class PerceptronPOSTaggerTest extends TestCase {
    public void testTrain() throws Exception {
        PerceptronTrainer trainer = new POSTrainer();
        trainer.train(PKU.PKU199801_TRAIN, PKU.POS_MODEL);// ??

        PerceptronPOSTagger tagger = new PerceptronPOSTagger(PKU.POS_MODEL);// ??

        System.out.println(Arrays.toString(tagger.tag("?", "?", "??", "?", "??", "??")));// ??

        AbstractLexicalAnalyzer analyzer = new AbstractLexicalAnalyzer(new PerceptronSegmenter(), tagger);// ???????

        System.out.println(analyzer.analyze("???????????"));// ??+????

    }

    public void testCompress() throws Exception {
        PerceptronPOSTagger tagger = new PerceptronPOSTagger();
        tagger.getModel().compress(0.01);
        double[] scores = tagger.evaluate("data/test/pku98/199801.txt");
        System.out.println(scores[0]);
        tagger.getModel().save(((Config.PerceptronPOSModelPath) + ".small"));
    }
}

