package com.annimon.stream;


import com.annimon.stream.function.LongConsumer;
import com.annimon.stream.function.LongFunction;
import com.annimon.stream.function.LongSupplier;
import com.annimon.stream.function.LongToIntFunction;
import com.annimon.stream.function.LongUnaryOperator;
import com.annimon.stream.test.hamcrest.OptionalIntMatcher;
import com.annimon.stream.test.hamcrest.OptionalMatcher;
import java.util.NoSuchElementException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link OptionalLong}
 */
public class OptionalLongTest {
    @Test
    public void testGetWithPresentValue() {
        long value = OptionalLong.of(10).getAsLong();
        Assert.assertEquals(10, value);
    }

    @Test
    public void testOfNullableWithPresentValue() {
        Assert.assertThat(OptionalLong.ofNullable(10L), hasValue(10L));
    }

    @Test
    public void testOfNullableWithAbsentValue() {
        Assert.assertThat(OptionalLong.ofNullable(null), isEmpty());
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetOnEmptyOptional() {
        OptionalLong.empty().getAsLong();
    }

    @Test
    public void testIsPresent() {
        Assert.assertThat(OptionalLong.of(10), isPresent());
    }

    @Test
    public void testIsPresentOnEmptyOptional() {
        Assert.assertThat(OptionalLong.empty(), isEmpty());
    }

    @Test
    public void testIfPresent() {
        OptionalLong.empty().ifPresent(new LongConsumer() {
            @Override
            public void accept(long value) {
                Assert.fail();
            }
        });
        OptionalLong.of(15).ifPresent(new LongConsumer() {
            @Override
            public void accept(long value) {
                Assert.assertEquals(15, value);
            }
        });
    }

    @Test
    public void testIfPresentOrElseWhenValuePresent() {
        OptionalLong.of(10L).ifPresentOrElse(new LongConsumer() {
            @Override
            public void accept(long value) {
                Assert.assertThat(value, Matchers.is(10L));
            }
        }, new Runnable() {
            @Override
            public void run() {
                Assert.fail("Should not execute empty action when value is present.");
            }
        });
    }

    @Test(expected = RuntimeException.class)
    public void testIfPresentOrElseWhenValueAbsent() {
        OptionalLong.empty().ifPresentOrElse(new LongConsumer() {
            @Override
            public void accept(long value) {
                Assert.fail();
            }
        }, new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException();
            }
        });
    }

    @Test
    public void testIfPresentOrElseWhenValuePresentAndEmptyActionNull() {
        OptionalLong.of(10).ifPresentOrElse(new LongConsumer() {
            @Override
            public void accept(long value) {
                Assert.assertThat(value, Matchers.is(10L));
            }
        }, null);
    }

    @Test(expected = RuntimeException.class)
    public void testIfPresentOrElseWhenValueAbsentAndConsumerNull() {
        OptionalLong.empty().ifPresentOrElse(null, new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException();
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void testIfPresentOrElseWhenValuePresentAndConsumerNull() {
        OptionalLong.of(10).ifPresentOrElse(null, new Runnable() {
            @Override
            public void run() {
                Assert.fail("Should not have been executed.");
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void testIfPresentOrElseWhenValueAbsentAndEmptyActionNull() {
        OptionalLong.empty().ifPresentOrElse(new LongConsumer() {
            @Override
            public void accept(long value) {
                Assert.fail("Should not have been executed.");
            }
        }, null);
    }

    @Test
    public void testExecuteIfPresent() {
        long value = OptionalLong.of(10).executeIfPresent(new LongConsumer() {
            @Override
            public void accept(long value) {
                Assert.assertEquals(10, value);
            }
        }).getAsLong();
        Assert.assertEquals(10L, value);
    }

    @Test
    public void testExecuteIfPresentOnAbsentValue() {
        OptionalLong.empty().executeIfPresent(new LongConsumer() {
            @Override
            public void accept(long value) {
                Assert.fail();
            }
        });
    }

    @Test(expected = RuntimeException.class)
    public void testExecuteIfAbsent() {
        OptionalLong.empty().executeIfAbsent(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException();
            }
        });
    }

    @Test
    public void testExecuteIfAbsentOnPresentValue() {
        OptionalLong.of(10).executeIfAbsent(new Runnable() {
            @Override
            public void run() {
                Assert.fail();
            }
        });
    }

    @Test
    public void testCustomIntermediate() {
        OptionalLong result = OptionalLong.of(10L).custom(new com.annimon.stream.function.Function<OptionalLong, OptionalLong>() {
            @Override
            public OptionalLong apply(OptionalLong optional) {
                return optional.filter(Functions.remainderLong(2));
            }
        });
        Assert.assertThat(result, hasValue(10L));
    }

    @Test
    public void testCustomTerminal() {
        Long result = OptionalLong.empty().custom(new com.annimon.stream.function.Function<OptionalLong, Long>() {
            @Override
            public Long apply(OptionalLong optional) {
                return optional.orElse(0L);
            }
        });
        Assert.assertThat(result, Matchers.is(0L));
    }

    @Test(expected = NullPointerException.class)
    public void testCustomException() {
        OptionalLong.empty().custom(null);
    }

    @Test
    public void testFilter() {
        OptionalLong result;
        result = OptionalLong.of(4).filter(Functions.remainderLong(2));
        Assert.assertThat(result, hasValue(4));
        result = OptionalLong.empty().filter(Functions.remainderLong(2));
        Assert.assertThat(result, isEmpty());
        result = OptionalLong.of(9).filter(Functions.remainderLong(2));
        Assert.assertThat(result, isEmpty());
    }

    @Test
    public void testFilterNot() {
        OptionalLong result;
        result = OptionalLong.of(4).filterNot(Functions.remainderLong(3));
        Assert.assertThat(result, hasValue(4));
        result = OptionalLong.empty().filterNot(Functions.remainderLong(3));
        Assert.assertThat(result, isEmpty());
        result = OptionalLong.of(9).filterNot(Functions.remainderLong(3));
        Assert.assertThat(result, isEmpty());
    }

    @Test
    public void testMap() {
        final LongUnaryOperator negatorFunction = new LongUnaryOperator() {
            @Override
            public long applyAsLong(long operand) {
                return -operand;
            }
        };
        OptionalLong result;
        result = OptionalLong.empty().map(negatorFunction);
        Assert.assertThat(result, isEmpty());
        result = OptionalLong.of(10).map(negatorFunction);
        Assert.assertThat(result, hasValue((-10)));
    }

    @Test
    public void testMapToInt() {
        final LongToIntFunction mapper = new LongToIntFunction() {
            @Override
            public int applyAsInt(long operand) {
                return ((int) (operand / 10000000000L));
            }
        };
        OptionalInt result;
        result = OptionalLong.empty().mapToInt(mapper);
        Assert.assertThat(result, OptionalIntMatcher.isEmpty());
        result = OptionalLong.of(100000000000L).mapToInt(mapper);
        Assert.assertThat(result, OptionalIntMatcher.hasValue(10));
    }

    @Test
    public void testMapToObj() {
        final LongFunction<String> asciiToString = new LongFunction<String>() {
            @Override
            public String apply(long value) {
                return String.valueOf(((char) (value)));
            }
        };
        Optional<String> result;
        result = OptionalLong.empty().mapToObj(asciiToString);
        Assert.assertThat(result, OptionalMatcher.isEmpty());
        result = OptionalLong.of(65).mapToObj(asciiToString);
        Assert.assertThat(result, OptionalMatcher.hasValue("A"));
        result = OptionalLong.empty().mapToObj(new LongFunction<String>() {
            @Override
            public String apply(long value) {
                return null;
            }
        });
        Assert.assertThat(result, OptionalMatcher.isEmpty());
    }

    @Test
    public void testStream() {
        long count = OptionalLong.of(10).stream().count();
        Assert.assertThat(count, Matchers.is(1L));
    }

    @Test
    public void testStreamOnEmptyOptional() {
        long count = OptionalLong.empty().stream().count();
        Assert.assertThat(count, Matchers.is(0L));
    }

    @Test
    public void testOr() {
        long value = OptionalLong.of(42).or(new com.annimon.stream.function.Supplier<OptionalLong>() {
            @Override
            public OptionalLong get() {
                return OptionalLong.of(19);
            }
        }).getAsLong();
        Assert.assertEquals(42, value);
    }

    @Test
    public void testOrOnEmptyOptional() {
        long value = OptionalLong.empty().or(new com.annimon.stream.function.Supplier<OptionalLong>() {
            @Override
            public OptionalLong get() {
                return OptionalLong.of(19);
            }
        }).getAsLong();
        Assert.assertEquals(19, value);
    }

    @Test
    public void testOrOnEmptyOptionalAndEmptySupplierOptional() {
        final OptionalLong optional = OptionalLong.empty().or(new com.annimon.stream.function.Supplier<OptionalLong>() {
            @Override
            public OptionalLong get() {
                return OptionalLong.empty();
            }
        });
        Assert.assertThat(optional, isEmpty());
    }

    @Test
    public void testOrElse() {
        Assert.assertEquals(17, OptionalLong.empty().orElse(17));
        Assert.assertEquals(17, OptionalLong.of(17).orElse(0));
    }

    @Test
    public void testOrElseGet() {
        Assert.assertEquals(21, OptionalLong.empty().orElseGet(new LongSupplier() {
            @Override
            public long getAsLong() {
                return 21;
            }
        }));
        Assert.assertEquals(21, OptionalLong.of(21).orElseGet(new LongSupplier() {
            @Override
            public long getAsLong() {
                throw new IllegalStateException();
            }
        }));
    }

    @Test
    public void testOrElseThrowWithPresentValue() {
        long value = OptionalLong.of(10).orElseThrow();
        Assert.assertEquals(10, value);
    }

    @Test(expected = NoSuchElementException.class)
    public void testOrElseThrowOnEmptyOptional() {
        OptionalLong.empty().orElseThrow();
    }

    @Test
    public void testOrElseThrow() {
        try {
            Assert.assertEquals(25, OptionalLong.of(25).orElseThrow(new com.annimon.stream.function.Supplier<NoSuchElementException>() {
                @Override
                public NoSuchElementException get() {
                    throw new IllegalStateException();
                }
            }));
        } catch (Exception e) {
            throw new IllegalStateException();
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void testOrElseThrow2() {
        Assert.assertEquals(25, OptionalLong.empty().orElseThrow(new com.annimon.stream.function.Supplier<NoSuchElementException>() {
            @Override
            public NoSuchElementException get() {
                return new NoSuchElementException();
            }
        }));
    }

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Test
    public void testEquals() {
        Assert.assertEquals(OptionalLong.empty(), OptionalLong.empty());
        Assert.assertNotEquals(OptionalLong.empty(), Optional.empty());
        Assert.assertEquals(OptionalLong.of(42), OptionalLong.of(42));
        Assert.assertNotEquals(OptionalLong.of(41), OptionalLong.of(42));
        Assert.assertNotEquals(OptionalLong.of(0), OptionalLong.empty());
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(OptionalLong.empty().hashCode(), 0);
        Assert.assertEquals(31, OptionalLong.of(31).hashCode());
    }

    @Test
    public void testToString() {
        Assert.assertEquals(OptionalLong.empty().toString(), "OptionalLong.empty");
        Assert.assertEquals(OptionalLong.of(42).toString(), "OptionalLong[42]");
    }
}

