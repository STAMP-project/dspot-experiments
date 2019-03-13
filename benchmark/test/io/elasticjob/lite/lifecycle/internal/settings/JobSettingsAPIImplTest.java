/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */
package io.elasticjob.lite.lifecycle.internal.settings;


import JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER;
import JobPropertiesEnum.JOB_EXCEPTION_HANDLER;
import io.elasticjob.lite.executor.handler.impl.DefaultExecutorServiceHandler;
import io.elasticjob.lite.executor.handler.impl.DefaultJobExceptionHandler;
import io.elasticjob.lite.lifecycle.api.JobSettingsAPI;
import io.elasticjob.lite.lifecycle.domain.JobSettings;
import io.elasticjob.lite.lifecycle.fixture.LifecycleJsonConstants;
import io.elasticjob.lite.reg.base.CoordinatorRegistryCenter;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class JobSettingsAPIImplTest {
    private JobSettingsAPI jobSettingsAPI;

    @Mock
    private CoordinatorRegistryCenter regCenter;

    @Test
    public void assertGetDataflowJobSettings() {
        Mockito.when(regCenter.get("/test_job/config")).thenReturn(LifecycleJsonConstants.getDataflowJobJson());
        JobSettings actual = jobSettingsAPI.getJobSettings("test_job");
        assertJobSettings(actual, "DATAFLOW", "io.elasticjob.lite.fixture.TestDataflowJob");
        Mockito.verify(regCenter).get("/test_job/config");
    }

    @Test
    public void assertGetScriptJobSettings() {
        Mockito.when(regCenter.get("/test_job/config")).thenReturn(LifecycleJsonConstants.getScriptJobJson());
        JobSettings actual = jobSettingsAPI.getJobSettings("test_job");
        assertJobSettings(actual, "SCRIPT", "io.elasticjob.lite.api.script.ScriptJob");
        Mockito.verify(regCenter).get("/test_job/config");
    }

    @Test
    public void assertUpdateJobSettings() {
        Mockito.when(regCenter.get("/test_job/config")).thenReturn(LifecycleJsonConstants.getDataflowJobJson());
        JobSettings jobSettings = new JobSettings();
        jobSettings.setJobName("test_job");
        jobSettings.setJobClass("io.elasticjob.lite.fixture.TestDataflowJob");
        jobSettings.setShardingTotalCount(10);
        jobSettings.setMaxTimeDiffSeconds((-1));
        jobSettings.setMonitorExecution(true);
        jobSettings.setCron("0/1 * * * * ?");
        jobSettings.setStreamingProcess(true);
        jobSettings.setFailover(false);
        jobSettings.setMisfire(true);
        jobSettings.getJobProperties().put(EXECUTOR_SERVICE_HANDLER.getKey(), DefaultExecutorServiceHandler.class.getCanonicalName());
        jobSettings.getJobProperties().put(JOB_EXCEPTION_HANDLER.getKey(), DefaultJobExceptionHandler.class.getCanonicalName());
        jobSettings.setReconcileIntervalMinutes(70);
        jobSettingsAPI.updateJobSettings(jobSettings);
        Mockito.verify(regCenter).update("/test_job/config", (((((("{\"jobName\":\"test_job\",\"jobClass\":\"io.elasticjob.lite.fixture.TestDataflowJob\"," + (("\"cron\":\"0/1 * * * * ?\",\"shardingTotalCount\":10,\"monitorExecution\":true,\"streamingProcess\":true," + "\"maxTimeDiffSeconds\":-1,\"monitorPort\":-1,\"failover\":false,\"misfire\":true,") + "\"jobProperties\":{\"executor_service_handler\":\"")) + (DefaultExecutorServiceHandler.class.getCanonicalName())) + "\",") + "\"job_exception_handler\":\"") + (DefaultJobExceptionHandler.class.getCanonicalName())) + "\"},\"reconcileIntervalMinutes\":70}"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertUpdateJobSettingsIfJobNameIsEmpty() {
        JobSettings jobSettings = new JobSettings();
        jobSettings.setJobName("");
        jobSettingsAPI.updateJobSettings(jobSettings);
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertUpdateJobSettingsIfCronIsEmpty() {
        JobSettings jobSettings = new JobSettings();
        jobSettings.setJobName("test_job");
        jobSettings.setCron("");
        jobSettingsAPI.updateJobSettings(jobSettings);
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertUpdateJobSettingsIfShardingTotalCountLessThanOne() {
        JobSettings jobSettings = new JobSettings();
        jobSettings.setJobName("test_job");
        jobSettings.setCron("0/1 * * * * ?");
        jobSettings.setShardingTotalCount(0);
        jobSettingsAPI.updateJobSettings(jobSettings);
    }

    @Test
    public void assertRemoveJobSettings() {
        Mockito.when(regCenter.get("/test_job/config")).thenReturn(LifecycleJsonConstants.getScriptJobJson());
        jobSettingsAPI.removeJobSettings("test_job");
        Mockito.verify(regCenter).remove("/test_job");
    }
}

