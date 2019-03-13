package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSONPath;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONPath_keySet extends TestCase {
    public static final Set<String> KEY_SET = new HashSet<String>();

    static {
        JSONPath_keySet.KEY_SET.add("id");
        JSONPath_keySet.KEY_SET.add("name");
    }

    public void test_nested() {
        JSONPath_keySet.Entity e = new JSONPath_keySet.Entity();
        e.id = 3L;
        e.name = "hello";
        Object obj = Collections.singletonMap("obj", e);
        Assert.assertEquals(JSONPath_keySet.KEY_SET, JSONPath.eval(obj, "$.obj.keySet()"));
        Assert.assertEquals(JSONPath_keySet.KEY_SET, new JSONPath("$.obj").keySet(obj));
    }

    public void test_unsupported() {
        JSONPath_keySet.Entity e = new JSONPath_keySet.Entity();
        e.id = 3L;
        JSONPath_keySet.Entity[] array = new JSONPath_keySet.Entity[]{ e };
        Map<String, JSONPath_keySet.Entity[]> map = Collections.singletonMap("array", array);
        Assert.assertEquals(array, JSONPath.eval(map, "$.array"));
        Assert.assertNull(JSONPath.eval(map, "$.array.keySet()"));
        Assert.assertNull(JSONPath.keySet(map, "$.array"));
        Assert.assertNull(new JSONPath("$.array").keySet(map));
    }

    public void test_null() {
        Assert.assertNull(JSONPath.eval(null, "$.keySet()"));
        Set<?> keySet = ((Set<?>) (JSONPath.eval(new HashMap<String, Object>(), "$.keySet()")));
        Assert.assertEquals(0, keySet.size());
    }

    public static class Entity {
        private Long id;

        private String name;

        public Long age;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

