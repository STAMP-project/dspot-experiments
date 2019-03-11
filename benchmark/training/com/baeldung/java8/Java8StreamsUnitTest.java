package com.baeldung.java8;


import com.baeldung.java_8_features.Detail;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class Java8StreamsUnitTest {
    private List<String> list;

    @Test
    public void checkStreamCount_whenCreating_givenDifferentSources() {
        String[] arr = new String[]{ "a", "b", "c" };
        Stream<String> streamArr = Arrays.stream(arr);
        Assert.assertEquals(streamArr.count(), 3);
        Stream<String> streamOf = Stream.of("a", "b", "c");
        Assert.assertEquals(streamOf.count(), 3);
        long count = list.stream().distinct().count();
        Assert.assertEquals(count, 9);
    }

    @Test
    public void checkStreamCount_whenOperationFilter_thanCorrect() {
        Stream<String> streamFilter = list.stream().filter(( element) -> element.isEmpty());
        Assert.assertEquals(streamFilter.count(), 2);
    }

    @Test
    public void checkStreamCount_whenOperationMap_thanCorrect() {
        List<String> uris = new ArrayList<>();
        uris.add("C:\\My.txt");
        Stream<Path> streamMap = uris.stream().map(( uri) -> Paths.get(uri));
        Assert.assertEquals(streamMap.count(), 1);
        List<Detail> details = new ArrayList<>();
        details.add(new Detail());
        details.add(new Detail());
        Stream<String> streamFlatMap = details.stream().flatMap(( detail) -> detail.getParts().stream());
        Assert.assertEquals(streamFlatMap.count(), 4);
    }

    @Test
    public void checkStreamCount_whenOperationMatch_thenCorrect() {
        boolean isValid = list.stream().anyMatch(( element) -> element.contains("h"));
        boolean isValidOne = list.stream().allMatch(( element) -> element.contains("h"));
        boolean isValidTwo = list.stream().noneMatch(( element) -> element.contains("h"));
        Assert.assertTrue(isValid);
        Assert.assertFalse(isValidOne);
        Assert.assertFalse(isValidTwo);
    }

    @Test
    public void checkStreamReducedValue_whenOperationReduce_thenCorrect() {
        List<Integer> integers = new ArrayList<>();
        integers.add(1);
        integers.add(1);
        integers.add(1);
        Integer reduced = integers.stream().reduce(23, ( a, b) -> a + b);
        Assert.assertTrue((reduced == 26));
    }

    @Test
    public void checkStreamContains_whenOperationCollect_thenCorrect() {
        List<String> resultList = list.stream().map(( element) -> element.toUpperCase()).collect(Collectors.toList());
        Assert.assertEquals(resultList.size(), list.size());
        Assert.assertTrue(resultList.contains(""));
    }

    @Test
    public void checkParallelStream_whenDoWork() {
        list.parallelStream().forEach(( element) -> doWork(element));
    }
}

