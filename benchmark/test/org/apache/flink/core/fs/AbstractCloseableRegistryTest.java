/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.core.fs;


import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.flink.util.AbstractCloseableRegistry;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link AbstractCloseableRegistry}.
 */
public abstract class AbstractCloseableRegistryTest<C extends Closeable, T> {
    private static final int TEST_TIMEOUT_SECONDS = 10;

    protected AbstractCloseableRegistryTest.ProducerThread[] streamOpenThreads;

    protected AbstractCloseableRegistry<C, T> closeableRegistry;

    protected AtomicInteger unclosedCounter;

    @Test
    public void testClose() throws Exception {
        setup(Integer.MAX_VALUE);
        startThreads();
        for (int i = 0; i < 5; ++i) {
            System.gc();
            Thread.sleep(40);
        }
        closeableRegistry.close();
        joinThreads();
        Assert.assertEquals(0, unclosedCounter.get());
        Assert.assertEquals(0, closeableRegistry.getNumberOfRegisteredCloseables());
        final AbstractCloseableRegistryTest.TestCloseable testCloseable = new AbstractCloseableRegistryTest.TestCloseable();
        try {
            registerCloseable(testCloseable);
            Assert.fail("Closed registry should not accept closeables!");
        } catch (IOException expected) {
        }
        Assert.assertTrue(testCloseable.isClosed());
        Assert.assertEquals(0, unclosedCounter.get());
        Assert.assertEquals(0, closeableRegistry.getNumberOfRegisteredCloseables());
    }

    @Test
    public void testNonBlockingClose() throws Exception {
        setup(Integer.MAX_VALUE);
        final AbstractCloseableRegistryTest.BlockingTestCloseable blockingCloseable = new AbstractCloseableRegistryTest.BlockingTestCloseable();
        registerCloseable(blockingCloseable);
        Assert.assertEquals(1, closeableRegistry.getNumberOfRegisteredCloseables());
        Thread closer = new Thread(() -> {
            try {
                closeableRegistry.close();
            } catch (IOException ignore) {
            }
        });
        closer.start();
        blockingCloseable.awaitClose(AbstractCloseableRegistryTest.TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        final AbstractCloseableRegistryTest.TestCloseable testCloseable = new AbstractCloseableRegistryTest.TestCloseable();
        try {
            registerCloseable(testCloseable);
            Assert.fail("Closed registry should not accept closeables!");
        } catch (IOException ignored) {
        }
        blockingCloseable.unblockClose();
        closer.join();
        Assert.assertTrue(testCloseable.isClosed());
        Assert.assertEquals(0, closeableRegistry.getNumberOfRegisteredCloseables());
    }

    /**
     * A testing producer.
     */
    protected abstract static class ProducerThread<C extends Closeable, T> extends Thread {
        protected final AbstractCloseableRegistry<C, T> registry;

        protected final AtomicInteger refCount;

        protected final int maxStreams;

        protected int numStreams;

        public ProducerThread(AbstractCloseableRegistry<C, T> registry, AtomicInteger refCount, int maxStreams) {
            this.registry = registry;
            this.refCount = refCount;
            this.maxStreams = maxStreams;
            this.numStreams = 0;
        }

        protected abstract void createAndRegisterStream() throws IOException;

        @Override
        public void run() {
            try {
                while ((numStreams) < (maxStreams)) {
                    createAndRegisterStream();
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException ignored) {
                    }
                    if ((maxStreams) != (Integer.MAX_VALUE)) {
                        ++(numStreams);
                    }
                } 
            } catch (Exception ex) {
                // ignored
            }
        }
    }

    /**
     * Testing stream which adds itself to a reference counter while not closed.
     */
    protected static final class TestStream extends FSDataInputStream {
        protected AtomicInteger refCount;

        public TestStream(AtomicInteger refCount) {
            this.refCount = refCount;
            refCount.incrementAndGet();
        }

        @Override
        public void seek(long desired) throws IOException {
        }

        @Override
        public long getPos() throws IOException {
            return 0;
        }

        @Override
        public int read() throws IOException {
            return 0;
        }

        @Override
        public synchronized void close() throws IOException {
            refCount.decrementAndGet();
        }
    }

    /**
     * A noop {@link Closeable} implementation that blocks inside {@link #close()}.
     */
    private static class BlockingTestCloseable implements Closeable {
        private final CountDownLatch closeCalledLatch = new CountDownLatch(1);

        private final CountDownLatch blockCloseLatch = new CountDownLatch(1);

        @Override
        public void close() throws IOException {
            closeCalledLatch.countDown();
            try {
                blockCloseLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        /**
         * Unblocks {@link #close()}.
         */
        public void unblockClose() {
            blockCloseLatch.countDown();
        }

        /**
         * Causes the current thread to wait until {@link #close()} is called.
         */
        public void awaitClose(final long timeout, final TimeUnit timeUnit) throws InterruptedException {
            closeCalledLatch.await(timeout, timeUnit);
        }
    }

    /**
     * A noop {@link Closeable} implementation that tracks whether it was closed.
     */
    private static class TestCloseable implements Closeable {
        private final AtomicBoolean closed = new AtomicBoolean();

        @Override
        public void close() throws IOException {
            Assert.assertTrue("TestCloseable was already closed", closed.compareAndSet(false, true));
        }

        public boolean isClosed() {
            return closed.get();
        }
    }
}

