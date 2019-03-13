package com.alibaba.json.bvt.issue_1500;


import SerializerFeature.WriteNullBooleanAsFalse;
import SerializerFeature.WriteNullListAsEmpty;
import SerializerFeature.WriteNullNumberAsZero;
import SerializerFeature.WriteNullStringAsEmpty;
import com.alibaba.fastjson.JSON;
import java.util.List;
import junit.framework.TestCase;


public class Issue1570 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1570.Model model = new Issue1570.Model();
        TestCase.assertEquals("{}", JSON.toJSONString(model, WriteNullBooleanAsFalse));
        TestCase.assertEquals("{\"value\":\"\"}", JSON.toJSONString(model, WriteNullStringAsEmpty));
    }

    public void test_for_issue_int() throws Exception {
        Issue1570.ModelInt model = new Issue1570.ModelInt();
        TestCase.assertEquals("{}", JSON.toJSONString(model, WriteNullBooleanAsFalse));
        TestCase.assertEquals("{\"value\":0}", JSON.toJSONString(model, WriteNullNumberAsZero));
    }

    public void test_for_issue_long() throws Exception {
        Issue1570.ModelLong model = new Issue1570.ModelLong();
        TestCase.assertEquals("{}", JSON.toJSONString(model, WriteNullBooleanAsFalse));
        TestCase.assertEquals("{\"value\":0}", JSON.toJSONString(model, WriteNullNumberAsZero));
    }

    public void test_for_issue_bool() throws Exception {
        Issue1570.ModelBool model = new Issue1570.ModelBool();
        TestCase.assertEquals("{}", JSON.toJSONString(model, WriteNullNumberAsZero));
        TestCase.assertEquals("{\"value\":false}", JSON.toJSONString(model, WriteNullBooleanAsFalse));
    }

    public void test_for_issue_list() throws Exception {
        Issue1570.ModelList model = new Issue1570.ModelList();
        TestCase.assertEquals("{}", JSON.toJSONString(model, WriteNullNumberAsZero));
        TestCase.assertEquals("{\"value\":[]}", JSON.toJSONString(model, WriteNullListAsEmpty));
    }

    public static class Model {
        public String value;
    }

    public static class ModelInt {
        public Integer value;
    }

    public static class ModelLong {
        public Long value;
    }

    public static class ModelBool {
        public Boolean value;
    }

    public static class ModelList {
        public List value;
    }
}

