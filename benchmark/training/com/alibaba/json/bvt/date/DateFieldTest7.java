package com.alibaba.json.bvt.date;


import JSON.defaultTimeZone;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateFieldTest7 extends TestCase {
    public void test_0() throws Exception {
        SerializeConfig config = new SerializeConfig();
        config.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd"));
        config.setAsmEnable(false);
        DateFieldTest7.Entity object = new DateFieldTest7.Entity();
        object.setValue(new Date());
        String text = JSON.toJSONString(object, config);
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

