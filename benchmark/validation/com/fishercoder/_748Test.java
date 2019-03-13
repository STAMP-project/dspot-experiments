package com.fishercoder;


import _748.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _748Test {
    private static Solution1 solution1;

    private static String[] words;

    private static String licensePlate;

    @Test
    public void test1() {
        _748Test.words = new String[]{ "step", "steps", "stripe", "stepple" };
        _748Test.licensePlate = "1s3 PSt";
        Assert.assertEquals("steps", _748Test.solution1.shortestCompletingWord(_748Test.licensePlate, _748Test.words));
    }

    @Test
    public void test2() {
        _748Test.words = new String[]{ "looks", "pest", "stew", "show" };
        _748Test.licensePlate = "1s3 456";
        Assert.assertEquals("pest", _748Test.solution1.shortestCompletingWord(_748Test.licensePlate, _748Test.words));
    }

    @Test
    public void test3() {
        _748Test.words = new String[]{ "suggest", "letter", "of", "husband", "easy", "education", "drug", "prevent", "writer", "old" };
        _748Test.licensePlate = "Ah71752";
        Assert.assertEquals("husband", _748Test.solution1.shortestCompletingWord(_748Test.licensePlate, _748Test.words));
    }
}

