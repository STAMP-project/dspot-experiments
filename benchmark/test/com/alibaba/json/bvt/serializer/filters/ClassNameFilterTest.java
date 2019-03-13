package com.alibaba.json.bvt.serializer.filters;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.NameFilter;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class ClassNameFilterTest extends TestCase {
    public void test_filter() throws Exception {
        NameFilter upcaseNameFilter = new NameFilter() {
            @Override
            public String process(Object object, String name, Object value) {
                return name.toUpperCase();
            }
        };
        // 
        SerializeConfig.getGlobalInstance().addFilter(ClassNameFilterTest.A.class, upcaseNameFilter);
        Assert.assertEquals("{\"ID\":0}", JSON.toJSONString(new ClassNameFilterTest.A()));
        Assert.assertEquals("{\"id\":0}", JSON.toJSONString(new ClassNameFilterTest.B()));
    }

    public static class A {
        public int id;
    }

    public static class B {
        public int id;
    }
}

