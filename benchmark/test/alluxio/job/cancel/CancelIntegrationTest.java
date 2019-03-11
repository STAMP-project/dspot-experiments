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
package alluxio.job.cancel;


import JobDefinitionRegistry.INSTANCE;
import alluxio.Constants;
import alluxio.job.AbstractVoidJobDefinition;
import alluxio.job.JobConfig;
import alluxio.job.JobIntegrationTest;
import alluxio.job.JobMasterContext;
import alluxio.job.JobWorkerContext;
import alluxio.job.util.SerializableVoid;
import alluxio.wire.WorkerInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.powermock.reflect.Whitebox;


/**
 * Tests the cancellation of a job.
 */
public final class CancelIntegrationTest extends JobIntegrationTest {
    static class CancelTestConfig implements JobConfig {
        private static final long serialVersionUID = 1L;

        @Override
        public String getName() {
            return "Cancel";
        }
    }

    public static class CancelTestDefinition extends AbstractVoidJobDefinition<CancelIntegrationTest.CancelTestConfig, Integer> {
        @Override
        public Map<WorkerInfo, Integer> selectExecutors(CancelIntegrationTest.CancelTestConfig config, List<WorkerInfo> jobWorkerInfoList, JobMasterContext jobMasterContext) throws Exception {
            Map<WorkerInfo, Integer> result = new HashMap<>();
            for (WorkerInfo info : jobWorkerInfoList) {
                result.put(info, 0);
            }
            return result;
        }

        @Override
        public SerializableVoid runTask(CancelIntegrationTest.CancelTestConfig config, Integer args, JobWorkerContext jobWorkerContext) throws Exception {
            // wait until interruption
            Thread.sleep((1000 * (Constants.SECOND_MS)));
            return null;
        }

        @Override
        public Class<CancelIntegrationTest.CancelTestConfig> getJobConfigClass() {
            return CancelIntegrationTest.CancelTestConfig.class;
        }
    }

    @Test(timeout = 10000)
    public void cancelTest() throws Exception {
        // register the job
        Whitebox.invokeMethod(INSTANCE, "add", CancelIntegrationTest.CancelTestConfig.class, new CancelIntegrationTest.CancelTestDefinition());
        long jobId = mJobMaster.run(new CancelIntegrationTest.CancelTestConfig());
        waitForJobRunning(jobId);
        // cancel the job
        mJobMaster.cancel(jobId);
        waitForJobCancelled(jobId);
    }
}

