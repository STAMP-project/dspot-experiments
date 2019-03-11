package com.annimon.stream.iterator;


import PrimitiveExtIterator.OfDouble;
import PrimitiveExtIterator.OfInt;
import PrimitiveExtIterator.OfLong;
import java.util.NoSuchElementException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PrimitiveExtIteratorTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(PrimitiveExtIterator.class, hasOnlyPrivateConstructors());
    }

    @Test
    public void testOfIntHasNext() {
        Assert.assertFalse(hasNext());
        Assert.assertTrue(hasNext());
    }

    @Test
    public void testOfLongHasNext() {
        Assert.assertFalse(hasNext());
        Assert.assertTrue(hasNext());
    }

    @Test
    public void testOfDoubleHasNext() {
        Assert.assertFalse(hasNext());
        Assert.assertTrue(hasNext());
    }

    @Test
    public void testOfIntNext() {
        final PrimitiveExtIteratorTest.PrimitiveExtIteratorOfIntImpl iterator = new PrimitiveExtIteratorTest.PrimitiveExtIteratorOfIntImpl();
        Assert.assertEquals(nextInt(), 1);
        Assert.assertEquals(nextInt(), 2);
    }

    @Test(expected = NoSuchElementException.class)
    public void testOfIntNextOnEmptyIterator() {
        nextInt();
    }

    @Test
    public void testOfLongNext() {
        final PrimitiveExtIteratorTest.PrimitiveExtIteratorOfLongImpl iterator = new PrimitiveExtIteratorTest.PrimitiveExtIteratorOfLongImpl();
        Assert.assertEquals(nextLong(), 1);
        Assert.assertEquals(nextLong(), 2);
    }

    @Test(expected = NoSuchElementException.class)
    public void testOfLongNextOnEmptyIterator() {
        nextLong();
    }

    @Test
    public void testOfDoubleNext() {
        final PrimitiveExtIteratorTest.PrimitiveExtIteratorOfDoubleImpl iterator = new PrimitiveExtIteratorTest.PrimitiveExtIteratorOfDoubleImpl();
        Assert.assertThat(nextDouble(), Matchers.closeTo(1.01, 1.0E-5));
        Assert.assertThat(nextDouble(), Matchers.closeTo(2.02, 1.0E-5));
    }

    @Test(expected = NoSuchElementException.class)
    public void testOfDoubleNextOnEmptyIterator() {
        nextDouble();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testOfIntRemove() {
        remove();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testOfLongRemove() {
        remove();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testOfDoubleRemove() {
        remove();
    }

    @Test(expected = NoSuchElementException.class)
    public void testOfInt() {
        final PrimitiveExtIteratorTest.PrimitiveExtIteratorOfIntImpl iterator = new PrimitiveExtIteratorTest.PrimitiveExtIteratorOfIntImpl();
        Assert.assertTrue(hasNext());
        Assert.assertTrue(hasNext());
        Assert.assertEquals(nextInt(), 1);
        Assert.assertEquals(nextInt(), 2);
        Assert.assertFalse(hasNext());
        Assert.assertFalse(hasNext());
        nextInt();
    }

    @Test(expected = NoSuchElementException.class)
    public void testOfLong() {
        final PrimitiveExtIteratorTest.PrimitiveExtIteratorOfLongImpl iterator = new PrimitiveExtIteratorTest.PrimitiveExtIteratorOfLongImpl();
        Assert.assertTrue(hasNext());
        Assert.assertTrue(hasNext());
        Assert.assertEquals(nextLong(), 1L);
        Assert.assertEquals(nextLong(), 2L);
        Assert.assertFalse(hasNext());
        Assert.assertFalse(hasNext());
        nextLong();
    }

    @Test(expected = NoSuchElementException.class)
    public void testOfDouble() {
        final PrimitiveExtIteratorTest.PrimitiveExtIteratorOfDoubleImpl iterator = new PrimitiveExtIteratorTest.PrimitiveExtIteratorOfDoubleImpl();
        Assert.assertTrue(hasNext());
        Assert.assertTrue(hasNext());
        Assert.assertThat(nextDouble(), Matchers.closeTo(1.01, 1.0E-5));
        Assert.assertThat(nextDouble(), Matchers.closeTo(2.02, 1.0E-5));
        Assert.assertFalse(hasNext());
        Assert.assertFalse(hasNext());
        nextDouble();
    }

    private class EmptyPrimitiveExtIteratorOfInt extends PrimitiveExtIterator.OfInt {
        @Override
        protected void nextIteration() {
            hasNext = false;
        }
    }

    private class PrimitiveExtIteratorOfIntImpl extends PrimitiveExtIterator.OfInt {
        @Override
        protected void nextIteration() {
            hasNext = (next) < 2;
            (next)++;
        }
    }

    private class EmptyPrimitiveExtIteratorOfLong extends PrimitiveExtIterator.OfLong {
        @Override
        protected void nextIteration() {
            hasNext = false;
        }
    }

    private class PrimitiveExtIteratorOfLongImpl extends PrimitiveExtIterator.OfLong {
        @Override
        protected void nextIteration() {
            hasNext = (next) < 2;
            (next)++;
        }
    }

    private class EmptyPrimitiveExtIteratorOfDouble extends PrimitiveExtIterator.OfDouble {
        @Override
        protected void nextIteration() {
            hasNext = false;
        }
    }

    private class PrimitiveExtIteratorOfDoubleImpl extends PrimitiveExtIterator.OfDouble {
        @Override
        protected void nextIteration() {
            hasNext = (next) < 2;
            next += 1.01;
        }
    }
}

