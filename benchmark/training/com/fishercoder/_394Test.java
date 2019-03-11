package com.fishercoder;


import _394.Solution1;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by varunu28 on 1/08/19.
 */
public class _394Test {
    private static Solution1 test;

    @Test
    public void test1() {
        Assert.assertEquals("aaabcbc", _394Test.test.decodeString("3[a]2[bc]"));
    }

    @Test
    public void test2() {
        Assert.assertEquals("accaccacc", _394Test.test.decodeString("3[a2[c]]"));
    }

    @Test
    public void test3() {
        Assert.assertEquals("abcabccdcdcdef", _394Test.test.decodeString("2[abc]3[cd]ef"));
    }
}

