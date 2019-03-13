package com.annimon.stream.streamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.annimon.stream.test.hamcrest.OptionalMatcher;
import org.junit.Assert;
import org.junit.Test;


public final class FindFirstTest {
    @Test
    public void testFindFirst() {
        Optional<Integer> result = Stream.range(0, 10).findFirst();
        Assert.assertThat(result, isPresent());
        Assert.assertNotNull(result.get());
        Assert.assertEquals(0, ((int) (result.get())));
    }

    @Test
    public void testFindFirstOnEmptyStream() {
        Assert.assertThat(Stream.empty().findFirst(), OptionalMatcher.isEmpty());
    }

    @Test
    public void testFindFirstAfterFiltering() {
        Optional<Integer> result = Stream.range(1, 1000).filter(Functions.remainder(6)).findFirst();
        Assert.assertThat(result, isPresent());
        Assert.assertNotNull(result.get());
        Assert.assertEquals(6, ((int) (result.get())));
    }
}

