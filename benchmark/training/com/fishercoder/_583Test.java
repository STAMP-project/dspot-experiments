package com.fishercoder;


import com.fishercoder.solutions._583;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/18/17.
 */
public class _583Test {
    private static _583 test;

    private static String word1;

    private static String word2;

    @Test
    public void test1() {
        _583Test.word1 = "sea";
        _583Test.word2 = "eat";
        Assert.assertEquals(2, _583Test.test.minDistance(_583Test.word1, _583Test.word2));
    }

    @Test
    public void test2() {
        _583Test.word1 = "sea";
        _583Test.word2 = "ate";
        Assert.assertEquals(4, _583Test.test.minDistance(_583Test.word1, _583Test.word2));
    }
}

