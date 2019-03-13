package com.alibaba.json.bvt.issue_1200;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 30/05/2017.
 */
public class Issue1229 extends TestCase {
    public void test_for_issue() throws Exception {
        final Object parsed = JSON.parse("{\"data\":{}}");
        TestCase.assertTrue((parsed instanceof JSONObject));
        TestCase.assertTrue(((get("data")) instanceof JSONObject));
        final Issue1229.Result<Issue1229.Data> result = JSON.parseObject("{\"data\":{}}", new com.alibaba.fastjson.TypeReference<Issue1229.Result<Issue1229.Data>>() {});
        TestCase.assertNotNull(result.data);
        TestCase.assertTrue(((result.data) instanceof Issue1229.Data));
        final Issue1229.Result<List<Issue1229.Data>> result2 = JSON.parseObject("{\"data\":[]}", new com.alibaba.fastjson.TypeReference<Issue1229.Result<List<Issue1229.Data>>>() {});
        TestCase.assertNotNull(result2.data);
        TestCase.assertTrue(((result2.data) instanceof List));
        TestCase.assertEquals(0, result2.data.size());
    }

    public static class Result<T> {
        T data;

        public void setData(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }

    public static class Data {}
}

