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


import JsonBasedTaskExtensionHandler_V1.VERSION;
import Property.REQUIRED;
import Property.SECURE;
import TaskExtension.CONFIGURATION_REQUEST;
import TaskExtension.TASK_VIEW_REQUEST;
import TaskExtension.VALIDATION_REQUEST;
import com.thoughtworks.go.plugin.api.config.Property;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;
import com.thoughtworks.go.plugin.api.task.TaskConfig;
import com.thoughtworks.go.plugin.api.task.TaskConfigProperty;
import com.thoughtworks.go.plugin.api.task.TaskView;
import com.thoughtworks.go.plugin.domain.common.PluginConstants;
import com.thoughtworks.go.plugin.infra.PluginManager;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class JsonBasedPluggableTaskTest {
    private PluginManager pluginManager;

    private JsonBasedPluggableTask task;

    private GoPluginApiResponse goPluginApiResponse;

    private String pluginId;

    @Test
    public void shouldGetTheTaskConfig() {
        String jsonResponse = "{" + ((("\"URL\":{\"default-value\":\"\",\"secure\":false,\"required\":true}," + "\"USER\":{\"default-value\":\"foo\",\"secure\":true,\"required\":true},") + "\"PASSWORD\":{}") + "}");
        Mockito.when(goPluginApiResponse.responseBody()).thenReturn(jsonResponse);
        TaskConfig config = task.config();
        Property url = config.get("URL");
        Assert.assertThat(url.getOption(REQUIRED), Matchers.is(true));
        Assert.assertThat(url.getOption(SECURE), Matchers.is(false));
        Property user = config.get("USER");
        Assert.assertThat(user.getOption(REQUIRED), Matchers.is(true));
        Assert.assertThat(user.getOption(SECURE), Matchers.is(true));
        Property password = config.get("PASSWORD");
        Assert.assertThat(password.getOption(REQUIRED), Matchers.is(true));
        Assert.assertThat(password.getOption(SECURE), Matchers.is(false));
        ArgumentCaptor<GoPluginApiRequest> argument = ArgumentCaptor.forClass(GoPluginApiRequest.class);
        Mockito.verify(pluginManager).submitTo(ArgumentMatchers.eq(pluginId), ArgumentMatchers.eq(PluginConstants.PLUGGABLE_TASK_EXTENSION), argument.capture());
        MatcherAssert.assertThat(argument.getValue().extension(), Matchers.is(PluginConstants.PLUGGABLE_TASK_EXTENSION));
        MatcherAssert.assertThat(argument.getValue().extensionVersion(), Matchers.is(VERSION));
        MatcherAssert.assertThat(argument.getValue().requestName(), Matchers.is(CONFIGURATION_REQUEST));
    }

    @Test
    public void shouldGetTaskView() {
        String jsonResponse = "{\"displayValue\":\"MyTaskPlugin\", \"template\":\"<html>junk</html>\"}";
        Mockito.when(goPluginApiResponse.responseBody()).thenReturn(jsonResponse);
        TaskView view = task.view();
        Assert.assertThat(view.displayValue(), Matchers.is("MyTaskPlugin"));
        Assert.assertThat(view.template(), Matchers.is("<html>junk</html>"));
        ArgumentCaptor<GoPluginApiRequest> argument = ArgumentCaptor.forClass(GoPluginApiRequest.class);
        Mockito.verify(pluginManager).submitTo(ArgumentMatchers.eq(pluginId), ArgumentMatchers.eq(PluginConstants.PLUGGABLE_TASK_EXTENSION), argument.capture());
        MatcherAssert.assertThat(argument.getValue().extension(), Matchers.is(PluginConstants.PLUGGABLE_TASK_EXTENSION));
        MatcherAssert.assertThat(argument.getValue().extensionVersion(), Matchers.is(VERSION));
        MatcherAssert.assertThat(argument.getValue().requestName(), Matchers.is(TASK_VIEW_REQUEST));
    }

    @Test
    public void shouldValidateTaskConfig() {
        String jsonResponse = "{\"errors\":{\"key1\":\"err1\",\"key2\":\"err3\"}}";
        String config = "{\"URL\":{\"secure\":false,\"value\":\"http://foo\",\"required\":true}}";
        Mockito.when(goPluginApiResponse.responseBody()).thenReturn(jsonResponse);
        TaskConfig configuration = new TaskConfig();
        final TaskConfigProperty property = new TaskConfigProperty("URL", "http://foo");
        property.with(SECURE, false);
        property.with(REQUIRED, true);
        configuration.add(property);
        ValidationResult result = task.validate(configuration);
        Assert.assertThat(result.isSuccessful(), Matchers.is(false));
        Assert.assertThat(result.getErrors().get(0).getKey(), Matchers.is("key1"));
        Assert.assertThat(result.getErrors().get(0).getMessage(), Matchers.is("err1"));
        Assert.assertThat(result.getErrors().get(1).getKey(), Matchers.is("key2"));
        Assert.assertThat(result.getErrors().get(1).getMessage(), Matchers.is("err3"));
        ArgumentCaptor<GoPluginApiRequest> argument = ArgumentCaptor.forClass(GoPluginApiRequest.class);
        Mockito.verify(pluginManager).submitTo(ArgumentMatchers.eq(pluginId), ArgumentMatchers.eq(PluginConstants.PLUGGABLE_TASK_EXTENSION), argument.capture());
        Assert.assertThat(argument.getValue().requestBody(), Matchers.is(config));
        MatcherAssert.assertThat(argument.getValue().extension(), Matchers.is(PluginConstants.PLUGGABLE_TASK_EXTENSION));
        MatcherAssert.assertThat(argument.getValue().extensionVersion(), Matchers.is(VERSION));
        MatcherAssert.assertThat(argument.getValue().requestName(), Matchers.is(VALIDATION_REQUEST));
    }

    @Test
    public void shouldGetTaskExecutor() {
        Assert.assertTrue(((task.executor()) instanceof JsonBasedTaskExecutor));
    }
}

