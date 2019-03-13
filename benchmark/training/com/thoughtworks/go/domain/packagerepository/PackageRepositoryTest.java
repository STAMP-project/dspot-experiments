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
package com.thoughtworks.go.domain.packagerepository;


import ConfigurationProperty.CONFIGURATION_KEY;
import PackageRepository.NAME;
import com.thoughtworks.go.config.BasicCruiseConfig;
import com.thoughtworks.go.config.ConfigSaveValidationContext;
import com.thoughtworks.go.config.helper.ConfigurationHolder;
import com.thoughtworks.go.plugin.access.packagematerial.PackageConfiguration;
import com.thoughtworks.go.plugin.access.packagematerial.PackageConfigurations;
import com.thoughtworks.go.plugin.access.packagematerial.PackageMetadataStore;
import com.thoughtworks.go.plugin.access.packagematerial.RepositoryMetadataStore;
import com.thoughtworks.go.security.GoCipher;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PackageRepositoryTest extends PackageMaterialTestBase {
    @Test
    public void shouldCheckEqualityOfPackageRepository() {
        Configuration configuration = new Configuration();
        Packages packages = new Packages(new PackageDefinition());
        PackageRepository packageRepository = createPackageRepository("plugin-id", "version", "id", "name", configuration, packages);
        Assert.assertThat(packageRepository, Matchers.is(createPackageRepository("plugin-id", "version", "id", "name", configuration, packages)));
    }

    @Test
    public void shouldCheckForFieldAssignments() {
        Configuration configuration = new Configuration();
        Packages packages = new Packages(new PackageDefinition());
        PackageRepository packageRepository = createPackageRepository("plugin-id", "version", "id", "name", configuration, packages);
        Assert.assertThat(packageRepository.getPluginConfiguration().getId(), Matchers.is("plugin-id"));
        Assert.assertThat(packageRepository.getPluginConfiguration().getVersion(), Matchers.is("version"));
        Assert.assertThat(packageRepository.getId(), Matchers.is("id"));
        Assert.assertThat(packageRepository.getName(), Matchers.is("name"));
    }

    @Test
    public void shouldSetRepositoryOnAllAssociatedPackages() {
        Configuration configuration = new Configuration();
        PackageDefinition packageDefinition = new PackageDefinition();
        PackageRepository packageRepository = createPackageRepository("plugin-id", "version", "id", "name", configuration, new Packages(packageDefinition));
        packageRepository.setRepositoryReferenceOnPackages();
        Assert.assertThat(packageDefinition.getRepository(), Matchers.is(packageRepository));
    }

    @Test
    public void shouldOnlyDisplayFieldsWhichAreNonSecureAndPartOfIdentityInGetConfigForDisplayWhenPluginExists() throws Exception {
        PackageConfigurations repositoryConfiguration = new PackageConfigurations();
        repositoryConfiguration.addConfiguration(new PackageConfiguration("key1").with(PART_OF_IDENTITY, true).with(SECURE, false));
        repositoryConfiguration.addConfiguration(new PackageConfiguration("key2").with(PART_OF_IDENTITY, false).with(SECURE, false));
        repositoryConfiguration.addConfiguration(new PackageConfiguration("key3").with(PART_OF_IDENTITY, true).with(SECURE, true));
        repositoryConfiguration.addConfiguration(new PackageConfiguration("key4").with(PART_OF_IDENTITY, false).with(SECURE, true));
        repositoryConfiguration.addConfiguration(new PackageConfiguration("key5").with(PART_OF_IDENTITY, true).with(SECURE, false));
        RepositoryMetadataStore.getInstance().addMetadataFor("plugin1", repositoryConfiguration);
        Configuration configuration = new Configuration(ConfigurationPropertyMother.create("key1", false, "value1"), ConfigurationPropertyMother.create("key2", false, "value2"), ConfigurationPropertyMother.create("key3", true, "value3"), ConfigurationPropertyMother.create("key4", true, "value4"), ConfigurationPropertyMother.create("key5", false, "value5"));
        PackageRepository repository = PackageRepositoryMother.create("repo1", "repo1-name", "plugin1", "1", configuration);
        Assert.assertThat(repository.getConfigForDisplay(), Matchers.is("Repository: [key1=value1, key5=value5]"));
    }

    @Test
    public void shouldConvertKeysToLowercaseInGetConfigForDisplay() throws Exception {
        RepositoryMetadataStore.getInstance().addMetadataFor("some-plugin", new PackageConfigurations());
        PackageMetadataStore.getInstance().addMetadataFor("some-plugin", new PackageConfigurations());
        Configuration configuration = new Configuration(ConfigurationPropertyMother.create("kEY1", false, "vALue1"), ConfigurationPropertyMother.create("KEY_MORE_2", false, "VALUE_2"), ConfigurationPropertyMother.create("key_3", false, "value3"));
        PackageRepository repository = PackageRepositoryMother.create("repo1", "repo1-name", "some-plugin", "1", configuration);
        Assert.assertThat(repository.getConfigForDisplay(), Matchers.is("Repository: [key1=vALue1, key_more_2=VALUE_2, key_3=value3]"));
    }

    @Test
    public void shouldNotDisplayEmptyValuesInGetConfigForDisplay() throws Exception {
        RepositoryMetadataStore.getInstance().addMetadataFor("some-plugin", new PackageConfigurations());
        PackageMetadataStore.getInstance().addMetadataFor("some-plugin", new PackageConfigurations());
        Configuration configuration = new Configuration(ConfigurationPropertyMother.create("rk1", false, ""), ConfigurationPropertyMother.create("rk2", false, "some-non-empty-value"), ConfigurationPropertyMother.create("rk3", false, null));
        PackageRepository repository = PackageRepositoryMother.create("repo-id", "repo", "some-plugin", "version", configuration);
        Assert.assertThat(repository.getConfigForDisplay(), Matchers.is("Repository: [rk2=some-non-empty-value]"));
    }

    @Test
    public void shouldDisplayAllNonSecureFieldsInGetConfigForDisplayWhenPluginDoesNotExist() {
        Configuration configuration = new Configuration(ConfigurationPropertyMother.create("key1", false, "value1"), ConfigurationPropertyMother.create("key2", true, "value2"), ConfigurationPropertyMother.create("key3", false, "value3"));
        PackageRepository repository = PackageRepositoryMother.create("repo1", "repo1-name", "some-plugin-which-does-not-exist", "1", configuration);
        Assert.assertThat(repository.getConfigForDisplay(), Matchers.is("WARNING! Plugin missing for Repository: [key1=value1, key3=value3]"));
    }

    @Test
    public void shouldMakeConfigurationSecureBasedOnMetadata() throws Exception {
        GoCipher goCipher = new GoCipher();
        /* secure property is set based on metadata */
        ConfigurationProperty secureProperty = new ConfigurationProperty(new ConfigurationKey("key1"), new ConfigurationValue("value1"), null, goCipher);
        ConfigurationProperty nonSecureProperty = new ConfigurationProperty(new ConfigurationKey("key2"), new ConfigurationValue("value2"), null, goCipher);
        PackageDefinition packageDefinition = new PackageDefinition("go", "name", new Configuration(secureProperty, nonSecureProperty));
        // meta data of package
        PackageConfigurations packageConfigurations = new PackageConfigurations();
        packageConfigurations.addConfiguration(new PackageConfiguration("key1").with(SECURE, true));
        packageConfigurations.addConfiguration(new PackageConfiguration("key2").with(SECURE, false));
        PackageMetadataStore.getInstance().addMetadataFor("plugin-id", packageConfigurations);
        /* secure property is set based on metadata */
        ConfigurationProperty secureRepoProperty = new ConfigurationProperty(new ConfigurationKey("key1"), new ConfigurationValue("value1"), null, goCipher);
        ConfigurationProperty nonSecureRepoProperty = new ConfigurationProperty(new ConfigurationKey("key2"), new ConfigurationValue("value2"), null, goCipher);
        PackageRepository packageRepository = createPackageRepository("plugin-id", "version", "id", "name", new Configuration(secureRepoProperty, nonSecureRepoProperty), new Packages(packageDefinition));
        // meta data of repo
        PackageConfigurations repositoryConfiguration = new PackageConfigurations();
        repositoryConfiguration.addConfiguration(new PackageConfiguration("key1").with(SECURE, true));
        repositoryConfiguration.addConfiguration(new PackageConfiguration("key2").with(SECURE, false));
        RepositoryMetadataStore.getInstance().addMetadataFor("plugin-id", repositoryConfiguration);
        packageRepository.applyPackagePluginMetadata();
        // assert package properties
        Assert.assertThat(secureProperty.isSecure(), Matchers.is(true));
        Assert.assertThat(secureProperty.getEncryptedConfigurationValue(), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(secureProperty.getEncryptedValue(), Matchers.is(goCipher.encrypt("value1")));
        Assert.assertThat(nonSecureProperty.isSecure(), Matchers.is(false));
        Assert.assertThat(nonSecureProperty.getValue(), Matchers.is("value2"));
        // assert repository properties
        Assert.assertThat(secureRepoProperty.isSecure(), Matchers.is(true));
        Assert.assertThat(secureRepoProperty.getEncryptedConfigurationValue(), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(secureRepoProperty.getEncryptedValue(), Matchers.is(goCipher.encrypt("value1")));
        Assert.assertThat(nonSecureRepoProperty.isSecure(), Matchers.is(false));
        Assert.assertThat(nonSecureRepoProperty.getValue(), Matchers.is("value2"));
    }

    @Test
    public void shouldNotUpdateSecurePropertyWhenPluginIsMissing() {
        GoCipher goCipher = new GoCipher();
        ConfigurationProperty secureProperty = new ConfigurationProperty(new ConfigurationKey("key1"), null, new EncryptedConfigurationValue("value"), goCipher);
        ConfigurationProperty nonSecureProperty = new ConfigurationProperty(new ConfigurationKey("key2"), new ConfigurationValue("value2"), null, goCipher);
        PackageDefinition packageDefinition = new PackageDefinition("go", "name", new Configuration(secureProperty, nonSecureProperty));
        ConfigurationProperty nonSecureRepoProperty = new ConfigurationProperty(new ConfigurationKey("key1"), new ConfigurationValue("value1"), null, goCipher);
        ConfigurationProperty secureRepoProperty = new ConfigurationProperty(new ConfigurationKey("key2"), null, new EncryptedConfigurationValue("value"), goCipher);
        PackageRepository packageRepository = createPackageRepository("plugin-id", "version", "id", "name", new Configuration(secureRepoProperty, nonSecureRepoProperty), new Packages(packageDefinition));
        packageRepository.applyPackagePluginMetadata();
        Assert.assertThat(secureProperty.getEncryptedConfigurationValue(), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(secureProperty.getConfigurationValue(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(nonSecureProperty.getConfigurationValue(), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(nonSecureProperty.getEncryptedConfigurationValue(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(secureRepoProperty.getEncryptedConfigurationValue(), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(secureRepoProperty.getConfigurationValue(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(nonSecureRepoProperty.getConfigurationValue(), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(nonSecureRepoProperty.getEncryptedConfigurationValue(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldSetConfigAttributesAsAvailable() throws Exception {
        // metadata setup
        PackageConfigurations repositoryConfiguration = new PackageConfigurations();
        repositoryConfiguration.add(new PackageConfiguration("url"));
        repositoryConfiguration.add(new PackageConfiguration("username"));
        repositoryConfiguration.add(new PackageConfiguration("password").with(SECURE, true));
        repositoryConfiguration.add(new PackageConfiguration("secureKeyNotChanged").with(SECURE, true));
        RepositoryMetadataStore.getInstance().addMetadataFor("yum", repositoryConfiguration);
        String name = "go-server";
        String repoId = "repo-id";
        String pluginId = "yum";
        ConfigurationHolder url = new ConfigurationHolder("url", "http://test.com");
        ConfigurationHolder username = new ConfigurationHolder("username", "user");
        String oldEncryptedValue = "oldEncryptedValue";
        ConfigurationHolder password = new ConfigurationHolder("password", "pass", oldEncryptedValue, true, "1");
        ConfigurationHolder secureKeyNotChanged = new ConfigurationHolder("secureKeyNotChanged", "pass", oldEncryptedValue, true, "0");
        Map attributes = createPackageRepositoryConfiguration(name, pluginId, repoId, url, username, password, secureKeyNotChanged);
        PackageRepository packageRepository = new PackageRepository();
        Packages packages = new Packages();
        packageRepository.setPackages(packages);
        packageRepository.setConfigAttributes(attributes);
        Assert.assertThat(packageRepository.getName(), Matchers.is(name));
        Assert.assertThat(packageRepository.getId(), Matchers.is(repoId));
        Assert.assertThat(packageRepository.getPluginConfiguration().getId(), Matchers.is(pluginId));
        Assert.assertThat(packageRepository.getConfiguration().get(0).getConfigurationKey().getName(), Matchers.is(url.name));
        Assert.assertThat(packageRepository.getConfiguration().get(0).getConfigurationValue().getValue(), Matchers.is(url.value));
        Assert.assertThat(packageRepository.getConfiguration().get(1).getConfigurationKey().getName(), Matchers.is(username.name));
        Assert.assertThat(packageRepository.getConfiguration().get(1).getConfigurationValue().getValue(), Matchers.is(username.value));
        Assert.assertThat(packageRepository.getConfiguration().get(2).getConfigurationKey().getName(), Matchers.is(password.name));
        Assert.assertThat(packageRepository.getConfiguration().get(2).getEncryptedValue(), Matchers.is(new GoCipher().encrypt(password.value)));
        Assert.assertThat(packageRepository.getConfiguration().get(2).getConfigurationValue(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(packageRepository.getConfiguration().get(3).getConfigurationKey().getName(), Matchers.is(secureKeyNotChanged.name));
        Assert.assertThat(packageRepository.getConfiguration().get(3).getEncryptedValue(), Matchers.is(oldEncryptedValue));
        Assert.assertThat(packageRepository.getConfiguration().get(3).getConfigurationValue(), Matchers.is(Matchers.nullValue()));
        Assert.assertSame(packageRepository.getPackages(), packages);
    }

    @Test
    public void shouldValidateIfNameIsMissing() {
        PackageRepository packageRepository = new PackageRepository();
        packageRepository.validate(new ConfigSaveValidationContext(new BasicCruiseConfig(), null));
        Assert.assertThat(packageRepository.errors().getAllOn("name"), Matchers.is(Arrays.asList("Please provide name")));
    }

    @Test
    public void shouldAddPackageDefinitionToRepo() {
        PackageRepository repository = PackageRepositoryMother.create("repo1");
        String existingPackageId = repository.getPackages().get(0).getId();
        PackageDefinition pkg = PackageDefinitionMother.create("pkg");
        repository.addPackage(pkg);
        Assert.assertThat(repository.getPackages().size(), Matchers.is(2));
        Assert.assertThat(repository.getPackages().get(0).getId(), Matchers.is(existingPackageId));
        Assert.assertThat(repository.getPackages().get(1).getId(), Matchers.is(pkg.getId()));
    }

    @Test
    public void shouldFindPackageById() throws Exception {
        PackageRepository repository = PackageRepositoryMother.create("repo-id2", "repo2", "plugin-id", "1.0", null);
        PackageDefinition p1 = PackageDefinitionMother.create("id1", "pkg1", null, repository);
        PackageDefinition p2 = PackageDefinitionMother.create("id2", "pkg2", null, repository);
        Packages packages = new Packages(p1, p2);
        repository.setPackages(packages);
        Assert.assertThat(repository.findPackage("id2"), Matchers.is(p2));
    }

    @Test
    public void shouldClearConfigurationsWhichAreEmptyAndNoErrors() throws Exception {
        PackageRepository packageRepository = new PackageRepository();
        packageRepository.getConfiguration().add(new ConfigurationProperty(new ConfigurationKey("name-one"), new ConfigurationValue()));
        packageRepository.getConfiguration().add(new ConfigurationProperty(new ConfigurationKey("name-two"), new EncryptedConfigurationValue()));
        packageRepository.getConfiguration().add(new ConfigurationProperty(new ConfigurationKey("name-three"), null, new EncryptedConfigurationValue(), null));
        ConfigurationProperty configurationProperty = new ConfigurationProperty(new ConfigurationKey("name-four"), null, new EncryptedConfigurationValue(), null);
        configurationProperty.addErrorAgainstConfigurationValue("error");
        packageRepository.getConfiguration().add(configurationProperty);
        packageRepository.clearEmptyConfigurations();
        Assert.assertThat(packageRepository.getConfiguration().size(), Matchers.is(1));
        Assert.assertThat(packageRepository.getConfiguration().get(0).getConfigurationKey().getName(), Matchers.is("name-four"));
    }

    @Test
    public void shouldValidateName() throws Exception {
        PackageRepository packageRepository = new PackageRepository();
        packageRepository.setName("some name");
        packageRepository.validate(new ConfigSaveValidationContext(null));
        Assert.assertThat(packageRepository.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(packageRepository.errors().getAllOn(NAME).get(0), Matchers.is("Invalid PackageRepository name 'some name'. This must be alphanumeric and can contain underscores and periods (however, it cannot start with a period). The maximum allowed length is 255 characters."));
    }

    @Test
    public void shouldRemoveGivenPackageFromTheRepository() throws Exception {
        PackageDefinition packageDefinitionOne = new PackageDefinition("pid1", "pname1", null);
        PackageDefinition packageDefinitionTwo = new PackageDefinition("pid2", "pname2", null);
        PackageRepository packageRepository = new PackageRepository();
        packageRepository.addPackage(packageDefinitionOne);
        packageRepository.addPackage(packageDefinitionTwo);
        packageRepository.removePackage("pid1");
        Assert.assertThat(packageRepository.getPackages().size(), Matchers.is(1));
        Assert.assertThat(packageRepository.getPackages(), Matchers.hasItems(packageDefinitionTwo));
    }

    @Test
    public void shouldThrowErrorWhenGivenPackageNotFoundDuringRemove() throws Exception {
        PackageRepository packageRepository = new PackageRepository();
        try {
            packageRepository.removePackage("invalid");
        } catch (RuntimeException e) {
            Assert.assertThat(e.getMessage(), Matchers.is("Could not find package with id:[invalid]"));
        }
    }

    @Test
    public void shouldFindPackageDefinitionBasedOnParams() throws Exception {
        PackageRepository packageRepository = PackageRepositoryMother.create("repo-id1", "packageRepository", "plugin-id", "1.0", null);
        PackageDefinition packageDefinitionOne = PackageDefinitionMother.create("pid1", packageRepository);
        PackageDefinition packageDefinitionTwo = PackageDefinitionMother.create("pid2", packageRepository);
        packageRepository.getPackages().addAll(Arrays.asList(packageDefinitionOne, packageDefinitionTwo));
        Map attributes = new HashMap();
        attributes.put("packageId", "pid1");
        PackageDefinition actualPackageDefinition = packageRepository.findOrCreatePackageDefinition(attributes);
        Assert.assertThat(actualPackageDefinition, Matchers.is(packageDefinitionOne));
    }

    @Test
    public void shouldCreatePackageBasedOnParams() throws Exception {
        PackageRepository packageRepository = PackageRepositoryMother.create("repo-id1", "packageRepository", "plugin-id", "1.0", null);
        Map packageDefAttr = createPackageDefinitionConfiguration("package_name", "pluginId", new ConfigurationHolder("key1", "value1"), new ConfigurationHolder("key2", "value2"));
        Map map = new HashMap();
        map.put("package_definition", packageDefAttr);
        PackageDefinition actualPackageDefinition = packageRepository.findOrCreatePackageDefinition(map);
        Assert.assertThat(actualPackageDefinition, Matchers.is(PackageDefinitionMother.create(null, "package_name", new Configuration(ConfigurationPropertyMother.create("key1", false, "value1"), ConfigurationPropertyMother.create("key2", false, "value2")), packageRepository)));
        Assert.assertThat(actualPackageDefinition.getRepository(), Matchers.is(packageRepository));
    }

    @Test
    public void shouldValidateUniqueNames() {
        PackageRepository packageRepository = new PackageRepository();
        packageRepository.setName("REPO");
        HashMap<String, PackageRepository> nameMap = new HashMap<>();
        PackageRepository original = new PackageRepository();
        original.setName("repo");
        nameMap.put("repo", original);
        packageRepository.validateNameUniqueness(nameMap);
        Assert.assertThat(packageRepository.errors().getAllOn(NAME).contains("You have defined multiple repositories called 'REPO'. Repository names are case-insensitive and must be unique."), Matchers.is(true));
        Assert.assertThat(original.errors().getAllOn(NAME).contains("You have defined multiple repositories called 'REPO'. Repository names are case-insensitive and must be unique."), Matchers.is(true));
    }

    @Test
    public void shouldValidateUniqueKeysInConfiguration() {
        ConfigurationProperty one = new ConfigurationProperty(new ConfigurationKey("one"), new ConfigurationValue("value1"));
        ConfigurationProperty duplicate1 = new ConfigurationProperty(new ConfigurationKey("ONE"), new ConfigurationValue("value2"));
        ConfigurationProperty duplicate2 = new ConfigurationProperty(new ConfigurationKey("ONE"), new ConfigurationValue("value3"));
        ConfigurationProperty two = new ConfigurationProperty(new ConfigurationKey("two"), new ConfigurationValue());
        PackageRepository repository = new PackageRepository();
        repository.setConfiguration(new Configuration(one, duplicate1, duplicate2, two));
        repository.setName("yum");
        repository.validate(null);
        Assert.assertThat(one.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(one.errors().getAllOn(CONFIGURATION_KEY).contains("Duplicate key 'ONE' found for Repository 'yum'"), Matchers.is(true));
        Assert.assertThat(duplicate1.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(one.errors().getAllOn(CONFIGURATION_KEY).contains("Duplicate key 'ONE' found for Repository 'yum'"), Matchers.is(true));
        Assert.assertThat(duplicate2.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(one.errors().getAllOn(CONFIGURATION_KEY).contains("Duplicate key 'ONE' found for Repository 'yum'"), Matchers.is(true));
        Assert.assertThat(two.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldGenerateIdIfNotAssigned() {
        PackageRepository packageRepository = new PackageRepository();
        packageRepository.ensureIdExists();
        Assert.assertThat(packageRepository.getId(), Matchers.is(Matchers.notNullValue()));
        packageRepository = new PackageRepository();
        packageRepository.setId("id");
        packageRepository.ensureIdExists();
        Assert.assertThat(packageRepository.getId(), Matchers.is("id"));
    }
}

