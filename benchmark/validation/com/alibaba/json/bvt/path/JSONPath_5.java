package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


public class JSONPath_5 extends TestCase {
    public void test_path() throws Exception {
        JSONPath_5.Model m = new JSONPath_5.Model();
        JSONPath_5.Value v = new JSONPath_5.Value(m);
        m.values.add(v);
        m.values.add(m.values);
        m.values.add(m);
        String json = JSON.toJSONString(m);
        System.out.println(json);
    }

    public static class Model {
        public List values = new ArrayList();
    }

    public static class Value {
        public JSONPath_5.Model model = new JSONPath_5.Model();

        public Value() {
        }

        public Value(JSONPath_5.Model model) {
            this.model = model;
        }
    }
}

