package com.alibaba.json.bvt.issue_1900;


import SerializerFeature.UseISO8601DateFormat;
import com.alibaba.fastjson.JSON;
import java.sql.Date;
import junit.framework.TestCase;


public class Issue1977 extends TestCase {
    public void test_for_issue() throws Exception {
        Date date = new Date(1533265119604L);
        String json = JSON.toJSONString(date, UseISO8601DateFormat);
        TestCase.assertEquals("\"2018-08-03T10:58:39.604+08:00\"", json);
        // new java.sql.Date();
    }
}

