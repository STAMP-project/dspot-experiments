package com.fishercoder;


import com.fishercoder.solutions._592;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/23/17.
 */
public class _592Test {
    private static _592 test;

    private static String expression;

    @Test
    public void test1() {
        _592Test.expression = "-1/2+1/2+1/3";
        Assert.assertEquals("1/3", _592Test.test.fractionAddition(_592Test.expression));
    }
}

