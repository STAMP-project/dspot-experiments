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


import SecretsExtensionV1.VERSION;
import com.thoughtworks.go.config.SecretConfig;
import com.thoughtworks.go.plugin.access.ExtensionsRegistry;
import com.thoughtworks.go.plugin.access.common.AbstractExtension;
import com.thoughtworks.go.plugin.access.secrets.v1.SecretsExtensionV1;
import com.thoughtworks.go.plugin.domain.common.PluginConstants;
import com.thoughtworks.go.plugin.infra.PluginManager;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import com.thoughtworks.go.util.ReflectionUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class SecretsExtensionTest {
    private static final String PLUGIN_ID = "cd.go.example.plugin";

    protected PluginManager pluginManager;

    private ExtensionsRegistry extensionsRegistry;

    protected GoPluginDescriptor descriptor;

    protected SecretsExtension extension;

    @Test
    public void shouldHaveVersionedSecretsExtensionForAllSupportedVersions() {
        for (String supportedVersion : SecretsExtension.SUPPORTED_VERSIONS) {
            final String message = String.format("Must define versioned extension class for %s extension with version %s", PluginConstants.SECRETS_EXTENSION, supportedVersion);
            Mockito.when(pluginManager.resolveExtensionVersion(SecretsExtensionTest.PLUGIN_ID, PluginConstants.SECRETS_EXTENSION, SecretsExtension.SUPPORTED_VERSIONS)).thenReturn(supportedVersion);
            final VersionedSecretsExtension extension = this.extension.getVersionedSecretsExtension(SecretsExtensionTest.PLUGIN_ID);
            Assert.assertNotNull(message, extension);
            Assert.assertThat(ReflectionUtil.getField(extension, "VERSION"), Matchers.is(supportedVersion));
        }
    }

    @Test
    public void getIcon_shouldDelegateToVersionedExtension() {
        SecretsExtensionV1 secretsExtensionV1 = Mockito.mock(SecretsExtensionV1.class);
        Map<String, VersionedSecretsExtension> secretsExtensionMap = Collections.singletonMap("1.0", secretsExtensionV1);
        extension = new SecretsExtension(pluginManager, extensionsRegistry, secretsExtensionMap);
        Mockito.when(pluginManager.resolveExtensionVersion(SecretsExtensionTest.PLUGIN_ID, PluginConstants.SECRETS_EXTENSION, SecretsExtension.SUPPORTED_VERSIONS)).thenReturn(VERSION);
        this.extension.getIcon(SecretsExtensionTest.PLUGIN_ID);
        Mockito.verify(secretsExtensionV1).getIcon(SecretsExtensionTest.PLUGIN_ID);
    }

    @Test
    public void getSecretsConfigMetadata_shouldDelegateToVersionedExtension() {
        SecretsExtensionV1 secretsExtensionV1 = Mockito.mock(SecretsExtensionV1.class);
        Map<String, VersionedSecretsExtension> secretsExtensionMap = Collections.singletonMap("1.0", secretsExtensionV1);
        extension = new SecretsExtension(pluginManager, extensionsRegistry, secretsExtensionMap);
        Mockito.when(pluginManager.resolveExtensionVersion(SecretsExtensionTest.PLUGIN_ID, PluginConstants.SECRETS_EXTENSION, SecretsExtension.SUPPORTED_VERSIONS)).thenReturn(VERSION);
        this.extension.getSecretsConfigMetadata(SecretsExtensionTest.PLUGIN_ID);
        Mockito.verify(secretsExtensionV1).getSecretsConfigMetadata(SecretsExtensionTest.PLUGIN_ID);
    }

    @Test
    public void getSecretsConfigView_shouldDelegateToVersionedExtension() {
        SecretsExtensionV1 secretsExtensionV1 = Mockito.mock(SecretsExtensionV1.class);
        Map<String, VersionedSecretsExtension> secretsExtensionMap = Collections.singletonMap("1.0", secretsExtensionV1);
        extension = new SecretsExtension(pluginManager, extensionsRegistry, secretsExtensionMap);
        Mockito.when(pluginManager.resolveExtensionVersion(SecretsExtensionTest.PLUGIN_ID, PluginConstants.SECRETS_EXTENSION, SecretsExtension.SUPPORTED_VERSIONS)).thenReturn(VERSION);
        this.extension.getSecretsConfigView(SecretsExtensionTest.PLUGIN_ID);
        Mockito.verify(secretsExtensionV1).getSecretsConfigView(SecretsExtensionTest.PLUGIN_ID);
    }

    @Test
    public void validateSecretsConfig_shouldDelegateToVersionedExtension() {
        SecretsExtensionV1 secretsExtensionV1 = Mockito.mock(SecretsExtensionV1.class);
        Map<String, VersionedSecretsExtension> secretsExtensionMap = Collections.singletonMap("1.0", secretsExtensionV1);
        extension = new SecretsExtension(pluginManager, extensionsRegistry, secretsExtensionMap);
        Map<String, String> configuration = Collections.singletonMap("key", "val");
        Mockito.when(pluginManager.resolveExtensionVersion(SecretsExtensionTest.PLUGIN_ID, PluginConstants.SECRETS_EXTENSION, SecretsExtension.SUPPORTED_VERSIONS)).thenReturn(VERSION);
        this.extension.validateSecretsConfig(SecretsExtensionTest.PLUGIN_ID, configuration);
        Mockito.verify(secretsExtensionV1).validateSecretsConfig(SecretsExtensionTest.PLUGIN_ID, configuration);
    }

    @Test
    public void lookupSecrets_shouldDelegateToVersionedExtension() {
        SecretsExtensionV1 secretsExtensionV1 = Mockito.mock(SecretsExtensionV1.class);
        Map<String, VersionedSecretsExtension> secretsExtensionMap = Collections.singletonMap("1.0", secretsExtensionV1);
        extension = new SecretsExtension(pluginManager, extensionsRegistry, secretsExtensionMap);
        List<String> keys = Arrays.asList("key1", "key2");
        Mockito.when(pluginManager.resolveExtensionVersion(SecretsExtensionTest.PLUGIN_ID, PluginConstants.SECRETS_EXTENSION, SecretsExtension.SUPPORTED_VERSIONS)).thenReturn(VERSION);
        this.extension.lookupSecrets(SecretsExtensionTest.PLUGIN_ID, keys, new SecretConfig());
        Mockito.verify(secretsExtensionV1).lookupSecrets(SecretsExtensionTest.PLUGIN_ID, keys, new SecretConfig());
    }

    @Test
    public void shouldExtendAbstractExtension() {
        Assert.assertTrue(((new SecretsExtension(pluginManager, extensionsRegistry)) instanceof AbstractExtension));
    }
}

