package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Bug_for_primitive_boolean extends TestCase {
    public void test_emptyStr() throws Exception {
        JSON.parseObject("{\"value\":\"\"}", Bug_for_primitive_boolean.VO.class);
    }

    public void test_null() throws Exception {
        JSON.parseObject("{\"value\":null}", Bug_for_primitive_boolean.VO.class);
    }

    public void test_strNull() throws Exception {
        JSON.parseObject("{\"value\":\"null\"}", Bug_for_primitive_boolean.VO.class);
    }

    public static class VO {
        private boolean value;

        public boolean getValue() {
            return value;
        }

        public void setValue(boolean value) {
            throw new UnsupportedOperationException();
        }
    }
}

