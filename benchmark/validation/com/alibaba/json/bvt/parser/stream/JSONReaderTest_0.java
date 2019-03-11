package com.alibaba.json.bvt.parser.stream;


import Feature.AllowArbitraryCommas;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import java.io.StringReader;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONReaderTest_0 extends TestCase {
    public void test_read() throws Exception {
        JSONReader reader = new JSONReader(new StringReader("{}"));
        reader.config(AllowArbitraryCommas, true);
        JSONObject object = ((JSONObject) (reader.readObject()));
        Assert.assertNotNull(object);
        reader.close();
    }
}

