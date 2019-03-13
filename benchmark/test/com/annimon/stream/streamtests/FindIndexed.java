package com.annimon.stream.streamtests;


import com.annimon.stream.IntPair;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public final class FindIndexed {
    @Test
    public void testFindIndexed() {
        IntPair<Integer> result = Stream.rangeClosed(1, 10).findIndexed(FindIndexed.sumEquals(7)).get();
        MatcherAssert.assertThat(result.getFirst(), CoreMatchers.is(3));
        MatcherAssert.assertThat(result.getSecond(), CoreMatchers.is(4));
    }

    @Test
    public void testFindIndexedWithStartAndStep() {
        IntPair<Integer> result = Stream.of(1, 11, 22, 12, 40).findIndexed(0, 10, FindIndexed.sumEquals(42)).get();
        MatcherAssert.assertThat(result.getFirst(), CoreMatchers.is(20));
        MatcherAssert.assertThat(result.getSecond(), CoreMatchers.is(22));
    }

    @Test
    public void testFindIndexedNoMatch() {
        Optional<IntPair<Integer>> result = Stream.range(0, 10).findIndexed(FindIndexed.sumEquals(42));
        MatcherAssert.assertThat(result, isEmpty());
    }
}

