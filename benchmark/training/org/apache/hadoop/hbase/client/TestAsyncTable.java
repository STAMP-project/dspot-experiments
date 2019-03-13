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
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.IntConsumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TableNotEnabledException;
import org.apache.hadoop.hbase.io.TimeRange;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Category({ MediumTests.class, ClientTests.class })
public class TestAsyncTable {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncTable.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NAME = TableName.valueOf("async");

    private static byte[] FAMILY = Bytes.toBytes("cf");

    private static byte[] QUALIFIER = Bytes.toBytes("cq");

    private static byte[] VALUE = Bytes.toBytes("value");

    private static int MAX_KEY_VALUE_SIZE = 64 * 1024;

    private static AsyncConnection ASYNC_CONN;

    @Rule
    public TestName testName = new TestName();

    private byte[] row;

    @Parameterized.Parameter
    public Supplier<AsyncTable<?>> getTable;

    @Test
    public void testSimple() throws Exception {
        AsyncTable<?> table = getTable.get();
        table.put(new Put(row).addColumn(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER, TestAsyncTable.VALUE)).get();
        Assert.assertTrue(table.exists(new Get(row).addColumn(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER)).get());
        Result result = table.get(new Get(row).addColumn(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER)).get();
        Assert.assertArrayEquals(TestAsyncTable.VALUE, result.getValue(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER));
        table.delete(new Delete(row)).get();
        result = table.get(new Get(row).addColumn(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER)).get();
        Assert.assertTrue(result.isEmpty());
        Assert.assertFalse(table.exists(new Get(row).addColumn(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER)).get());
    }

    @SuppressWarnings("FutureReturnValueIgnored")
    @Test
    public void testSimpleMultiple() throws Exception {
        AsyncTable<?> table = getTable.get();
        int count = 100;
        CountDownLatch putLatch = new CountDownLatch(count);
        IntStream.range(0, count).forEach(( i) -> table.put(new Put(concat(row, i)).addColumn(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER, concat(TestAsyncTable.VALUE, i))).thenAccept(( x) -> putLatch.countDown()));
        putLatch.await();
        BlockingQueue<Boolean> existsResp = new ArrayBlockingQueue<>(count);
        IntStream.range(0, count).forEach(( i) -> table.exists(new Get(concat(row, i)).addColumn(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER)).thenAccept(( x) -> existsResp.add(x)));
        for (int i = 0; i < count; i++) {
            Assert.assertTrue(existsResp.take());
        }
        BlockingQueue<Pair<Integer, Result>> getResp = new ArrayBlockingQueue<>(count);
        IntStream.range(0, count).forEach(( i) -> table.get(new Get(concat(row, i)).addColumn(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER)).thenAccept(( x) -> getResp.add(Pair.newPair(i, x))));
        for (int i = 0; i < count; i++) {
            Pair<Integer, Result> pair = getResp.take();
            Assert.assertArrayEquals(concat(TestAsyncTable.VALUE, pair.getFirst()), pair.getSecond().getValue(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER));
        }
        CountDownLatch deleteLatch = new CountDownLatch(count);
        IntStream.range(0, count).forEach(( i) -> table.delete(new Delete(concat(row, i))).thenAccept(( x) -> deleteLatch.countDown()));
        deleteLatch.await();
        IntStream.range(0, count).forEach(( i) -> table.exists(new Get(concat(row, i)).addColumn(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER)).thenAccept(( x) -> existsResp.add(x)));
        for (int i = 0; i < count; i++) {
            Assert.assertFalse(existsResp.take());
        }
        IntStream.range(0, count).forEach(( i) -> table.get(new Get(concat(row, i)).addColumn(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER)).thenAccept(( x) -> getResp.add(Pair.newPair(i, x))));
        for (int i = 0; i < count; i++) {
            Pair<Integer, Result> pair = getResp.take();
            Assert.assertTrue(pair.getSecond().isEmpty());
        }
    }

