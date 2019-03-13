package com.alibaba.json.bvt;


import SerializerFeature.QuoteFieldNames;
import SerializerFeature.UseSingleQuotes;
import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullListAsEmpty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.io.File;
import junit.framework.TestCase;
import org.junit.Assert;


public class FileFieldTest extends TestCase {
    public void test_codec_null() throws Exception {
        FileFieldTest.V0 v = new FileFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{\"value\":null}", text);
        FileFieldTest.V0 v1 = JSON.parseObject(text, FileFieldTest.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_codec_null_1() throws Exception {
        FileFieldTest.V0 v = new FileFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        Assert.assertEquals("{\"value\":null}", JSON.toJSONString(v, mapping, WriteMapNullValue, WriteNullListAsEmpty));
        Assert.assertEquals("{value:null}", JSON.toJSONStringZ(v, mapping, WriteMapNullValue, WriteNullListAsEmpty));
        Assert.assertEquals("{value:null}", JSON.toJSONStringZ(v, mapping, UseSingleQuotes, WriteMapNullValue, WriteNullListAsEmpty));
        Assert.assertEquals("{'value':null}", JSON.toJSONStringZ(v, mapping, UseSingleQuotes, QuoteFieldNames, WriteMapNullValue, WriteNullListAsEmpty));
    }

    public static class V0 {
        private File value;

        public File getValue() {
            return value;
        }

        public void setValue(File value) {
            this.value = value;
        }
    }
}

