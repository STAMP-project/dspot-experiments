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


import Approval.MANUAL;
import Approval.SUCCESS;
import Approval.TYPE;
import ArtifactType.build;
import ArtifactType.test;
import RunOnAllAgents.CounterBasedJobNameGenerator;
import StageConfig.APPROVAL;
import StageConfig.ARTIFACT_CLEANUP_PROHIBITED;
import StageConfig.CLEAN_WORKING_DIR;
import StageConfig.ENVIRONMENT_VARIABLES;
import StageConfig.FETCH_MATERIALS;
import StageConfig.JOBS;
import StageConfig.NAME;
import StageConfig.OPERATE_ROLES;
import StageConfig.OPERATE_USERS;
import StageConfig.SECURITY_MODE;
import com.thoughtworks.go.helper.PipelineConfigMother;
import com.thoughtworks.go.helper.StageConfigMother;
import com.thoughtworks.go.util.DataStructureUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static RunOnAllAgentsJobTypeConfig.MARKER;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;


public class StageConfigTest {
    private String md5 = "md5-test";

    @Test
    public void shouldSetPrimitiveAttributes() throws Exception {
        StageConfig config = new StageConfig();
        config.setConfigAttributes(Collections.singletonMap(NAME, "foo_bar"));
        config.setConfigAttributes(Collections.singletonMap(FETCH_MATERIALS, "0"));
        config.setConfigAttributes(Collections.singletonMap(CLEAN_WORKING_DIR, "1"));
        Assert.assertThat(config.name(), Matchers.is(new CaseInsensitiveString("foo_bar")));
        Assert.assertThat(config.isFetchMaterials(), Matchers.is(false));
        Assert.assertThat(config.isCleanWorkingDir(), Matchers.is(true));
    }

    @Test
    public void shouldSetArtifactCleanupOptOutAttribute() throws Exception {
        StageConfig config = new StageConfig();
        Assert.assertThat(config.isArtifactCleanupProhibited(), Matchers.is(false));
        config.setConfigAttributes(Collections.singletonMap(ARTIFACT_CLEANUP_PROHIBITED, "1"));
        Assert.assertThat(config.isArtifactCleanupProhibited(), Matchers.is(true));
        config.setConfigAttributes(new HashMap());
        Assert.assertThat(config.isArtifactCleanupProhibited(), Matchers.is(true));
        config.setConfigAttributes(Collections.singletonMap(ARTIFACT_CLEANUP_PROHIBITED, "0"));
        Assert.assertThat(config.isArtifactCleanupProhibited(), Matchers.is(false));
    }

