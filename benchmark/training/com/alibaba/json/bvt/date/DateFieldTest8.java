package com.alibaba.json.bvt.date;


import JSON.defaultTimeZone;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateFieldTest8 extends TestCase {
    public void test_0() throws Exception {
        DateFieldTest8.Entity object = new DateFieldTest8.Entity();
        object.setValue(new Date());
        String text = JSON.toJSONStringWithDateFormat(object, "yyyy");
        SimpleDateFormat format = new SimpleDateFormat("yyyy", JSON.defaultLocale);
        format.setTimeZone(defaultTimeZone);
        Assert.assertEquals((("{\"value\":\"" + (format.format(object.getValue()))) + "\"}"), text);
    }

    public void test_1() throws Exception {
        DateFieldTest8.Entity object = new DateFieldTest8.Entity();
        object.setValue(new Date());
        String text = JSON.toJSONString(object);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", JSON.defaultLocale);
        format.setTimeZone(defaultTimeZone);
        Assert.assertEquals((("{\"value\":\"" + (format.format(object.getValue()))) + "\"}"), text);
    }

    public static class Entity {
        @JSONField(format = "yyyy-MM-dd")
        private Date value;

        public Date getValue() {
            return value;
        }

        public void setValue(Date value) {
            this.value = value;
        }
    }
}

