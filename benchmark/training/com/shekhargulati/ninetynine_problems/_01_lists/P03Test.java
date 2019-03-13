package com.shekhargulati.ninetynine_problems._01_lists;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class P03Test {
    @Test
    public void shouldFindKthElementFromAList() throws Exception {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        Assert.assertThat(P03.kth(numbers, 2), CoreMatchers.is(CoreMatchers.equalTo(3)));
    }

    @Test
    public void shouldFindKthElementFromAListRecursive() throws Exception {
        LinkedList<Integer> numbers = CollectionUtils.linkedList(1, 2, 3, 4, 5);
        Assert.assertThat(P03.kthRecursive(numbers, 2), CoreMatchers.is(CoreMatchers.equalTo(3)));
    }

    @Test
    public void shouldFindKthElementFromAList_LinkedListAndStream() throws Exception {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        Assert.assertThat(P03.kthStream(numbers, 2), CoreMatchers.is(CoreMatchers.equalTo(3)));
    }
}

