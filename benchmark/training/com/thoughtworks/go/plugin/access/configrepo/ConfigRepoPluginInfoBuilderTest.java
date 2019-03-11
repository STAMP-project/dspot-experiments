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
package com.thoughtworks.go.plugin.access.configrepo;


import com.thoughtworks.go.plugin.domain.common.Metadata;
import com.thoughtworks.go.plugin.domain.common.PluginConfiguration;
import com.thoughtworks.go.plugin.domain.common.PluginView;
import com.thoughtworks.go.plugin.domain.configrepo.ConfigRepoPluginInfo;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ConfigRepoPluginInfoBuilderTest {
    private ConfigRepoExtension extension;

    @Test
    public void shouldBuildPluginInfo() throws Exception {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        Mockito.when(extension.getPluginSettingsView("plugin1")).thenReturn("some-html");
        ConfigRepoPluginInfo pluginInfo = new ConfigRepoPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        List<PluginConfiguration> pluginConfigurations = Arrays.asList(new PluginConfiguration("username", new Metadata(true, false)), new PluginConfiguration("password", new Metadata(true, true)));
        PluginView pluginView = new PluginView("some-html");
        Assert.assertThat(pluginInfo.getDescriptor(), Matchers.is(descriptor));
        Assert.assertThat(pluginInfo.getExtensionName(), Matchers.is("configrepo"));
        Assert.assertThat(pluginInfo.getPluginSettings(), Matchers.is(new com.thoughtworks.go.plugin.domain.common.PluggableInstanceSettings(pluginConfigurations, pluginView)));
    }

    @Test
    public void shouldContinueWithBuildingPluginInfoIfPluginSettingsIsNotProvidedByPlugin() throws Exception {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        Mockito.doThrow(new RuntimeException("foo")).when(extension).getPluginSettingsConfiguration("plugin1");
        ConfigRepoPluginInfo pluginInfo = new ConfigRepoPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        Assert.assertThat(pluginInfo.getDescriptor(), Matchers.is(descriptor));
        Assert.assertThat(pluginInfo.getExtensionName(), Matchers.is("configrepo"));
        Assert.assertNull(pluginInfo.getPluginSettings());
    }
}

