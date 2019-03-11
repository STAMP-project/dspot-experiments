/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.state;


import CheckpointStreamFactory.CheckpointStateOutputStream;
import java.io.IOException;
import java.util.Random;
import javax.annotation.Nullable;
import org.apache.flink.core.fs.FSDataInputStream;
import org.apache.flink.runtime.testutils.CommonTestUtils;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


public class DuplicatingCheckpointOutputStreamTest extends TestLogger {
    /**
     * Test that all writes are duplicated to both streams and that the state reflects what was written.
     */
    @Test
    public void testDuplicatedWrite() throws Exception {
        int streamCapacity = 1024 * 1024;
        TestMemoryCheckpointOutputStream primaryStream = new TestMemoryCheckpointOutputStream(streamCapacity);
        TestMemoryCheckpointOutputStream secondaryStream = new TestMemoryCheckpointOutputStream(streamCapacity);
        TestMemoryCheckpointOutputStream referenceStream = new TestMemoryCheckpointOutputStream(streamCapacity);
        DuplicatingCheckpointOutputStream duplicatingStream = new DuplicatingCheckpointOutputStream(primaryStream, secondaryStream, 64);
        Random random = new Random(42);
        for (int i = 0; i < 500; ++i) {
            int choice = random.nextInt(3);
            if (choice == 0) {
                int val = random.nextInt();
                write(val);
                duplicatingStream.write(val);
            } else {
                byte[] bytes = new byte[random.nextInt(128)];
                random.nextBytes(bytes);
                if (choice == 1) {
                    referenceStream.write(bytes);
                    duplicatingStream.write(bytes);
                } else {
                    int off = ((bytes.length) > 0) ? random.nextInt(bytes.length) : 0;
                    int len = ((bytes.length) > 0) ? random.nextInt(((bytes.length) - off)) : 0;
                    referenceStream.write(bytes, off, len);
                    duplicatingStream.write(bytes, off, len);
                }
            }
            Assert.assertEquals(getPos(), duplicatingStream.getPos());
        }
        StreamStateHandle refStateHandle = referenceStream.closeAndGetHandle();
        StreamStateHandle primaryStateHandle = duplicatingStream.closeAndGetPrimaryHandle();
        StreamStateHandle secondaryStateHandle = duplicatingStream.closeAndGetSecondaryHandle();
        Assert.assertTrue(CommonTestUtils.isSteamContentEqual(refStateHandle.openInputStream(), primaryStateHandle.openInputStream()));
        Assert.assertTrue(CommonTestUtils.isSteamContentEqual(refStateHandle.openInputStream(), secondaryStateHandle.openInputStream()));
        refStateHandle.discardState();
        primaryStateHandle.discardState();
        secondaryStateHandle.discardState();
    }

    /**
     * This is the first of a set of tests that check that exceptions from the secondary stream do not impact that we
     * can create a result for the first stream.
     */
    @Test
    public void testSecondaryWriteFail() throws Exception {
        DuplicatingCheckpointOutputStream duplicatingStream = createDuplicatingStreamWithFailingSecondary();
        testFailingSecondaryStream(duplicatingStream, () -> {
            for (int i = 0; i < 128; i++) {
                duplicatingStream.write(42);
            }
        });
    }

    @Test
    public void testFailingSecondaryWriteArrayFail() throws Exception {
        DuplicatingCheckpointOutputStream duplicatingStream = createDuplicatingStreamWithFailingSecondary();
        testFailingSecondaryStream(duplicatingStream, () -> duplicatingStream.write(new byte[512]));
    }

    @Test
    public void testFailingSecondaryWriteArrayOffsFail() throws Exception {
        DuplicatingCheckpointOutputStream duplicatingStream = createDuplicatingStreamWithFailingSecondary();
        testFailingSecondaryStream(duplicatingStream, () -> duplicatingStream.write(new byte[512], 20, 130));
    }

    @Test
    public void testFailingSecondaryFlush() throws Exception {
        DuplicatingCheckpointOutputStream duplicatingStream = createDuplicatingStreamWithFailingSecondary();
        testFailingSecondaryStream(duplicatingStream, duplicatingStream::flush);
    }

