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


import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.RegionLocations;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.regionserver.InternalScanner;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Threads;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MediumTests.class, ClientTests.class })
public class TestAsyncNonMetaRegionLocatorConcurrenyLimit {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncNonMetaRegionLocatorConcurrenyLimit.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NAME = TableName.valueOf("async");

    private static byte[] FAMILY = Bytes.toBytes("cf");

    private static AsyncConnectionImpl CONN;

    private static AsyncNonMetaRegionLocator LOCATOR;

    private static byte[][] SPLIT_KEYS;

    private static int MAX_ALLOWED = 2;

    private static AtomicInteger CONCURRENCY = new AtomicInteger(0);

    private static AtomicInteger MAX_CONCURRENCY = new AtomicInteger(0);

    public static final class CountingRegionObserver implements RegionCoprocessor , RegionObserver {
        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public boolean preScannerNext(ObserverContext<RegionCoprocessorEnvironment> c, InternalScanner s, List<Result> result, int limit, boolean hasNext) throws IOException {
            if (c.getEnvironment().getRegionInfo().isMetaRegion()) {
                int concurrency = TestAsyncNonMetaRegionLocatorConcurrenyLimit.CONCURRENCY.incrementAndGet();
                for (; ;) {
                    int max = TestAsyncNonMetaRegionLocatorConcurrenyLimit.MAX_CONCURRENCY.get();
                    if (concurrency <= max) {
                        break;
                    }
                    if (TestAsyncNonMetaRegionLocatorConcurrenyLimit.MAX_CONCURRENCY.compareAndSet(max, concurrency)) {
                        break;
                    }
                }
                Threads.sleepWithoutInterrupt(10);
            }
            return hasNext;
        }

        @Override
        public boolean postScannerNext(ObserverContext<RegionCoprocessorEnvironment> c, InternalScanner s, List<Result> result, int limit, boolean hasNext) throws IOException {
            if (c.getEnvironment().getRegionInfo().isMetaRegion()) {
                TestAsyncNonMetaRegionLocatorConcurrenyLimit.CONCURRENCY.decrementAndGet();
            }
            return hasNext;
        }
    }

    @Test
    public void test() throws InterruptedException, ExecutionException {
        List<CompletableFuture<RegionLocations>> futures = IntStream.range(0, 256).mapToObj(( i) -> Bytes.toBytes(String.format("%02x", i))).map(( r) -> TestAsyncNonMetaRegionLocatorConcurrenyLimit.LOCATOR.getRegionLocations(TestAsyncNonMetaRegionLocatorConcurrenyLimit.TABLE_NAME, r, RegionReplicaUtil.DEFAULT_REPLICA_ID, RegionLocateType.CURRENT, false)).collect(Collectors.toList());
        assertLocs(futures);
        Assert.assertTrue(((("max allowed is " + (TestAsyncNonMetaRegionLocatorConcurrenyLimit.MAX_ALLOWED)) + " but actual is ") + (TestAsyncNonMetaRegionLocatorConcurrenyLimit.MAX_CONCURRENCY.get())), ((TestAsyncNonMetaRegionLocatorConcurrenyLimit.MAX_CONCURRENCY.get()) <= (TestAsyncNonMetaRegionLocatorConcurrenyLimit.MAX_ALLOWED)));
    }
}

