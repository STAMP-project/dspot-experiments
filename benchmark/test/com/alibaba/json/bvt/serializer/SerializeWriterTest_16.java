package com.alibaba.json.bvt.serializer;


import SerializerFeature.BrowserCompatible;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import java.io.StringWriter;
import junit.framework.TestCase;
import org.junit.Assert;


public class SerializeWriterTest_16 extends TestCase {
    public void test_writer_1() throws Exception {
        StringWriter strOut = new StringWriter();
        SerializeWriter out = new SerializeWriter(strOut, 14);
        out.config(BrowserCompatible, true);
        try {
            JSONSerializer serializer = new JSONSerializer(out);
            SerializeWriterTest_16.VO vo = new SerializeWriterTest_16.VO();
            vo.setValue("abcd\t");
            serializer.write(vo);
        } finally {
            out.close();
        }
        Assert.assertEquals("{value:\"abcd\\t\"}", strOut.toString());
    }

    private static class VO {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

