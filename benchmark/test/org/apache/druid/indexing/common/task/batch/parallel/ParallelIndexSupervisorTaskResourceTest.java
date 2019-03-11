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
import TaskState.SUCCESS;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.ws.rs.core.Response;
import org.apache.druid.client.indexing.IndexingServiceClient;
import org.apache.druid.data.input.FiniteFirehoseFactory;
import org.apache.druid.data.input.InputSplit;
import org.apache.druid.data.input.impl.StringInputRowParser;
import org.apache.druid.indexer.RunnerTaskState;
import org.apache.druid.indexer.TaskLocation;
import org.apache.druid.indexer.TaskState;
import org.apache.druid.indexer.TaskStatus;
import org.apache.druid.indexer.TaskStatusPlus;
import org.apache.druid.indexing.common.TaskLock;
import org.apache.druid.indexing.common.TaskToolbox;
import org.apache.druid.indexing.common.actions.LockListAction;
import org.apache.druid.indexing.common.actions.TaskActionClient;
import org.apache.druid.indexing.common.task.IndexTaskClientFactory;
import org.apache.druid.indexing.common.task.TaskResource;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.ISE;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.timeline.partition.NumberedShardSpec;
import org.junit.Assert;
import org.junit.Test;


public class ParallelIndexSupervisorTaskResourceTest extends AbstractParallelIndexSupervisorTaskTest {
    private static final int NUM_SUB_TASKS = 10;

    /**
     * specId -> spec
     */
    private final ConcurrentMap<String, ParallelIndexSubTaskSpec> subTaskSpecs = new ConcurrentHashMap<>();

    /**
     * specId -> taskStatusPlus
     */
    private final ConcurrentMap<String, TaskStatusPlus> runningSpecs = new ConcurrentHashMap<>();

    /**
     * specId -> taskStatusPlus list
     */
    private final ConcurrentHashMap<String, List<TaskStatusPlus>> taskHistories = new ConcurrentHashMap<>();

    /**
     * taskId -> subTaskSpec
     */
    private final ConcurrentMap<String, ParallelIndexSubTaskSpec> taskIdToSpec = new ConcurrentHashMap<>();

    /**
     * taskId -> task
     */
    private final CopyOnWriteArrayList<ParallelIndexSupervisorTaskResourceTest.TestSubTask> runningTasks = new CopyOnWriteArrayList<>();

    private ExecutorService service;

    private ParallelIndexSupervisorTaskResourceTest.TestSupervisorTask task;

    @Test(timeout = 20000L)
    public void testAPIs() throws Exception {
        task = newTask(Intervals.of("2017/2018"), new ParallelIndexIOConfig(new ParallelIndexSupervisorTaskResourceTest.TestFirehose(IntStream.range(0, ParallelIndexSupervisorTaskResourceTest.NUM_SUB_TASKS).boxed().collect(Collectors.toList())), false));
        actionClient = createActionClient(task);
        toolbox = createTaskToolbox(task);
        prepareTaskForLocking(task);
        Assert.assertTrue(task.isReady(actionClient));
        final Future<TaskStatus> supervisorTaskFuture = service.submit(() -> task.run(toolbox));
        Thread.sleep(1000);
        final SinglePhaseParallelIndexTaskRunner runner = ((SinglePhaseParallelIndexTaskRunner) (getRunner()));
        Assert.assertNotNull("runner is null", runner);
        // test getMode
        Response response = task.getMode(ParallelIndexSupervisorTaskResourceTest.newRequest());
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("parallel", response.getEntity());
        // test expectedNumSucceededTasks
        response = task.getProgress(ParallelIndexSupervisorTaskResourceTest.newRequest());
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(ParallelIndexSupervisorTaskResourceTest.NUM_SUB_TASKS, getExpectedSucceeded());
        // Since taskMonitor works based on polling, it's hard to use a fancier way to check its state.
        // We use polling to check the state of taskMonitor in this test.
        while ((getNumSubTasks(SinglePhaseParallelIndexingProgress::getRunning)) < (ParallelIndexSupervisorTaskResourceTest.NUM_SUB_TASKS)) {
            Thread.sleep(100);
        } 
        int succeededTasks = 0;
        int failedTasks = 0;
        checkState(succeededTasks, failedTasks, buildStateMap());
        // numRunningTasks and numSucceededTasks after some successful subTasks
        succeededTasks += 2;
        for (int i = 0; i < succeededTasks; i++) {
            runningTasks.get(0).setState(SUCCESS);
        }
        while ((getNumSubTasks(SinglePhaseParallelIndexingProgress::getSucceeded)) < succeededTasks) {
            Thread.sleep(100);
        } 
        checkState(succeededTasks, failedTasks, buildStateMap());
        // numRunningTasks and numSucceededTasks after some failed subTasks
        failedTasks += 3;
        for (int i = 0; i < failedTasks; i++) {
            runningTasks.get(0).setState(FAILED);
        }
        // Wait for new tasks to be started
        while (((getNumSubTasks(SinglePhaseParallelIndexingProgress::getFailed)) < failedTasks) || ((runningTasks.size()) < ((ParallelIndexSupervisorTaskResourceTest.NUM_SUB_TASKS) - succeededTasks))) {
            Thread.sleep(100);
        } 
        checkState(succeededTasks, failedTasks, buildStateMap());
        // Make sure only one subTask is running
        succeededTasks += 7;
        for (int i = 0; i < 7; i++) {
            runningTasks.get(0).setState(SUCCESS);
        }
        while ((getNumSubTasks(SinglePhaseParallelIndexingProgress::getSucceeded)) < succeededTasks) {
            Thread.sleep(100);
        } 
        checkState(succeededTasks, failedTasks, buildStateMap());
        Assert.assertEquals(1, runningSpecs.size());
        final String lastRunningSpecId = runningSpecs.keySet().iterator().next();
        final List<TaskStatusPlus> taskHistory = taskHistories.get(lastRunningSpecId);
        // This should be a failed task history because new tasks appear later in runningTasks.
        Assert.assertEquals(1, taskHistory.size());
        // Test one more failure
        runningTasks.get(0).setState(FAILED);
        failedTasks++;
        while ((getNumSubTasks(SinglePhaseParallelIndexingProgress::getFailed)) < failedTasks) {
            Thread.sleep(100);
        } 
        while ((getNumSubTasks(SinglePhaseParallelIndexingProgress::getRunning)) < 1) {
            Thread.sleep(100);
        } 
        checkState(succeededTasks, failedTasks, buildStateMap());
        Assert.assertEquals(2, taskHistory.size());
        runningTasks.get(0).setState(SUCCESS);
        succeededTasks++;
        while ((getNumSubTasks(SinglePhaseParallelIndexingProgress::getSucceeded)) < succeededTasks) {
            Thread.sleep(100);
        } 
        Assert.assertEquals(SUCCESS, supervisorTaskFuture.get(1000, TimeUnit.MILLISECONDS).getStatusCode());
    }

