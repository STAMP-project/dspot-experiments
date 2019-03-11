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
package com.thoughtworks.go.plugin.access.artifact;


import ArtifactMessageConverterV1.VERSION;
import com.thoughtworks.go.config.ArtifactStore;
import com.thoughtworks.go.domain.ArtifactPlan;
import com.thoughtworks.go.plugin.access.ExtensionsRegistry;
import com.thoughtworks.go.plugin.access.artifact.model.PublishArtifactResponse;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.validation.ValidationError;
import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;
import com.thoughtworks.go.plugin.domain.common.Metadata;
import com.thoughtworks.go.plugin.domain.common.PluginConfiguration;
import com.thoughtworks.go.plugin.domain.common.PluginConstants;
import com.thoughtworks.go.plugin.infra.PluginManager;
import com.thoughtworks.go.util.command.EnvironmentVariableContext;
import java.util.Collections;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public abstract class ArtifactExtensionTestBase {
    static final String PLUGIN_ID = "foo.plugin";

    PluginManager pluginManager;

    ArtifactExtension artifactExtension;

    ArgumentCaptor<GoPluginApiRequest> requestArgumentCaptor;

    ExtensionsRegistry extensionsRegistry;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldGetSupportedVersions() {
        final ArtifactExtension artifactExtension = new ArtifactExtension(null, null);
        Assert.assertThat(artifactExtension.goSupportedVersions(), containsInAnyOrder("1.0", "2.0"));
    }

    @Test
    public void shouldRegisterMessageHandler() {
        final ArtifactExtension artifactExtension = new ArtifactExtension(null, null);
        Assert.assertTrue(((artifactExtension.getMessageHandler(VERSION)) instanceof ArtifactMessageConverterV1));
        Assert.assertTrue(((artifactExtension.getMessageHandler(ArtifactMessageConverterV2.VERSION)) instanceof ArtifactMessageConverterV2));
        Assert.assertThat(artifactExtension.getMessageHandler("3.0"), is(nullValue()));
    }

    @Test
    public void shouldGetArtifactStoreMetadataFromPlugin() {
        String responseBody = "[{\"key\":\"BUCKET_NAME\",\"metadata\":{\"required\":true,\"secure\":false}},{\"key\":\"AWS_ACCESS_KEY\",\"metadata\":{\"required\":true,\"secure\":true}}]";
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(ArtifactExtensionTestBase.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ARTIFACT_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(responseBody));
        final List<PluginConfiguration> response = artifactExtension.getArtifactStoreMetadata(ArtifactExtensionTestBase.PLUGIN_ID);
        final GoPluginApiRequest request = requestArgumentCaptor.getValue();
        Assert.assertThat(request.extension(), is(PluginConstants.ARTIFACT_EXTENSION));
        Assert.assertThat(request.requestName(), is(REQUEST_STORE_CONFIG_METADATA));
        Assert.assertNull(request.requestBody());
        Assert.assertThat(response.size(), is(2));
        Assert.assertThat(response, containsInAnyOrder(new PluginConfiguration("BUCKET_NAME", new Metadata(true, false)), new PluginConfiguration("AWS_ACCESS_KEY", new Metadata(true, true))));
    }

    @Test
    public void shouldGetArtifactStoreViewFromPlugin() {
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(ArtifactExtensionTestBase.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ARTIFACT_EXTENSION), requestArgumentCaptor.capture())).thenReturn(new DefaultGoPluginApiResponse(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE, "{ \"template\": \"artifact-store-view\"}"));
        String view = artifactExtension.getArtifactStoreView(ArtifactExtensionTestBase.PLUGIN_ID);
        final GoPluginApiRequest request = requestArgumentCaptor.getValue();
        Assert.assertThat(request.extension(), is(PluginConstants.ARTIFACT_EXTENSION));
        Assert.assertThat(request.requestName(), is(REQUEST_STORE_CONFIG_VIEW));
        Assert.assertNull(request.requestBody());
        Assert.assertThat(view, is("artifact-store-view"));
    }

    @Test
    public void shouldValidateArtifactStoreConfig() {
        String responseBody = "[{\"message\":\"ACCESS_KEY must not be blank.\",\"key\":\"ACCESS_KEY\"}]";
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(ArtifactExtensionTestBase.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ARTIFACT_EXTENSION), requestArgumentCaptor.capture())).thenReturn(new DefaultGoPluginApiResponse(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE, responseBody));
        ValidationResult validationResult = artifactExtension.validateArtifactStoreConfig(ArtifactExtensionTestBase.PLUGIN_ID, Collections.singletonMap("ACCESS_KEY", ""));
        final GoPluginApiRequest request = requestArgumentCaptor.getValue();
        Assert.assertThat(request.extension(), is(PluginConstants.ARTIFACT_EXTENSION));
        Assert.assertThat(request.requestName(), is(REQUEST_STORE_CONFIG_VALIDATE));
        Assert.assertThat(request.requestBody(), is("{\"ACCESS_KEY\":\"\"}"));
        Assert.assertThat(validationResult.isSuccessful(), is(false));
        Assert.assertThat(validationResult.getErrors(), containsInAnyOrder(new ValidationError("ACCESS_KEY", "ACCESS_KEY must not be blank.")));
    }

    @Test
    public void shouldGetPluggableArtifactMetadataFromPlugin() {
        String responseBody = "[{\"key\":\"FILENAME\",\"metadata\":{\"required\":true,\"secure\":false}},{\"key\":\"VERSION\",\"metadata\":{\"required\":true,\"secure\":true}}]";
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(ArtifactExtensionTestBase.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ARTIFACT_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(responseBody));
        final List<PluginConfiguration> response = artifactExtension.getPublishArtifactMetadata(ArtifactExtensionTestBase.PLUGIN_ID);
        final GoPluginApiRequest request = requestArgumentCaptor.getValue();
        Assert.assertThat(request.extension(), is(PluginConstants.ARTIFACT_EXTENSION));
        Assert.assertThat(request.requestName(), is(REQUEST_PUBLISH_ARTIFACT_METADATA));
        Assert.assertNull(request.requestBody());
        Assert.assertThat(response.size(), is(2));
        Assert.assertThat(response, containsInAnyOrder(new PluginConfiguration("FILENAME", new Metadata(true, false)), new PluginConfiguration("VERSION", new Metadata(true, true))));
    }

    @Test
    public void shouldGetPluggableArtifactViewFromPlugin() {
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(ArtifactExtensionTestBase.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ARTIFACT_EXTENSION), requestArgumentCaptor.capture())).thenReturn(new DefaultGoPluginApiResponse(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE, "{ \"template\": \"pluggable-artifact-view\"}"));
        String view = artifactExtension.getPublishArtifactView(ArtifactExtensionTestBase.PLUGIN_ID);
        final GoPluginApiRequest request = requestArgumentCaptor.getValue();
        Assert.assertThat(request.extension(), is(PluginConstants.ARTIFACT_EXTENSION));
        Assert.assertThat(request.requestName(), is(REQUEST_PUBLISH_ARTIFACT_VIEW));
        Assert.assertNull(request.requestBody());
        Assert.assertThat(view, is("pluggable-artifact-view"));
    }

    @Test
    public void shouldValidatePluggableArtifactConfig() {
        String responseBody = "[{\"message\":\"Filename must not be blank.\",\"key\":\"Filename\"}]";
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(ArtifactExtensionTestBase.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ARTIFACT_EXTENSION), requestArgumentCaptor.capture())).thenReturn(new DefaultGoPluginApiResponse(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE, responseBody));
        ValidationResult validationResult = artifactExtension.validatePluggableArtifactConfig(ArtifactExtensionTestBase.PLUGIN_ID, Collections.singletonMap("Filename", ""));
        final GoPluginApiRequest request = requestArgumentCaptor.getValue();
        Assert.assertThat(request.extension(), is(PluginConstants.ARTIFACT_EXTENSION));
        Assert.assertThat(request.requestName(), is(REQUEST_PUBLISH_ARTIFACT_VALIDATE));
        Assert.assertThat(request.requestBody(), is("{\"Filename\":\"\"}"));
        Assert.assertThat(validationResult.isSuccessful(), is(false));
        Assert.assertThat(validationResult.getErrors(), containsInAnyOrder(new ValidationError("Filename", "Filename must not be blank.")));
    }

    @Test
    public void shouldSubmitPublishArtifactRequest() {
        final String responseBody = "{\n" + ((("  \"metadata\": {\n" + "    \"artifact-version\": \"10.12.0\"\n") + "  }\n") + "}");
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(ArtifactExtensionTestBase.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ARTIFACT_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(responseBody));
        ArtifactPlan artifactPlan = new ArtifactPlan("{\"id\":\"installer\",\"storeId\":\"docker\"}");
        ArtifactStore artifactStore = new ArtifactStore("docker", "cd.go.docker");
        EnvironmentVariableContext env = new EnvironmentVariableContext("foo", "bar");
        final PublishArtifactResponse response = artifactExtension.publishArtifact(ArtifactExtensionTestBase.PLUGIN_ID, artifactPlan, artifactStore, "/temp", env);
        final GoPluginApiRequest request = requestArgumentCaptor.getValue();
        Assert.assertThat(request.extension(), is(PluginConstants.ARTIFACT_EXTENSION));
        Assert.assertThat(request.requestName(), is(REQUEST_PUBLISH_ARTIFACT));
        final String expectedJSON = "{" + (((((((((((("  \"artifact_plan\": {" + "    \"id\": \"installer\",") + "    \"storeId\": \"docker\"") + "  },") + "  \"artifact_store\": {") + "    \"configuration\": {},") + "    \"id\": \"docker\"") + "  },") + "  \"environment_variables\": {") + "    \"foo\": \"bar\"") + "  },") + "  \"agent_working_directory\": \"/temp\"") + "}");
        assertThatJson(expectedJSON).isEqualTo(request.requestBody());
        Assert.assertThat(response.getMetadata().size(), is(1));
        Assert.assertThat(response.getMetadata(), hasEntry("artifact-version", "10.12.0"));
    }

    @Test
    public void shouldGetFetchArtifactMetadataFromPlugin() {
        String responseBody = "[{\"key\":\"FILENAME\",\"metadata\":{\"required\":true,\"secure\":false}},{\"key\":\"VERSION\",\"metadata\":{\"required\":true,\"secure\":true}}]";
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(ArtifactExtensionTestBase.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ARTIFACT_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success(responseBody));
        final List<PluginConfiguration> response = artifactExtension.getFetchArtifactMetadata(ArtifactExtensionTestBase.PLUGIN_ID);
        final GoPluginApiRequest request = requestArgumentCaptor.getValue();
        Assert.assertThat(request.extension(), is(PluginConstants.ARTIFACT_EXTENSION));
        Assert.assertThat(request.requestName(), is(REQUEST_FETCH_ARTIFACT_METADATA));
        Assert.assertNull(request.requestBody());
        Assert.assertThat(response.size(), is(2));
        Assert.assertThat(response, containsInAnyOrder(new PluginConfiguration("FILENAME", new Metadata(true, false)), new PluginConfiguration("VERSION", new Metadata(true, true))));
    }

    @Test
    public void shouldGetFetchArtifactViewFromPlugin() {
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(ArtifactExtensionTestBase.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ARTIFACT_EXTENSION), requestArgumentCaptor.capture())).thenReturn(new DefaultGoPluginApiResponse(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE, "{ \"template\": \"fetch-artifact-view\"}"));
        String view = artifactExtension.getFetchArtifactView(ArtifactExtensionTestBase.PLUGIN_ID);
        final GoPluginApiRequest request = requestArgumentCaptor.getValue();
        Assert.assertThat(request.extension(), is(PluginConstants.ARTIFACT_EXTENSION));
        Assert.assertThat(request.requestName(), is(REQUEST_FETCH_ARTIFACT_VIEW));
        Assert.assertNull(request.requestBody());
        Assert.assertThat(view, is("fetch-artifact-view"));
    }

    @Test
    public void shouldValidateFetchArtifactConfig() {
        String responseBody = "[{\"message\":\"Filename must not be blank.\",\"key\":\"Filename\"}]";
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(ArtifactExtensionTestBase.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ARTIFACT_EXTENSION), requestArgumentCaptor.capture())).thenReturn(new DefaultGoPluginApiResponse(DefaultGoPluginApiResponse.SUCCESS_RESPONSE_CODE, responseBody));
        ValidationResult validationResult = artifactExtension.validateFetchArtifactConfig(ArtifactExtensionTestBase.PLUGIN_ID, Collections.singletonMap("Filename", ""));
        final GoPluginApiRequest request = requestArgumentCaptor.getValue();
        Assert.assertThat(request.extension(), is(PluginConstants.ARTIFACT_EXTENSION));
        Assert.assertThat(request.requestName(), is(REQUEST_FETCH_ARTIFACT_VALIDATE));
        Assert.assertThat(request.requestBody(), is("{\"Filename\":\"\"}"));
        Assert.assertThat(validationResult.isSuccessful(), is(false));
        Assert.assertThat(validationResult.getErrors(), containsInAnyOrder(new ValidationError("Filename", "Filename must not be blank.")));
    }

    @Test
    public void shouldGetCapabilities() {
        Mockito.when(pluginManager.submitTo(ArgumentMatchers.eq(ArtifactExtensionTestBase.PLUGIN_ID), ArgumentMatchers.eq(PluginConstants.ARTIFACT_EXTENSION), requestArgumentCaptor.capture())).thenReturn(DefaultGoPluginApiResponse.success("{}"));
        artifactExtension.getCapabilities(ArtifactExtensionTestBase.PLUGIN_ID);
        final GoPluginApiRequest request = requestArgumentCaptor.getValue();
        Assert.assertThat(request.extension(), is(PluginConstants.ARTIFACT_EXTENSION));
        Assert.assertThat(request.requestName(), is(REQUEST_GET_CAPABILITIES));
        Assert.assertNull(request.requestBody());
    }

    @Test
    public void allRequestMustHaveRequestPrefix() {
        Assert.assertThat(REQUEST_PREFIX, is("cd.go.artifact"));
        Assert.assertThat(REQUEST_STORE_CONFIG_METADATA, Matchers.startsWith(REQUEST_PREFIX));
        Assert.assertThat(REQUEST_STORE_CONFIG_VIEW, Matchers.startsWith(REQUEST_PREFIX));
        Assert.assertThat(REQUEST_STORE_CONFIG_VALIDATE, Matchers.startsWith(REQUEST_PREFIX));
        Assert.assertThat(REQUEST_PUBLISH_ARTIFACT_METADATA, Matchers.startsWith(REQUEST_PREFIX));
        Assert.assertThat(REQUEST_PUBLISH_ARTIFACT_VIEW, Matchers.startsWith(REQUEST_PREFIX));
        Assert.assertThat(REQUEST_PUBLISH_ARTIFACT_VALIDATE, Matchers.startsWith(REQUEST_PREFIX));
        Assert.assertThat(REQUEST_FETCH_ARTIFACT_METADATA, Matchers.startsWith(REQUEST_PREFIX));
        Assert.assertThat(REQUEST_FETCH_ARTIFACT_VIEW, Matchers.startsWith(REQUEST_PREFIX));
        Assert.assertThat(REQUEST_FETCH_ARTIFACT_VALIDATE, Matchers.startsWith(REQUEST_PREFIX));
        Assert.assertThat(REQUEST_PUBLISH_ARTIFACT, Matchers.startsWith(REQUEST_PREFIX));
        Assert.assertThat(REQUEST_FETCH_ARTIFACT, Matchers.startsWith(REQUEST_PREFIX));
        Assert.assertThat(REQUEST_GET_PLUGIN_ICON, Matchers.startsWith(REQUEST_PREFIX));
    }
}

