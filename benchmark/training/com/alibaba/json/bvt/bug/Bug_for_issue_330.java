package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_330 extends TestCase {
    public void test_for_issue() throws Exception {
        String jsonContent = "{\"data\":{\"content\":\"xxx\",\"hour\":1}}";
        Bug_for_issue_330.StatusBean<Bug_for_issue_330.WorkBean> bean = JSONObject.parseObject(jsonContent, new com.alibaba.fastjson.TypeReference<Bug_for_issue_330.StatusBean<Bug_for_issue_330.WorkBean>>() {});
        Assert.assertNotNull(bean.getData());
        Assert.assertEquals(1, bean.getData().getHour());
        Assert.assertEquals("xxx", bean.getData().getContent());
    }

    public static class StatusBean<T> {
        private int code;

        private String msg;

        private T data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }

    public static class WorkBean {
        private int hour;

        private String content;

        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}