    @Test
    public void shouldRemoveStageLevelAuthorizationWhenInheritingPermissionsFromGroup() {
        StageConfig config = new StageConfig();
        StageConfigMother.addApprovalWithRoles(config, "role1");
        StageConfigMother.addApprovalWithUsers(config, "user1");
        HashMap map = new HashMap();
        List operateUsers = new ArrayList();
        operateUsers.add(nameMap("user1"));
        map.put(OPERATE_USERS, operateUsers);
        List operateRoles = new ArrayList();
        operateRoles.add(nameMap("role1"));
        map.put(OPERATE_ROLES, operateRoles);
        map.put(SECURITY_MODE, "inherit");
        config.setConfigAttributes(map);
        Assert.assertThat(config.getApproval().getAuthConfig().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldSetOperateUsers() {
        StageConfig config = new StageConfig();
        HashMap map = new HashMap();
        List operateUsers = new ArrayList();
        operateUsers.add(nameMap("user1"));
        operateUsers.add(nameMap("user1"));
        operateUsers.add(nameMap("user2"));
        map.put(OPERATE_USERS, operateUsers);
        map.put(OPERATE_ROLES, new ArrayList());
        map.put(SECURITY_MODE, "define");
        config.setConfigAttributes(map);
        Assert.assertThat(config.getOperateUsers().size(), Matchers.is(2));
        Assert.assertThat(config.getOperateUsers(), Matchers.hasItem(new AdminUser(new CaseInsensitiveString("user1"))));
        Assert.assertThat(config.getOperateUsers(), Matchers.hasItem(new AdminUser(new CaseInsensitiveString("user2"))));
    }

    @Test
    public void shouldSetOperateRoles() {
        StageConfig config = new StageConfig();
        HashMap map = new HashMap();
        List operateRoles = new ArrayList();
        operateRoles.add(nameMap("role1"));
        operateRoles.add(nameMap("role1"));
        operateRoles.add(nameMap("role2"));
        map.put(OPERATE_ROLES, operateRoles);
        map.put(OPERATE_USERS, new ArrayList());
        map.put(SECURITY_MODE, "define");
        config.setConfigAttributes(map);
        Assert.assertThat(config.getOperateRoles().size(), Matchers.is(2));
        Assert.assertThat(config.getOperateRoles(), Matchers.hasItem(new AdminRole(new CaseInsensitiveString("role1"))));
        Assert.assertThat(config.getOperateRoles(), Matchers.hasItem(new AdminRole(new CaseInsensitiveString("role2"))));
    }

    @Test
    public void shouldPopulateEnvironmentVariablesFromAttributeMap() {
        StageConfig stageConfig = new StageConfig();
        HashMap map = new HashMap();
        HashMap valueHashMap = new HashMap();
        valueHashMap.put("name", "FOO");
        valueHashMap.put("value", "BAR");
        map.put(ENVIRONMENT_VARIABLES, valueHashMap);
        EnvironmentVariablesConfig mockEnvironmentVariablesConfig = Mockito.mock(EnvironmentVariablesConfig.class);
        stageConfig.setVariables(mockEnvironmentVariablesConfig);
        stageConfig.setConfigAttributes(map);
        Mockito.verify(mockEnvironmentVariablesConfig).setConfigAttributes(valueHashMap);
    }

    @Test
    public void shouldSetApprovalFromConfigAttrs() throws Exception {
        StageConfig config = new StageConfig();
        config.setConfigAttributes(Collections.singletonMap(APPROVAL, Collections.singletonMap(TYPE, MANUAL)));
        Assert.assertThat(config.getApproval().getType(), Matchers.is(MANUAL));
        config.setConfigAttributes(new HashMap());
        Assert.assertThat(config.getApproval().getType(), Matchers.is(MANUAL));
        config.setConfigAttributes(Collections.singletonMap(APPROVAL, Collections.singletonMap(TYPE, SUCCESS)));
        Assert.assertThat(config.getApproval().getType(), Matchers.is(SUCCESS));
        config.setConfigAttributes(new HashMap());
        Assert.assertThat(config.getApproval().getType(), Matchers.is(SUCCESS));
    }

    @Test
    public void shouldPickupJobConfigDetailsFromAttributeMap() throws Exception {
        StageConfig config = new StageConfig();
        Map stageAttrs = DataStructureUtils.m(JOBS, DataStructureUtils.a(DataStructureUtils.m(JobConfig.NAME, "con-job"), DataStructureUtils.m(JobConfig.NAME, "boring-job")));
        config.setConfigAttributes(stageAttrs);
        Assert.assertThat(config.getJobs().get(0).name(), Matchers.is(new CaseInsensitiveString("con-job")));
        Assert.assertThat(config.getJobs().get(1).name(), Matchers.is(new CaseInsensitiveString("boring-job")));
    }

    @Test
    public void shouldFindCorrectJobIfJobIsOnAllAgents() throws Exception {
        JobConfig allAgentsJob = new JobConfig("job-for-all-agents");
        allAgentsJob.setRunOnAllAgents(true);
        JobConfigs jobs = new JobConfigs();
        jobs.add(allAgentsJob);
        jobs.add(new JobConfig("job"));
        StageConfig stage = new StageConfig(new CaseInsensitiveString("stage-name"), jobs);
        JobConfig found = stage.jobConfigByInstanceName((("job-for-all-agents-" + (MARKER)) + "-1"), true);
        Assert.assertThat(found, Matchers.is(allAgentsJob));
    }

    @Test
    public void shouldFindCorrectJobIfJobIsOnAllAgentsAndAmbiguousName() throws Exception {
        JobConfig allAgentsJob = new JobConfig("job-for-all-agents");
        JobConfig ambiguousJob = new JobConfig("job-for-all");
        allAgentsJob.setRunOnAllAgents(true);
        ambiguousJob.setRunOnAllAgents(true);
        JobConfigs jobs = new JobConfigs();
        jobs.add(ambiguousJob);
        jobs.add(allAgentsJob);
        StageConfig stage = new StageConfig(new CaseInsensitiveString("stage-name"), jobs);
        JobConfig found = stage.jobConfigByInstanceName(CounterBasedJobNameGenerator.appendMarker("job-for-all-agents", 1), true);
        Assert.assertThat(found, Matchers.is(allAgentsJob));
    }

    @Test
    public void shouldReturnTrueIfStageHasTests() {
        StageConfig stageWithTests = StageConfigMother.stageConfigWithArtifact("stage1", "job1", test);
        StageConfig stageWithoutTests = StageConfigMother.stageConfigWithArtifact("stage2", "job2", build);
        Assert.assertThat(stageWithTests.hasTests(), Matchers.is(true));
        Assert.assertThat(stageWithoutTests.hasTests(), Matchers.is(false));
    }

    @Test
    public void shouldPopulateErrorMessagesWhenHasJobNamesRepeated() {
        CruiseConfig config = new BasicCruiseConfig();
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("pipeline", "stage-1", "con-job");
        config.addPipeline("group-foo", pipelineConfig);
        StageConfig stageConfig = pipelineConfig.get(0);
        JobConfig newJob = new JobConfig("foo!");
        StageConfig newlyAddedStage = new StageConfig(new CaseInsensitiveString("."), new JobConfigs(newJob));
        pipelineConfig.addStageWithoutValidityAssertion(newlyAddedStage);
        stageConfig.getJobs().addJobWithoutValidityAssertion(new JobConfig(new CaseInsensitiveString("con-job"), new ResourceConfigs(), new ArtifactConfigs(), new Tasks(new ExecTask("ls", "-la", "foo"))));
        List<ConfigErrors> allErrors = config.validateAfterPreprocess();
        Assert.assertThat(allErrors.size(), Matchers.is(4));
        Assert.assertThat(allErrors.get(0).on(JobConfig.NAME), Matchers.is("You have defined multiple jobs called 'con-job'. Job names are case-insensitive and must be unique."));
        Assert.assertThat(allErrors.get(1).on(JobConfig.NAME), Matchers.is("You have defined multiple jobs called 'con-job'. Job names are case-insensitive and must be unique."));
        Assert.assertThat(allErrors.get(2).on(NAME), Matchers.is("Invalid stage name '.'. This must be alphanumeric and can contain underscores and periods (however, it cannot start with a period). The maximum allowed length is 255 characters."));
        Assert.assertThat(allErrors.get(3).on(JobConfig.NAME), Matchers.is("Invalid job name 'foo!'. This must be alphanumeric and may contain underscores and periods. The maximum allowed length is 255 characters."));
        Assert.assertThat(stageConfig.getJobs().get(0).errors().on(JobConfig.NAME), Matchers.is("You have defined multiple jobs called 'con-job'. Job names are case-insensitive and must be unique."));
        Assert.assertThat(stageConfig.getJobs().get(1).errors().on(JobConfig.NAME), Matchers.is("You have defined multiple jobs called 'con-job'. Job names are case-insensitive and must be unique."));
        Assert.assertThat(newlyAddedStage.errors().on(NAME), Matchers.is("Invalid stage name '.'. This must be alphanumeric and can contain underscores and periods (however, it cannot start with a period). The maximum allowed length is 255 characters."));
        Assert.assertThat(newJob.errors().on(JobConfig.NAME), Matchers.is("Invalid job name 'foo!'. This must be alphanumeric and may contain underscores and periods. The maximum allowed length is 255 characters."));
    }

    @Test
    public void shouldReturnAllTheUsersAndRoleThatCanOperateThisStage() {
        StageConfig stage = StageConfigMother.stageConfig("stage");
        StageConfigMother.addApprovalWithUsers(stage, "user1", "user2");
        StageConfigMother.addApprovalWithRoles(stage, "role1", "role2");
        Assert.assertThat(stage.getOperateUsers(), Matchers.is(Arrays.asList(new AdminUser(new CaseInsensitiveString("user1")), new AdminUser(new CaseInsensitiveString("user2")))));
        Assert.assertThat(stage.getOperateRoles(), Matchers.is(Arrays.asList(new AdminRole(new CaseInsensitiveString("role1")), new AdminRole(new CaseInsensitiveString("role2")))));
    }

    @Test
    public void shouldFailValidationWhenNameIsBlank() {
        StageConfig stageConfig = new StageConfig();
        stageConfig.validate(null);
        Assert.assertThat(stageConfig.errors().on(NAME), ArgumentMatchers.contains("Invalid stage name 'null'"));
        stageConfig.setName(null);
        stageConfig.validate(null);
        Assert.assertThat(stageConfig.errors().on(NAME), ArgumentMatchers.contains("Invalid stage name 'null'"));
        stageConfig.setName(new CaseInsensitiveString(""));
        stageConfig.validate(null);
        Assert.assertThat(stageConfig.errors().on(NAME), ArgumentMatchers.contains("Invalid stage name 'null'"));
    }

    @Test
    public void shouldValidateTree() {
        EnvironmentVariablesConfig variables = Mockito.mock(EnvironmentVariablesConfig.class);
        JobConfigs jobConfigs = Mockito.mock(JobConfigs.class);
        Approval approval = Mockito.mock(Approval.class);
        StageConfig stageConfig = new StageConfig(new CaseInsensitiveString("stage$"), jobConfigs, approval);
        stageConfig.setVariables(variables);
        stageConfig.validateTree(PipelineConfigSaveValidationContext.forChain(true, "group", new PipelineConfig(), stageConfig));
        Assert.assertThat(stageConfig.errors().on(NAME), ArgumentMatchers.contains("Invalid stage name 'stage$'"));
        ArgumentCaptor<PipelineConfigSaveValidationContext> captor = ArgumentCaptor.forClass(PipelineConfigSaveValidationContext.class);
        Mockito.verify(jobConfigs).validateTree(captor.capture());
        PipelineConfigSaveValidationContext childContext = captor.getValue();
        Assert.assertThat(childContext.getParent(), Matchers.is(stageConfig));
        Mockito.verify(approval).validateTree(childContext);
        Mockito.verify(variables).validateTree(childContext);
    }

    @Test
    public void shouldAddValidateTreeErrorsOnStageConfigIfPipelineIsAssociatedToATemplate() {
        Approval approval = Mockito.mock(Approval.class);
        JobConfigs jobConfigs = Mockito.mock(JobConfigs.class);
        ConfigErrors jobErrors = new ConfigErrors();
        jobErrors.add("KEY", "ERROR");
        Mockito.when(jobConfigs.errors()).thenReturn(jobErrors);
        StageConfig stageConfig = new StageConfig(new CaseInsensitiveString("stage$"), jobConfigs, approval);
        PipelineConfig pipelineConfig = new PipelineConfig();
        pipelineConfig.setTemplateName(new CaseInsensitiveString("template"));
        stageConfig.validateTree(PipelineConfigSaveValidationContext.forChain(true, "group", pipelineConfig, stageConfig));
        Assert.assertThat(stageConfig.errors().on(NAME), ArgumentMatchers.contains("Invalid stage name 'stage$'"));
    }

    @Test
    public void shouldReturnTrueIfAllDescendentsAreValid() {
        EnvironmentVariablesConfig variables = Mockito.mock(EnvironmentVariablesConfig.class);
        JobConfigs jobConfigs = Mockito.mock(JobConfigs.class);
        Approval approval = Mockito.mock(Approval.class);
        Mockito.when(variables.validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class))).thenReturn(true);
        Mockito.when(jobConfigs.validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class))).thenReturn(true);
        Mockito.when(approval.validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class))).thenReturn(true);
        StageConfig stageConfig = new StageConfig(new CaseInsensitiveString("p1"), jobConfigs);
        stageConfig.setVariables(variables);
        stageConfig.setApproval(approval);
        boolean isValid = stageConfig.validateTree(PipelineConfigSaveValidationContext.forChain(true, "group", new PipelineConfig(), stageConfig));
        assertTrue(isValid);
        Mockito.verify(jobConfigs).validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class));
        Mockito.verify(variables).validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class));
        Mockito.verify(approval).validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class));
    }

    @Test
    public void shouldReturnFalseIfAnyDescendentIsInValid() {
        EnvironmentVariablesConfig variables = Mockito.mock(EnvironmentVariablesConfig.class);
        JobConfigs jobConfigs = Mockito.mock(JobConfigs.class);
        Approval approval = Mockito.mock(Approval.class);
        Mockito.when(variables.validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class))).thenReturn(false);
        Mockito.when(jobConfigs.validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class))).thenReturn(false);
        Mockito.when(approval.validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class))).thenReturn(false);
        StageConfig stageConfig = new StageConfig(new CaseInsensitiveString("p1"), jobConfigs);
        stageConfig.setVariables(variables);
        stageConfig.setApproval(approval);
        boolean isValid = stageConfig.validateTree(PipelineConfigSaveValidationContext.forChain(true, "group", new PipelineConfig(), stageConfig));
        assertFalse(isValid);
        Mockito.verify(jobConfigs).validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class));
        Mockito.verify(variables).validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class));
        Mockito.verify(approval).validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class));
    }
}

