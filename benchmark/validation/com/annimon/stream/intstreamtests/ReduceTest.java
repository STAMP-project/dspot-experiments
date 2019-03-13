package com.annimon.stream.intstreamtests;


import com.annimon.stream.IntStream;
import com.annimon.stream.function.IntBinaryOperator;
import org.junit.Assert;
import org.junit.Test;


public final class ReduceTest {
    @Test
    public void testReduceIdentity() {
        Assert.assertEquals(IntStream.empty().reduce(1, new IntBinaryOperator() {
            @Override
            public int applyAsInt(int left, int right) {
                return left + right;
            }
        }), 1);
        Assert.assertEquals(IntStream.of(42).reduce(1, new IntBinaryOperator() {
            @Override
            public int applyAsInt(int left, int right) {
                return left + right;
            }
        }), 43);
        Assert.assertEquals(IntStream.of(5, 7, 3, 9, 1).reduce(Integer.MIN_VALUE, new IntBinaryOperator() {
            @Override
            public int applyAsInt(int left, int right) {
                if (left >= right)
                    return left;

                return right;
            }
        }), 9);
    }

    @Test
    public void testReduce() {
        Assert.assertFalse(IntStream.empty().reduce(new IntBinaryOperator() {
            @Override
            public int applyAsInt(int left, int right) {
                throw new IllegalStateException();
            }
        }).isPresent());
        Assert.assertEquals(IntStream.of(42).reduce(new IntBinaryOperator() {
            @Override
            public int applyAsInt(int left, int right) {
                throw new IllegalStateException();
            }
        }).getAsInt(), 42);
        Assert.assertEquals(IntStream.of(41, 42).reduce(new IntBinaryOperator() {
            @Override
            public int applyAsInt(int left, int right) {
                if (right > left)
                    return right;

                return left;
            }
        }).getAsInt(), 42);
    }
}

