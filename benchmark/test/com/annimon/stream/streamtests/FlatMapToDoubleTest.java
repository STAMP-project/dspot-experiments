package com.annimon.stream.streamtests;


import com.annimon.stream.DoubleStream;
import com.annimon.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class FlatMapToDoubleTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testFlatMapToDouble() {
        Stream.of(2, 4).flatMapToDouble(new com.annimon.stream.function.Function<Integer, DoubleStream>() {
            @Override
            public DoubleStream apply(Integer t) {
                return DoubleStream.of((t / 10.0), (t / 20.0));
            }
        }).custom(assertElements(Matchers.array(Matchers.closeTo(0.2, 1.0E-4), Matchers.closeTo(0.1, 1.0E-4), Matchers.closeTo(0.4, 1.0E-4), Matchers.closeTo(0.2, 1.0E-4))));
    }
}

