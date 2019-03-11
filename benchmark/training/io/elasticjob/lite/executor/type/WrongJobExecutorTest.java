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


import State.TASK_RUNNING;
import io.elasticjob.lite.executor.JobFacade;
import io.elasticjob.lite.executor.ShardingContexts;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class WrongJobExecutorTest {
    @Mock
    private JobFacade jobFacade;

    private SimpleJobExecutor wrongSimpleJobExecutor;

    @Test(expected = RuntimeException.class)
    public void assertWrongJobExecutorWithSingleItem() throws NoSuchFieldException {
        Map<Integer, String> map = new HashMap<>(1, 1);
        map.put(0, "A");
        ShardingContexts shardingContexts = new ShardingContexts("fake_task_id", "test_job", 10, "", map);
        Mockito.when(jobFacade.getShardingContexts()).thenReturn(shardingContexts);
        wrongSimpleJobExecutor.execute();
    }

    @Test
    public void assertWrongJobExecutorWithMultipleItems() throws NoSuchFieldException {
        Map<Integer, String> map = new HashMap<>(1, 1);
        map.put(0, "A");
        map.put(1, "B");
        ShardingContexts shardingContexts = new ShardingContexts("fake_task_id", "test_job", 10, "", map);
        Mockito.when(jobFacade.getShardingContexts()).thenReturn(shardingContexts);
        wrongSimpleJobExecutor.execute();
        Mockito.verify(jobFacade).getShardingContexts();
        Mockito.verify(jobFacade).postJobStatusTraceEvent("fake_task_id", TASK_RUNNING, "");
    }
}

