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
package org.apache.druid.indexing.common.task;


import IngestionState.COMPLETED;
import RowIngestionMeters.BUILD_SEGMENTS;
import RowIngestionMeters.PROCESSED;
import RowIngestionMeters.PROCESSED_WITH_ERROR;
import RowIngestionMeters.THROWN_AWAY;
import RowIngestionMeters.UNPARSEABLE;
import TaskState.FAILED;
import TaskState.SUCCESS;
import TestDerbyConnector.DerbyConnectorRule;
import TransformSpec.NONE;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.commons.io.FileUtils;
import org.apache.druid.common.config.NullHandling;
import org.apache.druid.data.input.Firehose;
import org.apache.druid.data.input.FirehoseFactory;
import org.apache.druid.data.input.InputRow;
import org.apache.druid.data.input.impl.InputRowParser;
import org.apache.druid.indexer.TaskStatus;
import org.apache.druid.indexing.common.IngestionStatsAndErrorsTaskReportData;
import org.apache.druid.indexing.common.TaskToolboxFactory;
import org.apache.druid.indexing.common.stats.RowIngestionMetersFactory;
import org.apache.druid.indexing.overlord.TaskLockbox;
import org.apache.druid.indexing.overlord.TaskStorage;
import org.apache.druid.java.util.common.Pair;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.java.util.common.logger.Logger;
import org.apache.druid.java.util.common.parsers.ParseException;
import org.apache.druid.java.util.emitter.core.NoopEmitter;
import org.apache.druid.java.util.emitter.service.ServiceEmitter;
import org.apache.druid.math.expr.ExprMacroTable;
import org.apache.druid.metadata.TestDerbyConnector;
import org.apache.druid.query.SegmentDescriptor;
import org.apache.druid.query.filter.SelectorDimFilter;
import org.apache.druid.segment.TestHelper;
import org.apache.druid.segment.transform.TransformSpec;
import org.apache.druid.timeline.DataSegment;
import org.apache.druid.utils.Runnables;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


public class AppenderatorDriverRealtimeIndexTaskTest {
    private static final Logger log = new Logger(AppenderatorDriverRealtimeIndexTaskTest.class);

    private static final ServiceEmitter emitter = new ServiceEmitter("service", "host", new NoopEmitter());

    private static final ObjectMapper objectMapper = TestHelper.makeJsonMapper();

    private static final String FAIL_DIM = "__fail__";

    private static class TestFirehose implements Firehose {
        private final InputRowParser<Map<String, Object>> parser;

        private final Deque<Optional<Map<String, Object>>> queue = new ArrayDeque<>();

        private boolean closed = false;

        public TestFirehose(final InputRowParser<Map<String, Object>> parser) {
            this.parser = parser;
        }

        public void addRows(List<Map<String, Object>> rows) {
            synchronized(this) {
                rows.stream().map(Optional::ofNullable).forEach(queue::add);
                notifyAll();
            }
        }

        @Override
        public boolean hasMore() {
            try {
                synchronized(this) {
                    while ((queue.isEmpty()) && (!(closed))) {
                        wait();
                    } 
                    return !(queue.isEmpty());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw Throwables.propagate(e);
            }
        }

        @Override
        public InputRow nextRow() {
            synchronized(this) {
                final InputRow row = parser.parseBatch(queue.removeFirst().orElse(null)).get(0);
                if ((row != null) && ((row.getRaw(AppenderatorDriverRealtimeIndexTaskTest.FAIL_DIM)) != null)) {
                    throw new ParseException(AppenderatorDriverRealtimeIndexTaskTest.FAIL_DIM);
                }
                return row;
            }
        }

        @Override
        public Runnable commit() {
            return Runnables.getNoopRunnable();
        }

        @Override
        public void close() {
            synchronized(this) {
                closed = true;
                notifyAll();
            }
        }
    }

    private static class TestFirehoseFactory implements FirehoseFactory<InputRowParser> {
        public TestFirehoseFactory() {
        }

        @Override
        @SuppressWarnings("unchecked")
        public Firehose connect(InputRowParser parser, File temporaryDirectory) throws ParseException {
            return new AppenderatorDriverRealtimeIndexTaskTest.TestFirehose(parser);
        }
    }

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    @Rule
    public final DerbyConnectorRule derbyConnectorRule = new TestDerbyConnector.DerbyConnectorRule();

    private DateTime now;

    private ListeningExecutorService taskExec;

    private Map<SegmentDescriptor, Pair<Executor, Runnable>> handOffCallbacks;

    private Collection<DataSegment> publishedSegments;

    private CountDownLatch segmentLatch;

    private CountDownLatch handoffLatch;

    private TaskStorage taskStorage;

    private TaskLockbox taskLockbox;

    private TaskToolboxFactory taskToolboxFactory;

    private File baseDir;

    private File reportsFile;

    private RowIngestionMetersFactory rowIngestionMetersFactory;

    @Test(timeout = 60000L)
    public void testDefaultResource() {
        final AppenderatorDriverRealtimeIndexTask task = makeRealtimeTask(null);
        Assert.assertEquals(task.getId(), task.getTaskResource().getAvailabilityGroup());
    }

