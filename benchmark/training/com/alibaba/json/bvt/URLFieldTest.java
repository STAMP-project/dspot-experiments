package com.alibaba.json.bvt;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.net.URL;
import junit.framework.TestCase;
import org.junit.Assert;


public class URLFieldTest extends TestCase {
    public void test_codec() throws Exception {
        URLFieldTest.User user = new URLFieldTest.User();
        user.setValue(new URL("http://code.alibaba-tech.com"));
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(user, mapping, WriteMapNullValue);
        System.out.println(text);
        URLFieldTest.User user1 = JSON.parseObject(text, URLFieldTest.User.class);
        Assert.assertEquals(user1.getValue().toString(), user.getValue().toString());
    }

    public void test_codec_null() throws Exception {
        URLFieldTest.User user = new URLFieldTest.User();
        user.setValue(null);
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(user, mapping, WriteMapNullValue);
        URLFieldTest.User user1 = JSON.parseObject(text, URLFieldTest.User.class);
        Assert.assertEquals(user1.getValue(), user.getValue());
    }

    public static class User {
        private URL value;

        public URL getValue() {
            return value;
        }

        public void setValue(URL value) {
            this.value = value;
        }
    }
}

