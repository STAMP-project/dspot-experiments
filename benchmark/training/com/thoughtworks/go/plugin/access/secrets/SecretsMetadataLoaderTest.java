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
import com.thoughtworks.go.plugin.infra.PluginManager;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class SecretsMetadataLoaderTest {
    private SecretsExtension extension;

    private SecretsPluginInfoBuilder infoBuilder;

    private SecretsMetadataStore metadataStore;

    private PluginManager pluginManager;

    @Test
    public void shouldBeAPluginChangeListener() {
        SecretsMetadataLoader analyticsMetadataLoader = new SecretsMetadataLoader(pluginManager, metadataStore, infoBuilder, extension);
        Mockito.verify(pluginManager).addPluginChangeListener(ArgumentMatchers.eq(analyticsMetadataLoader));
    }

    @Test
    public void onPluginLoaded_shouldAddPluginInfoToMetadataStore() {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        SecretsMetadataLoader metadataLoader = new SecretsMetadataLoader(pluginManager, metadataStore, infoBuilder, extension);
        SecretsPluginInfo pluginInfo = new SecretsPluginInfo(descriptor, null, null);
        Mockito.when(extension.canHandlePlugin(descriptor.id())).thenReturn(true);
        Mockito.when(infoBuilder.pluginInfoFor(descriptor)).thenReturn(pluginInfo);
        metadataLoader.pluginLoaded(descriptor);
        Mockito.verify(metadataStore).setPluginInfo(pluginInfo);
    }

    @Test
    public void onPluginLoaded_shouldIgnoreNonSecretsPlugins() {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        SecretsMetadataLoader metadataLoader = new SecretsMetadataLoader(pluginManager, metadataStore, infoBuilder, extension);
        Mockito.when(extension.canHandlePlugin(descriptor.id())).thenReturn(false);
        metadataLoader.pluginLoaded(descriptor);
        Mockito.verifyZeroInteractions(infoBuilder);
        Mockito.verifyZeroInteractions(metadataStore);
    }

    @Test
    public void onPluginUnloaded_shouldRemoveTheCorrespondingPluginInfoFromStore() {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        SecretsMetadataLoader metadataLoader = new SecretsMetadataLoader(pluginManager, metadataStore, infoBuilder, extension);
        SecretsPluginInfo pluginInfo = new SecretsPluginInfo(descriptor, null, null);
        metadataStore.setPluginInfo(pluginInfo);
        metadataLoader.pluginUnLoaded(descriptor);
        Mockito.verify(metadataStore).remove(descriptor.id());
    }
}

