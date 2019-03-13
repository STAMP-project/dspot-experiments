package com.alibaba.json.bvt;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.net.InetSocketAddress;
import junit.framework.TestCase;
import org.junit.Assert;


public class InetSocketAddressFieldTest extends TestCase {
    public void test_codec() throws Exception {
        InetSocketAddressFieldTest.User user = new InetSocketAddressFieldTest.User();
        user.setValue(new InetSocketAddress(33));
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(user, mapping, WriteMapNullValue);
        InetSocketAddressFieldTest.User user1 = JSON.parseObject(text, InetSocketAddressFieldTest.User.class);
        Assert.assertEquals(user1.getValue(), user.getValue());
    }

    public void test_codec_null() throws Exception {
        InetSocketAddressFieldTest.User user = new InetSocketAddressFieldTest.User();
        user.setValue(null);
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(user, mapping, WriteMapNullValue);
        InetSocketAddressFieldTest.User user1 = JSON.parseObject(text, InetSocketAddressFieldTest.User.class);
        Assert.assertEquals(user1.getValue(), user.getValue());
    }

    public void test_codec_null_2() throws Exception {
        InetSocketAddressFieldTest.User user = JSON.parseObject("{\"value\":{\"address\":null,\"port\":33}}", InetSocketAddressFieldTest.User.class);
        Assert.assertEquals(33, user.getValue().getPort());
    }

    public static class User {
        private InetSocketAddress value;

        public InetSocketAddress getValue() {
            return value;
        }

        public void setValue(InetSocketAddress value) {
            this.value = value;
        }
    }
}

