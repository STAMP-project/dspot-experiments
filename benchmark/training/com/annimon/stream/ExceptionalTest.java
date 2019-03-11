package com.annimon.stream;


import com.annimon.stream.function.Function;
import com.annimon.stream.function.Supplier;
import com.annimon.stream.function.UnaryOperator;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@code Exceptional}.
 *
 * @see com.annimon.stream.Exceptional
 */
public class ExceptionalTest {
    @Test
    public void testGet() {
        int value = Exceptional.of(ExceptionalTest.tenSupplier).get();
        Assert.assertEquals(10, value);
    }

    @Test
    public void testIsPresent() {
        Assert.assertTrue(Exceptional.of(ExceptionalTest.tenSupplier).isPresent());
        Assert.assertFalse(Exceptional.of(ExceptionalTest.ioExceptionSupplier).isPresent());
    }

    @Test
    public void testGetOrElse() {
        int value = Exceptional.of(ExceptionalTest.ioExceptionSupplier).getOrElse(20);
        Assert.assertEquals(20, value);
        value = Exceptional.of(ExceptionalTest.tenSupplier).getOrElse(20);
        Assert.assertEquals(10, value);
        Supplier<Integer> supplier = new Supplier<Integer>() {
            @Override
            public Integer get() {
                return 3228;
            }
        };
        value = Exceptional.of(ExceptionalTest.ioExceptionSupplier).getOrElse(supplier);
        Assert.assertEquals(3228, value);
        value = Exceptional.of(ExceptionalTest.tenSupplier).getOrElse(supplier);
        Assert.assertEquals(10, value);
    }

    @Test
    public void testGetOptional() {
        Optional<Integer> value = Exceptional.of(ExceptionalTest.ioExceptionSupplier).getOptional();
        Assert.assertFalse(value.isPresent());
    }

    @Test
    public void testGetException() {
        Throwable throwable = Exceptional.of(ExceptionalTest.ioExceptionSupplier).getException();
        Assert.assertThat(throwable, CoreMatchers.instanceOf(IOException.class));
    }

    @Test
    public void testGetExceptionFromAlreadySuppliedThrowable() {
        Throwable throwable = Exceptional.of(new IOException()).getException();
        Assert.assertThat(throwable, CoreMatchers.instanceOf(IOException.class));
    }

    @Test
    public void testGetOrThrowWithoutException() throws Throwable {
        int value = Exceptional.of(ExceptionalTest.tenSupplier).getOrThrow();
        Assert.assertEquals(10, value);
    }

    @Test(expected = IOException.class)
    public void testGetOrThrowWithException() throws Throwable {
        Exceptional.of(ExceptionalTest.ioExceptionSupplier).getOrThrow();
    }

    @Test
    public void testGetOrThrowRuntimeExceptionWithoutException() throws Throwable {
        int value = Exceptional.of(ExceptionalTest.tenSupplier).getOrThrowRuntimeException();
        Assert.assertEquals(10, value);
    }

    @Test(expected = RuntimeException.class)
    public void testGetOrThrowRuntimeExceptionWithException() throws Throwable {
        Exceptional.of(ExceptionalTest.ioExceptionSupplier).getOrThrowRuntimeException();
    }

    @Test
    public void testGetOrThrowNewExceptionWithoutException() throws Throwable {
        int value = Exceptional.of(ExceptionalTest.tenSupplier).getOrThrow(new ArithmeticException());
        Assert.assertEquals(10, value);
    }

    @Test(expected = ArithmeticException.class)
    public void testGetOrThrowNewExceptionWithException() throws Throwable {
        Exceptional.of(ExceptionalTest.ioExceptionSupplier).getOrThrow(new ArithmeticException());
    }

    @Test
    public void testGetOrThrowNewExceptionTestCause() {
        try {
            Exceptional.of(ExceptionalTest.ioExceptionSupplier).getOrThrow(new ArithmeticException());
        } catch (ArithmeticException ae) {
            Assert.assertThat(ae.getCause(), CoreMatchers.instanceOf(IOException.class));
        }
    }

    @Test
    public void testOr() {
        int value = Exceptional.of(ExceptionalTest.tenSupplier).or(new Supplier<Exceptional<Integer>>() {
            @Override
            public Exceptional<Integer> get() {
                return Exceptional.of(ExceptionalTest.ioExceptionSupplier);
            }
        }).get();
        Assert.assertEquals(10, value);
    }

