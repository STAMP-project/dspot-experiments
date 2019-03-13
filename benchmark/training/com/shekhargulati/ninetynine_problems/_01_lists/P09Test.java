package com.shekhargulati.ninetynine_problems._01_lists;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class P09Test {
    @Test
    public void shouldReturnAListWithTwoListElementsWhenAListWithTwoUniqueElementsIsPassed() throws Exception {
        List<List<String>> packedList = P09.pack(Arrays.asList("a", "b"));
        Assert.assertThat(packedList, hasSize(2));
        Assert.assertThat(packedList.get(0), contains("a"));
        Assert.assertThat(packedList.get(1), contains("b"));
    }

    @Test
    public void shouldPackConsecutiveDuplicatesInTheirOwnLists_small() throws Exception {
        List<List<String>> packedList = P09.pack(Arrays.asList("a", "a", "b", "a"));
        Assert.assertThat(packedList, hasSize(3));
        Assert.assertThat(packedList.get(0), contains("a", "a"));
        Assert.assertThat(packedList.get(1), contains("b"));
        Assert.assertThat(packedList.get(2), contains("a"));
    }

    @Test
    public void shouldPackConsecutiveDuplicatesInTheirOwnLists() throws Exception {
        List<List<String>> packedList = P09.pack(Arrays.asList("a", "a", "a", "a", "b", "c", "c", "a", "a", "d", "e", "e", "e", "e"));
        Assert.assertThat(packedList, hasSize(6));
        Assert.assertThat(packedList.get(0), contains("a", "a", "a", "a"));
        Assert.assertThat(packedList.get(1), contains("b"));
        Assert.assertThat(packedList.get(2), contains("c", "c"));
        Assert.assertThat(packedList.get(3), contains("a", "a"));
        Assert.assertThat(packedList.get(4), contains("d"));
        Assert.assertThat(packedList.get(5), contains("e", "e", "e", "e"));
    }
}

