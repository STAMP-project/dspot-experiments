package com.hankcs.hanlp.corpus.tag;


import junit.framework.TestCase;


public class NatureTest extends TestCase {
    public void testFromString() throws Exception {
        Nature one = Nature.create("???1");
        Nature two = Nature.create("???2");
        TestCase.assertEquals(one, Nature.fromString("???1"));
        TestCase.assertEquals(two, Nature.fromString("???2"));
    }
}

