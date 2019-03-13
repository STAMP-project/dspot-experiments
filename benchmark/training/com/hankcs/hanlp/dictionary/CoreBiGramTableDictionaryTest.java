package com.hankcs.hanlp.dictionary;


import junit.framework.TestCase;


public class CoreBiGramTableDictionaryTest extends TestCase {
    public void testReload() throws Exception {
        int biFrequency = CoreBiGramTableDictionary.getBiFrequency("???", "??");
        CoreBiGramTableDictionary.reload();
        TestCase.assertEquals(biFrequency, CoreBiGramTableDictionary.getBiFrequency("???", "??"));
    }
}

