package com.alibaba.json.bvt.issue_1900;


import JSON.defaultTimeZone;
import com.alibaba.fastjson.JSON;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import junit.framework.TestCase;


public class Issue1955 extends TestCase {
    public void test_for_issue() throws Exception {
        String strVal = "0100-01-27 11:22:00.000";
        Date date = JSON.parseObject((('"' + strVal) + '"'), Date.class);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);
        df.setTimeZone(defaultTimeZone);
        TestCase.assertEquals(df.parse(strVal), date);
    }
}

