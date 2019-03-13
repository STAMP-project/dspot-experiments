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
package io.elasticjob.lite.internal.sharding;


import io.elasticjob.lite.config.JobCoreConfiguration;
import io.elasticjob.lite.config.LiteJobConfiguration;
import io.elasticjob.lite.executor.ShardingContexts;
import io.elasticjob.lite.fixture.TestSimpleJob;
import io.elasticjob.lite.internal.config.ConfigurationService;
import io.elasticjob.lite.internal.schedule.JobRegistry;
import io.elasticjob.lite.internal.schedule.JobScheduleController;
import io.elasticjob.lite.internal.storage.JobNodeStorage;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class ExecutionServiceTest {
    @Mock
    private JobNodeStorage jobNodeStorage;

    @Mock
    private ConfigurationService configService;

    @Mock
    private JobScheduleController jobScheduleController;

    private final ExecutionService executionService = new ExecutionService(null, "test_job");

    @Test
    public void assertRegisterJobBeginWithoutMonitorExecution() {
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(false).build());
        executionService.registerJobBegin(getShardingContext());
        Mockito.verify(jobNodeStorage, Mockito.times(0)).fillEphemeralJobNode(((String) (ArgumentMatchers.any())), ArgumentMatchers.any());
        Assert.assertTrue(JobRegistry.getInstance().isJobRunning("test_job"));
    }

    @Test
    public void assertRegisterJobBeginWithMonitorExecution() {
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
        executionService.registerJobBegin(getShardingContext());
        Mockito.verify(jobNodeStorage).fillEphemeralJobNode("sharding/0/running", "");
        Mockito.verify(jobNodeStorage).fillEphemeralJobNode("sharding/1/running", "");
        Mockito.verify(jobNodeStorage).fillEphemeralJobNode("sharding/2/running", "");
        Assert.assertTrue(JobRegistry.getInstance().isJobRunning("test_job"));
    }

    @Test
    public void assertRegisterJobCompletedWithoutMonitorExecution() {
        JobRegistry.getInstance().setJobRunning("test_job", true);
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(false).build());
        executionService.registerJobCompleted(new ShardingContexts("fake_task_id", "test_job", 10, "", Collections.<Integer, String>emptyMap()));
        Mockito.verify(jobNodeStorage, Mockito.times(0)).removeJobNodeIfExisted(((String) (ArgumentMatchers.any())));
        Mockito.verify(jobNodeStorage, Mockito.times(0)).createJobNodeIfNeeded(((String) (ArgumentMatchers.any())));
        Assert.assertFalse(JobRegistry.getInstance().isJobRunning("test_job"));
    }

    @Test
    public void assertRegisterJobCompletedWithMonitorExecution() {
        JobRegistry.getInstance().setJobRunning("test_job", true);
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
        executionService.registerJobCompleted(getShardingContext());
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("sharding/0/running");
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("sharding/1/running");
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("sharding/2/running");
        Assert.assertFalse(JobRegistry.getInstance().isJobRunning("test_job"));
    }

    @Test
    public void assertClearAllRunningInfo() {
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(false).build());
        executionService.clearAllRunningInfo();
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("sharding/0/running");
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("sharding/1/running");
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("sharding/2/running");
    }

    @Test
    public void assertClearRunningInfo() {
        executionService.clearRunningInfo(Arrays.asList(0, 1));
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("sharding/0/running");
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("sharding/1/running");
    }

    @Test
    public void assertNotHaveRunningItemsWithoutMonitorExecution() {
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(false).build());
        Assert.assertFalse(executionService.hasRunningItems(Arrays.asList(0, 1, 2)));
    }

    @Test
    public void assertHasRunningItemsWithMonitorExecution() {
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/0/running")).thenReturn(false);
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/1/running")).thenReturn(true);
        Assert.assertTrue(executionService.hasRunningItems(Arrays.asList(0, 1, 2)));
    }

    @Test
    public void assertNotHaveRunningItems() {
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/0/running")).thenReturn(false);
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/1/running")).thenReturn(false);
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/2/running")).thenReturn(false);
        Assert.assertFalse(executionService.hasRunningItems(Arrays.asList(0, 1, 2)));
    }

    @Test
    public void assertHasRunningItemsForAll() {
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestSimpleJob.class.getCanonicalName())).build());
        Mockito.when(jobNodeStorage.getJobNodeChildrenKeys("sharding")).thenReturn(Arrays.asList("0", "1", "2"));
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/0/running")).thenReturn(false);
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/1/running")).thenReturn(true);
        Assert.assertTrue(executionService.hasRunningItems());
    }

    @Test
    public void assertNotHaveRunningItemsForAll() {
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestSimpleJob.class.getCanonicalName())).build());
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/0/running")).thenReturn(false);
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/1/running")).thenReturn(false);
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/2/running")).thenReturn(false);
        Assert.assertFalse(executionService.hasRunningItems());
    }

    @Test
    public void assertMisfireIfNotRunning() {
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/0/running")).thenReturn(false);
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/1/running")).thenReturn(false);
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/2/running")).thenReturn(false);
        Assert.assertFalse(executionService.misfireIfHasRunningItems(Arrays.asList(0, 1, 2)));
    }

    @Test
    public void assertMisfireIfRunning() {
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/0/running")).thenReturn(false);
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/1/running")).thenReturn(true);
        Assert.assertTrue(executionService.misfireIfHasRunningItems(Arrays.asList(0, 1, 2)));
    }

    @Test
    public void assertSetMisfire() {
        executionService.setMisfire(Arrays.asList(0, 1, 2));
        Mockito.verify(jobNodeStorage).createJobNodeIfNeeded("sharding/0/misfire");
        Mockito.verify(jobNodeStorage).createJobNodeIfNeeded("sharding/1/misfire");
        Mockito.verify(jobNodeStorage).createJobNodeIfNeeded("sharding/2/misfire");
    }

    @Test
    public void assertGetMisfiredJobItems() {
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/0/misfire")).thenReturn(true);
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/1/misfire")).thenReturn(true);
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/2/misfire")).thenReturn(false);
        Assert.assertThat(executionService.getMisfiredJobItems(Arrays.asList(0, 1, 2)), CoreMatchers.is(Arrays.asList(0, 1)));
    }

    @Test
    public void assertClearMisfire() {
        executionService.clearMisfire(Arrays.asList(0, 1, 2));
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("sharding/0/misfire");
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("sharding/1/misfire");
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("sharding/2/misfire");
    }

    @Test
    public void assertGetDisabledItems() {
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/0/disabled")).thenReturn(true);
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/1/disabled")).thenReturn(true);
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/2/disabled")).thenReturn(false);
        Assert.assertThat(executionService.getDisabledItems(Arrays.asList(0, 1, 2)), CoreMatchers.is(Arrays.asList(0, 1)));
    }
}

