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
package com.thoughtworks.go.config.materials.git;


import AbstractMaterialConfig.MATERIAL_NAME;
import GitMaterialConfig.BRANCH;
import GitMaterialConfig.SHALLOW_CLONE;
import GitMaterialConfig.URL;
import ScmMaterialConfig.AUTO_UPDATE;
import ScmMaterialConfig.FILTER;
import ScmMaterialConfig.FOLDER;
import com.thoughtworks.go.config.CaseInsensitiveString;
import com.thoughtworks.go.config.ConfigSaveValidationContext;
import com.thoughtworks.go.config.materials.Filter;
import com.thoughtworks.go.config.materials.IgnoredFiles;
import com.thoughtworks.go.util.command.UrlArgument;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class GitMaterialConfigTest {
    @Test
    public void shouldSetConfigAttributes() {
        GitMaterialConfig gitMaterialConfig = new GitMaterialConfig("");
        Map<String, String> map = new HashMap<>();
        map.put(URL, "url");
        map.put(BRANCH, "some-branch");
        map.put(SHALLOW_CLONE, "true");
        map.put(FOLDER, "folder");
        map.put(AUTO_UPDATE, null);
        map.put(FILTER, "/root,/**/*.help");
        map.put(MATERIAL_NAME, "material-name");
        gitMaterialConfig.setConfigAttributes(map);
        Assert.assertThat(gitMaterialConfig.getUrl(), Matchers.is("url"));
        Assert.assertThat(gitMaterialConfig.getFolder(), Matchers.is("folder"));
        Assert.assertThat(gitMaterialConfig.getBranch(), Matchers.is("some-branch"));
        Assert.assertThat(gitMaterialConfig.getName(), Matchers.is(new CaseInsensitiveString("material-name")));
        Assert.assertThat(gitMaterialConfig.isAutoUpdate(), Matchers.is(false));
        Assert.assertThat(gitMaterialConfig.isShallowClone(), Matchers.is(true));
        Assert.assertThat(gitMaterialConfig.filter(), Matchers.is(new Filter(new IgnoredFiles("/root"), new IgnoredFiles("/**/*.help"))));
    }

    @Test
    public void byDefaultShallowCloneShouldBeOff() {
        Assert.assertThat(new GitMaterialConfig("http://url", "foo").isShallowClone(), Matchers.is(false));
        Assert.assertThat(new GitMaterialConfig("http://url", "foo", false).isShallowClone(), Matchers.is(false));
        Assert.assertThat(new GitMaterialConfig("http://url", "foo", null).isShallowClone(), Matchers.is(false));
        Assert.assertThat(new GitMaterialConfig("http://url", "foo", true).isShallowClone(), Matchers.is(true));
    }

    @Test
    public void validate_shouldEnsureUrlIsNotBlank() {
        GitMaterialConfig gitMaterialConfig = new GitMaterialConfig("");
        gitMaterialConfig.validate(new ConfigSaveValidationContext(null));
        Assert.assertThat(gitMaterialConfig.errors().on(URL), Matchers.is("URL cannot be blank"));
    }

    @Test
    public void shouldReturnIfAttributeMapIsNull() {
        GitMaterialConfig gitMaterialConfig = new GitMaterialConfig("");
        gitMaterialConfig.setConfigAttributes(null);
        Assert.assertThat(gitMaterialConfig, Matchers.is(new GitMaterialConfig("")));
    }

    @Test
    public void shouldReturnTheUrl() {
        String url = "git@github.com/my/repo";
        GitMaterialConfig config = new GitMaterialConfig(url);
        Assert.assertThat(config.getUrl(), Matchers.is(url));
    }

    @Test
    public void shouldReturnNullIfUrlForMaterialNotSpecified() {
        GitMaterialConfig config = new GitMaterialConfig();
        Assert.assertNull(config.getUrl());
    }

    @Test
    public void shouldSetUrlForAMaterial() {
        String url = "git@github.com/my/repo";
        GitMaterialConfig config = new GitMaterialConfig();
        config.setUrl(url);
        Assert.assertThat(config.getUrl(), Matchers.is(url));
    }

    @Test
    public void shouldHandleNullWhenSettingUrlForAMaterial() {
        GitMaterialConfig config = new GitMaterialConfig();
        config.setUrl(null);
        Assert.assertNull(config.getUrl());
    }

    @Test
    public void shouldHandleNullUrlAtTheTimeOfGitMaterialConfigCreation() {
        GitMaterialConfig config = new GitMaterialConfig(null);
        Assert.assertNull(config.getUrl());
    }

    @Test
    public void shouldHandleNullBranchAtTheTimeOfMaterialConfigCreation() {
        GitMaterialConfig config1 = new GitMaterialConfig("http://url", null);
        GitMaterialConfig config2 = new GitMaterialConfig(new UrlArgument("http://url"), null, "sub1", true, new Filter(), false, "folder", new CaseInsensitiveString("git"), false);
        Assert.assertThat(config1.getBranch(), Matchers.is("master"));
        Assert.assertThat(config2.getBranch(), Matchers.is("master"));
    }

    @Test
    public void shouldHandleNullBranchWhileSettingConfigAttributes() {
        GitMaterialConfig gitMaterialConfig = new GitMaterialConfig("http://url", "foo");
        gitMaterialConfig.setConfigAttributes(Collections.singletonMap(BRANCH, null));
        Assert.assertThat(gitMaterialConfig.getBranch(), Matchers.is("master"));
    }

    @Test
    public void shouldHandleEmptyBranchWhileSettingConfigAttributes() {
        GitMaterialConfig gitMaterialConfig = new GitMaterialConfig("http://url", "foo");
        gitMaterialConfig.setConfigAttributes(Collections.singletonMap(BRANCH, "     "));
        Assert.assertThat(gitMaterialConfig.getBranch(), Matchers.is("master"));
    }
}

