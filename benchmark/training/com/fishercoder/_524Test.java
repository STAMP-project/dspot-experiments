package com.fishercoder;


import com.fishercoder.solutions._524;
import java.util.ArrayList;
import java.util.Arrays;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 4/30/17.
 */
public class _524Test {
    private static _524 test;

    private static String expected;

    private static String actual;

    private static String s;

    private static ArrayList d;

    @Test
    public void test1() {
        _524Test.d = new ArrayList(Arrays.asList("ale", "apple", "monkey", "plea"));
        _524Test.s = "abpcplea";
        _524Test.expected = "apple";
        _524Test.actual = _524Test.test.findLongestWord(_524Test.expected, _524Test.d);
        Assert.assertEquals(_524Test.expected, _524Test.actual);
    }

    @Test
    public void test2() {
        _524Test.d = new ArrayList(Arrays.asList("a", "b", "c"));
        _524Test.s = "abpcplea";
        _524Test.expected = "a";
        _524Test.actual = _524Test.test.findLongestWord(_524Test.expected, _524Test.d);
        Assert.assertEquals(_524Test.expected, _524Test.actual);
    }

    @Test
    public void test3() {
        _524Test.d = new ArrayList(Arrays.asList("apple", "ewaf", "awefawfwaf", "awef", "awefe", "ewafeffewafewf"));
        _524Test.s = "aewfafwafjlwajflwajflwafj";
        _524Test.expected = "ewaf";
        _524Test.actual = _524Test.test.findLongestWord(_524Test.expected, _524Test.d);
        Assert.assertEquals(_524Test.expected, _524Test.actual);
    }
}

