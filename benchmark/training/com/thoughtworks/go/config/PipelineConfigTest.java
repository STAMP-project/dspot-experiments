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


import AbstractMaterialConfig.MATERIAL_TYPE;
import Approval.MANUAL;
import Approval.SUCCESS;
import MingleConfig.BASE_URL;
import MingleConfig.MQL_GROUPING_CONDITIONS;
import MingleConfig.PROJECT_IDENTIFIER;
import MqlCriteria.MQL;
import PipelineConfig.BLANK_LABEL_TEMPLATE_ERROR_MESSAGE;
import PipelineConfig.CONFIGURATION_TYPE;
import PipelineConfig.CONFIGURATION_TYPE_STAGES;
import PipelineConfig.CONFIGURATION_TYPE_TEMPLATE;
import PipelineConfig.ENVIRONMENT_VARIABLES;
import PipelineConfig.INTEGRATION_TYPE;
import PipelineConfig.INTEGRATION_TYPE_MINGLE;
import PipelineConfig.INTEGRATION_TYPE_NONE;
import PipelineConfig.INTEGRATION_TYPE_TRACKING_TOOL;
import PipelineConfig.LABEL_TEMPLATE;
import PipelineConfig.LOCK_BEHAVIOR;
import PipelineConfig.LOCK_VALUE_LOCK_ON_FAILURE;
import PipelineConfig.LOCK_VALUE_NONE;
import PipelineConfig.LOCK_VALUE_UNLOCK_WHEN_FINISHED;
import PipelineConfig.MATERIALS;
import PipelineConfig.MINGLE_CONFIG;
import PipelineConfig.NAME;
import PipelineConfig.PARAMS;
import PipelineConfig.STAGE;
import PipelineConfig.TEMPLATE_NAME;
import PipelineConfig.TIMER_CONFIG;
import PipelineConfig.TRACKING_TOOL;
import PipelineLabel.COUNT_TEMPLATE;
import StageConfig.APPROVAL;
import StageConfig.JOBS;
import SvnMaterialConfig.CHECK_EXTERNALS;
import SvnMaterialConfig.PASSWORD;
import SvnMaterialConfig.TYPE;
import SvnMaterialConfig.URL;
import SvnMaterialConfig.USERNAME;
import TimerConfig.TIMER_ONLY_ON_CHANGES;
import TimerConfig.TIMER_SPEC;
import com.thoughtworks.go.config.materials.MaterialConfigs;
import com.thoughtworks.go.config.materials.PackageMaterialConfig;
import com.thoughtworks.go.config.materials.PluggableSCMMaterialConfig;
import com.thoughtworks.go.config.materials.git.GitMaterialConfig;
import com.thoughtworks.go.config.materials.svn.SvnMaterialConfig;
import com.thoughtworks.go.config.remote.FileConfigOrigin;
import com.thoughtworks.go.config.remote.RepoConfigOrigin;
import com.thoughtworks.go.domain.materials.MaterialConfig;
import com.thoughtworks.go.helper.MaterialConfigsMother;
import com.thoughtworks.go.helper.PipelineConfigMother;
import com.thoughtworks.go.helper.StageConfigMother;
import com.thoughtworks.go.security.CryptoException;
import com.thoughtworks.go.security.GoCipher;
import com.thoughtworks.go.util.DataStructureUtils;
import com.thoughtworks.go.util.Node;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class PipelineConfigTest {
    private static final String BUILDING_PLAN_NAME = "building";

    private EnvironmentVariablesConfig mockEnvironmentVariablesConfig = Mockito.mock(EnvironmentVariablesConfig.class);

    private ParamsConfig mockParamsConfig = Mockito.mock(ParamsConfig.class);

    public enum Foo {

        Bar,
        Baz;}

    @Test
    public void shouldFindByName() {
        PipelineConfig pipelineConfig = new PipelineConfig(new CaseInsensitiveString("pipeline"), null, completedStage(), buildingStage());
        Assert.assertThat(pipelineConfig.findBy(new CaseInsensitiveString("completed stage")).name(), Matchers.is(new CaseInsensitiveString("completed stage")));
    }

    @Test
    public void shouldReturnDuplicateWithoutName() {
        PipelineConfig pipelineConfig = PipelineConfigMother.pipelineConfig("somePipeline");
        PipelineConfig clonedPipelineConfig = pipelineConfig.duplicate();
        Assert.assertThat(clonedPipelineConfig.name(), Matchers.is(new CaseInsensitiveString("")));
        Assert.assertThat(clonedPipelineConfig.materialConfigs(), Matchers.is(pipelineConfig.materialConfigs()));
        Assert.assertThat(clonedPipelineConfig.getFirstStageConfig(), Matchers.is(pipelineConfig.getFirstStageConfig()));
    }

    @Test
    public void shouldReturnDuplicateWithPipelineNameEmptyIfFetchArtifactTaskIsFetchingFromSamePipeline() {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("somePipeline", "stage", "job");
        StageConfig stageConfig = pipelineConfig.get(0);
        JobConfig jobConfig = stageConfig.getJobs().get(0);
        Tasks originalTasks = jobConfig.getTasks();
        originalTasks.add(new FetchTask(pipelineConfig.name(), stageConfig.name(), jobConfig.name(), "src", "dest"));
        originalTasks.add(new FetchTask(new CaseInsensitiveString("some_other_pipeline"), stageConfig.name(), jobConfig.name(), "src", "dest"));
        PipelineConfig clone = pipelineConfig.duplicate();
        Tasks clonedTasks = clone.get(0).getJobs().get(0).getTasks();
        Assert.assertThat(getTargetPipelineName(), Matchers.is(new CaseInsensitiveString("")));
        Assert.assertThat(getTargetPipelineName(), Matchers.is(new CaseInsensitiveString("some_other_pipeline")));
        Assert.assertThat(getTargetPipelineName(), Matchers.is(pipelineConfig.name()));
    }

    // #6821
    @Test
    public void shouldCopyOverAllEnvironmentVariablesWhileCloningAPipeline() throws CryptoException {
        PipelineConfig source = PipelineConfigMother.createPipelineConfig("somePipeline", "stage", "job");
        source.addEnvironmentVariable("k1", "v1");
        source.addEnvironmentVariable("k2", "v2");
        GoCipher goCipher = new GoCipher();
        source.addEnvironmentVariable(new EnvironmentVariableConfig(goCipher, "secret_key", "secret", true));
        PipelineConfig cloned = source.duplicate();
        EnvironmentVariablesConfig clonedEnvVariables = cloned.getPlainTextVariables();
        EnvironmentVariablesConfig sourceEnvVariables = source.getPlainTextVariables();
        Assert.assertThat(clonedEnvVariables.size(), Matchers.is(sourceEnvVariables.size()));
        clonedEnvVariables.getPlainTextVariables().containsAll(sourceEnvVariables.getPlainTextVariables());
        Assert.assertThat(cloned.getSecureVariables().size(), Matchers.is(source.getSecureVariables().size()));
        Assert.assertThat(cloned.getSecureVariables().containsAll(source.getSecureVariables()), Matchers.is(true));
    }

    @Test
    public void shouldGetStageByName() {
        PipelineConfig pipelineConfig = new PipelineConfig(new CaseInsensitiveString("pipeline"), null, completedStage(), buildingStage());
        Assert.assertThat(pipelineConfig.getStage(new CaseInsensitiveString("COMpleTEd stage")).name(), Matchers.is(new CaseInsensitiveString("completed stage")));
        Assert.assertThat(pipelineConfig.getStage(new CaseInsensitiveString("Does-not-exist")), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldReturnFalseIfThereIsNoNextStage() {
        PipelineConfig pipelineConfig = new PipelineConfig(new CaseInsensitiveString("pipeline"), null, completedStage(), buildingStage());
        Assert.assertThat(pipelineConfig.hasNextStage(buildingStage().name()), Matchers.is(false));
    }

    @Test
    public void shouldReturnFalseIfThereIsNextStage() {
        PipelineConfig pipelineConfig = new PipelineConfig(new CaseInsensitiveString("pipeline"), null, completedStage(), buildingStage());
        Assert.assertThat(pipelineConfig.hasNextStage(completedStage().name()), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseThePassInStageDoesNotExist() {
        PipelineConfig pipelineConfig = new PipelineConfig(new CaseInsensitiveString("pipeline"), null, completedStage(), buildingStage());
        Assert.assertThat(pipelineConfig.hasNextStage(new CaseInsensitiveString("notExist")), Matchers.is(false));
    }

    @Test
    public void shouldReturnTrueIfThereNoStagesDefined() {
        PipelineConfig pipelineConfig = new PipelineConfig(new CaseInsensitiveString("pipeline"), null);
        Assert.assertThat(pipelineConfig.hasNextStage(completedStage().name()), Matchers.is(false));
    }

    @Test
    public void shouldGetDependenciesAsNode() throws Exception {
        PipelineConfig pipelineConfig = new PipelineConfig();
        pipelineConfig.addMaterialConfig(new com.thoughtworks.go.config.materials.dependency.DependencyMaterialConfig(new CaseInsensitiveString("framework"), new CaseInsensitiveString("dev")));
        pipelineConfig.addMaterialConfig(new com.thoughtworks.go.config.materials.dependency.DependencyMaterialConfig(new CaseInsensitiveString("middleware"), new CaseInsensitiveString("dev")));
        Assert.assertThat(pipelineConfig.getDependenciesAsNode(), Matchers.is(new Node(new Node.DependencyNode(new CaseInsensitiveString("framework"), new CaseInsensitiveString("dev")), new Node.DependencyNode(new CaseInsensitiveString("middleware"), new CaseInsensitiveString("dev")))));
    }

    @Test
    public void shouldReturnTrueIfFirstStageIsManualApproved() {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("pipeline", "stage", "build");
        pipelineConfig.getFirstStageConfig().updateApproval(Approval.manualApproval());
        Assert.assertThat("First stage should be manual approved", pipelineConfig.isFirstStageManualApproval(), Matchers.is(true));
    }

    @Test
    public void shouldThrowExceptionForEmptyPipeline() {
        PipelineConfig pipelineConfig = new PipelineConfig(new CaseInsensitiveString("cruise"), new MaterialConfigs());
        try {
            pipelineConfig.isFirstStageManualApproval();
            Assert.fail("Should throw exception if pipeline has no pipeline");
        } catch (IllegalStateException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString((("Pipeline [" + (pipelineConfig.name())) + "] doesn't have any stage")));
        }
    }

    @Test
    public void shouldThrowExceptionOnAddingTemplatesIfItAlreadyHasStages() {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("pipeline", "stage", "build");
        try {
            PipelineTemplateConfig template = new PipelineTemplateConfig();
            template.add(StageConfigMother.stageConfig("first"));
            pipelineConfig.setTemplateName(new CaseInsensitiveString("some-template"));
            Assert.fail("Should throw exception because the pipeline has stages already");
        } catch (RuntimeException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("Cannot set template 'some-template' on pipeline 'pipeline' because it already has stages defined"));
        }
    }

    @Test
    public void shouldBombWhenAddingStagesIfItAlreadyHasATemplate() {
        PipelineConfig pipelineConfig = new PipelineConfig(new CaseInsensitiveString("mingle"), null);
        try {
            pipelineConfig.setTemplateName(new CaseInsensitiveString("some-template"));
            pipelineConfig.add(StageConfigMother.stageConfig("second"));
            Assert.fail("Should throw exception because pipeline already has a template");
        } catch (RuntimeException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("Cannot add stage 'second' to pipeline 'mingle', which already references template 'some-template'."));
        }
    }

    @Test
    public void shouldKnowIfATemplateWasApplied() {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("pipeline", "stage", "build");
        Assert.assertThat(pipelineConfig.hasTemplateApplied(), Matchers.is(false));
        pipelineConfig.clear();
        PipelineTemplateConfig template = new PipelineTemplateConfig();
        template.add(StageConfigMother.stageConfig("first"));
        pipelineConfig.usingTemplate(template);
        Assert.assertThat(pipelineConfig.hasTemplateApplied(), Matchers.is(true));
    }

    @Test
    public void shouldValidateCorrectPipelineLabelWithoutAnyMaterial() {
        PipelineConfig pipelineConfig = new PipelineConfig(new CaseInsensitiveString("cruise"), new MaterialConfigs(), new StageConfig(new CaseInsensitiveString("first"), new JobConfigs()));
        pipelineConfig.setLabelTemplate("pipeline-${COUNT}-alpha");
        pipelineConfig.validate(null);
        Assert.assertThat(pipelineConfig.errors().isEmpty(), Matchers.is(true));
        Assert.assertThat(pipelineConfig.errors().on(LABEL_TEMPLATE), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldValidateMissingLabel() {
        PipelineConfig pipelineConfig = createAndValidatePipelineLabel(null);
        Assert.assertThat(pipelineConfig.errors().on(LABEL_TEMPLATE), Matchers.is(BLANK_LABEL_TEMPLATE_ERROR_MESSAGE));
        pipelineConfig = createAndValidatePipelineLabel("");
        Assert.assertThat(pipelineConfig.errors().on(LABEL_TEMPLATE), Matchers.is(BLANK_LABEL_TEMPLATE_ERROR_MESSAGE));
    }

    @Test
    public void shouldValidateCorrectPipelineLabelWithoutTruncationSyntax() {
        String labelFormat = "pipeline-${COUNT}-${git}-454";
        PipelineConfig pipelineConfig = createAndValidatePipelineLabel(labelFormat);
        Assert.assertThat(pipelineConfig.errors().on(LABEL_TEMPLATE), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldValidatePipelineLabelWithNonExistingMaterial() {
        String labelFormat = "pipeline-${COUNT}-${NoSuchMaterial}";
        PipelineConfig pipelineConfig = createAndValidatePipelineLabel(labelFormat);
        Assert.assertThat(pipelineConfig.errors().on(LABEL_TEMPLATE), ArgumentMatchers.startsWith("You have defined a label template in pipeline"));
    }

    @Test
    public void shouldValidatePipelineLabelWithEnvironmentVariable() {
        String labelFormat = "pipeline-${COUNT}-${env:SOME_VAR}";
        PipelineConfig pipelineConfig = createAndValidatePipelineLabel(labelFormat);
        Assert.assertThat(pipelineConfig.errors().on(LABEL_TEMPLATE), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldValidateCorrectPipelineLabelWithTruncationSyntax() {
        String labelFormat = "pipeline-${COUNT}-${git[:7]}-alpha";
        PipelineConfig pipelineConfig = createAndValidatePipelineLabel(labelFormat);
        Assert.assertThat(pipelineConfig.errors().on(LABEL_TEMPLATE), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldValidatePipelineLabelWithBrokenTruncationSyntax1() {
        String labelFormat = "pipeline-${COUNT}-${git[:7}-alpha";
        PipelineConfig pipelineConfig = createAndValidatePipelineLabel(labelFormat);
        String expectedLabelTemplate = "Invalid label 'pipeline-${COUNT}-${git[:7}-alpha'.";
        Assert.assertThat(pipelineConfig.errors().on(LABEL_TEMPLATE), ArgumentMatchers.startsWith(expectedLabelTemplate));
    }

    @Test
    public void shouldValidatePipelineLabelWithBrokenTruncationSyntax2() {
        String labelFormat = "pipeline-${COUNT}-${git[7]}-alpha";
        PipelineConfig pipelineConfig = createAndValidatePipelineLabel(labelFormat);
        String expectedLabelTemplate = "Invalid label 'pipeline-${COUNT}-${git[7]}-alpha'.";
        Assert.assertThat(pipelineConfig.errors().on(LABEL_TEMPLATE), ArgumentMatchers.startsWith(expectedLabelTemplate));
    }

    @Test
    public void shouldValidateIncorrectPipelineLabelWithTruncationSyntax() {
        String labelFormat = "pipeline-${COUNT}-${noSuch[:7]}-alpha";
        PipelineConfig pipelineConfig = createAndValidatePipelineLabel(labelFormat);
        Assert.assertThat(pipelineConfig.errors().on(LABEL_TEMPLATE), ArgumentMatchers.startsWith("You have defined a label template in pipeline"));
    }

    @Test
    public void shouldNotAllowLabelTemplateWithLengthOfZeroInTruncationSyntax() throws Exception {
        String labelFormat = "pipeline-${COUNT}-${git[:0]}-alpha";
        PipelineConfig pipelineConfig = createAndValidatePipelineLabel(labelFormat);
        Assert.assertThat(pipelineConfig.errors().on(LABEL_TEMPLATE), Matchers.is(String.format("Length of zero not allowed on label %s defined on pipeline %s.", labelFormat, pipelineConfig.name())));
    }

    @Test
    public void shouldNotAllowLabelTemplateWithLengthOfZeroInTruncationSyntax2() throws Exception {
        String labelFormat = "pipeline-${COUNT}-${git[:0]}${one[:00]}-alpha";
        PipelineConfig pipelineConfig = createAndValidatePipelineLabel(labelFormat);
        Assert.assertThat(pipelineConfig.errors().on(LABEL_TEMPLATE), Matchers.is(String.format("Length of zero not allowed on label %s defined on pipeline %s.", labelFormat, pipelineConfig.name())));
    }

    @Test
    public void shouldSetPipelineConfigFromConfigAttributes() {
        PipelineConfig pipelineConfig = new PipelineConfig();
        HashMap mingleConfigMap = new HashMap();
        mingleConfigMap.put("mingleconfig", "mingleconfig");
        HashMap trackingToolMap = new HashMap();
        trackingToolMap.put("trackingtool", "trackingtool");
        HashMap timerConfigMap = new HashMap();
        String cronSpec = "0 0 11 * * ?";
        timerConfigMap.put(TIMER_SPEC, cronSpec);
        Map configMap = new HashMap();
        configMap.put(LABEL_TEMPLATE, "LABEL123-${COUNT}");
        configMap.put(MINGLE_CONFIG, mingleConfigMap);
        configMap.put(TRACKING_TOOL, trackingToolMap);
        configMap.put(TIMER_CONFIG, timerConfigMap);
        pipelineConfig.setConfigAttributes(configMap);
        Assert.assertThat(pipelineConfig.getLabelTemplate(), Matchers.is("LABEL123-${COUNT}"));
        Assert.assertThat(pipelineConfig.getTimer().getTimerSpec(), Matchers.is(cronSpec));
        Assert.assertThat(pipelineConfig.getTimer().shouldTriggerOnlyOnChanges(), Matchers.is(false));
    }

    @Test
    public void shouldSetPipelineConfigFromConfigAttributesForTimerConfig() {
        PipelineConfig pipelineConfig = new PipelineConfig();
        HashMap timerConfigMap = new HashMap();
        String cronSpec = "0 0 11 * * ?";
        timerConfigMap.put(TIMER_SPEC, cronSpec);
        timerConfigMap.put(TIMER_ONLY_ON_CHANGES, "1");
        Map configMap = new HashMap();
        configMap.put(LABEL_TEMPLATE, "LABEL123-${COUNT}");
        configMap.put(TIMER_CONFIG, timerConfigMap);
        pipelineConfig.setConfigAttributes(configMap);
        Assert.assertThat(pipelineConfig.getLabelTemplate(), Matchers.is("LABEL123-${COUNT}"));
        Assert.assertThat(pipelineConfig.getTimer().getTimerSpec(), Matchers.is(cronSpec));
        Assert.assertThat(pipelineConfig.getTimer().shouldTriggerOnlyOnChanges(), Matchers.is(true));
    }

    @Test
    public void shouldSetLabelTemplateToDefaultValueIfBlankIsEnteredWhileSettingConfigAttributes() {
        PipelineConfig pipelineConfig = new PipelineConfig();
        Map configMap = new HashMap();
        configMap.put(LABEL_TEMPLATE, "");
        pipelineConfig.setConfigAttributes(configMap);
        Assert.assertThat(pipelineConfig.getLabelTemplate(), Matchers.is(COUNT_TEMPLATE));
    }

    @Test
    public void shouldNotSetLockStatusOnPipelineConfigWhenValueIsNone() {
        Map configMap = new HashMap();
        configMap.put(LOCK_BEHAVIOR, LOCK_VALUE_NONE);
        PipelineConfig pipelineConfig = new PipelineConfig();
        pipelineConfig.setConfigAttributes(configMap);
        Assert.assertThat(pipelineConfig.isLockable(), Matchers.is(false));
    }

    @Test
    public void shouldSetLockStatusOnPipelineConfigWhenValueIsLockOnFailure() {
        Map configMap = new HashMap();
        configMap.put(LOCK_BEHAVIOR, LOCK_VALUE_LOCK_ON_FAILURE);
        PipelineConfig pipelineConfig = new PipelineConfig();
        pipelineConfig.setConfigAttributes(configMap);
        Assert.assertThat(pipelineConfig.isLockable(), Matchers.is(true));
    }

    @Test
    public void shouldSetLockStatusOnPipelineConfigWhenValueIsUnlockWhenFinished() {
        Map configMap = new HashMap();
        configMap.put(LOCK_BEHAVIOR, LOCK_VALUE_UNLOCK_WHEN_FINISHED);
        PipelineConfig pipelineConfig = new PipelineConfig();
        pipelineConfig.setConfigAttributes(configMap);
        Assert.assertThat(pipelineConfig.isPipelineUnlockableWhenFinished(), Matchers.is(true));
    }

    @Test
    public void isNotLockableWhenLockValueHasNotBeenSet() {
        PipelineConfig pipelineConfig = new PipelineConfig();
        Assert.assertThat(pipelineConfig.hasExplicitLock(), Matchers.is(false));
        Assert.assertThat(pipelineConfig.isLockable(), Matchers.is(false));
    }

    @Test
    public void shouldValidateLockBehaviorValues() throws Exception {
        Map configMap = new HashMap();
        configMap.put(LOCK_BEHAVIOR, "someRandomValue");
        PipelineConfig pipelineConfig = PipelineConfigMother.pipelineConfig("pipeline1");
        pipelineConfig.setConfigAttributes(configMap);
        pipelineConfig.validate(null);
        Assert.assertThat(pipelineConfig.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(pipelineConfig.errors().on(LOCK_BEHAVIOR), Matchers.containsString("Lock behavior has an invalid value (someRandomValue). Valid values are: "));
    }

    @Test
    public void shouldAllowNullForLockBehavior() throws Exception {
        PipelineConfig pipelineConfig = PipelineConfigMother.pipelineConfig("pipeline1");
        pipelineConfig.setLockBehaviorIfNecessary(null);
        pipelineConfig.validate(null);
        Assert.assertThat(pipelineConfig.errors().toString(), pipelineConfig.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldPopulateEnvironmentVariablesFromAttributeMap() {
        PipelineConfig pipelineConfig = new PipelineConfig();
        HashMap map = new HashMap();
        HashMap valueHashMap = new HashMap();
        valueHashMap.put("name", "FOO");
        valueHashMap.put("value", "BAR");
        map.put(ENVIRONMENT_VARIABLES, valueHashMap);
        pipelineConfig.setVariables(mockEnvironmentVariablesConfig);
        pipelineConfig.setConfigAttributes(map);
        Mockito.verify(mockEnvironmentVariablesConfig).setConfigAttributes(valueHashMap);
    }

    @Test
    public void shouldPopulateParamsFromAttributeMapWhenConfigurationTypeIsNotSet() {
        PipelineConfig pipelineConfig = new PipelineConfig();
        final HashMap map = new HashMap();
        final HashMap valueHashMap = new HashMap();
        valueHashMap.put("param-name", "FOO");
        valueHashMap.put("param-value", "BAR");
        map.put(PARAMS, valueHashMap);
        pipelineConfig.setParams(mockParamsConfig);
        pipelineConfig.setConfigAttributes(map);
        Mockito.verify(mockParamsConfig).setConfigAttributes(valueHashMap);
    }

    @Test
    public void shouldPopulateParamsFromAttributeMapIfConfigurationTypeIsTemplate() {
        PipelineConfig pipelineConfig = new PipelineConfig();
        HashMap map = new HashMap();
        HashMap valueHashMap = new HashMap();
        valueHashMap.put("param-name", "FOO");
        valueHashMap.put("param-value", "BAR");
        map.put(PARAMS, valueHashMap);
        map.put(CONFIGURATION_TYPE, CONFIGURATION_TYPE_TEMPLATE);
        map.put(TEMPLATE_NAME, "template");
        pipelineConfig.setParams(mockParamsConfig);
        pipelineConfig.setConfigAttributes(map);
        Mockito.verify(mockParamsConfig).setConfigAttributes(valueHashMap);
    }

    @Test
    public void shouldNotPopulateParamsFromAttributeMapIfConfigurationTypeIsStages() {
        PipelineConfig pipelineConfig = new PipelineConfig();
        HashMap map = new HashMap();
        HashMap valueHashMap = new HashMap();
        valueHashMap.put("param-name", "FOO");
        valueHashMap.put("param-value", "BAR");
        map.put(PARAMS, valueHashMap);
        map.put(CONFIGURATION_TYPE, CONFIGURATION_TYPE_STAGES);
        pipelineConfig.setParams(mockParamsConfig);
        pipelineConfig.setConfigAttributes(map);
        Mockito.verify(mockParamsConfig, Mockito.never()).setConfigAttributes(valueHashMap);
    }

    @Test
    public void shouldSetTheCorrectIntegrationType() {
        PipelineConfig pipelineConfig = new PipelineConfig();
        Assert.assertThat(pipelineConfig.getIntegrationType(), Matchers.is(INTEGRATION_TYPE_NONE));
        pipelineConfig = new PipelineConfig();
        pipelineConfig.setTrackingTool(new TrackingTool("link", "regex"));
        Assert.assertThat(pipelineConfig.getIntegrationType(), Matchers.is(INTEGRATION_TYPE_TRACKING_TOOL));
        pipelineConfig = new PipelineConfig();
        pipelineConfig.setMingleConfig(new MingleConfig("baseUri", "projId"));
        Assert.assertThat(pipelineConfig.getIntegrationType(), Matchers.is(INTEGRATION_TYPE_MINGLE));
    }

    @Test
    public void shouldSetIntegrationTypeToMingleInCaseAnEmptyMingleConfigIsSubmitted() {
        PipelineConfig pipelineConfig = new PipelineConfig();
        MingleConfig mingleConfig = new MingleConfig();
        mingleConfig.addError(BASE_URL, "some error");
        pipelineConfig.setMingleConfig(mingleConfig);
        String integrationType = pipelineConfig.getIntegrationType();
        Assert.assertThat(integrationType, Matchers.is(INTEGRATION_TYPE_MINGLE));
    }

    @Test
    public void shouldPopulateTrackingToolWhenIntegrationTypeIsTrackingToolAndLinkAndRegexAreDefined() {
        PipelineConfig pipelineConfig = new PipelineConfig();
        pipelineConfig.setMingleConfig(new MingleConfig("baseUri", "go"));
        HashMap map = new HashMap();
        HashMap valueHashMap = new HashMap();
        valueHashMap.put("link", "GoleyLink");
        valueHashMap.put("regex", "GoleyRegex");
        map.put(TRACKING_TOOL, valueHashMap);
        map.put(INTEGRATION_TYPE, INTEGRATION_TYPE_TRACKING_TOOL);
        pipelineConfig.setConfigAttributes(map);
        Assert.assertThat(pipelineConfig.getTrackingTool(), Matchers.is(new TrackingTool("GoleyLink", "GoleyRegex")));
        Assert.assertThat(pipelineConfig.getMingleConfig(), Matchers.is(new MingleConfig()));
        Assert.assertThat(pipelineConfig.getIntegrationType(), Matchers.is(INTEGRATION_TYPE_TRACKING_TOOL));
    }

    @Test
    public void shouldPopulateMingleConfigWhenIntegrationTypeIsMingle() {
        PipelineConfig pipelineConfig = new PipelineConfig();
        pipelineConfig.setTrackingTool(new TrackingTool("link", "regex"));
        Map map = new HashMap();
        HashMap valueHashMap = new HashMap();
        valueHashMap.put(BASE_URL, "url");
        valueHashMap.put(PROJECT_IDENTIFIER, "identifier");
        valueHashMap.put(MQL, "criteria");
        valueHashMap.put(MQL_GROUPING_CONDITIONS, valueHashMap);
        map.put(MINGLE_CONFIG, valueHashMap);
        map.put(INTEGRATION_TYPE, INTEGRATION_TYPE_MINGLE);
        pipelineConfig.setConfigAttributes(map);
        Assert.assertThat(pipelineConfig.getMingleConfig(), Matchers.is(new MingleConfig("url", "identifier", "criteria")));
        Assert.assertThat(pipelineConfig.getTrackingTool(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(pipelineConfig.getIntegrationType(), Matchers.is(INTEGRATION_TYPE_MINGLE));
    }

    @Test
    public void shouldResetMingleConfigWhenIntegrationTypeIsNone() {
        PipelineConfig pipelineConfig = new PipelineConfig();
        pipelineConfig.setMingleConfig(new MingleConfig("baseUri", "go"));
        Map map = new HashMap();
        map.put(INTEGRATION_TYPE, INTEGRATION_TYPE_NONE);
        pipelineConfig.setConfigAttributes(map);
        Assert.assertThat(pipelineConfig.getMingleConfig(), Matchers.is(new MingleConfig()));
        Assert.assertThat(pipelineConfig.getIntegrationType(), Matchers.is(INTEGRATION_TYPE_NONE));
    }

    @Test
    public void shouldResetTrackingToolWhenIntegrationTypeIsNone() {
        PipelineConfig pipelineConfig = new PipelineConfig();
        pipelineConfig.setTrackingTool(new TrackingTool("link", "regex"));
        Map map = new HashMap();
        map.put(INTEGRATION_TYPE, INTEGRATION_TYPE_NONE);
        pipelineConfig.setConfigAttributes(map);
        Assert.assertThat(pipelineConfig.getTrackingTool(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(pipelineConfig.getIntegrationType(), Matchers.is(INTEGRATION_TYPE_NONE));
    }

    @Test
    public void shouldGetIntegratedTrackingToolWhenGenericTrackingToolIsDefined() {
        PipelineConfig pipelineConfig = PipelineConfigMother.pipelineConfig("pipeline");
        TrackingTool trackingTool = new TrackingTool("http://example.com/${ID}", "Foo-(\\d+)");
        pipelineConfig.setTrackingTool(trackingTool);
        pipelineConfig.setMingleConfig(new MingleConfig());
        Assert.assertThat(pipelineConfig.getIntegratedTrackingTool(), Matchers.is(Optional.of(trackingTool)));
    }

    @Test
    public void shouldGetIntegratedTrackingToolWhenMingleTrackingToolIsDefined() {
        PipelineConfig pipelineConfig = PipelineConfigMother.pipelineConfig("pipeline");
        MingleConfig mingleConfig = new MingleConfig("http://example.com", "go-project");
        pipelineConfig.setTrackingTool(null);
        pipelineConfig.setMingleConfig(mingleConfig);
        Assert.assertThat(pipelineConfig.getIntegratedTrackingTool(), Matchers.is(Optional.of(mingleConfig.asTrackingTool())));
    }

    @Test
    public void shouldGetIntegratedTrackingToolWhenNoneIsDefined() {
        PipelineConfig pipelineConfig = PipelineConfigMother.pipelineConfig("pipeline");
        pipelineConfig.setTrackingTool(null);
        pipelineConfig.setMingleConfig(new MingleConfig());
        Assert.assertThat(pipelineConfig.getIntegratedTrackingTool(), Matchers.is(Optional.empty()));
    }

    @Test
    public void shouldGetTheCorrectConfigurationType() {
        PipelineConfig pipelineConfigWithTemplate = PipelineConfigMother.pipelineConfigWithTemplate("pipeline", "template");
        Assert.assertThat(pipelineConfigWithTemplate.getConfigurationType(), Matchers.is(CONFIGURATION_TYPE_TEMPLATE));
        PipelineConfig pipelineConfig = PipelineConfigMother.pipelineConfig("pipeline");
        Assert.assertThat(pipelineConfig.getConfigurationType(), Matchers.is(CONFIGURATION_TYPE_STAGES));
    }

    @Test
    public void shouldUseTemplateWhenSetConfigAttributesContainsTemplateName() {
        PipelineConfig pipelineConfig = PipelineConfigMother.pipelineConfig("pipeline");
        Assert.assertThat(pipelineConfig.hasTemplate(), Matchers.is(false));
        Map map = new HashMap();
        map.put(CONFIGURATION_TYPE, CONFIGURATION_TYPE_TEMPLATE);
        map.put(TEMPLATE_NAME, "foo-template");
        pipelineConfig.setConfigAttributes(map);
        Assert.assertThat(pipelineConfig.getConfigurationType(), Matchers.is(CONFIGURATION_TYPE_TEMPLATE));
        Assert.assertThat(pipelineConfig.getTemplateName(), Matchers.is(new CaseInsensitiveString("foo-template")));
    }

    @Test
    public void shouldIncrementIndexBy1OfGivenStage() {
        StageConfig moveMeStage = StageConfigMother.stageConfig("move-me");
        StageConfig dontMoveMeStage = StageConfigMother.stageConfig("dont-move-me");
        PipelineConfig pipelineConfig = PipelineConfigMother.pipelineConfig("pipeline", moveMeStage, dontMoveMeStage);
        pipelineConfig.incrementIndex(moveMeStage);
        Assert.assertThat(pipelineConfig.indexOf(moveMeStage), Matchers.is(1));
        Assert.assertThat(pipelineConfig.indexOf(dontMoveMeStage), Matchers.is(0));
    }

    @Test
    public void shouldDecrementIndexBy1OfGivenStage() {
        StageConfig moveMeStage = StageConfigMother.stageConfig("move-me");
        StageConfig dontMoveMeStage = StageConfigMother.stageConfig("dont-move-me");
        PipelineConfig pipelineConfig = PipelineConfigMother.pipelineConfig("pipeline", dontMoveMeStage, moveMeStage);
        pipelineConfig.decrementIndex(moveMeStage);
        Assert.assertThat(pipelineConfig.indexOf(moveMeStage), Matchers.is(0));
        Assert.assertThat(pipelineConfig.indexOf(dontMoveMeStage), Matchers.is(1));
    }

    @Test
    public void shouldThrowExceptionWhenTheStageIsNotFound() {
        StageConfig moveMeStage = StageConfigMother.stageConfig("move-me");
        StageConfig dontMoveMeStage = StageConfigMother.stageConfig("dont-move-me");
        PipelineConfig pipelineConfig = PipelineConfigMother.pipelineConfig("pipeline", dontMoveMeStage);
        try {
            pipelineConfig.incrementIndex(moveMeStage);
            Assert.fail("Should fail to increment the index of a stage that is not found");
        } catch (RuntimeException expected) {
            Assert.assertThat(expected.getMessage(), Matchers.is("Cannot find the stage 'move-me' in pipeline 'pipeline'"));
        }
        try {
            pipelineConfig.decrementIndex(moveMeStage);
            Assert.fail("Should fail to increment the index of a stage that is not found");
        } catch (RuntimeException expected) {
            Assert.assertThat(expected.getMessage(), Matchers.is("Cannot find the stage 'move-me' in pipeline 'pipeline'"));
        }
    }

    @Test
    public void shouldReturnListOfStageConfigWhichIsApplicableForFetchArtifact() {
        PipelineConfig superUpstream = PipelineConfigMother.createPipelineConfigWithStages("superUpstream", "s1", "s2", "s3");
        PipelineConfig upstream = PipelineConfigMother.createPipelineConfigWithStages("upstream", "s4", "s5", "s6");
        upstream.addMaterialConfig(new com.thoughtworks.go.config.materials.dependency.DependencyMaterialConfig(superUpstream.name(), new CaseInsensitiveString("s2")));
        PipelineConfig downstream = PipelineConfigMother.createPipelineConfigWithStages("downstream", "s7");
        downstream.addMaterialConfig(new com.thoughtworks.go.config.materials.dependency.DependencyMaterialConfig(upstream.name(), new CaseInsensitiveString("s5")));
        List<StageConfig> fetchableStages = upstream.validStagesForFetchArtifact(downstream, new CaseInsensitiveString("s7"));
        Assert.assertThat(fetchableStages.size(), Matchers.is(2));
        Assert.assertThat(fetchableStages, hasItem(upstream.get(0)));
        Assert.assertThat(fetchableStages, hasItem(upstream.get(1)));
    }

    @Test
    public void shouldReturnStagesBeforeCurrentForSelectedPipeline() {
        PipelineConfig downstream = PipelineConfigMother.createPipelineConfigWithStages("downstream", "s1", "s2");
        List<StageConfig> fetchableStages = downstream.validStagesForFetchArtifact(downstream, new CaseInsensitiveString("s2"));
        Assert.assertThat(fetchableStages.size(), Matchers.is(1));
        Assert.assertThat(fetchableStages, hasItem(downstream.get(0)));
    }

    @Test
    public void shouldUpdateNameAndMaterialsOnAttributes() {
        PipelineConfig pipelineConfig = new PipelineConfig();
        HashMap svnMaterialConfigMap = new HashMap();
        svnMaterialConfigMap.put(URL, "http://url");
        svnMaterialConfigMap.put(USERNAME, "loser");
        svnMaterialConfigMap.put(PASSWORD, "passwd");
        svnMaterialConfigMap.put(CHECK_EXTERNALS, false);
        HashMap materialConfigsMap = new HashMap();
        materialConfigsMap.put(MATERIAL_TYPE, TYPE);
        materialConfigsMap.put(TYPE, svnMaterialConfigMap);
        HashMap attributeMap = new HashMap();
        attributeMap.put(NAME, "startup");
        attributeMap.put(MATERIALS, materialConfigsMap);
        pipelineConfig.setConfigAttributes(attributeMap);
        Assert.assertThat(pipelineConfig.name(), Matchers.is(new CaseInsensitiveString("startup")));
        Assert.assertThat(pipelineConfig.materialConfigs().get(0), Matchers.is(new SvnMaterialConfig("http://url", "loser", "passwd", false)));
    }

    @Test
    public void shouldUpdateStageOnAttributes() {
        PipelineConfig pipelineConfig = new PipelineConfig();
        HashMap stageMap = new HashMap();
        List jobList = DataStructureUtils.a(DataStructureUtils.m(JobConfig.NAME, "JobName"));
        stageMap.put(StageConfig.NAME, "someStage");
        stageMap.put(JOBS, jobList);
        HashMap attributeMap = new HashMap();
        attributeMap.put(NAME, "startup");
        attributeMap.put(STAGE, stageMap);
        pipelineConfig.setConfigAttributes(attributeMap);
        Assert.assertThat(pipelineConfig.name(), Matchers.is(new CaseInsensitiveString("startup")));
        Assert.assertThat(pipelineConfig.get(0).name(), Matchers.is(new CaseInsensitiveString("someStage")));
        Assert.assertThat(pipelineConfig.get(0).getJobs().first().name(), Matchers.is(new CaseInsensitiveString("JobName")));
    }

    @Test
    public void shouldValidatePipelineName() {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("foo bar", "stage1", "job1");
        pipelineConfig.validate(null);
        Assert.assertThat(pipelineConfig.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(pipelineConfig.errors().on(NAME), Matchers.is("Invalid pipeline name 'foo bar'. This must be alphanumeric and can contain underscores and periods (however, it cannot start with a period). The maximum allowed length is 255 characters."));
    }

    @Test
    public void shouldRemoveExistingStagesWhileDoingAStageUpdate() {
        PipelineConfig pipelineConfig = new PipelineConfig(new CaseInsensitiveString("foo"), new MaterialConfigs(), new StageConfig(new CaseInsensitiveString("first"), new JobConfigs()), new StageConfig(new CaseInsensitiveString("second"), new JobConfigs()));
        HashMap stageMap = new HashMap();
        List jobList = DataStructureUtils.a(DataStructureUtils.m(JobConfig.NAME, "JobName"));
        stageMap.put(StageConfig.NAME, "someStage");
        stageMap.put(JOBS, jobList);
        HashMap attributeMap = new HashMap();
        attributeMap.put(NAME, "startup");
        attributeMap.put(STAGE, stageMap);
        pipelineConfig.setConfigAttributes(attributeMap);
        Assert.assertThat(pipelineConfig.name(), Matchers.is(new CaseInsensitiveString("startup")));
        Assert.assertThat(pipelineConfig.size(), Matchers.is(1));
        Assert.assertThat(pipelineConfig.get(0).name(), Matchers.is(new CaseInsensitiveString("someStage")));
        Assert.assertThat(pipelineConfig.get(0).getJobs().first().name(), Matchers.is(new CaseInsensitiveString("JobName")));
    }

    @Test
    public void shouldGetAllFetchTasks() {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("foo bar", "stage1", "job1");
        FetchTask firstFetch = new FetchTask();
        JobConfig firstJob = pipelineConfig.getFirstStageConfig().getJobs().get(0);
        firstJob.addTask(firstFetch);
        firstJob.addTask(new AntTask());
        JobConfig secondJob = new JobConfig();
        secondJob.addTask(new ExecTask());
        FetchTask secondFetch = new FetchTask();
        secondJob.addTask(secondFetch);
        pipelineConfig.add(new StageConfig(new CaseInsensitiveString("stage-2"), new JobConfigs(secondJob)));
        List<FetchTask> fetchTasks = pipelineConfig.getFetchTasks();
        Assert.assertThat(fetchTasks.size(), Matchers.is(2));
        Assert.assertThat(fetchTasks.contains(firstFetch), Matchers.is(true));
        Assert.assertThat(fetchTasks.contains(secondFetch), Matchers.is(true));
    }

    @Test
    public void shouldGetOnlyPlainTextVariables() throws CryptoException {
        PipelineConfig pipelineConfig = new PipelineConfig();
        EnvironmentVariableConfig username = new EnvironmentVariableConfig("username", "ram");
        pipelineConfig.addEnvironmentVariable(username);
        GoCipher goCipher = Mockito.mock(GoCipher.class);
        Mockito.when(goCipher.encrypt("=%HG*^&*&^")).thenReturn("encrypted");
        EnvironmentVariableConfig password = new EnvironmentVariableConfig(goCipher, "password", "=%HG*^&*&^", true);
        pipelineConfig.addEnvironmentVariable(password);
        EnvironmentVariablesConfig plainTextVariables = pipelineConfig.getPlainTextVariables();
        Assert.assertThat(plainTextVariables, Matchers.not(hasItem(password)));
        Assert.assertThat(plainTextVariables, hasItem(username));
    }

    @Test
    public void shouldGetOnlySecureVariables() throws CryptoException {
        PipelineConfig pipelineConfig = new PipelineConfig();
        EnvironmentVariableConfig username = new EnvironmentVariableConfig("username", "ram");
        pipelineConfig.addEnvironmentVariable(username);
        GoCipher goCipher = Mockito.mock(GoCipher.class);
        Mockito.when(goCipher.encrypt("=%HG*^&*&^")).thenReturn("encrypted");
        EnvironmentVariableConfig password = new EnvironmentVariableConfig(goCipher, "password", "=%HG*^&*&^", true);
        pipelineConfig.addEnvironmentVariable(password);
        List<EnvironmentVariableConfig> plainTextVariables = pipelineConfig.getSecureVariables();
        Assert.assertThat(plainTextVariables, hasItem(password));
        Assert.assertThat(plainTextVariables, Matchers.not(hasItem(username)));
    }

    @Test
    public void shouldTemplatizeAPipeline() {
        PipelineConfig config = PipelineConfigMother.createPipelineConfigWithStages("pipeline", "stage1", "stage2");
        config.templatize(new CaseInsensitiveString("template"));
        Assert.assertThat(config.hasTemplate(), Matchers.is(true));
        Assert.assertThat(config.hasTemplateApplied(), Matchers.is(false));
        Assert.assertThat(config.getTemplateName(), Matchers.is(new CaseInsensitiveString("template")));
        Assert.assertThat(config.isEmpty(), Matchers.is(true));
        config.templatize(new CaseInsensitiveString(""));
        Assert.assertThat(config.hasTemplate(), Matchers.is(false));
        config.templatize(null);
        Assert.assertThat(config.hasTemplate(), Matchers.is(false));
    }

    @Test
    public void shouldAssignApprovalTypeOnFirstStageAsAuto() throws Exception {
        Map approvalAttributes = Collections.singletonMap(Approval.TYPE, SUCCESS);
        Map<String, Map> map = Collections.singletonMap(APPROVAL, approvalAttributes);
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("p1", "s1", "j1");
        pipelineConfig.get(0).updateApproval(Approval.manualApproval());
        pipelineConfig.setConfigAttributes(map);
        Assert.assertThat(pipelineConfig.get(0).getApproval().getType(), Matchers.is(SUCCESS));
    }

    @Test
    public void shouldAssignApprovalTypeOnFirstStageAsManual() throws Exception {
        Map approvalAttributes = Collections.singletonMap(Approval.TYPE, MANUAL);
        Map<String, Map> map = Collections.singletonMap(APPROVAL, approvalAttributes);
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("p1", "s1", "j1");
        pipelineConfig.get(0).updateApproval(Approval.manualApproval());
        pipelineConfig.setConfigAttributes(map);
        Assert.assertThat(pipelineConfig.get(0).getApproval().getType(), Matchers.is(MANUAL));
    }

    @Test
    public void shouldAssignApprovalTypeOnFirstStageAsManualAndRestOfStagesAsUntouched() throws Exception {
        Map approvalAttributes = Collections.singletonMap(Approval.TYPE, MANUAL);
        Map<String, Map> map = Collections.singletonMap(APPROVAL, approvalAttributes);
        PipelineConfig pipelineConfig = PipelineConfigMother.pipelineConfig("p1", StageConfigMother.custom("s1", Approval.automaticApproval()), StageConfigMother.custom("s2", Approval.automaticApproval()));
        pipelineConfig.setConfigAttributes(map);
        Assert.assertThat(pipelineConfig.get(0).getApproval().getType(), Matchers.is(MANUAL));
        Assert.assertThat(pipelineConfig.get(1).getApproval().getType(), Matchers.is(SUCCESS));
    }

    @Test
    public void shouldGetPackageMaterialConfigs() throws Exception {
        SvnMaterialConfig svn = new SvnMaterialConfig("svn", false);
        PackageMaterialConfig packageMaterialOne = new PackageMaterialConfig();
        PackageMaterialConfig packageMaterialTwo = new PackageMaterialConfig();
        PipelineConfig pipelineConfig = PipelineConfigMother.pipelineConfig("p1", new MaterialConfigs(svn, packageMaterialOne, packageMaterialTwo));
        List<PackageMaterialConfig> packageMaterialConfigs = pipelineConfig.packageMaterialConfigs();
        Assert.assertThat(packageMaterialConfigs.size(), Matchers.is(2));
        Assert.assertThat(packageMaterialConfigs, hasItems(packageMaterialOne, packageMaterialTwo));
    }

    @Test
    public void shouldGetPluggableSCMMaterialConfigs() throws Exception {
        SvnMaterialConfig svn = new SvnMaterialConfig("svn", false);
        PluggableSCMMaterialConfig pluggableSCMMaterialOne = new PluggableSCMMaterialConfig("scm-id-1");
        PluggableSCMMaterialConfig pluggableSCMMaterialTwo = new PluggableSCMMaterialConfig("scm-id-2");
        PipelineConfig pipelineConfig = PipelineConfigMother.pipelineConfig("p1", new MaterialConfigs(svn, pluggableSCMMaterialOne, pluggableSCMMaterialTwo));
        List<PluggableSCMMaterialConfig> pluggableSCMMaterialConfigs = pipelineConfig.pluggableSCMMaterialConfigs();
        Assert.assertThat(pluggableSCMMaterialConfigs.size(), Matchers.is(2));
        Assert.assertThat(pluggableSCMMaterialConfigs, hasItems(pluggableSCMMaterialOne, pluggableSCMMaterialTwo));
    }

    @Test
    public void shouldReturnTrueWhenOneOfPipelineMaterialsIsTheSameAsConfigOrigin() {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("pipeline", "stage", "build");
        MaterialConfig material = pipelineConfig.materialConfigs().first();
        pipelineConfig.setOrigin(new RepoConfigOrigin(new com.thoughtworks.go.config.remote.ConfigRepoConfig(material, "plugin"), "1233"));
        Assert.assertThat(pipelineConfig.isConfigOriginSameAsOneOfMaterials(), Matchers.is(true));
    }

    @Test
    public void shouldReturnTrueWhenOneOfPipelineMaterialsIsTheSameAsConfigOriginButDestinationIsDifferent() {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("pipeline", "stage", "build");
        pipelineConfig.materialConfigs().clear();
        GitMaterialConfig pipeMaterialConfig = new GitMaterialConfig("http://git");
        pipeMaterialConfig.setFolder("dest1");
        pipelineConfig.materialConfigs().add(pipeMaterialConfig);
        GitMaterialConfig repoMaterialConfig = new GitMaterialConfig("http://git");
        pipelineConfig.setOrigin(new RepoConfigOrigin(new com.thoughtworks.go.config.remote.ConfigRepoConfig(repoMaterialConfig, "plugin"), "1233"));
        Assert.assertThat(pipelineConfig.isConfigOriginSameAsOneOfMaterials(), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseWhenOneOfPipelineMaterialsIsNotTheSameAsConfigOrigin() {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("pipeline", "stage", "build");
        MaterialConfig material = new GitMaterialConfig("http://git");
        pipelineConfig.setOrigin(new RepoConfigOrigin(new com.thoughtworks.go.config.remote.ConfigRepoConfig(material, "plugin"), "1233"));
        Assert.assertThat(pipelineConfig.isConfigOriginSameAsOneOfMaterials(), Matchers.is(false));
    }

    @Test
    public void shouldReturnFalseIfOneOfPipelineMaterialsIsTheSameAsConfigOrigin_WhenOriginIsFile() {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("pipeline", "stage", "build");
        pipelineConfig.setOrigin(new FileConfigOrigin());
        Assert.assertThat(pipelineConfig.isConfigOriginSameAsOneOfMaterials(), Matchers.is(false));
    }

    @Test
    public void shouldReturnTrueWhenConfigRevisionIsEqualToQuery() {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("pipeline", "stage", "build");
        MaterialConfig material = pipelineConfig.materialConfigs().first();
        pipelineConfig.setOrigin(new RepoConfigOrigin(new com.thoughtworks.go.config.remote.ConfigRepoConfig(material, "plugin"), "1233"));
        Assert.assertThat(pipelineConfig.isConfigOriginFromRevision("1233"), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseWhenConfigRevisionIsNotEqualToQuery() {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("pipeline", "stage", "build");
        MaterialConfig material = pipelineConfig.materialConfigs().first();
        pipelineConfig.setOrigin(new RepoConfigOrigin(new com.thoughtworks.go.config.remote.ConfigRepoConfig(material, "plugin"), "1233"));
        Assert.assertThat(pipelineConfig.isConfigOriginFromRevision("32"), Matchers.is(false));
    }

    @Test
    public void shouldReturnConfigRepoOriginDisplayNameWhenOriginIsRemote() {
        PipelineConfig pipelineConfig = new PipelineConfig();
        pipelineConfig.setOrigin(new RepoConfigOrigin(new com.thoughtworks.go.config.remote.ConfigRepoConfig(MaterialConfigsMother.gitMaterialConfig(), "plugin"), "revision1"));
        Assert.assertThat(pipelineConfig.getOriginDisplayName(), Matchers.is("AwesomeGitMaterial at revision1"));
    }

    @Test
    public void shouldReturnConfigRepoOriginDisplayNameWhenOriginIsFile() {
        PipelineConfig pipelineConfig = new PipelineConfig();
        pipelineConfig.setOrigin(new FileConfigOrigin());
        Assert.assertThat(pipelineConfig.getOriginDisplayName(), Matchers.is("cruise-config.xml"));
    }

    @Test
    public void shouldReturnConfigRepoOriginDisplayNameWhenOriginIsNotSet() {
        PipelineConfig pipelineConfig = new PipelineConfig();
        Assert.assertThat(pipelineConfig.getOriginDisplayName(), Matchers.is("cruise-config.xml"));
    }

    @Test
    public void shouldNotEncryptSecurePropertiesInStagesIfPipelineHasATemplate() {
        PipelineConfig pipelineConfig = new PipelineConfig();
        pipelineConfig.setTemplateName(new CaseInsensitiveString("some-template"));
        StageConfig mockStageConfig = Mockito.mock(StageConfig.class);
        pipelineConfig.addStageWithoutValidityAssertion(mockStageConfig);
        pipelineConfig.encryptSecureProperties(new BasicCruiseConfig(), pipelineConfig);
        Mockito.verify(mockStageConfig, Mockito.never()).encryptSecureProperties(ArgumentMatchers.eq(new BasicCruiseConfig()), ArgumentMatchers.eq(pipelineConfig), ArgumentMatchers.any(StageConfig.class));
    }

    @Test
    public void shouldEncryptSecurePropertiesInStagesIfPipelineHasStagesDefined() {
        PipelineConfig pipelineConfig = new PipelineConfig();
        StageConfig mockStageConfig = Mockito.mock(StageConfig.class);
        pipelineConfig.add(mockStageConfig);
        JobConfig jobConfig = new JobConfig(new CaseInsensitiveString("job"));
        jobConfig.artifactConfigs().add(new PluggableArtifactConfig("foo", "bar"));
        Mockito.when(mockStageConfig.getJobs()).thenReturn(new JobConfigs(jobConfig));
        Mockito.when(mockStageConfig.name()).thenReturn(new CaseInsensitiveString("stage"));
        pipelineConfig.encryptSecureProperties(new BasicCruiseConfig(), pipelineConfig);
        Mockito.verify(mockStageConfig).encryptSecureProperties(ArgumentMatchers.eq(new BasicCruiseConfig()), ArgumentMatchers.eq(pipelineConfig), ArgumentMatchers.any(StageConfig.class));
    }

    @Test
    public void shouldNotAttemptToEncryptPropertiesIfThereAreNoPluginConfigs() {
        PipelineConfig pipelineConfig = new PipelineConfig();
        StageConfig mockStageConfig = Mockito.mock(StageConfig.class);
        pipelineConfig.add(mockStageConfig);
        JobConfig jobConfig = new JobConfig(new CaseInsensitiveString("job"));
        Mockito.when(mockStageConfig.getJobs()).thenReturn(new JobConfigs(jobConfig));
        Mockito.when(mockStageConfig.name()).thenReturn(new CaseInsensitiveString("stage"));
        pipelineConfig.encryptSecureProperties(new BasicCruiseConfig(), pipelineConfig);
        Mockito.verify(mockStageConfig, Mockito.never()).encryptSecureProperties(ArgumentMatchers.eq(new BasicCruiseConfig()), ArgumentMatchers.eq(pipelineConfig), ArgumentMatchers.any(StageConfig.class));
    }
}

