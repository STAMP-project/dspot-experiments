package com.annimon.stream.iterator;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


public class LazyIteratorTest {
    @Test
    public void testHasNext() {
        Assert.assertFalse(LazyIteratorTest.newIteratorEmpty().hasNext());
        Assert.assertTrue(LazyIteratorTest.newIterator().hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testNext() {
        final LazyIterator<String> iterator = LazyIteratorTest.newIterator();
        Assert.assertEquals(iterator.next(), "1");
        Assert.assertEquals(iterator.next(), "2");
        LazyIteratorTest.newIteratorEmpty().next();
    }

    @Test
    public void testRemove() {
        final List<String> list = new LinkedList<String>();
        list.addAll(Arrays.asList("1", "2"));
        final LazyIterator<String> iterator = new LazyIterator<String>(list);
        Assert.assertEquals(iterator.next(), "1");
        iterator.remove();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(iterator.next(), "2");
        iterator.remove();
        Assert.assertFalse(iterator.hasNext());
        Assert.assertTrue(list.isEmpty());
    }

    @Test(expected = NoSuchElementException.class)
    public void testIterator() {
        final LazyIterator<String> iterator = LazyIteratorTest.newIterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(iterator.next(), "1");
        Assert.assertEquals(iterator.next(), "2");
        Assert.assertFalse(iterator.hasNext());
        Assert.assertFalse(iterator.hasNext());
        iterator.next();
    }
}

