/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.indexing.kinesis;


import RowIngestionMeters.BUILD_SEGMENTS;
import RowIngestionMeters.PROCESSED;
import RowIngestionMeters.PROCESSED_WITH_ERROR;
import RowIngestionMeters.THROWN_AWAY;
import RowIngestionMeters.UNPARSEABLE;
import SeekableStreamIndexTaskRunner.Status;
import Status.PAUSED;
import Status.READING;
import TaskState.FAILED;
import TaskState.SUCCESS;
import TestDerbyConnector.DerbyConnectorRule;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.druid.common.aws.AWSCredentialsConfig;
import org.apache.druid.data.input.impl.FloatDimensionSchema;
import org.apache.druid.data.input.impl.LongDimensionSchema;
import org.apache.druid.data.input.impl.StringDimensionSchema;
import org.apache.druid.data.input.impl.StringInputRowParser;
import org.apache.druid.data.input.impl.TimestampSpec;
import org.apache.druid.indexer.TaskStatus;
import org.apache.druid.indexing.common.IngestionStatsAndErrorsTaskReportData;
import org.apache.druid.indexing.common.TaskToolboxFactory;
import org.apache.druid.indexing.common.stats.RowIngestionMetersFactory;
import org.apache.druid.indexing.common.task.IndexTaskTest;
import org.apache.druid.indexing.common.task.Task;
import org.apache.druid.indexing.common.task.TaskResource;
import org.apache.druid.indexing.overlord.IndexerMetadataStorageCoordinator;
import org.apache.druid.indexing.overlord.TaskLockbox;
import org.apache.druid.indexing.overlord.TaskStorage;
import org.apache.druid.indexing.seekablestream.SeekableStreamPartitions;
import org.apache.druid.indexing.seekablestream.common.OrderedPartitionableRecord;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.java.util.common.granularity.Granularities;
import org.apache.druid.java.util.common.logger.Logger;
import org.apache.druid.java.util.common.parsers.JSONPathSpec;
import org.apache.druid.java.util.emitter.service.ServiceEmitter;
import org.apache.druid.math.expr.ExprMacroTable;
import org.apache.druid.metadata.TestDerbyConnector;
import org.apache.druid.query.SegmentDescriptor;
import org.apache.druid.query.aggregation.AggregatorFactory;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.query.aggregation.DoubleSumAggregatorFactory;
import org.apache.druid.query.filter.SelectorDimFilter;
import org.apache.druid.segment.TestHelper;
import org.apache.druid.segment.indexing.DataSchema;
import org.apache.druid.segment.realtime.firehose.ChatHandlerProvider;
import org.apache.druid.server.security.AuthorizerMapper;
import org.easymock.EasyMockSupport;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static Status.PAUSED;
import static Status.READING;


public class KinesisIndexTaskTest extends EasyMockSupport {
    private static final Logger log = new Logger(KinesisIndexTaskTest.class);

    private static final ObjectMapper objectMapper = TestHelper.makeJsonMapper();

    private static String stream = "stream";

    private static String shardId1 = "1";

    private static String shardId0 = "0";

    private static KinesisRecordSupplier recordSupplier;

    private static List<OrderedPartitionableRecord<String, String>> records = ImmutableList.of(new OrderedPartitionableRecord(KinesisIndexTaskTest.stream, "1", "0", KinesisIndexTaskTest.jb("2008", "a", "y", "10", "20.0", "1.0")), new OrderedPartitionableRecord(KinesisIndexTaskTest.stream, "1", "1", KinesisIndexTaskTest.jb("2009", "b", "y", "10", "20.0", "1.0")), new OrderedPartitionableRecord(KinesisIndexTaskTest.stream, "1", "2", KinesisIndexTaskTest.jb("2010", "c", "y", "10", "20.0", "1.0")), new OrderedPartitionableRecord(KinesisIndexTaskTest.stream, "1", "3", KinesisIndexTaskTest.jb("2011", "d", "y", "10", "20.0", "1.0")), new OrderedPartitionableRecord(KinesisIndexTaskTest.stream, "1", "4", KinesisIndexTaskTest.jb("2011", "e", "y", "10", "20.0", "1.0")), new OrderedPartitionableRecord(KinesisIndexTaskTest.stream, "1", "5", KinesisIndexTaskTest.jb("246140482-04-24T15:36:27.903Z", "x", "z", "10", "20.0", "1.0")), new OrderedPartitionableRecord(KinesisIndexTaskTest.stream, "1", "6", Collections.singletonList(StringUtils.toUtf8("unparseable"))), new OrderedPartitionableRecord(KinesisIndexTaskTest.stream, "1", "7", Collections.singletonList(StringUtils.toUtf8("unparseable2"))), new OrderedPartitionableRecord(KinesisIndexTaskTest.stream, "1", "8", Collections.singletonList(StringUtils.toUtf8("{}"))), new OrderedPartitionableRecord(KinesisIndexTaskTest.stream, "1", "9", KinesisIndexTaskTest.jb("2013", "f", "y", "10", "20.0", "1.0")), new OrderedPartitionableRecord(KinesisIndexTaskTest.stream, "1", "10", KinesisIndexTaskTest.jb("2049", "f", "y", "notanumber", "20.0", "1.0")), new OrderedPartitionableRecord(KinesisIndexTaskTest.stream, "1", "11", KinesisIndexTaskTest.jb("2049", "f", "y", "10", "notanumber", "1.0")), new OrderedPartitionableRecord(KinesisIndexTaskTest.stream, "1", "12", KinesisIndexTaskTest.jb("2049", "f", "y", "10", "20.0", "notanumber")), new OrderedPartitionableRecord(KinesisIndexTaskTest.stream, "0", "0", KinesisIndexTaskTest.jb("2012", "g", "y", "10", "20.0", "1.0")), new OrderedPartitionableRecord(KinesisIndexTaskTest.stream, "0", "1", KinesisIndexTaskTest.jb("2011", "h", "y", "10", "20.0", "1.0")));

    private static ServiceEmitter emitter;

    private static ListeningExecutorService taskExec;

    private final List<Task> runningTasks = new ArrayList<>();

    private long handoffConditionTimeout = 0;

    private boolean reportParseExceptions = false;

    private boolean logParseExceptions = true;

    private Integer maxParseExceptions = null;

    private Integer maxSavedParseExceptions = null;

    private boolean resetOffsetAutomatically = false;

    private boolean doHandoff = true;

    private int maxRowsInMemory = 1000;

    private Integer maxRowsPerSegment = null;

    private Long maxTotalRows = null;

    private Period intermediateHandoffPeriod = null;

    private int maxRecordsPerPoll;

    private boolean skipAvailabilityCheck = false;

    private TaskToolboxFactory toolboxFactory;

    private IndexerMetadataStorageCoordinator metadataStorageCoordinator;

    private TaskStorage taskStorage;

    private TaskLockbox taskLockbox;

    private File directory;

    private final Set<Integer> checkpointRequestsHash = new HashSet<>();

    private File reportsFile;

