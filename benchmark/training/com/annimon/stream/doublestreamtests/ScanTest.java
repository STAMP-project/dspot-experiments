package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import com.annimon.stream.function.DoubleBinaryOperator;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class ScanTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testScan() {
        DoubleStream.of(1.1, 2.2, 3.3).scan(new DoubleBinaryOperator() {
            @Override
            public double applyAsDouble(double left, double right) {
                return left + right;
            }
        }).custom(assertElements(Matchers.arrayContaining(Matchers.closeTo(1.1, 1.0E-5), Matchers.closeTo(3.3, 1.0E-5), Matchers.closeTo(6.6, 1.0E-5))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testScanNonAssociative() {
        DoubleStream.of(1.0, 0.2, 0.3, 0.5).scan(new DoubleBinaryOperator() {
            @Override
            public double applyAsDouble(double value1, double value2) {
                return value1 / value2;
            }
        }).custom(assertElements(Matchers.is(Matchers.arrayContaining(Matchers.closeTo(1.0, 1.0E-5), Matchers.closeTo((1.0 / 0.2), 1.0E-5), Matchers.closeTo(((1.0 / 0.2) / 0.3), 1.0E-5), Matchers.closeTo((((1.0 / 0.2) / 0.3) / 0.5), 1.0E-5)))));
    }

    @Test
    public void testScanOnEmptyStream() {
        DoubleStream.empty().scan(new DoubleBinaryOperator() {
            @Override
            public double applyAsDouble(double left, double right) {
                return left + right;
            }
        }).custom(assertIsEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testScanWithIdentity() {
        DoubleStream.of(2.2, 3.3, 1.1).scan(1.1, new DoubleBinaryOperator() {
            @Override
            public double applyAsDouble(double left, double right) {
                return left + right;
            }
        }).custom(assertElements(Matchers.arrayContaining(Matchers.closeTo(1.1, 1.0E-5), Matchers.closeTo(3.3, 1.0E-5), Matchers.closeTo(6.6, 1.0E-5), Matchers.closeTo(7.7, 1.0E-5))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testScanWithIdentityNonAssociative() {
        DoubleStream.of(0.2, 0.3, 0.5).scan(1.0, new DoubleBinaryOperator() {
            @Override
            public double applyAsDouble(double value1, double value2) {
                return value1 / value2;
            }
        }).custom(assertElements(Matchers.arrayContaining(Matchers.closeTo(1.0, 1.0E-5), Matchers.closeTo((1.0 / 0.2), 1.0E-5), Matchers.closeTo(((1.0 / 0.2) / 0.3), 1.0E-5), Matchers.closeTo((((1.0 / 0.2) / 0.3) / 0.5), 1.0E-5))));
    }

    @Test
    public void testScanWithIdentityOnEmptyStream() {
        DoubleStream.empty().scan(10.09, new DoubleBinaryOperator() {
            @Override
            public double applyAsDouble(double left, double right) {
                return left + right;
            }
        }).custom(assertElements(Matchers.arrayContaining(10.09)));
    }
}

