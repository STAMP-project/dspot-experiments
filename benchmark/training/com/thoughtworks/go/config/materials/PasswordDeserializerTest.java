/**
 * Copyright 2016 ThoughtWorks, Inc.
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


import com.thoughtworks.go.config.materials.svn.SvnMaterialConfig;
import com.thoughtworks.go.security.CryptoException;
import com.thoughtworks.go.security.GoCipher;
import com.thoughtworks.go.security.ResetCipher;
import java.io.IOException;
import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class PasswordDeserializerTest {
    @Rule
    public final ResetCipher resetCipher = new ResetCipher();

    @Test
    public void shouldErrorOutWhenBothPasswordAndEncryptedPasswordAreGivenForDeserialization() throws CryptoException {
        SvnMaterialConfig svnMaterialConfig = new SvnMaterialConfig();
        PasswordDeserializer passwordDeserializer = new PasswordDeserializer();
        passwordDeserializer.deserialize("password", new GoCipher().encrypt("encryptedPassword"), svnMaterialConfig);
        Assert.assertThat(svnMaterialConfig.errors().getAllOn("password"), Matchers.is(Arrays.asList("You may only specify `password` or `encrypted_password`, not both!")));
        Assert.assertThat(svnMaterialConfig.errors().getAllOn("encryptedPassword"), Matchers.is(Arrays.asList("You may only specify `password` or `encrypted_password`, not both!")));
    }

    @Test
    public void shouldErrorOutWhenEncryptedPasswordIsInvalid() {
        SvnMaterialConfig svnMaterialConfig = new SvnMaterialConfig();
        PasswordDeserializer passwordDeserializer = new PasswordDeserializer();
        passwordDeserializer.deserialize(null, "invalidEncryptedPassword", svnMaterialConfig);
        Assert.assertThat(svnMaterialConfig.errors().getAllOn("encryptedPassword"), Matchers.is(Arrays.asList("Encrypted value for password is invalid. This usually happens when the cipher text is invalid.")));
    }

    @Test
    public void shouldEncryptClearTextPasswordSentByUser() throws CryptoException {
        SvnMaterialConfig svnMaterialConfig = new SvnMaterialConfig();
        PasswordDeserializer passwordDeserializer = new PasswordDeserializer();
        String encrypted = passwordDeserializer.deserialize("password", null, svnMaterialConfig);
        Assert.assertThat(encrypted, Matchers.is(new GoCipher().encrypt("password")));
    }

    @Test
    public void shouldReturnTheEncryptedPasswordSentByUserIfValid() throws CryptoException {
        String encryptedPassword = new GoCipher().encrypt("password");
        SvnMaterialConfig svnMaterialConfig = new SvnMaterialConfig();
        PasswordDeserializer passwordDeserializer = new PasswordDeserializer();
        String encrypted = passwordDeserializer.deserialize(null, encryptedPassword, svnMaterialConfig);
        Assert.assertThat(encrypted, Matchers.is(encryptedPassword));
    }

    @Test
    public void shouldReturnNullIfBothPasswordAndEncryptedPasswordAreNull() {
        SvnMaterialConfig svnMaterialConfig = new SvnMaterialConfig();
        PasswordDeserializer passwordDeserializer = new PasswordDeserializer();
        String encrypted = passwordDeserializer.deserialize(null, null, svnMaterialConfig);
        Assert.assertNull(encrypted);
    }

    @Test
    public void shouldReturnNullIfBothPasswordAndEncryptedPasswordAreBlank() {
        SvnMaterialConfig svnMaterialConfig = new SvnMaterialConfig();
        PasswordDeserializer passwordDeserializer = new PasswordDeserializer();
        String encrypted = passwordDeserializer.deserialize("", "", svnMaterialConfig);
        Assert.assertNull(encrypted);
    }

    @Test
    public void shouldNotValidateEncryptedPasswordIfBlank() {
        SvnMaterialConfig svnMaterialConfig = new SvnMaterialConfig();
        PasswordDeserializer passwordDeserializer = new PasswordDeserializer();
        String encrypted = passwordDeserializer.deserialize(null, "", svnMaterialConfig);
        Assert.assertNull(encrypted);
    }

    @Test
    public void shouldReEncryptedDESPasswords() throws IOException {
        resetCipher.setupAESCipherFile();
        resetCipher.setupDESCipherFile();
        SvnMaterialConfig svnMaterialConfig = new SvnMaterialConfig();
        PasswordDeserializer passwordDeserializer = new PasswordDeserializer();
        String encrypted = passwordDeserializer.deserialize(null, "mvcX9yrQsM4iPgm1tDxN1A==", svnMaterialConfig);
        Assert.assertThat(encrypted, Matchers.startsWith("AES:"));
    }
}