    @Test(timeout = 60000L)
    public void testHandoffTimeout() throws Exception {
        expectPublishedSegments(1);
        final AppenderatorDriverRealtimeIndexTask task = makeRealtimeTask(null, NONE, true, 100L, true, 0, 1);
        final ListenableFuture<TaskStatus> statusFuture = runTask(task);
        // Wait for firehose to show up, it starts off null.
        while ((task.getFirehose()) == null) {
            Thread.sleep(50);
        } 
        final AppenderatorDriverRealtimeIndexTaskTest.TestFirehose firehose = ((AppenderatorDriverRealtimeIndexTaskTest.TestFirehose) (task.getFirehose()));
        firehose.addRows(ImmutableList.of(ImmutableMap.of("t", now.getMillis(), "dim1", "foo", "met1", "1")));
        // Stop the firehose, this will drain out existing events.
        firehose.close();
        // handoff would timeout, resulting in exception
        TaskStatus status = statusFuture.get();
        Assert.assertTrue(status.getErrorMsg().contains("java.util.concurrent.TimeoutException: Timeout waiting for task."));
    }

    @Test(timeout = 60000L)
    public void testBasics() throws Exception {
        expectPublishedSegments(1);
        final AppenderatorDriverRealtimeIndexTask task = makeRealtimeTask(null);
        final ListenableFuture<TaskStatus> statusFuture = runTask(task);
        // Wait for firehose to show up, it starts off null.
        while ((task.getFirehose()) == null) {
            Thread.sleep(50);
        } 
        final AppenderatorDriverRealtimeIndexTaskTest.TestFirehose firehose = ((AppenderatorDriverRealtimeIndexTaskTest.TestFirehose) (task.getFirehose()));
        firehose.addRows(ImmutableList.of(ImmutableMap.of("t", now.getMillis(), "dim1", "foo", "met1", "1"), ImmutableMap.of("t", now.getMillis(), "dim2", "bar", "met1", 2.0)));
        // Stop the firehose, this will drain out existing events.
        firehose.close();
        // Wait for publish.
        Collection<DataSegment> publishedSegments = awaitSegments();
        // Check metrics.
        Assert.assertEquals(2, task.getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task.getRowIngestionMeters().getThrownAway());
        Assert.assertEquals(0, task.getRowIngestionMeters().getUnparseable());
        // Do some queries.
        Assert.assertEquals(2, sumMetric(task, null, "rows").longValue());
        Assert.assertEquals(3, sumMetric(task, null, "met1").longValue());
        awaitHandoffs();
        for (DataSegment publishedSegment : publishedSegments) {
            Pair<Executor, Runnable> executorRunnablePair = handOffCallbacks.get(new SegmentDescriptor(publishedSegment.getInterval(), publishedSegment.getVersion(), publishedSegment.getShardSpec().getPartitionNum()));
            Assert.assertNotNull(((publishedSegment + " missing from handoff callbacks: ") + (handOffCallbacks)), executorRunnablePair);
            // Simulate handoff.
            executorRunnablePair.lhs.execute(executorRunnablePair.rhs);
        }
        handOffCallbacks.clear();
        // Wait for the task to finish.
        final TaskStatus taskStatus = statusFuture.get();
        Assert.assertEquals(SUCCESS, taskStatus.getStatusCode());
    }

    @Test(timeout = 60000L)
    public void testLateData() throws Exception {
        expectPublishedSegments(1);
        final AppenderatorDriverRealtimeIndexTask task = makeRealtimeTask(null);
        final ListenableFuture<TaskStatus> statusFuture = runTask(task);
        // Wait for firehose to show up, it starts off null.
        while ((task.getFirehose()) == null) {
            Thread.sleep(50);
        } 
        final AppenderatorDriverRealtimeIndexTaskTest.TestFirehose firehose = ((AppenderatorDriverRealtimeIndexTaskTest.TestFirehose) (task.getFirehose()));
        firehose.addRows(// Data is from 2 days ago, should still be processed
        ImmutableList.of(ImmutableMap.of("t", now.getMillis(), "dim1", "foo", "met1", "1"), ImmutableMap.of("t", now.minus(new Period("P2D")).getMillis(), "dim2", "bar", "met1", 2.0)));
        // Stop the firehose, this will drain out existing events.
        firehose.close();
        // Wait for publish.
        Collection<DataSegment> publishedSegments = awaitSegments();
        // Check metrics.
        Assert.assertEquals(2, task.getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task.getRowIngestionMeters().getThrownAway());
        Assert.assertEquals(0, task.getRowIngestionMeters().getUnparseable());
        // Do some queries.
        Assert.assertEquals(2, sumMetric(task, null, "rows").longValue());
        Assert.assertEquals(3, sumMetric(task, null, "met1").longValue());
        awaitHandoffs();
        for (DataSegment publishedSegment : publishedSegments) {
            Pair<Executor, Runnable> executorRunnablePair = handOffCallbacks.get(new SegmentDescriptor(publishedSegment.getInterval(), publishedSegment.getVersion(), publishedSegment.getShardSpec().getPartitionNum()));
            Assert.assertNotNull(((publishedSegment + " missing from handoff callbacks: ") + (handOffCallbacks)), executorRunnablePair);
            // Simulate handoff.
            executorRunnablePair.lhs.execute(executorRunnablePair.rhs);
        }
        handOffCallbacks.clear();
        // Wait for the task to finish.
        final TaskStatus taskStatus = statusFuture.get();
        Assert.assertEquals(SUCCESS, taskStatus.getStatusCode());
    }

