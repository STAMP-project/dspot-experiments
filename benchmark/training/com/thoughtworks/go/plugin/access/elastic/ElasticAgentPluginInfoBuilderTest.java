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
package com.thoughtworks.go.plugin.access.elastic;


import com.thoughtworks.go.plugin.access.common.settings.PluginSettingsConfiguration;
import com.thoughtworks.go.plugin.access.common.settings.PluginSettingsProperty;
import com.thoughtworks.go.plugin.domain.common.PluginConstants;
import com.thoughtworks.go.plugin.domain.elastic.Capabilities;
import com.thoughtworks.go.plugin.domain.elastic.ElasticAgentPluginInfo;
import com.thoughtworks.go.plugin.infra.PluginManager;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ElasticAgentPluginInfoBuilderTest {
    private ElasticAgentExtension extension;

    private PluginManager pluginManager;

    @Test
    public void shouldBuildPluginInfoWithProfileSettings() {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        List<PluginConfiguration> pluginConfigurations = Arrays.asList(new PluginConfiguration("aws_password", new Metadata(true, false)));
        PluginSettingsProperty property = new PluginSettingsProperty("ami-id", "ami-123");
        PluginSettingsConfiguration pluginSettingsConfiguration = new PluginSettingsConfiguration();
        pluginSettingsConfiguration.add(property);
        Image icon = new Image("content_type", "data", "hash");
        Mockito.when(pluginManager.resolveExtensionVersion("plugin1", PluginConstants.ELASTIC_AGENT_EXTENSION, ElasticAgentExtension.SUPPORTED_VERSIONS)).thenReturn("1.0");
        Mockito.when(extension.getPluginSettingsConfiguration(descriptor.id())).thenReturn(pluginSettingsConfiguration);
        Mockito.when(extension.getPluginSettingsView(descriptor.id())).thenReturn("some html");
        Mockito.when(extension.getIcon(descriptor.id())).thenReturn(icon);
        Mockito.when(extension.getProfileMetadata(descriptor.id())).thenReturn(pluginConfigurations);
        Mockito.when(extension.getProfileView(descriptor.id())).thenReturn("profile_view");
        ElasticAgentPluginInfoBuilder builder = new ElasticAgentPluginInfoBuilder(extension);
        ElasticAgentPluginInfo pluginInfo = builder.pluginInfoFor(descriptor);
        Assert.assertThat(pluginInfo.getDescriptor(), Matchers.is(descriptor));
        Assert.assertThat(pluginInfo.getExtensionName(), Matchers.is("elastic-agent"));
        Assert.assertThat(pluginInfo.getImage(), Matchers.is(icon));
        Assert.assertThat(pluginInfo.getProfileSettings(), Matchers.is(new PluggableInstanceSettings(pluginConfigurations, new PluginView("profile_view"))));
        Assert.assertThat(pluginInfo.getPluginSettings(), Matchers.is(new PluggableInstanceSettings(builder.configurations(pluginSettingsConfiguration), new PluginView("some html"))));
        Assert.assertFalse(pluginInfo.supportsStatusReport());
    }

    @Test
    public void shouldContinueWithBuildingPluginInfoIfPluginSettingsIsNotProvidedByThePlugin() {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        List<PluginConfiguration> pluginConfigurations = Arrays.asList(new PluginConfiguration("aws_password", new Metadata(true, false)));
        Image icon = new Image("content_type", "data", "hash");
        Mockito.doThrow(new RuntimeException("foo")).when(extension).getPluginSettingsConfiguration(descriptor.id());
        Mockito.when(pluginManager.resolveExtensionVersion("plugin1", PluginConstants.ELASTIC_AGENT_EXTENSION, ElasticAgentExtension.SUPPORTED_VERSIONS)).thenReturn("1.0");
        Mockito.when(extension.getIcon(descriptor.id())).thenReturn(icon);
        Mockito.when(extension.getProfileMetadata(descriptor.id())).thenReturn(pluginConfigurations);
        Mockito.when(extension.getProfileView(descriptor.id())).thenReturn("profile_view");
        ElasticAgentPluginInfoBuilder builder = new ElasticAgentPluginInfoBuilder(extension);
        ElasticAgentPluginInfo pluginInfo = builder.pluginInfoFor(descriptor);
        Assert.assertThat(pluginInfo.getDescriptor(), Matchers.is(descriptor));
        Assert.assertThat(pluginInfo.getExtensionName(), Matchers.is("elastic-agent"));
        Assert.assertThat(pluginInfo.getImage(), Matchers.is(icon));
        Assert.assertThat(pluginInfo.getProfileSettings(), Matchers.is(new PluggableInstanceSettings(pluginConfigurations, new PluginView("profile_view"))));
        Assert.assertNull(pluginInfo.getPluginSettings());
    }

    @Test
    public void shouldGetCapabilitiesForAPlugin() {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        Mockito.when(pluginManager.resolveExtensionVersion("plugin1", PluginConstants.ELASTIC_AGENT_EXTENSION, ElasticAgentExtension.SUPPORTED_VERSIONS)).thenReturn("2.0");
        Capabilities capabilities = new Capabilities(true);
        Mockito.when(extension.getCapabilities(descriptor.id())).thenReturn(capabilities);
        ElasticAgentPluginInfo pluginInfo = new ElasticAgentPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        Assert.assertThat(pluginInfo.getCapabilities(), Matchers.is(capabilities));
    }
}

