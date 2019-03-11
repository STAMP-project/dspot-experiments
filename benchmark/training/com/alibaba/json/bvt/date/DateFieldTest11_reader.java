package com.alibaba.json.bvt.date;


import JSON.defaultLocale;
import JSON.defaultTimeZone;
import com.alibaba.fastjson.JSONReader;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Date;
import junit.framework.TestCase;


/**
 * Created by wenshao on 07/04/2017.
 */
public class DateFieldTest11_reader extends TestCase {
    public void test_cn() throws Exception {
        DateFieldTest11_reader.Model vo = new JSONReader(new StringReader("{\"date0\":\"2016-05-06\",\"date1\":\"2017-03-01\"}")).readObject(DateFieldTest11_reader.Model.class);
        Calendar calendar = Calendar.getInstance(defaultTimeZone, defaultLocale);
        calendar.setTime(vo.date0);
        TestCase.assertEquals(2016, calendar.get(Calendar.YEAR));
        TestCase.assertEquals(4, calendar.get(Calendar.MONTH));
        TestCase.assertEquals(6, calendar.get(Calendar.DAY_OF_MONTH));
        TestCase.assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        TestCase.assertEquals(0, calendar.get(Calendar.MINUTE));
        TestCase.assertEquals(0, calendar.get(Calendar.SECOND));
        TestCase.assertEquals(0, calendar.get(Calendar.MILLISECOND));
        calendar.setTime(vo.date1);
        TestCase.assertEquals(2017, calendar.get(Calendar.YEAR));
        TestCase.assertEquals(2, calendar.get(Calendar.MONTH));
        TestCase.assertEquals(1, calendar.get(Calendar.DAY_OF_MONTH));
        TestCase.assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        TestCase.assertEquals(0, calendar.get(Calendar.MINUTE));
        TestCase.assertEquals(0, calendar.get(Calendar.SECOND));
        TestCase.assertEquals(0, calendar.get(Calendar.MILLISECOND));
    }

    public void test_cn_1() throws Exception {
        DateFieldTest11_reader.Model vo = new JSONReader(new StringReader("{\"date0\":1462464000000,\"date1\":1488297600000}")).readObject(DateFieldTest11_reader.Model.class);
        Calendar calendar = Calendar.getInstance(defaultTimeZone, defaultLocale);
        calendar.setTime(vo.date0);
        TestCase.assertEquals(2016, calendar.get(Calendar.YEAR));
        TestCase.assertEquals(4, calendar.get(Calendar.MONTH));
        TestCase.assertEquals(6, calendar.get(Calendar.DAY_OF_MONTH));
        TestCase.assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        TestCase.assertEquals(0, calendar.get(Calendar.MINUTE));
        TestCase.assertEquals(0, calendar.get(Calendar.SECOND));
        TestCase.assertEquals(0, calendar.get(Calendar.MILLISECOND));
        calendar.setTime(vo.date1);
        TestCase.assertEquals(2017, calendar.get(Calendar.YEAR));
        TestCase.assertEquals(2, calendar.get(Calendar.MONTH));
        TestCase.assertEquals(1, calendar.get(Calendar.DAY_OF_MONTH));
        TestCase.assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
        TestCase.assertEquals(0, calendar.get(Calendar.MINUTE));
        TestCase.assertEquals(0, calendar.get(Calendar.SECOND));
        TestCase.assertEquals(0, calendar.get(Calendar.MILLISECOND));
        System.out.println(vo.date0.getTime());
        System.out.println(vo.date1.getTime());
    }

    public static class Model {
        public Date date0;

        public Date date1;
    }
}

