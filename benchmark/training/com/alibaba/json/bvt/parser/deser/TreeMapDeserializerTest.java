package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import java.util.TreeMap;
import junit.framework.TestCase;
import org.junit.Assert;


public class TreeMapDeserializerTest extends TestCase {
    public void test_0() throws Exception {
        TreeMap treeMap = JSON.parseObject("{}", TreeMap.class);
        Assert.assertEquals(0, treeMap.size());
    }
}

