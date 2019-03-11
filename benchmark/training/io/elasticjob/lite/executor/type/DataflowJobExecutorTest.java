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
package io.elasticjob.lite.executor.type;


import io.elasticjob.lite.executor.JobFacade;
import io.elasticjob.lite.executor.ShardingContexts;
import io.elasticjob.lite.fixture.ShardingContextsBuilder;
import io.elasticjob.lite.fixture.job.JobCaller;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class DataflowJobExecutorTest {
    @Mock
    private JobCaller jobCaller;

    @Mock
    private JobFacade jobFacade;

    private ShardingContexts shardingContexts;

    private DataflowJobExecutor dataflowJobExecutor;

    @Test
    public void assertExecuteWhenFetchDataIsNullAndEmpty() {
        setUp(true, ShardingContextsBuilder.getMultipleShardingContexts());
        Mockito.when(jobCaller.fetchData(0)).thenReturn(null);
        Mockito.when(jobCaller.fetchData(1)).thenReturn(Collections.emptyList());
        dataflowJobExecutor.execute();
        Mockito.verify(jobCaller).fetchData(0);
        Mockito.verify(jobCaller).fetchData(1);
        Mockito.verify(jobCaller, Mockito.times(0)).processData(ArgumentMatchers.any());
    }

    @Test
    public void assertExecuteWhenFetchDataIsNotEmptyForUnStreamingProcessAndSingleShardingItem() {
        setUp(false, ShardingContextsBuilder.getSingleShardingContexts());
        Mockito.doThrow(new IllegalStateException()).when(jobCaller).fetchData(0);
        dataflowJobExecutor.execute();
        Mockito.verify(jobCaller).fetchData(0);
        Mockito.verify(jobCaller, Mockito.times(0)).processData(ArgumentMatchers.any());
    }

    @Test
    public void assertExecuteWhenFetchDataIsNotEmptyForUnStreamingProcessAndMultipleShardingItems() {
        setUp(false, ShardingContextsBuilder.getMultipleShardingContexts());
        Mockito.when(jobCaller.fetchData(0)).thenReturn(Arrays.<Object>asList(1, 2));
        Mockito.when(jobCaller.fetchData(1)).thenReturn(Arrays.<Object>asList(3, 4));
        Mockito.doThrow(new IllegalStateException()).when(jobCaller).processData(4);
        dataflowJobExecutor.execute();
        Mockito.verify(jobCaller).fetchData(0);
        Mockito.verify(jobCaller).fetchData(1);
        Mockito.verify(jobCaller).processData(1);
        Mockito.verify(jobCaller).processData(2);
        Mockito.verify(jobCaller).processData(3);
        Mockito.verify(jobCaller).processData(4);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void assertExecuteWhenFetchDataIsNotEmptyForStreamingProcessAndSingleShardingItem() {
        setUp(true, ShardingContextsBuilder.getSingleShardingContexts());
        Mockito.when(jobCaller.fetchData(0)).thenReturn(Collections.<Object>singletonList(1), Collections.emptyList());
        Mockito.when(jobFacade.isEligibleForJobRunning()).thenReturn(true);
        dataflowJobExecutor.execute();
        Mockito.verify(jobCaller, Mockito.times(2)).fetchData(0);
        Mockito.verify(jobCaller).processData(1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void assertExecuteWhenFetchDataIsNotEmptyForStreamingProcessAndMultipleShardingItems() {
        setUp(true, ShardingContextsBuilder.getMultipleShardingContexts());
        Mockito.when(jobCaller.fetchData(0)).thenReturn(Collections.<Object>singletonList(1), Collections.emptyList());
        Mockito.when(jobCaller.fetchData(1)).thenReturn(Collections.<Object>singletonList(2), Collections.emptyList());
        Mockito.when(jobFacade.isEligibleForJobRunning()).thenReturn(true);
        dataflowJobExecutor.execute();
        Mockito.verify(jobCaller, Mockito.times(2)).fetchData(0);
        Mockito.verify(jobCaller, Mockito.times(2)).fetchData(1);
        Mockito.verify(jobCaller).processData(1);
        Mockito.verify(jobCaller).processData(2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void assertExecuteWhenFetchDataIsNotEmptyAndProcessFailureWithExceptionForStreamingProcess() {
        setUp(true, ShardingContextsBuilder.getMultipleShardingContexts());
        Mockito.when(jobCaller.fetchData(0)).thenReturn(Collections.<Object>singletonList(1), Collections.emptyList());
        Mockito.when(jobCaller.fetchData(1)).thenReturn(Arrays.<Object>asList(2, 3), Collections.emptyList());
        Mockito.when(jobFacade.isEligibleForJobRunning()).thenReturn(true);
        Mockito.doThrow(new IllegalStateException()).when(jobCaller).processData(2);
        dataflowJobExecutor.execute();
        Mockito.verify(jobCaller, Mockito.times(2)).fetchData(0);
        Mockito.verify(jobCaller, Mockito.times(1)).fetchData(1);
        Mockito.verify(jobCaller).processData(1);
        Mockito.verify(jobCaller).processData(2);
        Mockito.verify(jobCaller, Mockito.times(0)).processData(3);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void assertExecuteWhenFetchDataIsNotEmptyAndIsEligibleForJobRunningForStreamingProcess() {
        setUp(true, ShardingContextsBuilder.getMultipleShardingContexts());
        Mockito.when(jobFacade.isEligibleForJobRunning()).thenReturn(true);
        Mockito.when(jobCaller.fetchData(0)).thenReturn(Arrays.<Object>asList(1, 2), Collections.emptyList());
        Mockito.when(jobCaller.fetchData(1)).thenReturn(Arrays.<Object>asList(3, 4), Collections.emptyList());
        Mockito.doThrow(new IllegalStateException()).when(jobCaller).processData(4);
        dataflowJobExecutor.execute();
        Mockito.verify(jobCaller, Mockito.times(2)).fetchData(0);
        Mockito.verify(jobCaller, Mockito.times(1)).fetchData(1);
        Mockito.verify(jobCaller).processData(1);
        Mockito.verify(jobCaller).processData(2);
        Mockito.verify(jobCaller).processData(3);
        Mockito.verify(jobCaller).processData(4);
    }

    @Test
    public void assertExecuteWhenFetchDataIsNotEmptyAndIsNotEligibleForJobRunningForStreamingProcess() {
        setUp(true, ShardingContextsBuilder.getMultipleShardingContexts());
        Mockito.when(jobFacade.isEligibleForJobRunning()).thenReturn(false);
        Mockito.when(jobCaller.fetchData(0)).thenReturn(Arrays.<Object>asList(1, 2));
        Mockito.when(jobCaller.fetchData(1)).thenReturn(Arrays.<Object>asList(3, 4));
        Mockito.doThrow(new IllegalStateException()).when(jobCaller).processData(4);
        dataflowJobExecutor.execute();
        Mockito.verify(jobCaller).fetchData(0);
        Mockito.verify(jobCaller).fetchData(1);
        Mockito.verify(jobCaller).processData(1);
        Mockito.verify(jobCaller).processData(2);
        Mockito.verify(jobCaller).processData(3);
        Mockito.verify(jobCaller).processData(4);
    }
}

