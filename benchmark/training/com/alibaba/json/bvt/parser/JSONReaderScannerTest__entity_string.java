package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONReaderScanner;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONReaderScannerTest__entity_string extends TestCase {
    public void test_scanInt() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append('[');
        for (int i = 0; i < 10; ++i) {
            if (i != 0) {
                buf.append(',');
            }
            // 1000000000000
            // 
            buf.append((("{\"id\":\"" + i) + "\"}"));
        }
        buf.append(']');
        Reader reader = new StringReader(buf.toString());
        JSONReaderScanner scanner = new JSONReaderScanner(reader);
        DefaultJSONParser parser = new DefaultJSONParser(scanner);
        List<JSONReaderScannerTest__entity_string.VO> array = parser.parseArray(JSONReaderScannerTest__entity_string.VO.class);
        for (int i = 0; i < (array.size()); ++i) {
            Assert.assertEquals(Integer.toString(i), array.get(i).getId());
        }
    }

    public static class VO {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}

