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
package com.thoughtworks.go.domain.config;


import Configuration.METADATA;
import ConfigurationProperty.CONFIGURATION_KEY;
import ConfigurationProperty.CONFIGURATION_VALUE;
import ConfigurationProperty.ENCRYPTED_VALUE;
import ConfigurationProperty.IS_CHANGED;
import com.thoughtworks.go.config.ConfigSaveValidationContext;
import com.thoughtworks.go.plugin.access.packagematerial.PackageConfiguration;
import com.thoughtworks.go.plugin.access.packagematerial.PackageConfigurations;
import com.thoughtworks.go.security.CryptoException;
import com.thoughtworks.go.security.GoCipher;
import java.lang.reflect.Method;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.util.ReflectionUtils;


public class ConfigurationPropertyTest {
    private GoCipher cipher;

    @Test
    public void shouldCheckForConfigurationPropertyEquality() {
        ConfigurationValue configurationValue = new ConfigurationValue();
        ConfigurationKey configurationKey = new ConfigurationKey();
        ConfigurationProperty configurationProperty = new ConfigurationProperty(configurationKey, configurationValue, null, null);
        Assert.assertThat(configurationProperty, Matchers.is(new ConfigurationProperty(configurationKey, configurationValue, null, null)));
    }

    @Test
    public void shouldGetPropertyForFingerprint() {
        ConfigurationValue configurationValue = new ConfigurationValue("value");
        ConfigurationKey configurationKey = new ConfigurationKey("key");
        ConfigurationProperty configurationProperty = new ConfigurationProperty(configurationKey, configurationValue, null, null);
        Assert.assertThat(configurationProperty.forFingerprint(), Matchers.is("key=value"));
    }

    @Test
    public void shouldGetKeyValuePairForFingerPrintString() {
        ConfigurationValue configurationValue = new ConfigurationValue("value");
        ConfigurationKey configurationKey = new ConfigurationKey("key");
        ConfigurationProperty configurationProperty = new ConfigurationProperty(configurationKey, configurationValue, null, null);
        Assert.assertThat(configurationProperty.forFingerprint(), Matchers.is("key=value"));
    }

