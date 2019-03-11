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
package com.thoughtworks.go.plugin.domain.analytics;


import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CapabilitiesTest {
    @Test
    public void shouldSupportDashboardAnalyticsIfPluginListsDashboardMetricsAsCapability() {
        Assert.assertTrue(supportsDashboardAnalytics());
        Assert.assertTrue(supportsDashboardAnalytics());
        Assert.assertFalse(new Capabilities(Collections.emptyList()).supportsDashboardAnalytics());
    }

    @Test
    public void shouldSupportPipelineAnalyticsIfPluginListsPipelineMetricsAsCapability() {
        Assert.assertTrue(supportsPipelineAnalytics());
        Assert.assertTrue(supportsPipelineAnalytics());
        Assert.assertFalse(new Capabilities(Collections.emptyList()).supportsPipelineAnalytics());
    }

    @Test
    public void shouldSupportVSMAnalyticsIfPluginListsVSMMetricsAsCapability() {
        Assert.assertTrue(supportsVSMAnalytics());
        Assert.assertTrue(supportsVSMAnalytics());
        Assert.assertFalse(new Capabilities(Collections.emptyList()).supportsPipelineAnalytics());
    }

    @Test
    public void shouldListSupportedDashBoardAnalytics() {
        Capabilities capabilities = new Capabilities(Arrays.asList(new SupportedAnalytics("dashboard", "id1", "title1"), new SupportedAnalytics("DashBoard", "id2", "title2")));
        Assert.assertThat(capabilities.supportedAnalyticsDashboardMetrics(), Matchers.is(Arrays.asList("title1", "title2")));
        Assert.assertTrue(new Capabilities(Collections.emptyList()).supportedAnalyticsDashboardMetrics().isEmpty());
    }

    @Test
    public void shouldListSupportedAnalyticsForDashboard() {
        Capabilities capabilities = new Capabilities(Arrays.asList(new SupportedAnalytics("dashboard", "id1", "title1"), new SupportedAnalytics("DashBoard", "id2", "title2")));
        Assert.assertThat(capabilities.supportedDashboardAnalytics(), Matchers.is(Arrays.asList(new SupportedAnalytics("dashboard", "id1", "title1"), new SupportedAnalytics("DashBoard", "id2", "title2"))));
        Assert.assertTrue(new Capabilities(Collections.emptyList()).supportedDashboardAnalytics().isEmpty());
    }

    @Test
    public void shouldListSupportedAnalyticsForPipelines() {
        Capabilities capabilities = new Capabilities(Arrays.asList(new SupportedAnalytics("pipeline", "id1", "title1"), new SupportedAnalytics("Pipeline", "id2", "title2")));
        Assert.assertThat(capabilities.supportedPipelineAnalytics(), Matchers.is(Arrays.asList(new SupportedAnalytics("pipeline", "id1", "title1"), new SupportedAnalytics("Pipeline", "id2", "title2"))));
        Assert.assertTrue(new Capabilities(Collections.emptyList()).supportedPipelineAnalytics().isEmpty());
    }

    @Test
    public void shouldListSupportedAnalyticsForVSM() {
        Capabilities capabilities = new Capabilities(Arrays.asList(new SupportedAnalytics("vsm", "id1", "title1"), new SupportedAnalytics("VsM", "id2", "title2")));
        Assert.assertThat(capabilities.supportedVSMAnalytics(), Matchers.is(Arrays.asList(new SupportedAnalytics("vsm", "id1", "title1"), new SupportedAnalytics("VsM", "id2", "title2"))));
        Assert.assertTrue(new Capabilities(Collections.emptyList()).supportedVSMAnalytics().isEmpty());
    }
}

