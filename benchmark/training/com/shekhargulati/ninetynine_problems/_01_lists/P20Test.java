package com.shekhargulati.ninetynine_problems._01_lists;


import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class P20Test {
    @Test
    public void shouldRemoveKthElementFromList() throws Exception {
        Object[] result = P20.removeAt(Arrays.asList("a", "b", "c", "d"), 2);
        Assert.assertThat(result[0], CoreMatchers.equalTo(Arrays.asList("a", "c", "d")));
        Assert.assertThat(result[1], CoreMatchers.equalTo("b"));
    }

    @Test
    public void shouldRemoveKthElementFromList_() throws Exception {
        Object[] result = P20.removeAt_splitAt(Arrays.asList("a", "b", "c", "d"), 2);
        Assert.assertThat(result[0], CoreMatchers.equalTo(Arrays.asList("a", "c", "d")));
        Assert.assertThat(result[1], CoreMatchers.equalTo("b"));
    }
}