    @Test(timeout = 60000L)
    public void testMaxRowsPerSegment() throws Exception {
        // Expect 2 segments as we will hit maxRowsPerSegment
        expectPublishedSegments(2);
        final AppenderatorDriverRealtimeIndexTask task = makeRealtimeTask(null);
        final ListenableFuture<TaskStatus> statusFuture = runTask(task);
        // Wait for firehose to show up, it starts off null.
        while ((task.getFirehose()) == null) {
            Thread.sleep(50);
        } 
        final AppenderatorDriverRealtimeIndexTaskTest.TestFirehose firehose = ((AppenderatorDriverRealtimeIndexTaskTest.TestFirehose) (task.getFirehose()));
        // maxRowsPerSegment is 1000 as configured in #makeRealtimeTask
        for (int i = 0; i < 2000; i++) {
            firehose.addRows(ImmutableList.of(ImmutableMap.of("t", now.getMillis(), "dim1", ("foo-" + i), "met1", "1")));
        }
        // Stop the firehose, this will drain out existing events.
        firehose.close();
        // Wait for publish.
        Collection<DataSegment> publishedSegments = awaitSegments();
        // Check metrics.
        Assert.assertEquals(2000, task.getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task.getRowIngestionMeters().getThrownAway());
        Assert.assertEquals(0, task.getRowIngestionMeters().getUnparseable());
        // Do some queries.
        Assert.assertEquals(2000, sumMetric(task, null, "rows").longValue());
        Assert.assertEquals(2000, sumMetric(task, null, "met1").longValue());
        awaitHandoffs();
        for (DataSegment publishedSegment : publishedSegments) {
            Pair<Executor, Runnable> executorRunnablePair = handOffCallbacks.get(new SegmentDescriptor(publishedSegment.getInterval(), publishedSegment.getVersion(), publishedSegment.getShardSpec().getPartitionNum()));
            Assert.assertNotNull(((publishedSegment + " missing from handoff callbacks: ") + (handOffCallbacks)), executorRunnablePair);
            // Simulate handoff.
            executorRunnablePair.lhs.execute(executorRunnablePair.rhs);
        }
        handOffCallbacks.clear();
        // Wait for the task to finish.
        final TaskStatus taskStatus = statusFuture.get();
        Assert.assertEquals(SUCCESS, taskStatus.getStatusCode());
    }

    @Test(timeout = 60000L)
    public void testMaxTotalRows() throws Exception {
        // Expect 2 segments as we will hit maxTotalRows
        expectPublishedSegments(2);
        final AppenderatorDriverRealtimeIndexTask task = makeRealtimeTask(null, Integer.MAX_VALUE, 1500L);
        final ListenableFuture<TaskStatus> statusFuture = runTask(task);
        // Wait for firehose to show up, it starts off null.
        while ((task.getFirehose()) == null) {
            Thread.sleep(50);
        } 
        final AppenderatorDriverRealtimeIndexTaskTest.TestFirehose firehose = ((AppenderatorDriverRealtimeIndexTaskTest.TestFirehose) (task.getFirehose()));
        // maxTotalRows is 1500
        for (int i = 0; i < 2000; i++) {
            firehose.addRows(ImmutableList.of(ImmutableMap.of("t", now.getMillis(), "dim1", ("foo-" + i), "met1", "1")));
        }
        // Stop the firehose, this will drain out existing events.
        firehose.close();
        // Wait for publish.
        Collection<DataSegment> publishedSegments = awaitSegments();
        // Check metrics.
        Assert.assertEquals(2000, task.getRowIngestionMeters().getProcessed());
        Assert.assertEquals(0, task.getRowIngestionMeters().getThrownAway());
        Assert.assertEquals(0, task.getRowIngestionMeters().getUnparseable());
        // Do some queries.
        Assert.assertEquals(2000, sumMetric(task, null, "rows").longValue());
        Assert.assertEquals(2000, sumMetric(task, null, "met1").longValue());
        awaitHandoffs();
        Assert.assertEquals(2, publishedSegments.size());
        for (DataSegment publishedSegment : publishedSegments) {
            Pair<Executor, Runnable> executorRunnablePair = handOffCallbacks.get(new SegmentDescriptor(publishedSegment.getInterval(), publishedSegment.getVersion(), publishedSegment.getShardSpec().getPartitionNum()));
            Assert.assertNotNull(((publishedSegment + " missing from handoff callbacks: ") + (handOffCallbacks)), executorRunnablePair);
            // Simulate handoff.
            executorRunnablePair.lhs.execute(executorRunnablePair.rhs);
        }
        handOffCallbacks.clear();
        // Wait for the task to finish.
        final TaskStatus taskStatus = statusFuture.get();
        Assert.assertEquals(SUCCESS, taskStatus.getStatusCode());
    }

