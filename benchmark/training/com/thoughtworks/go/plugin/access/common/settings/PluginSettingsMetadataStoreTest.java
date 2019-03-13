/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
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
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.plugin.access.common.settings;


import Property.REQUIRED;
import com.thoughtworks.go.plugin.domain.common.PluginConstants;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PluginSettingsMetadataStoreTest {
    @Test
    public void shouldPopulateDataCorrectly() {
        String existingPluginId = "plugin-id";
        PluginSettingsConfiguration configuration = new PluginSettingsConfiguration();
        String template = "template-value";
        PluginSettingsMetadataStore.getInstance().addMetadataFor(existingPluginId, PluginConstants.NOTIFICATION_EXTENSION, configuration, template);
        Assert.assertThat(PluginSettingsMetadataStore.getInstance().configuration(existingPluginId), Matchers.is(configuration));
        Assert.assertThat(PluginSettingsMetadataStore.getInstance().template(existingPluginId), Matchers.is(template));
        String nonExistingPluginId = "some-plugin-which-does-not-exist";
        Assert.assertThat(PluginSettingsMetadataStore.getInstance().configuration(nonExistingPluginId), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(PluginSettingsMetadataStore.getInstance().template(nonExistingPluginId), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldRemoveDataCorrectly() {
        String pluginId = "plugin-id";
        PluginSettingsMetadataStore.getInstance().addMetadataFor(pluginId, PluginConstants.NOTIFICATION_EXTENSION, new PluginSettingsConfiguration(), "template-value");
        Assert.assertThat(PluginSettingsMetadataStore.getInstance().hasPlugin(pluginId), Matchers.is(true));
        PluginSettingsMetadataStore.getInstance().removeMetadataFor(pluginId);
        Assert.assertThat(PluginSettingsMetadataStore.getInstance().hasPlugin(pluginId), Matchers.is(false));
    }

    @Test
    public void shouldBeAbleToCheckIfPluginExists() {
        PluginSettingsConfiguration configuration = new PluginSettingsConfiguration();
        String template = "template-value";
        PluginSettingsMetadataStore.getInstance().addMetadataFor("plugin-id", PluginConstants.NOTIFICATION_EXTENSION, configuration, template);
        Assert.assertThat(PluginSettingsMetadataStore.getInstance().hasPlugin("plugin-id"), Matchers.is(true));
        Assert.assertThat(PluginSettingsMetadataStore.getInstance().hasPlugin("some-plugin-which-does-not-exist"), Matchers.is(false));
    }

    @Test
    public void shouldCheckIfPluginSettingsConfigurationHasOption() {
        PluginSettingsConfiguration configuration = new PluginSettingsConfiguration();
        PluginSettingsProperty p1 = createProperty("k1", true);
        PluginSettingsProperty p2 = createProperty("k2", false);
        configuration.add(p1);
        configuration.add(p2);
        PluginSettingsMetadataStore.getInstance().addMetadataFor("plugin-id", PluginConstants.NOTIFICATION_EXTENSION, configuration, "template-value");
        Assert.assertTrue(PluginSettingsMetadataStore.getInstance().hasOption("plugin-id", "k1", REQUIRED));
        Assert.assertFalse(PluginSettingsMetadataStore.getInstance().hasOption("plugin-id", "k2", REQUIRED));
    }
}

