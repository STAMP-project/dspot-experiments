package com.shekhargulati.ninetynine_problems._01_lists;


import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class P16Test {
    @Test
    public void shouldDropEveryNthElement() throws Exception {
        List<String> result = P16.dropEveryNth(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"), 3);
        Assert.assertThat(result, hasSize(8));
        Assert.assertThat(result, contains("a", "b", "d", "e", "g", "h", "j", "k"));
    }

    @Test
    public void shouldReturnSameListWhenNIsLessThanListSize() throws Exception {
        List<String> result = P16.dropEveryNth(Arrays.asList("a", "b"), 3);
        Assert.assertThat(result, hasSize(2));
        Assert.assertThat(result, contains("a", "b"));
    }

    @Test
    public void shouldReturnSameListWhenNIsZero() throws Exception {
        List<String> result = P16.dropEveryNth(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"), 0);
        Assert.assertThat(result, hasSize(11));
        Assert.assertThat(result, contains("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k"));
    }
}

