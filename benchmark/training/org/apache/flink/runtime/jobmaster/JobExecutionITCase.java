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
package org.apache.flink.runtime.jobmaster;


import java.util.concurrent.CompletableFuture;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.minicluster.TestingMiniCluster;
import org.apache.flink.runtime.minicluster.TestingMiniClusterConfiguration;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Integration tests for job scheduling.
 */
public class JobExecutionITCase extends TestLogger {
    /**
     * Tests that tasks with a co-location constraint are scheduled in the same
     * slots. In fact it also tests that consumers are scheduled wrt their input
     * location if the co-location constraint is deactivated.
     */
    @Test
    public void testCoLocationConstraintJobExecution() throws Exception {
        final int numSlotsPerTaskExecutor = 1;
        final int numTaskExecutors = 3;
        final int parallelism = numTaskExecutors * numSlotsPerTaskExecutor;
        final JobGraph jobGraph = createJobGraph(parallelism);
        final TestingMiniClusterConfiguration miniClusterConfiguration = new TestingMiniClusterConfiguration.Builder().setNumSlotsPerTaskManager(numSlotsPerTaskExecutor).setNumTaskManagers(numTaskExecutors).setLocalCommunication(true).build();
        try (TestingMiniCluster miniCluster = new TestingMiniCluster(miniClusterConfiguration)) {
            start();
            miniCluster.submitJob(jobGraph).get();
            final CompletableFuture<JobResult> jobResultFuture = miniCluster.requestJobResult(jobGraph.getJobID());
            Assert.assertThat(jobResultFuture.get().isSuccess(), Matchers.is(true));
        }
    }
}

