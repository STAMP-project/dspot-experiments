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
package com.thoughtworks.go.config.materials.svn;


import AbstractMaterialConfig.MATERIAL_NAME;
import ScmMaterialConfig.AUTO_UPDATE;
import ScmMaterialConfig.FILTER;
import ScmMaterialConfig.FOLDER;
import SvnMaterialConfig.CHECK_EXTERNALS;
import SvnMaterialConfig.PASSWORD;
import SvnMaterialConfig.PASSWORD_CHANGED;
import SvnMaterialConfig.URL;
import SvnMaterialConfig.USERNAME;
import com.thoughtworks.go.config.CaseInsensitiveString;
import com.thoughtworks.go.config.ConfigSaveValidationContext;
import com.thoughtworks.go.config.materials.IgnoredFiles;
import com.thoughtworks.go.security.GoCipher;
import com.thoughtworks.go.util.ReflectionUtil;
import com.thoughtworks.go.util.command.UrlArgument;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class SvnMaterialConfigTest {
    @Test
    public void shouldSetConfigAttributes() {
        SvnMaterialConfig svnMaterialConfig = new SvnMaterialConfig("", "", "", false);
        Map<String, String> map = new HashMap<>();
        map.put(URL, "url");
        map.put(USERNAME, "username");
        map.put(CHECK_EXTERNALS, "true");
        map.put(FOLDER, "folder");
        map.put(AUTO_UPDATE, "0");
        map.put(FILTER, "/root,/**/*.help");
        map.put(MATERIAL_NAME, "material-name");
        svnMaterialConfig.setConfigAttributes(map);
        Assert.assertThat(svnMaterialConfig.getUrl(), Matchers.is("url"));
        Assert.assertThat(svnMaterialConfig.getUserName(), Matchers.is("username"));
        Assert.assertThat(svnMaterialConfig.isCheckExternals(), Matchers.is(true));
        Assert.assertThat(svnMaterialConfig.getFolder(), Matchers.is("folder"));
        Assert.assertThat(svnMaterialConfig.getName(), Matchers.is(new CaseInsensitiveString("material-name")));
        Assert.assertThat(svnMaterialConfig.isAutoUpdate(), Matchers.is(false));
        Assert.assertThat(svnMaterialConfig.filter(), Matchers.is(new com.thoughtworks.go.config.materials.Filter(new IgnoredFiles("/root"), new IgnoredFiles("/**/*.help"))));
    }

    @Test
    public void validate_shouldEnsureUrlIsNotBlank() {
        SvnMaterialConfig svnMaterialConfig = new SvnMaterialConfig("", "", "", false);
        svnMaterialConfig.validate(new ConfigSaveValidationContext(null));
        Assert.assertThat(svnMaterialConfig.errors().on(URL), Matchers.is("URL cannot be blank"));
    }

    @Test
    public void validate_shouldEnsureUrlIsNotNull() {
        SvnMaterialConfig svnMaterialConfig = new SvnMaterialConfig();
        svnMaterialConfig.setUrl(null);
        svnMaterialConfig.validate(new ConfigSaveValidationContext(null));
        Assert.assertThat(svnMaterialConfig.errors().on(URL), Matchers.is("URL cannot be blank"));
    }

    @Test
    public void validate_shouldEnsureMaterialNameIsValid() {
        SvnMaterialConfig svnMaterialConfig = new SvnMaterialConfig("/foo", "", "", false);
        svnMaterialConfig.validate(new ConfigSaveValidationContext(null));
        Assert.assertThat(svnMaterialConfig.errors().on(SvnMaterialConfig.MATERIAL_NAME), Matchers.is(Matchers.nullValue()));
        svnMaterialConfig.setName(new CaseInsensitiveString(".bad-name-with-dot"));
        svnMaterialConfig.validate(new ConfigSaveValidationContext(null));
        Assert.assertThat(svnMaterialConfig.errors().on(SvnMaterialConfig.MATERIAL_NAME), Matchers.is("Invalid material name '.bad-name-with-dot'. This must be alphanumeric and can contain underscores and periods (however, it cannot start with a period). The maximum allowed length is 255 characters."));
    }

    @Test
    public void validate_shouldEnsureDestFilePathIsValid() {
        SvnMaterialConfig svnMaterialConfig = new SvnMaterialConfig("/foo", "", "", false);
        svnMaterialConfig.setConfigAttributes(Collections.singletonMap(FOLDER, "../a"));
        svnMaterialConfig.validate(new ConfigSaveValidationContext(null));
        Assert.assertThat(svnMaterialConfig.errors().on(SvnMaterialConfig.FOLDER), Matchers.is("Dest folder '../a' is not valid. It must be a sub-directory of the working folder."));
    }

    @Test
    public void shouldThrowErrorsIfBothPasswordAndEncryptedPasswordAreProvided() {
        SvnMaterialConfig svnMaterialConfig = new SvnMaterialConfig(new UrlArgument("foo/bar"), "password", "encryptedPassword", new GoCipher(), null, false, "folder");
        svnMaterialConfig.validate(new ConfigSaveValidationContext(null));
        Assert.assertThat(svnMaterialConfig.errors().on("password"), Matchers.is("You may only specify `password` or `encrypted_password`, not both!"));
        Assert.assertThat(svnMaterialConfig.errors().on("encryptedPassword"), Matchers.is("You may only specify `password` or `encrypted_password`, not both!"));
    }

    @Test
    public void shouldValidateWhetherTheEncryptedPasswordIsCorrect() {
        SvnMaterialConfig svnMaterialConfig = new SvnMaterialConfig(new UrlArgument("foo/bar"), "", "encryptedPassword", new GoCipher(), null, false, "folder");
        svnMaterialConfig.validate(new ConfigSaveValidationContext(null));
        Assert.assertThat(svnMaterialConfig.errors().on("encryptedPassword"), Matchers.is("Encrypted password value for svn material with url 'foo/bar' is invalid. This usually happens when the cipher text is modified to have an invalid value."));
    }

    @Test
    public void setConfigAttributes_shouldUpdatePasswordWhenPasswordChangedBooleanChanged() throws Exception {
        SvnMaterialConfig svnMaterial = new SvnMaterialConfig("", "", "notSoSecret", false);
        Map<String, String> map = new HashMap<>();
        map.put(PASSWORD, "secret");
        map.put(PASSWORD_CHANGED, "1");
        svnMaterial.setConfigAttributes(map);
        Assert.assertThat(ReflectionUtil.getField(svnMaterial, "password"), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(svnMaterial.getPassword(), Matchers.is("secret"));
        Assert.assertThat(svnMaterial.getEncryptedPassword(), Matchers.is(new GoCipher().encrypt("secret")));
        // Dont change
        map.put(PASSWORD, "Hehehe");
        map.put(PASSWORD_CHANGED, "0");
        svnMaterial.setConfigAttributes(map);
        Assert.assertThat(ReflectionUtil.getField(svnMaterial, "password"), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(svnMaterial.getPassword(), Matchers.is("secret"));
        Assert.assertThat(svnMaterial.getEncryptedPassword(), Matchers.is(new GoCipher().encrypt("secret")));
        map.put(PASSWORD, "");
        map.put(PASSWORD_CHANGED, "1");
        svnMaterial.setConfigAttributes(map);
        Assert.assertThat(svnMaterial.getPassword(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(svnMaterial.getEncryptedPassword(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldReturnTheUrl() {
        String url = "git@github.com/my/repo";
        SvnMaterialConfig config = new SvnMaterialConfig();
        config.setUrl(url);
        Assert.assertThat(config.getUrl(), Matchers.is(url));
    }

    @Test
    public void shouldReturnNullIfUrlForMaterialNotSpecified() {
        SvnMaterialConfig config = new SvnMaterialConfig();
        Assert.assertNull(config.getUrl());
    }

    @Test
    public void shouldHandleNullWhenSettingUrlForAMaterial() {
        SvnMaterialConfig config = new SvnMaterialConfig();
        config.setUrl(null);
        Assert.assertNull(config.getUrl());
    }
}

