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
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Append;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.ByteArrayComparable;
import org.apache.hadoop.hbase.testclassification.CoprocessorTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


@Category({ CoprocessorTests.class, MediumTests.class })
public class TestPassCustomCellViaRegionObserver {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestPassCustomCellViaRegionObserver.class);

    @Rule
    public TestName testName = new TestName();

    private TableName tableName;

    private Table table = null;

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final byte[] ROW = Bytes.toBytes("ROW");

    private static final byte[] FAMILY = Bytes.toBytes("FAMILY");

    private static final byte[] QUALIFIER = Bytes.toBytes("QUALIFIER");

    private static final byte[] VALUE = Bytes.toBytes(10L);

    private static final byte[] APPEND_VALUE = Bytes.toBytes("MB");

    private static final byte[] QUALIFIER_FROM_CP = Bytes.toBytes("QUALIFIER_FROM_CP");

    @Test
    public void testMutation() throws Exception {
        Put put = new Put(TestPassCustomCellViaRegionObserver.ROW);
        put.addColumn(TestPassCustomCellViaRegionObserver.FAMILY, TestPassCustomCellViaRegionObserver.QUALIFIER, TestPassCustomCellViaRegionObserver.VALUE);
        table.put(put);
        byte[] value = TestPassCustomCellViaRegionObserver.VALUE;
        TestPassCustomCellViaRegionObserver.assertResult(table.get(new Get(TestPassCustomCellViaRegionObserver.ROW)), value, value);
        TestPassCustomCellViaRegionObserver.assertObserverHasExecuted();
        Increment inc = new Increment(TestPassCustomCellViaRegionObserver.ROW);
        inc.addColumn(TestPassCustomCellViaRegionObserver.FAMILY, TestPassCustomCellViaRegionObserver.QUALIFIER, 10L);
        table.increment(inc);
        // QUALIFIER -> 10 (put) + 10 (increment)
        // QUALIFIER_FROM_CP -> 10 (from cp's put) + 10 (from cp's increment)
        value = Bytes.toBytes(20L);
        TestPassCustomCellViaRegionObserver.assertResult(table.get(new Get(TestPassCustomCellViaRegionObserver.ROW)), value, value);
        TestPassCustomCellViaRegionObserver.assertObserverHasExecuted();
        Append append = new Append(TestPassCustomCellViaRegionObserver.ROW);
        append.addColumn(TestPassCustomCellViaRegionObserver.FAMILY, TestPassCustomCellViaRegionObserver.QUALIFIER, TestPassCustomCellViaRegionObserver.APPEND_VALUE);
        table.append(append);
        // 10L + "MB"
        value = ByteBuffer.wrap(new byte[(value.length) + (TestPassCustomCellViaRegionObserver.APPEND_VALUE.length)]).put(value).put(TestPassCustomCellViaRegionObserver.APPEND_VALUE).array();
        TestPassCustomCellViaRegionObserver.assertResult(table.get(new Get(TestPassCustomCellViaRegionObserver.ROW)), value, value);
        TestPassCustomCellViaRegionObserver.assertObserverHasExecuted();
        Delete delete = new Delete(TestPassCustomCellViaRegionObserver.ROW);
        delete.addColumns(TestPassCustomCellViaRegionObserver.FAMILY, TestPassCustomCellViaRegionObserver.QUALIFIER);
        table.delete(delete);
        Assert.assertTrue(Arrays.asList(table.get(new Get(TestPassCustomCellViaRegionObserver.ROW)).rawCells()).toString(), table.get(new Get(TestPassCustomCellViaRegionObserver.ROW)).isEmpty());
        TestPassCustomCellViaRegionObserver.assertObserverHasExecuted();
        Assert.assertTrue(table.checkAndPut(TestPassCustomCellViaRegionObserver.ROW, TestPassCustomCellViaRegionObserver.FAMILY, TestPassCustomCellViaRegionObserver.QUALIFIER, null, put));
        TestPassCustomCellViaRegionObserver.assertObserverHasExecuted();
        Assert.assertTrue(table.checkAndDelete(TestPassCustomCellViaRegionObserver.ROW, TestPassCustomCellViaRegionObserver.FAMILY, TestPassCustomCellViaRegionObserver.QUALIFIER, TestPassCustomCellViaRegionObserver.VALUE, delete));
        TestPassCustomCellViaRegionObserver.assertObserverHasExecuted();
        Assert.assertTrue(table.get(new Get(TestPassCustomCellViaRegionObserver.ROW)).isEmpty());
    }

    @Test
    public void testMultiPut() throws Exception {
        List<Put> puts = IntStream.range(0, 10).mapToObj(( i) -> new Put(TestPassCustomCellViaRegionObserver.ROW).addColumn(TestPassCustomCellViaRegionObserver.FAMILY, Bytes.toBytes(i), TestPassCustomCellViaRegionObserver.VALUE)).collect(Collectors.toList());
        table.put(puts);
        TestPassCustomCellViaRegionObserver.assertResult(table.get(new Get(TestPassCustomCellViaRegionObserver.ROW)), TestPassCustomCellViaRegionObserver.VALUE);
        TestPassCustomCellViaRegionObserver.assertObserverHasExecuted();
        List<Delete> deletes = IntStream.range(0, 10).mapToObj(( i) -> new Delete(TestPassCustomCellViaRegionObserver.ROW).addColumn(TestPassCustomCellViaRegionObserver.FAMILY, Bytes.toBytes(i))).collect(Collectors.toList());
        table.delete(deletes);
        Assert.assertTrue(table.get(new Get(TestPassCustomCellViaRegionObserver.ROW)).isEmpty());
        TestPassCustomCellViaRegionObserver.assertObserverHasExecuted();
    }

    public static class RegionObserverImpl implements RegionCoprocessor , RegionObserver {
        static final AtomicInteger COUNT = new AtomicInteger(0);

        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void prePut(ObserverContext<RegionCoprocessorEnvironment> c, Put put, WALEdit edit, Durability durability) throws IOException {
            put.add(TestPassCustomCellViaRegionObserver.createCustomCell(put));
            TestPassCustomCellViaRegionObserver.RegionObserverImpl.COUNT.incrementAndGet();
        }

        @Override
        public void preDelete(ObserverContext<RegionCoprocessorEnvironment> c, Delete delete, WALEdit edit, Durability durability) throws IOException {
            delete.add(TestPassCustomCellViaRegionObserver.createCustomCell(delete));
            TestPassCustomCellViaRegionObserver.RegionObserverImpl.COUNT.incrementAndGet();
        }

        @Override
        public boolean preCheckAndPut(ObserverContext<RegionCoprocessorEnvironment> c, byte[] row, byte[] family, byte[] qualifier, CompareOperator op, ByteArrayComparable comparator, Put put, boolean result) throws IOException {
            put.add(TestPassCustomCellViaRegionObserver.createCustomCell(put));
            TestPassCustomCellViaRegionObserver.RegionObserverImpl.COUNT.incrementAndGet();
            return result;
        }

        @Override
        public boolean preCheckAndDelete(ObserverContext<RegionCoprocessorEnvironment> c, byte[] row, byte[] family, byte[] qualifier, CompareOperator op, ByteArrayComparable comparator, Delete delete, boolean result) throws IOException {
            delete.add(TestPassCustomCellViaRegionObserver.createCustomCell(delete));
            TestPassCustomCellViaRegionObserver.RegionObserverImpl.COUNT.incrementAndGet();
            return result;
        }

        @Override
        public Result preAppend(ObserverContext<RegionCoprocessorEnvironment> c, Append append) throws IOException {
            append.add(TestPassCustomCellViaRegionObserver.createCustomCell(append));
            TestPassCustomCellViaRegionObserver.RegionObserverImpl.COUNT.incrementAndGet();
            return null;
        }

        @Override
        public Result preIncrement(ObserverContext<RegionCoprocessorEnvironment> c, Increment increment) throws IOException {
            increment.add(TestPassCustomCellViaRegionObserver.createCustomCell(increment));
            TestPassCustomCellViaRegionObserver.RegionObserverImpl.COUNT.incrementAndGet();
            return null;
        }
    }
}

