package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONLexerTest_11 extends TestCase {
    public void test_a() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"type\":[\"AAA\"]}", JSONLexerTest_11.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class VO {
        public VO() {
        }

        private JSONLexerTest_11.MyList<String> type;

        public JSONLexerTest_11.MyList<String> getType() {
            return type;
        }

        public void setType(JSONLexerTest_11.MyList<String> type) {
            this.type = type;
        }
    }

    public static class MyList<T> extends ArrayList<T> {
        public MyList() {
            throw new RuntimeException();
        }
    }
}

