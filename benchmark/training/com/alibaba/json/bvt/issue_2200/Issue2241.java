package com.alibaba.json.bvt.issue_2200;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import junit.framework.TestCase;


public class Issue2241 extends TestCase {
    public void test_for_issue() throws Exception {
        String text = "{\"createTime\":1548166745}";
        Issue2241.Order o = JSON.parseObject(text, Issue2241.Order.class);
        TestCase.assertEquals(1548166745000L, o.createTime.getTime());
        String json = JSON.toJSONString(o);
        TestCase.assertEquals("{\"createTime\":1548166745}", json);
    }

    public void test_for_issue2() throws Exception {
        String text = "{\"createTime\":1548166745}";
        Issue2241.Order2 o = JSON.parseObject(text, Issue2241.Order2.class);
        TestCase.assertEquals(1548166745000L, o.createTime.getTimeInMillis());
        String json = JSON.toJSONString(o);
        TestCase.assertEquals("{\"createTime\":1548166745}", json);
    }

    public void test_for_issue3() throws Exception {
        String text = "{\"createTime\":\"20180714224948\"}";
        Issue2241.Order3 o = JSON.parseObject(text, Issue2241.Order3.class);
        TestCase.assertEquals(1531579788000L, o.createTime.getTimeInMillis());
        String json = JSON.toJSONString(o);
        TestCase.assertEquals("{\"createTime\":\"20180714224948\"}", json);
    }

    public void test_for_issue4() throws Exception {
        String text = "{\"createTime\":1548166745}";
        Issue2241.Order4 o = JSON.parseObject(text, Issue2241.Order4.class);
        TestCase.assertEquals(1548166745L, o.createTime.toEpochSecond());
        String json = JSON.toJSONString(o);
        TestCase.assertEquals("{\"createTime\":1548166745}", json);
    }

    public static class Order {
        @JSONField(format = "unixtime")
        public Date createTime;
    }

    public static class Order2 {
        @JSONField(format = "unixtime")
        public Calendar createTime;
    }

    public static class Order3 {
        @JSONField(format = "yyyyMMddHHmmss")
        public Calendar createTime;
    }

    public static class Order4 {
        @JSONField(format = "unixtime")
        public ZonedDateTime createTime;
    }
}

