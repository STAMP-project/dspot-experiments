package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONLexerTest_14 extends TestCase {
    public void test_e() throws Exception {
        StringBuffer buf = new StringBuffer();
        buf.append("{\"type\":\'");
        for (int i = 0; i < 100; ++i) {
            buf.append('a');
        }
        buf.append("\\t");
        buf.append("'}");
        JSONLexerTest_14.VO vo = JSON.parseObject(buf.toString(), JSONLexerTest_14.VO.class);
        String type = vo.getType();
        for (int i = 0; i < 100; ++i) {
            Assert.assertEquals('a', type.charAt(i));
        }
        Assert.assertEquals('\t', type.charAt(100));
        Assert.assertEquals(101, type.length());
    }

    public static class VO {
        public VO() {
        }

        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}