    @Test
    public void testOrWithExceptionAndValueInSupplier() {
        int value = Exceptional.of(ExceptionalTest.ioExceptionSupplier).or(new Supplier<Exceptional<Integer>>() {
            @Override
            public Exceptional<Integer> get() {
                return Exceptional.of(ExceptionalTest.tenSupplier);
            }
        }).get();
        Assert.assertEquals(10, value);
    }

    @Test(expected = IOException.class)
    public void testOrWithExceptionBoth() throws Throwable {
        Exceptional.of(ExceptionalTest.ioExceptionSupplier).or(new Supplier<Exceptional<Integer>>() {
            @Override
            public Exceptional<Integer> get() {
                return Exceptional.of(ExceptionalTest.ioExceptionSupplier);
            }
        }).getOrThrow();
    }

    @Test
    public void testCustomIntermediate() {
        UnaryOperator<Exceptional<Integer>> incrementer = new UnaryOperator<Exceptional<Integer>>() {
            @Override
            public Exceptional<Integer> apply(Exceptional<Integer> exceptional) {
                return exceptional.map(new com.annimon.stream.function.ThrowableFunction<Integer, Integer, Throwable>() {
                    @Override
                    public Integer apply(Integer integer) throws Throwable {
                        return integer + 1;
                    }
                });
            }
        };
        int value;
        value = Exceptional.of(ExceptionalTest.ioExceptionSupplier).custom(incrementer).getOrElse(0);
        Assert.assertEquals(0, value);
        value = Exceptional.of(ExceptionalTest.tenSupplier).custom(incrementer).getOrElse(0);
        Assert.assertEquals(11, value);
    }

    @Test
    public void testCustomTerminal() {
        Function<Exceptional<Integer>, Integer> incrementer = new Function<Exceptional<Integer>, Integer>() {
            @Override
            public Integer apply(Exceptional<Integer> exceptional) {
                return exceptional.map(new com.annimon.stream.function.ThrowableFunction<Integer, Integer, Throwable>() {
                    @Override
                    public Integer apply(Integer integer) throws Throwable {
                        return integer + 1;
                    }
                }).getOrElse(0);
            }
        };
        int value;
        value = Exceptional.of(ExceptionalTest.ioExceptionSupplier).custom(incrementer);
        Assert.assertEquals(0, value);
        value = Exceptional.of(ExceptionalTest.tenSupplier).custom(incrementer);
        Assert.assertEquals(11, value);
    }

    @Test
    public void testMapWithoutException() {
        String value = Exceptional.of(ExceptionalTest.tenSupplier).map(new com.annimon.stream.function.ThrowableFunction<Integer, String, Throwable>() {
            @Override
            public String apply(Integer value) throws Throwable {
                return Integer.toString(value);
            }
        }).get();
        Assert.assertEquals("10", value);
    }

    @Test(expected = NumberFormatException.class)
    public void testMapWithException() throws Throwable {
        Exceptional.of(ExceptionalTest.tenSupplier).map(new com.annimon.stream.function.ThrowableFunction<Integer, String, Throwable>() {
            @Override
            public String apply(Integer value) throws Throwable {
                throw new NumberFormatException();
            }
        }).getOrThrow();
    }

    @Test(expected = NullPointerException.class)
    public void testMapOnNullFunction() throws Throwable {
        Exceptional.of(ExceptionalTest.tenSupplier).map(null).getOrThrow();
    }

    @Test(expected = IOException.class)
    public void testMapOnAlreadyFailedExceptional() throws Throwable {
        Exceptional.of(ExceptionalTest.ioExceptionSupplier).map(new com.annimon.stream.function.ThrowableFunction<Integer, String, Throwable>() {
            @Override
            public String apply(Integer value) throws Throwable {
                return Integer.toString(value);
            }
        }).getOrThrow();
    }

    @Test(expected = RuntimeException.class)
    public void testIfPresent() {
        Exceptional.of(ExceptionalTest.tenSupplier).ifPresent(new com.annimon.stream.function.Consumer<Integer>() {
            @Override
            public void accept(Integer value) {
                throw new RuntimeException();
            }
        });
    }

    @Test
    public void testIfPresentOnAbsentValue() {
        Exceptional.of(ExceptionalTest.ioExceptionSupplier).ifPresent(new com.annimon.stream.function.Consumer<Integer>() {
            @Override
            public void accept(Integer value) {
                Assert.fail();
            }
        });
    }

    @Test
    public void testIfException() {
        for (final ExceptionalTest.ExceptionType type : ExceptionalTest.ExceptionType.values()) {
            Exceptional.of(new com.annimon.stream.function.ThrowableSupplier<Integer, Throwable>() {
                @Override
                public Integer get() throws Throwable {
                    ExceptionalTest.throwException(type);
                    return 10;
                }
            }).ifException(new com.annimon.stream.function.Consumer<Throwable>() {
                @Override
                public void accept(Throwable value) {
                    Assert.assertEquals(type.getException(), value);
                }
            });
        }
    }

