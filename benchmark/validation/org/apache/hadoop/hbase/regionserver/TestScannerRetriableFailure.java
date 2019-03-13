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


import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TestTableName;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ RegionServerTests.class, LargeTests.class })
public class TestScannerRetriableFailure {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestScannerRetriableFailure.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestScannerRetriableFailure.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final String FAMILY_NAME_STR = "f";

    private static final byte[] FAMILY_NAME = Bytes.toBytes(TestScannerRetriableFailure.FAMILY_NAME_STR);

    @Rule
    public TestTableName TEST_TABLE = new TestTableName();

    public static class FaultyScannerObserver implements RegionCoprocessor , RegionObserver {
        private int faults = 0;

        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public boolean preScannerNext(final ObserverContext<RegionCoprocessorEnvironment> e, final InternalScanner s, final List<Result> results, final int limit, final boolean hasMore) throws IOException {
            final TableName tableName = e.getEnvironment().getRegionInfo().getTable();
            if ((!(tableName.isSystemTable())) && ((((faults)++) % 2) == 0)) {
                TestScannerRetriableFailure.LOG.debug(((" Injecting fault in table=" + tableName) + " scanner"));
                throw new IOException("injected fault");
            }
            return hasMore;
        }
    }

    @Test
    public void testFaultyScanner() throws Exception {
        TableName tableName = TEST_TABLE.getTableName();
        Table table = TestScannerRetriableFailure.UTIL.createTable(tableName, TestScannerRetriableFailure.FAMILY_NAME);
        try {
            final int NUM_ROWS = 100;
            loadTable(table, NUM_ROWS);
            checkTableRows(table, NUM_ROWS);
        } finally {
            table.close();
        }
    }
}