    @Test(timeout = 60000L)
    public void testTransformSpec() throws Exception {
        expectPublishedSegments(2);
        final TransformSpec transformSpec = new TransformSpec(new SelectorDimFilter("dim1", "foo", null), ImmutableList.of(new org.apache.druid.segment.transform.ExpressionTransform("dim1t", "concat(dim1,dim1)", ExprMacroTable.nil())));
        final AppenderatorDriverRealtimeIndexTask task = makeRealtimeTask(null, transformSpec, true, 0, true, 0, 1);
        final ListenableFuture<TaskStatus> statusFuture = runTask(task);
        // Wait for firehose to show up, it starts off null.
        while ((task.getFirehose()) == null) {
            Thread.sleep(50);
        } 
        final AppenderatorDriverRealtimeIndexTaskTest.TestFirehose firehose = ((AppenderatorDriverRealtimeIndexTaskTest.TestFirehose) (task.getFirehose()));
        firehose.addRows(ImmutableList.of(ImmutableMap.of("t", now.getMillis(), "dim1", "foo", "met1", "1"), ImmutableMap.of("t", now.minus(new Period("P1D")).getMillis(), "dim1", "foo", "met1", 2.0), ImmutableMap.of("t", now.getMillis(), "dim2", "bar", "met1", 2.0)));
        // Stop the firehose, this will drain out existing events.
        firehose.close();
        Collection<DataSegment> publishedSegments = awaitSegments();
        // Check metrics.
        Assert.assertEquals(2, task.getRowIngestionMeters().getProcessed());
        Assert.assertEquals(1, task.getRowIngestionMeters().getThrownAway());
        Assert.assertEquals(0, task.getRowIngestionMeters().getUnparseable());
        // Do some queries.
        Assert.assertEquals(2, sumMetric(task, null, "rows").longValue());
        Assert.assertEquals(2, sumMetric(task, new SelectorDimFilter("dim1t", "foofoo", null), "rows").longValue());
        if (NullHandling.replaceWithDefault()) {
            Assert.assertEquals(0, sumMetric(task, new SelectorDimFilter("dim1t", "barbar", null), "metric1").longValue());
        } else {
            Assert.assertNull(sumMetric(task, new SelectorDimFilter("dim1t", "barbar", null), "metric1"));
        }
        Assert.assertEquals(3, sumMetric(task, null, "met1").longValue());
        awaitHandoffs();
        for (DataSegment publishedSegment : publishedSegments) {
            Pair<Executor, Runnable> executorRunnablePair = handOffCallbacks.get(new SegmentDescriptor(publishedSegment.getInterval(), publishedSegment.getVersion(), publishedSegment.getShardSpec().getPartitionNum()));
            Assert.assertNotNull(((publishedSegment + " missing from handoff callbacks: ") + (handOffCallbacks)), executorRunnablePair);
            // Simulate handoff.
            executorRunnablePair.lhs.execute(executorRunnablePair.rhs);
        }
        handOffCallbacks.clear();
        // Wait for the task to finish.
        final TaskStatus taskStatus = statusFuture.get();
        Assert.assertEquals(SUCCESS, taskStatus.getStatusCode());
    }

    @Test(timeout = 60000L)
    public void testReportParseExceptionsOnBadMetric() throws Exception {
        expectPublishedSegments(0);
        final AppenderatorDriverRealtimeIndexTask task = makeRealtimeTask(null, true);
        final ListenableFuture<TaskStatus> statusFuture = runTask(task);
        // Wait for firehose to show up, it starts off null.
        while ((task.getFirehose()) == null) {
            Thread.sleep(50);
        } 
        final AppenderatorDriverRealtimeIndexTaskTest.TestFirehose firehose = ((AppenderatorDriverRealtimeIndexTaskTest.TestFirehose) (task.getFirehose()));
        firehose.addRows(ImmutableList.of(ImmutableMap.of("t", 2000000L, "dim1", "foo", "met1", "1"), ImmutableMap.of("t", 3000000L, "dim1", "foo", "met1", "foo"), ImmutableMap.of("t", now.minus(new Period("P1D")).getMillis(), "dim1", "foo", "met1", "foo"), ImmutableMap.of("t", 4000000L, "dim2", "bar", "met1", 2.0)));
        // Stop the firehose, this will drain out existing events.
        firehose.close();
        // Wait for the task to finish.
        TaskStatus status = statusFuture.get();
        Assert.assertTrue(status.getErrorMsg().contains("java.lang.RuntimeException: Max parse exceptions exceeded, terminating task..."));
        IngestionStatsAndErrorsTaskReportData reportData = getTaskReportData();
        Map<String, Object> expectedUnparseables = ImmutableMap.of(BUILD_SEGMENTS, Collections.singletonList("Found unparseable columns in row: [MapBasedInputRow{timestamp=1970-01-01T00:50:00.000Z, event={t=3000000, dim1=foo, met1=foo}, dimensions=[dim1, dim2, dim1t, dimLong, dimFloat]}], exceptions: [Unable to parse value[foo] for field[met1],]"));
        Assert.assertEquals(expectedUnparseables, reportData.getUnparseableEvents());
    }