    @SuppressWarnings("FutureReturnValueIgnored")
    @Test
    public void testIncrement() throws InterruptedException, ExecutionException {
        AsyncTable<?> table = getTable.get();
        int count = 100;
        CountDownLatch latch = new CountDownLatch(count);
        AtomicLong sum = new AtomicLong(0L);
        IntStream.range(0, count).forEach(( i) -> table.incrementColumnValue(row, TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER, 1).thenAccept(( x) -> {
            sum.addAndGet(x);
            latch.countDown();
        }));
        latch.await();
        Assert.assertEquals(count, Bytes.toLong(table.get(new Get(row).addColumn(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER)).get().getValue(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER)));
        Assert.assertEquals((((1 + count) * count) / 2), sum.get());
    }

    @SuppressWarnings("FutureReturnValueIgnored")
    @Test
    public void testAppend() throws InterruptedException, ExecutionException {
        AsyncTable<?> table = getTable.get();
        int count = 10;
        CountDownLatch latch = new CountDownLatch(count);
        char suffix = ':';
        AtomicLong suffixCount = new AtomicLong(0L);
        IntStream.range(0, count).forEachOrdered(( i) -> table.append(new Append(row).addColumn(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER, Bytes.toBytes((("" + i) + suffix)))).thenAccept(( r) -> {
            suffixCount.addAndGet(Bytes.toString(r.getValue(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER)).chars().filter(( x) -> x == suffix).count());
            latch.countDown();
        }));
        latch.await();
        Assert.assertEquals((((1 + count) * count) / 2), suffixCount.get());
        String value = Bytes.toString(table.get(new Get(row).addColumn(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER)).get().getValue(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER));
        int[] actual = Arrays.asList(value.split(("" + suffix))).stream().mapToInt(Integer::parseInt).sorted().toArray();
        Assert.assertArrayEquals(IntStream.range(0, count).toArray(), actual);
    }

    @SuppressWarnings("FutureReturnValueIgnored")
    @Test
    public void testCheckAndPut() throws InterruptedException, ExecutionException {
        AsyncTable<?> table = getTable.get();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger successIndex = new AtomicInteger((-1));
        int count = 10;
        CountDownLatch latch = new CountDownLatch(count);
        IntStream.range(0, count).forEach(( i) -> table.checkAndMutate(row, TestAsyncTable.FAMILY).qualifier(TestAsyncTable.QUALIFIER).ifNotExists().thenPut(new Put(row).addColumn(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER, concat(TestAsyncTable.VALUE, i))).thenAccept(( x) -> {
            if (x) {
                successCount.incrementAndGet();
                successIndex.set(i);
            }
            latch.countDown();
        }));
        latch.await();
        Assert.assertEquals(1, successCount.get());
        String actual = Bytes.toString(table.get(new Get(row)).get().getValue(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER));
        Assert.assertTrue(actual.endsWith(Integer.toString(successIndex.get())));
    }

    @SuppressWarnings("FutureReturnValueIgnored")
    @Test
    public void testCheckAndDelete() throws InterruptedException, ExecutionException {
        AsyncTable<?> table = getTable.get();
        int count = 10;
        CountDownLatch putLatch = new CountDownLatch((count + 1));
        table.put(new Put(row).addColumn(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER, TestAsyncTable.VALUE)).thenRun(() -> putLatch.countDown());
        IntStream.range(0, count).forEach(( i) -> table.put(new Put(row).addColumn(TestAsyncTable.FAMILY, concat(TestAsyncTable.QUALIFIER, i), TestAsyncTable.VALUE)).thenRun(() -> putLatch.countDown()));
        putLatch.await();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger successIndex = new AtomicInteger((-1));
        CountDownLatch deleteLatch = new CountDownLatch(count);
        IntStream.range(0, count).forEach(( i) -> table.checkAndMutate(row, TestAsyncTable.FAMILY).qualifier(TestAsyncTable.QUALIFIER).ifEquals(TestAsyncTable.VALUE).thenDelete(new Delete(row).addColumn(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER).addColumn(TestAsyncTable.FAMILY, concat(TestAsyncTable.QUALIFIER, i))).thenAccept(( x) -> {
            if (x) {
                successCount.incrementAndGet();
                successIndex.set(i);
            }
            deleteLatch.countDown();
        }));
        deleteLatch.await();
        Assert.assertEquals(1, successCount.get());
        Result result = table.get(new Get(row)).get();
        IntStream.range(0, count).forEach(( i) -> {
            if (i == (successIndex.get())) {
                Assert.assertFalse(result.containsColumn(TestAsyncTable.FAMILY, concat(TestAsyncTable.QUALIFIER, i)));
            } else {
                Assert.assertArrayEquals(TestAsyncTable.VALUE, result.getValue(TestAsyncTable.FAMILY, concat(TestAsyncTable.QUALIFIER, i)));
            }
        });
    }

