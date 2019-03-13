package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import java.util.LinkedList;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONLexerTest_5 extends TestCase {
    public void test_scanFieldString() throws Exception {
        JSONLexerTest_5.VO vo = JSON.parseObject("{\"values\":[\"abc\"]}", JSONLexerTest_5.VO.class);
        Assert.assertEquals("abc", vo.getValues().get(0));
    }

    public static class VO {
        public LinkedList<String> values;

        public LinkedList<String> getValues() {
            return values;
        }

        public void setValues(LinkedList<String> values) {
            this.values = values;
        }
    }
}

