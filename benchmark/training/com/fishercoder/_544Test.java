package com.fishercoder;


import com.fishercoder.solutions._544;
import junit.framework.TestCase;
import org.junit.Test;


public class _544Test {
    private static _544 test;

    private static int n;

    private static String expected;

    private static String actual;

    @Test
    public void test1() {
        _544Test.n = 2;
        _544Test.expected = "(1,2)";
        _544Test.actual = _544Test.test.findContestMatch(_544Test.n);
        TestCase.assertEquals(_544Test.expected, _544Test.actual);
    }

    @Test
    public void test2() {
        _544Test.n = 4;
        _544Test.expected = "((1,4),(2,3))";
        _544Test.actual = _544Test.test.findContestMatch(_544Test.n);
        TestCase.assertEquals(_544Test.expected, _544Test.actual);
    }

    @Test
    public void test3() {
        _544Test.n = 8;
        _544Test.expected = "(((1,8),(4,5)),((2,7),(3,6)))";
        _544Test.actual = _544Test.test.findContestMatch(_544Test.n);
        TestCase.assertEquals(_544Test.expected, _544Test.actual);
    }
}

