/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.android.desugar.runtime;


import ThrowableExtension.STRATEGY;
import com.google.devtools.build.android.desugar.runtime.ThrowableExtension.MimicDesugaringStrategy;
import com.google.devtools.build.android.desugar.runtime.ThrowableExtension.NullDesugaringStrategy;
import com.google.devtools.build.android.desugar.runtime.ThrowableExtension.ReuseDesugaringStrategy;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static ThrowableExtension.API_LEVEL;


/**
 * Test case for {@link ThrowableExtension}
 */
@RunWith(JUnit4.class)
public class ThrowableExtensionTest {
    /**
     * This test tests the behavior of closing resources via reflection. This is only enabled below
     * API 19. So, if the API level is 19 or above, this test will simply skip.
     */
    @Test
    public void testCloseResourceViaReflection() throws Throwable {
        class Resource extends ThrowableExtensionTest.AbstractResource {
            protected Resource(boolean exceptionOnClose) {
                super(exceptionOnClose);
            }

            public void close() throws Exception {
                super.internalClose();
            }
        }
        if ((API_LEVEL) >= 19) {
            return;
        }
        {
            Resource r = new Resource(false);
            assertThat(r.isClosed()).isFalse();
            ThrowableExtension.closeResource(null, r);
            assertThat(r.isClosed()).isTrue();
        }
        {
            Resource r = new Resource(true);
            assertThat(r.isClosed()).isFalse();
            MoreAsserts.assertThrows(IOException.class, () -> ThrowableExtension.closeResource(null, r));
        }
        {
            Resource r = new Resource(false);
            assertThat(r.isClosed()).isFalse();
            ThrowableExtension.closeResource(new Exception(), r);
            assertThat(r.isClosed()).isTrue();
        }
        {
            Resource r = new Resource(true);
            assertThat(r.isClosed()).isFalse();
            MoreAsserts.assertThrows(Exception.class, () -> ThrowableExtension.closeResource(new Exception(), r));
        }
    }

    /**
     * Test the new method closeResources() in the runtime library.
     *
     * <p>The method is introduced to fix b/37167433.
     */
    @Test
    public void testCloseResource() throws Throwable {
        /**
         * A resource implementing the interface AutoCloseable. This interface is only available since
         * API 19.
         */
        class AutoCloseableResource extends ThrowableExtensionTest.AbstractResource implements AutoCloseable {
            protected AutoCloseableResource(boolean exceptionOnClose) {
                super(exceptionOnClose);
            }

            @Override
            public void close() throws Exception {
                internalClose();
            }
        }
        /**
         * A resource implementing the interface Closeable.
         */
        class CloseableResource extends ThrowableExtensionTest.AbstractResource implements Closeable {
            protected CloseableResource(boolean exceptionOnClose) {
                super(exceptionOnClose);
            }

            @Override
            public void close() throws IOException {
                internalClose();
            }
        }
        {
            CloseableResource r = new CloseableResource(false);
            assertThat(r.isClosed()).isFalse();
            ThrowableExtension.closeResource(null, r);
            assertThat(r.isClosed()).isTrue();
        }
        {
            CloseableResource r = new CloseableResource(false);
            assertThat(r.isClosed()).isFalse();
            Exception suppressor = new Exception();
            ThrowableExtension.closeResource(suppressor, r);
            assertThat(r.isClosed()).isTrue();
            assertThat(ThrowableExtension.getSuppressed(suppressor)).isEmpty();
        }
        {
            CloseableResource r = new CloseableResource(true);
            assertThat(r.isClosed()).isFalse();
            MoreAsserts.assertThrows(IOException.class, () -> ThrowableExtension.closeResource(null, r));
            assertThat(r.isClosed()).isFalse();
        }
        {
            CloseableResource r = new CloseableResource(true);
            assertThat(r.isClosed()).isFalse();
            Exception suppressor = new Exception();
            MoreAsserts.assertThrows(Exception.class, () -> ThrowableExtension.closeResource(suppressor, r));
            assertThat(r.isClosed()).isFalse();// Failed to close.

            if (!(ThrowableExtensionTestUtility.isNullStrategy())) {
                assertThat(ThrowableExtension.getSuppressed(suppressor)).hasLength(1);
                assertThat(ThrowableExtension.getSuppressed(suppressor)[0].getClass()).isEqualTo(IOException.class);
            }
        }
        {
            AutoCloseableResource r = new AutoCloseableResource(false);
            assertThat(r.isClosed()).isFalse();
            ThrowableExtension.closeResource(null, r);
            assertThat(r.isClosed()).isTrue();
        }
        {
            AutoCloseableResource r = new AutoCloseableResource(false);
            assertThat(r.isClosed()).isFalse();
            Exception suppressor = new Exception();
            ThrowableExtension.closeResource(suppressor, r);
            assertThat(r.isClosed()).isTrue();
            assertThat(ThrowableExtension.getSuppressed(suppressor)).isEmpty();
        }
        {
            AutoCloseableResource r = new AutoCloseableResource(true);
            assertThat(r.isClosed()).isFalse();
            MoreAsserts.assertThrows(IOException.class, () -> ThrowableExtension.closeResource(null, r));
            assertThat(r.isClosed()).isFalse();
        }
        {
            AutoCloseableResource r = new AutoCloseableResource(true);
            assertThat(r.isClosed()).isFalse();
            Exception suppressor = new Exception();
            MoreAsserts.assertThrows(Exception.class, () -> ThrowableExtension.closeResource(suppressor, r));
            assertThat(r.isClosed()).isFalse();// Failed to close.

            if (!(ThrowableExtensionTestUtility.isNullStrategy())) {
                assertThat(ThrowableExtension.getSuppressed(suppressor)).hasLength(1);
                assertThat(ThrowableExtension.getSuppressed(suppressor)[0].getClass()).isEqualTo(IOException.class);
            }
            assertThat(r.isClosed()).isFalse();
        }
    }

