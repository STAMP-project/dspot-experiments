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
package com.thoughtworks.go.domain.packagerepository;


import AbstractMaterial.FINGERPRINT_DELIMITER;
import ConfigurationProperty.CONFIGURATION_KEY;
import PackageConfiguration.PART_OF_IDENTITY;
import PackageConfiguration.SECURE;
import PackageDefinition.ID;
import PackageDefinition.NAME;
import com.thoughtworks.go.config.BasicCruiseConfig;
import com.thoughtworks.go.config.ConfigSaveValidationContext;
import com.thoughtworks.go.config.helper.ConfigurationHolder;
import com.thoughtworks.go.domain.config.Configuration;
import com.thoughtworks.go.domain.config.ConfigurationKey;
import com.thoughtworks.go.domain.config.ConfigurationProperty;
import com.thoughtworks.go.domain.config.ConfigurationValue;
import com.thoughtworks.go.domain.config.EncryptedConfigurationValue;
import com.thoughtworks.go.domain.config.PluginConfiguration;
import com.thoughtworks.go.plugin.access.packagematerial.PackageConfiguration;
import com.thoughtworks.go.plugin.access.packagematerial.PackageConfigurations;
import com.thoughtworks.go.plugin.access.packagematerial.PackageMetadataStore;
import com.thoughtworks.go.plugin.access.packagematerial.RepositoryMetadataStore;
import com.thoughtworks.go.security.GoCipher;
import com.thoughtworks.go.util.CachedDigestUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PackageDefinitionTest extends PackageMaterialTestBase {
    @Test
    public void shouldCheckForEqualityOfPackageDefinition() {
        Configuration configuration = new Configuration();
        PackageDefinition packageDefinition = new PackageDefinition("id", "name", configuration);
        Assert.assertThat(packageDefinition, Matchers.is(new PackageDefinition("id", "name", configuration)));
    }

    @Test
    public void shouldOnlyDisplayFieldsWhichAreNonSecureAndPartOfIdentityInGetConfigForDisplayWhenPluginExists() {
        String pluginId = "plugin-id";
        PackageConfigurations repositoryConfigurations = new PackageConfigurations();
        repositoryConfigurations.add(new PackageConfiguration("rk1", "rv1").with(PART_OF_IDENTITY, true).with(SECURE, false));
        repositoryConfigurations.add(new PackageConfiguration("rk2", "rv2").with(PART_OF_IDENTITY, false).with(SECURE, false));
        repositoryConfigurations.add(new PackageConfiguration("rk3", "rv3").with(PART_OF_IDENTITY, true).with(SECURE, true));
        RepositoryMetadataStore.getInstance().addMetadataFor(pluginId, repositoryConfigurations);
        PackageConfigurations packageConfigurations = new PackageConfigurations();
        packageConfigurations.add(new PackageConfiguration("pk1", "pv1").with(PART_OF_IDENTITY, true).with(SECURE, false));
        packageConfigurations.add(new PackageConfiguration("pk2", "pv2").with(PART_OF_IDENTITY, false).with(SECURE, false));
        packageConfigurations.add(new PackageConfiguration("pk3", "pv3").with(PART_OF_IDENTITY, true).with(SECURE, true));
        packageConfigurations.add(new PackageConfiguration("pk4", "pv4").with(PART_OF_IDENTITY, false).with(SECURE, true));
        packageConfigurations.add(new PackageConfiguration("pk5", "pv5").with(PART_OF_IDENTITY, true).with(SECURE, false));
        PackageMetadataStore.getInstance().addMetadataFor(pluginId, packageConfigurations);
        PackageRepository repository = PackageRepositoryMother.create("repo-id", "repo", pluginId, "version", new Configuration(ConfigurationPropertyMother.create("rk1", false, "rv1"), ConfigurationPropertyMother.create("rk2", false, "rv2"), ConfigurationPropertyMother.create("rk3", true, "rv3")));
        Configuration packageConfig = new Configuration(ConfigurationPropertyMother.create("pk1", false, "pv1"), ConfigurationPropertyMother.create("pk2", false, "pv2"), ConfigurationPropertyMother.create("pk3", true, "pv3"), ConfigurationPropertyMother.create("pk4", true, "pv4"), ConfigurationPropertyMother.create("pk5", false, "pv5"));
        PackageDefinition packageDefinition = PackageDefinitionMother.create("p-id", "name", packageConfig, repository);
        packageDefinition.setRepository(repository);
        Assert.assertThat(packageDefinition.getConfigForDisplay(), Matchers.is("Repository: [rk1=rv1] - Package: [pk1=pv1, pk5=pv5]"));
    }

    @Test
    public void shouldDisplayAllNonSecureFieldsInGetConfigForDisplayWhenPluginDoesNotExist() {
        PackageRepository repository = PackageRepositoryMother.create("repo-id", "repo", "some-plugin-which-does-not-exist", "version", new Configuration(ConfigurationPropertyMother.create("rk1", false, "rv1"), ConfigurationPropertyMother.create("rk2", true, "rv2")));
        PackageDefinition packageDefinition = PackageDefinitionMother.create("p-id", "name", new Configuration(ConfigurationPropertyMother.create("pk1", false, "pv1"), ConfigurationPropertyMother.create("pk2", true, "pv2"), ConfigurationPropertyMother.create("pk3", false, "pv3")), repository);
        packageDefinition.setRepository(repository);
        Assert.assertThat(packageDefinition.getConfigForDisplay(), Matchers.is("WARNING! Plugin missing for Repository: [rk1=rv1] - Package: [pk1=pv1, pk3=pv3]"));
    }

    @Test
    public void shouldConvertKeysToLowercaseInGetConfigForDisplay() throws Exception {
        RepositoryMetadataStore.getInstance().addMetadataFor("some-plugin", new PackageConfigurations());
        PackageMetadataStore.getInstance().addMetadataFor("some-plugin", new PackageConfigurations());
        PackageRepository repository = PackageRepositoryMother.create("repo-id", "repo", "some-plugin", "version", new Configuration(ConfigurationPropertyMother.create("rk1", false, "rv1")));
        PackageDefinition packageDefinition = PackageDefinitionMother.create("p-id", "name", new Configuration(ConfigurationPropertyMother.create("pack_key_1", false, "pack_value_1"), ConfigurationPropertyMother.create("PACK_KEY_2", false, "PACK_VALUE_2"), ConfigurationPropertyMother.create("pacK_KeY3", false, "pacKValue_3")), repository);
        packageDefinition.setRepository(repository);
        Assert.assertThat(packageDefinition.getConfigForDisplay(), Matchers.is("Repository: [rk1=rv1] - Package: [pack_key_1=pack_value_1, pack_key_2=PACK_VALUE_2, pack_key3=pacKValue_3]"));
    }

    @Test
    public void shouldNotDisplayEmptyValuesInGetConfigForDisplay() throws Exception {
        RepositoryMetadataStore.getInstance().addMetadataFor("some-plugin", new PackageConfigurations());
        PackageMetadataStore.getInstance().addMetadataFor("some-plugin", new PackageConfigurations());
        PackageRepository repository = PackageRepositoryMother.create("repo-id", "repo", "some-plugin", "version", new Configuration(ConfigurationPropertyMother.create("rk1", false, "rv1")));
        PackageDefinition packageDefinition = PackageDefinitionMother.create("p-id", "name", new Configuration(ConfigurationPropertyMother.create("pk1", false, ""), ConfigurationPropertyMother.create("pk2", false, "pack_value_2"), ConfigurationPropertyMother.create("pk3", false, null)), repository);
        packageDefinition.setRepository(repository);
        Assert.assertThat(packageDefinition.getConfigForDisplay(), Matchers.is("Repository: [rk1=rv1] - Package: [pk2=pack_value_2]"));
    }

    @Test
    public void shouldGetFingerprint() {
        String pluginId = "pluginid";
        PackageConfigurations repositoryConfigurations = new PackageConfigurations();
        repositoryConfigurations.add(new PackageConfiguration("k1", "v1").with(PART_OF_IDENTITY, true));
        RepositoryMetadataStore.getInstance().addMetadataFor(pluginId, repositoryConfigurations);
        PackageConfigurations packageConfigurations = new PackageConfigurations();
        packageConfigurations.add(new PackageConfiguration("k2", "v2").with(PART_OF_IDENTITY, true));
        PackageMetadataStore.getInstance().addMetadataFor(pluginId, packageConfigurations);
        PackageRepository repository = PackageRepositoryMother.create("repo-id", "repo", pluginId, "version", new Configuration(ConfigurationPropertyMother.create("k1", false, "v1")));
        PackageDefinition packageDefinition = PackageDefinitionMother.create("p-id", "name", new Configuration(ConfigurationPropertyMother.create("k2", false, "v2")), repository);
        String fingerprint = packageDefinition.getFingerprint(FINGERPRINT_DELIMITER);
        Assert.assertThat(fingerprint, Matchers.is(CachedDigestUtils.sha256Hex("plugin-id=pluginid<|>k2=v2<|>k1=v1")));
    }

    @Test
    public void shouldNotConsiderPropertiesMarkedAsNotPartOfIdentity_GetFingerprint() {
        String pluginId = "plugin-id";
        PackageConfigurations repositoryConfigurations = new PackageConfigurations();
        repositoryConfigurations.add(new PackageConfiguration("rk1", "rv1").with(PART_OF_IDENTITY, true));
        repositoryConfigurations.add(new PackageConfiguration("rk2", "rv2").with(PART_OF_IDENTITY, false));
        RepositoryMetadataStore.getInstance().addMetadataFor(pluginId, repositoryConfigurations);
        PackageConfigurations packageConfigurations = new PackageConfigurations();
        packageConfigurations.add(new PackageConfiguration("pk1", "pv1").with(PART_OF_IDENTITY, false));
        packageConfigurations.add(new PackageConfiguration("pk2", "pv2").with(PART_OF_IDENTITY, true));
        PackageMetadataStore.getInstance().addMetadataFor(pluginId, packageConfigurations);
        PackageRepository repository = PackageRepositoryMother.create("repo-id", "repo", pluginId, "version", new Configuration(ConfigurationPropertyMother.create("rk1", false, "rv1"), ConfigurationPropertyMother.create("rk2", false, "rv2")));
        PackageDefinition packageDefinition = PackageDefinitionMother.create("p-id", "name", new Configuration(ConfigurationPropertyMother.create("pk1", false, "pv1"), ConfigurationPropertyMother.create("pk2", false, "pv2")), repository);
        String fingerprint = packageDefinition.getFingerprint(FINGERPRINT_DELIMITER);
        Assert.assertThat(fingerprint, Matchers.is(CachedDigestUtils.sha256Hex("plugin-id=plugin-id<|>pk2=pv2<|>rk1=rv1")));
    }

    @Test
    public void shouldNotConsiderAllPropertiesForFingerprintWhenMetadataIsNotAvailable() {
        String pluginId = "plugin-id";
        PackageRepository repository = PackageRepositoryMother.create("repo-id", "repo", pluginId, "version", new Configuration(ConfigurationPropertyMother.create("rk1", false, "rv1"), ConfigurationPropertyMother.create("rk2", false, "rv2")));
        PackageDefinition packageDefinition = PackageDefinitionMother.create("p-id", "name", new Configuration(ConfigurationPropertyMother.create("pk1", false, "pv1"), ConfigurationPropertyMother.create("pk2", false, "pv2")), repository);
        String fingerprint = packageDefinition.getFingerprint(FINGERPRINT_DELIMITER);
        Assert.assertThat(fingerprint, Matchers.is(CachedDigestUtils.sha256Hex("plugin-id=plugin-id<|>pk1=pv1<|>pk2=pv2<|>rk1=rv1<|>rk2=rv2")));
    }

    @Test
    public void shouldMakeConfigurationSecureBasedOnMetadata() throws Exception {
        /* secure property is set based on metadata */
        ConfigurationProperty secureProperty = new ConfigurationProperty(new ConfigurationKey("key1"), new ConfigurationValue("value1"), null, new GoCipher());
        ConfigurationProperty nonSecureProperty = new ConfigurationProperty(new ConfigurationKey("key2"), new ConfigurationValue("value2"), null, new GoCipher());
        PackageDefinition packageDefinition = new PackageDefinition("go", "name", new Configuration(secureProperty, nonSecureProperty));
        PackageRepository packageRepository = new PackageRepository();
        packageRepository.setPluginConfiguration(new PluginConfiguration("plugin-id", "1.0"));
        packageDefinition.setRepository(packageRepository);
        PackageConfigurations packageConfigurations = new PackageConfigurations();
        packageConfigurations.addConfiguration(new PackageConfiguration("key1").with(SECURE, true));
        packageConfigurations.addConfiguration(new PackageConfiguration("key2").with(SECURE, false));
        PackageMetadataStore.getInstance().addMetadataFor("plugin-id", packageConfigurations);
        packageDefinition.applyPackagePluginMetadata("plugin-id");
        Assert.assertThat(secureProperty.isSecure(), Matchers.is(true));
        Assert.assertThat(nonSecureProperty.isSecure(), Matchers.is(false));
    }

    @Test
    public void shouldSetConfigAttributes() throws Exception {
        PackageDefinition definition = new PackageDefinition();
        String pluginId = "plugin";
        Map config = createPackageDefinitionConfiguration("package-name", pluginId, new ConfigurationHolder("key1", "value1"), new ConfigurationHolder("key2", "value2", "encrypted-value", true, "1"), new ConfigurationHolder("key3", "test", "encrypted-value", true, "0"));
        PackageConfigurations metadata = new PackageConfigurations();
        metadata.addConfiguration(new PackageConfiguration("key1"));
        metadata.addConfiguration(new PackageConfiguration("key2").with(SECURE, true));
        metadata.addConfiguration(new PackageConfiguration("key3").with(SECURE, true));
        PackageMetadataStore.getInstance().addMetadataFor(pluginId, metadata);
        definition.setRepository(PackageRepositoryMother.create("1"));
        definition.setConfigAttributes(config);
        String encryptedValue = new GoCipher().encrypt("value2");
        Assert.assertThat(definition.getName(), Matchers.is("package-name"));
        Assert.assertThat(definition.getConfiguration().size(), Matchers.is(3));
        Assert.assertThat(definition.getConfiguration().getProperty("key1").getConfigurationValue().getValue(), Matchers.is("value1"));
        Assert.assertThat(definition.getConfiguration().getProperty("key1").getEncryptedConfigurationValue(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(definition.getConfiguration().getProperty("key2").getEncryptedValue(), Matchers.is(encryptedValue));
        Assert.assertThat(definition.getConfiguration().getProperty("key2").getConfigurationValue(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(definition.getConfiguration().getProperty("key3").getEncryptedValue(), Matchers.is("encrypted-value"));
        Assert.assertThat(definition.getConfiguration().getProperty("key3").getConfigurationValue(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldValidateIfNameIsMissing() {
        PackageDefinition packageDefinition = new PackageDefinition();
        packageDefinition.validate(new ConfigSaveValidationContext(new BasicCruiseConfig(), null));
        Assert.assertThat(packageDefinition.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(packageDefinition.errors().getAllOn("name"), Matchers.is(Arrays.asList("Package name is mandatory")));
    }

    @Test
    public void shouldAddErrorToGivenKey() throws Exception {
        PackageDefinition packageDefinition = new PackageDefinition();
        packageDefinition.addError("field", "error message");
        Assert.assertThat(packageDefinition.errors().getAllOn("field").contains("error message"), Matchers.is(true));
    }

    @Test
    public void shouldClearConfigurationsWhichAreEmptyAndNoErrors() throws Exception {
        PackageDefinition packageDefinition = new PackageDefinition();
        packageDefinition.getConfiguration().add(new ConfigurationProperty(new ConfigurationKey("name-one"), new ConfigurationValue()));
        packageDefinition.getConfiguration().add(new ConfigurationProperty(new ConfigurationKey("name-two"), new EncryptedConfigurationValue()));
        packageDefinition.getConfiguration().add(new ConfigurationProperty(new ConfigurationKey("name-three"), null, new EncryptedConfigurationValue(), null));
        ConfigurationProperty configurationProperty = new ConfigurationProperty(new ConfigurationKey("name-four"), null, new EncryptedConfigurationValue(), null);
        configurationProperty.addErrorAgainstConfigurationValue("error");
        packageDefinition.getConfiguration().add(configurationProperty);
        packageDefinition.clearEmptyConfigurations();
        Assert.assertThat(packageDefinition.getConfiguration().size(), Matchers.is(1));
        Assert.assertThat(packageDefinition.getConfiguration().get(0).getConfigurationKey().getName(), Matchers.is("name-four"));
    }

    @Test
    public void shouldValidateName() throws Exception {
        PackageDefinition packageDefinition = new PackageDefinition();
        packageDefinition.setName("some name");
        packageDefinition.validate(new ConfigSaveValidationContext(null));
        Assert.assertThat(packageDefinition.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(packageDefinition.errors().getAllOn(NAME).get(0), Matchers.is("Invalid Package name 'some name'. This must be alphanumeric and can contain underscores and periods (however, it cannot start with a period). The maximum allowed length is 255 characters."));
    }

    @Test
    public void shouldSetAutoUpdateValue() throws Exception {
        PackageDefinition aPackage = new PackageDefinition();
        Assert.assertThat(aPackage.isAutoUpdate(), Matchers.is(true));
        aPackage.setAutoUpdate(false);
        Assert.assertThat(aPackage.isAutoUpdate(), Matchers.is(false));
    }

    @Test
    public void shouldValidateUniqueNames() {
        PackageDefinition packageDefinition = new PackageDefinition();
        packageDefinition.setName("PKG");
        HashMap<String, PackageDefinition> nameMap = new HashMap<>();
        PackageDefinition original = new PackageDefinition();
        original.setName("pkg");
        nameMap.put("pkg", original);
        packageDefinition.validateNameUniqueness(nameMap);
        Assert.assertThat(packageDefinition.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(packageDefinition.errors().getAllOn(NAME).contains("You have defined multiple packages called 'PKG'. Package names are case-insensitive and must be unique within a repository."), Matchers.is(true));
    }

    @Test
    public void shouldValidateUniqueKeysInConfiguration() {
        ConfigurationProperty one = new ConfigurationProperty(new ConfigurationKey("one"), new ConfigurationValue("value1"));
        ConfigurationProperty duplicate1 = new ConfigurationProperty(new ConfigurationKey("ONE"), new ConfigurationValue("value2"));
        ConfigurationProperty duplicate2 = new ConfigurationProperty(new ConfigurationKey("ONE"), new ConfigurationValue("value3"));
        ConfigurationProperty two = new ConfigurationProperty(new ConfigurationKey("two"), new ConfigurationValue());
        PackageDefinition packageDefinition = new PackageDefinition();
        packageDefinition.setConfiguration(new Configuration(one, duplicate1, duplicate2, two));
        packageDefinition.setName("go-server");
        packageDefinition.validate(null);
        Assert.assertThat(one.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(one.errors().getAllOn(CONFIGURATION_KEY).contains("Duplicate key 'ONE' found for Package 'go-server'"), Matchers.is(true));
        Assert.assertThat(duplicate1.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(one.errors().getAllOn(CONFIGURATION_KEY).contains("Duplicate key 'ONE' found for Package 'go-server'"), Matchers.is(true));
        Assert.assertThat(duplicate2.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(one.errors().getAllOn(CONFIGURATION_KEY).contains("Duplicate key 'ONE' found for Package 'go-server'"), Matchers.is(true));
        Assert.assertThat(two.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldGenerateIdIfNotAssigned() {
        PackageDefinition packageDefinition = new PackageDefinition();
        packageDefinition.ensureIdExists();
        Assert.assertThat(packageDefinition.getId(), Matchers.is(Matchers.notNullValue()));
        packageDefinition = new PackageDefinition();
        packageDefinition.setId("id");
        packageDefinition.ensureIdExists();
        Assert.assertThat(packageDefinition.getId(), Matchers.is("id"));
    }

    @Test
    public void shouldAddFingerprintFieldErrorWhenPackageDefinitionWithSameFingerprintExist() throws Exception {
        String expectedErrorMessage = "Cannot save package or repo, found duplicate packages. [Repo Name: 'repo-repo1', Package Name: 'pkg1'], [Repo Name: 'repo-repo1', Package Name: 'pkg3']";
        PackageRepository repository = PackageRepositoryMother.create("repo1");
        PackageDefinition definition1 = PackageDefinitionMother.create("1", "pkg1", new Configuration(new ConfigurationProperty(new ConfigurationKey("k1"), new ConfigurationValue("v1"))), repository);
        PackageDefinition definition2 = PackageDefinitionMother.create("2", "pkg2", new Configuration(new ConfigurationProperty(new ConfigurationKey("k2"), new ConfigurationValue("v2"))), repository);
        PackageDefinition definition3 = PackageDefinitionMother.create("3", "pkg3", new Configuration(new ConfigurationProperty(new ConfigurationKey("k1"), new ConfigurationValue("v1"))), repository);
        HashMap<String, Packages> map = new HashMap<>();
        map.put(definition1.getFingerprint(AbstractMaterialConfig.FINGERPRINT_DELIMITER), new Packages(definition1, definition3));
        map.put(definition2.getFingerprint(AbstractMaterialConfig.FINGERPRINT_DELIMITER), new Packages(definition2));
        definition1.validateFingerprintUniqueness(map);
        definition2.validateFingerprintUniqueness(map);
        definition3.validateFingerprintUniqueness(map);
        Assert.assertThat(definition1.errors().getAllOn(ID), Matchers.is(Arrays.asList(expectedErrorMessage)));
        Assert.assertThat(definition3.errors().getAllOn(ID), Matchers.is(Arrays.asList(expectedErrorMessage)));
        Assert.assertThat(definition2.errors().getAllOn(ID), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldNotAddFingerprintFieldErrorWhenPackageDefinitionWithSameFingerprintNotFound() throws Exception {
        PackageRepository repository = PackageRepositoryMother.create("repo1");
        PackageDefinition packageDefinition = PackageDefinitionMother.create("1", "pkg1", new Configuration(new ConfigurationProperty(new ConfigurationKey("k1"), new ConfigurationValue("v1"))), repository);
        HashMap<String, Packages> map = new HashMap<>();
        map.put(packageDefinition.getFingerprint(AbstractMaterialConfig.FINGERPRINT_DELIMITER), new Packages(packageDefinition));
        packageDefinition.validateFingerprintUniqueness(map);
        Assert.assertThat(packageDefinition.errors().getAllOn(ID), Matchers.is(Matchers.nullValue()));
    }
}

