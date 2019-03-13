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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static AsyncConnectionImpl.RETRY_TIMER;


@Category({ MediumTests.class, ClientTests.class })
public class TestAsyncSingleRequestRpcRetryingCaller {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncSingleRequestRpcRetryingCaller.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NAME = TableName.valueOf("async");

    private static byte[] FAMILY = Bytes.toBytes("cf");

    private static byte[] QUALIFIER = Bytes.toBytes("cq");

    private static byte[] ROW = Bytes.toBytes("row");

    private static byte[] VALUE = Bytes.toBytes("value");

    private static AsyncConnectionImpl CONN;

    @Test
    public void testRegionMove() throws IOException, InterruptedException, ExecutionException {
        // This will leave a cached entry in location cache
        HRegionLocation loc = TestAsyncSingleRequestRpcRetryingCaller.CONN.getRegionLocator(TestAsyncSingleRequestRpcRetryingCaller.TABLE_NAME).getRegionLocation(TestAsyncSingleRequestRpcRetryingCaller.ROW).get();
        int index = TestAsyncSingleRequestRpcRetryingCaller.TEST_UTIL.getHBaseCluster().getServerWith(loc.getRegion().getRegionName());
        TestAsyncSingleRequestRpcRetryingCaller.TEST_UTIL.getAdmin().move(loc.getRegion().getEncodedNameAsBytes(), Bytes.toBytes(TestAsyncSingleRequestRpcRetryingCaller.TEST_UTIL.getHBaseCluster().getRegionServer((1 - index)).getServerName().getServerName()));
        AsyncTable<?> table = TestAsyncSingleRequestRpcRetryingCaller.CONN.getTableBuilder(TestAsyncSingleRequestRpcRetryingCaller.TABLE_NAME).setRetryPause(100, TimeUnit.MILLISECONDS).setMaxRetries(30).build();
        table.put(new Put(TestAsyncSingleRequestRpcRetryingCaller.ROW).addColumn(TestAsyncSingleRequestRpcRetryingCaller.FAMILY, TestAsyncSingleRequestRpcRetryingCaller.QUALIFIER, TestAsyncSingleRequestRpcRetryingCaller.VALUE)).get();
        // move back
        TestAsyncSingleRequestRpcRetryingCaller.TEST_UTIL.getAdmin().move(loc.getRegion().getEncodedNameAsBytes(), Bytes.toBytes(loc.getServerName().getServerName()));
        Result result = table.get(new Get(TestAsyncSingleRequestRpcRetryingCaller.ROW).addColumn(TestAsyncSingleRequestRpcRetryingCaller.FAMILY, TestAsyncSingleRequestRpcRetryingCaller.QUALIFIER)).get();
        Assert.assertArrayEquals(TestAsyncSingleRequestRpcRetryingCaller.VALUE, result.getValue(TestAsyncSingleRequestRpcRetryingCaller.FAMILY, TestAsyncSingleRequestRpcRetryingCaller.QUALIFIER));
    }

    @Test
    public void testMaxRetries() throws IOException, InterruptedException {
        try {
            TestAsyncSingleRequestRpcRetryingCaller.CONN.callerFactory.single().table(TestAsyncSingleRequestRpcRetryingCaller.TABLE_NAME).row(TestAsyncSingleRequestRpcRetryingCaller.ROW).operationTimeout(1, TimeUnit.DAYS).maxAttempts(3).pause(10, TimeUnit.MILLISECONDS).action(( controller, loc, stub) -> failedFuture()).call().get();
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertThat(e.getCause(), CoreMatchers.instanceOf(RetriesExhaustedException.class));
        }
    }

