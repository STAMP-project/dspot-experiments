package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.LinkedHashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONFieldTest_unwrapped_2 extends TestCase {
    public void test_jsonField() throws Exception {
        String text = "{\"id\":123,\"latitude\":37,\"longitude\":127}";
        Assert.assertEquals("{\"id\":123,\"latitude\":37,\"longitude\":127}", text);
        JSONFieldTest_unwrapped_2.VO vo2 = JSON.parseObject(text, JSONFieldTest_unwrapped_2.VO.class);
        TestCase.assertNotNull(vo2.properties);
        TestCase.assertEquals(37, vo2.properties.get("latitude"));
        TestCase.assertEquals(127, vo2.properties.get("longitude"));
    }

    public static class VO {
        public int id;

        private Map<String, Object> properties = new LinkedHashMap<String, Object>();

        @JSONField(unwrapped = true)
        public void setProperty(String key, Object value) {
            properties.put(key, value);
        }
    }
}

