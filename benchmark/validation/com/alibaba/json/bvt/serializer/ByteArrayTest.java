package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import junit.framework.TestCase;
import org.junit.Assert;


public class ByteArrayTest extends TestCase {
    public void test_bytes() throws Exception {
        ByteArrayTest.VO vo = new ByteArrayTest.VO();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        SerializerFeature[] features = new SerializerFeature[]{ SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullListAsEmpty };
        String text1 = JSON.toJSONString(vo, mapping, features);
        Assert.assertEquals("{\"value\":[]}", text1);
        String text2 = JSON.toJSONString(vo, features);
        Assert.assertEquals("{\"value\":[]}", text2);
    }

    public void test_bytes_1() throws Exception {
        ByteArrayTest.VO vo = new ByteArrayTest.VO();
        vo.setValue(new byte[]{ 1, 2, 3 });
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        SerializerFeature[] features = new SerializerFeature[]{ SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullListAsEmpty };
        String text1 = JSON.toJSONString(vo, mapping, features);
        Assert.assertEquals("{\"value\":\"AQID\"}", text1);
        String text2 = JSON.toJSONString(vo, features);
        Assert.assertEquals("{\"value\":\"AQID\"}", text2);
    }

    public static class VO {
        private byte[] value;

        public byte[] getValue() {
            return value;
        }

        public void setValue(byte[] value) {
            this.value = value;
        }
    }
}

