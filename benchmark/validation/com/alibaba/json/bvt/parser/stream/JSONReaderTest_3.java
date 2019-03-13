package com.alibaba.json.bvt.parser.stream;


import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONReader;
import java.io.StringReader;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONReaderTest_3 extends TestCase {
    public void test_read_Long() throws Exception {
        String text = "1001";
        JSONReader reader = new JSONReader(new StringReader(text));
        Exception error = null;
        try {
            reader.hasNext();
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }
}

