package com.alibaba.json.bvt;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.net.URI;
import junit.framework.TestCase;
import org.junit.Assert;


public class URIFieldTest extends TestCase {
    public void test_codec() throws Exception {
        URIFieldTest.User user = new URIFieldTest.User();
        user.setValue(URI.create("http://www.alibaba.com/abc"));
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(user, mapping, WriteMapNullValue);
        URIFieldTest.User user1 = JSON.parseObject(text, URIFieldTest.User.class);
        Assert.assertEquals(user1.getValue(), user.getValue());
    }

    public void test_codec_null() throws Exception {
        URIFieldTest.User user = new URIFieldTest.User();
        user.setValue(null);
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(user, mapping, WriteMapNullValue);
        URIFieldTest.User user1 = JSON.parseObject(text, URIFieldTest.User.class);
        Assert.assertEquals(user1.getValue(), user.getValue());
    }

    public static class User {
        private URI value;

        public URI getValue() {
            return value;
        }

        public void setValue(URI value) {
            this.value = value;
        }
    }
}

