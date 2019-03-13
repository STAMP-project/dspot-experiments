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
package com.thoughtworks.go.domain.config;


import ConfigurationProperty.CONFIGURATION_KEY;
import com.thoughtworks.go.domain.ConfigErrors;
import com.thoughtworks.go.domain.packagerepository.ConfigurationPropertyMother;
import com.thoughtworks.go.security.CryptoException;
import com.thoughtworks.go.security.GoCipher;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ConfigurationTest {
    @Test
    public void shouldCheckForEqualityForConfiguration() {
        ConfigurationProperty configurationProperty = new ConfigurationProperty();
        Configuration configuration = new Configuration(configurationProperty);
        Assert.assertThat(configuration, Matchers.is(new Configuration(configurationProperty)));
    }

    @Test
    public void shouldGetConfigForDisplay() {
        ConfigurationProperty property1 = new ConfigurationProperty(new ConfigurationKey("key1"), new ConfigurationValue("value1"), null, null);
        ConfigurationProperty property2 = new ConfigurationProperty(new ConfigurationKey("key2"), new ConfigurationValue("value2"), null, null);
        Configuration config = new Configuration(property1, property2);
        Assert.assertThat(config.forDisplay(Arrays.asList(property1)), Matchers.is("[key1=value1]"));
        Assert.assertThat(config.forDisplay(Arrays.asList(property1, property2)), Matchers.is("[key1=value1, key2=value2]"));
    }

    @Test
    public void shouldNotGetValuesOfSecureKeysInConfigForDisplay() {
        ConfigurationProperty property1 = new ConfigurationProperty(new ConfigurationKey("key1"), new ConfigurationValue("value1"), null, null);
        ConfigurationProperty property2 = new ConfigurationProperty(new ConfigurationKey("key2"), new ConfigurationValue("value2"), null, null);
        ConfigurationProperty property3 = new ConfigurationProperty(new ConfigurationKey("secure"), null, new EncryptedConfigurationValue("secured-value"), null);
        Configuration config = new Configuration(property1, property2, property3);
        Assert.assertThat(config.forDisplay(Arrays.asList(property1, property2, property3)), Matchers.is("[key1=value1, key2=value2]"));
    }

    @Test
    public void shouldGetConfigurationKeysAsList() {
        ConfigurationProperty property1 = new ConfigurationProperty(new ConfigurationKey("key1"), new ConfigurationValue("value1"), null, null);
        ConfigurationProperty property2 = new ConfigurationProperty(new ConfigurationKey("key2"), new ConfigurationValue("value2"), null, null);
        Configuration config = new Configuration(property1, property2);
        Assert.assertThat(config.listOfConfigKeys(), Matchers.is(Arrays.asList("key1", "key2")));
    }

    @Test
    public void shouldGetConfigPropertyForGivenKey() {
        ConfigurationProperty property1 = new ConfigurationProperty(new ConfigurationKey("key1"), new ConfigurationValue("value1"), null, null);
        ConfigurationProperty property2 = new ConfigurationProperty(new ConfigurationKey("key2"), new ConfigurationValue("value2"), null, null);
        Configuration config = new Configuration(property1, property2);
        Assert.assertThat(config.getProperty("key2"), Matchers.is(property2));
    }

    @Test
    public void shouldGetNullIfPropertyNotFoundForGivenKey() {
        Configuration config = new Configuration();
        Assert.assertThat(config.getProperty("key2"), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldClearConfigurationsWhichAreEmptyAndNoErrors() throws Exception {
        Configuration configuration = new Configuration();
        configuration.add(new ConfigurationProperty(new ConfigurationKey("name-one"), new ConfigurationValue()));
        configuration.add(new ConfigurationProperty(new ConfigurationKey("name-two"), new EncryptedConfigurationValue()));
        configuration.add(new ConfigurationProperty(new ConfigurationKey("name-three"), null, new EncryptedConfigurationValue(), null));
        ConfigurationProperty configurationProperty = new ConfigurationProperty(new ConfigurationKey("name-four"), null, new EncryptedConfigurationValue(), null);
        configurationProperty.addErrorAgainstConfigurationValue("error");
        configuration.add(configurationProperty);
        configuration.clearEmptyConfigurations();
        Assert.assertThat(configuration.size(), Matchers.is(1));
        Assert.assertThat(configuration.get(0).getConfigurationKey().getName(), Matchers.is("name-four"));
    }

    @Test
    public void shouldValidateUniqueKeysAreAddedToConfiguration() {
        ConfigurationProperty one = new ConfigurationProperty(new ConfigurationKey("one"), new ConfigurationValue("value1"));
        ConfigurationProperty duplicate1 = new ConfigurationProperty(new ConfigurationKey("ONE"), new ConfigurationValue("value2"));
        ConfigurationProperty duplicate2 = new ConfigurationProperty(new ConfigurationKey("ONE"), new ConfigurationValue("value3"));
        ConfigurationProperty two = new ConfigurationProperty(new ConfigurationKey("two"), new ConfigurationValue());
        Configuration configuration = new Configuration(one, duplicate1, duplicate2, two);
        configuration.validateUniqueness("Entity");
        Assert.assertThat(one.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(one.errors().getAllOn(CONFIGURATION_KEY).contains("Duplicate key 'ONE' found for Entity"), Matchers.is(true));
        Assert.assertThat(duplicate1.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(one.errors().getAllOn(CONFIGURATION_KEY).contains("Duplicate key 'ONE' found for Entity"), Matchers.is(true));
        Assert.assertThat(duplicate2.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(one.errors().getAllOn(CONFIGURATION_KEY).contains("Duplicate key 'ONE' found for Entity"), Matchers.is(true));
        Assert.assertThat(two.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void validateTreeShouldValidateAllConfigurationProperties() {
        ConfigurationProperty outputDirectory = Mockito.mock(ConfigurationProperty.class);
        ConfigurationProperty inputDirectory = Mockito.mock(ConfigurationProperty.class);
        Configuration configuration = new Configuration(outputDirectory, inputDirectory);
        configuration.validateTree();
        Mockito.verify(outputDirectory).validate(null);
        Mockito.verify(inputDirectory).validate(null);
    }

    @Test
    public void hasErrorsShouldVerifyIfAnyConfigurationPropertyHasErrors() {
        ConfigurationProperty outputDirectory = Mockito.mock(ConfigurationProperty.class);
        ConfigurationProperty inputDirectory = Mockito.mock(ConfigurationProperty.class);
        Mockito.when(outputDirectory.hasErrors()).thenReturn(false);
        Mockito.when(inputDirectory.hasErrors()).thenReturn(true);
        Configuration configuration = new Configuration(outputDirectory, inputDirectory);
        Assert.assertTrue(configuration.hasErrors());
        Mockito.verify(outputDirectory).hasErrors();
        Mockito.verify(inputDirectory).hasErrors();
    }

    @Test
    public void shouldReturnConfigWithErrorsAsMap() {
        ConfigurationProperty configurationProperty = ConfigurationPropertyMother.create("key", false, "value");
        configurationProperty.addError("key", "invalid key");
        Configuration configuration = new Configuration(configurationProperty);
        Map<String, Map<String, String>> configWithErrorsAsMap = configuration.getConfigWithErrorsAsMap();
        HashMap<Object, Object> expectedMap = new HashMap<>();
        HashMap<Object, Object> errorsMap = new HashMap<>();
        errorsMap.put("value", "value");
        ConfigErrors configErrors = new ConfigErrors();
        configErrors.add("key", "invalid key");
        errorsMap.put("errors", configErrors.getAll().toString());
        expectedMap.put("key", errorsMap);
        Assert.assertThat(configWithErrorsAsMap, Matchers.is(expectedMap));
    }

    @Test
    public void shouldReturnConfigWithMatadataAsMap() throws CryptoException {
        ConfigurationProperty configurationProperty1 = ConfigurationPropertyMother.create("key", false, "value");
        String password = new GoCipher().encrypt("password");
        ConfigurationProperty configurationProperty2 = ConfigurationPropertyMother.create("secure");
        configurationProperty2.setEncryptedValue(new EncryptedConfigurationValue(password));
        Configuration configuration = new Configuration(configurationProperty1, configurationProperty2);
        Map<String, Map<String, Object>> metadataAndValuesAsMap = configuration.getPropertyMetadataAndValuesAsMap();
        HashMap<Object, Object> expectedMap = new HashMap<>();
        HashMap<Object, Object> metadataMap1 = new HashMap<>();
        metadataMap1.put("value", "value");
        metadataMap1.put("displayValue", "value");
        metadataMap1.put("isSecure", false);
        expectedMap.put("key", metadataMap1);
        HashMap<Object, Object> metadataMap2 = new HashMap<>();
        metadataMap2.put("value", password);
        metadataMap2.put("displayValue", "****");
        metadataMap2.put("isSecure", true);
        expectedMap.put("secure", metadataMap2);
        Assert.assertThat(metadataAndValuesAsMap, Matchers.is(expectedMap));
    }
}

