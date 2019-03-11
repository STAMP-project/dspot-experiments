package com.alibaba.json.bvt.parser.deser.deny;


import JSON.DEFAULT_PARSER_FEATURE;
import ParserConfig.DENY_PROPERTY;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.ParserConfig;
import java.util.Properties;
import junit.framework.TestCase;
import org.junit.Assert;


public class DenyTest2 extends TestCase {
    public void test_0() throws Exception {
        String text = "{}";
        ParserConfig config = new ParserConfig();
        Properties properties = new Properties();
        properties.put(DENY_PROPERTY, "com.alibaba.json.bvtVO.deny");
        config.configFromPropety(properties);
        Exception error = null;
        try {
            JSON.parseObject("{\"@type\":\"com.alibaba.json.bvtVO.deny$A\"}", Object.class, config, DEFAULT_PARSER_FEATURE);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
        error.printStackTrace();
        JSON.parseObject(text, DenyTest2.B.class, config, DEFAULT_PARSER_FEATURE);
    }

    public static class B {}
}

