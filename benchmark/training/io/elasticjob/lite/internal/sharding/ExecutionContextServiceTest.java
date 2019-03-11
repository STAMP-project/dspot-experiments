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


import com.google.common.collect.Lists;
import io.elasticjob.lite.config.JobCoreConfiguration;
import io.elasticjob.lite.config.LiteJobConfiguration;
import io.elasticjob.lite.executor.ShardingContexts;
import io.elasticjob.lite.fixture.TestDataflowJob;
import io.elasticjob.lite.internal.config.ConfigurationService;
import io.elasticjob.lite.internal.storage.JobNodeStorage;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class ExecutionContextServiceTest {
    @Mock
    private JobNodeStorage jobNodeStorage;

    @Mock
    private ConfigurationService configService;

    private final ExecutionContextService executionContextService = new ExecutionContextService(null, "test_job");

    @Test
    public void assertGetShardingContextWhenNotAssignShardingItem() {
        Mockito.when(configService.load(false)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.dataflow.DataflowJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestDataflowJob.class.getCanonicalName(), true)).monitorExecution(false).build());
        ShardingContexts shardingContexts = executionContextService.getJobShardingContext(Collections.<Integer>emptyList());
        TestCase.assertTrue(shardingContexts.getTaskId().startsWith("test_job@-@@-@READY@-@"));
        Assert.assertThat(shardingContexts.getShardingTotalCount(), Is.is(3));
    }

    @Test
    public void assertGetShardingContextWhenAssignShardingItems() {
        Mockito.when(configService.load(false)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.dataflow.DataflowJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).shardingItemParameters("0=A,1=B,2=C").build(), TestDataflowJob.class.getCanonicalName(), true)).monitorExecution(false).build());
        Map<Integer, String> map = new HashMap<>(3);
        map.put(0, "A");
        map.put(1, "B");
        ShardingContexts expected = new ShardingContexts("fake_task_id", "test_job", 3, "", map);
        assertShardingContext(executionContextService.getJobShardingContext(Arrays.asList(0, 1)), expected);
    }

    @Test
    public void assertGetShardingContextWhenHasRunningItems() {
        Mockito.when(configService.load(false)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.dataflow.DataflowJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).shardingItemParameters("0=A,1=B,2=C").build(), TestDataflowJob.class.getCanonicalName(), true)).monitorExecution(true).build());
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/0/running")).thenReturn(false);
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/1/running")).thenReturn(true);
        Map<Integer, String> map = new HashMap<>(1, 1);
        map.put(0, "A");
        ShardingContexts expected = new ShardingContexts("fake_task_id", "test_job", 3, "", map);
        assertShardingContext(executionContextService.getJobShardingContext(Lists.newArrayList(0, 1)), expected);
    }
}

