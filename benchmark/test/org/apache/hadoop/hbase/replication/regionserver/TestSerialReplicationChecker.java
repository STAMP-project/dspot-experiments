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
package org.apache.hadoop.hbase.replication.regionserver;


import RegionState.State.OPEN;
import RegionState.State.OPENING;
import java.io.IOException;
import java.util.Arrays;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.replication.ReplicationException;
import org.apache.hadoop.hbase.replication.ReplicationQueueStorage;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ ReplicationTests.class, MediumTests.class })
public class TestSerialReplicationChecker {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSerialReplicationChecker.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static String PEER_ID = "1";

    private static ReplicationQueueStorage QUEUE_STORAGE;

    private static String WAL_FILE_NAME = "test.wal";

    private Connection conn;

    private SerialReplicationChecker checker;

    @Rule
    public final TestName name = new TestName();

    private TableName tableName;

    @Test
    public void testNoBarrierCanPush() throws IOException {
        RegionInfo region = RegionInfoBuilder.newBuilder(tableName).build();
        Assert.assertTrue(checker.canPush(createEntry(region, 100), createCell(region)));
    }

    @Test
    public void testLastRegionAndOpeningCanNotPush() throws IOException, ReplicationException {
        RegionInfo region = RegionInfoBuilder.newBuilder(tableName).build();
        addStateAndBarrier(region, OPEN, 10);
        Cell cell = createCell(region);
        // can push since we are in the first range
        Assert.assertTrue(checker.canPush(createEntry(region, 100), cell));
        setState(region, OPENING);
        // can not push since we are in the last range and the state is OPENING
        Assert.assertFalse(checker.canPush(createEntry(region, 102), cell));
        addStateAndBarrier(region, OPEN, 50);
        // can not push since the previous range has not been finished yet
        Assert.assertFalse(checker.canPush(createEntry(region, 102), cell));
        updatePushedSeqId(region, 49);
        // can push since the previous range has been finished
        Assert.assertTrue(checker.canPush(createEntry(region, 102), cell));
        setState(region, OPENING);
        Assert.assertFalse(checker.canPush(createEntry(region, 104), cell));
    }

    @Test
    public void testCanPushUnder() throws IOException, ReplicationException {
        RegionInfo region = RegionInfoBuilder.newBuilder(tableName).build();
        addStateAndBarrier(region, OPEN, 10, 100);
        updatePushedSeqId(region, 9);
        Cell cell = createCell(region);
        Assert.assertTrue(checker.canPush(createEntry(region, 20), cell));
        Mockito.verify(conn, Mockito.times(1)).getTable(ArgumentMatchers.any(TableName.class));
        // not continuous
        for (int i = 22; i < 100; i += 2) {
            Assert.assertTrue(checker.canPush(createEntry(region, i), cell));
        }
        // verify that we do not go to meta table
        Mockito.verify(conn, Mockito.times(1)).getTable(ArgumentMatchers.any(TableName.class));
    }

    @Test
    public void testCanPushIfContinuous() throws IOException, ReplicationException {
        RegionInfo region = RegionInfoBuilder.newBuilder(tableName).build();
        addStateAndBarrier(region, OPEN, 10);
        updatePushedSeqId(region, 9);
        Cell cell = createCell(region);
        Assert.assertTrue(checker.canPush(createEntry(region, 20), cell));
        Mockito.verify(conn, Mockito.times(1)).getTable(ArgumentMatchers.any(TableName.class));
        // continuous
        for (int i = 21; i < 100; i++) {
            Assert.assertTrue(checker.canPush(createEntry(region, i), cell));
        }
        // verify that we do not go to meta table
        Mockito.verify(conn, Mockito.times(1)).getTable(ArgumentMatchers.any(TableName.class));
    }

    @Test
    public void testCanPushAfterMerge() throws IOException, ReplicationException {
        // 0xFF is the escape byte when storing region name so let's make sure it can work.
        byte[] endKey = new byte[]{ ((byte) (255)), 0, ((byte) (255)), ((byte) (255)), 1 };
        RegionInfo regionA = RegionInfoBuilder.newBuilder(tableName).setEndKey(endKey).setRegionId(1).build();
        RegionInfo regionB = RegionInfoBuilder.newBuilder(tableName).setStartKey(endKey).setRegionId(2).build();
        RegionInfo region = RegionInfoBuilder.newBuilder(tableName).setRegionId(3).build();
        addStateAndBarrier(regionA, null, 10, 100);
        addStateAndBarrier(regionB, null, 20, 200);
        addStateAndBarrier(region, OPEN, 200);
        addParents(region, Arrays.asList(regionA, regionB));
        Cell cell = createCell(region);
        // can not push since both parents have not been finished yet
        Assert.assertFalse(checker.canPush(createEntry(region, 300), cell));
        updatePushedSeqId(regionB, 199);
        // can not push since regionA has not been finished yet
        Assert.assertFalse(checker.canPush(createEntry(region, 300), cell));
        updatePushedSeqId(regionA, 99);
        // can push since all parents have been finished
        Assert.assertTrue(checker.canPush(createEntry(region, 300), cell));
    }

    @Test
    public void testCanPushAfterSplit() throws IOException, ReplicationException {
        // 0xFF is the escape byte when storing region name so let's make sure it can work.
        byte[] endKey = new byte[]{ ((byte) (255)), 0, ((byte) (255)), ((byte) (255)), 1 };
        RegionInfo region = RegionInfoBuilder.newBuilder(tableName).setRegionId(1).build();
        RegionInfo regionA = RegionInfoBuilder.newBuilder(tableName).setEndKey(endKey).setRegionId(2).build();
        RegionInfo regionB = RegionInfoBuilder.newBuilder(tableName).setStartKey(endKey).setRegionId(3).build();
        addStateAndBarrier(region, null, 10, 100);
        addStateAndBarrier(regionA, OPEN, 100, 200);
        addStateAndBarrier(regionB, OPEN, 100, 300);
        addParents(regionA, Arrays.asList(region));
        addParents(regionB, Arrays.asList(region));
        Cell cellA = createCell(regionA);
        Cell cellB = createCell(regionB);
        // can not push since parent has not been finished yet
        Assert.assertFalse(checker.canPush(createEntry(regionA, 150), cellA));
        Assert.assertFalse(checker.canPush(createEntry(regionB, 200), cellB));
        updatePushedSeqId(region, 99);
        // can push since parent has been finished
        Assert.assertTrue(checker.canPush(createEntry(regionA, 150), cellA));
        Assert.assertTrue(checker.canPush(createEntry(regionB, 200), cellB));
    }

    @Test
    public void testCanPushEqualsToBarrier() throws IOException, ReplicationException {
        // For binary search, equals to an element will result to a positive value, let's test whether
        // it works.
        RegionInfo region = RegionInfoBuilder.newBuilder(tableName).build();
        addStateAndBarrier(region, OPEN, 10, 100);
        Cell cell = createCell(region);
        Assert.assertTrue(checker.canPush(createEntry(region, 10), cell));
        Assert.assertFalse(checker.canPush(createEntry(region, 100), cell));
        updatePushedSeqId(region, 99);
        Assert.assertTrue(checker.canPush(createEntry(region, 100), cell));
    }
}

