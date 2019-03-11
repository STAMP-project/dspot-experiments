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
package com.thoughtworks.go.config;


import EnvironmentVariableConfig.NAME;
import EnvironmentVariableConfig.VALUE;
import EnvironmentVariableContext.EnvironmentVariable.MASK_VALUE;
import com.thoughtworks.go.domain.ConfigErrors;
import com.thoughtworks.go.security.CryptoException;
import com.thoughtworks.go.security.GoCipher;
import com.thoughtworks.go.util.command.EnvironmentVariableContext;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class EnvironmentVariableConfigTest {
    private GoCipher goCipher;

    @Test
    public void shouldEncryptValueWhenConstructedAsSecure() throws CryptoException {
        GoCipher goCipher = Mockito.mock(GoCipher.class);
        String encryptedText = "encrypted";
        Mockito.when(goCipher.encrypt("password")).thenReturn(encryptedText);
        EnvironmentVariableConfig environmentVariableConfig = new EnvironmentVariableConfig(goCipher);
        HashMap attrs = getAttributeMap("password", "true", "true");
        environmentVariableConfig.setConfigAttributes(attrs);
        Assert.assertThat(environmentVariableConfig.getEncryptedValue(), Matchers.is(encryptedText));
        Assert.assertThat(environmentVariableConfig.getName(), Matchers.is("foo"));
        Assert.assertThat(environmentVariableConfig.isSecure(), Matchers.is(true));
    }

    @Test
    public void shouldAssignNameAndValueForAVanillaEnvironmentVariable() {
        EnvironmentVariableConfig environmentVariableConfig = new EnvironmentVariableConfig(((GoCipher) (null)));
        HashMap attrs = new HashMap();
        attrs.put(NAME, "foo");
        attrs.put(VALUE, "password");
        environmentVariableConfig.setConfigAttributes(attrs);
        Assert.assertThat(environmentVariableConfig.getValue(), Matchers.is("password"));
        Assert.assertThat(environmentVariableConfig.getName(), Matchers.is("foo"));
        Assert.assertThat(environmentVariableConfig.isSecure(), Matchers.is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowUpWhenTheAttributeMapHasBothNameAndValueAreEmpty() throws CryptoException {
        EnvironmentVariableConfig environmentVariableConfig = new EnvironmentVariableConfig(((GoCipher) (null)));
        HashMap attrs = new HashMap();
        attrs.put(VALUE, "");
        environmentVariableConfig.setConfigAttributes(attrs);
    }

    @Test
    public void shouldGetPlainTextValueFromAnEncryptedValue() throws CryptoException {
        GoCipher mockGoCipher = Mockito.mock(GoCipher.class);
        String plainText = "password";
        String cipherText = "encrypted";
        Mockito.when(mockGoCipher.encrypt(plainText)).thenReturn(cipherText);
        Mockito.when(mockGoCipher.decrypt(cipherText)).thenReturn(plainText);
        EnvironmentVariableConfig environmentVariableConfig = new EnvironmentVariableConfig(mockGoCipher);
        HashMap attrs = getAttributeMap(plainText, "true", "true");
        environmentVariableConfig.setConfigAttributes(attrs);
        Assert.assertThat(environmentVariableConfig.getValue(), Matchers.is(plainText));
        Mockito.verify(mockGoCipher).decrypt(cipherText);
    }

    @Test
    public void shouldGetPlainTextValue() throws CryptoException {
        GoCipher mockGoCipher = Mockito.mock(GoCipher.class);
        String plainText = "password";
        EnvironmentVariableConfig environmentVariableConfig = new EnvironmentVariableConfig(mockGoCipher);
        HashMap attrs = getAttributeMap(plainText, "false", "1");
        environmentVariableConfig.setConfigAttributes(attrs);
        Assert.assertThat(environmentVariableConfig.getValue(), Matchers.is(plainText));
        Mockito.verify(mockGoCipher, Mockito.never()).decrypt(ArgumentMatchers.anyString());
        Mockito.verify(mockGoCipher, Mockito.never()).encrypt(ArgumentMatchers.anyString());
    }

    @Test
    public void shouldReturnEncryptedValueForSecureVariables() throws CryptoException {
        Mockito.when(goCipher.encrypt("bar")).thenReturn("encrypted");
        Mockito.when(goCipher.decrypt("encrypted")).thenReturn("bar");
        EnvironmentVariableConfig environmentVariableConfig = new EnvironmentVariableConfig(goCipher, "foo", "bar", true);
        Assert.assertThat(environmentVariableConfig.getName(), Matchers.is("foo"));
        Assert.assertThat(environmentVariableConfig.getValue(), Matchers.is("bar"));
        Assert.assertThat(environmentVariableConfig.getValueForDisplay(), Matchers.is(environmentVariableConfig.getEncryptedValue()));
    }

    @Test
    public void shouldReturnValueForInSecureVariables() {
        EnvironmentVariableConfig environmentVariableConfig = new EnvironmentVariableConfig(goCipher, "foo", "bar", false);
        Assert.assertThat(environmentVariableConfig.getName(), Matchers.is("foo"));
        Assert.assertThat(environmentVariableConfig.getValue(), Matchers.is("bar"));
        Assert.assertThat(environmentVariableConfig.getValueForDisplay(), Matchers.is("bar"));
    }

    @Test
    public void shouldEncryptValueWhenChanged() throws CryptoException {
        GoCipher mockGoCipher = Mockito.mock(GoCipher.class);
        String plainText = "password";
        String cipherText = "encrypted";
        Mockito.when(mockGoCipher.encrypt(plainText)).thenReturn(cipherText);
        Mockito.when(mockGoCipher.decrypt(cipherText)).thenReturn(plainText);
        EnvironmentVariableConfig environmentVariableConfig = new EnvironmentVariableConfig(mockGoCipher);
        HashMap firstSubmit = getAttributeMap(plainText, "true", "true");
        environmentVariableConfig.setConfigAttributes(firstSubmit);
        Assert.assertThat(environmentVariableConfig.getEncryptedValue(), Matchers.is(cipherText));
    }

    @Test
    public void shouldRetainEncryptedVariableWhenNotEdited() throws CryptoException {
        GoCipher mockGoCipher = Mockito.mock(GoCipher.class);
        String plainText = "password";
        String cipherText = "encrypted";
        Mockito.when(mockGoCipher.encrypt(plainText)).thenReturn(cipherText);
        Mockito.when(mockGoCipher.decrypt(cipherText)).thenReturn(plainText);
        Mockito.when(mockGoCipher.encrypt(cipherText)).thenReturn("SHOULD NOT DO THIS");
        EnvironmentVariableConfig environmentVariableConfig = new EnvironmentVariableConfig(mockGoCipher);
        HashMap firstSubmit = getAttributeMap(plainText, "true", "true");
        environmentVariableConfig.setConfigAttributes(firstSubmit);
        HashMap secondSubmit = getAttributeMap(cipherText, "true", "false");
        environmentVariableConfig.setConfigAttributes(secondSubmit);
        Assert.assertThat(environmentVariableConfig.getEncryptedValue(), Matchers.is(cipherText));
        Assert.assertThat(environmentVariableConfig.getName(), Matchers.is("foo"));
        Assert.assertThat(environmentVariableConfig.isSecure(), Matchers.is(true));
        Mockito.verify(mockGoCipher, Mockito.never()).encrypt(cipherText);
    }

    @Test
    public void shouldAddPlainTextEnvironmentVariableToContext() {
        String key = "key";
        String plainText = "plainText";
        EnvironmentVariableConfig environmentVariableConfig = new EnvironmentVariableConfig(goCipher, key, plainText, false);
        EnvironmentVariableContext context = new EnvironmentVariableContext();
        environmentVariableConfig.addTo(context);
        Assert.assertThat(context.getProperty(key), Matchers.is(plainText));
        Assert.assertThat(context.getPropertyForDisplay(key), Matchers.is(plainText));
    }

    @Test
    public void shouldAddSecureEnvironmentVariableToContext() throws CryptoException {
        String key = "key";
        String plainText = "plainText";
        String cipherText = "encrypted";
        Mockito.when(goCipher.encrypt(plainText)).thenReturn(cipherText);
        Mockito.when(goCipher.decrypt(cipherText)).thenReturn(plainText);
        EnvironmentVariableConfig environmentVariableConfig = new EnvironmentVariableConfig(goCipher, key, plainText, true);
        EnvironmentVariableContext context = new EnvironmentVariableContext();
        environmentVariableConfig.addTo(context);
        Assert.assertThat(context.getProperty(key), Matchers.is(plainText));
        Assert.assertThat(context.getPropertyForDisplay(key), Matchers.is(MASK_VALUE));
    }

    @Test
    public void shouldAnnotateEnsureEncryptedMethodWithPostConstruct() throws NoSuchMethodException {
        Class<EnvironmentVariableConfig> klass = EnvironmentVariableConfig.class;
        Method declaredMethods = klass.getDeclaredMethod("ensureEncrypted");
        Assert.assertThat(declaredMethods.getAnnotation(PostConstruct.class), Matchers.is(Matchers.not(Matchers.nullValue())));
    }

    @Test
    public void shouldErrorOutOnValidateWhenEncryptedValueIsForceChanged() throws CryptoException {
        String plainText = "secure_value";
        String cipherText = "cipherText";
        Mockito.when(goCipher.encrypt(plainText)).thenReturn(cipherText);
        Mockito.when(goCipher.decrypt(cipherText)).thenThrow(new CryptoException("last block incomplete in decryption"));
        EnvironmentVariableConfig environmentVariableConfig = new EnvironmentVariableConfig(goCipher, "secure_key", plainText, true);
        environmentVariableConfig.validate(null);
        ConfigErrors error = environmentVariableConfig.errors();
        Assert.assertThat(error.isEmpty(), Matchers.is(false));
        Assert.assertThat(error.on(VALUE), Matchers.is("Encrypted value for variable named 'secure_key' is invalid. This usually happens when the cipher text is modified to have an invalid value."));
    }

    @Test
    public void shouldNotErrorOutWhenValidationIsSuccessfulForSecureVariables() throws CryptoException {
        String plainText = "secure_value";
        String cipherText = "cipherText";
        Mockito.when(goCipher.encrypt(plainText)).thenReturn(cipherText);
        Mockito.when(goCipher.decrypt(cipherText)).thenReturn(plainText);
        EnvironmentVariableConfig environmentVariableConfig = new EnvironmentVariableConfig(goCipher, "secure_key", plainText, true);
        environmentVariableConfig.validate(null);
        Assert.assertThat(environmentVariableConfig.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldNotErrorOutWhenValidationIsSuccessfulForPlainTextVariables() {
        EnvironmentVariableConfig environmentVariableConfig = new EnvironmentVariableConfig(goCipher, "plain_key", "plain_value", false);
        environmentVariableConfig.validate(null);
        Assert.assertThat(environmentVariableConfig.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldMaskValueIfSecure() {
        EnvironmentVariableConfig secureEnvironmentVariable = new EnvironmentVariableConfig(goCipher, "plain_key", "plain_value", true);
        Assert.assertThat(secureEnvironmentVariable.getDisplayValue(), Matchers.is("****"));
    }

    @Test
    public void shouldNotMaskValueIfNotSecure() {
        EnvironmentVariableConfig secureEnvironmentVariable = new EnvironmentVariableConfig(goCipher, "plain_key", "plain_value", false);
        Assert.assertThat(secureEnvironmentVariable.getDisplayValue(), Matchers.is("plain_value"));
    }

    @Test
    public void shouldCopyEnvironmentVariableConfig() {
        EnvironmentVariableConfig secureEnvironmentVariable = new EnvironmentVariableConfig(goCipher, "plain_key", "plain_value", true);
        EnvironmentVariableConfig copy = new EnvironmentVariableConfig(secureEnvironmentVariable);
        Assert.assertThat(copy.getName(), Matchers.is(secureEnvironmentVariable.getName()));
        Assert.assertThat(copy.getValue(), Matchers.is(secureEnvironmentVariable.getValue()));
        Assert.assertThat(copy.getEncryptedValue(), Matchers.is(secureEnvironmentVariable.getEncryptedValue()));
        Assert.assertThat(copy.isSecure(), Matchers.is(secureEnvironmentVariable.isSecure()));
    }

    @Test
    public void shouldNotConsiderErrorsForEqualsCheck() {
        EnvironmentVariableConfig config1 = new EnvironmentVariableConfig("name", "value");
        EnvironmentVariableConfig config2 = new EnvironmentVariableConfig("name", "value");
        config2.addError("name", "errrr");
        Assert.assertThat(config1.equals(config2), Matchers.is(true));
    }

    @Test
    public void shouldDeserializeWithErrorFlagIfAnEncryptedVarialeHasBothClearTextAndCipherText() throws Exception {
        EnvironmentVariableConfig variable = new EnvironmentVariableConfig();
        variable.deserialize("PASSWORD", "clearText", true, "c!ph3rt3xt");
        Assert.assertThat(variable.errors().getAllOn("value"), Matchers.is(Arrays.asList("You may only specify `value` or `encrypted_value`, not both!")));
        Assert.assertThat(variable.errors().getAllOn("encryptedValue"), Matchers.is(Arrays.asList("You may only specify `value` or `encrypted_value`, not both!")));
    }

    @Test
    public void shouldDeserializeWithNoErrorFlagIfAnEncryptedVarialeHasClearTextWithSecureTrue() throws Exception {
        EnvironmentVariableConfig variable = new EnvironmentVariableConfig();
        variable.deserialize("PASSWORD", "clearText", true, null);
        Assert.assertTrue(variable.errors().isEmpty());
    }

    @Test
    public void shouldDeserializeWithNoErrorFlagIfAnEncryptedVarialeHasEitherClearTextWithSecureFalse() throws Exception {
        EnvironmentVariableConfig variable = new EnvironmentVariableConfig();
        variable.deserialize("PASSWORD", "clearText", false, null);
        Assert.assertTrue(variable.errors().isEmpty());
    }

    @Test
    public void shouldDeserializeWithNoErrorFlagIfAnEncryptedVariableHasCipherTextSetWithSecureTrue() throws Exception {
        EnvironmentVariableConfig variable = new EnvironmentVariableConfig();
        variable.deserialize("PASSWORD", null, true, "cipherText");
        Assert.assertTrue(variable.errors().isEmpty());
    }

    @Test
    public void shouldErrorOutForEncryptedValueBeingSetWhenSecureIsFalse() throws Exception {
        EnvironmentVariableConfig variable = new EnvironmentVariableConfig();
        variable.deserialize("PASSWORD", null, false, "cipherText");
        variable.validateTree(null);
        Assert.assertThat(variable.errors().getAllOn("encryptedValue"), Matchers.is(Arrays.asList("You may specify encrypted value only when option 'secure' is true.")));
    }
}

