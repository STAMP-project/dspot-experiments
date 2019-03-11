package com.netflix.conductor.core.utils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class TestJsonUtils {
    private JsonUtils jsonUtils;

    @Test
    public void testArray() {
        List<Object> list = new LinkedList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("externalId", "[{\"taskRefName\":\"t001\",\"workflowId\":\"w002\"}]");
        map.put("name", "conductor");
        map.put("version", 2);
        list.add(map);
        // noinspection unchecked
        map = ((Map<String, Object>) (list.get(0)));
        Assert.assertTrue(((map.get("externalId")) instanceof String));
        int before = list.size();
        jsonUtils.expand(list);
        Assert.assertEquals(before, list.size());
        // noinspection unchecked
        map = ((Map<String, Object>) (list.get(0)));
        Assert.assertTrue(((map.get("externalId")) instanceof ArrayList));
    }

    @Test
    public void testMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("externalId", "{\"taskRefName\":\"t001\",\"workflowId\":\"w002\"}");
        map.put("name", "conductor");
        map.put("version", 2);
        Assert.assertTrue(((map.get("externalId")) instanceof String));
        jsonUtils.expand(map);
        Assert.assertTrue(((map.get("externalId")) instanceof LinkedHashMap));
    }
}

