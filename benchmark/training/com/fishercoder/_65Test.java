package com.fishercoder;


import _65.Solution1;
import _65.Solution2;
import junit.framework.TestCase;
import org.junit.Test;


public class _65Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    @Test
    public void test1() {
        TestCase.assertEquals(false, _65Test.solution1.isNumber("1 a"));
        TestCase.assertEquals(false, _65Test.solution2.isNumber("1 a"));
    }

    @Test
    public void test2() {
        TestCase.assertEquals(false, _65Test.solution1.isNumber("4e+"));
        TestCase.assertEquals(false, _65Test.solution2.isNumber("4e+"));
    }

    @Test
    public void test3() {
        TestCase.assertEquals(true, _65Test.solution1.isNumber("005047e+6"));
        TestCase.assertEquals(true, _65Test.solution2.isNumber("005047e+6"));
    }

    @Test
    public void test4() {
        TestCase.assertEquals(false, _65Test.solution1.isNumber(".e10"));
        TestCase.assertEquals(false, _65Test.solution2.isNumber(".e10"));
    }

    @Test
    public void test5() {
        TestCase.assertEquals(true, _65Test.solution1.isNumber("2e10"));
        TestCase.assertEquals(true, _65Test.solution2.isNumber("2e10"));
    }

    @Test
    public void test6() {
        TestCase.assertEquals(false, _65Test.solution1.isNumber("abc"));
        TestCase.assertEquals(false, _65Test.solution2.isNumber("abc"));
    }

    @Test
    public void test7() {
        TestCase.assertEquals(false, _65Test.solution1.isNumber(" -."));
        TestCase.assertEquals(false, _65Test.solution2.isNumber(" -."));
    }

    @Test
    public void test8() {
        TestCase.assertEquals(true, _65Test.solution1.isNumber("+.8"));
        TestCase.assertEquals(true, _65Test.solution2.isNumber("+.8"));
    }

    @Test
    public void test9() {
        TestCase.assertEquals(false, _65Test.solution1.isNumber("."));
        TestCase.assertEquals(false, _65Test.solution2.isNumber("."));
    }

    @Test
    public void test10() {
        TestCase.assertEquals(false, _65Test.solution1.isNumber(".e1"));
        TestCase.assertEquals(false, _65Test.solution2.isNumber(".e1"));
    }

    @Test
    public void test11() {
        TestCase.assertEquals(true, _65Test.solution1.isNumber("0"));
        TestCase.assertEquals(true, _65Test.solution2.isNumber("0"));
    }

    @Test
    public void test12() {
        TestCase.assertEquals(false, _65Test.solution1.isNumber("0e"));
        TestCase.assertEquals(false, _65Test.solution2.isNumber("0e"));
    }

    @Test
    public void test13() {
        TestCase.assertEquals(false, _65Test.solution1.isNumber("6ee69"));
        TestCase.assertEquals(false, _65Test.solution2.isNumber("6ee69"));
    }

    @Test
    public void test14() {
        TestCase.assertEquals(false, _65Test.solution1.isNumber("+++"));
        TestCase.assertEquals(false, _65Test.solution2.isNumber("+++"));
    }

    @Test
    public void test15() {
        TestCase.assertEquals(false, _65Test.solution1.isNumber("0e"));
        TestCase.assertEquals(false, _65Test.solution2.isNumber("0e"));
    }

    @Test
    public void test16() {
        TestCase.assertEquals(false, _65Test.solution1.isNumber("e9"));
        TestCase.assertEquals(false, _65Test.solution2.isNumber("e9"));
    }

    @Test
    public void test17() {
        TestCase.assertEquals(true, _65Test.solution1.isNumber(" 0.1 "));
        TestCase.assertEquals(true, _65Test.solution2.isNumber(" 0.1 "));
    }

    @Test
    public void test18() {
        TestCase.assertEquals(true, _65Test.solution1.isNumber("46.e3"));
        TestCase.assertEquals(true, _65Test.solution2.isNumber("46.e3"));
    }

    @Test
    public void test19() {
        TestCase.assertEquals(false, _65Test.solution1.isNumber(".."));
        TestCase.assertEquals(false, _65Test.solution2.isNumber(".."));
    }

    @Test
    public void test20() {
        TestCase.assertEquals(false, _65Test.solution1.isNumber(".e1"));
        TestCase.assertEquals(false, _65Test.solution2.isNumber(".e1"));
    }

    @Test
    public void test21() {
        TestCase.assertEquals(false, _65Test.solution1.isNumber(".."));
        TestCase.assertEquals(false, _65Test.solution2.isNumber(".."));
    }

    @Test
    public void test22() {
        TestCase.assertEquals(false, _65Test.solution1.isNumber("1e."));
        TestCase.assertEquals(false, _65Test.solution2.isNumber("1e."));
    }

    @Test
    public void test24() {
        TestCase.assertEquals(true, _65Test.solution1.isNumber("-1."));
        TestCase.assertEquals(true, _65Test.solution2.isNumber("-1."));
    }

    @Test
    public void test25() {
        TestCase.assertEquals(false, _65Test.solution1.isNumber("6e6.5"));
        TestCase.assertEquals(false, _65Test.solution2.isNumber("6e6.5"));
    }
}

