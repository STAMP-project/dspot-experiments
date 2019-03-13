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
package org.apache.flink.runtime.leaderelection;


import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.JobSubmissionResult;
import org.apache.flink.api.common.time.Deadline;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.runtime.execution.Environment;
import org.apache.flink.runtime.highavailability.nonha.embedded.TestingEmbeddedHaServices;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.tasks.AbstractInvokable;
import org.apache.flink.runtime.jobmaster.JobNotFinishedException;
import org.apache.flink.runtime.jobmaster.JobResult;
import org.apache.flink.runtime.minicluster.TestingMiniCluster;
import org.apache.flink.runtime.util.LeaderRetrievalUtils;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests which verify the cluster behaviour in case of leader changes.
 */
public class LeaderChangeClusterComponentsTest extends TestLogger {
    private static final Duration TESTING_TIMEOUT = Duration.ofMinutes(2L);

    private static final int SLOTS_PER_TM = 2;

    private static final int NUM_TMS = 2;

    public static final int PARALLELISM = (LeaderChangeClusterComponentsTest.SLOTS_PER_TM) * (LeaderChangeClusterComponentsTest.NUM_TMS);

    private static TestingMiniCluster miniCluster;

    private static TestingEmbeddedHaServices highAvailabilityServices;

    private JobGraph jobGraph;

    private JobID jobId;

    @Test
    public void testReelectionOfDispatcher() throws Exception {
        final CompletableFuture<JobSubmissionResult> submissionFuture = LeaderChangeClusterComponentsTest.miniCluster.submitJob(jobGraph);
        submissionFuture.get();
        CompletableFuture<JobResult> jobResultFuture = LeaderChangeClusterComponentsTest.miniCluster.requestJobResult(jobId);
        LeaderChangeClusterComponentsTest.highAvailabilityServices.revokeDispatcherLeadership().get();
        try {
            jobResultFuture.get();
            Assert.fail("Expected JobNotFinishedException");
        } catch (ExecutionException ee) {
            Assert.assertThat(ExceptionUtils.findThrowable(ee, JobNotFinishedException.class).isPresent(), Matchers.is(true));
        }
        LeaderChangeClusterComponentsTest.highAvailabilityServices.grantDispatcherLeadership();
        LeaderChangeClusterComponentsTest.BlockingOperator.isBlocking = false;
        final CompletableFuture<JobSubmissionResult> submissionFuture2 = LeaderChangeClusterComponentsTest.miniCluster.submitJob(jobGraph);
        submissionFuture2.get();
        final CompletableFuture<JobResult> jobResultFuture2 = LeaderChangeClusterComponentsTest.miniCluster.requestJobResult(jobId);
        JobResult jobResult = jobResultFuture2.get();
        Assert.assertThat(jobResult.isSuccess(), Matchers.is(true));
    }

    @Test
    public void testReelectionOfJobMaster() throws Exception {
        final CompletableFuture<JobSubmissionResult> submissionFuture = LeaderChangeClusterComponentsTest.miniCluster.submitJob(jobGraph);
        submissionFuture.get();
        CompletableFuture<JobResult> jobResultFuture = LeaderChangeClusterComponentsTest.miniCluster.requestJobResult(jobId);
        LeaderChangeClusterComponentsTest.highAvailabilityServices.revokeJobMasterLeadership(jobId).get();
        Assert.assertThat(jobResultFuture.isDone(), Matchers.is(false));
        LeaderChangeClusterComponentsTest.BlockingOperator.isBlocking = false;
        LeaderChangeClusterComponentsTest.highAvailabilityServices.grantJobMasterLeadership(jobId);
        JobResult jobResult = jobResultFuture.get();
        Assert.assertThat(jobResult.isSuccess(), Matchers.is(true));
    }

    @Test
    public void testTaskExecutorsReconnectToClusterWithLeadershipChange() throws Exception {
        final Deadline deadline = Deadline.fromNow(LeaderChangeClusterComponentsTest.TESTING_TIMEOUT);
        waitUntilTaskExecutorsHaveConnected(LeaderChangeClusterComponentsTest.NUM_TMS, deadline);
        LeaderChangeClusterComponentsTest.highAvailabilityServices.revokeResourceManagerLeadership().get();
        LeaderChangeClusterComponentsTest.highAvailabilityServices.grantResourceManagerLeadership();
        // wait for the ResourceManager to confirm the leadership
        Assert.assertThat(LeaderRetrievalUtils.retrieveLeaderConnectionInfo(getResourceManagerLeaderRetriever(), Time.minutes(LeaderChangeClusterComponentsTest.TESTING_TIMEOUT.toMinutes())).getLeaderSessionID(), Matchers.is(Matchers.notNullValue()));
        waitUntilTaskExecutorsHaveConnected(LeaderChangeClusterComponentsTest.NUM_TMS, deadline);
    }

    /**
     * Blocking invokable which is controlled by a static field.
     */
    public static class BlockingOperator extends AbstractInvokable {
        static boolean isBlocking = true;

        public BlockingOperator(Environment environment) {
            super(environment);
        }

        @Override
        public void invoke() throws Exception {
            if (LeaderChangeClusterComponentsTest.BlockingOperator.isBlocking) {
                synchronized(this) {
                    while (true) {
                        wait();
                    } 
                }
            }
        }
    }
}

