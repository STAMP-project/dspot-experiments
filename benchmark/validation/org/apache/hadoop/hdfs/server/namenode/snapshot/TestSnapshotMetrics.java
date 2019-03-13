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


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.SnapshottableDirectoryStatus;
import org.apache.hadoop.test.MetricsAsserts;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the snapshot-related metrics
 */
public class TestSnapshotMetrics {
    private static final long seed = 0;

    private static final short REPLICATION = 3;

    private static final String NN_METRICS = "NameNodeActivity";

    private static final String NS_METRICS = "FSNamesystem";

    private final Path dir = new Path("/TestSnapshot");

    private final Path sub1 = new Path(dir, "sub1");

    private final Path file1 = new Path(sub1, "file1");

    private final Path file2 = new Path(sub1, "file2");

    private Configuration conf;

    private MiniDFSCluster cluster;

    private DistributedFileSystem hdfs;

    /**
     * Test the metric SnapshottableDirectories, AllowSnapshotOps,
     * DisallowSnapshotOps, and listSnapshottableDirOps
     */
    @Test
    public void testSnapshottableDirs() throws Exception {
        cluster.getNamesystem().getSnapshotManager().setAllowNestedSnapshots(true);
        MetricsAsserts.assertGauge("SnapshottableDirectories", 0, MetricsAsserts.getMetrics(TestSnapshotMetrics.NS_METRICS));
        MetricsAsserts.assertCounter("AllowSnapshotOps", 0L, MetricsAsserts.getMetrics(TestSnapshotMetrics.NN_METRICS));
        MetricsAsserts.assertCounter("DisallowSnapshotOps", 0L, MetricsAsserts.getMetrics(TestSnapshotMetrics.NN_METRICS));
        // Allow snapshots for directories, and check the metrics
        hdfs.allowSnapshot(sub1);
        MetricsAsserts.assertGauge("SnapshottableDirectories", 1, MetricsAsserts.getMetrics(TestSnapshotMetrics.NS_METRICS));
        MetricsAsserts.assertCounter("AllowSnapshotOps", 1L, MetricsAsserts.getMetrics(TestSnapshotMetrics.NN_METRICS));
        Path sub2 = new Path(dir, "sub2");
        Path file = new Path(sub2, "file");
        DFSTestUtil.createFile(hdfs, file, 1024, TestSnapshotMetrics.REPLICATION, TestSnapshotMetrics.seed);
        hdfs.allowSnapshot(sub2);
        MetricsAsserts.assertGauge("SnapshottableDirectories", 2, MetricsAsserts.getMetrics(TestSnapshotMetrics.NS_METRICS));
        MetricsAsserts.assertCounter("AllowSnapshotOps", 2L, MetricsAsserts.getMetrics(TestSnapshotMetrics.NN_METRICS));
        Path subsub1 = new Path(sub1, "sub1sub1");
        Path subfile = new Path(subsub1, "file");
        DFSTestUtil.createFile(hdfs, subfile, 1024, TestSnapshotMetrics.REPLICATION, TestSnapshotMetrics.seed);
        hdfs.allowSnapshot(subsub1);
        MetricsAsserts.assertGauge("SnapshottableDirectories", 3, MetricsAsserts.getMetrics(TestSnapshotMetrics.NS_METRICS));
        MetricsAsserts.assertCounter("AllowSnapshotOps", 3L, MetricsAsserts.getMetrics(TestSnapshotMetrics.NN_METRICS));
        // Set an already snapshottable directory to snapshottable, should not
        // change the metrics
        hdfs.allowSnapshot(sub1);
        MetricsAsserts.assertGauge("SnapshottableDirectories", 3, MetricsAsserts.getMetrics(TestSnapshotMetrics.NS_METRICS));
        // But the number of allowSnapshot operations still increases
        MetricsAsserts.assertCounter("AllowSnapshotOps", 4L, MetricsAsserts.getMetrics(TestSnapshotMetrics.NN_METRICS));
        // Disallow the snapshot for snapshottable directories, then check the
        // metrics again
        hdfs.disallowSnapshot(sub1);
        MetricsAsserts.assertGauge("SnapshottableDirectories", 2, MetricsAsserts.getMetrics(TestSnapshotMetrics.NS_METRICS));
        MetricsAsserts.assertCounter("DisallowSnapshotOps", 1L, MetricsAsserts.getMetrics(TestSnapshotMetrics.NN_METRICS));
        // delete subsub1, snapshottable directories should be 1
        hdfs.delete(subsub1, true);
        MetricsAsserts.assertGauge("SnapshottableDirectories", 1, MetricsAsserts.getMetrics(TestSnapshotMetrics.NS_METRICS));
        // list all the snapshottable directories
        SnapshottableDirectoryStatus[] status = hdfs.getSnapshottableDirListing();
        Assert.assertEquals(1, status.length);
        MetricsAsserts.assertCounter("ListSnapshottableDirOps", 1L, MetricsAsserts.getMetrics(TestSnapshotMetrics.NN_METRICS));
    }

