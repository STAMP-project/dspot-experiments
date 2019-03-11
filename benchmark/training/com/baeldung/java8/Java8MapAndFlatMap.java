package com.baeldung.java8;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class Java8MapAndFlatMap {
    @Test
    public void givenStream_whenCalledMap_thenProduceList() {
        List<String> myList = Stream.of("a", "b").map(String::toUpperCase).collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList("A", "B"), myList);
    }

    @Test
    public void givenStream_whenCalledFlatMap_thenProduceFlattenedList() throws Exception {
        List<List<String>> list = Arrays.asList(Arrays.asList("a"), Arrays.asList("b"));
        System.out.println(list);
        System.out.println(list.stream().flatMap(Collection::stream).collect(Collectors.toList()));
    }

    @Test
    public void givenOptional_whenCalledMap_thenProduceOptional() {
        Optional<String> s = Optional.of("test");
        Assert.assertEquals(Optional.of("TEST"), s.map(String::toUpperCase));
    }

    @Test
    public void givenOptional_whenCalledFlatMap_thenProduceFlattenedOptional() {
        Assert.assertEquals(Optional.of(Optional.of("STRING")), Optional.of("string").map(( s) -> Optional.of("STRING")));
        Assert.assertEquals(Optional.of("STRING"), Optional.of("string").flatMap(( s) -> Optional.of("STRING")));
    }
}

