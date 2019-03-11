package com.annimon.stream.intstreamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.IntStream;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class FilterNotTest {
    @Test
    public void testFilterNot() {
        IntStream.rangeClosed(1, 10).filterNot(Functions.remainderInt(2)).custom(assertElements(Matchers.arrayContaining(1, 3, 5, 7, 9)));
    }
}