    @Test
    public void testIfExceptionIs() {
        final int INTERRUPTED = 0;
        final int EXCEPTION = 1;
        final int FILE_NOT_FOUND = 2;
        final boolean[] data = new boolean[3];
        Exceptional.of(new com.annimon.stream.function.ThrowableSupplier<Integer, Throwable>() {
            @Override
            public Integer get() throws Throwable {
                ExceptionalTest.throwException(ExceptionalTest.ExceptionType.INTERRUPTED);
                return 10;
            }
        }).ifExceptionIs(InterruptedException.class, new com.annimon.stream.function.Consumer<InterruptedException>() {
            @Override
            public void accept(InterruptedException value) {
                data[INTERRUPTED] = true;
            }
        }).ifExceptionIs(Exception.class, new com.annimon.stream.function.Consumer<Exception>() {
            @Override
            public void accept(Exception value) {
                data[EXCEPTION] = true;
            }
        }).ifExceptionIs(FileNotFoundException.class, new com.annimon.stream.function.Consumer<Throwable>() {
            @Override
            public void accept(Throwable value) {
                data[FILE_NOT_FOUND] = true;
            }
        });
        Assert.assertTrue(data[INTERRUPTED]);
        Assert.assertTrue(data[EXCEPTION]);
        Assert.assertFalse(data[FILE_NOT_FOUND]);
    }

    @Test
    public void testRecover() {
        int value = Exceptional.of(ExceptionalTest.ioExceptionSupplier).recover(new com.annimon.stream.function.ThrowableFunction<Throwable, Integer, Throwable>() {
            @Override
            public Integer apply(Throwable throwable) {
                return 10;
            }
        }).get();
        Assert.assertEquals(10, value);
    }

    @Test(expected = FileNotFoundException.class)
    public void testRecoverError() throws Throwable {
        Exceptional.of(ExceptionalTest.ioExceptionSupplier).recover(new com.annimon.stream.function.ThrowableFunction<Throwable, Integer, Throwable>() {
            @Override
            public Integer apply(Throwable throwable) throws FileNotFoundException {
                throw new FileNotFoundException();
            }
        }).getOrThrow();
    }

    @Test
    public void testRecoverChain() {
        int value = Exceptional.of(ExceptionalTest.ioExceptionSupplier).recover(new com.annimon.stream.function.ThrowableFunction<Throwable, Integer, Throwable>() {
            @Override
            public Integer apply(Throwable throwable) throws IOException {
                Assert.assertThat(throwable, CoreMatchers.instanceOf(IOException.class));
                throw new FileNotFoundException();
            }
        }).recover(new com.annimon.stream.function.ThrowableFunction<Throwable, Integer, Throwable>() {
            @Override
            public Integer apply(Throwable throwable) throws IOException {
                Assert.assertThat(throwable, CoreMatchers.instanceOf(FileNotFoundException.class));
                return 10;
            }
        }).get();
        Assert.assertEquals(10, value);
    }

    @Test
    public void testRecoverWith() {
        int value = Exceptional.of(ExceptionalTest.ioExceptionSupplier).recoverWith(new Function<Throwable, Exceptional<Integer>>() {
            @Override
            public Exceptional<Integer> apply(Throwable throwable) {
                return Exceptional.of(ExceptionalTest.tenSupplier);
            }
        }).get();
        Assert.assertEquals(10, value);
    }

    @Test(expected = IOException.class)
    public void testRecoverWithError() throws Throwable {
        Exceptional.of(ExceptionalTest.ioExceptionSupplier).recoverWith(new Function<Throwable, Exceptional<Integer>>() {
            @Override
            public Exceptional<Integer> apply(Throwable throwable) {
                return Exceptional.of(ExceptionalTest.ioExceptionSupplier);
            }
        }).getOrThrow();
    }

    @Test
    public void testEqualsReflexive() {
        final Exceptional<Integer> ten1 = Exceptional.of(ExceptionalTest.tenSupplier);
        Assert.assertEquals(ten1, ten1);
    }

    @Test
    public void testEqualsSymmetric() {
        final Exceptional<Integer> ten1 = Exceptional.of(ExceptionalTest.tenSupplier);
        final Exceptional<Integer> ten2 = Exceptional.of(ExceptionalTest.tenSupplier);
        Assert.assertEquals(ten1, ten2);
        Assert.assertEquals(ten2, ten1);
    }

