package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSONPath;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONPath_paths_test2 extends TestCase {
    public void test_map() throws Exception {
        JSONPath_paths_test2.Model model = new JSONPath_paths_test2.Model();
        model.id = 1001;
        model.name = "wenshao";
        Map<String, Object> paths = JSONPath.paths(model);
        Assert.assertEquals(3, paths.size());
        Assert.assertSame(model, paths.get("/"));
        Assert.assertEquals(1001, paths.get("/id"));
        Assert.assertEquals("wenshao", paths.get("/name"));
    }

    public static class Model {
        public int id;

        public String name;
    }
}

