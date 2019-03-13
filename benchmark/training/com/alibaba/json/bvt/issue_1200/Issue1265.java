package com.alibaba.json.bvt.issue_1200;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


/**
 * Created by wenshao on 22/07/2017.
 */
public class Issue1265 extends TestCase {
    public void test_0() throws Exception {
        Object t = JSON.parseObject("{\"value\":{\"id\":123}}", new com.alibaba.fastjson.TypeReference<Issue1265.Response>() {}).value;
        TestCase.assertEquals(123, getIntValue("id"));
        Issue1265.T1 t1 = JSON.parseObject("{\"value\":{\"id\":123}}", new com.alibaba.fastjson.TypeReference<Issue1265.Response<Issue1265.T1>>() {}).value;
        TestCase.assertEquals(123, t1.id);
        Issue1265.T2 t2 = JSON.parseObject("{\"value\":{\"id\":123}}", new com.alibaba.fastjson.TypeReference<Issue1265.Response<Issue1265.T2>>() {}).value;
        TestCase.assertEquals(123, t2.id);
    }

    public static class Response<T> {
        public T value;
    }

    public static class T1 {
        public int id;
    }

    public static class T2 {
        public int id;
    }
}