    @Test
    public void testMutateRow() throws IOException, InterruptedException, ExecutionException {
        AsyncTable<?> table = getTable.get();
        RowMutations mutation = new RowMutations(row);
        mutation.add(((Mutation) (new Put(row).addColumn(TestAsyncTable.FAMILY, concat(TestAsyncTable.QUALIFIER, 1), TestAsyncTable.VALUE))));
        table.mutateRow(mutation).get();
        Result result = table.get(new Get(row)).get();
        Assert.assertArrayEquals(TestAsyncTable.VALUE, result.getValue(TestAsyncTable.FAMILY, concat(TestAsyncTable.QUALIFIER, 1)));
        mutation = new RowMutations(row);
        mutation.add(((Mutation) (new Delete(row).addColumn(TestAsyncTable.FAMILY, concat(TestAsyncTable.QUALIFIER, 1)))));
        mutation.add(((Mutation) (new Put(row).addColumn(TestAsyncTable.FAMILY, concat(TestAsyncTable.QUALIFIER, 2), TestAsyncTable.VALUE))));
        table.mutateRow(mutation).get();
        result = table.get(new Get(row)).get();
        Assert.assertNull(result.getValue(TestAsyncTable.FAMILY, concat(TestAsyncTable.QUALIFIER, 1)));
        Assert.assertArrayEquals(TestAsyncTable.VALUE, result.getValue(TestAsyncTable.FAMILY, concat(TestAsyncTable.QUALIFIER, 2)));
    }

