package com.fishercoder;


import _211.Solution1.WordDictionary;
import org.junit.Assert;
import org.junit.Test;


public class _211Test {
    private static WordDictionary wordDictionarySolution1;

    @Test
    public void test1() {
        _211Test.wordDictionarySolution1.addWord("bad");
        _211Test.wordDictionarySolution1.addWord("dad");
        _211Test.wordDictionarySolution1.addWord("mad");
        Assert.assertEquals(false, _211Test.wordDictionarySolution1.search("pad"));
        Assert.assertEquals(true, _211Test.wordDictionarySolution1.search("bad"));
        Assert.assertEquals(true, _211Test.wordDictionarySolution1.search(".ad"));
        Assert.assertEquals(true, _211Test.wordDictionarySolution1.search("b.."));
    }
}

