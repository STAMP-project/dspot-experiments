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
package org.apache.hadoop.hdfs.server.namenode.snapshot;


import java.util.ArrayList;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Options;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.SnapshotAccessControlException;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class tests snapshot functionality. One or multiple snapshots are
 * created. The snapshotted directory is changed and verification is done to
 * ensure snapshots remain unchanges.
 */
public class TestDisallowModifyROSnapshot {
    private static final Path dir = new Path("/TestSnapshot");

    private static final Path sub1 = new Path(TestDisallowModifyROSnapshot.dir, "sub1");

    private static final Path sub2 = new Path(TestDisallowModifyROSnapshot.dir, "sub2");

    protected static Configuration conf;

    protected static MiniDFSCluster cluster;

    protected static FSNamesystem fsn;

    protected static DistributedFileSystem fs;

    /**
     * The list recording all previous snapshots. Each element in the array
     * records a snapshot root.
     */
    protected static ArrayList<Path> snapshotList = new ArrayList<Path>();

    static Path objInSnapshot = null;

    @Test(timeout = 60000, expected = SnapshotAccessControlException.class)
    public void testSetReplication() throws Exception {
        TestDisallowModifyROSnapshot.fs.setReplication(TestDisallowModifyROSnapshot.objInSnapshot, ((short) (1)));
    }

    @Test(timeout = 60000, expected = SnapshotAccessControlException.class)
    public void testSetPermission() throws Exception {
        TestDisallowModifyROSnapshot.fs.setPermission(TestDisallowModifyROSnapshot.objInSnapshot, new FsPermission("777"));
    }

    @Test(timeout = 60000, expected = SnapshotAccessControlException.class)
    public void testSetOwner() throws Exception {
        TestDisallowModifyROSnapshot.fs.setOwner(TestDisallowModifyROSnapshot.objInSnapshot, "username", "groupname");
    }

    @Test(timeout = 60000)
    public void testRename() throws Exception {
        try {
            TestDisallowModifyROSnapshot.fs.rename(TestDisallowModifyROSnapshot.objInSnapshot, new Path("/invalid/path"));
            Assert.fail("Didn't throw SnapshotAccessControlException");
        } catch (SnapshotAccessControlException e) {
            /* Ignored */
        }
        try {
            TestDisallowModifyROSnapshot.fs.rename(TestDisallowModifyROSnapshot.sub2, TestDisallowModifyROSnapshot.objInSnapshot);
            Assert.fail("Didn't throw SnapshotAccessControlException");
        } catch (SnapshotAccessControlException e) {
            /* Ignored */
        }
        try {
            TestDisallowModifyROSnapshot.fs.rename(TestDisallowModifyROSnapshot.sub2, TestDisallowModifyROSnapshot.objInSnapshot, ((Options.Rename) (null)));
            Assert.fail("Didn't throw SnapshotAccessControlException");
        } catch (SnapshotAccessControlException e) {
            /* Ignored */
        }
    }

    @Test(timeout = 60000, expected = SnapshotAccessControlException.class)
    public void testDelete() throws Exception {
        TestDisallowModifyROSnapshot.fs.delete(TestDisallowModifyROSnapshot.objInSnapshot, true);
    }

    @Test(timeout = 60000, expected = SnapshotAccessControlException.class)
    public void testQuota() throws Exception {
        TestDisallowModifyROSnapshot.fs.setQuota(TestDisallowModifyROSnapshot.objInSnapshot, 100, 100);
    }

    @Test(timeout = 60000, expected = SnapshotAccessControlException.class)
    public void testSetTime() throws Exception {
        TestDisallowModifyROSnapshot.fs.setTimes(TestDisallowModifyROSnapshot.objInSnapshot, 100, 100);
    }

    @Test(timeout = 60000, expected = SnapshotAccessControlException.class)
    public void testCreate() throws Exception {
        @SuppressWarnings("deprecation")
        DFSClient dfsclient = new DFSClient(TestDisallowModifyROSnapshot.conf);
        dfsclient.create(TestDisallowModifyROSnapshot.objInSnapshot.toString(), true);
    }

    @Test(timeout = 60000, expected = SnapshotAccessControlException.class)
    public void testAppend() throws Exception {
        TestDisallowModifyROSnapshot.fs.append(TestDisallowModifyROSnapshot.objInSnapshot, 65535, null);
    }

    @Test(timeout = 60000, expected = SnapshotAccessControlException.class)
    public void testMkdir() throws Exception {
        TestDisallowModifyROSnapshot.fs.mkdirs(TestDisallowModifyROSnapshot.objInSnapshot, new FsPermission("777"));
    }

    @Test(timeout = 60000, expected = SnapshotAccessControlException.class)
    public void testCreateSymlink() throws Exception {
        @SuppressWarnings("deprecation")
        DFSClient dfsclient = new DFSClient(TestDisallowModifyROSnapshot.conf);
        dfsclient.createSymlink(TestDisallowModifyROSnapshot.sub2.toString(), "/TestSnapshot/sub1/.snapshot", false);
    }
}

