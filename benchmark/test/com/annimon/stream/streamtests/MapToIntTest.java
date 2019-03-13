package com.annimon.stream.streamtests;


import com.annimon.stream.Stream;
import com.annimon.stream.function.ToIntFunction;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class MapToIntTest {
    @Test
    public void testMapToInt() {
        final ToIntFunction<String> stringToSquareInt = new ToIntFunction<String>() {
            @Override
            public int applyAsInt(String t) {
                final String str = t.substring(1, ((t.length()) - 1));
                final int value = Integer.parseInt(str);
                return value * value;
            }
        };
        Stream.of("[2]", "[3]", "[4]", "[8]", "[25]").mapToInt(stringToSquareInt).custom(assertElements(Matchers.arrayContaining(4, 9, 16, 64, 625)));
    }
}

