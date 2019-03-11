package com.hankcs.hanlp.model.bigram;


import junit.framework.TestCase;


public class BigramDependencyModelTest extends TestCase {
    public void testLoad() throws Exception {
        TestCase.assertEquals("??", BigramDependencyModel.get("?", "v", "??", "n"));
    }
}

