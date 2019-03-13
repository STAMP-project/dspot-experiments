package com.alibaba.json.bvt.parser.stream;


import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONReaderScanner;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONReaderScannerTest_matchField extends TestCase {
    public void test_true() throws Exception {
        DefaultJSONParser parser = new DefaultJSONParser(new JSONReaderScanner("{\"items\":[{}],\"value\":{}}"));
        JSONReaderScannerTest_matchField.VO vo = parser.parseObject(JSONReaderScannerTest_matchField.VO.class);
        Assert.assertNotNull(vo.getValue());
        Assert.assertNotNull(vo.getItems());
        Assert.assertEquals(1, vo.getItems().size());
        Assert.assertNotNull(vo.getItems().get(0));
        parser.close();
    }

    public static class VO {
        private List<JSONReaderScannerTest_matchField.Item> items;

        private JSONReaderScannerTest_matchField.Entity value;

        public JSONReaderScannerTest_matchField.Entity getValue() {
            return value;
        }

        public void setValue(JSONReaderScannerTest_matchField.Entity value) {
            this.value = value;
        }

        public List<JSONReaderScannerTest_matchField.Item> getItems() {
            return items;
        }

        public void setItems(List<JSONReaderScannerTest_matchField.Item> items) {
            this.items = items;
        }
    }

    public static class Entity {}

    public static class Item {}
}

