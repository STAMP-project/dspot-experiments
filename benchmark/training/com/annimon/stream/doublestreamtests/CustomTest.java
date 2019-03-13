package com.annimon.stream.doublestreamtests;


import com.annimon.stream.CustomOperators;
import com.annimon.stream.DoubleStream;
import com.annimon.stream.IntStream;
import com.annimon.stream.function.DoubleBinaryOperator;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class CustomTest {
    @Test(expected = NullPointerException.class)
    public void testCustom() {
        DoubleStream.empty().custom(null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCustomIntermediateOperator_Zip() {
        final DoubleBinaryOperator op = new DoubleBinaryOperator() {
            @Override
            public double applyAsDouble(double left, double right) {
                return left * right;
            }
        };
        DoubleStream s1 = DoubleStream.of(1.01, 2.02, 3.03);
        IntStream s2 = IntStream.range(2, 5);
        DoubleStream result = s1.custom(new CustomOperators.ZipWithIntStream(s2, op));
        Assert.assertThat(result, elements(Matchers.arrayContaining(Matchers.closeTo(2.02, 1.0E-5), Matchers.closeTo(6.06, 1.0E-5), Matchers.closeTo(12.12, 1.0E-5))));
    }

    @Test
    public void testCustomTerminalOperator_DoubleSummaryStatistics() {
        double[] result = DoubleStream.of(0.1, 0.02, 0.003).custom(new CustomOperators.DoubleSummaryStatistics());
        double count = result[0];
        double sum = result[1];
        double average = result[2];
        Assert.assertThat(count, Matchers.closeTo(3, 1.0E-4));
        Assert.assertThat(sum, Matchers.closeTo(0.123, 1.0E-4));
        Assert.assertThat(average, Matchers.closeTo(0.041, 1.0E-4));
    }
}

