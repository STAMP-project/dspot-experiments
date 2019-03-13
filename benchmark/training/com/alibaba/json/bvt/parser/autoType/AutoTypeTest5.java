package com.alibaba.json.bvt.parser.autoType;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.ParserConfig;
import java.util.concurrent.ConcurrentMap;
import junit.framework.TestCase;


public class AutoTypeTest5 extends TestCase {
    ConcurrentMap<String, Class<?>> mappings;

    private static int count_x = 0;

    public void test_0() throws Exception {
        TestCase.assertFalse(ParserConfig.getGlobalInstance().isAutoTypeSupport());
        JSON.parseObject("{\"value\":{\"@type\":\"com.alibaba.json.bvt.parser.autoType.AutoTypeTest5$V1\"}}", AutoTypeTest5.Model.class);
        int size = mappings.size();
        Exception error = null;
        try {
            JSON.parseObject("{\"value\":{\"@type\":\"com.alibaba.json.bvt.parser.autoType.AutoTypeTest5$X1\"}}", AutoTypeTest5.Model.class);
        } catch (JSONException x) {
            error = x;
        }
        TestCase.assertNotNull(error);
        TestCase.assertEquals(0, AutoTypeTest5.count_x);
        TestCase.assertEquals(size, mappings.size());
    }

    public static class Model {
        public AutoTypeTest5.V0 value;
    }

    public static class V0 {}

    public static class V1 extends AutoTypeTest5.V0 {}

    public static class X1 {
        static {
            (AutoTypeTest5.count_x)++;
        }
    }
}

