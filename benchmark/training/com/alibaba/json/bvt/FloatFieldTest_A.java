package com.alibaba.json.bvt;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class FloatFieldTest_A extends TestCase {
    public void test_codec() throws Exception {
        FloatFieldTest_A.User user = new FloatFieldTest_A.User();
        user.setValue(1001.0F);
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(user, mapping, WriteMapNullValue);
        System.out.println(text);
        FloatFieldTest_A.User user1 = JSON.parseObject(text, FloatFieldTest_A.User.class);
        Assert.assertEquals(user1.getValue(), user.getValue());
    }

    public void test_codec_null() throws Exception {
        FloatFieldTest_A.User user = new FloatFieldTest_A.User();
        user.setValue(null);
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(user, mapping, WriteMapNullValue);
        System.out.println(text);
        FloatFieldTest_A.User user1 = JSON.parseObject(text, FloatFieldTest_A.User.class);
        Assert.assertEquals(user1.getValue(), user.getValue());
    }

    public static class User {
        private Float value;

        public Float getValue() {
            return value;
        }

        public void setValue(Float value) {
            this.value = value;
        }
    }
}

