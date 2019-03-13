package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class OfDoubleTest {
    @Test
    public void testStreamOfDouble() {
        DoubleStream.of(1.234).custom(assertElements(Matchers.arrayContaining(1.234)));
    }
}

