package com.hankcs.hanlp.model.crf;


import HanLP.Config;
import com.hankcs.hanlp.corpus.PKU;
import com.hankcs.hanlp.model.perceptron.PerceptronSegmenter;
import com.hankcs.hanlp.tokenizer.lexical.AbstractLexicalAnalyzer;
import java.util.Arrays;
import junit.framework.TestCase;


public class CRFPOSTaggerTest extends TestCase {
    public static final String CORPUS = "data/test/pku98/199801.txt";

    public static String POS_MODEL_PATH = Config.CRFPOSModelPath;

    public void testTrain() throws Exception {
        CRFPOSTagger tagger = new CRFPOSTagger(null);// ???????

        tagger.train(PKU.PKU199801_TRAIN, PKU.POS_MODEL);// ??

        tagger = new CRFPOSTagger(PKU.POS_MODEL);// ??

        System.out.println(Arrays.toString(tagger.tag("?", "?", "??", "?", "??", "??")));// ??

        AbstractLexicalAnalyzer analyzer = new AbstractLexicalAnalyzer(new PerceptronSegmenter(), tagger);// ???????

        System.out.println(analyzer.analyze("???????????"));// ??+????

    }

    public void testLoad() throws Exception {
        CRFPOSTagger tagger = new CRFPOSTagger("data/model/crf/pku199801/pos.txt");
        System.out.println(Arrays.toString(tagger.tag("?", "?", "??", "?", "??", "??")));
    }

    public void testConvert() throws Exception {
        CRFTagger tagger = new CRFPOSTagger(null);
        tagger.convertCorpus(CRFPOSTaggerTest.CORPUS, "data/test/crf/pos-corpus.tsv");
    }

    public void testDumpTemplate() throws Exception {
        CRFTagger tagger = new CRFPOSTagger(null);
        tagger.dumpTemplate("data/test/crf/pos-template.txt");
    }
}

