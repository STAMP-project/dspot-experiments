package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.LinkedHashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONFieldTest_unwrapped_1 extends TestCase {
    public void test_jsonField() throws Exception {
        JSONFieldTest_unwrapped_1.VO vo = new JSONFieldTest_unwrapped_1.VO();
        vo.id = 123;
        vo.properties.put("latitude", 37);
        vo.properties.put("longitude", 127);
        String text = JSON.toJSONString(vo);
        Assert.assertEquals("{\"id\":123,\"latitude\":37,\"longitude\":127}", text);
        JSONFieldTest_unwrapped_1.VO vo2 = JSON.parseObject(text, JSONFieldTest_unwrapped_1.VO.class);
        TestCase.assertNotNull(vo2.properties);
        TestCase.assertEquals(37, vo2.properties.get("latitude"));
        TestCase.assertEquals(127, vo2.properties.get("longitude"));
    }

    public static class VO {
        public int id;

        @JSONField(unwrapped = true)
        public Map<String, Object> properties = new LinkedHashMap<String, Object>();
    }
}

