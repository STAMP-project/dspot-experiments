package com.shekhargulati.ninetynine_problems._01_lists;


import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class P02Test {
    @Test
    public void shouldFindSecondLastElementFromAList() throws Exception {
        List<Integer> numbers = Arrays.asList(1, 2, 11, 4, 5, 8, 10, 6);
        Assert.assertThat(P02.secondLast(numbers), CoreMatchers.is(CoreMatchers.equalTo(10)));
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowExceptionWhenListEmpty() throws Exception {
        P02.secondLast(Collections.emptyList());
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowExceptionWhenListHasSingleElement() throws Exception {
        P02.secondLast(Arrays.asList(1));
    }

    @Test
    public void shouldFindSecondLastElementFromALinkedList() throws Exception {
        LinkedList<Integer> numbers = CollectionUtils.linkedList(1, 2, 11, 4, 5, 8, 10, 6);
        Assert.assertThat(P02.secondLastRecursion(numbers), CoreMatchers.is(CoreMatchers.equalTo(10)));
    }
}

