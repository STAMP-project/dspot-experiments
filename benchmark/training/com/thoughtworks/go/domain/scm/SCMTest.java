/**
 * Copyright 2015 ThoughtWorks, Inc.
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
 */
package com.thoughtworks.go.domain.scm;


import ConfigurationProperty.CONFIGURATION_KEY;
import SCM.AUTO_UPDATE;
import SCM.NAME;
import SCM.SCM_ID;
import SCM.VALUE_KEY;
import com.thoughtworks.go.config.BasicCruiseConfig;
import com.thoughtworks.go.config.ConfigSaveValidationContext;
import com.thoughtworks.go.domain.config.Configuration;
import com.thoughtworks.go.domain.config.ConfigurationKey;
import com.thoughtworks.go.domain.config.ConfigurationProperty;
import com.thoughtworks.go.domain.config.ConfigurationValue;
import com.thoughtworks.go.domain.config.EncryptedConfigurationValue;
import com.thoughtworks.go.domain.config.PluginConfiguration;
import com.thoughtworks.go.domain.packagerepository.ConfigurationPropertyMother;
import com.thoughtworks.go.plugin.access.scm.SCMConfiguration;
import com.thoughtworks.go.plugin.access.scm.SCMConfigurations;
import com.thoughtworks.go.plugin.access.scm.SCMMetadataStore;
import com.thoughtworks.go.plugin.access.scm.SCMPreference;
import com.thoughtworks.go.security.GoCipher;
import com.thoughtworks.go.util.DataStructureUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class SCMTest {
    @Test
    public void shouldCheckEqualityOfSCM() {
        Configuration configuration = new Configuration();
        SCM scm = SCMMother.create("id", "name", "plugin-id", "version", configuration);
        Assert.assertThat(scm, Matchers.is(SCMMother.create("id", "name", "plugin-id", "version", configuration)));
    }

    @Test
    public void shouldCheckForFieldAssignments() {
        Configuration configuration = new Configuration();
        SCM scm = SCMMother.create("id", "name", "plugin-id", "version", configuration);
        Assert.assertThat(scm.getId(), Matchers.is("id"));
        Assert.assertThat(scm.getName(), Matchers.is("name"));
        Assert.assertThat(scm.getPluginConfiguration().getId(), Matchers.is("plugin-id"));
        Assert.assertThat(scm.getPluginConfiguration().getVersion(), Matchers.is("version"));
        Assert.assertThat(scm.getConfiguration().listOfConfigKeys().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldOnlyDisplayFieldsWhichAreNonSecureAndPartOfIdentityInGetConfigForDisplayWhenPluginExists() throws Exception {
        SCMConfigurations scmConfiguration = new SCMConfigurations();
        scmConfiguration.add(new SCMConfiguration("key1").with(PART_OF_IDENTITY, true).with(SECURE, false));
        scmConfiguration.add(new SCMConfiguration("key2").with(PART_OF_IDENTITY, false).with(SECURE, false));
        scmConfiguration.add(new SCMConfiguration("key3").with(PART_OF_IDENTITY, true).with(SECURE, true));
        scmConfiguration.add(new SCMConfiguration("key4").with(PART_OF_IDENTITY, false).with(SECURE, true));
        scmConfiguration.add(new SCMConfiguration("key5").with(PART_OF_IDENTITY, true).with(SECURE, false));
        SCMMetadataStore.getInstance().addMetadataFor("plugin-id", scmConfiguration, null);
        Configuration configuration = new Configuration(ConfigurationPropertyMother.create("key1", false, "value1"), ConfigurationPropertyMother.create("key2", false, "value2"), ConfigurationPropertyMother.create("key3", true, "value3"), ConfigurationPropertyMother.create("key4", true, "value4"), ConfigurationPropertyMother.create("key5", false, "value5"));
        SCM scm = SCMMother.create("scm", "scm-name", "plugin-id", "1.0", configuration);
        Assert.assertThat(scm.getConfigForDisplay(), Matchers.is("[key1=value1, key5=value5]"));
    }

    @Test
    public void shouldConvertKeysToLowercaseInGetConfigForDisplay() throws Exception {
        SCMMetadataStore.getInstance().addMetadataFor("plugin-id", new SCMConfigurations(), null);
        Configuration configuration = new Configuration(ConfigurationPropertyMother.create("kEY1", false, "vALue1"), ConfigurationPropertyMother.create("KEY_MORE_2", false, "VALUE_2"), ConfigurationPropertyMother.create("key_3", false, "value3"));
        SCM scm = SCMMother.create("scm", "scm-name", "plugin-id", "1.0", configuration);
        Assert.assertThat(scm.getConfigForDisplay(), Matchers.is("[key1=vALue1, key_more_2=VALUE_2, key_3=value3]"));
    }

    @Test
    public void shouldNotDisplayEmptyValuesInGetConfigForDisplay() throws Exception {
        SCMMetadataStore.getInstance().addMetadataFor("plugin-id", new SCMConfigurations(), null);
        Configuration configuration = new Configuration(ConfigurationPropertyMother.create("rk1", false, ""), ConfigurationPropertyMother.create("rk2", false, "some-non-empty-value"), ConfigurationPropertyMother.create("rk3", false, null));
        SCM scm = SCMMother.create("scm", "scm-name", "plugin-id", "1.0", configuration);
        Assert.assertThat(scm.getConfigForDisplay(), Matchers.is("[rk2=some-non-empty-value]"));
    }

    @Test
    public void shouldDisplayAllNonSecureFieldsInGetConfigForDisplayWhenPluginDoesNotExist() {
        Configuration configuration = new Configuration(ConfigurationPropertyMother.create("key1", false, "value1"), ConfigurationPropertyMother.create("key2", true, "value2"), ConfigurationPropertyMother.create("key3", false, "value3"));
        SCM scm = SCMMother.create("scm", "scm-name", "some-plugin-which-does-not-exist", "1.0", configuration);
        Assert.assertThat(scm.getConfigForDisplay(), Matchers.is("WARNING! Plugin missing. [key1=value1, key3=value3]"));
    }

    @Test
    public void shouldMakeConfigurationSecureBasedOnMetadata() throws Exception {
        GoCipher goCipher = new GoCipher();
        // meta data of SCM
        SCMConfigurations scmConfiguration = new SCMConfigurations();
        scmConfiguration.add(new SCMConfiguration("key1").with(SECURE, true));
        scmConfiguration.add(new SCMConfiguration("key2").with(SECURE, false));
        SCMMetadataStore.getInstance().addMetadataFor("plugin-id", scmConfiguration, null);
        /* secure property is set based on metadata */
        ConfigurationProperty secureProperty = new ConfigurationProperty(new ConfigurationKey("key1"), new ConfigurationValue("value1"), null, goCipher);
        ConfigurationProperty nonSecureProperty = new ConfigurationProperty(new ConfigurationKey("key2"), new ConfigurationValue("value2"), null, goCipher);
        SCM scm = SCMMother.create("scm-id", "scm-name", "plugin-id", "1.0", new Configuration(secureProperty, nonSecureProperty));
        scm.applyPluginMetadata();
        // assert SCM properties
        Assert.assertThat(secureProperty.isSecure(), Matchers.is(true));
        Assert.assertThat(secureProperty.getEncryptedConfigurationValue(), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(secureProperty.getEncryptedValue(), Matchers.is(goCipher.encrypt("value1")));
        Assert.assertThat(nonSecureProperty.isSecure(), Matchers.is(false));
        Assert.assertThat(nonSecureProperty.getValue(), Matchers.is("value2"));
    }

    @Test
    public void shouldNotUpdateSecurePropertyWhenPluginIsMissing() {
        GoCipher goCipher = new GoCipher();
        ConfigurationProperty secureProperty = new ConfigurationProperty(new ConfigurationKey("key1"), null, new EncryptedConfigurationValue("value"), goCipher);
        ConfigurationProperty nonSecureProperty = new ConfigurationProperty(new ConfigurationKey("key2"), new ConfigurationValue("value1"), null, goCipher);
        SCM scm = SCMMother.create("scm-id", "scm-name", "plugin-id", "version", new Configuration(secureProperty, nonSecureProperty));
        scm.applyPluginMetadata();
        Assert.assertThat(secureProperty.getEncryptedConfigurationValue(), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(secureProperty.getConfigurationValue(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(nonSecureProperty.getConfigurationValue(), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(nonSecureProperty.getEncryptedConfigurationValue(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldThrowUpOnSetConfigAttributesIfPluginIsNotAvailable() throws Exception {
        try {
            Map<String, String> attributeMap = DataStructureUtils.m(SCM_ID, "scm-id", NAME, "scm-name", AUTO_UPDATE, "false", "url", "http://localhost");
            SCM scm = new SCM(null, new PluginConfiguration("plugin-id", "1"), new Configuration());
            scm.setConfigAttributes(attributeMap);
            Assert.fail("should have thrown exception");
        } catch (Exception e) {
            Assert.assertThat(e, Matchers.instanceOf(RuntimeException.class));
            Assert.assertThat(e.getMessage(), Matchers.is("metadata unavailable for plugin: plugin-id"));
        }
    }

    @Test
    public void shouldSetConfigAttributesAsAvailable() throws Exception {
        SCMConfigurations scmConfigurations = new SCMConfigurations();
        scmConfigurations.add(new SCMConfiguration("url"));
        scmConfigurations.add(new SCMConfiguration("username"));
        scmConfigurations.add(new SCMConfiguration("password").with(SECURE, true));
        SCMMetadataStore.getInstance().addMetadataFor("plugin-id", scmConfigurations, null);
        Map<String, String> attributeMap = DataStructureUtils.m(SCM_ID, "scm-id", NAME, "scm-name", AUTO_UPDATE, "false", "url", "http://localhost", "username", "user", "password", "pass");
        SCM scm = new SCM(null, new PluginConfiguration("plugin-id", "1"), new Configuration());
        scm.setConfigAttributes(attributeMap);
        Assert.assertThat(scm.getId(), Matchers.is("scm-id"));
        Assert.assertThat(scm.getName(), Matchers.is("scm-name"));
        Assert.assertThat(scm.isAutoUpdate(), Matchers.is(false));
        Assert.assertThat(scm.getPluginConfiguration().getId(), Matchers.is("plugin-id"));
        Assert.assertThat(scm.getConfigAsMap().get("url").get(VALUE_KEY), Matchers.is("http://localhost"));
        Assert.assertThat(scm.getConfigAsMap().get("username").get(VALUE_KEY), Matchers.is("user"));
        Assert.assertThat(scm.getConfigAsMap().get("password").get(VALUE_KEY), Matchers.is("pass"));
        Assert.assertThat(scm.getConfiguration().getProperty("password").getConfigurationValue(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(scm.getConfiguration().getProperty("password").getEncryptedConfigurationValue(), Matchers.is(Matchers.not(Matchers.nullValue())));
    }

    @Test
    public void shouldPopulateItselfFromConfigAttributesMap() throws Exception {
        SCMConfigurations scmConfigurations = new SCMConfigurations();
        scmConfigurations.add(new SCMConfiguration("KEY1"));
        scmConfigurations.add(new SCMConfiguration("Key2"));
        SCMPreference scmPreference = Mockito.mock(SCMPreference.class);
        Mockito.when(scmPreference.getScmConfigurations()).thenReturn(scmConfigurations);
        SCMMetadataStore.getInstance().setPreferenceFor("plugin-id", scmPreference);
        Configuration configuration = new Configuration(ConfigurationPropertyMother.create("KEY1"), ConfigurationPropertyMother.create("Key2"));
        SCM scm = new SCM("scm-id", new PluginConfiguration("plugin-id", "1"), configuration);
        Map<String, String> attributeMap = DataStructureUtils.m("KEY1", "value1", "Key2", "value2");
        scm.setConfigAttributes(attributeMap);
        Assert.assertThat(scm.getConfigAsMap().get("KEY1").get(VALUE_KEY), Matchers.is("value1"));
        Assert.assertThat(scm.getConfigAsMap().get("Key2").get(VALUE_KEY), Matchers.is("value2"));
    }

    @Test
    public void shouldNotOverwriteValuesIfTheyAreNotAvailableInConfigAttributesMap() throws Exception {
        SCMConfigurations scmConfigurations = new SCMConfigurations();
        scmConfigurations.add(new SCMConfiguration("KEY1"));
        scmConfigurations.add(new SCMConfiguration("Key2"));
        SCMPreference scmPreference = Mockito.mock(SCMPreference.class);
        Mockito.when(scmPreference.getScmConfigurations()).thenReturn(scmConfigurations);
        SCMMetadataStore.getInstance().setPreferenceFor("plugin-id", scmPreference);
        Configuration configuration = new Configuration(ConfigurationPropertyMother.create("KEY1"), ConfigurationPropertyMother.create("Key2"));
        SCM scm = new SCM("scm-id", new PluginConfiguration("plugin-id", "1"), configuration);
        Map<String, String> attributeMap = DataStructureUtils.m("KEY1", "value1");
        scm.setConfigAttributes(attributeMap);
        Assert.assertThat(scm.getConfigAsMap().get("KEY1").get(VALUE_KEY), Matchers.is("value1"));
        Assert.assertThat(scm.getConfigAsMap().get("Key2").get(VALUE_KEY), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldIgnoreKeysPresentInConfigAttributesMapButNotPresentInConfigStore() throws Exception {
        SCMConfigurations scmConfigurations = new SCMConfigurations();
        scmConfigurations.add(new SCMConfiguration("KEY1"));
        SCMPreference scmPreference = Mockito.mock(SCMPreference.class);
        Mockito.when(scmPreference.getScmConfigurations()).thenReturn(scmConfigurations);
        SCMMetadataStore.getInstance().setPreferenceFor("plugin-id", scmPreference);
        Configuration configuration = new Configuration(ConfigurationPropertyMother.create("KEY1"));
        SCM scm = new SCM("scm-id", new PluginConfiguration("plugin-id", "1"), configuration);
        Map<String, String> attributeMap = DataStructureUtils.m("KEY1", "value1", "Key2", "value2");
        scm.setConfigAttributes(attributeMap);
        Assert.assertThat(scm.getConfigAsMap().get("KEY1").get(VALUE_KEY), Matchers.is("value1"));
        Assert.assertFalse(scm.getConfigAsMap().containsKey("Key2"));
    }

    @Test
    public void shouldAddPropertyComingFromAttributesMapIfPresentInConfigStoreEvenIfItISNotPresentInCurrentConfiguration() throws Exception {
        SCMConfigurations scmConfigurations = new SCMConfigurations();
        scmConfigurations.add(new SCMConfiguration("KEY1"));
        scmConfigurations.add(new SCMConfiguration("Key2"));
        SCMPreference scmPreference = Mockito.mock(SCMPreference.class);
        Mockito.when(scmPreference.getScmConfigurations()).thenReturn(scmConfigurations);
        SCMMetadataStore.getInstance().setPreferenceFor("plugin-id", scmPreference);
        Configuration configuration = new Configuration(ConfigurationPropertyMother.create("KEY1"));
        SCM scm = new SCM("scm-id", new PluginConfiguration("plugin-id", "1"), configuration);
        Map<String, String> attributeMap = DataStructureUtils.m("KEY1", "value1", "Key2", "value2");
        scm.setConfigAttributes(attributeMap);
        Assert.assertThat(scm.getConfigAsMap().get("KEY1").get(VALUE_KEY), Matchers.is("value1"));
        Assert.assertThat(scm.getConfigAsMap().get("Key2").get(VALUE_KEY), Matchers.is("value2"));
    }

    @Test
    public void shouldValidateIfNameIsMissing() {
        SCM scm = new SCM();
        scm.validate(new ConfigSaveValidationContext(new BasicCruiseConfig(), null));
        Assert.assertThat(scm.errors().getAllOn(NAME), Matchers.is(Arrays.asList("Please provide name")));
    }

    @Test
    public void shouldClearConfigurationsWhichAreEmptyAndNoErrors() throws Exception {
        SCM scm = new SCM();
        scm.getConfiguration().add(new ConfigurationProperty(new ConfigurationKey("name-one"), new ConfigurationValue()));
        scm.getConfiguration().add(new ConfigurationProperty(new ConfigurationKey("name-two"), new EncryptedConfigurationValue()));
        scm.getConfiguration().add(new ConfigurationProperty(new ConfigurationKey("name-three"), null, new EncryptedConfigurationValue(), null));
        ConfigurationProperty configurationProperty = new ConfigurationProperty(new ConfigurationKey("name-four"), null, new EncryptedConfigurationValue(), null);
        configurationProperty.addErrorAgainstConfigurationValue("error");
        scm.getConfiguration().add(configurationProperty);
        scm.clearEmptyConfigurations();
        Assert.assertThat(scm.getConfiguration().size(), Matchers.is(1));
        Assert.assertThat(scm.getConfiguration().get(0).getConfigurationKey().getName(), Matchers.is("name-four"));
    }

    @Test
    public void shouldValidateName() throws Exception {
        SCM scm = new SCM();
        scm.setName("some name");
        scm.validate(new ConfigSaveValidationContext(null));
        Assert.assertThat(scm.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(scm.errors().getAllOn(NAME).get(0), Matchers.is("Invalid SCM name 'some name'. This must be alphanumeric and can contain underscores and periods (however, it cannot start with a period). The maximum allowed length is 255 characters."));
    }

    @Test
    public void shouldValidateUniqueKeysInConfiguration() {
        ConfigurationProperty one = ConfigurationPropertyMother.create("one", false, "value1");
        ConfigurationProperty duplicate1 = ConfigurationPropertyMother.create("ONE", false, "value2");
        ConfigurationProperty duplicate2 = ConfigurationPropertyMother.create("ONE", false, "value3");
        ConfigurationProperty two = ConfigurationPropertyMother.create("two", false, null);
        SCM scm = new SCM();
        scm.setConfiguration(new Configuration(one, duplicate1, duplicate2, two));
        scm.setName("git");
        scm.validate(null);
        Assert.assertThat(one.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(one.errors().getAllOn(CONFIGURATION_KEY).contains("Duplicate key 'ONE' found for SCM 'git'"), Matchers.is(true));
        Assert.assertThat(duplicate1.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(one.errors().getAllOn(CONFIGURATION_KEY).contains("Duplicate key 'ONE' found for SCM 'git'"), Matchers.is(true));
        Assert.assertThat(duplicate2.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(one.errors().getAllOn(CONFIGURATION_KEY).contains("Duplicate key 'ONE' found for SCM 'git'"), Matchers.is(true));
        Assert.assertThat(two.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldGetConfigAsMap() throws Exception {
        PluginConfiguration pluginConfiguration = new PluginConfiguration("test-plugin-id", "13.4");
        GoCipher cipher = new GoCipher();
        List<String> keys = Arrays.asList("Avengers 1", "Avengers 2", "Avengers 3", "Avengers 4");
        List<String> values = Arrays.asList("Iron man", "Hulk", "Thor", "Captain America");
        Configuration configuration = new Configuration(new ConfigurationProperty(new ConfigurationKey(keys.get(0)), new ConfigurationValue(values.get(0))), new ConfigurationProperty(new ConfigurationKey(keys.get(1)), new ConfigurationValue(values.get(1))), new ConfigurationProperty(new ConfigurationKey(keys.get(2)), new ConfigurationValue(values.get(2))), new ConfigurationProperty(new ConfigurationKey(keys.get(3)), new ConfigurationValue(values.get(3)), new EncryptedConfigurationValue(cipher.encrypt(values.get(3))), cipher));
        SCM scm = new SCM("scm-id", pluginConfiguration, configuration);
        Map<String, Map<String, String>> configMap = scm.getConfigAsMap();
        Assert.assertThat(configMap.keySet().size(), Matchers.is(keys.size()));
        Assert.assertThat(configMap.values().size(), Matchers.is(values.size()));
        Assert.assertThat(configMap.keySet().containsAll(keys), Matchers.is(true));
        for (int i = 0; i < (keys.size()); i++) {
            Assert.assertThat(configMap.get(keys.get(i)).get(VALUE_KEY), Matchers.is(values.get(i)));
        }
    }

    @Test
    public void shouldGenerateIdIfNotAssigned() {
        SCM scm = new SCM();
        scm.ensureIdExists();
        Assert.assertThat(scm.getId(), Matchers.is(Matchers.notNullValue()));
        scm = new SCM();
        scm.setId("id");
        scm.ensureIdExists();
        Assert.assertThat(scm.getId(), Matchers.is("id"));
    }

    @Test
    public void shouldAddConfigurationPropertiesForAnyPlugin() {
        List<ConfigurationProperty> configurationProperties = Arrays.asList(ConfigurationPropertyMother.create("key", "value", "encValue"));
        Configuration configuration = new Configuration();
        SCM scm = SCMMother.create("id", "name", "does_not_exist", "1.1", configuration);
        Assert.assertThat(configuration.size(), Matchers.is(0));
        scm.addConfigurations(configurationProperties);
        Assert.assertThat(configuration.size(), Matchers.is(1));
    }

    @Test
    public void shouldGetSCMTypeCorrectly() {
        SCM scm = SCMMother.create("scm-id");
        Assert.assertThat(scm.getSCMType(), Matchers.is("pluggable_material_plugin"));
        scm.setPluginConfiguration(new PluginConfiguration("plugin-id-2", "1"));
        Assert.assertThat(scm.getSCMType(), Matchers.is("pluggable_material_plugin_id_2"));
    }
}

