package com.jsoniter;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestGenerics extends TestCase {
    static {
        // JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_STRICTLY);
    }

    public void test_int_list() throws IOException {
        JsonIterator iter = JsonIterator.parse("[1,2,3]");
        List<Integer> val = iter.read(new TypeLiteral<java.util.ArrayList<Integer>>() {});
        Assert.assertArrayEquals(new Integer[]{ 1, 2, 3 }, val.toArray(new Integer[0]));
    }

    public void test_string_list() throws IOException {
        JsonIterator iter = JsonIterator.parse("['hello', 'world']".replace('\'', '"'));
        List<String> val = iter.read(new TypeLiteral<List<String>>() {});
        Assert.assertArrayEquals(new String[]{ "hello", "world" }, val.toArray(new String[0]));
    }

    public void test_linked_list() throws IOException {
        JsonIterator iter = JsonIterator.parse("['hello', 'world']".replace('\'', '"'));
        List<String> val = iter.read(new TypeLiteral<java.util.LinkedList<String>>() {});
        Assert.assertArrayEquals(new String[]{ "hello", "world" }, val.toArray(new String[0]));
    }

    public void test_string_set() throws IOException {
        JsonIterator iter = JsonIterator.parse("['hello']".replace('\'', '"'));
        Set<String> val = iter.read(new TypeLiteral<Set<String>>() {});
        Assert.assertArrayEquals(new String[]{ "hello" }, val.toArray(new String[0]));
    }

    public void test_string_map() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'hello': 'world'}".replace('\'', '"'));
        Map<String, String> val = iter.read(new TypeLiteral<Map<String, String>>() {});
        TestCase.assertEquals("world", val.get("hello"));
    }

    public void test_integer_map() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'hello': 1}".replace('\'', '"'));
        Map<String, Integer> val = iter.read(new TypeLiteral<Map<String, Integer>>() {});
        TestCase.assertEquals(Integer.valueOf(1), val.get("hello"));
    }

    public void test_list_of_list() throws Exception {
        JsonIterator iter = JsonIterator.parse("[[1,2],[3,4]]");
        List<List<Integer>> listOfList = iter.read(new TypeLiteral<List<List<Integer>>>() {});
        TestCase.assertEquals(Integer.valueOf(4), listOfList.get(1).get(1));
    }

    public void test_complex_object() throws IOException {
        JsonIterator iter = JsonIterator.parse("{'field1': 100, 'field2': [[1,2],[3,4]]}".replace('\'', '"'));
        ComplexObject val = iter.read(ComplexObject.class);
        TestCase.assertEquals(100, val.field1);
        TestCase.assertEquals(Integer.valueOf(4), val.field2.get(1).get(1));
    }

    public static class Class1<A, B> {
        public List<A> field1;

        public B[] field2;

        public List<B>[] field3;

        public List<A[]> field4;

        public List<Map<A, List<B>>> getField6() {
            return null;
        }

        public <T> T getField7() {
            return null;
        }

        public void setField8(List<A> a) {
        }
    }

    public static class Class2<C, D, E> extends TestGenerics.Class1<C, D> {
        public E field5;
    }

    public static class Class3 extends TestGenerics.Class2<String, Integer, Float> {}

    public void test_generic_super_class() throws IOException {
        ClassDescriptor desc = ClassDescriptor.getDecodingClassDescriptor(new ClassInfo(TestGenerics.Class3.class), true);
        Map<String, String> fieldDecoderCacheKeys = new HashMap<String, String>();
        for (Binding field : desc.allDecoderBindings()) {
            fieldDecoderCacheKeys.put(field.name, field.valueTypeLiteral.getDecoderCacheKey());
        }
        for (Binding field : ClassDescriptor.getEncodingClassDescriptor(new ClassInfo(TestGenerics.Class3.class), true).getters) {
            fieldDecoderCacheKeys.put(field.name, field.valueTypeLiteral.getDecoderCacheKey());
        }
        TestCase.assertTrue(fieldDecoderCacheKeys.get("field1").endsWith("decoder.java.util.List_java.lang.String"));
        TestCase.assertTrue(fieldDecoderCacheKeys.get("field2").endsWith("decoder.java.lang.Integer_array"));
        TestCase.assertTrue(fieldDecoderCacheKeys.get("field3").endsWith("decoder.java.util.List_java.lang.Integer_array"));
        TestCase.assertTrue(fieldDecoderCacheKeys.get("field4").endsWith("decoder.java.util.List_java.lang.String_array"));
        TestCase.assertTrue(fieldDecoderCacheKeys.get("field5").endsWith("decoder.java.lang.Float"));
        TestCase.assertTrue(fieldDecoderCacheKeys.get("field6").endsWith("decoder.java.util.List_java.util.Map_java.lang.String_java.util.List_java.lang.Integer"));
        TestCase.assertTrue(fieldDecoderCacheKeys.get("field7").endsWith("decoder.java.lang.Object"));
        TestCase.assertTrue(fieldDecoderCacheKeys.get("field8").endsWith("decoder.java.util.List_java.lang.String"));
    }

    public static class NetRes<T> {
        public int code;

        public String desc;

        public T results;
    }

    public static class User {
        public String name;

        public int age;
    }

    public void test_issue_103() {
        String json = "{'code':1, 'desc':'OK', 'results':{'name':'aaa', 'age':18}}".replace('\'', '\"');
        TestGenerics.NetRes res = JsonIterator.deserialize(json, new TypeLiteral<TestGenerics.NetRes<TestGenerics.User>>() {});
        TestCase.assertEquals(TestGenerics.User.class, res.results.getClass());
    }

    public static class TestObject7 {
        public List<?> field;
    }

    public void test_wildcard() throws IOException {
        TestGenerics.TestObject7 obj = JsonIterator.deserialize("{\"field\":[1]}", TestGenerics.TestObject7.class);
        TestCase.assertEquals(1, obj.field.get(0));
    }
}

