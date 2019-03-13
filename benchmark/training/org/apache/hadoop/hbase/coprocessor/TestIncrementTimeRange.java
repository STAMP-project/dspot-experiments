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
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.io.TimeRange;
import org.apache.hadoop.hbase.testclassification.CoprocessorTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.ManualEnvironmentEdge;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * This test runs batch mutation with Increments which have custom TimeRange.
 * Custom Observer records the TimeRange.
 * We then verify that the recorded TimeRange has same bounds as the initial TimeRange.
 * See HBASE-15698
 */
@Category({ CoprocessorTests.class, MediumTests.class })
public class TestIncrementTimeRange {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestIncrementTimeRange.class);

    private static final HBaseTestingUtility util = new HBaseTestingUtility();

    private static ManualEnvironmentEdge mee = new ManualEnvironmentEdge();

    private static final TableName TEST_TABLE = TableName.valueOf("test");

    private static final byte[] TEST_FAMILY = Bytes.toBytes("f1");

    private static final byte[] ROW_A = Bytes.toBytes("aaa");

    private static final byte[] ROW_B = Bytes.toBytes("bbb");

    private static final byte[] ROW_C = Bytes.toBytes("ccc");

    private static final byte[] qualifierCol1 = Bytes.toBytes("col1");

    private static final byte[] bytes1 = Bytes.toBytes(1);

    private static final byte[] bytes2 = Bytes.toBytes(2);

    private static final byte[] bytes3 = Bytes.toBytes(3);

    private Table hTableInterface;

    private Table table;

    public static class MyObserver extends SimpleRegionObserver {
        static TimeRange tr10 = null;

        static TimeRange tr2 = null;

        @Override
        public Result preIncrement(final ObserverContext<RegionCoprocessorEnvironment> e, final Increment increment) throws IOException {
            NavigableMap<byte[], List<Cell>> map = increment.getFamilyCellMap();
            for (Map.Entry<byte[], List<Cell>> entry : map.entrySet()) {
                for (Cell cell : entry.getValue()) {
                    long incr = Bytes.toLong(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                    if (incr == 10) {
                        TestIncrementTimeRange.MyObserver.tr10 = increment.getTimeRange();
                    } else
                        if ((incr == 2) && (!(increment.getTimeRange().isAllTime()))) {
                            TestIncrementTimeRange.MyObserver.tr2 = increment.getTimeRange();
                        }

                }
            }
            return super.preIncrement(e, increment);
        }
    }

    @Test
    public void testHTableInterfaceMethods() throws Exception {
        hTableInterface = TestIncrementTimeRange.util.getConnection().getTable(TestIncrementTimeRange.TEST_TABLE);
        checkHTableInterfaceMethods();
    }
}

