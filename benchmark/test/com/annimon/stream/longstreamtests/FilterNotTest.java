package com.annimon.stream.longstreamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.LongStream;
import com.annimon.stream.function.LongPredicate;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class FilterNotTest {
    @Test
    public void testFilterNot() {
        final LongPredicate predicate = Functions.remainderLong(111);
        LongStream.of(322, 555, 666, 1984, 1998).filterNot(predicate).custom(assertElements(Matchers.arrayContaining(322L, 1984L)));
        LongStream.of(777, 999).filterNot(predicate).custom(assertIsEmpty());
    }
}

