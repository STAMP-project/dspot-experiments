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


import EnvironmentVariableContext.EnvironmentVariable.MASK_VALUE;
import com.google.gson.Gson;
import com.thoughtworks.go.config.CaseInsensitiveString;
import com.thoughtworks.go.config.PipelineConfig;
import com.thoughtworks.go.domain.materials.MatchedRevision;
import com.thoughtworks.go.domain.materials.Modification;
import com.thoughtworks.go.domain.materials.Modifications;
import com.thoughtworks.go.domain.materials.packagematerial.PackageMaterialInstance;
import com.thoughtworks.go.domain.materials.packagematerial.PackageMaterialRevision;
import com.thoughtworks.go.helper.MaterialsMother;
import com.thoughtworks.go.plugin.access.packagematerial.PackageConfigurations;
import com.thoughtworks.go.plugin.access.packagematerial.PackageMetadataStore;
import com.thoughtworks.go.plugin.access.packagematerial.RepositoryMetadataStore;
import com.thoughtworks.go.security.CryptoException;
import com.thoughtworks.go.security.GoCipher;
import com.thoughtworks.go.util.CachedDigestUtils;
import com.thoughtworks.go.util.command.EnvironmentVariableContext;
import com.thoughtworks.go.util.json.JsonHelper;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class PackageMaterialTest {
    @Test
    public void shouldCreatePackageMaterialInstance() {
        PackageMaterial material = MaterialsMother.packageMaterial();
        PackageMaterialInstance materialInstance = ((PackageMaterialInstance) (material.createMaterialInstance()));
        Assert.assertThat(materialInstance, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(materialInstance.getFlyweightName(), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(materialInstance.getConfiguration(), Matchers.is(JsonHelper.toJsonString(material)));
    }

    @Test
    public void shouldGetMaterialInstanceType() {
        Assert.assertThat(new PackageMaterial().getInstanceType().equals(PackageMaterialInstance.class), Matchers.is(true));
    }

    @Test
    public void shouldGetSqlCriteria() {
        PackageMaterial material = new PackageMaterial();
        PackageRepository repository = PackageRepositoryMother.create("repo-id", "repo", "pluginid", "version", new Configuration(ConfigurationPropertyMother.create("k1", false, "v1")));
        material.setPackageDefinition(PackageDefinitionMother.create("p-id", "name", new Configuration(ConfigurationPropertyMother.create("k2", false, "v2")), repository));
        Map<String, Object> criteria = material.getSqlCriteria();
        Assert.assertThat(criteria.get("type"), Matchers.is(PackageMaterial.class.getSimpleName()));
        Assert.assertThat(criteria.get("fingerprint"), Matchers.is(material.getFingerprint()));
    }

    @Test
    public void shouldGetFingerprintForMaterial() {
        PackageMaterial material = new PackageMaterial();
        PackageRepository repository = PackageRepositoryMother.create("repo-id", "repo", "pluginid", "version", new Configuration(ConfigurationPropertyMother.create("k1", false, "v1"), ConfigurationPropertyMother.create("secure-key", true, "secure-value")));
        material.setPackageDefinition(PackageDefinitionMother.create("p-id", "name", new Configuration(ConfigurationPropertyMother.create("k2", false, "v2")), repository));
        Assert.assertThat(material.getFingerprint(), Matchers.is(CachedDigestUtils.sha256Hex("plugin-id=pluginid<|>k2=v2<|>k1=v1<|>secure-key=secure-value")));
    }

    @Test
    public void shouldGetDifferentFingerprintWhenPluginIdChanges() {
        PackageMaterial material = new PackageMaterial();
        PackageRepository repository = PackageRepositoryMother.create("repo-id", "repo", "yum-1", "version", new Configuration(ConfigurationPropertyMother.create("k1", false, "v1")));
        material.setPackageDefinition(PackageDefinitionMother.create("p-id-1", "name", new Configuration(ConfigurationPropertyMother.create("k2", false, "v2")), repository));
        PackageMaterial anotherMaterial = new PackageMaterial();
        PackageRepository anotherRepository = PackageRepositoryMother.create("repo-id", "repo", "yum-2", "version", new Configuration(ConfigurationPropertyMother.create("k1", false, "v1")));
        anotherMaterial.setPackageDefinition(PackageDefinitionMother.create("p-id-2", "name", new Configuration(ConfigurationPropertyMother.create("k2", false, "v2")), anotherRepository));
        Assert.assertThat(material.getFingerprint().equals(anotherMaterial.getFingerprint()), Matchers.is(false));
    }

    @Test
    public void shouldGetDescription() {
        PackageMaterial material = new PackageMaterial();
        PackageRepository repository = PackageRepositoryMother.create("repo-id", "repo-name", "pluginid", "version", new Configuration(ConfigurationPropertyMother.create("k1", false, "v1")));
        material.setPackageDefinition(PackageDefinitionMother.create("p-id", "package-name", new Configuration(ConfigurationPropertyMother.create("k2", false, "v2")), repository));
        Assert.assertThat(material.getDescription(), Matchers.is("repo-name:package-name"));
    }

    @Test
    public void shouldGetDisplayName() {
        PackageMaterial material = new PackageMaterial();
        PackageRepository repository = PackageRepositoryMother.create("repo-id", "repo-name", "pluginid", "version", new Configuration(ConfigurationPropertyMother.create("k1", false, "v1")));
        material.setPackageDefinition(PackageDefinitionMother.create("p-id", "package-name", new Configuration(ConfigurationPropertyMother.create("k2", false, "v2")), repository));
        Assert.assertThat(material.getDisplayName(), Matchers.is("repo-name:package-name"));
    }

    @Test
    public void shouldTypeForDisplay() {
        PackageMaterial material = new PackageMaterial();
        Assert.assertThat(material.getTypeForDisplay(), Matchers.is("Package"));
    }

    @Test
    public void shouldGetAttributesForXml() {
        PackageMaterial material = new PackageMaterial();
        PackageRepository repository = PackageRepositoryMother.create("repo-id", "repo-name", "pluginid", "version", new Configuration(ConfigurationPropertyMother.create("k1", false, "v1")));
        material.setPackageDefinition(PackageDefinitionMother.create("p-id", "package-name", new Configuration(ConfigurationPropertyMother.create("k2", false, "v2")), repository));
        Map<String, Object> attributesForXml = material.getAttributesForXml();
        Assert.assertThat(attributesForXml.get("type").toString(), Matchers.is("PackageMaterial"));
        Assert.assertThat(attributesForXml.get("repositoryName").toString(), Matchers.is("repo-name"));
        Assert.assertThat(attributesForXml.get("packageName").toString(), Matchers.is("package-name"));
    }

    @Test
    public void shouldConvertPackageMaterialToJsonFormatToBeStoredInDb() throws CryptoException {
        GoCipher cipher = new GoCipher();
        String encryptedPassword = cipher.encrypt("password");
        ConfigurationProperty secureRepoProperty = new ConfigurationProperty(new ConfigurationKey("secure-key"), null, new EncryptedConfigurationValue(encryptedPassword), cipher);
        ConfigurationProperty repoProperty = new ConfigurationProperty(new ConfigurationKey("non-secure-key"), new ConfigurationValue("value"), null, cipher);
        PackageRepository packageRepository = new PackageRepository();
        packageRepository.setPluginConfiguration(new PluginConfiguration("plugin-id", "1.0"));
        packageRepository.setConfiguration(new Configuration(secureRepoProperty, repoProperty));
        ConfigurationProperty securePackageProperty = new ConfigurationProperty(new ConfigurationKey("secure-key"), null, new EncryptedConfigurationValue(encryptedPassword), cipher);
        ConfigurationProperty packageProperty = new ConfigurationProperty(new ConfigurationKey("non-secure-key"), new ConfigurationValue("value"), null, cipher);
        PackageDefinition packageDefinition = new PackageDefinition("id", "name", new Configuration(securePackageProperty, packageProperty));
        packageDefinition.setRepository(packageRepository);
        PackageMaterial packageMaterial = new PackageMaterial("id");
        packageMaterial.setPackageDefinition(packageDefinition);
        String json = JsonHelper.toJsonString(packageMaterial);
        String expected = ((("{\"package\":{\"config\":[{\"configKey\":{\"name\":\"secure-key\"},\"encryptedConfigValue\":{\"value\":" + (new Gson().toJson(encryptedPassword))) + "}},{\"configKey\":{\"name\":\"non-secure-key\"},\"configValue\":{\"value\":\"value\"}}],\"repository\":{\"plugin\":{\"id\":\"plugin-id\",\"version\":\"1.0\"},\"config\":[{\"configKey\":{\"name\":\"secure-key\"},\"encryptedConfigValue\":{\"value\":") + (new Gson().toJson(encryptedPassword))) + "}},{\"configKey\":{\"name\":\"non-secure-key\"},\"configValue\":{\"value\":\"value\"}}]}}}";
        Assert.assertThat(json, Matchers.is(expected));
        Assert.assertThat(JsonHelper.fromJson(expected, PackageMaterial.class), Matchers.is(packageMaterial));
    }

    @Test
    public void shouldGetJsonRepresentationForPackageMaterial() {
        PackageMaterial material = new PackageMaterial();
        PackageRepository repository = create("repo-id", "repo-name", "pluginid", "version", new Configuration(ConfigurationPropertyMother.create("k1", false, "v1")));
        material.setPackageDefinition(PackageDefinitionMother.create("p-id", "package-name", new Configuration(ConfigurationPropertyMother.create("k2", false, "v2")), repository));
        Map<String, String> jsonMap = new LinkedHashMap<>();
        material.toJson(jsonMap, new PackageMaterialRevision("rev123", new Date()));
        Assert.assertThat(jsonMap.get("scmType"), Matchers.is("Package"));
        Assert.assertThat(jsonMap.get("materialName"), Matchers.is("repo-name:package-name"));
        Assert.assertThat(jsonMap.get("action"), Matchers.is("Modified"));
        Assert.assertThat(jsonMap.get("location"), Matchers.is(material.getUriForDisplay()));
    }

    @Test
    public void shouldGetEmailContentForPackageMaterial() {
        PackageMaterial material = new PackageMaterial();
        PackageRepository repository = PackageRepositoryMother.create("repo-id", "repo-name", "pluginid", "version", new Configuration(ConfigurationPropertyMother.create("k1", false, "v1")));
        material.setPackageDefinition(PackageDefinitionMother.create("p-id", "package-name", new Configuration(ConfigurationPropertyMother.create("k2", false, "v2")), repository));
        StringBuilder content = new StringBuilder();
        Date date = new Date(1367472329111L);
        material.emailContent(content, new Modification(null, null, null, date, "rev123"));
        Assert.assertThat(content.toString(), Matchers.is(String.format("Package : repo-name:package-name\nrevision: rev123, completed on %s", date.toString())));
    }

    @Test
    public void shouldReturnFalseForIsUsedInFetchArtifact() {
        PackageMaterial material = new PackageMaterial();
        Assert.assertThat(material.isUsedInFetchArtifact(new PipelineConfig()), Matchers.is(false));
    }

    @Test
    public void shouldReturnMatchedRevisionForPackageMaterial() {
        PackageMaterial material = new PackageMaterial();
        PackageRepository repository = PackageRepositoryMother.create("repo-id", "repo-name", "pluginid", "version", new Configuration(ConfigurationPropertyMother.create("k1", false, "v1")));
        material.setPackageDefinition(PackageDefinitionMother.create("p-id", "package-name", new Configuration(ConfigurationPropertyMother.create("k2", false, "v2")), repository));
        Date timestamp = new Date();
        MatchedRevision matchedRevision = material.createMatchedRevision(new Modification("go", "comment", null, timestamp, "rev123"), "rev");
        Assert.assertThat(matchedRevision.getShortRevision(), Matchers.is("rev123"));
        Assert.assertThat(matchedRevision.getLongRevision(), Matchers.is("rev123"));
        Assert.assertThat(matchedRevision.getCheckinTime(), Matchers.is(timestamp));
        Assert.assertThat(matchedRevision.getUser(), Matchers.is("go"));
        Assert.assertThat(matchedRevision.getComment(), Matchers.is("comment"));
    }

    @Test
    public void shouldGetNameFromRepoNameAndPackageName() {
        PackageMaterial material = new PackageMaterial();
        PackageRepository repository = PackageRepositoryMother.create("repo-id", "repo-name", "pluginid", "version", new Configuration(ConfigurationPropertyMother.create("k1", false, "v1")));
        material.setPackageDefinition(PackageDefinitionMother.create("p-id", "package-name", new Configuration(ConfigurationPropertyMother.create("k2", false, "v2")), repository));
        Assert.assertThat(material.getName().toString(), Matchers.is("repo-name:package-name"));
    }

    @Test
    public void shouldPopulateEnvironmentContext() {
        PackageMaterial material = new PackageMaterial();
        PackageRepository repository = PackageRepositoryMother.create("repo-id", "tw-dev", "pluginid", "version", new Configuration(ConfigurationPropertyMother.create("k1", false, "v1"), ConfigurationPropertyMother.create("repo-secure", true, "value")));
        material.setPackageDefinition(PackageDefinitionMother.create("p-id", "go-agent", new Configuration(ConfigurationPropertyMother.create("k2", false, "v2"), ConfigurationPropertyMother.create("pkg-secure", true, "value")), repository));
        material.setName(new CaseInsensitiveString("tw-dev:go-agent"));
        Modifications modifications = new Modifications(new Modification(null, null, null, new Date(), "revision-123"));
        EnvironmentVariableContext environmentVariableContext = new EnvironmentVariableContext();
        material.populateEnvironmentContext(environmentVariableContext, new com.thoughtworks.go.domain.MaterialRevision(material, modifications), null);
        Assert.assertThat(environmentVariableContext.getProperty("GO_REPO_TW_DEV_GO_AGENT_K1"), Matchers.is("v1"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_REPO_TW_DEV_GO_AGENT_REPO_SECURE"), Matchers.is("value"));
        Assert.assertThat(environmentVariableContext.getPropertyForDisplay("GO_REPO_TW_DEV_GO_AGENT_REPO_SECURE"), Matchers.is(MASK_VALUE));
        Assert.assertThat(environmentVariableContext.getProperty("GO_PACKAGE_TW_DEV_GO_AGENT_K2"), Matchers.is("v2"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_PACKAGE_TW_DEV_GO_AGENT_PKG_SECURE"), Matchers.is("value"));
        Assert.assertThat(environmentVariableContext.getPropertyForDisplay("GO_PACKAGE_TW_DEV_GO_AGENT_PKG_SECURE"), Matchers.is(MASK_VALUE));
        Assert.assertThat(environmentVariableContext.getProperty("GO_PACKAGE_TW_DEV_GO_AGENT_LABEL"), Matchers.is("revision-123"));
    }

    @Test
    public void shouldPopulateEnvironmentContextWithEnvironmentVariablesCreatedOutOfAdditionalDataFromModification() {
        PackageMaterial material = new PackageMaterial();
        PackageRepository repository = PackageRepositoryMother.create("repo-id", "tw-dev", "pluginid", "version", new Configuration(ConfigurationPropertyMother.create("k1", false, "v1")));
        material.setPackageDefinition(PackageDefinitionMother.create("p-id", "go-agent", new Configuration(ConfigurationPropertyMother.create("k2", false, "v2")), repository));
        material.setName(new CaseInsensitiveString("tw-dev:go-agent"));
        HashMap<String, String> map = new HashMap<>();
        map.put("MY_NEW_KEY", "my_value");
        Modification modification = new Modification("loser", "comment", "email", new Date(), "revision-123", JsonHelper.toJsonString(map));
        Modifications modifications = new Modifications(modification);
        EnvironmentVariableContext environmentVariableContext = new EnvironmentVariableContext();
        material.populateEnvironmentContext(environmentVariableContext, new com.thoughtworks.go.domain.MaterialRevision(material, modifications), null);
        Assert.assertThat(environmentVariableContext.getProperty("GO_PACKAGE_TW_DEV_GO_AGENT_LABEL"), Matchers.is("revision-123"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_REPO_TW_DEV_GO_AGENT_K1"), Matchers.is("v1"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_PACKAGE_TW_DEV_GO_AGENT_K2"), Matchers.is("v2"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_PACKAGE_TW_DEV_GO_AGENT_MY_NEW_KEY"), Matchers.is("my_value"));
    }

    @Test
    public void shouldMarkEnvironmentContextCreatedForAdditionalDataAsSecureIfTheValueContainsAnySpecialCharacters() throws UnsupportedEncodingException {
        PackageMaterial material = new PackageMaterial();
        PackageRepository repository = PackageRepositoryMother.create("repo-id", "tw-dev", "pluginid", "version", new Configuration(ConfigurationPropertyMother.create("k1", false, "v1")));
        material.setPackageDefinition(PackageDefinitionMother.create("p-id", "go-agent", new Configuration(ConfigurationPropertyMother.create("k2", true, "!secure_value:with_special_chars"), ConfigurationPropertyMother.create("k3", true, "secure_value_with_regular_chars")), repository));
        material.setName(new CaseInsensitiveString("tw-dev:go-agent"));
        HashMap<String, String> map = new HashMap<>();
        map.put("ADDITIONAL_DATA_ONE", "foobar:!secure_value:with_special_chars");
        map.put("ADDITIONAL_DATA_URL_ENCODED", "something:%21secure_value%3Awith_special_chars");
        map.put("ADDITIONAL_DATA_TWO", "foobar:secure_value_with_regular_chars");
        Modification modification = new Modification("loser", "comment", "email", new Date(), "revision-123", JsonHelper.toJsonString(map));
        Modifications modifications = new Modifications(modification);
        EnvironmentVariableContext environmentVariableContext = new EnvironmentVariableContext();
        material.populateEnvironmentContext(environmentVariableContext, new com.thoughtworks.go.domain.MaterialRevision(material, modifications), null);
        Assert.assertThat(environmentVariableContext.getProperty("GO_PACKAGE_TW_DEV_GO_AGENT_LABEL"), Matchers.is("revision-123"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_REPO_TW_DEV_GO_AGENT_K1"), Matchers.is("v1"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_PACKAGE_TW_DEV_GO_AGENT_K2"), Matchers.is("!secure_value:with_special_chars"));
        Assert.assertThat(environmentVariableContext.getPropertyForDisplay("GO_PACKAGE_TW_DEV_GO_AGENT_K2"), Matchers.is("********"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_PACKAGE_TW_DEV_GO_AGENT_ADDITIONAL_DATA_ONE"), Matchers.is("foobar:!secure_value:with_special_chars"));
        Assert.assertThat(environmentVariableContext.getPropertyForDisplay("GO_PACKAGE_TW_DEV_GO_AGENT_ADDITIONAL_DATA_ONE"), Matchers.is("foobar:!secure_value:with_special_chars"));
        Assert.assertThat(environmentVariableContext.getPropertyForDisplay("GO_PACKAGE_TW_DEV_GO_AGENT_ADDITIONAL_DATA_TWO"), Matchers.is("foobar:secure_value_with_regular_chars"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_PACKAGE_TW_DEV_GO_AGENT_ADDITIONAL_DATA_URL_ENCODED"), Matchers.is("something:%21secure_value%3Awith_special_chars"));
        Assert.assertThat(environmentVariableContext.getPropertyForDisplay("GO_PACKAGE_TW_DEV_GO_AGENT_ADDITIONAL_DATA_URL_ENCODED"), Matchers.is("********"));
    }

    @Test
    public void shouldNotThrowUpWhenAdditionalDataIsNull() {
        PackageMaterial material = new PackageMaterial();
        PackageRepository repository = PackageRepositoryMother.create("repo-id", "tw-dev", "pluginid", "version", new Configuration(ConfigurationPropertyMother.create("k1", false, "v1")));
        material.setPackageDefinition(PackageDefinitionMother.create("p-id", "go-agent", new Configuration(ConfigurationPropertyMother.create("k2", false, "v2")), repository));
        material.setName(new CaseInsensitiveString("tw-dev:go-agent"));
        Modifications modifications = new Modifications(new Modification("loser", "comment", "email", new Date(), "revision-123", null));
        EnvironmentVariableContext environmentVariableContext = new EnvironmentVariableContext();
        material.populateEnvironmentContext(environmentVariableContext, new com.thoughtworks.go.domain.MaterialRevision(material, modifications), null);
        Assert.assertThat(environmentVariableContext.getProperty("GO_PACKAGE_TW_DEV_GO_AGENT_LABEL"), Matchers.is("revision-123"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_REPO_TW_DEV_GO_AGENT_K1"), Matchers.is("v1"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_PACKAGE_TW_DEV_GO_AGENT_K2"), Matchers.is("v2"));
    }

    @Test
    public void shouldNotThrowUpWhenAdditionalDataIsRandomJunkAndNotJSON() {
        PackageMaterial material = new PackageMaterial();
        PackageRepository repository = PackageRepositoryMother.create("repo-id", "tw-dev", "pluginid", "version", new Configuration(ConfigurationPropertyMother.create("k1", false, "v1")));
        material.setPackageDefinition(PackageDefinitionMother.create("p-id", "go-agent", new Configuration(ConfigurationPropertyMother.create("k2", false, "v2")), repository));
        material.setName(new CaseInsensitiveString("tw-dev:go-agent"));
        Modifications modifications = new Modifications(new Modification("loser", "comment", "email", new Date(), "revision-123", "salkdfjdsa-jjgkj!!!vcxknbvkjk"));
        EnvironmentVariableContext environmentVariableContext = new EnvironmentVariableContext();
        material.populateEnvironmentContext(environmentVariableContext, new com.thoughtworks.go.domain.MaterialRevision(material, modifications), null);
        Assert.assertThat(environmentVariableContext.getProperty("GO_PACKAGE_TW_DEV_GO_AGENT_LABEL"), Matchers.is("revision-123"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_REPO_TW_DEV_GO_AGENT_K1"), Matchers.is("v1"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_PACKAGE_TW_DEV_GO_AGENT_K2"), Matchers.is("v2"));
    }

    @Test
    public void shouldGetUriForDisplay() {
        RepositoryMetadataStore.getInstance().addMetadataFor("some-plugin", new PackageConfigurations());
        PackageMetadataStore.getInstance().addMetadataFor("some-plugin", new PackageConfigurations());
        PackageMaterial material = new PackageMaterial();
        Configuration configuration = new Configuration(ConfigurationPropertyMother.create("k1", false, "repo-v1"), ConfigurationPropertyMother.create("k2", false, "repo-v2"));
        PackageRepository repository = PackageRepositoryMother.create("repo-id", "repo-name", "some-plugin", "version", configuration);
        PackageDefinition packageDefinition = PackageDefinitionMother.create("p-id", "package-name", new Configuration(ConfigurationPropertyMother.create("k3", false, "package-v1")), repository);
        material.setPackageDefinition(packageDefinition);
        Assert.assertThat(material.getUriForDisplay(), Matchers.is("Repository: [k1=repo-v1, k2=repo-v2] - Package: [k3=package-v1]"));
    }

    @Test
    public void shouldGetUriForDisplayNameIfNameIsNull() {
        PackageMaterial material = new PackageMaterial();
        PackageRepository repository = PackageRepositoryMother.create("repo-id", null, "pluginid", "version", new Configuration(ConfigurationPropertyMother.create("k1", false, "repo-v1"), ConfigurationPropertyMother.create("k2", false, "repo-v2")));
        material.setPackageDefinition(PackageDefinitionMother.create("p-id", null, new Configuration(ConfigurationPropertyMother.create("k3", false, "package-v1")), repository));
        Assert.assertThat(material.getDisplayName(), Matchers.is(material.getUriForDisplay()));
    }

    @Test
    public void shouldGetLongDescription() {
        PackageMaterial material = new PackageMaterial();
        Configuration configuration = new Configuration(ConfigurationPropertyMother.create("k1", false, "repo-v1"), ConfigurationPropertyMother.create("k2", false, "repo-v2"));
        PackageRepository repository = PackageRepositoryMother.create("repo-id", "repo-name", "pluginid", "version", configuration);
        PackageDefinition packageDefinition = PackageDefinitionMother.create("p-id", "package-name", new Configuration(ConfigurationPropertyMother.create("k3", false, "package-v1")), repository);
        material.setPackageDefinition(packageDefinition);
        Assert.assertThat(material.getLongDescription(), Matchers.is(material.getUriForDisplay()));
    }

    @Test
    public void shouldPassEqualsCheckIfFingerprintIsSame() {
        PackageMaterial material1 = MaterialsMother.packageMaterial();
        material1.setName(new CaseInsensitiveString("name1"));
        PackageMaterial material2 = MaterialsMother.packageMaterial();
        material2.setName(new CaseInsensitiveString("name2"));
        Assert.assertThat(material1.equals(material2), Matchers.is(true));
    }

    @Test
    public void shouldFailEqualsCheckIfFingerprintDiffers() {
        PackageMaterial material1 = MaterialsMother.packageMaterial();
        material1.getPackageDefinition().getConfiguration().first().setConfigurationValue(new ConfigurationValue("new-url"));
        PackageMaterial material2 = MaterialsMother.packageMaterial();
        Assert.assertThat(material1.equals(material2), Matchers.is(false));
    }

    @Test
    public void shouldReturnSomethingMoreSaneForToString() throws Exception {
        PackageMaterial material = MaterialsMother.packageMaterial();
        RepositoryMetadataStore.getInstance().addMetadataFor(material.getPluginId(), new PackageConfigurations());
        PackageMetadataStore.getInstance().addMetadataFor(material.getPluginId(), new PackageConfigurations());
        Assert.assertThat(material.toString(), Matchers.is("'PackageMaterial{Repository: [k1=repo-v1, k2=repo-v2] - Package: [k3=package-v1]}'"));
    }

    @Test
    public void shouldReturnNameAsNullIfPackageDefinitionIsNotSet() {
        Assert.assertThat(new PackageMaterial().getName(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldNotCalculateFingerprintWhenAvailable() {
        String fingerprint = "fingerprint";
        PackageDefinition packageDefinition = Mockito.mock(PackageDefinition.class);
        PackageMaterial packageMaterial = new PackageMaterial();
        packageMaterial.setPackageDefinition(packageDefinition);
        packageMaterial.setFingerprint(fingerprint);
        Assert.assertThat(packageMaterial.getFingerprint(), Matchers.is(fingerprint));
        Mockito.verify(packageDefinition, Mockito.never()).getFingerprint(ArgumentMatchers.anyString());
    }

    @Test
    public void shouldTakeValueOfIsAutoUpdateFromPackageDefinition() throws Exception {
        PackageMaterial material = MaterialsMother.packageMaterial();
        material.getPackageDefinition().setAutoUpdate(true);
        Assert.assertThat(material.isAutoUpdate(), Matchers.is(true));
        material.getPackageDefinition().setAutoUpdate(false);
        Assert.assertThat(material.isAutoUpdate(), Matchers.is(false));
    }

    @Test
    public void shouldGetAttributesWithSecureFields() {
        PackageMaterial material = createPackageMaterialWithSecureConfiguration();
        Map<String, Object> attributes = material.getAttributes(true);
        Assert.assertThat(attributes.get("type"), Matchers.is("package"));
        Assert.assertThat(attributes.get("plugin-id"), Matchers.is("pluginid"));
        Map<String, Object> repositoryConfiguration = ((Map<String, Object>) (attributes.get("repository-configuration")));
        Assert.assertThat(repositoryConfiguration.get("k1"), Matchers.is("repo-v1"));
        Assert.assertThat(repositoryConfiguration.get("k2"), Matchers.is("repo-v2"));
        Map<String, Object> packageConfiguration = ((Map<String, Object>) (attributes.get("package-configuration")));
        Assert.assertThat(packageConfiguration.get("k3"), Matchers.is("package-v1"));
        Assert.assertThat(packageConfiguration.get("k4"), Matchers.is("package-v2"));
    }

    @Test
    public void shouldGetAttributesWithoutSecureFields() {
        PackageMaterial material = createPackageMaterialWithSecureConfiguration();
        Map<String, Object> attributes = material.getAttributes(false);
        Assert.assertThat(attributes.get("type"), Matchers.is("package"));
        Assert.assertThat(attributes.get("plugin-id"), Matchers.is("pluginid"));
        Map<String, Object> repositoryConfiguration = ((Map<String, Object>) (attributes.get("repository-configuration")));
        Assert.assertThat(repositoryConfiguration.get("k1"), Matchers.is("repo-v1"));
        Assert.assertThat(repositoryConfiguration.get("k2"), Matchers.is(Matchers.nullValue()));
        Map<String, Object> packageConfiguration = ((Map<String, Object>) (attributes.get("package-configuration")));
        Assert.assertThat(packageConfiguration.get("k3"), Matchers.is("package-v1"));
        Assert.assertThat(packageConfiguration.get("k4"), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldReturnFalseForPackageMaterial_supportsDestinationFolder() throws Exception {
        PackageMaterial material = new PackageMaterial();
        Assert.assertThat(material.supportsDestinationFolder(), Matchers.is(false));
    }
}

