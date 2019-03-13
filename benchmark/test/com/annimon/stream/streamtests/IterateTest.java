package com.annimon.stream.streamtests;


import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import java.math.BigInteger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public final class IterateTest {
    @Test
    public void testIterate() {
        final BigInteger two = BigInteger.valueOf(2);
        BigInteger sum = Stream.iterate(BigInteger.ONE, new com.annimon.stream.function.UnaryOperator<BigInteger>() {
            @Override
            public BigInteger apply(BigInteger value) {
                return value.multiply(two);
            }
        }).limit(100).reduce(BigInteger.ZERO, new com.annimon.stream.function.BinaryOperator<BigInteger>() {
            @Override
            public BigInteger apply(BigInteger value1, BigInteger value2) {
                return value1.add(value2);
            }
        });
        Assert.assertEquals(new BigInteger("1267650600228229401496703205375"), sum);
    }

    @Test(expected = NullPointerException.class)
    public void testIterateNull() {
        Stream.iterate(1, null);
    }

    @Test(timeout = 2000)
    public void testIterateIssue53() {
        Optional<Integer> res = Stream.iterate(0, new com.annimon.stream.function.UnaryOperator<Integer>() {
            @Override
            public Integer apply(Integer value) {
                return value + 1;
            }
        }).filter(new com.annimon.stream.function.Predicate<Integer>() {
            @Override
            public boolean test(Integer value) {
                return value == 0;
            }
        }).findFirst();
        Assert.assertThat(res, isPresent());
        Assert.assertThat(res.get(), Matchers.is(0));
    }

    @Test
    public void testIterateWithPredicate() {
        com.annimon.stream.function.Predicate<Integer> condition = new com.annimon.stream.function.Predicate<Integer>() {
            @Override
            public boolean test(Integer value) {
                return value < 20;
            }
        };
        com.annimon.stream.function.UnaryOperator<Integer> increment = new com.annimon.stream.function.UnaryOperator<Integer>() {
            @Override
            public Integer apply(Integer t) {
                return t + 5;
            }
        };
        Stream.iterate(0, condition, increment).custom(assertElements(Matchers.contains(0, 5, 10, 15)));
    }
}

