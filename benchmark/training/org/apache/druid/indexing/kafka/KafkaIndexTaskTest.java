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
package org.apache.druid.indexing.kafka;


import RowIngestionMeters.BUILD_SEGMENTS;
import RowIngestionMeters.PROCESSED;
import RowIngestionMeters.PROCESSED_WITH_ERROR;
import RowIngestionMeters.THROWN_AWAY;
import RowIngestionMeters.UNPARSEABLE;
import Status.PAUSED;
import Status.READING;
import TaskState.FAILED;
import TaskState.SUCCESS;
import TestDerbyConnector.DerbyConnectorRule;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.curator.test.TestingCluster;
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
import org.apache.druid.indexing.kafka.supervisor.KafkaSupervisorIOConfig;
import org.apache.druid.indexing.kafka.test.TestBroker;
import org.apache.druid.indexing.overlord.IndexerMetadataStorageCoordinator;
import org.apache.druid.indexing.overlord.TaskLockbox;
import org.apache.druid.indexing.overlord.TaskStorage;
import org.apache.druid.indexing.seekablestream.SeekableStreamIndexTaskRunner.Status;
import org.apache.druid.indexing.seekablestream.SeekableStreamPartitions;
import org.apache.druid.java.util.common.DateTimes;
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
import org.apache.druid.query.scan.ScanResultValue;
import org.apache.druid.query.spec.QuerySegmentSpec;
import org.apache.druid.segment.TestHelper;
import org.apache.druid.segment.indexing.DataSchema;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class KafkaIndexTaskTest {
    private static final Logger log = new Logger(KafkaIndexTaskTest.class);

    private static final ObjectMapper objectMapper = TestHelper.makeJsonMapper();

    private static final long POLL_RETRY_MS = 100;

    private static TestingCluster zkServer;

    private static TestBroker kafkaServer;

    private static ServiceEmitter emitter;

    private static ListeningExecutorService taskExec;

    private static int topicPostfix;

    private final List<Task> runningTasks = new ArrayList<>();

    private long handoffConditionTimeout = 0;

    private boolean reportParseExceptions = false;

    private boolean logParseExceptions = true;

    private Integer maxParseExceptions = null;

    private Integer maxSavedParseExceptions = null;

    private boolean resetOffsetAutomatically = false;

    private boolean doHandoff = true;

    private Integer maxRowsPerSegment = null;

    private Long maxTotalRows = null;

    private Period intermediateHandoffPeriod = null;

    private TaskToolboxFactory toolboxFactory;

    private IndexerMetadataStorageCoordinator metadataStorageCoordinator;

    private TaskStorage taskStorage;

    private TaskLockbox taskLockbox;

    private File directory;

    private String topic;

    private List<ProducerRecord<byte[], byte[]>> records;

    private final boolean isIncrementalHandoffSupported;

    private final Set<Integer> checkpointRequestsHash = new HashSet<>();

    private File reportsFile;

    private RowIngestionMetersFactory rowIngestionMetersFactory;

    private int handoffCount = 0;

    public KafkaIndexTaskTest(boolean isIncrementalHandoffSupported) {
        this.isIncrementalHandoffSupported = isIncrementalHandoffSupported;
    }

    private static final DataSchema DATA_SCHEMA = new DataSchema("test_ds", KafkaIndexTaskTest.objectMapper.convertValue(new StringInputRowParser(new org.apache.druid.data.input.impl.JSONParseSpec(new TimestampSpec("timestamp", "iso", null), new org.apache.druid.data.input.impl.DimensionsSpec(Arrays.asList(new StringDimensionSchema("dim1"), new StringDimensionSchema("dim1t"), new StringDimensionSchema("dim2"), new LongDimensionSchema("dimLong"), new FloatDimensionSchema("dimFloat")), null, null), new JSONPathSpec(true, ImmutableList.of()), ImmutableMap.of()), StandardCharsets.UTF_8.name()), Map.class), new AggregatorFactory[]{ new DoubleSumAggregatorFactory("met1sum", "met1"), new CountAggregatorFactory("rows") }, new org.apache.druid.segment.indexing.granularity.UniformGranularitySpec(Granularities.DAY, Granularities.NONE, null), null, KafkaIndexTaskTest.objectMapper);

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    @Rule
    public final DerbyConnectorRule derby = new TestDerbyConnector.DerbyConnectorRule();

    @Test(timeout = 60000L)
    public void testRunAfterDataInserted() throws Exception {
        // Insert data
        insertData();
        final KafkaIndexTask task = createTask(null, new KafkaIndexTaskIOConfig(0, "sequence0", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 2L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
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
        Assert.assertEquals(new KafkaDataSourceMetadata(new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L))), metadataStorageCoordinator.getDataSourceMetadata(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("d", "e"), readSegmentColumn("dim1", desc2));
    }

    @Test(timeout = 60000L)
    public void testRunBeforeDataInserted() throws Exception {
        final KafkaIndexTask task = createTask(null, new KafkaIndexTaskIOConfig(0, "sequence0", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 2L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
        final ListenableFuture<TaskStatus> future = runTask(task);
        // Wait for the task to start reading
        while ((task.getRunner().getStatus()) != (Status.READING)) {
            Thread.sleep(10);
        } 
        // Insert data
        insertData();
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
        Assert.assertEquals(new KafkaDataSourceMetadata(new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L))), metadataStorageCoordinator.getDataSourceMetadata(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("d", "e"), readSegmentColumn("dim1", desc2));
    }

    @Test(timeout = 60000L)
    public void testIncrementalHandOff() throws Exception {
        if (!(isIncrementalHandoffSupported)) {
            return;
        }
        final String baseSequenceName = "sequence0";
        // as soon as any segment has more than one record, incremental publishing should happen
        maxRowsPerSegment = 2;
        // Insert data
        insertData();
        Map<String, Object> consumerProps = KafkaIndexTaskTest.kafkaServer.consumerProperties();
        consumerProps.put("max.poll.records", "1");
        final SeekableStreamPartitions<Integer, Long> startPartitions = new SeekableStreamPartitions(topic, ImmutableMap.of(0, 0L, 1, 0L));
        // Checkpointing will happen at either checkpoint1 or checkpoint2 depending on ordering
        // of events fetched across two partitions from Kafka
        final SeekableStreamPartitions<Integer, Long> checkpoint1 = new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L, 1, 0L));
        final SeekableStreamPartitions<Integer, Long> checkpoint2 = new SeekableStreamPartitions(topic, ImmutableMap.of(0, 4L, 1, 2L));
        final SeekableStreamPartitions<Integer, Long> endPartitions = new SeekableStreamPartitions(topic, ImmutableMap.of(0, 10L, 1, 2L));
        final KafkaIndexTask task = createTask(null, new KafkaIndexTaskIOConfig(0, baseSequenceName, startPartitions, endPartitions, consumerProps, KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
        final ListenableFuture<TaskStatus> future = runTask(task);
        while ((task.getRunner().getStatus()) != (Status.PAUSED)) {
            Thread.sleep(10);
        } 
        final Map<Integer, Long> currentOffsets = ImmutableMap.copyOf(task.getRunner().getCurrentOffsets());
        Assert.assertTrue(((checkpoint1.getPartitionSequenceNumberMap().equals(currentOffsets)) || (checkpoint2.getPartitionSequenceNumberMap().equals(currentOffsets))));
        task.getRunner().setEndOffsets(currentOffsets, false);
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
        Assert.assertEquals(1, checkpointRequestsHash.size());
        Assert.assertTrue(checkpointRequestsHash.contains(Objects.hash(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource(), 0, new KafkaDataSourceMetadata(startPartitions), new KafkaDataSourceMetadata(new SeekableStreamPartitions(topic, currentOffsets)))));
        // Check metrics
        Assert.assertEquals(8, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(3, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(1, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published metadata
        SegmentDescriptor desc1 = sd(task, "2008/P1D", 0);
        SegmentDescriptor desc2 = sd(task, "2009/P1D", 0);
        SegmentDescriptor desc3 = sd(task, "2010/P1D", 0);
        SegmentDescriptor desc4 = sd(task, "2011/P1D", 0);
        SegmentDescriptor desc5 = sd(task, "2011/P1D", 1);
        SegmentDescriptor desc6 = sd(task, "2012/P1D", 0);
        SegmentDescriptor desc7 = sd(task, "2013/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2, desc3, desc4, desc5, desc6, desc7), publishedDescriptors());
        Assert.assertEquals(new KafkaDataSourceMetadata(new SeekableStreamPartitions(topic, ImmutableMap.of(0, 10L, 1, 2L))), metadataStorageCoordinator.getDataSourceMetadata(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("a"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("b"), readSegmentColumn("dim1", desc2));
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc3));
        Assert.assertTrue((((ImmutableList.of("d", "e").equals(readSegmentColumn("dim1", desc4))) && (ImmutableList.of("h").equals(readSegmentColumn("dim1", desc5)))) || ((ImmutableList.of("d", "h").equals(readSegmentColumn("dim1", desc4))) && (ImmutableList.of("e").equals(readSegmentColumn("dim1", desc5))))));
        Assert.assertEquals(ImmutableList.of("g"), readSegmentColumn("dim1", desc6));
        Assert.assertEquals(ImmutableList.of("f"), readSegmentColumn("dim1", desc7));
    }

    @Test(timeout = 60000L)
    public void testIncrementalHandOffMaxTotalRows() throws Exception {
        if (!(isIncrementalHandoffSupported)) {
            return;
        }
        final String baseSequenceName = "sequence0";
        // incremental publish should happen every 3 records
        maxRowsPerSegment = Integer.MAX_VALUE;
        maxTotalRows = 3L;
        // Insert data
        int numToAdd = (records.size()) - 2;
        try (final KafkaProducer<byte[], byte[]> kafkaProducer = KafkaIndexTaskTest.kafkaServer.newProducer()) {
            kafkaProducer.initTransactions();
            kafkaProducer.beginTransaction();
            for (int i = 0; i < numToAdd; i++) {
                kafkaProducer.send(records.get(i)).get();
            }
            kafkaProducer.commitTransaction();
        }
        Map<String, Object> consumerProps = KafkaIndexTaskTest.kafkaServer.consumerProperties();
        consumerProps.put("max.poll.records", "1");
        final SeekableStreamPartitions<Integer, Long> startPartitions = new SeekableStreamPartitions(topic, ImmutableMap.of(0, 0L, 1, 0L));
        final SeekableStreamPartitions<Integer, Long> checkpoint1 = new SeekableStreamPartitions(topic, ImmutableMap.of(0, 3L, 1, 0L));
        final SeekableStreamPartitions<Integer, Long> checkpoint2 = new SeekableStreamPartitions(topic, ImmutableMap.of(0, 10L, 1, 0L));
        final SeekableStreamPartitions<Integer, Long> endPartitions = new SeekableStreamPartitions(topic, ImmutableMap.of(0, 10L, 1, 2L));
        final KafkaIndexTask task = createTask(null, new KafkaIndexTaskIOConfig(0, baseSequenceName, startPartitions, endPartitions, consumerProps, KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
        final ListenableFuture<TaskStatus> future = runTask(task);
        while ((task.getRunner().getStatus()) != (Status.PAUSED)) {
            Thread.sleep(10);
        } 
        final Map<Integer, Long> currentOffsets = ImmutableMap.copyOf(task.getRunner().getCurrentOffsets());
        Assert.assertTrue(checkpoint1.getPartitionSequenceNumberMap().equals(currentOffsets));
        task.getRunner().setEndOffsets(currentOffsets, false);
        while ((task.getRunner().getStatus()) != (Status.PAUSED)) {
            Thread.sleep(10);
        } 
        // add remaining records
        try (final KafkaProducer<byte[], byte[]> kafkaProducer = KafkaIndexTaskTest.kafkaServer.newProducer()) {
            kafkaProducer.initTransactions();
            kafkaProducer.beginTransaction();
            for (int i = numToAdd; i < (records.size()); i++) {
                kafkaProducer.send(records.get(i)).get();
            }
            kafkaProducer.commitTransaction();
        }
        final Map<Integer, Long> nextOffsets = ImmutableMap.copyOf(task.getRunner().getCurrentOffsets());
        Assert.assertTrue(checkpoint2.getPartitionSequenceNumberMap().equals(nextOffsets));
        task.getRunner().setEndOffsets(nextOffsets, false);
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
        Assert.assertEquals(2, checkpointRequestsHash.size());
        Assert.assertTrue(checkpointRequestsHash.contains(Objects.hash(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource(), 0, new KafkaDataSourceMetadata(startPartitions), new KafkaDataSourceMetadata(new SeekableStreamPartitions(topic, currentOffsets)))));
        Assert.assertTrue(checkpointRequestsHash.contains(Objects.hash(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource(), 0, new KafkaDataSourceMetadata(new SeekableStreamPartitions(topic, currentOffsets)), new KafkaDataSourceMetadata(new SeekableStreamPartitions(topic, nextOffsets)))));
        // Check metrics
        Assert.assertEquals(8, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(3, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(1, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published metadata
        SegmentDescriptor desc1 = sd(task, "2008/P1D", 0);
        SegmentDescriptor desc2 = sd(task, "2009/P1D", 0);
        SegmentDescriptor desc3 = sd(task, "2010/P1D", 0);
        SegmentDescriptor desc4 = sd(task, "2011/P1D", 0);
        SegmentDescriptor desc5 = sd(task, "2011/P1D", 1);
        SegmentDescriptor desc6 = sd(task, "2012/P1D", 0);
        SegmentDescriptor desc7 = sd(task, "2013/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2, desc3, desc4, desc5, desc6, desc7), publishedDescriptors());
        Assert.assertEquals(new KafkaDataSourceMetadata(new SeekableStreamPartitions(topic, ImmutableMap.of(0, 10L, 1, 2L))), metadataStorageCoordinator.getDataSourceMetadata(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource()));
        Assert.assertEquals(ImmutableSet.of(desc1, desc2, desc3, desc4, desc5, desc6, desc7), publishedDescriptors());
        Assert.assertEquals(new KafkaDataSourceMetadata(new SeekableStreamPartitions(topic, ImmutableMap.of(0, 10L, 1, 2L))), metadataStorageCoordinator.getDataSourceMetadata(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("a"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("b"), readSegmentColumn("dim1", desc2));
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc3));
        Assert.assertTrue((((ImmutableList.of("d", "e").equals(readSegmentColumn("dim1", desc4))) && (ImmutableList.of("h").equals(readSegmentColumn("dim1", desc5)))) || ((ImmutableList.of("d", "h").equals(readSegmentColumn("dim1", desc4))) && (ImmutableList.of("e").equals(readSegmentColumn("dim1", desc5))))));
        Assert.assertEquals(ImmutableList.of("g"), readSegmentColumn("dim1", desc6));
        Assert.assertEquals(ImmutableList.of("f"), readSegmentColumn("dim1", desc7));
    }

    @Test(timeout = 60000L)
    public void testTimeBasedIncrementalHandOff() throws Exception {
        if (!(isIncrementalHandoffSupported)) {
            return;
        }
        final String baseSequenceName = "sequence0";
        // as soon as any segment hits maxRowsPerSegment or intermediateHandoffPeriod, incremental publishing should happen
        maxRowsPerSegment = Integer.MAX_VALUE;
        intermediateHandoffPeriod = new Period().withSeconds(0);
        // Insert data
        insertData();
        Map<String, Object> consumerProps = KafkaIndexTaskTest.kafkaServer.consumerProperties();
        consumerProps.put("max.poll.records", "1");
        final SeekableStreamPartitions<Integer, Long> startPartitions = new SeekableStreamPartitions(topic, ImmutableMap.of(0, 0L, 1, 0L));
        // Checkpointing will happen at checkpoint
        final SeekableStreamPartitions<Integer, Long> checkpoint = new SeekableStreamPartitions(topic, ImmutableMap.of(0, 1L, 1, 0L));
        final SeekableStreamPartitions<Integer, Long> endPartitions = new SeekableStreamPartitions(topic, ImmutableMap.of(0, 2L, 1, 0L));
        final KafkaIndexTask task = createTask(null, new KafkaIndexTaskIOConfig(0, baseSequenceName, startPartitions, endPartitions, consumerProps, KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
        final ListenableFuture<TaskStatus> future = runTask(task);
        // task will pause for checkpointing
        while ((task.getRunner().getStatus()) != (Status.PAUSED)) {
            Thread.sleep(10);
        } 
        final Map<Integer, Long> currentOffsets = ImmutableMap.copyOf(task.getRunner().getCurrentOffsets());
        Assert.assertTrue(checkpoint.getPartitionSequenceNumberMap().equals(currentOffsets));
        task.getRunner().setEndOffsets(currentOffsets, false);
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
        Assert.assertEquals(1, checkpointRequestsHash.size());
        Assert.assertTrue(checkpointRequestsHash.contains(Objects.hash(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource(), 0, new KafkaDataSourceMetadata(startPartitions), new KafkaDataSourceMetadata(new SeekableStreamPartitions(topic, checkpoint.getPartitionSequenceNumberMap())))));
        // Check metrics
        Assert.assertEquals(2, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published metadata
        SegmentDescriptor desc1 = sd(task, "2008/P1D", 0);
        SegmentDescriptor desc2 = sd(task, "2009/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2), publishedDescriptors());
        Assert.assertEquals(new KafkaDataSourceMetadata(new SeekableStreamPartitions(topic, ImmutableMap.of(0, 2L, 1, 0L))), metadataStorageCoordinator.getDataSourceMetadata(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("a"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("b"), readSegmentColumn("dim1", desc2));
    }

    @Test(timeout = 60000L)
    public void testIncrementalHandOffReadsThroughEndOffsets() throws Exception {
        if (!(isIncrementalHandoffSupported)) {
            return;
        }
        List<ProducerRecord<byte[], byte[]>> records = ImmutableList.of(new ProducerRecord(topic, 0, null, KafkaIndexTaskTest.jb("2008", "a", "y", "10", "20.0", "1.0")), new ProducerRecord(topic, 0, null, KafkaIndexTaskTest.jb("2009", "b", "y", "10", "20.0", "1.0")), new ProducerRecord(topic, 0, null, KafkaIndexTaskTest.jb("2010", "c", "y", "10", "20.0", "1.0")), new ProducerRecord(topic, 0, null, KafkaIndexTaskTest.jb("2011", "d", "y", "10", "20.0", "1.0")), new ProducerRecord(topic, 0, null, KafkaIndexTaskTest.jb("2011", "D", "y", "10", "20.0", "1.0")), new ProducerRecord(topic, 0, null, KafkaIndexTaskTest.jb("2012", "e", "y", "10", "20.0", "1.0")), new ProducerRecord(topic, 0, null, KafkaIndexTaskTest.jb("2009", "B", "y", "10", "20.0", "1.0")));
        final String baseSequenceName = "sequence0";
        // as soon as any segment has more than one record, incremental publishing should happen
        maxRowsPerSegment = 2;
        // Insert data
        try (final KafkaProducer<byte[], byte[]> kafkaProducer = KafkaIndexTaskTest.kafkaServer.newProducer()) {
            kafkaProducer.initTransactions();
            kafkaProducer.beginTransaction();
            for (ProducerRecord<byte[], byte[]> record : records) {
                kafkaProducer.send(record).get();
            }
            kafkaProducer.commitTransaction();
        }
        Map<String, Object> consumerProps = KafkaIndexTaskTest.kafkaServer.consumerProperties();
        consumerProps.put("max.poll.records", "1");
        final SeekableStreamPartitions<Integer, Long> startPartitions = new SeekableStreamPartitions(topic, ImmutableMap.of(0, 0L));
        final SeekableStreamPartitions<Integer, Long> checkpoint1 = new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L));
        final SeekableStreamPartitions<Integer, Long> endPartitions = new SeekableStreamPartitions(topic, ImmutableMap.of(0, 7L));
        final KafkaIndexTask task = createTask(null, new KafkaIndexTaskIOConfig(0, baseSequenceName, startPartitions, endPartitions, consumerProps, KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
        final ListenableFuture<TaskStatus> future = runTask(task);
        while ((task.getRunner().getStatus()) != (Status.PAUSED)) {
            Thread.sleep(10);
        } 
        final Map<Integer, Long> currentOffsets = ImmutableMap.copyOf(task.getRunner().getCurrentOffsets());
        Assert.assertTrue(checkpoint1.getPartitionSequenceNumberMap().equals(currentOffsets));
        // actual checkpoint offset is 5, but simulating behavior of publishing set end offset call, to ensure this task
        // will continue reading through the end offset of the checkpointed sequence
        task.getRunner().setEndOffsets(ImmutableMap.of(0, 6L), true);
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
        // processed count would be 5 if it stopped at it's current offsets
        Assert.assertEquals(6, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getThrownAway());
    }

    @Test(timeout = 60000L)
    public void testRunWithMinimumMessageTime() throws Exception {
        final KafkaIndexTask task = createTask(null, new KafkaIndexTaskIOConfig(0, "sequence0", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 0L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, DateTimes.of("2010"), null));
        final ListenableFuture<TaskStatus> future = runTask(task);
        // Wait for the task to start reading
        while ((task.getRunner().getStatus()) != (Status.READING)) {
            Thread.sleep(10);
        } 
        // Insert data
        insertData();
        // Wait for task to exit
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
        // Check metrics
        Assert.assertEquals(3, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(2, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published metadata
        SegmentDescriptor desc1 = sd(task, "2010/P1D", 0);
        SegmentDescriptor desc2 = sd(task, "2011/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2), publishedDescriptors());
        Assert.assertEquals(new KafkaDataSourceMetadata(new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L))), metadataStorageCoordinator.getDataSourceMetadata(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("d", "e"), readSegmentColumn("dim1", desc2));
    }

    @Test(timeout = 60000L)
    public void testRunWithMaximumMessageTime() throws Exception {
        final KafkaIndexTask task = createTask(null, new KafkaIndexTaskIOConfig(0, "sequence0", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 0L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, DateTimes.of("2010")));
        final ListenableFuture<TaskStatus> future = runTask(task);
        // Wait for the task to start reading
        while ((task.getRunner().getStatus()) != (Status.READING)) {
            Thread.sleep(10);
        } 
        // Insert data
        insertData();
        // Wait for task to exit
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
        // Check metrics
        Assert.assertEquals(3, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(2, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published metadata
        SegmentDescriptor desc1 = sd(task, "2008/P1D", 0);
        SegmentDescriptor desc2 = sd(task, "2009/P1D", 0);
        SegmentDescriptor desc3 = sd(task, "2010/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2, desc3), publishedDescriptors());
        Assert.assertEquals(new KafkaDataSourceMetadata(new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L))), metadataStorageCoordinator.getDataSourceMetadata(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("a"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("b"), readSegmentColumn("dim1", desc2));
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc3));
    }

    @Test(timeout = 60000L)
    public void testRunWithTransformSpec() throws Exception {
        final KafkaIndexTask task = createTask(null, KafkaIndexTaskTest.DATA_SCHEMA.withTransformSpec(new org.apache.druid.segment.transform.TransformSpec(new SelectorDimFilter("dim1", "b", null), ImmutableList.of(new org.apache.druid.segment.transform.ExpressionTransform("dim1t", "concat(dim1,dim1)", ExprMacroTable.nil())))), new KafkaIndexTaskIOConfig(0, "sequence0", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 0L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
        final ListenableFuture<TaskStatus> future = runTask(task);
        // Wait for the task to start reading
        while ((task.getRunner().getStatus()) != (Status.READING)) {
            Thread.sleep(10);
        } 
        // Insert data
        insertData();
        // Wait for task to exit
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
        // Check metrics
        Assert.assertEquals(1, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(4, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published metadata
        SegmentDescriptor desc1 = sd(task, "2009/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1), publishedDescriptors());
        Assert.assertEquals(new KafkaDataSourceMetadata(new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L))), metadataStorageCoordinator.getDataSourceMetadata(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("b"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("bb"), readSegmentColumn("dim1t", desc1));
    }

    @Test(timeout = 60000L)
    public void testRunOnNothing() throws Exception {
        // Insert data
        insertData();
        final KafkaIndexTask task = createTask(null, new KafkaIndexTaskIOConfig(0, "sequence0", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 2L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 2L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
        final ListenableFuture<TaskStatus> future = runTask(task);
        // Wait for task to exit
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
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
        // Insert data
        insertData();
        final KafkaIndexTask task = createTask(null, new KafkaIndexTaskIOConfig(0, "sequence0", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 2L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
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
        Assert.assertEquals(new KafkaDataSourceMetadata(new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L))), metadataStorageCoordinator.getDataSourceMetadata(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("d", "e"), readSegmentColumn("dim1", desc2));
    }

    @Test(timeout = 60000L)
    public void testHandoffConditionTimeoutWhenHandoffDoesNotOccur() throws Exception {
        doHandoff = false;
        handoffConditionTimeout = 100;
        // Insert data
        insertData();
        final KafkaIndexTask task = createTask(null, new KafkaIndexTaskIOConfig(0, "sequence0", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 2L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
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
        Assert.assertEquals(new KafkaDataSourceMetadata(new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L))), metadataStorageCoordinator.getDataSourceMetadata(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("d", "e"), readSegmentColumn("dim1", desc2));
    }

    @Test(timeout = 60000L)
    public void testReportParseExceptions() throws Exception {
        reportParseExceptions = true;
        // these will be ignored because reportParseExceptions is true
        maxParseExceptions = 1000;
        maxSavedParseExceptions = 2;
        // Insert data
        insertData();
        final KafkaIndexTask task = createTask(null, new KafkaIndexTaskIOConfig(0, "sequence0", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 2L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 7L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
        final ListenableFuture<TaskStatus> future = runTask(task);
        // Wait for task to exit
        Assert.assertEquals(FAILED, future.get().getStatusCode());
        // Check metrics
        Assert.assertEquals(3, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(1, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published metadata
        Assert.assertEquals(ImmutableSet.of(), publishedDescriptors());
        Assert.assertNull(metadataStorageCoordinator.getDataSourceMetadata(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource()));
    }

    @Test(timeout = 60000L)
    public void testMultipleParseExceptionsSuccess() throws Exception {
        reportParseExceptions = false;
        maxParseExceptions = 6;
        maxSavedParseExceptions = 6;
        // Insert data
        insertData();
        final KafkaIndexTask task = createTask(null, new KafkaIndexTaskIOConfig(0, "sequence0", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 2L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 13L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
        final ListenableFuture<TaskStatus> future = runTask(task);
        TaskStatus status = future.get();
        // Wait for task to exit
        Assert.assertEquals(SUCCESS, status.getStatusCode());
        Assert.assertEquals(null, status.getErrorMsg());
        // Check metrics
        Assert.assertEquals(4, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(3, task.getRunner().getRowIngestionMeters().getProcessedWithError());
        Assert.assertEquals(3, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(1, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published metadata
        SegmentDescriptor desc1 = sd(task, "2010/P1D", 0);
        SegmentDescriptor desc2 = sd(task, "2011/P1D", 0);
        SegmentDescriptor desc3 = sd(task, "2013/P1D", 0);
        SegmentDescriptor desc4 = sd(task, "2049/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2, desc3, desc4), publishedDescriptors());
        Assert.assertEquals(new KafkaDataSourceMetadata(new SeekableStreamPartitions(topic, ImmutableMap.of(0, 13L))), metadataStorageCoordinator.getDataSourceMetadata(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource()));
        IngestionStatsAndErrorsTaskReportData reportData = getTaskReportData();
        Map<String, Object> expectedMetrics = ImmutableMap.of(BUILD_SEGMENTS, ImmutableMap.of(PROCESSED, 4, PROCESSED_WITH_ERROR, 3, UNPARSEABLE, 3, THROWN_AWAY, 1));
        Assert.assertEquals(expectedMetrics, reportData.getRowStats());
        Map<String, Object> unparseableEvents = ImmutableMap.of(BUILD_SEGMENTS, Arrays.asList("Found unparseable columns in row: [MapBasedInputRow{timestamp=2049-01-01T00:00:00.000Z, event={timestamp=2049, dim1=f, dim2=y, dimLong=10, dimFloat=20.0, met1=notanumber}, dimensions=[dim1, dim1t, dim2, dimLong, dimFloat]}], exceptions: [Unable to parse value[notanumber] for field[met1],]", "Found unparseable columns in row: [MapBasedInputRow{timestamp=2049-01-01T00:00:00.000Z, event={timestamp=2049, dim1=f, dim2=y, dimLong=10, dimFloat=notanumber, met1=1.0}, dimensions=[dim1, dim1t, dim2, dimLong, dimFloat]}], exceptions: [could not convert value [notanumber] to float,]", "Found unparseable columns in row: [MapBasedInputRow{timestamp=2049-01-01T00:00:00.000Z, event={timestamp=2049, dim1=f, dim2=y, dimLong=notanumber, dimFloat=20.0, met1=1.0}, dimensions=[dim1, dim1t, dim2, dimLong, dimFloat]}], exceptions: [could not convert value [notanumber] to long,]", "Unable to parse row [unparseable2]", "Unable to parse row [unparseable]", "Encountered row with timestamp that cannot be represented as a long: [MapBasedInputRow{timestamp=246140482-04-24T15:36:27.903Z, event={timestamp=246140482-04-24T15:36:27.903Z, dim1=x, dim2=z, dimLong=10, dimFloat=20.0, met1=1.0}, dimensions=[dim1, dim1t, dim2, dimLong, dimFloat]}]"));
        Assert.assertEquals(unparseableEvents, reportData.getUnparseableEvents());
    }

    @Test(timeout = 60000L)
    public void testMultipleParseExceptionsFailure() throws Exception {
        reportParseExceptions = false;
        maxParseExceptions = 2;
        maxSavedParseExceptions = 2;
        // Insert data
        insertData();
        final KafkaIndexTask task = createTask(null, new KafkaIndexTaskIOConfig(0, "sequence0", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 2L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 10L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
        final ListenableFuture<TaskStatus> future = runTask(task);
        TaskStatus status = future.get();
        // Wait for task to exit
        Assert.assertEquals(FAILED, status.getStatusCode());
        IndexTaskTest.checkTaskStatusErrorMsgForParseExceptionsExceeded(status);
        // Check metrics
        Assert.assertEquals(3, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getProcessedWithError());
        Assert.assertEquals(3, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published metadata
        Assert.assertEquals(ImmutableSet.of(), publishedDescriptors());
        Assert.assertNull(metadataStorageCoordinator.getDataSourceMetadata(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource()));
        IngestionStatsAndErrorsTaskReportData reportData = getTaskReportData();
        Map<String, Object> expectedMetrics = ImmutableMap.of(BUILD_SEGMENTS, ImmutableMap.of(PROCESSED, 3, PROCESSED_WITH_ERROR, 0, UNPARSEABLE, 3, THROWN_AWAY, 0));
        Assert.assertEquals(expectedMetrics, reportData.getRowStats());
        Map<String, Object> unparseableEvents = ImmutableMap.of(BUILD_SEGMENTS, Arrays.asList("Unable to parse row [unparseable2]", "Unable to parse row [unparseable]"));
        Assert.assertEquals(unparseableEvents, reportData.getUnparseableEvents());
    }

    @Test(timeout = 60000L)
    public void testRunReplicas() throws Exception {
        final KafkaIndexTask task1 = createTask(null, new KafkaIndexTaskIOConfig(0, "sequence0", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 2L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
        final KafkaIndexTask task2 = createTask(null, new KafkaIndexTaskIOConfig(0, "sequence0", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 2L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
        final ListenableFuture<TaskStatus> future1 = runTask(task1);
        final ListenableFuture<TaskStatus> future2 = runTask(task2);
        // Insert data
        insertData();
        // Wait for tasks to exit
        Assert.assertEquals(SUCCESS, future1.get().getStatusCode());
        Assert.assertEquals(SUCCESS, future2.get().getStatusCode());
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
        Assert.assertEquals(new KafkaDataSourceMetadata(new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L))), metadataStorageCoordinator.getDataSourceMetadata(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("d", "e"), readSegmentColumn("dim1", desc2));
    }

    @Test(timeout = 60000L)
    public void testRunConflicting() throws Exception {
        final KafkaIndexTask task1 = createTask(null, new KafkaIndexTaskIOConfig(0, "sequence0", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 2L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
        final KafkaIndexTask task2 = createTask(null, new KafkaIndexTaskIOConfig(1, "sequence1", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 3L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 10L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
        // Insert data
        insertData();
        // Run first task
        final ListenableFuture<TaskStatus> future1 = runTask(task1);
        Assert.assertEquals(SUCCESS, future1.get().getStatusCode());
        // Run second task
        final ListenableFuture<TaskStatus> future2 = runTask(task2);
        Assert.assertEquals(FAILED, future2.get().getStatusCode());
        // Check metrics
        Assert.assertEquals(3, task1.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task1.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task1.getRunner().getRowIngestionMeters().getThrownAway());
        Assert.assertEquals(3, task2.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(3, task2.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(1, task2.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published segments & metadata, should all be from the first task
        SegmentDescriptor desc1 = sd(task1, "2010/P1D", 0);
        SegmentDescriptor desc2 = sd(task1, "2011/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2), publishedDescriptors());
        Assert.assertEquals(new KafkaDataSourceMetadata(new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L))), metadataStorageCoordinator.getDataSourceMetadata(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("d", "e"), readSegmentColumn("dim1", desc2));
    }

    @Test(timeout = 60000L)
    public void testRunConflictingWithoutTransactions() throws Exception {
        final KafkaIndexTask task1 = createTask(null, new KafkaIndexTaskIOConfig(0, "sequence0", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 2L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, false, null, null));
        final KafkaIndexTask task2 = createTask(null, new KafkaIndexTaskIOConfig(1, "sequence1", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 3L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 10L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, false, null, null));
        // Insert data
        insertData();
        // Run first task
        final ListenableFuture<TaskStatus> future1 = runTask(task1);
        Assert.assertEquals(SUCCESS, future1.get().getStatusCode());
        // Check published segments & metadata
        SegmentDescriptor desc1 = sd(task1, "2010/P1D", 0);
        SegmentDescriptor desc2 = sd(task1, "2011/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2), publishedDescriptors());
        Assert.assertNull(metadataStorageCoordinator.getDataSourceMetadata(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Run second task
        final ListenableFuture<TaskStatus> future2 = runTask(task2);
        Assert.assertEquals(SUCCESS, future2.get().getStatusCode());
        // Check metrics
        Assert.assertEquals(3, task1.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task1.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task1.getRunner().getRowIngestionMeters().getThrownAway());
        Assert.assertEquals(3, task2.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(3, task2.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(1, task2.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published segments & metadata
        SegmentDescriptor desc3 = sd(task2, "2011/P1D", 1);
        SegmentDescriptor desc4 = sd(task2, "2013/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2, desc3, desc4), publishedDescriptors());
        Assert.assertNull(metadataStorageCoordinator.getDataSourceMetadata(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("d", "e"), readSegmentColumn("dim1", desc2));
        Assert.assertEquals(ImmutableList.of("d", "e"), readSegmentColumn("dim1", desc3));
        Assert.assertEquals(ImmutableList.of("f"), readSegmentColumn("dim1", desc4));
    }

    @Test(timeout = 60000L)
    public void testRunOneTaskTwoPartitions() throws Exception {
        final KafkaIndexTask task = createTask(null, new KafkaIndexTaskIOConfig(0, "sequence0", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 2L, 1, 0L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L, 1, 2L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
        final ListenableFuture<TaskStatus> future = runTask(task);
        // Insert data
        insertData();
        // Wait for tasks to exit
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
        // Check metrics
        Assert.assertEquals(5, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published segments & metadata
        SegmentDescriptor desc1 = sd(task, "2010/P1D", 0);
        SegmentDescriptor desc2 = sd(task, "2011/P1D", 0);
        // desc3 will not be created in KafkaIndexTask (0.12.x) as it does not create per Kafka partition Druid segments
        SegmentDescriptor desc3 = sd(task, "2011/P1D", 1);
        SegmentDescriptor desc4 = sd(task, "2012/P1D", 0);
        Assert.assertEquals((isIncrementalHandoffSupported ? ImmutableSet.of(desc1, desc2, desc4) : ImmutableSet.of(desc1, desc2, desc3, desc4)), publishedDescriptors());
        Assert.assertEquals(new KafkaDataSourceMetadata(new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L, 1, 2L))), metadataStorageCoordinator.getDataSourceMetadata(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("g"), readSegmentColumn("dim1", desc4));
        // Check desc2/desc3 without strong ordering because two partitions are interleaved nondeterministically
        Assert.assertEquals((isIncrementalHandoffSupported ? ImmutableSet.of(ImmutableList.of("d", "e", "h")) : ImmutableSet.of(ImmutableList.of("d", "e"), ImmutableList.of("h"))), (isIncrementalHandoffSupported ? ImmutableSet.of(readSegmentColumn("dim1", desc2)) : ImmutableSet.of(readSegmentColumn("dim1", desc2), readSegmentColumn("dim1", desc3))));
    }

    @Test(timeout = 60000L)
    public void testRunTwoTasksTwoPartitions() throws Exception {
        final KafkaIndexTask task1 = createTask(null, new KafkaIndexTaskIOConfig(0, "sequence0", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 2L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
        final KafkaIndexTask task2 = createTask(null, new KafkaIndexTaskIOConfig(1, "sequence1", new SeekableStreamPartitions(topic, ImmutableMap.of(1, 0L)), new SeekableStreamPartitions(topic, ImmutableMap.of(1, 1L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
        final ListenableFuture<TaskStatus> future1 = runTask(task1);
        final ListenableFuture<TaskStatus> future2 = runTask(task2);
        // Insert data
        insertData();
        // Wait for tasks to exit
        Assert.assertEquals(SUCCESS, future1.get().getStatusCode());
        Assert.assertEquals(SUCCESS, future2.get().getStatusCode());
        // Check metrics
        Assert.assertEquals(3, task1.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task1.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task1.getRunner().getRowIngestionMeters().getThrownAway());
        Assert.assertEquals(1, task2.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task2.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task2.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published segments & metadata
        SegmentDescriptor desc1 = sd(task1, "2010/P1D", 0);
        SegmentDescriptor desc2 = sd(task1, "2011/P1D", 0);
        SegmentDescriptor desc3 = sd(task2, "2012/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2, desc3), publishedDescriptors());
        Assert.assertEquals(new KafkaDataSourceMetadata(new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L, 1, 1L))), metadataStorageCoordinator.getDataSourceMetadata(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("d", "e"), readSegmentColumn("dim1", desc2));
        Assert.assertEquals(ImmutableList.of("g"), readSegmentColumn("dim1", desc3));
    }

    @Test(timeout = 60000L)
    public void testRestore() throws Exception {
        final KafkaIndexTask task1 = createTask(null, new KafkaIndexTaskIOConfig(0, "sequence0", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 2L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 6L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
        final ListenableFuture<TaskStatus> future1 = runTask(task1);
        // Insert some data, but not enough for the task to finish
        try (final KafkaProducer<byte[], byte[]> kafkaProducer = KafkaIndexTaskTest.kafkaServer.newProducer()) {
            kafkaProducer.initTransactions();
            kafkaProducer.beginTransaction();
            for (ProducerRecord<byte[], byte[]> record : Iterables.limit(records, 4)) {
                kafkaProducer.send(record).get();
            }
            kafkaProducer.commitTransaction();
        }
        while ((countEvents(task1)) != 2) {
            Thread.sleep(25);
        } 
        Assert.assertEquals(2, countEvents(task1));
        // Stop without publishing segment
        task1.stopGracefully(toolboxFactory.build(task1).getConfig());
        unlockAppenderatorBasePersistDirForTask(task1);
        Assert.assertEquals(SUCCESS, future1.get().getStatusCode());
        // Start a new task
        final KafkaIndexTask task2 = createTask(task1.getId(), new KafkaIndexTaskIOConfig(0, "sequence0", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 2L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 6L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
        final ListenableFuture<TaskStatus> future2 = runTask(task2);
        // Insert remaining data
        try (final KafkaProducer<byte[], byte[]> kafkaProducer = KafkaIndexTaskTest.kafkaServer.newProducer()) {
            kafkaProducer.initTransactions();
            kafkaProducer.beginTransaction();
            for (ProducerRecord<byte[], byte[]> record : Iterables.skip(records, 4)) {
                kafkaProducer.send(record).get();
            }
            kafkaProducer.commitTransaction();
        }
        // Wait for task to exit
        Assert.assertEquals(SUCCESS, future2.get().getStatusCode());
        // Check metrics
        Assert.assertEquals(2, task1.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task1.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task1.getRunner().getRowIngestionMeters().getThrownAway());
        Assert.assertEquals(1, task2.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task2.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task2.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published segments & metadata
        SegmentDescriptor desc1 = sd(task1, "2010/P1D", 0);
        SegmentDescriptor desc2 = sd(task1, "2011/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2), publishedDescriptors());
        Assert.assertEquals(new KafkaDataSourceMetadata(new SeekableStreamPartitions(topic, ImmutableMap.of(0, 6L))), metadataStorageCoordinator.getDataSourceMetadata(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("d", "e"), readSegmentColumn("dim1", desc2));
    }

    @Test(timeout = 60000L)
    public void testRunWithPauseAndResume() throws Exception {
        final KafkaIndexTask task = createTask(null, new KafkaIndexTaskIOConfig(0, "sequence0", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 2L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 6L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
        final ListenableFuture<TaskStatus> future = runTask(task);
        // Insert some data, but not enough for the task to finish
        try (final KafkaProducer<byte[], byte[]> kafkaProducer = KafkaIndexTaskTest.kafkaServer.newProducer()) {
            kafkaProducer.initTransactions();
            kafkaProducer.beginTransaction();
            for (ProducerRecord<byte[], byte[]> record : Iterables.limit(records, 4)) {
                kafkaProducer.send(record).get();
            }
            kafkaProducer.flush();
            kafkaProducer.commitTransaction();
        }
        while ((countEvents(task)) != 2) {
            Thread.sleep(25);
        } 
        Assert.assertEquals(2, countEvents(task));
        Assert.assertEquals(READING, task.getRunner().getStatus());
        Map<Integer, Long> currentOffsets = KafkaIndexTaskTest.objectMapper.readValue(task.getRunner().pause().getEntity().toString(), new TypeReference<Map<Integer, Long>>() {});
        Assert.assertEquals(PAUSED, task.getRunner().getStatus());
        // Insert remaining data
        try (final KafkaProducer<byte[], byte[]> kafkaProducer = KafkaIndexTaskTest.kafkaServer.newProducer()) {
            kafkaProducer.initTransactions();
            kafkaProducer.beginTransaction();
            for (ProducerRecord<byte[], byte[]> record : Iterables.skip(records, 4)) {
                kafkaProducer.send(record).get();
            }
            kafkaProducer.commitTransaction();
        }
        try {
            future.get(10, TimeUnit.SECONDS);
            Assert.fail("Task completed when it should have been paused");
        } catch (TimeoutException e) {
            // carry on..
        }
        Assert.assertEquals(currentOffsets, task.getRunner().getCurrentOffsets());
        task.getRunner().resume();
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
        Assert.assertEquals(task.getRunner().getEndOffsets(), task.getRunner().getCurrentOffsets());
        // Check metrics
        Assert.assertEquals(3, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(0, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published metadata
        SegmentDescriptor desc1 = sd(task, "2010/P1D", 0);
        SegmentDescriptor desc2 = sd(task, "2011/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2), publishedDescriptors());
        Assert.assertEquals(new KafkaDataSourceMetadata(new SeekableStreamPartitions(topic, ImmutableMap.of(0, 6L))), metadataStorageCoordinator.getDataSourceMetadata(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("d", "e"), readSegmentColumn("dim1", desc2));
    }

    @Test(timeout = 60000L)
    public void testRunWithOffsetOutOfRangeExceptionAndPause() throws Exception {
        final KafkaIndexTask task = createTask(null, new KafkaIndexTaskIOConfig(0, "sequence0", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 2L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
        runTask(task);
        while (!(task.getRunner().getStatus().equals(READING))) {
            Thread.sleep(2000);
        } 
        task.getRunner().pause();
        while (!(task.getRunner().getStatus().equals(PAUSED))) {
            Thread.sleep(25);
        } 
    }

    @Test(timeout = 60000L)
    public void testRunWithOffsetOutOfRangeExceptionAndNextOffsetGreaterThanLeastAvailable() throws Exception {
        resetOffsetAutomatically = true;
        // Insert data
        insertData();
        final KafkaIndexTask task = createTask(null, new KafkaIndexTaskIOConfig(0, "sequence0", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 200L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 500L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
        runTask(task);
        while (!(task.getRunner().getStatus().equals(READING))) {
            Thread.sleep(20);
        } 
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(task.getRunner().getStatus(), READING);
            // Offset should not be reset
            Assert.assertTrue(((task.getRunner().getCurrentOffsets().get(0)) == 200L));
        }
    }

    @Test(timeout = 60000L)
    public void testRunContextSequenceAheadOfStartingOffsets() throws Exception {
        // This tests the case when a replacement task is created in place of a failed test
        // which has done some incremental handoffs, thus the context will contain starting
        // sequence offsets from which the task should start reading and ignore the start offsets
        if (!(isIncrementalHandoffSupported)) {
            return;
        }
        // Insert data
        insertData();
        final TreeMap<Integer, Map<Integer, Long>> sequences = new TreeMap<>();
        // Here the sequence number is 1 meaning that one incremental handoff was done by the failed task
        // and this task should start reading from offset 2 for partition 0
        sequences.put(1, ImmutableMap.of(0, 2L));
        final Map<String, Object> context = new HashMap<>();
        context.put("checkpoints", KafkaIndexTaskTest.objectMapper.writerWithType(new TypeReference<TreeMap<Integer, Map<Integer, Long>>>() {}).writeValueAsString(sequences));
        final KafkaIndexTask task = createTask(null, // task should ignore these and use sequence info sent in the context
        new KafkaIndexTaskIOConfig(0, "sequence0", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 0L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null), context);
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
        Assert.assertEquals(new KafkaDataSourceMetadata(new SeekableStreamPartitions(topic, ImmutableMap.of(0, 5L))), metadataStorageCoordinator.getDataSourceMetadata(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("c"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("d", "e"), readSegmentColumn("dim1", desc2));
    }

    @Test(timeout = 60000L)
    public void testRunWithDuplicateRequest() throws Exception {
        // Insert data
        insertData();
        final KafkaIndexTask task = createTask(null, new KafkaIndexTaskIOConfig(0, "sequence0", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 200L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 500L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
        runTask(task);
        while (!(task.getRunner().getStatus().equals(READING))) {
            Thread.sleep(20);
        } 
        // first setEndOffsets request
        task.getRunner().pause();
        task.getRunner().setEndOffsets(ImmutableMap.of(0, 500L), true);
        Assert.assertEquals(READING, task.getRunner().getStatus());
        // duplicate setEndOffsets request
        task.getRunner().pause();
        task.getRunner().setEndOffsets(ImmutableMap.of(0, 500L), true);
        Assert.assertEquals(READING, task.getRunner().getStatus());
    }

    @Test(timeout = 60000L)
    public void testRunTransactionModeRollback() throws Exception {
        final KafkaIndexTask task = createTask(null, new KafkaIndexTaskIOConfig(0, "sequence0", new SeekableStreamPartitions(topic, ImmutableMap.of(0, 0L)), new SeekableStreamPartitions(topic, ImmutableMap.of(0, 13L)), KafkaIndexTaskTest.kafkaServer.consumerProperties(), KafkaSupervisorIOConfig.DEFAULT_POLL_TIMEOUT_MILLIS, true, null, null));
        final ListenableFuture<TaskStatus> future = runTask(task);
        // Insert 2 records initially
        try (final KafkaProducer<byte[], byte[]> kafkaProducer = KafkaIndexTaskTest.kafkaServer.newProducer()) {
            kafkaProducer.initTransactions();
            kafkaProducer.beginTransaction();
            for (ProducerRecord<byte[], byte[]> record : Iterables.limit(records, 2)) {
                kafkaProducer.send(record).get();
            }
            kafkaProducer.commitTransaction();
        }
        while ((countEvents(task)) != 2) {
            Thread.sleep(25);
        } 
        Assert.assertEquals(2, countEvents(task));
        Assert.assertEquals(READING, task.getRunner().getStatus());
        // verify the 2 indexed records
        final QuerySegmentSpec firstInterval = KafkaIndexTaskTest.objectMapper.readValue("\"2008/2010\"", QuerySegmentSpec.class);
        Iterable<ScanResultValue> scanResultValues = scanData(task, firstInterval);
        Assert.assertEquals(2, Iterables.size(scanResultValues));
        // Insert 3 more records and rollback
        try (final KafkaProducer<byte[], byte[]> kafkaProducer = KafkaIndexTaskTest.kafkaServer.newProducer()) {
            kafkaProducer.initTransactions();
            kafkaProducer.beginTransaction();
            for (ProducerRecord<byte[], byte[]> record : Iterables.limit(Iterables.skip(records, 2), 3)) {
                kafkaProducer.send(record).get();
            }
            kafkaProducer.flush();
            kafkaProducer.abortTransaction();
        }
        Assert.assertEquals(2, countEvents(task));
        Assert.assertEquals(READING, task.getRunner().getStatus());
        final QuerySegmentSpec rollbackedInterval = KafkaIndexTaskTest.objectMapper.readValue("\"2010/2012\"", QuerySegmentSpec.class);
        scanResultValues = scanData(task, rollbackedInterval);
        // verify that there are no records indexed in the rollbacked time period
        Assert.assertEquals(0, Iterables.size(scanResultValues));
        // Insert remaining data
        try (final KafkaProducer<byte[], byte[]> kafkaProducer = KafkaIndexTaskTest.kafkaServer.newProducer()) {
            kafkaProducer.initTransactions();
            kafkaProducer.beginTransaction();
            for (ProducerRecord<byte[], byte[]> record : Iterables.skip(records, 5)) {
                kafkaProducer.send(record).get();
            }
            kafkaProducer.commitTransaction();
        }
        final QuerySegmentSpec endInterval = KafkaIndexTaskTest.objectMapper.readValue("\"2008/2049\"", QuerySegmentSpec.class);
        Iterable<ScanResultValue> scanResultValues1 = scanData(task, endInterval);
        Assert.assertEquals(2, Iterables.size(scanResultValues1));
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
        Assert.assertEquals(task.getRunner().getEndOffsets(), task.getRunner().getCurrentOffsets());
        // Check metrics
        Assert.assertEquals(3, task.getRunner().getRowIngestionMeters().getProcessed());
        Assert.assertEquals(3, task.getRunner().getRowIngestionMeters().getUnparseable());
        Assert.assertEquals(1, task.getRunner().getRowIngestionMeters().getThrownAway());
        // Check published metadata
        SegmentDescriptor desc1 = sd(task, "2008/P1D", 0);
        SegmentDescriptor desc2 = sd(task, "2009/P1D", 0);
        SegmentDescriptor desc3 = sd(task, "2013/P1D", 0);
        SegmentDescriptor desc4 = sd(task, "2049/P1D", 0);
        Assert.assertEquals(ImmutableSet.of(desc1, desc2, desc3, desc4), publishedDescriptors());
        Assert.assertEquals(new KafkaDataSourceMetadata(new SeekableStreamPartitions(topic, ImmutableMap.of(0, 13L))), metadataStorageCoordinator.getDataSourceMetadata(KafkaIndexTaskTest.DATA_SCHEMA.getDataSource()));
        // Check segments in deep storage
        Assert.assertEquals(ImmutableList.of("a"), readSegmentColumn("dim1", desc1));
        Assert.assertEquals(ImmutableList.of("b"), readSegmentColumn("dim1", desc2));
        Assert.assertEquals(ImmutableList.of("f"), readSegmentColumn("dim1", desc3));
        Assert.assertEquals(ImmutableList.of("f"), readSegmentColumn("dim1", desc4));
    }
}

