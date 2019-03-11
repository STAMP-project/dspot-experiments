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
package org.apache.flink.yarn;


import java.time.Duration;
import java.util.Arrays;
import org.apache.curator.test.TestingServer;
import org.apache.flink.api.common.JobID;
import org.apache.flink.client.program.rest.RestClusterClient;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.util.OperatingSystem;
import org.apache.flink.yarn.testjob.YarnTestJob;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.junit.Assume;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests that verify correct HA behavior.
 */
public class YARNHighAvailabilityITCase extends YarnTestBase {
    @ClassRule
    public static final TemporaryFolder FOLDER = new TemporaryFolder();

    private static final String LOG_DIR = "flink-yarn-tests-ha";

    private static final Duration TIMEOUT = Duration.ofSeconds(200L);

    private static TestingServer zkServer;

    private static String storageDir;

    private YarnTestJob.StopJobSignal stopJobSignal;

    private JobGraph job;

    /**
     * Tests that Yarn will restart a killed {@link YarnSessionClusterEntrypoint} which will then resume
     * a persisted {@link JobGraph}.
     */
    @Test
    public void testKillYarnSessionClusterEntrypoint() throws Exception {
        Assume.assumeTrue("This test kills processes via the pkill command. Thus, it only runs on Linux, Mac OS, Free BSD and Solaris.", ((((OperatingSystem.isLinux()) || (OperatingSystem.isMac())) || (OperatingSystem.isFreeBSD())) || (OperatingSystem.isSolaris())));
        final YarnClusterDescriptor yarnClusterDescriptor = setupYarnClusterDescriptor();
        yarnClusterDescriptor.addShipFiles(Arrays.asList(YarnTestBase.flinkShadedHadoopDir.listFiles()));
        final RestClusterClient<ApplicationId> restClusterClient = deploySessionCluster(yarnClusterDescriptor);
        try {
            final JobID jobId = submitJob(restClusterClient);
            final ApplicationId id = restClusterClient.getClusterId();
            YARNHighAvailabilityITCase.waitUntilJobIsRunning(restClusterClient, jobId);
            killApplicationMaster(yarnClusterDescriptor.getYarnSessionClusterEntrypoint());
            waitForApplicationAttempt(id, 2);
            waitForJobTermination(restClusterClient, jobId);
            killApplicationAndWait(id);
        } finally {
            restClusterClient.shutdown();
        }
    }

    @Test
    public void testJobRecoversAfterKillingTaskManager() throws Exception {
        final YarnClusterDescriptor yarnClusterDescriptor = setupYarnClusterDescriptor();
        yarnClusterDescriptor.addShipFiles(Arrays.asList(YarnTestBase.flinkShadedHadoopDir.listFiles()));
        final RestClusterClient<ApplicationId> restClusterClient = deploySessionCluster(yarnClusterDescriptor);
        try {
            final JobID jobId = submitJob(restClusterClient);
            YARNHighAvailabilityITCase.waitUntilJobIsRunning(restClusterClient, jobId);
            stopTaskManagerContainer();
            YARNHighAvailabilityITCase.waitUntilJobIsRestarted(restClusterClient, jobId, 1);
            waitForJobTermination(restClusterClient, jobId);
            killApplicationAndWait(restClusterClient.getClusterId());
        } finally {
            restClusterClient.shutdown();
        }
    }
}

