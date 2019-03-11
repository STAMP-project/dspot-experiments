package com.fishercoder;


import com.fishercoder.solutions._507;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 1/25/17.
 */
public class _507Test {
    private static _507 test;

    private static boolean expected;

    private static boolean actual;

    private static int num;

    @Test
    public void test1() {
        _507Test.expected = true;
        _507Test.num = 28;
        _507Test.actual = _507Test.test.checkPerfectNumber(_507Test.num);
        Assert.assertEquals(_507Test.expected, _507Test.actual);
    }
}

