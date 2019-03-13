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
package com.thoughtworks.go.domain;


import com.thoughtworks.go.config.materials.PluggableSCMMaterialConfig;
import com.thoughtworks.go.config.materials.dependency.DependencyMaterialConfig;
import com.thoughtworks.go.config.materials.mercurial.HgMaterialConfig;
import com.thoughtworks.go.config.materials.svn.SvnMaterialConfig;
import com.thoughtworks.go.domain.materials.MaterialConfig;
import com.thoughtworks.go.domain.scm.SCMMother;
import com.thoughtworks.go.helper.MaterialConfigsMother;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class GoConfigMaterialsTest {
    @Test
    public void shouldProvideSetOfSchedulableMaterials() {
        SvnMaterialConfig svnMaterialConfig = MaterialConfigsMother.svnMaterialConfig("url", "svnDir", true);
        PipelineConfig pipeline1 = new PipelineConfig(new CaseInsensitiveString("pipeline1"), new com.thoughtworks.go.config.materials.MaterialConfigs(svnMaterialConfig));
        CruiseConfig config = new BasicCruiseConfig(new BasicPipelineConfigs(pipeline1));
        Assert.assertThat(config.getAllUniqueMaterialsBelongingToAutoPipelines(), Matchers.hasItem(svnMaterialConfig));
    }

    @Test
    public void shouldIncludeScmMaterialsFromManualPipelinesInSchedulableMaterials() {
        PipelineConfig pipeline1 = pipelineWithManyMaterials(true);
        pipeline1.add(new StageConfig(new CaseInsensitiveString("manual-stage"), new JobConfigs(), new Approval()));
        CruiseConfig config = new BasicCruiseConfig(new BasicPipelineConfigs(pipeline1));
        Assert.assertThat(config.getAllUniqueMaterialsBelongingToAutoPipelines().size(), Matchers.is(4));
    }

    @Test
    public void shouldNotIncludePackageMaterialsWithAutoUpdateFalse() {
        PipelineConfig pipeline1 = pipelineWithManyMaterials(false);
        pipeline1.addMaterialConfig(getPackageMaterialConfigWithAutoUpdateFalse());
        pipeline1.addMaterialConfig(getPackageMaterialConfigWithAutoUpdateTrue());
        pipeline1.add(new StageConfig(new CaseInsensitiveString("manual-stage"), new JobConfigs(), new Approval()));
        CruiseConfig config = new BasicCruiseConfig(new BasicPipelineConfigs(pipeline1));
        Assert.assertThat(config.getAllUniqueMaterialsBelongingToAutoPipelines().size(), Matchers.is(4));
    }

    @Test
    public void shouldNotIncludePluggableSCMMaterialsWithAutoUpdateFalse() {
        PipelineConfig pipeline1 = pipelineWithManyMaterials(false);
        PluggableSCMMaterialConfig autoUpdateMaterialConfig = new PluggableSCMMaterialConfig(null, SCMMother.create("scm-id-1"), null, null);
        pipeline1.addMaterialConfig(autoUpdateMaterialConfig);
        PluggableSCMMaterialConfig nonAutoUpdateMaterialConfig = new PluggableSCMMaterialConfig(null, SCMMother.create("scm-id-2"), null, null);
        nonAutoUpdateMaterialConfig.getSCMConfig().setAutoUpdate(false);
        pipeline1.addMaterialConfig(nonAutoUpdateMaterialConfig);
        pipeline1.add(new StageConfig(new CaseInsensitiveString("manual-stage"), new JobConfigs(), new Approval()));
        CruiseConfig config = new BasicCruiseConfig(new BasicPipelineConfigs(pipeline1));
        Set<MaterialConfig> materialsBelongingToAutoPipelines = config.getAllUniqueMaterialsBelongingToAutoPipelines();
        Assert.assertThat(materialsBelongingToAutoPipelines.size(), Matchers.is(4));
        Assert.assertThat(materialsBelongingToAutoPipelines, Matchers.containsInAnyOrder(pipeline1.materialConfigs().get(1), pipeline1.materialConfigs().get(2), pipeline1.materialConfigs().get(3), pipeline1.materialConfigs().get(4)));
    }

    @Test
    public void uniqueMaterialForAutoPipelinesShouldNotReturnPackageMaterialsWithAutoUpdateFalse() throws Exception {
        PipelineConfig pipeline1 = pipelineWithManyMaterials(false);
        pipeline1.add(new StageConfig(new CaseInsensitiveString("manual-stage"), new JobConfigs(), new Approval()));
        CruiseConfig config = new BasicCruiseConfig(new BasicPipelineConfigs(pipeline1));
        Assert.assertThat(config.getAllUniqueMaterialsBelongingToAutoPipelines().size(), Matchers.is(3));
    }

    @Test
    public void shouldIncludeDependencyMaterialsFromManualPipelinesInSchedulableMaterials() {
        DependencyMaterialConfig dependencyMaterialConfig = MaterialConfigsMother.dependencyMaterialConfig();
        PipelineConfig pipeline1 = new PipelineConfig(new CaseInsensitiveString("pipeline1"), new com.thoughtworks.go.config.materials.MaterialConfigs(dependencyMaterialConfig));
        pipeline1.add(new StageConfig(new CaseInsensitiveString("manual-stage"), new JobConfigs(), new Approval()));
        CruiseConfig config = new BasicCruiseConfig(new BasicPipelineConfigs(pipeline1));
        Set<MaterialConfig> materialConfigs = config.getAllUniqueMaterialsBelongingToAutoPipelines();
        Assert.assertThat(materialConfigs.size(), Matchers.is(1));
        Assert.assertThat(materialConfigs.contains(dependencyMaterialConfig), Matchers.is(true));
    }

    @Test
    public void getStagesUsedAsMaterials() {
        HgMaterialConfig hg = MaterialConfigsMother.hgMaterialConfig();
        StageConfig upStage = new StageConfig(new CaseInsensitiveString("stage1"), new JobConfigs());
        PipelineConfig up1 = new PipelineConfig(new CaseInsensitiveString("up1"), new com.thoughtworks.go.config.materials.MaterialConfigs(hg), upStage);
        PipelineConfig up2 = new PipelineConfig(new CaseInsensitiveString("up2"), new com.thoughtworks.go.config.materials.MaterialConfigs(hg), new StageConfig(new CaseInsensitiveString("stage2"), new JobConfigs()));
        DependencyMaterialConfig dependency1 = MaterialConfigsMother.dependencyMaterialConfig("up1", "stage1");
        DependencyMaterialConfig dependency2 = MaterialConfigsMother.dependencyMaterialConfig("up2", "stage2");
        PipelineConfig down1 = new PipelineConfig(new CaseInsensitiveString("down1"), new com.thoughtworks.go.config.materials.MaterialConfigs(dependency1, dependency2, hg));
        PipelineConfig down2 = new PipelineConfig(new CaseInsensitiveString("down2"), new com.thoughtworks.go.config.materials.MaterialConfigs(dependency1, dependency2, hg));
        CruiseConfig config = new BasicCruiseConfig(new BasicPipelineConfigs(up1, up2, down1, down2));
        Set<StageConfig> stages = config.getStagesUsedAsMaterials(up1);
        Assert.assertThat(stages.size(), Matchers.is(1));
        Assert.assertThat(stages.contains(upStage), Matchers.is(true));
    }

    @Test
    public void shouldOnlyHaveOneCopyOfAMaterialIfOnlyTheFolderIsDifferent() {
        SvnMaterialConfig svn = MaterialConfigsMother.svnMaterialConfig("url", "folder1", true);
        SvnMaterialConfig svnInDifferentFolder = MaterialConfigsMother.svnMaterialConfig("url", "folder2");
        PipelineConfig pipeline1 = new PipelineConfig(new CaseInsensitiveString("pipeline1"), new com.thoughtworks.go.config.materials.MaterialConfigs(svn));
        PipelineConfig pipeline2 = new PipelineConfig(new CaseInsensitiveString("pipeline2"), new com.thoughtworks.go.config.materials.MaterialConfigs(svnInDifferentFolder));
        CruiseConfig config = new BasicCruiseConfig(new BasicPipelineConfigs(pipeline1, pipeline2));
        Assert.assertThat(config.getAllUniqueMaterialsBelongingToAutoPipelines().size(), Matchers.is(1));
    }

    @Test
    public void shouldHaveBothMaterialsIfTheTypeIsDifferent() {
        SvnMaterialConfig svn = MaterialConfigsMother.svnMaterialConfig("url", "folder1", true);
        HgMaterialConfig hg = MaterialConfigsMother.hgMaterialConfig("url", "folder2");
        PipelineConfig pipeline1 = new PipelineConfig(new CaseInsensitiveString("pipeline1"), new com.thoughtworks.go.config.materials.MaterialConfigs(svn));
        PipelineConfig pipeline2 = new PipelineConfig(new CaseInsensitiveString("pipeline2"), new com.thoughtworks.go.config.materials.MaterialConfigs(hg));
        CruiseConfig config = new BasicCruiseConfig(new BasicPipelineConfigs(pipeline1, pipeline2));
        Assert.assertThat(config.getAllUniqueMaterialsBelongingToAutoPipelines().size(), Matchers.is(2));
    }
}

