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


import RegionInfoBuilder.FIRST_META_REGIONINFO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.RetryCounter;
import org.apache.hadoop.hbase.util.Threads;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static CompactionState.NONE;


/**
 * Will split the table, and move region randomly when testing.
 */
@Category({ LargeTests.class, ClientTests.class })
public class TestAsyncTableGetMultiThreaded {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncTableGetMultiThreaded.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NAME = TableName.valueOf("async");

    private static byte[] FAMILY = Bytes.toBytes("cf");

    private static byte[] QUALIFIER = Bytes.toBytes("cq");

    private static int COUNT = 1000;

    private static AsyncConnection CONN;

    private static AsyncTable<?> TABLE;

    private static byte[][] SPLIT_KEYS;

    @Test
    public void test() throws Exception {
        int numThreads = 20;
        AtomicBoolean stop = new AtomicBoolean(false);
        ExecutorService executor = Executors.newFixedThreadPool(numThreads, Threads.newDaemonThreadFactory("TestAsyncGet-"));
        List<Future<?>> futures = new ArrayList<>();
        IntStream.range(0, numThreads).forEach(( i) -> futures.add(executor.submit(() -> {
            run(stop);
            return null;
        })));
        Collections.shuffle(Arrays.asList(TestAsyncTableGetMultiThreaded.SPLIT_KEYS), new Random(123));
        Admin admin = TestAsyncTableGetMultiThreaded.TEST_UTIL.getAdmin();
        for (byte[] splitPoint : TestAsyncTableGetMultiThreaded.SPLIT_KEYS) {
            int oldRegionCount = admin.getRegions(TestAsyncTableGetMultiThreaded.TABLE_NAME).size();
            admin.split(TestAsyncTableGetMultiThreaded.TABLE_NAME, splitPoint);
            waitFor(30000, new org.apache.hadoop.hbase.Waiter.ExplainingPredicate<Exception>() {
                @Override
                public boolean evaluate() throws Exception {
                    return (TestAsyncTableGetMultiThreaded.TEST_UTIL.getMiniHBaseCluster().getRegions(TestAsyncTableGetMultiThreaded.TABLE_NAME).size()) > oldRegionCount;
                }

                @Override
                public String explainFailure() throws Exception {
                    return "Split has not finished yet";
                }
            });
            for (HRegion region : TestAsyncTableGetMultiThreaded.TEST_UTIL.getHBaseCluster().getRegions(TestAsyncTableGetMultiThreaded.TABLE_NAME)) {
                region.compact(true);
                // Waiting for compaction to complete and references are cleaned up
                RetryCounter retrier = new RetryCounter(30, 1, TimeUnit.SECONDS);
                while (((NONE) != (admin.getCompactionStateForRegion(region.getRegionInfo().getRegionName()))) && (retrier.shouldRetry())) {
                    retrier.sleepUntilNextRetry();
                } 
                region.getStores().get(0).closeAndArchiveCompactedFiles();
            }
            Thread.sleep(5000);
            admin.balance(true);
            Thread.sleep(5000);
            ServerName metaServer = TestAsyncTableGetMultiThreaded.TEST_UTIL.getHBaseCluster().getServerHoldingMeta();
            ServerName newMetaServer = TestAsyncTableGetMultiThreaded.TEST_UTIL.getHBaseCluster().getRegionServerThreads().stream().map(( t) -> t.getRegionServer().getServerName()).filter(( s) -> !(s.equals(metaServer))).findAny().get();
            admin.move(FIRST_META_REGIONINFO.getEncodedNameAsBytes(), Bytes.toBytes(newMetaServer.getServerName()));
            Thread.sleep(5000);
        }
        stop.set(true);
        executor.shutdown();
        for (Future<?> future : futures) {
            future.get();
        }
    }
}

