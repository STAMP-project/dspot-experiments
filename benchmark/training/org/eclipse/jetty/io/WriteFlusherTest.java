/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.io;


import Callback.NOOP;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritePendingException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.FutureCallback;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class WriteFlusherTest {
    @Test
    public void testCompleteNoBlocking() throws Exception {
        testCompleteWrite(false);
    }

    @Test
    public void testIgnorePreviousFailures() throws Exception {
        testCompleteWrite(true);
    }

    @Test
    public void testClosedNoBlocking() throws Exception {
        ByteArrayEndPoint endPoint = new ByteArrayEndPoint(new byte[0], 16);
        endPoint.close();
        AtomicBoolean incompleteFlush = new AtomicBoolean();
        WriteFlusher flusher = new WriteFlusher(endPoint) {
            @Override
            protected void onIncompleteFlush() {
                incompleteFlush.set(true);
            }
        };
        FutureCallback callback = new FutureCallback();
        flusher.write(callback, BufferUtil.toBuffer("foo"));
        Assertions.assertTrue(callback.isDone());
        Assertions.assertFalse(incompleteFlush.get());
        ExecutionException e = Assertions.assertThrows(ExecutionException.class, () -> {
            callback.get();
        });
        MatcherAssert.assertThat(e.getCause(), Matchers.instanceOf(IOException.class));
        MatcherAssert.assertThat(e.getCause().getMessage(), Matchers.containsString("CLOSED"));
        Assertions.assertEquals("", endPoint.takeOutputString());
        Assertions.assertTrue(flusher.isFailed());
    }

    @Test
    public void testCompleteBlocking() throws Exception {
        ByteArrayEndPoint endPoint = new ByteArrayEndPoint(new byte[0], 10);
        AtomicBoolean incompleteFlush = new AtomicBoolean();
        WriteFlusher flusher = new WriteFlusher(endPoint) {
            @Override
            protected void onIncompleteFlush() {
                incompleteFlush.set(true);
            }
        };
        FutureCallback callback = new FutureCallback();
        flusher.write(callback, BufferUtil.toBuffer("How now brown cow!"));
        Assertions.assertFalse(callback.isDone());
        Assertions.assertFalse(callback.isCancelled());
        Assertions.assertTrue(incompleteFlush.get());
        Assertions.assertThrows(TimeoutException.class, () -> {
            callback.get(100, TimeUnit.MILLISECONDS);
        });
        incompleteFlush.set(false);
        Assertions.assertEquals("How now br", endPoint.takeOutputString());
        flusher.completeWrite();
        Assertions.assertTrue(callback.isDone());
        Assertions.assertEquals("own cow!", endPoint.takeOutputString());
        Assertions.assertFalse(incompleteFlush.get());
        Assertions.assertTrue(flusher.isIdle());
    }

    @Test
    public void testCloseWhileBlocking() throws Exception {
        ByteArrayEndPoint endPoint = new ByteArrayEndPoint(new byte[0], 10);
        AtomicBoolean incompleteFlush = new AtomicBoolean();
        WriteFlusher flusher = new WriteFlusher(endPoint) {
            @Override
            protected void onIncompleteFlush() {
                incompleteFlush.set(true);
            }
        };
        FutureCallback callback = new FutureCallback();
        flusher.write(callback, BufferUtil.toBuffer("How now brown cow!"));
        Assertions.assertFalse(callback.isDone());
        Assertions.assertFalse(callback.isCancelled());
        Assertions.assertTrue(incompleteFlush.get());
        incompleteFlush.set(false);
        Assertions.assertEquals("How now br", endPoint.takeOutputString());
        endPoint.close();
        flusher.completeWrite();
        Assertions.assertTrue(callback.isDone());
        Assertions.assertFalse(incompleteFlush.get());
        ExecutionException e = Assertions.assertThrows(ExecutionException.class, () -> callback.get());
        MatcherAssert.assertThat(e.getCause(), Matchers.instanceOf(IOException.class));
        MatcherAssert.assertThat(e.getCause().getMessage(), Matchers.containsString("CLOSED"));
        Assertions.assertEquals("", endPoint.takeOutputString());
        Assertions.assertTrue(flusher.isFailed());
    }

    @Test
    public void testFailWhileBlocking() throws Exception {
        ByteArrayEndPoint endPoint = new ByteArrayEndPoint(new byte[0], 10);
        AtomicBoolean incompleteFlush = new AtomicBoolean();
        WriteFlusher flusher = new WriteFlusher(endPoint) {
            @Override
            protected void onIncompleteFlush() {
                incompleteFlush.set(true);
            }
        };
        FutureCallback callback = new FutureCallback();
        flusher.write(callback, BufferUtil.toBuffer("How now brown cow!"));
        Assertions.assertFalse(callback.isDone());
        Assertions.assertFalse(callback.isCancelled());
        Assertions.assertTrue(incompleteFlush.get());
        incompleteFlush.set(false);
        Assertions.assertEquals("How now br", endPoint.takeOutputString());
        String reason = "Failure";
        flusher.onFail(new IOException(reason));
        flusher.completeWrite();
        Assertions.assertTrue(callback.isDone());
        Assertions.assertFalse(incompleteFlush.get());
        ExecutionException e = Assertions.assertThrows(ExecutionException.class, () -> callback.get());
        MatcherAssert.assertThat(e.getCause(), Matchers.instanceOf(IOException.class));
        MatcherAssert.assertThat(e.getCause().getMessage(), Matchers.containsString(reason));
        Assertions.assertEquals("", endPoint.takeOutputString());
        Assertions.assertTrue(flusher.isFailed());
    }

    @Test
    public void testConcurrent() throws Exception {
        Random random = new Random();
        ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(100);
        try {
            String reason = "THE_CAUSE";
            WriteFlusherTest.ConcurrentWriteFlusher[] flushers = new WriteFlusherTest.ConcurrentWriteFlusher[50000];
            FutureCallback[] futures = new FutureCallback[flushers.length];
            for (int i = 0; i < (flushers.length); ++i) {
                int size = 5 + (random.nextInt(15));
                ByteArrayEndPoint endPoint = new ByteArrayEndPoint(new byte[0], size);
                WriteFlusherTest.ConcurrentWriteFlusher flusher = new WriteFlusherTest.ConcurrentWriteFlusher(endPoint, scheduler, random);
                flushers[i] = flusher;
                FutureCallback callback = new FutureCallback();
                futures[i] = callback;
                scheduler.schedule(() -> onFail(new Throwable(reason)), ((random.nextInt(75)) + 1), TimeUnit.MILLISECONDS);
                flusher.write(callback, BufferUtil.toBuffer("How Now Brown Cow."), BufferUtil.toBuffer(" The quick brown fox jumped over the lazy dog!"));
            }
            int completed = 0;
            int failed = 0;
            for (int i = 0; i < (flushers.length); ++i) {
                try {
                    futures[i].get(15, TimeUnit.SECONDS);
                    Assertions.assertEquals("How Now Brown Cow. The quick brown fox jumped over the lazy dog!", flushers[i].getContent());
                    completed++;
                } catch (ExecutionException x) {
                    Assertions.assertEquals(reason, x.getCause().getMessage());
                    failed++;
                }
            }
            MatcherAssert.assertThat(completed, Matchers.greaterThan(0));
            MatcherAssert.assertThat(failed, Matchers.greaterThan(0));
            Assertions.assertEquals(flushers.length, (completed + failed));
        } finally {
            scheduler.shutdown();
        }
    }

    @Test
    public void testPendingWriteDoesNotStoreConsumedBuffers() throws Exception {
        ByteArrayEndPoint endPoint = new ByteArrayEndPoint(new byte[0], 10);
        int toWrite = endPoint.getOutput().capacity();
        byte[] chunk1 = new byte[toWrite / 2];
        Arrays.fill(chunk1, ((byte) (1)));
        ByteBuffer buffer1 = ByteBuffer.wrap(chunk1);
        byte[] chunk2 = new byte[toWrite];
        Arrays.fill(chunk1, ((byte) (2)));
        ByteBuffer buffer2 = ByteBuffer.wrap(chunk2);
        AtomicBoolean incompleteFlush = new AtomicBoolean();
        WriteFlusher flusher = new WriteFlusher(endPoint) {
            @Override
            protected void onIncompleteFlush() {
                incompleteFlush.set(true);
            }
        };
        flusher.write(NOOP, buffer1, buffer2);
        Assertions.assertTrue(incompleteFlush.get());
        Assertions.assertFalse(buffer1.hasRemaining());
        // Reuse buffer1
        buffer1.clear();
        Arrays.fill(chunk1, ((byte) (3)));
        int remaining1 = buffer1.remaining();
        // Complete the write
        endPoint.takeOutput();
        flusher.completeWrite();
        // Make sure buffer1 is unchanged
        Assertions.assertEquals(remaining1, buffer1.remaining());
    }

    @Test
    public void testConcurrentWrites() throws Exception {
        ByteArrayEndPoint endPoint = new ByteArrayEndPoint(new byte[0], 16);
        CountDownLatch flushLatch = new CountDownLatch(1);
        WriteFlusher flusher = new WriteFlusher(endPoint) {
            @Override
            protected ByteBuffer[] flush(ByteBuffer[] buffers) throws IOException {
                try {
                    flushLatch.countDown();
                    Thread.sleep(2000);
                    return super.flush(buffers);
                } catch (InterruptedException x) {
                    throw new InterruptedIOException();
                }
            }

            @Override
            protected void onIncompleteFlush() {
            }
        };
        // Two concurrent writes.
        new Thread(() -> flusher.write(NOOP, BufferUtil.toBuffer("foo"))).start();
        Assertions.assertTrue(flushLatch.await(1, TimeUnit.SECONDS));
        Assertions.assertThrows(WritePendingException.class, () -> {
            // The second write throws WritePendingException.
            flusher.write(NOOP, BufferUtil.toBuffer("bar"));
        });
    }

    @Test
    public void testConcurrentWriteAndOnFail() throws Exception {
        Assertions.assertThrows(ExecutionException.class, () -> {
            ByteArrayEndPoint endPoint = new ByteArrayEndPoint(new byte[0], 16);
            WriteFlusher flusher = new WriteFlusher(endPoint) {
                @Override
                protected ByteBuffer[] flush(ByteBuffer[] buffers) throws IOException {
                    ByteBuffer[] result = super.flush(buffers);
                    boolean notified = onFail(new Throwable());
                    Assertions.assertTrue(notified);
                    return result;
                }

                @Override
                protected void onIncompleteFlush() {
                }
            };
            FutureCallback callback = new FutureCallback();
            flusher.write(callback, BufferUtil.toBuffer("foo"));
            Assertions.assertTrue(flusher.isFailed());
            callback.get(1, TimeUnit.SECONDS);
        });
    }

    @Test
    public void testConcurrentIncompleteFlushAndOnFail() throws Exception {
        int capacity = 8;
        ByteArrayEndPoint endPoint = new ByteArrayEndPoint(new byte[0], capacity);
        String reason = "the_reason";
        WriteFlusher flusher = new WriteFlusher(endPoint) {
            @Override
            protected void onIncompleteFlush() {
                onFail(new Throwable(reason));
            }
        };
        FutureCallback callback = new FutureCallback();
        byte[] content = new byte[capacity * 2];
        flusher.write(callback, BufferUtil.toBuffer(content));
        try {
            // Callback must be failed.
            callback.get(1, TimeUnit.SECONDS);
        } catch (ExecutionException x) {
            Assertions.assertEquals(reason, x.getCause().getMessage());
        }
    }

    private static class ConcurrentWriteFlusher extends WriteFlusher implements Runnable {
        private final ByteArrayEndPoint endPoint;

        private final ScheduledExecutorService scheduler;

        private final Random random;

        private String content = "";

        private ConcurrentWriteFlusher(ByteArrayEndPoint endPoint, ScheduledThreadPoolExecutor scheduler, Random random) {
            super(endPoint);
            this.endPoint = endPoint;
            this.scheduler = scheduler;
            this.random = random;
        }

        @Override
        protected void onIncompleteFlush() {
            scheduler.schedule(this, (1 + (random.nextInt(9))), TimeUnit.MILLISECONDS);
        }

        @Override
        public void run() {
            content += endPoint.takeOutputString();
            completeWrite();
        }

        private String getContent() {
            content += endPoint.takeOutputString();
            return content;
        }
    }
}

