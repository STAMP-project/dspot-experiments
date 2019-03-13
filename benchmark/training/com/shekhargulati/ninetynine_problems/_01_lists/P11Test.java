package com.shekhargulati.ninetynine_problems._01_lists;


import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class P11Test {
    @Test
    public void shouldEncodeAList() throws Exception {
        List<Object> encodedList = P11.encode_modified(Arrays.asList("a", "a", "a", "a", "b", "c", "c", "a", "a", "d", "e", "e", "e", "e"));
        Assert.assertThat(encodedList, hasSize(6));
        Assert.assertThat(encodedList.get(0), CoreMatchers.is(CoreMatchers.equalTo(new AbstractMap.SimpleEntry<>(4, "a"))));
        Assert.assertThat(encodedList.get(1), CoreMatchers.is(CoreMatchers.equalTo("b")));
        Assert.assertThat(encodedList.get(2), CoreMatchers.is(CoreMatchers.equalTo(new AbstractMap.SimpleEntry<>(2, "c"))));
        Assert.assertThat(encodedList.get(3), CoreMatchers.is(CoreMatchers.equalTo(new AbstractMap.SimpleEntry<>(2, "a"))));
        Assert.assertThat(encodedList.get(4), CoreMatchers.is(CoreMatchers.equalTo("d")));
        Assert.assertThat(encodedList.get(5), CoreMatchers.is(CoreMatchers.equalTo(new AbstractMap.SimpleEntry<>(4, "e"))));
    }
}

