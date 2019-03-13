package com.fishercoder;


import com.fishercoder.solutions._591;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by stevesun on 6/4/17.
 */
public class _591Test {
    private static _591 test;

    @Test
    public void test1() {
        Assert.assertEquals(true, _591Test.test.isValid("<DIV>This is the first line <![CDATA[<div>]]></DIV>"));
    }

    @Test
    public void test2() {
        Assert.assertEquals(false, _591Test.test.isValid("<ABCDEFGHIJKLMN>This is the first line <![CDATA[<div>]]></ABCDEFGHIJKLMN>"));// tag_name is too long (> 9)

    }
}

