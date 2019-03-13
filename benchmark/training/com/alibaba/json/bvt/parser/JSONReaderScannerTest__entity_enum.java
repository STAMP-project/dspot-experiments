package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONReaderScanner;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONReaderScannerTest__entity_enum extends TestCase {
    public void test_scanInt() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append('[');
        for (int i = 0; i < 10; ++i) {
            if (i != 0) {
                buf.append(',');
            }
            // 1000000000000
            // 
            JSONReaderScannerTest__entity_enum.Type type;
            if ((i % 3) == 0) {
                type = JSONReaderScannerTest__entity_enum.Type.A;
            } else
                if ((i % 3) == 1) {
                    type = JSONReaderScannerTest__entity_enum.Type.AA;
                } else {
                    type = JSONReaderScannerTest__entity_enum.Type.AAA;
                }

            buf.append((("{\"id\":\"" + (type.name())) + "\"}"));
        }
        buf.append(']');
        Reader reader = new StringReader(buf.toString());
        JSONReaderScanner scanner = new JSONReaderScanner(reader);
        DefaultJSONParser parser = new DefaultJSONParser(scanner);
        List<JSONReaderScannerTest__entity_enum.VO> array = parser.parseArray(JSONReaderScannerTest__entity_enum.VO.class);
        for (int i = 0; i < (array.size()); ++i) {
            JSONReaderScannerTest__entity_enum.Type type;
            if ((i % 3) == 0) {
                type = JSONReaderScannerTest__entity_enum.Type.A;
            } else
                if ((i % 3) == 1) {
                    type = JSONReaderScannerTest__entity_enum.Type.AA;
                } else {
                    type = JSONReaderScannerTest__entity_enum.Type.AAA;
                }

            Assert.assertEquals(type, array.get(i).getId());
        }
    }

    public static class VO {
        private JSONReaderScannerTest__entity_enum.Type id;

        public JSONReaderScannerTest__entity_enum.Type getId() {
            return id;
        }

        public void setId(JSONReaderScannerTest__entity_enum.Type id) {
            this.id = id;
        }
    }

    public static enum Type {

        A,
        AA,
        AAA;}
}

