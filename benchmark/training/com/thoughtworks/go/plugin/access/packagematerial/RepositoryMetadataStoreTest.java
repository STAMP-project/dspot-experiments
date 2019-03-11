/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.plugin.access.packagematerial;


import PackageConfiguration.PART_OF_IDENTITY;
import PackageConfiguration.REQUIRED;
import PackageConfiguration.SECURE;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class RepositoryMetadataStoreTest {
    @Test
    public void shouldPopulateDataCorrectly() throws Exception {
        PackageConfigurations repositoryConfigurationPut = new PackageConfigurations();
        RepositoryMetadataStore.getInstance().addMetadataFor("plugin-id", repositoryConfigurationPut);
        Assert.assertThat(RepositoryMetadataStore.getInstance().getMetadata("plugin-id"), Matchers.is(repositoryConfigurationPut));
    }

    @Test
    public void shouldReturnNullForMetadataIfPluginIdIsNotProvided() {
        Assert.assertNull(RepositoryMetadataStore.getInstance().getMetadata(""));
    }

    @Test
    public void shouldReturnNullForMetadataIfPluginIdIsNonExistent() {
        Assert.assertNull(RepositoryMetadataStore.getInstance().getMetadata("non-existent-plugin-id"));
    }

    @Test
    public void shouldAnswerIfKeyHasGivenOption() throws Exception {
        PackageConfigurations repositoryConfigurationPut = new PackageConfigurations();
        repositoryConfigurationPut.add(new PackageConfiguration("key-one").with(SECURE, true).with(REQUIRED, true));
        repositoryConfigurationPut.add(new PackageConfiguration("key-two"));
        RepositoryMetadataStore metadataStore = RepositoryMetadataStore.getInstance();
        metadataStore.addMetadataFor("plugin-id", repositoryConfigurationPut);
        Assert.assertThat(metadataStore.hasOption("plugin-id", "key-one", SECURE), Matchers.is(true));
        Assert.assertThat(metadataStore.hasOption("plugin-id", "key-one", REQUIRED), Matchers.is(true));
        Assert.assertThat(metadataStore.hasOption("plugin-id", "key-one", PART_OF_IDENTITY), Matchers.is(true));
        Assert.assertThat(metadataStore.hasOption("plugin-id", "key-two", SECURE), Matchers.is(false));
        Assert.assertThat(metadataStore.hasOption("plugin-id", "key-two", REQUIRED), Matchers.is(true));
        Assert.assertThat(metadataStore.hasOption("plugin-id", "key-two", PART_OF_IDENTITY), Matchers.is(true));
    }

    @Test
    public void shouldGetAllPluginIds() throws Exception {
        RepositoryMetadataStore metadataStore = RepositoryMetadataStore.getInstance();
        metadataStore.addMetadataFor("plugin1", new PackageConfigurations());
        metadataStore.addMetadataFor("plugin2", new PackageConfigurations());
        metadataStore.addMetadataFor("plugin3", new PackageConfigurations());
        Assert.assertThat(metadataStore.getPlugins().size(), Matchers.is(3));
        Assert.assertThat(metadataStore.getPlugins().contains("plugin1"), Matchers.is(true));
        Assert.assertThat(metadataStore.getPlugins().contains("plugin2"), Matchers.is(true));
        Assert.assertThat(metadataStore.getPlugins().contains("plugin3"), Matchers.is(true));
    }

    @Test
    public void shouldBeAbleToCheckIfPluginExists() throws Exception {
        RepositoryMetadataStore metadataStore = RepositoryMetadataStore.getInstance();
        PackageConfigurations repositoryConfigurationPut = new PackageConfigurations();
        metadataStore.addMetadataFor("plugin-id", repositoryConfigurationPut);
        Assert.assertThat(metadataStore.hasPlugin("plugin-id"), Matchers.is(true));
        Assert.assertThat(metadataStore.hasPlugin("some-plugin-which-does-not-exist"), Matchers.is(false));
    }
}

