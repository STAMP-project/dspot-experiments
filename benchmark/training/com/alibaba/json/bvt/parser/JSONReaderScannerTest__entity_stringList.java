package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONReaderScanner;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONReaderScannerTest__entity_stringList extends TestCase {
    public void test_scanInt() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append('[');
        for (int i = 0; i < 10; ++i) {
            if (i != 0) {
                buf.append(',');
            }
            // 1000000000000
            // 
            buf.append((((("{\"id\":[\"" + i) + "\",\"") + (10000 + i)) + "\"]}"));
        }
        buf.append(']');
        Reader reader = new StringReader(buf.toString());
        JSONReaderScanner scanner = new JSONReaderScanner(reader);
        DefaultJSONParser parser = new DefaultJSONParser(scanner);
        List<JSONReaderScannerTest__entity_stringList.VO> array = parser.parseArray(JSONReaderScannerTest__entity_stringList.VO.class);
        for (int i = 0; i < (array.size()); ++i) {
            Assert.assertEquals(2, array.get(i).getId().size());
            Assert.assertEquals(Integer.toString(i), array.get(i).getId().get(0));
            Assert.assertEquals(Integer.toString((10000 + i)), array.get(i).getId().get(1));
        }
    }

    public static class VO {
        private List<String> id;

        public List<String> getId() {
            return id;
        }

        public void setId(List<String> id) {
            this.id = id;
        }
    }
}

