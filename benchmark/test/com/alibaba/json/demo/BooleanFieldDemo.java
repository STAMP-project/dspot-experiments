package com.alibaba.json.demo;


import SerializerFeature.IgnoreNonFieldGetter;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


/**
 * Created by wenshao on 15/02/2017.
 */
public class BooleanFieldDemo extends TestCase {
    public void test_boolean() throws Exception {
        BooleanFieldDemo.Model model = new BooleanFieldDemo.Model();
        String json = JSON.toJSONString(model, IgnoreNonFieldGetter);
        System.out.println(json);
    }

    public static class Model {
        public boolean isAvailable() {
            return true;
        }
    }
}