    /**
     * LightweightStackTraceRecorder tracks the calls of various printStackTrace(*), and ensures that
     *
     * <p>suppressed exceptions are printed only once.
     */
    @Test
    public void testLightweightStackTraceRecorder() throws IOException {
        MimicDesugaringStrategy strategy = new MimicDesugaringStrategy();
        ThrowableExtensionTest.ExceptionForTest receiver = new ThrowableExtensionTest.ExceptionForTest(strategy);
        FileNotFoundException suppressed = new FileNotFoundException();
        strategy.addSuppressed(receiver, suppressed);
        String trace = ThrowableExtensionTest.printStackTraceStderrToString(() -> strategy.printStackTrace(receiver));
        assertThat(trace).contains(MimicDesugaringStrategy.SUPPRESSED_PREFIX);
        assertThat(ThrowableExtensionTest.countOccurrences(trace, MimicDesugaringStrategy.SUPPRESSED_PREFIX)).isEqualTo(1);
    }

    @Test
    public void testMimicDesugaringStrategy() throws IOException {
        MimicDesugaringStrategy strategy = new MimicDesugaringStrategy();
        IOException receiver = new IOException();
        FileNotFoundException suppressed = new FileNotFoundException();
        strategy.addSuppressed(receiver, suppressed);
        assertThat(ThrowableExtensionTest.printStackTracePrintStreamToString(( stream) -> strategy.printStackTrace(receiver, stream))).contains(MimicDesugaringStrategy.SUPPRESSED_PREFIX);
        assertThat(ThrowableExtensionTest.printStackTracePrintWriterToString(( writer) -> strategy.printStackTrace(receiver, writer))).contains(MimicDesugaringStrategy.SUPPRESSED_PREFIX);
        assertThat(ThrowableExtensionTest.printStackTraceStderrToString(() -> strategy.printStackTrace(receiver))).contains(MimicDesugaringStrategy.SUPPRESSED_PREFIX);
    }

    private interface PrintStackTraceCaller {
        void printStackTrace();
    }

