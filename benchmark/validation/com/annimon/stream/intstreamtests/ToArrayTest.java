package com.annimon.stream.intstreamtests;


import com.annimon.stream.IntStream;
import com.annimon.stream.function.IntSupplier;
import org.junit.Assert;
import org.junit.Test;


public final class ToArrayTest {
    @Test
    public void testToArray() {
        Assert.assertEquals(IntStream.empty().toArray().length, 0);
        Assert.assertEquals(IntStream.of(100).toArray()[0], 100);
        Assert.assertEquals(IntStream.of(1, 2, 3, 4, 5, 6, 7, 8, 9).skip(4).toArray().length, 5);
        Assert.assertEquals(IntStream.generate(new IntSupplier() {
            @Override
            public int getAsInt() {
                return -1;
            }
        }).limit(14).toArray().length, 14);
        Assert.assertEquals(IntStream.of(IntStream.generate(new IntSupplier() {
            @Override
            public int getAsInt() {
                return -1;
            }
        }).limit(14).toArray()).sum(), (-14));
    }
}

