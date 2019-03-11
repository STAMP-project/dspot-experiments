package com.alibaba.json.bvt.bug;


import SerializerFeature.BeanToArray;
import SerializerFeature.NotWriteRootClassName;
import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;


public class Issue243 extends TestCase {
    public void testSerialize() {
        Issue243.RpcResponse response = new Issue243.RpcResponse(2, new Object());
        // String json = JSON.toJSONString(response, SerializerFeature.WriteClassName); // codeA with WriteClassName,
        // requestId is not ending with 'L'
        String json = response.toCommandJson();// codeA with WriteClassName, requestId is ending with 'L', and trouble

        // other json framework
        System.out.println(json);
        String json2 = JSON.toJSONString(response, BeanToArray, WriteClassName, NotWriteRootClassName);
        System.out.println(json2);
    }

    public static class RpcResponse {
        private int msgType = 50;

        private long requestId = 0;

        private JSONObject details = new JSONObject();

        private Object[] yieldResult = new Object[1];

        public RpcResponse() {
        }

        public RpcResponse(long requestId, Object result) {
            this.requestId = requestId;
            yieldResult[0] = result;
        }

        public int getMsgType() {
            return msgType;
        }

        public void setMsgType(int msgType) {
            this.msgType = msgType;
        }

        public long getRequestId() {
            return requestId;
        }

        public void setRequestId(long requestId) {
            this.requestId = requestId;
        }

        public JSONObject getDetails() {
            return details;
        }

        public void setDetails(JSONObject details) {
            this.details = details;
        }

        public Object[] getYieldResult() {
            return yieldResult;
        }

        public void setYieldResult(String[] yieldResult) {
            this.yieldResult = yieldResult;
        }

        protected Object[] fieldToArray() {
            return new Object[]{ msgType, requestId, details, yieldResult };
        }

        public String toCommandJson() {
            return JSON.toJSONString(fieldToArray(), WriteClassName);
        }
    }
}

