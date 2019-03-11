package com.alibaba.json.bvt.issue_1600;


import com.alibaba.fastjson.JSON;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class Issue1603_map_getter extends TestCase {
    public void test_emptyMap() throws Exception {
        Issue1603_map_getter.Model_1 m = JSON.parseObject("{\"values\":{\"a\":1001}}", Issue1603_map_getter.Model_1.class);
        TestCase.assertEquals(0, m.values.size());
    }

    public void test_unmodifiableMap() throws Exception {
        Issue1603_map_getter.Model_2 m = JSON.parseObject("{\"values\":{\"a\":1001}}", Issue1603_map_getter.Model_2.class);
        TestCase.assertEquals(0, m.values.size());
    }

    public static class Model_1 {
        private final Map<String, Object> values = Collections.emptyMap();

        public Map<String, Object> getValues() {
            return values;
        }
    }

    public static class Model_2 {
        private final Map<String, Object> values = Collections.unmodifiableMap(new HashMap<String, Object>());

        public Map<String, Object> getValues() {
            return values;
        }
    }
}

