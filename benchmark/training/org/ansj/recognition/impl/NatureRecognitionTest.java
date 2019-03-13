package org.ansj.recognition.impl;


import org.ansj.splitWord.analysis.NlpAnalysis;
import org.junit.Test;


/**
 * ??????
 *
 * @author Ansj
 */
public class NatureRecognitionTest {
    @Test
    public void test() {
        System.out.println(NlpAnalysis.parse("????????????????").recognition(new NatureRecognition()));
    }

    @Test
    public void natureGuess() {
        System.out.println(NatureRecognition.guessNature("???????????").nature);
        System.out.println(NatureRecognition.guessNature("??").nature);
        System.out.println(NatureRecognition.guessNature("????").nature);
        System.out.println(NatureRecognition.guessNature("???????").nature);
    }
}

