package com.shekhargulati.ninetynine_problems._01_lists;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class P22Test {
    @Test
    public void shouldCreateARangeBetween4And9() throws Exception {
        List<Integer> range = P22.range(4, 9);
        Assert.assertThat(range, hasSize(6));
        Assert.assertThat(range, contains(4, 5, 6, 7, 8, 9));
    }
}

