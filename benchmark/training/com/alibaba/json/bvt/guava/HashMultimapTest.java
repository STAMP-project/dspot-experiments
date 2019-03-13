package com.alibaba.json.bvt.guava;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.HashMultimap;
import junit.framework.TestCase;


/**
 * Created by wenshao on 15/01/2017.
 */
public class HashMultimapTest extends TestCase {
    public void test_for_multimap() throws Exception {
        HashMultimap map = HashMultimap.create();
        map.put("name", "a");
        map.put("name", "b");
        String json = JSON.toJSONString(map);
        TestCase.assertEquals("{\"name\":[\"a\",\"b\"]}", json);
    }
}

