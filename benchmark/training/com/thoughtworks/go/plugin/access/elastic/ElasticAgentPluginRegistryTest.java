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
import com.thoughtworks.go.plugin.infra.PluginManager;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import java.util.Collections;
import java.util.Map;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ElasticAgentPluginRegistryTest {
    private static final String PLUGIN_ID = "cd.go.example.plugin";

    @Mock
    private PluginManager pluginManager;

    @Mock
    private ElasticAgentExtension elasticAgentExtension;

    @Mock
    private GoPluginDescriptor pluginDescriptor;

    private ElasticAgentPluginRegistry elasticAgentPluginRegistry;

    @Test
    public void shouldTalkToExtensionToCreateElasticAgent() {
        final Map<String, String> configuration = Collections.singletonMap("GoServerURL", "foo");
        final JobIdentifier jobIdentifier = new JobIdentifier();
        final String autoRegisterKey = "auto-register-key";
        final String environment = "test-env";
        elasticAgentPluginRegistry.createAgent(ElasticAgentPluginRegistryTest.PLUGIN_ID, autoRegisterKey, environment, configuration, jobIdentifier);
        Mockito.verify(elasticAgentExtension, Mockito.times(1)).createAgent(ElasticAgentPluginRegistryTest.PLUGIN_ID, autoRegisterKey, environment, configuration, jobIdentifier);
        Mockito.verifyNoMoreInteractions(elasticAgentExtension);
    }

    @Test
    public void shouldTalkToExtensionToExecuteServerPingCall() {
        elasticAgentPluginRegistry.serverPing(ElasticAgentPluginRegistryTest.PLUGIN_ID);
        Mockito.verify(elasticAgentExtension, Mockito.times(1)).serverPing(ElasticAgentPluginRegistryTest.PLUGIN_ID);
        Mockito.verifyNoMoreInteractions(elasticAgentExtension);
    }

    @Test
    public void shouldTalkToExtensionToExecuteShouldAssignWorkCall() {
        final String environment = "test-env";
        final JobIdentifier jobIdentifier = new JobIdentifier();
        final Map<String, String> configuration = Collections.singletonMap("GoServerURL", "foo");
        final AgentMetadata agentMetadata = new AgentMetadata("som-id", "Idle", "Idle", "Enabled");
        elasticAgentPluginRegistry.shouldAssignWork(pluginDescriptor, agentMetadata, environment, configuration, jobIdentifier);
        Mockito.verify(elasticAgentExtension, Mockito.times(1)).shouldAssignWork(ElasticAgentPluginRegistryTest.PLUGIN_ID, agentMetadata, environment, configuration, jobIdentifier);
        Mockito.verifyNoMoreInteractions(elasticAgentExtension);
    }

    @Test
    public void shouldTalkToExtensionToGetPluginStatusReport() {
        elasticAgentPluginRegistry.getPluginStatusReport(ElasticAgentPluginRegistryTest.PLUGIN_ID);
        Mockito.verify(elasticAgentExtension, Mockito.times(1)).getPluginStatusReport(ElasticAgentPluginRegistryTest.PLUGIN_ID);
        Mockito.verifyNoMoreInteractions(elasticAgentExtension);
    }

    @Test
    public void shouldTalkToExtensionToGetAgentStatusReport() {
        final JobIdentifier jobIdentifier = new JobIdentifier();
        elasticAgentPluginRegistry.getAgentStatusReport(ElasticAgentPluginRegistryTest.PLUGIN_ID, jobIdentifier, "some-id");
        Mockito.verify(elasticAgentExtension, Mockito.times(1)).getAgentStatusReport(ElasticAgentPluginRegistryTest.PLUGIN_ID, jobIdentifier, "some-id");
        Mockito.verifyNoMoreInteractions(elasticAgentExtension);
    }

    @Test
    public void shouldTalkToExtensionToReportJobCompletion() {
        final JobIdentifier jobIdentifier = new JobIdentifier();
        final String elasticAgentId = "ea_1";
        elasticAgentPluginRegistry.reportJobCompletion(ElasticAgentPluginRegistryTest.PLUGIN_ID, elasticAgentId, jobIdentifier);
        Mockito.verify(elasticAgentExtension, Mockito.times(1)).reportJobCompletion(ElasticAgentPluginRegistryTest.PLUGIN_ID, elasticAgentId, jobIdentifier);
        Mockito.verifyNoMoreInteractions(elasticAgentExtension);
    }
}

