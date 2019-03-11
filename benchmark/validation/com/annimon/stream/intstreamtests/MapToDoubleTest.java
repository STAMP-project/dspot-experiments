package com.annimon.stream.intstreamtests;


import com.annimon.stream.IntStream;
import com.annimon.stream.function.IntToDoubleFunction;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class MapToDoubleTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testMapToDouble() {
        IntStream.rangeClosed(2, 4).mapToDouble(new IntToDoubleFunction() {
            @Override
            public double applyAsDouble(int value) {
                return value / 10.0;
            }
        }).custom(assertElements(Matchers.arrayContaining(Matchers.closeTo(0.2, 1.0E-5), Matchers.closeTo(0.3, 1.0E-5), Matchers.closeTo(0.4, 1.0E-5))));
    }
}

