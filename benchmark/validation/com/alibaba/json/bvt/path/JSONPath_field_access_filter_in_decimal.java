package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSONPath;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONPath_field_access_filter_in_decimal extends TestCase {
    public void test_list_in() throws Exception {
        JSONPath path = new JSONPath("[id in (1001)]");
        List<JSONPath_field_access_filter_in_decimal.Entity> entities = new ArrayList<JSONPath_field_access_filter_in_decimal.Entity>();
        entities.add(new JSONPath_field_access_filter_in_decimal.Entity(1001, "ljw2083"));
        entities.add(new JSONPath_field_access_filter_in_decimal.Entity(1002, "wenshao"));
        entities.add(new JSONPath_field_access_filter_in_decimal.Entity(1003, "yakolee"));
        entities.add(new JSONPath_field_access_filter_in_decimal.Entity(1004, null));
        List<Object> result = ((List<Object>) (path.eval(entities)));
        Assert.assertEquals(1, result.size());
        Assert.assertSame(entities.get(0), result.get(0));
    }

    public void test_list_not_in() throws Exception {
        JSONPath path = new JSONPath("[id not in (1001)]");
        List<JSONPath_field_access_filter_in_decimal.Entity> entities = new ArrayList<JSONPath_field_access_filter_in_decimal.Entity>();
        entities.add(new JSONPath_field_access_filter_in_decimal.Entity(1001, "ljw2083"));
        entities.add(new JSONPath_field_access_filter_in_decimal.Entity(1002, "wenshao"));
        entities.add(new JSONPath_field_access_filter_in_decimal.Entity(1003, "yakolee"));
        entities.add(new JSONPath_field_access_filter_in_decimal.Entity(1004, null));
        List<Object> result = ((List<Object>) (path.eval(entities)));
        Assert.assertEquals(3, result.size());
        Assert.assertSame(entities.get(1), result.get(0));
        Assert.assertSame(entities.get(2), result.get(1));
        Assert.assertSame(entities.get(3), result.get(2));
    }

    public void test_list_not_in_null() throws Exception {
        JSONPath path = new JSONPath("[id not in (null)]");
        List<JSONPath_field_access_filter_in_decimal.Entity> entities = new ArrayList<JSONPath_field_access_filter_in_decimal.Entity>();
        entities.add(new JSONPath_field_access_filter_in_decimal.Entity(1001, "ljw2083"));
        entities.add(new JSONPath_field_access_filter_in_decimal.Entity(1002, "wenshao"));
        entities.add(new JSONPath_field_access_filter_in_decimal.Entity(1003, "yakolee"));
        entities.add(new JSONPath_field_access_filter_in_decimal.Entity(1004, null));
        List<Object> result = ((List<Object>) (path.eval(entities)));
        Assert.assertEquals(4, result.size());
        Assert.assertSame(entities.get(0), result.get(0));
        Assert.assertSame(entities.get(1), result.get(1));
        Assert.assertSame(entities.get(2), result.get(2));
        Assert.assertSame(entities.get(3), result.get(3));
    }

    public void test_list_in_2() throws Exception {
        JSONPath path = new JSONPath("[id in (1001, 1003)]");
        List<JSONPath_field_access_filter_in_decimal.Entity> entities = new ArrayList<JSONPath_field_access_filter_in_decimal.Entity>();
        entities.add(new JSONPath_field_access_filter_in_decimal.Entity(1001, "ljw2083"));
        entities.add(new JSONPath_field_access_filter_in_decimal.Entity(1002, "wenshao"));
        entities.add(new JSONPath_field_access_filter_in_decimal.Entity(1003, "yakolee"));
        entities.add(new JSONPath_field_access_filter_in_decimal.Entity(1004, null));
        List<Object> result = ((List<Object>) (path.eval(entities)));
        Assert.assertEquals(2, result.size());
        Assert.assertSame(entities.get(0), result.get(0));
        Assert.assertSame(entities.get(2), result.get(1));
    }

    public void test_list_in_3() throws Exception {
        JSONPath path = new JSONPath("[id in (1001, 1003, 1004)]");
        List<JSONPath_field_access_filter_in_decimal.Entity> entities = new ArrayList<JSONPath_field_access_filter_in_decimal.Entity>();
        entities.add(new JSONPath_field_access_filter_in_decimal.Entity(1001, "ljw2083"));
        entities.add(new JSONPath_field_access_filter_in_decimal.Entity(1002, "wenshao"));
        entities.add(new JSONPath_field_access_filter_in_decimal.Entity(1003, "yakolee"));
        entities.add(new JSONPath_field_access_filter_in_decimal.Entity(1004, null));
        List<Object> result = ((List<Object>) (path.eval(entities)));
        Assert.assertEquals(3, result.size());
        Assert.assertSame(entities.get(0), result.get(0));
        Assert.assertSame(entities.get(2), result.get(1));
        Assert.assertSame(entities.get(3), result.get(2));
    }

    public void test_list_in_3_null() throws Exception {
        JSONPath path = new JSONPath("[id in (1001, 1003, null)]");
        List<JSONPath_field_access_filter_in_decimal.Entity> entities = new ArrayList<JSONPath_field_access_filter_in_decimal.Entity>();
        entities.add(new JSONPath_field_access_filter_in_decimal.Entity(1001, "ljw2083"));
        entities.add(new JSONPath_field_access_filter_in_decimal.Entity(1002, "wenshao"));
        entities.add(new JSONPath_field_access_filter_in_decimal.Entity(1003, "yakolee"));
        entities.add(new JSONPath_field_access_filter_in_decimal.Entity(null, null));
        List<Object> result = ((List<Object>) (path.eval(entities)));
        Assert.assertEquals(3, result.size());
        Assert.assertSame(entities.get(0), result.get(0));
        Assert.assertSame(entities.get(2), result.get(1));
        Assert.assertSame(entities.get(3), result.get(2));
    }

    public static class Entity {
        private BigDecimal id;

        private String name;

        public Entity(Integer id, String name) {
            if (id == null) {
                this.id = null;
            } else {
                this.id = BigDecimal.valueOf(id);
            }
            this.name = name;
        }

        public BigDecimal getId() {
            return id;
        }

        public void setId(BigDecimal id) {
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

