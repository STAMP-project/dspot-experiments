/**
 * Copyright 2017 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.executor;


import azkaban.utils.JSONUtils;
import azkaban.utils.Pair;
import com.google.common.collect.ImmutableMap;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;


public class ExecutorApiGatewayTest {
    private ExecutorApiGateway gateway;

    private ExecutorApiClient client;

    @Captor
    ArgumentCaptor<List<Pair<String, String>>> params;

    @Test
    public void testExecutorInfoJsonParser() throws Exception {
        final ExecutorInfo exeInfo = new ExecutorInfo(99.9, 14095, 50, System.currentTimeMillis(), 89, 10);
        final String json = JSONUtils.toJSON(exeInfo);
        Mockito.when(this.client.httpPost(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(json);
        final ExecutorInfo exeInfo2 = this.gateway.callForJsonType("localhost", 1234, "executor", null, ExecutorInfo.class);
        Assert.assertTrue(exeInfo.equals(exeInfo2));
    }

    @Test
    public void updateExecutions() throws Exception {
        final ImmutableMap<String, String> map = ImmutableMap.of("test", "response");
        Mockito.when(this.client.httpPost(ArgumentMatchers.eq(new URI("http://executor-2:1234/executor")), this.params.capture())).thenReturn(JSONUtils.toJSON(map));
        final Map<String, Object> response = this.gateway.updateExecutions(new Executor(2, "executor-2", 1234, true), Collections.singletonList(new ExecutableFlow()));
        Assert.assertEquals(map, response);
        Assert.assertEquals(new Pair("executionId", "[-1]"), this.params.getValue().get(0));
        Assert.assertEquals(new Pair("updatetime", "[-1]"), this.params.getValue().get(1));
        Assert.assertEquals(new Pair("action", "update"), this.params.getValue().get(2));
        Assert.assertEquals(new Pair("execid", "null"), this.params.getValue().get(3));
        Assert.assertEquals(new Pair("user", null), this.params.getValue().get(4));
    }
}

