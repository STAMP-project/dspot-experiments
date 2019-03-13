package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class IntFieldTest extends TestCase {
    public void test_model() throws Exception {
        IntFieldTest.Model model = new IntFieldTest.Model();
        model.id = -1001;
        String text = JSON.toJSONString(model);
        Assert.assertEquals("{\"id\":-1001}", text);
    }

    public void test_model_max() throws Exception {
        IntFieldTest.Model model = new IntFieldTest.Model();
        model.id = Integer.MIN_VALUE;
        String text = JSON.toJSONString(model);
        Assert.assertEquals("{\"id\":-2147483648}", text);
    }

    public static class Model {
        public int id;
    }
}

