package com.annimon.stream.intstreamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.IntStream;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class TakeWhileTest {
    @Test
    public void testTakeWhile() {
        IntStream.of(2, 4, 6, 7, 8, 10, 11).takeWhile(Functions.remainderInt(2)).custom(assertElements(Matchers.arrayContaining(2, 4, 6)));
    }

    @Test
    public void testTakeWhileNonFirstMatch() {
        IntStream.of(2, 4, 6, 7, 8, 10, 11).takeWhile(Functions.remainderInt(3)).custom(assertIsEmpty());
    }

    @Test
    public void testTakeWhileAllMatch() {
        IntStream.of(2, 4, 6, 7, 8, 10, 11).takeWhile(Functions.remainderInt(1)).custom(assertElements(Matchers.arrayContaining(2, 4, 6, 7, 8, 10, 11)));
    }
}

