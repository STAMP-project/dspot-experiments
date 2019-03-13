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


import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Threads;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MediumTests.class, ClientTests.class })
public class TestAsyncTableScanRenewLease {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncTableScanRenewLease.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NAME = TableName.valueOf("async");

    private static byte[] FAMILY = Bytes.toBytes("cf");

    private static byte[] CQ = Bytes.toBytes("cq");

    private static AsyncConnection CONN;

    private static AsyncTable<AdvancedScanResultConsumer> TABLE;

    private static int SCANNER_LEASE_TIMEOUT_PERIOD_MS = 5000;

    private static final class RenewLeaseConsumer implements AdvancedScanResultConsumer {
        private final List<Result> results = new ArrayList<>();

        private Throwable error;

        private boolean finished = false;

        private boolean suspended = false;

        @Override
        public synchronized void onNext(Result[] results, ScanController controller) {
            for (Result result : results) {
                this.results.add(result);
            }
            if (!(suspended)) {
                ScanResumer resumer = controller.suspend();
                new Thread(() -> {
                    Threads.sleep((2 * (TestAsyncTableScanRenewLease.SCANNER_LEASE_TIMEOUT_PERIOD_MS)));
                    try {
                        TestAsyncTableScanRenewLease.TABLE.put(new Put(Bytes.toBytes(String.format("%02d", 10))).addColumn(TestAsyncTableScanRenewLease.FAMILY, TestAsyncTableScanRenewLease.CQ, Bytes.toBytes(10))).get();
                    } catch (Exception e) {
                        onError(e);
                    }
                    resumer.resume();
                }).start();
            }
        }

        @Override
        public synchronized void onError(Throwable error) {
            this.finished = true;
            this.error = error;
            notifyAll();
        }

        @Override
        public synchronized void onComplete() {
            this.finished = true;
            notifyAll();
        }

        public synchronized List<Result> get() throws Throwable {
            while (!(finished)) {
                wait();
            } 
            if ((error) != null) {
                throw error;
            }
            return results;
        }
    }

    @Test
    public void test() throws Throwable {
        TestAsyncTableScanRenewLease.RenewLeaseConsumer consumer = new TestAsyncTableScanRenewLease.RenewLeaseConsumer();
        TestAsyncTableScanRenewLease.TABLE.scan(new Scan(), consumer);
        List<Result> results = consumer.get();
        // should not see the newly added value
        Assert.assertEquals(10, results.size());
        IntStream.range(0, 10).forEach(( i) -> {
            Result result = results.get(i);
            Assert.assertEquals(String.format("%02d", i), Bytes.toString(result.getRow()));
            Assert.assertEquals(i, Bytes.toInt(result.getValue(TestAsyncTableScanRenewLease.FAMILY, TestAsyncTableScanRenewLease.CQ)));
        });
        // now we can see the newly added value
        List<Result> results2 = TestAsyncTableScanRenewLease.TABLE.scanAll(new Scan()).get();
        Assert.assertEquals(11, results2.size());
        IntStream.range(0, 11).forEach(( i) -> {
            Result result = results2.get(i);
            Assert.assertEquals(String.format("%02d", i), Bytes.toString(result.getRow()));
            Assert.assertEquals(i, Bytes.toInt(result.getValue(TestAsyncTableScanRenewLease.FAMILY, TestAsyncTableScanRenewLease.CQ)));
        });
    }
}

