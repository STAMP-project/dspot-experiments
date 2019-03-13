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


import PluginConstants.ANALYTICS_EXTENSION;
import com.thoughtworks.go.plugin.access.ExtensionsRegistry;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.domain.analytics.AnalyticsData;
import com.thoughtworks.go.plugin.domain.analytics.AnalyticsPluginInfo;
import com.thoughtworks.go.plugin.domain.analytics.Capabilities;
import com.thoughtworks.go.plugin.domain.analytics.SupportedAnalytics;
import com.thoughtworks.go.plugin.domain.common.PluginConstants;
import com.thoughtworks.go.plugin.infra.PluginManager;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import java.util.Collections;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class AnalyticsExtensionTest {
    public static final String PLUGIN_ID = "plugin-id";

    @Mock
    private PluginManager pluginManager;

    @Mock
    ExtensionsRegistry extensionsRegistry;

    private ArgumentCaptor<GoPluginApiRequest> requestArgumentCaptor;

    private AnalyticsExtension analyticsExtension;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private AnalyticsMetadataStore metadataStore;

    @Test
    public void shouldTalkToPlugin_To_GetCapabilities() throws Exception {
        String responseBody = "{\n" + ((("\"supported_analytics\": [\n" + "  {\"type\": \"dashboard\", \"id\": \"abc\",  \"title\": \"Title 1\"},\n") + "  {\"type\": \"pipeline\", \"id\": \"abc\",  \"title\": \"Title 1\"}\n") + "]}");
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(AnalyticsExtensionTest.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ANALYTICS_EXTENSION), requestArgumentCaptor.capture())).thenReturn(new DefaultGoPluginApiResponse(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE, responseBody));
        Capabilities capabilities = analyticsExtension.getCapabilities(AnalyticsExtensionTest.PLUGIN_ID);
        assertRequest(requestArgumentCaptor.getValue(), ANALYTICS_EXTENSION, "1.0", REQUEST_GET_CAPABILITIES, null);
        Assert.assertThat(capabilities.supportedDashboardAnalytics(), Matchers.containsInAnyOrder(new SupportedAnalytics("dashboard", "abc", "Title 1")));
        Assert.assertThat(capabilities.supportedPipelineAnalytics(), Matchers.containsInAnyOrder(new SupportedAnalytics("pipeline", "abc", "Title 1")));
    }

    @Test
    public void shouldGetAnalytics() throws Exception {
        String responseBody = "{ \"view_path\": \"path/to/view\", \"data\": \"{}\" }";
        AnalyticsPluginInfo pluginInfo = new AnalyticsPluginInfo(new GoPluginDescriptor(AnalyticsExtensionTest.PLUGIN_ID, null, null, null, null, false), null, null, null);
        pluginInfo.setStaticAssetsPath("/assets/root");
        metadataStore.setPluginInfo(pluginInfo);
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(AnalyticsExtensionTest.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ANALYTICS_EXTENSION), requestArgumentCaptor.capture())).thenReturn(new DefaultGoPluginApiResponse(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE, responseBody));
        AnalyticsData pipelineAnalytics = analyticsExtension.getAnalytics(AnalyticsExtensionTest.PLUGIN_ID, "pipeline", "pipeline_with_highest_wait_time", Collections.singletonMap("pipeline_name", "test_pipeline"));
        String expectedRequestBody = "{" + (("\"type\": \"pipeline\"," + "\"id\": \"pipeline_with_highest_wait_time\",") + " \"params\": {\"pipeline_name\": \"test_pipeline\"}}");
        assertRequest(requestArgumentCaptor.getValue(), ANALYTICS_EXTENSION, "1.0", REQUEST_GET_ANALYTICS, expectedRequestBody);
        Assert.assertThat(pipelineAnalytics.getData(), Matchers.is("{}"));
        Assert.assertThat(pipelineAnalytics.getViewPath(), Matchers.is("path/to/view"));
        Assert.assertThat(pipelineAnalytics.getFullViewPath(), Matchers.is("/assets/root/path/to/view"));
    }

    @Test
    public void shouldFetchStaticAssets() throws Exception {
        String responseBody = "{ \"assets\": \"assets payload\" }";
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(AnalyticsExtensionTest.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ANALYTICS_EXTENSION), requestArgumentCaptor.capture())).thenReturn(new DefaultGoPluginApiResponse(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE, responseBody));
        String assets = analyticsExtension.getStaticAssets(AnalyticsExtensionTest.PLUGIN_ID);
        assertRequest(requestArgumentCaptor.getValue(), PluginConstants.ANALYTICS_EXTENSION, "1.0", REQUEST_GET_STATIC_ASSETS, null);
        Assert.assertThat(assets, Matchers.is("assets payload"));
    }

    @Test
    public void shouldErrorOutInAbsenceOfStaticAssets() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("No assets defined!");
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(AnalyticsExtensionTest.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ANALYTICS_EXTENSION), requestArgumentCaptor.capture())).thenReturn(new DefaultGoPluginApiResponse(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE, "{}"));
        analyticsExtension.getStaticAssets(AnalyticsExtensionTest.PLUGIN_ID);
    }
}

