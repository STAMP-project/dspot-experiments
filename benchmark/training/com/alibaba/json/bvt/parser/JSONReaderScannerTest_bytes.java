package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONReader;
import java.io.StringReader;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONReaderScannerTest_bytes extends TestCase {
    public void test_e() throws Exception {
        JSONReaderScannerTest_bytes.VO vo = new JSONReaderScannerTest_bytes.VO();
        vo.setValue("ABC".getBytes("UTF-8"));
        String text = JSON.toJSONString(vo);
        JSONReader reader = new JSONReader(new StringReader(text));
        JSONReaderScannerTest_bytes.VO vo2 = reader.readObject(JSONReaderScannerTest_bytes.VO.class);
        Assert.assertEquals("ABC", new String(vo2.getValue()));
        reader.close();
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

