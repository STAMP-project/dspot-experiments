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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.io.netty.util.HashedWheelTimer;
import org.apache.hbase.thirdparty.io.netty.util.Timeout;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static AsyncConnectionImpl.RETRY_TIMER;


@Category({ MediumTests.class, ClientTests.class })
public class TestAsyncBufferMutator {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncBufferMutator.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NAME = TableName.valueOf("async");

    private static TableName MULTI_REGION_TABLE_NAME = TableName.valueOf("async-multi-region");

    private static byte[] CF = Bytes.toBytes("cf");

    private static byte[] CQ = Bytes.toBytes("cq");

    private static int COUNT = 100;

    private static byte[] VALUE = new byte[1024];

    private static AsyncConnection CONN;

    @Test
    public void testWithMultiRegionTable() throws InterruptedException {
        test(TestAsyncBufferMutator.MULTI_REGION_TABLE_NAME);
    }

    @Test
    public void testWithSingleRegionTable() throws InterruptedException {
        test(TestAsyncBufferMutator.TABLE_NAME);
    }

    @Test
    public void testClosedMutate() throws InterruptedException {
        AsyncBufferedMutator mutator = TestAsyncBufferMutator.CONN.getBufferedMutator(TestAsyncBufferMutator.TABLE_NAME);
        mutator.close();
        Put put = addColumn(TestAsyncBufferMutator.CF, TestAsyncBufferMutator.CQ, TestAsyncBufferMutator.VALUE);
        try {
            mutator.mutate(put).get();
            Assert.fail("Close check failed");
        } catch (ExecutionException e) {
            Assert.assertThat(e.getCause(), CoreMatchers.instanceOf(IOException.class));
            Assert.assertTrue(e.getCause().getMessage().startsWith("Already closed"));
        }
        for (CompletableFuture<Void> f : mutator.mutate(Arrays.asList(put))) {
            try {
                f.get();
                Assert.fail("Close check failed");
            } catch (ExecutionException e) {
                Assert.assertThat(e.getCause(), CoreMatchers.instanceOf(IOException.class));
                Assert.assertTrue(e.getCause().getMessage().startsWith("Already closed"));
            }
        }
    }

    @Test
    public void testNoPeriodicFlush() throws InterruptedException, ExecutionException {
        try (AsyncBufferedMutator mutator = TestAsyncBufferMutator.CONN.getBufferedMutatorBuilder(TestAsyncBufferMutator.TABLE_NAME).disableWriteBufferPeriodicFlush().build()) {
            Put put = addColumn(TestAsyncBufferMutator.CF, TestAsyncBufferMutator.CQ, TestAsyncBufferMutator.VALUE);
            CompletableFuture<?> future = mutator.mutate(put);
            Thread.sleep(2000);
            // assert that we have not flushed it out
            Assert.assertFalse(future.isDone());
            mutator.flush();
            future.get();
        }
        AsyncTable<?> table = TestAsyncBufferMutator.CONN.getTable(TestAsyncBufferMutator.TABLE_NAME);
        Assert.assertArrayEquals(TestAsyncBufferMutator.VALUE, table.get(new Get(Bytes.toBytes(0))).get().getValue(TestAsyncBufferMutator.CF, TestAsyncBufferMutator.CQ));
    }

    @Test
    public void testPeriodicFlush() throws InterruptedException, ExecutionException {
        AsyncBufferedMutator mutator = TestAsyncBufferMutator.CONN.getBufferedMutatorBuilder(TestAsyncBufferMutator.TABLE_NAME).setWriteBufferPeriodicFlush(1, TimeUnit.SECONDS).build();
        Put put = addColumn(TestAsyncBufferMutator.CF, TestAsyncBufferMutator.CQ, TestAsyncBufferMutator.VALUE);
        CompletableFuture<?> future = mutator.mutate(put);
        future.get();
        AsyncTable<?> table = TestAsyncBufferMutator.CONN.getTable(TestAsyncBufferMutator.TABLE_NAME);
        Assert.assertArrayEquals(TestAsyncBufferMutator.VALUE, table.get(new Get(Bytes.toBytes(0))).get().getValue(TestAsyncBufferMutator.CF, TestAsyncBufferMutator.CQ));
    }

    // a bit deep into the implementation
    @Test
    public void testCancelPeriodicFlush() throws InterruptedException, ExecutionException {
        Put put = addColumn(TestAsyncBufferMutator.CF, TestAsyncBufferMutator.CQ, TestAsyncBufferMutator.VALUE);
        try (AsyncBufferedMutatorImpl mutator = ((AsyncBufferedMutatorImpl) (TestAsyncBufferMutator.CONN.getBufferedMutatorBuilder(TestAsyncBufferMutator.TABLE_NAME).setWriteBufferPeriodicFlush(1, TimeUnit.SECONDS).setWriteBufferSize((10 * (put.heapSize()))).build()))) {
            List<CompletableFuture<?>> futures = new ArrayList<>();
            futures.add(mutator.mutate(put));
            Timeout task = mutator.periodicFlushTask;
            // we should have scheduled a periodic flush task
            Assert.assertNotNull(task);
            for (int i = 1; ; i++) {
                futures.add(mutator.mutate(new Put(Bytes.toBytes(i)).addColumn(TestAsyncBufferMutator.CF, TestAsyncBufferMutator.CQ, TestAsyncBufferMutator.VALUE)));
                if ((mutator.periodicFlushTask) == null) {
                    break;
                }
            }
            Assert.assertTrue(task.isCancelled());
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            AsyncTable<?> table = TestAsyncBufferMutator.CONN.getTable(TestAsyncBufferMutator.TABLE_NAME);
            for (int i = 0; i < (futures.size()); i++) {
                Assert.assertArrayEquals(TestAsyncBufferMutator.VALUE, table.get(new Get(Bytes.toBytes(i))).get().getValue(TestAsyncBufferMutator.CF, TestAsyncBufferMutator.CQ));
            }
        }
    }

