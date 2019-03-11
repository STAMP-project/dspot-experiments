package com.alibaba.json.bvt.guava;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import java.util.Map;
import junit.framework.TestCase;


/**
 * Created by wenshao on 15/01/2017.
 */
public class MultiMapTes extends TestCase {
    public void test_multimap() throws Exception {
        Map<String, Integer> map = ImmutableMap.of("a", 1, "b", 1, "c", 2);
        SetMultimap<String, Integer> multimap = Multimaps.forMap(map);
        Multimap<Integer, String> inverse = Multimaps.invertFrom(multimap, HashMultimap.<Integer, String>create());
        String json = JSON.toJSONString(inverse);
        TestCase.assertEquals("{1:[\"a\",\"b\"],2:[\"c\"]}", json);
    }
}

