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


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.SnapshotDiffReport;
import org.junit.Test;


/**
 * This class includes end-to-end tests for snapshot related FsShell and
 * DFSAdmin commands.
 */
public class TestSnapshotCommands {
    private static Configuration conf;

    private static MiniDFSCluster cluster;

    private static DistributedFileSystem fs;

    @Test
    public void testAllowSnapshot() throws Exception {
        // Idempotent test
        DFSTestUtil.DFSAdminRun("-allowSnapshot /sub1", 0, ("Allowing snapshot " + "on /sub1 succeeded"), TestSnapshotCommands.conf);
        // allow normal dir success
        DFSTestUtil.FsShellRun("-mkdir /sub2", TestSnapshotCommands.conf);
        DFSTestUtil.DFSAdminRun("-allowSnapshot /sub2", 0, ("Allowing snapshot " + "on /sub2 succeeded"), TestSnapshotCommands.conf);
        // allow non-exists dir failed
        DFSTestUtil.DFSAdminRun("-allowSnapshot /sub3", (-1), null, TestSnapshotCommands.conf);
    }

    @Test
    public void testCreateSnapshot() throws Exception {
        // test createSnapshot
        DFSTestUtil.FsShellRun("-createSnapshot /sub1 sn0", 0, "Created snapshot /sub1/.snapshot/sn0", TestSnapshotCommands.conf);
        DFSTestUtil.FsShellRun("-createSnapshot /sub1 sn0", 1, "there is already a snapshot with the same name \"sn0\"", TestSnapshotCommands.conf);
        DFSTestUtil.FsShellRun("-rmr /sub1/sub1sub2", TestSnapshotCommands.conf);
        DFSTestUtil.FsShellRun("-mkdir /sub1/sub1sub3", TestSnapshotCommands.conf);
        DFSTestUtil.FsShellRun("-createSnapshot /sub1 sn1", 0, "Created snapshot /sub1/.snapshot/sn1", TestSnapshotCommands.conf);
        // check snapshot contents
        DFSTestUtil.FsShellRun("-ls /sub1", 0, "/sub1/sub1sub1", TestSnapshotCommands.conf);
        DFSTestUtil.FsShellRun("-ls /sub1", 0, "/sub1/sub1sub3", TestSnapshotCommands.conf);
        DFSTestUtil.FsShellRun("-ls /sub1/.snapshot", 0, "/sub1/.snapshot/sn0", TestSnapshotCommands.conf);
        DFSTestUtil.FsShellRun("-ls /sub1/.snapshot", 0, "/sub1/.snapshot/sn1", TestSnapshotCommands.conf);
        DFSTestUtil.FsShellRun("-ls /sub1/.snapshot/sn0", 0, "/sub1/.snapshot/sn0/sub1sub1", TestSnapshotCommands.conf);
        DFSTestUtil.FsShellRun("-ls /sub1/.snapshot/sn0", 0, "/sub1/.snapshot/sn0/sub1sub2", TestSnapshotCommands.conf);
        DFSTestUtil.FsShellRun("-ls /sub1/.snapshot/sn1", 0, "/sub1/.snapshot/sn1/sub1sub1", TestSnapshotCommands.conf);
        DFSTestUtil.FsShellRun("-ls /sub1/.snapshot/sn1", 0, "/sub1/.snapshot/sn1/sub1sub3", TestSnapshotCommands.conf);
    }

    @Test
    public void testMaxSnapshotLimit() throws Exception {
        DFSTestUtil.FsShellRun("-mkdir /sub3", TestSnapshotCommands.conf);
        DFSTestUtil.DFSAdminRun("-allowSnapshot /sub3", 0, ("Allowing snapshot " + "on /sub3 succeeded"), TestSnapshotCommands.conf);
        // test createSnapshot
        DFSTestUtil.FsShellRun("-createSnapshot /sub3 sn0", 0, "Created snapshot /sub3/.snapshot/sn0", TestSnapshotCommands.conf);
        DFSTestUtil.FsShellRun("-createSnapshot /sub3 sn1", 0, "Created snapshot /sub3/.snapshot/sn1", TestSnapshotCommands.conf);
        DFSTestUtil.FsShellRun("-createSnapshot /sub3 sn2", 0, "Created snapshot /sub3/.snapshot/sn2", TestSnapshotCommands.conf);
        DFSTestUtil.FsShellRun("-createSnapshot /sub3 sn3", 1, ("Failed to add snapshot: there are already 3 snapshot(s) and " + "the max snapshot limit is 3"), TestSnapshotCommands.conf);
    }

