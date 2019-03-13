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
package com.thoughtworks.go.domain;


import BasicPipelineConfigs.GROUP;
import PipelineConfig.NAME;
import com.thoughtworks.go.config.exceptions.PipelineGroupNotEmptyException;
import com.thoughtworks.go.config.exceptions.PipelineGroupNotFoundException;
import com.thoughtworks.go.config.materials.PackageMaterialConfig;
import com.thoughtworks.go.config.materials.PluggableSCMMaterialConfig;
import com.thoughtworks.go.config.merge.MergePipelineConfigs;
import com.thoughtworks.go.config.remote.FileConfigOrigin;
import com.thoughtworks.go.config.remote.RepoConfigOrigin;
import com.thoughtworks.go.helper.MaterialConfigsMother;
import com.thoughtworks.go.helper.PipelineConfigMother;
import com.thoughtworks.go.util.Pair;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class PipelineGroupsTest {
    @Test
    public void shouldOnlySavePipelineToTargetGroup() {
        PipelineConfigs defaultGroup = PipelineConfigMother.createGroup("defaultGroup", PipelineConfigMother.createPipelineConfig("pipeline1", "stage1"));
        PipelineConfigs defaultGroup2 = PipelineConfigMother.createGroup("defaultGroup2", PipelineConfigMother.createPipelineConfig("pipeline2", "stage2"));
        PipelineGroups pipelineGroups = new PipelineGroups(defaultGroup, defaultGroup2);
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("pipeline3", "stage1");
        pipelineGroups.addPipeline("defaultGroup", pipelineConfig);
        Assert.assertThat(defaultGroup, Matchers.hasItem(pipelineConfig));
        Assert.assertThat(defaultGroup2, Matchers.not(Matchers.hasItem(pipelineConfig)));
        Assert.assertThat(pipelineGroups.size(), Matchers.is(2));
    }

    @Test
    public void shouldRemovePipelineFromTheGroup() throws Exception {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("pipeline1", "stage1");
        PipelineConfigs defaultGroup = PipelineConfigMother.createGroup("defaultGroup", pipelineConfig);
        PipelineGroups pipelineGroups = new PipelineGroups(defaultGroup);
        Assert.assertThat(pipelineGroups.size(), Matchers.is(1));
        pipelineGroups.deletePipeline(pipelineConfig);
        Assert.assertThat(pipelineGroups.size(), Matchers.is(1));
        Assert.assertThat(defaultGroup.size(), Matchers.is(0));
    }

    @Test
    public void shouldSaveNewPipelineGroupOnTheTop() {
        PipelineConfigs defaultGroup = PipelineConfigMother.createGroup("defaultGroup", PipelineConfigMother.createPipelineConfig("pipeline1", "stage1"));
        PipelineConfigs defaultGroup2 = PipelineConfigMother.createGroup("defaultGroup2", PipelineConfigMother.createPipelineConfig("pipeline2", "stage2"));
        PipelineGroups pipelineGroups = new PipelineGroups(defaultGroup, defaultGroup2);
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("pipeline3", "stage1");
        pipelineGroups.addPipeline("defaultGroup3", pipelineConfig);
        PipelineConfigs group = PipelineConfigMother.createGroup("defaultGroup3", pipelineConfig);
        Assert.assertThat(pipelineGroups.indexOf(group), Matchers.is(0));
    }

    @Test
    public void validate_shouldMarkDuplicatePipelineGroupNamesAsError() {
        PipelineConfigs first = PipelineConfigMother.createGroup("first", "pipeline");
        PipelineConfigs dup = PipelineConfigMother.createGroup("first", "pipeline");
        PipelineGroups groups = new PipelineGroups(first, dup);
        groups.validate(null);
        Assert.assertThat(first.errors().on(GROUP), Matchers.is("Group with name 'first' already exists"));
        Assert.assertThat(dup.errors().on(GROUP), Matchers.is("Group with name 'first' already exists"));
    }

    @Test
    public void shouldReturnTrueWhenGroupNameIsEmptyAndDefaultGroupExists() {
        PipelineConfig existingPipeline = PipelineConfigMother.createPipelineConfig("pipeline1", "stage1");
        PipelineConfigs defaultGroup = PipelineConfigMother.createGroup("defaultGroup", existingPipeline);
        PipelineGroups pipelineGroups = new PipelineGroups(defaultGroup);
        PipelineConfig newPipeline = PipelineConfigMother.createPipelineConfig("pipeline3", "stage1");
        pipelineGroups.addPipeline("", newPipeline);
        Assert.assertThat(pipelineGroups.size(), Matchers.is(1));
        Assert.assertThat(defaultGroup, Matchers.hasItem(existingPipeline));
        Assert.assertThat(defaultGroup, Matchers.hasItem(newPipeline));
    }

    @Test
    public void shouldErrorOutIfDuplicatePipelineIsAdded() {
        PipelineConfig pipeline1 = PipelineConfigMother.createPipelineConfig("pipeline1", "stage1");
        PipelineConfig pipeline2 = PipelineConfigMother.createPipelineConfig("pipeline1", "stage1");
        PipelineConfig pipeline3 = PipelineConfigMother.createPipelineConfig("pipeline1", "stage1");
        PipelineConfig pipeline4 = PipelineConfigMother.createPipelineConfig("pipeline1", "stage1");
        pipeline3.setOrigin(new RepoConfigOrigin(new com.thoughtworks.go.config.remote.ConfigRepoConfig(MaterialConfigsMother.gitMaterialConfig(), "plugin"), "rev1"));
        pipeline4.setOrigin(new RepoConfigOrigin(new com.thoughtworks.go.config.remote.ConfigRepoConfig(MaterialConfigsMother.svnMaterialConfig(), "plugin"), "1"));
        PipelineConfigs defaultGroup = PipelineConfigMother.createGroup("defaultGroup", pipeline1);
        PipelineConfigs anotherGroup = PipelineConfigMother.createGroup("anotherGroup", pipeline2);
        PipelineConfigs thirdGroup = PipelineConfigMother.createGroup("thirdGroup", pipeline3);
        PipelineConfigs fourthGroup = PipelineConfigMother.createGroup("fourthGroup", pipeline4);
        PipelineGroups pipelineGroups = new PipelineGroups(defaultGroup, anotherGroup, thirdGroup, fourthGroup);
        pipelineGroups.validate(null);
        List<String> expectedSources = Arrays.asList(pipeline1.getOriginDisplayName(), pipeline2.getOriginDisplayName(), pipeline3.getOriginDisplayName(), pipeline4.getOriginDisplayName());
        assertDuplicateNameErrorOnPipeline(pipeline1, expectedSources, 3);
        assertDuplicateNameErrorOnPipeline(pipeline2, expectedSources, 3);
        assertDuplicateNameErrorOnPipeline(pipeline3, expectedSources, 3);
        assertDuplicateNameErrorOnPipeline(pipeline4, expectedSources, 3);
    }

    @Test
    public void shouldErrorOutIfDuplicatePipelineIsAddedToSameGroup() {
        PipelineConfig pipeline1 = PipelineConfigMother.createPipelineConfig("pipeline1", "stage1");
        PipelineConfig pipeline2 = PipelineConfigMother.createPipelineConfig("pipeline1", "stage1");
        PipelineConfigs defaultGroup = PipelineConfigMother.createGroup("defaultGroup", pipeline1, pipeline2);
        PipelineGroups pipelineGroups = new PipelineGroups(defaultGroup);
        pipelineGroups.validate(null);
        Assert.assertThat(pipeline1.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(pipeline1.errors().on(NAME), Matchers.is("You have defined multiple pipelines named 'pipeline1'. Pipeline names must be unique. Source(s): [cruise-config.xml]"));
        Assert.assertThat(pipeline2.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(pipeline2.errors().on(NAME), Matchers.is("You have defined multiple pipelines named 'pipeline1'. Pipeline names must be unique. Source(s): [cruise-config.xml]"));
    }

    @Test
    public void shouldFindAPipelineGroupByName() {
        PipelineConfig pipeline = PipelineConfigMother.createPipelineConfig("pipeline1", "stage1");
        PipelineConfigs defaultGroup = PipelineConfigMother.createGroup("defaultGroup", pipeline);
        PipelineGroups pipelineGroups = new PipelineGroups(defaultGroup);
        Assert.assertThat(pipelineGroups.findGroup("defaultGroup"), Matchers.is(defaultGroup));
    }

    @Test(expected = PipelineGroupNotFoundException.class)
    public void shouldThrowGroupNotFoundExceptionWhenSearchingForANonExistingGroup() {
        PipelineConfig pipeline = PipelineConfigMother.createPipelineConfig("pipeline1", "stage1");
        PipelineConfigs defaultGroup = PipelineConfigMother.createGroup("defaultGroup", pipeline);
        PipelineGroups pipelineGroups = new PipelineGroups(defaultGroup);
        pipelineGroups.findGroup("NonExistantGroup");
    }

    @Test
    public void shouldGetPackageUsageInPipelines() throws Exception {
        PackageMaterialConfig packageOne = new PackageMaterialConfig("package-id-one");
        PackageMaterialConfig packageTwo = new PackageMaterialConfig("package-id-two");
        final PipelineConfig p1 = PipelineConfigMother.pipelineConfig("pipeline1", new com.thoughtworks.go.config.materials.MaterialConfigs(packageOne, packageTwo), new JobConfigs(new JobConfig(new CaseInsensitiveString("jobName"))));
        final PipelineConfig p2 = PipelineConfigMother.pipelineConfig("pipeline2", new com.thoughtworks.go.config.materials.MaterialConfigs(packageTwo), new JobConfigs(new JobConfig(new CaseInsensitiveString("jobName"))));
        PipelineGroups groups = new PipelineGroups();
        PipelineConfigs groupOne = new BasicPipelineConfigs(p1);
        PipelineConfigs groupTwo = new BasicPipelineConfigs(p2);
        groups.addAll(Arrays.asList(groupOne, groupTwo));
        Map<String, List<Pair<PipelineConfig, PipelineConfigs>>> packageToPipelineMap = groups.getPackageUsageInPipelines();
        Assert.assertThat(packageToPipelineMap.get("package-id-one").size(), Matchers.is(1));
        Assert.assertThat(packageToPipelineMap.get("package-id-one"), Matchers.hasItems(new Pair(p1, groupOne)));
        Assert.assertThat(packageToPipelineMap.get("package-id-two").size(), Matchers.is(2));
        Assert.assertThat(packageToPipelineMap.get("package-id-two"), Matchers.hasItems(new Pair(p1, groupOne), new Pair(p2, groupTwo)));
    }

    @Test
    public void shouldComputePackageUsageInPipelinesOnlyOnce() throws Exception {
        PackageMaterialConfig packageOne = new PackageMaterialConfig("package-id-one");
        PackageMaterialConfig packageTwo = new PackageMaterialConfig("package-id-two");
        final PipelineConfig p1 = PipelineConfigMother.pipelineConfig("pipeline1", new com.thoughtworks.go.config.materials.MaterialConfigs(packageOne, packageTwo), new JobConfigs(new JobConfig(new CaseInsensitiveString("jobName"))));
        final PipelineConfig p2 = PipelineConfigMother.pipelineConfig("pipeline2", new com.thoughtworks.go.config.materials.MaterialConfigs(packageTwo), new JobConfigs(new JobConfig(new CaseInsensitiveString("jobName"))));
        PipelineGroups groups = new PipelineGroups();
        groups.addAll(Arrays.asList(new BasicPipelineConfigs(p1), new BasicPipelineConfigs(p2)));
        Map<String, List<Pair<PipelineConfig, PipelineConfigs>>> result1 = groups.getPackageUsageInPipelines();
        Map<String, List<Pair<PipelineConfig, PipelineConfigs>>> result2 = groups.getPackageUsageInPipelines();
        Assert.assertSame(result1, result2);
    }

    @Test
    public void shouldGetPluggableSCMMaterialUsageInPipelines() throws Exception {
        PluggableSCMMaterialConfig pluggableSCMMaterialOne = new PluggableSCMMaterialConfig("scm-id-one");
        PluggableSCMMaterialConfig pluggableSCMMaterialTwo = new PluggableSCMMaterialConfig("scm-id-two");
        final PipelineConfig p1 = PipelineConfigMother.pipelineConfig("pipeline1", new com.thoughtworks.go.config.materials.MaterialConfigs(pluggableSCMMaterialOne, pluggableSCMMaterialTwo), new JobConfigs(new JobConfig(new CaseInsensitiveString("jobName"))));
        final PipelineConfig p2 = PipelineConfigMother.pipelineConfig("pipeline2", new com.thoughtworks.go.config.materials.MaterialConfigs(pluggableSCMMaterialTwo), new JobConfigs(new JobConfig(new CaseInsensitiveString("jobName"))));
        PipelineGroups groups = new PipelineGroups();
        PipelineConfigs groupOne = new BasicPipelineConfigs(p1);
        PipelineConfigs groupTwo = new BasicPipelineConfigs(p2);
        groups.addAll(Arrays.asList(groupOne, groupTwo));
        Map<String, List<Pair<PipelineConfig, PipelineConfigs>>> pluggableSCMMaterialUsageInPipelinesOne = groups.getPluggableSCMMaterialUsageInPipelines();
        Assert.assertThat(pluggableSCMMaterialUsageInPipelinesOne.get("scm-id-one").size(), Matchers.is(1));
        Assert.assertThat(pluggableSCMMaterialUsageInPipelinesOne.get("scm-id-one"), Matchers.hasItems(new Pair(p1, groupOne)));
        Assert.assertThat(pluggableSCMMaterialUsageInPipelinesOne.get("scm-id-two").size(), Matchers.is(2));
        Assert.assertThat(pluggableSCMMaterialUsageInPipelinesOne.get("scm-id-two"), Matchers.hasItems(new Pair(p1, groupOne), new Pair(p2, groupTwo)));
        Map<String, List<Pair<PipelineConfig, PipelineConfigs>>> pluggableSCMMaterialUsageInPipelinesTwo = groups.getPluggableSCMMaterialUsageInPipelines();
        Assert.assertSame(pluggableSCMMaterialUsageInPipelinesOne, pluggableSCMMaterialUsageInPipelinesTwo);
    }

    @Test
    public void shouldGetLocalPartsWhenOriginIsNull() {
        PipelineConfigs defaultGroup = PipelineConfigMother.createGroup("defaultGroup", PipelineConfigMother.createPipelineConfig("pipeline1", "stage1"));
        PipelineGroups groups = new PipelineGroups(defaultGroup);
        Assert.assertThat(groups.getLocal().size(), Matchers.is(1));
        Assert.assertThat(groups.getLocal().get(0), Matchers.is(defaultGroup));
    }

    @Test
    public void shouldGetLocalPartsWhenOriginIsFile() {
        PipelineConfigs defaultGroup = PipelineConfigMother.createGroup("defaultGroup", PipelineConfigMother.createPipelineConfig("pipeline1", "stage1"));
        defaultGroup.setOrigins(new FileConfigOrigin());
        PipelineGroups groups = new PipelineGroups(defaultGroup);
        Assert.assertThat(groups.getLocal().size(), Matchers.is(1));
        Assert.assertThat(groups.getLocal().get(0), Matchers.is(defaultGroup));
    }

    @Test
    public void shouldGetLocalPartsWhenOriginIsRepo() {
        PipelineConfigs defaultGroup = PipelineConfigMother.createGroup("defaultGroup", PipelineConfigMother.createPipelineConfig("pipeline1", "stage1"));
        defaultGroup.setOrigins(new RepoConfigOrigin());
        PipelineGroups groups = new PipelineGroups(defaultGroup);
        Assert.assertThat(groups.getLocal().size(), Matchers.is(0));
        Assert.assertThat(groups.getLocal().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldGetLocalPartsWhenOriginIsMixed() {
        PipelineConfigs localGroup = PipelineConfigMother.createGroup("defaultGroup", PipelineConfigMother.createPipelineConfig("pipeline1", "stage1"));
        localGroup.setOrigins(new FileConfigOrigin());
        PipelineConfigs remoteGroup = PipelineConfigMother.createGroup("defaultGroup", PipelineConfigMother.createPipelineConfig("pipeline2", "stage1"));
        remoteGroup.setOrigins(new RepoConfigOrigin());
        MergePipelineConfigs mergePipelineConfigs = new MergePipelineConfigs(localGroup, remoteGroup);
        PipelineGroups groups = new PipelineGroups(mergePipelineConfigs);
        Assert.assertThat(groups.getLocal().size(), Matchers.is(1));
        Assert.assertThat(groups.getLocal(), Matchers.hasItem(localGroup));
    }

    @Test
    public void shouldFindGroupByPipelineName() throws Exception {
        PipelineConfig p1Config = PipelineConfigMother.createPipelineConfig("pipeline1", "stage1");
        PipelineConfig p2Config = PipelineConfigMother.createPipelineConfig("pipeline2", "stage1");
        PipelineConfig p3Config = PipelineConfigMother.createPipelineConfig("pipeline3", "stage1");
        PipelineConfigs group1 = PipelineConfigMother.createGroup("group1", p1Config, p2Config);
        PipelineConfigs group2 = PipelineConfigMother.createGroup("group2", p3Config);
        PipelineGroups groups = new PipelineGroups(group1, group2);
        Assert.assertThat(groups.findGroupByPipeline(new CaseInsensitiveString("pipeline1")), Matchers.is(group1));
        Assert.assertThat(groups.findGroupByPipeline(new CaseInsensitiveString("pipeline2")), Matchers.is(group1));
        Assert.assertThat(groups.findGroupByPipeline(new CaseInsensitiveString("pipeline3")), Matchers.is(group2));
    }

    @Test
    public void shouldDeleteGroupWhenEmpty() {
        PipelineConfigs group = PipelineConfigMother.createGroup("group", new PipelineConfig[]{  });
        PipelineGroups groups = new PipelineGroups(group);
        groups.deleteGroup("group");
        Assert.assertThat(groups.size(), Matchers.is(0));
    }

    @Test
    public void shouldDeleteGroupWithSameNameWhenEmpty() {
        PipelineConfigs group = PipelineConfigMother.createGroup("group", new PipelineConfig[]{  });
        group.setAuthorization(new Authorization(new ViewConfig(new AdminUser(new CaseInsensitiveString("user")))));
        PipelineGroups groups = new PipelineGroups(group);
        groups.deleteGroup("group");
        Assert.assertThat(groups.size(), Matchers.is(0));
    }

    @Test(expected = PipelineGroupNotEmptyException.class)
    public void shouldThrowExceptionWhenDeletingGroupWhenNotEmpty() {
        PipelineConfig p1Config = PipelineConfigMother.createPipelineConfig("pipeline1", "stage1");
        PipelineConfigs group = PipelineConfigMother.createGroup("group", p1Config);
        PipelineGroups groups = new PipelineGroups(group);
        groups.deleteGroup("group");
    }
}

