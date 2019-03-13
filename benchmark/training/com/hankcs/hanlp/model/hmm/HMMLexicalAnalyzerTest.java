package com.hankcs.hanlp.model.hmm;


import junit.framework.TestCase;


public class HMMLexicalAnalyzerTest extends TestCase {
    public static final String CORPUS_PATH = "data/test/pku98/199801.txt";

    public void testTrain() throws Exception {
        HMMSegmenter segmenter = new HMMSegmenter();
        segmenter.train(HMMLexicalAnalyzerTest.CORPUS_PATH);
        HMMPOSTagger tagger = new HMMPOSTagger();
        tagger.train(HMMLexicalAnalyzerTest.CORPUS_PATH);
        HMMNERecognizer recognizer = new HMMNERecognizer();
        recognizer.train(HMMLexicalAnalyzerTest.CORPUS_PATH);
        HMMLexicalAnalyzer analyzer = new HMMLexicalAnalyzer(segmenter, tagger, recognizer);
        System.out.println(analyzer.analyze("???????????"));
    }
}

