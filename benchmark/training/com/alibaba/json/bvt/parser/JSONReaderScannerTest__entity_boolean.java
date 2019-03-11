package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONReaderScanner;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONReaderScannerTest__entity_boolean extends TestCase {
    public void test_scanInt() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append('[');
        for (int i = 0; i < 10; ++i) {
            if (i != 0) {
                buf.append(',');
            }
            // 1000000000000
            // 
            if ((i % 2) == 0) {
                buf.append("{\"id\":true}");
            } else {
                buf.append("{\"id\":false}");
            }
        }
        buf.append(']');
        Reader reader = new StringReader(buf.toString());
        JSONReaderScanner scanner = new JSONReaderScanner(reader);
        DefaultJSONParser parser = new DefaultJSONParser(scanner);
        List<JSONReaderScannerTest__entity_boolean.VO> array = parser.parseArray(JSONReaderScannerTest__entity_boolean.VO.class);
        for (int i = 0; i < (array.size()); ++i) {
            if ((i % 2) == 0) {
                Assert.assertEquals(true, array.get(i).getId());
            } else {
                Assert.assertEquals(false, array.get(i).getId());
            }
        }
    }

    public static class VO {
        private boolean id;

        public boolean getId() {
            return id;
        }

        public void setId(boolean id) {
            this.id = id;
        }
    }
}

