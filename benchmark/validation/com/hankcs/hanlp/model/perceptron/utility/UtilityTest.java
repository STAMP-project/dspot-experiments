package com.hankcs.hanlp.model.perceptron.utility;


import com.hankcs.hanlp.corpus.PKU;
import com.hankcs.hanlp.corpus.document.sentence.Sentence;
import com.hankcs.hanlp.model.hmm.HMMNERecognizer;
import com.hankcs.hanlp.model.perceptron.PerceptronNERecognizer;
import com.hankcs.hanlp.model.perceptron.tagset.NERTagSet;
import java.util.Arrays;
import java.util.Map;
import junit.framework.TestCase;


public class UtilityTest extends TestCase {
    public void testCombineNER() throws Exception {
        NERTagSet nerTagSet = new HMMNERecognizer().getNERTagSet();
        String[] nerArray = Utility.reshapeNER(Utility.convertSentenceToNER(Sentence.create("???/nr ?/v ?/w ???/ns ?/d ?/p [???/nt ??/v ???/ns ???/b ???/n ??/n ??/a ???/n]/nt ??/v ??/v ??/v ?/w"), nerTagSet))[2];
        System.out.println(Arrays.toString(nerArray));
        System.out.println(Utility.combineNER(nerArray, nerTagSet));
    }

    public void testEvaluateNER() throws Exception {
        Map<String, double[]> scores = Utility.evaluateNER(new PerceptronNERecognizer(), PKU.PKU199801_TEST);
        Utility.printNERScore(scores);
    }
}

