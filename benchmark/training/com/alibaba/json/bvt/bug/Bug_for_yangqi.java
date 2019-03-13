package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


public class Bug_for_yangqi extends TestCase {
    public void test_for_bug() throws Exception {
        Bug_for_yangqi.B b = JSON.parseObject("{\"id\":123,\"values\":[{}]}", Bug_for_yangqi.B.class);
    }

    abstract static class A {
        private int id;

        private List<Bug_for_yangqi.Value> values = new ArrayList<Bug_for_yangqi.Value>();

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<Bug_for_yangqi.Value> getValues() {
            return values;
        }

        public void setValues(List<Bug_for_yangqi.Value> values) {
            this.values = values;
        }
    }

    public static class B extends Bug_for_yangqi.A {}

    public static class Value {}
}

