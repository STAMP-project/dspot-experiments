package com.alibaba.json.bvt.parser.deser.deny;


import ParserConfig.AUTOTYPE_SUPPORT_PROPERTY;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import java.util.Properties;
import junit.framework.TestCase;


public class DenyTest8 extends TestCase {
    public void test_autoTypeDeny() throws Exception {
        ParserConfig config = new ParserConfig();
        TestCase.assertFalse(config.isAutoTypeSupport());
        config.setAutoTypeSupport(true);
        TestCase.assertTrue(config.isAutoTypeSupport());
        Properties properties = new Properties();
        properties.put(AUTOTYPE_SUPPORT_PROPERTY, "false");
        config.configFromPropety(properties);
        TestCase.assertFalse(config.isAutoTypeSupport());
        config.addAccept("com.alibaba.json.bvt.parser.deser.deny.DenyTest8");
        Object obj = JSON.parseObject("{\"@type\":\"com.alibaba.json.bvt.parser.deser.deny.DenyTest8$Model\"}", Object.class, config);
        TestCase.assertEquals(DenyTest8.Model.class, obj.getClass());
    }

    public static class Model {}
}

