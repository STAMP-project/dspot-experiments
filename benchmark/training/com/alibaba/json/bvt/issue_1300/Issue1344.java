package com.alibaba.json.bvt.issue_1300;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONCreator;
import junit.framework.TestCase;


/**
 * Created by wenshao on 26/07/2017.
 */
public class Issue1344 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1344.TestException testException = new Issue1344.TestException("aaa");
        System.out.println(("before?" + (testException.getMessage())));
        String json = JSONObject.toJSONString(testException);
        System.out.println(json);
        Issue1344.TestException o = JSONObject.parseObject(json, Issue1344.TestException.class);
        System.out.println(("after?" + (o.getMessage())));
    }

    public static class TestException extends Exception {
        @JSONCreator
        public TestException() {
        }

        public TestException(String data) {
            super(("Data : " + data));
        }
    }
}

