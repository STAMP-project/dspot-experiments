package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class OfArrayTest {
    @Test
    public void testStreamOfDoubles() {
        DoubleStream.of(3.2, 2.8).custom(assertElements(Matchers.arrayContaining(3.2, 2.8)));
    }

    @Test(expected = NullPointerException.class)
    public void testStreamOfDoublesNull() {
        DoubleStream.of(((double[]) (null)));
    }

    @Test
    public void testStreamOfEmptyArray() {
        DoubleStream.of(new double[0]).custom(assertIsEmpty());
    }
}

