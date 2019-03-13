package com.annimon.stream.intstreamtests;


import com.annimon.stream.IntStream;
import com.annimon.stream.function.IntConsumer;
import org.junit.Assert;
import org.junit.Test;


public final class ForEachTest {
    @Test
    public void testForEach() {
        IntStream.empty().forEach(new IntConsumer() {
            @Override
            public void accept(int value) {
                throw new IllegalStateException();
            }
        });
        IntStream.of(42).forEach(new IntConsumer() {
            @Override
            public void accept(int value) {
                Assert.assertEquals(42, value);
            }
        });
        final int[] sum = new int[1];
        IntStream.rangeClosed(10, 20).forEach(new IntConsumer() {
            @Override
            public void accept(int value) {
                sum[0] += value;
            }
        });
        Assert.assertEquals(sum[0], 165);
    }
}

