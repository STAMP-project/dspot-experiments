package com.kickstarter.libs.utils;


import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class MapUtilsTest extends TestCase {
    public void testCompact_withoutNullValues() {
        final Map<Object, Object> map = new HashMap<Object, Object>() {
            {
                put("a", 1);
                put("b", 2);
                put("c", 3);
            }
        };
        TestCase.assertEquals(MapUtils.compact(map), map);
    }

    public void testCompact_withNullValues() {
        final Map<Object, Object> map = new HashMap<Object, Object>() {
            {
                put("a", 1);
                put("b", null);
                put("c", 3);
            }
        };
        final Map<Object, Object> mapWithoutNull = new HashMap<Object, Object>() {
            {
                put("a", 1);
                put("c", 3);
            }
        };
        TestCase.assertEquals(MapUtils.compact(map), mapWithoutNull);
    }

    public void testPrefixKeys() {
        final Map<String, Object> map = new HashMap<String, Object>() {
            {
                put("a", 1);
                put("b", 2);
                put("c", 3);
            }
        };
        final Map<String, Object> mapWithPrefix = new HashMap<String, Object>() {
            {
                put("test_a", 1);
                put("test_b", 2);
                put("test_c", 3);
            }
        };
        TestCase.assertEquals(MapUtils.prefixKeys(map, "test_"), mapWithPrefix);
    }
}

