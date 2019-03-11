package com.alibaba.json.bvt.issue_1900;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Issue1944 extends TestCase {
    public void test_for_issue() throws Exception {
        TestCase.assertEquals(90.82195F, JSON.parseObject("{\"value\":90.82195113}", Issue1944.Model.class).value);
    }

    public static class Model {
        public float value;
    }
}

