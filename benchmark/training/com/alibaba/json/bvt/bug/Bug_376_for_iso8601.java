/**
 * Copyright 2015 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.json.bvt.bug;


import Feature.AllowISO8601DateFormat;
import com.alibaba.fastjson.JSON;
import java.text.DateFormat;
import java.util.Date;
import junit.framework.TestCase;


public class Bug_376_for_iso8601 extends TestCase {
    public void test_fix() {
        String s = "{date: \"2015-07-22T19:13:42Z\"}";
        String s2 = "{date: \"2015-07-22T19:13:42.000Z\"}";
        Bug_376_for_iso8601.MyObj o = JSON.parseObject(s, Bug_376_for_iso8601.MyObj.class, AllowISO8601DateFormat);
        Bug_376_for_iso8601.MyObj o2 = JSON.parseObject(s2, Bug_376_for_iso8601.MyObj.class, AllowISO8601DateFormat);
        System.out.println(DateFormat.getDateTimeInstance().format(o.getDate()));
        System.out.println(DateFormat.getDateTimeInstance().format(o2.getDate()));
        // ??????
        // 2015-7-22 19:13:42
        // 2015-7-23 3:13:42
        // ??????
        // 2015-7-23 3:13:42
        // 2015-7-23 3:13:42
    }

    static class MyObj {
        private Date date;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }
}

