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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.regionserver.NoSuchColumnFamilyException;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Category({ LargeTests.class, ClientTests.class })
public class TestAsyncTableBatch {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncTableBatch.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NAME = TableName.valueOf("async");

    private static byte[] FAMILY = Bytes.toBytes("cf");

    private static byte[] CQ = Bytes.toBytes("cq");

    private static byte[] CQ1 = Bytes.toBytes("cq1");

    private static int COUNT = 1000;

    private static AsyncConnection CONN;

    private static byte[][] SPLIT_KEYS;

    private static int MAX_KEY_VALUE_SIZE = 64 * 1024;

    @Parameterized.Parameter(0)
    public String tableType;

    @Parameterized.Parameter(1)
    public Function<TableName, AsyncTable<?>> tableGetter;

    @Test
    public void test() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        AsyncTable<?> table = tableGetter.apply(TestAsyncTableBatch.TABLE_NAME);
        table.putAll(IntStream.range(0, TestAsyncTableBatch.COUNT).mapToObj(( i) -> new Put(getRow(i)).addColumn(TestAsyncTableBatch.FAMILY, TestAsyncTableBatch.CQ, Bytes.toBytes(i))).collect(Collectors.toList())).get();
        List<Result> results = table.getAll(IntStream.range(0, TestAsyncTableBatch.COUNT).mapToObj(( i) -> Arrays.asList(new Get(getRow(i)), new Get(Arrays.copyOf(getRow(i), 4)))).flatMap(( l) -> l.stream()).collect(Collectors.toList())).get();
        Assert.assertEquals((2 * (TestAsyncTableBatch.COUNT)), results.size());
        for (int i = 0; i < (TestAsyncTableBatch.COUNT); i++) {
            Assert.assertEquals(i, Bytes.toInt(getValue(TestAsyncTableBatch.FAMILY, TestAsyncTableBatch.CQ)));
            Assert.assertTrue(isEmpty());
        }
        Admin admin = TestAsyncTableBatch.TEST_UTIL.getAdmin();
        admin.flush(TestAsyncTableBatch.TABLE_NAME);
        List<Future<?>> splitFutures = TestAsyncTableBatch.TEST_UTIL.getHBaseCluster().getRegions(TestAsyncTableBatch.TABLE_NAME).stream().map(( r) -> {
            byte[] startKey = r.getRegionInfo().getStartKey();
            int number = (startKey.length == 0) ? 55 : Integer.parseInt(Bytes.toString(startKey));
            byte[] splitPoint = Bytes.toBytes(String.format("%03d", (number + 55)));
            try {
                return admin.splitRegionAsync(r.getRegionInfo().getRegionName(), splitPoint);
            } catch ( e) {
                throw new <e>UncheckedIOException();
            }
        }).collect(Collectors.toList());
        for (Future<?> future : splitFutures) {
            future.get(30, TimeUnit.SECONDS);
        }
        table.deleteAll(IntStream.range(0, TestAsyncTableBatch.COUNT).mapToObj(( i) -> new Delete(getRow(i))).collect(Collectors.toList())).get();
        results = table.getAll(IntStream.range(0, TestAsyncTableBatch.COUNT).mapToObj(( i) -> new Get(getRow(i))).collect(Collectors.toList())).get();
        Assert.assertEquals(TestAsyncTableBatch.COUNT, results.size());
        results.forEach(( r) -> assertTrue(r.isEmpty()));
    }

    @Test
    public void testWithRegionServerFailover() throws Exception {
        AsyncTable<?> table = tableGetter.apply(TestAsyncTableBatch.TABLE_NAME);
        table.putAll(IntStream.range(0, TestAsyncTableBatch.COUNT).mapToObj(( i) -> new Put(getRow(i)).addColumn(TestAsyncTableBatch.FAMILY, TestAsyncTableBatch.CQ, Bytes.toBytes(i))).collect(Collectors.toList())).get();
        TestAsyncTableBatch.TEST_UTIL.getMiniHBaseCluster().getRegionServer(0).abort("Aborting for tests");
        Thread.sleep(100);
        table.putAll(IntStream.range(TestAsyncTableBatch.COUNT, (2 * (TestAsyncTableBatch.COUNT))).mapToObj(( i) -> new Put(getRow(i)).addColumn(TestAsyncTableBatch.FAMILY, TestAsyncTableBatch.CQ, Bytes.toBytes(i))).collect(Collectors.toList())).get();
        List<Result> results = table.getAll(IntStream.range(0, (2 * (TestAsyncTableBatch.COUNT))).mapToObj(( i) -> new Get(getRow(i))).collect(Collectors.toList())).get();
        Assert.assertEquals((2 * (TestAsyncTableBatch.COUNT)), results.size());
        results.forEach(( r) -> assertFalse(r.isEmpty()));
        table.deleteAll(IntStream.range(0, (2 * (TestAsyncTableBatch.COUNT))).mapToObj(( i) -> new Delete(getRow(i))).collect(Collectors.toList())).get();
        results = table.getAll(IntStream.range(0, (2 * (TestAsyncTableBatch.COUNT))).mapToObj(( i) -> new Get(getRow(i))).collect(Collectors.toList())).get();
        Assert.assertEquals((2 * (TestAsyncTableBatch.COUNT)), results.size());
        results.forEach(( r) -> assertTrue(r.isEmpty()));
    }

    @Test
    public void testMixed() throws IOException, InterruptedException, ExecutionException {
        AsyncTable<?> table = tableGetter.apply(TestAsyncTableBatch.TABLE_NAME);
        table.putAll(IntStream.range(0, 7).mapToObj(( i) -> new Put(Bytes.toBytes(i)).addColumn(TestAsyncTableBatch.FAMILY, TestAsyncTableBatch.CQ, Bytes.toBytes(((long) (i))))).collect(Collectors.toList())).get();
        List<Row> actions = new ArrayList<>();
        actions.add(new Get(Bytes.toBytes(0)));
        actions.add(new Put(Bytes.toBytes(1)).addColumn(TestAsyncTableBatch.FAMILY, TestAsyncTableBatch.CQ, Bytes.toBytes(2L)));
        actions.add(new Delete(Bytes.toBytes(2)));
        actions.add(new Increment(Bytes.toBytes(3)).addColumn(TestAsyncTableBatch.FAMILY, TestAsyncTableBatch.CQ, 1));
        actions.add(new Append(Bytes.toBytes(4)).addColumn(TestAsyncTableBatch.FAMILY, TestAsyncTableBatch.CQ, Bytes.toBytes(4)));
        RowMutations rm = new RowMutations(Bytes.toBytes(5));
        rm.add(((Mutation) (new Put(Bytes.toBytes(5)).addColumn(TestAsyncTableBatch.FAMILY, TestAsyncTableBatch.CQ, Bytes.toBytes(100L)))));
        rm.add(((Mutation) (new Put(Bytes.toBytes(5)).addColumn(TestAsyncTableBatch.FAMILY, TestAsyncTableBatch.CQ1, Bytes.toBytes(200L)))));
        actions.add(rm);
        actions.add(new Get(Bytes.toBytes(6)));
        List<Object> results = table.batchAll(actions).get();
        Assert.assertEquals(7, results.size());
        Result getResult = ((Result) (results.get(0)));
        Assert.assertEquals(0, Bytes.toLong(getResult.getValue(TestAsyncTableBatch.FAMILY, TestAsyncTableBatch.CQ)));
        Assert.assertEquals(2, Bytes.toLong(getValue(TestAsyncTableBatch.FAMILY, TestAsyncTableBatch.CQ)));
        Assert.assertTrue(isEmpty());
        Result incrementResult = ((Result) (results.get(3)));
        Assert.assertEquals(4, Bytes.toLong(incrementResult.getValue(TestAsyncTableBatch.FAMILY, TestAsyncTableBatch.CQ)));
        Result appendResult = ((Result) (results.get(4)));
        byte[] appendValue = appendResult.getValue(TestAsyncTableBatch.FAMILY, TestAsyncTableBatch.CQ);
        Assert.assertEquals(12, appendValue.length);
        Assert.assertEquals(4, Bytes.toLong(appendValue));
        Assert.assertEquals(4, Bytes.toInt(appendValue, 8));
        Assert.assertEquals(100, Bytes.toLong(getValue(TestAsyncTableBatch.FAMILY, TestAsyncTableBatch.CQ)));
        Assert.assertEquals(200, Bytes.toLong(getValue(TestAsyncTableBatch.FAMILY, TestAsyncTableBatch.CQ1)));
        getResult = ((Result) (results.get(6)));
        Assert.assertEquals(6, Bytes.toLong(getResult.getValue(TestAsyncTableBatch.FAMILY, TestAsyncTableBatch.CQ)));
    }

    public static final class ErrorInjectObserver implements RegionCoprocessor , RegionObserver {
        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void preGetOp(ObserverContext<RegionCoprocessorEnvironment> e, Get get, List<Cell> results) throws IOException {
            if ((e.getEnvironment().getRegionInfo().getEndKey().length) == 0) {
                throw new DoNotRetryRegionException("Inject Error");
            }
        }
    }

    @Test
    public void testPartialSuccess() throws IOException, InterruptedException, ExecutionException {
        Admin admin = TestAsyncTableBatch.TEST_UTIL.getAdmin();
        TableDescriptor htd = TableDescriptorBuilder.newBuilder(admin.getDescriptor(TestAsyncTableBatch.TABLE_NAME)).setCoprocessor(TestAsyncTableBatch.ErrorInjectObserver.class.getName()).build();
        admin.modifyTable(htd);
        AsyncTable<?> table = tableGetter.apply(TestAsyncTableBatch.TABLE_NAME);
        table.putAll(Arrays.asList(TestAsyncTableBatch.SPLIT_KEYS).stream().map(( k) -> new Put(k).addColumn(TestAsyncTableBatch.FAMILY, TestAsyncTableBatch.CQ, k)).collect(Collectors.toList())).get();
        List<CompletableFuture<Result>> futures = table.get(Arrays.asList(TestAsyncTableBatch.SPLIT_KEYS).stream().map(( k) -> new Get(k)).collect(Collectors.toList()));
        for (int i = 0; i < ((TestAsyncTableBatch.SPLIT_KEYS.length) - 1); i++) {
            Assert.assertArrayEquals(TestAsyncTableBatch.SPLIT_KEYS[i], getValue(TestAsyncTableBatch.FAMILY, TestAsyncTableBatch.CQ));
        }
        try {
            futures.get(((TestAsyncTableBatch.SPLIT_KEYS.length) - 1)).get();
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertThat(e.getCause(), CoreMatchers.instanceOf(RetriesExhaustedException.class));
        }
    }

    @Test
    public void testPartialSuccessOnSameRegion() throws InterruptedException, ExecutionException {
        AsyncTable<?> table = tableGetter.apply(TestAsyncTableBatch.TABLE_NAME);
        List<CompletableFuture<Object>> futures = table.batch(Arrays.asList(new Put(Bytes.toBytes("put")).addColumn(Bytes.toBytes("not-exists"), TestAsyncTableBatch.CQ, Bytes.toBytes("bad")), new Increment(Bytes.toBytes("inc")).addColumn(TestAsyncTableBatch.FAMILY, TestAsyncTableBatch.CQ, 1), new Put(Bytes.toBytes("put")).addColumn(TestAsyncTableBatch.FAMILY, TestAsyncTableBatch.CQ, Bytes.toBytes("good"))));
        try {
            futures.get(0).get();
            Assert.fail();
        } catch (ExecutionException e) {
            Assert.assertThat(e.getCause(), CoreMatchers.instanceOf(RetriesExhaustedException.class));
            Assert.assertThat(e.getCause().getCause(), CoreMatchers.instanceOf(NoSuchColumnFamilyException.class));
        }
        Assert.assertEquals(1, Bytes.toLong(getValue(TestAsyncTableBatch.FAMILY, TestAsyncTableBatch.CQ)));
        Assert.assertTrue(isEmpty());
        Assert.assertEquals("good", Bytes.toString(getValue(TestAsyncTableBatch.FAMILY, TestAsyncTableBatch.CQ)));
    }

    @Test
    public void testInvalidPut() {
        AsyncTable<?> table = tableGetter.apply(TestAsyncTableBatch.TABLE_NAME);
        try {
            table.batch(Arrays.asList(new Delete(Bytes.toBytes(0)), new Put(Bytes.toBytes(0))));
            Assert.fail("Should fail since the put does not contain any cells");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("No columns to insert"));
        }
        try {
            table.batch(Arrays.asList(addColumn(TestAsyncTableBatch.FAMILY, TestAsyncTableBatch.CQ, new byte[TestAsyncTableBatch.MAX_KEY_VALUE_SIZE]), new Delete(Bytes.toBytes(0))));
            Assert.fail("Should fail since the put exceeds the max key value size");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("KeyValue size too large"));
        }
    }
}

