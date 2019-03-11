package com.fishercoder;


import com.fishercoder.solutions._522;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 12/31/16.
 */
public class _522Test {
    private static _522 test;

    private static int expected;

    private static int actual;

    private static String[] strs;

    @Test
    public void test1() {
        _522Test.strs = new String[]{ "aaa", "aaa", "aa" };
        _522Test.expected = -1;
        _522Test.actual = _522Test.test.findLUSlength(_522Test.strs);
        Assert.assertEquals(_522Test.expected, _522Test.actual);
    }
}

