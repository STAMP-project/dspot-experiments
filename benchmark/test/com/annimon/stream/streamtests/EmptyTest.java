package com.annimon.stream.streamtests;


import com.annimon.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public final class EmptyTest {
    @Test
    public void testStreamEmpty() {
        Assert.assertThat(Stream.empty(), isEmpty());
    }
}

