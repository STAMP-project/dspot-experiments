package com.shekhargulati.tadm.ch03;


import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class Problem3_2Test {
    @Test
    public void shouldReverseALinkedList() throws Exception {
        List<Integer> numbers = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toCollection(LinkedList::new));
        List<Integer> reversedNumbers = new Problem3_2().reverse(numbers);
        Assert.assertThat(reversedNumbers, CoreMatchers.equalTo(Stream.of(10, 9, 8, 7, 6, 5, 4, 3, 2, 1).collect(Collectors.toCollection(LinkedList::new))));
    }
}

