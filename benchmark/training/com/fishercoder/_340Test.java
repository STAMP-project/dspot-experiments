package com.fishercoder;


import _340.Solution1;
import _340.Solution2;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by stevesun on 6/10/17.
 */
public class _340Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    @Test
    public void test1() {
        Assert.assertEquals(3, _340Test.solution1.lengthOfLongestSubstringKDistinct("eceba", 2));
        Assert.assertEquals(3, _340Test.solution2.lengthOfLongestSubstringKDistinct("eceba", 2));
    }

    @Test
    public void test2() {
        Assert.assertEquals(0, _340Test.solution1.lengthOfLongestSubstringKDistinct("", 0));
        Assert.assertEquals(0, _340Test.solution2.lengthOfLongestSubstringKDistinct("", 0));
    }

    @Test
    public void test3() {
        Assert.assertEquals(0, _340Test.solution1.lengthOfLongestSubstringKDistinct("a", 0));
    }

    @Test
    public void test4() {
        Assert.assertEquals(1, _340Test.solution1.lengthOfLongestSubstringKDistinct("a", 1));
    }

    @Test
    public void test5() {
        Assert.assertEquals(1, _340Test.solution1.lengthOfLongestSubstringKDistinct("a", 2));
    }

    @Test
    public void test6() {
        Assert.assertEquals(2, _340Test.solution1.lengthOfLongestSubstringKDistinct("aa", 1));
    }

    @Test
    public void test7() {
        Assert.assertEquals(3, _340Test.solution1.lengthOfLongestSubstringKDistinct("bacc", 2));
    }
}

