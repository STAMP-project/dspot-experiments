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
package com.thoughtworks.go.config.materials.dependency;


import DependencyMaterialConfig.PIPELINE_STAGE_NAME;
import com.thoughtworks.go.config.materials.MaterialConfigs;
import com.thoughtworks.go.domain.materials.Material;
import com.thoughtworks.go.domain.materials.Modification;
import com.thoughtworks.go.domain.materials.dependency.DependencyMaterialRevision;
import com.thoughtworks.go.helper.GoConfigMother;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class DependencyMaterialTest {
    private DependencyMaterial dependencyMaterial;

    @Test
    public void shouldReturnCruiseAsUser() {
        Assert.assertThat(dependencyMaterial.getUserName(), Matchers.is("cruise"));
    }

    @Test
    public void shouldReturnJson() {
        Map<String, String> json = new LinkedHashMap<>();
        dependencyMaterial.toJson(json, create("pipeline", 10, "1.0.123", "stage", 1));
        Assert.assertThat(json.get("location"), Matchers.is("pipeline/stage"));
        Assert.assertThat(json.get("scmType"), Matchers.is("Dependency"));
        Assert.assertThat(json.get("folder"), Matchers.is(""));
        Assert.assertThat(json.get("action"), Matchers.is("Completed"));
    }

    @Test
    public void shouldDifferIfStageCounterHasChanged() {
        DependencyMaterialRevision rev1 = create("pipeline", 10, "1.0.123", "stage", 1);
        DependencyMaterialRevision rev2 = create("pipeline", 10, "1.0.123", "stage", 2);
        DependencyMaterialRevision rev3 = create("pipeline", 11, "1.0.123", "stage", 1);
        Assert.assertThat(rev1, Matchers.is(Matchers.not(rev2)));
        Assert.assertThat(rev2, Matchers.is(Matchers.not(rev3)));
        Assert.assertThat(rev3, Matchers.is(Matchers.not(rev1)));
    }

    @Test
    public void shouldParseMaterialRevisionWithPipelineLabel() {
        ArrayList<Modification> mods = new ArrayList<>();
        Modification mod = new Modification(new Date(), "pipelineName/123/stageName/2", "pipeline-label-123", null);
        mods.add(mod);
        DependencyMaterialRevision revision = ((DependencyMaterialRevision) (new com.thoughtworks.go.domain.materials.Modifications(mods).latestRevision(dependencyMaterial)));
        Assert.assertThat(revision.getRevision(), Matchers.is("pipelineName/123/stageName/2"));
        Assert.assertThat(revision.getPipelineLabel(), Matchers.is("pipeline-label-123"));
        Assert.assertThat(revision.getPipelineCounter(), Matchers.is(123));
        Assert.assertThat(revision.getPipelineName(), Matchers.is("pipelineName"));
        Assert.assertThat(revision.getStageName(), Matchers.is("stageName"));
        Assert.assertThat(revision.getStageCounter(), Matchers.is(2));
    }

    @Test
    public void shouldBeUniqueBasedOnpipelineAndStageName() throws Exception {
        DependencyMaterial material1 = new DependencyMaterial(new CaseInsensitiveString("pipeline1"), new CaseInsensitiveString("stage1"));
        Map<String, Object> map = new HashMap<>();
        material1.appendCriteria(map);
        Assert.assertThat(map, Matchers.hasEntry("pipelineName", "pipeline1"));
        Assert.assertThat(map, Matchers.hasEntry("stageName", "stage1"));
        Assert.assertThat(map.size(), Matchers.is(2));
    }

    @Test
    public void shouldUsePipelineNameAsMaterialNameIfItIsNotSet() throws Exception {
        Assert.assertThat(getName(), Matchers.is(new CaseInsensitiveString("pipeline1")));
    }

    @Test
    public void shouldUseMaterialNameAsMaterialNameIfItIsSet() throws Exception {
        DependencyMaterial material = new DependencyMaterial(new CaseInsensitiveString("pipeline1"), new CaseInsensitiveString("stage1"));
        material.setName(new CaseInsensitiveString("my-material-name"));
        Assert.assertThat(material.getName(), Matchers.is(new CaseInsensitiveString("my-material-name")));
    }

    @Test
    public void shouldGenerateSqlCriteriaMapInSpecificOrder() throws Exception {
        Map<String, Object> map = dependencyMaterial.getSqlCriteria();
        Assert.assertThat(map.size(), Matchers.is(3));
        Iterator<Map.Entry<String, Object>> iter = map.entrySet().iterator();
        Assert.assertThat(iter.next().getKey(), Matchers.is("type"));
        Assert.assertThat(iter.next().getKey(), Matchers.is("pipelineName"));
        Assert.assertThat(iter.next().getKey(), Matchers.is("stageName"));
    }

    @Test
    public void equalsImplementation() throws Exception {
        DependencyMaterial one = new DependencyMaterial(new CaseInsensitiveString("pipelineName"), new CaseInsensitiveString("stage"));
        DependencyMaterial two = new DependencyMaterial(new CaseInsensitiveString("pipelineName"), new CaseInsensitiveString("stage"));
        two.setName(new CaseInsensitiveString("other-name-that-should-be-ignored-in-equals-comparison"));
        Assert.assertEquals(two, one);
        DependencyMaterial three = new DependencyMaterial(new CaseInsensitiveString("otherPipelineName"), new CaseInsensitiveString("stage"));
        Assert.assertNotEquals(one, three);
    }

    @Test
    public void hashCodeImplementation() throws Exception {
        DependencyMaterial one = new DependencyMaterial(new CaseInsensitiveString("pipelineName"), new CaseInsensitiveString("stage"));
        DependencyMaterial two = new DependencyMaterial(new CaseInsensitiveString("pipelineName"), new CaseInsensitiveString("stage"));
        two.setName(new CaseInsensitiveString("other-name-that-should-be-ignored-in-hashcode-generation"));
        Assert.assertEquals(two.hashCode(), one.hashCode());
        DependencyMaterial three = new DependencyMaterial(new CaseInsensitiveString("otherPipelineName"), new CaseInsensitiveString("stage"));
        Assert.assertNotEquals(one.hashCode(), three.hashCode());
    }

    @Test
    public void shouldReturnUpstreamPipelineNameAsDisplayNameIfMaterialNameIsNotDefined() throws Exception {
        DependencyMaterial material = new DependencyMaterial(new CaseInsensitiveString("upstream"), new CaseInsensitiveString("first"));
        Assert.assertThat(material.getDisplayName(), Matchers.is("upstream"));
    }

    @Test
    public void shouldReturnMaterialNameIfDefined() throws Exception {
        DependencyMaterial material = new DependencyMaterial(new CaseInsensitiveString("upstream"), new CaseInsensitiveString("first"));
        material.setName(new CaseInsensitiveString("my_name"));
        Assert.assertThat(material.getDisplayName(), Matchers.is("my_name"));
    }

    @Test
    public void shouldNotTruncateshortRevision() throws Exception {
        Material material = new DependencyMaterial(new CaseInsensitiveString("upstream"), new CaseInsensitiveString("first"));
        Assert.assertThat(material.getShortRevision("pipeline-name/1/stage-name/5"), Matchers.is("pipeline-name/1/stage-name/5"));
    }

    @Test
    public void shouldUseACombinationOfPipelineAndStageNameAsURI() {
        Material material = new DependencyMaterial(new CaseInsensitiveString("pipeline-foo"), new CaseInsensitiveString("stage-bar"));
        Assert.assertThat(material.getUriForDisplay(), Matchers.is("pipeline-foo / stage-bar"));
    }

    @Test
    public void shouldDetectDependencyMaterialUsedInFetchArtifact() {
        DependencyMaterial material = new DependencyMaterial(new CaseInsensitiveString("pipeline-foo"), new CaseInsensitiveString("stage-bar"));
        PipelineConfig pipelineConfig = Mockito.mock(PipelineConfig.class);
        ArrayList<FetchTask> fetchTasks = new ArrayList<>();
        fetchTasks.add(new FetchTask(new CaseInsensitiveString("something"), new CaseInsensitiveString("new"), "src", "dest"));
        fetchTasks.add(new FetchTask(new CaseInsensitiveString("pipeline-foo"), new CaseInsensitiveString("stage-bar"), new CaseInsensitiveString("job"), "src", "dest"));
        Mockito.when(pipelineConfig.getFetchTasks()).thenReturn(fetchTasks);
        Assert.assertThat(material.isUsedInFetchArtifact(pipelineConfig), Matchers.is(true));
    }

    @Test
    public void shouldDetectDependencyMaterialUsedInFetchArtifactFromAncestor() {
        DependencyMaterial material = new DependencyMaterial(new CaseInsensitiveString("parent-pipeline"), new CaseInsensitiveString("stage-bar"));
        PipelineConfig pipelineConfig = Mockito.mock(PipelineConfig.class);
        ArrayList<FetchTask> fetchTasks = new ArrayList<>();
        fetchTasks.add(new FetchTask(new CaseInsensitiveString("grandparent-pipeline/parent-pipeline"), new CaseInsensitiveString("grandparent-stage"), new CaseInsensitiveString("grandparent-job"), "src", "dest"));
        Mockito.when(pipelineConfig.getFetchTasks()).thenReturn(fetchTasks);
        Assert.assertThat(material.isUsedInFetchArtifact(pipelineConfig), Matchers.is(true));
    }

    @Test
    public void shouldDetectDependencyMaterialNotUsedInFetchArtifact() {
        DependencyMaterial material = new DependencyMaterial(new CaseInsensitiveString("pipeline-foo"), new CaseInsensitiveString("stage-bar"));
        PipelineConfig pipelineConfig = Mockito.mock(PipelineConfig.class);
        ArrayList<FetchTask> fetchTasks = new ArrayList<>();
        fetchTasks.add(new FetchTask(new CaseInsensitiveString("something"), new CaseInsensitiveString("new"), "src", "dest"));
        fetchTasks.add(new FetchTask(new CaseInsensitiveString("another"), new CaseInsensitiveString("boo"), new CaseInsensitiveString("foo"), "src", "dest"));
        Mockito.when(pipelineConfig.getFetchTasks()).thenReturn(fetchTasks);
        Assert.assertThat(material.isUsedInFetchArtifact(pipelineConfig), Matchers.is(false));
    }

    @Test
    public void shouldGetAttributesAllFields() {
        DependencyMaterial material = new DependencyMaterial(new CaseInsensitiveString("pipeline-name"), new CaseInsensitiveString("stage-name"));
        Map<String, Object> attributesWithSecureFields = material.getAttributes(true);
        assertAttributes(attributesWithSecureFields);
        Map<String, Object> attributesWithoutSecureFields = material.getAttributes(false);
        assertAttributes(attributesWithoutSecureFields);
    }

    @Test
    public void shouldHandleNullOriginDuringValidationWhenUpstreamPipelineDoesNotExist() {
        DependencyMaterialConfig dependencyMaterialConfig = new DependencyMaterialConfig(new CaseInsensitiveString("upstream_stage"), new CaseInsensitiveString("upstream_pipeline"), new CaseInsensitiveString("stage"));
        PipelineConfig pipeline = new PipelineConfig(new CaseInsensitiveString("p"), new MaterialConfigs());
        pipeline.setOrigin(null);
        dependencyMaterialConfig.validateTree(PipelineConfigSaveValidationContext.forChain(true, "group", new BasicCruiseConfig(), pipeline));
        Assert.assertThat(dependencyMaterialConfig.errors().on(PIPELINE_STAGE_NAME), Matchers.is("Pipeline with name 'upstream_pipeline' does not exist, it is defined as a dependency for pipeline 'p' (cruise-config.xml)"));
    }

    @Test
    public void shouldHandleNullOriginDuringValidationWhenUpstreamStageDoesNotExist() {
        CruiseConfig cruiseConfig = GoConfigMother.pipelineHavingJob("upstream_pipeline", "upstream_stage", "j1", null, null);
        DependencyMaterialConfig dependencyMaterialConfig = new DependencyMaterialConfig(new CaseInsensitiveString("upstream_pipeline"), new CaseInsensitiveString("does_not_exist"));
        PipelineConfig pipeline = new PipelineConfig(new CaseInsensitiveString("downstream"), new MaterialConfigs());
        pipeline.setOrigin(null);
        dependencyMaterialConfig.validateTree(PipelineConfigSaveValidationContext.forChain(true, "group", cruiseConfig, pipeline));
        Assert.assertThat(dependencyMaterialConfig.errors().on(PIPELINE_STAGE_NAME), Matchers.is("Stage with name 'does_not_exist' does not exist on pipeline 'upstream_pipeline', it is being referred to from pipeline 'downstream' (cruise-config.xml)"));
    }

    @Test
    public void shouldReturnFalseForDependencyMaterial_supportsDestinationFolder() throws Exception {
        DependencyMaterial material = new DependencyMaterial();
        Assert.assertThat(material.supportsDestinationFolder(), Matchers.is(false));
    }
}