    private RowIngestionMetersFactory rowIngestionMetersFactory;

    private static final DataSchema DATA_SCHEMA = new DataSchema("test_ds", KinesisIndexTaskTest.objectMapper.convertValue(new StringInputRowParser(new org.apache.druid.data.input.impl.JSONParseSpec(new TimestampSpec("timestamp", "iso", null), new org.apache.druid.data.input.impl.DimensionsSpec(Arrays.asList(new StringDimensionSchema("dim1"), new StringDimensionSchema("dim1t"), new StringDimensionSchema("dim2"), new LongDimensionSchema("dimLong"), new FloatDimensionSchema("dimFloat")), null, null), new JSONPathSpec(true, ImmutableList.of()), ImmutableMap.of()), StandardCharsets.UTF_8.name()), Map.class), new AggregatorFactory[]{ new DoubleSumAggregatorFactory("met1sum", "met1"), new CountAggregatorFactory("rows") }, new org.apache.druid.segment.indexing.granularity.UniformGranularitySpec(Granularities.DAY, Granularities.NONE, null), null, KinesisIndexTaskTest.objectMapper);

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    @Rule
    public final DerbyConnectorRule derby = new TestDerbyConnector.DerbyConnectorRule();

    @Test(timeout = 120000L)
    public void testRunAfterDataInserted() throws Exception {
        KinesisIndexTaskTest.recordSupplier.assign(anyObject());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.getEarliestSequenceNumber(anyObject())).andReturn("0").anyTimes();
        KinesisIndexTaskTest.recordSupplier.seek(anyObject(), anyString());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.poll(anyLong())).andReturn(KinesisIndexTaskTest.records.subList(2, 5)).once();
        KinesisIndexTaskTest.recordSupplier.close();
        expectLastCall().once();
        replayAll();
        final KinesisIndexTask task = createTask(null, new KinesisIndexTaskIOConfig(null, "sequence0", new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "2")), new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "4")), true, null, null, "awsEndpoint", null, null, null, null, null, false));
        final ListenableFuture<TaskStatus> future = runTask(task);
        // Wait for task to exit
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
        verifyAll();
        // Check metrics
        Assert.assertEquals(3, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published metadata
        SegmentDescriptor desc1 = sd(task, "2010/P1D", 0);
        SegmentDescriptor desc2 = sd(task, "2011/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2), publishedDescriptors());
        Assert.assertEquals(new KinesisDataSourceMetadata(new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "4"))), metadataStorageCoordinator.getDataSourceMetadata(KinesisIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("d", "e"), readSegmentColumn("dim1", desc2));
    }

    @Test(timeout = 120000L)
    public void testRunBeforeDataInserted() throws Exception {
        KinesisIndexTaskTest.recordSupplier.assign(anyObject());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.getEarliestSequenceNumber(anyObject())).andReturn("0").anyTimes();
        KinesisIndexTaskTest.recordSupplier.seek(anyObject(), anyString());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.poll(anyLong())).andReturn(Collections.emptyList()).times(5).andReturn(KinesisIndexTaskTest.records.subList(13, 15)).once();
        KinesisIndexTaskTest.recordSupplier.close();
        expectLastCall().once();
        replayAll();
        final KinesisIndexTask task = createTask(null, new KinesisIndexTaskIOConfig(null, "sequence0", new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId0, "0")), new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId0, "1")), true, null, null, "awsEndpoint", null, null, null, null, null, false));
        final ListenableFuture<TaskStatus> future = runTask(task);
        // Wait for task to exit
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
        verifyAll();
        // Check metrics
        Assert.assertEquals(2, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published metadata
        SegmentDescriptor desc1 = sd(task, "2011/P1D", 0);
        SegmentDescriptor desc2 = sd(task, "2012/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2), publishedDescriptors());
        Assert.assertEquals(new KinesisDataSourceMetadata(new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId0, "1"))), metadataStorageCoordinator.getDataSourceMetadata(KinesisIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("h"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("g"), readSegmentColumn("dim1", desc2));
    }

    @Test(timeout = 120000L)
    public void testIncrementalHandOff() throws Exception {
        final String baseSequenceName = "sequence0";
        // as soon as any segment has more than one record, incremental publishing should happen
        maxRowsPerSegment = 2;
        maxRecordsPerPoll = 1;
        KinesisIndexTaskTest.recordSupplier.assign(anyObject());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.getEarliestSequenceNumber(anyObject())).andReturn("0").anyTimes();
        KinesisIndexTaskTest.recordSupplier.seek(anyObject(), anyString());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.poll(anyLong())).andReturn(KinesisIndexTaskTest.records.subList(0, 5)).once().andReturn(KinesisIndexTaskTest.records.subList(4, KinesisIndexTaskTest.records.size())).once();
        KinesisIndexTaskTest.recordSupplier.close();
        expectLastCall().once();
        replayAll();
        final SeekableStreamPartitions<String, String> startPartitions = new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "0", KinesisIndexTaskTest.shardId0, "0"));
        final SeekableStreamPartitions<String, String> checkpoint1 = new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "4", KinesisIndexTaskTest.shardId0, "0"));
        final SeekableStreamPartitions<String, String> endPartitions = new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "9", KinesisIndexTaskTest.shardId0, "1"));
        final KinesisIndexTask task = createTask(null, new KinesisIndexTaskIOConfig(null, baseSequenceName, startPartitions, endPartitions, true, null, null, "awsEndpoint", null, null, null, null, null, false));
        final ListenableFuture<TaskStatus> future = runTask(task);
        while ((task.getRunner().getStatus()) != (Status.PAUSED)) {
            Thread.sleep(10);
        } 
        final Map<String, String> currentOffsets = ImmutableMap.copyOf(task.getRunner().getCurrentOffsets());
        Assert.assertTrue(checkpoint1.getPartitionSequenceNumberMap().equals(currentOffsets));
        task.getRunner().setEndOffsets(currentOffsets, false);
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
        verifyAll();
        Assert.assertEquals(1, checkpointRequestsHash.size());
        Assert.assertTrue(checkpointRequestsHash.contains(Objects.hash(KinesisIndexTaskTest.DATA_SCHEMA.getDataSource(), 0, new KinesisDataSourceMetadata(startPartitions), new KinesisDataSourceMetadata(new SeekableStreamPartitions(KinesisIndexTaskTest.stream, currentOffsets)))));
        // Check metrics
        Assert.assertEquals(8, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(4, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published metadata
        SegmentDescriptor desc1 = sd(task, "2008/P1D", 0);
        SegmentDescriptor desc2 = sd(task, "2009/P1D", 0);
        SegmentDescriptor desc3 = sd(task, "2010/P1D", 0);
        SegmentDescriptor desc4 = sd(task, "2011/P1D", 0);
        SegmentDescriptor desc5 = sd(task, "2011/P1D", 1);
        SegmentDescriptor desc6 = sd(task, "2012/P1D", 0);
        SegmentDescriptor desc7 = sd(task, "2013/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2, desc3, desc4, desc5, desc6, desc7), publishedDescriptors());
        Assert.assertEquals(new KinesisDataSourceMetadata(new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "9", KinesisIndexTaskTest.shardId0, "1"))), metadataStorageCoordinator.getDataSourceMetadata(KinesisIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("a"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("b"), readSegmentColumn("dim1", desc2));
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc3));
        Assert.assertTrue((((ImmutableList.of("d", "e").equals(readSegmentColumn("dim1", desc4))) && (ImmutableList.of("h").equals(readSegmentColumn("dim1", desc5)))) || ((ImmutableList.of("d", "h").equals(readSegmentColumn("dim1", desc4))) && (ImmutableList.of("e").equals(readSegmentColumn("dim1", desc5))))));
        Assert.assertEquals(ImmutableList.of("g"), readSegmentColumn("dim1", desc6));
        Assert.assertEquals(ImmutableList.of("f"), readSegmentColumn("dim1", desc7));
    }

    @Test(timeout = 120000L)
    public void testIncrementalHandOffMaxTotalRows() throws Exception {
        final String baseSequenceName = "sequence0";
        // incremental publish should happen every 3 records
        maxRowsPerSegment = Integer.MAX_VALUE;
        maxTotalRows = 3L;
        KinesisIndexTaskTest.recordSupplier.assign(anyObject());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.getEarliestSequenceNumber(anyObject())).andReturn("0").anyTimes();
        KinesisIndexTaskTest.recordSupplier.seek(anyObject(), anyString());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.poll(anyLong())).andReturn(KinesisIndexTaskTest.records.subList(0, 3)).once().andReturn(KinesisIndexTaskTest.records.subList(2, 10)).once().andReturn(KinesisIndexTaskTest.records.subList(9, 11));
        KinesisIndexTaskTest.recordSupplier.close();
        expectLastCall().once();
        replayAll();
        // Insert data
        final SeekableStreamPartitions<String, String> startPartitions = new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "0"));
        // Checkpointing will happen at either checkpoint1 or checkpoint2 depending on ordering
        // of events fetched across two partitions from Kafka
        final SeekableStreamPartitions<String, String> checkpoint1 = new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "2"));
        final SeekableStreamPartitions<String, String> checkpoint2 = new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "9"));
        final SeekableStreamPartitions<String, String> endPartitions = new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "10"));
        final KinesisIndexTask task = createTask(null, new KinesisIndexTaskIOConfig(null, baseSequenceName, startPartitions, endPartitions, true, null, null, "awsEndpoint", null, null, null, null, null, false));
        final ListenableFuture<TaskStatus> future = runTask(task);
        while ((task.getRunner().getStatus()) != (PAUSED)) {
            Thread.sleep(10);
        } 
        final Map<String, String> currentOffsets = ImmutableMap.copyOf(task.getRunner().getCurrentOffsets());
        Assert.assertEquals(checkpoint1.getPartitionSequenceNumberMap(), currentOffsets);
        task.getRunner().setEndOffsets(currentOffsets, false);
        while ((task.getRunner().getStatus()) != (PAUSED)) {
            Thread.sleep(10);
        } 
        final Map<String, String> nextOffsets = ImmutableMap.copyOf(task.getRunner().getCurrentOffsets());
        Assert.assertEquals(checkpoint2.getPartitionSequenceNumberMap(), nextOffsets);
        task.getRunner().setEndOffsets(nextOffsets, false);
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
        verifyAll();
        Assert.assertEquals(2, checkpointRequestsHash.size());
        Assert.assertTrue(checkpointRequestsHash.contains(Objects.hash(KinesisIndexTaskTest.DATA_SCHEMA.getDataSource(), 0, new KinesisDataSourceMetadata(startPartitions), new KinesisDataSourceMetadata(new SeekableStreamPartitions(KinesisIndexTaskTest.stream, currentOffsets)))));
        Assert.assertTrue(checkpointRequestsHash.contains(Objects.hash(KinesisIndexTaskTest.DATA_SCHEMA.getDataSource(), 0, new KinesisDataSourceMetadata(new SeekableStreamPartitions(KinesisIndexTaskTest.stream, currentOffsets)), new KinesisDataSourceMetadata(new SeekableStreamPartitions(KinesisIndexTaskTest.stream, nextOffsets)))));
        // Check metrics
        Assert.assertEquals(6, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(4, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published metadata
        SegmentDescriptor desc1 = sd(task, "2008/P1D", 0);
        SegmentDescriptor desc2 = sd(task, "2009/P1D", 0);
        SegmentDescriptor desc3 = sd(task, "2010/P1D", 0);
        SegmentDescriptor desc4 = sd(task, "2011/P1D", 0);
        SegmentDescriptor desc5 = sd(task, "2049/P1D", 0);
        SegmentDescriptor desc7 = sd(task, "2013/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2, desc3, desc4, desc5, desc7), publishedDescriptors());
        Assert.assertEquals(new KinesisDataSourceMetadata(new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "10"))), metadataStorageCoordinator.getDataSourceMetadata(KinesisIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("a"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("b"), readSegmentColumn("dim1", desc2));
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc3));
        Assert.assertEquals(ImmutableList.of("d", "e"), readSegmentColumn("dim1", desc4));
        Assert.assertEquals(ImmutableList.of("f"), readSegmentColumn("dim1", desc5));
        Assert.assertEquals(ImmutableList.of("f"), readSegmentColumn("dim1", desc7));
    }

    @Test(timeout = 120000L)
    public void testRunWithMinimumMessageTime() throws Exception {
        KinesisIndexTaskTest.recordSupplier.assign(anyObject());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.getEarliestSequenceNumber(anyObject())).andReturn("0").anyTimes();
        KinesisIndexTaskTest.recordSupplier.seek(anyObject(), anyString());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.poll(anyLong())).andReturn(KinesisIndexTaskTest.records.subList(0, 13)).once();
        KinesisIndexTaskTest.recordSupplier.close();
        expectLastCall().once();
        replayAll();
        final KinesisIndexTask task = createTask(null, new KinesisIndexTaskIOConfig(null, "sequence0", new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "0")), new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "4")), true, DateTimes.of("2010"), null, "awsEndpoint", null, null, null, null, null, false));
        final ListenableFuture<TaskStatus> future = runTask(task);
        // Wait for the task to start reading
        while ((task.getRunner().getStatus()) != (READING)) {
            Thread.sleep(10);
        } 
        // Wait for task to exit
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
        verifyAll();
        // Check metrics
        Assert.assertEquals(3, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(2, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published metadata
        SegmentDescriptor desc1 = sd(task, "2010/P1D", 0);
        SegmentDescriptor desc2 = sd(task, "2011/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2), publishedDescriptors());
        Assert.assertEquals(new KinesisDataSourceMetadata(new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "4"))), metadataStorageCoordinator.getDataSourceMetadata(KinesisIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("d", "e"), readSegmentColumn("dim1", desc2));
    }

    @Test(timeout = 120000L)
    public void testRunWithMaximumMessageTime() throws Exception {
        KinesisIndexTaskTest.recordSupplier.assign(anyObject());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.getEarliestSequenceNumber(anyObject())).andReturn("0").anyTimes();
        KinesisIndexTaskTest.recordSupplier.seek(anyObject(), anyString());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.poll(anyLong())).andReturn(KinesisIndexTaskTest.records.subList(0, 13)).once();
        KinesisIndexTaskTest.recordSupplier.close();
        expectLastCall().once();
        replayAll();
        final KinesisIndexTask task = createTask(null, new KinesisIndexTaskIOConfig(null, "sequence0", new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "0")), new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "4")), true, null, DateTimes.of("2010"), "awsEndpoint", null, null, null, null, null, false));
        final ListenableFuture<TaskStatus> future = runTask(task);
        // Wait for the task to start reading
        while ((task.getRunner().getStatus()) != (READING)) {
            Thread.sleep(10);
        } 
        // Wait for task to exit
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
        verifyAll();
        // Check metrics
        Assert.assertEquals(3, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(2, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published metadata
        SegmentDescriptor desc1 = sd(task, "2008/P1D", 0);
        SegmentDescriptor desc2 = sd(task, "2009/P1D", 0);
        SegmentDescriptor desc3 = sd(task, "2010/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2, desc3), publishedDescriptors());
        Assert.assertEquals(new KinesisDataSourceMetadata(new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "4"))), metadataStorageCoordinator.getDataSourceMetadata(KinesisIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("a"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("b"), readSegmentColumn("dim1", desc2));
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc3));
    }

    @Test(timeout = 120000L)
    public void testRunWithTransformSpec() throws Exception {
        KinesisIndexTaskTest.recordSupplier.assign(anyObject());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.getEarliestSequenceNumber(anyObject())).andReturn("0").anyTimes();
        KinesisIndexTaskTest.recordSupplier.seek(anyObject(), anyString());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.poll(anyLong())).andReturn(KinesisIndexTaskTest.records.subList(0, 13)).once();
        KinesisIndexTaskTest.recordSupplier.close();
        expectLastCall().once();
        replayAll();
        final KinesisIndexTask task = createTask(null, KinesisIndexTaskTest.DATA_SCHEMA.withTransformSpec(new org.apache.druid.segment.transform.TransformSpec(new SelectorDimFilter("dim1", "b", null), ImmutableList.of(new org.apache.druid.segment.transform.ExpressionTransform("dim1t", "concat(dim1,dim1)", ExprMacroTable.nil())))), new KinesisIndexTaskIOConfig(null, "sequence0", new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "0")), new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "4")), true, null, null, "awsEndpoint", null, null, null, null, null, false));
        final ListenableFuture<TaskStatus> future = runTask(task);
        // Wait for the task to start reading
        while ((task.getRunner().getStatus()) != (READING)) {
            Thread.sleep(10);
        } 
        // Wait for task to exit
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
        verifyAll();
        // Check metrics
        Assert.assertEquals(1, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(4, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published metadata
        SegmentDescriptor desc1 = sd(task, "2009/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1), publishedDescriptors());
        Assert.assertEquals(new KinesisDataSourceMetadata(new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "4"))), metadataStorageCoordinator.getDataSourceMetadata(KinesisIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("b"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("bb"), readSegmentColumn("dim1t", desc1));
    }

    @Test(timeout = 120000L)
    public void testRunOnNothing() throws Exception {
        KinesisIndexTaskTest.recordSupplier.assign(anyObject());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.getEarliestSequenceNumber(anyObject())).andReturn("0").anyTimes();
        KinesisIndexTaskTest.recordSupplier.seek(anyObject(), anyString());
        expectLastCall().anyTimes();
        KinesisIndexTaskTest.recordSupplier.close();
        expectLastCall().once();
        replayAll();
        final KinesisIndexTask task = createTask(null, new KinesisIndexTaskIOConfig(null, "sequence0", new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "2")), new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "2")), true, null, null, "awsEndpoint", null, null, null, null, null, false));
        final ListenableFuture<TaskStatus> future = runTask(task);
        // Wait for task to exit
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
        verifyAll();
        // Check metrics
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published metadata
        Assert.assertEquals(ImmutableSet.of(), publishedDescriptors());
    }

    @Test(timeout = 60000L)
    public void testHandoffConditionTimeoutWhenHandoffOccurs() throws Exception {
        handoffConditionTimeout = 5000;
        KinesisIndexTaskTest.recordSupplier.assign(anyObject());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.getEarliestSequenceNumber(anyObject())).andReturn("0").anyTimes();
        KinesisIndexTaskTest.recordSupplier.seek(anyObject(), anyString());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.poll(anyLong())).andReturn(KinesisIndexTaskTest.records.subList(2, 13)).once();
        KinesisIndexTaskTest.recordSupplier.close();
        expectLastCall().once();
        replayAll();
        final KinesisIndexTask task = createTask(null, new KinesisIndexTaskIOConfig(null, "sequence0", new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "2")), new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "4")), true, null, null, "awsEndpoint", null, null, null, null, null, false));
        final ListenableFuture<TaskStatus> future = runTask(task);
        // Wait for task to exit
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
        verifyAll();
        // Check metrics
        Assert.assertEquals(3, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published metadata
        SegmentDescriptor desc1 = sd(task, "2010/P1D", 0);
        SegmentDescriptor desc2 = sd(task, "2011/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2), publishedDescriptors());
        Assert.assertEquals(new KinesisDataSourceMetadata(new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "4"))), metadataStorageCoordinator.getDataSourceMetadata(KinesisIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("d", "e"), readSegmentColumn("dim1", desc2));
    }

    @Test(timeout = 60000L)
    public void testHandoffConditionTimeoutWhenHandoffDoesNotOccur() throws Exception {
        doHandoff = false;
        handoffConditionTimeout = 100;
        KinesisIndexTaskTest.recordSupplier.assign(anyObject());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.getEarliestSequenceNumber(anyObject())).andReturn("0").anyTimes();
        KinesisIndexTaskTest.recordSupplier.seek(anyObject(), anyString());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.poll(anyLong())).andReturn(KinesisIndexTaskTest.records.subList(2, 13)).once();
        KinesisIndexTaskTest.recordSupplier.close();
        expectLastCall().once();
        replayAll();
        final KinesisIndexTask task = createTask(null, new KinesisIndexTaskIOConfig(null, "sequence0", new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "2")), new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "4")), true, null, null, "awsEndpoint", null, null, null, null, null, false));
        final ListenableFuture<TaskStatus> future = runTask(task);
        // Wait for task to exit
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
        verifyAll();
        // Check metrics
        Assert.assertEquals(3, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published metadata
        SegmentDescriptor desc1 = sd(task, "2010/P1D", 0);
        SegmentDescriptor desc2 = sd(task, "2011/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2), publishedDescriptors());
        Assert.assertEquals(new KinesisDataSourceMetadata(new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "4"))), metadataStorageCoordinator.getDataSourceMetadata(KinesisIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("d", "e"), readSegmentColumn("dim1", desc2));
    }

    @Test(timeout = 120000L)
    public void testReportParseExceptions() throws Exception {
        reportParseExceptions = true;
        // these will be ignored because reportParseExceptions is true
        maxParseExceptions = 1000;
        maxSavedParseExceptions = 2;
        KinesisIndexTaskTest.recordSupplier.assign(anyObject());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.getEarliestSequenceNumber(anyObject())).andReturn("0").anyTimes();
        KinesisIndexTaskTest.recordSupplier.seek(anyObject(), anyString());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.poll(anyLong())).andReturn(KinesisIndexTaskTest.records.subList(2, 13)).once();
        replayAll();
        final KinesisIndexTask task = createTask(null, new KinesisIndexTaskIOConfig(null, "sequence0", new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "2")), new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "5")), true, null, null, "awsEndpoint", null, null, null, null, null, false));
        final ListenableFuture<TaskStatus> future = runTask(task);
        // Wait for task to exit
        Assert.assertEquals(FAILED, future.get().getStatusCode());
        verifyAll();
        // Check metrics
        Assert.assertEquals(3, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(1, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published metadata
        Assert.assertEquals(ImmutableSet.of(), publishedDescriptors());
        Assert.assertNull(metadataStorageCoordinator.getDataSourceMetadata(KinesisIndexTaskTest.DATA_SCHEMA.getDataSource()));
    }

    @Test(timeout = 120000L)
    public void testMultipleParseExceptionsSuccess() throws Exception {
        reportParseExceptions = false;
        maxParseExceptions = 7;
        maxSavedParseExceptions = 7;
        KinesisIndexTaskTest.recordSupplier.assign(anyObject());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.getEarliestSequenceNumber(anyObject())).andReturn("0").anyTimes();
        KinesisIndexTaskTest.recordSupplier.seek(anyObject(), anyString());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.poll(anyLong())).andReturn(KinesisIndexTaskTest.records.subList(2, 13)).once();
        KinesisIndexTaskTest.recordSupplier.close();
        expectLastCall().once();
        replayAll();
        final KinesisIndexTask task = createTask(null, new KinesisIndexTaskIOConfig(null, "sequence0", new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "2")), new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "12")), true, null, null, "awsEndpoint", null, null, null, null, null, false));
        final ListenableFuture<TaskStatus> future = runTask(task);
        TaskStatus status = future.get();
        // Wait for task to exit
        Assert.assertEquals(SUCCESS, status.getStatusCode());
        verifyAll();
        Assert.assertNull(status.getErrorMsg());
        // Check metrics
        Assert.assertEquals(4, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(3, task.getRunner().getRowIngestionMeters().getProcessedWithError());
        Assert.assertEquals(4, task.getRunner().getRowIngestionMeters().getUnparseable());
        // Check published metadata
        SegmentDescriptor desc1 = sd(task, "2010/P1D", 0);
        SegmentDescriptor desc2 = sd(task, "2011/P1D", 0);
        SegmentDescriptor desc3 = sd(task, "2013/P1D", 0);
        SegmentDescriptor desc4 = sd(task, "2049/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2, desc3, desc4), publishedDescriptors());
        Assert.assertEquals(new KinesisDataSourceMetadata(new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "12"))), metadataStorageCoordinator.getDataSourceMetadata(KinesisIndexTaskTest.DATA_SCHEMA.getDataSource()));
        IngestionStatsAndErrorsTaskReportData reportData = getTaskReportData();
        Map<String, Object> expectedMetrics = ImmutableMap.of(BUILD_SEGMENTS, ImmutableMap.of(PROCESSED, 4, PROCESSED_WITH_ERROR, 3, UNPARSEABLE, 4, THROWN_AWAY, 0));
        Assert.assertEquals(expectedMetrics, reportData.getRowStats());
        Map<String, Object> unparseableEvents = ImmutableMap.of(BUILD_SEGMENTS, Arrays.asList("Found unparseable columns in row: [MapBasedInputRow{timestamp=2049-01-01T00:00:00.000Z, event={timestamp=2049, dim1=f, dim2=y, dimLong=10, dimFloat=20.0, met1=notanumber}, dimensions=[dim1, dim1t, dim2, dimLong, dimFloat]}], exceptions: [Unable to parse value[notanumber] for field[met1],]", "Found unparseable columns in row: [MapBasedInputRow{timestamp=2049-01-01T00:00:00.000Z, event={timestamp=2049, dim1=f, dim2=y, dimLong=10, dimFloat=notanumber, met1=1.0}, dimensions=[dim1, dim1t, dim2, dimLong, dimFloat]}], exceptions: [could not convert value [notanumber] to float,]", "Found unparseable columns in row: [MapBasedInputRow{timestamp=2049-01-01T00:00:00.000Z, event={timestamp=2049, dim1=f, dim2=y, dimLong=notanumber, dimFloat=20.0, met1=1.0}, dimensions=[dim1, dim1t, dim2, dimLong, dimFloat]}], exceptions: [could not convert value [notanumber] to long,]", "Unparseable timestamp found! Event: {}", "Unable to parse row [unparseable2]", "Unable to parse row [unparseable]", "Encountered row with timestamp that cannot be represented as a long: [MapBasedInputRow{timestamp=246140482-04-24T15:36:27.903Z, event={timestamp=246140482-04-24T15:36:27.903Z, dim1=x, dim2=z, dimLong=10, dimFloat=20.0, met1=1.0}, dimensions=[dim1, dim1t, dim2, dimLong, dimFloat]}]"));
        Assert.assertEquals(unparseableEvents, reportData.getUnparseableEvents());
    }

    @Test(timeout = 120000L)
    public void testMultipleParseExceptionsFailure() throws Exception {
        reportParseExceptions = false;
        maxParseExceptions = 2;
        maxSavedParseExceptions = 2;
        KinesisIndexTaskTest.recordSupplier.assign(anyObject());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.getEarliestSequenceNumber(anyObject())).andReturn("0").anyTimes();
        KinesisIndexTaskTest.recordSupplier.seek(anyObject(), anyString());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.poll(anyLong())).andReturn(KinesisIndexTaskTest.records.subList(2, 13)).once();
        replayAll();
        final KinesisIndexTask task = createTask(null, new KinesisIndexTaskIOConfig(null, "sequence0", new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "2")), new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "9")), true, null, null, "awsEndpoint", null, null, null, null, null, false));
        final ListenableFuture<TaskStatus> future = runTask(task);
        TaskStatus status = future.get();
        // Wait for task to exit
        Assert.assertEquals(FAILED, status.getStatusCode());
        verifyAll();
        IndexTaskTest.checkTaskStatusErrorMsgForParseExceptionsExceeded(status);
        // Check metrics
        Assert.assertEquals(3, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getProcessedWithError());
        Assert.assertEquals(3, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published metadata
        Assert.assertEquals(ImmutableSet.of(), publishedDescriptors());
        Assert.assertNull(metadataStorageCoordinator.getDataSourceMetadata(KinesisIndexTaskTest.DATA_SCHEMA.getDataSource()));
        IngestionStatsAndErrorsTaskReportData reportData = getTaskReportData();
        Map<String, Object> expectedMetrics = ImmutableMap.of(BUILD_SEGMENTS, ImmutableMap.of(PROCESSED, 3, PROCESSED_WITH_ERROR, 0, UNPARSEABLE, 3, THROWN_AWAY, 0));
        Assert.assertEquals(expectedMetrics, reportData.getRowStats());
        Map<String, Object> unparseableEvents = ImmutableMap.of(BUILD_SEGMENTS, Arrays.asList("Unable to parse row [unparseable2]", "Unable to parse row [unparseable]"));
        Assert.assertEquals(unparseableEvents, reportData.getUnparseableEvents());
    }

    @Test(timeout = 120000L)
    public void testRunReplicas() throws Exception {
        // Insert data
        KinesisIndexTaskTest.recordSupplier.assign(anyObject());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.getEarliestSequenceNumber(anyObject())).andReturn("0").anyTimes();
        KinesisIndexTaskTest.recordSupplier.seek(anyObject(), anyString());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.poll(anyLong())).andReturn(KinesisIndexTaskTest.records.subList(2, 13)).times(2);
        KinesisIndexTaskTest.recordSupplier.close();
        expectLastCall().times(2);
        replayAll();
        final KinesisIndexTask task1 = createTask(null, new KinesisIndexTaskIOConfig(null, "sequence0", new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "2")), new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "4")), true, null, null, "awsEndpoint", null, null, null, null, null, false));
        final KinesisIndexTask task2 = createTask(null, new KinesisIndexTaskIOConfig(null, "sequence0", new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "2")), new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "4")), true, null, null, "awsEndpoint", null, null, null, null, null, false));
        final ListenableFuture<TaskStatus> future1 = runTask(task1);
        final ListenableFuture<TaskStatus> future2 = runTask(task2);
        // Wait for tasks to exit
        Assert.assertEquals(SUCCESS, future1.get().getStatusCode());
        Assert.assertEquals(SUCCESS, future2.get().getStatusCode());
        verifyAll();
        // Check metrics
        Assert.assertEquals(3, task1.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task1.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task1.getRunner().getRowIngestionMeters().getThrownAway());
        Assert.assertEquals(3, task2.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task2.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task2.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published segments & metadata
        SegmentDescriptor desc1 = sd(task1, "2010/P1D", 0);
        SegmentDescriptor desc2 = sd(task1, "2011/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2), publishedDescriptors());
        Assert.assertEquals(new KinesisDataSourceMetadata(new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "4"))), metadataStorageCoordinator.getDataSourceMetadata(KinesisIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("d", "e"), readSegmentColumn("dim1", desc2));
    }

    @Test(timeout = 120000L)
    public void testRunConflicting() throws Exception {
        KinesisIndexTaskTest.recordSupplier.assign(anyObject());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.getEarliestSequenceNumber(anyObject())).andReturn("0").anyTimes();
        KinesisIndexTaskTest.recordSupplier.seek(anyObject(), anyString());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.poll(anyLong())).andReturn(KinesisIndexTaskTest.records.subList(2, 13)).once().andReturn(KinesisIndexTaskTest.records.subList(3, 13)).once();
        KinesisIndexTaskTest.recordSupplier.close();
        expectLastCall().once();
        replayAll();
        final KinesisIndexTask task1 = createTask(null, new KinesisIndexTaskIOConfig(null, "sequence0", new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "2")), new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "4")), true, null, null, "awsEndpoint", null, null, null, null, null, false));
        final KinesisIndexTask task2 = createTask(null, new KinesisIndexTaskIOConfig(null, "sequence1", new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "3")), new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "9")), true, null, null, "awsEndpoint", null, null, null, null, null, false));
        // Run first task
        final ListenableFuture<TaskStatus> future1 = runTask(task1);
        Assert.assertEquals(SUCCESS, future1.get().getStatusCode());
        // Run second task
        final ListenableFuture<TaskStatus> future2 = runTask(task2);
        Assert.assertEquals(FAILED, future2.get().getStatusCode());
        verifyAll();
        // Check metrics
        Assert.assertEquals(3, task1.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task1.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task1.getRunner().getRowIngestionMeters().getThrownAway());
        Assert.assertEquals(3, task2.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(4, task2.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task2.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published segments & metadata, should all be from the first task
        SegmentDescriptor desc1 = sd(task1, "2010/P1D", 0);
        SegmentDescriptor desc2 = sd(task1, "2011/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2), publishedDescriptors());
        Assert.assertEquals(new KinesisDataSourceMetadata(new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "4"))), metadataStorageCoordinator.getDataSourceMetadata(KinesisIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("d", "e"), readSegmentColumn("dim1", desc2));
    }

    @Test(timeout = 120000L)
    public void testRunConflictingWithoutTransactions() throws Exception {
        KinesisIndexTaskTest.recordSupplier.assign(anyObject());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.getEarliestSequenceNumber(anyObject())).andReturn("0").anyTimes();
        KinesisIndexTaskTest.recordSupplier.seek(anyObject(), anyString());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.poll(anyLong())).andReturn(KinesisIndexTaskTest.records.subList(2, 13)).once().andReturn(KinesisIndexTaskTest.records.subList(3, 13)).once();
        KinesisIndexTaskTest.recordSupplier.close();
        expectLastCall().times(2);
        replayAll();
        final KinesisIndexTask task1 = createTask(null, new KinesisIndexTaskIOConfig(null, "sequence0", new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "2")), new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "4")), false, null, null, "awsEndpoint", null, null, null, null, null, false));
        final KinesisIndexTask task2 = createTask(null, new KinesisIndexTaskIOConfig(null, "sequence1", new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "3")), new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "9")), false, null, null, "awsEndpoint", null, null, null, null, null, false));
        // Run first task
        final ListenableFuture<TaskStatus> future1 = runTask(task1);
        Assert.assertEquals(SUCCESS, future1.get().getStatusCode());
        // Check published segments & metadata
        SegmentDescriptor desc1 = sd(task1, "2010/P1D", 0);
        SegmentDescriptor desc2 = sd(task1, "2011/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2), publishedDescriptors());
        Assert.assertNull(metadataStorageCoordinator.getDataSourceMetadata(KinesisIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Run second task
        final ListenableFuture<TaskStatus> future2 = runTask(task2);
        Assert.assertEquals(SUCCESS, future2.get().getStatusCode());
        verifyAll();
        // Check metrics
        Assert.assertEquals(3, task1.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task1.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task1.getRunner().getRowIngestionMeters().getThrownAway());
        Assert.assertEquals(3, task2.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(4, task2.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task2.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published segments & metadata
        SegmentDescriptor desc3 = sd(task2, "2011/P1D", 1);
        SegmentDescriptor desc4 = sd(task2, "2013/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2, desc3, desc4), publishedDescriptors());
        Assert.assertNull(metadataStorageCoordinator.getDataSourceMetadata(KinesisIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("d", "e"), readSegmentColumn("dim1", desc2));
        Assert.assertEquals(ImmutableList.of("d", "e"), readSegmentColumn("dim1", desc3));
        Assert.assertEquals(ImmutableList.of("f"), readSegmentColumn("dim1", desc4));
    }

    @Test(timeout = 120000L)
    public void testRunOneTaskTwoPartitions() throws Exception {
        // Insert data
        KinesisIndexTaskTest.recordSupplier.assign(anyObject());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.getEarliestSequenceNumber(anyObject())).andReturn("0").anyTimes();
        KinesisIndexTaskTest.recordSupplier.seek(anyObject(), anyString());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.poll(anyLong())).andReturn(KinesisIndexTaskTest.records.subList(2, KinesisIndexTaskTest.records.size())).once();
        KinesisIndexTaskTest.recordSupplier.close();
        expectLastCall().once();
        replayAll();
        final KinesisIndexTask task = createTask(null, new KinesisIndexTaskIOConfig(null, "sequence1", new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "2", KinesisIndexTaskTest.shardId0, "0")), new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "4", KinesisIndexTaskTest.shardId0, "1")), true, null, null, "awsEndpoint", null, null, null, null, null, false));
        final ListenableFuture<TaskStatus> future = runTask(task);
        while ((countEvents(task)) < 5) {
            Thread.sleep(10);
        } 
        // Wait for tasks to exit
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
        verifyAll();
        // Check metrics
        Assert.assertEquals(5, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published segments & metadata
        SegmentDescriptor desc1 = sd(task, "2010/P1D", 0);
        SegmentDescriptor desc2 = sd(task, "2011/P1D", 0);
        SegmentDescriptor desc4 = sd(task, "2012/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2, desc4), publishedDescriptors());
        Assert.assertEquals(new KinesisDataSourceMetadata(new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "4", KinesisIndexTaskTest.shardId0, "1"))), metadataStorageCoordinator.getDataSourceMetadata(KinesisIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("g"), readSegmentColumn("dim1", desc4));
        // Check desc2/desc3 without strong ordering because two partitions are interleaved nondeterministically
        Assert.assertEquals(ImmutableSet.of(ImmutableList.of("d", "e", "h")), ImmutableSet.of(readSegmentColumn("dim1", desc2)));
    }

    @Test(timeout = 120000L)
    public void testRunTwoTasksTwoPartitions() throws Exception {
        KinesisIndexTaskTest.recordSupplier.assign(anyObject());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.getEarliestSequenceNumber(anyObject())).andReturn("0").anyTimes();
        KinesisIndexTaskTest.recordSupplier.seek(anyObject(), anyString());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.poll(anyLong())).andReturn(KinesisIndexTaskTest.records.subList(2, 13)).once().andReturn(KinesisIndexTaskTest.records.subList(13, 15)).once();
        KinesisIndexTaskTest.recordSupplier.close();
        expectLastCall().times(2);
        replayAll();
        final KinesisIndexTask task1 = createTask(null, new KinesisIndexTaskIOConfig(null, "sequence0", new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "2")), new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "4")), true, null, null, "awsEndpoint", null, null, null, null, null, false));
        final KinesisIndexTask task2 = createTask(null, new KinesisIndexTaskIOConfig(null, "sequence1", new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId0, "0")), new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId0, "1")), true, null, null, "awsEndpoint", null, null, null, null, null, false));
        final ListenableFuture<TaskStatus> future1 = runTask(task1);
        Assert.assertEquals(SUCCESS, future1.get().getStatusCode());
        final ListenableFuture<TaskStatus> future2 = runTask(task2);
        Assert.assertEquals(SUCCESS, future2.get().getStatusCode());
        verifyAll();
        // Check metrics
        Assert.assertEquals(3, task1.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task1.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task1.getRunner().getRowIngestionMeters().getThrownAway());
        Assert.assertEquals(2, task2.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task2.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task2.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published segments & metadata
        SegmentDescriptor desc1 = sd(task1, "2010/P1D", 0);
        SegmentDescriptor desc2 = sd(task1, "2011/P1D", 0);
        SegmentDescriptor desc3 = sd(task2, "2011/P1D", 1);
        SegmentDescriptor desc4 = sd(task2, "2012/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2, desc3, desc4), publishedDescriptors());
        Assert.assertEquals(new KinesisDataSourceMetadata(new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "4", KinesisIndexTaskTest.shardId0, "1"))), metadataStorageCoordinator.getDataSourceMetadata(KinesisIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc1));
        // Check desc2/desc3 without strong ordering because two partitions are interleaved nondeterministically
        Assert.assertEquals(ImmutableSet.of(ImmutableList.of("d", "e"), ImmutableList.of("h")), ImmutableSet.of(readSegmentColumn("dim1", desc2), readSegmentColumn("dim1", desc3)));
        Assert.assertEquals(ImmutableList.of("g"), readSegmentColumn("dim1", desc4));
    }

    @Test(timeout = 120000L)
    public void testRestore() throws Exception {
        KinesisIndexTaskTest.recordSupplier.assign(anyObject());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.getEarliestSequenceNumber(anyObject())).andReturn("0").anyTimes();
        KinesisIndexTaskTest.recordSupplier.seek(anyObject(), anyString());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.poll(anyLong())).andReturn(KinesisIndexTaskTest.records.subList(2, 4)).once().andReturn(Collections.emptyList()).anyTimes();
        replayAll();
        final KinesisIndexTask task1 = createTask("task1", new KinesisIndexTaskIOConfig(null, "sequence0", new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "2")), new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "5")), true, null, null, "awsEndpoint", null, null, null, null, null, false));
        final ListenableFuture<TaskStatus> future1 = runTask(task1);
        while ((countEvents(task1)) != 2) {
            Thread.sleep(25);
        } 
        Assert.assertEquals(2, countEvents(task1));
        // Stop without publishing segment
        task1.stopGracefully(toolboxFactory.build(task1).getConfig());
        unlockAppenderatorBasePersistDirForTask(task1);
        Assert.assertEquals(SUCCESS, future1.get().getStatusCode());
        verifyAll();
        reset(KinesisIndexTaskTest.recordSupplier);
        KinesisIndexTaskTest.recordSupplier.assign(anyObject());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.getEarliestSequenceNumber(anyObject())).andReturn("0").anyTimes();
        KinesisIndexTaskTest.recordSupplier.seek(anyObject(), anyString());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.poll(anyLong())).andReturn(KinesisIndexTaskTest.records.subList(3, 6)).once();
        KinesisIndexTaskTest.recordSupplier.close();
        expectLastCall();
        replayAll();
        // Start a new task
        final KinesisIndexTask task2 = createTask(task1.getId(), new KinesisIndexTaskIOConfig(null, "sequence0", new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "2")), new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "5")), true, null, null, "awsEndpoint", null, null, ImmutableSet.of(KinesisIndexTaskTest.shardId1), null, null, false));
        final ListenableFuture<TaskStatus> future2 = runTask(task2);
        while ((countEvents(task2)) < 3) {
            Thread.sleep(25);
        } 
        Assert.assertEquals(3, countEvents(task2));
        // Wait for task to exit
        Assert.assertEquals(SUCCESS, future2.get().getStatusCode());
        verifyAll();
        // Check metrics
        Assert.assertEquals(2, task1.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task1.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task1.getRunner().getRowIngestionMeters().getThrownAway());
        Assert.assertEquals(1, task2.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(1, task2.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task2.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published segments & metadata
        SegmentDescriptor desc1 = sd(task1, "2010/P1D", 0);
        SegmentDescriptor desc2 = sd(task1, "2011/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2), publishedDescriptors());
        Assert.assertEquals(new KinesisDataSourceMetadata(new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "5"))), metadataStorageCoordinator.getDataSourceMetadata(KinesisIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("d", "e"), readSegmentColumn("dim1", desc2));
    }

    @Test(timeout = 120000L)
    public void testRunWithPauseAndResume() throws Exception {
        KinesisIndexTaskTest.recordSupplier.assign(anyObject());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.getEarliestSequenceNumber(anyObject())).andReturn("0").anyTimes();
        KinesisIndexTaskTest.recordSupplier.seek(anyObject(), anyString());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.poll(anyLong())).andReturn(KinesisIndexTaskTest.records.subList(2, 5)).once().andReturn(Collections.emptyList()).anyTimes();
        replayAll();
        final KinesisIndexTask task = createTask("task1", new KinesisIndexTaskIOConfig(null, "sequence0", new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "2")), new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "13")), true, null, null, "awsEndpoint", null, null, null, null, null, false));
        final ListenableFuture<TaskStatus> future = runTask(task);
        while ((countEvents(task)) != 3) {
            Thread.sleep(25);
        } 
        Assert.assertEquals(3, countEvents(task));
        Assert.assertEquals(READING, task.getRunner().getStatus());
        task.getRunner().pause();
        while ((task.getRunner().getStatus()) != (PAUSED)) {
            Thread.sleep(10);
        } 
        Assert.assertEquals(PAUSED, task.getRunner().getStatus());
        verifyAll();
        Map<String, String> currentOffsets = task.getRunner().getCurrentOffsets();
        try {
            future.get(10, TimeUnit.SECONDS);
            Assert.fail("Task completed when it should have been paused");
        } catch (TimeoutException e) {
            // carry on..
        }
        Assert.assertEquals(currentOffsets, task.getRunner().getCurrentOffsets());
        reset(KinesisIndexTaskTest.recordSupplier);
        KinesisIndexTaskTest.recordSupplier.assign(anyObject());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.getEarliestSequenceNumber(anyObject())).andReturn("0").anyTimes();
        KinesisIndexTaskTest.recordSupplier.seek(anyObject(), anyString());
        expectLastCall().anyTimes();
        KinesisIndexTaskTest.recordSupplier.close();
        expectLastCall().once();
        replayAll();
        task.getRunner().setEndOffsets(currentOffsets, true);
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
        verifyAll();
        Assert.assertEquals(task.getRunner().getEndOffsets(), task.getRunner().getCurrentOffsets());
        // Check metrics
        Assert.assertEquals(3, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published metadata
        SegmentDescriptor desc1 = sd(task, "2010/P1D", 0);
        SegmentDescriptor desc2 = sd(task, "2011/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2), publishedDescriptors());
        Assert.assertEquals(new KinesisDataSourceMetadata(new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, currentOffsets.get(KinesisIndexTaskTest.shardId1)))), metadataStorageCoordinator.getDataSourceMetadata(KinesisIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("d", "e"), readSegmentColumn("dim1", desc2));
    }

    @Test(timeout = 60000L)
    public void testRunContextSequenceAheadOfStartingOffsets() throws Exception {
        // This tests the case when a replacement task is created in place of a failed test
        // which has done some incremental handoffs, thus the context will contain starting
        // sequence sequences from which the task should start reading and ignore the start sequences
        // Insert data
        KinesisIndexTaskTest.recordSupplier.assign(anyObject());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.getEarliestSequenceNumber(anyObject())).andReturn("0").anyTimes();
        KinesisIndexTaskTest.recordSupplier.seek(anyObject(), anyString());
        expectLastCall().anyTimes();
        expect(KinesisIndexTaskTest.recordSupplier.poll(anyLong())).andReturn(KinesisIndexTaskTest.records.subList(2, 13)).once();
        KinesisIndexTaskTest.recordSupplier.close();
        expectLastCall();
        replayAll();
        final TreeMap<Integer, Map<String, String>> sequences = new TreeMap<>();
        // Here the sequence number is 1 meaning that one incremental handoff was done by the failed task
        // and this task should start reading from stream 2 for partition 0
        sequences.put(1, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "2"));
        final Map<String, Object> context = new HashMap<>();
        context.put("checkpoints", KinesisIndexTaskTest.objectMapper.writerWithType(new TypeReference<TreeMap<Integer, Map<String, String>>>() {}).writeValueAsString(sequences));
        final KinesisIndexTask task = createTask("task1", new KinesisIndexTaskIOConfig(null, "sequence0", new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "0")), new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "4")), true, null, null, "awsEndpoint", null, null, null, null, null, false), context);
        final ListenableFuture<TaskStatus> future = runTask(task);
        // Wait for task to exit
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
        // Check metrics
        Assert.assertEquals(3, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published metadata
        SegmentDescriptor desc1 = sd(task, "2010/P1D", 0);
        SegmentDescriptor desc2 = sd(task, "2011/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2), publishedDescriptors());
        Assert.assertEquals(new KinesisDataSourceMetadata(new SeekableStreamPartitions(KinesisIndexTaskTest.stream, ImmutableMap.of(KinesisIndexTaskTest.shardId1, "4"))), metadataStorageCoordinator.getDataSourceMetadata(KinesisIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("d", "e"), readSegmentColumn("dim1", desc2));
    }

    @JsonTypeName("index_kinesis")
    private static class TestableKinesisIndexTask extends KinesisIndexTask {
        @JsonCreator
        public TestableKinesisIndexTask(@JsonProperty("id")
        String id, @JsonProperty("resource")
        TaskResource taskResource, @JsonProperty("dataSchema")
        DataSchema dataSchema, @JsonProperty("tuningConfig")
        KinesisIndexTaskTuningConfig tuningConfig, @JsonProperty("ioConfig")
        KinesisIndexTaskIOConfig ioConfig, @JsonProperty("context")
        Map<String, Object> context, @JacksonInject
        ChatHandlerProvider chatHandlerProvider, @JacksonInject
        AuthorizerMapper authorizerMapper, @JacksonInject
        RowIngestionMetersFactory rowIngestionMetersFactory, @JacksonInject
        AWSCredentialsConfig awsCredentialsConfig) {
            super(id, taskResource, dataSchema, tuningConfig, ioConfig, context, chatHandlerProvider, authorizerMapper, rowIngestionMetersFactory, awsCredentialsConfig);
        }

        @Override
        protected KinesisRecordSupplier newTaskRecordSupplier() {
            return KinesisIndexTaskTest.recordSupplier;
        }
    }
}

