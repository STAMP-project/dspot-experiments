package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class ConcatTest {
    @Test
    public void testStreamConcat() {
        DoubleStream a1 = DoubleStream.empty();
        DoubleStream b1 = DoubleStream.empty();
        Assert.assertThat(DoubleStream.concat(a1, b1), isEmpty());
        DoubleStream a2 = DoubleStream.of(10.123, Math.PI);
        DoubleStream b2 = DoubleStream.empty();
        DoubleStream.concat(a2, b2).custom(assertElements(Matchers.arrayContaining(10.123, Math.PI)));
        DoubleStream a3 = DoubleStream.of(10.123, Math.PI);
        DoubleStream b3 = DoubleStream.empty();
        DoubleStream.concat(a3, b3).custom(assertElements(Matchers.arrayContaining(10.123, Math.PI)));
        DoubleStream a4 = DoubleStream.of(10.123, Math.PI, (-1.0E11), (-1.0E8));
        DoubleStream b4 = DoubleStream.of(1.617, 9.81);
        DoubleStream.concat(a4, b4).custom(assertElements(Matchers.arrayContaining(10.123, Math.PI, (-1.0E11), (-1.0E8), 1.617, 9.81)));
    }

    @Test(expected = NullPointerException.class)
    public void testStreamConcatNullA() {
        DoubleStream.concat(null, DoubleStream.empty());
    }

    @Test(expected = NullPointerException.class)
    public void testStreamConcatNullB() {
        DoubleStream.concat(DoubleStream.empty(), null);
    }
}

