package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.Collections;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class CollectionEmptyMapTest extends TestCase {
    public void test_0() throws Exception {
        Map<String, Object> map = Collections.emptyMap();
        String text = JSON.toJSONString(map, WriteClassName);
        Assert.assertEquals("{\"@type\":\"java.util.Collections$EmptyMap\"}", text);
        Assert.assertSame(map, JSON.parse(text));
    }
}

