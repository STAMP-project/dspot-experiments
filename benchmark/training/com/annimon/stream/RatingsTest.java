package com.annimon.stream;


import Function.Util;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Real example of calculate ratings.
 */
public class RatingsTest {
    private static Map<String, String> fileContents;

    @Test
    public void testRatings() {
        String ratings = // lines to string
        // Convert to formatted string
        // Sort by total rating descending
        // Calculate summary ratings
        // Group by name
        // calculate sum by line and store in pair <int, string>
        // split line by whitespaces
        // list files
        // read content of files
        Stream.of(RatingsTest.fileContents.keySet()).flatMap(Util.safe(new com.annimon.stream.function.ThrowableFunction<String, Stream<String>, Throwable>() {
            @Override
            public Stream<String> apply(String filename) throws IOException {
                return RatingsTest.readLines(filename);
            }
        })).map(new com.annimon.stream.function.Function<String, String[]>() {
            @Override
            public String[] apply(String line) {
                return line.split("\\s+");
            }
        }).map(new com.annimon.stream.function.Function<String[], IntPair<String>>() {
            @Override
            public IntPair<String> apply(String[] arr) {
                // <sum of marks, name>
                return new IntPair<String>(Stream.of(arr).skip(1).mapToInt(new com.annimon.stream.function.ToIntFunction<String>() {
                    @Override
                    public int applyAsInt(String t) {
                        return Integer.parseInt(t);
                    }
                }).sum(), arr[0]);
            }
        }).groupBy(new com.annimon.stream.function.Function<IntPair<String>, String>() {
            @Override
            public String apply(IntPair<String> t) {
                return t.getSecond();
            }
        }).map(new com.annimon.stream.function.Function<Map.Entry<String, List<IntPair<String>>>, IntPair<String>>() {
            @Override
            public IntPair<String> apply(Map.Entry<String, List<IntPair<String>>> entry) {
                final String name = entry.getKey();
                final int ratings = Stream.of(entry.getValue()).mapToInt(Functions.<String>intPairIndex()).sum();
                return new IntPair<String>(ratings, name);
            }
        }).sortBy(new com.annimon.stream.function.Function<IntPair<String>, Integer>() {
            @Override
            public Integer apply(IntPair<String> value) {
                return -(value.getFirst());
            }
        }).map(new com.annimon.stream.function.Function<IntPair<String>, String>() {
            @Override
            public String apply(IntPair<String> value) {
                return String.format("%12s: %d", value.getSecond(), value.getFirst());
            }
        }).collect(Collectors.joining("\n"));
        /* Scala analogue
        .flatMap(filename => Source.fromFile(filename, "UTF-8").getLines)
        .map(s => s.split("\\s+"))
        .map { arr => (arr(0), arr
        .drop(1)
        .map(_.toInt)
        .sum) }
        .groupBy(_._1)
        .map { case (name, ratings) => (name, ratings.map(_._2).sum)  }
        .toSeq
        .sortBy(- _._2)
        .map { case (name, rating) => "%12s: %d".format(name, rating) }
        .foreach(println);
         */
        Assert.assertEquals(("  LongFlight: 38\n" + ((((" SonicTime3D: 29\n" + "SpaceCatcher: 28\n") + "     Units2D: 21\n") + " aPlatformer: 17\n") + "      Galaxy: 11")), ratings);
    }
}

