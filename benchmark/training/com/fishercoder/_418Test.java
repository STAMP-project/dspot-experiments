package com.fishercoder;


import com.fishercoder.solutions._418;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by stevesun on 6/11/17.
 */
public class _418Test {
    private static _418 test;

    private static String[] sentence;

    @Test
    public void test1() {
        _418Test.sentence = new String[]{ "hello", "world" };
        Assert.assertEquals(1, _418Test.test.wordsTyping(_418Test.sentence, 2, 8));
    }

    @Test
    public void test2() {
        _418Test.sentence = new String[]{ "a", "bcd", "e" };
        Assert.assertEquals(2, _418Test.test.wordsTyping(_418Test.sentence, 3, 6));
    }

    @Test
    public void test3() {
        _418Test.sentence = new String[]{ "I", "had", "apple", "pie" };
        Assert.assertEquals(1, _418Test.test.wordsTyping(_418Test.sentence, 4, 5));
    }

    @Test
    public void test4() {
        _418Test.sentence = new String[]{ "f", "p", "a" };
        Assert.assertEquals(10, _418Test.test.wordsTyping(_418Test.sentence, 8, 7));
    }

    @Test
    public void test5() {
        _418Test.sentence = new String[]{ "hello", "leetcode" };
        Assert.assertEquals(1, _418Test.test.wordsTyping(_418Test.sentence, 1, 20));
    }

    @Test
    public void test6() {
        _418Test.sentence = new String[]{ "h" };
        Assert.assertEquals(4, _418Test.test.wordsTyping(_418Test.sentence, 2, 3));
    }
}

