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
package com.thoughtworks.go.plugin.access.analytics;


import Property.REQUIRED;
import Property.SECURE;
import com.thoughtworks.go.plugin.access.common.settings.PluginSettingsConfiguration;
import com.thoughtworks.go.plugin.access.common.settings.PluginSettingsProperty;
import com.thoughtworks.go.plugin.domain.analytics.AnalyticsPluginInfo;
import com.thoughtworks.go.plugin.domain.analytics.Capabilities;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class AnalyticsPluginInfoBuilderTest {
    private AnalyticsExtension extension;

    @Test
    public void shouldBuildPluginInfoWithCapabilities() throws Exception {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        Capabilities capabilities = new Capabilities(Collections.emptyList());
        Mockito.when(extension.getCapabilities(descriptor.id())).thenReturn(capabilities);
        AnalyticsPluginInfo pluginInfo = new AnalyticsPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        Assert.assertThat(pluginInfo.getCapabilities(), Matchers.is(capabilities));
    }

    @Test
    public void shouldBuildPluginInfoWithImage() throws Exception {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        Image icon = new Image("content_type", "data", "hash");
        Mockito.when(extension.getIcon(descriptor.id())).thenReturn(icon);
        AnalyticsPluginInfo pluginInfo = new AnalyticsPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        Assert.assertThat(pluginInfo.getImage(), Matchers.is(icon));
    }

    @Test
    public void shouldBuildPluginInfoWithPluginDescriptor() throws Exception {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        AnalyticsPluginInfo pluginInfo = new AnalyticsPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        Assert.assertThat(pluginInfo.getDescriptor(), Matchers.is(descriptor));
    }

    @Test
    public void shouldBuildPluginInfoWithPluginSettingsConfiguration() throws Exception {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        PluginSettingsConfiguration value = new PluginSettingsConfiguration();
        value.add(new PluginSettingsProperty("username", null).with(REQUIRED, true).with(SECURE, false));
        value.add(new PluginSettingsProperty("password", null).with(REQUIRED, true).with(SECURE, true));
        Mockito.when(extension.getPluginSettingsConfiguration("plugin1")).thenReturn(value);
        Mockito.when(extension.getPluginSettingsView("plugin1")).thenReturn("some-html");
        AnalyticsPluginInfo pluginInfo = new AnalyticsPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        List<PluginConfiguration> pluginConfigurations = Arrays.asList(new PluginConfiguration("username", new Metadata(true, false)), new PluginConfiguration("password", new Metadata(true, true)));
        PluginView pluginView = new PluginView("some-html");
        Assert.assertThat(pluginInfo.getDescriptor(), Matchers.is(descriptor));
        Assert.assertThat(pluginInfo.getExtensionName(), Matchers.is("analytics"));
        Assert.assertThat(pluginInfo.getPluginSettings(), Matchers.is(new PluggableInstanceSettings(pluginConfigurations, pluginView)));
    }

    @Test
    public void shouldContinueBuildingPluginInfoIfPluginSettingsIsNotProvidedByPlugin() {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        Mockito.doThrow(new RuntimeException("foo")).when(extension).getPluginSettingsConfiguration("plugin1");
        AnalyticsPluginInfo pluginInfo = new AnalyticsPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        Assert.assertThat(pluginInfo.getDescriptor(), Matchers.is(descriptor));
        Assert.assertThat(pluginInfo.getExtensionName(), Matchers.is("analytics"));
        Assert.assertNull(pluginInfo.getPluginSettings());
    }
}

