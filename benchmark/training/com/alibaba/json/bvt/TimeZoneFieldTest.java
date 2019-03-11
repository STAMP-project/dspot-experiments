package com.alibaba.json.bvt;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.junit.Assert;


public class TimeZoneFieldTest extends TestCase {
    public void test_codec() throws Exception {
        TimeZoneFieldTest.User user = new TimeZoneFieldTest.User();
        user.setValue(TimeZone.getDefault());
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(user, mapping, WriteMapNullValue);
        TimeZoneFieldTest.User user1 = JSON.parseObject(text, TimeZoneFieldTest.User.class);
        Assert.assertEquals(user1.getValue(), user.getValue());
    }

    public void test_codec_null() throws Exception {
        TimeZoneFieldTest.User user = new TimeZoneFieldTest.User();
        user.setValue(null);
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(user, mapping, WriteMapNullValue);
        TimeZoneFieldTest.User user1 = JSON.parseObject(text, TimeZoneFieldTest.User.class);
        Assert.assertEquals(user1.getValue(), user.getValue());
    }

    public static class User {
        private TimeZone value;

        public TimeZone getValue() {
            return value;
        }

        public void setValue(TimeZone value) {
            this.value = value;
        }
    }
}

