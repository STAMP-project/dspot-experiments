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
package org.apache.beam.sdk.io.gcp.spanner;


import BatchSpannerRead.GeneratePartitionsFn;
import GlobalWindow.INSTANCE;
import SpannerIO.Read;
import Type.StructField;
import com.google.cloud.Timestamp;
import com.google.cloud.spanner.BatchReadOnlyTransaction;
import com.google.cloud.spanner.BatchTransactionId;
import com.google.cloud.spanner.FakeBatchTransactionId;
import com.google.cloud.spanner.FakePartitionFactory;
import com.google.cloud.spanner.KeySet;
import com.google.cloud.spanner.Partition;
import com.google.cloud.spanner.PartitionOptions;
import com.google.cloud.spanner.ResultSets;
import com.google.cloud.spanner.Statement;
import com.google.cloud.spanner.Struct;
import com.google.cloud.spanner.TimestampBound;
import com.google.cloud.spanner.Type;
import com.google.cloud.spanner.Value;
import com.google.protobuf.ByteString;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFnTester;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link SpannerIO}.
 */
@RunWith(JUnit4.class)
public class SpannerIOReadTest implements Serializable {
    @Rule
    public final transient TestPipeline pipeline = TestPipeline.create().enableAbandonedNodeEnforcement(false);

    @Rule
    public final transient ExpectedException thrown = ExpectedException.none();

    private FakeServiceFactory serviceFactory;

    private BatchReadOnlyTransaction mockBatchTx;

    private static final Type FAKE_TYPE = Type.struct(StructField.of("id", Type.int64()), StructField.of("name", Type.string()));

    private static final List<Struct> FAKE_ROWS = Arrays.asList(Struct.newBuilder().set("id").to(Value.int64(1)).set("name").to("Alice").build(), Struct.newBuilder().set("id").to(Value.int64(2)).set("name").to("Bob").build(), Struct.newBuilder().set("id").to(Value.int64(3)).set("name").to("Carl").build(), Struct.newBuilder().set("id").to(Value.int64(4)).set("name").to("Dan").build(), Struct.newBuilder().set("id").to(Value.int64(5)).set("name").to("Evan").build(), Struct.newBuilder().set("id").to(Value.int64(6)).set("name").to("Floyd").build());

    @Test
    public void runQuery() throws Exception {
        SpannerIO.Read read = SpannerIO.read().withProjectId("test").withInstanceId("123").withDatabaseId("aaa").withQuery("SELECT * FROM users").withServiceFactory(serviceFactory);
        List<Partition> fakePartitions = Arrays.asList(Mockito.mock(Partition.class), Mockito.mock(Partition.class), Mockito.mock(Partition.class));
        BatchTransactionId id = Mockito.mock(BatchTransactionId.class);
        Transaction tx = Transaction.create(id);
        PCollectionView<Transaction> txView = pipeline.apply(Create.of(tx)).apply(View.<Transaction>asSingleton());
        BatchSpannerRead.GeneratePartitionsFn fn = new BatchSpannerRead.GeneratePartitionsFn(read.getSpannerConfig(), txView);
        DoFnTester<ReadOperation, Partition> fnTester = DoFnTester.of(fn);
        fnTester.setSideInput(txView, INSTANCE, tx);
        Mockito.when(serviceFactory.mockBatchClient().batchReadOnlyTransaction(id)).thenReturn(mockBatchTx);
        Mockito.when(mockBatchTx.partitionQuery(ArgumentMatchers.any(PartitionOptions.class), ArgumentMatchers.any(Statement.class))).thenReturn(fakePartitions);
        List<Partition> result = fnTester.processBundle(read.getReadOperation());
        Assert.assertThat(result, Matchers.containsInAnyOrder(fakePartitions.toArray()));
        Mockito.verify(serviceFactory.mockBatchClient()).batchReadOnlyTransaction(id);
        Mockito.verify(mockBatchTx).partitionQuery(ArgumentMatchers.any(PartitionOptions.class), ArgumentMatchers.eq(Statement.of(("SELECT * " + "FROM users"))));
    }

