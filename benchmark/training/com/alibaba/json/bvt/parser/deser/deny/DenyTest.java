package com.alibaba.json.bvt.parser.deser.deny;


import JSON.DEFAULT_PARSER_FEATURE;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.ParserConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class DenyTest extends TestCase {
    public void test_0() throws Exception {
        String text = "{}";
        ParserConfig config = new ParserConfig();
        config.addDeny(null);
        config.addDeny("com.alibaba.json.bvtVO.deny");
        Exception error = null;
        try {
            JSON.parseObject("{\"@type\":\"com.alibaba.json.bvtVO.deny$A\"}", Object.class, config, DEFAULT_PARSER_FEATURE);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
        JSON.parseObject(text, DenyTest.B.class, config, DEFAULT_PARSER_FEATURE);
    }

    public void test_1() throws Exception {
        String text = "{}";
        ParserConfig config = new ParserConfig();
        config.addDeny(null);
        config.addDeny("com.alibaba.json.bvt.parser.deser.deny.DenyTest.B");
        Exception error = null;
        try {
            JSON.parseObject("{\"@type\":\"LLLcom.alibaba.json.bvt.parser.deser.deny.DenyTest$B;;;\"}", Object.class, config, DEFAULT_PARSER_FEATURE);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
        JSON.parseObject(text, DenyTest.B.class, config, DEFAULT_PARSER_FEATURE);
    }

    public static class B {}
}

