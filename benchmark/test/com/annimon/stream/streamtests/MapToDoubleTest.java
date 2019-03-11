package com.annimon.stream.streamtests;


import com.annimon.stream.Stream;
import com.annimon.stream.function.ToDoubleFunction;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class MapToDoubleTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testMapToDouble() {
        final ToDoubleFunction<String> stringToDouble = new ToDoubleFunction<String>() {
            @Override
            public double applyAsDouble(String t) {
                return Double.parseDouble(t);
            }
        };
        Stream.of("1.23", "4.56789", "10.1112").mapToDouble(stringToDouble).custom(assertElements(Matchers.arrayContaining(Matchers.closeTo(1.23, 1.0E-6), Matchers.closeTo(4.56789, 1.0E-6), Matchers.closeTo(10.1112, 1.0E-6))));
    }
}

