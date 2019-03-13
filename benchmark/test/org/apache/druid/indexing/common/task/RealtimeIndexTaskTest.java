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


import Granularities.DAY;
import TaskState.SUCCESS;
import TransformSpec.NONE;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.druid.common.config.NullHandling;
import org.apache.druid.data.input.Firehose;
import org.apache.druid.data.input.FirehoseFactory;
import org.apache.druid.data.input.InputRow;
import org.apache.druid.data.input.impl.InputRowParser;
import org.apache.druid.indexer.TaskStatus;
import org.apache.druid.indexing.common.TaskToolbox;
import org.apache.druid.indexing.common.config.TaskStorageConfig;
import org.apache.druid.indexing.overlord.TaskStorage;
import org.apache.druid.indexing.test.TestIndexerMetadataStorageCoordinator;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.Pair;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.java.util.common.logger.Logger;
import org.apache.druid.java.util.common.parsers.ParseException;
import org.apache.druid.java.util.emitter.core.NoopEmitter;
import org.apache.druid.java.util.emitter.service.ServiceEmitter;
import org.apache.druid.math.expr.ExprMacroTable;
import org.apache.druid.query.SegmentDescriptor;
import org.apache.druid.query.filter.SelectorDimFilter;
import org.apache.druid.segment.transform.TransformSpec;
import org.apache.druid.timeline.DataSegment;
import org.apache.druid.utils.Runnables;
import org.hamcrest.CoreMatchers;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableMessageMatcher;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


public class RealtimeIndexTaskTest {
    private static final Logger log = new Logger(RealtimeIndexTaskTest.class);

