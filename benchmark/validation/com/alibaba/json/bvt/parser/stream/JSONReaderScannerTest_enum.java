package com.alibaba.json.bvt.parser.stream;


import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONReaderScanner;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONReaderScannerTest_enum extends TestCase {
    public void test_a() throws Exception {
        DefaultJSONParser parser = new DefaultJSONParser(new JSONReaderScanner("{\"type\":\"A\"}"));
        JSONReaderScannerTest_enum.VO vo = parser.parseObject(JSONReaderScannerTest_enum.VO.class);
        Assert.assertEquals(JSONReaderScannerTest_enum.Type.A, vo.getType());
        parser.close();
    }

    public void test_b() throws Exception {
        DefaultJSONParser parser = new DefaultJSONParser(new JSONReaderScanner("{\"type\":\"B\"}"));
        JSONReaderScannerTest_enum.VO vo = parser.parseObject(JSONReaderScannerTest_enum.VO.class);
        Assert.assertEquals(JSONReaderScannerTest_enum.Type.B, vo.getType());
        parser.close();
    }

    public void test_c() throws Exception {
        DefaultJSONParser parser = new DefaultJSONParser(new JSONReaderScanner("{\"type\":\"C\"}"));
        JSONReaderScannerTest_enum.VO vo = parser.parseObject(JSONReaderScannerTest_enum.VO.class);
        Assert.assertEquals(JSONReaderScannerTest_enum.Type.C, vo.getType());
        parser.close();
    }

    public void test_x() throws Exception {
        DefaultJSONParser parser = new DefaultJSONParser(new JSONReaderScanner("{\"type\":\"XXXXXXXXXXXXXXXXXXXXXXXX\"}"));
        JSONReaderScannerTest_enum.VO vo = parser.parseObject(JSONReaderScannerTest_enum.VO.class);
        Assert.assertEquals(JSONReaderScannerTest_enum.Type.XXXXXXXXXXXXXXXXXXXXXXXX, vo.getType());
        parser.close();
    }

    public static class VO {
        private JSONReaderScannerTest_enum.Type type;

        public JSONReaderScannerTest_enum.Type getType() {
            return type;
        }

        public void setType(JSONReaderScannerTest_enum.Type type) {
            this.type = type;
        }
    }

    public static enum Type {

        A,
        B,
        C,
        D,
        XXXXXXXXXXXXXXXXXXXXXXXX;}
}

