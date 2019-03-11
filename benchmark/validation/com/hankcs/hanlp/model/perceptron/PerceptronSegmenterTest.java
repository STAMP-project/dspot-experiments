package com.hankcs.hanlp.model.perceptron;


import com.hankcs.hanlp.dictionary.CustomDictionary;
import java.util.List;
import junit.framework.TestCase;


public class PerceptronSegmenterTest extends TestCase {
    private PerceptronSegmenter segmenter;

    public void testEmptyString() throws Exception {
        segmenter.segment("");
    }

    public void testNRF() throws Exception {
        String text = "?????????????????????";
        List<String> wordList = segmenter.segment(text);
        TestCase.assertTrue(wordList.contains("???????"));
    }

    public void testNoCustomDictionary() throws Exception {
        PerceptronLexicalAnalyzer analyzer = new PerceptronLexicalAnalyzer();
        analyzer.enableCustomDictionary(false);
        CustomDictionary.insert("??????");
        TestCase.assertEquals("[??/v, ??/n, ??/n]", analyzer.seg("??????").toString());
    }

    public void testLearnAndSeg() throws Exception {
        PerceptronLexicalAnalyzer analyzer = new PerceptronLexicalAnalyzer();
        analyzer.learn("?/c ???/nr ?/v ??/n ??/v [??/s ??/vn ????/n]/nt");
        TestCase.assertEquals("[?/c, ???/k, ?/v, ??/n, ??/v, ????????/nt]", analyzer.seg("?????????????????").toString());
    }
}

