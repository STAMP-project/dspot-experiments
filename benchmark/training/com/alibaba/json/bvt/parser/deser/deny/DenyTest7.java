package com.alibaba.json.bvt.parser.deser.deny;


import ParserConfig.AUTOTYPE_SUPPORT_PROPERTY;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.ParserConfig;
import java.util.Properties;
import junit.framework.TestCase;


public class DenyTest7 extends TestCase {
    public void test_autoTypeDeny() throws Exception {
        ParserConfig config = new ParserConfig();
        TestCase.assertFalse(config.isAutoTypeSupport());
        config.setAutoTypeSupport(true);
        TestCase.assertTrue(config.isAutoTypeSupport());
        Properties properties = new Properties();
        properties.put(AUTOTYPE_SUPPORT_PROPERTY, "false");
        config.configFromPropety(properties);
        TestCase.assertFalse(config.isAutoTypeSupport());
        Exception error = null;
        try {
            Object obj = JSON.parseObject("{\"@type\":\"com.alibaba.json.bvt.parser.deser.deny.DenyTest7$Model\"}", Object.class, config);
            System.out.println(obj.getClass());
        } catch (JSONException ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
    }

    public static class Model {}
}

