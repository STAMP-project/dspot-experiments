package com.alibaba.json.bvt.parser.stream;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONReaderScanner;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONReaderScannerTest extends TestCase {
    public void test_singleQuote() throws Exception {
        DefaultJSONParser parser = new DefaultJSONParser(new JSONReaderScanner("{\'name\':\'\u5f20\u4e09\\\'\\n\\r\\\"\'}"));
        JSONObject json = parser.parseObject();
        Assert.assertEquals("\u5f20\u4e09\'\n\r\"", json.get("name"));
        parser.close();
    }

    public void test_doubleQuote() throws Exception {
        DefaultJSONParser parser = new DefaultJSONParser(new JSONReaderScanner("{\"name\":\"\u5f20\u4e09\\\'\\n\\r\\\"\"}"));
        JSONObject json = parser.parseObject();
        Assert.assertEquals("\u5f20\u4e09\'\n\r\"", json.get("name"));
        parser.close();
    }

    public void test_doubleQuote_2() throws Exception {
        DefaultJSONParser parser = new DefaultJSONParser(new JSONReaderScanner("{name:\"\u5f20\u4e09\\\'\\n\\r\\\"\"}"));
        JSONObject json = parser.parseObject();
        Assert.assertEquals("\u5f20\u4e09\'\n\r\"", json.get("name"));
        parser.close();
    }
}

