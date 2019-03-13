package com.shekhargulati.ninetynine_problems._01_lists;


import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class P05Test {
    @Test
    public void shouldReverseAList() throws Exception {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        Assert.assertThat(P05.reverse(numbers), CoreMatchers.is(CoreMatchers.equalTo(Arrays.asList(5, 4, 3, 2, 1))));
    }

    @Test
    public void shouldReverseAList_IntStream() throws Exception {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        Assert.assertThat(P05.reverse_IntStream(numbers), CoreMatchers.is(CoreMatchers.equalTo(Arrays.asList(5, 4, 3, 2, 1))));
    }

    @Test
    public void shouldReverseAList_Stream() throws Exception {
        ArrayDeque<Integer> numbers = CollectionUtils.arrayDeque(1, 2, 3, 4, 5);
        Assert.assertThat(P05.reverse_customStream(numbers), CoreMatchers.is(CoreMatchers.equalTo(Arrays.asList(5, 4, 3, 2, 1))));
    }
}