    /**
     * Test the metrics Snapshots, CreateSnapshotOps, DeleteSnapshotOps,
     * RenameSnapshotOps
     */
    @Test
    public void testSnapshots() throws Exception {
        cluster.getNamesystem().getSnapshotManager().setAllowNestedSnapshots(true);
        MetricsAsserts.assertGauge("Snapshots", 0, MetricsAsserts.getMetrics(TestSnapshotMetrics.NS_METRICS));
        MetricsAsserts.assertCounter("CreateSnapshotOps", 0L, MetricsAsserts.getMetrics(TestSnapshotMetrics.NN_METRICS));
        // Create a snapshot for a non-snapshottable directory, thus should not
        // change the metrics
        try {
            hdfs.createSnapshot(sub1, "s1");
        } catch (Exception e) {
        }
        MetricsAsserts.assertGauge("Snapshots", 0, MetricsAsserts.getMetrics(TestSnapshotMetrics.NS_METRICS));
        MetricsAsserts.assertCounter("CreateSnapshotOps", 1L, MetricsAsserts.getMetrics(TestSnapshotMetrics.NN_METRICS));
        // Create snapshot for sub1
        hdfs.allowSnapshot(sub1);
        hdfs.createSnapshot(sub1, "s1");
        MetricsAsserts.assertGauge("Snapshots", 1, MetricsAsserts.getMetrics(TestSnapshotMetrics.NS_METRICS));
        MetricsAsserts.assertCounter("CreateSnapshotOps", 2L, MetricsAsserts.getMetrics(TestSnapshotMetrics.NN_METRICS));
        hdfs.createSnapshot(sub1, "s2");
        MetricsAsserts.assertGauge("Snapshots", 2, MetricsAsserts.getMetrics(TestSnapshotMetrics.NS_METRICS));
        MetricsAsserts.assertCounter("CreateSnapshotOps", 3L, MetricsAsserts.getMetrics(TestSnapshotMetrics.NN_METRICS));
        hdfs.getSnapshotDiffReport(sub1, "s1", "s2");
        MetricsAsserts.assertCounter("SnapshotDiffReportOps", 1L, MetricsAsserts.getMetrics(TestSnapshotMetrics.NN_METRICS));
        // Create snapshot for a directory under sub1
        Path subsub1 = new Path(sub1, "sub1sub1");
        Path subfile = new Path(subsub1, "file");
        DFSTestUtil.createFile(hdfs, subfile, 1024, TestSnapshotMetrics.REPLICATION, TestSnapshotMetrics.seed);
        hdfs.allowSnapshot(subsub1);
        hdfs.createSnapshot(subsub1, "s11");
        MetricsAsserts.assertGauge("Snapshots", 3, MetricsAsserts.getMetrics(TestSnapshotMetrics.NS_METRICS));
        MetricsAsserts.assertCounter("CreateSnapshotOps", 4L, MetricsAsserts.getMetrics(TestSnapshotMetrics.NN_METRICS));
        // delete snapshot
        hdfs.deleteSnapshot(sub1, "s2");
        MetricsAsserts.assertGauge("Snapshots", 2, MetricsAsserts.getMetrics(TestSnapshotMetrics.NS_METRICS));
        MetricsAsserts.assertCounter("DeleteSnapshotOps", 1L, MetricsAsserts.getMetrics(TestSnapshotMetrics.NN_METRICS));
        // rename snapshot
        hdfs.renameSnapshot(sub1, "s1", "NewS1");
        MetricsAsserts.assertGauge("Snapshots", 2, MetricsAsserts.getMetrics(TestSnapshotMetrics.NS_METRICS));
        MetricsAsserts.assertCounter("RenameSnapshotOps", 1L, MetricsAsserts.getMetrics(TestSnapshotMetrics.NN_METRICS));
    }
}

