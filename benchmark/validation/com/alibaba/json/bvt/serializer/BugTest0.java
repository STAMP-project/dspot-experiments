package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import java.sql.Date;
import java.sql.Timestamp;
import junit.framework.TestCase;
import org.junit.Assert;


public class BugTest0 extends TestCase {
    public void test_0() throws Exception {
        Timestamp t = new Timestamp(System.currentTimeMillis());
        String text = JSON.toJSONString(t);
        Timestamp t1 = JSON.parseObject(text, Timestamp.class);
        Assert.assertEquals(t, t1);
    }

    public void test_1() throws Exception {
        long t1 = System.currentTimeMillis();
        String text = JSON.toJSONString(t1);
        Timestamp t2 = JSON.parseObject(text, Timestamp.class);
        Assert.assertEquals(t1, t2.getTime());
    }

    public void test_2() throws Exception {
        Date t = new Date(System.currentTimeMillis());
        String text = JSON.toJSONString(t);
        Date t1 = JSON.parseObject(text, Date.class);
        Assert.assertEquals(t, t1);
    }

    public void test_3() throws Exception {
        long t1 = System.currentTimeMillis();
        String text = JSON.toJSONString(t1);
        Date t2 = JSON.parseObject(text, Date.class);
        Assert.assertEquals(t1, t2.getTime());
    }

    public void test_4() throws Exception {
        BugTest0.A a = new BugTest0.A();
        a.setDate(new Date(System.currentTimeMillis()));
        a.setTime(new Timestamp(System.currentTimeMillis()));
        String text = JSON.toJSONString(a);
        BugTest0.A a1 = JSON.parseObject(text, BugTest0.A.class);
        Assert.assertEquals(a.getDate(), a1.getDate());
        Assert.assertEquals(a.getTime(), a1.getTime());
    }

    public void test_error_0() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("\"222A\"", Timestamp.class);
        } catch (JSONException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_1() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("\"222B\"", Date.class);
        } catch (JSONException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_3() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("true", Timestamp.class);
        } catch (JSONException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_4() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("true", Date.class);
        } catch (JSONException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public static class A {
        private Timestamp time;

        private Date date;

        public Timestamp getTime() {
            return time;
        }

        public void setTime(Timestamp time) {
            this.time = time;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }
}

