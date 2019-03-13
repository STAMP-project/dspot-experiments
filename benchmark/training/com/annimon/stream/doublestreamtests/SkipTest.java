package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class SkipTest {
    @Test
    public void testSkip() {
        DoubleStream.of(0.1, 0.2, 0.3).skip(2).custom(assertElements(Matchers.arrayContaining(0.3)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSkipNegative() {
        DoubleStream.of(0.1, 2.345).skip((-2)).count();
    }

    @Test
    public void testSkipZero() {
        DoubleStream.of(0.1, 0.2, 0.3).skip(0).custom(assertElements(Matchers.arrayContaining(0.1, 0.2, 0.3)));
    }

    @Test
    public void testSkipMoreThanCount() {
        DoubleStream.of(0.1, 0.2, 0.3).skip(5).custom(assertIsEmpty());
    }
}

