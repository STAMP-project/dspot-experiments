package com.hankcs.hanlp.corpus.document.sentence.word;


import junit.framework.TestCase;


public class WordTest extends TestCase {
    public void testCreate() throws Exception {
        TestCase.assertEquals("???/nz", Word.create("???/nz").toString());
        TestCase.assertEquals("[??/nsf ??/n]/nz", CompoundWord.create("[??/nsf ??/n]/nz").toString());
        TestCase.assertEquals("[??/n ??/n ??/vn ??/n]/nt", CompoundWord.create("[??/n ??/n ??/vn ??/n]nt").toString());
    }

    public void testSpace() throws Exception {
        CompoundWord compoundWord = CompoundWord.create("[9/m  11/m ?/f]/mq");
        TestCase.assertEquals(3, compoundWord.innerList.size());
    }
}

