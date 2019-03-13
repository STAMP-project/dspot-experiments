package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import com.annimon.stream.function.DoubleFunction;
import java.util.NoSuchElementException;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class FlatMapTest {
    @Test
    public void testFlatMap() {
        DoubleFunction<DoubleStream> twicer = new DoubleFunction<DoubleStream>() {
            @Override
            public DoubleStream apply(double value) {
                return DoubleStream.of(value, value);
            }
        };
        DoubleStream.of(0.012, (-3.039), 100.0).flatMap(twicer).custom(assertElements(Matchers.arrayContaining(0.012, 0.012, (-3.039), (-3.039), 100.0, 100.0)));
        DoubleStream.of(0.012, (-3.039), 100.0).flatMap(new DoubleFunction<DoubleStream>() {
            @Override
            public DoubleStream apply(double value) {
                if (value < 0)
                    return DoubleStream.of(value);

                return null;
            }
        }).custom(assertElements(Matchers.arrayContaining((-3.039))));
        DoubleStream.of(0.012, (-3.039), 100.0).flatMap(new DoubleFunction<DoubleStream>() {
            @Override
            public DoubleStream apply(double value) {
                if (value < 0)
                    return DoubleStream.empty();

                return DoubleStream.of(value);
            }
        }).custom(assertElements(Matchers.arrayContaining(0.012, 100.0)));
    }

    @Test(expected = NoSuchElementException.class)
    public void testFlatMapIterator() {
        DoubleStream.empty().flatMap(new DoubleFunction<DoubleStream>() {
            @Override
            public DoubleStream apply(double value) {
                return DoubleStream.of(value);
            }
        }).iterator().nextDouble();
    }
}

