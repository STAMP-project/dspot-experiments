package com.shekhargulati.ninetynine_problems._01_lists;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class P15Test {
    @Test
    public void shouldDuplicateElementsInAList() throws Exception {
        List<String> duplicates = P15.duplicate(Arrays.asList("a", "b", "c"), 3);
        Assert.assertThat(duplicates, hasSize(9));
        Assert.assertThat(duplicates, contains("a", "a", "a", "b", "b", "b", "c", "c", "c"));
    }
}

