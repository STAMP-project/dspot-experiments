package com.fishercoder;


import _744.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _744Test {
    private static Solution1 solution1;

    private static char[] letters;

    @Test
    public void test1() {
        _744Test.letters = new char[]{ 'c', 'f', 'j' };
        Assert.assertEquals('c', _744Test.solution1.nextGreatestLetter(_744Test.letters, 'a'));
    }

    @Test
    public void test2() {
        _744Test.letters = new char[]{ 'c', 'f', 'j' };
        Assert.assertEquals('f', _744Test.solution1.nextGreatestLetter(_744Test.letters, 'c'));
    }

    @Test
    public void test3() {
        _744Test.letters = new char[]{ 'c', 'f', 'j' };
        Assert.assertEquals('f', _744Test.solution1.nextGreatestLetter(_744Test.letters, 'd'));
    }

    @Test
    public void test4() {
        _744Test.letters = new char[]{ 'c', 'f', 'j' };
        Assert.assertEquals('j', _744Test.solution1.nextGreatestLetter(_744Test.letters, 'g'));
    }

    @Test
    public void test5() {
        _744Test.letters = new char[]{ 'c', 'f', 'j' };
        Assert.assertEquals('c', _744Test.solution1.nextGreatestLetter(_744Test.letters, 'j'));
    }

    @Test
    public void test6() {
        _744Test.letters = new char[]{ 'c', 'f', 'j' };
        Assert.assertEquals('c', _744Test.solution1.nextGreatestLetter(_744Test.letters, 'k'));
    }
}

