package com.insightfullogic.java8.examples.chapter3;


import SampleData.johnColtrane;
import SampleData.theBeatles;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class StreamExercisesTest {
    @Test
    public void external() {
        Assert.assertEquals(4, countBandMembersExternal(Arrays.asList(johnColtrane, theBeatles)));
    }

    @Test
    public void mapExample() {
        Stream<Integer> stream = Stream.of(1, 2, 3);
        List<Integer> values = StreamExercises.StreamExercises.map(stream, ( x) -> x + 1);
        Assert.assertEquals(Arrays.asList(2, 3, 4), values);
    }

    @Test
    public void mapExampleParallel() {
        Stream<Integer> parallelStream = Stream.of(1, 2, 3).parallel();
        List<Integer> values = StreamExercises.StreamExercises.map(parallelStream, ( x) -> x + 1);
        Assert.assertEquals(Arrays.asList(2, 3, 4), values);
    }
}

