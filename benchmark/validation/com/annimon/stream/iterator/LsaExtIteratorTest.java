package com.annimon.stream.iterator;


import java.util.NoSuchElementException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class LsaExtIteratorTest {
    @Test
    public void testHasNext() {
        Assert.assertFalse(hasNext());
        Assert.assertTrue(hasNext());
    }

    @Test
    public void testNext() {
        final LsaExtIteratorTest.LsaExtIteratorImpl iterator = new LsaExtIteratorTest.LsaExtIteratorImpl();
        Assert.assertEquals("1", next());
        Assert.assertEquals("2", next());
    }

    @Test(expected = NoSuchElementException.class)
    public void testNextOnEmptyIterator() {
        next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() {
        remove();
    }

    @Test(expected = NoSuchElementException.class)
    public void testIterator() {
        final LsaExtIteratorTest.LsaExtIteratorImpl iterator = new LsaExtIteratorTest.LsaExtIteratorImpl();
        Assert.assertTrue(hasNext());
        Assert.assertTrue(hasNext());
        Assert.assertEquals("1", next());
        Assert.assertEquals("2", next());
        Assert.assertFalse(hasNext());
        Assert.assertFalse(hasNext());
        next();
    }

    @Test
    public void testReferenceToPreviousElement() {
        final LsaExtIteratorTest.LsaExtIteratorImpl iterator = new LsaExtIteratorTest.LsaExtIteratorImpl();
        Assert.assertEquals(next(), "1");
        Assert.assertEquals(next(), "2");
        Assert.assertThat(iterator.next, Matchers.is(Matchers.nullValue()));
    }

    public static class EmptyLsaExtIterator extends LsaExtIterator<String> {
        @Override
        public void nextIteration() {
            hasNext = false;
            next = "";
        }
    }

    public static class LsaExtIteratorImpl extends LsaExtIterator<String> {
        int index = 0;

        @Override
        public void nextIteration() {
            hasNext = (index) < 2;
            next = Integer.toString((++(index)));
        }
    }
}

