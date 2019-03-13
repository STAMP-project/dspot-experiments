package com.annimon.stream.iterator;


import PrimitiveIndexedIterator.OfDouble;
import PrimitiveIndexedIterator.OfInt;
import PrimitiveIndexedIterator.OfLong;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PrimitiveIndexedIteratorTest {
    @Test
    public void testPrivateConstructor() throws Exception {
        Assert.assertThat(PrimitiveIndexedIterator.class, hasOnlyPrivateConstructors());
    }

    @Test
    public void testIntDefault() {
        PrimitiveIndexedIterator.OfInt iterator = new PrimitiveIndexedIterator.OfInt(new PrimitiveIterators.OfInt());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertThat(iterator.getIndex(), Matchers.is(0));
        Assert.assertThat(iterator.next(), Matchers.is(1));
        Assert.assertTrue(iterator.hasNext());
        Assert.assertThat(iterator.getIndex(), Matchers.is(1));
        Assert.assertThat(iterator.getIndex(), Matchers.is(1));
        Assert.assertThat(iterator.next(), Matchers.is(2));
        Assert.assertFalse(iterator.hasNext());
        Assert.assertThat(iterator.getIndex(), Matchers.is(2));
    }

    @Test
    public void testIntWithStartAndStep() {
        PrimitiveIndexedIterator.OfInt iterator = new PrimitiveIndexedIterator.OfInt(10, (-2), new PrimitiveIterators.OfInt());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertThat(iterator.getIndex(), Matchers.is(10));
        Assert.assertThat(iterator.next(), Matchers.is(1));
        Assert.assertTrue(iterator.hasNext());
        Assert.assertThat(iterator.getIndex(), Matchers.is(8));
        Assert.assertThat(iterator.getIndex(), Matchers.is(8));
        Assert.assertThat(iterator.next(), Matchers.is(2));
        Assert.assertFalse(iterator.hasNext());
        Assert.assertThat(iterator.getIndex(), Matchers.is(6));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIntRemove() {
        PrimitiveIndexedIterator.OfInt iterator = new PrimitiveIndexedIterator.OfInt(new PrimitiveIterators.OfInt());
        iterator.next();
        iterator.remove();
    }

    @Test
    public void testLongDefault() {
        PrimitiveIndexedIterator.OfLong iterator = new PrimitiveIndexedIterator.OfLong(new PrimitiveIterators.OfLong());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertThat(iterator.getIndex(), Matchers.is(0));
        Assert.assertThat(iterator.next(), Matchers.is(1L));
        Assert.assertTrue(iterator.hasNext());
        Assert.assertThat(iterator.getIndex(), Matchers.is(1));
        Assert.assertThat(iterator.getIndex(), Matchers.is(1));
        Assert.assertThat(iterator.next(), Matchers.is(2L));
        Assert.assertFalse(iterator.hasNext());
        Assert.assertThat(iterator.getIndex(), Matchers.is(2));
    }

    @Test
    public void testLongWithStartAndStep() {
        PrimitiveIndexedIterator.OfLong iterator = new PrimitiveIndexedIterator.OfLong(10, (-2), new PrimitiveIterators.OfLong());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertThat(iterator.getIndex(), Matchers.is(10));
        Assert.assertThat(iterator.next(), Matchers.is(1L));
        Assert.assertTrue(iterator.hasNext());
        Assert.assertThat(iterator.getIndex(), Matchers.is(8));
        Assert.assertThat(iterator.getIndex(), Matchers.is(8));
        Assert.assertThat(iterator.next(), Matchers.is(2L));
        Assert.assertFalse(iterator.hasNext());
        Assert.assertThat(iterator.getIndex(), Matchers.is(6));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testLongRemove() {
        PrimitiveIndexedIterator.OfLong iterator = new PrimitiveIndexedIterator.OfLong(new PrimitiveIterators.OfLong());
        iterator.next();
        iterator.remove();
    }

    @Test
    public void testDoubleDefault() {
        PrimitiveIndexedIterator.OfDouble iterator = new PrimitiveIndexedIterator.OfDouble(new PrimitiveIterators.OfDouble());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertThat(iterator.getIndex(), Matchers.is(0));
        Assert.assertThat(iterator.next(), Matchers.closeTo(1.01, 0.001));
        Assert.assertTrue(iterator.hasNext());
        Assert.assertThat(iterator.getIndex(), Matchers.is(1));
        Assert.assertThat(iterator.getIndex(), Matchers.is(1));
        Assert.assertThat(iterator.next(), Matchers.closeTo(2.02, 0.001));
        Assert.assertFalse(iterator.hasNext());
        Assert.assertThat(iterator.getIndex(), Matchers.is(2));
    }

    @Test
    public void testDoubleWithStartAndStep() {
        PrimitiveIndexedIterator.OfDouble iterator = new PrimitiveIndexedIterator.OfDouble(10, (-2), new PrimitiveIterators.OfDouble());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertThat(iterator.getIndex(), Matchers.is(10));
        Assert.assertThat(iterator.next(), Matchers.closeTo(1.01, 0.001));
        Assert.assertTrue(iterator.hasNext());
        Assert.assertThat(iterator.getIndex(), Matchers.is(8));
        Assert.assertThat(iterator.getIndex(), Matchers.is(8));
        Assert.assertThat(iterator.next(), Matchers.closeTo(2.02, 0.001));
        Assert.assertFalse(iterator.hasNext());
        Assert.assertThat(iterator.getIndex(), Matchers.is(6));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDoubleRemove() {
        PrimitiveIndexedIterator.OfDouble iterator = new PrimitiveIndexedIterator.OfDouble(new PrimitiveIterators.OfDouble());
        iterator.next();
        iterator.remove();
    }
}

