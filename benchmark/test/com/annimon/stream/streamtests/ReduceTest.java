package com.annimon.stream.streamtests;


import com.annimon.stream.Functions;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


public final class ReduceTest {
    @Test
    public void testReduceSumFromZero() {
        int result = Stream.range(0, 10).reduce(0, Functions.addition());
        Assert.assertEquals(45, result);
    }

    @Test
    public void testReduceSumFromMinus45() {
        int result = Stream.range(0, 10).reduce((-45), Functions.addition());
        Assert.assertEquals(0, result);
    }

    @Test
    public void testReduceWithAnotherType() {
        int result = Stream.of("a", "bb", "ccc", "dddd").reduce(0, new com.annimon.stream.function.BiFunction<Integer, String, Integer>() {
            @Override
            public Integer apply(Integer length, String s) {
                return length + (s.length());
            }
        });
        Assert.assertEquals(10, result);
    }

    @Test
    public void testReduceOptional() {
        Optional<Integer> result = Stream.range(0, 10).reduce(Functions.addition());
        Assert.assertThat(result, isPresent());
        Assert.assertNotNull(result.get());
        Assert.assertEquals(45, ((int) (result.get())));
    }

    @Test
    public void testReduceOptionalOnEmptyStream() {
        Optional<Integer> result = Stream.<Integer>empty().reduce(Functions.addition());
        Assert.assertThat(result, isEmpty());
        Assert.assertEquals(119, ((int) (result.orElse(119))));
    }
}

