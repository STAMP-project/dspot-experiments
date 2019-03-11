package com.alibaba.json.bvt;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.util.Locale;
import junit.framework.TestCase;
import org.junit.Assert;


public class LocaleFieldTest extends TestCase {
    public void test_codec() throws Exception {
        LocaleFieldTest.User user = new LocaleFieldTest.User();
        user.setValue(Locale.CANADA);
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(user, mapping, WriteMapNullValue);
        LocaleFieldTest.User user1 = JSON.parseObject(text, LocaleFieldTest.User.class);
        Assert.assertEquals(user1.getValue(), user.getValue());
    }

    public void test_codec_null() throws Exception {
        LocaleFieldTest.User user = new LocaleFieldTest.User();
        user.setValue(null);
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(user, mapping, WriteMapNullValue);
        LocaleFieldTest.User user1 = JSON.parseObject(text, LocaleFieldTest.User.class);
        Assert.assertEquals(user1.getValue(), user.getValue());
    }

    public static class User {
        private Locale value;

        public Locale getValue() {
            return value;
        }

        public void setValue(Locale value) {
            this.value = value;
        }
    }
}

