package com.shekhargulati.ninetynine_problems._01_lists;


import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class P28Test {
    @Test
    public void shouldSortByElementLength() throws Exception {
        List<List<String>> input = Arrays.asList(Arrays.asList("a", "b", "c"), Arrays.asList("d", "e"), Arrays.asList("f", "g", "h"), Arrays.asList("d", "e"), Arrays.asList("i", "j", "k"), Arrays.asList("m", "n"), Arrays.asList("o"));
        List<List<String>> result = P28.lsort(input);
        Assert.assertThat(result, CoreMatchers.is(CoreMatchers.equalTo(Arrays.asList(Arrays.asList("o"), Arrays.asList("d", "e"), Arrays.asList("d", "e"), Arrays.asList("m", "n"), Arrays.asList("a", "b", "c"), Arrays.asList("f", "g", "h"), Arrays.asList("i", "j", "k")))));
    }

    @Test
    public void shouldSortByLengthFrequency() throws Exception {
        List<List<String>> input = Arrays.asList(Arrays.asList("a", "b", "c"), Arrays.asList("d", "e"), Arrays.asList("f", "g", "h"), Arrays.asList("d", "e"), Arrays.asList("i", "j", "k", "l"), Arrays.asList("m", "n"), Arrays.asList("o"));
        List<List<String>> result = P28.lfsort(input);
        Assert.assertThat(result, CoreMatchers.is(CoreMatchers.equalTo(Arrays.asList(Arrays.asList("i", "j", "k", "l"), Arrays.asList("o"), Arrays.asList("a", "b", "c"), Arrays.asList("f", "g", "h"), Arrays.asList("d", "e"), Arrays.asList("d", "e"), Arrays.asList("m", "n")))));
    }
}