    @Test
    public void shouldGetEncryptValueWhenConstructedAsSecure() throws CryptoException {
        GoCipher goCipher = Mockito.mock(GoCipher.class);
        String encryptedText = "encryptedValue";
        Mockito.when(goCipher.encrypt("secureValue")).thenReturn(encryptedText);
        ConfigurationProperty property = new ConfigurationProperty(new ConfigurationKey("secureKey"), new ConfigurationValue("secureValue"), new EncryptedConfigurationValue("old-encrypted-text"), goCipher);
        property.handleSecureValueConfiguration(true);
        Assert.assertThat(property.isSecure(), Matchers.is(true));
        Assert.assertThat(property.getEncryptedValue(), Matchers.is(encryptedText));
        Assert.assertThat(property.getConfigurationKey().getName(), Matchers.is("secureKey"));
        Assert.assertThat(property.getConfigurationValue(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldNotEncryptWhenWhenConstructedAsNotSecure() {
        GoCipher goCipher = Mockito.mock(GoCipher.class);
        ConfigurationProperty property = new ConfigurationProperty(new ConfigurationKey("secureKey"), new ConfigurationValue("secureValue"), null, goCipher);
        property.handleSecureValueConfiguration(false);
        Assert.assertThat(property.isSecure(), Matchers.is(false));
        Assert.assertThat(property.getConfigurationKey().getName(), Matchers.is("secureKey"));
        Assert.assertThat(getValue(), Matchers.is("secureValue"));
        Assert.assertThat(property.getEncryptedConfigurationValue(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldNotClearEncryptedValueWhenWhenNewValueNotProvided() {
        GoCipher goCipher = Mockito.mock(GoCipher.class);
        ConfigurationProperty property = new ConfigurationProperty(new ConfigurationKey("secureKey"), null, new EncryptedConfigurationValue("secureValue"), goCipher);
        property.handleSecureValueConfiguration(true);
        Assert.assertThat(property.isSecure(), Matchers.is(true));
        Assert.assertThat(property.getConfigurationKey().getName(), Matchers.is("secureKey"));
        Assert.assertThat(property.getConfigurationValue(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(property.getEncryptedConfigurationValue(), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(property.getEncryptedValue(), Matchers.is("secureValue"));
    }

    @Test
    public void shouldSetEmptyEncryptedValueWhenValueIsEmptyAndSecure() throws Exception {
        GoCipher goCipher = Mockito.mock(GoCipher.class);
        ConfigurationProperty property = new ConfigurationProperty(new ConfigurationKey("secureKey"), new ConfigurationValue(""), new EncryptedConfigurationValue("old"), goCipher);
        property.handleSecureValueConfiguration(true);
        Assert.assertThat(property.getEncryptedValue(), Matchers.is(""));
        Mockito.verify(cipher, Mockito.never()).decrypt(ArgumentMatchers.anyString());
    }

    @Test
    public void shouldFailValidationIfAPropertyDoesNotHaveValue() {
        ConfigurationProperty property = new ConfigurationProperty(new ConfigurationKey("secureKey"), null, new EncryptedConfigurationValue("invalid-encrypted-value"), new GoCipher());
        property.validate(ConfigSaveValidationContext.forChain(property));
        Assert.assertThat(property.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(property.errors().getAllOn(ENCRYPTED_VALUE).contains("Encrypted value for property with key 'secureKey' is invalid. This usually happens when the cipher text is modified to have an invalid value."), Matchers.is(true));
    }

    @Test
    public void shouldPassValidationIfBothNameAndValueAreProvided() {
        GoCipher cipher = Mockito.mock(GoCipher.class);
        ConfigurationProperty property = new ConfigurationProperty(new ConfigurationKey("name"), new ConfigurationValue("value"), null, cipher);
        property.validate(ConfigSaveValidationContext.forChain(property));
        Assert.assertThat(property.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldPassValidationIfBothNameAndEncryptedValueAreProvidedForSecureProperty() throws CryptoException {
        String encrypted = "encrypted";
        String decrypted = "decrypted";
        Mockito.when(cipher.decrypt(encrypted)).thenReturn(decrypted);
        ConfigurationProperty property = new ConfigurationProperty(new ConfigurationKey("name"), null, new EncryptedConfigurationValue(encrypted), cipher);
        property.validate(ConfigSaveValidationContext.forChain(property));
        Assert.assertThat(property.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldSetConfigAttributesForNonSecureProperty() {
        ConfigurationProperty configurationProperty = new ConfigurationProperty();
        HashMap attributes = new HashMap();
        HashMap keyMap = new HashMap();
        keyMap.put("name", "fooKey");
        attributes.put(CONFIGURATION_KEY, keyMap);
        HashMap valueMap = new HashMap();
        valueMap.put("value", "fooValue");
        attributes.put(CONFIGURATION_VALUE, valueMap);
        PackageConfigurations metadata = new PackageConfigurations();
        metadata.addConfiguration(new PackageConfiguration("fooKey", null));
        attributes.put(METADATA, metadata);
        configurationProperty.setConfigAttributes(attributes, null);
        Assert.assertThat(configurationProperty.getConfigurationKey().getName(), Matchers.is("fooKey"));
        Assert.assertThat(getValue(), Matchers.is("fooValue"));
    }

    @Test
    public void shouldInitializeConfigValueToBlankWhenBothValueAndEncryptedValueIsNull() {
        ConfigurationProperty configurationProperty = new ConfigurationProperty(new ConfigurationKey("key"), ((ConfigurationValue) (null)));
        configurationProperty.initialize();
        Assert.assertThat(configurationProperty.getConfigurationKey().getName(), Matchers.is("key"));
        Assert.assertThat(configurationProperty.getConfigurationValue(), Matchers.is(Matchers.notNullValue()));
        Assert.assertThat(getValue(), Matchers.is(""));
        Assert.assertThat(configurationProperty.getEncryptedConfigurationValue(), Matchers.is(Matchers.nullValue()));
        Method initializeMethod = ReflectionUtils.findMethod(ConfigurationProperty.class, "initialize");
        Assert.assertThat(initializeMethod.getAnnotation(PostConstruct.class), Matchers.is(Matchers.notNullValue()));
    }

    @Test
    public void shouldSetConfigAttributesForSecurePropertyWhenUserChangesIt() throws Exception {
        ConfigurationProperty configurationProperty = new ConfigurationProperty();
        HashMap attributes = new HashMap();
        HashMap keyMap = new HashMap();
        final String secureKey = "fooKey";
        keyMap.put("name", secureKey);
        attributes.put(CONFIGURATION_KEY, keyMap);
        HashMap valueMap = new HashMap();
        valueMap.put("value", "fooValue");
        attributes.put(CONFIGURATION_VALUE, valueMap);
        attributes.put(IS_CHANGED, "0");
        configurationProperty.setConfigAttributes(attributes, new SecureKeyInfoProvider() {
            @Override
            public boolean isSecure(String key) {
                return secureKey.equals(key);
            }
        });
        String encryptedValue = new GoCipher().encrypt("fooValue");
        Assert.assertThat(configurationProperty.getConfigurationKey().getName(), Matchers.is(secureKey));
        Assert.assertThat(configurationProperty.getConfigurationValue(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(configurationProperty.getEncryptedValue(), Matchers.is(encryptedValue));
    }

    @Test
    public void shouldSetConfigAttributesForSecurePropertyWhenUserDoesNotChangeIt() {
        ConfigurationProperty configurationProperty = new ConfigurationProperty();
        HashMap attributes = new HashMap();
        HashMap keyMap = new HashMap();
        final String secureKey = "fooKey";
        keyMap.put("name", secureKey);
        attributes.put(CONFIGURATION_KEY, keyMap);
        HashMap valueMap = new HashMap();
        valueMap.put("value", "fooValue");
        attributes.put(CONFIGURATION_VALUE, valueMap);
        HashMap encryptedValueMap = new HashMap();
        encryptedValueMap.put("value", "encryptedValue");
        attributes.put(ENCRYPTED_VALUE, encryptedValueMap);
        configurationProperty.setConfigAttributes(attributes, new SecureKeyInfoProvider() {
            @Override
            public boolean isSecure(String key) {
                return secureKey.equals(key);
            }
        });
        Assert.assertThat(configurationProperty.getConfigurationKey().getName(), Matchers.is(secureKey));
        Assert.assertThat(configurationProperty.getConfigurationValue(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(configurationProperty.getEncryptedValue(), Matchers.is("encryptedValue"));
    }

    @Test
    public void shouldSetConfigAttributesWhenMetadataIsNotPassedInMap() {
        ConfigurationProperty configurationProperty = new ConfigurationProperty();
        HashMap attributes = new HashMap();
        HashMap keyMap = new HashMap();
        keyMap.put("name", "fooKey");
        attributes.put(CONFIGURATION_KEY, keyMap);
        HashMap valueMap = new HashMap();
        valueMap.put("value", "fooValue");
        attributes.put(CONFIGURATION_VALUE, valueMap);
        configurationProperty.setConfigAttributes(attributes, null);
        Assert.assertThat(configurationProperty.getConfigurationKey().getName(), Matchers.is("fooKey"));
        Assert.assertThat(getValue(), Matchers.is("fooValue"));
        Assert.assertThat(configurationProperty.getEncryptedConfigurationValue(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldGetValueForSecureProperty() throws Exception {
        Mockito.when(cipher.decrypt("encrypted-value")).thenReturn("decrypted-value");
        ConfigurationProperty configurationProperty = new ConfigurationProperty(new ConfigurationKey("key"), null, new EncryptedConfigurationValue("encrypted-value"), cipher);
        Assert.assertThat(configurationProperty.getValue(), Matchers.is("decrypted-value"));
    }

    @Test
    public void shouldGetEmptyValueWhenSecurePropertyValueIsNullOrEmpty() throws Exception {
        Assert.assertThat(getValue(), Matchers.is(""));
        Assert.assertThat(getValue(), Matchers.is(""));
        Mockito.verify(cipher, Mockito.never()).decrypt(ArgumentMatchers.anyString());
    }

    @Test
    public void shouldGetValueForNonSecureProperty() {
        ConfigurationProperty configurationProperty = new ConfigurationProperty(new ConfigurationKey("key"), new ConfigurationValue("value"), null, cipher);
        Assert.assertThat(configurationProperty.getValue(), Matchers.is("value"));
    }

    @Test
    public void shouldGetNullValueForPropertyWhenValueIsNull() {
        ConfigurationProperty configurationProperty = new ConfigurationProperty(new ConfigurationKey("key"), null, null, cipher);
        Assert.assertThat(configurationProperty.getValue(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldCheckIfSecureValueFieldHasNoErrors() {
        EncryptedConfigurationValue encryptedValue = new EncryptedConfigurationValue("encrypted-value");
        Assert.assertThat(doesNotHaveErrorsAgainstConfigurationValue(), Matchers.is(true));
        encryptedValue.addError("value", "some-error");
        Assert.assertThat(doesNotHaveErrorsAgainstConfigurationValue(), Matchers.is(false));
    }

    @Test
    public void shouldCheckIfNonSecureValueFieldHasNoErrors() {
        ConfigurationValue configurationValue = new ConfigurationValue("encrypted-value");
        Assert.assertThat(doesNotHaveErrorsAgainstConfigurationValue(), Matchers.is(true));
        configurationValue.addError("value", "some-error");
        Assert.assertThat(doesNotHaveErrorsAgainstConfigurationValue(), Matchers.is(false));
    }

    @Test
    public void shouldValidateKeyUniqueness() {
        ConfigurationProperty property = new ConfigurationProperty(new ConfigurationKey("key"), new ConfigurationValue());
        HashMap<String, ConfigurationProperty> map = new HashMap<>();
        ConfigurationProperty original = new ConfigurationProperty(new ConfigurationKey("key"), new ConfigurationValue());
        map.put("key", original);
        property.validateKeyUniqueness(map, "Repo");
        Assert.assertThat(property.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(property.errors().getAllOn(CONFIGURATION_KEY).contains("Duplicate key 'key' found for Repo"), Matchers.is(true));
        Assert.assertThat(original.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(original.errors().getAllOn(CONFIGURATION_KEY).contains("Duplicate key 'key' found for Repo"), Matchers.is(true));
    }

    @Test
    public void shouldGetMaskedStringIfConfigurationPropertyIsSecure() {
        Assert.assertThat(getDisplayValue(), Matchers.is("****"));
        Assert.assertThat(getDisplayValue(), Matchers.is("value"));
    }
}

