package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSONPath;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONPath_list_field extends TestCase {
    public void test_list_field() throws Exception {
        JSONPath path = new JSONPath("$.name");
        List<JSONPath_list_field.Entity> entities = new ArrayList<JSONPath_list_field.Entity>();
        entities.add(new JSONPath_list_field.Entity("wenshao"));
        entities.add(new JSONPath_list_field.Entity("ljw2083"));
        List<String> names = ((List<String>) (path.eval(entities)));
        Assert.assertSame(entities.get(0).getName(), names.get(0));
        Assert.assertSame(entities.get(1).getName(), names.get(1));
    }

    public void test_list_field_simple() throws Exception {
        JSONPath path = new JSONPath("name");
        List<JSONPath_list_field.Entity> entities = new ArrayList<JSONPath_list_field.Entity>();
        entities.add(new JSONPath_list_field.Entity("wenshao"));
        entities.add(new JSONPath_list_field.Entity("ljw2083"));
        List<String> names = ((List<String>) (path.eval(entities)));
        Assert.assertSame(entities.get(0).getName(), names.get(0));
        Assert.assertSame(entities.get(1).getName(), names.get(1));
    }

    public static class Entity {
        private String name;

        public Entity(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

