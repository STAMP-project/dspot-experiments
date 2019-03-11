package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Bug_for_primitive_byte extends TestCase {
    public void test_emptyStr() throws Exception {
        JSON.parseObject("{\"value\":\"\"}", Bug_for_primitive_byte.VO.class);
    }

    public void test_null() throws Exception {
        JSON.parseObject("{\"value\":null}", Bug_for_primitive_byte.VO.class);
    }

    public void test_strNull() throws Exception {
        JSON.parseObject("{\"value\":\"null\"}", Bug_for_primitive_byte.VO.class);
    }

    public static class VO {
        private byte value;

        public byte getValue() {
            return value;
        }

        public void setValue(byte value) {
            throw new UnsupportedOperationException();
        }
    }
}

