package com.alibaba.json.bvt;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.util.regex.Pattern;
import junit.framework.TestCase;
import org.junit.Assert;


public class PatternFieldTest extends TestCase {
    public void test_codec() throws Exception {
        PatternFieldTest.User user = new PatternFieldTest.User();
        user.setValue(Pattern.compile("."));
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(user, mapping, WriteMapNullValue);
        PatternFieldTest.User user1 = JSON.parseObject(text, PatternFieldTest.User.class);
        Assert.assertEquals(user1.getValue().pattern(), user.getValue().pattern());
    }

    public void test_codec_null() throws Exception {
        PatternFieldTest.User user = new PatternFieldTest.User();
        user.setValue(null);
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(user, mapping, WriteMapNullValue);
        PatternFieldTest.User user1 = JSON.parseObject(text, PatternFieldTest.User.class);
        Assert.assertEquals(user1.getValue(), user.getValue());
    }

    public static class User {
        private Pattern value;

        public Pattern getValue() {
            return value;
        }

        public void setValue(Pattern value) {
            this.value = value;
        }
    }
}

