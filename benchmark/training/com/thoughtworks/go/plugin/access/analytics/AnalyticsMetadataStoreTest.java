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


import com.thoughtworks.go.plugin.api.info.PluginDescriptor;
import com.thoughtworks.go.plugin.domain.analytics.AnalyticsPluginInfo;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class AnalyticsMetadataStoreTest {
    private AnalyticsMetadataStore store = AnalyticsMetadataStore.instance();

    @Test
    public void shouldHandleUpdateAssetsPath() throws Exception {
        PluginDescriptor pluginDescriptor = Mockito.mock(PluginDescriptor.class);
        AnalyticsPluginInfo pluginInfo = new AnalyticsPluginInfo(pluginDescriptor, null, null, null);
        Mockito.when(pluginDescriptor.id()).thenReturn("plugin_id");
        store.setPluginInfo(pluginInfo);
        store.updateAssetsPath("plugin_id", "static_assets_path");
        Assert.assertThat(pluginInfo.getStaticAssetsPath(), Matchers.is("static_assets_path"));
    }
}

