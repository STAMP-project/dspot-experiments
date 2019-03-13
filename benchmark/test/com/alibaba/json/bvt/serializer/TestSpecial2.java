package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class TestSpecial2 extends TestCase {
    public void test_0() throws Exception {
        StringBuilder buf = new StringBuilder();
        buf.append('\r');
        buf.append('\r');
        for (int i = 0; i < 1000; ++i) {
            buf.append(((char) (160)));
        }
        TestSpecial2.VO vo = new TestSpecial2.VO();
        vo.setValue(buf.toString());
        System.out.println(JSON.toJSONString(vo));
    }

    public static class VO {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

