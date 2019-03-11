package com.baeldung.nullsafecollectionstreams;


import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;
import org.junit.Test;


public class NullSafeCollectionStreamsUsingCommonsEmptyIfNullUnitTest {
    private final NullSafeCollectionStreamsUsingCommonsEmptyIfNull instance = new NullSafeCollectionStreamsUsingCommonsEmptyIfNull();

    @Test
    public void whenCollectionIsNull_thenExpectAnEmptyStream() {
        Collection<String> collection = null;
        Stream<String> expResult = Stream.empty();
        Stream<String> result = instance.collectionAsStream(collection);
        NullSafeCollectionStreamsUsingCommonsEmptyIfNullUnitTest.assertStreamEquals(expResult, result);
    }

    @Test
    public void whenCollectionHasElements_thenExpectAStreamOfExactlyTheSameElements() {
        Collection<String> collection = Arrays.asList("a", "b", "c");
        Stream<String> expResult = Arrays.stream(new String[]{ "a", "b", "c" });
        Stream<String> result = instance.collectionAsStream(collection);
        NullSafeCollectionStreamsUsingCommonsEmptyIfNullUnitTest.assertStreamEquals(expResult, result);
    }
}

