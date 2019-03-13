package com.alibaba.json.bvt.feature;


import SerializerFeature.IgnoreErrorGetter;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class IgnoreErrorGetterTest_field extends TestCase {
    public void test_feature() throws Exception {
        IgnoreErrorGetterTest_field.Model model = new IgnoreErrorGetterTest_field.Model();
        model.setId(1001);
        String text = JSON.toJSONString(model, IgnoreErrorGetter);
        Assert.assertEquals("{\"id\":1001}", text);
    }

    public static class Model {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            throw new IllegalStateException();
        }
    }
}

