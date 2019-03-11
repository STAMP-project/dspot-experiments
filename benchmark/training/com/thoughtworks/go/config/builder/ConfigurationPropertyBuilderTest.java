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
package com.thoughtworks.go.config.builder;


import Property.SECURE;
import com.thoughtworks.go.domain.config.ConfigurationProperty;
import com.thoughtworks.go.plugin.api.config.Property;
import com.thoughtworks.go.security.GoCipher;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ConfigurationPropertyBuilderTest {
    @Test
    public void shouldCreateConfigurationPropertyWithEncyrptedValueForSecureProperty() {
        Property key = new Property("key");
        key.with(SECURE, true);
        ConfigurationProperty property = new ConfigurationPropertyBuilder().create("key", null, "enc_value", true);
        Assert.assertThat(property.getConfigKeyName(), Matchers.is("key"));
        Assert.assertThat(property.getEncryptedValue(), Matchers.is("enc_value"));
        Assert.assertNull(property.getConfigurationValue());
    }

    @Test
    public void shouldCreateWithEncyrptedValueForOnlyPlainTextInputForSecureProperty() throws Exception {
        Property key = new Property("key");
        key.with(SECURE, true);
        ConfigurationProperty property = new ConfigurationPropertyBuilder().create("key", "value", null, true);
        Assert.assertThat(property.getConfigKeyName(), Matchers.is("key"));
        Assert.assertThat(property.getEncryptedValue(), Matchers.is(new GoCipher().encrypt("value")));
        Assert.assertNull(property.getConfigurationValue());
    }

    @Test
    public void shouldCreatePropertyInAbsenceOfPlainAndEncryptedTextInputForSecureProperty() throws Exception {
        Property key = new Property("key");
        key.with(SECURE, true);
        ConfigurationProperty property = new ConfigurationPropertyBuilder().create("key", null, null, true);
        Assert.assertThat(property.errors().size(), Matchers.is(0));
        Assert.assertThat(property.getConfigKeyName(), Matchers.is("key"));
        Assert.assertNull(property.getEncryptedConfigurationValue());
        Assert.assertNull(property.getConfigurationValue());
    }

    @Test
    public void shouldCreateWithErrorsIfBothPlainAndEncryptedTextInputAreSpecifiedForSecureProperty() {
        Property key = new Property("key");
        key.with(SECURE, true);
        ConfigurationProperty property = new ConfigurationPropertyBuilder().create("key", "value", "enc_value", true);
        Assert.assertThat(property.errors().get("configurationValue").get(0), Matchers.is("You may only specify `value` or `encrypted_value`, not both!"));
        Assert.assertThat(property.errors().get("encryptedValue").get(0), Matchers.is("You may only specify `value` or `encrypted_value`, not both!"));
        Assert.assertThat(property.getConfigurationValue().getValue(), Matchers.is("value"));
        Assert.assertThat(property.getEncryptedValue(), Matchers.is("enc_value"));
    }

    @Test
    public void shouldCreateWithErrorsIfBothPlainAndEncryptedTextInputAreSpecifiedForUnSecuredProperty() {
        Property key = new Property("key");
        key.with(SECURE, false);
        ConfigurationProperty property = new ConfigurationPropertyBuilder().create("key", "value", "enc_value", false);
        Assert.assertThat(property.errors().get("configurationValue").get(0), Matchers.is("You may only specify `value` or `encrypted_value`, not both!"));
        Assert.assertThat(property.errors().get("encryptedValue").get(0), Matchers.is("You may only specify `value` or `encrypted_value`, not both!"));
        Assert.assertThat(property.getConfigurationValue().getValue(), Matchers.is("value"));
        Assert.assertThat(property.getEncryptedValue(), Matchers.is("enc_value"));
    }

    @Test
    public void shouldCreateWithErrorsInPresenceOfEncryptedTextInputForUnSecuredProperty() {
        Property key = new Property("key");
        key.with(SECURE, false);
        ConfigurationProperty property = new ConfigurationPropertyBuilder().create("key", null, "enc_value", false);
        Assert.assertThat(property.errors().get("encryptedValue").get(0), Matchers.is("encrypted_value cannot be specified to a unsecured property."));
        Assert.assertThat(property.getEncryptedValue(), Matchers.is("enc_value"));
    }

    @Test
    public void shouldCreateWithValueForAUnsecuredProperty() {
        Property key = new Property("key");
        key.with(SECURE, false);
        ConfigurationProperty property = new ConfigurationPropertyBuilder().create("key", "value", null, false);
        Assert.assertThat(property.getConfigurationValue().getValue(), Matchers.is("value"));
        Assert.assertNull(property.getEncryptedConfigurationValue());
    }
}

