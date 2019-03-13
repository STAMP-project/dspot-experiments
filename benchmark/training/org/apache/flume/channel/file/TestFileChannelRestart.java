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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.channel.file;


import FileChannelConfiguration.CAPACITY;
import FileChannelConfiguration.COMPRESS_BACKUP_CHECKPOINT;
import FileChannelConfiguration.MAX_FILE_SIZE;
import FileChannelConfiguration.TRANSACTION_CAPACITY;
import FileChannelConfiguration.USE_DUAL_CHECKPOINTS;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.fest.reflect.exception.ReflectionError;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static EventQueueBackingStoreFile.INDEX_CHECKPOINT_MARKER;
import static EventQueueBackingStoreFile.INDEX_VERSION;
import static Log.PREFIX;
import static Serialization.SIZE_OF_LONG;


public class TestFileChannelRestart extends TestFileChannelBase {
    protected static final Logger LOG = LoggerFactory.getLogger(TestFileChannelRestart.class);

    @Test
    public void testRestartLogReplayV1() throws Exception {
        doTestRestart(true, false, false, false);
    }

    @Test
    public void testRestartLogReplayV2() throws Exception {
        doTestRestart(false, false, false, false);
    }

    @Test
    public void testFastReplayV1() throws Exception {
        doTestRestart(true, true, true, true);
    }

    @Test
    public void testFastReplayV2() throws Exception {
        doTestRestart(false, true, true, true);
    }

    @Test
    public void testFastReplayNegativeTestV1() throws Exception {
        doTestRestart(true, true, false, true);
    }

    @Test
    public void testFastReplayNegativeTestV2() throws Exception {
        doTestRestart(false, true, false, true);
    }

    @Test
    public void testNormalReplayV1() throws Exception {
        doTestRestart(true, true, true, false);
    }

    @Test
    public void testNormalReplayV2() throws Exception {
        doTestRestart(false, true, true, false);
    }

    @Test
    public void testRestartWhenMetaDataExistsButCheckpointDoesNot() throws Exception {
        doTestRestartWhenMetaDataExistsButCheckpointDoesNot(false);
    }

    @Test
    public void testRestartWhenMetaDataExistsButCheckpointDoesNotWithBackup() throws Exception {
        doTestRestartWhenMetaDataExistsButCheckpointDoesNot(true);
    }

    @Test
    public void testRestartWhenCheckpointExistsButMetaDoesNot() throws Exception {
        doTestRestartWhenCheckpointExistsButMetaDoesNot(false);
    }

    @Test
    public void testRestartWhenCheckpointExistsButMetaDoesNotWithBackup() throws Exception {
        doTestRestartWhenCheckpointExistsButMetaDoesNot(true);
    }

    @Test
    public void testRestartWhenNoCheckpointExists() throws Exception {
        doTestRestartWhenNoCheckpointExists(false);
    }

    @Test
    public void testRestartWhenNoCheckpointExistsWithBackup() throws Exception {
        doTestRestartWhenNoCheckpointExists(true);
    }

    @Test
    public void testBadCheckpointVersion() throws Exception {
        doTestBadCheckpointVersion(false);
    }

    @Test
    public void testBadCheckpointVersionWithBackup() throws Exception {
        doTestBadCheckpointVersion(true);
    }

    @Test
    public void testBadCheckpointMetaVersion() throws Exception {
        doTestBadCheckpointMetaVersion(false);
    }

    @Test
    public void testBadCheckpointMetaVersionWithBackup() throws Exception {
        doTestBadCheckpointMetaVersion(true);
    }

    @Test
    public void testDifferingOrderIDCheckpointAndMetaVersion() throws Exception {
        doTestDifferingOrderIDCheckpointAndMetaVersion(false);
    }

    @Test
    public void testDifferingOrderIDCheckpointAndMetaVersionWithBackup() throws Exception {
        doTestDifferingOrderIDCheckpointAndMetaVersion(true);
    }

    @Test
    public void testIncompleteCheckpoint() throws Exception {
        doTestIncompleteCheckpoint(false);
    }

    @Test
    public void testIncompleteCheckpointWithCheckpoint() throws Exception {
        doTestIncompleteCheckpoint(true);
    }

    @Test
    public void testCorruptInflightPuts() throws Exception {
        doTestCorruptInflights("inflightputs", false);
    }

    @Test
    public void testCorruptInflightPutsWithBackup() throws Exception {
        doTestCorruptInflights("inflightputs", true);
    }

    @Test
    public void testCorruptInflightTakes() throws Exception {
        doTestCorruptInflights("inflighttakes", false);
    }

    @Test
    public void testCorruptInflightTakesWithBackup() throws Exception {
        doTestCorruptInflights("inflighttakes", true);
    }

