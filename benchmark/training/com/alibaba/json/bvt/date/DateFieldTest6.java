package com.alibaba.json.bvt.date;


import JSON.defaultTimeZone;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateFieldTest6 extends TestCase {
    public void test_0() throws Exception {
        SerializeConfig mapping = new SerializeConfig();
        mapping.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd"));
        DateFieldTest6.Entity object = new DateFieldTest6.Entity();
        object.setValue(new Date());
        String text = JSON.toJSONString(object, mapping);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", JSON.defaultLocale);
        format.setTimeZone(defaultTimeZone);
        Assert.assertEquals((("{\"value\":\"" + (format.format(object.getValue()))) + "\"}"), text);
    }

    public static class Entity {
        private Date value;

        public Date getValue() {
            return value;
        }

        public void setValue(Date value) {
            this.value = value;
        }
    }
}

