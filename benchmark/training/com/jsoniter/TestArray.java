package com.jsoniter;


import com.jsoniter.any.Any;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestArray extends TestCase {
    static {
        // JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_WITH_HASH);
    }

    public void test_empty_array() throws IOException {
        JsonIterator iter = JsonIterator.parse("[]");
        TestCase.assertFalse(iter.readArray());
        iter.reset(iter.buf);
        int[] array = iter.read(int[].class);
        TestCase.assertEquals(0, array.length);
        iter.reset(iter.buf);
        List<String> list = iter.read(new com.jsoniter.spi.TypeLiteral<List<String>>() {});
        TestCase.assertEquals(0, list.size());
        iter.reset(iter.buf);
        Any any = iter.readAny();
        TestCase.assertEquals(0, any.size());
    }

    public void test_one_element() throws IOException {
        JsonIterator iter = JsonIterator.parse("[1]");
        TestCase.assertTrue(iter.readArray());
        TestCase.assertEquals(1, iter.readInt());
        TestCase.assertFalse(iter.readArray());
        iter.reset(iter.buf);
        int[] array = iter.read(int[].class);
        Assert.assertArrayEquals(new int[]{ 1 }, array);
        iter.reset(iter.buf);
        List<Integer> list = iter.read(new com.jsoniter.spi.TypeLiteral<List<Integer>>() {});
        TestCase.assertEquals(Arrays.asList(1), list);
        iter.reset(iter.buf);
        Assert.assertArrayEquals(new Object[]{ 1 }, iter.read(Object[].class));
        iter.reset(iter.buf);
        TestCase.assertEquals(1, iter.read(Any[].class)[0].toInt());
        iter.reset(iter.buf);
        TestCase.assertEquals(1, iter.readAny().toInt(0));
        iter.reset(iter.buf);
        final List<Integer> values = new ArrayList<Integer>();
        iter.readArrayCB(new JsonIterator.ReadArrayCallback() {
            @Override
            public boolean handle(JsonIterator iter, Object attachment) throws IOException {
                values.add(iter.readInt());
                return true;
            }
        }, null);
        TestCase.assertEquals(Arrays.asList(1), values);
    }

    public void test_two_elements() throws IOException {
        JsonIterator iter = JsonIterator.parse(" [ 1 , 2 ] ");
        TestCase.assertTrue(iter.readArray());
        TestCase.assertEquals(1, iter.readInt());
        TestCase.assertTrue(iter.readArray());
        TestCase.assertEquals(2, iter.readInt());
        TestCase.assertFalse(iter.readArray());
        iter.reset(iter.buf);
        int[] array = iter.read(int[].class);
        Assert.assertArrayEquals(new int[]{ 1, 2 }, array);
        iter.reset(iter.buf);
        List<Integer> list = iter.read(new com.jsoniter.spi.TypeLiteral<List<Integer>>() {});
        TestCase.assertEquals(Arrays.asList(1, 2), list);
        iter.reset(iter.buf);
        Assert.assertArrayEquals(new Object[]{ 1, 2 }, iter.read(Object[].class));
        iter.reset(iter.buf);
        TestCase.assertEquals(1, iter.read(Any[].class)[0].toInt());
        iter.reset(iter.buf);
        TestCase.assertEquals(1, iter.readAny().toInt(0));
        iter = JsonIterator.parse(" [ 1 , null, 2 ] ");
        TestCase.assertEquals(Arrays.asList(1, null, 2), iter.read());
    }

    public void test_three_elements() throws IOException {
        JsonIterator iter = JsonIterator.parse(" [ 1 , 2, 3 ] ");
        TestCase.assertTrue(iter.readArray());
        TestCase.assertEquals(1, iter.readInt());
        TestCase.assertTrue(iter.readArray());
        TestCase.assertEquals(2, iter.readInt());
        TestCase.assertTrue(iter.readArray());
        TestCase.assertEquals(3, iter.readInt());
        TestCase.assertFalse(iter.readArray());
        iter.reset(iter.buf);
        int[] array = iter.read(int[].class);
        Assert.assertArrayEquals(new int[]{ 1, 2, 3 }, array);
        iter.reset(iter.buf);
        List<Integer> list = iter.read(new com.jsoniter.spi.TypeLiteral<List<Integer>>() {});
        TestCase.assertEquals(Arrays.asList(1, 2, 3), list);
        iter.reset(iter.buf);
        Assert.assertArrayEquals(new Object[]{ 1, 2, 3 }, iter.read(Object[].class));
        iter.reset(iter.buf);
        TestCase.assertEquals(1, iter.read(Any[].class)[0].toInt());
        iter.reset(iter.buf);
        TestCase.assertEquals(1, iter.readAny().toInt(0));
    }

    public void test_four_elements() throws IOException {
        JsonIterator iter = JsonIterator.parse(" [ 1 , 2, 3, 4 ] ");
        TestCase.assertTrue(iter.readArray());
        TestCase.assertEquals(1, iter.readInt());
        TestCase.assertTrue(iter.readArray());
        TestCase.assertEquals(2, iter.readInt());
        TestCase.assertTrue(iter.readArray());
        TestCase.assertEquals(3, iter.readInt());
        TestCase.assertTrue(iter.readArray());
        TestCase.assertEquals(4, iter.readInt());
        TestCase.assertFalse(iter.readArray());
        iter.reset(iter.buf);
        int[] array = iter.read(int[].class);
        Assert.assertArrayEquals(new int[]{ 1, 2, 3, 4 }, array);
        iter.reset(iter.buf);
        List<Integer> list = iter.read(new com.jsoniter.spi.TypeLiteral<List<Integer>>() {});
        TestCase.assertEquals(Arrays.asList(1, 2, 3, 4), list);
        iter.reset(iter.buf);
        Assert.assertArrayEquals(new Object[]{ 1, 2, 3, 4 }, iter.read(Object[].class));
        iter.reset(iter.buf);
        TestCase.assertEquals(1, iter.read(Any[].class)[0].toInt());
        iter.reset(iter.buf);
        TestCase.assertEquals(1, iter.readAny().toInt(0));
    }

    public void test_five_elements() throws IOException {
        JsonIterator iter = JsonIterator.parse(" [ 1 , 2, 3, 4, 5  ] ");
        TestCase.assertTrue(iter.readArray());
        TestCase.assertEquals(1, iter.readInt());
        TestCase.assertTrue(iter.readArray());
        TestCase.assertEquals(2, iter.readInt());
        TestCase.assertTrue(iter.readArray());
        TestCase.assertEquals(3, iter.readInt());
        TestCase.assertTrue(iter.readArray());
        TestCase.assertEquals(4, iter.readInt());
        TestCase.assertTrue(iter.readArray());
        TestCase.assertEquals(5, iter.readInt());
        TestCase.assertFalse(iter.readArray());
        iter.reset(iter.buf);
        int[] array = iter.read(int[].class);
        Assert.assertArrayEquals(new int[]{ 1, 2, 3, 4, 5 }, array);
        iter.reset(iter.buf);
        List<Integer> list = iter.read(new com.jsoniter.spi.TypeLiteral<List<Integer>>() {});
        TestCase.assertEquals(Arrays.asList(1, 2, 3, 4, 5), list);
        iter.reset(iter.buf);
        Assert.assertArrayEquals(new Object[]{ 1, 2, 3, 4, 5 }, iter.read(Object[].class));
        iter.reset(iter.buf);
        TestCase.assertEquals(1, iter.read(Any[].class)[0].toInt());
        iter.reset(iter.buf);
        TestCase.assertEquals(1, iter.readAny().toInt(0));
    }

    public void test_null() throws IOException {
        JsonIterator iter = JsonIterator.parse("null");
        TestCase.assertNull(iter.read(double[].class));
    }

    public void test_boolean_array() throws IOException {
        JsonIterator iter = JsonIterator.parse("[true, false, true]");
        Assert.assertArrayEquals(new boolean[]{ true, false, true }, iter.read(boolean[].class));
    }

    public void test_iterator() throws IOException {
        Any any = JsonIterator.deserialize("[1,2,3,4]");
        Iterator<Any> iter = any.iterator();
        TestCase.assertEquals(1, iter.next().toInt());
        iter = any.iterator();
        TestCase.assertEquals(1, iter.next().toInt());
        TestCase.assertEquals(2, iter.next().toInt());
        iter = any.iterator();
        TestCase.assertEquals(1, iter.next().toInt());
        TestCase.assertEquals(2, iter.next().toInt());
        TestCase.assertEquals(3, iter.next().toInt());
        iter = any.iterator();
        TestCase.assertEquals(1, iter.next().toInt());
        TestCase.assertEquals(2, iter.next().toInt());
        TestCase.assertEquals(3, iter.next().toInt());
        TestCase.assertEquals(4, iter.next().toInt());
        TestCase.assertFalse(iter.hasNext());
    }

    public void test_array_lazy_any_to_string() {
        Any any = JsonIterator.deserialize("[1,2,3]");
        any.asList().add(Any.wrap(4));
        TestCase.assertEquals("[1,2,3,4]", any.toString());
    }
}

