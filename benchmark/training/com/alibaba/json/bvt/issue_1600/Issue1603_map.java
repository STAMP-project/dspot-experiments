package com.alibaba.json.bvt.issue_1600;


import com.alibaba.fastjson.JSON;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class Issue1603_map extends TestCase {
    public void test_emptyMap() throws Exception {
        Issue1603_map.Model_1 m = JSON.parseObject("{\"values\":{\"a\":1001}}", Issue1603_map.Model_1.class);
        TestCase.assertEquals(0, m.values.size());
    }

    public void test_unmodifiableMap() throws Exception {
        Issue1603_map.Model_2 m = JSON.parseObject("{\"values\":{\"a\":1001}}", Issue1603_map.Model_2.class);
        TestCase.assertEquals(0, m.values.size());
    }

    public static class Model_1 {
        public final Map<String, Object> values = Collections.emptyMap();
    }

    public static class Model_2 {
        public final Map<String, Object> values = Collections.unmodifiableMap(new HashMap<String, Object>());
    }
}

