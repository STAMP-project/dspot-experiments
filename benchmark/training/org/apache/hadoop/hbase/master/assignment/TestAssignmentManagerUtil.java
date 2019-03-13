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
package org.apache.hadoop.hbase.master.assignment;


import java.io.IOException;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseIOException;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionReplicaUtil;
import org.apache.hadoop.hbase.master.procedure.MasterProcedureEnv;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MasterTests.class, MediumTests.class })
public class TestAssignmentManagerUtil {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAssignmentManagerUtil.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NAME = TableName.valueOf("AM");

    private static MasterProcedureEnv ENV;

    private static AssignmentManager AM;

    private static int REGION_REPLICATION = 3;

    @Test
    public void testCreateUnassignProcedureForSplitFail() throws IOException {
        RegionInfo region = getPrimaryRegions().get(0);
        TestAssignmentManagerUtil.AM.getRegionStates().getRegionStateNode(region).setProcedure(TransitRegionStateProcedure.unassign(TestAssignmentManagerUtil.ENV, region));
        try {
            AssignmentManagerUtil.createUnassignProceduresForSplitOrMerge(TestAssignmentManagerUtil.ENV, Stream.of(region), TestAssignmentManagerUtil.REGION_REPLICATION);
            Assert.fail("Should fail as the region is in transition");
        } catch (HBaseIOException e) {
            // expected
        }
    }

    @Test
    public void testCreateUnassignProceduresForMergeFail() throws IOException {
        List<RegionInfo> regions = getPrimaryRegions();
        RegionInfo regionA = regions.get(0);
        RegionInfo regionB = regions.get(1);
        TestAssignmentManagerUtil.AM.getRegionStates().getRegionStateNode(regionB).setProcedure(TransitRegionStateProcedure.unassign(TestAssignmentManagerUtil.ENV, regionB));
        try {
            AssignmentManagerUtil.createUnassignProceduresForSplitOrMerge(TestAssignmentManagerUtil.ENV, Stream.of(regionA, regionB), TestAssignmentManagerUtil.REGION_REPLICATION);
            Assert.fail("Should fail as the region is in transition");
        } catch (HBaseIOException e) {
            // expected
        }
        IntStream.range(0, TestAssignmentManagerUtil.REGION_REPLICATION).mapToObj(( i) -> RegionReplicaUtil.getRegionInfoForReplica(regionA, i)).map(TestAssignmentManagerUtil.AM.getRegionStates()::getRegionStateNode).forEachOrdered(( rn) -> assertFalse(("Should have unset the proc for " + rn), rn.isInTransition()));
    }
}