    @Test
    public void runRead() throws Exception {
        SpannerIO.Read read = SpannerIO.read().withProjectId("test").withInstanceId("123").withDatabaseId("aaa").withTable("users").withColumns("id", "name").withServiceFactory(serviceFactory);
        List<Partition> fakePartitions = Arrays.asList(Mockito.mock(Partition.class), Mockito.mock(Partition.class), Mockito.mock(Partition.class));
        BatchTransactionId id = Mockito.mock(BatchTransactionId.class);
        Transaction tx = Transaction.create(id);
        PCollectionView<Transaction> txView = pipeline.apply(Create.of(tx)).apply(View.<Transaction>asSingleton());
        BatchSpannerRead.GeneratePartitionsFn fn = new BatchSpannerRead.GeneratePartitionsFn(read.getSpannerConfig(), txView);
        DoFnTester<ReadOperation, Partition> fnTester = DoFnTester.of(fn);
        fnTester.setSideInput(txView, INSTANCE, tx);
        Mockito.when(serviceFactory.mockBatchClient().batchReadOnlyTransaction(id)).thenReturn(mockBatchTx);
        Mockito.when(mockBatchTx.partitionRead(ArgumentMatchers.any(PartitionOptions.class), ArgumentMatchers.eq("users"), ArgumentMatchers.eq(KeySet.all()), ArgumentMatchers.eq(Arrays.asList("id", "name")))).thenReturn(fakePartitions);
        List<Partition> result = fnTester.processBundle(read.getReadOperation());
        Assert.assertThat(result, Matchers.containsInAnyOrder(fakePartitions.toArray()));
        Mockito.verify(serviceFactory.mockBatchClient()).batchReadOnlyTransaction(id);
        Mockito.verify(mockBatchTx).partitionRead(ArgumentMatchers.any(PartitionOptions.class), ArgumentMatchers.eq("users"), ArgumentMatchers.eq(KeySet.all()), ArgumentMatchers.eq(Arrays.asList("id", "name")));
    }

    @Test
    public void runReadUsingIndex() throws Exception {
        SpannerIO.Read read = SpannerIO.read().withProjectId("test").withInstanceId("123").withDatabaseId("aaa").withTimestamp(Timestamp.now()).withTable("users").withColumns("id", "name").withIndex("theindex").withServiceFactory(serviceFactory);
        List<Partition> fakePartitions = Arrays.asList(Mockito.mock(Partition.class), Mockito.mock(Partition.class), Mockito.mock(Partition.class));
        FakeBatchTransactionId id = new FakeBatchTransactionId("one");
        Transaction tx = Transaction.create(id);
        PCollectionView<Transaction> txView = pipeline.apply(Create.of(tx)).apply(View.<Transaction>asSingleton());
        BatchSpannerRead.GeneratePartitionsFn fn = new BatchSpannerRead.GeneratePartitionsFn(read.getSpannerConfig(), txView);
        DoFnTester<ReadOperation, Partition> fnTester = DoFnTester.of(fn);
        fnTester.setSideInput(txView, INSTANCE, tx);
        Mockito.when(serviceFactory.mockBatchClient().batchReadOnlyTransaction(id)).thenReturn(mockBatchTx);
        Mockito.when(mockBatchTx.partitionReadUsingIndex(ArgumentMatchers.any(PartitionOptions.class), ArgumentMatchers.eq("users"), ArgumentMatchers.eq("theindex"), ArgumentMatchers.eq(KeySet.all()), ArgumentMatchers.eq(Arrays.asList("id", "name")))).thenReturn(fakePartitions);
        List<Partition> result = fnTester.processBundle(read.getReadOperation());
        Assert.assertThat(result, Matchers.containsInAnyOrder(fakePartitions.toArray()));
        Mockito.verify(serviceFactory.mockBatchClient()).batchReadOnlyTransaction(id);
        Mockito.verify(mockBatchTx).partitionReadUsingIndex(ArgumentMatchers.any(PartitionOptions.class), ArgumentMatchers.eq("users"), ArgumentMatchers.eq("theindex"), ArgumentMatchers.eq(KeySet.all()), ArgumentMatchers.eq(Arrays.asList("id", "name")));
    }

