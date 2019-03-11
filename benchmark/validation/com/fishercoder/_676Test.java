package com.fishercoder;


import _676.Solution1.MagicDictionary;
import org.junit.Assert;
import org.junit.Test;


public class _676Test {
    private static MagicDictionary magicDictionarySol1;

    @Test
    public void test1() {
        _676Test.magicDictionarySol1.buildDict(new String[]{ "hello", "leetcode" });
        Assert.assertEquals(false, _676Test.magicDictionarySol1.search("hello"));
        Assert.assertEquals(true, _676Test.magicDictionarySol1.search("hhllo"));
        Assert.assertEquals(false, _676Test.magicDictionarySol1.search("hell"));
        Assert.assertEquals(false, _676Test.magicDictionarySol1.search("leetcoded"));
    }
}

