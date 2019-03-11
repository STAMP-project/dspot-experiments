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
package com.thoughtworks.go.plugin.access.elastic;


import com.thoughtworks.go.domain.JobIdentifier;
import com.thoughtworks.go.plugin.access.elastic.models.AgentMetadata;
import com.thoughtworks.go.plugin.access.elastic.v3.ElasticAgentExtensionV3;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.validation.ValidationError;
import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;
import com.thoughtworks.go.plugin.domain.common.Image;
import com.thoughtworks.go.plugin.domain.common.Metadata;
import com.thoughtworks.go.plugin.domain.common.PluginConfiguration;
import com.thoughtworks.go.plugin.domain.common.PluginConstants;
import com.thoughtworks.go.plugin.domain.elastic.Capabilities;
import com.thoughtworks.go.plugin.infra.PluginManager;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ElasticAgentExtensionV3Test {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String PLUGIN_ID = "cd.go.example.plugin";

    @Mock
    private PluginManager pluginManager;

    @Mock
    private GoPluginDescriptor descriptor;

    private ArgumentCaptor<GoPluginApiRequest> requestArgumentCaptor;

    private ElasticAgentExtensionV3 extensionV3;

    @Test
    public void shouldGetPluginIcon() {
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(ElasticAgentExtensionV3Test.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ELASTIC_AGENT_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success("{\"content_type\":\"image/png\",\"data\":\"Zm9vYmEK\"}"));
        final Image icon = extensionV3.getIcon(ElasticAgentExtensionV3Test.PLUGIN_ID);
        Assert.assertThat(icon.getContentType(), Matchers.is("image/png"));
        Assert.assertThat(icon.getData(), Matchers.is("Zm9vYmEK"));
        assertExtensionRequest("3.0", REQUEST_GET_PLUGIN_SETTINGS_ICON, null);
    }

    @Test
    public void shouldGetCapabilitiesOfAPlugin() {
        final String responseBody = "{\"supports_status_report\":\"true\", \"supports_agent_status_report\":\"true\"}";
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(ElasticAgentExtensionV3Test.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ELASTIC_AGENT_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(responseBody));
        final Capabilities capabilities = extensionV3.getCapabilities(ElasticAgentExtensionV3Test.PLUGIN_ID);
        Assert.assertTrue(capabilities.supportsStatusReport());
        Assert.assertTrue(capabilities.supportsAgentStatusReport());
    }

    @Test
    public void shouldGetProfileMetadata() {
        String responseBody = "[{\"key\":\"Username\",\"metadata\":{\"required\":true,\"secure\":false}},{\"key\":\"Password\",\"metadata\":{\"required\":true,\"secure\":true}}]";
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(ElasticAgentExtensionV3Test.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ELASTIC_AGENT_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(responseBody));
        final List<PluginConfiguration> metadata = extensionV3.getElasticProfileMetadata(ElasticAgentExtensionV3Test.PLUGIN_ID);
        Assert.assertThat(metadata, Matchers.hasSize(2));
        Assert.assertThat(metadata, Matchers.containsInAnyOrder(new PluginConfiguration("Username", new Metadata(true, false)), new PluginConfiguration("Password", new Metadata(true, true))));
        assertExtensionRequest("3.0", REQUEST_GET_PROFILE_METADATA, null);
    }

    @Test
    public void shouldGetProfileView() {
        String responseBody = "{ \"template\": \"<div>This is profile view snippet</div>\" }";
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(ElasticAgentExtensionV3Test.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ELASTIC_AGENT_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(responseBody));
        final String view = extensionV3.getElasticProfileView(ElasticAgentExtensionV3Test.PLUGIN_ID);
        Assert.assertThat(view, Matchers.is("<div>This is profile view snippet</div>"));
        assertExtensionRequest("3.0", REQUEST_GET_PROFILE_VIEW, null);
    }

    @Test
    public void shouldValidateProfile() {
        String responseBody = "[{\"message\":\"Url must not be blank.\",\"key\":\"Url\"},{\"message\":\"SearchBase must not be blank.\",\"key\":\"SearchBase\"}]";
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(ElasticAgentExtensionV3Test.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ELASTIC_AGENT_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(responseBody));
        final ValidationResult result = extensionV3.validateElasticProfile(ElasticAgentExtensionV3Test.PLUGIN_ID, Collections.emptyMap());
        Assert.assertThat(result.isSuccessful(), Matchers.is(false));
        Assert.assertThat(result.getErrors(), Matchers.containsInAnyOrder(new ValidationError("Url", "Url must not be blank."), new ValidationError("SearchBase", "SearchBase must not be blank.")));
        assertExtensionRequest("3.0", REQUEST_VALIDATE_PROFILE, "{}");
    }

    @Test
    public void shouldMakeCreateAgentCall() {
        final Map<String, String> profile = Collections.singletonMap("ServerURL", "https://example.com/go");
        final JobIdentifier jobIdentifier = new JobIdentifier("up42", 2, "Test", "up42_stage", "10", "up42_job");
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(ElasticAgentExtensionV3Test.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ELASTIC_AGENT_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(null));
        extensionV3.createAgent(ElasticAgentExtensionV3Test.PLUGIN_ID, "auto-registration-key", "test-env", profile, jobIdentifier);
        String expectedRequestBody = "{\n" + (((((((((((((("  \"auto_register_key\": \"auto-registration-key\",\n" + "  \"properties\": {\n") + "    \"ServerURL\": \"https://example.com/go\"\n") + "  },\n") + "  \"environment\": \"test-env\",\n") + "  \"job_identifier\": {\n") + "    \"pipeline_name\": \"up42\",\n") + "    \"pipeline_label\": \"Test\",\n") + "    \"pipeline_counter\": 2,\n") + "    \"stage_name\": \"up42_stage\",\n") + "    \"stage_counter\": \"10\",\n") + "    \"job_name\": \"up42_job\",\n") + "    \"job_id\": -1\n") + "  }\n") + "}");
        assertExtensionRequest("3.0", REQUEST_CREATE_AGENT, expectedRequestBody);
    }

    @Test
    public void shouldSendServerPing() {
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(ElasticAgentExtensionV3Test.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ELASTIC_AGENT_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(null));
        extensionV3.serverPing(ElasticAgentExtensionV3Test.PLUGIN_ID);
        assertExtensionRequest("3.0", REQUEST_SERVER_PING, null);
    }

    @Test
    public void shouldMakeShouldAssignWorkCall() {
        final Map<String, String> profile = Collections.singletonMap("ServerURL", "https://example.com/go");
        final AgentMetadata agentMetadata = new AgentMetadata("foo-agent-id", "Idle", "Idle", "Enabled");
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(ElasticAgentExtensionV3Test.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ELASTIC_AGENT_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success("true"));
        final boolean shouldAssignWork = extensionV3.shouldAssignWork(ElasticAgentExtensionV3Test.PLUGIN_ID, agentMetadata, "test-env", profile, new JobIdentifier());
        Assert.assertTrue(shouldAssignWork);
        String expectedRequestBody = "{\n" + ((((((((((("  \"properties\": {\n" + "    \"ServerURL\": \"https://example.com/go\"\n") + "  },\n") + "  \"environment\": \"test-env\",\n") + "  \"agent\": {\n") + "    \"agent_id\": \"foo-agent-id\",\n") + "    \"agent_state\": \"Idle\",\n") + "    \"build_state\": \"Idle\",\n") + "    \"config_state\": \"Enabled\"\n") + "  },\n") + "  \"job_identifier\": {}\n") + "}");
        assertExtensionRequest("3.0", REQUEST_SHOULD_ASSIGN_WORK, expectedRequestBody);
    }

    @Test
    public void shouldGetStatusReport() {
        final String responseBody = "{\"view\":\"<div>This is a status report snippet.</div>\"}";
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(ElasticAgentExtensionV3Test.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ELASTIC_AGENT_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(responseBody));
        final String statusReportView = extensionV3.getPluginStatusReport(ElasticAgentExtensionV3Test.PLUGIN_ID);
        Assert.assertThat(statusReportView, Matchers.is("<div>This is a status report snippet.</div>"));
        assertExtensionRequest("3.0", REQUEST_STATUS_REPORT, null);
    }

    @Test
    public void shouldGetAgentStatusReport() {
        final String responseBody = "{\"view\":\"<div>This is a status report snippet.</div>\"}";
        final JobIdentifier jobIdentifier = new JobIdentifier("up42", 2, "Test", "up42_stage", "10", "up42_job");
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(ElasticAgentExtensionV3Test.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ELASTIC_AGENT_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(responseBody));
        extensionV3.getAgentStatusReport(ElasticAgentExtensionV3Test.PLUGIN_ID, jobIdentifier, "GoCD193659b3b930480287b898eeef0ade37");
        final String requestBody = "{\n" + (((((((((("  \"job_identifier\": {\n" + "    \"pipeline_name\": \"up42\",\n") + "    \"pipeline_label\": \"Test\",\n") + "    \"pipeline_counter\": 2,\n") + "    \"stage_name\": \"up42_stage\",\n") + "    \"stage_counter\": \"10\",\n") + "    \"job_name\": \"up42_job\",\n") + "    \"job_id\": -1\n") + "  },\n") + "  \"elastic_agent_id\": \"GoCD193659b3b930480287b898eeef0ade37\"\n") + "}");
        assertExtensionRequest("3.0", REQUEST_AGENT_STATUS_REPORT, requestBody);
    }

    @Test
    public void shouldDoNothingWhenPluginDoesNotJobCompletionRequest() {
        String elasticAgentId = "agent1";
        final JobIdentifier jobIdentifier = new JobIdentifier("up42", 2, "Test", "up42_stage", "10", "up42_job");
        extensionV3.jobCompletion(ElasticAgentExtensionV3Test.PLUGIN_ID, elasticAgentId, jobIdentifier);
        Mockito.verifyZeroInteractions(pluginManager);
    }

    @Test
    public void allRequestMustHaveRequestPrefix() {
        Assert.assertThat(REQUEST_PREFIX, Matchers.is("cd.go.elastic-agent"));
        Assert.assertThat(REQUEST_CREATE_AGENT, Matchers.startsWith(REQUEST_PREFIX));
        Assert.assertThat(REQUEST_SERVER_PING, Matchers.startsWith(REQUEST_PREFIX));
        Assert.assertThat(REQUEST_SHOULD_ASSIGN_WORK, Matchers.startsWith(REQUEST_PREFIX));
        Assert.assertThat(REQUEST_GET_PROFILE_METADATA, Matchers.startsWith(REQUEST_PREFIX));
        Assert.assertThat(REQUEST_GET_PROFILE_VIEW, Matchers.startsWith(REQUEST_PREFIX));
        Assert.assertThat(REQUEST_VALIDATE_PROFILE, Matchers.startsWith(REQUEST_PREFIX));
        Assert.assertThat(REQUEST_GET_PLUGIN_SETTINGS_ICON, Matchers.startsWith(REQUEST_PREFIX));
    }
}

