package com.alibaba.json.bvt.issue_1700;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.Date;
import junit.framework.TestCase;


public class Issue1727 extends TestCase {
    public void test_for_issue() throws Exception {
        String jsonString = "{\"gmtCreate\":\"20180131214157805-0800\"}";
        JSONObject.parseObject(jsonString, Issue1727.Model.class);// ????

        JSONObject.toJavaObject(JSON.parseObject(jsonString), Issue1727.Model.class);
    }

    public static class Model {
        @JSONField(format = "yyyyMMddHHmmssSSSZ")
        public Date gmtCreate;
    }
}

