package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSONPath;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONPath_field_access_filter_rlike extends TestCase {
    public void test_list_like_extract() throws Exception {
        JSONPath path = new JSONPath("$[name rlike 'ljw2083']");
        List<JSONPath_field_access_filter_rlike.Entity> entities = new ArrayList<JSONPath_field_access_filter_rlike.Entity>();
        entities.add(new JSONPath_field_access_filter_rlike.Entity(1001, "ljw2083"));
        entities.add(new JSONPath_field_access_filter_rlike.Entity(1002, "wenshao"));
        entities.add(new JSONPath_field_access_filter_rlike.Entity(1003, null));
        entities.add(new JSONPath_field_access_filter_rlike.Entity(null, null));
        List<Object> result = ((List<Object>) (path.eval(entities)));
        Assert.assertEquals(1, result.size());
        Assert.assertSame(entities.get(0), result.get(0));
    }

    public void test_list_not_like_extract() throws Exception {
        JSONPath path = new JSONPath("$[name not rlike 'wenshao']");
        List<JSONPath_field_access_filter_rlike.Entity> entities = new ArrayList<JSONPath_field_access_filter_rlike.Entity>();
        entities.add(new JSONPath_field_access_filter_rlike.Entity(1001, "ljw2083"));
        entities.add(new JSONPath_field_access_filter_rlike.Entity(1002, "wenshao"));
        entities.add(new JSONPath_field_access_filter_rlike.Entity(1003, "yakolee"));
        entities.add(new JSONPath_field_access_filter_rlike.Entity(null, null));
        List<Object> result = ((List<Object>) (path.eval(entities)));
        Assert.assertEquals(2, result.size());
        Assert.assertSame(entities.get(0), result.get(0));
        Assert.assertSame(entities.get(2), result.get(1));
    }

    public void test_list_like_left_match() throws Exception {
        JSONPath path = new JSONPath("$[?(@.name like 'ljw%')]");
        List<JSONPath_field_access_filter_rlike.Entity> entities = new ArrayList<JSONPath_field_access_filter_rlike.Entity>();
        entities.add(new JSONPath_field_access_filter_rlike.Entity(1001, "ljw2083"));
        entities.add(new JSONPath_field_access_filter_rlike.Entity(1002, "wenshao"));
        entities.add(new JSONPath_field_access_filter_rlike.Entity(1003, "yakolee"));
        entities.add(new JSONPath_field_access_filter_rlike.Entity(null, null));
        List<Object> result = ((List<Object>) (path.eval(entities)));
        Assert.assertEquals(1, result.size());
        Assert.assertSame(entities.get(0), result.get(0));
    }

    public void test_list_like_right_match() throws Exception {
        JSONPath path = new JSONPath("$[?(@.name like '%2083')]");
        List<JSONPath_field_access_filter_rlike.Entity> entities = new ArrayList<JSONPath_field_access_filter_rlike.Entity>();
        entities.add(new JSONPath_field_access_filter_rlike.Entity(1001, "ljw2083"));
        entities.add(new JSONPath_field_access_filter_rlike.Entity(1002, "wenshao"));
        entities.add(new JSONPath_field_access_filter_rlike.Entity(1003, "yakolee"));
        entities.add(new JSONPath_field_access_filter_rlike.Entity(null, null));
        List<Object> result = ((List<Object>) (path.eval(entities)));
        Assert.assertEquals(1, result.size());
        Assert.assertSame(entities.get(0), result.get(0));
    }

    public void test_list_like_contains() throws Exception {
        JSONPath path = new JSONPath("$[?(@.name like '%208%')]");
        List<JSONPath_field_access_filter_rlike.Entity> entities = new ArrayList<JSONPath_field_access_filter_rlike.Entity>();
        entities.add(new JSONPath_field_access_filter_rlike.Entity(1001, "ljw2083"));
        entities.add(new JSONPath_field_access_filter_rlike.Entity(1002, "wenshao"));
        entities.add(new JSONPath_field_access_filter_rlike.Entity(1003, "yakolee"));
        entities.add(new JSONPath_field_access_filter_rlike.Entity(null, null));
        List<Object> result = ((List<Object>) (path.eval(entities)));
        Assert.assertEquals(1, result.size());
        Assert.assertSame(entities.get(0), result.get(0));
    }

    public void test_list_like_match_two_segement() throws Exception {
        JSONPath path = new JSONPath("$[?(@.name like 'ljw%83')]");
        List<JSONPath_field_access_filter_rlike.Entity> entities = new ArrayList<JSONPath_field_access_filter_rlike.Entity>();
        entities.add(new JSONPath_field_access_filter_rlike.Entity(1001, "ljw2083"));
        entities.add(new JSONPath_field_access_filter_rlike.Entity(1002, "wenshao"));
        entities.add(new JSONPath_field_access_filter_rlike.Entity(1003, "yakolee"));
        entities.add(new JSONPath_field_access_filter_rlike.Entity(null, null));
        List<Object> result = ((List<Object>) (path.eval(entities)));
        Assert.assertEquals(1, result.size());
        Assert.assertSame(entities.get(0), result.get(0));
    }

    public void test_list_like_match_two_segement_2() throws Exception {
        JSONPath path = new JSONPath("$[?(@.name like 'ljw%w2083')]");
        List<JSONPath_field_access_filter_rlike.Entity> entities = new ArrayList<JSONPath_field_access_filter_rlike.Entity>();
        entities.add(new JSONPath_field_access_filter_rlike.Entity(1001, "ljw2083"));
        entities.add(new JSONPath_field_access_filter_rlike.Entity(1002, "wenshao"));
        entities.add(new JSONPath_field_access_filter_rlike.Entity(1003, "yakolee"));
        entities.add(new JSONPath_field_access_filter_rlike.Entity(null, null));
        List<Object> result = ((List<Object>) (path.eval(entities)));
        Assert.assertEquals(0, result.size());
    }

    public void test_list_like_match_two_segement_3() throws Exception {
        JSONPath path = new JSONPath("$[?(@.name like 'ljw%2%0%83')]");
        List<JSONPath_field_access_filter_rlike.Entity> entities = new ArrayList<JSONPath_field_access_filter_rlike.Entity>();
        entities.add(new JSONPath_field_access_filter_rlike.Entity(1001, "ljw2083"));
        entities.add(new JSONPath_field_access_filter_rlike.Entity(1002, "wenshao"));
        entities.add(new JSONPath_field_access_filter_rlike.Entity(1003, "yakolee"));
        entities.add(new JSONPath_field_access_filter_rlike.Entity(null, null));
        List<Object> result = ((List<Object>) (path.eval(entities)));
        Assert.assertEquals(1, result.size());
        Assert.assertSame(entities.get(0), result.get(0));
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

