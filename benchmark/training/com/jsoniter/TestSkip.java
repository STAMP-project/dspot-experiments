package com.jsoniter;


import java.io.IOException;
import junit.framework.TestCase;


public class TestSkip extends TestCase {
    public void test_skip_number() throws IOException {
        JsonIterator iter = JsonIterator.parse("[1,2]");
        TestCase.assertTrue(iter.readArray());
        iter.skip();
        TestCase.assertTrue(iter.readArray());
        TestCase.assertEquals(2, iter.readInt());
        TestCase.assertFalse(iter.readArray());
    }

    public void test_skip_string() throws IOException {
        JsonIterator iter = JsonIterator.parse("['hello',2]".replace('\'', '"'));
        TestCase.assertTrue(iter.readArray());
        iter.skip();
        TestCase.assertTrue(iter.readArray());
        TestCase.assertEquals(2, iter.readInt());
        TestCase.assertFalse(iter.readArray());
    }

    public void test_skip_object() throws IOException {
        JsonIterator iter = JsonIterator.parse("[{'hello': {'world': 'a'}},2]".replace('\'', '"'));
        TestCase.assertTrue(iter.readArray());
        iter.skip();
        TestCase.assertTrue(iter.readArray());
        TestCase.assertEquals(2, iter.readInt());
        TestCase.assertFalse(iter.readArray());
    }

    public void test_skip_array() throws IOException {
        JsonIterator iter = JsonIterator.parse("[ [1,  3] ,2]".replace('\'', '"'));
        TestCase.assertTrue(iter.readArray());
        iter.skip();
        TestCase.assertTrue(iter.readArray());
        TestCase.assertEquals(2, iter.readInt());
        TestCase.assertFalse(iter.readArray());
    }

    public void test_skip_nested() throws IOException {
        JsonIterator iter = JsonIterator.parse("[ [1, {'a': ['b'] },  3] ,2]".replace('\'', '"'));
        TestCase.assertTrue(iter.readArray());
        iter.skip();
        TestCase.assertTrue(iter.readArray());
        TestCase.assertEquals(2, iter.readInt());
        TestCase.assertFalse(iter.readArray());
    }
}

