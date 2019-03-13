/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3;


import java.io.IOException;
import java.io.UncheckedIOException;
import org.apache.commons.lang3.Functions.FailableConsumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


class FunctionsTest {
    public static class SomeException extends Exception {
        private static final long serialVersionUID = -4965704778119283411L;

        private Throwable t;

        SomeException(String pMsg) {
            super(pMsg);
        }

        public void setThrowable(Throwable pThrowable) {
            t = pThrowable;
        }

        public void test() throws Throwable {
            if ((t) != null) {
                throw t;
            }
        }
    }

    public static class Testable {
        private Throwable t;

        Testable(Throwable pTh) {
            t = pTh;
        }

        public void setThrowable(Throwable pThrowable) {
            t = pThrowable;
        }

        public void test() throws Throwable {
            test(t);
        }

        public void test(Throwable pThrowable) throws Throwable {
            if (pThrowable != null) {
                throw pThrowable;
            }
        }

        public Integer testInt() throws Throwable {
            return testInt(t);
        }

        public Integer testInt(Throwable pThrowable) throws Throwable {
            if (pThrowable != null) {
                throw pThrowable;
            }
            return 0;
        }
    }

    public static class FailureOnOddInvocations {
        private static int invocation;

        FailureOnOddInvocations() throws FunctionsTest.SomeException {
            final int i = ++(FunctionsTest.FailureOnOddInvocations.invocation);
            if ((i % 2) == 1) {
                throw new FunctionsTest.SomeException(("Odd Invocation: " + i));
            }
        }
    }

    public static class CloseableObject {
        private boolean closed;

        public void run(Throwable pTh) throws Throwable {
            if (pTh != null) {
                throw pTh;
            }
        }

        public void reset() {
            closed = false;
        }

        public void close() {
            closed = true;
        }

        public boolean isClosed() {
            return closed;
        }
    }

    @Test
    public void testApplyFunction() {
        final IllegalStateException ise = new IllegalStateException();
        final FunctionsTest.Testable testable = new FunctionsTest.Testable(ise);
        Throwable e = Assertions.assertThrows(IllegalStateException.class, () -> Functions.apply(FunctionsTest.Testable::testInt, testable));
        Assertions.assertSame(ise, e);
        final Error error = new OutOfMemoryError();
        testable.setThrowable(error);
        e = Assertions.assertThrows(OutOfMemoryError.class, () -> Functions.apply(FunctionsTest.Testable::testInt, testable));
        Assertions.assertSame(error, e);
        final IOException ioe = new IOException("Unknown I/O error");
        testable.setThrowable(ioe);
        e = Assertions.assertThrows(UncheckedIOException.class, () -> Functions.apply(FunctionsTest.Testable::testInt, testable));
        final Throwable t = e.getCause();
        Assertions.assertNotNull(t);
        Assertions.assertSame(ioe, t);
        testable.setThrowable(null);
        final Integer i = Functions.apply(FunctionsTest.Testable::testInt, testable);
        Assertions.assertNotNull(i);
        Assertions.assertEquals(0, i.intValue());
    }

    @Test
    public void testApplyBiFunction() {
        final IllegalStateException ise = new IllegalStateException();
        final FunctionsTest.Testable testable = new FunctionsTest.Testable(null);
        Throwable e = Assertions.assertThrows(IllegalStateException.class, () -> Functions.apply(FunctionsTest.Testable::testInt, testable, ise));
        Assertions.assertSame(ise, e);
        final Error error = new OutOfMemoryError();
        e = Assertions.assertThrows(OutOfMemoryError.class, () -> Functions.apply(FunctionsTest.Testable::testInt, testable, error));
        Assertions.assertSame(error, e);
        final IOException ioe = new IOException("Unknown I/O error");
        e = Assertions.assertThrows(UncheckedIOException.class, () -> Functions.apply(FunctionsTest.Testable::testInt, testable, ioe));
        final Throwable t = e.getCause();
        Assertions.assertNotNull(t);
        Assertions.assertSame(ioe, t);
        final Integer i = Functions.apply(FunctionsTest.Testable::testInt, testable, ((Throwable) (null)));
        Assertions.assertNotNull(i);
        Assertions.assertEquals(0, i.intValue());
    }

    @Test
    public void testTryWithResources() {
        final FunctionsTest.CloseableObject co = new FunctionsTest.CloseableObject();
        final FailableConsumer<Throwable, ? extends Throwable> consumer = co::run;
        final IllegalStateException ise = new IllegalStateException();
        Throwable e = Assertions.assertThrows(IllegalStateException.class, () -> Functions.tryWithResources(() -> consumer.accept(ise), co::close));
        Assertions.assertSame(ise, e);
        Assertions.assertTrue(co.isClosed());
        co.reset();
        final Error error = new OutOfMemoryError();
        e = Assertions.assertThrows(OutOfMemoryError.class, () -> Functions.tryWithResources(() -> consumer.accept(error), co::close));
        Assertions.assertSame(error, e);
        Assertions.assertTrue(co.isClosed());
        co.reset();
        final IOException ioe = new IOException("Unknown I/O error");
        UncheckedIOException uioe = Assertions.assertThrows(UncheckedIOException.class, () -> Functions.tryWithResources(() -> consumer.accept(ioe), co::close));
        final IOException cause = uioe.getCause();
        Assertions.assertSame(ioe, cause);
        Assertions.assertTrue(co.isClosed());
        co.reset();
        Functions.tryWithResources(() -> consumer.accept(null), co::close);
        Assertions.assertTrue(co.isClosed());
    }
}

