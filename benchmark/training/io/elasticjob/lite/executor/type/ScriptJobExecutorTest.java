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


import io.elasticjob.lite.exception.JobSystemException;
import io.elasticjob.lite.executor.JobFacade;
import io.elasticjob.lite.fixture.ShardingContextsBuilder;
import io.elasticjob.lite.fixture.config.TestScriptJobConfiguration;
import io.elasticjob.lite.fixture.handler.IgnoreJobExceptionHandler;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class ScriptJobExecutorTest {
    @Mock
    private JobFacade jobFacade;

    private ScriptJobExecutor scriptJobExecutor;

    @Test
    public void assertExecuteWhenCommandLineIsEmpty() throws IOException {
        ElasticJobVerify.prepareForIsNotMisfire(jobFacade, ShardingContextsBuilder.getMultipleShardingContexts());
        Mockito.when(jobFacade.loadJobRootConfiguration(true)).thenReturn(new TestScriptJobConfiguration("", IgnoreJobExceptionHandler.class));
        scriptJobExecutor = new ScriptJobExecutor(jobFacade);
        scriptJobExecutor.execute();
    }

    @Test(expected = JobSystemException.class)
    public void assertExecuteWhenExecuteFailureForSingleShardingItems() throws IOException, NoSuchFieldException {
        assertExecuteWhenExecuteFailure(ShardingContextsBuilder.getSingleShardingContexts());
    }

    @Test
    public void assertExecuteWhenExecuteFailureForMultipleShardingItems() throws IOException, NoSuchFieldException {
        assertExecuteWhenExecuteFailure(ShardingContextsBuilder.getMultipleShardingContexts());
    }

    @Test
    public void assertExecuteSuccessForMultipleShardingItems() throws IOException, NoSuchFieldException {
        assertExecuteSuccess(ShardingContextsBuilder.getMultipleShardingContexts());
    }

    @Test
    public void assertExecuteSuccessForSingleShardingItems() throws IOException, NoSuchFieldException {
        assertExecuteSuccess(ShardingContextsBuilder.getSingleShardingContexts());
    }
}

