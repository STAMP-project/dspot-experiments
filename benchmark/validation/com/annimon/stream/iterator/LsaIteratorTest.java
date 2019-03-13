package com.annimon.stream.iterator;


import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;


public class LsaIteratorTest {
    @Test
    public void testHasNext() {
        Assert.assertFalse(new LsaIteratorTest.EmptyLsaIterator().hasNext());
        Assert.assertTrue(new LsaIteratorTest.LsaIteratorImpl().hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testNext() {
        final LsaIteratorTest.LsaIteratorImpl iterator = new LsaIteratorTest.LsaIteratorImpl();
        Assert.assertEquals(next(), "1");
        Assert.assertEquals(next(), "2");
        next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() {
        remove();
    }

    @Test(expected = NoSuchElementException.class)
    public void testIterator() {
        final LsaIteratorTest.LsaIteratorImpl iterator = new LsaIteratorTest.LsaIteratorImpl();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(next(), "1");
        Assert.assertEquals(next(), "2");
        Assert.assertFalse(iterator.hasNext());
        Assert.assertFalse(iterator.hasNext());
        next();
    }

    public static class EmptyLsaIterator extends LsaIterator<String> {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public String nextIteration() {
            return "";
        }
    }

    public static class LsaIteratorImpl extends LsaIterator<String> {
        int index = 0;

        @Override
        public boolean hasNext() {
            return (index) < 2;
        }

        @Override
        public String nextIteration() {
            return Integer.toString((++(index)));
        }
    }
}

