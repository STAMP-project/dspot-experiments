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


import DFSConfigKeys.DFS_NAMENODE_NAME_DIR_KEY;
import DFSConfigKeys.DFS_NAMENODE_NUM_EXTRA_EDITS_RETAINED_KEY;
import com.google.common.base.Joiner;
import java.io.File;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Functional tests for NNStorageRetentionManager. This differs from
 * {@link TestNNStorageRetentionManager} in that the other test suite
 * is only unit/mock-based tests whereas this suite starts miniclusters,
 * etc.
 */
public class TestNNStorageRetentionFunctional {
    private static final File TEST_ROOT_DIR = new File(MiniDFSCluster.getBaseDirectory());

    private static final Logger LOG = LoggerFactory.getLogger(TestNNStorageRetentionFunctional.class);

    /**
     * Test case where two directories are configured as NAME_AND_EDITS
     * and one of them fails to save storage. Since the edits and image
     * failure states are decoupled, the failure of image saving should
     * not prevent the purging of logs from that dir.
     */
    @Test
    public void testPurgingWithNameEditsDirAfterFailure() throws Exception {
        MiniDFSCluster cluster = null;
        Configuration conf = new HdfsConfiguration();
        conf.setLong(DFS_NAMENODE_NUM_EXTRA_EDITS_RETAINED_KEY, 0);
        File sd0 = new File(TestNNStorageRetentionFunctional.TEST_ROOT_DIR, "nn0");
        File sd1 = new File(TestNNStorageRetentionFunctional.TEST_ROOT_DIR, "nn1");
        File cd0 = new File(sd0, "current");
        File cd1 = new File(sd1, "current");
        conf.set(DFS_NAMENODE_NAME_DIR_KEY, Joiner.on(",").join(sd0, sd1));
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).manageNameDfsDirs(false).format(true).build();
            NameNode nn = cluster.getNameNode();
            TestNNStorageRetentionFunctional.doSaveNamespace(nn);
            TestNNStorageRetentionFunctional.LOG.info("After first save, images 0 and 2 should exist in both dirs");
            GenericTestUtils.assertGlobEquals(cd0, "fsimage_\\d*", NNStorage.getImageFileName(0), NNStorage.getImageFileName(2));
            GenericTestUtils.assertGlobEquals(cd1, "fsimage_\\d*", NNStorage.getImageFileName(0), NNStorage.getImageFileName(2));
            GenericTestUtils.assertGlobEquals(cd0, "edits_.*", NNStorage.getFinalizedEditsFileName(1, 2), NNStorage.getInProgressEditsFileName(3));
            GenericTestUtils.assertGlobEquals(cd1, "edits_.*", NNStorage.getFinalizedEditsFileName(1, 2), NNStorage.getInProgressEditsFileName(3));
            TestNNStorageRetentionFunctional.doSaveNamespace(nn);
            TestNNStorageRetentionFunctional.LOG.info(("After second save, image 0 should be purged, " + "and image 4 should exist in both."));
            GenericTestUtils.assertGlobEquals(cd0, "fsimage_\\d*", NNStorage.getImageFileName(2), NNStorage.getImageFileName(4));
            GenericTestUtils.assertGlobEquals(cd1, "fsimage_\\d*", NNStorage.getImageFileName(2), NNStorage.getImageFileName(4));
            GenericTestUtils.assertGlobEquals(cd0, "edits_.*", NNStorage.getFinalizedEditsFileName(3, 4), NNStorage.getInProgressEditsFileName(5));
            GenericTestUtils.assertGlobEquals(cd1, "edits_.*", NNStorage.getFinalizedEditsFileName(3, 4), NNStorage.getInProgressEditsFileName(5));
            TestNNStorageRetentionFunctional.LOG.info("Failing first storage dir by chmodding it");
            Assert.assertEquals(0, FileUtil.chmod(cd0.getAbsolutePath(), "000"));
            TestNNStorageRetentionFunctional.doSaveNamespace(nn);
            TestNNStorageRetentionFunctional.LOG.info("Restoring accessibility of first storage dir");
            Assert.assertEquals(0, FileUtil.chmod(cd0.getAbsolutePath(), "755"));
            TestNNStorageRetentionFunctional.LOG.info("nothing should have been purged in first storage dir");
            GenericTestUtils.assertGlobEquals(cd0, "fsimage_\\d*", NNStorage.getImageFileName(2), NNStorage.getImageFileName(4));
            GenericTestUtils.assertGlobEquals(cd0, "edits_.*", NNStorage.getFinalizedEditsFileName(3, 4), NNStorage.getInProgressEditsFileName(5));
            TestNNStorageRetentionFunctional.LOG.info("fsimage_2 should be purged in second storage dir");
            GenericTestUtils.assertGlobEquals(cd1, "fsimage_\\d*", NNStorage.getImageFileName(4), NNStorage.getImageFileName(6));
            GenericTestUtils.assertGlobEquals(cd1, "edits_.*", NNStorage.getFinalizedEditsFileName(5, 6), NNStorage.getInProgressEditsFileName(7));
            TestNNStorageRetentionFunctional.LOG.info(("On next save, we should purge logs from the failed dir," + " but not images, since the image directory is in failed state."));
            TestNNStorageRetentionFunctional.doSaveNamespace(nn);
            GenericTestUtils.assertGlobEquals(cd1, "fsimage_\\d*", NNStorage.getImageFileName(6), NNStorage.getImageFileName(8));
            GenericTestUtils.assertGlobEquals(cd1, "edits_.*", NNStorage.getFinalizedEditsFileName(7, 8), NNStorage.getInProgressEditsFileName(9));
            GenericTestUtils.assertGlobEquals(cd0, "fsimage_\\d*", NNStorage.getImageFileName(2), NNStorage.getImageFileName(4));
            GenericTestUtils.assertGlobEquals(cd0, "edits_.*", NNStorage.getInProgressEditsFileName(9));
        } finally {
            FileUtil.chmod(cd0.getAbsolutePath(), "755");
            TestNNStorageRetentionFunctional.LOG.info("Shutting down...");
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }
}

