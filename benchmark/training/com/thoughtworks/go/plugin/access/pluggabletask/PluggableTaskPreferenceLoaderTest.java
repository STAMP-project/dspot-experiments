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


import com.thoughtworks.go.plugin.api.task.Task;
import com.thoughtworks.go.plugin.api.task.TaskConfig;
import com.thoughtworks.go.plugin.api.task.TaskView;
import com.thoughtworks.go.plugin.infra.Action;
import com.thoughtworks.go.plugin.infra.PluginManager;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class PluggableTaskPreferenceLoaderTest {
    private TaskExtension taskExtension;

    @Test
    public void shouldRegisterPluginListenerWithPluginManager() throws Exception {
        PluginManager pluginManager = Mockito.mock(PluginManager.class);
        PluggableTaskPreferenceLoader pluggableTaskPreferenceLoader = new PluggableTaskPreferenceLoader(pluginManager, taskExtension);
        Mockito.verify(pluginManager).addPluginChangeListener(pluggableTaskPreferenceLoader);
    }

    @Test
    public void shouldSetConfigForTheTaskCorrespondingToGivenPluginId() throws Exception {
        final GoPluginDescriptor descriptor = Mockito.mock(GoPluginDescriptor.class);
        String pluginId = "test-plugin-id";
        Mockito.when(descriptor.id()).thenReturn(pluginId);
        final Task task = Mockito.mock(Task.class);
        TaskConfig config = new TaskConfig();
        TaskView taskView = Mockito.mock(TaskView.class);
        Mockito.when(task.config()).thenReturn(config);
        Mockito.when(task.view()).thenReturn(taskView);
        PluginManager pluginManager = Mockito.mock(PluginManager.class);
        final TaskExtension taskExtension = Mockito.mock(TaskExtension.class);
        Mockito.when(taskExtension.canHandlePlugin(pluginId)).thenReturn(true);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                final Action<Task> action = ((Action<Task>) (invocationOnMock.getArguments()[1]));
                action.execute(task, descriptor);
                return null;
            }
        }).when(taskExtension).doOnTask(ArgumentMatchers.eq(pluginId), ArgumentMatchers.any(Action.class));
        PluggableTaskPreferenceLoader pluggableTaskPreferenceLoader = new PluggableTaskPreferenceLoader(pluginManager, taskExtension);
        pluggableTaskPreferenceLoader.pluginLoaded(descriptor);
        Assert.assertThat(PluggableTaskConfigStore.store().hasPreferenceFor(pluginId), Matchers.is(true));
        Assert.assertThat(PluggableTaskConfigStore.store().preferenceFor(pluginId), Matchers.is(new TaskPreference(task)));
        Mockito.verify(pluginManager).addPluginChangeListener(pluggableTaskPreferenceLoader);
    }

    @Test
    public void shouldRemoveConfigForTheTaskCorrespondingToGivenPluginId() throws Exception {
        final GoPluginDescriptor descriptor = Mockito.mock(GoPluginDescriptor.class);
        String pluginId = "test-plugin-id";
        Mockito.when(descriptor.id()).thenReturn(pluginId);
        final Task task = Mockito.mock(Task.class);
        TaskConfig config = new TaskConfig();
        TaskView taskView = Mockito.mock(TaskView.class);
        Mockito.when(task.config()).thenReturn(config);
        Mockito.when(task.view()).thenReturn(taskView);
        PluggableTaskConfigStore.store().setPreferenceFor(pluginId, new TaskPreference(task));
        PluginManager pluginManager = Mockito.mock(PluginManager.class);
        PluggableTaskPreferenceLoader pluggableTaskPreferenceLoader = new PluggableTaskPreferenceLoader(pluginManager, taskExtension);
        Assert.assertThat(PluggableTaskConfigStore.store().hasPreferenceFor(pluginId), Matchers.is(true));
        pluggableTaskPreferenceLoader.pluginUnLoaded(descriptor);
        Assert.assertThat(PluggableTaskConfigStore.store().hasPreferenceFor(pluginId), Matchers.is(false));
        Mockito.verify(pluginManager).addPluginChangeListener(pluggableTaskPreferenceLoader);
    }

    @Test
    public void shouldLoadPreferencesOnlyForTaskPlugins() {
        final GoPluginDescriptor descriptor = Mockito.mock(GoPluginDescriptor.class);
        String pluginId = "test-plugin-id";
        Mockito.when(descriptor.id()).thenReturn(pluginId);
        final Task task = Mockito.mock(Task.class);
        TaskConfig config = new TaskConfig();
        TaskView taskView = Mockito.mock(TaskView.class);
        Mockito.when(task.config()).thenReturn(config);
        Mockito.when(task.view()).thenReturn(taskView);
        PluginManager pluginManager = Mockito.mock(PluginManager.class);
        final TaskExtension taskExtension = Mockito.mock(TaskExtension.class);
        Mockito.when(taskExtension.canHandlePlugin(pluginId)).thenReturn(false);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                final Action<Task> action = ((Action<Task>) (invocationOnMock.getArguments()[1]));
                action.execute(task, descriptor);
                return null;
            }
        }).when(taskExtension).doOnTask(ArgumentMatchers.eq(pluginId), ArgumentMatchers.any(Action.class));
        PluggableTaskPreferenceLoader pluggableTaskPreferenceLoader = new PluggableTaskPreferenceLoader(pluginManager, taskExtension);
        pluggableTaskPreferenceLoader.pluginLoaded(descriptor);
        Assert.assertThat(PluggableTaskConfigStore.store().hasPreferenceFor(pluginId), Matchers.is(false));
        Mockito.verify(pluginManager).addPluginChangeListener(pluggableTaskPreferenceLoader);
    }
}

