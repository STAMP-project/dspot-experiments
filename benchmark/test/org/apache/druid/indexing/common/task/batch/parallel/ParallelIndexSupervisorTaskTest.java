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
package org.apache.druid.indexing.common.task.batch.parallel;


import TaskState.SUCCESS;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.druid.client.indexing.IndexingServiceClient;
import org.apache.druid.data.input.FiniteFirehoseFactory;
import org.apache.druid.data.input.InputSplit;
import org.apache.druid.indexing.common.TaskToolbox;
import org.apache.druid.indexing.common.actions.TaskActionClient;
import org.apache.druid.indexing.common.task.TaskResource;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.segment.realtime.firehose.LocalFirehoseFactory;
import org.apache.druid.timeline.DataSegment;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Test;


public class ParallelIndexSupervisorTaskTest extends AbstractParallelIndexSupervisorTaskTest {
    private File inputDir;

    @Test
    public void testIsReady() throws Exception {
        final ParallelIndexSupervisorTask task = newTask(Intervals.of("2017/2018"), new ParallelIndexIOConfig(new LocalFirehoseFactory(inputDir, "test_*", null), false));
        actionClient = createActionClient(task);
        toolbox = createTaskToolbox(task);
        prepareTaskForLocking(task);
        Assert.assertTrue(task.isReady(actionClient));
        final SinglePhaseParallelIndexTaskRunner runner = ((SinglePhaseParallelIndexTaskRunner) (task.createRunner(toolbox)));
        final Iterator<ParallelIndexSubTaskSpec> subTaskSpecIterator = runner.subTaskSpecIterator().iterator();
        while (subTaskSpecIterator.hasNext()) {
            final ParallelIndexSubTaskSpec spec = subTaskSpecIterator.next();
            final ParallelIndexSubTask subTask = new ParallelIndexSubTask(null, spec.getGroupId(), null, spec.getSupervisorTaskId(), 0, spec.getIngestionSpec(), spec.getContext(), indexingServiceClient, null);
            final TaskActionClient subTaskActionClient = createActionClient(subTask);
            prepareTaskForLocking(subTask);
            Assert.assertTrue(subTask.isReady(subTaskActionClient));
        } 
    }

    @Test
    public void testWithoutInterval() throws Exception {
        // Ingest all data.
        runTestWithoutIntervalTask();
        // Read the segments for one day.
        final Interval interval = Intervals.of("2017-12-24/P1D");
        final List<DataSegment> oldSegments = getStorageCoordinator().getUsedSegmentsForInterval("dataSource", interval);
        Assert.assertEquals(1, oldSegments.size());
        // Reingest the same data. Each segment should get replaced by a segment with a newer version.
        runTestWithoutIntervalTask();
        // Verify that the segment has been replaced.
        final List<DataSegment> newSegments = getStorageCoordinator().getUsedSegmentsForInterval("dataSource", interval);
        Assert.assertEquals(1, newSegments.size());
        Assert.assertTrue(((oldSegments.get(0).getVersion().compareTo(newSegments.get(0).getVersion())) < 0));
    }

    @Test
    public void testRunInParallel() throws Exception {
        final ParallelIndexSupervisorTask task = newTask(Intervals.of("2017/2018"), new ParallelIndexIOConfig(new LocalFirehoseFactory(inputDir, "test_*", null), false));
        actionClient = createActionClient(task);
        toolbox = createTaskToolbox(task);
        prepareTaskForLocking(task);
        Assert.assertTrue(task.isReady(actionClient));
        Assert.assertEquals(SUCCESS, task.run(toolbox).getStatusCode());
    }

    @Test
    public void testRunInSequential() throws Exception {
        final ParallelIndexSupervisorTask task = newTask(Intervals.of("2017/2018"), new ParallelIndexIOConfig(new LocalFirehoseFactory(inputDir, "test_*", null) {
            @Override
            public boolean isSplittable() {
                return false;
            }
        }, false));
        actionClient = createActionClient(task);
        toolbox = createTaskToolbox(task);
        prepareTaskForLocking(task);
        Assert.assertTrue(task.isReady(actionClient));
        Assert.assertEquals(SUCCESS, task.run(toolbox).getStatusCode());
    }

