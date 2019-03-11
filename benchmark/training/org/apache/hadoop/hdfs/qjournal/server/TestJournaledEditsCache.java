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
package org.apache.hadoop.hdfs.qjournal.server;


import Journal.LOG;
import JournaledEditsCache.CacheMissException;
import com.google.common.primitives.Bytes;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hdfs.qjournal.QJMTestUtil;
import org.apache.hadoop.hdfs.server.namenode.NameNodeLayoutVersion;
import org.apache.hadoop.test.GenericTestUtils.LogCapturer;
import org.apache.hadoop.test.PathUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the {@link JournaledEditsCache} used for caching edits in-memory on the
 * {@link Journal}.
 */
public class TestJournaledEditsCache {
    private static final int EDITS_CAPACITY = 100;

    private static final File TEST_DIR = PathUtils.getTestDir(TestJournaledEditsCache.class, false);

    private JournaledEditsCache cache;

    @Test
    public void testCacheSingleSegment() throws Exception {
        storeEdits(1, 20);
        // Leading part of the segment
        assertTxnCountAndContents(1, 5, 5);
        // All of the segment
        assertTxnCountAndContents(1, 20, 20);
        // Past the segment
        assertTxnCountAndContents(1, 40, 20);
        // Trailing part of the segment
        assertTxnCountAndContents(10, 11, 20);
        // Trailing part of the segment, past the end
        assertTxnCountAndContents(10, 20, 20);
    }

    @Test
    public void testCacheBelowCapacityRequestOnBoundary() throws Exception {
        storeEdits(1, 5);
        storeEdits(6, 20);
        storeEdits(21, 30);
        // First segment only
        assertTxnCountAndContents(1, 3, 3);
        // Second segment only
        assertTxnCountAndContents(6, 10, 15);
        // First and second segment
        assertTxnCountAndContents(1, 7, 7);
        // All three segments
        assertTxnCountAndContents(1, 25, 25);
        // Second and third segment
        assertTxnCountAndContents(6, 20, 25);
        // Second and third segment; request past the end
        assertTxnCountAndContents(6, 50, 30);
        // Third segment only; request past the end
        assertTxnCountAndContents(21, 20, 30);
    }

    @Test
    public void testCacheBelowCapacityRequestOffBoundary() throws Exception {
        storeEdits(1, 5);
        storeEdits(6, 20);
        storeEdits(21, 30);
        // First segment only
        assertTxnCountAndContents(3, 1, 3);
        // First and second segment
        assertTxnCountAndContents(3, 6, 8);
        // Second and third segment
        assertTxnCountAndContents(15, 10, 24);
        // Second and third segment; request past the end
        assertTxnCountAndContents(15, 50, 30);
        // Start read past the end
        List<ByteBuffer> buffers = new ArrayList<>();
        Assert.assertEquals(0, cache.retrieveEdits(31, 10, buffers));
        Assert.assertTrue(buffers.isEmpty());
    }

    @Test
    public void testCacheAboveCapacity() throws Exception {
        int thirdCapacity = (TestJournaledEditsCache.EDITS_CAPACITY) / 3;
        storeEdits(1, thirdCapacity);
        storeEdits((thirdCapacity + 1), (thirdCapacity * 2));
        storeEdits(((thirdCapacity * 2) + 1), TestJournaledEditsCache.EDITS_CAPACITY);
        storeEdits(((TestJournaledEditsCache.EDITS_CAPACITY) + 1), (thirdCapacity * 4));
        storeEdits(((thirdCapacity * 4) + 1), (thirdCapacity * 5));
        try {
            cache.retrieveEdits(1, 10, new ArrayList());
            Assert.fail();
        } catch (IOException ioe) {
            // expected
        }
        assertTxnCountAndContents(((TestJournaledEditsCache.EDITS_CAPACITY) + 1), TestJournaledEditsCache.EDITS_CAPACITY, (thirdCapacity * 5));
    }

    @Test
    public void testCacheSingleAdditionAboveCapacity() throws Exception {
        LogCapturer logs = LogCapturer.captureLogs(LOG);
        storeEdits(1, ((TestJournaledEditsCache.EDITS_CAPACITY) * 2));
        logs.stopCapturing();
        Assert.assertTrue(logs.getOutput().contains("batch of edits was too large"));
        try {
            cache.retrieveEdits(1, 1, new ArrayList());
            Assert.fail();
        } catch (IOException ioe) {
            // expected
        }
        storeEdits((((TestJournaledEditsCache.EDITS_CAPACITY) * 2) + 1), (((TestJournaledEditsCache.EDITS_CAPACITY) * 2) + 5));
        assertTxnCountAndContents((((TestJournaledEditsCache.EDITS_CAPACITY) * 2) + 1), 5, (((TestJournaledEditsCache.EDITS_CAPACITY) * 2) + 5));
    }

    @Test
    public void testCacheWithFutureLayoutVersion() throws Exception {
        byte[] firstHalf = QJMTestUtil.createGabageTxns(1, 5);
        byte[] secondHalf = QJMTestUtil.createGabageTxns(6, 5);
        int futureVersion = (NameNodeLayoutVersion.CURRENT_LAYOUT_VERSION) - 1;
        cache.storeEdits(Bytes.concat(firstHalf, secondHalf), 1, 10, futureVersion);
        List<ByteBuffer> buffers = new ArrayList<>();
        Assert.assertEquals(5, cache.retrieveEdits(6, 5, buffers));
        Assert.assertArrayEquals(TestJournaledEditsCache.getHeaderForLayoutVersion(futureVersion), buffers.get(0).array());
        byte[] retBytes = new byte[buffers.get(1).remaining()];
        System.arraycopy(buffers.get(1).array(), buffers.get(1).position(), retBytes, 0, buffers.get(1).remaining());
        Assert.assertArrayEquals(secondHalf, retBytes);
    }

    @Test
    public void testCacheWithMultipleLayoutVersions() throws Exception {
        int oldLayout = (NameNodeLayoutVersion.CURRENT_LAYOUT_VERSION) + 1;
        cache.storeEdits(QJMTestUtil.createTxnData(1, 5), 1, 5, oldLayout);
        storeEdits(6, 10);
        // Ensure the cache will only return edits from a single
        // layout version at a time
        try {
            cache.retrieveEdits(1, 50, new ArrayList());
            Assert.fail("Expected a cache miss");
        } catch (JournaledEditsCache cme) {
            // expected
        }
        assertTxnCountAndContents(6, 50, 10);
    }

    @Test
    public void testCacheEditsWithGaps() throws Exception {
        storeEdits(1, 5);
        storeEdits(10, 15);
        try {
            cache.retrieveEdits(1, 20, new ArrayList());
            Assert.fail();
        } catch (JournaledEditsCache cme) {
            Assert.assertEquals(9, cme.getCacheMissAmount());
        }
        assertTxnCountAndContents(10, 10, 15);
    }

    @Test(expected = CacheMissException.class)
    public void testReadUninitializedCache() throws Exception {
        cache.retrieveEdits(1, 10, new ArrayList());
    }

    @Test(expected = CacheMissException.class)
    public void testCacheMalformedInput() throws Exception {
        storeEdits(1, 1);
        cache.retrieveEdits((-1), 10, new ArrayList());
    }
}