    @Test
    public void testOperationTimeout() throws IOException, InterruptedException {
        long startNs = System.nanoTime();
        try {
            TestAsyncSingleRequestRpcRetryingCaller.CONN.callerFactory.single().table(TestAsyncSingleRequestRpcRetryingCaller.TABLE_NAME).row(TestAsyncSingleRequestRpcRetryingCaller.ROW).operationTimeout(1, TimeUnit.SECONDS).pause(100, TimeUnit.MILLISECONDS).maxAttempts(Integer.MAX_VALUE).action(( controller, loc, stub) -> failedFuture()).call().get();
            Assert.fail();
        } catch (ExecutionException e) {
            e.printStackTrace();
            Assert.assertThat(e.getCause(), CoreMatchers.instanceOf(RetriesExhaustedException.class));
        }
        long costNs = (System.nanoTime()) - startNs;
        Assert.assertTrue((costNs >= (TimeUnit.SECONDS.toNanos(1))));
        Assert.assertTrue((costNs < (TimeUnit.SECONDS.toNanos(2))));
    }

    @Test
    public void testLocateError() throws IOException, InterruptedException, ExecutionException {
        AtomicBoolean errorTriggered = new AtomicBoolean(false);
        AtomicInteger count = new AtomicInteger(0);
        HRegionLocation loc = TestAsyncSingleRequestRpcRetryingCaller.CONN.getRegionLocator(TestAsyncSingleRequestRpcRetryingCaller.TABLE_NAME).getRegionLocation(TestAsyncSingleRequestRpcRetryingCaller.ROW).get();
        AsyncRegionLocator mockedLocator = new AsyncRegionLocator(TestAsyncSingleRequestRpcRetryingCaller.CONN, RETRY_TIMER) {
            @Override
            CompletableFuture<HRegionLocation> getRegionLocation(TableName tableName, byte[] row, int replicaId, RegionLocateType locateType, long timeoutNs) {
                if (tableName.equals(TestAsyncSingleRequestRpcRetryingCaller.TABLE_NAME)) {
                    CompletableFuture<HRegionLocation> future = new CompletableFuture<>();
                    if ((count.getAndIncrement()) == 0) {
                        errorTriggered.set(true);
                        future.completeExceptionally(new RuntimeException("Inject error!"));
                    } else {
                        future.complete(loc);
                    }
                    return future;
                } else {
                    return super.getRegionLocation(tableName, row, replicaId, locateType, timeoutNs);
                }
            }

            @Override
            void updateCachedLocationOnError(HRegionLocation loc, Throwable exception) {
            }
        };
        try (AsyncConnectionImpl mockedConn = new AsyncConnectionImpl(TestAsyncSingleRequestRpcRetryingCaller.CONN.getConfiguration(), TestAsyncSingleRequestRpcRetryingCaller.CONN.registry, TestAsyncSingleRequestRpcRetryingCaller.CONN.registry.getClusterId().get(), User.getCurrent()) {
            @Override
            AsyncRegionLocator getLocator() {
                return mockedLocator;
            }
        }) {
            AsyncTable<?> table = mockedConn.getTableBuilder(TestAsyncSingleRequestRpcRetryingCaller.TABLE_NAME).setRetryPause(100, TimeUnit.MILLISECONDS).setMaxRetries(5).build();
            table.put(new Put(TestAsyncSingleRequestRpcRetryingCaller.ROW).addColumn(TestAsyncSingleRequestRpcRetryingCaller.FAMILY, TestAsyncSingleRequestRpcRetryingCaller.QUALIFIER, TestAsyncSingleRequestRpcRetryingCaller.VALUE)).get();
            Assert.assertTrue(errorTriggered.get());
            errorTriggered.set(false);
            count.set(0);
            Result result = table.get(new Get(TestAsyncSingleRequestRpcRetryingCaller.ROW).addColumn(TestAsyncSingleRequestRpcRetryingCaller.FAMILY, TestAsyncSingleRequestRpcRetryingCaller.QUALIFIER)).get();
            Assert.assertArrayEquals(TestAsyncSingleRequestRpcRetryingCaller.VALUE, result.getValue(TestAsyncSingleRequestRpcRetryingCaller.FAMILY, TestAsyncSingleRequestRpcRetryingCaller.QUALIFIER));
            Assert.assertTrue(errorTriggered.get());
        }
    }
}

