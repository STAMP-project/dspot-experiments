package com.alibaba.json.bvt.date;


import JSON.defaultTimeZone;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import junit.framework.TestCase;


public class DateFieldFormatTest extends TestCase {
    public void test_format_() throws Exception {
        Date now = new Date();
        DateFieldFormatTest.Model model = new DateFieldFormatTest.Model();
        model.serverTime = now;
        model.publishTime = now;
        model.setStartDate(now);
        String text = JSON.toJSONString(model);
        System.out.println(text);
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA);
        SimpleDateFormat df3 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        df1.setTimeZone(defaultTimeZone);
        df2.setTimeZone(defaultTimeZone);
        df3.setTimeZone(defaultTimeZone);
        String t1 = df1.format(now);
        String t2 = df2.format(now);
        String t3 = df3.format(now);
        TestCase.assertEquals((((((("{\"publishTime\":\"" + t2) + "\",\"serverTime\":\"") + t1) + "\",\"startDate\":\"") + t3) + "\"}"), text);
        DateFieldFormatTest.Model model2 = JSON.parseObject(text, DateFieldFormatTest.Model.class);
        SimpleDateFormat df4 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA);
        SimpleDateFormat df5 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        SimpleDateFormat df6 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        df4.setTimeZone(defaultTimeZone);
        df5.setTimeZone(defaultTimeZone);
        df6.setTimeZone(defaultTimeZone);
        TestCase.assertEquals(t2, df4.format(model2.publishTime));
        TestCase.assertEquals(t1, df5.format(model2.serverTime));
        TestCase.assertEquals(t3, df6.format(model2.getStartDate()));
    }

    public static class Model {
        @JSONField(format = "yyyy-MM-dd HH:mm:ss")
        public Date serverTime;

        @JSONField(format = "yyyy/MM/dd HH:mm:ss")
        public Date publishTime;

        @JSONField(format = "yyyy-MM-dd")
        private Date startDate;

        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }
    }
}

