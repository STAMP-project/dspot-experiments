package com.alibaba.json.bvt.issue_1200;


import SerializerFeature.UseISO8601DateFormat;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.Date;
import junit.framework.TestCase;


/**
 * Created by wenshao on 30/06/2017.
 */
public class Issue1298 extends TestCase {
    public void test_for_issue() throws Exception {
        JSONObject object = new JSONObject();
        object.put("date", "2017-06-29T08:06:30.000+05:30");
        Date date = object.getObject("date", Date.class);
        TestCase.assertEquals("\"2017-06-29T10:36:30+08:00\"", JSON.toJSONString(date, UseISO8601DateFormat));
    }

    public void test_for_issue_1() throws Exception {
        JSONObject object = new JSONObject();
        object.put("date", "2017-08-15 20:00:00.000");
        Date date = object.getObject("date", Date.class);
        TestCase.assertEquals("\"2017-08-15T20:00:00+08:00\"", JSON.toJSONString(date, UseISO8601DateFormat));
        JSON.parseObject("\"2017-08-15 20:00:00.000\"", Date.class);
    }
}

