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
package org.apache.hadoop.hbase.client;


import RegionState.State.OPEN;
import RegionState.State.SPLIT;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.master.assignment.RegionStates;
import org.apache.hadoop.hbase.snapshot.SnapshotTestingUtils;
import org.junit.Assert;
import org.junit.Test;


public class CloneSnapshotFromClientAfterSplittingRegionTestBase extends CloneSnapshotFromClientTestBase {
    @Test
    public void testCloneSnapshotAfterSplittingRegion() throws IOException, InterruptedException {
        // Turn off the CatalogJanitor
        admin.catalogJanitorSwitch(false);
        try {
            List<RegionInfo> regionInfos = admin.getRegions(tableName);
            RegionReplicaUtil.removeNonDefaultRegions(regionInfos);
            // Split the first region
            splitRegion(regionInfos.get(0));
            // Take a snapshot
            admin.snapshot(snapshotName2, tableName);
            // Clone the snapshot to another table
            TableName clonedTableName = TableName.valueOf((((getValidMethodName()) + "-") + (System.currentTimeMillis())));
            admin.cloneSnapshot(snapshotName2, clonedTableName);
            SnapshotTestingUtils.waitForTableToBeOnline(CloneSnapshotFromClientTestBase.TEST_UTIL, clonedTableName);
            RegionStates regionStates = CloneSnapshotFromClientTestBase.TEST_UTIL.getHBaseCluster().getMaster().getAssignmentManager().getRegionStates();
            // The region count of the cloned table should be the same as the one of the original table
            int openRegionCountOfOriginalTable = regionStates.getRegionByStateOfTable(tableName).get(OPEN).size();
            int openRegionCountOfClonedTable = regionStates.getRegionByStateOfTable(clonedTableName).get(OPEN).size();
            Assert.assertEquals(openRegionCountOfOriginalTable, openRegionCountOfClonedTable);
            int splitRegionCountOfOriginalTable = regionStates.getRegionByStateOfTable(tableName).get(SPLIT).size();
            int splitRegionCountOfClonedTable = regionStates.getRegionByStateOfTable(clonedTableName).get(SPLIT).size();
            Assert.assertEquals(splitRegionCountOfOriginalTable, splitRegionCountOfClonedTable);
            CloneSnapshotFromClientTestBase.TEST_UTIL.deleteTable(clonedTableName);
        } finally {
            admin.catalogJanitorSwitch(true);
        }
    }
}

