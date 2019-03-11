package com.annimon.stream.intstreamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.IntStream;
import com.annimon.stream.function.IntPredicate;
import org.junit.Assert;
import org.junit.Test;


public final class NoneMatchTest {
    @Test
    public void testNoneMatch() {
        IntStream.empty().noneMatch(new IntPredicate() {
            @Override
            public boolean test(int value) {
                throw new IllegalStateException();
            }
        });
        Assert.assertFalse(IntStream.of(42).noneMatch(new IntPredicate() {
            @Override
            public boolean test(int value) {
                return value == 42;
            }
        }));
        Assert.assertFalse(IntStream.of(5, 7, 9, 10, 7, 5).noneMatch(Functions.remainderInt(2)));
        Assert.assertTrue(IntStream.of(5, 7, 9, 11, 7, 5).noneMatch(Functions.remainderInt(2)));
    }
}

