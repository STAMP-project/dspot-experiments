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
package org.apache.flink.runtime.taskexecutor;


import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import org.apache.flink.runtime.execution.Environment;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobmaster.JobResult;
import org.apache.flink.runtime.jobmaster.TestingAbstractInvokables;
import org.apache.flink.runtime.minicluster.TestingMiniCluster;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Integration tests for the {@link TaskExecutor}.
 */
public class TaskExecutorITCase extends TestLogger {
    private static final Duration TESTING_TIMEOUT = Duration.ofMinutes(2L);

    private static final int NUM_TMS = 2;

    private static final int SLOTS_PER_TM = 2;

    private static final int PARALLELISM = (TaskExecutorITCase.NUM_TMS) * (TaskExecutorITCase.SLOTS_PER_TM);

    private TestingMiniCluster miniCluster;

    /**
     * Tests that a job can be re-executed after the job has failed due
     * to a TaskExecutor termination.
     */
    @Test
    public void testJobReExecutionAfterTaskExecutorTermination() throws Exception {
        final JobGraph jobGraph = createJobGraph(TaskExecutorITCase.PARALLELISM);
        final CompletableFuture<JobResult> jobResultFuture = submitJobAndWaitUntilRunning(jobGraph);
        // kill one TaskExecutor which should fail the job execution
        miniCluster.terminateTaskExecutor(0);
        final JobResult jobResult = jobResultFuture.get();
        Assert.assertThat(jobResult.isSuccess(), Matchers.is(false));
        miniCluster.startTaskExecutor();
        TaskExecutorITCase.BlockingOperator.unblock();
        miniCluster.submitJob(jobGraph).get();
        miniCluster.requestJobResult(jobGraph.getJobID()).get();
    }

    /**
     * Tests that the job can recover from a failing {@link TaskExecutor}.
     */
    @Test
    public void testJobRecoveryWithFailingTaskExecutor() throws Exception {
        final JobGraph jobGraph = createJobGraphWithRestartStrategy(TaskExecutorITCase.PARALLELISM);
        final CompletableFuture<JobResult> jobResultFuture = submitJobAndWaitUntilRunning(jobGraph);
        // start an additional TaskExecutor
        miniCluster.startTaskExecutor();
        miniCluster.terminateTaskExecutor(0).get();// this should fail the job

        TaskExecutorITCase.BlockingOperator.unblock();
        Assert.assertThat(jobResultFuture.get().isSuccess(), Matchers.is(true));
    }

    /**
     * Blocking invokable which is controlled by a static field.
     */
    public static class BlockingOperator extends TestingAbstractInvokables.Receiver {
        private static CountDownLatch countDownLatch = new CountDownLatch(1);

        public BlockingOperator(Environment environment) {
            super(environment);
        }

        @Override
        public void invoke() throws Exception {
            TaskExecutorITCase.BlockingOperator.countDownLatch.await();
            super.invoke();
        }

        public static void unblock() {
            TaskExecutorITCase.BlockingOperator.countDownLatch.countDown();
        }

        public static void reset() {
            TaskExecutorITCase.BlockingOperator.countDownLatch = new CountDownLatch(1);
        }
    }
}

