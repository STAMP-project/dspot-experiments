package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import com.annimon.stream.Functions;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class TakeUntilTest {
    @Test
    public void testTakeUntil() {
        DoubleStream.of(0.3, 2.2, 10.2, 30.234, 10.09, 80.0).takeUntil(Functions.greaterThan(Math.PI)).custom(assertElements(Matchers.arrayContaining(0.3, 2.2, 10.2)));
    }

    @Test
    public void testTakeUntilFirstMatch() {
        DoubleStream.of(11.2, 3.234, 0.09, 2.2, 80.0).takeUntil(Functions.greaterThan(Math.PI)).custom(assertElements(Matchers.arrayContaining(11.2)));
    }

    @Test
    public void testTakeUntilNoneMatch() {
        DoubleStream.of(1.2, 1.19, 0.09, 2.2).takeUntil(Functions.greaterThan(128)).custom(assertElements(Matchers.arrayContaining(1.2, 1.19, 0.09, 2.2)));
    }
}

