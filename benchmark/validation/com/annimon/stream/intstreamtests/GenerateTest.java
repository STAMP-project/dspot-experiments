package com.annimon.stream.intstreamtests;


import com.annimon.stream.IntStream;
import com.annimon.stream.function.IntSupplier;
import org.junit.Assert;
import org.junit.Test;


public final class GenerateTest {
    @Test
    public void testStreamGenerate() {
        IntSupplier s = new IntSupplier() {
            @Override
            public int getAsInt() {
                return 42;
            }
        };
        Assert.assertEquals(42, IntStream.generate(s).findFirst().getAsInt());
    }

    @Test(expected = NullPointerException.class)
    public void testStreamGenerateNull() {
        IntStream.generate(null);
    }
}

