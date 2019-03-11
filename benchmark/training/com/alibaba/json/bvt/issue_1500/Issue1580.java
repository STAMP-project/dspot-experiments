package com.alibaba.json.bvt.issue_1500;


import SerializerFeature.BeanToArray;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import junit.framework.TestCase;


public class Issue1580 extends TestCase {
    public void test_for_issue() throws Exception {
        SimplePropertyPreFilter classAFilter = new SimplePropertyPreFilter(Issue1580.Model.class, "code");
        SerializeFilter[] filters = new SerializeFilter[]{ classAFilter };
        Issue1580.Model model = new Issue1580.Model();
        model.code = 1001;
        model.name = "N1";
        String json = JSON.toJSONString(model, filters, BeanToArray);
        TestCase.assertEquals("[1001,null]", json);
    }

    public static class Model {
        private int code;

        private String name;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

