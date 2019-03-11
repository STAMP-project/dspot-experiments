package com.alibaba.json.bvt.parser.array;


import Feature.SupportArrayToBean;
import JSON.defaultTimeZone;
import com.alibaba.fastjson.JSON;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class BeanToArrayTest_date extends TestCase {
    public void test_date() throws Exception {
        long time = System.currentTimeMillis();
        BeanToArrayTest_date.Model model = JSON.parseObject((((("[" + time) + ",") + time) + "]"), BeanToArrayTest_date.Model.class, SupportArrayToBean);
        Assert.assertEquals(time, model.v1.getTime());
        Assert.assertEquals(time, model.v2.getTime());
    }

    public void test_date_reader() throws Exception {
        long time = System.currentTimeMillis();
        BeanToArrayTest_date.Model model = readObject(BeanToArrayTest_date.Model.class);
        Assert.assertEquals(time, model.v1.getTime());
        Assert.assertEquals(time, model.v2.getTime());
    }

    public void test_date_null() throws Exception {
        BeanToArrayTest_date.Model model = JSON.parseObject("[null,null]", BeanToArrayTest_date.Model.class, SupportArrayToBean);
        Assert.assertNull(model.v1);
        Assert.assertNull(model.v2);
    }

    public void test_date_null_reader() throws Exception {
        BeanToArrayTest_date.Model model = readObject(BeanToArrayTest_date.Model.class);
        Assert.assertNull(model.v1);
        Assert.assertNull(model.v2);
    }

    public void test_date2() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", JSON.defaultLocale);
        dateFormat.setTimeZone(defaultTimeZone);
        BeanToArrayTest_date.Model model = JSON.parseObject("[\"2016-01-01\",\"2016-01-02\"]", BeanToArrayTest_date.Model.class, SupportArrayToBean);
        Assert.assertEquals(dateFormat.parse("2016-01-01").getTime(), model.v1.getTime());
        Assert.assertEquals(dateFormat.parse("2016-01-02").getTime(), model.v2.getTime());
    }

    public void test_date2_reader() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", JSON.defaultLocale);
        dateFormat.setTimeZone(defaultTimeZone);
        BeanToArrayTest_date.Model model = readObject(BeanToArrayTest_date.Model.class);
        Assert.assertEquals(dateFormat.parse("2016-01-01").getTime(), model.v1.getTime());
        Assert.assertEquals(dateFormat.parse("2016-01-02").getTime(), model.v2.getTime());
    }

    public static class Model {
        public Date v1;

        public Date v2;
    }
}

