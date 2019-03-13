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
package org.apache.hadoop.hbase.snapshot;


import FlushSnapshotSubprocedure.RegionSnapshotTask;
import SnapshotManifestV2.DESCRIPTOR_VERSION;
import SnapshotProtos.SnapshotDescription;
import SnapshotProtos.SnapshotDescription.Type.FLUSH;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.errorhandling.ForeignExceptionDispatcher;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.snapshot.FlushSnapshotSubprocedure;
import org.apache.hadoop.hbase.shaded.protobuf.generated.SnapshotProtos;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Testing the region snapshot task on a cluster.
 *
 * @see org.apache.hadoop.hbase.regionserver.snapshot.FlushSnapshotSubprocedure.RegionSnapshotTask
 */
@Category({ MediumTests.class, RegionServerTests.class })
public class TestRegionSnapshotTask {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionSnapshotTask.class);

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private static HBaseTestingUtility TEST_UTIL;

    private static Configuration conf;

    private static FileSystem fs;

    private static Path rootDir;

    /**
     * Tests adding a region to the snapshot manifest while compactions are running on the region.
     * The idea is to slow down the process of adding a store file to the manifest while
     * triggering compactions on the region, allowing the store files to be marked for archival while
     * snapshot operation is running.
     * This test checks for the correct behavior in such a case that the compacted files should
     * not be moved around if a snapshot operation is in progress.
     * See HBASE-18398
     */
    @Test
    public void testAddRegionWithCompactions() throws Exception {
        final TableName tableName = TableName.valueOf("test_table");
        Table table = setupTable(tableName);
        List<HRegion> hRegions = TestRegionSnapshotTask.TEST_UTIL.getHBaseCluster().getRegions(tableName);
        final SnapshotProtos.SnapshotDescription snapshot = SnapshotDescription.newBuilder().setTable(tableName.getNameAsString()).setType(FLUSH).setName("test_table_snapshot").setVersion(DESCRIPTOR_VERSION).build();
        ForeignExceptionDispatcher monitor = new ForeignExceptionDispatcher(snapshot.getName());
        final HRegion region = Mockito.spy(hRegions.get(0));
        Path workingDir = SnapshotDescriptionUtils.getWorkingSnapshotDir(snapshot, TestRegionSnapshotTask.rootDir, TestRegionSnapshotTask.conf);
        final SnapshotManifest manifest = SnapshotManifest.create(TestRegionSnapshotTask.conf, TestRegionSnapshotTask.fs, workingDir, snapshot, monitor);
        manifest.addTableDescriptor(table.getTableDescriptor());
        if (!(TestRegionSnapshotTask.fs.exists(workingDir))) {
            TestRegionSnapshotTask.fs.mkdirs(workingDir);
        }
        Assert.assertTrue(TestRegionSnapshotTask.fs.exists(workingDir));
        SnapshotDescriptionUtils.writeSnapshotInfo(snapshot, workingDir, TestRegionSnapshotTask.fs);
        Mockito.doAnswer(( __) -> {
            addRegionToSnapshot(snapshot, region, manifest);
            return null;
        }).when(region).addRegionToSnapshot(snapshot, monitor);
        FlushSnapshotSubprocedure.RegionSnapshotTask snapshotTask = new FlushSnapshotSubprocedure.RegionSnapshotTask(region, snapshot, true, monitor);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future f = executor.submit(snapshotTask);
        // Trigger major compaction and wait for snaphot operation to finish
        LOG.info("Starting major compaction");
        region.compact(true);
        LOG.info("Finished major compaction");
        f.get();
        // Consolidate region manifests into a single snapshot manifest
        manifest.consolidate();
        // Make sure that the region manifest exists, which means the snapshot operation succeeded
        Assert.assertNotNull(manifest.getRegionManifests());
        // Sanity check, there should be only one region
        Assert.assertEquals(1, manifest.getRegionManifests().size());
        // Make sure that no files went missing after the snapshot operation
        SnapshotReferenceUtil.verifySnapshot(TestRegionSnapshotTask.conf, TestRegionSnapshotTask.fs, manifest);
    }
}

