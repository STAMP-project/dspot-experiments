package com.shekhargulati.ninetynine_problems._01_lists;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class P21Test {
    @Test
    public void shouldInsertElementAtSecondPosition() throws Exception {
        List<String> input = Stream.of("a", "b", "c", "d").collect(Collectors.toList());
        List<String> result = P21.insertAt(input, 2, "alfa");
        Assert.assertThat(result, hasSize(5));
        Assert.assertThat(result, contains("a", "alfa", "b", "c", "d"));
    }

    @Test
    public void shouldInsertElementAtFirstPosition() throws Exception {
        List<String> input = Stream.of("a", "b", "c", "d").collect(Collectors.toList());
        List<String> result = P21.insertAt(input, 1, "alfa");
        Assert.assertThat(result, hasSize(5));
        Assert.assertThat(result, contains("alfa", "a", "b", "c", "d"));
    }

    @Test
    public void shouldInsertElementAtEnd() throws Exception {
        List<String> input = Stream.of("a", "b", "c", "d").collect(Collectors.toList());
        List<String> result = P21.insertAt(input, 5, "alfa");
        Assert.assertThat(result, hasSize(5));
        Assert.assertThat(result, contains("a", "b", "c", "d", "alfa"));
    }

    @Test
    public void shouldInsertElementAtSecondPosition_split() throws Exception {
        List<String> input = Stream.of("a", "b", "c", "d").collect(Collectors.toList());
        List<String> result = P21.insertAt_split(input, 2, "alfa");
        Assert.assertThat(result, hasSize(5));
        Assert.assertThat(result, contains("a", "alfa", "b", "c", "d"));
    }

    @Test
    public void shouldInsertElementAtFirstPosition_split() throws Exception {
        List<String> input = Stream.of("a", "b", "c", "d").collect(Collectors.toList());
        List<String> result = P21.insertAt_split(input, 1, "alfa");
        Assert.assertThat(result, hasSize(5));
        Assert.assertThat(result, contains("alfa", "a", "b", "c", "d"));
    }

    @Test
    public void shouldInsertElementAtEnd_split() throws Exception {
        List<String> input = Stream.of("a", "b", "c", "d").collect(Collectors.toList());
        List<String> result = P21.insertAt_split(input, 5, "alfa");
        Assert.assertThat(result, hasSize(5));
        Assert.assertThat(result, contains("a", "b", "c", "d", "alfa"));
    }
}