    @Test
    public void testMkdirUsingReservedName() throws Exception {
        // test can not create dir with reserved name: .snapshot
        DFSTestUtil.FsShellRun("-ls /", TestSnapshotCommands.conf);
        DFSTestUtil.FsShellRun("-mkdir /.snapshot", 1, "File exists", TestSnapshotCommands.conf);
        DFSTestUtil.FsShellRun("-mkdir /sub1/.snapshot", 1, "File exists", TestSnapshotCommands.conf);
        // mkdir -p ignore reserved name check if dir already exists
        DFSTestUtil.FsShellRun("-mkdir -p /sub1/.snapshot", TestSnapshotCommands.conf);
        DFSTestUtil.FsShellRun("-mkdir -p /sub1/sub1sub1/.snapshot", 1, "mkdir: \".snapshot\" is a reserved name.", TestSnapshotCommands.conf);
    }

    @Test
    public void testRenameSnapshot() throws Exception {
        DFSTestUtil.FsShellRun("-createSnapshot /sub1 sn.orig", TestSnapshotCommands.conf);
        DFSTestUtil.FsShellRun("-renameSnapshot /sub1 sn.orig sn.rename", TestSnapshotCommands.conf);
        DFSTestUtil.FsShellRun("-ls /sub1/.snapshot", 0, "/sub1/.snapshot/sn.rename", TestSnapshotCommands.conf);
        DFSTestUtil.FsShellRun("-ls /sub1/.snapshot/sn.rename", 0, "/sub1/.snapshot/sn.rename/sub1sub1", TestSnapshotCommands.conf);
        DFSTestUtil.FsShellRun("-ls /sub1/.snapshot/sn.rename", 0, "/sub1/.snapshot/sn.rename/sub1sub2", TestSnapshotCommands.conf);
        // try renaming from a non-existing snapshot
        DFSTestUtil.FsShellRun("-renameSnapshot /sub1 sn.nonexist sn.rename", 1, "renameSnapshot: The snapshot sn.nonexist does not exist for directory /sub1", TestSnapshotCommands.conf);
        // try renaming a non-existing snapshot to itself
        DFSTestUtil.FsShellRun("-renameSnapshot /sub1 sn.nonexist sn.nonexist", 1, ("renameSnapshot: The snapshot sn.nonexist " + "does not exist for directory /sub1"), TestSnapshotCommands.conf);
        // try renaming to existing snapshots
        DFSTestUtil.FsShellRun("-createSnapshot /sub1 sn.new", TestSnapshotCommands.conf);
        DFSTestUtil.FsShellRun("-renameSnapshot /sub1 sn.new sn.rename", 1, "renameSnapshot: The snapshot sn.rename already exists for directory /sub1", TestSnapshotCommands.conf);
        DFSTestUtil.FsShellRun("-renameSnapshot /sub1 sn.rename sn.new", 1, "renameSnapshot: The snapshot sn.new already exists for directory /sub1", TestSnapshotCommands.conf);
    }

    @Test
    public void testDeleteSnapshot() throws Exception {
        DFSTestUtil.FsShellRun("-createSnapshot /sub1 sn1", TestSnapshotCommands.conf);
        DFSTestUtil.FsShellRun("-deleteSnapshot /sub1 sn1", TestSnapshotCommands.conf);
        DFSTestUtil.FsShellRun("-deleteSnapshot /sub1 sn1", 1, "deleteSnapshot: Cannot delete snapshot sn1 from path /sub1: the snapshot does not exist.", TestSnapshotCommands.conf);
    }

    @Test
    public void testDisallowSnapshot() throws Exception {
        DFSTestUtil.FsShellRun("-createSnapshot /sub1 sn1", TestSnapshotCommands.conf);
        // cannot delete snapshotable dir
        DFSTestUtil.FsShellRun("-rmr /sub1", 1, "The directory /sub1 cannot be deleted since /sub1 is snapshottable and already has snapshots", TestSnapshotCommands.conf);
        DFSTestUtil.DFSAdminRun("-disallowSnapshot /sub1", (-1), "disallowSnapshot: The directory /sub1 has snapshot(s). Please redo the operation after removing all the snapshots.", TestSnapshotCommands.conf);
        DFSTestUtil.FsShellRun("-deleteSnapshot /sub1 sn1", TestSnapshotCommands.conf);
        DFSTestUtil.DFSAdminRun("-disallowSnapshot /sub1", 0, "Disallowing snapshot on /sub1 succeeded", TestSnapshotCommands.conf);
        // Idempotent test
        DFSTestUtil.DFSAdminRun("-disallowSnapshot /sub1", 0, "Disallowing snapshot on /sub1 succeeded", TestSnapshotCommands.conf);
        // now it can be deleted
        DFSTestUtil.FsShellRun("-rmr /sub1", TestSnapshotCommands.conf);
    }

