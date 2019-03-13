package com.alibaba.json.bvt.feature;


import SerializerFeature.IgnoreErrorGetter;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class IgnoreErrorGetterTest extends TestCase {
    public void test_feature() throws Exception {
        IgnoreErrorGetterTest.Model model = new IgnoreErrorGetterTest.Model();
        model.id = 1001;
        String text = JSON.toJSONString(model, IgnoreErrorGetter);
        Assert.assertEquals("{\"id\":1001}", text);
    }

    public static class Model {
        public int id;
    }
}

