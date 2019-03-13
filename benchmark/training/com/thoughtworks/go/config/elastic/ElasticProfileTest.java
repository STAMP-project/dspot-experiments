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
package com.thoughtworks.go.config.elastic;


import ConfigurationProperty.CONFIGURATION_KEY;
import ElasticProfile.ID;
import ElasticProfile.PLUGIN_ID;
import com.thoughtworks.go.domain.config.ConfigurationKey;
import com.thoughtworks.go.domain.config.ConfigurationProperty;
import com.thoughtworks.go.domain.config.ConfigurationValue;
import com.thoughtworks.go.domain.config.EncryptedConfigurationValue;
import com.thoughtworks.go.domain.packagerepository.ConfigurationPropertyMother;
import com.thoughtworks.go.plugin.access.elastic.ElasticAgentMetadataStore;
import com.thoughtworks.go.plugin.domain.common.Metadata;
import com.thoughtworks.go.plugin.domain.common.PluggableInstanceSettings;
import com.thoughtworks.go.plugin.domain.elastic.ElasticAgentPluginInfo;
import java.util.ArrayList;
import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ElasticProfileTest {
    private ElasticAgentMetadataStore store = ElasticAgentMetadataStore.instance();

    @Test
    public void shouldNotAllowNullPluginIdOrProfileId() throws Exception {
        ElasticProfile profile = new ElasticProfile();
        profile.validate(null);
        Assert.assertThat(profile.errors().size(), Matchers.is(2));
        Assert.assertThat(profile.errors().on(PLUGIN_ID), Matchers.is("Elastic agent profile cannot have a blank plugin id."));
        Assert.assertThat(profile.errors().on(ID), Matchers.is("Elastic agent profile cannot have a blank id."));
    }

    @Test
    public void shouldValidateElasticPluginIdPattern() throws Exception {
        ElasticProfile profile = new ElasticProfile("!123", "docker");
        profile.validate(null);
        Assert.assertThat(profile.errors().size(), Matchers.is(1));
        Assert.assertThat(profile.errors().on(ID), Matchers.is("Invalid id '!123'. This must be alphanumeric and can contain underscores and periods (however, it cannot start with a period). The maximum allowed length is 255 characters."));
    }

    @Test
    public void shouldValidateConfigPropertyNameUniqueness() throws Exception {
        ConfigurationProperty prop1 = ConfigurationPropertyMother.create("USERNAME");
        ConfigurationProperty prop2 = ConfigurationPropertyMother.create("USERNAME");
        ElasticProfile profile = new ElasticProfile("docker.unit-test", "cd.go.elastic-agent.docker", prop1, prop2);
        profile.validate(null);
        Assert.assertThat(profile.errors().size(), Matchers.is(0));
        Assert.assertThat(prop1.errors().size(), Matchers.is(1));
        Assert.assertThat(prop2.errors().size(), Matchers.is(1));
        Assert.assertThat(prop1.errors().on(CONFIGURATION_KEY), Matchers.is("Duplicate key 'USERNAME' found for Elastic agent profile 'docker.unit-test'"));
        Assert.assertThat(prop2.errors().on(CONFIGURATION_KEY), Matchers.is("Duplicate key 'USERNAME' found for Elastic agent profile 'docker.unit-test'"));
    }

    @Test
    public void addConfigurations_shouldAddConfigurationsWithValue() throws Exception {
        ConfigurationProperty property = new ConfigurationProperty(new ConfigurationKey("username"), new ConfigurationValue("some_name"));
        ElasticProfile profile = new ElasticProfile("id", "plugin_id");
        profile.addConfigurations(Arrays.asList(property));
        Assert.assertThat(profile.size(), Matchers.is(1));
        Assert.assertThat(profile, Matchers.contains(new ConfigurationProperty(new ConfigurationKey("username"), new ConfigurationValue("some_name"))));
    }

    @Test
    public void addConfigurations_shouldAddConfigurationsWithEncryptedValue() throws Exception {
        ConfigurationProperty property = new ConfigurationProperty(new ConfigurationKey("username"), new EncryptedConfigurationValue("some_name"));
        ElasticProfile profile = new ElasticProfile("id", "plugin_id");
        profile.addConfigurations(Arrays.asList(property));
        Assert.assertThat(profile.size(), Matchers.is(1));
        Assert.assertThat(profile, Matchers.contains(new ConfigurationProperty(new ConfigurationKey("username"), new EncryptedConfigurationValue("some_name"))));
    }

    @Test
    public void addConfiguration_shouldEncryptASecureVariable() throws Exception {
        PluggableInstanceSettings profileSettings = new PluggableInstanceSettings(Arrays.asList(new com.thoughtworks.go.plugin.domain.common.PluginConfiguration("password", new Metadata(true, true))));
        ElasticAgentPluginInfo pluginInfo = new ElasticAgentPluginInfo(pluginDescriptor("plugin_id"), profileSettings, null, null, null);
        store.setPluginInfo(pluginInfo);
        ElasticProfile profile = new ElasticProfile("id", "plugin_id");
        profile.addConfigurations(Arrays.asList(new ConfigurationProperty(new ConfigurationKey("password"), new ConfigurationValue("pass"))));
        Assert.assertThat(profile.size(), Matchers.is(1));
        Assert.assertTrue(profile.first().isSecure());
    }

    @Test
    public void addConfiguration_shouldIgnoreEncryptionInAbsenceOfCorrespondingConfigurationInStore() throws Exception {
        ElasticAgentPluginInfo pluginInfo = new ElasticAgentPluginInfo(pluginDescriptor("plugin_id"), new PluggableInstanceSettings(new ArrayList()), null, null, null);
        store.setPluginInfo(pluginInfo);
        ElasticProfile profile = new ElasticProfile("id", "plugin_id");
        profile.addConfigurations(Arrays.asList(new ConfigurationProperty(new ConfigurationKey("password"), new ConfigurationValue("pass"))));
        Assert.assertThat(profile.size(), Matchers.is(1));
        Assert.assertFalse(profile.first().isSecure());
        Assert.assertThat(profile, Matchers.contains(new ConfigurationProperty(new ConfigurationKey("password"), new ConfigurationValue("pass"))));
    }

    @Test
    public void postConstruct_shouldEncryptSecureConfigurations() {
        PluggableInstanceSettings profileSettings = new PluggableInstanceSettings(Arrays.asList(new com.thoughtworks.go.plugin.domain.common.PluginConfiguration("password", new Metadata(true, true))));
        ElasticAgentPluginInfo pluginInfo = new ElasticAgentPluginInfo(pluginDescriptor("plugin_id"), profileSettings, null, null, null);
        store.setPluginInfo(pluginInfo);
        ElasticProfile profile = new ElasticProfile("id", "plugin_id", new ConfigurationProperty(new ConfigurationKey("password"), new ConfigurationValue("pass")));
        profile.encryptSecureConfigurations();
        Assert.assertThat(profile.size(), Matchers.is(1));
        Assert.assertTrue(profile.first().isSecure());
    }

    @Test
    public void postConstruct_shouldIgnoreEncryptionIfPluginInfoIsNotDefined() {
        ElasticProfile profile = new ElasticProfile("id", "plugin_id", new ConfigurationProperty(new ConfigurationKey("password"), new ConfigurationValue("pass")));
        profile.encryptSecureConfigurations();
        Assert.assertThat(profile.size(), Matchers.is(1));
        Assert.assertFalse(profile.first().isSecure());
    }
}

