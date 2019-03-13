package com.insightfullogic.java8.exercises.chapter5;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class GroupingByTest {
    @Test
    public void stringsByLength() {
        GroupingBy<String, Integer> stringIntegerGroupingBy = new GroupingBy(String::length);
        Map<Integer, List<String>> results = Stream.of("a", "b", "cc", "dd").collect(stringIntegerGroupingBy);
        System.out.println(results);
        Assert.assertEquals(2, results.size());
        Assert.assertEquals(Arrays.asList("a", "b"), results.get(1));
        Assert.assertEquals(Arrays.asList("cc", "dd"), results.get(2));
    }
}

