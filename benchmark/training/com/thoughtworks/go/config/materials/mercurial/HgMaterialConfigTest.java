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
package com.thoughtworks.go.config.materials.mercurial;


import AbstractMaterialConfig.MATERIAL_NAME;
import HgMaterialConfig.URL;
import ScmMaterialConfig.AUTO_UPDATE;
import ScmMaterialConfig.FILTER;
import ScmMaterialConfig.FOLDER;
import com.thoughtworks.go.config.CaseInsensitiveString;
import com.thoughtworks.go.config.ConfigSaveValidationContext;
import com.thoughtworks.go.config.materials.IgnoredFiles;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class HgMaterialConfigTest {
    @Test
    public void shouldSetConfigAttributes() {
        HgMaterialConfig hgMaterialConfig = new HgMaterialConfig("", null);
        Map<String, String> map = new HashMap<>();
        map.put(URL, "url");
        map.put(FOLDER, "folder");
        map.put(AUTO_UPDATE, "0");
        map.put(FILTER, "/root,/**/*.help");
        map.put(MATERIAL_NAME, "material-name");
        hgMaterialConfig.setConfigAttributes(map);
        Assert.assertThat(hgMaterialConfig.getUrl(), Matchers.is("url"));
        Assert.assertThat(hgMaterialConfig.getFolder(), Matchers.is("folder"));
        Assert.assertThat(hgMaterialConfig.getName(), Matchers.is(new CaseInsensitiveString("material-name")));
        Assert.assertThat(hgMaterialConfig.isAutoUpdate(), Matchers.is(false));
        Assert.assertThat(hgMaterialConfig.filter(), Matchers.is(new com.thoughtworks.go.config.materials.Filter(new IgnoredFiles("/root"), new IgnoredFiles("/**/*.help"))));
    }

    @Test
    public void validate_shouldEnsureUrlIsNotBlank() {
        HgMaterialConfig hgMaterialConfig = new HgMaterialConfig("", null);
        hgMaterialConfig.validate(new ConfigSaveValidationContext(null));
        Assert.assertThat(hgMaterialConfig.errors().on(URL), Matchers.is("URL cannot be blank"));
    }

    @Test
    public void shouldReturnIfAttributeMapIsNull() {
        HgMaterialConfig hgMaterialConfig = new HgMaterialConfig("", null);
        hgMaterialConfig.setConfigAttributes(null);
        Assert.assertThat(hgMaterialConfig, Matchers.is(new HgMaterialConfig("", null)));
    }

    @Test
    public void shouldReturnTheUrl() {
        String url = "git@github.com/my/repo";
        HgMaterialConfig config = new HgMaterialConfig(url, null);
        Assert.assertThat(config.getUrl(), Matchers.is(url));
    }

    @Test
    public void shouldReturnNullIfUrlForMaterialNotSpecified() {
        HgMaterialConfig config = new HgMaterialConfig();
        Assert.assertNull(config.getUrl());
    }

    @Test
    public void shouldSetUrlForAMaterial() {
        String url = "git@github.com/my/repo";
        HgMaterialConfig config = new HgMaterialConfig();
        config.setUrl(url);
        Assert.assertThat(config.getUrl(), Matchers.is(url));
    }

    @Test
    public void shouldHandleNullWhenSettingUrlForAMaterial() {
        HgMaterialConfig config = new HgMaterialConfig();
        config.setUrl(null);
        Assert.assertNull(config.getUrl());
    }
}

