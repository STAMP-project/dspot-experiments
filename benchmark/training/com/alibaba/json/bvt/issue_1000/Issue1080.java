package com.alibaba.json.bvt.issue_1000;


import com.alibaba.fastjson.JSON;
import java.util.Date;
import junit.framework.TestCase;


/**
 * Created by wenshao on 17/03/2017.
 */
public class Issue1080 extends TestCase {
    public void test_for_issue() throws Exception {
        Date date = JSON.parseObject("\"2017-3-17 00:00:01\"", Date.class);
        String json = JSON.toJSONStringWithDateFormat(date, "yyyy-MM-dd");
        TestCase.assertEquals("\"2017-03-17\"", json);
    }

    public void test_for_issue_2() throws Exception {
        Date date = JSON.parseObject("\"2017-3-7 00:00:01\"", Date.class);
        String json = JSON.toJSONStringWithDateFormat(date, "yyyy-MM-dd");
        TestCase.assertEquals("\"2017-03-07\"", json);
    }
}