    @Test(timeout = 60000L)
    public void testNoReportParseExceptions() throws Exception {
        expectPublishedSegments(1);
        final AppenderatorDriverRealtimeIndexTask task = makeRealtimeTask(null, NONE, false, 0, true, null, 1);
        final ListenableFuture<TaskStatus> statusFuture = runTask(task);
        // Wait for firehose to show up, it starts off null.
        while ((task.getFirehose()) == null) {
            Thread.sleep(50);
        } 
        final AppenderatorDriverRealtimeIndexTaskTest.TestFirehose firehose = ((AppenderatorDriverRealtimeIndexTaskTest.TestFirehose) (task.getFirehose()));
        firehose.addRows(// Good row- will be processed.
        // Null row- will be thrown away.
        // Bad metric- will count as processed, but that particular metric won't update.
        // Bad row- will be unparseable.
        // Good row- will be processed.
        Arrays.asList(ImmutableMap.of("t", now.getMillis(), "dim1", "foo", "met1", "1"), null, ImmutableMap.of("t", now.getMillis(), "dim1", "foo", "met1", "foo"), ImmutableMap.of("dim1", "foo", "met1", 2.0, AppenderatorDriverRealtimeIndexTaskTest.FAIL_DIM, "x"), ImmutableMap.of("t", now.getMillis(), "dim2", "bar", "met1", 2.0)));
        // Stop the firehose, this will drain out existing events.
        firehose.close();
        // Wait for publish.
        Collection<DataSegment> publishedSegments = awaitSegments();
        DataSegment publishedSegment = Iterables.getOnlyElement(publishedSegments);
        // Check metrics.
        Assert.assertEquals(2, task.getRowIngestionMeters().getProcessed());
        Assert.assertEquals(1, task.getRowIngestionMeters().getProcessedWithError());
        Assert.assertEquals(0, task.getRowIngestionMeters().getThrownAway());
        Assert.assertEquals(2, task.getRowIngestionMeters().getUnparseable());
        // Do some queries.
        Assert.assertEquals(3, sumMetric(task, null, "rows").longValue());
        Assert.assertEquals(3, sumMetric(task, null, "met1").longValue());
        awaitHandoffs();
        // Simulate handoff.
        for (Map.Entry<SegmentDescriptor, Pair<Executor, Runnable>> entry : handOffCallbacks.entrySet()) {
            final Pair<Executor, Runnable> executorRunnablePair = entry.getValue();
            Assert.assertEquals(new SegmentDescriptor(publishedSegment.getInterval(), publishedSegment.getVersion(), publishedSegment.getShardSpec().getPartitionNum()), entry.getKey());
            executorRunnablePair.lhs.execute(executorRunnablePair.rhs);
        }
        handOffCallbacks.clear();
        Map<String, Object> expectedMetrics = ImmutableMap.of(BUILD_SEGMENTS, ImmutableMap.of(PROCESSED, 2, PROCESSED_WITH_ERROR, 1, UNPARSEABLE, 2, THROWN_AWAY, 0));
        // Wait for the task to finish.
        final TaskStatus taskStatus = statusFuture.get();
        Assert.assertEquals(SUCCESS, taskStatus.getStatusCode());
        IngestionStatsAndErrorsTaskReportData reportData = getTaskReportData();
        Assert.assertEquals(expectedMetrics, reportData.getRowStats());
    }

