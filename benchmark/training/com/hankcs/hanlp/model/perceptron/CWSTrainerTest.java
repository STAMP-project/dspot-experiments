package com.hankcs.hanlp.model.perceptron;


import HanLP.Config;
import PerceptronTrainer.Result;
import com.hankcs.hanlp.corpus.document.sentence.Sentence;
import com.hankcs.hanlp.corpus.document.sentence.word.CompoundWord;
import com.hankcs.hanlp.corpus.document.sentence.word.IWord;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import junit.framework.TestCase;


public class CWSTrainerTest extends TestCase {
    public static final String SENTENCE = "???????????????????????????????";

    public void testTrain() throws Exception {
        Config.enableDebug();
        PerceptronTrainer trainer = new CWSTrainer();
        PerceptronTrainer.Result result = trainer.train("data/test/pku98/199801.txt", Config.CWS_MODEL_FILE);
        // System.out.printf("???F1:%.2f\n", result.prf[2]);
        PerceptronSegmenter segmenter = new PerceptronSegmenter(result.model);
        // ????
        // Segment segmenter = new AveragedPerceptronSegment(POS_MODEL_FILE);
        System.out.println(segmenter.segment("??????"));
    }

    public void testCWS() throws Exception {
        PerceptronSegmenter segmenter = new PerceptronSegmenter(Config.CWS_MODEL_FILE);
        segmenter.learn("??? ?? ??");
        System.out.println(segmenter.segment("???????????"));
    }

    public void testCWSandPOS() throws Exception {
        Segment segmenter = new PerceptronLexicalAnalyzer(Config.CWS_MODEL_FILE, Config.POS_MODEL_FILE);
        System.out.println(segmenter.seg(CWSTrainerTest.SENTENCE));
    }

    public void testCWSandPOSandNER() throws Exception {
        PerceptronLexicalAnalyzer segmenter = new PerceptronLexicalAnalyzer(Config.CWS_MODEL_FILE, Config.POS_MODEL_FILE, Config.NER_MODEL_FILE);
        Sentence sentence = segmenter.analyze(CWSTrainerTest.SENTENCE);
        System.out.println(sentence);
        System.out.println(segmenter.seg(CWSTrainerTest.SENTENCE));
        for (IWord word : sentence) {
            if (word instanceof CompoundWord)
                System.out.println(((CompoundWord) (word)).innerList);

        }
    }

    public void testCompareWithHanLP() throws Exception {
        System.out.println(NLPTokenizer.segment(CWSTrainerTest.SENTENCE));
    }
}

