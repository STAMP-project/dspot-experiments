/**
 * Copyright 2018 ThoughtWorks, Inc.
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


import com.rits.cloning.Cloner;
import com.thoughtworks.go.config.exceptions.PipelineGroupNotEmptyException;
import com.thoughtworks.go.config.preprocessor.ConfigParamPreprocessor;
import com.thoughtworks.go.domain.config.Configuration;
import com.thoughtworks.go.helper.GoConfigMother;
import com.thoughtworks.go.helper.MaterialConfigsMother;
import com.thoughtworks.go.helper.PartialConfigMother;
import com.thoughtworks.go.helper.PipelineConfigMother;
import com.thoughtworks.go.security.CryptoException;
import com.thoughtworks.go.security.GoCipher;
import com.thoughtworks.go.security.ResetCipher;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class BasicCruiseConfigTest extends CruiseConfigTestBase {
    @Rule
    public final ResetCipher resetCipher = new ResetCipher();

    @Test
    public void getAllLocalPipelineConfigs_shouldReturnOnlyLocalPipelinesWhenNoRemotes() {
        PipelineConfig pipeline1 = PipelineConfigMother.createPipelineConfig("local-pipe-1", "stage1");
        cruiseConfig.getGroups().addPipeline("existing_group", pipeline1);
        List<PipelineConfig> localPipelines = cruiseConfig.getAllLocalPipelineConfigs(false);
        Assert.assertThat(localPipelines.size(), is(1));
        Assert.assertThat(localPipelines, hasItem(pipeline1));
    }

    @Test
    public void shouldGenerateAMapOfAllPipelinesAndTheirParentDependencies() {
        /* -----+ p2 --> p4
         p1
           -----+ p3
         */
        PipelineConfig p1 = PipelineConfigMother.createPipelineConfig("p1", "s1", "j1");
        PipelineConfig p2 = PipelineConfigMother.createPipelineConfig("p2", "s2", "j1");
        p2.addMaterialConfig(new com.thoughtworks.go.config.materials.dependency.DependencyMaterialConfig(new CaseInsensitiveString("p1"), new CaseInsensitiveString("s1")));
        PipelineConfig p3 = PipelineConfigMother.createPipelineConfig("p3", "s3", "j1");
        p3.addMaterialConfig(new com.thoughtworks.go.config.materials.dependency.DependencyMaterialConfig(new CaseInsensitiveString("p1"), new CaseInsensitiveString("s1")));
        PipelineConfig p4 = PipelineConfigMother.createPipelineConfig("p4", "s4", "j1");
        p4.addMaterialConfig(new com.thoughtworks.go.config.materials.dependency.DependencyMaterialConfig(new CaseInsensitiveString("p2"), new CaseInsensitiveString("s2")));
        pipelines.addAll(Arrays.asList(p4, p2, p1, p3));
        Map<CaseInsensitiveString, List<PipelineConfig>> expectedPipelines = cruiseConfig.generatePipelineVsDownstreamMap();
        Assert.assertThat(expectedPipelines.size(), is(4));
        Assert.assertThat(expectedPipelines.get(new CaseInsensitiveString("p1")), hasItems(p2, p3));
        Assert.assertThat(expectedPipelines.get(new CaseInsensitiveString("p2")), hasItems(p4));
        Assert.assertThat(expectedPipelines.get(new CaseInsensitiveString("p3")).isEmpty(), is(true));
        Assert.assertThat(expectedPipelines.get(new CaseInsensitiveString("p4")).isEmpty(), is(true));
    }

    @Test
    public void shouldSetOriginInPipelines() {
        pipelines = new BasicPipelineConfigs("group_main", new Authorization(), PipelineConfigMother.pipelineConfig("pipe1"));
        BasicCruiseConfig mainCruiseConfig = new BasicCruiseConfig(pipelines);
        PipelineConfig pipe = pipelines.get(0);
        mainCruiseConfig.setOrigins(new FileConfigOrigin());
        Assert.assertThat(pipe.getOrigin(), is(new FileConfigOrigin()));
    }

    @Test
    public void shouldSetOriginInEnvironments() {
        BasicCruiseConfig mainCruiseConfig = new BasicCruiseConfig(pipelines);
        BasicEnvironmentConfig env = new BasicEnvironmentConfig(new CaseInsensitiveString("e"));
        mainCruiseConfig.addEnvironment(env);
        mainCruiseConfig.setOrigins(new FileConfigOrigin());
        Assert.assertThat(env.getOrigin(), is(new FileConfigOrigin()));
    }

    @Test
    public void shouldGetPipelinesWithGroupName() throws Exception {
        PipelineConfigs group1 = PipelineConfigMother.createGroup("group1", PipelineConfigMother.createPipelineConfig("pipeline1", "stage1"));
        PipelineConfigs group2 = PipelineConfigMother.createGroup("group2", PipelineConfigMother.createPipelineConfig("pipeline2", "stage2"));
        CruiseConfig config = createCruiseConfig();
        config.setGroup(new com.thoughtworks.go.domain.PipelineGroups(group1, group2));
        Assert.assertThat(config.pipelines("group1"), is(group1));
        Assert.assertThat(config.pipelines("group2"), is(group2));
    }

    @Test
    public void shouldReturnTrueForPipelineThatInFirstGroup() {
        PipelineConfigs group1 = PipelineConfigMother.createGroup("group1", PipelineConfigMother.createPipelineConfig("pipeline1", "stage1"));
        CruiseConfig config = new BasicCruiseConfig(group1);
        Assert.assertThat("shouldReturnTrueForPipelineThatInFirstGroup", config.isInFirstGroup(new CaseInsensitiveString("pipeline1")), is(true));
    }

    @Test
    public void shouldReturnFalseForPipelineThatNotInFirstGroup() {
        PipelineConfigs group1 = PipelineConfigMother.createGroup("group1", PipelineConfigMother.createPipelineConfig("pipeline1", "stage1"));
        PipelineConfigs group2 = PipelineConfigMother.createGroup("group2", PipelineConfigMother.createPipelineConfig("pipeline2", "stage2"));
        CruiseConfig config = new BasicCruiseConfig(group1, group2);
        Assert.assertThat("shouldReturnFalseForPipelineThatNotInFirstGroup", config.isInFirstGroup(new CaseInsensitiveString("pipeline2")), is(false));
    }

    @Test
    public void shouldIncludeRemotePipelinesAsPartOfCachedPipelineConfigs() {
        BasicCruiseConfig cruiseConfig = GoConfigMother.configWithPipelines("p1", "p2");
        ConfigRepoConfig repoConfig1 = new ConfigRepoConfig(MaterialConfigsMother.gitMaterialConfig("url1"), "plugin");
        ConfigRepoConfig repoConfig2 = new ConfigRepoConfig(MaterialConfigsMother.gitMaterialConfig("url2"), "plugin");
        cruiseConfig.setConfigRepos(new ConfigReposConfig(repoConfig1, repoConfig2));
        PartialConfig partialConfigInRepo1 = PartialConfigMother.withPipeline("pipeline_in_repo1", new RepoConfigOrigin(repoConfig1, "repo1_r1"));
        PartialConfig partialConfigInRepo2 = PartialConfigMother.withPipeline("pipeline_in_repo2", new RepoConfigOrigin(repoConfig2, "repo2_r1"));
        cruiseConfig.merge(Arrays.asList(partialConfigInRepo1, partialConfigInRepo2), false);
        Assert.assertThat(cruiseConfig.getAllPipelineNames().contains(new CaseInsensitiveString("pipeline_in_repo1")), is(true));
        Assert.assertThat(cruiseConfig.getAllPipelineNames().contains(new CaseInsensitiveString("pipeline_in_repo2")), is(true));
    }

    @Test
    public void shouldRejectRemotePipelinesNotOriginatingFromRegisteredConfigReposFromCachedPipelineConfigs() {
        BasicCruiseConfig cruiseConfig = GoConfigMother.configWithPipelines("p1", "p2");
        ConfigRepoConfig repoConfig1 = new ConfigRepoConfig(MaterialConfigsMother.gitMaterialConfig("url1"), "plugin");
        ConfigRepoConfig repoConfig2 = new ConfigRepoConfig(MaterialConfigsMother.gitMaterialConfig("url2"), "plugin");
        cruiseConfig.setConfigRepos(new ConfigReposConfig(repoConfig2));
        PartialConfig partialConfigInRepo1 = PartialConfigMother.withPipeline("pipeline_in_repo1", new RepoConfigOrigin(repoConfig1, "repo1_r1"));
        PartialConfig partialConfigInRepo2 = PartialConfigMother.withPipeline("pipeline_in_repo2", new RepoConfigOrigin(repoConfig2, "repo2_r1"));
        cruiseConfig.merge(Arrays.asList(partialConfigInRepo1, partialConfigInRepo2), false);
        Assert.assertThat(cruiseConfig.getAllPipelineNames().contains(new CaseInsensitiveString("pipeline_in_repo1")), is(false));
        Assert.assertThat(cruiseConfig.getAllPipelineNames().contains(new CaseInsensitiveString("pipeline_in_repo2")), is(true));
    }

    @Test
    public void shouldReturnAListOfPipelineNamesAssociatedWithOneTemplate() {
        ArrayList<CaseInsensitiveString> pipelinesAssociatedWithATemplate = new ArrayList<>();
        pipelinesAssociatedWithATemplate.add(new CaseInsensitiveString("p1"));
        BasicCruiseConfig cruiseConfig = GoConfigMother.defaultCruiseConfig();
        new GoConfigMother().addPipelineWithTemplate(cruiseConfig, "p1", "t1", "s1", "j1");
        Assert.assertThat(cruiseConfig.pipelinesAssociatedWithTemplate(new CaseInsensitiveString("t1")), is(pipelinesAssociatedWithATemplate));
    }

    @Test
    public void shouldReturnNullForAssociatedPipelineNamesWhenTemplateNameIsBlank() {
        ArrayList<CaseInsensitiveString> pipelinesAssociatedWithATemplate = new ArrayList<>();
        pipelinesAssociatedWithATemplate.add(new CaseInsensitiveString("p1"));
        BasicCruiseConfig cruiseConfig = GoConfigMother.defaultCruiseConfig();
        new GoConfigMother().addPipelineWithTemplate(cruiseConfig, "p1", "t1", "s1", "j1");
        Assert.assertThat(cruiseConfig.pipelinesAssociatedWithTemplate(new CaseInsensitiveString("")), is(new ArrayList<CaseInsensitiveString>()));
    }

    @Test
    public void shouldReturnAnEmptyListForPipelinesIfTemplateNameIsNull() {
        BasicCruiseConfig cruiseConfig = GoConfigMother.defaultCruiseConfig();
        Assert.assertThat(cruiseConfig.pipelinesAssociatedWithTemplate(null).isEmpty(), is(true));
    }

    @Test
    public void shouldReturnAnEmptyListIfThereAreNoPipelinesAssociatedWithGivenTemplate() {
        BasicCruiseConfig cruiseConfig = GoConfigMother.defaultCruiseConfig();
        Assert.assertThat(cruiseConfig.pipelinesAssociatedWithTemplate(new CaseInsensitiveString("non-existent-template")).isEmpty(), is(true));
    }

    @Test
    public void shouldGetAllGroupsForAnAdminUser() {
        BasicCruiseConfig cruiseConfig = GoConfigMother.defaultCruiseConfig();
        new GoConfigMother().addPipelineWithGroup(cruiseConfig, "group", "p1", "s1", "j1");
        GoConfigMother.enableSecurityWithPasswordFilePlugin(cruiseConfig);
        GoConfigMother.addUserAsSuperAdmin(cruiseConfig, "superadmin");
        List<String> groupsForUser = cruiseConfig.getGroupsForUser(new CaseInsensitiveString("superadmin"), new ArrayList());
        Assert.assertThat(groupsForUser, contains("group"));
    }

    @Test
    public void shouldGetAllGroupsForUserInAnAdminRole() {
        GoConfigMother goConfigMother = new GoConfigMother();
        BasicCruiseConfig cruiseConfig = GoConfigMother.defaultCruiseConfig();
        goConfigMother.addPipelineWithGroup(cruiseConfig, "group", "p1", "s1", "j1");
        GoConfigMother.enableSecurityWithPasswordFilePlugin(cruiseConfig);
        Role role = goConfigMother.createRole("role1", "foo", "bar");
        cruiseConfig.server().security().addRole(role);
        goConfigMother.addRoleAsSuperAdmin(cruiseConfig, "role1");
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(role);
        List<String> groupsForUser = cruiseConfig.getGroupsForUser(new CaseInsensitiveString("foo"), roles);
        Assert.assertThat(groupsForUser, contains("group"));
    }

    @Test
    public void shouldGetSpecificGroupsForAGroupAdminUser() {
        GoConfigMother goConfigMother = new GoConfigMother();
        BasicCruiseConfig cruiseConfig = GoConfigMother.defaultCruiseConfig();
        GoConfigMother.enableSecurityWithPasswordFilePlugin(cruiseConfig);
        GoConfigMother.addUserAsSuperAdmin(cruiseConfig, "superadmin");
        goConfigMother.addPipelineWithGroup(cruiseConfig, "group1", "p1", "s1", "j1");
        goConfigMother.addPipelineWithGroup(cruiseConfig, "group2", "p2", "s1", "j1");
        goConfigMother.addPipelineWithGroup(cruiseConfig, "group3", "p3", "s1", "j1");
        goConfigMother.addAdminUserForPipelineGroup(cruiseConfig, "foo", "group1");
        goConfigMother.addAdminUserForPipelineGroup(cruiseConfig, "foo", "group2");
        List<String> groupsForUser = cruiseConfig.getGroupsForUser(new CaseInsensitiveString("foo"), new ArrayList());
        Assert.assertThat(groupsForUser, not(contains("group3")));
        Assert.assertThat(groupsForUser, containsInAnyOrder("group2", "group1"));
    }

    @Test
    public void shouldGetSpecificGroupsForAUserInGroupAdminRole() {
        GoConfigMother goConfigMother = new GoConfigMother();
        BasicCruiseConfig cruiseConfig = GoConfigMother.defaultCruiseConfig();
        GoConfigMother.enableSecurityWithPasswordFilePlugin(cruiseConfig);
        GoConfigMother.addUserAsSuperAdmin(cruiseConfig, "superadmin");
        goConfigMother.addPipelineWithGroup(cruiseConfig, "group1", "p1", "s1", "j1");
        goConfigMother.addPipelineWithGroup(cruiseConfig, "group2", "p2", "s1", "j1");
        goConfigMother.addPipelineWithGroup(cruiseConfig, "group3", "p3", "s1", "j1");
        Role role = goConfigMother.createRole("role1", "foo", "bar");
        cruiseConfig.server().security().addRole(role);
        goConfigMother.addAdminRoleForPipelineGroup(cruiseConfig, "role1", "group1");
        goConfigMother.addAdminRoleForPipelineGroup(cruiseConfig, "role1", "group2");
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(role);
        List<String> groupsForUser = cruiseConfig.getGroupsForUser(new CaseInsensitiveString("foo"), roles);
        Assert.assertThat(groupsForUser, not(contains("group3")));
        Assert.assertThat(groupsForUser, containsInAnyOrder("group2", "group1"));
    }

    @Test
    public void shouldEncryptSecurePluggableArtifactConfigPropertiesOfAllPipelinesInConfig() throws CryptoException, IOException {
        // ancestor => parent => child [fetch pluggable artifact(ancestor), fetch pluggable artifact(parent)]
        resetCipher.setupAESCipherFile();
        BasicCruiseConfig config = setupPipelines();
        BasicCruiseConfig preprocessed = new Cloner().deepClone(config);
        new ConfigParamPreprocessor().process(preprocessed);
        config.encryptSecureProperties(preprocessed);
        PipelineConfig ancestor = config.pipelineConfigByName(new CaseInsensitiveString("ancestor"));
        PipelineConfig child = config.pipelineConfigByName(new CaseInsensitiveString("child"));
        Configuration ancestorPublishArtifactConfig = getConfiguration();
        GoCipher goCipher = new GoCipher();
        Assert.assertThat(ancestorPublishArtifactConfig.getProperty("k1").getEncryptedValue(), is(goCipher.encrypt("pub_v1")));
        Assert.assertThat(ancestorPublishArtifactConfig.getProperty("k1").getConfigValue(), is(nullValue()));
        Assert.assertThat(ancestorPublishArtifactConfig.getProperty("k1").getValue(), is("pub_v1"));
        Assert.assertThat(ancestorPublishArtifactConfig.getProperty("k2").getEncryptedValue(), is(nullValue()));
        Assert.assertThat(ancestorPublishArtifactConfig.getProperty("k2").getConfigValue(), is("pub_v2"));
        Assert.assertThat(ancestorPublishArtifactConfig.getProperty("k2").getValue(), is("pub_v2"));
        Assert.assertThat(ancestorPublishArtifactConfig.getProperty("k3").getEncryptedValue(), is(goCipher.encrypt("pub_v3")));
        Assert.assertThat(ancestorPublishArtifactConfig.getProperty("k3").getConfigValue(), is(nullValue()));
        Assert.assertThat(ancestorPublishArtifactConfig.getProperty("k3").getValue(), is("pub_v3"));
        Configuration childFetchFromAncestorConfig = getConfiguration();
        Assert.assertThat(childFetchFromAncestorConfig.getProperty("k1").getEncryptedValue(), is(goCipher.encrypt("fetch_v1")));
        Assert.assertThat(childFetchFromAncestorConfig.getProperty("k1").getConfigValue(), is(nullValue()));
        Assert.assertThat(childFetchFromAncestorConfig.getProperty("k1").getValue(), is("fetch_v1"));
        Assert.assertThat(childFetchFromAncestorConfig.getProperty("k2").getEncryptedValue(), is(nullValue()));
        Assert.assertThat(childFetchFromAncestorConfig.getProperty("k2").getConfigValue(), is("fetch_v2"));
        Assert.assertThat(childFetchFromAncestorConfig.getProperty("k2").getValue(), is("fetch_v2"));
        Assert.assertThat(childFetchFromAncestorConfig.getProperty("k3").getEncryptedValue(), is(goCipher.encrypt("fetch_v3")));
        Assert.assertThat(childFetchFromAncestorConfig.getProperty("k3").getConfigValue(), is(nullValue()));
        Assert.assertThat(childFetchFromAncestorConfig.getProperty("k3").getValue(), is("fetch_v3"));
        Configuration childFetchFromParentConfig = getConfiguration();
        Assert.assertThat(childFetchFromParentConfig.getProperty("k1").getEncryptedValue(), is(goCipher.encrypt("fetch_v1")));
        Assert.assertThat(childFetchFromParentConfig.getProperty("k1").getConfigValue(), is(nullValue()));
        Assert.assertThat(childFetchFromParentConfig.getProperty("k1").getValue(), is("fetch_v1"));
        Assert.assertThat(childFetchFromParentConfig.getProperty("k2").getEncryptedValue(), is(nullValue()));
        Assert.assertThat(childFetchFromParentConfig.getProperty("k2").getConfigValue(), is("fetch_v2"));
        Assert.assertThat(childFetchFromParentConfig.getProperty("k2").getValue(), is("fetch_v2"));
        Assert.assertThat(childFetchFromParentConfig.getProperty("k3").getEncryptedValue(), is(goCipher.encrypt("fetch_v3")));
        Assert.assertThat(childFetchFromParentConfig.getProperty("k3").getConfigValue(), is(nullValue()));
        Assert.assertThat(childFetchFromParentConfig.getProperty("k3").getValue(), is("fetch_v3"));
    }

    @Test
    public void shouldDeletePipelineGroupWithGroupName() {
        PipelineConfigs group = PipelineConfigMother.createGroup("group", new PipelineConfig[]{  });
        CruiseConfig config = createCruiseConfig();
        config.setGroup(new com.thoughtworks.go.domain.PipelineGroups(group));
        Assert.assertThat(config.getGroups().isEmpty(), is(false));
        config.deletePipelineGroup("group");
        Assert.assertThat(config.getGroups().isEmpty(), is(true));
    }

    @Test(expected = PipelineGroupNotEmptyException.class)
    public void shouldNotDeletePipelineGroupIfNotEmpty() {
        PipelineConfigs group = PipelineConfigMother.createGroup("group", PipelineConfigMother.createPipelineConfig("pipeline", "stage"));
        CruiseConfig config = createCruiseConfig();
        config.setGroup(new com.thoughtworks.go.domain.PipelineGroups(group));
        Assert.assertThat(config.getGroups().isEmpty(), is(false));
        config.deletePipelineGroup("group");
    }
}

