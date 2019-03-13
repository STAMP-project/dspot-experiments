package com.alibaba.json.bvt.issue_1500;


import SerializerFeature.WriteNullBooleanAsFalse;
import SerializerFeature.WriteNullListAsEmpty;
import SerializerFeature.WriteNullNumberAsZero;
import SerializerFeature.WriteNullStringAsEmpty;
import com.alibaba.fastjson.JSON;
import java.util.List;
import junit.framework.TestCase;


public class Issue1570_private extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1570_private.Model model = new Issue1570_private.Model();
        TestCase.assertEquals("{}", JSON.toJSONString(model, WriteNullBooleanAsFalse));
        TestCase.assertEquals("{\"value\":\"\"}", JSON.toJSONString(model, WriteNullStringAsEmpty));
    }

    public void test_for_issue_int() throws Exception {
        Issue1570_private.ModelInt model = new Issue1570_private.ModelInt();
        TestCase.assertEquals("{}", JSON.toJSONString(model, WriteNullBooleanAsFalse));
        TestCase.assertEquals("{\"value\":0}", JSON.toJSONString(model, WriteNullNumberAsZero));
    }

    public void test_for_issue_long() throws Exception {
        Issue1570_private.ModelLong model = new Issue1570_private.ModelLong();
        TestCase.assertEquals("{}", JSON.toJSONString(model, WriteNullBooleanAsFalse));
        TestCase.assertEquals("{\"value\":0}", JSON.toJSONString(model, WriteNullNumberAsZero));
    }

    public void test_for_issue_bool() throws Exception {
        Issue1570_private.ModelBool model = new Issue1570_private.ModelBool();
        TestCase.assertEquals("{}", JSON.toJSONString(model, WriteNullNumberAsZero));
        TestCase.assertEquals("{\"value\":false}", JSON.toJSONString(model, WriteNullBooleanAsFalse));
    }

    public void test_for_issue_list() throws Exception {
        Issue1570_private.ModelList model = new Issue1570_private.ModelList();
        TestCase.assertEquals("{}", JSON.toJSONString(model, WriteNullNumberAsZero));
        TestCase.assertEquals("{\"value\":[]}", JSON.toJSONString(model, WriteNullListAsEmpty));
    }

    private static class Model {
        public String value;
    }

    private static class ModelInt {
        public Integer value;
    }

    private static class ModelLong {
        public Long value;
    }

    private static class ModelBool {
        public Boolean value;
    }

    private static class ModelList {
        public List value;
    }
}

