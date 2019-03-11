package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import com.annimon.stream.Functions;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class DropWhileTest {
    @Test
    public void testDropWhile() {
        DoubleStream.of(10.2, 30.234, 10.09, 2.2, 80.0).dropWhile(Functions.greaterThan(Math.PI)).custom(assertElements(Matchers.arrayContaining(2.2, 80.0)));
    }

    @Test
    public void testDropWhileNonFirstMatch() {
        DoubleStream.of(1.2, 3.234, 0.09, 2.2, 80.0).dropWhile(Functions.greaterThan(Math.PI)).custom(assertElements(Matchers.arrayContaining(1.2, 3.234, 0.09, 2.2, 80.0)));
    }

    @Test
    public void testDropWhileAllMatch() {
        DoubleStream.of(10.2, 30.234, 80.0).dropWhile(Functions.greaterThan(Math.PI)).custom(assertIsEmpty());
    }
}

