package com.baeldung.stream;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class StreamAddUnitTest {
    @Test
    public void givenStream_whenAppendingObject_thenAppended() {
        Stream<String> anStream = Stream.of("a", "b", "c", "d", "e");
        Stream<String> newStream = Stream.concat(anStream, Stream.of("A"));
        List<String> resultList = newStream.collect(Collectors.toList());
        Assert.assertEquals(resultList.get(((resultList.size()) - 1)), "A");
    }

    @Test
    public void givenStream_whenPrependingObject_thenPrepended() {
        Stream<Integer> anStream = Stream.of(1, 2, 3, 4, 5);
        Stream<Integer> newStream = Stream.concat(Stream.of(99), anStream);
        Assert.assertEquals(newStream.findFirst().get(), ((Integer) (99)));
    }

    @Test
    public void givenStream_whenInsertingObject_thenInserted() {
        Stream<Double> anStream = Stream.of(1.1, 2.2, 3.3);
        Stream<Double> newStream = insertInStream(anStream, 9.9, 3);
        List<Double> resultList = newStream.collect(Collectors.toList());
        Assert.assertEquals(resultList.get(3), ((Double) (9.9)));
    }
}

