package com.insightfullogic.java8.examples.chapter3;


import com.insightfullogic.java8.examples.chapter1.Track;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public class HigherOrderFunctionExamplesTest {
    @Test
    public void collectToList() {
        // BEGIN collect_to_list_1
        List<String> collected = // <1>
        Stream.of("a", "b", "c").collect(Collectors.toList());// <2>

        Assert.assertEquals(Arrays.asList("a", "b", "c"), collected);// <3>

        // END collect_to_list_1
    }

    @Test
    public void mapToUpperCase() {
        // BEGIN map_to_uppercase
        List<String> collected = // <1>
        Stream.of("a", "b", "hello").map(( string) -> string.toUpperCase()).collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList("A", "B", "HELLO"), collected);
        // END map_to_uppercase
    }

    @Test
    public void forToUpperCase() {
        // BEGIN for_to_uppercase
        List<String> collected = new ArrayList<>();
        for (String string : Arrays.asList("a", "b", "hello")) {
            String uppercaseString = string.toUpperCase();
            collected.add(uppercaseString);
        }
        Assert.assertEquals(Arrays.asList("A", "B", "HELLO"), collected);
        // END for_to_uppercase
    }

    @Test
    public void imperativeMaxLength() {
        // BEGIN imperativeMaxLength
        List<Track> tracks = Arrays.asList(new Track("Bakai", 524), new Track("Violets for Your Furs", 378), new Track("Time Was", 451));
        Track shortestTrack = tracks.get(0);
        for (Track track : tracks) {
            if ((track.getLength()) < (shortestTrack.getLength())) {
                shortestTrack = track;
            }
        }
        Assert.assertEquals(tracks.get(1), shortestTrack);
        // END imperativeMaxLength
    }

    @Test
    public void streamsMaxLength() {
        // BEGIN streamsMaxLength
        List<Track> tracks = Arrays.asList(new Track("Bakai", 524), new Track("Violets for Your Furs", 378), new Track("Time Was", 451));
        Track shortestTrack = tracks.stream().min(Comparator.comparing(( track) -> track.getLength())).get();
        Assert.assertEquals(tracks.get(1), shortestTrack);
        // END streamsMaxLength
    }

    @Test
    public void streamsAnyMatch() {
        // BEGIN streamsAnyMatch
        List<Track> tracksOnColtrane = Arrays.asList(new Track("Bakai", 524), new Track("Violets for Your Furs", 378), new Track("Time Was", 451));
        boolean matchLength = tracksOnColtrane.stream().anyMatch(( track) -> (track.getLength()) > 500);
        Assert.assertTrue(matchLength);
        // END streamsAnyMatch
    }

    @Test
    public void imperativeAnyMatch() {
        // BEGIN imperativeAnyMatch
        List<Track> tracksOnColtrane = Arrays.asList(new Track("Bakai", 524), new Track("Violets for Your Furs", 378), new Track("Time Was", 451));
        boolean matchLength = false;
        for (Track track : tracksOnColtrane) {
            if ((track.getLength()) > 500) {
                matchLength = true;
            }
        }
        Assert.assertTrue(matchLength);
        // END imperativeAnyMatch
    }

    @Test
    public void sumUsingReduce() {
        // BEGIN count_using_reduce
        int count = Stream.of(1, 2, 3).reduce(0, ( acc, element) -> acc + element);
        Assert.assertEquals(6, count);
        // END count_using_reduce
    }

    @Test
    public void expandedReduce() {
        // BEGIN expanded_reduce
        BinaryOperator<Integer> accumulator = ( acc, element) -> acc + element;
        int count = accumulator.apply(accumulator.apply(accumulator.apply(0, 1), 2), 3);
        // END expanded_reduce
        Assert.assertEquals(6, count);
    }

    @Test
    public void countUsingReduceFor() {
        // BEGIN count_using_reduce_for
        int acc = 0;
        for (Integer element : Arrays.asList(1, 2, 3)) {
            acc = acc + element;
        }
        Assert.assertEquals(6, acc);
        // END count_using_reduce_for
    }

    @Test
    public void functionalStringsWithNumbers() {
        // BEGIN strings_numbers_filter
        List<String> beginningWithNumbers = Stream.of("a", "1abc", "abc1").filter(( value) -> Character.isDigit(value.charAt(0))).collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList("1abc"), beginningWithNumbers);
        // END strings_numbers_filter
    }

    @Test
    public void imperativeStringsWithNumbers() {
        // BEGIN strings_numbers_for
        List<String> beginningWithNumbers = new ArrayList<>();
        for (String value : Arrays.asList("a", "1abc", "abc1")) {
            if (Character.isDigit(value.charAt(0))) {
                beginningWithNumbers.add(value);
            }
        }
        Assert.assertEquals(Arrays.asList("1abc"), beginningWithNumbers);
        // END strings_numbers_for
    }

    @Test
    public void flatMapCharacters() {
        // BEGIN flatmap_characters
        List<Integer> together = Stream.of(Arrays.asList(1, 2), Arrays.asList(3, 4)).flatMap(( numbers) -> numbers.stream()).collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4), together);
        // END flatmap_characters
    }
}

