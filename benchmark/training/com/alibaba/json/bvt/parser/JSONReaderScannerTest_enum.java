package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSONReader;
import java.io.StringReader;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONReaderScannerTest_enum extends TestCase {
    public void test_e() throws Exception {
        JSONReader reader = new JSONReader(new StringReader("{type:'AA'}"));
        JSONReaderScannerTest_enum.VO vo2 = reader.readObject(JSONReaderScannerTest_enum.VO.class);
        Assert.assertEquals(JSONReaderScannerTest_enum.Type.AA, vo2.getType());
        reader.close();
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

        AA,
        BB,
        CC;}
}

