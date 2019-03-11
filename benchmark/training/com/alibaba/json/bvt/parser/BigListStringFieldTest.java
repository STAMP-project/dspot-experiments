package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class BigListStringFieldTest extends TestCase {
    public void test_list() throws Exception {
        BigListStringFieldTest.Model model = new BigListStringFieldTest.Model();
        model.values = new ArrayList<String>(10000);
        for (int i = 0; i < 10000; ++i) {
            String value = random(100);
            model.values.add(value);
        }
        String text = JSON.toJSONString(model);
        BigListStringFieldTest.Model model2 = JSON.parseObject(text, BigListStringFieldTest.Model.class);
        Assert.assertEquals(model.values, model2.values);
    }

    public static class Model {
        public List<String> values;
    }
}

