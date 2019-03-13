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


import DefaultGoApiResponse.SUCCESS_RESPONSE_CODE;
import PluginSettingsConstants.REQUEST_PLUGIN_SETTINGS_CONFIGURATION;
import PluginSettingsConstants.REQUEST_PLUGIN_SETTINGS_VIEW;
import PluginSettingsConstants.REQUEST_VALIDATE_PLUGIN_SETTINGS;
import com.thoughtworks.go.plugin.access.ExtensionsRegistry;
import com.thoughtworks.go.plugin.access.common.AbstractExtension;
import com.thoughtworks.go.plugin.access.common.settings.PluginSettingsConfiguration;
import com.thoughtworks.go.plugin.access.common.settings.PluginSettingsJsonMessageHandler1_0;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.execution.ExecutionResult;
import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;
import com.thoughtworks.go.plugin.api.task.TaskConfig;
import com.thoughtworks.go.plugin.domain.common.PluginConstants;
import com.thoughtworks.go.plugin.infra.Action;
import com.thoughtworks.go.plugin.infra.ActionWithReturn;
import com.thoughtworks.go.plugin.infra.PluginManager;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class TaskExtensionTest {
    @Mock
    private PluginManager pluginManager;

    @Mock
    private ExtensionsRegistry extensionsRegistry;

    @Mock
    private PluginSettingsJsonMessageHandler1_0 pluginSettingsJSONMessageHandler;

    @Mock
    private JsonBasedTaskExtensionHandler jsonMessageHandler;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private TaskExtension extension;

    private String pluginId;

    private PluginSettingsConfiguration pluginSettingsConfiguration;

    private ArgumentCaptor<GoPluginApiRequest> requestArgumentCaptor;

    @Test
    public void shouldExtendAbstractExtension() throws Exception {
        Assert.assertTrue(((extension) instanceof AbstractExtension));
    }

    @Test
    public void shouldTalkToPluginToGetPluginSettingsConfiguration() throws Exception {
        extension.registerHandler("1.0", pluginSettingsJSONMessageHandler);
        extension.messageHandlerMap.put("1.0", jsonMessageHandler);
        String responseBody = "expected-response";
        PluginSettingsConfiguration deserializedResponse = new PluginSettingsConfiguration();
        Mockito.when(pluginSettingsJSONMessageHandler.responseMessageForPluginSettingsConfiguration(responseBody)).thenReturn(deserializedResponse);
        Mockito.when(pluginManager.isPluginOfType(PluginConstants.PLUGGABLE_TASK_EXTENSION, pluginId)).thenReturn(true);
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(pluginId), ArgumentMatchers.eq(PluginConstants.PLUGGABLE_TASK_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(responseBody));
        PluginSettingsConfiguration response = extension.getPluginSettingsConfiguration(pluginId);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.PLUGGABLE_TASK_EXTENSION, "1.0", REQUEST_PLUGIN_SETTINGS_CONFIGURATION, null);
        Mockito.verify(pluginSettingsJSONMessageHandler).responseMessageForPluginSettingsConfiguration(responseBody);
        Assert.assertSame(response, deserializedResponse);
    }

    @Test
    public void shouldTalkToPluginToGetPluginSettingsView() throws Exception {
        extension.registerHandler("1.0", pluginSettingsJSONMessageHandler);
        extension.messageHandlerMap.put("1.0", jsonMessageHandler);
        String responseBody = "expected-response";
        String deserializedResponse = "";
        Mockito.when(pluginSettingsJSONMessageHandler.responseMessageForPluginSettingsView(responseBody)).thenReturn(deserializedResponse);
        Mockito.when(pluginManager.isPluginOfType(PluginConstants.PLUGGABLE_TASK_EXTENSION, pluginId)).thenReturn(true);
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(pluginId), ArgumentMatchers.eq(PluginConstants.PLUGGABLE_TASK_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(responseBody));
        String response = extension.getPluginSettingsView(pluginId);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.PLUGGABLE_TASK_EXTENSION, "1.0", REQUEST_PLUGIN_SETTINGS_VIEW, null);
        Mockito.verify(pluginSettingsJSONMessageHandler).responseMessageForPluginSettingsView(responseBody);
        Assert.assertSame(response, deserializedResponse);
    }

    @Test
    public void shouldTalkToPluginToValidatePluginSettings() throws Exception {
        extension.registerHandler("1.0", pluginSettingsJSONMessageHandler);
        extension.messageHandlerMap.put("1.0", jsonMessageHandler);
        String requestBody = "expected-request";
        Mockito.when(pluginSettingsJSONMessageHandler.requestMessageForPluginSettingsValidation(pluginSettingsConfiguration)).thenReturn(requestBody);
        String responseBody = "expected-response";
        ValidationResult deserializedResponse = new ValidationResult();
        Mockito.when(pluginSettingsJSONMessageHandler.responseMessageForPluginSettingsValidation(responseBody)).thenReturn(deserializedResponse);
        Mockito.when(pluginManager.isPluginOfType(PluginConstants.PLUGGABLE_TASK_EXTENSION, pluginId)).thenReturn(true);
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(pluginId), ArgumentMatchers.eq(PluginConstants.PLUGGABLE_TASK_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(responseBody));
        ValidationResult response = extension.validatePluginSettings(pluginId, pluginSettingsConfiguration);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.PLUGGABLE_TASK_EXTENSION, "1.0", REQUEST_VALIDATE_PLUGIN_SETTINGS, requestBody);
        Mockito.verify(pluginSettingsJSONMessageHandler).responseMessageForPluginSettingsValidation(responseBody);
        Assert.assertSame(response, deserializedResponse);
    }

    @Test
    public void shouldExecuteTheTask() {
        ActionWithReturn actionWithReturn = Mockito.mock(ActionWithReturn.class);
        Mockito.when(actionWithReturn.execute(ArgumentMatchers.any(JsonBasedPluggableTask.class), ArgumentMatchers.nullable(GoPluginDescriptor.class))).thenReturn(ExecutionResult.success("yay"));
        ExecutionResult executionResult = extension.execute(pluginId, actionWithReturn);
        Mockito.verify(actionWithReturn).execute(ArgumentMatchers.any(JsonBasedPluggableTask.class), ArgumentMatchers.nullable(GoPluginDescriptor.class));
        Assert.assertThat(executionResult.getMessagesForDisplay(), Matchers.is("yay"));
        Assert.assertTrue(executionResult.isSuccessful());
    }

    @Test
    public void shouldPerformTheActionOnTask() {
        Action action = Mockito.mock(Action.class);
        final GoPluginDescriptor descriptor = Mockito.mock(GoPluginDescriptor.class);
        Mockito.when(pluginManager.getPluginDescriptorFor(pluginId)).thenReturn(descriptor);
        extension.doOnTask(pluginId, action);
        Mockito.verify(action).execute(ArgumentMatchers.any(JsonBasedPluggableTask.class), ArgumentMatchers.eq(descriptor));
    }

    @Test
    public void shouldValidateTask() {
        GoPluginApiResponse response = Mockito.mock(GoPluginApiResponse.class);
        TaskExtension jsonBasedTaskExtension = new TaskExtension(pluginManager, extensionsRegistry);
        TaskConfig taskConfig = Mockito.mock(TaskConfig.class);
        Mockito.when(response.responseCode()).thenReturn(SUCCESS_RESPONSE_CODE);
        Mockito.when(pluginManager.isPluginOfType(PluginConstants.PLUGGABLE_TASK_EXTENSION, pluginId)).thenReturn(true);
        Mockito.when(response.responseBody()).thenReturn("{\"errors\":{\"key\":\"error\"}}");
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(pluginId), ArgumentMatchers.eq(PluginConstants.PLUGGABLE_TASK_EXTENSION), ArgumentMatchers.any(GoPluginApiRequest.class))).thenReturn(response);
        ValidationResult validationResult = jsonBasedTaskExtension.validate(pluginId, taskConfig);
        Mockito.verify(pluginManager).submitTo(ArgumentMatchers.eq(pluginId), ArgumentMatchers.eq(PluginConstants.PLUGGABLE_TASK_EXTENSION), ArgumentMatchers.any(GoPluginApiRequest.class));
        Assert.assertFalse(validationResult.isSuccessful());
        Assert.assertEquals(validationResult.getErrors().get(0).getKey(), "key");
        Assert.assertEquals(validationResult.getErrors().get(0).getMessage(), "error");
    }
}

