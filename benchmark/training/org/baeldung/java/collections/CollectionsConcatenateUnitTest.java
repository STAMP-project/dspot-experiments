package org.baeldung.java.collections;


import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.junit.Assert;
import org.junit.Test;


public class CollectionsConcatenateUnitTest {
    @Test
    public void givenUsingJava8_whenConcatenatingUsingConcat_thenCorrect() {
        Collection<String> collectionA = Arrays.asList("S", "T");
        Collection<String> collectionB = Arrays.asList("U", "V");
        Collection<String> collectionC = Arrays.asList("W", "X");
        Stream<String> combinedStream = Stream.concat(Stream.concat(collectionA.stream(), collectionB.stream()), collectionC.stream());
        Collection<String> collectionCombined = combinedStream.collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList("S", "T", "U", "V", "W", "X"), collectionCombined);
    }

    @Test
    public void givenUsingJava8_whenConcatenatingUsingflatMap_thenCorrect() {
        Collection<String> collectionA = Arrays.asList("S", "T");
        Collection<String> collectionB = Arrays.asList("U", "V");
        Stream<String> combinedStream = Stream.of(collectionA, collectionB).flatMap(Collection::stream);
        Collection<String> collectionCombined = combinedStream.collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList("S", "T", "U", "V"), collectionCombined);
    }

    @Test
    public void givenUsingGuava_whenConcatenatingUsingIterables_thenCorrect() {
        Collection<String> collectionA = Arrays.asList("S", "T");
        Collection<String> collectionB = Arrays.asList("U", "V");
        Iterable<String> combinedIterables = Iterables.unmodifiableIterable(Iterables.concat(collectionA, collectionB));
        Collection<String> collectionCombined = Lists.newArrayList(combinedIterables);
        Assert.assertEquals(Arrays.asList("S", "T", "U", "V"), collectionCombined);
    }

    @Test
    public void givenUsingJava7_whenConcatenatingUsingIterables_thenCorrect() {
        Collection<String> collectionA = Arrays.asList("S", "T");
        Collection<String> collectionB = Arrays.asList("U", "V");
        Iterable<String> combinedIterables = CollectionsConcatenateUnitTest.concat(collectionA, collectionB);
        Collection<String> collectionCombined = CollectionsConcatenateUnitTest.makeListFromIterable(combinedIterables);
        Assert.assertEquals(Arrays.asList("S", "T", "U", "V"), collectionCombined);
    }

    @Test
    public void givenUsingApacheCommons_whenConcatenatingUsingUnion_thenCorrect() {
        Collection<String> collectionA = Arrays.asList("S", "T");
        Collection<String> collectionB = Arrays.asList("U", "V");
        Iterable<String> combinedIterables = CollectionUtils.union(collectionA, collectionB);
        Collection<String> collectionCombined = Lists.newArrayList(combinedIterables);
        Assert.assertEquals(Arrays.asList("S", "T", "U", "V"), collectionCombined);
    }

    @Test
    public void givenUsingApacheCommons_whenConcatenatingUsingChainedIterable_thenCorrect() {
        Collection<String> collectionA = Arrays.asList("S", "T");
        Collection<String> collectionB = Arrays.asList("U", "V");
        Iterable<String> combinedIterables = IterableUtils.chainedIterable(collectionA, collectionB);
        Collection<String> collectionCombined = Lists.newArrayList(combinedIterables);
        Assert.assertEquals(Arrays.asList("S", "T", "U", "V"), collectionCombined);
    }
}

