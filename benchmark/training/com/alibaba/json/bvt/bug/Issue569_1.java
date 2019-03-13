package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Issue569_1 extends TestCase {
    public void test_for_issue() throws Exception {
        String json = "{\"result\":{}}";
        Issue569_1.InterfaceResult<Issue569_1.Value> result = JSON.parseObject(json, new com.alibaba.fastjson.TypeReference<Issue569_1.InterfaceResult<Issue569_1.Value>>() {});
        TestCase.assertNotNull(result.getResult());
        TestCase.assertEquals(Issue569_1.Value.class, result.getResult().getClass());
    }

    public static class BaseInterfaceResult {}

    public static class InterfaceResult<T> extends Issue569_1.BaseInterfaceResult {
        public T getResult() {
            return result;
        }

        public void setResult(T result) {
            this.result = result;
        }

        private T result;
    }

    public static class Value {}
}

