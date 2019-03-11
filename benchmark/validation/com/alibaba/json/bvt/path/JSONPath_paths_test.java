package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSONPath;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONPath_paths_test extends TestCase {
    public void test_map() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", 1001);
        map.put("name", "wenshao");
        Map<String, Object> paths = JSONPath.paths(map);
        Assert.assertEquals(3, paths.size());
        Assert.assertSame(map, paths.get("/"));
        Assert.assertEquals(1001, paths.get("/id"));
        Assert.assertEquals("wenshao", paths.get("/name"));
    }
}

