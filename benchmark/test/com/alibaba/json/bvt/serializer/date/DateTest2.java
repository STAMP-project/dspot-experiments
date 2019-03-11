package com.alibaba.json.bvt.serializer.date;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.Date;
import junit.framework.TestCase;
import org.junit.Assert;


public class DateTest2 extends TestCase {
    public void test_null() throws Exception {
        long millis = System.currentTimeMillis();
        DateTest2.VO vo = new DateTest2.VO();
        vo.setValue(new Date(millis));
        Assert.assertEquals((("new Date(" + millis) + ")"), JSON.toJSONString(new Date(millis), WriteClassName));
    }

    public static class VO {
        private Object value;

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}