    private static final ServiceEmitter emitter = new ServiceEmitter("service", "host", new NoopEmitter());

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
                if ((row != null) && ((row.getRaw(RealtimeIndexTaskTest.FAIL_DIM)) != null)) {
                    throw new ParseException(RealtimeIndexTaskTest.FAIL_DIM);
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
            return new RealtimeIndexTaskTest.TestFirehose(parser);
        }
    }

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    private DateTime now;

    private ListeningExecutorService taskExec;

    private Map<SegmentDescriptor, Pair<Executor, Runnable>> handOffCallbacks;

    @Test
    public void testMakeTaskId() {
        Assert.assertEquals("index_realtime_test_0_2015-01-02T00:00:00.000Z_abcdefgh", RealtimeIndexTask.makeTaskId("test", 0, DateTimes.of("2015-01-02"), "abcdefgh"));
    }

    @Test(timeout = 60000L)
    public void testDefaultResource() {
        final RealtimeIndexTask task = makeRealtimeTask(null);
        Assert.assertEquals(task.getId(), task.getTaskResource().getAvailabilityGroup());
    }

    @Test(timeout = 60000L, expected = ExecutionException.class)
    public void testHandoffTimeout() throws Exception {
        final TestIndexerMetadataStorageCoordinator mdc = new TestIndexerMetadataStorageCoordinator();
        final RealtimeIndexTask task = makeRealtimeTask(null, NONE, true, 100L);
        final TaskToolbox taskToolbox = makeToolbox(task, mdc, tempFolder.newFolder());
        final ListenableFuture<TaskStatus> statusFuture = runTask(task, taskToolbox);
        // Wait for firehose to show up, it starts off null.
        while ((task.getFirehose()) == null) {
            Thread.sleep(50);
        } 
        final RealtimeIndexTaskTest.TestFirehose firehose = ((RealtimeIndexTaskTest.TestFirehose) (task.getFirehose()));
        firehose.addRows(ImmutableList.of(ImmutableMap.of("t", now.getMillis(), "dim1", "foo", "met1", "1")));
        // Stop the firehose, this will drain out existing events.
        firehose.close();
        // Wait for publish.
        while (mdc.getPublished().isEmpty()) {
            Thread.sleep(50);
        } 
        Assert.assertEquals(1, task.getMetrics().processed());
        Assert.assertNotNull(Iterables.getOnlyElement(mdc.getPublished()));
        // handoff would timeout, resulting in exception
        statusFuture.get();
    }

    @Test(timeout = 60000L)
    public void testBasics() throws Exception {
        final TestIndexerMetadataStorageCoordinator mdc = new TestIndexerMetadataStorageCoordinator();
        final RealtimeIndexTask task = makeRealtimeTask(null);
        final TaskToolbox taskToolbox = makeToolbox(task, mdc, tempFolder.newFolder());
        final ListenableFuture<TaskStatus> statusFuture = runTask(task, taskToolbox);
        final DataSegment publishedSegment;
        // Wait for firehose to show up, it starts off null.
        while ((task.getFirehose()) == null) {
            Thread.sleep(50);
        } 
        final RealtimeIndexTaskTest.TestFirehose firehose = ((RealtimeIndexTaskTest.TestFirehose) (task.getFirehose()));
        firehose.addRows(ImmutableList.of(ImmutableMap.of("t", now.getMillis(), "dim1", "foo", "met1", "1"), ImmutableMap.of("t", now.minus(new Period("P1D")).getMillis(), "dim1", "foo", "met1", 2.0), ImmutableMap.of("t", now.getMillis(), "dim2", "bar", "met1", 2.0)));
        // Stop the firehose, this will drain out existing events.
        firehose.close();
        // Wait for publish.
        while (mdc.getPublished().isEmpty()) {
            Thread.sleep(50);
        } 
        publishedSegment = Iterables.getOnlyElement(mdc.getPublished());
        // Check metrics.
        Assert.assertEquals(2, task.getMetrics().processed());
        Assert.assertEquals(1, task.getMetrics().thrownAway());
        Assert.assertEquals(0, task.getMetrics().unparseable());
        // Do some queries.
        Assert.assertEquals(2, sumMetric(task, null, "rows").longValue());
        Assert.assertEquals(3, sumMetric(task, null, "met1").longValue());
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

    @Test(timeout = 60000L)
    public void testTransformSpec() throws Exception {
        final TestIndexerMetadataStorageCoordinator mdc = new TestIndexerMetadataStorageCoordinator();
        final TransformSpec transformSpec = new TransformSpec(new SelectorDimFilter("dim1", "foo", null), ImmutableList.of(new org.apache.druid.segment.transform.ExpressionTransform("dim1t", "concat(dim1,dim1)", ExprMacroTable.nil())));
        final RealtimeIndexTask task = makeRealtimeTask(null, transformSpec, true, 0);
        final TaskToolbox taskToolbox = makeToolbox(task, mdc, tempFolder.newFolder());
        final ListenableFuture<TaskStatus> statusFuture = runTask(task, taskToolbox);
        final DataSegment publishedSegment;
        // Wait for firehose to show up, it starts off null.
        while ((task.getFirehose()) == null) {
            Thread.sleep(50);
        } 
        final RealtimeIndexTaskTest.TestFirehose firehose = ((RealtimeIndexTaskTest.TestFirehose) (task.getFirehose()));
        firehose.addRows(ImmutableList.of(ImmutableMap.of("t", now.getMillis(), "dim1", "foo", "met1", "1"), ImmutableMap.of("t", now.minus(new Period("P1D")).getMillis(), "dim1", "foo", "met1", 2.0), ImmutableMap.of("t", now.getMillis(), "dim2", "bar", "met1", 2.0)));
        // Stop the firehose, this will drain out existing events.
        firehose.close();
        // Wait for publish.
        while (mdc.getPublished().isEmpty()) {
            Thread.sleep(50);
        } 
        publishedSegment = Iterables.getOnlyElement(mdc.getPublished());
        // Check metrics.
        Assert.assertEquals(1, task.getMetrics().processed());
        Assert.assertEquals(2, task.getMetrics().thrownAway());
        Assert.assertEquals(0, task.getMetrics().unparseable());
        // Do some queries.
        Assert.assertEquals(1, sumMetric(task, null, "rows").longValue());
        Assert.assertEquals(1, sumMetric(task, new SelectorDimFilter("dim1t", "foofoo", null), "rows").longValue());
        if (NullHandling.replaceWithDefault()) {
            Assert.assertEquals(0, sumMetric(task, new SelectorDimFilter("dim1t", "barbar", null), "rows").longValue());
        } else {
            Assert.assertNull(sumMetric(task, new SelectorDimFilter("dim1t", "barbar", null), "rows"));
        }
        Assert.assertEquals(1, sumMetric(task, null, "met1").longValue());
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

    @Test(timeout = 60000L)
    public void testReportParseExceptionsOnBadMetric() throws Exception {
        final TestIndexerMetadataStorageCoordinator mdc = new TestIndexerMetadataStorageCoordinator();
        final RealtimeIndexTask task = makeRealtimeTask(null, true);
        final TaskToolbox taskToolbox = makeToolbox(task, mdc, tempFolder.newFolder());
        final ListenableFuture<TaskStatus> statusFuture = runTask(task, taskToolbox);
        // Wait for firehose to show up, it starts off null.
        while ((task.getFirehose()) == null) {
            Thread.sleep(50);
        } 
        final RealtimeIndexTaskTest.TestFirehose firehose = ((RealtimeIndexTaskTest.TestFirehose) (task.getFirehose()));
        firehose.addRows(ImmutableList.of(ImmutableMap.of("t", now.getMillis(), "dim1", "foo", "met1", "1"), ImmutableMap.of("t", now.getMillis(), "dim1", "foo", "met1", "foo"), ImmutableMap.of("t", now.minus(new Period("P1D")).getMillis(), "dim1", "foo", "met1", "foo"), ImmutableMap.of("t", now.getMillis(), "dim2", "bar", "met1", 2.0)));
        // Stop the firehose, this will drain out existing events.
        firehose.close();
        // Wait for the task to finish.
        expectedException.expect(ExecutionException.class);
        expectedException.expectCause(CoreMatchers.instanceOf(ParseException.class));
        expectedException.expectCause(ThrowableMessageMatcher.hasMessage(CoreMatchers.containsString("[Unable to parse value[foo] for field[met1]")));
        statusFuture.get();
    }

    @Test(timeout = 60000L)
    public void testNoReportParseExceptions() throws Exception {
        final TestIndexerMetadataStorageCoordinator mdc = new TestIndexerMetadataStorageCoordinator();
        final RealtimeIndexTask task = makeRealtimeTask(null, false);
        final TaskToolbox taskToolbox = makeToolbox(task, mdc, tempFolder.newFolder());
        final ListenableFuture<TaskStatus> statusFuture = runTask(task, taskToolbox);
        final DataSegment publishedSegment;
        // Wait for firehose to show up, it starts off null.
        while ((task.getFirehose()) == null) {
            Thread.sleep(50);
        } 
        final RealtimeIndexTaskTest.TestFirehose firehose = ((RealtimeIndexTaskTest.TestFirehose) (task.getFirehose()));
        firehose.addRows(// Good row- will be processed.
        // Null row- will be thrown away.
        // Bad metric- will count as processed, but that particular metric won't update.
        // Bad row- will be unparseable.
        // Old row- will be thrownAway.
        // Good row- will be processed.
        Arrays.asList(ImmutableMap.of("t", now.getMillis(), "dim1", "foo", "met1", "1"), null, ImmutableMap.of("t", now.getMillis(), "dim1", "foo", "met1", "foo"), ImmutableMap.of("dim1", "foo", "met1", 2.0, RealtimeIndexTaskTest.FAIL_DIM, "x"), ImmutableMap.of("t", now.minus(Period.days(1)).getMillis(), "dim1", "foo", "met1", 2.0), ImmutableMap.of("t", now.getMillis(), "dim2", "bar", "met1", 2.0)));
        // Stop the firehose, this will drain out existing events.
        firehose.close();
        // Wait for publish.
        while (mdc.getPublished().isEmpty()) {
            Thread.sleep(50);
        } 
        publishedSegment = Iterables.getOnlyElement(mdc.getPublished());
        // Check metrics.
        Assert.assertEquals(3, task.getMetrics().processed());
        Assert.assertEquals(1, task.getMetrics().thrownAway());
        Assert.assertEquals(2, task.getMetrics().unparseable());
        // Do some queries.
        Assert.assertEquals(3, sumMetric(task, null, "rows").longValue());
        Assert.assertEquals(3, sumMetric(task, null, "met1").longValue());
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

    @Test(timeout = 60000L)
    public void testRestore() throws Exception {
        final File directory = tempFolder.newFolder();
        final RealtimeIndexTask task1 = makeRealtimeTask(null);
        final DataSegment publishedSegment;
        // First run:
        {
            final TestIndexerMetadataStorageCoordinator mdc = new TestIndexerMetadataStorageCoordinator();
            final TaskToolbox taskToolbox = makeToolbox(task1, mdc, directory);
            final ListenableFuture<TaskStatus> statusFuture = runTask(task1, taskToolbox);
            // Wait for firehose to show up, it starts off null.
            while ((task1.getFirehose()) == null) {
                Thread.sleep(50);
            } 
            final RealtimeIndexTaskTest.TestFirehose firehose = ((RealtimeIndexTaskTest.TestFirehose) (task1.getFirehose()));
            firehose.addRows(ImmutableList.of(ImmutableMap.of("t", now.getMillis(), "dim1", "foo")));
            // Trigger graceful shutdown.
            task1.stopGracefully(taskToolbox.getConfig());
            // Wait for the task to finish. The status doesn't really matter, but we'll check it anyway.
            final TaskStatus taskStatus = statusFuture.get();
            Assert.assertEquals(SUCCESS, taskStatus.getStatusCode());
            // Nothing should be published.
            Assert.assertEquals(new HashSet(), mdc.getPublished());
        }
        // Second run:
        {
            final TestIndexerMetadataStorageCoordinator mdc = new TestIndexerMetadataStorageCoordinator();
            final RealtimeIndexTask task2 = makeRealtimeTask(task1.getId());
            final TaskToolbox taskToolbox = makeToolbox(task2, mdc, directory);
            final ListenableFuture<TaskStatus> statusFuture = runTask(task2, taskToolbox);
            // Wait for firehose to show up, it starts off null.
            while ((task2.getFirehose()) == null) {
                Thread.sleep(50);
            } 
            // Do a query, at this point the previous data should be loaded.
            Assert.assertEquals(1, sumMetric(task2, null, "rows").longValue());
            final RealtimeIndexTaskTest.TestFirehose firehose = ((RealtimeIndexTaskTest.TestFirehose) (task2.getFirehose()));
            firehose.addRows(ImmutableList.of(ImmutableMap.of("t", now.getMillis(), "dim2", "bar")));
            // Stop the firehose, this will drain out existing events.
            firehose.close();
            // Wait for publish.
            while (mdc.getPublished().isEmpty()) {
                Thread.sleep(50);
            } 
            publishedSegment = Iterables.getOnlyElement(mdc.getPublished());
            // Do a query.
            Assert.assertEquals(2, sumMetric(task2, null, "rows").longValue());
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
        final TaskStorage taskStorage = new org.apache.druid.indexing.overlord.HeapMemoryTaskStorage(new TaskStorageConfig(null));
        final TestIndexerMetadataStorageCoordinator mdc = new TestIndexerMetadataStorageCoordinator();
        final File directory = tempFolder.newFolder();
        final RealtimeIndexTask task1 = makeRealtimeTask(null);
        final DataSegment publishedSegment;
        // First run:
        {
            final TaskToolbox taskToolbox = makeToolbox(task1, taskStorage, mdc, directory);
            final ListenableFuture<TaskStatus> statusFuture = runTask(task1, taskToolbox);
            // Wait for firehose to show up, it starts off null.
            while ((task1.getFirehose()) == null) {
                Thread.sleep(50);
            } 
            final RealtimeIndexTaskTest.TestFirehose firehose = ((RealtimeIndexTaskTest.TestFirehose) (task1.getFirehose()));
            firehose.addRows(ImmutableList.of(ImmutableMap.of("t", now.getMillis(), "dim1", "foo")));
            // Stop the firehose, this will trigger a finishJob.
            firehose.close();
            // Wait for publish.
            while (mdc.getPublished().isEmpty()) {
                Thread.sleep(50);
            } 
            publishedSegment = Iterables.getOnlyElement(mdc.getPublished());
            // Do a query.
            Assert.assertEquals(1, sumMetric(task1, null, "rows").longValue());
            // Trigger graceful shutdown.
            task1.stopGracefully(taskToolbox.getConfig());
            // Wait for the task to finish. The status doesn't really matter.
            while (!(statusFuture.isDone())) {
                Thread.sleep(50);
            } 
        }
        // Second run:
        {
            final RealtimeIndexTask task2 = makeRealtimeTask(task1.getId());
            final TaskToolbox taskToolbox = makeToolbox(task2, taskStorage, mdc, directory);
            final ListenableFuture<TaskStatus> statusFuture = runTask(task2, taskToolbox);
            // Wait for firehose to show up, it starts off null.
            while ((task2.getFirehose()) == null) {
                Thread.sleep(50);
            } 
            // Stop the firehose again, this will start another handoff.
            final RealtimeIndexTaskTest.TestFirehose firehose = ((RealtimeIndexTaskTest.TestFirehose) (task2.getFirehose()));
            // Stop the firehose, this will trigger a finishJob.
            firehose.close();
            // publishedSegment is still published. No reason it shouldn't be.
            Assert.assertEquals(ImmutableSet.of(publishedSegment), mdc.getPublished());
            // Wait for a handoffCallback to show up.
            while (handOffCallbacks.isEmpty()) {
                Thread.sleep(50);
            } 
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
        final File directory = tempFolder.newFolder();
        final RealtimeIndexTask task1 = makeRealtimeTask(null);
        // First run:
        {
            final TestIndexerMetadataStorageCoordinator mdc = new TestIndexerMetadataStorageCoordinator();
            final TaskToolbox taskToolbox = makeToolbox(task1, mdc, directory);
            final ListenableFuture<TaskStatus> statusFuture = runTask(task1, taskToolbox);
            // Wait for firehose to show up, it starts off null.
            while ((task1.getFirehose()) == null) {
                Thread.sleep(50);
            } 
            final RealtimeIndexTaskTest.TestFirehose firehose = ((RealtimeIndexTaskTest.TestFirehose) (task1.getFirehose()));
            firehose.addRows(ImmutableList.of(ImmutableMap.of("t", now.getMillis(), "dim1", "foo")));
            // Trigger graceful shutdown.
            task1.stopGracefully(taskToolbox.getConfig());
            // Wait for the task to finish. The status doesn't really matter, but we'll check it anyway.
            final TaskStatus taskStatus = statusFuture.get();
            Assert.assertEquals(SUCCESS, taskStatus.getStatusCode());
            // Nothing should be published.
            Assert.assertEquals(new HashSet(), mdc.getPublished());
        }
        // Corrupt the data:
        final File smooshFile = new File(StringUtils.format("%s/persistent/task/%s/work/persist/%s/%s_%s/0/00000.smoosh", directory, task1.getId(), task1.getDataSource(), DAY.bucketStart(now), DAY.bucketEnd(now)));
        Files.write(smooshFile.toPath(), StringUtils.toUtf8("oops!"));
        // Second run:
        {
            final TestIndexerMetadataStorageCoordinator mdc = new TestIndexerMetadataStorageCoordinator();
            final RealtimeIndexTask task2 = makeRealtimeTask(task1.getId());
            final TaskToolbox taskToolbox = makeToolbox(task2, mdc, directory);
            final ListenableFuture<TaskStatus> statusFuture = runTask(task2, taskToolbox);
            // Wait for the task to finish.
            boolean caught = false;
            try {
                statusFuture.get();
            } catch (Exception e) {
                caught = true;
            }
            Assert.assertTrue("expected exception", caught);
        }
    }

    @Test(timeout = 60000L)
    public void testStopBeforeStarting() throws Exception {
        final File directory = tempFolder.newFolder();
        final RealtimeIndexTask task1 = makeRealtimeTask(null);
        final TestIndexerMetadataStorageCoordinator mdc = new TestIndexerMetadataStorageCoordinator();
        final TaskToolbox taskToolbox = makeToolbox(task1, mdc, directory);
        task1.stopGracefully(taskToolbox.getConfig());
        final ListenableFuture<TaskStatus> statusFuture = runTask(task1, taskToolbox);
        // Wait for the task to finish.
        final TaskStatus taskStatus = statusFuture.get();
        Assert.assertEquals(SUCCESS, taskStatus.getStatusCode());
    }
}

