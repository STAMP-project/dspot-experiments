package com.alibaba.json.bvt.parser.deser.deny;


import ParserConfig.AUTOTYPE_ACCEPT;
import ParserConfig.AUTOTYPE_SUPPORT_PROPERTY;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import java.util.Properties;
import junit.framework.TestCase;


public class DenyTest9 extends TestCase {
    public void test_autoTypeDeny() throws Exception {
        ParserConfig config = new ParserConfig();
        TestCase.assertFalse(config.isAutoTypeSupport());
        config.setAutoTypeSupport(true);
        TestCase.assertTrue(config.isAutoTypeSupport());
        Properties properties = new Properties();
        properties.put(AUTOTYPE_SUPPORT_PROPERTY, "false");
        properties.put(AUTOTYPE_ACCEPT, "com.alibaba.json.bvt.parser.deser.deny.DenyTest9");
        // -ea -Dfastjson.parser.autoTypeAccept=com.alibaba.json.bvt.parser.deser.deny.DenyTest9
        config.configFromPropety(properties);
        TestCase.assertFalse(config.isAutoTypeSupport());
        Object obj = JSON.parseObject("{\"@type\":\"com.alibaba.json.bvt.parser.deser.deny.DenyTest9$Model\"}", Object.class, config);
        TestCase.assertEquals(DenyTest9.Model.class, obj.getClass());
    }

    public static class Model {}
}

