package com.alibaba.json.bvt;


import com.alibaba.fastjson.util.IOUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class CharTypesTest extends TestCase {
    static byte[] specicalFlags_singleQuotes = IOUtils.specicalFlags_singleQuotes;

    static byte[] specicalFlags_doubleQuotes = IOUtils.specicalFlags_doubleQuotes;

    public void test_0() throws Exception {
        Assert.assertTrue(CharTypesTest.isSpecial_doubleQuotes('\n'));
        Assert.assertTrue(CharTypesTest.isSpecial_doubleQuotes('\r'));
        Assert.assertTrue(CharTypesTest.isSpecial_doubleQuotes('\b'));
        Assert.assertTrue(CharTypesTest.isSpecial_doubleQuotes('\f'));
        Assert.assertTrue(CharTypesTest.isSpecial_doubleQuotes('\"'));
        Assert.assertFalse(CharTypesTest.isSpecial_doubleQuotes('0'));
        Assert.assertTrue(CharTypesTest.isSpecial_doubleQuotes('\u0000'));
        Assert.assertFalse(CharTypesTest.isSpecial_doubleQuotes('?'));
        Assert.assertFalse(CharTypesTest.isSpecial_doubleQuotes('?'));
    }
}

