package com.fishercoder;


import _537.Solution1;
import _537.Solution2;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 1/25/17.
 */
public class _537Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static String expected;

    private static String a;

    private static String b;

    @Test
    public void test1() {
        _537Test.expected = "0+2i";
        _537Test.a = "1+1i";
        _537Test.b = "1+1i";
        Assert.assertEquals(_537Test.expected, _537Test.solution1.complexNumberMultiply(_537Test.a, _537Test.b));
        Assert.assertEquals(_537Test.expected, _537Test.solution2.complexNumberMultiply(_537Test.a, _537Test.b));
    }

    @Test
    public void test2() {
        _537Test.expected = "0+-2i";
        _537Test.a = "1+-1i";
        _537Test.b = "1+-1i";
        Assert.assertEquals(_537Test.expected, _537Test.solution2.complexNumberMultiply(_537Test.a, _537Test.b));
    }
}

