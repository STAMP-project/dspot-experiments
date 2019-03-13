package org.ansj.recognition.impl;


import org.ansj.domain.Result;
import org.junit.Assert;
import org.junit.Test;


public class URLRecognitionTest {
    @Test
    public void recognition() throws Exception {
        Result recognition = null;
        // recognition = ToAnalysis.parse("http://www.baidu.com??????").recognition(new URLRecognition());
        // 
        // Assert.assertEquals(recognition.get(0).getName(), ("http://www.baidu.com"));
        recognition = org.ansj.splitWord.analysis.ToAnalysis.parse("http://www.ansj-sun123.23423.com??????").recognition(new URLRecognition());
        System.out.println(recognition);
        Assert.assertEquals(recognition.get(0).getName(), "http://www.ansj-sun123.23423.com");
        recognition = org.ansj.splitWord.analysis.ToAnalysis.parse("http://www.ansj-sun123.23423.com...??????").recognition(new URLRecognition());
        System.out.println(recognition);
        Assert.assertEquals(recognition.get(0).getName(), "http://www.ansj-sun123.23423.com");
        recognition = org.ansj.splitWord.analysis.ToAnalysis.parse("http://localhost...??????").recognition(new URLRecognition());
        System.out.println(recognition);
        Assert.assertEquals(recognition.get(0).getName(), "http://localhost");
        recognition = org.ansj.splitWord.analysis.ToAnalysis.parse("http://127.0.0.1...??????").recognition(new URLRecognition());
        System.out.println(recognition);
        Assert.assertEquals(recognition.get(0).getName(), "http://127.0.0.1");
        recognition = org.ansj.splitWord.analysis.ToAnalysis.parse("http://...??????").recognition(new URLRecognition());
        System.out.println(recognition);
        Assert.assertEquals(recognition.get(0).getName(), "http");
    }
}

