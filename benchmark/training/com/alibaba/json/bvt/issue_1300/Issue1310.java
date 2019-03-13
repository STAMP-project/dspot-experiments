package com.alibaba.json.bvt.issue_1300;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;


/**
 * Created by wenshao on 29/07/2017.
 */
public class Issue1310 extends TestCase {
    public void test_trim() throws Exception {
        Issue1310.Model model = new Issue1310.Model();
        model.value = " a ";
        TestCase.assertEquals("{\"value\":\"a\"}", JSON.toJSONString(model));
        Issue1310.Model model2 = JSON.parseObject("{\"value\":\" a \"}", Issue1310.Model.class);
        TestCase.assertEquals("a", model2.value);
    }

    public static class Model {
        @JSONField(format = "trim")
        public String value;
    }
}

