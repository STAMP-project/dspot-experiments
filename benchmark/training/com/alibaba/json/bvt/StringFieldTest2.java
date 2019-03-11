package com.alibaba.json.bvt;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import junit.framework.TestCase;
import org.junit.Assert;


public class StringFieldTest2 extends TestCase {
    public void test_codec_null_1() throws Exception {
        StringFieldTest2.V0 v = new StringFieldTest2.V0();
        SerializeConfig mapping = new SerializeConfig();
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{\"value\":\"\"}", text);
    }

    public static class V0 {
        @JSONField(serialzeFeatures = SerializerFeature.WriteNullStringAsEmpty)
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

