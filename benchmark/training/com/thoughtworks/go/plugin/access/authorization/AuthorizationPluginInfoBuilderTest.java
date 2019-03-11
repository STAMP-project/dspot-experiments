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
package com.thoughtworks.go.plugin.access.authorization;


import com.thoughtworks.go.plugin.domain.authorization.AuthorizationPluginInfo;
import com.thoughtworks.go.plugin.domain.authorization.Capabilities;
import com.thoughtworks.go.plugin.domain.authorization.SupportedAuthType;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class AuthorizationPluginInfoBuilderTest {
    private AuthorizationExtension extension;

    @Test
    public void shouldBuildPluginInfoWithAuthSettings() throws Exception {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        List<PluginConfiguration> pluginConfigurations = Arrays.asList(new PluginConfiguration("username", new Metadata(true, false)), new PluginConfiguration("password", new Metadata(true, true)));
        Mockito.when(extension.getAuthConfigMetadata(descriptor.id())).thenReturn(pluginConfigurations);
        Mockito.when(extension.getAuthConfigView(descriptor.id())).thenReturn("auth_config");
        AuthorizationPluginInfo pluginInfo = new AuthorizationPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        Assert.assertThat(pluginInfo.getAuthConfigSettings(), Matchers.is(new PluggableInstanceSettings(pluginConfigurations, new PluginView("auth_config"))));
    }

    @Test
    public void shouldBuildPluginInfoWithRoleSettings() throws Exception {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        List<PluginConfiguration> pluginConfigurations = Arrays.asList(new PluginConfiguration("group", new Metadata(true, false)), new PluginConfiguration("something_secure", new Metadata(true, true)));
        Mockito.when(extension.getRoleConfigurationMetadata(descriptor.id())).thenReturn(pluginConfigurations);
        Mockito.when(extension.getRoleConfigurationView(descriptor.id())).thenReturn("role_config");
        AuthorizationPluginInfo pluginInfo = new AuthorizationPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        Assert.assertThat(pluginInfo.getRoleSettings(), Matchers.is(new PluggableInstanceSettings(pluginConfigurations, new PluginView("role_config"))));
    }

    @Test
    public void shouldNotHaveRoleSettingsInPluginInfoIfPluginCannotAuthorize() throws Exception {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        Capabilities capabilities = new Capabilities(SupportedAuthType.Password, true, false, false);
        Mockito.when(extension.getCapabilities(descriptor.id())).thenReturn(capabilities);
        AuthorizationPluginInfo pluginInfo = new AuthorizationPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        Assert.assertNull(pluginInfo.getRoleSettings());
    }

    @Test
    public void shouldBuildPluginInfoWithPluginDescriptor() throws Exception {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        AuthorizationPluginInfo pluginInfo = new AuthorizationPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        Assert.assertThat(pluginInfo.getDescriptor(), Matchers.is(descriptor));
    }

    @Test
    public void shouldBuildPluginInfoWithImage() throws Exception {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        Image icon = new Image("content_type", "data", "hash");
        Mockito.when(extension.getIcon(descriptor.id())).thenReturn(icon);
        AuthorizationPluginInfo pluginInfo = new AuthorizationPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        Assert.assertThat(pluginInfo.getImage(), Matchers.is(icon));
    }

    @Test
    public void shouldBuildPluginInfoWithCapablities() throws Exception {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        Capabilities capabilities = new Capabilities(SupportedAuthType.Password, true, true, false);
        Mockito.when(extension.getCapabilities(descriptor.id())).thenReturn(capabilities);
        AuthorizationPluginInfo pluginInfo = new AuthorizationPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        Assert.assertThat(pluginInfo.getCapabilities(), Matchers.is(capabilities));
    }
}

