package com.alibaba.json.bvt;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.net.InetAddress;
import junit.framework.TestCase;
import org.junit.Assert;


public class InetAddressFieldTest extends TestCase {
    public void test_codec() throws Exception {
        InetAddressFieldTest.User user = new InetAddressFieldTest.User();
        user.setValue(InetAddress.getLocalHost());
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(user, mapping, WriteMapNullValue);
        InetAddressFieldTest.User user1 = JSON.parseObject(text, InetAddressFieldTest.User.class);
        Assert.assertEquals(user1.getValue(), user.getValue());
    }

    public void test_codec_null() throws Exception {
        InetAddressFieldTest.User user = new InetAddressFieldTest.User();
        user.setValue(null);
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(user, mapping, WriteMapNullValue);
        InetAddressFieldTest.User user1 = JSON.parseObject(text, InetAddressFieldTest.User.class);
        Assert.assertEquals(user1.getValue(), user.getValue());
    }

    public static class User {
        private InetAddress value;

        public InetAddress getValue() {
            return value;
        }

        public void setValue(InetAddress value) {
            this.value = value;
        }
    }
}

