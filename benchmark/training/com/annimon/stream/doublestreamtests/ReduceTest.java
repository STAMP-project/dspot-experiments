package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import com.annimon.stream.function.DoubleBinaryOperator;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class ReduceTest {
    @Test
    public void testReduceWithIdentity() {
        double result = DoubleStream.of(0.012, (-3.772), 3.039, 19.84, 100.0).reduce(0.0, new DoubleBinaryOperator() {
            @Override
            public double applyAsDouble(double left, double right) {
                return left + right;
            }
        });
        Assert.assertThat(result, Matchers.closeTo(119.119, 1.0E-4));
    }

    @Test
    public void testReduceWithIdentityOnEmptyStream() {
        double result = DoubleStream.empty().reduce(Math.PI, new DoubleBinaryOperator() {
            @Override
            public double applyAsDouble(double left, double right) {
                return left + right;
            }
        });
        Assert.assertThat(result, Matchers.closeTo(Math.PI, 1.0E-5));
    }

    @Test
    public void testReduce() {
        Assert.assertThat(DoubleStream.of(0.012, (-3.772), 3.039, 19.84, 100.0).reduce(new DoubleBinaryOperator() {
            @Override
            public double applyAsDouble(double left, double right) {
                return left + right;
            }
        }), hasValueThat(Matchers.closeTo(119.119, 1.0E-4)));
    }

    @Test
    public void testReduceOnEmptyStream() {
        Assert.assertThat(DoubleStream.empty().reduce(new DoubleBinaryOperator() {
            @Override
            public double applyAsDouble(double left, double right) {
                return left + right;
            }
        }), isEmpty());
    }
}

