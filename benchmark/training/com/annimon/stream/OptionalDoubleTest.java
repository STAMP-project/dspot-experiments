package com.annimon.stream;


import com.annimon.stream.function.DoubleConsumer;
import com.annimon.stream.function.DoubleFunction;
import com.annimon.stream.function.DoubleSupplier;
import com.annimon.stream.function.DoubleToIntFunction;
import com.annimon.stream.function.DoubleToLongFunction;
import com.annimon.stream.function.DoubleUnaryOperator;
import com.annimon.stream.test.hamcrest.OptionalMatcher;
import java.util.NoSuchElementException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link OptionalDouble}
 */
public class OptionalDoubleTest {
    @Test
    public void testGetWithPresentValue() {
        double value = OptionalDouble.of(10.123).getAsDouble();
        Assert.assertThat(value, Matchers.closeTo(10.123, 1.0E-4));
    }

    @Test
    public void testOfNullableWithPresentValue() {
        Assert.assertThat(OptionalDouble.ofNullable(10.123), hasValueThat(Matchers.closeTo(10.123, 1.0E-4)));
    }

    @Test
    public void testOfNullableWithAbsentValue() {
        Assert.assertThat(OptionalDouble.ofNullable(null), isEmpty());
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetOnEmptyOptional() {
        OptionalDouble.empty().getAsDouble();
    }

    @Test
    public void testIsPresent() {
        Assert.assertTrue(OptionalDouble.of(10.123).isPresent());
    }

    @Test
    public void testIsPresentOnEmptyOptional() {
        Assert.assertFalse(OptionalDouble.empty().isPresent());
    }

    @Test
    public void testIfPresent() {
        OptionalDouble.empty().ifPresent(new DoubleConsumer() {
            @Override
            public void accept(double value) {
                Assert.fail();
            }
        });
        OptionalDouble.of(10.123).ifPresent(new DoubleConsumer() {
            @Override
            public void accept(double value) {
                Assert.assertThat(value, Matchers.closeTo(10.123, 1.0E-4));
            }
        });
    }

    @Test
    public void testIfPresentOrElseWhenValuePresent() {
        OptionalDouble.of(Math.PI).ifPresentOrElse(new DoubleConsumer() {
            @Override
            public void accept(double value) {
                Assert.assertThat(value, Matchers.closeTo(Math.PI, 1.0E-4));
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
        OptionalDouble.empty().ifPresentOrElse(new DoubleConsumer() {
            @Override
            public void accept(double value) {
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
        OptionalDouble.of(Math.PI).ifPresentOrElse(new DoubleConsumer() {
            @Override
            public void accept(double value) {
                Assert.assertThat(value, Matchers.closeTo(Math.PI, 1.0E-4));
            }
        }, null);
    }

    @Test(expected = RuntimeException.class)
    public void testIfPresentOrElseWhenValueAbsentAndConsumerNull() {
        OptionalDouble.empty().ifPresentOrElse(null, new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException();
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void testIfPresentOrElseWhenValuePresentAndConsumerNull() {
        OptionalDouble.of(Math.PI).ifPresentOrElse(null, new Runnable() {
            @Override
            public void run() {
                Assert.fail("Should not have been executed.");
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void testIfPresentOrElseWhenValueAbsentAndEmptyActionNull() {
        OptionalDouble.empty().ifPresentOrElse(new DoubleConsumer() {
            @Override
            public void accept(double value) {
                Assert.fail("Should not have been executed.");
            }
        }, null);
    }

    @Test
    public void testExecuteIfPresent() {
        double value = OptionalDouble.of(10.123).executeIfPresent(new DoubleConsumer() {
            @Override
            public void accept(double value) {
                Assert.assertThat(value, Matchers.closeTo(10.123, 1.0E-4));
            }
        }).getAsDouble();
        Assert.assertThat(value, Matchers.closeTo(10.123, 1.0E-4));
    }

    @Test
    public void testExecuteIfPresentOnAbsentValue() {
        OptionalDouble.empty().executeIfPresent(new DoubleConsumer() {
            @Override
            public void accept(double value) {
                Assert.fail();
            }
        });
    }

    @Test(expected = RuntimeException.class)
    public void testExecuteIfAbsent() {
        OptionalDouble.empty().executeIfAbsent(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException();
            }
        });
    }

    @Test
    public void testExecuteIfAbsentOnPresentValue() {
        OptionalDouble.of(10.123).executeIfAbsent(new Runnable() {
            @Override
            public void run() {
                Assert.fail();
            }
        });
    }

    @Test
    public void testCustomIntermediate() {
        OptionalDouble result = OptionalDouble.of(10).custom(new com.annimon.stream.function.Function<OptionalDouble, OptionalDouble>() {
            @Override
            public OptionalDouble apply(OptionalDouble optional) {
                return optional.filter(Functions.greaterThan(Math.PI));
            }
        });
        Assert.assertThat(result, hasValueThat(Matchers.closeTo(10, 1.0E-4)));
    }

    @Test
    public void testCustomTerminal() {
        Double result = OptionalDouble.empty().custom(new com.annimon.stream.function.Function<OptionalDouble, Double>() {
            @Override
            public Double apply(OptionalDouble optional) {
                return optional.orElse(0);
            }
        });
        Assert.assertThat(result, Matchers.closeTo(0, 1.0E-4));
    }

    @Test(expected = NullPointerException.class)
    public void testCustomException() {
        OptionalDouble.empty().custom(null);
    }

    @Test
    public void testFilter() {
        OptionalDouble result;
        result = OptionalDouble.of(10.0).filter(Functions.greaterThan(Math.PI));
        Assert.assertThat(result, hasValueThat(Matchers.closeTo(10.0, 1.0E-6)));
        result = OptionalDouble.empty().filter(Functions.greaterThan(Math.PI));
        Assert.assertThat(result, isEmpty());
        result = OptionalDouble.of(1.19).filter(Functions.greaterThan(Math.PI));
        Assert.assertThat(result, isEmpty());
    }

    @Test
    public void testFilterNot() {
        OptionalDouble result;
        result = OptionalDouble.of(1.19).filterNot(Functions.greaterThan(Math.PI));
        Assert.assertThat(result, hasValueThat(Matchers.closeTo(1.19, 1.0E-6)));
        result = OptionalDouble.empty().filterNot(Functions.greaterThan(Math.PI));
        Assert.assertThat(result, isEmpty());
        result = OptionalDouble.of(10.0).filterNot(Functions.greaterThan(Math.PI));
        Assert.assertThat(result, isEmpty());
    }

    @Test
    public void testMap() {
        final DoubleUnaryOperator negatorFunction = new DoubleUnaryOperator() {
            @Override
            public double applyAsDouble(double operand) {
                return -operand;
            }
        };
        OptionalDouble result;
        result = OptionalDouble.empty().map(negatorFunction);
        Assert.assertThat(result, isEmpty());
        result = OptionalDouble.of(10.123).map(negatorFunction);
        Assert.assertThat(result, hasValueThat(Matchers.closeTo((-10.123), 1.0E-4)));
    }

    @Test
    public void testMapToObj() {
        final DoubleFunction<String> asciiToString = new DoubleFunction<String>() {
            @Override
            public String apply(double value) {
                return String.valueOf(((char) (value)));
            }
        };
        Optional<String> result;
        result = OptionalDouble.empty().mapToObj(asciiToString);
        Assert.assertThat(result, OptionalMatcher.isEmpty());
        result = OptionalDouble.of(65.0).mapToObj(asciiToString);
        Assert.assertThat(result, OptionalMatcher.hasValue("A"));
        result = OptionalDouble.empty().mapToObj(new DoubleFunction<String>() {
            @Override
            public String apply(double value) {
                return null;
            }
        });
        Assert.assertThat(result, OptionalMatcher.isEmpty());
    }

    @Test
    public void testMapToInt() {
        Assert.assertThat(OptionalDouble.of(0.2).mapToInt(new DoubleToIntFunction() {
            @Override
            public int applyAsInt(double value) {
                return ((int) (value * 10));
            }
        }).getAsInt(), Matchers.is(2));
        Assert.assertFalse(OptionalDouble.empty().mapToInt(new DoubleToIntFunction() {
            @Override
            public int applyAsInt(double value) {
                Assert.fail();
                return 0;
            }
        }).isPresent());
    }

    @Test
    public void testMapToLong() {
        Assert.assertThat(OptionalDouble.of(0.2).mapToLong(new DoubleToLongFunction() {
            @Override
            public long applyAsLong(double value) {
                return ((long) (value * 10));
            }
        }).getAsLong(), Matchers.is(2L));
        Assert.assertFalse(OptionalDouble.empty().mapToLong(new DoubleToLongFunction() {
            @Override
            public long applyAsLong(double value) {
                Assert.fail();
                return 0;
            }
        }).isPresent());
    }

    @Test
    public void testStream() {
        long count = OptionalDouble.of(10.0).stream().count();
        Assert.assertThat(count, Matchers.is(1L));
    }

    @Test
    public void testStreamOnEmptyOptional() {
        long count = OptionalDouble.empty().stream().count();
        Assert.assertThat(count, Matchers.is(0L));
    }

    @Test
    public void testOr() {
        double value = OptionalDouble.of(10.123).or(new com.annimon.stream.function.Supplier<OptionalDouble>() {
            @Override
            public OptionalDouble get() {
                return OptionalDouble.of(19);
            }
        }).getAsDouble();
        Assert.assertThat(value, Matchers.closeTo(10.123, 1.0E-4));
    }

    @Test
    public void testOrOnEmptyOptional() {
        double value = OptionalDouble.empty().or(new com.annimon.stream.function.Supplier<OptionalDouble>() {
            @Override
            public OptionalDouble get() {
                return OptionalDouble.of(Math.PI);
            }
        }).getAsDouble();
        Assert.assertThat(value, Matchers.closeTo(Math.PI, 1.0E-4));
    }

    @Test
    public void testOrOnEmptyOptionalAndEmptySupplierOptional() {
        final OptionalDouble optional = OptionalDouble.empty().or(new com.annimon.stream.function.Supplier<OptionalDouble>() {
            @Override
            public OptionalDouble get() {
                return OptionalDouble.empty();
            }
        });
        Assert.assertThat(optional, isEmpty());
    }

    @Test
    public void testOrElse() {
        Assert.assertThat(OptionalDouble.empty().orElse(10.123), Matchers.closeTo(10.123, 1.0E-4));
        Assert.assertThat(OptionalDouble.of(10.123).orElse(0.0), Matchers.closeTo(10.123, 1.0E-4));
    }

    @Test
    public void testOrElseGet() {
        Assert.assertThat(OptionalDouble.empty().orElseGet(new DoubleSupplier() {
            @Override
            public double getAsDouble() {
                return 21.098;
            }
        }), Matchers.closeTo(21.098, 1.0E-4));
        Assert.assertThat(OptionalDouble.of(21.098).orElseGet(new DoubleSupplier() {
            @Override
            public double getAsDouble() {
                throw new IllegalStateException();
            }
        }), Matchers.closeTo(21.098, 1.0E-4));
    }

    @Test
    public void testOrElseThrowWithPresentValue() {
        double value = OptionalDouble.of(10.123).orElseThrow();
        Assert.assertThat(value, Matchers.closeTo(10.123, 1.0E-4));
    }

    @Test(expected = NoSuchElementException.class)
    public void testOrElseThrowOnEmptyOptional() {
        OptionalDouble.empty().orElseThrow();
    }

    @Test
    public void testOrElseThrow() {
        try {
            Assert.assertThat(OptionalDouble.of(10.123).orElseThrow(new com.annimon.stream.function.Supplier<NoSuchElementException>() {
                @Override
                public NoSuchElementException get() {
                    throw new IllegalStateException();
                }
            }), Matchers.closeTo(10.123, 1.0E-4));
        } catch (Exception e) {
            throw new IllegalStateException();
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void testOrElseThrow2() {
        OptionalDouble.empty().orElseThrow(new com.annimon.stream.function.Supplier<NoSuchElementException>() {
            @Override
            public NoSuchElementException get() {
                return new NoSuchElementException();
            }
        });
    }

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Test
    public void testEquals() {
        Assert.assertEquals(OptionalDouble.empty(), OptionalDouble.empty());
        Assert.assertNotEquals(OptionalDouble.empty(), Optional.empty());
        Assert.assertEquals(OptionalDouble.of(Math.PI), OptionalDouble.of(Math.PI));
        Assert.assertNotEquals(OptionalDouble.of(41.0), OptionalDouble.of(42.0));
        Assert.assertNotEquals(OptionalDouble.of(0.0), OptionalDouble.empty());
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(OptionalDouble.empty().hashCode(), 0);
        Assert.assertEquals(Double.valueOf(31.0).hashCode(), OptionalDouble.of(31.0).hashCode());
    }

    @Test
    public void testToString() {
        Assert.assertEquals(OptionalDouble.empty().toString(), "OptionalDouble.empty");
        Assert.assertThat(OptionalDouble.of(42.0).toString(), CoreMatchers.containsString("OptionalDouble[42"));
    }
}

