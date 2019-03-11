package com.alibaba.json.bvt.parser.stream;


import JSONToken.COLON;
import JSONToken.LBRACE;
import JSONToken.LITERAL_STRING;
import com.alibaba.fastjson.JSONReader;
import java.io.StringReader;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONReaderTest_1 extends TestCase {
    public void test_read() throws Exception {
        String text = "{\"id\":1001}";
        JSONReader reader = new JSONReader(new StringReader(text));
        Assert.assertEquals(LBRACE, reader.peek());
        reader.startObject();
        Assert.assertEquals(LITERAL_STRING, reader.peek());
        Assert.assertEquals("id", reader.readString());
        Assert.assertEquals(COLON, reader.peek());
        Assert.assertEquals(Integer.valueOf(1001), reader.readInteger());
        reader.endObject();
        reader.close();
    }
}

