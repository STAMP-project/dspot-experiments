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
package org.eclipse.jetty.websocket.common.message;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.MappedByteBufferPool;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDir;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDirExtension;
import org.eclipse.jetty.util.BufferUtil;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;


@ExtendWith(WorkDirExtension.class)
public class MessageInputStreamTest {
    public WorkDir testdir;

    public ByteBufferPool bufferPool = new MappedByteBufferPool();

    @Test
    public void testBasicAppendRead() throws IOException {
        try (MessageInputStream stream = new MessageInputStream()) {
            Assertions.assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
                // Append a single message (simple, short)
                ByteBuffer payload = BufferUtil.toBuffer("Hello World", StandardCharsets.UTF_8);
                boolean fin = true;
                stream.appendFrame(payload, fin);
                // Read entire message it from the stream.
                byte[] buf = new byte[32];
                int len = stream.read(buf);
                String message = new String(buf, 0, len, StandardCharsets.UTF_8);
                // Test it
                MatcherAssert.assertThat("Message", message, Matchers.is("Hello World"));
            });
        }
    }

    @Test
    public void testBlockOnRead() throws Exception {
        try (MessageInputStream stream = new MessageInputStream()) {
            final AtomicBoolean hadError = new AtomicBoolean(false);
            final CountDownLatch startLatch = new CountDownLatch(1);
            // This thread fills the stream (from the "worker" thread)
            // But slowly (intentionally).
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        startLatch.countDown();
                        boolean fin = false;
                        TimeUnit.MILLISECONDS.sleep(200);
                        stream.appendFrame(BufferUtil.toBuffer("Saved", StandardCharsets.UTF_8), fin);
                        TimeUnit.MILLISECONDS.sleep(200);
                        stream.appendFrame(BufferUtil.toBuffer(" by ", StandardCharsets.UTF_8), fin);
                        fin = true;
                        TimeUnit.MILLISECONDS.sleep(200);
                        stream.appendFrame(BufferUtil.toBuffer("Zero", StandardCharsets.UTF_8), fin);
                    } catch (IOException | InterruptedException e) {
                        hadError.set(true);
                        e.printStackTrace(System.err);
                    }
                }
            }).start();
            Assertions.assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
                // wait for thread to start
                startLatch.await();
                // Read it from the stream.
                byte[] buf = new byte[32];
                int len = stream.read(buf);
                String message = new String(buf, 0, len, StandardCharsets.UTF_8);
                // Test it
                MatcherAssert.assertThat("Error when appending", hadError.get(), Matchers.is(false));
                MatcherAssert.assertThat("Message", message, Matchers.is("Saved by Zero"));
            });
        }
    }

    @Test
    public void testBlockOnReadInitial() throws IOException {
        try (MessageInputStream stream = new MessageInputStream()) {
            final AtomicBoolean hadError = new AtomicBoolean(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        boolean fin = true;
                        // wait for a little bit before populating buffers
                        TimeUnit.MILLISECONDS.sleep(400);
                        stream.appendFrame(BufferUtil.toBuffer("I will conquer", StandardCharsets.UTF_8), fin);
                    } catch (IOException | InterruptedException e) {
                        hadError.set(true);
                        e.printStackTrace(System.err);
                    }
                }
            }).start();
            Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
                // Read byte from stream.
                int b = stream.read();
                // Should be a byte, blocking till byte received.
                // Test it
                MatcherAssert.assertThat("Error when appending", hadError.get(), Matchers.is(false));
                MatcherAssert.assertThat("Initial byte", b, Matchers.is(((int) ('I'))));
            });
        }
    }

    @Test
    public void testReadByteNoBuffersClosed() throws IOException {
        try (MessageInputStream stream = new MessageInputStream()) {
            final AtomicBoolean hadError = new AtomicBoolean(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // wait for a little bit before sending input closed
                        TimeUnit.MILLISECONDS.sleep(400);
                        stream.messageComplete();
                    } catch (InterruptedException e) {
                        hadError.set(true);
                        e.printStackTrace(System.err);
                    }
                }
            }).start();
            Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
                // Read byte from stream.
                int b = stream.read();
                // Should be a -1, indicating the end of the stream.
                // Test it
                MatcherAssert.assertThat("Error when appending", hadError.get(), Matchers.is(false));
                MatcherAssert.assertThat("Initial byte", b, Matchers.is((-1)));
            });
        }
    }

    @Test
    public void testAppendEmptyPayloadRead() throws IOException {
        try (MessageInputStream stream = new MessageInputStream()) {
            Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
                // Append parts of message
                ByteBuffer msg1 = BufferUtil.toBuffer("Hello ", StandardCharsets.UTF_8);
                ByteBuffer msg2 = ByteBuffer.allocate(0);// what is being tested

                ByteBuffer msg3 = BufferUtil.toBuffer("World", StandardCharsets.UTF_8);
                stream.appendFrame(msg1, false);
                stream.appendFrame(msg2, false);
                stream.appendFrame(msg3, true);
                // Read entire message it from the stream.
                byte[] buf = new byte[32];
                int len = stream.read(buf);
                String message = new String(buf, 0, len, StandardCharsets.UTF_8);
                // Test it
                MatcherAssert.assertThat("Message", message, Matchers.is("Hello World"));
            });
        }
    }

    @Test
    public void testAppendNullPayloadRead() throws IOException {
        try (MessageInputStream stream = new MessageInputStream()) {
            Assertions.assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
                // Append parts of message
                ByteBuffer msg1 = BufferUtil.toBuffer("Hello ", StandardCharsets.UTF_8);
                ByteBuffer msg2 = null;// what is being tested

                ByteBuffer msg3 = BufferUtil.toBuffer("World", StandardCharsets.UTF_8);
                stream.appendFrame(msg1, false);
                stream.appendFrame(msg2, false);
                stream.appendFrame(msg3, true);
                // Read entire message it from the stream.
                byte[] buf = new byte[32];
                int len = stream.read(buf);
                String message = new String(buf, 0, len, StandardCharsets.UTF_8);
                // Test it
                MatcherAssert.assertThat("Message", message, Matchers.is("Hello World"));
            });
        }
    }
}

