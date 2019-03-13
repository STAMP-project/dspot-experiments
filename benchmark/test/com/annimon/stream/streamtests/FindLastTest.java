package com.annimon.stream.streamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.annimon.stream.test.hamcrest.OptionalMatcher;
import org.junit.Assert;
import org.junit.Test;


public final class FindLastTest {
    @Test
    public void testFindLast() {
        Optional<Integer> result = Stream.rangeClosed(0, 10).findLast();
        Assert.assertThat(result, isPresent());
        Assert.assertNotNull(result.get());
        Assert.assertEquals(10, ((int) (result.get())));
    }

    @Test
    public void testFindLastOnEmptyStream() {
        Assert.assertThat(Stream.empty().findLast(), OptionalMatcher.isEmpty());
    }

    @Test
    public void testFindLastAfterFiltering() {
        Optional<Integer> result = Stream.range(1, 100).filter(Functions.remainder(6)).findLast();
        Assert.assertThat(result, isPresent());
        Assert.assertNotNull(result.get());
        Assert.assertEquals(96, ((int) (result.get())));
    }
}