    @Test
    public void testEqualsTransitive() {
        final Exceptional<Integer> ten1 = Exceptional.of(ExceptionalTest.tenSupplier);
        final Exceptional<Integer> ten2 = Exceptional.of(ExceptionalTest.tenSupplier);
        final Exceptional<Integer> ten3 = Exceptional.of(ExceptionalTest.tenSupplier);
        Assert.assertEquals(ten1, ten2);
        Assert.assertEquals(ten2, ten3);
        Assert.assertEquals(ten1, ten3);
    }

    @Test
    public void testEqualsWithDifferentTypes() {
        final Exceptional<Integer> ten1 = Exceptional.of(ExceptionalTest.tenSupplier);
        Assert.assertFalse(ten1.equals(10));
    }

    @Test
    public void testEqualsWithDifferentNumberTypes() {
        final Exceptional<Integer> ten1 = Exceptional.of(ExceptionalTest.tenSupplier);
        final Exceptional<Byte> tenByte = Exceptional.of(new com.annimon.stream.function.ThrowableSupplier<Byte, Throwable>() {
            @Override
            public Byte get() throws Throwable {
                return ((byte) (10));
            }
        });
        Assert.assertNotEquals(ten1, tenByte);
    }

    @Test
    public void testEqualsWithDifferentExceptions() {
        final Exceptional<Integer> ten1 = Exceptional.of(ExceptionalTest.tenSupplier);
        final Exceptional<Integer> ten2 = Exceptional.of(new com.annimon.stream.function.ThrowableSupplier<Integer, Throwable>() {
            @Override
            public Integer get() throws Throwable {
                ExceptionalTest.throwIO();
                return 10;
            }
        });
        Assert.assertNotEquals(ten1, ten2);
    }

    @Test
    public void testEqualsWithDifferentGenericTypes() {
        final Exceptional<Integer> ten = Exceptional.of(ExceptionalTest.tenSupplier);
        final Exceptional<Integer> io = Exceptional.of(ExceptionalTest.ioExceptionSupplier);
        Assert.assertNotEquals(ten, io);
    }

    @Test
    public void testHashCodeWithSameObject() {
        final Exceptional<Integer> ten1 = Exceptional.of(ExceptionalTest.tenSupplier);
        final Exceptional<Integer> ten2 = Exceptional.of(ExceptionalTest.tenSupplier);
        int initial = ten1.hashCode();
        Assert.assertEquals(initial, ten1.hashCode());
        Assert.assertEquals(initial, ten1.hashCode());
        Assert.assertEquals(initial, ten2.hashCode());
    }

    @Test
    public void testHashCodeWithDifferentGenericType() {
        final Exceptional<Byte> tenByte = Exceptional.of(new com.annimon.stream.function.ThrowableSupplier<Byte, Throwable>() {
            @Override
            public Byte get() throws Throwable {
                return ((byte) (10));
            }
        });
        final Exceptional<Integer> io = Exceptional.of(ExceptionalTest.ioExceptionSupplier);
        Assert.assertNotEquals(io.hashCode(), tenByte.hashCode());
    }

    @Test
    public void testToStringWithoutException() {
        Assert.assertEquals("Exceptional value 10", Exceptional.of(ExceptionalTest.tenSupplier).toString());
    }

    @Test
    public void testToStringWithException() {
        Assert.assertEquals("Exceptional throwable java.io.IOException", Exceptional.of(ExceptionalTest.ioExceptionSupplier).toString());
    }

    private static final com.annimon.stream.function.ThrowableSupplier<Integer, Throwable> tenSupplier = new com.annimon.stream.function.ThrowableSupplier<Integer, Throwable>() {
        @Override
        public Integer get() throws IOException {
            return 10;
        }
    };

    private static final com.annimon.stream.function.ThrowableSupplier<Integer, Throwable> ioExceptionSupplier = new com.annimon.stream.function.ThrowableSupplier<Integer, Throwable>() {
        @Override
        public Integer get() throws Throwable {
            ExceptionalTest.throwIO();
            return 10;
        }
    };

    private enum ExceptionType {

        NULL_POINTER(new NullPointerException()),
        UNSUPPORTED_OPERATION(new UnsupportedOperationException()),
        FILE_NOT_FOUND(new FileNotFoundException()),
        INTERRUPTED(new InterruptedException()),
        UNSUPPORTED_ENCODING(new UnsupportedEncodingException()),
        IO(new IOException());
        private final Exception exception;

        ExceptionType(Exception exception) {
            this.exception = exception;
        }

        public Exception getException() {
            return exception;
        }
    }
}

