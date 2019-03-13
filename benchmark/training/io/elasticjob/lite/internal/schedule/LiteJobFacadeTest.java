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
package io.elasticjob.lite.internal.schedule;


import com.google.common.collect.Lists;
import io.elasticjob.lite.api.listener.fixture.ElasticJobListenerCaller;
import io.elasticjob.lite.config.JobCoreConfiguration;
import io.elasticjob.lite.config.LiteJobConfiguration;
import io.elasticjob.lite.event.JobEventBus;
import io.elasticjob.lite.exception.JobExecutionEnvironmentException;
import io.elasticjob.lite.executor.ShardingContexts;
import io.elasticjob.lite.fixture.TestDataflowJob;
import io.elasticjob.lite.fixture.TestSimpleJob;
import io.elasticjob.lite.internal.config.ConfigurationService;
import io.elasticjob.lite.internal.failover.FailoverService;
import io.elasticjob.lite.internal.sharding.ExecutionContextService;
import io.elasticjob.lite.internal.sharding.ExecutionService;
import io.elasticjob.lite.internal.sharding.ShardingService;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class LiteJobFacadeTest {
    @Mock
    private ConfigurationService configService;

    @Mock
    private ShardingService shardingService;

    @Mock
    private ExecutionContextService executionContextService;

    @Mock
    private ExecutionService executionService;

    @Mock
    private FailoverService failoverService;

    @Mock
    private JobEventBus eventBus;

    @Mock
    private ElasticJobListenerCaller caller;

    private LiteJobFacade liteJobFacade;

    @Test
    public void assertLoad() {
        LiteJobConfiguration expected = LiteJobConfiguration.newBuilder(null).build();
        Mockito.when(configService.load(true)).thenReturn(expected);
        Assert.assertThat(liteJobFacade.loadJobRootConfiguration(true), Is.is(expected));
    }

    @Test
    public void assertCheckMaxTimeDiffSecondsTolerable() throws JobExecutionEnvironmentException {
        liteJobFacade.checkJobExecutionEnvironment();
        Mockito.verify(configService).checkMaxTimeDiffSecondsTolerable();
    }

    @Test
    public void assertFailoverIfUnnecessary() {
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).failover(false).build(), TestSimpleJob.class.getCanonicalName())).build());
        liteJobFacade.failoverIfNecessary();
        Mockito.verify(failoverService, Mockito.times(0)).failoverIfNecessary();
    }

    @Test
    public void assertFailoverIfNecessary() {
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).failover(true).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
        liteJobFacade.failoverIfNecessary();
        Mockito.verify(failoverService).failoverIfNecessary();
    }

    @Test
    public void assertRegisterJobBegin() {
        ShardingContexts shardingContexts = new ShardingContexts("fake_task_id", "test_job", 10, "", Collections.<Integer, String>emptyMap());
        liteJobFacade.registerJobBegin(shardingContexts);
        Mockito.verify(executionService).registerJobBegin(shardingContexts);
    }

    @Test
    public void assertRegisterJobCompletedWhenFailoverDisabled() {
        ShardingContexts shardingContexts = new ShardingContexts("fake_task_id", "test_job", 10, "", Collections.<Integer, String>emptyMap());
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).failover(false).build(), TestSimpleJob.class.getCanonicalName())).build());
        liteJobFacade.registerJobCompleted(shardingContexts);
        Mockito.verify(executionService).registerJobCompleted(shardingContexts);
        Mockito.verify(failoverService, Mockito.times(0)).updateFailoverComplete(shardingContexts.getShardingItemParameters().keySet());
    }

    @Test
    public void assertRegisterJobCompletedWhenFailoverEnabled() {
        ShardingContexts shardingContexts = new ShardingContexts("fake_task_id", "test_job", 10, "", Collections.<Integer, String>emptyMap());
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).failover(true).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
        liteJobFacade.registerJobCompleted(shardingContexts);
        Mockito.verify(executionService).registerJobCompleted(shardingContexts);
        Mockito.verify(failoverService).updateFailoverComplete(shardingContexts.getShardingItemParameters().keySet());
    }

    @Test
    public void assertGetShardingContextWhenIsFailoverEnableAndFailover() {
        ShardingContexts shardingContexts = new ShardingContexts("fake_task_id", "test_job", 10, "", Collections.<Integer, String>emptyMap());
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).failover(true).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
        Mockito.when(failoverService.getLocalFailoverItems()).thenReturn(Collections.singletonList(1));
        Mockito.when(executionContextService.getJobShardingContext(Collections.singletonList(1))).thenReturn(shardingContexts);
        Assert.assertThat(liteJobFacade.getShardingContexts(), Is.is(shardingContexts));
        Mockito.verify(shardingService, Mockito.times(0)).shardingIfNecessary();
    }

    @Test
    public void assertGetShardingContextWhenIsFailoverEnableAndNotFailover() {
        ShardingContexts shardingContexts = new ShardingContexts("fake_task_id", "test_job", 10, "", Collections.<Integer, String>emptyMap());
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).failover(true).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
        Mockito.when(failoverService.getLocalFailoverItems()).thenReturn(Collections.<Integer>emptyList());
        Mockito.when(shardingService.getLocalShardingItems()).thenReturn(Lists.newArrayList(0, 1));
        Mockito.when(failoverService.getLocalTakeOffItems()).thenReturn(Collections.singletonList(0));
        Mockito.when(executionContextService.getJobShardingContext(Collections.singletonList(1))).thenReturn(shardingContexts);
        Assert.assertThat(liteJobFacade.getShardingContexts(), Is.is(shardingContexts));
        Mockito.verify(shardingService).shardingIfNecessary();
    }

    @Test
    public void assertGetShardingContextWhenIsFailoverDisable() {
        ShardingContexts shardingContexts = new ShardingContexts("fake_task_id", "test_job", 10, "", Collections.<Integer, String>emptyMap());
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).failover(false).build(), TestSimpleJob.class.getCanonicalName())).build());
        Mockito.when(shardingService.getLocalShardingItems()).thenReturn(Lists.newArrayList(0, 1));
        Mockito.when(executionContextService.getJobShardingContext(Lists.newArrayList(0, 1))).thenReturn(shardingContexts);
        Assert.assertThat(liteJobFacade.getShardingContexts(), Is.is(shardingContexts));
        Mockito.verify(shardingService).shardingIfNecessary();
    }

    @Test
    public void assertGetShardingContextWhenHasDisabledItems() {
        ShardingContexts shardingContexts = new ShardingContexts("fake_task_id", "test_job", 10, "", Collections.<Integer, String>emptyMap());
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).failover(false).build(), TestSimpleJob.class.getCanonicalName())).build());
        Mockito.when(shardingService.getLocalShardingItems()).thenReturn(Lists.newArrayList(0, 1));
        Mockito.when(executionService.getDisabledItems(Lists.newArrayList(0, 1))).thenReturn(Collections.singletonList(1));
        Mockito.when(executionContextService.getJobShardingContext(Lists.newArrayList(0))).thenReturn(shardingContexts);
        Assert.assertThat(liteJobFacade.getShardingContexts(), Is.is(shardingContexts));
        Mockito.verify(shardingService).shardingIfNecessary();
    }

    @Test
    public void assertMisfireIfRunning() {
        Mockito.when(executionService.misfireIfHasRunningItems(Arrays.asList(0, 1))).thenReturn(true);
        Assert.assertThat(liteJobFacade.misfireIfRunning(Arrays.asList(0, 1)), Is.is(true));
    }

    @Test
    public void assertClearMisfire() {
        liteJobFacade.clearMisfire(Arrays.asList(0, 1));
        Mockito.verify(executionService).clearMisfire(Arrays.asList(0, 1));
    }

    @Test
    public void assertIsNeedSharding() {
        Mockito.when(shardingService.isNeedSharding()).thenReturn(true);
        Assert.assertThat(liteJobFacade.isNeedSharding(), Is.is(true));
    }

    @Test
    public void assertBeforeJobExecuted() {
        liteJobFacade.beforeJobExecuted(new ShardingContexts("fake_task_id", "test_job", 10, "", Collections.<Integer, String>emptyMap()));
        Mockito.verify(caller).before();
    }

    @Test
    public void assertAfterJobExecuted() {
        liteJobFacade.afterJobExecuted(new ShardingContexts("fake_task_id", "test_job", 10, "", Collections.<Integer, String>emptyMap()));
        Mockito.verify(caller).after();
    }

    @Test
    public void assertNotEligibleForJobRunningWhenNeedSharding() {
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.dataflow.DataflowJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestDataflowJob.class.getCanonicalName(), true)).build());
        Mockito.when(shardingService.isNeedSharding()).thenReturn(true);
        Assert.assertThat(liteJobFacade.isEligibleForJobRunning(), Is.is(false));
        Mockito.verify(shardingService).isNeedSharding();
    }

    @Test
    public void assertNotEligibleForJobRunningWhenUnStreamingProcess() {
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.dataflow.DataflowJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestDataflowJob.class.getCanonicalName(), false)).build());
        Assert.assertThat(liteJobFacade.isEligibleForJobRunning(), Is.is(false));
        Mockito.verify(configService).load(true);
    }

    @Test
    public void assertEligibleForJobRunningWhenNotNeedShardingAndStreamingProcess() {
        Mockito.when(shardingService.isNeedSharding()).thenReturn(false);
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.dataflow.DataflowJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestDataflowJob.class.getCanonicalName(), true)).build());
        Assert.assertThat(liteJobFacade.isEligibleForJobRunning(), Is.is(true));
        Mockito.verify(shardingService).isNeedSharding();
        Mockito.verify(configService).load(true);
    }

    @Test
    public void assertPostJobExecutionEvent() {
        liteJobFacade.postJobExecutionEvent(null);
        Mockito.verify(eventBus).post(null);
    }
}

