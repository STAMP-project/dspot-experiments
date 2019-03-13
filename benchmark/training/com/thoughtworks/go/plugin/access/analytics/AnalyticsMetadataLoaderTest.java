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
package com.thoughtworks.go.plugin.access.analytics;


import com.thoughtworks.go.plugin.access.common.PluginMetadataChangeListener;
import com.thoughtworks.go.plugin.domain.analytics.AnalyticsPluginInfo;
import com.thoughtworks.go.plugin.infra.PluginManager;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class AnalyticsMetadataLoaderTest {
    private AnalyticsExtension extension;

    private AnalyticsPluginInfoBuilder infoBuilder;

    private AnalyticsMetadataStore metadataStore;

    private PluginManager pluginManager;

    @Test
    public void shouldBeAPluginChangeListener() throws Exception {
        AnalyticsMetadataLoader analyticsMetadataLoader = new AnalyticsMetadataLoader(pluginManager, metadataStore, infoBuilder, extension);
        Mockito.verify(pluginManager).addPluginChangeListener(ArgumentMatchers.eq(analyticsMetadataLoader));
    }

    @Test
    public void onPluginLoaded_shouldAddPluginInfoToMetadataStore() throws Exception {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        AnalyticsMetadataLoader metadataLoader = new AnalyticsMetadataLoader(pluginManager, metadataStore, infoBuilder, extension);
        AnalyticsPluginInfo pluginInfo = new AnalyticsPluginInfo(descriptor, null, null, null);
        Mockito.when(extension.canHandlePlugin(descriptor.id())).thenReturn(true);
        Mockito.when(infoBuilder.pluginInfoFor(descriptor)).thenReturn(pluginInfo);
        metadataLoader.pluginLoaded(descriptor);
        Mockito.verify(metadataStore).setPluginInfo(pluginInfo);
    }

    @Test
    public void onPluginLoad_shouldNotifyPluginMetadataLoadListeners() throws Exception {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        AnalyticsMetadataLoader metadataLoader = new AnalyticsMetadataLoader(pluginManager, metadataStore, infoBuilder, extension);
        PluginMetadataChangeListener pluginMetadataChangeListener = Mockito.mock(PluginMetadataChangeListener.class);
        AnalyticsPluginInfo pluginInfo = new AnalyticsPluginInfo(descriptor, null, null, null);
        Mockito.when(extension.canHandlePlugin(descriptor.id())).thenReturn(true);
        Mockito.when(infoBuilder.pluginInfoFor(descriptor)).thenReturn(pluginInfo);
        metadataLoader.registerListeners(pluginMetadataChangeListener);
        metadataLoader.pluginLoaded(descriptor);
        InOrder inOrder = Mockito.inOrder(metadataStore, pluginMetadataChangeListener);
        inOrder.verify(metadataStore).setPluginInfo(pluginInfo);
        inOrder.verify(pluginMetadataChangeListener).onPluginMetadataCreate(descriptor.id());
    }

    @Test
    public void onPluginLoaded_shouldIgnoreNonAnalyticsPlugins() throws Exception {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        AnalyticsMetadataLoader metadataLoader = new AnalyticsMetadataLoader(pluginManager, metadataStore, infoBuilder, extension);
        Mockito.when(extension.canHandlePlugin(descriptor.id())).thenReturn(false);
        metadataLoader.pluginLoaded(descriptor);
        Mockito.verifyZeroInteractions(infoBuilder);
        Mockito.verifyZeroInteractions(metadataStore);
    }

    @Test
    public void onPluginUnloaded_shouldRemoveTheCorrespondingPluginInfoFromStore() throws Exception {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        AnalyticsMetadataLoader metadataLoader = new AnalyticsMetadataLoader(pluginManager, metadataStore, infoBuilder, extension);
        AnalyticsPluginInfo pluginInfo = new AnalyticsPluginInfo(descriptor, null, null, null);
        metadataStore.setPluginInfo(pluginInfo);
        metadataLoader.pluginUnLoaded(descriptor);
        Mockito.verify(metadataStore).remove(descriptor.id());
    }

    @Test
    public void onPluginUnLoaded_shouldNotifyPluginMetadataLoadListeners() throws Exception {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        AnalyticsMetadataLoader metadataLoader = new AnalyticsMetadataLoader(pluginManager, metadataStore, infoBuilder, extension);
        AnalyticsPluginInfo pluginInfo = new AnalyticsPluginInfo(descriptor, null, null, null);
        PluginMetadataChangeListener pluginMetadataChangeListener = Mockito.mock(PluginMetadataChangeListener.class);
        Mockito.when(extension.canHandlePlugin(descriptor.id())).thenReturn(true);
        metadataStore.setPluginInfo(pluginInfo);
        metadataLoader.registerListeners(pluginMetadataChangeListener);
        metadataLoader.pluginUnLoaded(descriptor);
        InOrder inOrder = Mockito.inOrder(metadataStore, pluginMetadataChangeListener);
        inOrder.verify(metadataStore).remove(descriptor.id());
        inOrder.verify(pluginMetadataChangeListener).onPluginMetadataRemove(descriptor.id());
    }
}