    @Test(timeout = 60000)
    public void testSnapshotCommandsWithURI() throws Exception {
        Configuration config = new HdfsConfiguration();
        // fs.defaultFS should not be used, when path is fully qualified.
        config.set("fs.defaultFS", "hdfs://127.0.0.1:1024");
        String path = (TestSnapshotCommands.fs.getUri()) + "/Fully/QPath";
        DFSTestUtil.DFSAdminRun(("-allowSnapshot " + path), 0, (("Allowing snapshot on " + path) + " succeeded"), config);
        DFSTestUtil.FsShellRun((("-createSnapshot " + path) + " sn1"), config);
        // create file1
        DFSTestUtil.createFile(TestSnapshotCommands.fs, new Path(((TestSnapshotCommands.fs.getUri()) + "/Fully/QPath/File1")), 1024, ((short) (1)), 100);
        // create file2
        DFSTestUtil.createFile(TestSnapshotCommands.fs, new Path(((TestSnapshotCommands.fs.getUri()) + "/Fully/QPath/File2")), 1024, ((short) (1)), 100);
        DFSTestUtil.FsShellRun((("-createSnapshot " + path) + " sn2"), config);
        // verify the snapshotdiff using api and command line
        SnapshotDiffReport report = TestSnapshotCommands.fs.getSnapshotDiffReport(new Path(path), "sn1", "sn2");
        DFSTestUtil.toolRun(new org.apache.hadoop.hdfs.tools.snapshot.SnapshotDiff(config), (path + " sn1 sn2"), 0, report.toString());
        DFSTestUtil.FsShellRun((("-renameSnapshot " + path) + " sn2 sn3"), config);
        DFSTestUtil.FsShellRun((("-deleteSnapshot " + path) + " sn1"), config);
        DFSTestUtil.FsShellRun((("-deleteSnapshot " + path) + " sn3"), config);
        DFSTestUtil.DFSAdminRun(("-disallowSnapshot " + path), 0, (("Disallowing snapshot on " + path) + " succeeded"), config);
        TestSnapshotCommands.fs.delete(new Path("/Fully/QPath"), true);
    }

    @Test(timeout = 60000)
    public void testSnapshotDiff() throws Exception {
        Configuration config = new HdfsConfiguration();
        Path snapDirPath = new Path(((TestSnapshotCommands.fs.getUri().toString()) + "/snap_dir"));
        String snapDir = snapDirPath.toString();
        TestSnapshotCommands.fs.mkdirs(snapDirPath);
        DFSTestUtil.DFSAdminRun(("-allowSnapshot " + snapDirPath), 0, (("Allowing snapshot on " + snapDirPath) + " succeeded"), config);
        DFSTestUtil.createFile(TestSnapshotCommands.fs, new Path(snapDirPath, "file1"), 1024, ((short) (1)), 100);
        DFSTestUtil.FsShellRun((("-createSnapshot " + snapDirPath) + " sn1"), config);
        DFSTestUtil.createFile(TestSnapshotCommands.fs, new Path(snapDirPath, "file2"), 1024, ((short) (1)), 100);
        DFSTestUtil.createFile(TestSnapshotCommands.fs, new Path(snapDirPath, "file3"), 1024, ((short) (1)), 100);
        DFSTestUtil.FsShellRun((("-createSnapshot " + snapDirPath) + " sn2"), config);
        // verify the snapshot diff using api and command line
        SnapshotDiffReport report_s1_s2 = TestSnapshotCommands.fs.getSnapshotDiffReport(snapDirPath, "sn1", "sn2");
        DFSTestUtil.toolRun(new org.apache.hadoop.hdfs.tools.snapshot.SnapshotDiff(config), (snapDir + " sn1 sn2"), 0, report_s1_s2.toString());
        DFSTestUtil.FsShellRun((("-renameSnapshot " + snapDirPath) + " sn2 sn3"), config);
        SnapshotDiffReport report_s1_s3 = TestSnapshotCommands.fs.getSnapshotDiffReport(snapDirPath, "sn1", "sn3");
        DFSTestUtil.toolRun(new org.apache.hadoop.hdfs.tools.snapshot.SnapshotDiff(config), (snapDir + " sn1 sn3"), 0, report_s1_s3.toString());
        // Creating 100 more files so as to force DiffReport generation
        // backend ChunkedArrayList to create multiple chunks.
        for (int i = 0; i < 100; i++) {
            DFSTestUtil.createFile(TestSnapshotCommands.fs, new Path(snapDirPath, ("file_" + i)), 1, ((short) (1)), 100);
        }
        DFSTestUtil.FsShellRun((("-createSnapshot " + snapDirPath) + " sn4"), config);
        DFSTestUtil.toolRun(new org.apache.hadoop.hdfs.tools.snapshot.SnapshotDiff(config), (snapDir + " sn1 sn4"), 0, null);
        DFSTestUtil.FsShellRun((("-deleteSnapshot " + snapDir) + " sn1"), config);
        DFSTestUtil.FsShellRun((("-deleteSnapshot " + snapDir) + " sn3"), config);
        DFSTestUtil.FsShellRun((("-deleteSnapshot " + snapDir) + " sn4"), config);
        DFSTestUtil.DFSAdminRun(("-disallowSnapshot " + snapDir), 0, (("Disallowing snapshot on " + snapDirPath) + " succeeded"), config);
        TestSnapshotCommands.fs.delete(new Path("/Fully/QPath"), true);
    }
}

