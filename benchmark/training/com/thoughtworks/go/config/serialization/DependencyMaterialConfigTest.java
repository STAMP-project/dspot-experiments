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
package com.thoughtworks.go.config.serialization;


import AbstractMaterialConfig.MATERIAL_NAME;
import DependencyMaterialConfig.PIPELINE_STAGE_NAME;
import com.thoughtworks.go.config.CruiseConfig;
import com.thoughtworks.go.config.MagicalGoConfigXmlLoader;
import com.thoughtworks.go.config.MagicalGoConfigXmlWriter;
import com.thoughtworks.go.config.PipelineConfig;
import com.thoughtworks.go.config.materials.MaterialConfigs;
import com.thoughtworks.go.config.materials.dependency.DependencyMaterialConfig;
import com.thoughtworks.go.config.materials.perforce.P4MaterialConfig;
import com.thoughtworks.go.config.remote.FileConfigOrigin;
import com.thoughtworks.go.domain.ConfigErrors;
import com.thoughtworks.go.domain.materials.dependency.NewGoConfigMother;
import com.thoughtworks.go.helper.GoConfigMother;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class DependencyMaterialConfigTest {
    private MagicalGoConfigXmlWriter writer;

    private MagicalGoConfigXmlLoader loader;

    private CruiseConfig config;

    private PipelineConfig pipelineConfig;

    @Test
    public void shouldBeAbleToLoadADependencyMaterialFromConfig() throws Exception {
        String xml = "<pipeline pipelineName=\"pipeline-name\" stageName=\"stage-name\" />";
        DependencyMaterialConfig material = loader.fromXmlPartial(xml, DependencyMaterialConfig.class);
        Assert.assertThat(material.getPipelineName(), Matchers.is(new CaseInsensitiveString("pipeline-name")));
        Assert.assertThat(material.getStageName(), Matchers.is(new CaseInsensitiveString("stage-name")));
        Assert.assertThat(writer.toXmlPartial(material), Matchers.is(xml));
    }

    @Test
    public void shouldBeAbleToSaveADependencyMaterialToConfig() throws Exception {
        DependencyMaterialConfig originalMaterial = new DependencyMaterialConfig(new CaseInsensitiveString("pipeline-name"), new CaseInsensitiveString("stage-name"));
        NewGoConfigMother mother = new NewGoConfigMother();
        mother.addPipeline("pipeline-name", "stage-name", "job-name");
        mother.addPipeline("dependent", "stage-name", "job-name").addMaterialConfig(originalMaterial);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        writer.write(mother.cruiseConfig(), buffer, false);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer.toByteArray());
        CruiseConfig config = loader.loadConfigHolder(IOUtils.toString(inputStream, StandardCharsets.UTF_8)).config;
        DependencyMaterialConfig material = ((DependencyMaterialConfig) (config.pipelineConfigByName(new CaseInsensitiveString("dependent")).materialConfigs().get(1)));
        Assert.assertThat(material, Matchers.is(originalMaterial));
        Assert.assertThat(material.getPipelineName(), Matchers.is(new CaseInsensitiveString("pipeline-name")));
        Assert.assertThat(material.getStageName(), Matchers.is(new CaseInsensitiveString("stage-name")));
    }

    @Test
    public void shouldBeAbleToHaveADependencyAndOneOtherMaterial() throws Exception {
        NewGoConfigMother mother = new NewGoConfigMother();
        mother.addPipeline("pipeline-name", "stage-name", "job-name");
        PipelineConfig pipelineConfig = mother.addPipeline("dependent", "stage-name", "job-name", new DependencyMaterialConfig(new CaseInsensitiveString("pipeline-name"), new CaseInsensitiveString("stage-name")));
        pipelineConfig.addMaterialConfig(new P4MaterialConfig("localhost:1666", "foo"));
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        CruiseConfig cruiseConfig = mother.cruiseConfig();
        writer.write(cruiseConfig, buffer, false);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer.toByteArray());
        CruiseConfig config = loader.loadConfigHolder(IOUtils.toString(inputStream, StandardCharsets.UTF_8)).config;
        MaterialConfigs materialConfigs = config.pipelineConfigByName(new CaseInsensitiveString("dependent")).materialConfigs();
        Assert.assertThat(materialConfigs.get(0), Matchers.is(Matchers.instanceOf(DependencyMaterialConfig.class)));
        Assert.assertThat(materialConfigs.get(1), Matchers.is(Matchers.instanceOf(P4MaterialConfig.class)));
    }

    @Test
    public void shouldAddErrorForInvalidMaterialName() {
        DependencyMaterialConfig materialConfig = new DependencyMaterialConfig(new CaseInsensitiveString("wrong name"), new CaseInsensitiveString("pipeline-foo"), new CaseInsensitiveString("stage-bar"));
        materialConfig.validate(ConfigSaveValidationContext.forChain(new BasicCruiseConfig(), pipelineConfig));
        Assert.assertThat(materialConfig.errors().on(MATERIAL_NAME), Matchers.is("Invalid material name 'wrong name'. This must be alphanumeric and can contain underscores and periods (however, it cannot start with a period). The maximum allowed length is 255 characters."));
    }

    @Test
    public void shouldAddErrorWhenInvalidPipelineNameStage() {
        DependencyMaterialConfig dependencyMaterialConfig = new DependencyMaterialConfig();
        Map<String, String> configMap = new HashMap<>();
        configMap.put(PIPELINE_STAGE_NAME, "invalid pipeline stage");
        dependencyMaterialConfig.setConfigAttributes(configMap);
        Assert.assertThat(dependencyMaterialConfig.getPipelineStageName(), Matchers.is("invalid pipeline stage"));
        Assert.assertThat(dependencyMaterialConfig.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(dependencyMaterialConfig.errors().on(PIPELINE_STAGE_NAME), Matchers.is("'invalid pipeline stage' should conform to the pattern 'pipeline [stage]'"));
    }

    @Test
    public void shouldNotBombValidationWhenMaterialNameIsNotSet() {
        DependencyMaterialConfig dependencyMaterialConfig = new DependencyMaterialConfig(new CaseInsensitiveString("pipeline-foo"), new CaseInsensitiveString("stage-bar"));
        dependencyMaterialConfig.validate(ConfigSaveValidationContext.forChain(new BasicCruiseConfig(), pipelineConfig));
        Assert.assertThat(dependencyMaterialConfig.errors().on(MATERIAL_NAME), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldNOTBeValidIfThePipelineExistsButTheStageDoesNot() throws Exception {
        DependencyMaterialConfig dependencyMaterialConfig = new DependencyMaterialConfig(new CaseInsensitiveString("pipeline2"), new CaseInsensitiveString("stage-not-existing does not exist!"));
        dependencyMaterialConfig.validate(ConfigSaveValidationContext.forChain(config, pipelineConfig));
        ConfigErrors configErrors = dependencyMaterialConfig.errors();
        Assert.assertThat(configErrors.isEmpty(), Matchers.is(false));
        Assert.assertThat(configErrors.on(PIPELINE_STAGE_NAME), Matchers.containsString("Stage with name 'stage-not-existing does not exist!' does not exist on pipeline 'pipeline2'"));
    }

    @Test
    public void shouldNOTBeValidIfTheReferencedPipelineDoesNotExist() throws Exception {
        CruiseConfig config = GoConfigMother.configWithPipelines("pipeline1", "pipeline2", "pipeline3", "go");
        DependencyMaterialConfig dependencyMaterialConfig = new DependencyMaterialConfig(new CaseInsensitiveString("pipeline-not-exist"), new CaseInsensitiveString("stage"));
        dependencyMaterialConfig.validate(ConfigSaveValidationContext.forChain(config, pipelineConfig));
        ConfigErrors configErrors = dependencyMaterialConfig.errors();
        Assert.assertThat(configErrors.isEmpty(), Matchers.is(false));
        Assert.assertThat(configErrors.on(PIPELINE_STAGE_NAME), Matchers.containsString("Pipeline with name 'pipeline-not-exist' does not exist"));
    }

    @Test
    public void setConfigAttributes_shouldPopulateFromConfigAttributes() {
        DependencyMaterialConfig dependencyMaterialConfig = new DependencyMaterialConfig(new CaseInsensitiveString(""), new CaseInsensitiveString(""));
        Assert.assertThat(dependencyMaterialConfig.getPipelineStageName(), Matchers.is(Matchers.nullValue()));
        HashMap<String, String> configMap = new HashMap<>();
        configMap.put(MATERIAL_NAME, "name1");
        configMap.put(PIPELINE_STAGE_NAME, "pipeline-1 [stage-1]");
        dependencyMaterialConfig.setConfigAttributes(configMap);
        Assert.assertThat(dependencyMaterialConfig.getMaterialName(), Matchers.is(new CaseInsensitiveString("name1")));
        Assert.assertThat(dependencyMaterialConfig.getPipelineName(), Matchers.is(new CaseInsensitiveString("pipeline-1")));
        Assert.assertThat(dependencyMaterialConfig.getStageName(), Matchers.is(new CaseInsensitiveString("stage-1")));
        Assert.assertThat(dependencyMaterialConfig.getPipelineStageName(), Matchers.is("pipeline-1 [stage-1]"));
    }

    @Test
    public void setConfigAttributes_shouldNotPopulateNameFromConfigAttributesIfNameIsEmptyOrNull() {
        DependencyMaterialConfig dependencyMaterialConfig = new DependencyMaterialConfig(new CaseInsensitiveString("name2"), new CaseInsensitiveString("pipeline"), new CaseInsensitiveString("stage"));
        HashMap<String, String> configMap = new HashMap<>();
        configMap.put(MATERIAL_NAME, "");
        dependencyMaterialConfig.setConfigAttributes(configMap);
        Assert.assertThat(dependencyMaterialConfig.getMaterialName(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldValidateTree() {
        DependencyMaterialConfig dependencyMaterialConfig = new DependencyMaterialConfig(new CaseInsensitiveString("upstream_stage"), new CaseInsensitiveString("upstream_pipeline"), new CaseInsensitiveString("stage"));
        PipelineConfig pipeline = new PipelineConfig(new CaseInsensitiveString("p"), new MaterialConfigs());
        pipeline.setOrigin(new FileConfigOrigin());
        dependencyMaterialConfig.validateTree(PipelineConfigSaveValidationContext.forChain(true, "group", config, pipeline));
        Assert.assertThat(dependencyMaterialConfig.errors().on(PIPELINE_STAGE_NAME), Matchers.is("Pipeline with name 'upstream_pipeline' does not exist, it is defined as a dependency for pipeline 'p' (cruise-config.xml)"));
    }
}

