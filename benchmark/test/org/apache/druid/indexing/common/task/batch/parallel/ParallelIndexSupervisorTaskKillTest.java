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


import TaskState.FAILED;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.druid.client.indexing.IndexingServiceClient;
import org.apache.druid.data.input.FiniteFirehoseFactory;
import org.apache.druid.data.input.InputSplit;
import org.apache.druid.data.input.impl.StringInputRowParser;
import org.apache.druid.indexer.TaskState;
import org.apache.druid.indexer.TaskStatus;
import org.apache.druid.indexer.TaskStatusPlus;
import org.apache.druid.indexing.common.TaskToolbox;
import org.apache.druid.indexing.common.actions.TaskActionClient;
import org.apache.druid.indexing.common.task.IndexTaskClientFactory;
import org.apache.druid.indexing.common.task.TaskResource;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.java.util.common.Pair;
import org.junit.Assert;
import org.junit.Test;


public class ParallelIndexSupervisorTaskKillTest extends AbstractParallelIndexSupervisorTaskTest {
    private ExecutorService service;

    @Test(timeout = 5000L)
    public void testStopGracefully() throws Exception {
        final ParallelIndexSupervisorTask task = newTask(Intervals.of("2017/2018"), // Sub tasks would run forever
        new ParallelIndexIOConfig(new ParallelIndexSupervisorTaskKillTest.TestFirehoseFactory(Pair.of(new ParallelIndexSupervisorTaskKillTest.TestInput(Integer.MAX_VALUE, TaskState.SUCCESS), 4)), false));
        actionClient = createActionClient(task);
        toolbox = createTaskToolbox(task);
        prepareTaskForLocking(task);
        Assert.assertTrue(task.isReady(actionClient));
        final Future<TaskState> future = service.submit(() -> task.run(toolbox).getStatusCode());
        while ((task.getRunner()) == null) {
            Thread.sleep(100);
        } 
        task.stopGracefully(null);
        Assert.assertEquals(FAILED, future.get());
        final AbstractParallelIndexSupervisorTaskTest.TestParallelIndexTaskRunner runner = ((AbstractParallelIndexSupervisorTaskTest.TestParallelIndexTaskRunner) (task.getRunner()));
        Assert.assertTrue(getRunningTaskIds().isEmpty());
        // completeSubTaskSpecs should be empty because no task has reported its status to TaskMonitor
        Assert.assertTrue(getCompleteSubTaskSpecs().isEmpty());
        Assert.assertEquals(4, getTaskMonitor().getNumKilledTasks());
    }

    @Test(timeout = 5000L)
    public void testSubTaskFail() throws Exception {
        final ParallelIndexSupervisorTask task = newTask(Intervals.of("2017/2018"), new ParallelIndexIOConfig(new ParallelIndexSupervisorTaskKillTest.TestFirehoseFactory(Pair.of(new ParallelIndexSupervisorTaskKillTest.TestInput(10L, TaskState.FAILED), 1), Pair.of(new ParallelIndexSupervisorTaskKillTest.TestInput(Integer.MAX_VALUE, TaskState.FAILED), 3)), false));
        actionClient = createActionClient(task);
        toolbox = createTaskToolbox(task);
        prepareTaskForLocking(task);
        Assert.assertTrue(task.isReady(actionClient));
        final TaskState state = task.run(toolbox).getStatusCode();
        Assert.assertEquals(FAILED, state);
        final AbstractParallelIndexSupervisorTaskTest.TestParallelIndexTaskRunner runner = ((AbstractParallelIndexSupervisorTaskTest.TestParallelIndexTaskRunner) (task.getRunner()));
        Assert.assertTrue(getRunningTaskIds().isEmpty());
        final List<SubTaskSpec<ParallelIndexSubTask>> completeSubTaskSpecs = runner.getCompleteSubTaskSpecs();
        Assert.assertEquals(1, completeSubTaskSpecs.size());
        final TaskHistory<ParallelIndexSubTask> history = runner.getCompleteSubTaskSpecAttemptHistory(getId());
        Assert.assertNotNull(history);
        Assert.assertEquals(3, history.getAttemptHistory().size());
        for (TaskStatusPlus status : history.getAttemptHistory()) {
            Assert.assertEquals(FAILED, status.getStatusCode());
        }
        Assert.assertEquals(3, getTaskMonitor().getNumKilledTasks());
    }

    private static class TestInput {
        private final long runTime;

        private final TaskState finalState;

        private TestInput(long runTime, TaskState finalState) {
            this.runTime = runTime;
            this.finalState = finalState;
        }
    }

    private static class TestFirehoseFactory implements FiniteFirehoseFactory<StringInputRowParser, ParallelIndexSupervisorTaskKillTest.TestInput> {
        private final List<InputSplit<ParallelIndexSupervisorTaskKillTest.TestInput>> splits;

        @SafeVarargs
        private TestFirehoseFactory(Pair<ParallelIndexSupervisorTaskKillTest.TestInput, Integer>... inputSpecs) {
            splits = new ArrayList();
            for (Pair<ParallelIndexSupervisorTaskKillTest.TestInput, Integer> inputSpec : inputSpecs) {
                final int numInputs = inputSpec.rhs;
                for (int i = 0; i < numInputs; i++) {
                    splits.add(new InputSplit(new ParallelIndexSupervisorTaskKillTest.TestInput(inputSpec.lhs.runTime, inputSpec.lhs.finalState)));
                }
            }
        }

