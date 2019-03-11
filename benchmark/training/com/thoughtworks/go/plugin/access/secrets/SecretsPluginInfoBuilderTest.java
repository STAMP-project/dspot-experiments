/**
 * Copyright 2019 ThoughtWorks, Inc.
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
package com.thoughtworks.go.plugin.access.secrets;


import com.thoughtworks.go.plugin.domain.secrets.SecretsPluginInfo;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class SecretsPluginInfoBuilderTest {
    private SecretsExtension extension;

    @Test
    public void shouldBuildPluginInfoWithImage() {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        Image icon = new Image("content_type", "data", "hash");
        Mockito.when(extension.getIcon(descriptor.id())).thenReturn(icon);
        SecretsPluginInfo pluginInfo = new SecretsPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        Assert.assertThat(pluginInfo.getImage(), Matchers.is(icon));
    }

    @Test
    public void shouldBuildPluginInfoWithPluginDescriptor() {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        SecretsPluginInfo pluginInfo = new SecretsPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        Assert.assertThat(pluginInfo.getDescriptor(), Matchers.is(descriptor));
    }

    @Test
    public void shouldBuildPluginInfoWithSecuritySettings() {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        List<PluginConfiguration> pluginConfigurations = Arrays.asList(new PluginConfiguration("username", new Metadata(true, false)), new PluginConfiguration("password", new Metadata(true, true)));
        Mockito.when(extension.getSecretsConfigMetadata(descriptor.id())).thenReturn(pluginConfigurations);
        Mockito.when(extension.getSecretsConfigView(descriptor.id())).thenReturn("secrets_config_view");
        SecretsPluginInfo pluginInfo = new SecretsPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        Assert.assertThat(pluginInfo.getSecretsConfigSettings(), Matchers.is(new PluggableInstanceSettings(pluginConfigurations, new PluginView("secrets_config_view"))));
    }
}

