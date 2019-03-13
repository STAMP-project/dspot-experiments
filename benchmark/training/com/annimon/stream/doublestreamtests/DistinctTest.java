package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class DistinctTest {
    @Test
    public void testDistinct() {
        DoubleStream.of(0.09, 1.2, 0, 2.2, 0.09, 1.2, 3.2, 0.09).distinct().custom(assertElements(Matchers.arrayContaining(0.09, 1.2, 0.0, 2.2, 3.2)));
    }
}

