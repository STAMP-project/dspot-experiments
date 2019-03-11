/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.store;


import LogSegment.HEADER_SIZE;
import TestUtils.RANDOM;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.utils.ByteBufferInputStream;
import com.github.ambry.utils.TestUtils;
import com.github.ambry.utils.Utils;
import com.github.ambry.utils.UtilsTest;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ClosedChannelException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

import static LogSegment.BYTE_BUFFER_SIZE_FOR_APPEND;


/**
 * Tests for {@link LogSegment}.
 */
public class LogSegmentTest {
    private static final int STANDARD_SEGMENT_SIZE = 1024;

    private static final int BIG_SEGMENT_SIZE = 3 * (BYTE_BUFFER_SIZE_FOR_APPEND);

    private final File tempDir;

    private final StoreMetrics metrics;

    /**
     * Sets up a temporary directory that can be used.
     *
     * @throws IOException
     * 		
     */
    public LogSegmentTest() throws IOException {
        tempDir = Files.createTempDirectory(("logSegmentDir-" + (UtilsTest.getRandomString(10)))).toFile();
        tempDir.deleteOnExit();
        MetricRegistry metricRegistry = new MetricRegistry();
        metrics = new StoreMetrics(metricRegistry);
    }

    /**
     * Tests appending and reading to make sure data is written and the data read is consistent with the data written.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void basicWriteAndReadTest() throws IOException {
        String segmentName = "log_current";
        LogSegment segment = getSegment(segmentName, LogSegmentTest.STANDARD_SEGMENT_SIZE, true);
        segment.initBufferForAppend();
        try {
            Assert.assertEquals("Name of segment is inconsistent with what was provided", segmentName, segment.getName());
            Assert.assertEquals("Capacity of segment is inconsistent with what was provided", LogSegmentTest.STANDARD_SEGMENT_SIZE, segment.getCapacityInBytes());
            Assert.assertEquals("Start offset is not equal to header size", HEADER_SIZE, segment.getStartOffset());
            int writeSize = 100;
            byte[] buf = TestUtils.getRandomBytes((3 * writeSize));
            long writeStartOffset = segment.getStartOffset();
            // append with buffer
            int written = segment.appendFrom(ByteBuffer.wrap(buf, 0, writeSize));
            Assert.assertEquals("Size written did not match size of buffer provided", writeSize, written);
            Assert.assertEquals("End offset is not as expected", (writeStartOffset + writeSize), segment.getEndOffset());
            readAndEnsureMatch(segment, writeStartOffset, Arrays.copyOfRange(buf, 0, writeSize));
            // append with channel
            segment.appendFrom(Channels.newChannel(new ByteBufferInputStream(ByteBuffer.wrap(buf, writeSize, writeSize))), writeSize);
            Assert.assertEquals("End offset is not as expected", (writeStartOffset + (2 * writeSize)), segment.getEndOffset());
            readAndEnsureMatch(segment, (writeStartOffset + writeSize), Arrays.copyOfRange(buf, writeSize, (2 * writeSize)));
            // should throw exception if channel's available data less than requested.
            try {
                segment.appendFrom(Channels.newChannel(new ByteBufferInputStream(ByteBuffer.allocate(writeSize))), (writeSize + 1));
                Assert.fail("Should throw exception.");
            } catch (IOException e) {
            }
            // use writeFrom
            segment.writeFrom(Channels.newChannel(new ByteBufferInputStream(ByteBuffer.wrap(buf, (2 * writeSize), writeSize))), segment.getEndOffset(), writeSize);
            Assert.assertEquals("End offset is not as expected", (writeStartOffset + (3 * writeSize)), segment.getEndOffset());
            readAndEnsureMatch(segment, (writeStartOffset + (2 * writeSize)), Arrays.copyOfRange(buf, (2 * writeSize), buf.length));
            readAndEnsureMatch(segment, writeStartOffset, buf);
            // check file size and end offset (they will not match)
            Assert.assertEquals("End offset is not equal to the cumulative bytes written", (writeStartOffset + (3 * writeSize)), segment.getEndOffset());
            Assert.assertEquals("Size in bytes is not equal to size written", (writeStartOffset + (3 * writeSize)), segment.sizeInBytes());
            Assert.assertEquals("Capacity is not equal to allocated size ", LogSegmentTest.STANDARD_SEGMENT_SIZE, segment.getCapacityInBytes());
            // ensure flush doesn't throw any errors.
            segment.flush();
            // close and reopen segment and ensure persistence.
            segment.close();
            segment = new LogSegment(segmentName, new File(tempDir, segmentName), metrics);
            segment.setEndOffset((writeStartOffset + (buf.length)));
            readAndEnsureMatch(segment, writeStartOffset, buf);
        } finally {
            closeSegmentAndDeleteFile(segment);
        }
    }

    /**
     * Verifies getting and closing views and makes sure that data and ref counts are consistent.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void viewAndRefCountTest() throws IOException {
        String segmentName = "log_current";
        LogSegment segment = getSegment(segmentName, LogSegmentTest.STANDARD_SEGMENT_SIZE, true);
        try {
            long startOffset = segment.getStartOffset();
            int readSize = 100;
            int viewCount = 5;
            byte[] data = appendRandomData(segment, (readSize * viewCount));
            for (int i = 0; i < viewCount; i++) {
                getAndVerifyView(segment, startOffset, (i * readSize), data, (i + 1));
            }
            for (int i = 0; i < viewCount; i++) {
                segment.closeView();
                Assert.assertEquals("Ref count is not as expected", ((viewCount - i) - 1), segment.refCount());
            }
        } finally {
            closeSegmentAndDeleteFile(segment);
        }
    }

    /**
     * Tests setting end offset - makes sure legal values are set correctly and illegal values are rejected.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void endOffsetTest() throws IOException {
        String segmentName = "log_current";
        LogSegment segment = getSegment(segmentName, LogSegmentTest.STANDARD_SEGMENT_SIZE, true);
        try {
            long writeStartOffset = segment.getStartOffset();
            int segmentSize = 500;
            appendRandomData(segment, segmentSize);
            Assert.assertEquals("End offset is not as expected", (writeStartOffset + segmentSize), segment.getEndOffset());
            // should be able to set end offset to something >= initial offset and <= file size
            int[] offsetsToSet = new int[]{ ((int) (writeStartOffset)), segmentSize / 2, segmentSize };
            for (int offset : offsetsToSet) {
                segment.setEndOffset(offset);
                Assert.assertEquals("End offset is not equal to what was set", offset, segment.getEndOffset());
                Assert.assertEquals("File channel positioning is incorrect", offset, segment.getView().getSecond().position());
            }
            // cannot set end offset to illegal values (< initial offset or > file size)
            int[] invalidOffsets = new int[]{ ((int) (writeStartOffset - 1)), ((int) ((segment.sizeInBytes()) + 1)) };
            for (int offset : invalidOffsets) {
                try {
                    segment.setEndOffset(offset);
                    Assert.fail((("Setting log end offset an invalid offset [" + offset) + "] should have failed"));
                } catch (IllegalArgumentException e) {
                    // expected. Nothing to do.
                }
            }
        } finally {
            closeSegmentAndDeleteFile(segment);
        }
    }

    /**
     * Tests {@link LogSegment#appendFrom(ByteBuffer)} and {@link LogSegment#appendFrom(ReadableByteChannel, long)} for
     * various cases.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void appendTest() throws IOException {
        // buffer append
        doAppendTest(new LogSegmentTest.Appender() {
            @Override
            public void append(LogSegment segment, ByteBuffer buffer) throws IOException {
                int writeSize = buffer.remaining();
                int written = segment.appendFrom(buffer);
                Assert.assertEquals("Size written did not match size of buffer provided", writeSize, written);
            }
        });
        // channel append
        doAppendTest(new LogSegmentTest.Appender() {
            @Override
            public void append(LogSegment segment, ByteBuffer buffer) throws IOException {
                int writeSize = buffer.remaining();
                segment.appendFrom(Channels.newChannel(new ByteBufferInputStream(buffer)), writeSize);
                Assert.assertFalse("The buffer was not completely written", buffer.hasRemaining());
            }
        });
        // direct IO append
        if (Utils.isLinux()) {
            doAppendTest(new LogSegmentTest.Appender() {
                @Override
                public void append(LogSegment segment, ByteBuffer buffer) throws IOException {
                    int writeSize = buffer.remaining();
                    segment.appendFromDirectly(buffer.array(), 0, writeSize);
                }
            });
        }
    }

    /**
     * Tests {@link LogSegment#readInto(ByteBuffer, long)} and {@link LogSegment#readIntoDirectly(byte[], long, int)}(if
     * current OS is Linux) for various cases.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void readTest() throws IOException {
        Random random = new Random();
        String segmentName = "log_current";
        LogSegment segment = getSegment(segmentName, LogSegmentTest.STANDARD_SEGMENT_SIZE, true);
        try {
            long writeStartOffset = segment.getStartOffset();
            byte[] data = appendRandomData(segment, ((2 * (LogSegmentTest.STANDARD_SEGMENT_SIZE)) / 3));
            readAndEnsureMatch(segment, writeStartOffset, data);
            readDirectlyAndEnsureMatch(segment, writeStartOffset, data);
            int readCount = 10;
            for (int i = 0; i < readCount; i++) {
                int position = random.nextInt(data.length);
                int size = random.nextInt(((data.length) - position));
                readAndEnsureMatch(segment, (writeStartOffset + position), Arrays.copyOfRange(data, position, (position + size)));
                readDirectlyAndEnsureMatch(segment, (writeStartOffset + position), Arrays.copyOfRange(data, position, (position + size)));
            }
            // check for position > endOffset and < data size written to the segment
            // setting end offset to 1/3 * (sizeInBytes - startOffset)
            segment.setEndOffset(((segment.getStartOffset()) + (((segment.sizeInBytes()) - (segment.getStartOffset())) / 3)));
            int position = ((int) (segment.getEndOffset())) + (random.nextInt(((data.length) - ((int) (segment.getEndOffset())))));
            int size = random.nextInt(((data.length) - position));
            readAndEnsureMatch(segment, (writeStartOffset + position), Arrays.copyOfRange(data, position, (position + size)));
            readDirectlyAndEnsureMatch(segment, (writeStartOffset + position), Arrays.copyOfRange(data, position, (position + size)));
            // error scenarios for general IO
            byte[] byteArray = new byte[data.length];
            ByteBuffer readBuf = ByteBuffer.wrap(byteArray);
            // data cannot be read at invalid offsets.
            long[] invalidOffsets = new long[]{ writeStartOffset - 1, segment.sizeInBytes(), (segment.sizeInBytes()) + 1 };
            ByteBuffer buffer = ByteBuffer.wrap(TestUtils.getRandomBytes(1));
            for (long invalidOffset : invalidOffsets) {
                try {
                    segment.readInto(readBuf, invalidOffset);
                    Assert.fail("Should have failed to read because position provided is invalid");
                } catch (IndexOutOfBoundsException e) {
                    Assert.assertEquals("Position of buffer has changed", 0, buffer.position());
                }
            }
            if (Utils.isLinux()) {
                for (long invalidOffset : invalidOffsets) {
                    try {
                        segment.readIntoDirectly(byteArray, invalidOffset, data.length);
                        Assert.fail("Should have failed to read because position provided is invalid");
                    } catch (IndexOutOfBoundsException e) {
                        Assert.assertEquals("Position of buffer has changed", 0, buffer.position());
                    }
                }
            }
            // position + buffer.remaining > sizeInBytes
            long readOverFlowCount = metrics.overflowReadError.getCount();
            byteArray = new byte[2];
            readBuf = ByteBuffer.allocate(2);
            segment.setEndOffset(segment.getStartOffset());
            position = ((int) (segment.sizeInBytes())) - 1;
            try {
                segment.readInto(readBuf, position);
                Assert.fail("Should have failed to read because position + buffer.remaining() > sizeInBytes");
            } catch (IndexOutOfBoundsException e) {
                Assert.assertEquals("Read overflow should have been reported", (readOverFlowCount + 1), metrics.overflowReadError.getCount());
                Assert.assertEquals("Position of buffer has changed", 0, readBuf.position());
            }
            if (Utils.isLinux()) {
                try {
                    segment.readIntoDirectly(byteArray, position, byteArray.length);
                    Assert.fail("Should have failed to read because position + buffer.remaining() > sizeInBytes");
                } catch (IndexOutOfBoundsException e) {
                    Assert.assertEquals("Read overflow should have been reported", (readOverFlowCount + 2), metrics.overflowReadError.getCount());
                }
            }
            segment.close();
            // read after close
            buffer = ByteBuffer.allocate(1);
            try {
                segment.readInto(buffer, writeStartOffset);
                Assert.fail("Should have failed to read because segment is closed");
            } catch (ClosedChannelException e) {
                Assert.assertEquals("Position of buffer has changed", 0, buffer.position());
            }
            if (Utils.isLinux()) {
                byteArray = new byte[1];
                try {
                    segment.readIntoDirectly(byteArray, writeStartOffset, byteArray.length);
                    Assert.fail("Should have failed to read because segment is closed");
                } catch (ClosedChannelException e) {
                }
            }
        } finally {
            closeSegmentAndDeleteFile(segment);
        }
    }

    /**
     * Tests {@link LogSegment#writeFrom(ReadableByteChannel, long, long)} for various cases.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void writeFromTest() throws IOException {
        String currSegmentName = "log_current";
        LogSegment segment = getSegment(currSegmentName, LogSegmentTest.STANDARD_SEGMENT_SIZE, true);
        try {
            long writeStartOffset = segment.getStartOffset();
            byte[] bufOne = TestUtils.getRandomBytes(((LogSegmentTest.STANDARD_SEGMENT_SIZE) / 3));
            byte[] bufTwo = TestUtils.getRandomBytes(((LogSegmentTest.STANDARD_SEGMENT_SIZE) / 2));
            segment.writeFrom(Channels.newChannel(new ByteBufferInputStream(ByteBuffer.wrap(bufOne))), writeStartOffset, bufOne.length);
            Assert.assertEquals("End offset is not as expected", (writeStartOffset + (bufOne.length)), segment.getEndOffset());
            readAndEnsureMatch(segment, writeStartOffset, bufOne);
            // overwrite using bufTwo
            segment.writeFrom(Channels.newChannel(new ByteBufferInputStream(ByteBuffer.wrap(bufTwo))), writeStartOffset, bufTwo.length);
            Assert.assertEquals("End offset is not as expected", (writeStartOffset + (bufTwo.length)), segment.getEndOffset());
            readAndEnsureMatch(segment, writeStartOffset, bufTwo);
            // overwrite using bufOne
            segment.writeFrom(Channels.newChannel(new ByteBufferInputStream(ByteBuffer.wrap(bufOne))), writeStartOffset, bufOne.length);
            // end offset should not have changed
            Assert.assertEquals("End offset is not as expected", (writeStartOffset + (bufTwo.length)), segment.getEndOffset());
            readAndEnsureMatch(segment, writeStartOffset, bufOne);
            readAndEnsureMatch(segment, (writeStartOffset + (bufOne.length)), Arrays.copyOfRange(bufTwo, bufOne.length, bufTwo.length));
            // write at random locations
            for (int i = 0; i < 10; i++) {
                long offset = writeStartOffset + (Utils.getRandomLong(RANDOM, (((segment.sizeInBytes()) - (bufOne.length)) - writeStartOffset)));
                segment.writeFrom(Channels.newChannel(new ByteBufferInputStream(ByteBuffer.wrap(bufOne))), offset, bufOne.length);
                readAndEnsureMatch(segment, offset, bufOne);
            }
            // try to overwrite using a channel that won't fit
            ByteBuffer failBuf = ByteBuffer.wrap(TestUtils.getRandomBytes(((int) (((LogSegmentTest.STANDARD_SEGMENT_SIZE) - writeStartOffset) + 1))));
            long writeOverFlowCount = metrics.overflowWriteError.getCount();
            try {
                segment.writeFrom(Channels.newChannel(new ByteBufferInputStream(failBuf)), writeStartOffset, failBuf.remaining());
                Assert.fail("WriteFrom should have failed because data won't fit");
            } catch (IndexOutOfBoundsException e) {
                Assert.assertEquals("Write overflow should have been reported", (writeOverFlowCount + 1), metrics.overflowWriteError.getCount());
                Assert.assertEquals("Position of buffer has changed", 0, failBuf.position());
            }
            // data cannot be written at invalid offsets.
            long[] invalidOffsets = new long[]{ writeStartOffset - 1, LogSegmentTest.STANDARD_SEGMENT_SIZE, (LogSegmentTest.STANDARD_SEGMENT_SIZE) + 1 };
            ByteBuffer buffer = ByteBuffer.wrap(TestUtils.getRandomBytes(1));
            for (long invalidOffset : invalidOffsets) {
                try {
                    segment.writeFrom(Channels.newChannel(new ByteBufferInputStream(buffer)), invalidOffset, buffer.remaining());
                    Assert.fail("WriteFrom should have failed because offset provided for write is invalid");
                } catch (IndexOutOfBoundsException e) {
                    Assert.assertEquals("Position of buffer has changed", 0, buffer.position());
                }
            }
            segment.close();
            // ensure that writeFrom fails.
            try {
                segment.writeFrom(Channels.newChannel(new ByteBufferInputStream(buffer)), writeStartOffset, buffer.remaining());
                Assert.fail("WriteFrom should have failed because segments are closed");
            } catch (ClosedChannelException e) {
                Assert.assertEquals("Position of buffer has changed", 0, buffer.position());
            }
        } finally {
            closeSegmentAndDeleteFile(segment);
        }
    }

    /**
     * Tests for special constructor cases.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void constructorTest() throws IOException {
        LogSegment segment = getSegment("log_current", LogSegmentTest.STANDARD_SEGMENT_SIZE, false);
        Assert.assertEquals("Start offset should be 0 when no headers are written", 0, segment.getStartOffset());
    }

    /**
     * Tests for bad construction cases of {@link LogSegment}.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void badConstructionTest() throws IOException {
        // try to construct with a file that does not exist.
        String name = "log_non_existent";
        File file = new File(tempDir, name);
        try {
            new LogSegment(name, file, LogSegmentTest.STANDARD_SEGMENT_SIZE, metrics, true);
            Assert.fail("Construction should have failed because the backing file does not exist");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
        try {
            new LogSegment(name, file, metrics);
            Assert.fail("Construction should have failed because the backing file does not exist");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
        // try to construct with a file that is a directory
        name = tempDir.getName();
        file = new File(tempDir.getParent(), name);
        try {
            new LogSegment(name, file, LogSegmentTest.STANDARD_SEGMENT_SIZE, metrics, true);
            Assert.fail("Construction should have failed because the backing file does not exist");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
        name = tempDir.getName();
        file = new File(tempDir.getParent(), name);
        try {
            new LogSegment(name, file, metrics);
            Assert.fail("Construction should have failed because the backing file does not exist");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
        // unknown version
        LogSegment segment = getSegment("dummy_log", LogSegmentTest.STANDARD_SEGMENT_SIZE, true);
        file = segment.getView().getFirst();
        byte[] header = getHeader(segment);
        byte savedByte = header[0];
        // mess with version
        header[0] = ((byte) ((header[0]) + 10));
        writeHeader(segment, header);
        try {
            new LogSegment(name, file, metrics);
            Assert.fail("Construction should have failed because version is unknown");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
        // bad CRC
        // fix version but mess with data after version
        header[0] = savedByte;
        header[2] = ((header[2]) == ((byte) (1))) ? ((byte) (0)) : ((byte) (1));
        writeHeader(segment, header);
        try {
            new LogSegment(name, file, metrics);
            Assert.fail("Construction should have failed because crc check should have failed");
        } catch (IllegalStateException e) {
            // expected. Nothing to do.
        }
        closeSegmentAndDeleteFile(segment);
    }

    /**
     * Interface for abstracting append operations.
     */
    private interface Appender {
        /**
         * Appends the data of {@code buffer} to {@code segment}.
         *
         * @param segment
         * 		the {@link LogSegment} to append {@code buffer} to.
         * @param buffer
         * 		the data to append to {@code segment}.
         * @throws IOException
         * 		
         */
        void append(LogSegment segment, ByteBuffer buffer) throws IOException;
    }
}