    @Test
    public void testNullDesugaringStrategy() throws IOException {
        NullDesugaringStrategy strategy = new NullDesugaringStrategy();
        IOException receiver = new IOException();
        FileNotFoundException suppressed = new FileNotFoundException();
        strategy.addSuppressed(receiver, suppressed);
        assertThat(strategy.getSuppressed(receiver)).isEmpty();
        strategy.addSuppressed(receiver, suppressed);
        assertThat(strategy.getSuppressed(receiver)).isEmpty();
        assertThat(ThrowableExtensionTest.printStackTracePrintStreamToString(( stream) -> receiver.printStackTrace(stream))).isEqualTo(ThrowableExtensionTest.printStackTracePrintStreamToString(( stream) -> strategy.printStackTrace(receiver, stream)));
        assertThat(ThrowableExtensionTest.printStackTracePrintWriterToString(receiver::printStackTrace)).isEqualTo(ThrowableExtensionTest.printStackTracePrintWriterToString(( writer) -> strategy.printStackTrace(receiver, writer)));
        assertThat(ThrowableExtensionTest.printStackTraceStderrToString(receiver::printStackTrace)).isEqualTo(ThrowableExtensionTest.printStackTraceStderrToString(() -> strategy.printStackTrace(receiver)));
    }

    @Test
    public void testReuseDesugaringStrategy() throws IOException {
        ReuseDesugaringStrategy strategy = new ReuseDesugaringStrategy();
        IOException receiver = new IOException();
        FileNotFoundException suppressed = new FileNotFoundException();
        strategy.addSuppressed(receiver, suppressed);
        assertThat(strategy.getSuppressed(receiver)).asList().containsExactly(((Object[]) (receiver.getSuppressed())));
        assertThat(ThrowableExtensionTest.printStackTracePrintStreamToString(( stream) -> receiver.printStackTrace(stream))).isEqualTo(ThrowableExtensionTest.printStackTracePrintStreamToString(( stream) -> strategy.printStackTrace(receiver, stream)));
        assertThat(ThrowableExtensionTest.printStackTracePrintWriterToString(receiver::printStackTrace)).isEqualTo(ThrowableExtensionTest.printStackTracePrintWriterToString(( writer) -> strategy.printStackTrace(receiver, writer)));
        assertThat(ThrowableExtensionTest.printStackTraceStderrToString(receiver::printStackTrace)).isEqualTo(ThrowableExtensionTest.printStackTraceStderrToString(() -> strategy.printStackTrace(receiver)));
    }

    /**
     * This class
     */
    private static class ExceptionForTest extends Exception {
        private final MimicDesugaringStrategy strategy;

        public ExceptionForTest(MimicDesugaringStrategy strategy) {
            this.strategy = strategy;
        }

        @Override
        public void printStackTrace() {
            this.printStackTrace(System.err);
        }

        /**
         * This method should call this.printStackTrace(PrintWriter) directly. I deliberately change it
         * to strategy.printStackTrace(Throwable, PrintWriter) to simulate the behavior of Desguar, that
         * is, the direct call is intercepted and redirected to ThrowableExtension.
         */
        @Override
        public void printStackTrace(PrintStream s) {
            this.strategy.printStackTrace(this, new PrintWriter(new BufferedWriter(new OutputStreamWriter(s, StandardCharsets.UTF_8))));
        }
    }

    @Test
    public void testStrategySelection() throws IOException, ClassNotFoundException {
        String expectedStrategyClassName = ThrowableExtensionTestUtility.getTwrStrategyClassNameSpecifiedInSystemProperty();
        assertThat(expectedStrategyClassName).isNotEmpty();
        assertThat(expectedStrategyClassName).isEqualTo(STRATEGY.getClass().getName());
        Class<?> expectedStrategyClass = Class.forName(expectedStrategyClassName);
        if (expectedStrategyClass.equals(ReuseDesugaringStrategy.class)) {
            testThrowableExtensionWithReuseDesugaringStrategy();
        } else
            if (expectedStrategyClass.equals(MimicDesugaringStrategy.class)) {
                testThrowableExtensionWithMimicDesugaringStrategy();
            } else
                if (expectedStrategyClass.equals(NullDesugaringStrategy.class)) {
                    testThrowableExtensionWithNullDesugaringStrategy();
                } else {
                    Assert.fail(("unrecognized expected strategy class " + expectedStrategyClassName));
                }


    }

    /**
     * A mocked closeable class, which we can query the closedness.
     */
    private abstract static class AbstractResource {
        private final boolean exceptionOnClose;

        private boolean closed;

        protected AbstractResource(boolean exceptionOnClose) {
            this.exceptionOnClose = exceptionOnClose;
        }

        boolean isClosed() {
            return closed;
        }

        void internalClose() throws IOException {
            if (exceptionOnClose) {
                throw new IOException("intended exception");
            }
            closed = true;
        }
    }
}

