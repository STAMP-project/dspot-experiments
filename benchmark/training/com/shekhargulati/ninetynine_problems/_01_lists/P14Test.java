package com.shekhargulati.ninetynine_problems._01_lists;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class P14Test {
    @Test
    public void shouldDuplicateElementsInAList() throws Exception {
        List<String> duplicates = P14.duplicate(Arrays.asList("a", "b", "c", "d"));
        Assert.assertThat(duplicates, hasSize(8));
        Assert.assertThat(duplicates, contains("a", "a", "b", "b", "c", "c", "d", "d"));
    }
}

