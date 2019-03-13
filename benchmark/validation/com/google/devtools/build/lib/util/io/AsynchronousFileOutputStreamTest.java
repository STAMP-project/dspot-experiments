/**
 * Copyright 2018 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.util.io;


import com.google.common.io.ByteStreams;
import com.google.devtools.build.lib.runtime.commands.proto.BazelFlagsProto.FlagInfo;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests {@link AsynchronousFileOutputStream}.
 */
@RunWith(JUnit4.class)
public class AsynchronousFileOutputStreamTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private final Random random = ThreadLocalRandom.current();

    private static final char[] RAND_CHARS = "abcdefghijklmnopqrstuvwxzy0123456789-".toCharArray();

    private static final int RAND_STRING_LENGTH = 10;

    @Test
    public void testConcurrentWrites() throws Exception {
        Path logPath = tmp.newFile().toPath();
        AsynchronousFileOutputStream out = new AsynchronousFileOutputStream(logPath.toString());
        Thread[] writers = new Thread[10];
        final CountDownLatch start = new CountDownLatch(writers.length);
        for (int i = 0; i < (writers.length); ++i) {
            String name = "Thread # " + i;
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        start.countDown();
                        start.await();
                    } catch (InterruptedException e) {
                        return;
                    }
                    for (int j = 0; j < 10; ++j) {
                        out.write((((name + " time # ") + j) + "\n"));
                    }
                }
            };
            writers[i] = thread;
            thread.start();
        }
        for (int i = 0; i < (writers.length); ++i) {
            writers[i].join();
        }
        out.close();
        String contents = new String(ByteStreams.toByteArray(Files.newInputStream(logPath)), StandardCharsets.UTF_8);
        for (int i = 0; i < (writers.length); ++i) {
            for (int j = 0; j < 10; ++j) {
                assertThat(contents).contains((((("Thread # " + i) + " time # ") + j) + "\n"));
            }
        }
    }

    @Test
    public void testConcurrentProtoWrites() throws Exception {
        Path logPath = tmp.newFile().toPath();
        AsynchronousFileOutputStream out = new AsynchronousFileOutputStream(logPath.toString());
        ArrayList<FlagInfo> messages = new ArrayList<>();
        for (int i = 0; i < 100; ++i) {
            messages.add(generateRandomMessage());
        }
        Thread[] writers = new Thread[(messages.size()) / 10];
        final CountDownLatch start = new CountDownLatch(writers.length);
        for (int i = 0; i < (writers.length); ++i) {
            int startIndex = i * 10;
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        start.countDown();
                        start.await();
                    } catch (InterruptedException e) {
                        return;
                    }
                    for (int j = startIndex; j < (startIndex + 10); ++j) {
                        out.write(messages.get(j));
                    }
                }
            };
            writers[i] = thread;
            thread.start();
        }
        for (int i = 0; i < (writers.length); ++i) {
            writers[i].join();
        }
        out.close();
        ArrayList<FlagInfo> readMessages = new ArrayList<>();
        try (InputStream in = Files.newInputStream(logPath)) {
            for (int i = 0; i < (messages.size()); ++i) {
                readMessages.add(FlagInfo.parseDelimitedFrom(in));
            }
        }
        assertThat(readMessages).containsExactlyElementsIn(messages);
    }

    @Test
    public void testFailedClosePropagatesIOException() throws Exception {
        OutputStream failingOutputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
            }

            @Override
            public void close() throws IOException {
                throw new IOException("foo");
            }
        };
        AsynchronousFileOutputStream out = new AsynchronousFileOutputStream("", failingOutputStream);
        out.write("bla");
        try {
            out.close();
            Assert.fail("Expected an IOException");
        } catch (IOException expected) {
            assertThat(expected).hasMessageThat().isEqualTo("foo");
        }
    }

    @Test
    public void testFailedClosePropagatesUncheckedException() throws Exception {
        OutputStream failingOutputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
            }

            @Override
            public void close() throws IOException {
                throw new RuntimeException("foo");
            }
        };
        AsynchronousFileOutputStream out = new AsynchronousFileOutputStream("", failingOutputStream);
        out.write("bla");
        try {
            out.close();
            Assert.fail("Expected a RuntimeException");
        } catch (RuntimeException expected) {
            assertThat(expected).hasMessageThat().isEqualTo("foo");
        }
    }

    @Test
    public void testFailedWritePropagatesIOException() throws Exception {
        OutputStream failingOutputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException("foo");
            }

            @Override
            public void close() throws IOException {
            }
        };
        AsynchronousFileOutputStream out = new AsynchronousFileOutputStream("", failingOutputStream);
        out.write("bla");
        out.write("blo");
        try {
            out.close();
            Assert.fail("Expected an IOException");
        } catch (IOException expected) {
            assertThat(expected).hasMessageThat().isEqualTo("foo");
        }
    }

    @Test
    public void testFailedWritePropagatesUncheckedException() throws Exception {
        OutputStream failingOutputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new RuntimeException("foo");
            }

            @Override
            public void close() throws IOException {
            }
        };
        AsynchronousFileOutputStream out = new AsynchronousFileOutputStream("", failingOutputStream);
        out.write("bla");
        out.write("blo");
        try {
            out.close();
            Assert.fail("Expected a RuntimeException");
        } catch (RuntimeException expected) {
            assertThat(expected).hasMessageThat().isEqualTo("foo");
        }
    }

    @Test
    public void testWriteAfterCloseThrowsException() throws Exception {
        AsynchronousFileOutputStream out = new AsynchronousFileOutputStream("", new ByteArrayOutputStream());
        out.write("bla");
        out.close();
        try {
            out.write("blo");
            Assert.fail("Expected an IllegalStateException");
        } catch (IllegalStateException expected) {
            // Expected.
        }
    }
}

