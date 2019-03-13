package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class AverageTest {
    @Test
    public void testAverage() {
        Assert.assertThat(DoubleStream.of(0.1, 0.02, 0.003).average(), hasValueThat(Matchers.closeTo(0.041, 1.0E-5)));
        Assert.assertThat(DoubleStream.empty().average(), isEmpty());
    }
}

