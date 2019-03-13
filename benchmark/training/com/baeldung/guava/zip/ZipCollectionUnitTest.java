package com.baeldung.guava.zip;


import com.google.common.collect.Streams;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.junit.Assert;
import org.junit.Test;


public class ZipCollectionUnitTest {
    private List<String> names;

    private List<Integer> ages;

    private List<String> expectedOutput;

    @Test
    public void zipCollectionUsingGuava21() {
        List<String> output = Streams.zip(names.stream(), ages.stream(), ( name, age) -> (name + ":") + age).collect(Collectors.toList());
        Assert.assertEquals(output, expectedOutput);
    }

    @Test
    public void zipCollectionUsingIntStream() {
        List<String> output = IntStream.range(0, Math.min(names.size(), ages.size())).mapToObj(( i) -> ((names.get(i)) + ":") + (ages.get(i))).collect(Collectors.toList());
        Assert.assertEquals(output, expectedOutput);
    }

    @Test
    public void zipCollectionUsingJool() {
        Seq<String> output = Seq.of("John", "Jane", "Jack").zip(Seq.of(24, 25, 27), ( x, y) -> (x + ":") + y);
        Assert.assertEquals(output.toList(), expectedOutput);
    }

    @Test
    public void zipCollectionUsingJoolTuple() {
        Seq<Tuple2<String, Integer>> output = Seq.of("John", "Jane", "Dennis").zip(Seq.of(24, 25, 27));
        Tuple2<String, Integer> element1 = new Tuple2<String, Integer>("John", 24);
        Tuple2<String, Integer> element2 = new Tuple2<String, Integer>("Jane", 25);
        Tuple2<String, Integer> element3 = new Tuple2<String, Integer>("Dennis", 27);
        List<Tuple2> expectedOutput = Arrays.asList(element1, element2, element3);
        Assert.assertEquals(output.collect(Collectors.toList()), expectedOutput);
    }

    @Test
    public void zipCollectionUsingJoolWithIndex() {
        Seq<Tuple2<String, Long>> output = Seq.of("John", "Jane", "Dennis").zipWithIndex();
        Tuple2<String, Long> element1 = new Tuple2("John", 0L);
        Tuple2<String, Long> element2 = new Tuple2("Jane", 1L);
        Tuple2<String, Long> element3 = new Tuple2("Dennis", 2L);
        List<Tuple2> expectedOutput = Arrays.asList(element1, element2, element3);
        Assert.assertEquals(output.collect(Collectors.toList()), expectedOutput);
    }
}

