package com.alibaba.json.bvt.serializer.filters;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.ValueFilter;
import junit.framework.TestCase;
import org.junit.Assert;


public class ValueFilterTest_field_int extends TestCase {
    public void test_valuefilter() throws Exception {
        ValueFilter filter = new ValueFilter() {
            public Object process(Object source, String name, Object value) {
                if (name.equals("id")) {
                    return "AAA";
                }
                return value;
            }
        };
        ValueFilterTest_field_int.Bean a = new ValueFilterTest_field_int.Bean();
        String text = JSON.toJSONString(a, filter);
        Assert.assertEquals("{\"id\":\"AAA\"}", text);
    }

    public static class Bean {
        public int id;

        public String name;
    }
}

