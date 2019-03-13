package com.alibaba.json.bvt.issue_2200;


import Feature.ErrorOnEnumNotMatch;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import junit.framework.TestCase;


public class Issue2249 extends TestCase {
    public void test_for_issue() throws Exception {
        TestCase.assertSame(Issue2249.Type.Big, JSON.parseObject("\"big\"", Issue2249.Type.class));
        TestCase.assertSame(Issue2249.Type.Big, JSON.parseObject("\"Big\"", Issue2249.Type.class));
        TestCase.assertSame(Issue2249.Type.Big, JSON.parseObject("\"BIG\"", Issue2249.Type.class));
        TestCase.assertSame(Issue2249.Type.Small, JSON.parseObject("\"Small\"", Issue2249.Type.class));
        TestCase.assertSame(Issue2249.Type.Small, JSON.parseObject("\"small\"", Issue2249.Type.class));
        TestCase.assertSame(Issue2249.Type.Small, JSON.parseObject("\"SMALL\"", Issue2249.Type.class));
        TestCase.assertSame(Issue2249.Type.Medium, JSON.parseObject("\"medium\"", Issue2249.Type.class));
        TestCase.assertSame(Issue2249.Type.Medium, JSON.parseObject("\"MEDIUM\"", Issue2249.Type.class));
        TestCase.assertSame(Issue2249.Type.Medium, JSON.parseObject("\"Medium\"", Issue2249.Type.class));
        TestCase.assertSame(Issue2249.Type.Medium, JSON.parseObject("\"MediuM\"", Issue2249.Type.class));
        TestCase.assertNull(JSON.parseObject("\"\"", Issue2249.Type.class));
    }

    public void test_for_issue_1() throws Exception {
        TestCase.assertSame(Issue2249.Type.Big, JSON.parseObject("{\"type\":\"bIG\"}", Issue2249.Model.class).type);
        TestCase.assertSame(Issue2249.Type.Big, JSON.parseObject("{\"type\":\"big\"}", Issue2249.Model.class).type);
        TestCase.assertSame(Issue2249.Type.Big, JSON.parseObject("{\"type\":\"Big\"}", Issue2249.Model.class).type);
        TestCase.assertSame(Issue2249.Type.Big, JSON.parseObject("{\"type\":\"BIG\"}", Issue2249.Model.class).type);
        TestCase.assertSame(Issue2249.Type.Small, JSON.parseObject("{\"type\":\"Small\"}", Issue2249.Model.class).type);
        TestCase.assertSame(Issue2249.Type.Small, JSON.parseObject("{\"type\":\"SmAll\"}", Issue2249.Model.class).type);
        TestCase.assertSame(Issue2249.Type.Small, JSON.parseObject("{\"type\":\"small\"}", Issue2249.Model.class).type);
        TestCase.assertSame(Issue2249.Type.Small, JSON.parseObject("{\"type\":\"SMALL\"}", Issue2249.Model.class).type);
        TestCase.assertSame(Issue2249.Type.Medium, JSON.parseObject("{\"type\":\"Medium\"}", Issue2249.Model.class).type);
        TestCase.assertSame(Issue2249.Type.Medium, JSON.parseObject("{\"type\":\"MediuM\"}", Issue2249.Model.class).type);
        TestCase.assertSame(Issue2249.Type.Medium, JSON.parseObject("{\"type\":\"medium\"}", Issue2249.Model.class).type);
        TestCase.assertSame(Issue2249.Type.Medium, JSON.parseObject("{\"type\":\"MEDIUM\"}", Issue2249.Model.class).type);
    }

    public void test_for_issue_null() throws Exception {
        TestCase.assertNull(JSON.parseObject("{\"type\":\"\"}", Issue2249.Model.class).type);
    }

    public void test_for_issue_null_2() throws Exception {
        TestCase.assertNull(JSON.parseObject("{\"type\":\"\"}", Issue2249.Model.class, ErrorOnEnumNotMatch).type);
    }

    public void test_for_issue_error() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("\"xxx\"", Issue2249.Type.class, ErrorOnEnumNotMatch);
        } catch (JSONException e) {
            error = e;
        }
        TestCase.assertNotNull(error);
    }

    public void test_for_issue_error_1() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"type\":\"xxx\"}", Issue2249.Model.class, ErrorOnEnumNotMatch);
        } catch (JSONException e) {
            error = e;
        }
        TestCase.assertNotNull(error);
    }

    public enum Type {

        Big,
        Small,
        Medium;}

    public static class Model {
        public Issue2249.Type type;
    }
}

