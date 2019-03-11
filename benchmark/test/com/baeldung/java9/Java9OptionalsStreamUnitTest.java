package com.baeldung.java9;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class Java9OptionalsStreamUnitTest {
    private static List<Optional<String>> listOfOptionals = Arrays.asList(Optional.empty(), Optional.of("foo"), Optional.empty(), Optional.of("bar"));

    @Test
    public void filterOutPresentOptionalsWithFilter() {
        Assert.assertEquals(4, Java9OptionalsStreamUnitTest.listOfOptionals.size());
        List<String> filteredList = Java9OptionalsStreamUnitTest.listOfOptionals.stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        Assert.assertEquals(2, filteredList.size());
        Assert.assertEquals("foo", filteredList.get(0));
        Assert.assertEquals("bar", filteredList.get(1));
    }

    @Test
    public void filterOutPresentOptionalsWithFlatMap() {
        Assert.assertEquals(4, Java9OptionalsStreamUnitTest.listOfOptionals.size());
        List<String> filteredList = Java9OptionalsStreamUnitTest.listOfOptionals.stream().flatMap(( o) -> o.isPresent() ? Stream.of(o.get()) : Stream.empty()).collect(Collectors.toList());
        Assert.assertEquals(2, filteredList.size());
        Assert.assertEquals("foo", filteredList.get(0));
        Assert.assertEquals("bar", filteredList.get(1));
    }

    @Test
    public void filterOutPresentOptionalsWithFlatMap2() {
        Assert.assertEquals(4, Java9OptionalsStreamUnitTest.listOfOptionals.size());
        List<String> filteredList = Java9OptionalsStreamUnitTest.listOfOptionals.stream().flatMap(( o) -> o.map(Stream::of).orElseGet(Stream::empty)).collect(Collectors.toList());
        Assert.assertEquals(2, filteredList.size());
        Assert.assertEquals("foo", filteredList.get(0));
        Assert.assertEquals("bar", filteredList.get(1));
    }

    @Test
    public void filterOutPresentOptionalsWithJava9() {
        Assert.assertEquals(4, Java9OptionalsStreamUnitTest.listOfOptionals.size());
        List<String> filteredList = Java9OptionalsStreamUnitTest.listOfOptionals.stream().flatMap(Optional::stream).collect(Collectors.toList());
        Assert.assertEquals(2, filteredList.size());
        Assert.assertEquals("foo", filteredList.get(0));
        Assert.assertEquals("bar", filteredList.get(1));
    }
}

