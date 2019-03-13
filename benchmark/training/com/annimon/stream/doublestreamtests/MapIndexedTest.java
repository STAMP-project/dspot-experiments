package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import com.annimon.stream.function.IndexedDoubleUnaryOperator;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class MapIndexedTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testMapIndexed() {
        DoubleStream.of(0.1, 0.3, 0.8, 1.2).mapIndexed(new IndexedDoubleUnaryOperator() {
            @Override
            public double applyAsDouble(int index, double value) {
                return index * value;
            }
        }).custom(assertElements(// (0 * 0.1)
        // (1 * 0.3)
        // (2 * 0.8)
        // (3 * 1.2)
        Matchers.arrayContaining(Matchers.closeTo(0, 0.001), Matchers.closeTo(0.3, 0.001), Matchers.closeTo(1.6, 0.001), Matchers.closeTo(3.6, 0.001))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMapIndexedWithStartAndStep() {
        DoubleStream.of(0.1, 0.3, 0.8, 1.2).mapIndexed(4, (-2), new IndexedDoubleUnaryOperator() {
            @Override
            public double applyAsDouble(int index, double value) {
                return index * value;
            }
        }).custom(assertElements(// (4 * 0.1)
        // (2 * 0.3)
        // (0 * 0.8)
        // (-2 * 1.2)
        Matchers.arrayContaining(Matchers.closeTo(0.4, 0.001), Matchers.closeTo(0.6, 0.001), Matchers.closeTo(0, 0.001), Matchers.closeTo((-2.4), 0.001))));
    }
}