    @Test
    public void testFastReplayWithCheckpoint() throws Exception {
        testFastReplay(false, true);
    }

    @Test
    public void testFastReplayWithBadCheckpoint() throws Exception {
        testFastReplay(true, true);
    }

    @Test
    public void testNoFastReplayWithCheckpoint() throws Exception {
        testFastReplay(false, false);
    }

    @Test
    public void testNoFastReplayWithBadCheckpoint() throws Exception {
        testFastReplay(true, false);
    }

    @Test
    public void testTruncatedCheckpointMeta() throws Exception {
        doTestTruncatedCheckpointMeta(false);
    }

    @Test
    public void testTruncatedCheckpointMetaWithBackup() throws Exception {
        doTestTruncatedCheckpointMeta(true);
    }

    @Test
    public void testCorruptCheckpointMeta() throws Exception {
        doTestCorruptCheckpointMeta(false);
    }

    @Test
    public void testCorruptCheckpointMetaWithBackup() throws Exception {
        doTestCorruptCheckpointMeta(true);
    }

    // This test will fail without FLUME-1893
    @Test
    public void testCorruptCheckpointVersionMostSignificant4Bytes() throws Exception {
        Map<String, String> overrides = Maps.newHashMap();
        channel = createFileChannel(overrides);
        channel.start();
        Assert.assertTrue(channel.isOpen());
        Set<String> in = TestUtils.putEvents(channel, "restart", 10, 100);
        Assert.assertEquals(100, in.size());
        TestUtils.forceCheckpoint(channel);
        channel.stop();
        File checkpoint = new File(checkpointDir, "checkpoint");
        RandomAccessFile writer = new RandomAccessFile(checkpoint, "rw");
        writer.seek(((INDEX_VERSION) * (SIZE_OF_LONG)));
        writer.write(new byte[]{ ((byte) (1)), ((byte) (5)) });
        writer.getFD().sync();
        writer.close();
        channel = createFileChannel(overrides);
        channel.start();
        Assert.assertTrue(channel.isOpen());
        Set<String> out = TestUtils.consumeChannel(channel);
        Assert.assertTrue(channel.didFullReplayDueToBadCheckpointException());
        TestUtils.compareInputAndOut(in, out);
    }

    // This test will fail without FLUME-1893
    @Test
    public void testCorruptCheckpointCompleteMarkerMostSignificant4Bytes() throws Exception {
        Map<String, String> overrides = Maps.newHashMap();
        channel = createFileChannel(overrides);
        channel.start();
        Assert.assertTrue(channel.isOpen());
        Set<String> in = TestUtils.putEvents(channel, "restart", 10, 100);
        Assert.assertEquals(100, in.size());
        TestUtils.forceCheckpoint(channel);
        channel.stop();
        File checkpoint = new File(checkpointDir, "checkpoint");
        RandomAccessFile writer = new RandomAccessFile(checkpoint, "rw");
        writer.seek(((INDEX_CHECKPOINT_MARKER) * (SIZE_OF_LONG)));
        writer.write(new byte[]{ ((byte) (1)), ((byte) (5)) });
        writer.getFD().sync();
        writer.close();
        channel = createFileChannel(overrides);
        channel.start();
        Assert.assertTrue(channel.isOpen());
        Set<String> out = TestUtils.consumeChannel(channel);
        Assert.assertTrue(channel.didFullReplayDueToBadCheckpointException());
        TestUtils.compareInputAndOut(in, out);
    }

    @Test
    public void testWithExtraLogs() throws Exception {
        Map<String, String> overrides = Maps.newHashMap();
        overrides.put(CAPACITY, "10");
        overrides.put(TRANSACTION_CAPACITY, "10");
        channel = createFileChannel(overrides);
        channel.start();
        Assert.assertTrue(channel.isOpen());
        Set<String> in = TestUtils.fillChannel(channel, "extralogs");
        for (int i = 0; i < (dataDirs.length); i++) {
            File file = new File(dataDirs[i], ((PREFIX) + (1000 + i)));
            Assert.assertTrue(file.createNewFile());
            Assert.assertTrue(((file.length()) == 0));
            File metaDataFile = Serialization.getMetaDataFile(file);
            File metaDataTempFile = Serialization.getMetaDataTempFile(metaDataFile);
            Assert.assertTrue(metaDataTempFile.createNewFile());
        }
        channel.stop();
        channel = createFileChannel(overrides);
        channel.start();
        Assert.assertTrue(channel.isOpen());
        Set<String> out = TestUtils.consumeChannel(channel);
        TestUtils.compareInputAndOut(in, out);
    }

    // Make sure the entire channel was not replayed, only the events from the
    // backup.
    @Test
    public void testBackupUsedEnsureNoFullReplayWithoutCompression() throws Exception {
        testBackupUsedEnsureNoFullReplay(false);
    }

