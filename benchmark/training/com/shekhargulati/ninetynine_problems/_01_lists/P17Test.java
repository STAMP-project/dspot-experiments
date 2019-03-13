package com.shekhargulati.ninetynine_problems._01_lists;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class P17Test {
    @Test
    public void shouldSplitInTwoHalves() throws Exception {
        Map<Boolean, List<String>> result = P17.split(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "k"), 3);
        Assert.assertThat(result.get(true), contains("a", "b", "c"));
        Assert.assertThat(result.get(false), contains("d", "e", "f", "g", "h", "i", "k"));
    }
}

