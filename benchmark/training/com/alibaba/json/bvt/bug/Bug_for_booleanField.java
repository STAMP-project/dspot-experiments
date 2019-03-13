package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_booleanField extends TestCase {
    public void test_boolean() throws Exception {
        Assert.assertEquals("{\"is-abc\":false}", JSON.toJSONString(new Bug_for_booleanField.BooleanJson()));
        Assert.assertTrue(JSON.parseObject("{\"is-abc\":true}", Bug_for_booleanField.BooleanJson.class).isAbc());
    }

    public static class BooleanJson {
        @JSONField(name = "is-abc")
        private boolean isAbc;

        public boolean isAbc() {
            return isAbc;
        }

        public void setAbc(boolean value) {
            this.isAbc = value;
        }
    }
}

