package com.annimon.stream.longstreamtests;


import com.annimon.stream.LongStream;
import com.annimon.stream.function.LongConsumer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class ForEachTest {
    @Test
    public void testForEach() {
        final long[] expected = new long[]{ 12L, 32L, 22L, 9L };
        LongStream.of(12L, 32L, 22L, 9L).forEach(new LongConsumer() {
            private int index = 0;

            @Override
            public void accept(long value) {
                Assert.assertThat(value, Matchers.is(expected[((index)++)]));
            }
        });
    }

    @Test
    public void testForEachOnEmptyStream() {
        LongStream.empty().forEach(new LongConsumer() {
            @Override
            public void accept(long value) {
                Assert.fail();
            }
        });
    }
}

