package com.annimon.stream.streamtests;


import com.annimon.stream.Stream;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class IteratorTest {
    @Test
    public void testIterator() {
        Assert.assertThat(Stream.of(1).iterator(), Matchers.is(CoreMatchers.not(CoreMatchers.nullValue())));
    }
}