    private static class TestFirehose implements FiniteFirehoseFactory<StringInputRowParser, Integer> {
        private final List<Integer> ids;

        TestFirehose(List<Integer> ids) {
            this.ids = ids;
        }

        @Override
        public Stream<InputSplit<Integer>> getSplits() {
            return ids.stream().map(InputSplit::new);
        }

        @Override
        public int getNumSplits() {
            return ids.size();
        }

        @Override
        public FiniteFirehoseFactory<StringInputRowParser, Integer> withSplit(InputSplit<Integer> split) {
            return new ParallelIndexSupervisorTaskResourceTest.TestFirehose(Collections.singletonList(split.get()));
        }
    }

    private class TestSupervisorTask extends AbstractParallelIndexSupervisorTaskTest.TestParallelIndexSupervisorTask {
        TestSupervisorTask(String id, TaskResource taskResource, ParallelIndexIngestionSpec ingestionSchema, Map<String, Object> context, IndexingServiceClient indexingServiceClient) {
            super(id, taskResource, ingestionSchema, context, indexingServiceClient);
        }

        @Override
        ParallelIndexTaskRunner createRunner(TaskToolbox toolbox) {
            setRunner(new ParallelIndexSupervisorTaskResourceTest.TestRunner(toolbox, this, indexingServiceClient));
            return getRunner();
        }
    }

    private class TestRunner extends AbstractParallelIndexSupervisorTaskTest.TestParallelIndexTaskRunner {
        private final ParallelIndexSupervisorTask supervisorTask;

        TestRunner(TaskToolbox toolbox, ParallelIndexSupervisorTask supervisorTask, @Nullable
        IndexingServiceClient indexingServiceClient) {
            super(toolbox, supervisorTask.getId(), supervisorTask.getGroupId(), supervisorTask.getIngestionSchema(), supervisorTask.getContext(), indexingServiceClient);
            this.supervisorTask = supervisorTask;
        }

        @Override
        ParallelIndexSubTaskSpec newTaskSpec(InputSplit split) {
            final FiniteFirehoseFactory baseFirehoseFactory = ((FiniteFirehoseFactory) (getIngestionSchema().getIOConfig().getFirehoseFactory()));
            final ParallelIndexSupervisorTaskResourceTest.TestSubTaskSpec spec = new ParallelIndexSupervisorTaskResourceTest.TestSubTaskSpec((((supervisorTask.getId()) + "_") + (getAndIncrementNextSpecId())), supervisorTask.getGroupId(), supervisorTask, this, new ParallelIndexIngestionSpec(getIngestionSchema().getDataSchema(), new ParallelIndexIOConfig(baseFirehoseFactory.withSplit(split), getIngestionSchema().getIOConfig().isAppendToExisting()), getIngestionSchema().getTuningConfig()), supervisorTask.getContext(), split);
            subTaskSpecs.put(getId(), spec);
            return spec;
        }
    }

