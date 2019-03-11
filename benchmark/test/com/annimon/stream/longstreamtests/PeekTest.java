package com.annimon.stream.longstreamtests;


import com.annimon.stream.LongStream;
import com.annimon.stream.function.LongConsumer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class PeekTest {
    @Test
    public void testPeek() {
        LongStream.empty().peek(new LongConsumer() {
            @Override
            public void accept(long value) {
                Assert.fail();
            }
        }).custom(assertIsEmpty());
        final long[] expected = new long[]{ 12, 34 };
        Assert.assertThat(LongStream.of(12, 34).peek(new LongConsumer() {
            private int index = 0;

            @Override
            public void accept(long value) {
                Assert.assertThat(value, Matchers.is(expected[((index)++)]));
            }
        }).count(), Matchers.is(2L));
    }
}

