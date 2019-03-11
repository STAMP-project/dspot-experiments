package com.baeldung.vavr.interoperability;


import io.vavr.collection.HashMap;
import io.vavr.collection.LinkedHashSet;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.collection.Stream;
import java.util.Arrays;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;


public class CollectionsInteroperabilityUnitTest {
    @Test
    public void givenParams_whenVavrList_thenReturnJavaList() {
        List<String> vavrStringList = List.of("JAVA", "Javascript", "Scala");
        java.util.List<String> javaStringList = vavrStringList.toJavaList();
        Assert.assertTrue((javaStringList instanceof java.util.List));
    }

    @Test
    public void givenParams_whenVavrStream_thenReturnJavaStream() {
        Stream<String> vavrStream = Stream.of("JAVA", "Javascript", "Scala");
        java.util.stream.Stream<String> javaParallelStream = vavrStream.toJavaParallelStream();
        Assert.assertTrue((javaParallelStream instanceof java.util.stream.Stream));
        java.util.List<String> javaStringList = vavrStream.toJavaList();
        Assert.assertTrue((javaStringList instanceof java.util.List));
    }

    @Test
    public void givenParams_whenVavrMap_thenReturnJavaMap() {
        Map<String, String> vavrMap = HashMap.of("1", "a", "2", "b", "3", "c");
        java.util.Map<String, String> javaMap = vavrMap.toJavaMap();
        Assert.assertTrue((javaMap instanceof java.util.Map));
    }

    @Test
    public void givenParams_whenJavaList_thenReturnVavrListUsingOfAll() {
        java.util.List<String> javaList = Arrays.asList("Java", "Haskell", "Scala");
        List<String> vavrList = List.ofAll(javaList);
        Assert.assertTrue((vavrList instanceof List));
    }

    @Test
    public void givenParams_whenJavaStream_thenReturnVavrListUsingOfAll() {
        java.util.stream.Stream<String> javaStream = Arrays.asList("Java", "Haskell", "Scala").stream();
        Stream<String> vavrStream = Stream.ofAll(javaStream);
        Assert.assertTrue((vavrStream instanceof Stream));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void givenParams_whenVavrListConverted_thenException() {
        java.util.List<String> javaList = List.of("Java", "Haskell", "Scala").asJava();
        javaList.add("Python");
        Assert.assertEquals(4, javaList.size());
    }

    @Test
    public void givenParams_whenVavrListConvertedToMutable_thenRetunMutableList() {
        java.util.List<String> javaList = List.of("Java", "Haskell", "Scala").asJavaMutable();
        javaList.add("Python");
        Assert.assertEquals(4, javaList.size());
    }

    @Test
    public void givenParams_WhenVavarListConvertedToLinkedSet_thenReturnLinkedSet() {
        List<String> vavrList = List.of("Java", "Haskell", "Scala", "Java");
        Set<String> linkedSet = vavrList.toLinkedSet();
        Assert.assertEquals(3, linkedSet.size());
        Assert.assertTrue((linkedSet instanceof LinkedHashSet));
    }

    @Test
    public void givenParams_WhenVavrList_thenReturnJavaOptional() {
        List<String> vavrList = List.of("Java");
        Optional<String> optional = vavrList.toJavaOptional();
        Assert.assertEquals("Java", optional.get());
    }
}

