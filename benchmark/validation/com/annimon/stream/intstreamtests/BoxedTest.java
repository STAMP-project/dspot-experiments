package com.annimon.stream.intstreamtests;


import com.annimon.stream.IntStream;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class BoxedTest {
    @Test
    public void testBoxed() {
        IntStream.of(1, 10, 20).boxed().custom(assertElements(Matchers.contains(1, 10, 20)));
    }
}

