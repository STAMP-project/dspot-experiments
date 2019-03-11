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


import PropertyKey.JOB_MASTER_LOST_WORKER_INTERVAL_MS;
import PropertyKey.JOB_MASTER_WORKER_HEARTBEAT_INTERVAL_MS;
import PropertyKey.JOB_MASTER_WORKER_TIMEOUT_MS;
import PropertyKey.Name;
import Status.COMPLETED;
import alluxio.Constants;
import alluxio.exception.status.ResourceExhaustedException;
import alluxio.job.JobDefinitionRegistryRule;
import alluxio.job.SleepJobConfig;
import alluxio.job.SleepJobDefinition;
import alluxio.job.util.JobTestUtils;
import alluxio.master.LocalAlluxioJobCluster;
import alluxio.master.job.JobMaster;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.testutils.LocalAlluxioClusterResource;
import alluxio.util.CommonUtils;
import alluxio.util.WaitForOptions;
import alluxio.wire.WorkerInfo;
import alluxio.worker.JobWorkerProcess;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 * Integration tests for the job master.
 */
public final class JobMasterIntegrationTest extends BaseIntegrationTest {
    private static final long WORKER_TIMEOUT_MS = 500;

    private static final long LOST_WORKER_INTERVAL_MS = 500;

    private JobMaster mJobMaster;

    private JobWorkerProcess mJobWorker;

    private LocalAlluxioJobCluster mLocalAlluxioJobCluster;

    @Rule
    public LocalAlluxioClusterResource mLocalAlluxioClusterResource = new LocalAlluxioClusterResource.Builder().setProperty(JOB_MASTER_WORKER_HEARTBEAT_INTERVAL_MS, 20).setProperty(JOB_MASTER_WORKER_TIMEOUT_MS, JobMasterIntegrationTest.WORKER_TIMEOUT_MS).setProperty(JOB_MASTER_LOST_WORKER_INTERVAL_MS, JobMasterIntegrationTest.LOST_WORKER_INTERVAL_MS).build();

    @Rule
    public JobDefinitionRegistryRule mJobRule = new JobDefinitionRegistryRule(SleepJobConfig.class, new SleepJobDefinition());

    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.JOB_MASTER_JOB_CAPACITY, "1", Name.JOB_MASTER_FINISHED_JOB_RETENTION_MS, "0" })
    public void flowControl() throws Exception {
        for (int i = 0; i < 10; i++) {
            while (true) {
                try {
                    mJobMaster.run(new SleepJobConfig(100));
                    break;
                } catch (ResourceExhaustedException e) {
                    // sleep for a little before retrying the job
                    CommonUtils.sleepMs(100);
                }
            } 
        }
    }

    @Test
    public void restartMasterAndLoseWorker() throws Exception {
        long jobId = mJobMaster.run(new SleepJobConfig(1));
        JobTestUtils.waitForJobStatus(mJobMaster, jobId, COMPLETED);
        mJobMaster.stop();
        mJobMaster.start(true);
        CommonUtils.waitFor("Worker to register with restarted job master", () -> !(mJobMaster.getWorkerInfoList().isEmpty()), WaitForOptions.defaults().setTimeoutMs((10 * (Constants.SECOND_MS))));
        mJobWorker.stop();
        CommonUtils.sleepMs(((JobMasterIntegrationTest.WORKER_TIMEOUT_MS) + (JobMasterIntegrationTest.LOST_WORKER_INTERVAL_MS)));
        Assert.assertTrue(mJobMaster.getWorkerInfoList().isEmpty());
    }

    @Test
    @LocalAlluxioClusterResource.Config(confParams = { Name.JOB_MASTER_LOST_WORKER_INTERVAL_MS, "10000000" })
    public void restartMasterAndReregisterWorker() throws Exception {
        long jobId = mJobMaster.run(new SleepJobConfig(1));
        JobTestUtils.waitForJobStatus(mJobMaster, jobId, COMPLETED);
        mJobMaster.stop();
        mJobMaster.start(true);
        CommonUtils.waitFor("Worker to register with restarted job master", () -> !(mJobMaster.getWorkerInfoList().isEmpty()), WaitForOptions.defaults().setTimeoutMs((10 * (Constants.SECOND_MS))));
        final long firstWorkerId = mJobMaster.getWorkerInfoList().get(0).getId();
        mLocalAlluxioJobCluster.restartWorker();
        CommonUtils.waitFor("Restarted worker to register with job master", () -> {
            List<WorkerInfo> workerInfo = mJobMaster.getWorkerInfoList();
            return (!(workerInfo.isEmpty())) && ((workerInfo.get(0).getId()) != firstWorkerId);
        }, WaitForOptions.defaults().setTimeoutMs((10 * (Constants.SECOND_MS))));
        // The restarted worker should replace the original worker since they have the same address.
        Assert.assertEquals(1, mJobMaster.getWorkerInfoList().size());
    }
}

