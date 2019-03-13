package com.annimon.stream.longstreamtests;


import com.annimon.stream.LongStream;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class BoxedTest {
    @Test
    public void testBoxed() {
        LongStream.of(10L, 20L, 30L).boxed().custom(assertElements(Matchers.contains(10L, 20L, 30L)));
    }
}

