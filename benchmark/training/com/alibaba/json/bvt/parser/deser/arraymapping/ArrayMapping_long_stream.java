package com.alibaba.json.bvt.parser.deser.arraymapping;


import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.parser.Feature;
import java.io.StringReader;
import junit.framework.TestCase;
import org.junit.Assert;


public class ArrayMapping_long_stream extends TestCase {
    public void test_for_error() throws Exception {
        JSONReader reader = new JSONReader(new StringReader("[1001,\"wenshao\"]"), Feature.SupportArrayToBean);
        ArrayMapping_long_stream.Model model = reader.readObject(ArrayMapping_long_stream.Model.class);
        Assert.assertEquals(1001, model.id);
        Assert.assertEquals("wenshao", model.name);
    }

    public static class Model {
        public long id;

        public String name;
    }
}

