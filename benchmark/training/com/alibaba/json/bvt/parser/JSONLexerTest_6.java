package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import java.util.LinkedList;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONLexerTest_6 extends TestCase {
    public void test_scanFieldString() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"values\":[\"abc\"]}", JSONLexerTest_6.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class VO {
        public JSONLexerTest_6.MyList<String> values;

        public JSONLexerTest_6.MyList<String> getValues() {
            return values;
        }

        public void setValues(JSONLexerTest_6.MyList<String> values) {
            this.values = values;
        }
    }

    @SuppressWarnings("serial")
    private class MyList<T> extends LinkedList<T> {}
}

