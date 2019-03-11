package org.ansj.recognition.impl;


import org.ansj.domain.Result;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.junit.Test;


/**
 * ?????????
 *
 * @author Ansj
 */
public class IDCardRecognitionTest {
    @Test
    public void test() {
        Result result = ToAnalysis.parse("???????????25??13282619771220503X?????????????????130722198506280057h");
        System.out.println(result.recognition(new IDCardRecognition()));
    }
}

