package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParserConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import junit.framework.TestCase;
import org.junit.Assert;


@SuppressWarnings("deprecation")
public class DefaultObjectDeserializerTest2 extends TestCase {
    public void test_1() throws Exception {
        String input = "{'map':{}}";
        DefaultJSONParser parser = new DefaultJSONParser(input, ParserConfig.getGlobalInstance(), JSON.DEFAULT_PARSER_FEATURE);
        SortedMap<String, SortedMap> map = JSON.parseObject(input, getType());
        Assert.assertEquals(TreeMap.class, map.get("map").getClass());
    }

    public void test_8() throws Exception {
        String input = "{'map':{}}";
        ConcurrentMap<String, HashMap> map = JSON.parseObject(input, getType());
        Assert.assertEquals(HashMap.class, map.get("map").getClass());
    }

    public static interface Map1<K, V> extends Map<K, V> {}

    public static class Map2<K, V> extends HashMap<K, V> {}
}