    @Test
    public void testBackupUsedEnsureNoFullReplayWithCompression() throws Exception {
        testBackupUsedEnsureNoFullReplay(true);
    }

    // Make sure data files required by the backup checkpoint are not deleted.
    @Test
    public void testDataFilesRequiredByBackupNotDeleted() throws Exception {
        Map<String, String> overrides = Maps.newHashMap();
        overrides.put(USE_DUAL_CHECKPOINTS, "true");
        overrides.put(MAX_FILE_SIZE, "1000");
        channel = createFileChannel(overrides);
        channel.start();
        String prefix = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";
        Assert.assertTrue(channel.isOpen());
        TestUtils.putEvents(channel, prefix, 10, 100);
        Set<String> origFiles = Sets.newHashSet();
        for (File dir : dataDirs) {
            origFiles.addAll(Lists.newArrayList(dir.list()));
        }
        TestUtils.forceCheckpoint(channel);
        TestUtils.takeEvents(channel, 10, 50);
        long beforeSecondCheckpoint = System.currentTimeMillis();
        TestUtils.forceCheckpoint(channel);
        Set<String> newFiles = Sets.newHashSet();
        int olderThanCheckpoint = 0;
        int totalMetaFiles = 0;
        for (File dir : dataDirs) {
            File[] metadataFiles = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.endsWith(".meta")) {
                        return true;
                    }
                    return false;
                }
            });
            totalMetaFiles = metadataFiles.length;
            for (File metadataFile : metadataFiles) {
                if ((metadataFile.lastModified()) < beforeSecondCheckpoint) {
                    olderThanCheckpoint++;
                }
            }
            newFiles.addAll(Lists.newArrayList(dir.list()));
        }
        /* Files which are not required by the new checkpoint should not have been
        modified by the checkpoint.
         */
        Assert.assertTrue((olderThanCheckpoint > 0));
        Assert.assertTrue((totalMetaFiles != olderThanCheckpoint));
        /* All files needed by original checkpoint should still be there. */
        Assert.assertTrue(newFiles.containsAll(origFiles));
        TestUtils.takeEvents(channel, 10, 50);
        TestUtils.forceCheckpoint(channel);
        newFiles = Sets.newHashSet();
        for (File dir : dataDirs) {
            newFiles.addAll(Lists.newArrayList(dir.list()));
        }
        Assert.assertTrue((!(newFiles.containsAll(origFiles))));
    }

    @Test(expected = IOException.class)
    public void testSlowBackup() throws Throwable {
        Map<String, String> overrides = Maps.newHashMap();
        overrides.put(USE_DUAL_CHECKPOINTS, "true");
        overrides.put(MAX_FILE_SIZE, "1000");
        channel = createFileChannel(overrides);
        channel.start();
        Assert.assertTrue(channel.isOpen());
        Set<String> in = TestUtils.putEvents(channel, "restart", 10, 100);
        Assert.assertEquals(100, in.size());
        TestFileChannelRestart.slowdownBackup(channel);
        TestUtils.forceCheckpoint(channel);
        in = TestUtils.putEvents(channel, "restart", 10, 100);
        TestUtils.takeEvents(channel, 10, 100);
        Assert.assertEquals(100, in.size());
        try {
            TestUtils.forceCheckpoint(channel);
        } catch (ReflectionError ex) {
            throw ex.getCause();
        } finally {
            channel.stop();
        }
    }

    @Test
    public void testCompressBackup() throws Throwable {
        Map<String, String> overrides = Maps.newHashMap();
        overrides.put(USE_DUAL_CHECKPOINTS, "true");
        overrides.put(MAX_FILE_SIZE, "1000");
        overrides.put(COMPRESS_BACKUP_CHECKPOINT, "true");
        channel = createFileChannel(overrides);
        channel.start();
        Assert.assertTrue(channel.isOpen());
        TestUtils.putEvents(channel, "restart", 10, 100);
        TestUtils.forceCheckpoint(channel);
        // Wait for the backup checkpoint
        Thread.sleep(2000);
        Assert.assertTrue(compressedBackupCheckpoint.exists());
        Serialization.decompressFile(compressedBackupCheckpoint, uncompressedBackupCheckpoint);
        File checkpoint = new File(checkpointDir, "checkpoint");
        Assert.assertTrue(FileUtils.contentEquals(checkpoint, uncompressedBackupCheckpoint));
        channel.stop();
    }

    @Test
    public void testToggleCheckpointCompressionFromTrueToFalse() throws Exception {
        restartToggleCompression(true);
    }

    @Test
    public void testToggleCheckpointCompressionFromFalseToTrue() throws Exception {
        restartToggleCompression(false);
    }
}

