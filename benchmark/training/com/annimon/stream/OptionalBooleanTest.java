package com.annimon.stream;


import BooleanPredicate.Util;
import com.annimon.stream.function.BooleanConsumer;
import com.annimon.stream.function.BooleanFunction;
import com.annimon.stream.function.BooleanPredicate;
import com.annimon.stream.function.BooleanSupplier;
import com.annimon.stream.test.hamcrest.OptionalMatcher;
import java.util.NoSuchElementException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link OptionalBoolean}
 */
public class OptionalBooleanTest {
    @Test
    public void testGetWithPresentValue() {
        boolean value = OptionalBoolean.of(true).getAsBoolean();
        Assert.assertTrue(value);
    }

    @Test
    public void testOfNullableWithPresentValue() {
        Assert.assertThat(OptionalBoolean.ofNullable(true), hasValue(true));
    }

    @Test
    public void testOfNullableWithAbsentValue() {
        Assert.assertThat(OptionalBoolean.ofNullable(null), isEmpty());
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetOnEmptyOptional() {
        OptionalBoolean.empty().getAsBoolean();
    }

    @Test
    public void testIsPresent() {
        Assert.assertThat(OptionalBoolean.of(false), isPresent());
    }

    @Test
    public void testIsPresentOnEmptyOptional() {
        Assert.assertThat(OptionalBoolean.empty(), isEmpty());
    }

    @Test
    public void testIfPresent() {
        OptionalBoolean.empty().ifPresent(new BooleanConsumer() {
            @Override
            public void accept(boolean value) {
                Assert.fail();
            }
        });
        OptionalBoolean.of(true).ifPresent(new BooleanConsumer() {
            @Override
            public void accept(boolean value) {
                Assert.assertTrue(value);
            }
        });
    }

    @Test
    public void testIfPresentOrElseWhenValuePresent() {
        OptionalBoolean.of(false).ifPresentOrElse(new BooleanConsumer() {
            @Override
            public void accept(boolean value) {
                Assert.assertFalse(value);
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
        OptionalBoolean.empty().ifPresentOrElse(new BooleanConsumer() {
            @Override
            public void accept(boolean value) {
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
        OptionalBoolean.of(true).ifPresentOrElse(new BooleanConsumer() {
            @Override
            public void accept(boolean value) {
                Assert.assertTrue(value);
            }
        }, null);
    }

    @Test(expected = RuntimeException.class)
    public void testIfPresentOrElseWhenValueAbsentAndConsumerNull() {
        OptionalBoolean.empty().ifPresentOrElse(null, new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException();
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void testIfPresentOrElseWhenValuePresentAndConsumerNull() {
        OptionalBoolean.of(false).ifPresentOrElse(null, new Runnable() {
            @Override
            public void run() {
                Assert.fail("Should not have been executed.");
            }
        });
    }

    @Test(expected = NullPointerException.class)
    public void testIfPresentOrElseWhenValueAbsentAndEmptyActionNull() {
        OptionalBoolean.empty().ifPresentOrElse(new BooleanConsumer() {
            @Override
            public void accept(boolean value) {
                Assert.fail("Should not have been executed.");
            }
        }, null);
    }

    @Test
    public void testExecuteIfPresent() {
        boolean value = OptionalBoolean.of(true).executeIfPresent(new BooleanConsumer() {
            @Override
            public void accept(boolean value) {
                Assert.assertTrue(value);
            }
        }).getAsBoolean();
        Assert.assertTrue(value);
    }

    @Test
    public void testExecuteIfPresentOnAbsentValue() {
        OptionalBoolean.empty().executeIfPresent(new BooleanConsumer() {
            @Override
            public void accept(boolean value) {
                Assert.fail();
            }
        });
    }

    @Test(expected = RuntimeException.class)
    public void testExecuteIfAbsent() {
        OptionalBoolean.empty().executeIfAbsent(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException();
            }
        });
    }

    @Test
    public void testExecuteIfAbsentOnPresentValue() {
        OptionalBoolean.of(false).executeIfAbsent(new Runnable() {
            @Override
            public void run() {
                Assert.fail();
            }
        });
    }

    @Test
    public void testCustomIntermediate() {
        OptionalBoolean result = OptionalBoolean.of(true).custom(new com.annimon.stream.function.Function<OptionalBoolean, OptionalBoolean>() {
            @Override
            public OptionalBoolean apply(OptionalBoolean optional) {
                return optional.filter(Util.identity());
            }
        });
        Assert.assertThat(result, hasValue(true));
    }

    @Test
    public void testCustomTerminal() {
        Boolean result = OptionalBoolean.empty().custom(new com.annimon.stream.function.Function<OptionalBoolean, Boolean>() {
            @Override
            public Boolean apply(OptionalBoolean optional) {
                return optional.orElse(false);
            }
        });
        Assert.assertThat(result, Matchers.is(false));
    }

    @Test(expected = NullPointerException.class)
    public void testCustomException() {
        OptionalBoolean.empty().custom(null);
    }

    @Test
    public void testFilter() {
        final BooleanPredicate predicate = new BooleanPredicate() {
            @Override
            public boolean test(boolean value) {
                return value || false;
            }
        };
        OptionalBoolean result;
        result = OptionalBoolean.of(true).filter(predicate);
        Assert.assertThat(result, hasValue(true));
        result = OptionalBoolean.empty().filter(predicate);
        Assert.assertThat(result, isEmpty());
        result = OptionalBoolean.of(false).filter(predicate);
        Assert.assertThat(result, isEmpty());
    }

    @Test
    public void testFilterNot() {
        final BooleanPredicate predicate = new BooleanPredicate() {
            @Override
            public boolean test(boolean value) {
                return value || false;
            }
        };
        OptionalBoolean result;
        result = OptionalBoolean.of(false).filterNot(predicate);
        Assert.assertThat(result, hasValue(false));
        result = OptionalBoolean.empty().filterNot(predicate);
        Assert.assertThat(result, isEmpty());
        result = OptionalBoolean.of(true).filterNot(predicate);
        Assert.assertThat(result, isEmpty());
    }

    @Test
    public void testMap() {
        final BooleanPredicate negate = Util.negate(Util.identity());
        OptionalBoolean result;
        result = OptionalBoolean.empty().map(negate);
        Assert.assertThat(result, isEmpty());
        result = OptionalBoolean.of(true).map(negate);
        Assert.assertThat(result, hasValue(false));
        result = OptionalBoolean.of(false).map(negate);
        Assert.assertThat(result, hasValue(true));
    }

    @Test
    public void testMapToObj() {
        final BooleanFunction<String> toString = new BooleanFunction<String>() {
            @Override
            public String apply(boolean value) {
                return Boolean.toString(value);
            }
        };
        Optional<String> result;
        result = OptionalBoolean.empty().mapToObj(toString);
        Assert.assertThat(result, OptionalMatcher.isEmpty());
        result = OptionalBoolean.of(false).mapToObj(toString);
        Assert.assertThat(result, OptionalMatcher.hasValue("false"));
        result = OptionalBoolean.of(true).mapToObj(toString);
        Assert.assertThat(result, OptionalMatcher.hasValue("true"));
        result = OptionalBoolean.empty().mapToObj(new BooleanFunction<String>() {
            @Override
            public String apply(boolean value) {
                return null;
            }
        });
        Assert.assertThat(result, OptionalMatcher.isEmpty());
    }

    @Test
    public void testOr() {
        boolean value = OptionalBoolean.of(true).or(new com.annimon.stream.function.Supplier<OptionalBoolean>() {
            @Override
            public OptionalBoolean get() {
                return OptionalBoolean.of(false);
            }
        }).getAsBoolean();
        Assert.assertTrue(value);
    }

    @Test
    public void testOrOnEmptyOptional() {
        boolean value = OptionalBoolean.empty().or(new com.annimon.stream.function.Supplier<OptionalBoolean>() {
            @Override
            public OptionalBoolean get() {
                return OptionalBoolean.of(false);
            }
        }).getAsBoolean();
        Assert.assertFalse(value);
    }

    @Test
    public void testOrOnEmptyOptionalAndEmptySupplierOptional() {
        final OptionalBoolean optional = OptionalBoolean.empty().or(new com.annimon.stream.function.Supplier<OptionalBoolean>() {
            @Override
            public OptionalBoolean get() {
                return OptionalBoolean.empty();
            }
        });
        Assert.assertThat(optional, isEmpty());
    }

    @Test
    public void testOrElse() {
        Assert.assertTrue(OptionalBoolean.empty().orElse(true));
        Assert.assertFalse(OptionalBoolean.empty().orElse(false));
    }

    @Test
    public void testOrElseGet() {
        Assert.assertTrue(OptionalBoolean.empty().orElseGet(new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                return true;
            }
        }));
        Assert.assertTrue(OptionalBoolean.of(true).orElseGet(new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                throw new IllegalStateException();
            }
        }));
    }

    @Test
    public void testOrElseThrowWithPresentValue() {
        boolean value = OptionalBoolean.of(true).orElseThrow();
        Assert.assertTrue(value);
    }

    @Test(expected = NoSuchElementException.class)
    public void testOrElseThrowOnEmptyOptional() {
        OptionalBoolean.empty().orElseThrow();
    }

    @Test
    public void testOrElseThrow() {
        try {
            Assert.assertFalse(OptionalBoolean.of(false).orElseThrow(new com.annimon.stream.function.Supplier<NoSuchElementException>() {
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
        OptionalBoolean.empty().orElseThrow(new com.annimon.stream.function.Supplier<NoSuchElementException>() {
            @Override
            public NoSuchElementException get() {
                return new NoSuchElementException();
            }
        });
    }

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Test
    public void testEquals() {
        Assert.assertEquals(OptionalBoolean.empty(), OptionalBoolean.empty());
        Assert.assertNotEquals(OptionalBoolean.empty(), Optional.empty());
        Assert.assertEquals(OptionalBoolean.of(true), OptionalBoolean.of(true));
        Assert.assertNotEquals(OptionalBoolean.of(false), OptionalBoolean.of(true));
        Assert.assertNotEquals(OptionalBoolean.of(false), OptionalBoolean.empty());
    }

    @Test
    public void testSingleInstance() {
        Assert.assertSame(OptionalBoolean.of(true), OptionalBoolean.of(true));
        Assert.assertSame(OptionalBoolean.of(true), OptionalBoolean.of(false).map(new BooleanPredicate() {
            @Override
            public boolean test(boolean value) {
                return !value;
            }
        }));
        Assert.assertSame(OptionalBoolean.of(false), OptionalBoolean.of(false));
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(OptionalBoolean.empty().hashCode(), 0);
        Assert.assertEquals(1231, OptionalBoolean.of(true).hashCode());
        Assert.assertEquals(1237, OptionalBoolean.of(false).hashCode());
    }

    @Test
    public void testToString() {
        Assert.assertEquals(OptionalBoolean.empty().toString(), "OptionalBoolean.empty");
        Assert.assertEquals(OptionalBoolean.of(true).toString(), "OptionalBoolean[true]");
    }
}