    @Test(timeout = 60000L)
    public void testMultipleParseExceptionsSuccess() throws Exception {
        expectPublishedSegments(1);
        final AppenderatorDriverRealtimeIndexTask task = makeRealtimeTask(null, NONE, false, 0, true, 10, 10);
        final ListenableFuture<TaskStatus> statusFuture = runTask(task);
        // Wait for firehose to show up, it starts off null.
        while ((task.getFirehose()) == null) {
            Thread.sleep(50);
        } 
        final AppenderatorDriverRealtimeIndexTaskTest.TestFirehose firehose = ((AppenderatorDriverRealtimeIndexTaskTest.TestFirehose) (task.getFirehose()));
        firehose.addRows(// Good row- will be processed.
        // Null row- will be thrown away.
        // Bad metric- will count as processed, but that particular metric won't update.
        // Bad long dim- will count as processed, but bad dims will get default values
        // Bad row- will be unparseable.
        // Good row- will be processed.
        Arrays.asList(ImmutableMap.of("t", 1521251960729L, "dim1", "foo", "met1", "1"), null, ImmutableMap.of("t", 1521251960729L, "dim1", "foo", "met1", "foo"), ImmutableMap.of("t", 1521251960729L, "dim1", "foo", "dimLong", "notnumber", "dimFloat", "notnumber", "met1", "foo"), ImmutableMap.of("dim1", "foo", "met1", 2.0, AppenderatorDriverRealtimeIndexTaskTest.FAIL_DIM, "x"), ImmutableMap.of("t", 1521251960729L, "dim2", "bar", "met1", 2.0)));
        // Stop the firehose, this will drain out existing events.
        firehose.close();
        // Wait for publish.
        Collection<DataSegment> publishedSegments = awaitSegments();
        DataSegment publishedSegment = Iterables.getOnlyElement(publishedSegments);
        // Check metrics.
        Assert.assertEquals(2, task.getRowIngestionMeters().getProcessed());
        Assert.assertEquals(2, task.getRowIngestionMeters().getProcessedWithError());
        Assert.assertEquals(0, task.getRowIngestionMeters().getThrownAway());
        Assert.assertEquals(2, task.getRowIngestionMeters().getUnparseable());
        // Do some queries.
        Assert.assertEquals(4, sumMetric(task, null, "rows").longValue());
        Assert.assertEquals(3, sumMetric(task, null, "met1").longValue());
        awaitHandoffs();
        // Simulate handoff.
        for (Map.Entry<SegmentDescriptor, Pair<Executor, Runnable>> entry : handOffCallbacks.entrySet()) {
            final Pair<Executor, Runnable> executorRunnablePair = entry.getValue();
            Assert.assertEquals(new SegmentDescriptor(publishedSegment.getInterval(), publishedSegment.getVersion(), publishedSegment.getShardSpec().getPartitionNum()), entry.getKey());
            executorRunnablePair.lhs.execute(executorRunnablePair.rhs);
        }
        handOffCallbacks.clear();
        Map<String, Object> expectedMetrics = ImmutableMap.of(BUILD_SEGMENTS, ImmutableMap.of(PROCESSED, 2, PROCESSED_WITH_ERROR, 2, UNPARSEABLE, 2, THROWN_AWAY, 0));
        // Wait for the task to finish.
        final TaskStatus taskStatus = statusFuture.get();
        Assert.assertEquals(SUCCESS, taskStatus.getStatusCode());
        IngestionStatsAndErrorsTaskReportData reportData = getTaskReportData();
        Assert.assertEquals(expectedMetrics, reportData.getRowStats());
        Map<String, Object> expectedUnparseables = ImmutableMap.of(BUILD_SEGMENTS, Arrays.asList("Unparseable timestamp found! Event: {dim1=foo, met1=2.0, __fail__=x}", "Found unparseable columns in row: [MapBasedInputRow{timestamp=2018-03-17T01:59:20.729Z, event={t=1521251960729, dim1=foo, dimLong=notnumber, dimFloat=notnumber, met1=foo}, dimensions=[dim1, dim2, dim1t, dimLong, dimFloat]}], exceptions: [could not convert value [notnumber] to long,could not convert value [notnumber] to float,Unable to parse value[foo] for field[met1],]", "Found unparseable columns in row: [MapBasedInputRow{timestamp=2018-03-17T01:59:20.729Z, event={t=1521251960729, dim1=foo, met1=foo}, dimensions=[dim1, dim2, dim1t, dimLong, dimFloat]}], exceptions: [Unable to parse value[foo] for field[met1],]", "Unparseable timestamp found! Event: null"));
        Assert.assertEquals(expectedUnparseables, reportData.getUnparseableEvents());
        Assert.assertEquals(COMPLETED, reportData.getIngestionState());
    }

    @Test(timeout = 60000L)
    public void testMultipleParseExceptionsFailure() throws Exception {
        expectPublishedSegments(1);
        final AppenderatorDriverRealtimeIndexTask task = makeRealtimeTask(null, NONE, false, 0, true, 3, 10);
        final ListenableFuture<TaskStatus> statusFuture = runTask(task);
        // Wait for firehose to show up, it starts off null.
        while ((task.getFirehose()) == null) {
            Thread.sleep(50);
        } 
        final AppenderatorDriverRealtimeIndexTaskTest.TestFirehose firehose = ((AppenderatorDriverRealtimeIndexTaskTest.TestFirehose) (task.getFirehose()));
        firehose.addRows(// Good row- will be processed.
        // Null row- will be thrown away.
        // Bad metric- will count as processed, but that particular metric won't update.
        // Bad long dim- will count as processed, but bad dims will get default values
        // Bad row- will be unparseable.
        // Good row- will be processed.
        Arrays.asList(ImmutableMap.of("t", 1521251960729L, "dim1", "foo", "met1", "1"), null, ImmutableMap.of("t", 1521251960729L, "dim1", "foo", "met1", "foo"), ImmutableMap.of("t", 1521251960729L, "dim1", "foo", "dimLong", "notnumber", "dimFloat", "notnumber", "met1", "foo"), ImmutableMap.of("dim1", "foo", "met1", 2.0, AppenderatorDriverRealtimeIndexTaskTest.FAIL_DIM, "x"), ImmutableMap.of("t", 1521251960729L, "dim2", "bar", "met1", 2.0)));
        // Stop the firehose, this will drain out existing events.
        firehose.close();
        // Wait for the task to finish.
        final TaskStatus taskStatus = statusFuture.get();
        Assert.assertEquals(FAILED, taskStatus.getStatusCode());
        Assert.assertTrue(taskStatus.getErrorMsg().contains("Max parse exceptions exceeded, terminating task..."));
        IngestionStatsAndErrorsTaskReportData reportData = getTaskReportData();
        Map<String, Object> expectedMetrics = ImmutableMap.of(BUILD_SEGMENTS, ImmutableMap.of(PROCESSED, 1, PROCESSED_WITH_ERROR, 2, UNPARSEABLE, 2, THROWN_AWAY, 0));
        Assert.assertEquals(expectedMetrics, reportData.getRowStats());
        Map<String, Object> expectedUnparseables = ImmutableMap.of(BUILD_SEGMENTS, Arrays.asList("Unparseable timestamp found! Event: {dim1=foo, met1=2.0, __fail__=x}", "Found unparseable columns in row: [MapBasedInputRow{timestamp=2018-03-17T01:59:20.729Z, event={t=1521251960729, dim1=foo, dimLong=notnumber, dimFloat=notnumber, met1=foo}, dimensions=[dim1, dim2, dim1t, dimLong, dimFloat]}], exceptions: [could not convert value [notnumber] to long,could not convert value [notnumber] to float,Unable to parse value[foo] for field[met1],]", "Found unparseable columns in row: [MapBasedInputRow{timestamp=2018-03-17T01:59:20.729Z, event={t=1521251960729, dim1=foo, met1=foo}, dimensions=[dim1, dim2, dim1t, dimLong, dimFloat]}], exceptions: [Unable to parse value[foo] for field[met1],]", "Unparseable timestamp found! Event: null"));
        Assert.assertEquals(expectedUnparseables, reportData.getUnparseableEvents());
        Assert.assertEquals(IngestionState.BUILD_SEGMENTS, reportData.getIngestionState());
    }

