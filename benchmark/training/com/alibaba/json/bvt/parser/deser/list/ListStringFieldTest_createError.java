package com.alibaba.json.bvt.parser.deser.list;


import Feature.SupportArrayToBean;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONReader;
import java.io.StringReader;
import java.util.ArrayList;
import junit.framework.TestCase;
import org.junit.Assert;


public class ListStringFieldTest_createError extends TestCase {
    public void test_null() throws Exception {
        Exception error = null;
        try {
            String text = "{\"values\":[]}";
            JSON.parseObject(text, ListStringFieldTest_createError.Model.class, SupportArrayToBean);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_reader() throws Exception {
        Exception error = null;
        try {
            String text = "{\"values\":[]}";
            JSONReader reader = new JSONReader(new StringReader(text));
            reader.readObject(ListStringFieldTest_createError.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class Model {
        private ListStringFieldTest_createError.MyErrorList<String> values;

        public ListStringFieldTest_createError.MyErrorList<String> getValues() {
            return values;
        }

        public void setValues(ListStringFieldTest_createError.MyErrorList<String> values) {
            this.values = values;
        }
    }

    public static class MyErrorList<T> extends ArrayList<T> {
        public MyErrorList() {
            throw new IllegalStateException();
        }
    }
}