    @Test
    public void testFailingSecondarySync() throws Exception {
        DuplicatingCheckpointOutputStream duplicatingStream = createDuplicatingStreamWithFailingSecondary();
        testFailingSecondaryStream(duplicatingStream, duplicatingStream::sync);
    }

    /**
     * This is the first of a set of tests that check that exceptions from the primary stream are immediately reported.
     */
    @Test
    public void testPrimaryWriteFail() throws Exception {
        DuplicatingCheckpointOutputStream duplicatingStream = createDuplicatingStreamWithFailingPrimary();
        testFailingPrimaryStream(duplicatingStream, () -> {
            for (int i = 0; i < 128; i++) {
                duplicatingStream.write(42);
            }
        });
    }

    @Test
    public void testFailingPrimaryWriteArrayFail() throws Exception {
        DuplicatingCheckpointOutputStream duplicatingStream = createDuplicatingStreamWithFailingPrimary();
        testFailingPrimaryStream(duplicatingStream, () -> duplicatingStream.write(new byte[512]));
    }

    @Test
    public void testFailingPrimaryWriteArrayOffsFail() throws Exception {
        DuplicatingCheckpointOutputStream duplicatingStream = createDuplicatingStreamWithFailingPrimary();
        testFailingPrimaryStream(duplicatingStream, () -> duplicatingStream.write(new byte[512], 20, 130));
    }

    @Test
    public void testFailingPrimaryFlush() throws Exception {
        DuplicatingCheckpointOutputStream duplicatingStream = createDuplicatingStreamWithFailingPrimary();
        testFailingPrimaryStream(duplicatingStream, duplicatingStream::flush);
    }

    @Test
    public void testFailingPrimarySync() throws Exception {
        DuplicatingCheckpointOutputStream duplicatingStream = createDuplicatingStreamWithFailingPrimary();
        testFailingPrimaryStream(duplicatingStream, duplicatingStream::sync);
    }

    /**
     * Tests that in case of unaligned stream positions, the secondary stream is closed and the primary still works.
     * This is important because some code may rely on seeking to stream offsets in the created state files and if the
     * streams are not aligned this code could fail.
     */
    @Test
    public void testUnalignedStreamsException() throws IOException {
        int streamCapacity = 1024 * 1024;
        TestMemoryCheckpointOutputStream primaryStream = new TestMemoryCheckpointOutputStream(streamCapacity);
        TestMemoryCheckpointOutputStream secondaryStream = new TestMemoryCheckpointOutputStream(streamCapacity);
        write(42);
        DuplicatingCheckpointOutputStream stream = new DuplicatingCheckpointOutputStream(primaryStream, secondaryStream);
        Assert.assertNotNull(stream.getSecondaryStreamException());
        Assert.assertTrue(secondaryStream.isClosed());
        stream.write(23);
        try {
            stream.closeAndGetSecondaryHandle();
            Assert.fail();
        } catch (IOException ignore) {
            Assert.assertEquals(ignore.getCause(), stream.getSecondaryStreamException());
        }
        StreamStateHandle primaryHandle = stream.closeAndGetPrimaryHandle();
        try (FSDataInputStream inputStream = primaryHandle.openInputStream()) {
            Assert.assertEquals(42, inputStream.read());
            Assert.assertEquals(23, inputStream.read());
            Assert.assertEquals((-1), inputStream.read());
        }
    }

    /**
     * Stream that throws {@link IOException} on all relevant methods under test.
     */
    private static class FailingCheckpointOutStream extends CheckpointStreamFactory.CheckpointStateOutputStream {
        private boolean closed = false;

        @Nullable
        @Override
        public StreamStateHandle closeAndGetHandle() throws IOException {
            throw new IOException();
        }

        @Override
        public long getPos() throws IOException {
            return 0;
        }

        @Override
        public void write(int b) throws IOException {
            throw new IOException();
        }

        @Override
        public void flush() throws IOException {
            throw new IOException();
        }

        @Override
        public void sync() throws IOException {
            throw new IOException();
        }

        @Override
        public void close() throws IOException {
            this.closed = true;
        }

        public boolean isClosed() {
            return closed;
        }
    }

    @FunctionalInterface
    private interface StreamTestMethod {
        void call() throws IOException;
    }
}

