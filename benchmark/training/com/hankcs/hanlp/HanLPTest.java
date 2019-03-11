package com.hankcs.hanlp;


import com.hankcs.hanlp.model.perceptron.PerceptronLexicalAnalyzer;
import com.hankcs.hanlp.seg.Viterbi.ViterbiSegment;
import junit.framework.TestCase;


public class HanLPTest extends TestCase {
    public void testNewSegment() throws Exception {
        TestCase.assertTrue(((HanLP.newSegment("???")) instanceof ViterbiSegment));
        TestCase.assertTrue(((HanLP.newSegment("???")) instanceof PerceptronLexicalAnalyzer));
    }

    public void testDicUpdate() {
        System.out.println(HanLP.segment("??????????"));
    }
}

