package com.shekhargulati.tadm.ch04.excercises;


import com.shekhargulati.IntPair;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class Problem4_2Test {
    @Test
    public void shouldFindPairThatMaximizeWithinUnsortedArray() throws Exception {
        int[] numbers = new int[]{ 6, 13, 19, 3, 8 };
        IntPair p = Problem4_2.maximize_unsorted(numbers);
        Assert.assertThat(p, CoreMatchers.equalTo(new IntPair(3, 19)));
    }

    @Test
    public void shouldFindPairThatMaximizeWithinSortedArray() throws Exception {
        int[] numbers = new int[]{ 3, 6, 8, 13, 19 };
        IntPair p = Problem4_2.maximize_sorted(numbers);
        Assert.assertThat(p, CoreMatchers.equalTo(new IntPair(3, 19)));
    }

    @Test
    public void shouldFindPairThatMinimizeWithinUnsortedArray() throws Exception {
        int[] numbers = new int[]{ 6, 13, 19, 3, 8, 14 };
        IntPair p = Problem4_2.minimize_unsorted(numbers);
        Assert.assertThat(p, CoreMatchers.equalTo(new IntPair(13, 14)));
    }

    @Test
    public void shouldFindPairThatMinimiseWithinSortedArray() throws Exception {
        int[] numbers = new int[]{ 3, 6, 8, 13, 19 };
        IntPair p = Problem4_2.minimize_sorted(numbers);
        Assert.assertThat(p, CoreMatchers.equalTo(new IntPair(6, 8)));
    }
}

