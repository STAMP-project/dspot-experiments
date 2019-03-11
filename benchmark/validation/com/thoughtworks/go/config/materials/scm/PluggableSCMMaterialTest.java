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
package com.thoughtworks.go.config.materials.scm;


import EnvironmentVariableContext.EnvironmentVariable.MASK_VALUE;
import com.google.gson.Gson;
import com.thoughtworks.go.config.CaseInsensitiveString;
import com.thoughtworks.go.config.PipelineConfig;
import com.thoughtworks.go.config.materials.PluggableSCMMaterial;
import com.thoughtworks.go.config.materials.PluggableSCMMaterialConfig;
import com.thoughtworks.go.domain.materials.MatchedRevision;
import com.thoughtworks.go.domain.materials.Modification;
import com.thoughtworks.go.domain.materials.Modifications;
import com.thoughtworks.go.domain.materials.scm.PluggableSCMMaterialInstance;
import com.thoughtworks.go.domain.materials.scm.PluggableSCMMaterialRevision;
import com.thoughtworks.go.domain.packagerepository.ConfigurationPropertyMother;
import com.thoughtworks.go.domain.scm.SCM;
import com.thoughtworks.go.domain.scm.SCMMother;
import com.thoughtworks.go.helper.MaterialConfigsMother;
import com.thoughtworks.go.helper.MaterialsMother;
import com.thoughtworks.go.plugin.access.scm.SCMConfigurations;
import com.thoughtworks.go.plugin.access.scm.SCMMetadataStore;
import com.thoughtworks.go.plugin.access.scm.SCMView;
import com.thoughtworks.go.security.CryptoException;
import com.thoughtworks.go.security.GoCipher;
import com.thoughtworks.go.util.CachedDigestUtils;
import com.thoughtworks.go.util.command.EnvironmentVariableContext;
import com.thoughtworks.go.util.json.JsonHelper;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class PluggableSCMMaterialTest {
    @Test
    public void shouldCreatePluggableSCMMaterialInstance() {
        PluggableSCMMaterial material = MaterialsMother.pluggableSCMMaterial();
        PluggableSCMMaterialInstance materialInstance = ((PluggableSCMMaterialInstance) (material.createMaterialInstance()));
        Assert.assertThat(materialInstance, Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(materialInstance.getFlyweightName(), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(materialInstance.getConfiguration(), Matchers.is(JsonHelper.toJsonString(material)));
    }

    @Test
    public void shouldGetMaterialInstanceType() {
        Assert.assertThat(new PluggableSCMMaterial().getInstanceType().equals(PluggableSCMMaterialInstance.class), Matchers.is(true));
    }

    @Test
    public void shouldGetSqlCriteria() {
        SCM scmConfig = SCMMother.create("scm-id", "scm-name", "pluginid", "version", new Configuration(ConfigurationPropertyMother.create("k1", false, "v1")));
        PluggableSCMMaterial material = new PluggableSCMMaterial();
        material.setSCMConfig(scmConfig);
        Map<String, Object> criteria = material.getSqlCriteria();
        Assert.assertThat(criteria.get("type"), Matchers.is(PluggableSCMMaterial.class.getSimpleName()));
        Assert.assertThat(criteria.get("fingerprint"), Matchers.is(material.getFingerprint()));
    }

    @Test
    public void shouldGetFingerprintForMaterial() {
        ConfigurationProperty k1 = ConfigurationPropertyMother.create("k1", false, "v1");
        ConfigurationProperty k2 = ConfigurationPropertyMother.create("secure-key", true, "secure-value");
        SCM scmConfig = SCMMother.create("scm-id", "scm-name", "pluginid", "version", new Configuration(k1, k2));
        PluggableSCMMaterial material = new PluggableSCMMaterial();
        material.setSCMConfig(scmConfig);
        Assert.assertThat(material.getFingerprint(), Matchers.is(CachedDigestUtils.sha256Hex("plugin-id=pluginid<|>k1=v1<|>secure-key=secure-value")));
    }

    @Test
    public void shouldGetDifferentFingerprintWhenPluginIdChanges() {
        SCM scmConfig = SCMMother.create("scm-id", "scm-name", "plugin-1", "version", new Configuration(ConfigurationPropertyMother.create("k1", false, "v1")));
        PluggableSCMMaterial material = new PluggableSCMMaterial();
        material.setSCMConfig(scmConfig);
        SCM anotherSCMConfig = SCMMother.create("scm-id", "scm-name", "plugin-2", "version", new Configuration(ConfigurationPropertyMother.create("k1", false, "v1")));
        PluggableSCMMaterial anotherMaterial = new PluggableSCMMaterial();
        anotherMaterial.setSCMConfig(anotherSCMConfig);
        Assert.assertThat(material.getFingerprint().equals(anotherMaterial.getFingerprint()), Matchers.is(false));
    }

    @Test
    public void shouldGetDescription() {
        ConfigurationProperty k1 = ConfigurationPropertyMother.create("k1", false, "v1");
        SCM scmConfig = SCMMother.create("scm-id", "scm-name", "pluginid", "version", new Configuration(k1));
        PluggableSCMMaterial material = new PluggableSCMMaterial();
        material.setSCMConfig(scmConfig);
        Assert.assertThat(material.getDescription(), Matchers.is("scm-name"));
    }

    @Test
    public void shouldGetDisplayName() {
        ConfigurationProperty k1 = ConfigurationPropertyMother.create("k1", false, "v1");
        SCM scmConfig = SCMMother.create("scm-id", "scm-name", "pluginid", "version", new Configuration(k1));
        PluggableSCMMaterial material = new PluggableSCMMaterial();
        material.setSCMConfig(scmConfig);
        Assert.assertThat(material.getDisplayName(), Matchers.is("scm-name"));
    }

    @Test
    public void shouldTypeForDisplay() {
        PluggableSCMMaterial material = new PluggableSCMMaterial();
        Assert.assertThat(material.getTypeForDisplay(), Matchers.is("SCM"));
    }

    @Test
    public void shouldGetAttributesForXml() {
        ConfigurationProperty k1 = ConfigurationPropertyMother.create("k1", false, "v1");
        SCM scmConfig = SCMMother.create("scm-id", "scm-name", "pluginid", "version", new Configuration(k1));
        PluggableSCMMaterial material = new PluggableSCMMaterial();
        material.setSCMConfig(scmConfig);
        Map<String, Object> attributesForXml = material.getAttributesForXml();
        Assert.assertThat(attributesForXml.get("type").toString(), Matchers.is(PluggableSCMMaterial.class.getSimpleName()));
        Assert.assertThat(attributesForXml.get("scmName").toString(), Matchers.is("scm-name"));
    }

    @Test
    public void shouldConvertPluggableSCMMaterialToJsonFormatToBeStoredInDb() throws CryptoException {
        GoCipher cipher = new GoCipher();
        String encryptedPassword = cipher.encrypt("password");
        ConfigurationProperty secureSCMProperty = new ConfigurationProperty(new ConfigurationKey("secure-key"), null, new EncryptedConfigurationValue(encryptedPassword), cipher);
        ConfigurationProperty scmProperty = new ConfigurationProperty(new ConfigurationKey("non-secure-key"), new ConfigurationValue("value"), null, cipher);
        SCM scmConfig = SCMMother.create("scm-id", "scm-name", "plugin-id", "1.0", new Configuration(secureSCMProperty, scmProperty));
        PluggableSCMMaterial pluggableSCMMaterial = new PluggableSCMMaterial();
        pluggableSCMMaterial.setSCMConfig(scmConfig);
        String json = JsonHelper.toJsonString(pluggableSCMMaterial);
        String expected = ("{\"scm\":{\"plugin\":{\"id\":\"plugin-id\",\"version\":\"1.0\"},\"config\":[{\"configKey\":{\"name\":\"secure-key\"},\"encryptedConfigValue\":{\"value\":" + (new Gson().toJson(encryptedPassword))) + "}},{\"configKey\":{\"name\":\"non-secure-key\"},\"configValue\":{\"value\":\"value\"}}]}}";
        Assert.assertThat(json, Matchers.is(expected));
        Assert.assertThat(JsonHelper.fromJson(expected, PluggableSCMMaterial.class), Matchers.is(pluggableSCMMaterial));
    }

    @Test
    public void shouldGetJsonRepresentationForPluggableSCMMaterial() {
        ConfigurationProperty k1 = ConfigurationPropertyMother.create("k1", false, "v1");
        SCM scmConfig = SCMMother.create("scm-id", "scm-name", "pluginid", "version", new Configuration(k1));
        PluggableSCMMaterial material = new PluggableSCMMaterial();
        material.setSCMConfig(scmConfig);
        material.setFolder("folder");
        Map<String, String> jsonMap = new LinkedHashMap<>();
        material.toJson(jsonMap, new PluggableSCMMaterialRevision("rev123", new Date()));
        Assert.assertThat(jsonMap.get("scmType"), Matchers.is("SCM"));
        Assert.assertThat(jsonMap.get("materialName"), Matchers.is("scm-name"));
        Assert.assertThat(jsonMap.get("location"), Matchers.is(material.getUriForDisplay()));
        Assert.assertThat(jsonMap.get("folder"), Matchers.is("folder"));
        Assert.assertThat(jsonMap.get("action"), Matchers.is("Modified"));
    }

    @Test
    public void shouldGetEmailContentForPluggableSCMMaterial() {
        ConfigurationProperty k1 = ConfigurationPropertyMother.create("k1", false, "v1");
        SCM scmConfig = SCMMother.create("scm-id", "scm-name", "pluginid", "version", new Configuration(k1));
        PluggableSCMMaterial material = new PluggableSCMMaterial();
        material.setSCMConfig(scmConfig);
        StringBuilder content = new StringBuilder();
        Date date = new Date(1367472329111L);
        material.emailContent(content, new Modification(null, "comment", null, date, "rev123"));
        Assert.assertThat(content.toString(), Matchers.is(String.format("SCM : scm-name\nrevision: rev123, completed on %s\ncomment", date.toString())));
    }

    @Test
    public void shouldReturnFalseForIsUsedInFetchArtifact() {
        PluggableSCMMaterial material = new PluggableSCMMaterial();
        Assert.assertThat(material.isUsedInFetchArtifact(new PipelineConfig()), Matchers.is(false));
    }

    @Test
    public void shouldReturnMatchedRevisionForPluggableSCMMaterial() {
        ConfigurationProperty k1 = ConfigurationPropertyMother.create("k1", false, "v1");
        SCM scmConfig = SCMMother.create("scm-id", "scm-name", "pluginid", "version", new Configuration(k1));
        PluggableSCMMaterial material = new PluggableSCMMaterial();
        material.setSCMConfig(scmConfig);
        Date timestamp = new Date();
        MatchedRevision matchedRevision = material.createMatchedRevision(new Modification("go", "comment", null, timestamp, "rev123"), "rev");
        Assert.assertThat(matchedRevision.getShortRevision(), Matchers.is("rev123"));
        Assert.assertThat(matchedRevision.getLongRevision(), Matchers.is("rev123"));
        Assert.assertThat(matchedRevision.getCheckinTime(), Matchers.is(timestamp));
        Assert.assertThat(matchedRevision.getUser(), Matchers.is("go"));
        Assert.assertThat(matchedRevision.getComment(), Matchers.is("comment"));
    }

    @Test
    public void shouldGetNameFromSCMName() {
        ConfigurationProperty k1 = ConfigurationPropertyMother.create("k1", false, "v1");
        SCM scmConfig = SCMMother.create("scm-id", "scm-name", "pluginid", "version", new Configuration(k1));
        PluggableSCMMaterial material = new PluggableSCMMaterial();
        material.setSCMConfig(scmConfig);
        Assert.assertThat(material.getName().toString(), Matchers.is("scm-name"));
    }

    @Test
    public void shouldPopulateEnvironmentContext() {
        ConfigurationProperty k1 = ConfigurationPropertyMother.create("k1", false, "v1");
        ConfigurationProperty k2 = ConfigurationPropertyMother.create("scm-secure", true, "value");
        SCM scmConfig = SCMMother.create("scm-id", "tw-dev", "pluginid", "version", new Configuration(k1, k2));
        PluggableSCMMaterial material = new PluggableSCMMaterial();
        material.setSCMConfig(scmConfig);
        material.setName(new CaseInsensitiveString("tw-dev:go-agent"));
        Modifications modifications = new Modifications(new Modification(null, null, null, new Date(), "revision-123"));
        EnvironmentVariableContext environmentVariableContext = new EnvironmentVariableContext();
        material.populateEnvironmentContext(environmentVariableContext, new com.thoughtworks.go.domain.MaterialRevision(material, modifications), null);
        Assert.assertThat(environmentVariableContext.getProperty("GO_SCM_TW_DEV_GO_AGENT_K1"), Matchers.is("v1"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_SCM_TW_DEV_GO_AGENT_SCM_SECURE"), Matchers.is("value"));
        Assert.assertThat(environmentVariableContext.getPropertyForDisplay("GO_SCM_TW_DEV_GO_AGENT_SCM_SECURE"), Matchers.is(MASK_VALUE));
        Assert.assertThat(environmentVariableContext.getProperty("GO_SCM_TW_DEV_GO_AGENT_LABEL"), Matchers.is("revision-123"));
    }

    @Test
    public void shouldPopulateEnvironmentContextWithEnvironmentVariablesCreatedOutOfAdditionalDataFromModification() {
        ConfigurationProperty k1 = ConfigurationPropertyMother.create("k1", false, "v1");
        SCM scmConfig = SCMMother.create("scm-id", "tw-dev", "pluginid", "version", new Configuration(k1));
        PluggableSCMMaterial material = new PluggableSCMMaterial();
        material.setSCMConfig(scmConfig);
        material.setName(new CaseInsensitiveString("tw-dev:go-agent"));
        HashMap<String, String> map = new HashMap<>();
        map.put("MY_NEW_KEY", "my_value");
        Modification modification = new Modification("loser", "comment", "email", new Date(), "revision-123", JsonHelper.toJsonString(map));
        Modifications modifications = new Modifications(modification);
        EnvironmentVariableContext environmentVariableContext = new EnvironmentVariableContext();
        material.populateEnvironmentContext(environmentVariableContext, new com.thoughtworks.go.domain.MaterialRevision(material, modifications), null);
        Assert.assertThat(environmentVariableContext.getProperty("GO_SCM_TW_DEV_GO_AGENT_LABEL"), Matchers.is("revision-123"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_SCM_TW_DEV_GO_AGENT_K1"), Matchers.is("v1"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_SCM_TW_DEV_GO_AGENT_MY_NEW_KEY"), Matchers.is("my_value"));
    }

    @Test
    public void shouldMarkEnvironmentContextCreatedForAdditionalDataAsSecureIfTheValueContainsAnySpecialCharacters() throws UnsupportedEncodingException {
        ConfigurationProperty k1 = ConfigurationPropertyMother.create("k1", false, "v1");
        ConfigurationProperty k2 = ConfigurationPropertyMother.create("k2", true, "!secure_value:with_special_chars");
        SCM scmConfig = SCMMother.create("scm-id", "tw-dev", "pluginid", "version", new Configuration(k1, k2));
        PluggableSCMMaterial material = new PluggableSCMMaterial();
        material.setSCMConfig(scmConfig);
        material.setName(new CaseInsensitiveString("tw-dev:go-agent"));
        HashMap<String, String> map = new HashMap<>();
        map.put("ADDITIONAL_DATA_ONE", "foobar:!secure_value:with_special_chars");
        map.put("ADDITIONAL_DATA_URL_ENCODED", "something:%21secure_value%3Awith_special_chars");
        map.put("ADDITIONAL_DATA_TWO", "foobar:secure_value_with_regular_chars");
        Modification modification = new Modification("loser", "comment", "email", new Date(), "revision-123", JsonHelper.toJsonString(map));
        Modifications modifications = new Modifications(modification);
        EnvironmentVariableContext environmentVariableContext = new EnvironmentVariableContext();
        material.populateEnvironmentContext(environmentVariableContext, new com.thoughtworks.go.domain.MaterialRevision(material, modifications), null);
        Assert.assertThat(environmentVariableContext.getProperty("GO_SCM_TW_DEV_GO_AGENT_LABEL"), Matchers.is("revision-123"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_SCM_TW_DEV_GO_AGENT_K1"), Matchers.is("v1"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_SCM_TW_DEV_GO_AGENT_K2"), Matchers.is("!secure_value:with_special_chars"));
        Assert.assertThat(environmentVariableContext.getPropertyForDisplay("GO_SCM_TW_DEV_GO_AGENT_K2"), Matchers.is(MASK_VALUE));
        Assert.assertThat(environmentVariableContext.getProperty("GO_SCM_TW_DEV_GO_AGENT_ADDITIONAL_DATA_ONE"), Matchers.is("foobar:!secure_value:with_special_chars"));
        Assert.assertThat(environmentVariableContext.getPropertyForDisplay("GO_SCM_TW_DEV_GO_AGENT_ADDITIONAL_DATA_ONE"), Matchers.is("foobar:!secure_value:with_special_chars"));
        Assert.assertThat(environmentVariableContext.getPropertyForDisplay("GO_SCM_TW_DEV_GO_AGENT_ADDITIONAL_DATA_TWO"), Matchers.is("foobar:secure_value_with_regular_chars"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_SCM_TW_DEV_GO_AGENT_ADDITIONAL_DATA_URL_ENCODED"), Matchers.is("something:%21secure_value%3Awith_special_chars"));
        Assert.assertThat(environmentVariableContext.getPropertyForDisplay("GO_SCM_TW_DEV_GO_AGENT_ADDITIONAL_DATA_URL_ENCODED"), Matchers.is(MASK_VALUE));
    }

    @Test
    public void shouldNotThrowUpWhenAdditionalDataIsNull() {
        ConfigurationProperty k1 = ConfigurationPropertyMother.create("k1", false, "v1");
        SCM scmConfig = SCMMother.create("scm-id", "tw-dev", "pluginid", "version", new Configuration(k1));
        PluggableSCMMaterial material = new PluggableSCMMaterial();
        material.setSCMConfig(scmConfig);
        material.setName(new CaseInsensitiveString("tw-dev:go-agent"));
        Modifications modifications = new Modifications(new Modification("loser", "comment", "email", new Date(), "revision-123", null));
        EnvironmentVariableContext environmentVariableContext = new EnvironmentVariableContext();
        material.populateEnvironmentContext(environmentVariableContext, new com.thoughtworks.go.domain.MaterialRevision(material, modifications), null);
        Assert.assertThat(environmentVariableContext.getProperty("GO_SCM_TW_DEV_GO_AGENT_LABEL"), Matchers.is("revision-123"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_SCM_TW_DEV_GO_AGENT_K1"), Matchers.is("v1"));
    }

    @Test
    public void shouldNotThrowUpWhenAdditionalDataIsRandomJunkAndNotJSON() {
        ConfigurationProperty k1 = ConfigurationPropertyMother.create("k1", false, "v1");
        SCM scmConfig = SCMMother.create("scm-id", "tw-dev", "pluginid", "version", new Configuration(k1));
        PluggableSCMMaterial material = new PluggableSCMMaterial();
        material.setSCMConfig(scmConfig);
        material.setName(new CaseInsensitiveString("tw-dev:go-agent"));
        Modifications modifications = new Modifications(new Modification("loser", "comment", "email", new Date(), "revision-123", "salkdfjdsa-jjgkj!!!vcxknbvkjk"));
        EnvironmentVariableContext environmentVariableContext = new EnvironmentVariableContext();
        material.populateEnvironmentContext(environmentVariableContext, new com.thoughtworks.go.domain.MaterialRevision(material, modifications), null);
        Assert.assertThat(environmentVariableContext.getProperty("GO_SCM_TW_DEV_GO_AGENT_LABEL"), Matchers.is("revision-123"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_SCM_TW_DEV_GO_AGENT_K1"), Matchers.is("v1"));
    }

    @Test
    public void shouldGetUriForDisplay() {
        SCMMetadataStore.getInstance().addMetadataFor("some-plugin", new SCMConfigurations(), null);
        ConfigurationProperty k1 = ConfigurationPropertyMother.create("k1", false, "scm-v1");
        ConfigurationProperty k2 = ConfigurationPropertyMother.create("k2", false, "scm-v2");
        Configuration configuration = new Configuration(k1, k2);
        SCM scmConfig = SCMMother.create("scm-id", "scm-name", "some-plugin", "version", configuration);
        PluggableSCMMaterial material = new PluggableSCMMaterial();
        material.setSCMConfig(scmConfig);
        Assert.assertThat(material.getUriForDisplay(), Matchers.is("[k1=scm-v1, k2=scm-v2]"));
    }

    @Test
    public void shouldGetUriForDisplayNameIfNameIsNull() {
        ConfigurationProperty k1 = ConfigurationPropertyMother.create("k1", false, "scm-v1");
        ConfigurationProperty k2 = ConfigurationPropertyMother.create("k2", false, "scm-v2");
        SCM scmConfig = SCMMother.create("scm-id", null, "pluginid", "version", new Configuration(k1, k2));
        PluggableSCMMaterial material = new PluggableSCMMaterial();
        material.setSCMConfig(scmConfig);
        Assert.assertThat(material.getDisplayName(), Matchers.is(material.getUriForDisplay()));
    }

    @Test
    public void shouldGetLongDescription() {
        ConfigurationProperty k1 = ConfigurationPropertyMother.create("k1", false, "scm-v1");
        ConfigurationProperty k2 = ConfigurationPropertyMother.create("k2", false, "scm-v2");
        Configuration configuration = new Configuration(k1, k2);
        SCM scmConfig = SCMMother.create("scm-id", "scm-name", "pluginid", "version", configuration);
        PluggableSCMMaterial material = new PluggableSCMMaterial();
        material.setSCMConfig(scmConfig);
        Assert.assertThat(material.getLongDescription(), Matchers.is(material.getUriForDisplay()));
    }

    @Test
    public void shouldPassEqualsCheckIfFingerprintIsSame() {
        PluggableSCMMaterial material1 = MaterialsMother.pluggableSCMMaterial();
        material1.setName(new CaseInsensitiveString("name1"));
        PluggableSCMMaterial material2 = MaterialsMother.pluggableSCMMaterial();
        material2.setName(new CaseInsensitiveString("name2"));
        Assert.assertThat(material1.equals(material2), Matchers.is(true));
    }

    @Test
    public void shouldFailEqualsCheckIfFingerprintDiffers() {
        PluggableSCMMaterial material1 = MaterialsMother.pluggableSCMMaterial();
        material1.getScmConfig().getConfiguration().first().setConfigurationValue(new ConfigurationValue("new-url"));
        PluggableSCMMaterial material2 = MaterialsMother.pluggableSCMMaterial();
        Assert.assertThat(material1.equals(material2), Matchers.is(false));
    }

    @Test
    public void shouldReturnSomethingMoreSaneForToString() throws Exception {
        PluggableSCMMaterial material = MaterialsMother.pluggableSCMMaterial();
        SCMMetadataStore.getInstance().addMetadataFor(material.getPluginId(), new SCMConfigurations(), null);
        Assert.assertThat(material.toString(), Matchers.is("'PluggableSCMMaterial{[k1=v1, k2=v2]}'"));
    }

    @Test
    public void shouldReturnNameAsNullIfSCMConfigIsNotSet() {
        Assert.assertThat(new PluggableSCMMaterial().getName(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldNotCalculateFingerprintWhenAvailable() {
        String fingerprint = "fingerprint";
        SCM scmConfig = Mockito.mock(SCM.class);
        PluggableSCMMaterial pluggableSCMMaterial = new PluggableSCMMaterial();
        pluggableSCMMaterial.setSCMConfig(scmConfig);
        pluggableSCMMaterial.setFingerprint(fingerprint);
        Assert.assertThat(pluggableSCMMaterial.getFingerprint(), Matchers.is(fingerprint));
        Mockito.verify(scmConfig, Mockito.never()).getFingerprint();
    }

    @Test
    public void shouldTakeValueOfIsAutoUpdateFromSCMConfig() throws Exception {
        PluggableSCMMaterial material = MaterialsMother.pluggableSCMMaterial();
        material.getScmConfig().setAutoUpdate(true);
        Assert.assertThat(material.isAutoUpdate(), Matchers.is(true));
        material.getScmConfig().setAutoUpdate(false);
        Assert.assertThat(material.isAutoUpdate(), Matchers.is(false));
    }

    @Test
    public void shouldReturnWorkingDirectoryCorrectly() {
        PluggableSCMMaterial material = new PluggableSCMMaterial();
        material.setFolder("dest");
        String baseFolder = new File(System.getProperty("java.io.tmpdir")).getAbsolutePath();
        String workingFolder = new File(baseFolder, "dest").getAbsolutePath();
        Assert.assertThat(material.workingDirectory(new File(baseFolder)).getAbsolutePath(), Matchers.is(workingFolder));
        material.setFolder(null);
        Assert.assertThat(material.workingDirectory(new File(baseFolder)).getAbsolutePath(), Matchers.is(baseFolder));
    }

    @Test
    public void shouldGetAttributesWithSecureFields() {
        PluggableSCMMaterial material = createPluggableSCMMaterialWithSecureConfiguration();
        Map<String, Object> attributes = material.getAttributes(true);
        Assert.assertThat(attributes.get("type"), Matchers.is("scm"));
        Assert.assertThat(attributes.get("plugin-id"), Matchers.is("pluginid"));
        Map<String, Object> configuration = ((Map<String, Object>) (attributes.get("scm-configuration")));
        Assert.assertThat(configuration.get("k1"), Matchers.is("v1"));
        Assert.assertThat(configuration.get("k2"), Matchers.is("v2"));
    }

    @Test
    public void shouldGetAttributesWithoutSecureFields() {
        PluggableSCMMaterial material = createPluggableSCMMaterialWithSecureConfiguration();
        Map<String, Object> attributes = material.getAttributes(false);
        Assert.assertThat(attributes.get("type"), Matchers.is("scm"));
        Assert.assertThat(attributes.get("plugin-id"), Matchers.is("pluginid"));
        Map<String, Object> configuration = ((Map<String, Object>) (attributes.get("scm-configuration")));
        Assert.assertThat(configuration.get("k1"), Matchers.is("v1"));
        Assert.assertThat(configuration.get("k2"), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldCorrectlyGetTypeDisplay() {
        PluggableSCMMaterial pluggableSCMMaterial = new PluggableSCMMaterial("scm-id");
        Assert.assertThat(pluggableSCMMaterial.getTypeForDisplay(), Matchers.is("SCM"));
        pluggableSCMMaterial.setSCMConfig(SCMMother.create("scm-id"));
        Assert.assertThat(pluggableSCMMaterial.getTypeForDisplay(), Matchers.is("SCM"));
        SCMMetadataStore.getInstance().addMetadataFor("plugin", null, null);
        Assert.assertThat(pluggableSCMMaterial.getTypeForDisplay(), Matchers.is("SCM"));
        SCMView scmView = Mockito.mock(SCMView.class);
        Mockito.when(scmView.displayValue()).thenReturn("scm-name");
        SCMMetadataStore.getInstance().addMetadataFor("plugin", null, scmView);
        Assert.assertThat(pluggableSCMMaterial.getTypeForDisplay(), Matchers.is("scm-name"));
    }

    @Test
    public void shouldReturnTrueForPluggableScmMaterial_supportsDestinationFolder() throws Exception {
        PluggableSCMMaterial material = new PluggableSCMMaterial();
        Assert.assertThat(material.supportsDestinationFolder(), Matchers.is(true));
    }

    @Test
    public void shouldUpdateMaterialFromMaterialConfig() {
        PluggableSCMMaterial material = MaterialsMother.pluggableSCMMaterial();
        PluggableSCMMaterialConfig materialConfig = MaterialConfigsMother.pluggableSCMMaterialConfig();
        Configuration configuration = new Configuration(new ConfigurationProperty(new ConfigurationKey("new_key"), new ConfigurationValue("new_value")));
        materialConfig.getSCMConfig().setConfiguration(configuration);
        material.updateFromConfig(materialConfig);
        Assert.assertThat(material.getScmConfig().getConfiguration().equals(materialConfig.getSCMConfig().getConfiguration()), Matchers.is(true));
    }
}

