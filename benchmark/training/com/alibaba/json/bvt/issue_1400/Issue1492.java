package com.alibaba.json.bvt.issue_1400;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.io.Serializable;
import junit.framework.TestCase;


public class Issue1492 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1492.DubboResponse resp = new Issue1492.DubboResponse();
        // test for JSONObject
        JSONObject obj = new JSONObject();
        obj.put("key1", "value1");
        obj.put("key2", "value2");
        resp.setData(obj);
        String str = JSON.toJSONString(resp);
        System.out.println(str);
        Issue1492.DubboResponse resp1 = JSON.parseObject(str, Issue1492.DubboResponse.class);
        TestCase.assertEquals(str, JSON.toJSONString(resp1));
        // test for JSONArray
        JSONArray arr = new JSONArray();
        arr.add("key1");
        arr.add("key2");
        resp.setData(arr);
        String str2 = JSON.toJSONString(resp);
        System.out.println(str2);
        Issue1492.DubboResponse resp2 = JSON.parseObject(str2, Issue1492.DubboResponse.class);
        TestCase.assertEquals(str2, JSON.toJSONString(resp2));
    }

    public static final class DubboResponse implements Serializable {
        private String message;

        private String error;

        private JSON data;

        private boolean success;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public JSON getData() {
            return data;
        }

        public void setData(JSON data) {
            this.data = data;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }
}

