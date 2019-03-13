package com.shekhargulati.ninetynine_problems._02_arithmetic;


import java.util.List;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;


public class P35Test {
    @Test
    public void _8_isthesumof_3_and_5() throws Exception {
        List<Integer> numbers = P35.goldbach(8);
        Assert.assertThat(numbers, hasSize(2));
        Assert.assertThat(numbers, IsCollectionContaining.hasItems(3, 5));
    }

    @Test
    public void _4_isthesumof_2_and_2() throws Exception {
        List<Integer> numbers = P35.goldbach(4);
        Assert.assertThat(numbers, hasSize(2));
        Assert.assertThat(numbers, IsCollectionContaining.hasItems(2, 2));
    }

    @Test
    public void _28_isthesumof_5_and_23() throws Exception {
        List<Integer> numbers = P35.goldbach(28);
        Assert.assertThat(numbers, hasSize(2));
        Assert.assertThat(numbers, IsCollectionContaining.hasItems(5, 23));
    }
}

