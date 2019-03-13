package com.alibaba.json.bvt.feature;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import junit.framework.TestCase;
import org.junit.Assert;


public class FeaturesTest extends TestCase {
    public void test_0() throws Exception {
        SerializeConfig config = new SerializeConfig();
        config.setAsmEnable(false);
        String text = JSON.toJSONString(new FeaturesTest.Entity(), config);
        Assert.assertEquals("{\"value\":null}", text);
    }

    public void test_1() throws Exception {
        SerializeConfig config = new SerializeConfig();
        config.setAsmEnable(true);
        String text = JSON.toJSONString(new FeaturesTest.Entity(), config);
        Assert.assertEquals("{\"value\":null}", text);
    }

    public static class Entity {
        private Object value;

        @JSONField(serialzeFeatures = { SerializerFeature.WriteMapNullValue })
        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}

