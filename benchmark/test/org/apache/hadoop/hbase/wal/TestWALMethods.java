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
package org.apache.hadoop.hbase.wal;


import WAL.Entry;
import java.io.IOException;
import java.util.NavigableSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hadoop.hbase.wal.WALSplitter.EntryBuffers;
import org.apache.hadoop.hbase.wal.WALSplitter.PipelineController;
import org.apache.hadoop.hbase.wal.WALSplitter.RegionEntryBuffer;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Simple testing of a few WAL methods.
 */
@Category({ RegionServerTests.class, SmallTests.class })
public class TestWALMethods {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestWALMethods.class);

    private static final byte[] TEST_REGION = Bytes.toBytes("test_region");

    private static final TableName TEST_TABLE = TableName.valueOf("test_table");

    private final HBaseTestingUtility util = new HBaseTestingUtility();

    @Test
    public void testServerNameFromWAL() throws Exception {
        Path walPath = new Path("/hbase/WALs/regionserver-2.example.com,22101,1487767381290", "regionserver-2.example.com%2C22101%2C1487767381290.null0.1487785392316");
        ServerName name = AbstractFSWALProvider.getServerNameFromWALDirectoryName(walPath);
        Assert.assertEquals(ServerName.valueOf("regionserver-2.example.com", 22101, 1487767381290L), name);
    }

    @Test
    public void testServerNameFromTestWAL() throws Exception {
        Path walPath = new Path("/user/example/test-data/12ff1404-68c6-4715-a4b9-775e763842bc/WALs/TestWALRecordReader", "TestWALRecordReader.default.1487787939118");
        ServerName name = AbstractFSWALProvider.getServerNameFromWALDirectoryName(walPath);
        Assert.assertNull(name);
    }

    /**
     * Assert that getSplitEditFilesSorted returns files in expected order and
     * that it skips moved-aside files.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testGetSplitEditFilesSorted() throws IOException {
        FileSystem fs = FileSystem.get(util.getConfiguration());
        Path regiondir = getDataTestDir("regiondir");
        fs.delete(regiondir, true);
        fs.mkdirs(regiondir);
        Path recoverededits = WALSplitter.getRegionDirRecoveredEditsDir(regiondir);
        String first = WALSplitter.formatRecoveredEditsFileName((-1));
        createFile(fs, recoverededits, first);
        createFile(fs, recoverededits, WALSplitter.formatRecoveredEditsFileName(0));
        createFile(fs, recoverededits, WALSplitter.formatRecoveredEditsFileName(1));
        createFile(fs, recoverededits, WALSplitter.formatRecoveredEditsFileName(11));
        createFile(fs, recoverededits, WALSplitter.formatRecoveredEditsFileName(2));
        createFile(fs, recoverededits, WALSplitter.formatRecoveredEditsFileName(50));
        String last = WALSplitter.formatRecoveredEditsFileName(Long.MAX_VALUE);
        createFile(fs, recoverededits, last);
        createFile(fs, recoverededits, (((Long.toString(Long.MAX_VALUE)) + ".") + (System.currentTimeMillis())));
        final Configuration walConf = new Configuration(util.getConfiguration());
        FSUtils.setRootDir(walConf, regiondir);
        getWAL(null);
        NavigableSet<Path> files = WALSplitter.getSplitEditFilesSorted(fs, regiondir);
        Assert.assertEquals(7, files.size());
        Assert.assertEquals(files.pollFirst().getName(), first);
        Assert.assertEquals(files.pollLast().getName(), last);
        Assert.assertEquals(files.pollFirst().getName(), WALSplitter.formatRecoveredEditsFileName(0));
        Assert.assertEquals(files.pollFirst().getName(), WALSplitter.formatRecoveredEditsFileName(1));
        Assert.assertEquals(files.pollFirst().getName(), WALSplitter.formatRecoveredEditsFileName(2));
        Assert.assertEquals(files.pollFirst().getName(), WALSplitter.formatRecoveredEditsFileName(11));
    }

    @Test
    public void testRegionEntryBuffer() throws Exception {
        WALSplitter.RegionEntryBuffer reb = new WALSplitter.RegionEntryBuffer(TestWALMethods.TEST_TABLE, TestWALMethods.TEST_REGION);
        Assert.assertEquals(0, reb.heapSize());
        reb.appendEntry(createTestLogEntry(1));
        Assert.assertTrue(((reb.heapSize()) > 0));
    }

    @Test
    public void testEntrySink() throws Exception {
        EntryBuffers sink = new EntryBuffers(new PipelineController(), ((1 * 1024) * 1024));
        for (int i = 0; i < 1000; i++) {
            WAL.Entry entry = createTestLogEntry(i);
            sink.appendEntry(entry);
        }
        Assert.assertTrue(((sink.totalBuffered) > 0));
        long amountInChunk = sink.totalBuffered;
        // Get a chunk
        RegionEntryBuffer chunk = sink.getChunkToWrite();
        Assert.assertEquals(chunk.heapSize(), amountInChunk);
        // Make sure it got marked that a thread is "working on this"
        Assert.assertTrue(sink.isRegionCurrentlyWriting(TestWALMethods.TEST_REGION));
        // Insert some more entries
        for (int i = 0; i < 500; i++) {
            WAL.Entry entry = createTestLogEntry(i);
            sink.appendEntry(entry);
        }
        // Asking for another chunk shouldn't work since the first one
        // is still writing
        Assert.assertNull(sink.getChunkToWrite());
        // If we say we're done writing the first chunk, then we should be able
        // to get the second
        sink.doneWriting(chunk);
        RegionEntryBuffer chunk2 = sink.getChunkToWrite();
        Assert.assertNotNull(chunk2);
        Assert.assertNotSame(chunk, chunk2);
        long amountInChunk2 = sink.totalBuffered;
        // The second chunk had fewer rows than the first
        Assert.assertTrue((amountInChunk2 < amountInChunk));
        sink.doneWriting(chunk2);
        Assert.assertEquals(0, sink.totalBuffered);
    }
}

