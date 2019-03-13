package com.alibaba.json.bvt.serializer;


import SerializerFeature.QuoteFieldNames;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import junit.framework.TestCase;
import org.junit.Assert;


public class SerializeWriterTest_18 extends TestCase {
    public void test_writer_1() throws Exception {
        SerializeWriter out = new SerializeWriter(14);
        out.config(QuoteFieldNames, true);
        try {
            JSONSerializer serializer = new JSONSerializer(out);
            SerializeWriterTest_18.VO vo = new SerializeWriterTest_18.VO();
            vo.setValue("#");
            serializer.write(vo);
            Assert.assertEquals("{\"value\":\"#\"}", out.toString());
        } finally {
            out.close();
        }
    }

    public static class VO {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

