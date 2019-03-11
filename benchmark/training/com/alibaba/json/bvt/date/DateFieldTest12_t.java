package com.alibaba.json.bvt.date;


import JSON.defaultTimeZone;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateFieldTest12_t extends TestCase {
    public void test_1() throws Exception {
        DateFieldTest12_t.Entity object = new DateFieldTest12_t.Entity();
        object.setValue(new Date());
        String text = JSON.toJSONString(object);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", JSON.defaultLocale);
        format.setTimeZone(defaultTimeZone);
        Assert.assertEquals((("{\"value\":\"" + (format.format(object.getValue()))) + "\"}"), text);
        DateFieldTest12_t.Entity object2 = JSON.parseObject(text, DateFieldTest12_t.Entity.class);
    }

    public static class Entity {
        @JSONField(format = "yyyy-MM-ddTHH:mm:ssZ")
        private Date value;

        public Date getValue() {
            return value;
        }

        public void setValue(Date value) {
            this.value = value;
        }
    }
}

