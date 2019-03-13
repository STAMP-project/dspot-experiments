/**
 * Copyright 2015 ThoughtWorks, Inc.
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
package com.thoughtworks.go.serverhealth;


import HealthStateScope.ScopeType.PLUGIN;
import com.thoughtworks.go.config.CaseInsensitiveString;
import com.thoughtworks.go.config.CruiseConfig;
import com.thoughtworks.go.config.materials.mercurial.HgMaterialConfig;
import com.thoughtworks.go.config.materials.svn.SvnMaterial;
import com.thoughtworks.go.helper.GoConfigMother;
import com.thoughtworks.go.helper.MaterialConfigsMother;
import com.thoughtworks.go.helper.MaterialsMother;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class HealthStateScopeTest {
    private static final SvnMaterial MATERIAL1 = MaterialsMother.svnMaterial("url1");

    private static final SvnMaterial MATERIAL2 = MaterialsMother.svnMaterial("url2");

    @Test
    public void shouldHaveAUniqueScopeForEachMaterial() throws Exception {
        HealthStateScope scope1 = HealthStateScope.forMaterial(HealthStateScopeTest.MATERIAL1);
        HealthStateScope scope2 = HealthStateScope.forMaterial(HealthStateScopeTest.MATERIAL1);
        Assert.assertThat(scope1, Matchers.is(scope2));
    }

    @Test
    public void shouldHaveDifferentScopeForDifferentMaterials() throws Exception {
        HealthStateScope scope1 = HealthStateScope.forMaterial(HealthStateScopeTest.MATERIAL1);
        HealthStateScope scope2 = HealthStateScope.forMaterial(HealthStateScopeTest.MATERIAL2);
        Assert.assertThat(scope1, Matchers.not(scope2));
    }

    @Test
    public void shouldHaveUniqueScopeForStages() throws Exception {
        HealthStateScope scope1 = HealthStateScope.forStage("blahPipeline", "blahStage");
        HealthStateScope scope2 = HealthStateScope.forStage("blahPipeline", "blahStage");
        HealthStateScope scope25 = HealthStateScope.forStage("blahPipeline", "blahOtherStage");
        HealthStateScope scope3 = HealthStateScope.forStage("blahOtherPipeline", "blahOtherStage");
        Assert.assertThat(scope1, Matchers.is(scope2));
        Assert.assertThat(scope1, Matchers.not(scope25));
        Assert.assertThat(scope1, Matchers.not(scope3));
    }

    @Test
    public void shouldRemoveScopeWhenMaterialIsRemovedFromConfig() throws Exception {
        HgMaterialConfig hgMaterialConfig = MaterialConfigsMother.hgMaterialConfig();
        CruiseConfig config = GoConfigMother.pipelineHavingJob("blahPipeline", "blahStage", "blahJob", "fii", "baz");
        config.pipelineConfigByName(new CaseInsensitiveString("blahPipeline")).addMaterialConfig(hgMaterialConfig);
        Assert.assertThat(HealthStateScope.forMaterialConfig(hgMaterialConfig).isRemovedFromConfig(config), Matchers.is(false));
        Assert.assertThat(HealthStateScope.forMaterial(MaterialsMother.svnMaterial("file:///bar")).isRemovedFromConfig(config), Matchers.is(true));
    }

    @Test
    public void shouldNotRemoveScopeWhenMaterialBelongsToConfigRepoMaterial() throws Exception {
        HgMaterialConfig hgMaterialConfig = MaterialConfigsMother.hgMaterialConfig();
        CruiseConfig config = GoConfigMother.pipelineHavingJob("blahPipeline", "blahStage", "blahJob", "fii", "baz");
        config.getConfigRepos().add(new com.thoughtworks.go.config.remote.ConfigRepoConfig(hgMaterialConfig, "id1", "foo"));
        Assert.assertThat(HealthStateScope.forMaterialConfig(hgMaterialConfig).isRemovedFromConfig(config), Matchers.is(false));
    }

    @Test
    public void shouldNotRemoveScopeWhenMaterialUpdateBelongsToConfigRepoMaterial() throws Exception {
        HgMaterialConfig hgMaterialConfig = MaterialConfigsMother.hgMaterialConfig();
        CruiseConfig config = GoConfigMother.pipelineHavingJob("blahPipeline", "blahStage", "blahJob", "fii", "baz");
        config.getConfigRepos().add(new com.thoughtworks.go.config.remote.ConfigRepoConfig(hgMaterialConfig, "id1", "foo"));
        Assert.assertThat(HealthStateScope.forMaterialConfigUpdate(hgMaterialConfig).isRemovedFromConfig(config), Matchers.is(false));
    }

    @Test
    public void shouldRemoveScopeWhenStageIsRemovedFromConfig() throws Exception {
        CruiseConfig config = GoConfigMother.pipelineHavingJob("blahPipeline", "blahStage", "blahJob", "fii", "baz");
        Assert.assertThat(HealthStateScope.forPipeline("fooPipeline").isRemovedFromConfig(config), Matchers.is(true));
        Assert.assertThat(HealthStateScope.forPipeline("blahPipeline").isRemovedFromConfig(config), Matchers.is(false));
        Assert.assertThat(HealthStateScope.forStage("fooPipeline", "blahStage").isRemovedFromConfig(config), Matchers.is(true));
        Assert.assertThat(HealthStateScope.forStage("blahPipeline", "blahStageRemoved").isRemovedFromConfig(config), Matchers.is(true));
        Assert.assertThat(HealthStateScope.forStage("blahPipeline", "blahStage").isRemovedFromConfig(config), Matchers.is(false));
    }

    @Test
    public void shouldRemoveScopeWhenJobIsRemovedFromConfig() throws Exception {
        CruiseConfig config = GoConfigMother.pipelineHavingJob("blahPipeline", "blahStage", "blahJob", "fii", "baz");
        Assert.assertThat(HealthStateScope.forJob("fooPipeline", "blahStage", "barJob").isRemovedFromConfig(config), Matchers.is(true));
        Assert.assertThat(HealthStateScope.forJob("blahPipeline", "blahStage", "blahJob").isRemovedFromConfig(config), Matchers.is(false));
    }

    @Test
    public void shouldUnderstandPluginScope() {
        HealthStateScope scope = HealthStateScope.aboutPlugin("plugin.one");
        Assert.assertThat(scope.getScope(), Matchers.is("plugin.one"));
        Assert.assertThat(scope.getType(), Matchers.is(PLUGIN));
    }
}

