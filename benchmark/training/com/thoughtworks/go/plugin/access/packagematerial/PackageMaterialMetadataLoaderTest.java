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
package com.thoughtworks.go.plugin.access.packagematerial;


import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import com.thoughtworks.go.plugin.infra.PluginManager;
import com.thoughtworks.go.plugin.infra.plugininfo.GoPluginDescriptor;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class PackageMaterialMetadataLoaderTest {
    private PackageMaterialMetadataLoader metadataLoader;

    private GoPluginDescriptor pluginDescriptor;

    private PackageRepositoryExtension packageRepositoryExtension;

    private PluginManager pluginManager;

    @Test
    public void shouldFetchPackageMetadataForPluginsWhichImplementPackageRepositoryMaterialExtensionPoint() {
        RepositoryConfiguration expectedRepoConfigurations = new RepositoryConfiguration();
        PackageConfiguration expectedPackageConfigurations = new PackageConfiguration();
        Mockito.when(packageRepositoryExtension.getRepositoryConfiguration(pluginDescriptor.id())).thenReturn(expectedRepoConfigurations);
        Mockito.when(packageRepositoryExtension.getPackageConfiguration(pluginDescriptor.id())).thenReturn(expectedPackageConfigurations);
        metadataLoader.fetchRepositoryAndPackageMetaData(pluginDescriptor);
        Assert.assertThat(RepositoryMetadataStore.getInstance().getMetadata(pluginDescriptor.id()).getRepositoryConfiguration(), Matchers.is(expectedRepoConfigurations));
        Assert.assertThat(PackageMetadataStore.getInstance().getMetadata(pluginDescriptor.id()).getPackageConfiguration(), Matchers.is(expectedPackageConfigurations));
    }

    @Test
    public void shouldThrowExceptionWhenNullRepositoryConfigurationReturned() {
        Mockito.when(packageRepositoryExtension.getRepositoryConfiguration(pluginDescriptor.id())).thenReturn(null);
        try {
            metadataLoader.fetchRepositoryAndPackageMetaData(pluginDescriptor);
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Plugin[plugin-id] returned null repository configuration"));
        }
        Assert.assertThat(RepositoryMetadataStore.getInstance().getMetadata(pluginDescriptor.id()), Matchers.nullValue());
        Assert.assertThat(PackageMetadataStore.getInstance().getMetadata(pluginDescriptor.id()), Matchers.nullValue());
    }

    @Test
    public void shouldThrowExceptionWhenNullPackageConfigurationReturned() {
        Mockito.when(packageRepositoryExtension.getPackageConfiguration(pluginDescriptor.id())).thenReturn(null);
        try {
            metadataLoader.fetchRepositoryAndPackageMetaData(pluginDescriptor);
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Plugin[plugin-id] returned null repository configuration"));
        }
        Assert.assertThat(RepositoryMetadataStore.getInstance().getMetadata(pluginDescriptor.id()), Matchers.nullValue());
        Assert.assertThat(PackageMetadataStore.getInstance().getMetadata(pluginDescriptor.id()), Matchers.nullValue());
    }

    @Test
    public void shouldRegisterAsPluginFrameworkStartListener() throws Exception {
        metadataLoader = new PackageMaterialMetadataLoader(pluginManager, packageRepositoryExtension);
        Mockito.verify(pluginManager).addPluginChangeListener(metadataLoader);
    }

    @Test
    public void shouldFetchMetadataOnPluginLoadedCallback() throws Exception {
        PackageMaterialMetadataLoader spy = Mockito.spy(metadataLoader);
        Mockito.doNothing().when(spy).fetchRepositoryAndPackageMetaData(pluginDescriptor);
        Mockito.when(packageRepositoryExtension.canHandlePlugin(pluginDescriptor.id())).thenReturn(true);
        spy.pluginLoaded(pluginDescriptor);
        Mockito.verify(spy).fetchRepositoryAndPackageMetaData(pluginDescriptor);
    }

    @Test
    public void shouldNotTryToFetchMetadataOnPluginLoadedCallback() throws Exception {
        PackageMaterialMetadataLoader spy = Mockito.spy(metadataLoader);
        Mockito.when(packageRepositoryExtension.canHandlePlugin(pluginDescriptor.id())).thenReturn(false);
        spy.pluginLoaded(pluginDescriptor);
        Mockito.verify(spy, Mockito.never()).fetchRepositoryAndPackageMetaData(pluginDescriptor);
    }

    @Test
    public void shouldRemoveMetadataOnPluginUnLoadedCallback() throws Exception {
        RepositoryMetadataStore.getInstance().addMetadataFor(pluginDescriptor.id(), new PackageConfigurations());
        PackageMetadataStore.getInstance().addMetadataFor(pluginDescriptor.id(), new PackageConfigurations());
        Mockito.when(packageRepositoryExtension.canHandlePlugin(pluginDescriptor.id())).thenReturn(true);
        metadataLoader.pluginUnLoaded(pluginDescriptor);
        Assert.assertThat(RepositoryMetadataStore.getInstance().getMetadata(pluginDescriptor.id()), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(PackageMetadataStore.getInstance().getMetadata(pluginDescriptor.id()), Matchers.is(Matchers.nullValue()));
    }
}