    @SuppressWarnings("FutureReturnValueIgnored")
    @Test
    public void testCheckAndMutate() throws InterruptedException, ExecutionException {
        AsyncTable<?> table = getTable.get();
        int count = 10;
        CountDownLatch putLatch = new CountDownLatch((count + 1));
        table.put(new Put(row).addColumn(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER, TestAsyncTable.VALUE)).thenRun(() -> putLatch.countDown());
        IntStream.range(0, count).forEach(( i) -> table.put(new Put(row).addColumn(TestAsyncTable.FAMILY, concat(TestAsyncTable.QUALIFIER, i), TestAsyncTable.VALUE)).thenRun(() -> putLatch.countDown()));
        putLatch.await();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger successIndex = new AtomicInteger((-1));
        CountDownLatch mutateLatch = new CountDownLatch(count);
        IntStream.range(0, count).forEach(( i) -> {
            RowMutations mutation = new RowMutations(row);
            try {
                mutation.add(((Mutation) (new Delete(row).addColumn(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER))));
                mutation.add(((Mutation) (new Put(row).addColumn(TestAsyncTable.FAMILY, concat(TestAsyncTable.QUALIFIER, i), concat(TestAsyncTable.VALUE, i)))));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            table.checkAndMutate(row, TestAsyncTable.FAMILY).qualifier(TestAsyncTable.QUALIFIER).ifEquals(TestAsyncTable.VALUE).thenMutate(mutation).thenAccept(( x) -> {
                if (x) {
                    successCount.incrementAndGet();
                    successIndex.set(i);
                }
                mutateLatch.countDown();
            });
        });
        mutateLatch.await();
        Assert.assertEquals(1, successCount.get());
        Result result = table.get(new Get(row)).get();
        IntStream.range(0, count).forEach(( i) -> {
            if (i == (successIndex.get())) {
                Assert.assertArrayEquals(concat(TestAsyncTable.VALUE, i), result.getValue(TestAsyncTable.FAMILY, concat(TestAsyncTable.QUALIFIER, i)));
            } else {
                Assert.assertArrayEquals(TestAsyncTable.VALUE, result.getValue(TestAsyncTable.FAMILY, concat(TestAsyncTable.QUALIFIER, i)));
            }
        });
    }

    @Test
    public void testCheckAndMutateWithTimeRange() throws Exception {
        AsyncTable<?> table = getTable.get();
        final long ts = (System.currentTimeMillis()) / 2;
        Put put = new Put(row);
        put.addColumn(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER, ts, TestAsyncTable.VALUE);
        boolean ok = table.checkAndMutate(row, TestAsyncTable.FAMILY).qualifier(TestAsyncTable.QUALIFIER).ifNotExists().thenPut(put).get();
        Assert.assertTrue(ok);
        ok = table.checkAndMutate(row, TestAsyncTable.FAMILY).qualifier(TestAsyncTable.QUALIFIER).timeRange(TimeRange.at((ts + 10000))).ifEquals(TestAsyncTable.VALUE).thenPut(put).get();
        Assert.assertFalse(ok);
        ok = table.checkAndMutate(row, TestAsyncTable.FAMILY).qualifier(TestAsyncTable.QUALIFIER).timeRange(TimeRange.at(ts)).ifEquals(TestAsyncTable.VALUE).thenPut(put).get();
        Assert.assertTrue(ok);
        RowMutations rm = new RowMutations(row).add(((Mutation) (put)));
        ok = table.checkAndMutate(row, TestAsyncTable.FAMILY).qualifier(TestAsyncTable.QUALIFIER).timeRange(TimeRange.at((ts + 10000))).ifEquals(TestAsyncTable.VALUE).thenMutate(rm).get();
        Assert.assertFalse(ok);
        ok = table.checkAndMutate(row, TestAsyncTable.FAMILY).qualifier(TestAsyncTable.QUALIFIER).timeRange(TimeRange.at(ts)).ifEquals(TestAsyncTable.VALUE).thenMutate(rm).get();
        Assert.assertTrue(ok);
        Delete delete = new Delete(row).addColumn(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER);
        ok = table.checkAndMutate(row, TestAsyncTable.FAMILY).qualifier(TestAsyncTable.QUALIFIER).timeRange(TimeRange.at((ts + 10000))).ifEquals(TestAsyncTable.VALUE).thenDelete(delete).get();
        Assert.assertFalse(ok);
        ok = table.checkAndMutate(row, TestAsyncTable.FAMILY).qualifier(TestAsyncTable.QUALIFIER).timeRange(TimeRange.at(ts)).ifEquals(TestAsyncTable.VALUE).thenDelete(delete).get();
        Assert.assertTrue(ok);
    }

    @Test
    public void testDisabled() throws InterruptedException, ExecutionException {
        TestAsyncTable.ASYNC_CONN.getAdmin().disableTable(TestAsyncTable.TABLE_NAME).get();
        try {
            getTable.get().get(new Get(row)).get();
            Assert.fail("Should fail since table has been disabled");
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            Assert.assertThat(cause, CoreMatchers.instanceOf(TableNotEnabledException.class));
            Assert.assertThat(cause.getMessage(), CoreMatchers.containsString(TestAsyncTable.TABLE_NAME.getNameAsString()));
        }
    }

    @Test
    public void testInvalidPut() {
        try {
            getTable.get().put(new Put(Bytes.toBytes(0)));
            Assert.fail("Should fail since the put does not contain any cells");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("No columns to insert"));
        }
        try {
            getTable.get().put(addColumn(TestAsyncTable.FAMILY, TestAsyncTable.QUALIFIER, new byte[TestAsyncTable.MAX_KEY_VALUE_SIZE]));
            Assert.fail("Should fail since the put exceeds the max key value size");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("KeyValue size too large"));
        }
    }
}

