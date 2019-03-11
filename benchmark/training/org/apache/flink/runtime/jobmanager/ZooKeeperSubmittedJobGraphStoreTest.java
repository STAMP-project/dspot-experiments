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
package org.apache.flink.runtime.jobmanager;


import org.apache.curator.framework.CuratorFramework;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.checkpoint.TestingRetrievableStateStorageHelper;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.util.ZooKeeperUtils;
import org.apache.flink.runtime.zookeeper.ZooKeeperResource;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 * Tests for the {@link ZooKeeperSubmittedJobGraphStore}.
 */
public class ZooKeeperSubmittedJobGraphStoreTest extends TestLogger {
    @Rule
    public ZooKeeperResource zooKeeperResource = new ZooKeeperResource();

    private Configuration configuration;

    /**
     * Tests that we fail with an exception if the job cannot be removed from the
     * ZooKeeperSubmittedJobGraphStore.
     *
     * <p>Tests that a close ZooKeeperSubmittedJobGraphStore no longer holds any locks.
     */
    @Test
    public void testJobGraphRemovalFailureAndLockRelease() throws Exception {
        try (final CuratorFramework client = ZooKeeperUtils.startCuratorFramework(configuration)) {
            final TestingRetrievableStateStorageHelper<SubmittedJobGraph> stateStorage = new TestingRetrievableStateStorageHelper();
            final ZooKeeperSubmittedJobGraphStore submittedJobGraphStore = createSubmittedJobGraphStore(client, stateStorage);
            submittedJobGraphStore.start(null);
            final ZooKeeperSubmittedJobGraphStore otherSubmittedJobGraphStore = createSubmittedJobGraphStore(client, stateStorage);
            otherSubmittedJobGraphStore.start(null);
            final SubmittedJobGraph jobGraph = new SubmittedJobGraph(new JobGraph());
            submittedJobGraphStore.putJobGraph(jobGraph);
            final SubmittedJobGraph recoveredJobGraph = otherSubmittedJobGraphStore.recoverJobGraph(jobGraph.getJobId());
            Assert.assertThat(recoveredJobGraph, Matchers.is(Matchers.notNullValue()));
            try {
                otherSubmittedJobGraphStore.removeJobGraph(recoveredJobGraph.getJobId());
                Assert.fail("It should not be possible to remove the JobGraph since the first store still has a lock on it.");
            } catch (Exception ignored) {
                // expected
            }
            submittedJobGraphStore.stop();
            // now we should be able to delete the job graph
            otherSubmittedJobGraphStore.removeJobGraph(recoveredJobGraph.getJobId());
            Assert.assertThat(otherSubmittedJobGraphStore.recoverJobGraph(recoveredJobGraph.getJobId()), Matchers.is(Matchers.nullValue()));
            otherSubmittedJobGraphStore.stop();
        }
    }
}

