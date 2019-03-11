package com.annimon.stream.longstreamtests;


import com.annimon.stream.LongStream;
import com.annimon.stream.function.LongSupplier;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class GenerateTest {
    @Test
    public void testStreamGenerate() {
        LongStream stream = LongStream.generate(new LongSupplier() {
            @Override
            public long getAsLong() {
                return 1234L;
            }
        });
        Assert.assertThat(stream.limit(3), elements(Matchers.arrayContaining(1234L, 1234L, 1234L)));
    }

    @Test(expected = NullPointerException.class)
    public void testStreamGenerateNull() {
        LongStream.generate(null);
    }
}

