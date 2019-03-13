/**
 * Copyright 2017 ThoughtWorks, Inc.
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
package com.thoughtworks.go.plugins.presentation;


import com.google.gson.Gson;
import com.thoughtworks.go.config.pluggabletask.PluggableTask;
import com.thoughtworks.go.domain.config.Configuration;
import com.thoughtworks.go.domain.config.ConfigurationProperty;
import com.thoughtworks.go.domain.config.PluginConfiguration;
import com.thoughtworks.go.plugin.access.pluggabletask.TaskPreference;
import com.thoughtworks.go.presentation.MissingPluggableTaskViewModel;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PluggableTaskViewModelFactoryTest {
    private TaskPreference taskPreference;

    @Test
    public void typeForDisplayAndTemplateOfViewModelShouldBeGotFromThePlugin() throws Exception {
        PluggableTask pluggableTask = new PluggableTask(new PluginConfiguration("plugin-1", "2"), new Configuration());
        PluggableTaskViewModelFactory factory = new PluggableTaskViewModelFactory();
        PluggableViewModel<PluggableTask> viewModel = factory.viewModelFor(pluggableTask, "new");
        Assert.assertThat(viewModel.getTypeForDisplay(), Matchers.is("First plugin"));
        Assert.assertThat(viewModel.getParameters().get("template"), Matchers.is("<input type='text' ng-model='abc'></input>"));
    }

    @Test
    public void templateShouldBeLoadedFromClasspathWithClasspathPrefix() throws Exception {
        PluggableViewModel<PluggableTask> viewModel = getModelWithTaskTemplateAt("/com/thoughtworks/go/plugins/presentation/test-template.html");
        Assert.assertThat(viewModel.getParameters().get("template"), Matchers.is("<html>my-template</html>"));
    }

    @Test
    public void shouldReturnErrorMessageIfTemplateIsMissingFromPlugin() {
        PluggableViewModel<PluggableTask> viewModel = getModelWithTaskTemplateAt("/test-template-missing.html");
        Assert.assertThat(viewModel.getParameters().get("template"), Matchers.is("Template \"/test-template-missing.html\" is missing."));
    }

    @Test
    public void shouldProvideATemplateWithAnErrorMessageWhenTemplateProvidedIsNull() throws Exception {
        PluggableViewModel<PluggableTask> viewModel = getModelWithTaskTemplateHavingValue(null);
        Assert.assertThat(viewModel.getParameters().get("template"), Matchers.is("View template provided by plugin is null."));
    }

    @Test
    public void shouldProvideNoTemplateWhenTemplateProvidedIsEmpty() throws Exception {
        PluggableViewModel<PluggableTask> viewModel = getModelWithTaskTemplateHavingValue("");
        Assert.assertThat(viewModel.getParameters().get("template"), Matchers.is(""));
    }

    @Test
    public void dataForViewShouldBeGotFromTheTaskInJSONFormat() throws Exception {
        Configuration configuration = new Configuration(create("key1", false, "value1"), create("KEY2", false, "value2"));
        PluggableTask taskConfig = new PluggableTask(new PluginConfiguration("plugin-1", "2"), configuration);
        PluggableTaskViewModelFactory factory = new PluggableTaskViewModelFactory();
        PluggableViewModel<PluggableTask> viewModel = factory.viewModelFor(taskConfig, "new");
        String actualData = ((String) (viewModel.getParameters().get("data")));
        Gson gson = new Gson();
        Map actual = gson.fromJson(actualData, Map.class);
        Map expected = gson.fromJson("{\"KEY2\":{\"value\":\"value2\"},\"key1\":{\"value\":\"value1\"}}", Map.class);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void dataForViewShouldIncludeErrorsIfAny() throws Exception {
        ConfigurationProperty property1 = create("key1", false, "value1");
        property1.addError("key1", "error msg");
        ConfigurationProperty property2 = create("KEY2", false, "value2");
        Configuration configuration = new Configuration(property1, property2);
        PluggableTask taskConfig = new PluggableTask(new PluginConfiguration("plugin-1", "2"), configuration);
        PluggableTaskViewModelFactory factory = new PluggableTaskViewModelFactory();
        PluggableViewModel<PluggableTask> viewModel = factory.viewModelFor(taskConfig, "new");
        String actualData = ((String) (viewModel.getParameters().get("data")));
        Gson gson = new Gson();
        Map actual = gson.fromJson(actualData, Map.class);
        Map expected = gson.fromJson("{\"KEY2\":{\"value\": \"value2\"},\"key1\":{\"value\" : \"value1\", \"errors\" : \"error msg\"}}", Map.class);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldGivePluggableViewModelWithAllTheViewInformationForActionNew() throws Exception {
        assertPluggableViewModel("new", "admin/tasks/pluggable_task/new");
    }

    @Test
    public void shouldGivePluggableViewModelWithAllTheViewInformationForActionEdit() throws Exception {
        assertPluggableViewModel("edit", "admin/tasks/pluggable_task/edit");
    }

    @Test
    public void shouldGivePluggableViewModelWithAllTheViewInformationForActionListEntry() throws Exception {
        assertPluggableViewModel("list-entry", "admin/tasks/pluggable_task/_list_entry.html");
    }

    @Test
    public void shouldReturnMissingPluginTaskViewIfPluginIsMissing() {
        String pluginId = "pluginId";
        PluggableTaskViewModelFactory factory = new PluggableTaskViewModelFactory();
        PluggableViewModel<PluggableTask> viewModel = factory.viewModelFor(new PluggableTask(new PluginConfiguration(pluginId, "1"), new Configuration()), "edit");
        Assert.assertThat(viewModel.getParameters().get("template"), Matchers.is(String.format("Associated plugin '%s' not found. Please contact the Go admin to install the plugin.", pluginId)));
        Assert.assertThat(viewModel.getTypeForDisplay(), Matchers.is(pluginId));
        Assert.assertThat((viewModel instanceof MissingPluggableTaskViewModel), Matchers.is(true));
    }
}

