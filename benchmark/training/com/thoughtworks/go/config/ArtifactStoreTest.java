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
package com.thoughtworks.go.config;


import ArtifactStore.ID;
import ArtifactStore.PLUGIN_ID;
import ConfigurationProperty.CONFIGURATION_KEY;
import com.thoughtworks.go.domain.config.ConfigurationKey;
import com.thoughtworks.go.domain.config.ConfigurationProperty;
import com.thoughtworks.go.domain.config.ConfigurationValue;
import com.thoughtworks.go.domain.packagerepository.ConfigurationPropertyMother;
import com.thoughtworks.go.plugin.access.artifact.ArtifactMetadataStore;
import com.thoughtworks.go.plugin.domain.artifact.ArtifactPluginInfo;
import com.thoughtworks.go.plugin.domain.common.Metadata;
import com.thoughtworks.go.plugin.domain.common.PluggableInstanceSettings;
import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ArtifactStoreTest {
    private ArtifactMetadataStore store;

    @Test
    public void addConfigurations_shouldAddConfigurationsWithValue() {
        ConfigurationProperty property = ConfigurationPropertyMother.create("username", false, "some_value");
        ArtifactStore artifactStore = new ArtifactStore("id", "plugin_id");
        artifactStore.addConfigurations(Arrays.asList(property));
        Assert.assertThat(artifactStore.size(), Matchers.is(1));
        Assert.assertThat(artifactStore, Matchers.contains(ConfigurationPropertyMother.create("username", false, "some_value")));
    }

    @Test
    public void addConfigurations_shouldAddConfigurationsWithEncryptedValue() {
        ConfigurationProperty property = ConfigurationPropertyMother.create("username", true, "some_value");
        ArtifactStore artifactStore = new ArtifactStore("id", "plugin_id");
        artifactStore.addConfigurations(Arrays.asList(property));
        Assert.assertThat(artifactStore.size(), Matchers.is(1));
        Assert.assertThat(artifactStore, Matchers.contains(ConfigurationPropertyMother.create("username", true, "some_value")));
    }

    @Test
    public void shouldReturnObjectDescription() {
        Assert.assertThat(new ArtifactStore().getObjectDescription(), Matchers.is("Artifact store"));
    }

    @Test
    public void shouldNotAllowNullPluginIdOrArtifactStoreId() {
        ArtifactStore store = new ArtifactStore();
        store.validate(null);
        Assert.assertThat(store.errors().size(), Matchers.is(2));
        Assert.assertThat(store.errors().on(PLUGIN_ID), Matchers.is("Artifact store cannot have a blank plugin id."));
        Assert.assertThat(store.errors().on(ID), Matchers.is("Artifact store cannot have a blank id."));
    }

    @Test
    public void shouldValidateArtifactStoreIdPattern() {
        ArtifactStore store = new ArtifactStore("!123", "docker");
        store.validate(null);
        Assert.assertThat(store.errors().size(), Matchers.is(1));
        Assert.assertThat(store.errors().on(ID), Matchers.is("Invalid id '!123'. This must be alphanumeric and can contain underscores and periods (however, it cannot start with a period). The maximum allowed length is 255 characters."));
    }

    @Test
    public void shouldValidateConfigPropertyNameUniqueness() {
        ConfigurationProperty prop1 = ConfigurationPropertyMother.create("USERNAME");
        ConfigurationProperty prop2 = ConfigurationPropertyMother.create("USERNAME");
        ArtifactStore store = new ArtifactStore("s3.plugin", "cd.go.s3.plugin", prop1, prop2);
        store.validate(null);
        Assert.assertThat(store.errors().size(), Matchers.is(0));
        Assert.assertThat(prop1.errors().size(), Matchers.is(1));
        Assert.assertThat(prop2.errors().size(), Matchers.is(1));
        Assert.assertThat(prop1.errors().on(CONFIGURATION_KEY), Matchers.is("Duplicate key 'USERNAME' found for Artifact store 's3.plugin'"));
        Assert.assertThat(prop2.errors().on(CONFIGURATION_KEY), Matchers.is("Duplicate key 'USERNAME' found for Artifact store 's3.plugin'"));
    }

    @Test
    public void shouldReturnTrueIfPluginInfoIsDefined() {
        final ArtifactPluginInfo pluginInfo = new ArtifactPluginInfo(pluginDescriptor("plugin_id"), null, null, null, null, null);
        store.setPluginInfo(pluginInfo);
        final ArtifactStore artifactStore = new ArtifactStore("id", "plugin_id");
        Assert.assertTrue(artifactStore.hasPluginInfo());
    }

    @Test
    public void shouldReturnFalseIfPluginInfoIsDefined() {
        final ArtifactStore artifactStore = new ArtifactStore("id", "plugin_id");
        Assert.assertFalse(artifactStore.hasPluginInfo());
    }

    @Test
    public void postConstruct_shouldEncryptSecureConfigurations() {
        final PluggableInstanceSettings storeConfig = new PluggableInstanceSettings(Arrays.asList(new com.thoughtworks.go.plugin.domain.common.PluginConfiguration("password", new Metadata(true, true))));
        final ArtifactPluginInfo pluginInfo = new ArtifactPluginInfo(pluginDescriptor("plugin_id"), storeConfig, null, null, null, null);
        store.setPluginInfo(pluginInfo);
        ArtifactStore artifactStore = new ArtifactStore("id", "plugin_id", new ConfigurationProperty(new ConfigurationKey("password"), new ConfigurationValue("pass")));
        artifactStore.encryptSecureConfigurations();
        Assert.assertThat(artifactStore.size(), Matchers.is(1));
        Assert.assertTrue(artifactStore.first().isSecure());
    }

    @Test
    public void postConstruct_shouldIgnoreEncryptionIfPluginInfoIsNotDefined() {
        ArtifactStore artifactStore = new ArtifactStore("id", "plugin_id", new ConfigurationProperty(new ConfigurationKey("password"), new ConfigurationValue("pass")));
        artifactStore.encryptSecureConfigurations();
        Assert.assertThat(artifactStore.size(), Matchers.is(1));
        Assert.assertFalse(artifactStore.first().isSecure());
    }
}

