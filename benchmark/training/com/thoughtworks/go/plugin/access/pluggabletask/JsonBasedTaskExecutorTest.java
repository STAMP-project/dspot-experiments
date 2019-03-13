/**
 * Copyright 2018 ThoughtWorks, Inc.
 *
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
 */
package com.thoughtworks.go.plugin.access.pluggabletask;


import TaskExtension.EXECUTION_REQUEST;
import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.access.PluginRequestHelper;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.execution.ExecutionResult;
import com.thoughtworks.go.plugin.api.task.Console;
import com.thoughtworks.go.plugin.domain.common.PluginConstants;
import com.thoughtworks.go.plugin.infra.PluginManager;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class JsonBasedTaskExecutorTest {
    private final String extensionVersion = "1.0";

    private TaskExecutionContext context;

    private PluginManager pluginManager;

    private String pluginId;

    private GoPluginApiResponse response;

    private JsonBasedTaskExtensionHandler handler;

    private PluginRequestHelper pluginRequestHelper;

    private HashMap<String, JsonBasedTaskExtensionHandler> handlerHashMap = new HashMap<>();

    @Test
    public void shouldExecuteAndReturnSuccessfulExecutionResultTaskThroughPlugin() {
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(pluginId), ArgumentMatchers.eq(PluginConstants.PLUGGABLE_TASK_EXTENSION), ArgumentMatchers.any(GoPluginApiRequest.class))).thenReturn(response);
        Mockito.when(handler.toExecutionResult(response.responseBody())).thenReturn(ExecutionResult.success("message1"));
        ExecutionResult result = new JsonBasedTaskExecutor(pluginId, pluginRequestHelper, handlerHashMap).execute(config(), context);
        Assert.assertThat(result.isSuccessful(), Matchers.is(true));
        Assert.assertThat(result.getMessagesForDisplay(), Matchers.is("message1"));
        ArgumentCaptor<GoPluginApiRequest> argument = ArgumentCaptor.forClass(GoPluginApiRequest.class);
        Mockito.verify(pluginManager).submitTo(ArgumentMatchers.eq(pluginId), ArgumentMatchers.eq(PluginConstants.PLUGGABLE_TASK_EXTENSION), argument.capture());
        Assert.assertThat(argument.getValue().extension(), Matchers.is(PluginConstants.PLUGGABLE_TASK_EXTENSION));
        Assert.assertThat(argument.getValue().extensionVersion(), Matchers.is(extensionVersion));
        Assert.assertThat(argument.getValue().requestName(), Matchers.is(EXECUTION_REQUEST));
    }

    @Test
    public void shouldExecuteAndReturnFailureExecutionResultTaskThroughPlugin() {
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(pluginId), ArgumentMatchers.eq(PluginConstants.PLUGGABLE_TASK_EXTENSION), ArgumentMatchers.any(GoPluginApiRequest.class))).thenReturn(response);
        Mockito.when(handler.toExecutionResult(response.responseBody())).thenReturn(ExecutionResult.failure("error1"));
        ExecutionResult result = new JsonBasedTaskExecutor(pluginId, pluginRequestHelper, handlerHashMap).execute(config(), context);
        Assert.assertThat(result.isSuccessful(), Matchers.is(false));
        Assert.assertThat(result.getMessagesForDisplay(), Matchers.is("error1"));
    }

    @Test
    public void shouldConstructExecutionRequestWithRequiredDetails() {
        String workingDir = "working-dir";
        Console console = Mockito.mock(Console.class);
        Mockito.when(context.workingDir()).thenReturn(workingDir);
        EnvironmentVariables environment = getEnvironmentVariables();
        Mockito.when(context.environment()).thenReturn(environment);
        Mockito.when(context.console()).thenReturn(console);
        final GoPluginApiRequest[] executionRequest = new GoPluginApiRequest[1];
        Mockito.when(response.responseBody()).thenReturn("{\"success\":true,\"messages\":[\"message1\",\"message2\"]}");
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                GoPluginApiRequest request = ((GoPluginApiRequest) (invocationOnMock.getArguments()[2]));
                executionRequest[0] = request;
                return response;
            }
        }).when(pluginManager).submitTo(ArgumentMatchers.eq(pluginId), ArgumentMatchers.eq(PluginConstants.PLUGGABLE_TASK_EXTENSION), ArgumentMatchers.any(GoPluginApiRequest.class));
        handler = new JsonBasedTaskExtensionHandler_V1();
        handlerHashMap.put("1.0", handler);
        new JsonBasedTaskExecutor(pluginId, pluginRequestHelper, handlerHashMap).execute(config(), context);
        Assert.assertTrue(((executionRequest.length) == 1));
        Map result = ((Map) (new GsonBuilder().create().fromJson(executionRequest[0].requestBody(), Object.class)));
        Map context = ((Map) (result.get("context")));
        Assert.assertThat(context.get("workingDirectory"), Matchers.is(workingDir));
        Map environmentVariables = ((Map) (context.get("environmentVariables")));
        Assert.assertThat(environmentVariables.size(), Matchers.is(2));
        Assert.assertThat(environmentVariables.get("ENV1").toString(), Matchers.is("VAL1"));
        Assert.assertThat(environmentVariables.get("ENV2").toString(), Matchers.is("VAL2"));
        Assert.assertThat(executionRequest[0].requestParameters().size(), Matchers.is(0));
    }
}

