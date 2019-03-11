package com.alibaba.json.bvt.parser.deser.list;


import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONReader;
import java.io.StringReader;
import java.util.Map;
import java.util.TreeSet;
import junit.framework.TestCase;
import org.junit.Assert;


public class ListStringFieldTest_stream_TreeSet extends TestCase {
    public void test_list() throws Exception {
        String text = "{\"values\":[\"a\",\"b\",\"ab\\\\c\"]}";
        JSONReader reader = new JSONReader(new StringReader(text));
        ListStringFieldTest_stream_TreeSet.Model model = reader.readObject(ListStringFieldTest_stream_TreeSet.Model.class);
        Assert.assertEquals(3, model.values.size());
        Assert.assertTrue(model.values.contains("a"));
        Assert.assertTrue(model.values.contains("b"));
    }

    public void test_null() throws Exception {
        String text = "{\"values\":null}";
        JSONReader reader = new JSONReader(new StringReader(text));
        ListStringFieldTest_stream_TreeSet.Model model = reader.readObject(ListStringFieldTest_stream_TreeSet.Model.class);
        Assert.assertNull(model.values);
    }

    public void test_empty() throws Exception {
        String text = "{\"values\":[]}";
        JSONReader reader = new JSONReader(new StringReader(text));
        ListStringFieldTest_stream_TreeSet.Model model = reader.readObject(ListStringFieldTest_stream_TreeSet.Model.class);
        Assert.assertEquals(0, model.values.size());
    }

    public void test_map_empty() throws Exception {
        String text = "{\"model\":{\"values\":[]}}";
        JSONReader reader = new JSONReader(new StringReader(text));
        Map<String, ListStringFieldTest_stream_TreeSet.Model> map = reader.readObject(new com.alibaba.fastjson.TypeReference<Map<String, ListStringFieldTest_stream_TreeSet.Model>>() {});
        ListStringFieldTest_stream_TreeSet.Model model = ((ListStringFieldTest_stream_TreeSet.Model) (map.get("model")));
        Assert.assertEquals(0, model.values.size());
    }

    public void test_notMatch() throws Exception {
        String text = "{\"value\":[]}";
        JSONReader reader = new JSONReader(new StringReader(text));
        ListStringFieldTest_stream_TreeSet.Model model = reader.readObject(ListStringFieldTest_stream_TreeSet.Model.class);
        Assert.assertNull(model.values);
    }

    public void test_error() throws Exception {
        String text = "{\"values\":[1";
        JSONReader reader = new JSONReader(new StringReader(text));
        Exception error = null;
        try {
            reader.readObject(ListStringFieldTest_stream_TreeSet.Model.class);
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
            reader.readObject(ListStringFieldTest_stream_TreeSet.Model.class);
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
            reader.readObject(new com.alibaba.fastjson.TypeReference<Map<String, ListStringFieldTest_stream_TreeSet.Model>>() {});
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
            reader.readObject(new com.alibaba.fastjson.TypeReference<Map<String, ListStringFieldTest_stream_TreeSet.Model>>() {});
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class Model {
        private TreeSet<String> values;

        public TreeSet<String> getValues() {
            return values;
        }

        public void setValues(TreeSet<String> values) {
            this.values = values;
        }
    }
}

