package com.fishercoder;


import com.fishercoder.solutions._439;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/18/17.
 */
public class _439Test {
    private static _439 test;

    private static String expression;

    private static String expected;

    private static String actual;

    @Test
    public void test1() {
        _439Test.expression = "T?2:3";
        _439Test.expected = "2";
        Assert.assertEquals(_439Test.expected, _439Test.test.parseTernary(_439Test.expression));
    }

    @Test
    public void test2() {
        _439Test.expression = "F?1:T?4:5";
        _439Test.expected = "4";
        Assert.assertEquals(_439Test.expected, _439Test.test.parseTernary(_439Test.expression));
    }

    @Test
    public void test3() {
        _439Test.expression = "T?T?F:5:3";
        _439Test.expected = "F";
        Assert.assertEquals(_439Test.expected, _439Test.test.parseTernary(_439Test.expression));
    }

    @Test
    public void test4() {
        _439Test.expression = "T?T:F?T?1:2:F?3:4";
        _439Test.expected = "T";
        Assert.assertEquals(_439Test.expected, _439Test.test.parseTernary(_439Test.expression));
    }
}

