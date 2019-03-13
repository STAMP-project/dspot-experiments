package com.alibaba.json.bvt.parser;


import JSON.defaultTimeZone;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.TypeUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class TypeUtilsTest_castToDate extends TestCase {
    public void test_castToDate() throws Exception {
        JSON.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
        Date date = TypeUtils.castToDate("2012-07-15 12:12:11");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(defaultTimeZone);
        Assert.assertEquals(format.parseObject("2012-07-15 12:12:11"), date);
        JSON.DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    }

    public void test_castToDate_error() throws Exception {
        Exception error = null;
        try {
            TypeUtils.castToDate("????-MM-dd");
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_castToDate_zero() throws Exception {
        Assert.assertEquals(new Date(0), TypeUtils.castToDate("0"));
    }

    public void test_castToDate_negative() throws Exception {
        Assert.assertEquals(new Date((-1)), TypeUtils.castToDate((-1)));
    }
}

