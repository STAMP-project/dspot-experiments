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


import TestUtils.RANDOM;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.utils.ByteBufferInputStream;
import com.github.ambry.utils.ByteBufferOutputStream;
import com.github.ambry.utils.TestUtils;
import com.github.ambry.utils.Utils;
import com.github.ambry.utils.UtilsTest;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for {@link StoreMessageReadSet} and {@link BlobReadOptions}.
 */
@RunWith(Parameterized.class)
public class StoreMessageReadSetTest {
    private static final StoreKeyFactory STORE_KEY_FACTORY;

    static {
        try {
            STORE_KEY_FACTORY = Utils.getObj("com.github.ambry.store.MockIdFactory");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private final File tempDir;

    private final StoreMetrics metrics;

    private final boolean doDataPrefetch;

    /**
     * Creates a temporary directory.
     *
     * @throws IOException
     * 		
     */
    public StoreMessageReadSetTest(boolean doDataPrefetch) throws IOException {
        tempDir = StoreTestUtils.createTempDirectory(("storeMessageReadSetDir-" + (UtilsTest.getRandomString(10))));
        MetricRegistry metricRegistry = new MetricRegistry();
        metrics = new StoreMetrics(metricRegistry);
        this.doDataPrefetch = doDataPrefetch;
    }

    /**
     * Primarily tests {@link StoreMessageReadSet} and its APIs but also checks the the {@link Comparable} APIs of
     * {@link BlobReadOptions}.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void storeMessageReadSetTest() throws IOException {
        int logCapacity = 2000;
        int segCapacity = 1000;
        Log log = new Log(tempDir.getAbsolutePath(), logCapacity, segCapacity, StoreTestUtils.DEFAULT_DISK_SPACE_ALLOCATOR, metrics);
        try {
            LogSegment firstSegment = log.getFirstSegment();
            int availableSegCapacity = ((int) (segCapacity - (firstSegment.getStartOffset())));
            byte[] srcOfTruth = TestUtils.getRandomBytes((2 * availableSegCapacity));
            ReadableByteChannel dataChannel = Channels.newChannel(new ByteBufferInputStream(ByteBuffer.wrap(srcOfTruth)));
            log.appendFrom(dataChannel, availableSegCapacity);
            log.appendFrom(dataChannel, availableSegCapacity);
            LogSegment secondSegment = log.getNextSegment(firstSegment);
            Offset firstSegOffset1 = new Offset(firstSegment.getName(), firstSegment.getStartOffset());
            Offset firstSegOffset2 = new Offset(firstSegment.getName(), ((firstSegment.getStartOffset()) + (availableSegCapacity / 2)));
            Offset secondSegOffset1 = new Offset(secondSegment.getName(), secondSegment.getStartOffset());
            Offset secondSegOffset2 = new Offset(secondSegment.getName(), ((secondSegment.getStartOffset()) + (availableSegCapacity / 2)));
            List<MockId> mockIdList = new ArrayList<>();
            MockId mockId = new MockId("id1");
            mockIdList.add(mockId);
            BlobReadOptions ro1 = new BlobReadOptions(log, firstSegOffset2, new MessageInfo(mockId, (availableSegCapacity / 3), 1, Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM), ((System.currentTimeMillis()) + (RANDOM.nextInt(10000)))));
            mockId = new MockId("id2");
            mockIdList.add(mockId);
            BlobReadOptions ro2 = new BlobReadOptions(log, secondSegOffset1, new MessageInfo(mockId, (availableSegCapacity / 4), 1, Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM), ((System.currentTimeMillis()) + (RANDOM.nextInt(10000)))));
            mockId = new MockId("id3");
            mockIdList.add(mockId);
            BlobReadOptions ro3 = new BlobReadOptions(log, secondSegOffset2, new MessageInfo(mockId, (availableSegCapacity / 2), 1, Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM), ((System.currentTimeMillis()) + (RANDOM.nextInt(10000)))));
            mockId = new MockId("id4");
            mockIdList.add(mockId);
            BlobReadOptions ro4 = new BlobReadOptions(log, firstSegOffset1, new MessageInfo(mockId, (availableSegCapacity / 5), 1, Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM), ((System.currentTimeMillis()) + (RANDOM.nextInt(10000)))));
            // to test equality in the compareTo() of BlobReadOptions
            mockId = new MockId("id5");
            mockIdList.add(mockId);
            BlobReadOptions ro5 = new BlobReadOptions(log, firstSegOffset2, new MessageInfo(mockId, (availableSegCapacity / 6), 1, Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM), ((System.currentTimeMillis()) + (RANDOM.nextInt(10000)))));
            List<BlobReadOptions> options = new ArrayList(Arrays.asList(ro1, ro2, ro3, ro4, ro5));
            MessageReadSet readSet = new StoreMessageReadSet(options);
            Assert.assertEquals(readSet.count(), options.size());
            // options should get sorted by offsets in the constructor
            Assert.assertEquals(readSet.getKeyAt(0), mockIdList.get(3));
            Assert.assertEquals(readSet.sizeInBytes(0), (availableSegCapacity / 5));
            Assert.assertEquals(readSet.getKeyAt(1), mockIdList.get(0));
            Assert.assertEquals(readSet.sizeInBytes(1), (availableSegCapacity / 3));
            Assert.assertEquals(readSet.getKeyAt(2), mockIdList.get(4));
            Assert.assertEquals(readSet.sizeInBytes(2), (availableSegCapacity / 6));
            Assert.assertEquals(readSet.getKeyAt(3), mockIdList.get(1));
            Assert.assertEquals(readSet.sizeInBytes(3), (availableSegCapacity / 4));
            Assert.assertEquals(readSet.getKeyAt(4), mockIdList.get(2));
            Assert.assertEquals(readSet.sizeInBytes(4), (availableSegCapacity / 2));
            ByteBuffer readBuf = ByteBuffer.allocate((availableSegCapacity / 5));
            ByteBufferOutputStream stream = new ByteBufferOutputStream(readBuf);
            // read the first one all at once
            if (doDataPrefetch) {
                readSet.doPrefetch(0, 0, Long.MAX_VALUE);
            }
            long written = readSet.writeTo(0, Channels.newChannel(stream), 0, Long.MAX_VALUE);
            Assert.assertEquals("Return value from writeTo() is incorrect", (availableSegCapacity / 5), written);
            Assert.assertArrayEquals(readBuf.array(), Arrays.copyOfRange(srcOfTruth, 0, (availableSegCapacity / 5)));
            // read the second one byte by byte
            readBuf = ByteBuffer.allocate((availableSegCapacity / 3));
            stream = new ByteBufferOutputStream(readBuf);
            WritableByteChannel channel = Channels.newChannel(stream);
            long currentReadOffset = 0;
            if (doDataPrefetch) {
                readSet.doPrefetch(1, currentReadOffset, (availableSegCapacity / 3));
            }
            while (currentReadOffset < (availableSegCapacity / 3)) {
                written = readSet.writeTo(1, channel, currentReadOffset, 1);
                Assert.assertEquals("Return value from writeTo() is incorrect", 1, written);
                currentReadOffset++;
            } 
            long startOffset = availableSegCapacity / 2;
            long endOffset = (availableSegCapacity / 2) + (availableSegCapacity / 3);
            Assert.assertArrayEquals(readBuf.array(), Arrays.copyOfRange(srcOfTruth, ((int) (startOffset)), ((int) (endOffset))));
            // read the last one in multiple stages
            readBuf = ByteBuffer.allocate((availableSegCapacity / 2));
            stream = new ByteBufferOutputStream(readBuf);
            channel = Channels.newChannel(stream);
            currentReadOffset = 0;
            if (doDataPrefetch) {
                readSet.doPrefetch(4, currentReadOffset, (availableSegCapacity / 2));
            }
            while (currentReadOffset < (availableSegCapacity / 2)) {
                written = readSet.writeTo(4, channel, currentReadOffset, (availableSegCapacity / 6));
                long expectedWritten = Math.min(((availableSegCapacity / 2) - currentReadOffset), (availableSegCapacity / 6));
                Assert.assertEquals("Return value from writeTo() is incorrect", expectedWritten, written);
                currentReadOffset += availableSegCapacity / 6;
            } 
            startOffset = availableSegCapacity + (availableSegCapacity / 2);
            endOffset = startOffset + (availableSegCapacity / 2);
            Assert.assertArrayEquals(readBuf.array(), Arrays.copyOfRange(srcOfTruth, ((int) (startOffset)), ((int) (endOffset))));
            // should not write anything if relative offset is at the size
            readBuf = ByteBuffer.allocate(1);
            stream = new ByteBufferOutputStream(readBuf);
            channel = Channels.newChannel(stream);
            written = readSet.writeTo(0, channel, readSet.sizeInBytes(0), 1);
            Assert.assertEquals("No data should have been written", 0, written);
            try {
                readSet.sizeInBytes(options.size());
                Assert.fail("Reading should have failed because index is out of bounds");
            } catch (IndexOutOfBoundsException e) {
                // expected. Nothing to do.
            }
            try {
                readSet.writeTo(options.size(), channel, 100, 100);
                Assert.fail("Reading should have failed because index is out of bounds");
            } catch (IndexOutOfBoundsException e) {
                // expected. Nothing to do.
            }
            try {
                readSet.getKeyAt(options.size());
                Assert.fail("Getting key should have failed because index is out of bounds");
            } catch (IndexOutOfBoundsException e) {
                // expected. Nothing to do.
            }
        } finally {
            log.close();
        }
    }

    /**
     * Tests {@link BlobReadOptions} for getter correctness, serialization/deserialization and bad input.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void blobReadOptionsTest() throws IOException {
        int logCapacity = 2000;
        int[] segCapacities = new int[]{ 2000, 1000 };
        for (int segCapacity : segCapacities) {
            Log log = new Log(tempDir.getAbsolutePath(), logCapacity, segCapacity, StoreTestUtils.DEFAULT_DISK_SPACE_ALLOCATOR, metrics);
            try {
                LogSegment firstSegment = log.getFirstSegment();
                int availableSegCapacity = ((int) (segCapacity - (firstSegment.getStartOffset())));
                int count = logCapacity / segCapacity;
                for (int i = 0; i < count; i++) {
                    ByteBuffer buffer = ByteBuffer.allocate(availableSegCapacity);
                    log.appendFrom(Channels.newChannel(new ByteBufferInputStream(buffer)), availableSegCapacity);
                }
                long offset = (Utils.getRandomLong(RANDOM, availableSegCapacity)) + (firstSegment.getStartOffset());
                long size = Utils.getRandomLong(RANDOM, ((firstSegment.getEndOffset()) - offset));
                long expiresAtMs = Utils.getRandomLong(RANDOM, 1000);
                long operationTimeMs = (System.currentTimeMillis()) + (RANDOM.nextInt(10000));
                long crc = RANDOM.nextLong();
                MockId id = new MockId("id1");
                short accountId = Utils.getRandomShort(RANDOM);
                short containerId = Utils.getRandomShort(RANDOM);
                boolean deleted = RANDOM.nextBoolean();
                boolean ttlUpdated = RANDOM.nextBoolean();
                // basic test
                MessageInfo info = new MessageInfo(id, size, deleted, ttlUpdated, expiresAtMs, crc, accountId, containerId, operationTimeMs);
                BlobReadOptions options = new BlobReadOptions(log, new Offset(firstSegment.getName(), offset), info);
                Assert.assertEquals("Ref count of log segment should have increased", 1, firstSegment.refCount());
                verifyGetters(options, firstSegment, offset, true, info);
                options.close();
                Assert.assertEquals("Ref count of log segment should have decreased", 0, firstSegment.refCount());
                // toBytes() and back test
                doSerDeTest(options, firstSegment, log);
                if (count > 1) {
                    // toBytes() and back test for the second segment
                    LogSegment secondSegment = log.getNextSegment(firstSegment);
                    options = new BlobReadOptions(log, new Offset(secondSegment.getName(), offset), info);
                    Assert.assertEquals("Ref count of log segment should have increased", 1, secondSegment.refCount());
                    options.close();
                    Assert.assertEquals("Ref count of log segment should have decreased", 0, secondSegment.refCount());
                    doSerDeTest(options, secondSegment, log);
                }
                try {
                    new BlobReadOptions(log, new Offset(firstSegment.getName(), firstSegment.getEndOffset()), new MessageInfo(null, 1, 1, Utils.getRandomShort(RANDOM), Utils.getRandomShort(RANDOM), operationTimeMs));
                    Assert.fail("Construction should have failed because offset + size > endOffset");
                } catch (IllegalArgumentException e) {
                    // expected. Nothing to do.
                }
            } finally {
                log.close();
                Assert.assertTrue(((tempDir) + " could not be cleaned"), StoreTestUtils.cleanDirectory(tempDir, false));
            }
        }
    }
}

