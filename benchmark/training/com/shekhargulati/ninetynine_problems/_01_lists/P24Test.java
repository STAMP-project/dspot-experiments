package com.shekhargulati.ninetynine_problems._01_lists;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class P24Test {
    @Test
    public void shouldGive6RandomNumbersFromARangeStartingFrom1To49() throws Exception {
        List<Integer> randomList = P24.randomSelect(6, 1, 49);
        Assert.assertThat(randomList, hasSize(6));
        System.out.println(randomList);// One possible output [47, 30, 36, 38, 11, 1]

    }
}

