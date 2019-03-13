package com.annimon.stream.streamtests;


import com.annimon.stream.Stream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class OfNullableTest {
    @Test
    public void testStreamOfNullable() {
        String t = null;
        Assert.assertThat(Stream.ofNullable(t), isEmpty());
        Assert.assertThat(Stream.ofNullable(5), elements(Matchers.contains(5)));
    }

    @Test
    public void testStreamOfNullableArray() {
        String[] t = null;
        Assert.assertThat(Stream.ofNullable(t), isEmpty());
        Assert.assertThat(Stream.ofNullable(new Integer[]{ 1, 2, 3 }), elements(Matchers.contains(1, 2, 3)));
    }

    @Test
    public void testStreamOfNullableMap() {
        Map<Integer, String> t = null;
        Assert.assertThat(Stream.ofNullable(t), isEmpty());
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        map.put(1, 2);
        map.put(3, 4);
        Stream.ofNullable(map).flatMap(new com.annimon.stream.function.Function<Map.Entry<Integer, Integer>, Stream<Integer>>() {
            @Override
            public Stream<Integer> apply(Map.Entry<Integer, Integer> e) {
                return Stream.of(e.getKey(), e.getValue());
            }
        }).custom(assertElements(Matchers.contains(1, 2, 3, 4)));
    }

    @Test
    public void testStreamOfNullableWithIterator() {
        Assert.assertThat(Stream.ofNullable(((Iterator<?>) (null))), isEmpty());
        Assert.assertThat(Stream.ofNullable(Arrays.asList(5, 10, 15).iterator()), elements(Matchers.contains(5, 10, 15)));
    }

    @Test
    public void testStreamOfNullableWithIterable() {
        Assert.assertThat(Stream.ofNullable(((List<?>) (null))), isEmpty());
        Assert.assertThat(Stream.ofNullable(Arrays.asList(5, 10, 15)), elements(Matchers.contains(5, 10, 15)));
    }
}