    @Test
    public void readPipeline() throws Exception {
        Timestamp timestamp = Timestamp.ofTimeMicroseconds(12345);
        TimestampBound timestampBound = TimestampBound.ofReadTimestamp(timestamp);
        SpannerConfig spannerConfig = SpannerConfig.create().withProjectId("test").withInstanceId("123").withDatabaseId("aaa").withServiceFactory(serviceFactory);
        PCollection<Struct> one = pipeline.apply("read q", SpannerIO.read().withSpannerConfig(spannerConfig).withQuery("SELECT * FROM users").withTimestampBound(timestampBound));
        FakeBatchTransactionId txId = new FakeBatchTransactionId("readPipelineTest");
        Mockito.when(mockBatchTx.getBatchTransactionId()).thenReturn(txId);
        Mockito.when(serviceFactory.mockBatchClient().batchReadOnlyTransaction(timestampBound)).thenReturn(mockBatchTx);
        Mockito.when(serviceFactory.mockBatchClient().batchReadOnlyTransaction(ArgumentMatchers.any(BatchTransactionId.class))).thenReturn(mockBatchTx);
        Partition fakePartition = FakePartitionFactory.createFakeQueryPartition(ByteString.copyFromUtf8("one"));
        Mockito.when(mockBatchTx.partitionQuery(ArgumentMatchers.any(PartitionOptions.class), ArgumentMatchers.eq(Statement.of("SELECT * FROM users")))).thenReturn(Arrays.asList(fakePartition, fakePartition));
        Mockito.when(mockBatchTx.execute(ArgumentMatchers.any(Partition.class))).thenReturn(ResultSets.forRows(SpannerIOReadTest.FAKE_TYPE, SpannerIOReadTest.FAKE_ROWS.subList(0, 2)), ResultSets.forRows(SpannerIOReadTest.FAKE_TYPE, SpannerIOReadTest.FAKE_ROWS.subList(2, 6)));
        PAssert.that(one).containsInAnyOrder(SpannerIOReadTest.FAKE_ROWS);
        pipeline.run();
    }

    @Test
    public void readAllPipeline() throws Exception {
        Timestamp timestamp = Timestamp.ofTimeMicroseconds(12345);
        TimestampBound timestampBound = TimestampBound.ofReadTimestamp(timestamp);
        SpannerConfig spannerConfig = SpannerConfig.create().withProjectId("test").withInstanceId("123").withDatabaseId("aaa").withServiceFactory(serviceFactory);
        PCollectionView<Transaction> tx = pipeline.apply("tx", SpannerIO.createTransaction().withSpannerConfig(spannerConfig).withTimestampBound(timestampBound));
        PCollection<ReadOperation> reads = pipeline.apply(Create.of(ReadOperation.create().withQuery("SELECT * FROM users"), ReadOperation.create().withTable("users").withColumns("id", "name")));
        PCollection<Struct> one = reads.apply("read all", SpannerIO.readAll().withSpannerConfig(spannerConfig).withTransaction(tx));
        BatchTransactionId txId = new FakeBatchTransactionId("tx");
        Mockito.when(mockBatchTx.getBatchTransactionId()).thenReturn(txId);
        Mockito.when(serviceFactory.mockBatchClient().batchReadOnlyTransaction(timestampBound)).thenReturn(mockBatchTx);
        Mockito.when(serviceFactory.mockBatchClient().batchReadOnlyTransaction(ArgumentMatchers.any(BatchTransactionId.class))).thenReturn(mockBatchTx);
        Partition fakePartition = FakePartitionFactory.createFakeReadPartition(ByteString.copyFromUtf8("partition"));
        Mockito.when(mockBatchTx.partitionQuery(ArgumentMatchers.any(PartitionOptions.class), ArgumentMatchers.eq(Statement.of("SELECT * FROM users")))).thenReturn(Arrays.asList(fakePartition, fakePartition));
        Mockito.when(mockBatchTx.partitionRead(ArgumentMatchers.any(PartitionOptions.class), ArgumentMatchers.eq("users"), ArgumentMatchers.eq(KeySet.all()), ArgumentMatchers.eq(Arrays.asList("id", "name")))).thenReturn(Arrays.asList(fakePartition));
        Mockito.when(mockBatchTx.execute(ArgumentMatchers.any(Partition.class))).thenReturn(ResultSets.forRows(SpannerIOReadTest.FAKE_TYPE, SpannerIOReadTest.FAKE_ROWS.subList(0, 2)), ResultSets.forRows(SpannerIOReadTest.FAKE_TYPE, SpannerIOReadTest.FAKE_ROWS.subList(2, 4)), ResultSets.forRows(SpannerIOReadTest.FAKE_TYPE, SpannerIOReadTest.FAKE_ROWS.subList(4, 6)));
        PAssert.that(one).containsInAnyOrder(SpannerIOReadTest.FAKE_ROWS);
        pipeline.run();
    }
}

