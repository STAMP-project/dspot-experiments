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
import com.thoughtworks.go.config.materials.git.GitMaterialConfig;
import com.thoughtworks.go.config.merge.MergePipelineConfigs;
import com.thoughtworks.go.domain.ConfigErrors;
import com.thoughtworks.go.domain.config.Configuration;
import com.thoughtworks.go.domain.config.PluginConfiguration;
import com.thoughtworks.go.domain.materials.MaterialConfig;
import com.thoughtworks.go.helper.EnvironmentConfigMother;
import com.thoughtworks.go.helper.GoConfigMother;
import com.thoughtworks.go.helper.MaterialConfigsMother;
import com.thoughtworks.go.helper.PartialConfigMother;
import com.thoughtworks.go.helper.PipelineConfigMother;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class MergeCruiseConfigTest extends CruiseConfigTestBase {
    @Test
    public void shouldMergeWhenSameEnvironmentExistsInManyPartials() {
        BasicCruiseConfig cruiseConfig = GoConfigMother.configWithPipelines("p1", "p2");
        ConfigRepoConfig repoConfig1 = new ConfigRepoConfig(MaterialConfigsMother.gitMaterialConfig("url1"), "plugin");
        ConfigRepoConfig repoConfig2 = new ConfigRepoConfig(MaterialConfigsMother.gitMaterialConfig("url2"), "plugin");
        cruiseConfig.setConfigRepos(new ConfigReposConfig(repoConfig1, repoConfig2));
        PartialConfig partialConfigInRepo1 = PartialConfigMother.withEnvironment("environment", new RepoConfigOrigin(repoConfig1, "repo1_r1"));
        RepoConfigOrigin configOrigin = new RepoConfigOrigin(repoConfig2, "repo2_r1");
        PartialConfig partialConfigInRepo2 = PartialConfigMother.withEnvironment("environment", configOrigin);
        BasicEnvironmentConfig environment2InRepo2 = EnvironmentConfigMother.environment("environment2_in_repo2");
        environment2InRepo2.setOrigins(configOrigin);
        partialConfigInRepo2.getEnvironments().add(environment2InRepo2);
        cruiseConfig.merge(new java.util.ArrayList(Arrays.asList(partialConfigInRepo2, partialConfigInRepo1)), false);
        Assert.assertThat(cruiseConfig.getEnvironments().hasEnvironmentNamed(new CaseInsensitiveString("environment")), is(true));
        Assert.assertThat(cruiseConfig.getEnvironments().hasEnvironmentNamed(new CaseInsensitiveString("environment2_in_repo2")), is(true));
    }

    @Test
    public void shouldHaveValidationErrorsForDuplicateValidSCMs() {
        BasicCruiseConfig cruiseConfig = GoConfigMother.configWithPipelines("p1", "p2");
        ConfigRepoConfig repoConfig1 = new ConfigRepoConfig(MaterialConfigsMother.gitMaterialConfig("url1"), "plugin");
        ConfigRepoConfig repoConfig2 = new ConfigRepoConfig(MaterialConfigsMother.gitMaterialConfig("url2"), "plugin");
        cruiseConfig.setConfigRepos(new ConfigReposConfig(repoConfig1, repoConfig2));
        PartialConfig partialConfigInRepo1 = PartialConfigMother.withSCM("scmid", "name", new PluginConfiguration("plugin_id", "1"), new Configuration(), new FileConfigOrigin());
        RepoConfigOrigin configOrigin = new RepoConfigOrigin(repoConfig2, "repo2_r1");
        PartialConfig partialConfigInRepo2 = PartialConfigMother.withSCM("scmid", "name", new PluginConfiguration("plugin_id", "1"), new Configuration(), configOrigin);
        cruiseConfig.merge(new java.util.ArrayList(Arrays.asList(partialConfigInRepo2, partialConfigInRepo1)), false);
        Assert.assertThat(cruiseConfig.getSCMs().size(), is(2));
        Assert.assertThat(cruiseConfig.validateAfterPreprocess().size(), is(2));
    }

    @Test
    public void shouldOnlyMergeLocalSCMsWhenEditIsTrue() {
        BasicCruiseConfig cruiseConfig = GoConfigMother.configWithPipelines("p1", "p2");
        ConfigRepoConfig repoConfig1 = new ConfigRepoConfig(MaterialConfigsMother.gitMaterialConfig("url1"), "plugin");
        cruiseConfig.setConfigRepos(new ConfigReposConfig(repoConfig1));
        RepoConfigOrigin configOrigin = new RepoConfigOrigin(repoConfig1, "repo1_r1");
        PartialConfig completeSCM = PartialConfigMother.withSCM("scmid", "name", new PluginConfiguration("plugin_id", "1"), new Configuration(), configOrigin);
        PartialConfig localSCM = PartialConfigMother.withSCM("local_id", "local", new PluginConfiguration("plugin_id2", "1"), new Configuration(), new FileConfigOrigin());
        cruiseConfig.merge(new java.util.ArrayList(Arrays.asList(localSCM, completeSCM)), true);
        Assert.assertThat(cruiseConfig.getSCMs().size(), is(1));
        Assert.assertThat(cruiseConfig.getSCMs().contains(localSCM.getScms().first()), is(true));
    }

    @Test
    public void mergeShouldThrowWhenCalledSecondTime() {
        cruiseConfig = new BasicCruiseConfig(new BasicCruiseConfig(pipelines), PartialConfigMother.withEnvironment("remote-env"));
        Assert.assertThat(cruiseConfig.getEnvironments().size(), is(1));
        try {
            cruiseConfig.merge(Arrays.asList(PartialConfigMother.withEnvironment("remote-env")), false);
        } catch (RuntimeException ex) {
            // ok
            Assert.assertThat(cruiseConfig.getEnvironments().size(), is(1));
            cruiseConfig.validateAfterPreprocess();
            return;
        }
        Assert.fail("should have thrown");
    }

    @Test
    public void shouldReturnRemoteOriginOfTheGroup() {
        Assert.assertThat(cruiseConfig.findGroup("remote_group").getOrigin(), is(PartialConfigMother.createRepoOrigin()));
    }

    @Test
    public void getAllLocalPipelineConfigs_shouldReturnOnlyLocalPipelinesWhenRemoteExist() {
        PipelineConfig pipeline1 = PipelineConfigMother.createPipelineConfig("local-pipe-1", "stage1");
        cruiseConfig.getGroups().addPipeline("existing_group", pipeline1);
        List<PipelineConfig> localPipelines = cruiseConfig.getAllLocalPipelineConfigs(false);
        Assert.assertThat(localPipelines.size(), is(1));
        Assert.assertThat(localPipelines, hasItem(pipeline1));
    }

    @Test
    public void getAllLocalPipelineConfigs_shouldReturnEmptyListWhenNoLocalPipelines() {
        List<PipelineConfig> localPipelines = cruiseConfig.getAllLocalPipelineConfigs(false);
        Assert.assertThat(localPipelines.size(), is(0));
    }

    @Test
    public void getAllLocalPipelineConfigs_shouldExcludePipelinesReferencedByRemoteEnvironmentWhenRequested() {
        pipelines = new BasicPipelineConfigs("group_main", new Authorization(), PipelineConfigMother.pipelineConfig("local-pipeline-1"));
        cruiseConfig = new BasicCruiseConfig(pipelines);
        ConfigReposConfig reposConfig = new ConfigReposConfig();
        ConfigRepoConfig configRepoConfig = new ConfigRepoConfig(new GitMaterialConfig("http://git"), "myplug");
        reposConfig.add(configRepoConfig);
        cruiseConfig.setConfigRepos(reposConfig);
        PartialConfig partialConfig = PartialConfigMother.withPipelineInGroup("remote-pipeline-1", "g2");
        BasicEnvironmentConfig remoteEnvironment = new BasicEnvironmentConfig(new CaseInsensitiveString("UAT"));
        remoteEnvironment.setOrigins(new RepoConfigOrigin());
        // remote environment declares a local pipeline as member
        remoteEnvironment.addPipeline(new CaseInsensitiveString("local-pipeline-1"));
        partialConfig.getEnvironments().add(remoteEnvironment);
        partialConfig.setOrigins(new RepoConfigOrigin(configRepoConfig, "123"));
        cruiseConfig.merge(Arrays.asList(partialConfig), true);
        Assert.assertThat(cruiseConfig.hasPipelineNamed(new CaseInsensitiveString("local-pipeline-1")), is(true));
        List<PipelineConfig> localPipelines = cruiseConfig.getAllLocalPipelineConfigs(true);
        Assert.assertThat(localPipelines.size(), is(0));
    }

    @Test
    public void shouldGetPipelinesWithGroupName() throws Exception {
        PipelineConfig pipeline1 = PipelineConfigMother.createPipelineConfig("pipeline1", "stage1");
        cruiseConfig.getGroups().addPipeline("existing_group", pipeline1);
        Assert.assertThat(cruiseConfig.pipelines("existing_group"), hasItem(pipeline1));
        Assert.assertThat(cruiseConfig.pipelines("remote_group").hasPipeline(new CaseInsensitiveString("remote-pipe-1")), is(true));
    }

    @Test
    public void shouldReturnTrueForPipelineThatInFirstGroup_WhenFirstGroupIsLocal() {
        PipelineConfigs group1 = PipelineConfigMother.createGroup("group1", PipelineConfigMother.createPipelineConfig("pipeline1", "stage1"));
        CruiseConfig config = new BasicCruiseConfig(new BasicCruiseConfig(group1), new PartialConfig());
        Assert.assertThat("shouldReturnTrueForPipelineThatInFirstGroup", config.isInFirstGroup(new CaseInsensitiveString("pipeline1")), is(true));
    }

    @Test
    public void shouldReturnTrueForPipelineThatInFirstGroup_WhenFirstGroupIsRemote() {
        CruiseConfig config = new BasicCruiseConfig(new BasicCruiseConfig(), PartialConfigMother.withPipelineInGroup("remote-pipe-1", "remote_group"));
        Assert.assertThat("shouldReturnTrueForPipelineThatInFirstGroup", config.isInFirstGroup(new CaseInsensitiveString("remote-pipe-1")), is(true));
    }

    @Test
    public void shouldReturnFalseForPipelineThatNotInFirstGroup_WhenSecondGroupIsLocal() {
        PipelineConfigs group1 = PipelineConfigMother.createGroup("group1", PipelineConfigMother.createPipelineConfig("pipeline1", "stage1"));
        PipelineConfigs group2 = PipelineConfigMother.createGroup("group2", PipelineConfigMother.createPipelineConfig("pipeline2", "stage2"));
        CruiseConfig config = new BasicCruiseConfig(new BasicCruiseConfig(group1, group2), new PartialConfig());
        Assert.assertThat("shouldReturnFalseForPipelineThatNotInFirstGroup", config.isInFirstGroup(new CaseInsensitiveString("pipeline2")), is(false));
    }

    @Test
    public void shouldReturnFalseForPipelineThatNotInFirstGroup_WhenSecondGroupIsRemote() {
        PipelineConfigs group1 = PipelineConfigMother.createGroup("group1", PipelineConfigMother.createPipelineConfig("pipeline1", "stage1"));
        CruiseConfig config = new BasicCruiseConfig(new BasicCruiseConfig(group1), PartialConfigMother.withPipelineInGroup("remote-pipe-1", "remote_group"));
        Assert.assertThat("shouldReturnFalseForPipelineThatNotInFirstGroup", config.isInFirstGroup(new CaseInsensitiveString("pipeline2")), is(false));
    }

    @Test
    public void shouldGenerateAMapOfAllPipelinesAndTheirParentDependencies_WhenAllPipelinesInMapAreLocal() {
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
        cruiseConfig = new BasicCruiseConfig(new BasicCruiseConfig(pipelines), PartialConfigMother.withPipelineInGroup("remote-pipe-1", "remote_group"));
        Map<CaseInsensitiveString, List<PipelineConfig>> expectedPipelines = cruiseConfig.generatePipelineVsDownstreamMap();
        Assert.assertThat(expectedPipelines.size(), is(5));
        Assert.assertThat(expectedPipelines.get(new CaseInsensitiveString("p1")), hasItems(p2, p3));
        Assert.assertThat(expectedPipelines.get(new CaseInsensitiveString("p2")), hasItems(p4));
        Assert.assertThat(expectedPipelines.get(new CaseInsensitiveString("p3")).isEmpty(), is(true));
        Assert.assertThat(expectedPipelines.get(new CaseInsensitiveString("p4")).isEmpty(), is(true));
        Assert.assertThat(expectedPipelines.get(new CaseInsensitiveString("remote-pipe-1")).isEmpty(), is(true));
    }

    @Test
    public void shouldGenerateAMapOfAllPipelinesAndTheirParentDependencies_WhenThereAreRemotePipelinesInMap() {
        /* -----+ p2 --> p4
         p1
           -----+ p3 --> remote-pipe-1
         */
        PipelineConfig p1 = PipelineConfigMother.createPipelineConfig("p1", "s1", "j1");
        PipelineConfig p2 = PipelineConfigMother.createPipelineConfig("p2", "s2", "j1");
        p2.addMaterialConfig(new com.thoughtworks.go.config.materials.dependency.DependencyMaterialConfig(new CaseInsensitiveString("p1"), new CaseInsensitiveString("s1")));
        PipelineConfig p3 = PipelineConfigMother.createPipelineConfig("p3", "s3", "j1");
        p3.addMaterialConfig(new com.thoughtworks.go.config.materials.dependency.DependencyMaterialConfig(new CaseInsensitiveString("p1"), new CaseInsensitiveString("s1")));
        PipelineConfig p4 = PipelineConfigMother.createPipelineConfig("p4", "s4", "j1");
        p4.addMaterialConfig(new com.thoughtworks.go.config.materials.dependency.DependencyMaterialConfig(new CaseInsensitiveString("p2"), new CaseInsensitiveString("s2")));
        pipelines.addAll(Arrays.asList(p4, p2, p1, p3));
        PipelineConfig remotePipe1 = PipelineConfigMother.createPipelineConfig("remote-pipe-1", "s5", "j1");
        remotePipe1.addMaterialConfig(new com.thoughtworks.go.config.materials.dependency.DependencyMaterialConfig(new CaseInsensitiveString("p3"), new CaseInsensitiveString("s3")));
        PartialConfig part = new PartialConfig();
        part.getGroups().addPipeline("remoteGroup", remotePipe1);
        cruiseConfig = new BasicCruiseConfig(new BasicCruiseConfig(pipelines), part);
        Map<CaseInsensitiveString, List<PipelineConfig>> expectedPipelines = cruiseConfig.generatePipelineVsDownstreamMap();
        Assert.assertThat(expectedPipelines.size(), is(5));
        Assert.assertThat(expectedPipelines.get(new CaseInsensitiveString("p1")), hasItems(p2, p3));
        Assert.assertThat(expectedPipelines.get(new CaseInsensitiveString("p2")), hasItems(p4));
        Assert.assertThat(expectedPipelines.get(new CaseInsensitiveString("p3")), hasItems(remotePipe1));
        Assert.assertThat(expectedPipelines.get(new CaseInsensitiveString("remote-pipe-1")).isEmpty(), is(true));
        Assert.assertThat(expectedPipelines.get(new CaseInsensitiveString("p4")).isEmpty(), is(true));
    }

    @Test
    public void shouldCollectOriginErrorsFromEnvironments_InMergedConfig() {
        pipelines = new BasicPipelineConfigs("group_main", new Authorization(), PipelineConfigMother.pipelineConfig("pipe1"));
        BasicCruiseConfig mainCruiseConfig = new BasicCruiseConfig(pipelines);
        PartialConfig partialConfig = PartialConfigMother.withPipelineInGroup("pipe2", "g2");
        partialConfig.getGroups().get(0).get(0).setOrigin(new RepoConfigOrigin());
        cruiseConfig = new BasicCruiseConfig(mainCruiseConfig, partialConfig);
        BasicEnvironmentConfig uat = new BasicEnvironmentConfig(new CaseInsensitiveString("UAT"));
        uat.addPipeline(new CaseInsensitiveString("pipe2"));
        cruiseConfig.addEnvironment(uat);
        List<ConfigErrors> allErrors = cruiseConfig.validateAfterPreprocess();
        Assert.assertThat(allErrors.size(), is(1));
        Assert.assertNotNull(allErrors.get(0).on("origin"));
    }

    @Test
    public void shouldCollectOriginErrorsFromMaterialConfigs_InMergedConfig() {
        BasicCruiseConfig mainCruiseConfig = new BasicCruiseConfig(pipelines);
        PartialConfig partialConfig = PartialConfigMother.withPipelineInGroup("pipe2", "g2");
        partialConfig.getGroups().get(0).get(0).setOrigin(new RepoConfigOrigin());
        cruiseConfig = new BasicCruiseConfig(mainCruiseConfig, partialConfig);
        PipelineConfig pipeline1 = goConfigMother.addPipeline(cruiseConfig, "pipeline1", "stage", "build");
        PipelineConfig pipeline2 = PipelineConfigMother.createPipelineConfigWithStage("pipeline2", "stage");
        pipeline2.setOrigin(new RepoConfigOrigin());
        partialConfig.getGroups().addPipeline("g2", pipeline2);
        goConfigMother.setDependencyOn(cruiseConfig, pipeline1, "pipeline2", "stage");
        List<ConfigErrors> allErrors = cruiseConfig.validateAfterPreprocess();
        Assert.assertThat(allErrors.size(), is(1));
        Assert.assertNotNull(allErrors.get(0).on("origin"));
    }

    @Test
    public void shouldCollectAllTheErrorsInTheChildren_InMergedConfig() {
        BasicCruiseConfig mainCruiseConfig = GoConfigMother.configWithPipelines("pipeline-1");
        PartialConfig partialConfig = PartialConfigMother.withPipelineInGroup("pipe2", "g2");
        partialConfig.getGroups().get(0).get(0).setOrigin(new RepoConfigOrigin());
        CruiseConfig config = new BasicCruiseConfig(mainCruiseConfig, partialConfig);
        shouldCollectAllTheErrorsInTheChildrenHelper(config);
    }

    @Test
    public void shouldCollectPipelineNameConflictErrorsInTheChildren_InMergedConfig_WhenPipelinesIn2Groups() {
        BasicCruiseConfig mainCruiseConfig = GoConfigMother.configWithPipelines("pipeline-1");
        PartialConfig partialConfig = PartialConfigMother.withPipelineInGroup("pipeline-1", "g2");
        partialConfig.setOrigin(new RepoConfigOrigin());
        CruiseConfig config = new BasicCruiseConfig(mainCruiseConfig, partialConfig);
        List<ConfigErrors> allErrors = config.validateAfterPreprocess();
        Assert.assertThat(allErrors.size(), is(2));
        Assert.assertThat(allErrors.get(0).on("name"), is("You have defined multiple pipelines named 'pipeline-1'. Pipeline names must be unique. Source(s): [http://some.git at 1234fed, cruise-config.xml]"));
        Assert.assertThat(allErrors.get(1).on("name"), is("You have defined multiple pipelines named 'pipeline-1'. Pipeline names must be unique. Source(s): [http://some.git at 1234fed, cruise-config.xml]"));
    }

    @Test
    public void shouldCollectPipelineNameConflictErrorsInTheChildren_InMergedConfig2_WhenPipelinesInDefaultGroup() {
        BasicCruiseConfig cruiseConfig = GoConfigMother.configWithPipelines("pipeline1");
        // pipeline1 is in xml and in config repo - this is an error at merged scope
        PartialConfig remotePart = PartialConfigMother.withPipelineInGroup("pipeline1", "defaultGroup");
        remotePart.setOrigin(new RepoConfigOrigin());
        BasicCruiseConfig merged = new BasicCruiseConfig(cruiseConfig, remotePart);
        List<ConfigErrors> allErrors = merged.validateAfterPreprocess();
        Assert.assertThat(remotePart.getGroups().get(0).getPipelines().get(0).errors().size(), is(1));
        Assert.assertThat(allErrors.size(), is(2));
        Assert.assertThat(allErrors.get(0).on("name"), is("You have defined multiple pipelines named 'pipeline1'. Pipeline names must be unique. Source(s): [http://some.git at 1234fed, cruise-config.xml]"));
        Assert.assertThat(allErrors.get(1).on("name"), is("You have defined multiple pipelines named 'pipeline1'. Pipeline names must be unique. Source(s): [http://some.git at 1234fed, cruise-config.xml]"));
    }

    @Test
    public void shouldCollectPipelineNameConflictErrorsInTheChildren_InMergedConfig_WhenCloned() {
        // we need this case because cloning has proven to be problematic with complex object graph in merged config
        BasicCruiseConfig mainCruiseConfig = GoConfigMother.configWithPipelines("pipeline-1");
        PartialConfig partialConfig = PartialConfigMother.withPipelineInGroup("pipeline-1", "g2");
        partialConfig.setOrigin(new RepoConfigOrigin());
        CruiseConfig config = new BasicCruiseConfig(mainCruiseConfig, partialConfig);
        Cloner CLONER = new Cloner();
        CruiseConfig cloned = CLONER.deepClone(config);
        List<ConfigErrors> allErrors = cloned.validateAfterPreprocess();
        Assert.assertThat(allErrors.size(), is(2));
        Assert.assertThat(allErrors.get(0).on("name"), is("You have defined multiple pipelines named 'pipeline-1'. Pipeline names must be unique. Source(s): [http://some.git at 1234fed, cruise-config.xml]"));
        Assert.assertThat(allErrors.get(1).on("name"), is("You have defined multiple pipelines named 'pipeline-1'. Pipeline names must be unique. Source(s): [http://some.git at 1234fed, cruise-config.xml]"));
    }

    @Test
    public void shouldReturnGroupsOtherThanMain_WhenMerged() {
        BasicCruiseConfig mainCruiseConfig = new BasicCruiseConfig(pipelines);
        cruiseConfig = new BasicCruiseConfig(mainCruiseConfig, PartialConfigMother.withPipeline("pipe2"));
        Assert.assertNotSame(mainCruiseConfig.getGroups(), cruiseConfig.getGroups());
    }

    @Test
    public void shouldReturnTrueHasPipelinesFrom2Parts() {
        pipelines = new BasicPipelineConfigs("group_main", new Authorization(), PipelineConfigMother.pipelineConfig("pipe1"));
        BasicCruiseConfig mainCruiseConfig = new BasicCruiseConfig(pipelines);
        cruiseConfig = new BasicCruiseConfig(mainCruiseConfig, PartialConfigMother.withPipeline("pipe2"));
        Assert.assertThat(cruiseConfig.hasPipelineNamed(new CaseInsensitiveString("pipe1")), is(true));
        Assert.assertThat(cruiseConfig.hasPipelineNamed(new CaseInsensitiveString("pipe2")), is(true));
    }

    @Test
    public void shouldReturnFalseWhenHasNotPipelinesFrom2Parts() {
        pipelines = new BasicPipelineConfigs("group_main", new Authorization(), PipelineConfigMother.pipelineConfig("pipe1"));
        BasicCruiseConfig mainCruiseConfig = new BasicCruiseConfig(pipelines);
        cruiseConfig = new BasicCruiseConfig(mainCruiseConfig, PartialConfigMother.withPipeline("pipe2"));
        Assert.assertThat(cruiseConfig.hasPipelineNamed(new CaseInsensitiveString("pipe3")), is(false));
    }

    @Test
    public void shouldReturnGroupsFrom2Parts() {
        pipelines = new BasicPipelineConfigs("group_main", new Authorization(), PipelineConfigMother.pipelineConfig("pipe1"));
        BasicCruiseConfig mainCruiseConfig = new BasicCruiseConfig(pipelines);
        cruiseConfig = new BasicCruiseConfig(mainCruiseConfig, PartialConfigMother.withPipelineInGroup("pipe2", "g2"));
        Assert.assertThat(cruiseConfig.hasPipelineGroup("g2"), is(true));
    }

    @Test
    public void addPipeline_shouldAddPipelineToMain() {
        pipelines = new BasicPipelineConfigs("group_main", new Authorization(), PipelineConfigMother.pipelineConfig("pipe1"));
        pipelines.setOrigin(new FileConfigOrigin());
        BasicCruiseConfig mainCruiseConfig = new BasicCruiseConfig(pipelines);
        cruiseConfig = new BasicCruiseConfig(mainCruiseConfig, PartialConfigMother.withPipeline("pipe2"));
        cruiseConfig.addPipeline("group_main", PipelineConfigMother.pipelineConfig("pipe3"));
        Assert.assertThat(mainCruiseConfig.hasPipelineNamed(new CaseInsensitiveString("pipe3")), is(true));
        Assert.assertThat(cruiseConfig.hasPipelineNamed(new CaseInsensitiveString("pipe3")), is(true));
    }

    @Test
    public void addPipelineWithoutValidation_shouldAddPipelineToMain() {
        pipelines = new BasicPipelineConfigs("group_main", new Authorization(), PipelineConfigMother.pipelineConfig("pipe1"));
        pipelines.setOrigin(new FileConfigOrigin());
        BasicCruiseConfig mainCruiseConfig = new BasicCruiseConfig(pipelines);
        cruiseConfig = new BasicCruiseConfig(mainCruiseConfig, PartialConfigMother.withPipeline("pipe2"));
        cruiseConfig.addPipelineWithoutValidation("group_main", PipelineConfigMother.pipelineConfig("pipe3"));
        Assert.assertThat(mainCruiseConfig.hasPipelineNamed(new CaseInsensitiveString("pipe3")), is(true));
        Assert.assertThat(cruiseConfig.hasPipelineNamed(new CaseInsensitiveString("pipe3")), is(true));
    }

    @Test
    public void shouldgetAllPipelineNamesFromAllParts() {
        pipelines = new BasicPipelineConfigs("group_main", new Authorization(), PipelineConfigMother.pipelineConfig("pipe1"));
        BasicCruiseConfig mainCruiseConfig = new BasicCruiseConfig(pipelines);
        cruiseConfig = new BasicCruiseConfig(mainCruiseConfig, PartialConfigMother.withPipelineInGroup("pipe2", "g2"), PartialConfigMother.withPipelineInGroup("pipe3", "g3"));
        Assert.assertThat(cruiseConfig.getAllPipelineNames(), contains(new CaseInsensitiveString("pipe1"), new CaseInsensitiveString("pipe2"), new CaseInsensitiveString("pipe3")));
    }

    @Test
    public void createsMergePipelineConfigsOnlyWhenManyParts() {
        Assert.assertThat(((cruiseConfig.getGroups().get(0)) instanceof MergePipelineConfigs), is(false));
        BasicCruiseConfig mainCruiseConfig = new BasicCruiseConfig(pipelines);
        cruiseConfig = new BasicCruiseConfig(mainCruiseConfig, PartialConfigMother.withPipelineInGroup("pipe2", "existing_group"));
        Assert.assertThat(((cruiseConfig.getGroups().get(0)) instanceof MergePipelineConfigs), is(true));
    }

    @Test
    public void shouldGetUniqueMaterialsWithConfigRepos() {
        BasicCruiseConfig mainCruiseConfig = new BasicCruiseConfig(pipelines);
        ConfigReposConfig reposConfig = new ConfigReposConfig();
        GitMaterialConfig configRepo = new GitMaterialConfig("http://git");
        reposConfig.add(new ConfigRepoConfig(configRepo, "myplug"));
        mainCruiseConfig.setConfigRepos(reposConfig);
        PartialConfig partialConfig = PartialConfigMother.withPipeline("pipe2");
        MaterialConfig pipeRepo = partialConfig.getGroups().get(0).get(0).materialConfigs().get(0);
        cruiseConfig = new BasicCruiseConfig(mainCruiseConfig, partialConfig);
        Set<MaterialConfig> materials = cruiseConfig.getAllUniqueMaterialsBelongingToAutoPipelinesAndConfigRepos();
        Assert.assertThat(materials, hasItem(configRepo));
        Assert.assertThat(materials, hasItem(pipeRepo));
        Assert.assertThat(materials.size(), is(2));
    }

    @Test
    public void shouldGetUniqueMaterialsWithoutConfigRepos() {
        BasicCruiseConfig mainCruiseConfig = new BasicCruiseConfig(pipelines);
        ConfigReposConfig reposConfig = new ConfigReposConfig();
        GitMaterialConfig configRepo = new GitMaterialConfig("http://git");
        reposConfig.add(new ConfigRepoConfig(configRepo, "myplug"));
        mainCruiseConfig.setConfigRepos(reposConfig);
        PartialConfig partialConfig = PartialConfigMother.withPipeline("pipe2");
        MaterialConfig pipeRepo = partialConfig.getGroups().get(0).get(0).materialConfigs().get(0);
        cruiseConfig = new BasicCruiseConfig(mainCruiseConfig, partialConfig);
        Set<MaterialConfig> materials = cruiseConfig.getAllUniqueMaterialsBelongingToAutoPipelines();
        Assert.assertThat(materials, hasItem(pipeRepo));
        Assert.assertThat(materials.size(), is(1));
    }

    @Test
    public void shouldUpdatePipelineConfigsListWhenAPartialIsMerged() {
        cruiseConfig = new BasicCruiseConfig(pipelines);
        PartialConfig partial = PartialConfigMother.withPipeline("pipeline3");
        ConfigRepoConfig configRepoConfig = new ConfigRepoConfig(new GitMaterialConfig("http://git"), "myplug");
        partial.setOrigins(new RepoConfigOrigin(configRepoConfig, "123"));
        ConfigReposConfig reposConfig = new ConfigReposConfig();
        reposConfig.add(configRepoConfig);
        cruiseConfig.setConfigRepos(reposConfig);
        cruiseConfig.merge(Arrays.asList(partial), false);
        PipelineConfig pipeline3 = partial.getGroups().first().findBy(new CaseInsensitiveString("pipeline3"));
        Assert.assertThat(cruiseConfig.getAllPipelineConfigs().contains(pipeline3), is(true));
        Assert.assertThat(cruiseConfig.getAllPipelineNames().contains(pipeline3.name()), is(true));
    }
}

