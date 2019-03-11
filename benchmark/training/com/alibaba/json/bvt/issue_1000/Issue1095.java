package com.alibaba.json.bvt.issue_1000;


import Feature.AllowISO8601DateFormat;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.Date;
import junit.framework.TestCase;


/**
 * Created by wenshao on 22/03/2017.
 */
public class Issue1095 extends TestCase {
    public void test_for_issue() throws Exception {
        String text = "{\"Grade\": 1, \"UpdateTime\": \"2017-03-22T11:41:17\"}";
        JSONObject jsonObject = JSON.parseObject(text, AllowISO8601DateFormat);
        TestCase.assertEquals(Date.class, jsonObject.get("UpdateTime").getClass());
    }
}

