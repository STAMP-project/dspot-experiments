package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONLexerTest_15 extends TestCase {
    public void test_e() throws Exception {
        Assert.assertTrue((12300.0 == (((Double) (JSON.parse("123e2D"))).doubleValue())));
    }
}

