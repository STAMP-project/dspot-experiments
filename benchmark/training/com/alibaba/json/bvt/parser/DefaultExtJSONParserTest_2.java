package com.alibaba.json.bvt.parser;


import Feature.AllowSingleQuotes;
import Feature.AllowUnQuotedFieldNames;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class DefaultExtJSONParserTest_2 extends TestCase {
    public void test_0() throws Exception {
        DefaultJSONParser parser = new DefaultJSONParser("{'a':3}");
        parser.config(AllowSingleQuotes, true);
        DefaultExtJSONParserTest_2.A a = parser.parseObject(DefaultExtJSONParserTest_2.A.class);
        Assert.assertEquals(3, a.getA());
    }

    public void test_1() throws Exception {
        DefaultJSONParser parser = new DefaultJSONParser("{a:3}");
        parser.config(AllowUnQuotedFieldNames, true);
        DefaultExtJSONParserTest_2.A a = parser.parseObject(DefaultExtJSONParserTest_2.A.class);
        Assert.assertEquals(3, a.getA());
    }

    public void test_2() throws Exception {
        DefaultJSONParser parser = new DefaultJSONParser("{a:3}");
        parser.config(AllowUnQuotedFieldNames, true);
        Map a = parser.parseObject(Map.class);
        Assert.assertEquals(3, a.get("a"));
    }

    public void test_3() throws Exception {
        DefaultJSONParser parser = new DefaultJSONParser("{a:3}");
        parser.config(AllowUnQuotedFieldNames, true);
        HashMap a = parser.parseObject(HashMap.class);
        Assert.assertEquals(3, a.get("a"));
    }

    public void test_4() throws Exception {
        DefaultJSONParser parser = new DefaultJSONParser("{a:3}");
        parser.config(AllowUnQuotedFieldNames, true);
        LinkedHashMap a = parser.parseObject(LinkedHashMap.class);
        Assert.assertEquals(3, a.get("a"));
    }

    public void test_error_0() throws Exception {
        Exception error = null;
        try {
            String text = "[{\"old\":false,\"name\":\"\u6821\u957f\",\"age\":3,\"salary\":123456789.0123]";
            DefaultJSONParser parser = new DefaultJSONParser(text);
            parser.parseArray(DefaultExtJSONParserTest.User.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_1() throws Exception {
        JSONException error = null;
        try {
            DefaultJSONParser parser = new DefaultJSONParser("{'a'3}");
            parser.config(AllowSingleQuotes, true);
            parser.parseObject(DefaultExtJSONParserTest_2.A.class);
        } catch (JSONException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_2() throws Exception {
        JSONException error = null;
        try {
            DefaultJSONParser parser = new DefaultJSONParser("{a 3}");
            parser.config(AllowUnQuotedFieldNames, true);
            parser.parseObject(DefaultExtJSONParserTest_2.A.class);
        } catch (JSONException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_3() throws Exception {
        JSONException error = null;
        try {
            DefaultJSONParser parser = new DefaultJSONParser("{");
            parser.config(AllowUnQuotedFieldNames, true);
            parser.parseObject(DefaultExtJSONParserTest_2.A.class);
        } catch (JSONException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_4() throws Exception {
        JSONException error = null;
        try {
            DefaultJSONParser parser = new DefaultJSONParser("{\"a\"3}");
            parser.parseObject(DefaultExtJSONParserTest_2.A.class);
        } catch (JSONException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_5() throws Exception {
        JSONException error = null;
        try {
            DefaultJSONParser parser = new DefaultJSONParser("{a:3}");
            parser.config(AllowUnQuotedFieldNames, false);
            parser.parseObject(DefaultExtJSONParserTest_2.A.class);
        } catch (JSONException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_6() throws Exception {
        JSONException error = null;
        try {
            DefaultJSONParser parser = new DefaultJSONParser("{'a':3}");
            parser.config(AllowSingleQuotes, false);
            parser.parseObject(DefaultExtJSONParserTest_2.A.class);
        } catch (JSONException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public static class A {
        private int a;

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }
    }
}

