package com.alibaba.json.bvt.parser.deser.generic;


import com.alibaba.fastjson.JSON;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class ListStrFieldTest extends TestCase {
    public void test_0() throws Exception {
        ListStrFieldTest.Model model = JSON.parseObject("{\"values\":null}", ListStrFieldTest.Model.class);
        Assert.assertNull(model.values);
    }

    public static class Model {
        public List<String> values;
    }
}