    @Test(timeout = 60000L)
    public void testRestore() throws Exception {
        expectPublishedSegments(0);
        final AppenderatorDriverRealtimeIndexTask task1 = makeRealtimeTask(null);
        final DataSegment publishedSegment;
        // First run:
        {
            final ListenableFuture<TaskStatus> statusFuture = runTask(task1);
            // Wait for firehose to show up, it starts off null.
            while ((task1.getFirehose()) == null) {
                Thread.sleep(50);
            } 
            final AppenderatorDriverRealtimeIndexTaskTest.TestFirehose firehose = ((AppenderatorDriverRealtimeIndexTaskTest.TestFirehose) (task1.getFirehose()));
            firehose.addRows(ImmutableList.of(ImmutableMap.of("t", now.getMillis(), "dim1", "foo")));
            // Trigger graceful shutdown.
            task1.stopGracefully(taskToolboxFactory.build(task1).getConfig());
            // Wait for the task to finish. The status doesn't really matter, but we'll check it anyway.
            final TaskStatus taskStatus = statusFuture.get();
            Assert.assertEquals(SUCCESS, taskStatus.getStatusCode());
            // Nothing should be published.
            Assert.assertTrue(publishedSegments.isEmpty());
        }
        // Second run:
        {
            expectPublishedSegments(1);
            final AppenderatorDriverRealtimeIndexTask task2 = makeRealtimeTask(task1.getId());
            final ListenableFuture<TaskStatus> statusFuture = runTask(task2);
            // Wait for firehose to show up, it starts off null.
            while ((task2.getFirehose()) == null) {
                Thread.sleep(50);
            } 
            // Do a query, at this point the previous data should be loaded.
            Assert.assertEquals(1, sumMetric(task2, null, "rows").longValue());
            final AppenderatorDriverRealtimeIndexTaskTest.TestFirehose firehose = ((AppenderatorDriverRealtimeIndexTaskTest.TestFirehose) (task2.getFirehose()));
            firehose.addRows(ImmutableList.of(ImmutableMap.of("t", now.getMillis(), "dim2", "bar")));
            // Stop the firehose, this will drain out existing events.
            firehose.close();
            Collection<DataSegment> publishedSegments = awaitSegments();
            publishedSegment = Iterables.getOnlyElement(publishedSegments);
            // Do a query.
            Assert.assertEquals(2, sumMetric(task2, null, "rows").longValue());
            awaitHandoffs();
            // Simulate handoff.
            for (Map.Entry<SegmentDescriptor, Pair<Executor, Runnable>> entry : handOffCallbacks.entrySet()) {
                final Pair<Executor, Runnable> executorRunnablePair = entry.getValue();
                Assert.assertEquals(new SegmentDescriptor(publishedSegment.getInterval(), publishedSegment.getVersion(), publishedSegment.getShardSpec().getPartitionNum()), entry.getKey());
                executorRunnablePair.lhs.execute(executorRunnablePair.rhs);
            }
            handOffCallbacks.clear();
            // Wait for the task to finish.
            final TaskStatus taskStatus = statusFuture.get();
            Assert.assertEquals(SUCCESS, taskStatus.getStatusCode());
        }
    }

