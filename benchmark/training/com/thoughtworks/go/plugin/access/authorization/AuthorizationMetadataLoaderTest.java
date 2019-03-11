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
package com.thoughtworks.go.plugin.access.authorization;


import com.thoughtworks.go.plugin.domain.authorization.AuthorizationPluginInfo;
import com.thoughtworks.go.plugin.infra.PluginManager;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class AuthorizationMetadataLoaderTest {
    private AuthorizationExtension extension;

    private AuthorizationPluginInfoBuilder infoBuilder;

    private AuthorizationMetadataStore metadataStore;

    private PluginManager pluginManager;

    @Test
    public void shouldBeAPluginChangeListener() throws Exception {
        AuthorizationMetadataLoader authorizationMetadataLoader = new AuthorizationMetadataLoader(pluginManager, metadataStore, infoBuilder, extension);
        Mockito.verify(pluginManager).addPluginChangeListener(ArgumentMatchers.eq(authorizationMetadataLoader));
    }

    @Test
    public void onPluginLoaded_shouldAddPluginInfoToMetadataStore() throws Exception {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        AuthorizationMetadataLoader metadataLoader = new AuthorizationMetadataLoader(pluginManager, metadataStore, infoBuilder, extension);
        AuthorizationPluginInfo pluginInfo = new AuthorizationPluginInfo(descriptor, null, null, null, null);
        Mockito.when(extension.canHandlePlugin(descriptor.id())).thenReturn(true);
        Mockito.when(infoBuilder.pluginInfoFor(descriptor)).thenReturn(pluginInfo);
        metadataLoader.pluginLoaded(descriptor);
        Mockito.verify(metadataStore).setPluginInfo(pluginInfo);
    }

    @Test
    public void onPluginLoaded_shouldIgnoreNonAuthorizationPlugins() throws Exception {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        AuthorizationMetadataLoader metadataLoader = new AuthorizationMetadataLoader(pluginManager, metadataStore, infoBuilder, extension);
        Mockito.when(extension.canHandlePlugin(descriptor.id())).thenReturn(false);
        metadataLoader.pluginLoaded(descriptor);
        Mockito.verifyZeroInteractions(infoBuilder);
        Mockito.verifyZeroInteractions(metadataStore);
    }

    @Test
    public void onPluginUnloded_shouldRemoveTheCorrespondingPluginInfoFromStore() throws Exception {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        AuthorizationMetadataLoader metadataLoader = new AuthorizationMetadataLoader(pluginManager, metadataStore, infoBuilder, extension);
        AuthorizationPluginInfo pluginInfo = new AuthorizationPluginInfo(descriptor, null, null, null, null);
        metadataStore.setPluginInfo(pluginInfo);
        metadataLoader.pluginUnLoaded(descriptor);
        Mockito.verify(metadataStore).remove(descriptor.id());
    }
}

