package com.annimon.stream.longstreamtests;


import LongPredicate.Util;
import com.annimon.stream.Functions;
import com.annimon.stream.LongStream;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class TakeUntilTest {
    @Test
    public void testTakeUntil() {
        LongStream.of(2, 4, 6, 7, 8, 10, 11).takeUntil(Util.negate(Functions.remainderLong(2))).custom(assertElements(Matchers.arrayContaining(2L, 4L, 6L, 7L)));
    }

    @Test
    public void testTakeUntilFirstMatch() {
        LongStream.of(2, 4, 6, 7, 8, 10, 11).takeUntil(Functions.remainderLong(2)).custom(assertElements(Matchers.arrayContaining(2L)));
    }

    @Test
    public void testTakeUntilNoneMatch() {
        LongStream.of(2, 4, 6, 7, 8, 10, 11).takeUntil(Functions.remainderLong(128)).custom(assertElements(Matchers.arrayContaining(2L, 4L, 6L, 7L, 8L, 10L, 11L)));
    }
}

