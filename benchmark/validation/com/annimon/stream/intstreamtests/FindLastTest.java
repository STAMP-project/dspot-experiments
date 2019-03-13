package com.annimon.stream.intstreamtests;


import com.annimon.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;


public final class FindLastTest {
    @Test
    public void testFindLast() {
        Assert.assertThat(IntStream.of(3, 10, 19, 4, 50).findLast(), hasValue(50));
        Assert.assertThat(IntStream.of(50).findLast(), hasValue(50));
        Assert.assertThat(IntStream.empty().findFirst(), isEmpty());
    }
}

