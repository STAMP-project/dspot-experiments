package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class TypeUtilsTest3 extends TestCase {
    public void test_enum() throws Exception {
        Assert.assertEquals(TypeUtilsTest3.Type.A, JSON.parseObject("\"A\"", TypeUtilsTest3.Type.class));
        Assert.assertEquals(TypeUtilsTest3.Type.A, JSON.parseObject(("" + (TypeUtilsTest3.Type.A.ordinal())), TypeUtilsTest3.Type.class));
        Assert.assertEquals(TypeUtilsTest3.Type.B, JSON.parseObject(("" + (TypeUtilsTest3.Type.B.ordinal())), TypeUtilsTest3.Type.class));
        Assert.assertEquals(TypeUtilsTest3.Type.C, JSON.parseObject(("" + (TypeUtilsTest3.Type.C.ordinal())), TypeUtilsTest3.Type.class));
    }

    public void test_enum_2() throws Exception {
        Assert.assertEquals(TypeUtilsTest3.Type.A, TypeUtils.cast("A", TypeUtilsTest3.Type.class, null));
        Assert.assertEquals(TypeUtilsTest3.Type.A, TypeUtils.cast(TypeUtilsTest3.Type.A.ordinal(), TypeUtilsTest3.Type.class, null));
        Assert.assertEquals(TypeUtilsTest3.Type.B, TypeUtils.cast(TypeUtilsTest3.Type.B.ordinal(), TypeUtilsTest3.Type.class, null));
    }

    public void test_error() throws Exception {
        TestCase.assertNull(TypeUtils.castToEnum("\"A1\"", TypeUtilsTest3.Type.class, null));
    }

    public void test_error_1() throws Exception {
        Exception error = null;
        try {
            TypeUtils.castToEnum(Boolean.TRUE, TypeUtilsTest3.Type.class, null);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_2() throws Exception {
        Exception error = null;
        try {
            TypeUtils.castToEnum(1000, TypeUtilsTest3.Type.class, null);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_null() throws Exception {
        Assert.assertEquals(null, TypeUtils.cast(null, getType(), null));
    }

    public void test_null_1() throws Exception {
        Assert.assertEquals(null, TypeUtils.cast("", getType(), null));
    }

    public void test_null_2() throws Exception {
        Assert.assertEquals(null, TypeUtils.cast("", getType(), null));
    }

    public void test_error_3() throws Exception {
        Exception error = null;
        try {
            TypeUtils.cast("xxx", getType(), null);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_ex() throws Exception {
        RuntimeException ex = new RuntimeException();
        JSONObject object = ((JSONObject) (JSON.toJSON(ex)));
        JSONArray array = object.getJSONArray("stackTrace");
        array.getJSONObject(0).put("lineNumber", null);
        JSON.parseObject(object.toJSONString(), Exception.class);
    }

    public static enum Type {

        A,
        B,
        C;}
}

