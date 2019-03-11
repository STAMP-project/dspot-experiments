package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import com.annimon.stream.function.DoubleUnaryOperator;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class MapTest {
    @Test
    public void testMap() {
        DoubleUnaryOperator negator = new DoubleUnaryOperator() {
            @Override
            public double applyAsDouble(double operand) {
                return -operand;
            }
        };
        DoubleStream.of(0.012, 3.039, 100.0).map(negator).custom(assertElements(Matchers.arrayContaining((-0.012), (-3.039), (-100.0))));
        DoubleStream.empty().map(negator).custom(assertIsEmpty());
    }
}

