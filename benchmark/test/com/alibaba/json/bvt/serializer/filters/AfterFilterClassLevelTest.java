package com.alibaba.json.bvt.serializer.filters;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.AfterFilter;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class AfterFilterClassLevelTest extends TestCase {
    public void test_0() throws Exception {
        Object[] array = new Object[]{ new AfterFilterClassLevelTest.ModelA(), new AfterFilterClassLevelTest.ModelB() };
        SerializeConfig config = new SerializeConfig();
        // 
        config.addFilter(AfterFilterClassLevelTest.ModelA.class, new AfterFilter() {
            @Override
            public void writeAfter(Object object) {
                writeKeyValue("type", "A");
            }
        });
        // 
        config.addFilter(AfterFilterClassLevelTest.ModelB.class, new AfterFilter() {
            @Override
            public void writeAfter(Object object) {
                writeKeyValue("type", "B");
            }
        });
        String text2 = JSON.toJSONString(array, config);
        Assert.assertEquals("[{\"id\":1001,\"type\":\"A\"},{\"id\":1002,\"type\":\"B\"}]", text2);
        String text = JSON.toJSONString(array);
        Assert.assertEquals("[{\"id\":1001},{\"id\":1002}]", text);
    }

    public static class ModelA {
        public int id = 1001;
    }

    public static class ModelB {
        public int id = 1002;
    }
}

