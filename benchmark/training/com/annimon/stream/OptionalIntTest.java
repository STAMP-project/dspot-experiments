package com.annimon.stream;


import com.annimon.stream.function.IntConsumer;
import com.annimon.stream.function.IntFunction;
import com.annimon.stream.function.IntSupplier;
import com.annimon.stream.function.IntToDoubleFunction;
import com.annimon.stream.function.IntToLongFunction;
import com.annimon.stream.function.IntUnaryOperator;
import com.annimon.stream.test.hamcrest.OptionalDoubleMatcher;
import com.annimon.stream.test.hamcrest.OptionalLongMatcher;
import com.annimon.stream.test.hamcrest.OptionalMatcher;
import java.util.NoSuchElementException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link OptionalInt}
 */
public class OptionalIntTest {
    @Test
    public void testGetWithPresentValue() {
        int value = OptionalInt.of(10).getAsInt();
        Assert.assertEquals(10, value);
    }

    @Test
    public void testOfNullableWithPresentValue() {
        Assert.assertThat(OptionalInt.ofNullable(10), hasValue(10));
    }

    @Test
    public void testOfNullableWithAbsentValue() {
        Assert.assertThat(OptionalInt.ofNullable(null), isEmpty());
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetOnEmptyOptional() {
        OptionalInt.empty().getAsInt();
    }

    @Test
    public void testIsPresent() {
        Assert.assertThat(OptionalInt.of(10), isPresent());
    }

    @Test
    public void testIsPresentOnEmptyOptional() {
        Assert.assertThat(OptionalInt.empty(), isEmpty());
    }

    @Test
    public void testIfPresent() {
        OptionalInt.empty().ifPresent(new IntConsumer() {
            @Override
            public void accept(int value) {
                Assert.fail();
            }
        });
        OptionalInt.of(15).ifPresent(new IntConsumer() {
            @Override
            public void accept(int value) {
                Assert.assertEquals(15, value);
            }
        });
    }

    @Test
    public void testIfPresentOrElseWhenValuePresent() {
        OptionalInt.of(10).ifPresentOrElse(new IntConsumer() {
            @Override
            public void accept(int value) {
                Assert.assertThat(value, Matchers.is(10));
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
        OptionalInt.empty().ifPresentOrElse(new IntConsumer() {
            @Override
            public void accept(int value) {
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
        OptionalInt.of(10).ifPresentOrElse(new IntConsumer() {
            @Override
            public void accept(int value) {
                Assert.assertThat(value, Matchers.is(10));
            }
        }, null);
    }

    @Test(expected = RuntimeException.class)
    public void testIfPresentOrElseWhenValueAbsentAndConsumerNull() {
        OptionalInt.empty().ifPresentOrElse(null, new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException();
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void testIfPresentOrElseWhenValuePresentAndConsumerNull() {
        OptionalInt.of(10).ifPresentOrElse(null, new Runnable() {
            @Override
            public void run() {
                Assert.fail("Should not have been executed.");
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void testIfPresentOrElseWhenValueAbsentAndEmptyActionNull() {
        OptionalInt.empty().ifPresentOrElse(new IntConsumer() {
            @Override
            public void accept(int value) {
                Assert.fail("Should not have been executed.");
            }
        }, null);
    }

    @Test
    public void testExecuteIfPresent() {
        int value = OptionalInt.of(10).executeIfPresent(new IntConsumer() {
            @Override
            public void accept(int value) {
                Assert.assertEquals(10, value);
            }
        }).getAsInt();
        Assert.assertEquals(10, value);
    }

    @Test
    public void testExecuteIfPresentOnAbsentValue() {
        OptionalInt.empty().executeIfPresent(new IntConsumer() {
            @Override
            public void accept(int value) {
                Assert.fail();
            }
        });
    }

    @Test(expected = RuntimeException.class)
    public void testExecuteIfAbsent() {
        OptionalInt.empty().executeIfAbsent(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException();
            }
        });
    }

    @Test
    public void testExecuteIfAbsentOnPresentValue() {
        OptionalInt.of(10).executeIfAbsent(new Runnable() {
            @Override
            public void run() {
                Assert.fail();
            }
        });
    }

    @Test
    public void testCustomIntermediate() {
        OptionalInt result = OptionalInt.of(10).custom(new com.annimon.stream.function.Function<OptionalInt, OptionalInt>() {
            @Override
            public OptionalInt apply(OptionalInt optional) {
                return optional.filter(Functions.remainderInt(2));
            }
        });
        Assert.assertThat(result, hasValue(10));
    }

    @Test
    public void testCustomTerminal() {
        Integer result = OptionalInt.empty().custom(new com.annimon.stream.function.Function<OptionalInt, Integer>() {
            @Override
            public Integer apply(OptionalInt optional) {
                return optional.orElse(0);
            }
        });
        Assert.assertThat(result, Matchers.is(0));
    }

    @Test(expected = NullPointerException.class)
    public void testCustomException() {
        OptionalInt.empty().custom(null);
    }

    @Test
    public void testFilter() {
        OptionalInt result;
        result = OptionalInt.of(4).filter(Functions.remainderInt(2));
        Assert.assertThat(result, hasValue(4));
        result = OptionalInt.empty().filter(Functions.remainderInt(2));
        Assert.assertThat(result, isEmpty());
        result = OptionalInt.of(9).filter(Functions.remainderInt(2));
        Assert.assertThat(result, isEmpty());
    }

    @Test
    public void testFilterNot() {
        OptionalInt result;
        result = OptionalInt.of(4).filterNot(Functions.remainderInt(3));
        Assert.assertThat(result, hasValue(4));
        result = OptionalInt.empty().filterNot(Functions.remainderInt(3));
        Assert.assertThat(result, isEmpty());
        result = OptionalInt.of(9).filterNot(Functions.remainderInt(3));
        Assert.assertThat(result, isEmpty());
    }

    @Test
    public void testMap() {
        final IntUnaryOperator negatorFunction = new IntUnaryOperator() {
            @Override
            public int applyAsInt(int operand) {
                return -operand;
            }
        };
        OptionalInt result;
        result = OptionalInt.empty().map(negatorFunction);
        Assert.assertThat(result, isEmpty());
        result = OptionalInt.of(10).map(negatorFunction);
        Assert.assertThat(result, hasValue((-10)));
    }

    @Test
    public void testMapToObj() {
        final IntFunction<String> asciiToString = new IntFunction<String>() {
            @Override
            public String apply(int value) {
                return String.valueOf(((char) (value)));
            }
        };
        Optional<String> result;
        result = OptionalInt.empty().mapToObj(asciiToString);
        Assert.assertThat(result, OptionalMatcher.isEmpty());
        result = OptionalInt.of(65).mapToObj(asciiToString);
        Assert.assertThat(result, OptionalMatcher.hasValue("A"));
        result = OptionalInt.empty().mapToObj(new IntFunction<String>() {
            @Override
            public String apply(int value) {
                return null;
            }
        });
        Assert.assertThat(result, OptionalMatcher.isEmpty());
    }

    @Test
    public void testMapToLong() {
        final IntToLongFunction mapper = new IntToLongFunction() {
            @Override
            public long applyAsLong(int value) {
                return value * 10000000000L;
            }
        };
        OptionalLong result;
        result = OptionalInt.empty().mapToLong(mapper);
        Assert.assertThat(result, OptionalLongMatcher.isEmpty());
        result = OptionalInt.of(65).mapToLong(mapper);
        Assert.assertThat(result, OptionalLongMatcher.hasValue(650000000000L));
    }

    @Test
    public void testMapToDouble() {
        final IntToDoubleFunction mapper = new IntToDoubleFunction() {
            @Override
            public double applyAsDouble(int value) {
                return value / 100.0;
            }
        };
        OptionalDouble result;
        result = OptionalInt.empty().mapToDouble(mapper);
        Assert.assertThat(result, OptionalDoubleMatcher.isEmpty());
        result = OptionalInt.of(65).mapToDouble(mapper);
        Assert.assertThat(result, OptionalDoubleMatcher.hasValueThat(Matchers.closeTo(0.65, 1.0E-4)));
    }

    @Test
    public void testStream() {
        long count = OptionalInt.of(10).stream().count();
        Assert.assertThat(count, Matchers.is(1L));
    }

    @Test
    public void testStreamOnEmptyOptional() {
        long count = OptionalInt.empty().stream().count();
        Assert.assertThat(count, Matchers.is(0L));
    }

    @Test
    public void testOr() {
        int value = OptionalInt.of(42).or(new com.annimon.stream.function.Supplier<OptionalInt>() {
            @Override
            public OptionalInt get() {
                return OptionalInt.of(19);
            }
        }).getAsInt();
        Assert.assertEquals(42, value);
    }

    @Test
    public void testOrOnEmptyOptional() {
        int value = OptionalInt.empty().or(new com.annimon.stream.function.Supplier<OptionalInt>() {
            @Override
            public OptionalInt get() {
                return OptionalInt.of(19);
            }
        }).getAsInt();
        Assert.assertEquals(19, value);
    }

    @Test
    public void testOrOnEmptyOptionalAndEmptySupplierOptional() {
        final OptionalInt optional = OptionalInt.empty().or(new com.annimon.stream.function.Supplier<OptionalInt>() {
            @Override
            public OptionalInt get() {
                return OptionalInt.empty();
            }
        });
        Assert.assertThat(optional, isEmpty());
    }

    @Test
    public void testOrElse() {
        Assert.assertEquals(17, OptionalInt.empty().orElse(17));
        Assert.assertEquals(17, OptionalInt.of(17).orElse(0));
    }

    @Test
    public void testOrElseGet() {
        Assert.assertEquals(21, OptionalInt.empty().orElseGet(new IntSupplier() {
            @Override
            public int getAsInt() {
                return 21;
            }
        }));
        Assert.assertEquals(21, OptionalInt.of(21).orElseGet(new IntSupplier() {
            @Override
            public int getAsInt() {
                throw new IllegalStateException();
            }
        }));
    }

    @Test
    public void testOrElseThrowWithPresentValue() {
        int value = OptionalInt.of(10).orElseThrow();
        Assert.assertEquals(10, value);
    }

    @Test(expected = NoSuchElementException.class)
    public void testOrElseThrowOnEmptyOptional() {
        OptionalInt.empty().orElseThrow();
    }

    @Test
    public void testOrElseThrow() {
        try {
            Assert.assertEquals(25, OptionalInt.of(25).orElseThrow(new com.annimon.stream.function.Supplier<NoSuchElementException>() {
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
        Assert.assertEquals(25, OptionalInt.empty().orElseThrow(new com.annimon.stream.function.Supplier<NoSuchElementException>() {
            @Override
            public NoSuchElementException get() {
                return new NoSuchElementException();
            }
        }));
    }

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Test
    public void testEquals() {
        Assert.assertEquals(OptionalInt.empty(), OptionalInt.empty());
        Assert.assertNotEquals(OptionalInt.empty(), Optional.empty());
        Assert.assertEquals(OptionalInt.of(42), OptionalInt.of(42));
        Assert.assertNotEquals(OptionalInt.of(41), OptionalInt.of(42));
        Assert.assertNotEquals(OptionalInt.of(0), OptionalInt.empty());
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(OptionalInt.empty().hashCode(), 0);
        Assert.assertEquals(31, OptionalInt.of(31).hashCode());
    }

    @Test
    public void testToString() {
        Assert.assertEquals(OptionalInt.empty().toString(), "OptionalInt.empty");
        Assert.assertEquals(OptionalInt.of(42).toString(), "OptionalInt[42]");
    }
}

