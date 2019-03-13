package com.alibaba.json.bvt;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.util.UUID;
import junit.framework.TestCase;
import org.junit.Assert;


public class UUIDFieldTest extends TestCase {
    public void test_codec() throws Exception {
        UUIDFieldTest.User user = new UUIDFieldTest.User();
        user.setValue(UUID.randomUUID());
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(user, mapping, WriteMapNullValue);
        UUIDFieldTest.User user1 = JSON.parseObject(text, UUIDFieldTest.User.class);
        Assert.assertEquals(user1.getValue(), user.getValue());
    }

    public void test_codec_null() throws Exception {
        UUIDFieldTest.User user = new UUIDFieldTest.User();
        user.setValue(null);
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(user, mapping, WriteMapNullValue);
        UUIDFieldTest.User user1 = JSON.parseObject(text, UUIDFieldTest.User.class);
        Assert.assertEquals(user1.getValue(), user.getValue());
    }

    public static class User {
        private UUID value;

        public UUID getValue() {
            return value;
        }

        public void setValue(UUID value) {
            this.value = value;
        }
    }
}

