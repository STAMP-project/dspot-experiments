package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.LinkedHashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONFieldTest_unwrapped_4 extends TestCase {
    public void test_jsonField() throws Exception {
        JSONFieldTest_unwrapped_4.Health vo = new JSONFieldTest_unwrapped_4.Health();
        vo.id = 123;
        vo.border = 234;
        vo.details.put("latitude", 37);
        vo.details.put("longitude", 127);
        String text = JSON.toJSONString(vo);
        Assert.assertEquals("{\"border\":234,\"latitude\":37,\"longitude\":127,\"id\":123}", text);
        JSONFieldTest_unwrapped_4.Health vo2 = JSON.parseObject(text, JSONFieldTest_unwrapped_4.Health.class);
        TestCase.assertNotNull(vo2.details);
        TestCase.assertEquals(37, vo2.details.get("latitude"));
        TestCase.assertEquals(127, vo2.details.get("longitude"));
    }

    public void test_null() throws Exception {
        JSONFieldTest_unwrapped_4.Health vo = new JSONFieldTest_unwrapped_4.Health();
        vo.id = 123;
        vo.border = 234;
        vo.details = null;
        String text = JSON.toJSONString(vo);
        Assert.assertEquals("{\"border\":234,\"id\":123}", text);
    }

    public void test_empty() throws Exception {
        JSONFieldTest_unwrapped_4.Health vo = new JSONFieldTest_unwrapped_4.Health();
        vo.id = 123;
        vo.border = 234;
        String text = JSON.toJSONString(vo);
        Assert.assertEquals("{\"border\":234,\"id\":123}", text);
    }

    public static class Health {
        public int id;

        public int border;

        @JSONField(unwrapped = true)
        public Map<String, Object> details = new LinkedHashMap<String, Object>();
    }
}

