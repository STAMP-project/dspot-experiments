package com.alibaba.json.bvt;


import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullListAsEmpty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class IntegerArrayFieldTest extends TestCase {
    public void test_codec() throws Exception {
        IntegerArrayFieldTest.User user = new IntegerArrayFieldTest.User();
        user.setValue(new Integer[]{ Integer.valueOf(1), Integer.valueOf(2) });
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(user, mapping, WriteMapNullValue);
        IntegerArrayFieldTest.User user1 = JSON.parseObject(text, IntegerArrayFieldTest.User.class);
        Assert.assertEquals(user1.getValue()[0], user.getValue()[0]);
        Assert.assertEquals(user1.getValue()[1], user.getValue()[1]);
    }

    public void test_codec_null() throws Exception {
        IntegerArrayFieldTest.User user = new IntegerArrayFieldTest.User();
        user.setValue(null);
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(user, mapping, WriteMapNullValue);
        IntegerArrayFieldTest.User user1 = JSON.parseObject(text, IntegerArrayFieldTest.User.class);
        Assert.assertEquals(user1.getValue(), user.getValue());
    }

    public void test_codec_null_1() throws Exception {
        IntegerArrayFieldTest.User user = new IntegerArrayFieldTest.User();
        user.setValue(null);
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(user, mapping, WriteMapNullValue, WriteNullListAsEmpty);
        IntegerArrayFieldTest.User user1 = JSON.parseObject(text, IntegerArrayFieldTest.User.class);
        Assert.assertEquals(0, user1.getValue().length);
    }

    public static class User {
        private Integer[] value;

        public Integer[] getValue() {
            return value;
        }

        public void setValue(Integer[] value) {
            this.value = value;
        }
    }
}

