package com.alibaba.json.bvt.parser.deser.list;


import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONReader;
import java.io.StringReader;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;
import org.junit.Assert;


public class ListStringFieldTest_stream_hashSet extends TestCase {
    public void test_list() throws Exception {
        String text = "{\"values\":[\"a\",null,\"b\",\"ab\\\\c\"]}";
        JSONReader reader = new JSONReader(new StringReader(text));
        ListStringFieldTest_stream_hashSet.Model model = reader.readObject(ListStringFieldTest_stream_hashSet.Model.class);
        Assert.assertEquals(4, model.values.size());
        Assert.assertTrue(model.values.contains("a"));
        Assert.assertTrue(model.values.contains("b"));
        Assert.assertTrue(model.values.contains(null));
        Assert.assertTrue(model.values.contains("ab\\c"));
    }

    public void test_null() throws Exception {
        String text = "{\"values\":null}";
        JSONReader reader = new JSONReader(new StringReader(text));
        ListStringFieldTest_stream_hashSet.Model model = reader.readObject(ListStringFieldTest_stream_hashSet.Model.class);
        Assert.assertNull(model.values);
    }

    public void test_empty() throws Exception {
        String text = "{\"values\":[]}";
        JSONReader reader = new JSONReader(new StringReader(text));
        ListStringFieldTest_stream_hashSet.Model model = reader.readObject(ListStringFieldTest_stream_hashSet.Model.class);
        Assert.assertEquals(0, model.values.size());
    }

    public void test_map_empty() throws Exception {
        String text = "{\"model\":{\"values\":[]}}";
        JSONReader reader = new JSONReader(new StringReader(text));
        Map<String, ListStringFieldTest_stream_hashSet.Model> map = reader.readObject(new com.alibaba.fastjson.TypeReference<Map<String, ListStringFieldTest_stream_hashSet.Model>>() {});
        ListStringFieldTest_stream_hashSet.Model model = ((ListStringFieldTest_stream_hashSet.Model) (map.get("model")));
        Assert.assertEquals(0, model.values.size());
    }

    public void test_notMatch() throws Exception {
        String text = "{\"value\":[]}";
        JSONReader reader = new JSONReader(new StringReader(text));
        ListStringFieldTest_stream_hashSet.Model model = reader.readObject(ListStringFieldTest_stream_hashSet.Model.class);
        Assert.assertNull(model.values);
    }

    public void test_error() throws Exception {
        String text = "{\"values\":[1";
        JSONReader reader = new JSONReader(new StringReader(text));
        Exception error = null;
        try {
            reader.readObject(ListStringFieldTest_stream_hashSet.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_1() throws Exception {
        String text = "{\"values\":[\"b\"[";
        JSONReader reader = new JSONReader(new StringReader(text));
        Exception error = null;
        try {
            reader.readObject(ListStringFieldTest_stream_hashSet.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_2() throws Exception {
        String text = "{\"model\":{\"values\":[][";
        JSONReader reader = new JSONReader(new StringReader(text));
        Exception error = null;
        try {
            reader.readObject(new com.alibaba.fastjson.TypeReference<Map<String, ListStringFieldTest_stream_hashSet.Model>>() {});
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_3() throws Exception {
        String text = "{\"model\":{\"values\":[]}[";
        JSONReader reader = new JSONReader(new StringReader(text));
        Exception error = null;
        try {
            reader.readObject(new com.alibaba.fastjson.TypeReference<Map<String, ListStringFieldTest_stream_hashSet.Model>>() {});
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class Model {
        private Set<String> values;

        public Set<String> getValues() {
            return values;
        }

        public void setValues(Set<String> values) {
            this.values = values;
        }
    }
}

