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
package org.apache.hadoop.hbase.coprocessor;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.regionserver.MiniBatchOperationInProgress;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.apache.hadoop.hbase.wal.WALKey;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(MediumTests.class)
public class TestRegionObserverForAddingMutationsFromCoprocessors {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionObserverForAddingMutationsFromCoprocessors.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRegionObserverForAddingMutationsFromCoprocessors.class);

    private static HBaseTestingUtility util;

    private static final byte[] dummy = Bytes.toBytes("dummy");

    private static final byte[] row1 = Bytes.toBytes("r1");

    private static final byte[] row2 = Bytes.toBytes("r2");

    private static final byte[] row3 = Bytes.toBytes("r3");

    private static final byte[] test = Bytes.toBytes("test");

    @Rule
    public TestName name = new TestName();

    private TableName tableName;

    /**
     * Test various multiput operations.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMulti() throws Exception {
        createTable(TestRegionObserverForAddingMutationsFromCoprocessors.TestMultiMutationCoprocessor.class.getName());
        try (Table t = TestRegionObserverForAddingMutationsFromCoprocessors.util.getConnection().getTable(tableName)) {
            t.put(new Put(TestRegionObserverForAddingMutationsFromCoprocessors.row1).addColumn(TestRegionObserverForAddingMutationsFromCoprocessors.test, TestRegionObserverForAddingMutationsFromCoprocessors.dummy, TestRegionObserverForAddingMutationsFromCoprocessors.dummy));
            TestRegionObserverForAddingMutationsFromCoprocessors.assertRowCount(t, 3);
        }
    }

    /**
     * Tests that added mutations from coprocessors end up in the WAL.
     */
    @Test
    public void testCPMutationsAreWrittenToWALEdit() throws Exception {
        createTable(TestRegionObserverForAddingMutationsFromCoprocessors.TestMultiMutationCoprocessor.class.getName());
        try (Table t = TestRegionObserverForAddingMutationsFromCoprocessors.util.getConnection().getTable(tableName)) {
            t.put(new Put(TestRegionObserverForAddingMutationsFromCoprocessors.row1).addColumn(TestRegionObserverForAddingMutationsFromCoprocessors.test, TestRegionObserverForAddingMutationsFromCoprocessors.dummy, TestRegionObserverForAddingMutationsFromCoprocessors.dummy));
            TestRegionObserverForAddingMutationsFromCoprocessors.assertRowCount(t, 3);
        }
        Assert.assertNotNull(TestRegionObserverForAddingMutationsFromCoprocessors.TestWALObserver.savedEdit);
        Assert.assertEquals(4, TestRegionObserverForAddingMutationsFromCoprocessors.TestWALObserver.savedEdit.getCells().size());
    }

    @Test
    public void testDeleteCell() throws Exception {
        createTable(TestRegionObserverForAddingMutationsFromCoprocessors.TestDeleteCellCoprocessor.class.getName());
        try (Table t = TestRegionObserverForAddingMutationsFromCoprocessors.util.getConnection().getTable(tableName)) {
            t.put(Lists.newArrayList(new Put(TestRegionObserverForAddingMutationsFromCoprocessors.row1).addColumn(TestRegionObserverForAddingMutationsFromCoprocessors.test, TestRegionObserverForAddingMutationsFromCoprocessors.dummy, TestRegionObserverForAddingMutationsFromCoprocessors.dummy), new Put(TestRegionObserverForAddingMutationsFromCoprocessors.row2).addColumn(TestRegionObserverForAddingMutationsFromCoprocessors.test, TestRegionObserverForAddingMutationsFromCoprocessors.dummy, TestRegionObserverForAddingMutationsFromCoprocessors.dummy), new Put(TestRegionObserverForAddingMutationsFromCoprocessors.row3).addColumn(TestRegionObserverForAddingMutationsFromCoprocessors.test, TestRegionObserverForAddingMutationsFromCoprocessors.dummy, TestRegionObserverForAddingMutationsFromCoprocessors.dummy)));
            TestRegionObserverForAddingMutationsFromCoprocessors.assertRowCount(t, 3);
            t.delete(new Delete(TestRegionObserverForAddingMutationsFromCoprocessors.test).addColumn(TestRegionObserverForAddingMutationsFromCoprocessors.test, TestRegionObserverForAddingMutationsFromCoprocessors.dummy));// delete non-existing row

            TestRegionObserverForAddingMutationsFromCoprocessors.assertRowCount(t, 1);
        }
    }

    @Test
    public void testDeleteFamily() throws Exception {
        createTable(TestRegionObserverForAddingMutationsFromCoprocessors.TestDeleteFamilyCoprocessor.class.getName());
        try (Table t = TestRegionObserverForAddingMutationsFromCoprocessors.util.getConnection().getTable(tableName)) {
            t.put(Lists.newArrayList(new Put(TestRegionObserverForAddingMutationsFromCoprocessors.row1).addColumn(TestRegionObserverForAddingMutationsFromCoprocessors.test, TestRegionObserverForAddingMutationsFromCoprocessors.dummy, TestRegionObserverForAddingMutationsFromCoprocessors.dummy), new Put(TestRegionObserverForAddingMutationsFromCoprocessors.row2).addColumn(TestRegionObserverForAddingMutationsFromCoprocessors.test, TestRegionObserverForAddingMutationsFromCoprocessors.dummy, TestRegionObserverForAddingMutationsFromCoprocessors.dummy), new Put(TestRegionObserverForAddingMutationsFromCoprocessors.row3).addColumn(TestRegionObserverForAddingMutationsFromCoprocessors.test, TestRegionObserverForAddingMutationsFromCoprocessors.dummy, TestRegionObserverForAddingMutationsFromCoprocessors.dummy)));
            TestRegionObserverForAddingMutationsFromCoprocessors.assertRowCount(t, 3);
            t.delete(new Delete(TestRegionObserverForAddingMutationsFromCoprocessors.test).addFamily(TestRegionObserverForAddingMutationsFromCoprocessors.test));// delete non-existing row

            TestRegionObserverForAddingMutationsFromCoprocessors.assertRowCount(t, 1);
        }
    }

    @Test
    public void testDeleteRow() throws Exception {
        createTable(TestRegionObserverForAddingMutationsFromCoprocessors.TestDeleteRowCoprocessor.class.getName());
        try (Table t = TestRegionObserverForAddingMutationsFromCoprocessors.util.getConnection().getTable(tableName)) {
            t.put(Lists.newArrayList(new Put(TestRegionObserverForAddingMutationsFromCoprocessors.row1).addColumn(TestRegionObserverForAddingMutationsFromCoprocessors.test, TestRegionObserverForAddingMutationsFromCoprocessors.dummy, TestRegionObserverForAddingMutationsFromCoprocessors.dummy), new Put(TestRegionObserverForAddingMutationsFromCoprocessors.row2).addColumn(TestRegionObserverForAddingMutationsFromCoprocessors.test, TestRegionObserverForAddingMutationsFromCoprocessors.dummy, TestRegionObserverForAddingMutationsFromCoprocessors.dummy), new Put(TestRegionObserverForAddingMutationsFromCoprocessors.row3).addColumn(TestRegionObserverForAddingMutationsFromCoprocessors.test, TestRegionObserverForAddingMutationsFromCoprocessors.dummy, TestRegionObserverForAddingMutationsFromCoprocessors.dummy)));
            TestRegionObserverForAddingMutationsFromCoprocessors.assertRowCount(t, 3);
            t.delete(new Delete(TestRegionObserverForAddingMutationsFromCoprocessors.test).addColumn(TestRegionObserverForAddingMutationsFromCoprocessors.test, TestRegionObserverForAddingMutationsFromCoprocessors.dummy));// delete non-existing row

            TestRegionObserverForAddingMutationsFromCoprocessors.assertRowCount(t, 1);
        }
    }

    @Test
    public void testPutWithTTL() throws Exception {
        createTable(TestRegionObserverForAddingMutationsFromCoprocessors.TestPutWithTTLCoprocessor.class.getName());
        try (Table t = TestRegionObserverForAddingMutationsFromCoprocessors.util.getConnection().getTable(tableName)) {
            t.put(new Put(TestRegionObserverForAddingMutationsFromCoprocessors.row1).addColumn(TestRegionObserverForAddingMutationsFromCoprocessors.test, TestRegionObserverForAddingMutationsFromCoprocessors.dummy, TestRegionObserverForAddingMutationsFromCoprocessors.dummy).setTTL(3000));
            TestRegionObserverForAddingMutationsFromCoprocessors.assertRowCount(t, 2);
            // wait long enough for the TTL to expire
            Thread.sleep(5000);
            TestRegionObserverForAddingMutationsFromCoprocessors.assertRowCount(t, 0);
        }
    }

    public static class TestPutWithTTLCoprocessor implements RegionCoprocessor , RegionObserver {
        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void preBatchMutate(ObserverContext<RegionCoprocessorEnvironment> c, MiniBatchOperationInProgress<Mutation> miniBatchOp) throws IOException {
            Mutation mut = miniBatchOp.getOperation(0);
            List<Cell> cells = mut.getFamilyCellMap().get(TestRegionObserverForAddingMutationsFromCoprocessors.test);
            Put[] puts = new Put[]{ new Put(Bytes.toBytes("cpPut")).addColumn(TestRegionObserverForAddingMutationsFromCoprocessors.test, TestRegionObserverForAddingMutationsFromCoprocessors.dummy, cells.get(0).getTimestamp(), Bytes.toBytes("cpdummy")).setTTL(mut.getTTL()) };
            TestRegionObserverForAddingMutationsFromCoprocessors.LOG.info(("Putting:" + (Arrays.toString(puts))));
            miniBatchOp.addOperationsFromCP(0, puts);
        }
    }

    public static class TestMultiMutationCoprocessor implements RegionCoprocessor , RegionObserver {
        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void preBatchMutate(ObserverContext<RegionCoprocessorEnvironment> c, MiniBatchOperationInProgress<Mutation> miniBatchOp) throws IOException {
            Mutation mut = miniBatchOp.getOperation(0);
            List<Cell> cells = mut.getFamilyCellMap().get(TestRegionObserverForAddingMutationsFromCoprocessors.test);
            Put[] puts = new Put[]{ new Put(TestRegionObserverForAddingMutationsFromCoprocessors.row1).addColumn(TestRegionObserverForAddingMutationsFromCoprocessors.test, TestRegionObserverForAddingMutationsFromCoprocessors.dummy, cells.get(0).getTimestamp(), Bytes.toBytes("cpdummy")), new Put(TestRegionObserverForAddingMutationsFromCoprocessors.row2).addColumn(TestRegionObserverForAddingMutationsFromCoprocessors.test, TestRegionObserverForAddingMutationsFromCoprocessors.dummy, cells.get(0).getTimestamp(), TestRegionObserverForAddingMutationsFromCoprocessors.dummy), new Put(TestRegionObserverForAddingMutationsFromCoprocessors.row3).addColumn(TestRegionObserverForAddingMutationsFromCoprocessors.test, TestRegionObserverForAddingMutationsFromCoprocessors.dummy, cells.get(0).getTimestamp(), TestRegionObserverForAddingMutationsFromCoprocessors.dummy) };
            TestRegionObserverForAddingMutationsFromCoprocessors.LOG.info(("Putting:" + (Arrays.toString(puts))));
            miniBatchOp.addOperationsFromCP(0, puts);
        }
    }

    public static class TestDeleteCellCoprocessor implements RegionCoprocessor , RegionObserver {
        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void preBatchMutate(ObserverContext<RegionCoprocessorEnvironment> c, MiniBatchOperationInProgress<Mutation> miniBatchOp) throws IOException {
            Mutation mut = miniBatchOp.getOperation(0);
            if (mut instanceof Delete) {
                List<Cell> cells = mut.getFamilyCellMap().get(TestRegionObserverForAddingMutationsFromCoprocessors.test);
                Delete[] deletes = new Delete[]{ // delete only 2 rows
                new Delete(TestRegionObserverForAddingMutationsFromCoprocessors.row1).addColumns(TestRegionObserverForAddingMutationsFromCoprocessors.test, TestRegionObserverForAddingMutationsFromCoprocessors.dummy, cells.get(0).getTimestamp()), new Delete(TestRegionObserverForAddingMutationsFromCoprocessors.row2).addColumns(TestRegionObserverForAddingMutationsFromCoprocessors.test, TestRegionObserverForAddingMutationsFromCoprocessors.dummy, cells.get(0).getTimestamp()) };
                TestRegionObserverForAddingMutationsFromCoprocessors.LOG.info(("Deleting:" + (Arrays.toString(deletes))));
                miniBatchOp.addOperationsFromCP(0, deletes);
            }
        }
    }

    public static class TestDeleteFamilyCoprocessor implements RegionCoprocessor , RegionObserver {
        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void preBatchMutate(ObserverContext<RegionCoprocessorEnvironment> c, MiniBatchOperationInProgress<Mutation> miniBatchOp) throws IOException {
            Mutation mut = miniBatchOp.getOperation(0);
            if (mut instanceof Delete) {
                List<Cell> cells = mut.getFamilyCellMap().get(TestRegionObserverForAddingMutationsFromCoprocessors.test);
                Delete[] deletes = new Delete[]{ // delete only 2 rows
                new Delete(TestRegionObserverForAddingMutationsFromCoprocessors.row1).addFamily(TestRegionObserverForAddingMutationsFromCoprocessors.test, cells.get(0).getTimestamp()), new Delete(TestRegionObserverForAddingMutationsFromCoprocessors.row2).addFamily(TestRegionObserverForAddingMutationsFromCoprocessors.test, cells.get(0).getTimestamp()) };
                TestRegionObserverForAddingMutationsFromCoprocessors.LOG.info(("Deleting:" + (Arrays.toString(deletes))));
                miniBatchOp.addOperationsFromCP(0, deletes);
            }
        }
    }

    public static class TestDeleteRowCoprocessor implements RegionCoprocessor , RegionObserver {
        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void preBatchMutate(ObserverContext<RegionCoprocessorEnvironment> c, MiniBatchOperationInProgress<Mutation> miniBatchOp) throws IOException {
            Mutation mut = miniBatchOp.getOperation(0);
            if (mut instanceof Delete) {
                List<Cell> cells = mut.getFamilyCellMap().get(TestRegionObserverForAddingMutationsFromCoprocessors.test);
                Delete[] deletes = new Delete[]{ // delete only 2 rows
                new Delete(TestRegionObserverForAddingMutationsFromCoprocessors.row1, cells.get(0).getTimestamp()), new Delete(TestRegionObserverForAddingMutationsFromCoprocessors.row2, cells.get(0).getTimestamp()) };
                TestRegionObserverForAddingMutationsFromCoprocessors.LOG.info(("Deleting:" + (Arrays.toString(deletes))));
                miniBatchOp.addOperationsFromCP(0, deletes);
            }
        }
    }

    public static class TestWALObserver implements WALCoprocessor , WALObserver {
        static WALEdit savedEdit = null;

        @Override
        public Optional<WALObserver> getWALObserver() {
            return Optional.of(this);
        }

        @Override
        public void postWALWrite(ObserverContext<? extends WALCoprocessorEnvironment> ctx, RegionInfo info, WALKey logKey, WALEdit logEdit) throws IOException {
            if (info.getTable().equals(TableName.valueOf("testCPMutationsAreWrittenToWALEdit"))) {
                TestRegionObserverForAddingMutationsFromCoprocessors.TestWALObserver.savedEdit = logEdit;
            }
        }
    }
}

