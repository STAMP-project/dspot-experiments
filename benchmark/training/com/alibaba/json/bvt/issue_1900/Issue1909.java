package com.alibaba.json.bvt.issue_1900;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.List;
import junit.framework.TestCase;


public class Issue1909 extends TestCase {
    public void test_for_issue() throws Exception {
        JSONArray params = new JSONArray();
        params.add("val1");
        params.add(2);
        Issue1909.ParamRequest pr = new Issue1909.ParamRequest("methodName", "stringID", params);
        System.out.println(JSON.toJSONString(pr));
        Issue1909.Request paramRequest = JSON.parseObject(JSON.toJSONString(pr), Issue1909.ParamRequest.class);
    }

    public static class ParamRequest extends Issue1909.Request {
        private String methodName;

        @JSONField(name = "id", ordinal = 3, serialize = true, deserialize = true)
        private Object id;

        private List<Object> params;

        public ParamRequest(String methodName, Object id, List<Object> params) {
            this.methodName = methodName;
            this.id = id;
            this.params = params;
        }

        public String getMethodName() {
            return methodName;
        }

        public Object getId() {
            return id;
        }

        public List<Object> getParams() {
            return params;
        }
    }

    public static class Request {}
}

