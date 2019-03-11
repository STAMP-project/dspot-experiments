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
package org.apache.hadoop.hdfs.server.namenode;


import DFSConfigKeys.DFS_NAMENODE_MAX_EXTRA_EDITS_SEGMENTS_RETAINED_KEY;
import DFSConfigKeys.DFS_NAMENODE_NUM_EXTRA_EDITS_RETAINED_KEY;
import NameNodeDirType.EDITS;
import NameNodeDirType.IMAGE;
import NameNodeDirType.IMAGE_AND_EDITS;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.server.common.Storage.StorageDirectory;
import org.apache.hadoop.hdfs.server.namenode.NNStorage.NameNodeDirType;
import org.apache.hadoop.hdfs.server.namenode.NNStorageRetentionManager.StoragePurger;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TestNNStorageRetentionManager {
    final Configuration conf = new Configuration();

    /**
     * Test the "easy case" where we have more images in the
     * directory than we need to keep. Should purge the
     * old ones.
     */
    @Test
    public void testPurgeEasyCase() throws IOException {
        TestNNStorageRetentionManager.TestCaseDescription tc = new TestNNStorageRetentionManager.TestCaseDescription();
        tc.addRoot("/foo1", IMAGE_AND_EDITS);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(100))), true);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(200))), true);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(300))), false);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(400))), false);
        tc.addLog(("/foo1/current/" + (NNStorage.getFinalizedEditsFileName(101, 200))), true);
        tc.addLog(("/foo1/current/" + (NNStorage.getFinalizedEditsFileName(201, 300))), true);
        tc.addLog(("/foo1/current/" + (NNStorage.getFinalizedEditsFileName(301, 400))), false);
        tc.addLog(("/foo1/current/" + (NNStorage.getInProgressEditsFileName(401))), false);
        // Test that other files don't get purged
        tc.addLog("/foo1/current/VERSION", false);
        runTest(tc);
    }

    /**
     * Same as above, but across multiple directories
     */
    @Test
    public void testPurgeMultipleDirs() throws IOException {
        TestNNStorageRetentionManager.TestCaseDescription tc = new TestNNStorageRetentionManager.TestCaseDescription();
        tc.addRoot("/foo1", IMAGE_AND_EDITS);
        tc.addRoot("/foo2", IMAGE_AND_EDITS);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(100))), true);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(200))), true);
        tc.addImage(("/foo2/current/" + (NNStorage.getImageFileName(200))), true);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(300))), false);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(400))), false);
        tc.addLog(("/foo1/current/" + (NNStorage.getFinalizedEditsFileName(101, 200))), true);
        tc.addLog(("/foo1/current/" + (NNStorage.getFinalizedEditsFileName(201, 300))), true);
        tc.addLog(("/foo2/current/" + (NNStorage.getFinalizedEditsFileName(201, 300))), true);
        tc.addLog(("/foo1/current/" + (NNStorage.getFinalizedEditsFileName(301, 400))), false);
        tc.addLog(("/foo2/current/" + (NNStorage.getFinalizedEditsFileName(301, 400))), false);
        tc.addLog(("/foo1/current/" + (NNStorage.getInProgressEditsFileName(401))), false);
        runTest(tc);
    }

    /**
     * Test that if we have fewer fsimages than the configured
     * retention, we don't purge any of them
     */
    @Test
    public void testPurgeLessThanRetention() throws IOException {
        TestNNStorageRetentionManager.TestCaseDescription tc = new TestNNStorageRetentionManager.TestCaseDescription();
        tc.addRoot("/foo1", IMAGE_AND_EDITS);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(100))), false);
        tc.addLog(("/foo1/current/" + (NNStorage.getFinalizedEditsFileName(101, 200))), false);
        tc.addLog(("/foo1/current/" + (NNStorage.getFinalizedEditsFileName(201, 300))), false);
        tc.addLog(("/foo1/current/" + (NNStorage.getFinalizedEditsFileName(301, 400))), false);
        tc.addLog(("/foo1/current/" + (NNStorage.getInProgressEditsFileName(401))), false);
        runTest(tc);
    }

    /**
     * Check for edge case with no logs present at all.
     */
    @Test
    public void testNoLogs() throws IOException {
        TestNNStorageRetentionManager.TestCaseDescription tc = new TestNNStorageRetentionManager.TestCaseDescription();
        tc.addRoot("/foo1", IMAGE_AND_EDITS);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(100))), true);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(200))), true);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(300))), false);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(400))), false);
        runTest(tc);
    }

    /**
     * Check for edge case with no logs or images present at all.
     */
    @Test
    public void testEmptyDir() throws IOException {
        TestNNStorageRetentionManager.TestCaseDescription tc = new TestNNStorageRetentionManager.TestCaseDescription();
        tc.addRoot("/foo1", IMAGE_AND_EDITS);
        runTest(tc);
    }

    /**
     * Test that old in-progress logs are properly purged
     */
    @Test
    public void testOldInProgress() throws IOException {
        TestNNStorageRetentionManager.TestCaseDescription tc = new TestNNStorageRetentionManager.TestCaseDescription();
        tc.addRoot("/foo1", IMAGE_AND_EDITS);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(100))), true);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(200))), true);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(300))), false);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(400))), false);
        tc.addLog(("/foo1/current/" + (NNStorage.getInProgressEditsFileName(101))), true);
        runTest(tc);
    }

    @Test
    public void testSeparateEditDirs() throws IOException {
        TestNNStorageRetentionManager.TestCaseDescription tc = new TestNNStorageRetentionManager.TestCaseDescription();
        tc.addRoot("/foo1", IMAGE);
        tc.addRoot("/foo2", EDITS);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(100))), true);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(200))), true);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(300))), false);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(400))), false);
        tc.addLog(("/foo2/current/" + (NNStorage.getFinalizedEditsFileName(101, 200))), true);
        tc.addLog(("/foo2/current/" + (NNStorage.getFinalizedEditsFileName(201, 300))), true);
        tc.addLog(("/foo2/current/" + (NNStorage.getFinalizedEditsFileName(301, 400))), false);
        tc.addLog(("/foo2/current/" + (NNStorage.getInProgressEditsFileName(401))), false);
        runTest(tc);
    }

    @Test
    public void testRetainExtraLogs() throws IOException {
        conf.setLong(DFS_NAMENODE_NUM_EXTRA_EDITS_RETAINED_KEY, 50);
        TestNNStorageRetentionManager.TestCaseDescription tc = new TestNNStorageRetentionManager.TestCaseDescription();
        tc.addRoot("/foo1", IMAGE);
        tc.addRoot("/foo2", EDITS);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(100))), true);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(200))), true);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(300))), false);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(400))), false);
        tc.addLog(("/foo2/current/" + (NNStorage.getFinalizedEditsFileName(101, 200))), true);
        // Since we need 50 extra edits, *do* retain the 201-300 segment
        tc.addLog(("/foo2/current/" + (NNStorage.getFinalizedEditsFileName(201, 300))), false);
        tc.addLog(("/foo2/current/" + (NNStorage.getFinalizedEditsFileName(301, 400))), false);
        tc.addLog(("/foo2/current/" + (NNStorage.getInProgressEditsFileName(401))), false);
        runTest(tc);
    }

    @Test
    public void testRetainExtraLogsLimitedSegments() throws IOException {
        conf.setLong(DFS_NAMENODE_NUM_EXTRA_EDITS_RETAINED_KEY, 150);
        conf.setLong(DFS_NAMENODE_MAX_EXTRA_EDITS_SEGMENTS_RETAINED_KEY, 2);
        TestNNStorageRetentionManager.TestCaseDescription tc = new TestNNStorageRetentionManager.TestCaseDescription();
        tc.addRoot("/foo1", IMAGE);
        tc.addRoot("/foo2", EDITS);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(100))), true);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(200))), true);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(300))), false);
        tc.addImage(("/foo1/current/" + (NNStorage.getImageFileName(400))), false);
        // Segments containing txns upto txId 250 are extra and should be purged.
        tc.addLog(("/foo2/current/" + (NNStorage.getFinalizedEditsFileName(1, 100))), true);
        tc.addLog(("/foo2/current/" + (NNStorage.getFinalizedEditsFileName(101, 175))), true);
        tc.addLog((("/foo2/current/" + (NNStorage.getInProgressEditsFileName(176))) + ".empty"), true);
        tc.addLog(("/foo2/current/" + (NNStorage.getFinalizedEditsFileName(176, 200))), true);
        tc.addLog(("/foo2/current/" + (NNStorage.getFinalizedEditsFileName(201, 225))), true);
        tc.addLog((("/foo2/current/" + (NNStorage.getInProgressEditsFileName(226))) + ".corrupt"), true);
        tc.addLog(("/foo2/current/" + (NNStorage.getFinalizedEditsFileName(226, 240))), true);
        // Only retain 2 extra segments. The 301-350 and 351-400 segments are
        // considered required, not extra.
        tc.addLog(("/foo2/current/" + (NNStorage.getFinalizedEditsFileName(241, 275))), false);
        tc.addLog(("/foo2/current/" + (NNStorage.getFinalizedEditsFileName(276, 300))), false);
        tc.addLog((("/foo2/current/" + (NNStorage.getInProgressEditsFileName(301))) + ".empty"), false);
        tc.addLog(("/foo2/current/" + (NNStorage.getFinalizedEditsFileName(301, 350))), false);
        tc.addLog((("/foo2/current/" + (NNStorage.getInProgressEditsFileName(351))) + ".corrupt"), false);
        tc.addLog(("/foo2/current/" + (NNStorage.getFinalizedEditsFileName(351, 400))), false);
        tc.addLog(("/foo2/current/" + (NNStorage.getInProgressEditsFileName(401))), false);
        runTest(tc);
    }

    private class TestCaseDescription {
        private final Map<File, TestNNStorageRetentionManager.TestCaseDescription.FakeRoot> dirRoots = Maps.newLinkedHashMap();

        private final Set<File> expectedPurgedLogs = Sets.newLinkedHashSet();

        private final Set<File> expectedPurgedImages = Sets.newLinkedHashSet();

        private class FakeRoot {
            final NameNodeDirType type;

            final List<File> files;

            FakeRoot(NameNodeDirType type) {
                this.type = type;
                files = Lists.newArrayList();
            }

            StorageDirectory mockStorageDir() {
                return FSImageTestUtil.mockStorageDirectory(type, false, TestNNStorageRetentionManager.filesToPaths(files).toArray(new String[0]));
            }
        }

        void addRoot(String root, NameNodeDirType dir) {
            dirRoots.put(new File(root), new TestNNStorageRetentionManager.TestCaseDescription.FakeRoot(dir));
        }

        private void addFile(File file) {
            for (Map.Entry<File, TestNNStorageRetentionManager.TestCaseDescription.FakeRoot> entry : dirRoots.entrySet()) {
                if (TestNNStorageRetentionManager.fileToPath(file).startsWith(TestNNStorageRetentionManager.fileToPath(entry.getKey()))) {
                    entry.getValue().files.add(file);
                }
            }
        }

        void addLog(String path, boolean expectPurge) {
            File file = new File(path);
            addFile(file);
            if (expectPurge) {
                expectedPurgedLogs.add(file);
            }
        }

        void addImage(String path, boolean expectPurge) {
            File file = new File(path);
            addFile(file);
            if (expectPurge) {
                expectedPurgedImages.add(file);
            }
        }

        NNStorage mockStorage() throws IOException {
            List<StorageDirectory> sds = Lists.newArrayList();
            for (TestNNStorageRetentionManager.TestCaseDescription.FakeRoot root : dirRoots.values()) {
                sds.add(root.mockStorageDir());
            }
            return TestNNStorageRetentionManager.mockStorageForDirs(sds.toArray(new StorageDirectory[0]));
        }

        @SuppressWarnings("unchecked")
        public FSEditLog mockEditLog(StoragePurger purger) throws IOException {
            final List<JournalManager> jms = Lists.newArrayList();
            final JournalSet journalSet = new JournalSet(0);
            for (TestNNStorageRetentionManager.TestCaseDescription.FakeRoot root : dirRoots.values()) {
                if (!(root.type.isOfType(EDITS)))
                    continue;

                // passing null NNStorage for unit test because it does not use it
                FileJournalManager fjm = new FileJournalManager(conf, root.mockStorageDir(), null);
                fjm.purger = purger;
                jms.add(fjm);
                journalSet.add(fjm, false);
            }
            FSEditLog mockLog = Mockito.mock(FSEditLog.class);
            Mockito.doAnswer(new Answer<Void>() {
                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();
                    assert (args.length) == 1;
                    long txId = ((Long) (args[0]));
                    for (JournalManager jm : jms) {
                        jm.purgeLogsOlderThan(txId);
                    }
                    return null;
                }
            }).when(mockLog).purgeLogsOlderThan(Mockito.anyLong());
            Mockito.doAnswer(new Answer<Void>() {
                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    Object[] args = invocation.getArguments();
                    journalSet.selectInputStreams(((Collection<EditLogInputStream>) (args[0])), ((Long) (args[1])), ((Boolean) (args[2])), ((Boolean) (args[3])));
                    return null;
                }
            }).when(mockLog).selectInputStreams(Mockito.anyCollection(), Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyBoolean());
            return mockLog;
        }
    }
}

