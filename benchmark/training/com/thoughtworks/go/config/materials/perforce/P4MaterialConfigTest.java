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
package com.thoughtworks.go.config.materials.perforce;


import AbstractMaterialConfig.MATERIAL_NAME;
import P4MaterialConfig.PASSWORD;
import P4MaterialConfig.PASSWORD_CHANGED;
import P4MaterialConfig.SERVER_AND_PORT;
import P4MaterialConfig.USERNAME;
import P4MaterialConfig.USE_TICKETS;
import P4MaterialConfig.VIEW;
import ScmMaterialConfig.AUTO_UPDATE;
import ScmMaterialConfig.FILTER;
import ScmMaterialConfig.FOLDER;
import com.thoughtworks.go.config.CaseInsensitiveString;
import com.thoughtworks.go.config.ConfigSaveValidationContext;
import com.thoughtworks.go.config.materials.IgnoredFiles;
import com.thoughtworks.go.security.GoCipher;
import com.thoughtworks.go.util.ReflectionUtil;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class P4MaterialConfigTest {
    @Test
    public void shouldSetConfigAttributes() {
        P4MaterialConfig p4MaterialConfig = new P4MaterialConfig("", "");
        Map<String, String> map = new HashMap<>();
        map.put(SERVER_AND_PORT, "serverAndPort");
        map.put(USERNAME, "username");
        map.put(PASSWORD, "password");
        map.put(USE_TICKETS, "true");
        map.put(VIEW, "some-view");
        map.put(FOLDER, "folder");
        map.put(AUTO_UPDATE, "false");
        map.put(FILTER, "/root,/**/*.help");
        map.put(MATERIAL_NAME, "material-name");
        p4MaterialConfig.setConfigAttributes(map);
        Assert.assertThat(p4MaterialConfig.getServerAndPort(), Matchers.is("serverAndPort"));
        Assert.assertThat(p4MaterialConfig.getUserName(), Matchers.is("username"));
        Assert.assertThat(p4MaterialConfig.getView(), Matchers.is("some-view"));
        Assert.assertThat(p4MaterialConfig.getUseTickets(), Matchers.is(true));
        Assert.assertThat(p4MaterialConfig.getFolder(), Matchers.is("folder"));
        Assert.assertThat(p4MaterialConfig.getName(), Matchers.is(new CaseInsensitiveString("material-name")));
        Assert.assertThat(p4MaterialConfig.isAutoUpdate(), Matchers.is(false));
        Assert.assertThat(p4MaterialConfig.filter(), Matchers.is(new com.thoughtworks.go.config.materials.Filter(new IgnoredFiles("/root"), new IgnoredFiles("/**/*.help"))));
    }

    @Test
    public void validate_shouldEnsureThatViewIsNotBlank() {
        assertError("example.com:1233", "", VIEW, "P4 view cannot be empty.");
    }

    @Test
    public void shouldNotDoAnyValidationOnP4PortExceptToEnsureThatItIsNotEmpty() throws Exception {
        assertError("", "view", SERVER_AND_PORT, "P4 port cannot be empty.");
        assertError(" ", "view", SERVER_AND_PORT, "P4 port cannot be empty.");
        assertNoError("example.com:1818", "view", SERVER_AND_PORT);
        assertNoError("ssl:host:1234", "view", SERVER_AND_PORT);
        assertNoError("ssl:host:non_numerical_port", "view", SERVER_AND_PORT);
        assertNoError("complete_junk:::abc:::123:::def", "view", SERVER_AND_PORT);
        assertNoError(":1234", "view", SERVER_AND_PORT);
        assertNoError(":abc", "view", SERVER_AND_PORT);
        assertNoError("1234", "view", SERVER_AND_PORT);
        assertNoError("tcp:abc:1234", "view", SERVER_AND_PORT);
    }

    @Test
    public void shouldReturnIfAttributeMapIsNull() {
        P4MaterialConfig p4MaterialConfig = new P4MaterialConfig("", "");
        p4MaterialConfig.setConfigAttributes(null);
        Assert.assertThat(p4MaterialConfig, Matchers.is(new P4MaterialConfig("", "")));
    }

    @Test
    public void setConfigAttributes_shouldUpdatePasswordWhenPasswordChangedBooleanChanged() throws Exception {
        P4MaterialConfig materialConfig = new P4MaterialConfig("", "");
        materialConfig.setPassword("notSecret");
        Map<String, String> map = new HashMap<>();
        map.put(PASSWORD, "secret");
        map.put(PASSWORD_CHANGED, "1");
        materialConfig.setConfigAttributes(map);
        Assert.assertThat(ReflectionUtil.getField(materialConfig, "password"), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(materialConfig.getPassword(), Matchers.is("secret"));
        Assert.assertThat(materialConfig.getEncryptedPassword(), Matchers.is(new GoCipher().encrypt("secret")));
        // Dont change
        map.put(SvnMaterialConfig.PASSWORD, "Hehehe");
        map.put(SvnMaterialConfig.PASSWORD_CHANGED, "0");
        materialConfig.setConfigAttributes(map);
        Assert.assertThat(ReflectionUtil.getField(materialConfig, "password"), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(materialConfig.getPassword(), Matchers.is("secret"));
        Assert.assertThat(materialConfig.getEncryptedPassword(), Matchers.is(new GoCipher().encrypt("secret")));
        // Dont change
        map.put(SvnMaterialConfig.PASSWORD, "");
        map.put(SvnMaterialConfig.PASSWORD_CHANGED, "1");
        materialConfig.setConfigAttributes(map);
        Assert.assertThat(materialConfig.getPassword(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(materialConfig.getEncryptedPassword(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldNotSetUseTicketsIfNotInConfigAttributesMap() {
        P4MaterialConfig p4MaterialConfig = new P4MaterialConfig("", "");
        HashMap<String, String> map = new HashMap<>();
        map.put(USE_TICKETS, "true");
        p4MaterialConfig.setConfigAttributes(map);
        Assert.assertThat(p4MaterialConfig.getUseTickets(), Matchers.is(true));
        p4MaterialConfig.setConfigAttributes(new HashMap());
        Assert.assertThat(p4MaterialConfig.getUseTickets(), Matchers.is(false));
    }

    @Test
    public void shouldThrowErrorsIfBothPasswordAndEncryptedPasswordAreProvided() {
        P4MaterialConfig materialConfig = new P4MaterialConfig("foo/bar, 80", "password", "encryptedPassword", new GoCipher());
        materialConfig.validate(new ConfigSaveValidationContext(null));
        Assert.assertThat(materialConfig.errors().on("password"), Matchers.is("You may only specify `password` or `encrypted_password`, not both!"));
        Assert.assertThat(materialConfig.errors().on("encryptedPassword"), Matchers.is("You may only specify `password` or `encrypted_password`, not both!"));
    }

    @Test
    public void shouldValidateWhetherTheEncryptedPasswordIsCorrect() {
        P4MaterialConfig materialConfig = new P4MaterialConfig("foo/bar, 80", "", "encryptedPassword", new GoCipher());
        materialConfig.validate(new ConfigSaveValidationContext(null));
        Assert.assertThat(materialConfig.errors().on("encryptedPassword"), Matchers.is("Encrypted password value for P4 material with serverAndPort 'foo/bar, 80' is invalid. This usually happens when the cipher text is modified to have an invalid value."));
    }
}

