package com.fishercoder;


import _408.Solution1;
import junit.framework.Assert;
import org.junit.Test;


public class _408Test {
    private static Solution1 solution1;

    private static Boolean expected;

    private static Boolean actual;

    private static String word;

    private static String abbr;

    @Test
    public void test1() {
        _408Test.word = "internationalization";
        _408Test.abbr = "i12iz4n";
        _408Test.expected = true;
        _408Test.actual = _408Test.solution1.validWordAbbreviation(_408Test.word, _408Test.abbr);
        Assert.assertEquals(_408Test.expected, _408Test.actual);
    }

    @Test
    public void test2() {
        _408Test.word = "apple";
        _408Test.abbr = "a2e";
        _408Test.expected = false;
        _408Test.actual = _408Test.solution1.validWordAbbreviation(_408Test.word, _408Test.abbr);
        Assert.assertEquals(_408Test.expected, _408Test.actual);
    }

    @Test
    public void test3() {
        _408Test.word = "internationalization";
        _408Test.abbr = "i5a11o1";
        _408Test.expected = true;
        _408Test.actual = _408Test.solution1.validWordAbbreviation(_408Test.word, _408Test.abbr);
        Assert.assertEquals(_408Test.expected, _408Test.actual);
    }

    @Test
    public void test4() {
        _408Test.word = "hi";
        _408Test.abbr = "1";
        _408Test.expected = false;
        _408Test.actual = _408Test.solution1.validWordAbbreviation(_408Test.word, _408Test.abbr);
        Assert.assertEquals(_408Test.expected, _408Test.actual);
    }

    @Test
    public void test5() {
        _408Test.word = "a";
        _408Test.abbr = "1";
        _408Test.expected = true;
        _408Test.actual = _408Test.solution1.validWordAbbreviation(_408Test.word, _408Test.abbr);
        Assert.assertEquals(_408Test.expected, _408Test.actual);
    }

    @Test
    public void test6() {
        _408Test.word = "a";
        _408Test.abbr = "2";
        _408Test.expected = false;
        _408Test.actual = _408Test.solution1.validWordAbbreviation(_408Test.word, _408Test.abbr);
        Assert.assertEquals(_408Test.expected, _408Test.actual);
    }

    @Test
    public void test7() {
        _408Test.word = "hi";
        _408Test.abbr = "1i";
        _408Test.expected = true;
        _408Test.actual = _408Test.solution1.validWordAbbreviation(_408Test.word, _408Test.abbr);
        Assert.assertEquals(_408Test.expected, _408Test.actual);
    }

    @Test
    public void test8() {
        _408Test.word = "hi";
        _408Test.abbr = "3";
        _408Test.expected = false;
        _408Test.actual = _408Test.solution1.validWordAbbreviation(_408Test.word, _408Test.abbr);
        Assert.assertEquals(_408Test.expected, _408Test.actual);
    }

    @Test
    public void test9() {
        _408Test.word = "hi";
        _408Test.abbr = "11";
        _408Test.expected = false;
        _408Test.actual = _408Test.solution1.validWordAbbreviation(_408Test.word, _408Test.abbr);
        Assert.assertEquals(_408Test.expected, _408Test.actual);
    }

    @Test
    public void test10() {
        _408Test.word = "word";
        _408Test.abbr = "1o1d";
        _408Test.expected = true;
        _408Test.actual = _408Test.solution1.validWordAbbreviation(_408Test.word, _408Test.abbr);
        Assert.assertEquals(_408Test.expected, _408Test.actual);
    }

    @Test
    public void test11() {
        _408Test.word = "abbreviation";
        _408Test.abbr = "a010n";
        _408Test.expected = false;
        _408Test.actual = _408Test.solution1.validWordAbbreviation(_408Test.word, _408Test.abbr);
        Assert.assertEquals(_408Test.expected, _408Test.actual);
    }
}

