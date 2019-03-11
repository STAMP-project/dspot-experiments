package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.json.test.TestUtils;
import java.io.UnsupportedEncodingException;
import junit.framework.TestCase;
import org.junit.Assert;


public class ByteArrayFieldTest_3 extends TestCase {
    public void test_0() throws Exception {
        ByteArrayFieldTest_3.Entity entity = new ByteArrayFieldTest_3.Entity("???????");
        String text = JSON.toJSONString(entity);
        JSONObject json = JSON.parseObject(text);
        Assert.assertEquals(TestUtils.encodeToBase64String(entity.getValue(), false), json.getString("value"));
        ByteArrayFieldTest_3.Entity entity2 = JSON.parseObject(text, ByteArrayFieldTest_3.Entity.class);
        Assert.assertEquals("???????", new String(entity2.getValue(), "UTF-8"));
    }

    private static class Entity {
        private byte[] value;

        public Entity() {
        }

        public Entity(String value) throws UnsupportedEncodingException {
            this.value = value.getBytes("UTF-8");
        }

        public byte[] getValue() {
            return value;
        }

        public void setValue(byte[] value) {
            this.value = value;
        }
    }
}

