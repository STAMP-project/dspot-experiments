package com.jsoniter.output;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;


public class TestGenerics extends TestCase {
    static {
        // JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
    }

    private ByteArrayOutputStream baos;

    private JsonStream stream;

    public interface TestObject6Interface<A> {
        A getHello();
    }

    public static class TestObject6 implements TestGenerics.TestObject6Interface<Integer> {
        public Integer getHello() {
            return 0;
        }
    }

    public void test_inherited_getter_is_not_duplicate() throws IOException {
        TestGenerics.TestObject6 obj = new TestGenerics.TestObject6();
        stream.writeVal(obj);
        stream.close();
        TestCase.assertEquals("{\"hello\":0}", baos.toString());
    }

    public static class TestObject7 {
        public List<?> field;

        public Map<?, ?> field2;
    }

    public void test_wildcard() throws IOException {
        TestGenerics.TestObject7 obj = new TestGenerics.TestObject7();
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(1);
        obj.field = list;
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("hello", 1);
        obj.field2 = map;
        TestCase.assertEquals("{\"field\":[1],\"field2\":{\"hello\":1}}", JsonStream.serialize(obj));
    }
}

