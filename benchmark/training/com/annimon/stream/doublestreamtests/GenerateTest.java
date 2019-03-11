package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import com.annimon.stream.function.DoubleSupplier;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class GenerateTest {
    @Test
    public void testStreamGenerate() {
        DoubleStream stream = DoubleStream.generate(new DoubleSupplier() {
            @Override
            public double getAsDouble() {
                return 1.234;
            }
        });
        Assert.assertThat(stream.limit(3), elements(Matchers.arrayContaining(1.234, 1.234, 1.234)));
    }

    @Test(expected = NullPointerException.class)
    public void testStreamGenerateNull() {
        DoubleStream.generate(null);
    }
}

