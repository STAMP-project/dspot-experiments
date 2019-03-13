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
package org.apache.hadoop.hdfs;


import SafeModeAction.SAFEMODE_ENTER;
import SafeModeAction.SAFEMODE_LEAVE;
import SystemErasureCodingPolicies.RS_3_2_POLICY_ID;
import SystemErasureCodingPolicies.RS_6_3_POLICY_ID;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.contract.ContractTestUtils;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.hdfs.protocol.SystemErasureCodingPolicies;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class TestErasureCodingPolicyWithSnapshot {
    private MiniDFSCluster cluster;

    private DistributedFileSystem fs;

    private Configuration conf;

    private static final int SUCCESS = 0;

    private ErasureCodingPolicy ecPolicy;

    private short groupSize;

    @Rule
    public Timeout globalTimeout = new Timeout(120000);

    /**
     * Test correctness of successive snapshot creation and deletion with erasure
     * coding policies. Create snapshot of ecDir's parent directory.
     */
    @Test
    public void testSnapshotsOnErasureCodingDirsParentDir() throws Exception {
        final int len = 1024;
        final Path ecDirParent = new Path("/parent");
        final Path ecDir = new Path(ecDirParent, "ecdir");
        final Path ecFile = new Path(ecDir, "ecfile");
        fs.mkdirs(ecDir);
        fs.allowSnapshot(ecDirParent);
        // set erasure coding policy
        fs.setErasureCodingPolicy(ecDir, ecPolicy.getName());
        DFSTestUtil.createFile(fs, ecFile, len, ((short) (1)), 65261);
        String contents = DFSTestUtil.readFile(fs, ecFile);
        final Path snap1 = fs.createSnapshot(ecDirParent, "snap1");
        final Path snap1ECDir = new Path(snap1, ecDir.getName());
        Assert.assertEquals("Got unexpected erasure coding policy", ecPolicy, fs.getErasureCodingPolicy(snap1ECDir));
        // Now delete the dir which has erasure coding policy. Re-create the dir again, and
        // take another snapshot
        fs.delete(ecDir, true);
        fs.mkdir(ecDir, FsPermission.getDirDefault());
        final Path snap2 = fs.createSnapshot(ecDirParent, "snap2");
        final Path snap2ECDir = new Path(snap2, ecDir.getName());
        Assert.assertNull("Expected null erasure coding policy", fs.getErasureCodingPolicy(snap2ECDir));
        // Make dir again with system default ec policy
        fs.setErasureCodingPolicy(ecDir, ecPolicy.getName());
        final Path snap3 = fs.createSnapshot(ecDirParent, "snap3");
        final Path snap3ECDir = new Path(snap3, ecDir.getName());
        // Check that snap3's ECPolicy has the correct settings
        ErasureCodingPolicy ezSnap3 = fs.getErasureCodingPolicy(snap3ECDir);
        Assert.assertEquals("Got unexpected erasure coding policy", ecPolicy, ezSnap3);
        // Check that older snapshots still have the old ECPolicy settings
        Assert.assertEquals("Got unexpected erasure coding policy", ecPolicy, fs.getErasureCodingPolicy(snap1ECDir));
        Assert.assertNull("Expected null erasure coding policy", fs.getErasureCodingPolicy(snap2ECDir));
        // Verify contents of the snapshotted file
        final Path snapshottedECFile = new Path((((((snap1.toString()) + "/") + (ecDir.getName())) + "/") + (ecFile.getName())));
        Assert.assertEquals("Contents of snapshotted file have changed unexpectedly", contents, DFSTestUtil.readFile(fs, snapshottedECFile));
        // Now delete the snapshots out of order and verify the EC policy
        // correctness
        fs.deleteSnapshot(ecDirParent, snap2.getName());
        Assert.assertEquals("Got unexpected erasure coding policy", ecPolicy, fs.getErasureCodingPolicy(snap1ECDir));
        Assert.assertEquals("Got unexpected erasure coding policy", ecPolicy, fs.getErasureCodingPolicy(snap3ECDir));
        fs.deleteSnapshot(ecDirParent, snap1.getName());
        Assert.assertEquals("Got unexpected erasure coding policy", ecPolicy, fs.getErasureCodingPolicy(snap3ECDir));
    }

    /**
     * Test creation of snapshot on directory has erasure coding policy.
     */
    @Test
    public void testSnapshotsOnErasureCodingDir() throws Exception {
        final Path ecDir = new Path("/ecdir");
        fs.mkdirs(ecDir);
        fs.allowSnapshot(ecDir);
        fs.setErasureCodingPolicy(ecDir, ecPolicy.getName());
        final Path snap1 = fs.createSnapshot(ecDir, "snap1");
        Assert.assertEquals("Got unexpected erasure coding policy", ecPolicy, fs.getErasureCodingPolicy(snap1));
    }

    /**
     * Test verify erasure coding policy is present after restarting the NameNode.
     */
    @Test
    public void testSnapshotsOnErasureCodingDirAfterNNRestart() throws Exception {
        final Path ecDir = new Path("/ecdir");
        fs.mkdirs(ecDir);
        fs.allowSnapshot(ecDir);
        // set erasure coding policy
        fs.setErasureCodingPolicy(ecDir, ecPolicy.getName());
        final Path snap1 = fs.createSnapshot(ecDir, "snap1");
        ErasureCodingPolicy ecSnap = fs.getErasureCodingPolicy(snap1);
        Assert.assertEquals("Got unexpected erasure coding policy", ecPolicy, ecSnap);
        // save namespace, restart namenode, and check ec policy correctness.
        fs.setSafeMode(SAFEMODE_ENTER);
        fs.saveNamespace();
        fs.setSafeMode(SAFEMODE_LEAVE);
        cluster.restartNameNode(true);
        ErasureCodingPolicy ecSnap1 = fs.getErasureCodingPolicy(snap1);
        Assert.assertEquals("Got unexpected erasure coding policy", ecPolicy, ecSnap1);
        Assert.assertEquals("Got unexpected ecSchema", ecSnap.getSchema(), ecSnap1.getSchema());
    }

    /**
     * Test copy a snapshot will not preserve its erasure coding policy info.
     */
    @Test
    public void testCopySnapshotWillNotPreserveErasureCodingPolicy() throws Exception {
        final int len = 1024;
        final Path ecDir = new Path("/ecdir");
        final Path ecFile = new Path(ecDir, "ecFile");
        fs.mkdirs(ecDir);
        fs.allowSnapshot(ecDir);
        // set erasure coding policy
        fs.setErasureCodingPolicy(ecDir, ecPolicy.getName());
        DFSTestUtil.createFile(fs, ecFile, len, ((short) (1)), 65261);
        final Path snap1 = fs.createSnapshot(ecDir, "snap1");
        Path snap1Copy = new Path(((ecDir.toString()) + "-copy"));
        final Path snap1CopyECDir = new Path("/ecdir-copy");
        String[] argv = new String[]{ "-cp", "-px", snap1.toUri().toString(), snap1Copy.toUri().toString() };
        int ret = ToolRunner.run(new org.apache.hadoop.fs.FsShell(conf), argv);
        Assert.assertEquals("cp -px is not working on a snapshot", TestErasureCodingPolicyWithSnapshot.SUCCESS, ret);
        Assert.assertNull("Got unexpected erasure coding policy", fs.getErasureCodingPolicy(snap1CopyECDir));
        Assert.assertEquals("Got unexpected erasure coding policy", ecPolicy, fs.getErasureCodingPolicy(snap1));
    }

    @Test(timeout = 300000)
    public void testFileStatusAcrossNNRestart() throws IOException {
        final int len = 1024;
        final Path normalFile = new Path("/", "normalFile");
        DFSTestUtil.createFile(fs, normalFile, len, ((short) (1)), 65261);
        final Path ecDir = new Path("/ecdir");
        final Path ecFile = new Path(ecDir, "ecFile");
        fs.mkdirs(ecDir);
        // Set erasure coding policy
        fs.setErasureCodingPolicy(ecDir, ecPolicy.getName());
        DFSTestUtil.createFile(fs, ecFile, len, ((short) (1)), 65261);
        // Verify FileStatus for normal and EC files
        ContractTestUtils.assertNotErasureCoded(fs, normalFile);
        ContractTestUtils.assertErasureCoded(fs, ecFile);
        cluster.restartNameNode(true);
        // Verify FileStatus for normal and EC files
        ContractTestUtils.assertNotErasureCoded(fs, normalFile);
        ContractTestUtils.assertErasureCoded(fs, ecFile);
    }

    @Test
    public void testErasureCodingPolicyOnDotSnapshotDir() throws IOException {
        final Path ecDir = new Path("/ecdir");
        fs.mkdirs(ecDir);
        fs.allowSnapshot(ecDir);
        // set erasure coding policy and create snapshot
        fs.setErasureCodingPolicy(ecDir, ecPolicy.getName());
        final Path snap = fs.createSnapshot(ecDir, "snap1");
        // verify the EC policy correctness
        ErasureCodingPolicy ecSnap = fs.getErasureCodingPolicy(snap);
        Assert.assertEquals("Got unexpected erasure coding policy", ecPolicy, ecSnap);
        // verify the EC policy is null, not an exception
        final Path ecDotSnapshotDir = new Path(ecDir, ".snapshot");
        ErasureCodingPolicy ecSnap1 = fs.getErasureCodingPolicy(ecDotSnapshotDir);
        Assert.assertNull("Got unexpected erasure coding policy", ecSnap1);
    }

    /**
     * Test creation of snapshot on directory which changes its
     * erasure coding policy.
     */
    @Test
    public void testSnapshotsOnErasureCodingDirAfterECPolicyChanges() throws Exception {
        final Path ecDir = new Path("/ecdir");
        fs.mkdirs(ecDir);
        fs.allowSnapshot(ecDir);
        final Path snap1 = fs.createSnapshot(ecDir, "snap1");
        Assert.assertNull("Expected null erasure coding policy", fs.getErasureCodingPolicy(snap1));
        // Set erasure coding policy
        final ErasureCodingPolicy ec63Policy = SystemErasureCodingPolicies.getByID(RS_6_3_POLICY_ID);
        fs.setErasureCodingPolicy(ecDir, ec63Policy.getName());
        final Path snap2 = fs.createSnapshot(ecDir, "snap2");
        Assert.assertEquals("Got unexpected erasure coding policy", ec63Policy, fs.getErasureCodingPolicy(snap2));
        // Verify the EC policy correctness after the unset operation
        fs.unsetErasureCodingPolicy(ecDir);
        final Path snap3 = fs.createSnapshot(ecDir, "snap3");
        Assert.assertNull("Expected null erasure coding policy", fs.getErasureCodingPolicy(snap3));
        // Change the erasure coding policy and take another snapshot
        final ErasureCodingPolicy ec32Policy = SystemErasureCodingPolicies.getByID(RS_3_2_POLICY_ID);
        fs.enableErasureCodingPolicy(ec32Policy.getName());
        fs.setErasureCodingPolicy(ecDir, ec32Policy.getName());
        final Path snap4 = fs.createSnapshot(ecDir, "snap4");
        Assert.assertEquals("Got unexpected erasure coding policy", ec32Policy, fs.getErasureCodingPolicy(snap4));
        // Check that older snapshot still have the old ECPolicy settings
        Assert.assertNull("Expected null erasure coding policy", fs.getErasureCodingPolicy(snap1));
        Assert.assertEquals("Got unexpected erasure coding policy", ec63Policy, fs.getErasureCodingPolicy(snap2));
        Assert.assertNull("Expected null erasure coding policy", fs.getErasureCodingPolicy(snap3));
    }
}

