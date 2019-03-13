package com.alibaba.json.bvt.serializer;


import SerializerFeature.WriteEnumUsingName;
import SerializerFeature.WriteEnumUsingToString;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import java.io.IOException;
import java.io.Writer;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONSerializerTest2 extends TestCase {
    public void test_0() throws Exception {
        JSONSerializer serializer = new JSONSerializer();
        serializer.getMapping().clearSerializers();
        int size = JSONSerializerMapTest.size(serializer.getMapping());
        serializer.config(WriteEnumUsingToString, false);
        serializer.config(WriteEnumUsingName, false);
        serializer.write(JSONSerializerTest2.Type.A);
        Assert.assertTrue((size < (JSONSerializerMapTest.size(serializer.getMapping()))));
        Assert.assertEquals(Integer.toString(JSONSerializerTest2.Type.A.ordinal()), serializer.getWriter().toString());
    }

    public void test_1() throws Exception {
        JSONSerializer serializer = new JSONSerializer();
        serializer.config(WriteEnumUsingToString, false);
        serializer.config(WriteEnumUsingName, false);
        serializer.write(new JSONSerializerTest2.A(JSONSerializerTest2.Type.B));
        Assert.assertEquals((("{\"type\":" + (Integer.toString(JSONSerializerTest2.Type.B.ordinal()))) + "}"), serializer.getWriter().toString());
        JSONSerializerTest2.A a = JSON.parseObject(serializer.getWriter().toString(), JSONSerializerTest2.A.class);
        Assert.assertEquals(a.getType(), JSONSerializerTest2.Type.B);
    }

    public void test_2() throws Exception {
        JSONSerializer serializer = new JSONSerializer();
        serializer.write(new JSONSerializerTest2.C());
        Assert.assertEquals("{}", serializer.getWriter().toString());
    }

    public void test_3() throws Exception {
        JSONSerializer serializer = new JSONSerializer();
        serializer.config(WriteEnumUsingToString, true);
        serializer.write(new JSONSerializerTest2.A(JSONSerializerTest2.Type.B));
        Assert.assertEquals("{\"type\":\"B\"}", serializer.getWriter().toString());
        JSONSerializerTest2.A a = JSON.parseObject(serializer.getWriter().toString(), JSONSerializerTest2.A.class);
        Assert.assertEquals(a.getType(), JSONSerializerTest2.Type.B);
    }

    public void test_error() throws Exception {
        Exception error = null;
        try {
            JSONSerializer.write(new Writer() {
                @Override
                public void write(char[] cbuf, int off, int len) throws IOException {
                    throw new IOException();
                }

                @Override
                public void flush() throws IOException {
                    throw new IOException();
                }

                @Override
                public void close() throws IOException {
                    throw new IOException();
                }
            }, ((Object) ("abc")));
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static enum Type {

        A,
        B;}

    public static class A {
        private JSONSerializerTest2.Type type;

        public A() {
        }

        public A(JSONSerializerTest2.Type type) {
            super();
            this.type = type;
        }

        public JSONSerializerTest2.Type getType() {
            return type;
        }

        public void setType(JSONSerializerTest2.Type type) {
            this.type = type;
        }
    }

    public static class C {}
}

