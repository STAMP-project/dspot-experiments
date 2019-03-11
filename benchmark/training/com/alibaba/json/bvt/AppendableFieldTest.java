package com.alibaba.json.bvt;


import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullStringAsEmpty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class AppendableFieldTest extends TestCase {
    public void test_codec_null() throws Exception {
        AppendableFieldTest.V0 v = new AppendableFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{\"value\":null}", text);
    }

    public void test_codec_null_1() throws Exception {
        AppendableFieldTest.V0 v = new AppendableFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        Assert.assertTrue((!(mapping.isAsmEnable())));
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue, WriteNullStringAsEmpty);
        Assert.assertEquals("{\"value\":\"\"}", text);
    }

    public static class V0 {
        private Appendable value;

        public Appendable getValue() {
            return value;
        }

        public void setValue(Appendable value) {
            this.value = value;
        }
    }
}

