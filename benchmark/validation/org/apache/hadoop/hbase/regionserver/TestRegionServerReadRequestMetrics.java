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
package org.apache.hadoop.hbase.regionserver;


import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Append;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.RowMutations;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// Depends on Master being able to host regions. Needs fixing.
@Ignore
@Category(MediumTests.class)
public class TestRegionServerReadRequestMetrics {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionServerReadRequestMetrics.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRegionServerReadRequestMetrics.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final TableName TABLE_NAME = TableName.valueOf("test");

    private static final byte[] CF1 = Bytes.toBytes("c1");

    private static final byte[] CF2 = Bytes.toBytes("c2");

    private static final byte[] ROW1 = Bytes.toBytes("a");

    private static final byte[] ROW2 = Bytes.toBytes("b");

    private static final byte[] ROW3 = Bytes.toBytes("c");

    private static final byte[] COL1 = Bytes.toBytes("q1");

    private static final byte[] COL2 = Bytes.toBytes("q2");

    private static final byte[] COL3 = Bytes.toBytes("q3");

    private static final byte[] VAL1 = Bytes.toBytes("v1");

    private static final byte[] VAL2 = Bytes.toBytes("v2");

    private static final byte[] VAL3 = Bytes.toBytes(0L);

    private static final int MAX_TRY = 20;

    private static final int SLEEP_MS = 100;

    private static final int TTL = 1;

    private static Admin admin;

    private static Collection<ServerName> serverNames;

    private static Table table;

    private static RegionInfo regionInfo;

    private static Map<TestRegionServerReadRequestMetrics.Metric, Long> requestsMap = new HashMap<>();

    private static Map<TestRegionServerReadRequestMetrics.Metric, Long> requestsMapPrev = new HashMap<>();

    @Test
    public void testReadRequestsCountNotFiltered() throws Exception {
        int resultCount;
        Scan scan;
        Append append;
        Put put;
        Increment increment;
        Get get;
        // test for scan
        scan = new Scan();
        try (ResultScanner scanner = TestRegionServerReadRequestMetrics.table.getScanner(scan)) {
            resultCount = 0;
            for (Result ignore : scanner) {
                resultCount++;
            }
            TestRegionServerReadRequestMetrics.testReadRequests(resultCount, 3, 0);
        }
        // test for scan
        scan = new Scan(TestRegionServerReadRequestMetrics.ROW2, TestRegionServerReadRequestMetrics.ROW3);
        try (ResultScanner scanner = TestRegionServerReadRequestMetrics.table.getScanner(scan)) {
            resultCount = 0;
            for (Result ignore : scanner) {
                resultCount++;
            }
            TestRegionServerReadRequestMetrics.testReadRequests(resultCount, 1, 0);
        }
        // test for get
        get = new Get(TestRegionServerReadRequestMetrics.ROW2);
        Result result = TestRegionServerReadRequestMetrics.table.get(get);
        resultCount = (result.isEmpty()) ? 0 : 1;
        TestRegionServerReadRequestMetrics.testReadRequests(resultCount, 1, 0);
        // test for increment
        increment = new Increment(TestRegionServerReadRequestMetrics.ROW1);
        increment.addColumn(TestRegionServerReadRequestMetrics.CF1, TestRegionServerReadRequestMetrics.COL3, 1);
        result = TestRegionServerReadRequestMetrics.table.increment(increment);
        resultCount = (result.isEmpty()) ? 0 : 1;
        TestRegionServerReadRequestMetrics.testReadRequests(resultCount, 1, 0);
        // test for checkAndPut
        put = new Put(TestRegionServerReadRequestMetrics.ROW1);
        put.addColumn(TestRegionServerReadRequestMetrics.CF1, TestRegionServerReadRequestMetrics.COL2, TestRegionServerReadRequestMetrics.VAL2);
        boolean checkAndPut = TestRegionServerReadRequestMetrics.table.checkAndMutate(TestRegionServerReadRequestMetrics.ROW1, TestRegionServerReadRequestMetrics.CF1).qualifier(TestRegionServerReadRequestMetrics.COL2).ifEquals(TestRegionServerReadRequestMetrics.VAL2).thenPut(put);
        resultCount = (checkAndPut) ? 1 : 0;
        TestRegionServerReadRequestMetrics.testReadRequests(resultCount, 1, 0);
        // test for append
        append = new Append(TestRegionServerReadRequestMetrics.ROW1);
        append.addColumn(TestRegionServerReadRequestMetrics.CF1, TestRegionServerReadRequestMetrics.COL2, TestRegionServerReadRequestMetrics.VAL2);
        result = TestRegionServerReadRequestMetrics.table.append(append);
        resultCount = (result.isEmpty()) ? 0 : 1;
        TestRegionServerReadRequestMetrics.testReadRequests(resultCount, 1, 0);
        // test for checkAndMutate
        put = new Put(TestRegionServerReadRequestMetrics.ROW1);
        put.addColumn(TestRegionServerReadRequestMetrics.CF1, TestRegionServerReadRequestMetrics.COL1, TestRegionServerReadRequestMetrics.VAL1);
        RowMutations rm = new RowMutations(TestRegionServerReadRequestMetrics.ROW1);
        rm.add(put);
        boolean checkAndMutate = TestRegionServerReadRequestMetrics.table.checkAndMutate(TestRegionServerReadRequestMetrics.ROW1, TestRegionServerReadRequestMetrics.CF1).qualifier(TestRegionServerReadRequestMetrics.COL1).ifEquals(TestRegionServerReadRequestMetrics.VAL1).thenMutate(rm);
        resultCount = (checkAndMutate) ? 1 : 0;
        TestRegionServerReadRequestMetrics.testReadRequests(resultCount, 1, 0);
    }

