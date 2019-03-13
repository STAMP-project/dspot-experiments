package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class BoxedTest {
    @Test
    public void testBoxed() {
        DoubleStream.of(0.1, 0.2, 0.3).boxed().custom(assertElements(Matchers.contains(0.1, 0.2, 0.3)));
    }
}