    @Test
    public void testCancelPeriodicFlushByManuallyFlush() throws InterruptedException, ExecutionException {
        try (AsyncBufferedMutatorImpl mutator = ((AsyncBufferedMutatorImpl) (TestAsyncBufferMutator.CONN.getBufferedMutatorBuilder(TestAsyncBufferMutator.TABLE_NAME).setWriteBufferPeriodicFlush(1, TimeUnit.SECONDS).build()))) {
            CompletableFuture<?> future = mutator.mutate(new Put(Bytes.toBytes(0)).addColumn(TestAsyncBufferMutator.CF, TestAsyncBufferMutator.CQ, TestAsyncBufferMutator.VALUE));
            Timeout task = mutator.periodicFlushTask;
            // we should have scheduled a periodic flush task
            Assert.assertNotNull(task);
            mutator.flush();
            Assert.assertTrue(task.isCancelled());
            future.get();
            AsyncTable<?> table = TestAsyncBufferMutator.CONN.getTable(TestAsyncBufferMutator.TABLE_NAME);
            Assert.assertArrayEquals(TestAsyncBufferMutator.VALUE, table.get(new Get(Bytes.toBytes(0))).get().getValue(TestAsyncBufferMutator.CF, TestAsyncBufferMutator.CQ));
        }
    }

    @Test
    public void testCancelPeriodicFlushByClose() throws InterruptedException, ExecutionException {
        CompletableFuture<?> future;
        Timeout task;
        try (AsyncBufferedMutatorImpl mutator = ((AsyncBufferedMutatorImpl) (TestAsyncBufferMutator.CONN.getBufferedMutatorBuilder(TestAsyncBufferMutator.TABLE_NAME).setWriteBufferPeriodicFlush(1, TimeUnit.SECONDS).build()))) {
            future = mutator.mutate(new Put(Bytes.toBytes(0)).addColumn(TestAsyncBufferMutator.CF, TestAsyncBufferMutator.CQ, TestAsyncBufferMutator.VALUE));
            task = mutator.periodicFlushTask;
            // we should have scheduled a periodic flush task
            Assert.assertNotNull(task);
        }
        Assert.assertTrue(task.isCancelled());
        future.get();
        AsyncTable<?> table = TestAsyncBufferMutator.CONN.getTable(TestAsyncBufferMutator.TABLE_NAME);
        Assert.assertArrayEquals(TestAsyncBufferMutator.VALUE, table.get(new Get(Bytes.toBytes(0))).get().getValue(TestAsyncBufferMutator.CF, TestAsyncBufferMutator.CQ));
    }

    private static final class AsyncBufferMutatorForTest extends AsyncBufferedMutatorImpl {
        private int flushCount;

        AsyncBufferMutatorForTest(HashedWheelTimer periodicalFlushTimer, AsyncTable<?> table, long writeBufferSize, long periodicFlushTimeoutNs, int maxKeyValueSize) {
            super(periodicalFlushTimer, table, writeBufferSize, periodicFlushTimeoutNs, maxKeyValueSize);
        }

        @Override
        protected void internalFlush() {
            (flushCount)++;
            super.internalFlush();
        }
    }

    @Test
    public void testRaceBetweenNormalFlushAndPeriodicFlush() throws InterruptedException, ExecutionException {
        Put put = addColumn(TestAsyncBufferMutator.CF, TestAsyncBufferMutator.CQ, TestAsyncBufferMutator.VALUE);
        try (TestAsyncBufferMutator.AsyncBufferMutatorForTest mutator = new TestAsyncBufferMutator.AsyncBufferMutatorForTest(RETRY_TIMER, TestAsyncBufferMutator.CONN.getTable(TestAsyncBufferMutator.TABLE_NAME), (10 * (put.heapSize())), TimeUnit.MILLISECONDS.toNanos(200), (1024 * 1024))) {
            CompletableFuture<?> future = mutator.mutate(put);
            Timeout task = mutator.periodicFlushTask;
            // we should have scheduled a periodic flush task
            Assert.assertNotNull(task);
            synchronized(mutator) {
                // synchronized on mutator to prevent periodic flush to be executed
                Thread.sleep(500);
                // the timeout should be issued
                Assert.assertTrue(task.isExpired());
                // but no flush is issued as we hold the lock
                Assert.assertEquals(0, mutator.flushCount);
                Assert.assertFalse(future.isDone());
                // manually flush, then release the lock
                flush();
            }
            // this is a bit deep into the implementation in netty but anyway let's add a check here to
            // confirm that an issued timeout can not be canceled by netty framework.
            Assert.assertFalse(task.isCancelled());
            // and the mutation is done
            future.get();
            AsyncTable<?> table = TestAsyncBufferMutator.CONN.getTable(TestAsyncBufferMutator.TABLE_NAME);
            Assert.assertArrayEquals(TestAsyncBufferMutator.VALUE, table.get(new Get(Bytes.toBytes(0))).get().getValue(TestAsyncBufferMutator.CF, TestAsyncBufferMutator.CQ));
            // only the manual flush, the periodic flush should have been canceled by us
            Assert.assertEquals(1, mutator.flushCount);
        }
    }
}

