package com.shekhargulati.ninetynine_problems._01_lists;


import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class P12Test {
    @Test
    public void shouldDecodeEncodedList() throws Exception {
        List<String> encoded = P12.decode(Arrays.asList(new AbstractMap.SimpleEntry(4, "a"), "b", new AbstractMap.SimpleEntry(2, "c"), new AbstractMap.SimpleEntry(2, "a"), "d", new AbstractMap.SimpleEntry(4, "e")));
        Assert.assertThat(encoded, hasSize(14));
    }
}

