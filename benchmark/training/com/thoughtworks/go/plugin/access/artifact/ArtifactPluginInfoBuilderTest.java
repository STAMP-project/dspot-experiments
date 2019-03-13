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
package com.thoughtworks.go.plugin.access.artifact;


import PluginConstants.ARTIFACT_EXTENSION;
import com.thoughtworks.go.plugin.domain.artifact.ArtifactPluginInfo;
import com.thoughtworks.go.plugin.domain.artifact.Capabilities;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ArtifactPluginInfoBuilderTest {
    private ArtifactExtension extension;

    @Test
    public void shouldBuildPluginInfoWithCapabilities() {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        Mockito.when(extension.getCapabilities(descriptor.id())).thenReturn(new Capabilities());
        ArtifactPluginInfo pluginInfo = new ArtifactPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        Assert.assertNotNull(pluginInfo.getCapabilities());
    }

    @Test
    public void shouldBuildPluginInfoWithStoreSettings() {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        List<PluginConfiguration> pluginConfigurations = Arrays.asList(new PluginConfiguration("S3_BUCKET", new Metadata(true, false)), new PluginConfiguration("AWS_ACCESS_KEY_ID", new Metadata(true, true)));
        Mockito.when(extension.getArtifactStoreMetadata(descriptor.id())).thenReturn(pluginConfigurations);
        Mockito.when(extension.getArtifactStoreView(descriptor.id())).thenReturn("store_config");
        ArtifactPluginInfo pluginInfo = new ArtifactPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        Assert.assertThat(pluginInfo.getStoreConfigSettings(), Matchers.is(new PluggableInstanceSettings(pluginConfigurations, new PluginView("store_config"))));
    }

    @Test
    public void shouldBuildPluginInfoWithPublishArtifactConfigSettings() {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        List<PluginConfiguration> pluginConfigurations = Arrays.asList(new PluginConfiguration("FILENAME", new Metadata(true, false)));
        Mockito.when(extension.getPublishArtifactMetadata(descriptor.id())).thenReturn(pluginConfigurations);
        Mockito.when(extension.getPublishArtifactView(descriptor.id())).thenReturn("artifact_config");
        ArtifactPluginInfo pluginInfo = new ArtifactPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        Assert.assertThat(pluginInfo.getArtifactConfigSettings(), Matchers.is(new PluggableInstanceSettings(pluginConfigurations, new PluginView("artifact_config"))));
    }

    @Test
    public void shouldBuildPluginInfoWithFetchArtifactConfigSettings() {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        List<PluginConfiguration> pluginConfigurations = Arrays.asList(new PluginConfiguration("FILENAME", new Metadata(true, false)), new PluginConfiguration("SECURE", new Metadata(true, true)));
        Mockito.when(extension.getFetchArtifactMetadata(descriptor.id())).thenReturn(pluginConfigurations);
        Mockito.when(extension.getFetchArtifactView(descriptor.id())).thenReturn("fetch_artifact_view");
        ArtifactPluginInfo pluginInfo = new ArtifactPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        Assert.assertThat(pluginInfo.getFetchArtifactSettings(), Matchers.is(new PluggableInstanceSettings(pluginConfigurations, new PluginView("fetch_artifact_view"))));
    }

    @Test
    public void shouldContinueWithBuildingPluginInfoIfPluginSettingsIsNotProvidedByPlugin() {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        Mockito.doThrow(new RuntimeException("foo")).when(extension).getPluginSettingsConfiguration("plugin1");
        ArtifactPluginInfo artifactPluginInfo = new ArtifactPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        Assert.assertThat(artifactPluginInfo.getDescriptor(), Matchers.is(descriptor));
        Assert.assertThat(artifactPluginInfo.getExtensionName(), Matchers.is(ARTIFACT_EXTENSION));
    }
}

