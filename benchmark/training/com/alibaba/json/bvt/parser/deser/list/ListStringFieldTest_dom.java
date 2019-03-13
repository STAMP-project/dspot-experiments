package com.alibaba.json.bvt.parser.deser.list;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class ListStringFieldTest_dom extends TestCase {
    public void test_list() throws Exception {
        String text = "{\"values\":[\"a\",null,\"b\",\"ab\\\\c\"]}";
        ListStringFieldTest_dom.Model model = JSON.parseObject(text, ListStringFieldTest_dom.Model.class);
        Assert.assertEquals(4, model.values.size());
        Assert.assertEquals("a", model.values.get(0));
        Assert.assertEquals(null, model.values.get(1));
        Assert.assertEquals("b", model.values.get(2));
        Assert.assertEquals("ab\\c", model.values.get(3));
    }

    public void test_null() throws Exception {
        String text = "{\"values\":null}";
        ListStringFieldTest_dom.Model model = JSON.parseObject(text, ListStringFieldTest_dom.Model.class);
        Assert.assertNull(model.values);
    }

    public void test_empty() throws Exception {
        String text = "{\"values\":[]}";
        ListStringFieldTest_dom.Model model = JSON.parseObject(text, ListStringFieldTest_dom.Model.class);
        Assert.assertEquals(0, model.values.size());
    }

    public void test_null_element() throws Exception {
        String text = "{\"values\":[\"abc\",null]}";
        ListStringFieldTest_dom.Model model = JSON.parseObject(text, ListStringFieldTest_dom.Model.class);
        Assert.assertEquals(2, model.values.size());
        Assert.assertEquals("abc", model.values.get(0));
        Assert.assertEquals(null, model.values.get(1));
    }

    public void test_map_empty() throws Exception {
        String text = "{\"model\":{\"values\":[]}}";
        Map<String, ListStringFieldTest_dom.Model> map = JSON.parseObject(text, new com.alibaba.fastjson.TypeReference<Map<String, ListStringFieldTest_dom.Model>>() {});
        ListStringFieldTest_dom.Model model = ((ListStringFieldTest_dom.Model) (map.get("model")));
        Assert.assertEquals(0, model.values.size());
    }

    public void test_notMatch() throws Exception {
        String text = "{\"value\":[]}";
        ListStringFieldTest_dom.Model model = JSON.parseObject(text, ListStringFieldTest_dom.Model.class);
        Assert.assertNull(model.values);
    }

    public void test_error() throws Exception {
        String text = "{\"values\":[1";
        Exception error = null;
        try {
            JSON.parseObject(text, ListStringFieldTest_dom.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_1() throws Exception {
        String text = "{\"values\":[\"b\"[";
        Exception error = null;
        try {
            JSON.parseObject(text, ListStringFieldTest_dom.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_2() throws Exception {
        String text = "{\"model\":{\"values\":[][";
        Exception error = null;
        try {
            JSON.parseObject(text, new com.alibaba.fastjson.TypeReference<Map<String, ListStringFieldTest_dom.Model>>() {});
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_3() throws Exception {
        String text = "{\"model\":{\"values\":[]}[";
        Exception error = null;
        try {
            JSON.parseObject(text, new com.alibaba.fastjson.TypeReference<Map<String, ListStringFieldTest_dom.Model>>() {});
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_4() throws Exception {
        String text = "{\"model\":{\"values\":[\"aaa]}[";
        Exception error = null;
        try {
            JSON.parseObject(text, new com.alibaba.fastjson.TypeReference<Map<String, ListStringFieldTest_dom.Model>>() {});
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_n() throws Exception {
        String text = "{\"values\":[n";
        Exception error = null;
        try {
            JSON.parseObject(text, ListStringFieldTest_dom.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_nu() throws Exception {
        String text = "{\"values\":[nu";
        Exception error = null;
        try {
            JSON.parseObject(text, ListStringFieldTest_dom.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_nul() throws Exception {
        String text = "{\"values\":[nul";
        Exception error = null;
        try {
            JSON.parseObject(text, ListStringFieldTest_dom.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_null() throws Exception {
        String text = "{\"values\":[null";
        Exception error = null;
        try {
            JSON.parseObject(text, ListStringFieldTest_dom.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_rbacket() throws Exception {
        String text = "{\"values\":[null,]";
        Exception error = null;
        try {
            JSON.parseObject(text, ListStringFieldTest_dom.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class Model {
        private List<String> values;

        public List<String> getValues() {
            return values;
        }

        public void setValues(List<String> values) {
            this.values = values;
        }
    }
}

