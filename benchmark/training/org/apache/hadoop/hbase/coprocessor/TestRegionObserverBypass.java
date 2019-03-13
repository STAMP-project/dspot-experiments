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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import junit.framework.Assert;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.CoprocessorTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManagerTestHelper;
import org.apache.hadoop.hbase.util.IncrementingEnvironmentEdge;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ CoprocessorTests.class, MediumTests.class })
public class TestRegionObserverBypass {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionObserverBypass.class);

    private static HBaseTestingUtility util;

    private static final TableName tableName = TableName.valueOf("test");

    private static final byte[] dummy = Bytes.toBytes("dummy");

    private static final byte[] row1 = Bytes.toBytes("r1");

    private static final byte[] row2 = Bytes.toBytes("r2");

    private static final byte[] row3 = Bytes.toBytes("r3");

    private static final byte[] test = Bytes.toBytes("test");

    /**
     * do a single put that is bypassed by a RegionObserver
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSimple() throws Exception {
        Table t = TestRegionObserverBypass.util.getConnection().getTable(TestRegionObserverBypass.tableName);
        Put p = new Put(TestRegionObserverBypass.row1);
        p.addColumn(TestRegionObserverBypass.test, TestRegionObserverBypass.dummy, TestRegionObserverBypass.dummy);
        // before HBASE-4331, this would throw an exception
        t.put(p);
        checkRowAndDelete(t, TestRegionObserverBypass.row1, 0);
        t.close();
    }

    /**
     * Test various multiput operations.
     * If the column family is 'test', then bypass is invoked.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMulti() throws Exception {
        // ensure that server time increments every time we do an operation, otherwise
        // previous deletes will eclipse successive puts having the same timestamp
        EnvironmentEdgeManagerTestHelper.injectEdge(new IncrementingEnvironmentEdge());
        Table t = TestRegionObserverBypass.util.getConnection().getTable(TestRegionObserverBypass.tableName);
        List<Put> puts = new ArrayList<>();
        Put p = new Put(TestRegionObserverBypass.row1);
        p.addColumn(TestRegionObserverBypass.dummy, TestRegionObserverBypass.dummy, TestRegionObserverBypass.dummy);
        puts.add(p);
        p = new Put(TestRegionObserverBypass.row2);
        p.addColumn(TestRegionObserverBypass.test, TestRegionObserverBypass.dummy, TestRegionObserverBypass.dummy);
        puts.add(p);
        p = new Put(TestRegionObserverBypass.row3);
        p.addColumn(TestRegionObserverBypass.test, TestRegionObserverBypass.dummy, TestRegionObserverBypass.dummy);
        puts.add(p);
        // before HBASE-4331, this would throw an exception
        t.put(puts);
        checkRowAndDelete(t, TestRegionObserverBypass.row1, 1);
        checkRowAndDelete(t, TestRegionObserverBypass.row2, 0);
        checkRowAndDelete(t, TestRegionObserverBypass.row3, 0);
        puts.clear();
        p = new Put(TestRegionObserverBypass.row1);
        p.addColumn(TestRegionObserverBypass.test, TestRegionObserverBypass.dummy, TestRegionObserverBypass.dummy);
        puts.add(p);
        p = new Put(TestRegionObserverBypass.row2);
        p.addColumn(TestRegionObserverBypass.test, TestRegionObserverBypass.dummy, TestRegionObserverBypass.dummy);
        puts.add(p);
        p = new Put(TestRegionObserverBypass.row3);
        p.addColumn(TestRegionObserverBypass.test, TestRegionObserverBypass.dummy, TestRegionObserverBypass.dummy);
        puts.add(p);
        // before HBASE-4331, this would throw an exception
        t.put(puts);
        checkRowAndDelete(t, TestRegionObserverBypass.row1, 0);
        checkRowAndDelete(t, TestRegionObserverBypass.row2, 0);
        checkRowAndDelete(t, TestRegionObserverBypass.row3, 0);
        puts.clear();
        p = new Put(TestRegionObserverBypass.row1);
        p.addColumn(TestRegionObserverBypass.test, TestRegionObserverBypass.dummy, TestRegionObserverBypass.dummy);
        puts.add(p);
        p = new Put(TestRegionObserverBypass.row2);
        p.addColumn(TestRegionObserverBypass.test, TestRegionObserverBypass.dummy, TestRegionObserverBypass.dummy);
        puts.add(p);
        p = new Put(TestRegionObserverBypass.row3);
        p.addColumn(TestRegionObserverBypass.dummy, TestRegionObserverBypass.dummy, TestRegionObserverBypass.dummy);
        puts.add(p);
        // this worked fine even before HBASE-4331
        t.put(puts);
        checkRowAndDelete(t, TestRegionObserverBypass.row1, 0);
        checkRowAndDelete(t, TestRegionObserverBypass.row2, 0);
        checkRowAndDelete(t, TestRegionObserverBypass.row3, 1);
        puts.clear();
        p = new Put(TestRegionObserverBypass.row1);
        p.addColumn(TestRegionObserverBypass.dummy, TestRegionObserverBypass.dummy, TestRegionObserverBypass.dummy);
        puts.add(p);
        p = new Put(TestRegionObserverBypass.row2);
        p.addColumn(TestRegionObserverBypass.test, TestRegionObserverBypass.dummy, TestRegionObserverBypass.dummy);
        puts.add(p);
        p = new Put(TestRegionObserverBypass.row3);
        p.addColumn(TestRegionObserverBypass.dummy, TestRegionObserverBypass.dummy, TestRegionObserverBypass.dummy);
        puts.add(p);
        // this worked fine even before HBASE-4331
        t.put(puts);
        checkRowAndDelete(t, TestRegionObserverBypass.row1, 1);
        checkRowAndDelete(t, TestRegionObserverBypass.row2, 0);
        checkRowAndDelete(t, TestRegionObserverBypass.row3, 1);
        puts.clear();
        p = new Put(TestRegionObserverBypass.row1);
        p.addColumn(TestRegionObserverBypass.test, TestRegionObserverBypass.dummy, TestRegionObserverBypass.dummy);
        puts.add(p);
        p = new Put(TestRegionObserverBypass.row2);
        p.addColumn(TestRegionObserverBypass.dummy, TestRegionObserverBypass.dummy, TestRegionObserverBypass.dummy);
        puts.add(p);
        p = new Put(TestRegionObserverBypass.row3);
        p.addColumn(TestRegionObserverBypass.test, TestRegionObserverBypass.dummy, TestRegionObserverBypass.dummy);
        puts.add(p);
        // before HBASE-4331, this would throw an exception
        t.put(puts);
        checkRowAndDelete(t, TestRegionObserverBypass.row1, 0);
        checkRowAndDelete(t, TestRegionObserverBypass.row2, 1);
        checkRowAndDelete(t, TestRegionObserverBypass.row3, 0);
        t.close();
        EnvironmentEdgeManager.reset();
    }

    /**
     * Test that when bypass is called, we skip out calling any other coprocessors stacked up method,
     * in this case, a prePut.
     * If the column family is 'test', then bypass is invoked.
     */
    @Test
    public void testBypassAlsoCompletes() throws IOException {
        // ensure that server time increments every time we do an operation, otherwise
        // previous deletes will eclipse successive puts having the same timestamp
        EnvironmentEdgeManagerTestHelper.injectEdge(new IncrementingEnvironmentEdge());
        Table t = TestRegionObserverBypass.util.getConnection().getTable(TestRegionObserverBypass.tableName);
        List<Put> puts = new ArrayList<>();
        Put p = new Put(TestRegionObserverBypass.row1);
        p.addColumn(TestRegionObserverBypass.dummy, TestRegionObserverBypass.dummy, TestRegionObserverBypass.dummy);
        puts.add(p);
        p = new Put(TestRegionObserverBypass.row2);
        p.addColumn(TestRegionObserverBypass.test, TestRegionObserverBypass.dummy, TestRegionObserverBypass.dummy);
        puts.add(p);
        p = new Put(TestRegionObserverBypass.row3);
        p.addColumn(TestRegionObserverBypass.test, TestRegionObserverBypass.dummy, TestRegionObserverBypass.dummy);
        puts.add(p);
        t.put(puts);
        // Ensure expected result.
        checkRowAndDelete(t, TestRegionObserverBypass.row1, 1);
        checkRowAndDelete(t, TestRegionObserverBypass.row2, 0);
        checkRowAndDelete(t, TestRegionObserverBypass.row3, 0);
        // We have three Coprocessors stacked up on the prePut. See the beforeClass setup. We did three
        // puts above two of which bypassed. A bypass means do not call the other coprocessors in the
        // stack so for the two 'test' calls in the above, we should not have call through to all all
        // three coprocessors in the chain. So we should have:
        // 3 invocations for first put + 1 invocation + 1 bypass for second put + 1 invocation +
        // 1 bypass for the last put. Assert.
        Assert.assertEquals("Total CP invocation count", 5, TestRegionObserverBypass.TestCoprocessor.PREPUT_INVOCATIONS.get());
        Assert.assertEquals("Total CP bypasses", 2, TestRegionObserverBypass.TestCoprocessor.PREPUT_BYPASSES.get());
    }

    public static class TestCoprocessor implements RegionCoprocessor , RegionObserver {
        static AtomicInteger PREPUT_INVOCATIONS = new AtomicInteger(0);

        static AtomicInteger PREPUT_BYPASSES = new AtomicInteger(0);

        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void prePut(final ObserverContext<RegionCoprocessorEnvironment> e, final Put put, final WALEdit edit, final Durability durability) throws IOException {
            TestRegionObserverBypass.TestCoprocessor.PREPUT_INVOCATIONS.incrementAndGet();
            Map<byte[], List<Cell>> familyMap = put.getFamilyCellMap();
            if (familyMap.containsKey(TestRegionObserverBypass.test)) {
                TestRegionObserverBypass.TestCoprocessor.PREPUT_BYPASSES.incrementAndGet();
                e.bypass();
            }
        }
    }

    /**
     * Calls through to TestCoprocessor.
     */
    public static class TestCoprocessor2 extends TestRegionObserverBypass.TestCoprocessor {}

    /**
     * Calls through to TestCoprocessor.
     */
    public static class TestCoprocessor3 extends TestRegionObserverBypass.TestCoprocessor {}
}

