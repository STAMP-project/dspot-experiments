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
package org.apache.hadoop.hbase.replication;


import HConstants.ZOOKEEPER_ZNODE_PARENT;
import VerifyReplication.Verifier.Counters.BADROWS;
import VerifyReplication.Verifier.Counters.GOODROWS;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.mapreduce.replication.VerifyReplication;
import org.apache.hadoop.hbase.snapshot.SnapshotTestingUtils;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.CommonFSUtils;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hadoop.mapreduce.Job;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ ReplicationTests.class, LargeTests.class })
public class TestVerifyReplicationCrossDiffHdfs {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestVerifyReplicationCrossDiffHdfs.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestVerifyReplicationCrossDiffHdfs.class);

    private static HBaseTestingUtility util1;

    private static HBaseTestingUtility util2;

    private static HBaseTestingUtility mapReduceUtil = new HBaseTestingUtility();

    private static Configuration conf1 = HBaseConfiguration.create();

    private static Configuration conf2;

    private static final byte[] FAMILY = Bytes.toBytes("f");

    private static final byte[] QUALIFIER = Bytes.toBytes("q");

    private static final String PEER_ID = "1";

    private static final TableName TABLE_NAME = TableName.valueOf("testVerifyRepCrossDiffHDFS");

    @Test
    public void testVerifyRepBySnapshot() throws Exception {
        Path rootDir = FSUtils.getRootDir(TestVerifyReplicationCrossDiffHdfs.conf1);
        FileSystem fs = rootDir.getFileSystem(TestVerifyReplicationCrossDiffHdfs.conf1);
        String sourceSnapshotName = "sourceSnapshot-" + (System.currentTimeMillis());
        SnapshotTestingUtils.createSnapshotAndValidate(TestVerifyReplicationCrossDiffHdfs.util1.getAdmin(), TestVerifyReplicationCrossDiffHdfs.TABLE_NAME, Bytes.toString(TestVerifyReplicationCrossDiffHdfs.FAMILY), sourceSnapshotName, rootDir, fs, true);
        // Take target snapshot
        Path peerRootDir = FSUtils.getRootDir(TestVerifyReplicationCrossDiffHdfs.conf2);
        FileSystem peerFs = peerRootDir.getFileSystem(TestVerifyReplicationCrossDiffHdfs.conf2);
        String peerSnapshotName = "peerSnapshot-" + (System.currentTimeMillis());
        SnapshotTestingUtils.createSnapshotAndValidate(TestVerifyReplicationCrossDiffHdfs.util2.getAdmin(), TestVerifyReplicationCrossDiffHdfs.TABLE_NAME, Bytes.toString(TestVerifyReplicationCrossDiffHdfs.FAMILY), peerSnapshotName, peerRootDir, peerFs, true);
        String peerFSAddress = peerFs.getUri().toString();
        String temPath1 = new Path(fs.getUri().toString(), "/tmp1").toString();
        String temPath2 = "/tmp2";
        String[] args = new String[]{ "--sourceSnapshotName=" + sourceSnapshotName, "--sourceSnapshotTmpDir=" + temPath1, "--peerSnapshotName=" + peerSnapshotName, "--peerSnapshotTmpDir=" + temPath2, "--peerFSAddress=" + peerFSAddress, "--peerHBaseRootAddress=" + (FSUtils.getRootDir(TestVerifyReplicationCrossDiffHdfs.conf2)), TestVerifyReplicationCrossDiffHdfs.PEER_ID, TestVerifyReplicationCrossDiffHdfs.TABLE_NAME.toString() };
        // Use the yarn's config override the source cluster's config.
        Configuration newConf = HBaseConfiguration.create(TestVerifyReplicationCrossDiffHdfs.conf1);
        HBaseConfiguration.merge(newConf, TestVerifyReplicationCrossDiffHdfs.mapReduceUtil.getConfiguration());
        newConf.set(ZOOKEEPER_ZNODE_PARENT, "/1");
        CommonFSUtils.setRootDir(newConf, CommonFSUtils.getRootDir(TestVerifyReplicationCrossDiffHdfs.conf1));
        Job job = new VerifyReplication().createSubmittableJob(newConf, args);
        if (job == null) {
            Assert.fail("Job wasn't created, see the log");
        }
        if (!(job.waitForCompletion(true))) {
            Assert.fail("Job failed, see the log");
        }
        Assert.assertEquals(10, job.getCounters().findCounter(GOODROWS).getValue());
        Assert.assertEquals(0, job.getCounters().findCounter(BADROWS).getValue());
    }
}

