package com.annimon.stream.streamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class FilterNotTest {
    @Test
    public void testFilterNot() {
        Stream.range(0, 10).filterNot(Functions.remainder(2)).custom(assertElements(Matchers.contains(1, 3, 5, 7, 9)));
    }
}

