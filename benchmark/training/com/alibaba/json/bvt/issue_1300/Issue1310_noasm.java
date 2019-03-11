package com.alibaba.json.bvt.issue_1300;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;


/**
 * Created by wenshao on 29/07/2017.
 */
public class Issue1310_noasm extends TestCase {
    public void test_trim() throws Exception {
        Issue1310_noasm.Model model = new Issue1310_noasm.Model();
        model.value = " a ";
        TestCase.assertEquals("{\"value\":\"a\"}", JSON.toJSONString(model));
        Issue1310_noasm.Model model2 = JSON.parseObject("{\"value\":\" a \"}", Issue1310_noasm.Model.class);
        TestCase.assertEquals("a", model2.value);
    }

    private static class Model {
        @JSONField(format = "trim")
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

