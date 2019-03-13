package com.shekhargulati.ninetynine_problems._01_lists;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class P18Test {
    @Test
    public void shouldGiveSliceOfAList() throws Exception {
        List<String> slice = P18.slice(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "k"), 3, 7);
        Assert.assertThat(slice, hasSize(5));
        Assert.assertThat(slice, contains("c", "d", "e", "f", "g"));
    }
}