    @Test(timeout = 60000L)
    public void testRestoreAfterHandoffAttemptDuringShutdown() throws Exception {
        final AppenderatorDriverRealtimeIndexTask task1 = makeRealtimeTask(null);
        final DataSegment publishedSegment;
        // First run:
        {
            expectPublishedSegments(1);
            final ListenableFuture<TaskStatus> statusFuture = runTask(task1);
            // Wait for firehose to show up, it starts off null.
            while ((task1.getFirehose()) == null) {
                Thread.sleep(50);
            } 
            final AppenderatorDriverRealtimeIndexTaskTest.TestFirehose firehose = ((AppenderatorDriverRealtimeIndexTaskTest.TestFirehose) (task1.getFirehose()));
            firehose.addRows(ImmutableList.of(ImmutableMap.of("t", now.getMillis(), "dim1", "foo")));
            // Stop the firehose, this will trigger a finishJob.
            firehose.close();
            Collection<DataSegment> publishedSegments = awaitSegments();
            publishedSegment = Iterables.getOnlyElement(publishedSegments);
            // Do a query.
            Assert.assertEquals(1, sumMetric(task1, null, "rows").longValue());
            // Trigger graceful shutdown.
            task1.stopGracefully(taskToolboxFactory.build(task1).getConfig());
            // Wait for the task to finish. The status doesn't really matter.
            while (!(statusFuture.isDone())) {
                Thread.sleep(50);
            } 
        }
        // Second run:
        {
            expectPublishedSegments(1);
            final AppenderatorDriverRealtimeIndexTask task2 = makeRealtimeTask(task1.getId());
            final ListenableFuture<TaskStatus> statusFuture = runTask(task2);
            // Wait for firehose to show up, it starts off null.
            while ((task2.getFirehose()) == null) {
                Thread.sleep(50);
            } 
            // Stop the firehose again, this will start another handoff.
            final AppenderatorDriverRealtimeIndexTaskTest.TestFirehose firehose = ((AppenderatorDriverRealtimeIndexTaskTest.TestFirehose) (task2.getFirehose()));
            // Stop the firehose, this will trigger a finishJob.
            firehose.close();
            awaitHandoffs();
            // Simulate handoff.
            for (Map.Entry<SegmentDescriptor, Pair<Executor, Runnable>> entry : handOffCallbacks.entrySet()) {
                final Pair<Executor, Runnable> executorRunnablePair = entry.getValue();
                Assert.assertEquals(new SegmentDescriptor(publishedSegment.getInterval(), publishedSegment.getVersion(), publishedSegment.getShardSpec().getPartitionNum()), entry.getKey());
                executorRunnablePair.lhs.execute(executorRunnablePair.rhs);
            }
            handOffCallbacks.clear();
            // Wait for the task to finish.
            final TaskStatus taskStatus = statusFuture.get();
            Assert.assertEquals(SUCCESS, taskStatus.getStatusCode());
        }
    }

    @Test(timeout = 60000L)
    public void testRestoreCorruptData() throws Exception {
        final AppenderatorDriverRealtimeIndexTask task1 = makeRealtimeTask(null);
        // First run:
        {
            expectPublishedSegments(0);
            final ListenableFuture<TaskStatus> statusFuture = runTask(task1);
            // Wait for firehose to show up, it starts off null.
            while ((task1.getFirehose()) == null) {
                Thread.sleep(50);
            } 
            final AppenderatorDriverRealtimeIndexTaskTest.TestFirehose firehose = ((AppenderatorDriverRealtimeIndexTaskTest.TestFirehose) (task1.getFirehose()));
            firehose.addRows(ImmutableList.of(ImmutableMap.of("t", now.getMillis(), "dim1", "foo")));
            // Trigger graceful shutdown.
            task1.stopGracefully(taskToolboxFactory.build(task1).getConfig());
            // Wait for the task to finish. The status doesn't really matter, but we'll check it anyway.
            final TaskStatus taskStatus = statusFuture.get();
            Assert.assertEquals(SUCCESS, taskStatus.getStatusCode());
            // Nothing should be published.
            Assert.assertTrue(publishedSegments.isEmpty());
        }
        Optional<File> optional = FileUtils.listFiles(baseDir, null, true).stream().filter(( f) -> f.getName().equals("00000.smoosh")).findFirst();
        Assert.assertTrue("Could not find smoosh file", optional.isPresent());
        // Corrupt the data:
        final File smooshFile = optional.get();
        Files.write(smooshFile.toPath(), StringUtils.toUtf8("oops!"));
        // Second run:
        {
            expectPublishedSegments(0);
            final AppenderatorDriverRealtimeIndexTask task2 = makeRealtimeTask(task1.getId());
            final ListenableFuture<TaskStatus> statusFuture = runTask(task2);
            // Wait for the task to finish.
            TaskStatus status = statusFuture.get();
            Map<String, Object> expectedMetrics = ImmutableMap.of(BUILD_SEGMENTS, ImmutableMap.of(PROCESSED_WITH_ERROR, 0, PROCESSED, 0, UNPARSEABLE, 0, THROWN_AWAY, 0));
            IngestionStatsAndErrorsTaskReportData reportData = getTaskReportData();
            Assert.assertEquals(expectedMetrics, reportData.getRowStats());
            Assert.assertTrue(status.getErrorMsg().contains("java.lang.IllegalArgumentException\n\tat java.nio.Buffer.position"));
        }
    }

    @Test(timeout = 60000L)
    public void testStopBeforeStarting() throws Exception {
        expectPublishedSegments(0);
        final AppenderatorDriverRealtimeIndexTask task1 = makeRealtimeTask(null);
        task1.stopGracefully(taskToolboxFactory.build(task1).getConfig());
        final ListenableFuture<TaskStatus> statusFuture = runTask(task1);
        // Wait for the task to finish.
        final TaskStatus taskStatus = statusFuture.get();
        Assert.assertEquals(SUCCESS, taskStatus.getStatusCode());
    }
}

