package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSONPath;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONPath_object_filter extends TestCase {
    public void test_object_filter() throws Exception {
        JSONPath path = new JSONPath("[id=123]");
        JSONPath_object_filter.Entity entity = new JSONPath_object_filter.Entity(123, "ljw2083");
        Assert.assertSame(entity, path.eval(entity));
    }

    public void test_object_filter_not_match() throws Exception {
        JSONPath path = new JSONPath("[id=124]");
        JSONPath_object_filter.Entity entity = new JSONPath_object_filter.Entity(123, "ljw2083");
        Assert.assertNull(path.eval(entity));
    }

    public static class Entity {
        private Integer id;

        private String name;

        public Entity(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
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

