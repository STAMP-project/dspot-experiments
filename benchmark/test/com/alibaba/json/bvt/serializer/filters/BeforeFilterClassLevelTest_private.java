package com.alibaba.json.bvt.serializer.filters;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.BeforeFilter;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class BeforeFilterClassLevelTest_private extends TestCase {
    public void test_0() throws Exception {
        Object[] array = new Object[]{ new BeforeFilterClassLevelTest_private.ModelA(), new BeforeFilterClassLevelTest_private.ModelB() };
        SerializeConfig config = new SerializeConfig();
        // 
        config.addFilter(BeforeFilterClassLevelTest_private.ModelA.class, new BeforeFilter() {
            @Override
            public void writeBefore(Object object) {
                writeKeyValue("type", "A");
            }
        });
        // 
        config.addFilter(BeforeFilterClassLevelTest_private.ModelB.class, new BeforeFilter() {
            @Override
            public void writeBefore(Object object) {
                writeKeyValue("type", "B");
            }
        });
        String text2 = JSON.toJSONString(array, config);
        Assert.assertEquals("[{\"type\":\"A\",\"id\":1001},{\"type\":\"B\",\"id\":1002}]", text2);
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

