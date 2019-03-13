package com.baeldung.guava.tutorial;


import com.google.common.collect.Streams;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class GuavaStreamsUnitTest {
    private List<Integer> numbers;

    @Test
    public void createStreamsWithCollection() {
        // Deprecated API to create stream from collection
        Stream streamFromCollection = Streams.stream(numbers);
        // Assert.assertNotNull(streamFromCollection);
        StreamUtility.assertStreamEquals(streamFromCollection, numbers.stream());
    }

    @Test
    public void createStreamsWithIterable() {
        Iterable<Integer> numbersIterable = numbers;
        Stream streamFromIterable = Streams.stream(numbersIterable);
        Assert.assertNotNull(streamFromIterable);
        StreamUtility.assertStreamEquals(streamFromIterable, numbers.stream());
    }

    @Test
    public void createStreamsWithIterator() {
        Iterator<Integer> numbersIterator = numbers.iterator();
        Stream streamFromIterator = Streams.stream(numbersIterator);
        Assert.assertNotNull(streamFromIterator);
        StreamUtility.assertStreamEquals(streamFromIterator, numbers.stream());
    }

    @Test
    public void createStreamsWithOptional() {
        Stream streamFromOptional = Streams.stream(Optional.of(1));
        Assert.assertNotNull(streamFromOptional);
        Assert.assertEquals(streamFromOptional.count(), 1);
    }

    @Test
    public void createStreamsWithOptionalLong() {
        LongStream streamFromOptionalLong = Streams.stream(OptionalLong.of(1));
        Assert.assertNotNull(streamFromOptionalLong);
        Assert.assertEquals(streamFromOptionalLong.count(), 1);
    }

    @Test
    public void createStreamsWithOptionalInt() {
        IntStream streamFromOptionalInt = Streams.stream(OptionalInt.of(1));
        // Assert.assertNotNull(streamFromOptionalInt);
        Assert.assertEquals(streamFromOptionalInt.count(), 1);
    }

    @Test
    public void createStreamsWithOptionalDouble() {
        DoubleStream streamFromOptionalDouble = Streams.stream(OptionalDouble.of(1.0));
        // Assert.assertNotNull(streamFromOptionalDouble);
        Assert.assertEquals(streamFromOptionalDouble.count(), 1);
    }

    @Test
    public void concatStreamsOfSameType() {
        List<Integer> oddNumbers = Arrays.asList(1, 3, 5, 7, 9, 11, 13, 15, 17, 19);
        List<Integer> evenNumbers = Arrays.asList(2, 4, 6, 8, 10, 12, 14, 16, 18, 20);
        Stream<Integer> combinedStreams = Streams.concat(oddNumbers.stream(), evenNumbers.stream());
        // Assert.assertNotNull(combinedStreams);
        StreamUtility.assertStreamEquals(combinedStreams, Stream.concat(oddNumbers.stream(), evenNumbers.stream()));
    }

    @Test
    public void concatStreamsOfTypeLongStream() {
        LongStream combinedStreams = Streams.concat(LongStream.range(1, 21), LongStream.range(21, 40));
        Assert.assertNotNull(combinedStreams);
        StreamUtility.assertStreamEquals(combinedStreams, LongStream.range(1, 40));
    }

    @Test
    public void concatStreamsOfTypeIntStream() {
        IntStream combinedStreams = Streams.concat(IntStream.range(1, 20), IntStream.range(21, 40));
        Assert.assertNotNull(combinedStreams);
        StreamUtility.assertStreamEquals(combinedStreams, IntStream.concat(IntStream.range(1, 20), IntStream.range(21, 40)));
    }

    @Test
    public void findLastOfStream() {
        Optional<Integer> lastElement = Streams.findLast(numbers.stream());
        Assert.assertEquals(lastElement.get(), numbers.get(19));
    }

    @Test
    public void mapWithIndexTest() {
        Stream<String> stringStream = Stream.of("a", "b", "c");
        Stream<String> mappedStream = Streams.mapWithIndex(stringStream, ( str, index) -> (str + ":") + index);
        // Assert.assertNotNull(mappedStream);
        Assert.assertEquals(mappedStream.findFirst().get(), "a:0");
    }

    @Test
    public void streamsZipTest() {
        Stream<String> stringSream = Stream.of("a", "b", "c");
        Stream<Integer> intStream = Stream.of(1, 2, 3);
        Stream<String> mappedStream = Streams.zip(stringSream, intStream, ( str, index) -> (str + ":") + index);
        // Assert.assertNotNull(mappedStream);
        Assert.assertEquals(mappedStream.findFirst().get(), "a:1");
    }
}

