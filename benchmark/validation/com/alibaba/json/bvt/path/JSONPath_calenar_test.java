package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSONPath;
import java.util.Calendar;
import junit.framework.TestCase;


public class JSONPath_calenar_test extends TestCase {
    public void test_map() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, 6);
        calendar.set(Calendar.DAY_OF_MONTH, 30);
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 8);
        calendar.set(Calendar.SECOND, 43);
        TestCase.assertEquals(2017, JSONPath.eval(calendar, "/year"));
        TestCase.assertEquals(6, JSONPath.eval(calendar, "/month"));
        TestCase.assertEquals(30, JSONPath.eval(calendar, "/day"));
        TestCase.assertEquals(16, JSONPath.eval(calendar, "/hour"));
        TestCase.assertEquals(8, JSONPath.eval(calendar, "/minute"));
        TestCase.assertEquals(43, JSONPath.eval(calendar, "/second"));
    }
}

