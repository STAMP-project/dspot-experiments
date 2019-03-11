package com.alibaba.json.bvt.issue_2200;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.Date;
import junit.framework.TestCase;


public class Issue2216 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue2216.Model model = JSON.parseObject("{\"value\":\"20181229162849000+0800\"}", Issue2216.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertNotNull(model.value);
        TestCase.assertEquals(1546072129000L, model.value.getTime());
    }

    public void test_for_issue_2() throws Exception {
        Issue2216.Model model = JSON.parseObject("{\"value\":\"20181229162849000+0800\"}").toJavaObject(Issue2216.Model.class);
        TestCase.assertNotNull(model);
        TestCase.assertNotNull(model.value);
        TestCase.assertEquals(1546072129000L, model.value.getTime());
    }

    public static class Model {
        @JSONField(format = "yyyyMMddHHmmssSSSZ")
        public Date value;
    }
}

