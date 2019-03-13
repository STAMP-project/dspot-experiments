package org.ansj.recognition.impl;


import org.ansj.domain.Result;
import org.junit.Assert;
import org.junit.Test;


public class EmailRecognitionTest {
    @Test
    public void recognition() throws Exception {
        Result recognition = null;
        recognition = org.ansj.splitWord.analysis.ToAnalysis.parse("ansj-sun@163.com??????").recognition(new EmailRecognition());
        System.out.println(recognition);
        Assert.assertEquals(recognition.get(0).getName(), "ansj-sun@163.com");
    }
}

