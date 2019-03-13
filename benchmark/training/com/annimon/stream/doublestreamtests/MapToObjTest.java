package com.annimon.stream.doublestreamtests;


import com.annimon.stream.DoubleStream;
import com.annimon.stream.function.DoubleFunction;
import com.annimon.stream.test.hamcrest.StreamMatcher;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class MapToObjTest {
    @Test
    public void testMapToObj() {
        DoubleFunction<String> doubleToString = new DoubleFunction<String>() {
            @Override
            public String apply(double value) {
                return Double.toString(value);
            }
        };
        DoubleStream.of(1.0, 2.12, 3.234).mapToObj(doubleToString).custom(assertElements(Matchers.contains("1.0", "2.12", "3.234")));
        DoubleStream.empty().mapToObj(doubleToString).custom(StreamMatcher.<String>assertIsEmpty());
    }
}

