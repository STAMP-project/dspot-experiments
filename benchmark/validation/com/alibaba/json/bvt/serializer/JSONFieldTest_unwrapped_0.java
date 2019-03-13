package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONFieldTest_unwrapped_0 extends TestCase {
    public void test_jsonField() throws Exception {
        JSONFieldTest_unwrapped_0.VO vo = new JSONFieldTest_unwrapped_0.VO();
        vo.id = 123;
        vo.localtion = new JSONFieldTest_unwrapped_0.Localtion(127, 37);
        String text = JSON.toJSONString(vo);
        Assert.assertEquals("{\"id\":123,\"latitude\":37,\"longitude\":127}", text);
        JSONFieldTest_unwrapped_0.VO vo2 = JSON.parseObject(text, JSONFieldTest_unwrapped_0.VO.class);
        TestCase.assertNotNull(vo2.localtion);
        TestCase.assertEquals(vo.localtion.latitude, vo2.localtion.latitude);
        TestCase.assertEquals(vo.localtion.longitude, vo2.localtion.longitude);
    }

    public static class VO {
        public int id;

        @JSONField(unwrapped = true)
        public JSONFieldTest_unwrapped_0.Localtion localtion;
    }

    public static class Localtion {
        public int longitude;

        public int latitude;

        public Localtion() {
        }

        public Localtion(int longitude, int latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }
    }
}

