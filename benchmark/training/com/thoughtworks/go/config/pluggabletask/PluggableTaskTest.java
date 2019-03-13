/**
 * Copyright 2015 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.config.pluggabletask;


import PluggableTask.VALUE_KEY;
import Property.SECURE;
import com.thoughtworks.go.config.AntTask;
import com.thoughtworks.go.config.OnCancelConfig;
import com.thoughtworks.go.domain.TaskProperty;
import com.thoughtworks.go.domain.config.Configuration;
import com.thoughtworks.go.domain.config.ConfigurationKey;
import com.thoughtworks.go.domain.config.ConfigurationProperty;
import com.thoughtworks.go.domain.config.ConfigurationValue;
import com.thoughtworks.go.domain.config.PluginConfiguration;
import com.thoughtworks.go.domain.packagerepository.ConfigurationPropertyMother;
import com.thoughtworks.go.plugin.access.pluggabletask.PluggableTaskConfigStore;
import com.thoughtworks.go.plugin.access.pluggabletask.TaskPreference;
import com.thoughtworks.go.plugin.api.config.Property;
import com.thoughtworks.go.plugin.api.task.Task;
import com.thoughtworks.go.plugin.api.task.TaskConfig;
import com.thoughtworks.go.plugin.api.task.TaskConfigProperty;
import com.thoughtworks.go.plugin.api.task.TaskView;
import com.thoughtworks.go.plugin.api.task.com.thoughtworks.go.domain.Task;
import com.thoughtworks.go.security.GoCipher;
import com.thoughtworks.go.util.DataStructureUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class PluggableTaskTest {
    @Test
    public void testConfigAsMap() throws Exception {
        PluginConfiguration pluginConfiguration = new PluginConfiguration("test-plugin-id", "13.4");
        GoCipher cipher = new GoCipher();
        List<String> keys = Arrays.asList("Avengers 1", "Avengers 2", "Avengers 3", "Avengers 4");
        List<String> values = Arrays.asList("Iron man", "Hulk", "Thor", "Captain America");
        Configuration configuration = new Configuration(new ConfigurationProperty(new ConfigurationKey(keys.get(0)), new ConfigurationValue(values.get(0))), new ConfigurationProperty(new ConfigurationKey(keys.get(1)), new ConfigurationValue(values.get(1))), new ConfigurationProperty(new ConfigurationKey(keys.get(2)), new ConfigurationValue(values.get(2))), new ConfigurationProperty(new ConfigurationKey(keys.get(3)), null, new com.thoughtworks.go.domain.config.EncryptedConfigurationValue(cipher.encrypt(values.get(3))), cipher));
        PluggableTask task = new PluggableTask(pluginConfiguration, configuration);
        Map<String, Map<String, String>> configMap = task.configAsMap();
        Assert.assertThat(configMap.keySet().size(), Matchers.is(keys.size()));
        Assert.assertThat(configMap.values().size(), Matchers.is(values.size()));
        Assert.assertThat(configMap.keySet().containsAll(keys), Matchers.is(true));
        for (int i = 0; i < (keys.size()); i++) {
            Assert.assertThat(configMap.get(keys.get(i)).get(VALUE_KEY), Matchers.is(values.get(i)));
        }
    }

    @Test
    public void shouldReturnTrueWhenPluginConfigurationForTwoPluggableTasksIsExactlyTheSame() {
        PluginConfiguration pluginConfiguration = new PluginConfiguration("test-plugin-1", "1.0");
        PluggableTask pluggableTask1 = new PluggableTask(pluginConfiguration, new Configuration());
        PluggableTask pluggableTask2 = new PluggableTask(pluginConfiguration, new Configuration());
        Assert.assertTrue(pluggableTask1.hasSameTypeAs(pluggableTask2));
    }

    @Test
    public void shouldReturnFalseWhenPluginConfigurationForTwoPluggableTasksIsDifferent() {
        PluginConfiguration pluginConfiguration1 = new PluginConfiguration("test-plugin-1", "1.0");
        PluginConfiguration pluginConfiguration2 = new PluginConfiguration("test-plugin-2", "1.0");
        PluggableTask pluggableTask1 = new PluggableTask(pluginConfiguration1, new Configuration());
        PluggableTask pluggableTask2 = new PluggableTask(pluginConfiguration2, new Configuration());
        Assert.assertFalse(pluggableTask1.hasSameTypeAs(pluggableTask2));
    }

    @Test
    public void shouldReturnFalseWhenPluggableTaskIsComparedWithAnyOtherTask() {
        PluginConfiguration pluginConfiguration = new PluginConfiguration("test-plugin-1", "1.0");
        PluggableTask pluggableTask = new PluggableTask(pluginConfiguration, new Configuration());
        AntTask antTask = new AntTask();
        Assert.assertFalse(pluggableTask.hasSameTypeAs(antTask));
    }

    @Test
    public void taskTypeShouldBeSanitizedToHaveNoSpecialCharacters() throws Exception {
        Assert.assertThat(getTaskType(), Matchers.is("pluggable_task_abc_def"));
        Assert.assertThat(getTaskType(), Matchers.is("pluggable_task_abc_def"));
        Assert.assertThat(getTaskType(), Matchers.is("pluggable_task_abcdef"));
        Assert.assertThat(getTaskType(), Matchers.is("pluggable_task_abc_def"));
        Assert.assertThat(getTaskType(), Matchers.is("pluggable_task_abc___def"));
        Assert.assertThat(getTaskType(), Matchers.is("pluggable_task_Abc_dEF"));
        Assert.assertThat(getTaskType(), Matchers.is("pluggable_task_1234567890_ABCDEF"));
    }

    @Test
    public void shouldPopulatePropertiesForDisplay() throws Exception {
        Configuration configuration = new Configuration(ConfigurationPropertyMother.create("KEY1", false, "value1"), ConfigurationPropertyMother.create("Key2", false, "value2"), ConfigurationPropertyMother.create("key3", true, "encryptedValue1"));
        PluggableTask task = new PluggableTask(new PluginConfiguration("abc.def", "1"), configuration);
        List<TaskProperty> propertiesForDisplay = task.getPropertiesForDisplay();
        Assert.assertThat(propertiesForDisplay.size(), Matchers.is(3));
        assertProperty(propertiesForDisplay.get(0), "KEY1", "value1", "key1");
        assertProperty(propertiesForDisplay.get(1), "Key2", "value2", "key2");
        assertProperty(propertiesForDisplay.get(2), "key3", "****", "key3");
    }

    @Test
    public void shouldPopulatePropertiesForDisplayRetainingOrderAndDisplayNameIfConfigured() throws Exception {
        Task taskDetails = Mockito.mock(Task.class);
        TaskConfig taskConfig = new TaskConfig();
        addProperty(taskConfig, "KEY2", "Key 2", 1);
        addProperty(taskConfig, "KEY1", "Key 1", 0);
        addProperty(taskConfig, "KEY3", "Key 3", 2);
        Mockito.when(taskDetails.config()).thenReturn(taskConfig);
        Mockito.when(taskDetails.view()).thenReturn(Mockito.mock(TaskView.class));
        String pluginId = "plugin_with_all_details";
        PluggableTaskConfigStore.store().setPreferenceFor(pluginId, new TaskPreference(taskDetails));
        Configuration configuration = new Configuration(ConfigurationPropertyMother.create("KEY3", true, "encryptedValue1"), ConfigurationPropertyMother.create("KEY1", false, "value1"), ConfigurationPropertyMother.create("KEY2", false, "value2"));
        PluggableTask task = new PluggableTask(new PluginConfiguration(pluginId, "1"), configuration);
        List<TaskProperty> propertiesForDisplay = task.getPropertiesForDisplay();
        Assert.assertThat(propertiesForDisplay.size(), Matchers.is(3));
        assertProperty(propertiesForDisplay.get(0), "Key 1", "value1", "key1");
        assertProperty(propertiesForDisplay.get(1), "Key 2", "value2", "key2");
        assertProperty(propertiesForDisplay.get(2), "Key 3", "****", "key3");
    }

    @Test
    public void shouldGetOnlyConfiguredPropertiesIfACertainPropertyDefinedByPluginIsNotConfiguredByUser() throws Exception {
        Task taskDetails = Mockito.mock(Task.class);
        TaskConfig taskConfig = new TaskConfig();
        addProperty(taskConfig, "KEY2", "Key 2", 1);
        addProperty(taskConfig, "KEY1", "Key 1", 0);
        addProperty(taskConfig, "KEY3", "Key 3", 2);
        Mockito.when(taskDetails.config()).thenReturn(taskConfig);
        Mockito.when(taskDetails.view()).thenReturn(Mockito.mock(TaskView.class));
        String pluginId = "plugin_with_all_details";
        PluggableTaskConfigStore.store().setPreferenceFor(pluginId, new TaskPreference(taskDetails));
        Configuration configuration = new Configuration(ConfigurationPropertyMother.create("KEY1", false, "value1"), ConfigurationPropertyMother.create("KEY2", false, "value2"));
        PluggableTask task = new PluggableTask(new PluginConfiguration(pluginId, "1"), configuration);
        List<TaskProperty> propertiesForDisplay = task.getPropertiesForDisplay();
        Assert.assertThat(propertiesForDisplay.size(), Matchers.is(2));
        assertProperty(propertiesForDisplay.get(0), "Key 1", "value1", "key1");
        assertProperty(propertiesForDisplay.get(1), "Key 2", "value2", "key2");
    }

    @Test
    public void shouldPopulateItselfFromConfigAttributesMap() throws Exception {
        TaskPreference taskPreference = Mockito.mock(TaskPreference.class);
        Configuration configuration = new Configuration(ConfigurationPropertyMother.create("KEY1"), ConfigurationPropertyMother.create("Key2"));
        PluggableTaskConfigStore.store().setPreferenceFor("abc.def", taskPreference);
        PluggableTask task = new PluggableTask(new PluginConfiguration("abc.def", "1"), configuration);
        Map<String, String> attributeMap = DataStructureUtils.m("KEY1", "value1", "Key2", "value2");
        TaskConfig taskConfig = new TaskConfig();
        TaskProperty property1 = new TaskProperty("KEY1", "value1");
        TaskProperty property2 = new TaskProperty("Key2", "value2");
        taskConfig.addProperty(property1.getName());
        taskConfig.addProperty(property2.getName());
        Mockito.when(taskPreference.getConfig()).thenReturn(taskConfig);
        task.setTaskConfigAttributes(attributeMap);
        Assert.assertThat(task.configAsMap().get("KEY1").get(VALUE_KEY), Matchers.is("value1"));
        Assert.assertThat(task.configAsMap().get("Key2").get(VALUE_KEY), Matchers.is("value2"));
    }

    @Test
    public void shouldHandleSecureConfigurations() throws Exception {
        TaskPreference taskPreference = Mockito.mock(TaskPreference.class);
        Configuration configuration = new Configuration();
        PluggableTaskConfigStore.store().setPreferenceFor("abc.def", taskPreference);
        PluggableTask task = new PluggableTask(new PluginConfiguration("abc.def", "1"), configuration);
        Map<String, String> attributeMap = DataStructureUtils.m("KEY1", "value1");
        TaskConfig taskConfig = new TaskConfig();
        taskConfig.addProperty("KEY1").with(SECURE, true);
        Mockito.when(taskPreference.getConfig()).thenReturn(taskConfig);
        task.setTaskConfigAttributes(attributeMap);
        Assert.assertThat(task.getConfiguration().size(), Matchers.is(1));
        Assert.assertTrue(task.getConfiguration().first().isSecure());
        Assert.assertThat(task.getConfiguration().first().getValue(), Matchers.is("value1"));
    }

    @Test
    public void shouldNotOverwriteValuesIfTheyAreNotAvailableInConfigAttributesMap() throws Exception {
        TaskPreference taskPreference = Mockito.mock(TaskPreference.class);
        Configuration configuration = new Configuration(ConfigurationPropertyMother.create("KEY1"), ConfigurationPropertyMother.create("Key2"));
        PluggableTaskConfigStore.store().setPreferenceFor("abc.def", taskPreference);
        PluggableTask task = new PluggableTask(new PluginConfiguration("abc.def", "1"), configuration);
        Map<String, String> attributeMap = DataStructureUtils.m("KEY1", "value1");
        TaskConfig taskConfig = new TaskConfig();
        TaskProperty property1 = new TaskProperty("KEY1", "value1");
        TaskProperty property2 = new TaskProperty("Key2", null);
        taskConfig.addProperty(property1.getName());
        taskConfig.addProperty(property2.getName());
        Mockito.when(taskPreference.getConfig()).thenReturn(taskConfig);
        task.setTaskConfigAttributes(attributeMap);
        Assert.assertThat(task.configAsMap().get("KEY1").get(VALUE_KEY), Matchers.is("value1"));
        Assert.assertThat(task.configAsMap().get("Key2").get(VALUE_KEY), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldIgnoreKeysPresentInConfigAttributesMapButNotPresentInConfigStore() throws Exception {
        TaskPreference taskPreference = Mockito.mock(TaskPreference.class);
        Configuration configuration = new Configuration(ConfigurationPropertyMother.create("KEY1"));
        PluggableTaskConfigStore.store().setPreferenceFor("abc.def", taskPreference);
        PluggableTask task = new PluggableTask(new PluginConfiguration("abc.def", "1"), configuration);
        Map<String, String> attributeMap = DataStructureUtils.m("KEY1", "value1", "Key2", "value2");
        TaskConfig taskConfig = new TaskConfig();
        TaskProperty property1 = new TaskProperty("KEY1", "value1");
        taskConfig.addProperty(property1.getName());
        Mockito.when(taskPreference.getConfig()).thenReturn(taskConfig);
        task.setTaskConfigAttributes(attributeMap);
        Assert.assertThat(task.configAsMap().get("KEY1").get(VALUE_KEY), Matchers.is("value1"));
        Assert.assertFalse(task.configAsMap().containsKey("Key2"));
    }

    @Test
    public void shouldAddPropertyComingFromAttributesMapIfPresentInConfigStoreEvenIfItISNotPresentInCurrentConfiguration() throws Exception {
        TaskPreference taskPreference = Mockito.mock(TaskPreference.class);
        Configuration configuration = new Configuration(ConfigurationPropertyMother.create("KEY1"));
        PluggableTaskConfigStore.store().setPreferenceFor("abc.def", taskPreference);
        PluggableTask task = new PluggableTask(new PluginConfiguration("abc.def", "1"), configuration);
        Map<String, String> attributeMap = DataStructureUtils.m("KEY1", "value1", "Key2", "value2");
        TaskConfig taskConfig = new TaskConfig();
        TaskProperty property1 = new TaskProperty("KEY1", "value1");
        TaskProperty property2 = new TaskProperty("Key2", "value2");
        taskConfig.addProperty(property1.getName());
        taskConfig.addProperty(property2.getName());
        Mockito.when(taskPreference.getConfig()).thenReturn(taskConfig);
        task.setTaskConfigAttributes(attributeMap);
        Assert.assertThat(task.configAsMap().get("KEY1").get(VALUE_KEY), Matchers.is("value1"));
        Assert.assertThat(task.configAsMap().get("Key2").get(VALUE_KEY), Matchers.is("value2"));
    }

    @Test
    public void shouldAddConfigurationProperties() {
        List<ConfigurationProperty> configurationProperties = Arrays.asList(ConfigurationPropertyMother.create("key", "value", "encValue"), new ConfigurationProperty());
        PluginConfiguration pluginConfiguration = new PluginConfiguration("github.pr", "1.1");
        TaskPreference taskPreference = Mockito.mock(TaskPreference.class);
        TaskConfig taskConfig = new TaskConfig();
        Configuration configuration = new Configuration();
        Property property = new Property("key");
        property.with(SECURE, false);
        PluggableTaskConfigStore.store().setPreferenceFor(pluginConfiguration.getId(), taskPreference);
        TaskConfigProperty taskConfigProperty = taskConfig.addProperty("key");
        Mockito.when(taskPreference.getConfig()).thenReturn(taskConfig);
        PluggableTask pluggableTask = new PluggableTask(pluginConfiguration, configuration);
        pluggableTask.addConfigurations(configurationProperties);
        Assert.assertThat(configuration.size(), Matchers.is(2));
    }

    @Test
    public void shouldAddConfigurationPropertiesForAInvalidPlugin() {
        List<ConfigurationProperty> configurationProperties = Arrays.asList(ConfigurationPropertyMother.create("key", "value", "encValue"));
        PluginConfiguration pluginConfiguration = new PluginConfiguration("does_not_exist", "1.1");
        Configuration configuration = new Configuration();
        PluggableTask pluggableTask = new PluggableTask(pluginConfiguration, configuration);
        pluggableTask.addConfigurations(configurationProperties);
        Assert.assertThat(configuration.size(), Matchers.is(1));
    }

    @Test
    public void isValidShouldVerifyIfPluginIdIsValid() {
        PluginConfiguration pluginConfiguration = new PluginConfiguration("does_not_exist", "1.1");
        Configuration configuration = new Configuration();
        PluggableTask pluggableTask = new PluggableTask(pluginConfiguration, configuration);
        pluggableTask.isValid();
        Assert.assertThat(pluggableTask.errors().get("pluggable_task").get(0), Matchers.is("Could not find plugin for given pluggable id:[does_not_exist]."));
    }

    @Test
    public void isValidShouldVerifyForValidConfigurationProperties() {
        PluginConfiguration pluginConfiguration = new PluginConfiguration("github.pr", "1.1");
        Configuration configuration = Mockito.mock(Configuration.class);
        PluggableTaskConfigStore.store().setPreferenceFor(pluginConfiguration.getId(), Mockito.mock(TaskPreference.class));
        Mockito.when(configuration.hasErrors()).thenReturn(true);
        PluggableTask pluggableTask = new PluggableTask(pluginConfiguration, configuration);
        Assert.assertFalse(pluggableTask.isValid());
        Mockito.verify(configuration).validateTree();
        Mockito.verify(configuration).hasErrors();
    }

    @Test
    public void shouldBeAbleToGetTaskConfigRepresentation() {
        List<ConfigurationProperty> configurationProperties = Arrays.asList(ConfigurationPropertyMother.create("source", false, "src_dir"), ConfigurationPropertyMother.create("destination", false, "des_dir"));
        Configuration configuration = new Configuration();
        configuration.addAll(configurationProperties);
        PluginConfiguration pluginConfiguration = new PluginConfiguration("plugin_id", "version");
        PluggableTask pluggableTask = new PluggableTask(pluginConfiguration, configuration);
        TaskConfig taskConfig = pluggableTask.toTaskConfig();
        Assert.assertThat(taskConfig.size(), Matchers.is(2));
        Assert.assertThat(taskConfig.get("source").getValue(), Matchers.is("src_dir"));
        Assert.assertThat(taskConfig.get("destination").getValue(), Matchers.is("des_dir"));
    }

    @Test
    public void validateTreeShouldVerifyIfOnCancelTasksHasErrors() {
        PluggableTask pluggableTask = new PluggableTask(new PluginConfiguration(), new Configuration());
        pluggableTask.onCancelConfig = Mockito.mock(OnCancelConfig.class);
        com.thoughtworks.go.domain.Task cancelTask = Mockito.mock(Task.class);
        Mockito.when(pluggableTask.onCancelConfig.getTask()).thenReturn(cancelTask);
        Mockito.when(cancelTask.hasCancelTask()).thenReturn(false);
        Mockito.when(pluggableTask.onCancelConfig.validateTree(null)).thenReturn(false);
        Assert.assertFalse(pluggableTask.validateTree(null));
    }

    @Test
    public void validateTreeShouldVerifyIfCancelTasksHasNestedCancelTask() {
        PluggableTask pluggableTask = new PluggableTask(new PluginConfiguration(), new Configuration());
        pluggableTask.onCancelConfig = Mockito.mock(OnCancelConfig.class);
        com.thoughtworks.go.domain.Task cancelTask = Mockito.mock(Task.class);
        Mockito.when(pluggableTask.onCancelConfig.getTask()).thenReturn(cancelTask);
        Mockito.when(cancelTask.hasCancelTask()).thenReturn(true);
        Mockito.when(pluggableTask.onCancelConfig.validateTree(null)).thenReturn(true);
        Assert.assertFalse(pluggableTask.validateTree(null));
        Assert.assertThat(pluggableTask.errors().get("onCancelConfig").get(0), Matchers.is("Cannot nest 'oncancel' within a cancel task"));
    }

    @Test
    public void validateTreeShouldVerifyIfPluggableTaskHasErrors() {
        PluggableTask pluggableTask = new PluggableTask(new PluginConfiguration(), new Configuration());
        pluggableTask.addError("task", "invalid plugin");
        Assert.assertFalse(pluggableTask.validateTree(null));
    }

    @Test
    public void validateTreeShouldVerifyIfConfigurationHasErrors() {
        Configuration configuration = Mockito.mock(Configuration.class);
        PluggableTask pluggableTask = new PluggableTask(new PluginConfiguration(), configuration);
        Mockito.when(configuration.hasErrors()).thenReturn(true);
        Assert.assertFalse(pluggableTask.validateTree(null));
    }

    @Test
    public void postConstructShouldHandleSecureConfigurationForConfigurationProperties() throws Exception {
        TaskPreference taskPreference = Mockito.mock(TaskPreference.class);
        ConfigurationProperty configurationProperty = ConfigurationPropertyMother.create("KEY1");
        Configuration configuration = new Configuration(configurationProperty);
        PluggableTaskConfigStore.store().setPreferenceFor("abc.def", taskPreference);
        TaskConfig taskConfig = new TaskConfig();
        taskConfig.addProperty("KEY1").with(SECURE, true);
        Mockito.when(taskPreference.getConfig()).thenReturn(taskConfig);
        PluggableTask task = new PluggableTask(new PluginConfiguration("abc.def", "1"), configuration);
        Assert.assertFalse(configurationProperty.isSecure());
        task.applyPluginMetadata();
        Assert.assertTrue(configurationProperty.isSecure());
    }

    @Test
    public void postConstructShouldDoNothingForPluggableTaskWithoutCorrespondingPlugin() throws Exception {
        ConfigurationProperty configurationProperty = ConfigurationPropertyMother.create("KEY1");
        Configuration configuration = new Configuration(configurationProperty);
        PluggableTask task = new PluggableTask(new PluginConfiguration("abc.def", "1"), configuration);
        Assert.assertFalse(configurationProperty.isSecure());
        task.applyPluginMetadata();
        Assert.assertFalse(configurationProperty.isSecure());
    }

    @Test
    public void postConstructShouldDoNothingForAInvalidConfigurationProperty() throws Exception {
        TaskPreference taskPreference = Mockito.mock(TaskPreference.class);
        ConfigurationProperty configurationProperty = ConfigurationPropertyMother.create("KEY1");
        Configuration configuration = new Configuration(configurationProperty);
        PluggableTaskConfigStore.store().setPreferenceFor("abc.def", taskPreference);
        TaskConfig taskConfig = new TaskConfig();
        Mockito.when(taskPreference.getConfig()).thenReturn(taskConfig);
        PluggableTask task = new PluggableTask(new PluginConfiguration("abc.def", "1"), configuration);
        Assert.assertFalse(configurationProperty.isSecure());
        task.applyPluginMetadata();
        Assert.assertFalse(configurationProperty.isSecure());
    }
}

