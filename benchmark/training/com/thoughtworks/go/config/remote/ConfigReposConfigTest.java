/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.config.remote;


import com.thoughtworks.go.config.materials.git.GitMaterialConfig;
import com.thoughtworks.go.domain.materials.MaterialConfig;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ConfigReposConfigTest {
    private ConfigReposConfig repos;

    @Test
    public void shouldReturnFalseThatHasMaterialWhenEmpty() {
        Assert.assertThat(repos.isEmpty(), Matchers.is(true));
        Assert.assertThat(repos.hasMaterial(Mockito.mock(MaterialConfig.class)), Matchers.is(false));
    }

    @Test
    public void shouldReturnTrueThatHasMaterialWhenAddedConfigRepo() {
        repos.add(new ConfigRepoConfig(new GitMaterialConfig("http://git"), "myplugin"));
        Assert.assertThat(repos.hasMaterial(new GitMaterialConfig("http://git")), Matchers.is(true));
    }

    @Test
    public void shouldFindConfigRepoWithSpecifiedId() {
        String id = "repo1";
        ConfigRepoConfig configRepo1 = new ConfigRepoConfig(new GitMaterialConfig("http://git"), "myplugin", id);
        repos.add(configRepo1);
        Assert.assertThat(repos.getConfigRepo(id), Matchers.is(configRepo1));
    }

    @Test
    public void shouldFindReturnNullWhenConfigRepoWithSpecifiedIdIsNotPresent() {
        Assert.assertNull(repos.getConfigRepo("repo1"));
    }

    @Test
    public void shouldReturnTrueThatHasConfigRepoWhenAddedConfigRepo() {
        repos.add(new ConfigRepoConfig(new GitMaterialConfig("http://git"), "myplugin", "repo-id"));
        Assert.assertThat(repos.contains(new ConfigRepoConfig(new GitMaterialConfig("http://git"), "myplugin", "repo-id")), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseThatHasConfigRepoWhenEmpty() {
        Assert.assertThat(repos.isEmpty(), Matchers.is(true));
        Assert.assertThat(repos.contains(new ConfigRepoConfig(new GitMaterialConfig("http://git"), "myplugin")), Matchers.is(false));
    }

    @Test
    public void shouldErrorWhenDuplicateReposExist() {
        ConfigRepoConfig repo1 = new ConfigRepoConfig(new GitMaterialConfig("http://git"), "myplugin");
        ConfigRepoConfig repo2 = new ConfigRepoConfig(new GitMaterialConfig("http://git"), "myotherplugin");
        repos.add(repo1);
        repos.add(repo2);
        // this is a limitation, we identify config repos by material fingerprint later
        // so there cannot be one repository parsed by 2 plugins.
        // This also does not seem like practical use case anyway
        repos.validate(null);
        Assert.assertThat(repos.errors().on("material"), Matchers.is("You have defined multiple configuration repositories with the same repository."));
    }

    @Test
    public void shouldErrorWhenDuplicateIdsExist() {
        ConfigRepoConfig repo1 = new ConfigRepoConfig(new GitMaterialConfig("http://git1"), "myplugin", "id");
        ConfigRepoConfig repo2 = new ConfigRepoConfig(new GitMaterialConfig("http://git2"), "myotherplugin", "id");
        repos.add(repo1);
        repos.add(repo2);
        repos.validate(null);
        Assert.assertThat(repos.errors().on("id"), Matchers.is("You have defined multiple configuration repositories with the same id."));
    }

    @Test
    public void shouldNotErrorWhenReposFingerprintDiffer() {
        ConfigRepoConfig repo1 = new ConfigRepoConfig(new GitMaterialConfig("http://git"), "myplugin", "id1");
        ConfigRepoConfig repo2 = new ConfigRepoConfig(new GitMaterialConfig("https://git", "develop"), "myotherplugin", "id2");
        repos.add(repo1);
        repos.add(repo2);
        repos.validate(null);
        Assert.assertThat(repo1.errors().isEmpty(), Matchers.is(true));
        Assert.assertThat(repo2.errors().isEmpty(), Matchers.is(true));
    }
}

