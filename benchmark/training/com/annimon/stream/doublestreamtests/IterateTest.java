package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import com.annimon.stream.function.DoublePredicate;
import com.annimon.stream.function.DoubleUnaryOperator;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class IterateTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testStreamIterate() {
        DoubleUnaryOperator operator = new DoubleUnaryOperator() {
            @Override
            public double applyAsDouble(double operand) {
                return operand + 0.01;
            }
        };
        DoubleStream.iterate(0.0, operator).limit(3).custom(assertElements(Matchers.arrayContaining(Matchers.closeTo(0.0, 1.0E-5), Matchers.closeTo(0.01, 1.0E-5), Matchers.closeTo(0.02, 1.0E-5))));
    }

    @Test(expected = NullPointerException.class)
    public void testStreamIterateNull() {
        DoubleStream.iterate(0, null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStreamIterateWithPredicate() {
        DoublePredicate condition = new DoublePredicate() {
            @Override
            public boolean test(double value) {
                return value < 0.2;
            }
        };
        DoubleUnaryOperator increment = new DoubleUnaryOperator() {
            @Override
            public double applyAsDouble(double t) {
                return t + 0.05;
            }
        };
        DoubleStream.iterate(0, condition, increment).custom(assertElements(Matchers.arrayContaining(Matchers.closeTo(0.0, 1.0E-5), Matchers.closeTo(0.05, 1.0E-5), Matchers.closeTo(0.1, 1.0E-5), Matchers.closeTo(0.15, 1.0E-5))));
    }
}