    @Test
    public void testPublishEmptySegments() throws Exception {
        final ParallelIndexSupervisorTask task = newTask(Intervals.of("2020/2021"), new ParallelIndexIOConfig(new LocalFirehoseFactory(inputDir, "test_*", null), false));
        actionClient = createActionClient(task);
        toolbox = createTaskToolbox(task);
        prepareTaskForLocking(task);
        Assert.assertTrue(task.isReady(actionClient));
        Assert.assertEquals(SUCCESS, task.run(toolbox).getStatusCode());
    }

    @Test
    public void testWith1MaxNumSubTasks() throws Exception {
        final ParallelIndexSupervisorTask task = newTask(Intervals.of("2017/2018"), new ParallelIndexIOConfig(new LocalFirehoseFactory(inputDir, "test_*", null), false), new ParallelIndexTuningConfig(null, null, null, null, null, null, null, null, null, null, null, null, null, 1, null, null, null, null, null, null, null));
        actionClient = createActionClient(task);
        toolbox = createTaskToolbox(task);
        prepareTaskForLocking(task);
        Assert.assertTrue(task.isReady(actionClient));
        Assert.assertEquals(SUCCESS, task.run(toolbox).getStatusCode());
        Assert.assertNull("Runner must be null if the task was in the sequential mode", task.getRunner());
    }

    private static class TestSupervisorTask extends AbstractParallelIndexSupervisorTaskTest.TestParallelIndexSupervisorTask {
        private final IndexingServiceClient indexingServiceClient;

        TestSupervisorTask(String id, TaskResource taskResource, ParallelIndexIngestionSpec ingestionSchema, Map<String, Object> context, IndexingServiceClient indexingServiceClient) {
            super(id, taskResource, ingestionSchema, context, indexingServiceClient);
            this.indexingServiceClient = indexingServiceClient;
        }

        @Override
        ParallelIndexTaskRunner createRunner(TaskToolbox toolbox) {
            setRunner(new ParallelIndexSupervisorTaskTest.TestRunner(toolbox, this, indexingServiceClient));
            return getRunner();
        }
    }

    private static class TestRunner extends AbstractParallelIndexSupervisorTaskTest.TestParallelIndexTaskRunner {
        private final ParallelIndexSupervisorTask supervisorTask;

        TestRunner(TaskToolbox toolbox, ParallelIndexSupervisorTask supervisorTask, @Nullable
        IndexingServiceClient indexingServiceClient) {
            super(toolbox, supervisorTask.getId(), supervisorTask.getGroupId(), supervisorTask.getIngestionSchema(), supervisorTask.getContext(), indexingServiceClient);
            this.supervisorTask = supervisorTask;
        }

        @Override
        ParallelIndexSubTaskSpec newTaskSpec(InputSplit split) {
            final FiniteFirehoseFactory baseFirehoseFactory = ((FiniteFirehoseFactory) (getIngestionSchema().getIOConfig().getFirehoseFactory()));
            return new ParallelIndexSupervisorTaskTest.TestParallelIndexSubTaskSpec((((supervisorTask.getId()) + "_") + (getAndIncrementNextSpecId())), supervisorTask.getGroupId(), supervisorTask, new ParallelIndexIngestionSpec(getIngestionSchema().getDataSchema(), new ParallelIndexIOConfig(baseFirehoseFactory.withSplit(split), getIngestionSchema().getIOConfig().isAppendToExisting()), getIngestionSchema().getTuningConfig()), supervisorTask.getContext(), split);
        }
    }

    private static class TestParallelIndexSubTaskSpec extends ParallelIndexSubTaskSpec {
        private final ParallelIndexSupervisorTask supervisorTask;

        TestParallelIndexSubTaskSpec(String id, String groupId, ParallelIndexSupervisorTask supervisorTask, ParallelIndexIngestionSpec ingestionSpec, Map<String, Object> context, InputSplit inputSplit) {
            super(id, groupId, supervisorTask.getId(), ingestionSpec, context, inputSplit);
            this.supervisorTask = supervisorTask;
        }

        @Override
        public ParallelIndexSubTask newSubTask(int numAttempts) {
            return new ParallelIndexSubTask(null, getGroupId(), null, getSupervisorTaskId(), numAttempts, getIngestionSpec(), getContext(), null, new AbstractParallelIndexSupervisorTaskTest.LocalParallelIndexTaskClientFactory(supervisorTask));
        }
    }
}

