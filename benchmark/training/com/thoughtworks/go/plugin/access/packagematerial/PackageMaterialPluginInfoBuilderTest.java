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
package com.thoughtworks.go.plugin.access.packagematerial;


import com.thoughtworks.go.plugin.domain.packagematerial.PackageMaterialPluginInfo;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;


public class PackageMaterialPluginInfoBuilderTest {
    private PackageRepositoryExtension extension;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldBuildPluginInfo() throws Exception {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        PackageMaterialPluginInfo pluginInfo = new PackageMaterialPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        List<PluginConfiguration> packageSettings = Arrays.asList(new PluginConfiguration("username", new PackageMaterialMetadata(true, false, false, "foo", 1)), new PluginConfiguration("password", new PackageMaterialMetadata(true, true, true, "", 2)));
        List<PluginConfiguration> repoSettings = Arrays.asList(new PluginConfiguration("foo", new PackageMaterialMetadata(true, false, true, "", 1)), new PluginConfiguration("bar", new PackageMaterialMetadata(true, true, true, "", 2)));
        List<PluginConfiguration> pluginSettings = Arrays.asList(new PluginConfiguration("k1", new Metadata(true, false)));
        Assert.assertThat(pluginInfo.getDescriptor(), Matchers.is(descriptor));
        Assert.assertThat(pluginInfo.getExtensionName(), Matchers.is("package-repository"));
        Assert.assertThat(pluginInfo.getPackageSettings(), Matchers.is(new PluggableInstanceSettings(packageSettings, null)));
        Assert.assertThat(pluginInfo.getRepositorySettings(), Matchers.is(new PluggableInstanceSettings(repoSettings, null)));
        Assert.assertThat(pluginInfo.getPluginSettings(), Matchers.is(new PluggableInstanceSettings(pluginSettings, new PluginView("some-html"))));
    }

    @Test
    public void shouldThrowAnExceptionWhenRepoConfigProvidedByPluginIsNull() {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        Mockito.when(extension.getRepositoryConfiguration("plugin1")).thenReturn(null);
        thrown.expectMessage("Plugin[plugin1] returned null repository configuration");
        new PackageMaterialPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        new PackageMaterialPluginInfoBuilder(extension).pluginInfoFor(descriptor);
    }

    @Test
    public void shouldThrowAnExceptionWhenPackageConfigProvidedByPluginIsNull() {
        GoPluginDescriptor descriptor = new GoPluginDescriptor("plugin1", null, null, null, null, false);
        Mockito.when(extension.getPackageConfiguration("plugin1")).thenReturn(null);
        thrown.expectMessage("Plugin[plugin1] returned null package configuration");
        new PackageMaterialPluginInfoBuilder(extension).pluginInfoFor(descriptor);
        new PackageMaterialPluginInfoBuilder(extension).pluginInfoFor(descriptor);
    }
}

