package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSONPath;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONPath_list_size_3 extends TestCase {
    public void test_java_bean() throws Exception {
        JSONPath_list_size_3.Model model = new JSONPath_list_size_3.Model();
        model.id = 1001;
        model.name = "wenshao";
        JSONPath path = new JSONPath("$.size()");
        Integer result = ((Integer) (path.eval(model)));
        Assert.assertEquals(2, result.intValue());
    }

    public void test_java_bean_field_null() throws Exception {
        JSONPath_list_size_3.Model model = new JSONPath_list_size_3.Model();
        model.id = 1001;
        model.name = null;
        JSONPath path = new JSONPath("$.size()");
        Integer result = ((Integer) (path.eval(model)));
        Assert.assertEquals(1, result.intValue());
    }

    public static class Model {
        public int id;

        public String name;
    }
}

