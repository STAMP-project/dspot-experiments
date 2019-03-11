package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSONPath;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONPath_field_access_filter_notNull extends TestCase {
    public void test_list_map() throws Exception {
        JSONPath path = new JSONPath("$[?(@.name)]");
        List<JSONPath_field_access_filter_notNull.Entity> entities = new ArrayList<JSONPath_field_access_filter_notNull.Entity>();
        entities.add(new JSONPath_field_access_filter_notNull.Entity(1001, "ljw2083"));
        entities.add(new JSONPath_field_access_filter_notNull.Entity(1002, "wenshao"));
        entities.add(new JSONPath_field_access_filter_notNull.Entity(1003, null));
        List<Object> result = ((List<Object>) (path.eval(entities)));
        Assert.assertEquals(2, result.size());
        Assert.assertSame(entities.get(0), result.get(0));
        Assert.assertSame(entities.get(1), result.get(1));
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

