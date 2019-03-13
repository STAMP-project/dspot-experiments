package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONScannerTest_scanFieldBoolean_unquote extends TestCase {
    public void test_4() throws Exception {
        String text = "{\"value\":false,id:2}";
        JSONScannerTest_scanFieldBoolean.VO obj = JSON.parseObject(text, JSONScannerTest_scanFieldBoolean.VO.class);
        Assert.assertEquals(false, obj.getValue());
    }
}

