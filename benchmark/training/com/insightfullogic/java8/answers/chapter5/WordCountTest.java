package com.insightfullogic.java8.answers.chapter5;


import java.util.Map;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class WordCountTest {
    @Test
    public void passesBookExample() {
        Stream<String> names = Stream.of("John", "Paul", "George", "John", "Paul", "John");
        Map<String, Long> counts = WordCount.countWords(names);
        Assert.assertEquals(3, counts.size());
        Assert.assertEquals(Long.valueOf(3), counts.get("John"));
        Assert.assertEquals(Long.valueOf(2), counts.get("Paul"));
        Assert.assertEquals(Long.valueOf(1), counts.get("George"));
    }
}

