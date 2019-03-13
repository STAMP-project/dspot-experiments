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


import CellBuilderType.SHALLOW_COPY;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.ExtendedCellBuilderFactory;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Append;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TestFromClientSide;
import org.apache.hadoop.hbase.regionserver.NoSuchColumnFamilyException;
import org.apache.hadoop.hbase.testclassification.CoprocessorTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test coprocessor methods
 * {@link RegionObserver#postIncrementBeforeWAL(ObserverContext, Mutation, List)} and
 * {@link RegionObserver#postAppendBeforeWAL(ObserverContext, Mutation, List)}. These methods may
 * change the cells which will be applied to memstore and WAL. So add unit test for the case which
 * change the cell's column family.
 */
@Category({ CoprocessorTests.class, MediumTests.class })
public class TestPostIncrementAndAppendBeforeWAL {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestPostIncrementAndAppendBeforeWAL.class);

    @Rule
    public TestName name = new TestName();

    private static final Logger LOG = LoggerFactory.getLogger(TestFromClientSide.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static Connection connection;

    private static final byte[] ROW = Bytes.toBytes("row");

    private static final String CF1 = "cf1";

    private static final byte[] CF1_BYTES = Bytes.toBytes(TestPostIncrementAndAppendBeforeWAL.CF1);

    private static final String CF2 = "cf2";

    private static final byte[] CF2_BYTES = Bytes.toBytes(TestPostIncrementAndAppendBeforeWAL.CF2);

    private static final String CF_NOT_EXIST = "cf_not_exist";

    private static final byte[] CF_NOT_EXIST_BYTES = Bytes.toBytes(TestPostIncrementAndAppendBeforeWAL.CF_NOT_EXIST);

    private static final byte[] CQ1 = Bytes.toBytes("cq1");

    private static final byte[] CQ2 = Bytes.toBytes("cq2");

    private static final byte[] VALUE = Bytes.toBytes("value");

    @Test
    public void testChangeCellWithDifferntColumnFamily() throws Exception {
        TableName tableName = TableName.valueOf(name.getMethodName());
        createTableWithCoprocessor(tableName, TestPostIncrementAndAppendBeforeWAL.ChangeCellWithDifferntColumnFamilyObserver.class.getName());
        try (Table table = TestPostIncrementAndAppendBeforeWAL.connection.getTable(tableName)) {
            Increment increment = new Increment(TestPostIncrementAndAppendBeforeWAL.ROW).addColumn(TestPostIncrementAndAppendBeforeWAL.CF1_BYTES, TestPostIncrementAndAppendBeforeWAL.CQ1, 1);
            table.increment(increment);
            Get get = new Get(TestPostIncrementAndAppendBeforeWAL.ROW).addColumn(TestPostIncrementAndAppendBeforeWAL.CF2_BYTES, TestPostIncrementAndAppendBeforeWAL.CQ1);
            Result result = table.get(get);
            Assert.assertEquals(1, result.size());
            Assert.assertEquals(1, Bytes.toLong(result.getValue(TestPostIncrementAndAppendBeforeWAL.CF2_BYTES, TestPostIncrementAndAppendBeforeWAL.CQ1)));
            Append append = new Append(TestPostIncrementAndAppendBeforeWAL.ROW).addColumn(TestPostIncrementAndAppendBeforeWAL.CF1_BYTES, TestPostIncrementAndAppendBeforeWAL.CQ2, TestPostIncrementAndAppendBeforeWAL.VALUE);
            table.append(append);
            get = new Get(TestPostIncrementAndAppendBeforeWAL.ROW).addColumn(TestPostIncrementAndAppendBeforeWAL.CF2_BYTES, TestPostIncrementAndAppendBeforeWAL.CQ2);
            result = table.get(get);
            Assert.assertEquals(1, result.size());
            Assert.assertTrue(Bytes.equals(TestPostIncrementAndAppendBeforeWAL.VALUE, result.getValue(TestPostIncrementAndAppendBeforeWAL.CF2_BYTES, TestPostIncrementAndAppendBeforeWAL.CQ2)));
        }
    }

    @Test
    public void testChangeCellWithNotExistColumnFamily() throws Exception {
        TableName tableName = TableName.valueOf(name.getMethodName());
        createTableWithCoprocessor(tableName, TestPostIncrementAndAppendBeforeWAL.ChangeCellWithNotExistColumnFamilyObserver.class.getName());
        try (Table table = TestPostIncrementAndAppendBeforeWAL.connection.getTable(tableName)) {
            try {
                Increment increment = new Increment(TestPostIncrementAndAppendBeforeWAL.ROW).addColumn(TestPostIncrementAndAppendBeforeWAL.CF1_BYTES, TestPostIncrementAndAppendBeforeWAL.CQ1, 1);
                table.increment(increment);
                Assert.fail("should throw NoSuchColumnFamilyException");
            } catch (Exception e) {
                Assert.assertTrue((e instanceof NoSuchColumnFamilyException));
            }
            try {
                Append append = new Append(TestPostIncrementAndAppendBeforeWAL.ROW).addColumn(TestPostIncrementAndAppendBeforeWAL.CF1_BYTES, TestPostIncrementAndAppendBeforeWAL.CQ2, TestPostIncrementAndAppendBeforeWAL.VALUE);
                table.append(append);
                Assert.fail("should throw NoSuchColumnFamilyException");
            } catch (Exception e) {
                Assert.assertTrue((e instanceof NoSuchColumnFamilyException));
            }
        }
    }

    public static class ChangeCellWithDifferntColumnFamilyObserver implements RegionCoprocessor , RegionObserver {
        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public List<Pair<Cell, Cell>> postIncrementBeforeWAL(ObserverContext<RegionCoprocessorEnvironment> ctx, Mutation mutation, List<Pair<Cell, Cell>> cellPairs) throws IOException {
            return cellPairs.stream().map(( pair) -> new Pair<>(pair.getFirst(), newCellWithDifferentColumnFamily(pair.getSecond()))).collect(Collectors.toList());
        }

        private Cell newCellWithDifferentColumnFamily(Cell cell) {
            return ExtendedCellBuilderFactory.create(SHALLOW_COPY).setRow(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength()).setFamily(TestPostIncrementAndAppendBeforeWAL.CF2_BYTES, 0, TestPostIncrementAndAppendBeforeWAL.CF2_BYTES.length).setQualifier(CellUtil.cloneQualifier(cell)).setTimestamp(cell.getTimestamp()).setType(cell.getType().getCode()).setValue(CellUtil.cloneValue(cell)).build();
        }

        @Override
        public List<Pair<Cell, Cell>> postAppendBeforeWAL(ObserverContext<RegionCoprocessorEnvironment> ctx, Mutation mutation, List<Pair<Cell, Cell>> cellPairs) throws IOException {
            return cellPairs.stream().map(( pair) -> new Pair<>(pair.getFirst(), newCellWithDifferentColumnFamily(pair.getSecond()))).collect(Collectors.toList());
        }
    }

    public static class ChangeCellWithNotExistColumnFamilyObserver implements RegionCoprocessor , RegionObserver {
        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public List<Pair<Cell, Cell>> postIncrementBeforeWAL(ObserverContext<RegionCoprocessorEnvironment> ctx, Mutation mutation, List<Pair<Cell, Cell>> cellPairs) throws IOException {
            return cellPairs.stream().map(( pair) -> new Pair<>(pair.getFirst(), newCellWithNotExistColumnFamily(pair.getSecond()))).collect(Collectors.toList());
        }

        private Cell newCellWithNotExistColumnFamily(Cell cell) {
            return ExtendedCellBuilderFactory.create(SHALLOW_COPY).setRow(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength()).setFamily(TestPostIncrementAndAppendBeforeWAL.CF_NOT_EXIST_BYTES, 0, TestPostIncrementAndAppendBeforeWAL.CF_NOT_EXIST_BYTES.length).setQualifier(CellUtil.cloneQualifier(cell)).setTimestamp(cell.getTimestamp()).setType(cell.getType().getCode()).setValue(CellUtil.cloneValue(cell)).build();
        }

        @Override
        public List<Pair<Cell, Cell>> postAppendBeforeWAL(ObserverContext<RegionCoprocessorEnvironment> ctx, Mutation mutation, List<Pair<Cell, Cell>> cellPairs) throws IOException {
            return cellPairs.stream().map(( pair) -> new Pair<>(pair.getFirst(), newCellWithNotExistColumnFamily(pair.getSecond()))).collect(Collectors.toList());
        }
    }
}

