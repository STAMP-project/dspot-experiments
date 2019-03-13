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
package com.thoughtworks.go.domain.builder.pluggableTask;


import DefaultGoPublisher.ERR;
import com.thoughtworks.go.config.pluggabletask.PluggableTask;
import com.thoughtworks.go.domain.RunIfConfigs;
import com.thoughtworks.go.domain.builder.Builder;
import com.thoughtworks.go.domain.config.PluginConfiguration;
import com.thoughtworks.go.plugin.access.ExtensionsRegistry;
import com.thoughtworks.go.plugin.access.pluggabletask.TaskExtension;
import com.thoughtworks.go.plugin.api.response.execution.ExecutionResult;
import com.thoughtworks.go.plugin.infra.ActionWithReturn;
import com.thoughtworks.go.plugin.infra.PluginManager;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import com.thoughtworks.go.util.ReflectionUtil;
import com.thoughtworks.go.util.command.CruiseControlException;
import com.thoughtworks.go.util.command.EnvironmentVariableContext;
import com.thoughtworks.go.work.DefaultGoPublisher;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class PluggableTaskBuilderTest {
    public static final String TEST_PLUGIN_ID = "test-plugin-id";

    @Mock
    private RunIfConfigs runIfConfigs;

    @Mock
    private Builder cancelBuilder;

    @Mock
    private PluggableTask pluggableTask;

    @Mock
    private PluginManager pluginManager;

    @Mock
    private ExtensionsRegistry extensionsRegistry;

    @Mock
    private EnvironmentVariableContext variableContext;

    @Mock
    private DefaultGoPublisher goPublisher;

    @Mock
    private GoPluginDescriptor pluginDescriptor;

    private TaskExtension taskExtension;

    @Test
    public void shouldInvokeTheTaskExecutorOfThePlugin() throws Exception {
        final int[] executeTaskCalled = new int[1];
        PluggableTaskBuilder taskBuilder = new PluggableTaskBuilder(runIfConfigs, cancelBuilder, pluggableTask, PluggableTaskBuilderTest.TEST_PLUGIN_ID, "test-directory") {
            @Override
            protected ExecutionResult executeTask(Task task, DefaultGoPublisher publisher, EnvironmentVariableContext environmentVariableContext, String consoleLogCharset) {
                (executeTaskCalled[0])++;
                return ExecutionResult.success("Test succeeded");
            }
        };
        taskBuilder.build(goPublisher, variableContext, taskExtension, null, null, "utf-8");
        Assert.assertThat(executeTaskCalled[0], is(1));
    }

    @Test
    public void shouldBuildExecutorConfigPlusExecutionContextAndInvokeTheTaskExecutorWithIt() throws Exception {
        Task task = Mockito.mock(Task.class);
        TaskConfig defaultTaskConfig = Mockito.mock(TaskConfig.class);
        Mockito.when(task.config()).thenReturn(defaultTaskConfig);
        final TaskConfig executorTaskConfig = Mockito.mock(TaskConfig.class);
        final TaskExecutionContext taskExecutionContext = Mockito.mock(TaskExecutionContext.class);
        PluggableTaskBuilder taskBuilder = new PluggableTaskBuilder(runIfConfigs, cancelBuilder, pluggableTask, PluggableTaskBuilderTest.TEST_PLUGIN_ID, "test-directory") {
            @Override
            protected TaskConfig buildTaskConfig(TaskConfig config) {
                return executorTaskConfig;
            }

            @Override
            protected TaskExecutionContext buildTaskContext(DefaultGoPublisher publisher, EnvironmentVariableContext environmentVariableContext, String consoleLogCharset) {
                return taskExecutionContext;
            }
        };
        TaskExecutor taskExecutor = Mockito.mock(TaskExecutor.class);
        Mockito.when(taskExecutor.execute(executorTaskConfig, taskExecutionContext)).thenReturn(new ExecutionResult());
        Mockito.when(task.executor()).thenReturn(taskExecutor);
        taskBuilder.executeTask(task, null, null, "utf-8");
        Mockito.verify(task).config();
        Mockito.verify(task).executor();
        Mockito.verify(taskExecutor).execute(executorTaskConfig, taskExecutionContext);
        Assert.assertThat(ReflectionUtil.getStaticField(JobConsoleLogger.class, "context"), is(not(nullValue())));
    }

    @Test
    public void shouldReturnDefaultValueInExecConfigWhenNoConfigValueIsProvided() throws Exception {
        Map<String, Map<String, String>> configMap = new HashMap<>();
        PluggableTask task = Mockito.mock(PluggableTask.class);
        Mockito.when(task.getPluginConfiguration()).thenReturn(new PluginConfiguration());
        Mockito.when(task.configAsMap()).thenReturn(configMap);
        PluggableTaskBuilder taskBuilder = new PluggableTaskBuilder(runIfConfigs, cancelBuilder, task, PluggableTaskBuilderTest.TEST_PLUGIN_ID, "test-directory");
        TaskConfig defaultTaskConfig = new TaskConfig();
        String propertyName = "URL";
        String defaultValue = "ABC.TXT";
        defaultTaskConfig.addProperty(propertyName).withDefault(defaultValue);
        TaskConfig config = taskBuilder.buildTaskConfig(defaultTaskConfig);
        Assert.assertThat(config.getValue(propertyName), is(defaultValue));
    }

    @Test
    public void shouldReturnDefaultValueInExecConfigWhenConfigValueIsNull() throws Exception {
        TaskConfig defaultTaskConfig = new TaskConfig();
        String propertyName = "URL";
        String defaultValue = "ABC.TXT";
        Map<String, Map<String, String>> configMap = new HashMap<>();
        configMap.put(propertyName, null);
        PluggableTask task = Mockito.mock(PluggableTask.class);
        Mockito.when(task.getPluginConfiguration()).thenReturn(new PluginConfiguration());
        Mockito.when(task.configAsMap()).thenReturn(configMap);
        PluggableTaskBuilder taskBuilder = new PluggableTaskBuilder(runIfConfigs, cancelBuilder, task, PluggableTaskBuilderTest.TEST_PLUGIN_ID, "test-directory");
        defaultTaskConfig.addProperty(propertyName).withDefault(defaultValue);
        TaskConfig config = taskBuilder.buildTaskConfig(defaultTaskConfig);
        Assert.assertThat(config.getValue(propertyName), is(defaultValue));
    }

    @Test
    public void shouldReturnDefaultValueInExecConfigWhenConfigValueIsEmptyString() throws Exception {
        TaskConfig defaultTaskConfig = new TaskConfig();
        String propertyName = "URL";
        String defaultValue = "ABC.TXT";
        Map<String, Map<String, String>> configMap = new HashMap<>();
        HashMap<String, String> configValue = new HashMap<>();
        configValue.put("value", "");
        configMap.put(propertyName, configValue);
        PluggableTask task = Mockito.mock(PluggableTask.class);
        Mockito.when(task.getPluginConfiguration()).thenReturn(new PluginConfiguration());
        Mockito.when(task.configAsMap()).thenReturn(configMap);
        PluggableTaskBuilder taskBuilder = new PluggableTaskBuilder(runIfConfigs, cancelBuilder, task, PluggableTaskBuilderTest.TEST_PLUGIN_ID, "test-directory");
        defaultTaskConfig.addProperty(propertyName).withDefault(defaultValue);
        TaskConfig config = taskBuilder.buildTaskConfig(defaultTaskConfig);
        Assert.assertThat(config.getValue(propertyName), is(defaultValue));
    }

    @Test
    public void shouldReturnConfigValueInExecConfig() throws Exception {
        TaskConfig defaultTaskConfig = new TaskConfig();
        String propertyName = "URL";
        String defaultValue = "ABC.TXT";
        HashMap<String, String> configValue = new HashMap<>();
        configValue.put("value", "XYZ.TXT");
        Map<String, Map<String, String>> configMap = new HashMap<>();
        configMap.put(propertyName, configValue);
        PluggableTask task = Mockito.mock(PluggableTask.class);
        Mockito.when(task.getPluginConfiguration()).thenReturn(new PluginConfiguration());
        Mockito.when(task.configAsMap()).thenReturn(configMap);
        PluggableTaskBuilder taskBuilder = new PluggableTaskBuilder(runIfConfigs, cancelBuilder, task, PluggableTaskBuilderTest.TEST_PLUGIN_ID, "test-directory");
        defaultTaskConfig.addProperty(propertyName).withDefault(defaultValue);
        TaskConfig config = taskBuilder.buildTaskConfig(defaultTaskConfig);
        Assert.assertThat(config.getValue(propertyName), is(configValue.get("value")));
    }

    @Test
    public void shouldReturnPluggableTaskContext() throws Exception {
        PluggableTask task = Mockito.mock(PluggableTask.class);
        Mockito.when(task.getPluginConfiguration()).thenReturn(new PluginConfiguration());
        String workingDir = "test-directory";
        PluggableTaskBuilder taskBuilder = new PluggableTaskBuilder(runIfConfigs, cancelBuilder, task, PluggableTaskBuilderTest.TEST_PLUGIN_ID, workingDir);
        TaskExecutionContext taskExecutionContext = taskBuilder.buildTaskContext(goPublisher, variableContext, "utf-8");
        Assert.assertThat((taskExecutionContext instanceof PluggableTaskContext), is(true));
        Assert.assertThat(taskExecutionContext.workingDir(), is(workingDir));
    }

    @Test
    public void shouldPublishErrorMessageIfPluginThrowsAnException() throws CruiseControlException {
        PluggableTask task = Mockito.mock(PluggableTask.class);
        Mockito.when(task.getPluginConfiguration()).thenReturn(new PluginConfiguration());
        PluggableTaskBuilder taskBuilder = new PluggableTaskBuilder(runIfConfigs, cancelBuilder, pluggableTask, PluggableTaskBuilderTest.TEST_PLUGIN_ID, "test-directory") {
            @Override
            protected ExecutionResult executeTask(Task task, DefaultGoPublisher publisher, EnvironmentVariableContext environmentVariableContext, String consoleLogCharset) {
                throw new RuntimeException("err");
            }
        };
        try {
            taskBuilder.build(goPublisher, variableContext, taskExtension, null, null, "utf-8");
            Assert.fail("expected exception to be thrown");
        } catch (Exception e) {
            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            Mockito.verify(goPublisher).taggedConsumeLine(ArgumentMatchers.eq(ERR), captor.capture());
            String error = "Error: err";
            Assert.assertThat(captor.getValue(), is(error));
            Assert.assertThat(e.getMessage(), is(new RuntimeException("err").toString()));
        }
    }

    @Test
    public void shouldPublishErrorMessageIfPluginReturnsAFailureResponse() throws CruiseControlException {
        PluggableTask task = Mockito.mock(PluggableTask.class);
        Mockito.when(task.getPluginConfiguration()).thenReturn(new PluginConfiguration());
        PluggableTaskBuilder taskBuilder = new PluggableTaskBuilder(runIfConfigs, cancelBuilder, pluggableTask, PluggableTaskBuilderTest.TEST_PLUGIN_ID, "test-directory") {
            @Override
            protected ExecutionResult executeTask(Task task, DefaultGoPublisher publisher, EnvironmentVariableContext environmentVariableContext, String consoleLogCharset) {
                return ExecutionResult.failure("err");
            }
        };
        try {
            taskBuilder.build(goPublisher, variableContext, taskExtension, null, null, "utf-8");
            Assert.fail("expected exception to be thrown");
        } catch (Exception e) {
            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            Mockito.verify(goPublisher).taggedConsumeLine(ArgumentMatchers.eq(ERR), captor.capture());
            Assert.assertThat(captor.getValue(), is("err"));
            Assert.assertThat(e.getMessage(), is("err"));
        }
    }

    @Test
    public void shouldRegisterTaskConfigDuringExecutionAndUnregisterOnSuccessfulCompletion() throws CruiseControlException {
        final PluggableTaskBuilder builder = Mockito.spy(new PluggableTaskBuilder(runIfConfigs, cancelBuilder, pluggableTask, "", ""));
        taskExtension = Mockito.mock(TaskExtension.class);
        Mockito.when(taskExtension.execute(ArgumentMatchers.eq(PluggableTaskBuilderTest.TEST_PLUGIN_ID), ArgumentMatchers.any(ActionWithReturn.class))).thenReturn(ExecutionResult.success("yay"));
        builder.build(goPublisher, variableContext, taskExtension, null, null, "utf-8");
        Assert.assertThat(ReflectionUtil.getStaticField(JobConsoleLogger.class, "context"), is(nullValue()));
    }

    @Test
    public void shouldUnsetTaskExecutionContextFromJobConsoleLoggerWhenTaskExecutionFails() throws CruiseControlException {
        final PluggableTaskBuilder builder = Mockito.spy(new PluggableTaskBuilder(runIfConfigs, cancelBuilder, pluggableTask, "", ""));
        taskExtension = Mockito.mock(TaskExtension.class);
        Mockito.when(taskExtension.execute(ArgumentMatchers.eq(PluggableTaskBuilderTest.TEST_PLUGIN_ID), ArgumentMatchers.any(ActionWithReturn.class))).thenReturn(ExecutionResult.failure("oh no"));
        try {
            builder.build(goPublisher, variableContext, taskExtension, null, null, "utf-8");
            Assert.fail("should throw exception");
        } catch (Exception e) {
            Assert.assertThat(ReflectionUtil.getStaticField(JobConsoleLogger.class, "context"), is(nullValue()));
        }
    }

    @Test
    public void shouldUnsetTaskExecutionContextFromJobConsoleLoggerWhenTaskExecutionThrowsException() throws CruiseControlException {
        final PluggableTaskBuilder builder = Mockito.spy(new PluggableTaskBuilder(runIfConfigs, cancelBuilder, pluggableTask, "", ""));
        taskExtension = Mockito.mock(TaskExtension.class);
        Mockito.when(taskExtension.execute(ArgumentMatchers.eq(PluggableTaskBuilderTest.TEST_PLUGIN_ID), ArgumentMatchers.any(ActionWithReturn.class))).thenThrow(new RuntimeException("something"));
        try {
            builder.build(goPublisher, variableContext, taskExtension, null, null, "utf-8");
            Assert.fail("should throw exception");
        } catch (Exception e) {
            Assert.assertThat(ReflectionUtil.getStaticField(JobConsoleLogger.class, "context"), is(nullValue()));
        }
    }
}

