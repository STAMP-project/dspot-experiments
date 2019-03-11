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


import com.thoughtworks.go.domain.config.ConfigurationKey;
import com.thoughtworks.go.domain.config.ConfigurationProperty;
import com.thoughtworks.go.domain.config.ConfigurationValue;
import com.thoughtworks.go.domain.config.EncryptedConfigurationValue;
import com.thoughtworks.go.plugin.access.authorization.AuthorizationMetadataStore;
import com.thoughtworks.go.plugin.domain.authorization.AuthorizationPluginInfo;
import com.thoughtworks.go.plugin.domain.common.Metadata;
import com.thoughtworks.go.plugin.domain.common.PluggableInstanceSettings;
import java.util.ArrayList;
import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class SecurityAuthConfigTest {
    private AuthorizationMetadataStore store = AuthorizationMetadataStore.instance();

    @Test
    public void addConfigurations_shouldAddConfigurationsWithValue() throws Exception {
        ConfigurationProperty property = new ConfigurationProperty(new ConfigurationKey("username"), new ConfigurationValue("some_name"));
        SecurityAuthConfig authConfig = new SecurityAuthConfig("id", "plugin_id");
        authConfig.addConfigurations(Arrays.asList(property));
        Assert.assertThat(authConfig.size(), Matchers.is(1));
        Assert.assertThat(authConfig, Matchers.contains(new ConfigurationProperty(new ConfigurationKey("username"), new ConfigurationValue("some_name"))));
    }

    @Test
    public void addConfigurations_shouldAddConfigurationsWithEncryptedValue() throws Exception {
        ConfigurationProperty property = new ConfigurationProperty(new ConfigurationKey("username"), new EncryptedConfigurationValue("some_name"));
        SecurityAuthConfig authConfig = new SecurityAuthConfig("id", "plugin_id");
        authConfig.addConfigurations(Arrays.asList(property));
        Assert.assertThat(authConfig.size(), Matchers.is(1));
        Assert.assertThat(authConfig, Matchers.contains(new ConfigurationProperty(new ConfigurationKey("username"), new EncryptedConfigurationValue("some_name"))));
    }

    @Test
    public void addConfiguration_shouldEncryptASecureVariable() throws Exception {
        PluggableInstanceSettings profileSettings = new PluggableInstanceSettings(Arrays.asList(new com.thoughtworks.go.plugin.domain.common.PluginConfiguration("password", new Metadata(true, true))));
        AuthorizationPluginInfo pluginInfo = new AuthorizationPluginInfo(pluginDescriptor("plugin_id"), profileSettings, null, null, null);
        store.setPluginInfo(pluginInfo);
        SecurityAuthConfig authConfig = new SecurityAuthConfig("id", "plugin_id");
        authConfig.addConfigurations(Arrays.asList(new ConfigurationProperty(new ConfigurationKey("password"), new ConfigurationValue("pass"))));
        Assert.assertThat(authConfig.size(), Matchers.is(1));
        Assert.assertTrue(authConfig.first().isSecure());
    }

    @Test
    public void addConfiguration_shouldIgnoreEncryptionInAbsenceOfCorrespondingConfigurationInStore() throws Exception {
        AuthorizationPluginInfo pluginInfo = new AuthorizationPluginInfo(pluginDescriptor("plugin_id"), new PluggableInstanceSettings(new ArrayList()), null, null, null);
        store.setPluginInfo(pluginInfo);
        SecurityAuthConfig authConfig = new SecurityAuthConfig("id", "plugin_id");
        authConfig.addConfigurations(Arrays.asList(new ConfigurationProperty(new ConfigurationKey("password"), new ConfigurationValue("pass"))));
        Assert.assertThat(authConfig.size(), Matchers.is(1));
        Assert.assertFalse(authConfig.first().isSecure());
        Assert.assertThat(authConfig, Matchers.contains(new ConfigurationProperty(new ConfigurationKey("password"), new ConfigurationValue("pass"))));
    }

    @Test
    public void postConstruct_shouldEncryptSecureConfigurations() throws Exception {
        PluggableInstanceSettings profileSettings = new PluggableInstanceSettings(Arrays.asList(new com.thoughtworks.go.plugin.domain.common.PluginConfiguration("password", new Metadata(true, true))));
        AuthorizationPluginInfo pluginInfo = new AuthorizationPluginInfo(pluginDescriptor("plugin_id"), profileSettings, null, null, null);
        store.setPluginInfo(pluginInfo);
        SecurityAuthConfig authConfig = new SecurityAuthConfig("id", "plugin_id", new ConfigurationProperty(new ConfigurationKey("password"), new ConfigurationValue("pass")));
        authConfig.encryptSecureConfigurations();
        Assert.assertThat(authConfig.size(), Matchers.is(1));
        Assert.assertTrue(authConfig.first().isSecure());
    }

    @Test
    public void postConstruct_shouldIgnoreEncryptionIfPluginInfoIsNotDefined() throws Exception {
        SecurityAuthConfig authConfig = new SecurityAuthConfig("id", "plugin_id", new ConfigurationProperty(new ConfigurationKey("password"), new ConfigurationValue("pass")));
        authConfig.encryptSecureConfigurations();
        Assert.assertThat(authConfig.size(), Matchers.is(1));
        Assert.assertFalse(authConfig.first().isSecure());
    }
}

