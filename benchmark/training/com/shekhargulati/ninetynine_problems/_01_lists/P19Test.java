package com.shekhargulati.ninetynine_problems._01_lists;


import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class P19Test {
    @Test
    public void shouldRotateAListByThreeElementsWhenNIs3() throws Exception {
        List<String> rotated = P19.rotate(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"), 3);
        Assert.assertThat(rotated, CoreMatchers.equalTo(Arrays.asList("d", "e", "f", "g", "h", "a", "b", "c")));
    }

    @Test
    public void shouldReturnSameListWhenNIs0() throws Exception {
        List<String> rotated = P19.rotate(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"), 0);
        Assert.assertThat(rotated, CoreMatchers.equalTo(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h")));
    }

    @Test
    public void shouldRotateWhenNIsNegative() throws Exception {
        List<String> rotated = P19.rotate(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h"), (-2));
        Assert.assertThat(rotated, CoreMatchers.equalTo(Arrays.asList("g", "h", "a", "b", "c", "d", "e", "f")));
    }
}

