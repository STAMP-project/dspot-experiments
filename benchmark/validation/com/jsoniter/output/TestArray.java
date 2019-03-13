package com.jsoniter.output;


import EncodingMode.DYNAMIC_MODE;
import EncodingMode.REFLECTION_MODE;
import com.jsoniter.spi.Config;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import junit.framework.TestCase;


public class TestArray extends TestCase {
    static {
        // JsonStream.setMode(EncodingMode.DYNAMIC_MODE);
    }

    private ByteArrayOutputStream baos;

    private JsonStream stream;

    public void test_gen_array() throws IOException {
        stream.writeVal(new String[]{ "hello", "world" });
        stream.close();
        TestCase.assertEquals("['hello','world']".replace('\'', '"'), baos.toString());
    }

    public void test_collection() throws IOException {
        ArrayList list = new ArrayList();
        list.add("hello");
        list.add("world");
        stream.writeVal(new com.jsoniter.spi.TypeLiteral<java.util.List<String>>() {}, list);
        stream.close();
        TestCase.assertEquals("['hello','world']".replace('\'', '"'), baos.toString());
    }

    public void test_collection_without_type() throws IOException {
        ArrayList list = new ArrayList();
        list.add("hello");
        list.add("world");
        stream.writeVal(list);
        stream.close();
        TestCase.assertEquals("['hello','world']".replace('\'', '"'), baos.toString());
    }

    public void test_empty_array() throws IOException {
        stream.writeVal(new String[0]);
        stream.close();
        TestCase.assertEquals("[]".replace('\'', '"'), baos.toString());
    }

    public void test_null_array() throws IOException {
        stream.writeVal(new com.jsoniter.spi.TypeLiteral<String[]>() {}, null);
        stream.close();
        TestCase.assertEquals("null".replace('\'', '"'), baos.toString());
    }

    public void test_empty_collection() throws IOException {
        stream.writeVal(new ArrayList());
        stream.close();
        TestCase.assertEquals("[]".replace('\'', '"'), baos.toString());
    }

    public void test_null_collection() throws IOException {
        stream.writeVal(new com.jsoniter.spi.TypeLiteral<ArrayList>() {}, null);
        stream.close();
        TestCase.assertEquals("null".replace('\'', '"'), baos.toString());
    }

    public static class TestObject1 {
        public java.util.List<String> field1;
    }

    public void test_list_of_objects() throws IOException {
        TestArray.TestObject1 obj = new TestArray.TestObject1();
        obj.field1 = Arrays.asList("a", "b");
        stream.writeVal(new com.jsoniter.spi.TypeLiteral<java.util.List<TestArray.TestObject1>>() {}, Arrays.asList(obj));
        stream.close();
        TestCase.assertEquals("[{\"field1\":[\"a\",\"b\"]}]", baos.toString());
    }

    public void test_array_of_null() throws IOException {
        stream.writeVal(new TestArray.TestObject1[1]);
        stream.close();
        TestCase.assertEquals("[null]", baos.toString());
    }

    public void test_list_of_null() throws IOException {
        TestArray.TestObject1 obj = new TestArray.TestObject1();
        obj.field1 = Arrays.asList("a", "b");
        ArrayList<TestArray.TestObject1> list = new ArrayList<TestArray.TestObject1>();
        list.add(null);
        stream.writeVal(new com.jsoniter.spi.TypeLiteral<java.util.List<TestArray.TestObject1>>() {}, list);
        stream.close();
        TestCase.assertEquals("[null]", baos.toString());
    }

    public void test_hash_set() throws IOException {
        TestCase.assertEquals("[]", JsonStream.serialize(new HashSet<Integer>()));
        HashSet<Integer> set = new HashSet<Integer>();
        set.add(1);
        TestCase.assertEquals("[1]", JsonStream.serialize(set));
    }

    public void test_arrays_as_list() throws IOException {
        TestCase.assertEquals("[1,2,3]", JsonStream.serialize(Arrays.asList(1, 2, 3)));
    }

    public void test_default_empty_collection() throws IOException {
        TestCase.assertEquals("[]", JsonStream.serialize(Collections.emptySet()));
    }

    public void test_indention() {
        Config cfg = new Config.Builder().encodingMode(REFLECTION_MODE).indentionStep(2).build();
        TestCase.assertEquals(("[\n" + (("  1,\n" + "  2\n") + "]")), JsonStream.serialize(cfg, new int[]{ 1, 2 }));
        cfg = new Config.Builder().encodingMode(DYNAMIC_MODE).indentionStep(2).build();
        TestCase.assertEquals(("[\n" + (("  1,\n" + "  2\n") + "]")), JsonStream.serialize(cfg, new int[]{ 1, 2 }));
    }

    public void test_indention_with_empty_array() {
        Config cfg = new Config.Builder().encodingMode(REFLECTION_MODE).indentionStep(2).build();
        TestCase.assertEquals("[]", JsonStream.serialize(cfg, new int[]{  }));
        cfg = new Config.Builder().encodingMode(DYNAMIC_MODE).indentionStep(2).build();
        TestCase.assertEquals("[]", JsonStream.serialize(cfg, new int[]{  }));
    }
}

