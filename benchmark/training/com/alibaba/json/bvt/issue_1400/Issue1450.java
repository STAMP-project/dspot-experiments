package com.alibaba.json.bvt.issue_1400;


import com.alibaba.fastjson.JSON;
import java.time.LocalDateTime;
import junit.framework.TestCase;


public class Issue1450 extends TestCase {
    public void test_for_issue() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2018, 8, 31, 15, 26, 37, 1);
        String json = JSON.toJSONStringWithDateFormat(localDateTime, "yyyy-MM-dd HH:mm:ss");// 2018-08-31T15:26:37.000000001

        TestCase.assertEquals("\"2018-08-31 15:26:37\"", json);
    }
}

