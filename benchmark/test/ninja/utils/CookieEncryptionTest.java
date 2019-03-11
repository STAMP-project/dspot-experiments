/**
 * Copyright (C) 2012-2019 the original author or authors.
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
package ninja.utils;


import NinjaConstant.applicationCookieEncrypted;
import NinjaConstant.applicationSecret;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class CookieEncryptionTest {
    @Mock
    NinjaProperties ninjaProperties;

    @Test
    public void testThatEncryptionAndDecryptionWorksWhenEnabled() {
        String applicationSecret = SecretGenerator.generateSecret();
        Mockito.when(ninjaProperties.getOrDie(applicationSecret)).thenReturn(applicationSecret);
        Mockito.when(ninjaProperties.getBooleanWithDefault(applicationCookieEncrypted, false)).thenReturn(true);
        CookieEncryption cookieEncryption = new CookieEncryption(ninjaProperties);
        String stringToEncrypt = "a_very_big_secret";
        String encrypted = cookieEncryption.encrypt(stringToEncrypt);
        Assert.assertThat(encrypted, CoreMatchers.not(CoreMatchers.equalTo(stringToEncrypt)));
        String decrypted = cookieEncryption.decrypt(encrypted);
        Assert.assertThat(decrypted, CoreMatchers.equalTo(stringToEncrypt));
    }

    @Test
    public void testThatEncryptionDoesNotDoAnythingWhenDisabled() {
        Mockito.when(ninjaProperties.getBooleanWithDefault(applicationCookieEncrypted, false)).thenReturn(false);
        CookieEncryption cookieEncryption = new CookieEncryption(ninjaProperties);
        String stringToEncrypt = "a_very_big_secret";
        String encrypted = cookieEncryption.encrypt(stringToEncrypt);
        Assert.assertThat(encrypted, CoreMatchers.equalTo(stringToEncrypt));
        String decrypted = cookieEncryption.decrypt(encrypted);
        Assert.assertThat(decrypted, CoreMatchers.equalTo(stringToEncrypt));
    }

    @Test(expected = RuntimeException.class)
    public void testThatEncryptionFailsWhenSecretEmpty() {
        String applicationSecret = "";
        Mockito.when(ninjaProperties.getOrDie(applicationSecret)).thenReturn(applicationSecret);
        Mockito.when(ninjaProperties.getBooleanWithDefault(applicationCookieEncrypted, false)).thenReturn(true);
        new CookieEncryption(ninjaProperties);
    }

    @Test(expected = RuntimeException.class)
    public void testThatEncryptionFailsWhenSecretTooSmall() {
        String applicationSecret = "1234";
        Mockito.when(ninjaProperties.getOrDie(applicationSecret)).thenReturn(applicationSecret);
        Mockito.when(ninjaProperties.getBooleanWithDefault(applicationCookieEncrypted, false)).thenReturn(true);
        new CookieEncryption(ninjaProperties);
    }
}

