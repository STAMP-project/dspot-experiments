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


import com.thoughtworks.go.security.CryptoException;
import com.thoughtworks.go.security.GoCipher;
import com.thoughtworks.go.util.ReflectionUtil;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class MailHostTest {
    @Test
    public void shouldEncryptMailHostPassword() throws CryptoException {
        GoCipher mockGoCipher = Mockito.mock(GoCipher.class);
        Mockito.when(mockGoCipher.encrypt("password")).thenReturn("encrypted");
        MailHost mailHost = new MailHost("hostname", 42, "username", "password", null, true, true, "from", "mail@admin.com", mockGoCipher);
        Assert.assertThat(ReflectionUtil.getField(mailHost, "password"), Matchers.is("password"));
        Assert.assertThat(mailHost.getEncryptedPassword(), Matchers.is("encrypted"));
    }

    @Test
    public void shouldDecryptMailHostPassword() throws CryptoException {
        GoCipher mockGoCipher = Mockito.mock(GoCipher.class);
        Mockito.when(mockGoCipher.decrypt("encrypted")).thenReturn("password");
        Mockito.when(mockGoCipher.maybeReEncryptForPostConstructWithoutExceptions("encrypted")).thenReturn("encrypted");
        MailHost mailHost = new MailHost("hostname", 42, "username", null, null, true, true, "from", "mail@admin.com", mockGoCipher);
        ReflectionUtil.setField(mailHost, "encryptedPassword", "encrypted");
        mailHost.ensureEncrypted();
        Assert.assertThat(mailHost.getPassword(), Matchers.is("password"));
    }

    @Test
    public void shouldReturnTrueIfTwoMailhostsHaveDifferentPasswords() {
        MailHost mailHost1 = new MailHost("blah", 42, "blah", "password-1", true, true, "from", "to");
        MailHost mailHost2 = new MailHost("blah", 42, "blah", "password-2", true, true, "from", "to");
        MailHost mailHost3 = new MailHost("blah", 42, "blah", "password-2", false, true, "from", "to");
        Assert.assertThat(mailHost1, Matchers.is(mailHost2));
        Assert.assertThat(mailHost1.hashCode(), Matchers.is(mailHost2.hashCode()));
        Assert.assertThat(mailHost2, Matchers.is(mailHost3));
        Assert.assertThat(mailHost2.hashCode(), Matchers.is(mailHost3.hashCode()));
    }

    @Test
    public void shouldReturnNullIfPasswordIsNotSetAndEncryptedPasswordIsEmpty() {
        MailHost mailHost = new MailHost("blah", 42, "blah", "", "", false, true, "from", "to", null);
        mailHost.ensureEncrypted();
        Assert.assertThat(mailHost.getCurrentPassword(), Matchers.is(Matchers.nullValue()));
        mailHost = new MailHost("blah", 42, "blah", "", null, false, true, "from", "to", null);
        mailHost.ensureEncrypted();
        Assert.assertThat(mailHost.getCurrentPassword(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldNullifyPasswordIfBlank() {
        MailHost mailHost = new MailHost("blah", 42, "", "", "", false, true, "from", "to", null);
        mailHost.ensureEncrypted();
        Assert.assertThat(mailHost.getUserName(), Matchers.is(Matchers.nullValue()));
    }
}

