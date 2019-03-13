package com.shekhargulati.ninetynine_problems._01_lists;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class P08Test {
    @Test
    public void shouldRemoveConsecutiveDuplicatesInAList() throws Exception {
        List<String> compressedList = P08.compress(Arrays.asList("a", "a", "a", "a", "b", "c", "c", "d", "e", "e", "e", "e"));
        Assert.assertThat(compressedList, hasSize(5));
        Assert.assertThat(compressedList, contains("a", "b", "c", "d", "e"));
    }

    @Test
    public void shouldNotRemoveNonConsecutiveSimilarElementsFromAList() throws Exception {
        List<String> compressedList = P08.compress(Arrays.asList("a", "a", "a", "a", "b", "c", "c", "a", "a", "d", "e", "e", "e", "e"));
        Assert.assertThat(compressedList, hasSize(6));
        Assert.assertThat(compressedList, contains("a", "b", "c", "a", "d", "e"));
    }
}

