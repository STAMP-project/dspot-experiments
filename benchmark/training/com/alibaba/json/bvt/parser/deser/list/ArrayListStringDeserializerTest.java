package com.alibaba.json.bvt.parser.deser.list;


import Feature.AllowSingleQuotes;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import junit.framework.TestCase;
import org.junit.Assert;


public class ArrayListStringDeserializerTest extends TestCase {
    public void test_null() throws Exception {
        Assert.assertNull(JSON.parseObject("null", new com.alibaba.fastjson.TypeReference<java.util.List<String>>() {}));
        Assert.assertNull(JSON.parseArray("null", new Type[]{ getType() }));
        Assert.assertNull(JSON.parseArray("null", ArrayListStringDeserializerTest.Entity.class));
        Assert.assertNull(JSON.parseArray("null", ArrayListStringDeserializerTest.Entity[].class));
        Assert.assertNull(JSON.parseArray("null"));
        Assert.assertNull(JSON.parseObject("null"));
        Assert.assertNull(JSON.parseObject("null", Object[].class));
        Assert.assertNull(JSON.parseObject("null", ArrayListStringDeserializerTest.Entity[].class));
        Assert.assertNull(JSON.parseArray("[null]", new Type[]{ getType() }).get(0));
    }

    public void test_strings() throws Exception {
        ArrayListStringDeserializerTest.Entity a = JSON.parseObject("{units:['NANOSECONDS', 'SECONDS', 3, null]}", ArrayListStringDeserializerTest.Entity.class);
        Assert.assertEquals("NANOSECONDS", a.getUnits().get(0));
        Assert.assertEquals("SECONDS", a.getUnits().get(1));
        Assert.assertEquals("3", a.getUnits().get(2));
        Assert.assertEquals(null, a.getUnits().get(3));
    }

    public void test_strings_() throws Exception {
        ArrayListStringDeserializerTest.Entity a = JSON.parseObject("{units:['NANOSECONDS',,,, 'SECONDS', 3, null]}", ArrayListStringDeserializerTest.Entity.class);
        Assert.assertEquals("NANOSECONDS", a.getUnits().get(0));
        Assert.assertEquals("SECONDS", a.getUnits().get(1));
        Assert.assertEquals("3", a.getUnits().get(2));
        Assert.assertEquals(null, a.getUnits().get(3));
    }

    public void test_strings_2() throws Exception {
        java.util.List<String> list = JSON.parseObject("['NANOSECONDS', 'SECONDS', 3, null]", new com.alibaba.fastjson.TypeReference<java.util.List<String>>() {});
        Assert.assertEquals("NANOSECONDS", list.get(0));
        Assert.assertEquals("SECONDS", list.get(1));
        Assert.assertEquals("3", list.get(2));
        Assert.assertEquals(null, list.get(3));
    }

    public void test_strings_3() throws Exception {
        java.util.List<String> list = JSON.parseObject("['NANOSECONDS', 'SECONDS', 3, null]", new com.alibaba.fastjson.TypeReference<java.util.List<String>>() {}.getType(), 0, AllowSingleQuotes);
        Assert.assertEquals("NANOSECONDS", list.get(0));
        Assert.assertEquals("SECONDS", list.get(1));
        Assert.assertEquals("3", list.get(2));
        Assert.assertEquals(null, list.get(3));
    }

    public void test_string_error_not_eof() throws Exception {
        JSONException ex = null;
        try {
            JSON.parseObject("[}", new com.alibaba.fastjson.TypeReference<java.util.List<String>>() {}.getType(), 0, AllowSingleQuotes);
        } catch (JSONException e) {
            ex = e;
        }
        Assert.assertNotNull(ex);
    }

    public void test_string_error() throws Exception {
        JSONException ex = null;
        try {
            JSON.parseObject("'123'", new com.alibaba.fastjson.TypeReference<java.util.List<String>>() {});
        } catch (JSONException e) {
            ex = e;
        }
        Assert.assertNotNull(ex);
    }

    public void test_string_error_1() throws Exception {
        JSONException ex = null;
        try {
            ArrayListStringDeserializerTest.parseObject("{units:['NANOSECONDS',,,, 'SECONDS', 3, null]}", ArrayListStringDeserializerTest.Entity.class);
        } catch (JSONException e) {
            ex = e;
        }
        Assert.assertNotNull(ex);
    }

    public static class Entity {
        private java.util.List<String> units = new ArrayList<String>();

        public java.util.List<String> getUnits() {
            return units;
        }

        public void setUnits(java.util.List<String> units) {
            this.units = units;
        }
    }
}

