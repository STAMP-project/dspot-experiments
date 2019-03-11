package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestSpecial4 extends TestCase {
    public void test_0() throws Exception {
        StringBuilder buf = new StringBuilder();
        buf.append('\r');
        buf.append('\r');
        for (int i = 0; i < 1000; ++i) {
            buf.append('\u2028');
        }
        TestSpecial4.VO vo = new TestSpecial4.VO();
        vo.setValue(buf.toString());
        String text = JSON.toJSONString(vo);
        TestSpecial4.VO vo2 = JSON.parseObject(text, TestSpecial4.VO.class);
        Assert.assertEquals(vo.value, vo2.value);
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

