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
import com.thoughtworks.go.plugin.access.ExtensionsRegistry;
import com.thoughtworks.go.plugin.access.common.AbstractExtension;
import com.thoughtworks.go.plugin.access.elastic.v3.ElasticAgentPluginConstantsV3;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.domain.common.PluginConstants;
import com.thoughtworks.go.plugin.infra.PluginManager;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import com.thoughtworks.go.util.ReflectionUtil;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ElasticAgentExtensionTest {
    private static final String PLUGIN_ID = "cd.go.example.plugin";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    protected PluginManager pluginManager;

    private ExtensionsRegistry extensionsRegistry;

    protected ArgumentCaptor<GoPluginApiRequest> requestArgumentCaptor;

    protected GoPluginDescriptor descriptor;

    protected ElasticAgentExtension extension;

    @Test
    public void shouldHaveVersionedElasticAgentExtensionForAllSupportedVersions() {
        for (String supportedVersion : ElasticAgentExtension.SUPPORTED_VERSIONS) {
            final String message = String.format("Must define versioned extension class for %s extension with version %s", PluginConstants.ELASTIC_AGENT_EXTENSION, supportedVersion);
            Mockito.when(pluginManager.resolveExtensionVersion(ElasticAgentExtensionTest.PLUGIN_ID, PluginConstants.ELASTIC_AGENT_EXTENSION, ElasticAgentExtension.SUPPORTED_VERSIONS)).thenReturn(supportedVersion);
            final VersionedElasticAgentExtension extension = this.extension.getVersionedElasticAgentExtension(ElasticAgentExtensionTest.PLUGIN_ID);
            Assert.assertNotNull(message, extension);
            Assert.assertThat(ReflectionUtil.getField(extension, "VERSION"), Matchers.is(supportedVersion));
        }
    }

    @Test
    public void shouldCallTheVersionedExtensionBasedOnResolvedVersion() {
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(ElasticAgentExtensionTest.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ELASTIC_AGENT_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success("{\"content_type\":\"image/png\",\"data\":\"Zm9vYmEK\"}"));
        extension.getIcon(ElasticAgentExtensionTest.PLUGIN_ID);
        assertExtensionRequest("3.0", ElasticAgentPluginConstantsV3.REQUEST_GET_PLUGIN_SETTINGS_ICON, null);
    }

    @Test
    public void shouldExtendAbstractExtension() {
        Assert.assertTrue(((new ElasticAgentExtension(pluginManager, extensionsRegistry)) instanceof AbstractExtension));
    }

    @Test
    public void shouldMakeJobCompletionCall() {
        Mockito.when(pluginManager.resolveExtensionVersion(ElasticAgentExtensionTest.PLUGIN_ID, PluginConstants.ELASTIC_AGENT_EXTENSION, ElasticAgentExtension.SUPPORTED_VERSIONS)).thenReturn("4.0");
        final String elasticAgentId = "ea1";
        final JobIdentifier jobIdentifier = new JobIdentifier("up42", 2, "Test", "up42_stage", "10", "up42_job");
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(ElasticAgentExtensionTest.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ELASTIC_AGENT_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(null));
        extension.reportJobCompletion(ElasticAgentExtensionTest.PLUGIN_ID, elasticAgentId, jobIdentifier);
        Mockito.verify(pluginManager, Mockito.times(1)).submitTo(ArgumentMatchers.eq(ElasticAgentExtensionTest.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ELASTIC_AGENT_EXTENSION), ArgumentMatchers.any(GoPluginApiRequest.class));
    }
}

