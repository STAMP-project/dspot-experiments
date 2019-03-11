package com.hankcs.hanlp.dictionary.other;


import junit.framework.TestCase;


public class PartOfSpeechTagDictionaryTest extends TestCase {
    public void testTranslate() throws Exception {
        TestCase.assertEquals("??", PartOfSpeechTagDictionary.translate("n"));
    }
}