    private class TestSubTaskSpec extends ParallelIndexSubTaskSpec {
        private final ParallelIndexSupervisorTask supervisorTask;

        TestSubTaskSpec(String id, String groupId, ParallelIndexSupervisorTask supervisorTask, SinglePhaseParallelIndexTaskRunner runner, ParallelIndexIngestionSpec ingestionSpec, Map<String, Object> context, InputSplit inputSplit) {
            super(id, groupId, supervisorTask.getId(), ingestionSpec, context, inputSplit);
            this.supervisorTask = supervisorTask;
        }

        @Override
        public ParallelIndexSubTask newSubTask(int numAttempts) {
            try {
                // taskId is suffixed by the current time and this sleep is to make sure that every sub task has different id
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            final ParallelIndexSupervisorTaskResourceTest.TestSubTask subTask = new ParallelIndexSupervisorTaskResourceTest.TestSubTask(getGroupId(), getSupervisorTaskId(), numAttempts, getIngestionSpec(), getContext(), new AbstractParallelIndexSupervisorTaskTest.LocalParallelIndexTaskClientFactory(supervisorTask));
            final ParallelIndexSupervisorTaskResourceTest.TestFirehose firehose = ((ParallelIndexSupervisorTaskResourceTest.TestFirehose) (getIngestionSpec().getIOConfig().getFirehoseFactory()));
            final InputSplit<Integer> split = firehose.getSplits().findFirst().orElse(null);
            if (split == null) {
                throw new ISE("Split is null");
            }
            runningTasks.add(subTask);
            taskIdToSpec.put(getId(), this);
            runningSpecs.put(getId(), new TaskStatusPlus(getId(), getType(), DateTimes.EPOCH, DateTimes.EPOCH, TaskState.RUNNING, RunnerTaskState.RUNNING, (-1L), TaskLocation.unknown(), null, null));
            return subTask;
        }
    }

    private class TestSubTask extends ParallelIndexSubTask {
        private volatile TaskState state = TaskState.RUNNING;

        TestSubTask(String groupId, String supervisorTaskId, int numAttempts, ParallelIndexIngestionSpec ingestionSchema, Map<String, Object> context, IndexTaskClientFactory<ParallelIndexTaskClient> taskClientFactory) {
            super(null, groupId, null, supervisorTaskId, numAttempts, ingestionSchema, context, null, taskClientFactory);
        }

        @Override
        public boolean isReady(TaskActionClient taskActionClient) {
            return true;
        }

        @Override
        public TaskStatus run(final TaskToolbox toolbox) throws Exception {
            while ((state) == (TaskState.RUNNING)) {
                Thread.sleep(100);
            } 
            final ParallelIndexSupervisorTaskResourceTest.TestFirehose firehose = ((ParallelIndexSupervisorTaskResourceTest.TestFirehose) (getIngestionSchema().getIOConfig().getFirehoseFactory()));
            final List<TaskLock> locks = toolbox.getTaskActionClient().submit(new org.apache.druid.indexing.common.actions.SurrogateAction(getSupervisorTaskId(), new LockListAction()));
            Preconditions.checkState(((locks.size()) == 1), "There should be a single lock");
            getRunner().collectReport(new PushedSegmentsReport(getId(), Collections.singletonList(new org.apache.druid.timeline.DataSegment(getDataSource(), Intervals.of("2017/2018"), locks.get(0).getVersion(), null, null, null, new NumberedShardSpec(firehose.ids.get(0), ParallelIndexSupervisorTaskResourceTest.NUM_SUB_TASKS), 0, 1L))));
            return TaskStatus.fromCode(getId(), state);
        }

        void setState(TaskState state) {
            Preconditions.checkArgument(((state == (TaskState.SUCCESS)) || (state == (TaskState.FAILED))), "state[%s] should be SUCCESS of FAILED", state);
            this.state = state;
            final int taskIndex = IntStream.range(0, runningTasks.size()).filter(( i) -> getId().equals(getId())).findAny().orElse((-1));
            if (taskIndex == (-1)) {
                throw new ISE("Can't find an index for task[%s]", getId());
            }
            runningTasks.remove(taskIndex);
            final String specId = Preconditions.checkNotNull(taskIdToSpec.get(getId()), "spec for task[%s]", getId()).getId();
            runningSpecs.remove(specId);
            taskHistories.computeIfAbsent(specId, ( k) -> new ArrayList<>()).add(new TaskStatusPlus(getId(), getType(), DateTimes.EPOCH, DateTimes.EPOCH, state, RunnerTaskState.NONE, (-1L), TaskLocation.unknown(), null, null));
        }
    }
}

