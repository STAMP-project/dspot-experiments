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
package io.elasticjob.lite.api.listener;


import com.google.common.collect.Sets;
import io.elasticjob.lite.api.listener.fixture.ElasticJobListenerCaller;
import io.elasticjob.lite.api.listener.fixture.TestDistributeOnceElasticJobListener;
import io.elasticjob.lite.exception.JobSystemException;
import io.elasticjob.lite.executor.ShardingContexts;
import io.elasticjob.lite.internal.guarantee.GuaranteeService;
import io.elasticjob.lite.util.env.TimeService;
import java.util.Arrays;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class DistributeOnceElasticJobListenerTest {
    @Mock
    private GuaranteeService guaranteeService;

    @Mock
    private TimeService timeService;

    @Mock
    private ElasticJobListenerCaller elasticJobListenerCaller;

    private ShardingContexts shardingContexts;

    private TestDistributeOnceElasticJobListener distributeOnceElasticJobListener;

    @Test
    public void assertBeforeJobExecutedWhenIsAllStarted() {
        Mockito.when(guaranteeService.isAllStarted()).thenReturn(true);
        distributeOnceElasticJobListener.beforeJobExecuted(shardingContexts);
        Mockito.verify(guaranteeService).registerStart(Sets.newHashSet(0, 1));
        Mockito.verify(elasticJobListenerCaller).before();
        Mockito.verify(guaranteeService).clearAllStartedInfo();
    }

    @Test
    public void assertBeforeJobExecutedWhenIsNotAllStartedAndNotTimeout() {
        Mockito.when(guaranteeService.isAllStarted()).thenReturn(false);
        Mockito.when(timeService.getCurrentMillis()).thenReturn(0L);
        distributeOnceElasticJobListener.beforeJobExecuted(shardingContexts);
        Mockito.verify(guaranteeService).registerStart(Sets.newHashSet(0, 1));
        Mockito.verify(guaranteeService, Mockito.times(0)).clearAllStartedInfo();
    }

    @Test(expected = JobSystemException.class)
    public void assertBeforeJobExecutedWhenIsNotAllStartedAndTimeout() {
        Mockito.when(guaranteeService.isAllStarted()).thenReturn(false);
        Mockito.when(timeService.getCurrentMillis()).thenReturn(0L, 2L);
        distributeOnceElasticJobListener.beforeJobExecuted(shardingContexts);
        Mockito.verify(guaranteeService).registerStart(Arrays.asList(0, 1));
        Mockito.verify(guaranteeService, Mockito.times(0)).clearAllStartedInfo();
    }

    @Test
    public void assertAfterJobExecutedWhenIsAllCompleted() {
        Mockito.when(guaranteeService.isAllCompleted()).thenReturn(true);
        distributeOnceElasticJobListener.afterJobExecuted(shardingContexts);
        Mockito.verify(guaranteeService).registerComplete(Sets.newHashSet(0, 1));
        Mockito.verify(elasticJobListenerCaller).after();
        Mockito.verify(guaranteeService).clearAllCompletedInfo();
    }

    @Test
    public void assertAfterJobExecutedWhenIsAllCompletedAndNotTimeout() {
        Mockito.when(guaranteeService.isAllCompleted()).thenReturn(false);
        Mockito.when(timeService.getCurrentMillis()).thenReturn(0L);
        distributeOnceElasticJobListener.afterJobExecuted(shardingContexts);
        Mockito.verify(guaranteeService).registerComplete(Sets.newHashSet(0, 1));
        Mockito.verify(guaranteeService, Mockito.times(0)).clearAllCompletedInfo();
    }

    @Test(expected = JobSystemException.class)
    public void assertAfterJobExecutedWhenIsAllCompletedAndTimeout() {
        Mockito.when(guaranteeService.isAllCompleted()).thenReturn(false);
        Mockito.when(timeService.getCurrentMillis()).thenReturn(0L, 2L);
        distributeOnceElasticJobListener.afterJobExecuted(shardingContexts);
        Mockito.verify(guaranteeService).registerComplete(Arrays.asList(0, 1));
        Mockito.verify(guaranteeService, Mockito.times(0)).clearAllCompletedInfo();
    }
}

