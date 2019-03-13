package com.alibaba.json.bvt.issue_2000;


import JSON.defaultTimeZone;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.TestCase;


public class Issue2088 extends TestCase {
    public void test_for_issue() throws Exception {
        String json = "{\"date\":\"20181011103607186+0800\"}";
        Issue2088.Model m = JSON.parseObject(json, Issue2088.Model.class);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSSZ");
        format.setTimeZone(defaultTimeZone);
        Date date = format.parse("20181011103607186+0800");
        TestCase.assertEquals(date, m.date);
    }

    public void test_for_issue_1() throws Exception {
        String json = "{\"date\":\"20181011103607186-0800\"}";
        Issue2088.Model m = JSON.parseObject(json, Issue2088.Model.class);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSSZ", JSON.defaultLocale);
        format.setTimeZone(defaultTimeZone);
        Date date = format.parse("20181011103607186-0800");
        TestCase.assertEquals(date, m.date);
    }

    public static class Model {
        @JSONField(format = "yyyyMMddHHmmssSSSZ")
        public Date date;
    }
}

