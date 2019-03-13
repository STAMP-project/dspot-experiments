package com.alibaba.json.bvt.serializer.filters;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.AfterFilter;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class AfterFilterClassLevelTest_private extends TestCase {
    public void test_0() throws Exception {
        Object[] array = new Object[]{ new AfterFilterClassLevelTest_private.ModelA(), new AfterFilterClassLevelTest_private.ModelB() };
        SerializeConfig config = new SerializeConfig();
        // 
        config.addFilter(AfterFilterClassLevelTest_private.ModelA.class, new AfterFilter() {
            @Override
            public void writeAfter(Object object) {
                writeKeyValue("type", "A");
            }
        });
        // 
        config.addFilter(AfterFilterClassLevelTest_private.ModelB.class, new AfterFilter() {
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

    private static class ModelA {
        public int id = 1001;
    }

    private static class ModelB {
        public int id = 1002;
    }
}