        private TestFirehoseFactory(InputSplit<ParallelIndexSupervisorTaskKillTest.TestInput> split) {
            this.splits = Collections.singletonList(split);
        }

        @Override
        public Stream<InputSplit<ParallelIndexSupervisorTaskKillTest.TestInput>> getSplits() {
            return splits.stream();
        }

        @Override
        public int getNumSplits() {
            return splits.size();
        }

        @Override
        public FiniteFirehoseFactory<StringInputRowParser, ParallelIndexSupervisorTaskKillTest.TestInput> withSplit(InputSplit<ParallelIndexSupervisorTaskKillTest.TestInput> split) {
            return new ParallelIndexSupervisorTaskKillTest.TestFirehoseFactory(split);
        }
    }

    private static class TestSupervisorTask extends AbstractParallelIndexSupervisorTaskTest.TestParallelIndexSupervisorTask {
        private final IndexingServiceClient indexingServiceClient;

        private TestSupervisorTask(ParallelIndexIngestionSpec ingestionSchema, Map<String, Object> context, IndexingServiceClient indexingServiceClient) {
            super(null, null, ingestionSchema, context, indexingServiceClient);
            this.indexingServiceClient = indexingServiceClient;
        }

        @Override
        ParallelIndexTaskRunner createRunner(TaskToolbox toolbox) {
            setRunner(new ParallelIndexSupervisorTaskKillTest.TestRunner(toolbox, this, indexingServiceClient));
            return getRunner();
        }
    }

    private static class TestRunner extends AbstractParallelIndexSupervisorTaskTest.TestParallelIndexTaskRunner {
        private final ParallelIndexSupervisorTask supervisorTask;

        private TestRunner(TaskToolbox toolbox, ParallelIndexSupervisorTask supervisorTask, IndexingServiceClient indexingServiceClient) {
            super(toolbox, supervisorTask.getId(), supervisorTask.getGroupId(), supervisorTask.getIngestionSchema(), supervisorTask.getContext(), indexingServiceClient);
            this.supervisorTask = supervisorTask;
        }

        @Override
        ParallelIndexSubTaskSpec newTaskSpec(InputSplit split) {
            final FiniteFirehoseFactory baseFirehoseFactory = ((FiniteFirehoseFactory) (getIngestionSchema().getIOConfig().getFirehoseFactory()));
            return new ParallelIndexSupervisorTaskKillTest.TestParallelIndexSubTaskSpec((((supervisorTask.getId()) + "_") + (getAndIncrementNextSpecId())), supervisorTask.getGroupId(), supervisorTask, new ParallelIndexIngestionSpec(getIngestionSchema().getDataSchema(), new ParallelIndexIOConfig(baseFirehoseFactory.withSplit(split), getIngestionSchema().getIOConfig().isAppendToExisting()), getIngestionSchema().getTuningConfig()), supervisorTask.getContext(), split);
        }
    }

    private static class TestParallelIndexSubTaskSpec extends ParallelIndexSubTaskSpec {
        private final ParallelIndexSupervisorTask supervisorTask;

        private TestParallelIndexSubTaskSpec(String id, String groupId, ParallelIndexSupervisorTask supervisorTask, ParallelIndexIngestionSpec ingestionSpec, Map<String, Object> context, InputSplit inputSplit) {
            super(id, groupId, supervisorTask.getId(), ingestionSpec, context, inputSplit);
            this.supervisorTask = supervisorTask;
        }

        @Override
        public ParallelIndexSubTask newSubTask(int numAttempts) {
            return new ParallelIndexSupervisorTaskKillTest.TestParallelIndexSubTask(null, getGroupId(), null, getSupervisorTaskId(), numAttempts, getIngestionSpec(), getContext(), null, new AbstractParallelIndexSupervisorTaskTest.LocalParallelIndexTaskClientFactory(supervisorTask));
        }
    }

    private static class TestParallelIndexSubTask extends ParallelIndexSubTask {
        private TestParallelIndexSubTask(@Nullable
        String id, String groupId, TaskResource taskResource, String supervisorTaskId, int numAttempts, ParallelIndexIngestionSpec ingestionSchema, Map<String, Object> context, IndexingServiceClient indexingServiceClient, IndexTaskClientFactory<ParallelIndexTaskClient> taskClientFactory) {
            super(id, groupId, taskResource, supervisorTaskId, numAttempts, ingestionSchema, context, indexingServiceClient, taskClientFactory);
        }

        @Override
        public boolean isReady(TaskActionClient taskActionClient) {
            return true;
        }

        @Override
        public TaskStatus run(final TaskToolbox toolbox) throws Exception {
            final ParallelIndexSupervisorTaskKillTest.TestFirehoseFactory firehoseFactory = ((ParallelIndexSupervisorTaskKillTest.TestFirehoseFactory) (getIngestionSchema().getIOConfig().getFirehoseFactory()));
            final ParallelIndexSupervisorTaskKillTest.TestInput testInput = Iterables.getOnlyElement(firehoseFactory.splits).get();
            Thread.sleep(testInput.runTime);
            return TaskStatus.fromCode(getId(), testInput.finalState);
        }
    }
}

