/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.job.master;


import HeartbeatContext.JOB_MASTER_LOST_WORKER_DETECTION;
import HeartbeatContext.JOB_WORKER_COMMAND_HANDLING;
import PropertyKey.JOB_MASTER_WORKER_TIMEOUT_MS;
import alluxio.ConfigurationRule;
import alluxio.Constants;
import alluxio.conf.ServerConfiguration;
import alluxio.heartbeat.HeartbeatContext;
import alluxio.heartbeat.HeartbeatScheduler;
import alluxio.heartbeat.ManuallyScheduleHeartbeat;
import alluxio.master.LocalAlluxioJobCluster;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.testutils.LocalAlluxioClusterResource;
import alluxio.util.CommonUtils;
import alluxio.util.WaitForOptions;
import alluxio.worker.JobWorkerIdRegistry;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 * Tests that we properly handle worker heartbeat timeouts and reregistrations.
 */
public class LostWorkerIntegrationTest extends BaseIntegrationTest {
    private static final int WORKER_HEARTBEAT_TIMEOUT_MS = 10;

    @Rule
    public ManuallyScheduleHeartbeat mSchedule = new ManuallyScheduleHeartbeat(HeartbeatContext.JOB_MASTER_LOST_WORKER_DETECTION, HeartbeatContext.JOB_WORKER_COMMAND_HANDLING);

    @Rule
    public ConfigurationRule mConfigurationRule = new ConfigurationRule(ImmutableMap.of(JOB_MASTER_WORKER_TIMEOUT_MS, Integer.toString(LostWorkerIntegrationTest.WORKER_HEARTBEAT_TIMEOUT_MS)), ServerConfiguration.global());

    // We need this because LocalAlluxioJobCluster doesn't work without it.
    @Rule
    public LocalAlluxioClusterResource mLocalAlluxioClusterResource = new LocalAlluxioClusterResource.Builder().build();

    private LocalAlluxioJobCluster mLocalAlluxioJobCluster;

    @Test
    public void lostWorkerReregisters() throws Exception {
        final Long initialId = JobWorkerIdRegistry.getWorkerId();
        // Sleep so that the master thinks the worker has gone too long without a heartbeat.
        CommonUtils.sleepMs(((LostWorkerIntegrationTest.WORKER_HEARTBEAT_TIMEOUT_MS) + 1));
        HeartbeatScheduler.execute(JOB_MASTER_LOST_WORKER_DETECTION);
        Assert.assertTrue(mLocalAlluxioJobCluster.getMaster().getJobMaster().getWorkerInfoList().isEmpty());
        // Reregister the worker.
        HeartbeatScheduler.execute(JOB_WORKER_COMMAND_HANDLING);
        CommonUtils.waitFor("worker to reregister", () -> (!(mLocalAlluxioJobCluster.getMaster().getJobMaster().getWorkerInfoList().isEmpty())) && ((JobWorkerIdRegistry.getWorkerId()) != initialId), WaitForOptions.defaults().setTimeoutMs((10 * (Constants.SECOND_MS))));
    }
}

