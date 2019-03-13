package com.hankcs.hanlp.mining.word;


import junit.framework.TestCase;


public class TermFrequencyCounterTest extends TestCase {
    public void testGetKeywords() throws Exception {
        TermFrequencyCounter counter = new TermFrequencyCounter();
        counter.add("????????");
        System.out.println(counter);
        System.out.println(counter.getKeywords("????????????????"));
    }
}

