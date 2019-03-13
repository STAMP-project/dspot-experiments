package com.annimon.stream.intstreamtests;


import IntPredicate.Util;
import com.annimon.stream.Functions;
import com.annimon.stream.IntStream;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class TakeUntilTest {
    @Test
    public void testTakeUntil() {
        IntStream.of(2, 4, 6, 7, 8, 10, 11).takeUntil(Util.negate(Functions.remainderInt(2))).custom(assertElements(Matchers.arrayContaining(2, 4, 6, 7)));
    }

    @Test
    public void testTakeUntilFirstMatch() {
        IntStream.of(2, 4, 6, 7, 8, 10, 11).takeUntil(Functions.remainderInt(2)).custom(assertElements(Matchers.arrayContaining(2)));
    }

    @Test
    public void testTakeUntilNoneMatch() {
        IntStream.of(2, 4, 6, 7, 8, 10, 11).takeUntil(Functions.remainderInt(128)).custom(assertElements(Matchers.arrayContaining(2, 4, 6, 7, 8, 10, 11)));
    }
}

