package com.shekhargulati.tadm.ch04.examples;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class SelectionSortTest {
    @Test
    public void shouldSortNumbersInAscendingOrder() throws Exception {
        int[] sortedArr = SelectionSort.sort(new int[]{ 2, 5, 1, 3, 4 });
        Assert.assertThat(sortedArr, CoreMatchers.equalTo(new int[]{ 1, 2, 3, 4, 5 }));
    }
}

