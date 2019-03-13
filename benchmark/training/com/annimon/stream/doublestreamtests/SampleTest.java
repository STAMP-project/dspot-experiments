package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class SampleTest {
    @Test
    public void testSample() {
        DoubleStream.of(1.2, 3.234, 0.09, 2.2, 80.0).sample(2).custom(assertElements(Matchers.arrayContaining(1.2, 0.09, 80.0)));
    }

    @Test
    public void testSampleWithStep1() {
        DoubleStream.of(1.2, 3.234, 0.09, 2.2, 80.0).sample(1).custom(assertElements(Matchers.arrayContaining(1.2, 3.234, 0.09, 2.2, 80.0)));
    }

    @Test(expected = IllegalArgumentException.class, timeout = 1000)
    public void testSampleWithNegativeStep() {
        DoubleStream.of(1.2, 3.234).sample((-1)).count();
    }
}

