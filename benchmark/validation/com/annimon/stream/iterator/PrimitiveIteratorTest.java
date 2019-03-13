package com.annimon.stream.iterator;


import java.util.NoSuchElementException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PrimitiveIteratorTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(PrimitiveIterator.class, hasOnlyPrivateConstructors());
    }

    @Test
    public void testOfIntHasNext() {
        Assert.assertFalse(new PrimitiveIterators.OfIntEmpty().hasNext());
        Assert.assertTrue(new PrimitiveIterators.OfInt().hasNext());
    }

    @Test
    public void testOfLongHasNext() {
        Assert.assertFalse(new PrimitiveIterators.OfLongEmpty().hasNext());
        Assert.assertTrue(new PrimitiveIterators.OfLong().hasNext());
    }

    @Test
    public void testOfDoubleHasNext() {
        Assert.assertFalse(new PrimitiveIterators.OfDoubleEmpty().hasNext());
        Assert.assertTrue(new PrimitiveIterators.OfDouble().hasNext());
    }

    @Test(expected = NoSuchElementException.class)
    public void testOfIntNext() {
        final PrimitiveIterators.OfInt iterator = new PrimitiveIterators.OfInt();
        Assert.assertEquals(iterator.nextInt(), 1);
        Assert.assertEquals(iterator.nextInt(), 2);
        new PrimitiveIterators.OfIntEmpty().nextInt();
    }

    @Test(expected = NoSuchElementException.class)
    public void testOfLongNext() {
        final PrimitiveIterators.OfLong iterator = new PrimitiveIterators.OfLong();
        Assert.assertEquals(iterator.nextLong(), 1);
        Assert.assertEquals(iterator.nextLong(), 2);
        new PrimitiveIterators.OfLongEmpty().nextLong();
    }

    @Test(expected = NoSuchElementException.class)
    public void testOfDoubleNext() {
        final PrimitiveIterators.OfDouble iterator = new PrimitiveIterators.OfDouble();
        Assert.assertThat(iterator.nextDouble(), Matchers.closeTo(1.01, 1.0E-5));
        Assert.assertThat(iterator.nextDouble(), Matchers.closeTo(2.02, 1.0E-5));
        new PrimitiveIterators.OfDoubleEmpty().nextDouble();
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
        final PrimitiveIterators.OfInt iterator = new PrimitiveIterators.OfInt();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(iterator.nextInt(), 1);
        Assert.assertEquals(iterator.nextInt(), 2);
        Assert.assertFalse(iterator.hasNext());
        Assert.assertFalse(iterator.hasNext());
        iterator.nextInt();
    }

    @Test(expected = NoSuchElementException.class)
    public void testOfLong() {
        final PrimitiveIterators.OfLong iterator = new PrimitiveIterators.OfLong();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(iterator.nextLong(), 1L);
        Assert.assertEquals(iterator.nextLong(), 2L);
        Assert.assertFalse(iterator.hasNext());
        Assert.assertFalse(iterator.hasNext());
        iterator.nextLong();
    }

    @Test(expected = NoSuchElementException.class)
    public void testOfDouble() {
        final PrimitiveIterators.OfDouble iterator = new PrimitiveIterators.OfDouble();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertThat(iterator.nextDouble(), Matchers.closeTo(1.01, 1.0E-5));
        Assert.assertThat(iterator.nextDouble(), Matchers.closeTo(2.02, 1.0E-5));
        Assert.assertFalse(iterator.hasNext());
        Assert.assertFalse(iterator.hasNext());
        iterator.nextDouble();
    }
}

