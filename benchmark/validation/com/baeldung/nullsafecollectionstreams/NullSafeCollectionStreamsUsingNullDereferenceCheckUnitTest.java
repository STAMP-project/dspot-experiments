package com.baeldung.nullsafecollectionstreams;


import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;
import org.junit.Test;


/**
 *
 *
 * @author Kwaje Anthony <kwajeanthony@gmail.com>
 */
public class NullSafeCollectionStreamsUsingNullDereferenceCheckUnitTest {
    private final NullSafeCollectionStreamsUsingNullDereferenceCheck instance = new NullSafeCollectionStreamsUsingNullDereferenceCheck();

    @Test
    public void whenCollectionIsNull_thenExpectAnEmptyStream() {
        Collection<String> collection = null;
        Stream<String> expResult = Stream.empty();
        Stream<String> result = instance.collectionAsStream(collection);
        NullSafeCollectionStreamsUsingNullDereferenceCheckUnitTest.assertStreamEquals(expResult, result);
    }

    @Test
    public void whenCollectionHasElements_thenExpectAStreamOfExactlyTheSameElements() {
        Collection<String> collection = Arrays.asList("a", "b", "c");
        Stream<String> expResult = Arrays.stream(new String[]{ "a", "b", "c" });
        Stream<String> result = instance.collectionAsStream(collection);
        NullSafeCollectionStreamsUsingNullDereferenceCheckUnitTest.assertStreamEquals(expResult, result);
    }
}

