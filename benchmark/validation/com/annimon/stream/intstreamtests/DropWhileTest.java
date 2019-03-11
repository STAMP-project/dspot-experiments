package com.annimon.stream.intstreamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.IntStream;
import org.hamcrest.Matchers;
import org.junit.Test;


public final class DropWhileTest {
    @Test
    public void testDropWhile() {
        IntStream.of(2, 4, 6, 7, 8, 10, 11).dropWhile(Functions.remainderInt(2)).custom(assertElements(Matchers.arrayContaining(7, 8, 10, 11)));
    }

    @Test
    public void testDropWhileNonFirstMatch() {
        IntStream.of(2, 4, 6, 7, 8, 10, 11).dropWhile(Functions.remainderInt(3)).custom(assertElements(Matchers.arrayContaining(2, 4, 6, 7, 8, 10, 11)));
    }

    @Test
    public void testDropWhileAllMatch() {
        IntStream.of(2, 4, 6, 7, 8, 10, 11).dropWhile(Functions.remainderInt(1)).custom(assertIsEmpty());
    }
}