    @Test
    public void testReadRequestsCountWithTTLExpiration() throws Exception {
        TestRegionServerReadRequestMetrics.putTTLExpiredData();
        Scan scan = new Scan();
        scan.addFamily(TestRegionServerReadRequestMetrics.CF2);
        try (ResultScanner scanner = TestRegionServerReadRequestMetrics.table.getScanner(scan)) {
            int resultCount = 0;
            for (Result ignore : scanner) {
                resultCount++;
            }
            TestRegionServerReadRequestMetrics.testReadRequests(resultCount, 2, 1);
        }
    }

    public static class ScanRegionCoprocessor implements RegionCoprocessor , RegionObserver {
        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void postOpen(ObserverContext<RegionCoprocessorEnvironment> c) {
            RegionCoprocessorEnvironment env = c.getEnvironment();
            Region region = env.getRegion();
            try {
                putData(region);
                RegionScanner scanner = region.getScanner(new Scan());
                List<Cell> result = new LinkedList<>();
                while (scanner.next(result)) {
                    result.clear();
                } 
            } catch (Exception e) {
                TestRegionServerReadRequestMetrics.LOG.warn("Got exception in coprocessor", e);
            }
        }

        private void putData(Region region) throws Exception {
            Put put = new Put(TestRegionServerReadRequestMetrics.ROW1);
            put.addColumn(TestRegionServerReadRequestMetrics.CF1, TestRegionServerReadRequestMetrics.COL1, TestRegionServerReadRequestMetrics.VAL1);
            region.put(put);
            put = new Put(TestRegionServerReadRequestMetrics.ROW2);
            put.addColumn(TestRegionServerReadRequestMetrics.CF1, TestRegionServerReadRequestMetrics.COL1, TestRegionServerReadRequestMetrics.VAL1);
            region.put(put);
            put = new Put(TestRegionServerReadRequestMetrics.ROW3);
            put.addColumn(TestRegionServerReadRequestMetrics.CF1, TestRegionServerReadRequestMetrics.COL1, TestRegionServerReadRequestMetrics.VAL1);
            region.put(put);
        }
    }

    private enum Metric {

        REGION_READ,
        SERVER_READ,
        FILTERED_REGION_READ,
        FILTERED_SERVER_READ;}
}

