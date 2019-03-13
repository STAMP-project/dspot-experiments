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
package com.thoughtworks.go.config.materials;


import PackageMaterialConfig.PACKAGE_ID;
import com.thoughtworks.go.config.CaseInsensitiveString;
import com.thoughtworks.go.config.ConfigSaveValidationContext;
import com.thoughtworks.go.config.PipelineConfigSaveValidationContext;
import com.thoughtworks.go.config.materials.git.GitMaterialConfig;
import com.thoughtworks.go.domain.packagerepository.ConfigurationPropertyMother;
import com.thoughtworks.go.domain.packagerepository.PackageDefinition;
import com.thoughtworks.go.domain.packagerepository.PackageDefinitionMother;
import com.thoughtworks.go.domain.packagerepository.PackageRepository;
import com.thoughtworks.go.domain.packagerepository.PackageRepositoryMother;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class PackageMaterialConfigTest {
    @Test
    public void shouldAddErrorIfMaterialDoesNotHaveAPackageId() throws Exception {
        PackageMaterialConfig packageMaterialConfig = new PackageMaterialConfig();
        packageMaterialConfig.validateConcreteMaterial(new ConfigSaveValidationContext(null, null));
        Assert.assertThat(packageMaterialConfig.errors().getAll().size(), Matchers.is(1));
        Assert.assertThat(packageMaterialConfig.errors().on(PACKAGE_ID), Matchers.is("Please select a repository and package"));
    }

    @Test
    public void shouldAddErrorIfPackageDoesNotExistsForGivenPackageId() throws Exception {
        PipelineConfigSaveValidationContext configSaveValidationContext = Mockito.mock(PipelineConfigSaveValidationContext.class);
        Mockito.when(configSaveValidationContext.findPackageById(ArgumentMatchers.anyString())).thenReturn(Mockito.mock(PackageRepository.class));
        PackageRepository packageRepository = Mockito.mock(PackageRepository.class);
        Mockito.when(packageRepository.doesPluginExist()).thenReturn(true);
        PackageMaterialConfig packageMaterialConfig = new PackageMaterialConfig(new CaseInsensitiveString("package-name"), "package-id", PackageDefinitionMother.create("package-id"));
        packageMaterialConfig.getPackageDefinition().setRepository(packageRepository);
        packageMaterialConfig.validateTree(configSaveValidationContext);
        Assert.assertThat(packageMaterialConfig.errors().getAll().size(), Matchers.is(1));
        Assert.assertThat(packageMaterialConfig.errors().on(PACKAGE_ID), Matchers.is("Could not find plugin for given package id:[package-id]."));
    }

    @Test
    public void shouldAddErrorIfPackagePluginDoesNotExistsForGivenPackageId() throws Exception {
        PipelineConfigSaveValidationContext configSaveValidationContext = Mockito.mock(PipelineConfigSaveValidationContext.class);
        Mockito.when(configSaveValidationContext.findPackageById(ArgumentMatchers.anyString())).thenReturn(Mockito.mock(PackageRepository.class));
        PackageRepository packageRepository = Mockito.mock(PackageRepository.class);
        Mockito.when(packageRepository.doesPluginExist()).thenReturn(false);
        PackageMaterialConfig packageMaterialConfig = new PackageMaterialConfig(new CaseInsensitiveString("package-name"), "package-id", PackageDefinitionMother.create("package-id"));
        packageMaterialConfig.getPackageDefinition().setRepository(packageRepository);
        packageMaterialConfig.validateTree(configSaveValidationContext);
        Assert.assertThat(packageMaterialConfig.errors().getAll().size(), Matchers.is(1));
        Assert.assertThat(packageMaterialConfig.errors().on(PACKAGE_ID), Matchers.is("Could not find plugin for given package id:[package-id]."));
    }

    @Test
    public void shouldAddErrorIfMaterialNameUniquenessValidationFails() throws Exception {
        PackageMaterialConfig packageMaterialConfig = new PackageMaterialConfig("package-id");
        Map<CaseInsensitiveString, AbstractMaterialConfig> nameToMaterialMap = new HashMap<>();
        PackageMaterialConfig existingMaterial = new PackageMaterialConfig("package-id");
        nameToMaterialMap.put(new CaseInsensitiveString("package-id"), existingMaterial);
        nameToMaterialMap.put(new CaseInsensitiveString("foo"), new GitMaterialConfig("url"));
        packageMaterialConfig.validateNameUniqueness(nameToMaterialMap);
        Assert.assertThat(packageMaterialConfig.errors().getAll().size(), Matchers.is(1));
        Assert.assertThat(packageMaterialConfig.errors().on(PACKAGE_ID), Matchers.is("Duplicate package material detected!"));
        Assert.assertThat(existingMaterial.errors().getAll().size(), Matchers.is(1));
        Assert.assertThat(existingMaterial.errors().on(PACKAGE_ID), Matchers.is("Duplicate package material detected!"));
        Assert.assertThat(nameToMaterialMap.size(), Matchers.is(2));
    }

    @Test
    public void shouldPassMaterialUniquenessIfIfNoDuplicateMaterialFound() throws Exception {
        PackageMaterialConfig packageMaterialConfig = new PackageMaterialConfig("package-id");
        Map<CaseInsensitiveString, AbstractMaterialConfig> nameToMaterialMap = new HashMap<>();
        nameToMaterialMap.put(new CaseInsensitiveString("repo-name:pkg-name"), new PackageMaterialConfig("package-id-new"));
        nameToMaterialMap.put(new CaseInsensitiveString("foo"), new GitMaterialConfig("url"));
        packageMaterialConfig.validateNameUniqueness(nameToMaterialMap);
        Assert.assertThat(packageMaterialConfig.errors().getAll().size(), Matchers.is(0));
        Assert.assertThat(nameToMaterialMap.size(), Matchers.is(3));
    }

    @Test
    public void shouldNotAddErrorDuringUniquenessValidationIfMaterialNameIsEmpty() throws Exception {
        PackageMaterialConfig packageMaterialConfig = new PackageMaterialConfig("");
        Map<CaseInsensitiveString, AbstractMaterialConfig> nameToMaterialMap = new HashMap<>();
        packageMaterialConfig.validateNameUniqueness(nameToMaterialMap);
        Assert.assertThat(packageMaterialConfig.errors().getAll().size(), Matchers.is(0));
        Assert.assertThat(nameToMaterialMap.size(), Matchers.is(0));
    }

    @Test
    public void shouldSetConfigAttributesForThePackageMaterial() throws Exception {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(PACKAGE_ID, "packageId");
        PackageMaterialConfig packageMaterialConfig = new PackageMaterialConfig();
        packageMaterialConfig.setConfigAttributes(attributes);
        Assert.assertThat(packageMaterialConfig.getPackageId(), Matchers.is("packageId"));
    }

    @Test
    public void shouldSetPackageIdToNullIfConfigAttributesForThePackageMaterialDoesNotContainPackageId() throws Exception {
        Map<String, String> attributes = new HashMap<>();
        PackageMaterialConfig packageMaterialConfig = new PackageMaterialConfig("id");
        packageMaterialConfig.setConfigAttributes(attributes);
        Assert.assertThat(packageMaterialConfig.getPackageId(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldSetPackageIdAsNullIfPackageDefinitionIsNull() {
        PackageMaterialConfig materialConfig = new PackageMaterialConfig("1");
        materialConfig.setPackageDefinition(null);
        Assert.assertThat(materialConfig.getPackageId(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(materialConfig.getPackageDefinition(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldGetNameFromRepoNameAndPackageName() {
        PackageMaterialConfig materialConfig = new PackageMaterialConfig();
        PackageRepository repository = PackageRepositoryMother.create("repo-id", "repo-name", "pluginid", "version", new com.thoughtworks.go.domain.config.Configuration(ConfigurationPropertyMother.create("k1", false, "v1")));
        materialConfig.setPackageDefinition(PackageDefinitionMother.create("p-id", "package-name", new com.thoughtworks.go.domain.config.Configuration(ConfigurationPropertyMother.create("k2", false, "v2")), repository));
        Assert.assertThat(materialConfig.getName().toString(), Matchers.is("repo-name:package-name"));
        materialConfig.setPackageDefinition(null);
        Assert.assertThat(materialConfig.getName(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldCheckEquals() throws Exception {
        PackageRepository repository = PackageRepositoryMother.create("repo-id", "repo-name", "pluginid", "version", new com.thoughtworks.go.domain.config.Configuration(ConfigurationPropertyMother.create("k1", false, "v1")));
        PackageDefinition packageDefinition = PackageDefinitionMother.create("p-id", "package-name", new com.thoughtworks.go.domain.config.Configuration(ConfigurationPropertyMother.create("k2", false, "v2")), repository);
        PackageMaterialConfig p1 = new PackageMaterialConfig();
        p1.setPackageDefinition(packageDefinition);
        PackageMaterialConfig p2 = new PackageMaterialConfig();
        p2.setPackageDefinition(packageDefinition);
        Assert.assertThat(p1.equals(p2), Matchers.is(true));
        p1 = new PackageMaterialConfig();
        p2 = new PackageMaterialConfig();
        Assert.assertThat(p1.equals(p2), Matchers.is(true));
        p2.setPackageDefinition(packageDefinition);
        Assert.assertThat(p1.equals(p2), Matchers.is(false));
        p1.setPackageDefinition(packageDefinition);
        p2 = new PackageMaterialConfig();
        Assert.assertThat(p1.equals(p2), Matchers.is(false));
        Assert.assertThat(p1.equals(null), Matchers.is(false));
    }

    @Test
    public void shouldDelegateToPackageDefinitionForAutoUpdate() throws Exception {
        PackageDefinition packageDefinition = Mockito.mock(PackageDefinition.class);
        Mockito.when(packageDefinition.isAutoUpdate()).thenReturn(false);
        PackageMaterialConfig materialConfig = new PackageMaterialConfig(new CaseInsensitiveString("name"), "package-id", packageDefinition);
        Assert.assertThat(materialConfig.isAutoUpdate(), Matchers.is(false));
        Mockito.verify(packageDefinition).isAutoUpdate();
    }
}

