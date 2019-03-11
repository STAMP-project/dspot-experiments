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
package com.thoughtworks.go.plugin.access.analytics.V2.models;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CapabilitiesTest {
    @Test
    public void shouldDeserializeFromJSON() throws Exception {
        String json = "{\n" + (((("\"supported_analytics\": [\n" + "  {\"type\": \"dashboard\", \"id\": \"abc\",  \"title\": \"Title 1\"},\n") + "  {\"type\": \"pipeline\", \"id\": \"abc\",  \"title\": \"Title 2\"},\n") + "  {\"type\": \"vsm\", \"id\": \"abc\",  \"title\": \"Title 3\", \"required_params\": [\"param1\", \"param2\"]}\n") + "]}");
        Capabilities capabilities = Capabilities.fromJSON(json);
        Assert.assertThat(capabilities.getSupportedAnalytics().size(), Matchers.is(3));
        Assert.assertThat(capabilities.getSupportedAnalytics().get(0), Matchers.is(new SupportedAnalytics("dashboard", "abc", "Title 1")));
        Assert.assertThat(capabilities.getSupportedAnalytics().get(1), Matchers.is(new SupportedAnalytics("pipeline", "abc", "Title 2")));
        Assert.assertThat(capabilities.getSupportedAnalytics().get(2), Matchers.is(new SupportedAnalytics("vsm", "abc", "Title 3")));
    }

    @Test
    public void shouldConvertToDomainCapabilities() throws Exception {
        String json = "{\n" + (((("\"supported_analytics\": [\n" + "  {\"type\": \"dashboard\", \"id\": \"abc\",  \"title\": \"Title 1\"},\n") + "  {\"type\": \"pipeline\", \"id\": \"abc\",  \"title\": \"Title 2\"},\n") + "  {\"type\": \"vsm\", \"id\": \"abc\",  \"title\": \"Title 3\", \"required_params\": [\"param1\", \"param2\"]}\n") + "]}");
        Capabilities capabilities = Capabilities.fromJSON(json);
        com.thoughtworks.go.plugin.domain.analytics.Capabilities domain = capabilities.toCapabilities();
        Assert.assertThat(domain.supportedDashboardAnalytics(), Matchers.containsInAnyOrder(new com.thoughtworks.go.plugin.domain.analytics.SupportedAnalytics("dashboard", "abc", "Title 1")));
        Assert.assertThat(domain.supportedPipelineAnalytics(), Matchers.containsInAnyOrder(new com.thoughtworks.go.plugin.domain.analytics.SupportedAnalytics("pipeline", "abc", "Title 2")));
        Assert.assertThat(domain.supportedVSMAnalytics(), Matchers.containsInAnyOrder(new com.thoughtworks.go.plugin.domain.analytics.SupportedAnalytics("vsm", "abc", "Title 3")));
    }
}

