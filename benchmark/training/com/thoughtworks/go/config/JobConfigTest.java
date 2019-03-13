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


import ExecTask.ARGS;
import ExecTask.COMMAND;
import ExecTask.WORKING_DIR;
import JobConfig.ARTIFACT_CONFIGS;
import JobConfig.DEFAULT_TIMEOUT;
import JobConfig.ELASTIC_PROFILE_ID;
import JobConfig.ENVIRONMENT_VARIABLES;
import JobConfig.NAME;
import JobConfig.NEVER_TIMEOUT;
import JobConfig.OVERRIDE_TIMEOUT;
import JobConfig.RESOURCES;
import JobConfig.RUN_INSTANCE_COUNT;
import JobConfig.RUN_MULTIPLE_INSTANCE;
import JobConfig.RUN_ON_ALL_AGENTS;
import JobConfig.RUN_SINGLE_INSTANCE;
import JobConfig.RUN_TYPE;
import JobConfig.TABS;
import JobConfig.TASKS;
import JobConfig.TIMEOUT;
import Tab.PATH;
import Task.TASK_TYPE;
import Tasks.TASK_OPTIONS;
import com.thoughtworks.go.domain.ConfigErrors;
import com.thoughtworks.go.domain.NullTask;
import com.thoughtworks.go.domain.Task;
import com.thoughtworks.go.helper.JobConfigMother;
import com.thoughtworks.go.helper.PipelineConfigMother;
import com.thoughtworks.go.service.TaskFactory;
import com.thoughtworks.go.util.DataStructureUtils;
import com.thoughtworks.go.util.ReflectionUtil;
import java.util.HashMap;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class JobConfigTest {
    JobConfig config;

    @Test
    public void shouldCopyAttributeValuesFromAttributeMap() throws Exception {
        config = new JobConfig();// override the setup mock

        TaskFactory taskFactory = Mockito.mock(TaskFactory.class);
        ExecTask emptyExecTask = new ExecTask();
        Mockito.when(taskFactory.taskInstanceFor(emptyExecTask.getTaskType())).thenReturn(emptyExecTask);
        config.setConfigAttributes(DataStructureUtils.m(NAME, "foo-job", TASKS, DataStructureUtils.m(TASK_OPTIONS, "exec", "exec", DataStructureUtils.m(TASK_TYPE, "exec", COMMAND, "ls", ARGS, "-la", WORKING_DIR, "/tmp"))), taskFactory);
        Assert.assertThat(config.name(), Matchers.is(new CaseInsensitiveString("foo-job")));
        Assert.assertThat(config.getTasks().get(0), Matchers.is(new ExecTask("ls", "-la", "/tmp")));
        Assert.assertThat(config.getTasks().size(), Matchers.is(1));
    }

    @Test
    public void shouldSetTimeoutIfSpecified() throws Exception {
        config.setConfigAttributes(DataStructureUtils.m(NAME, "foo-job", "timeoutType", OVERRIDE_TIMEOUT, TIMEOUT, "100", TASKS, DataStructureUtils.m(TASK_OPTIONS, "exec", "exec", DataStructureUtils.m(TASK_TYPE, "exec", COMMAND, "ls", ARGS, "-la", WORKING_DIR, "/tmp"))));
        Assert.assertThat(config.getTimeout(), Matchers.is("100"));
    }

    @Test
    public void shouldClearTimeoutIfSubmittedWithEmptyValue() throws Exception {
        config.setConfigAttributes(DataStructureUtils.m(NAME, "foo-job", "timeoutType", OVERRIDE_TIMEOUT, TIMEOUT, "", TASKS, DataStructureUtils.m(TASK_OPTIONS, "exec", "exec", DataStructureUtils.m(TASK_TYPE, "exec", COMMAND, "ls", ARGS, "-la", WORKING_DIR, "/tmp"))));
        Assert.assertThat(config.getTimeout(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldSetTimeoutToZeroIfSubmittedWithNever() throws Exception {
        config.setConfigAttributes(DataStructureUtils.m(NAME, "foo-job", "timeoutType", NEVER_TIMEOUT, TIMEOUT, "100", TASKS, DataStructureUtils.m(TASK_OPTIONS, "exec", "exec", DataStructureUtils.m(TASK_TYPE, "exec", COMMAND, "ls", ARGS, "-la", WORKING_DIR, "/tmp"))));
        Assert.assertThat(config.getTimeout(), Matchers.is("0"));
    }

    @Test
    public void shouldSetTimeoutToNullIfSubmittedWithDefault() throws Exception {
        config.setConfigAttributes(DataStructureUtils.m(NAME, "foo-job", "timeoutType", DEFAULT_TIMEOUT, TIMEOUT, "", TASKS, DataStructureUtils.m(TASK_OPTIONS, "exec", "exec", DataStructureUtils.m(TASK_TYPE, "exec", COMMAND, "ls", ARGS, "-la", WORKING_DIR, "/tmp"))));
        Assert.assertThat(config.getTimeout(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldNotSetJobNameIfNotGiven() throws Exception {
        JobConfig config = new JobConfig("some-job-name");
        config.setConfigAttributes(DataStructureUtils.m());
        Assert.assertThat(config.name(), Matchers.is(new CaseInsensitiveString("some-job-name")));
        config.setConfigAttributes(DataStructureUtils.m(NAME, null));
        Assert.assertThat(config.name(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldReturnAntTaskAsDefaultIfNoTasksSpecified() {
        JobConfig jobConfig = new JobConfig();
        Assert.assertThat(jobConfig.tasks(), org.hamcrest.Matchers.iterableWithSize(1));
        Task task = jobConfig.tasks().first();
        Assert.assertThat(task, Matchers.instanceOf(NullTask.class));
    }

    @Test
    public void shouldNotSetTasksIfNoTasksGiven() throws Exception {
        config = new JobConfig();
        AntTask task = new AntTask();
        task.setTarget("hello");
        config.addTask(task);
        config.setConfigAttributes(DataStructureUtils.m());
        AntTask taskAfterUpdate = ((AntTask) (config.getTasks().get(0)));
        Assert.assertThat(taskAfterUpdate.getTarget(), Matchers.is("hello"));
        Assert.assertThat(config.getTasks().size(), Matchers.is(1));
        config.setConfigAttributes(DataStructureUtils.m(TASKS, null));
        Assert.assertThat(config.getTasks().size(), Matchers.is(0));
    }

    @Test
    public void shouldValidateTheJobName() {
        Assert.assertThat(createJobAndValidate(".name").errors().isEmpty(), Matchers.is(true));
        ConfigErrors configErrors = createJobAndValidate("name pavan").errors();
        Assert.assertThat(configErrors.isEmpty(), Matchers.is(false));
        Assert.assertThat(configErrors.on(NAME), Matchers.is("Invalid job name 'name pavan'. This must be alphanumeric and may contain underscores and periods. The maximum allowed length is 255 characters."));
    }

    @Test
    public void shouldFailValidationWhenJobNameIsEmpty() {
        ConfigErrors configErrors = createJobAndValidate(null).errors();
        Assert.assertThat(configErrors.isEmpty(), Matchers.is(false));
        Assert.assertThat(configErrors.on(NAME), Matchers.is("Name is a required field"));
    }

    @Test
    public void shouldValidateTheJobNameAgainstHaving_runOnAll() {
        String jobName = "a-runOnAll-1";
        ConfigErrors configErrors = createJobAndValidate(jobName).errors();
        Assert.assertThat(configErrors.isEmpty(), Matchers.is(false));
        Assert.assertThat(configErrors.on(NAME), Matchers.is(String.format("A job cannot have 'runOnAll' in it's name: %s because it is a reserved keyword", jobName)));
    }

    @Test
    public void shouldValidateTheJobNameAgainstHaving_runInstance() {
        String jobName = "a-runInstance-1";
        ConfigErrors configErrors = createJobAndValidate(jobName).errors();
        Assert.assertThat(configErrors.isEmpty(), Matchers.is(false));
        Assert.assertThat(configErrors.on(NAME), Matchers.is(String.format("A job cannot have 'runInstance' in it's name: %s because it is a reserved keyword", jobName)));
    }

    @Test
    public void shouldValidateAgainstSettingRunInstanceCountToIncorrectValue() {
        JobConfig jobConfig1 = new JobConfig(new CaseInsensitiveString("test"));
        jobConfig1.setRunInstanceCount((-1));
        jobConfig1.validate(ConfigSaveValidationContext.forChain(new BasicCruiseConfig()));
        ConfigErrors configErrors1 = jobConfig1.errors();
        Assert.assertThat(configErrors1.isEmpty(), Matchers.is(false));
        Assert.assertThat(configErrors1.on(RUN_TYPE), Matchers.is("'Run Instance Count' cannot be a negative number as it represents number of instances Go needs to spawn during runtime."));
        JobConfig jobConfig2 = new JobConfig(new CaseInsensitiveString("test"));
        ReflectionUtil.setField(jobConfig2, "runInstanceCount", "abcd");
        jobConfig2.validate(ConfigSaveValidationContext.forChain(new BasicCruiseConfig()));
        ConfigErrors configErrors2 = jobConfig2.errors();
        Assert.assertThat(configErrors2.isEmpty(), Matchers.is(false));
        Assert.assertThat(configErrors2.on(RUN_TYPE), Matchers.is("'Run Instance Count' should be a valid positive integer as it represents number of instances Go needs to spawn during runtime."));
    }

    @Test
    public void shouldValidateAgainstSettingRunOnAllAgentsAndRunInstanceCountSetTogether() {
        JobConfig jobConfig = new JobConfig(new CaseInsensitiveString("test"));
        jobConfig.setRunOnAllAgents(true);
        jobConfig.setRunInstanceCount(10);
        jobConfig.validate(ConfigSaveValidationContext.forChain(new BasicCruiseConfig()));
        ConfigErrors configErrors = jobConfig.errors();
        Assert.assertThat(configErrors.isEmpty(), Matchers.is(false));
        Assert.assertThat(configErrors.on(RUN_TYPE), Matchers.is("Job cannot be 'run on all agents' type and 'run multiple instance' type together."));
    }

    @Test
    public void shouldValidateEmptyAndNullResources() {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfigWithJobConfigs("pipeline1");
        JobConfig jobConfig = JobConfigMother.createJobConfigWithJobNameAndEmptyResources();
        ValidationContext validationContext = Mockito.mock(ValidationContext.class);
        Mockito.when(validationContext.getPipeline()).thenReturn(pipelineConfig);
        Mockito.when(validationContext.getStage()).thenReturn(pipelineConfig.getFirstStageConfig());
        jobConfig.validate(validationContext);
        Assert.assertThat(jobConfig.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(jobConfig.errors().getAll().get(0), Matchers.is("Empty resource name in job \"defaultJob\" of stage \"mingle\" of pipeline \"pipeline1\". If a template is used, please ensure that the resource parameters are defined for this pipeline."));
    }

    @Test
    public void shouldValidateAgainstPresenceOfBothResourcesAndElasticProfileId() {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfigWithJobConfigs("pipeline1");
        JobConfig jobConfig = JobConfigMother.createJobConfigWithJobNameAndEmptyResources();
        ValidationContext validationContext = Mockito.mock(ValidationContext.class);
        jobConfig.setElasticProfileId("docker.unit-test");
        Mockito.when(validationContext.getPipeline()).thenReturn(pipelineConfig);
        Mockito.when(validationContext.getStage()).thenReturn(pipelineConfig.getFirstStageConfig());
        jobConfig.validate(validationContext);
        Assert.assertThat(jobConfig.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(jobConfig.errors().on(ELASTIC_PROFILE_ID), Matchers.is("Job cannot have both `resource` and `elasticProfileId`"));
        Assert.assertThat(jobConfig.errors().on(RESOURCES), Matchers.is("Job cannot have both `resource` and `elasticProfileId`"));
    }

    @Test
    public void shouldValidateElasticProfileId() {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfigWithJobConfigs("pipeline1");
        JobConfig jobConfig = JobConfigMother.createJobConfigWithJobNameAndEmptyResources();
        ValidationContext validationContext = Mockito.mock(ValidationContext.class);
        jobConfig.setResourceConfigs(new ResourceConfigs());
        jobConfig.setElasticProfileId("non-existent-profile-id");
        Mockito.when(validationContext.getPipeline()).thenReturn(pipelineConfig);
        Mockito.when(validationContext.getStage()).thenReturn(pipelineConfig.getFirstStageConfig());
        Mockito.when(validationContext.isValidProfileId("non-existent-profile-id")).thenReturn(false);
        jobConfig.validate(validationContext);
        Assert.assertThat(jobConfig.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(jobConfig.errors().on(ELASTIC_PROFILE_ID), Matchers.is("No profile defined corresponding to profile_id 'non-existent-profile-id'"));
    }

    @Test
    public void shouldErrorOutIfTwoJobsHaveSameName() {
        HashMap<String, JobConfig> visitedConfigs = new HashMap<>();
        visitedConfigs.put("defaultJob".toLowerCase(), new JobConfig("defaultJob"));
        JobConfig defaultJob = new JobConfig("defaultJob");
        defaultJob.validateNameUniqueness(visitedConfigs);
        Assert.assertThat(defaultJob.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(defaultJob.errors().on(NAME), Matchers.is("You have defined multiple jobs called 'defaultJob'. Job names are case-insensitive and must be unique."));
        JobConfig defaultJobAllLowerCase = new JobConfig("defaultjob");
        defaultJobAllLowerCase.validateNameUniqueness(visitedConfigs);
        Assert.assertThat(defaultJobAllLowerCase.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(defaultJobAllLowerCase.errors().on(NAME), Matchers.is("You have defined multiple jobs called 'defaultjob'. Job names are case-insensitive and must be unique."));
    }

    @Test
    public void shouldNotValidateJobNameUniquenessInAbsenceOfName() {
        JobConfig job = new JobConfig();
        job.validateNameUniqueness(new HashMap());
        Assert.assertTrue(job.errors().isEmpty());
    }

    @Test
    public void shouldNotValidateJobNameUniquenessIfNameIsEmptyString() {
        JobConfig job = new JobConfig(" ");
        job.validateNameUniqueness(new HashMap());
        Assert.assertTrue(job.errors().isEmpty());
    }

    @Test
    public void shouldPopulateEnvironmentVariablesFromAttributeMap() {
        JobConfig jobConfig = new JobConfig();
        HashMap map = new HashMap();
        HashMap valueHashMap = new HashMap();
        valueHashMap.put("name", "FOO");
        valueHashMap.put("value", "BAR");
        map.put(ENVIRONMENT_VARIABLES, valueHashMap);
        EnvironmentVariablesConfig mockEnvironmentVariablesConfig = Mockito.mock(EnvironmentVariablesConfig.class);
        jobConfig.setVariables(mockEnvironmentVariablesConfig);
        jobConfig.setConfigAttributes(map);
        Mockito.verify(mockEnvironmentVariablesConfig).setConfigAttributes(valueHashMap);
    }

    @Test
    public void shouldPopulateResourcesFromAttributeMap() {
        HashMap map = new HashMap();
        String value = "a,  b,c   ,d,e";
        map.put(RESOURCES, value);
        ResourceConfigs resourceConfigs = new ResourceConfigs();
        resourceConfigs.add(new ResourceConfig("z"));
        JobConfig jobConfig = new JobConfig(new CaseInsensitiveString("job-name"), resourceConfigs, null);
        jobConfig.setConfigAttributes(map);
        Assert.assertThat(jobConfig.resourceConfigs().size(), Matchers.is(5));
    }

    @Test
    public void shouldPopulateTabsFromAttributeMap() {
        JobConfig jobConfig = new JobConfig("job-name");
        jobConfig.setConfigAttributes(DataStructureUtils.m(TABS, DataStructureUtils.a(DataStructureUtils.m(Tab.NAME, "tab1", PATH, "path1"), DataStructureUtils.m(Tab.NAME, "tab2", PATH, "path2"))));
        Assert.assertThat(jobConfig.getTabs().size(), Matchers.is(2));
        Assert.assertThat(jobConfig.getTabs().get(0).getName(), Matchers.is("tab1"));
        Assert.assertThat(jobConfig.getTabs().get(1).getName(), Matchers.is("tab2"));
        Assert.assertThat(jobConfig.getTabs().get(0).getPath(), Matchers.is("path1"));
        Assert.assertThat(jobConfig.getTabs().get(1).getPath(), Matchers.is("path2"));
    }

    @Test
    public void shouldSetJobRunTypeCorrectly_forRails4() {
        // single instance
        HashMap map1 = new HashMap();
        map1.put(RUN_TYPE, RUN_SINGLE_INSTANCE);
        map1.put(RUN_INSTANCE_COUNT, "10");// should be ignored

        JobConfig jobConfig1 = new JobConfig();
        jobConfig1.setConfigAttributes(map1);
        Assert.assertThat(jobConfig1.isRunOnAllAgents(), Matchers.is(false));
        Assert.assertThat(jobConfig1.isRunMultipleInstanceType(), Matchers.is(false));
        Assert.assertThat(jobConfig1.getRunInstanceCount(), Matchers.is(Matchers.nullValue()));
        // run on all agents
        HashMap map2 = new HashMap();
        map2.put(RUN_TYPE, RUN_ON_ALL_AGENTS);
        JobConfig jobConfig2 = new JobConfig();
        jobConfig2.setConfigAttributes(map2);
        Assert.assertThat(jobConfig2.isRunOnAllAgents(), Matchers.is(true));
        Assert.assertThat(jobConfig2.isRunMultipleInstanceType(), Matchers.is(false));
        Assert.assertThat(jobConfig2.getRunInstanceCount(), Matchers.is(Matchers.nullValue()));
        // run multiple instance
        HashMap map3 = new HashMap();
        map3.put(RUN_TYPE, RUN_MULTIPLE_INSTANCE);
        map3.put(RUN_INSTANCE_COUNT, "10");
        JobConfig jobConfig3 = new JobConfig();
        jobConfig3.setConfigAttributes(map3);
        Assert.assertThat(jobConfig3.isRunMultipleInstanceType(), Matchers.is(true));
        Assert.assertThat(jobConfig3.getRunInstanceCountValue(), Matchers.is(10));
        Assert.assertThat(jobConfig3.isRunOnAllAgents(), Matchers.is(false));
        HashMap map4 = new HashMap();
        map4.put(RUN_TYPE, RUN_MULTIPLE_INSTANCE);
        map4.put(RUN_INSTANCE_COUNT, "");
        JobConfig jobConfig4 = new JobConfig();
        jobConfig4.setConfigAttributes(map4);
        Assert.assertThat(jobConfig4.isRunMultipleInstanceType(), Matchers.is(false));
        Assert.assertThat(jobConfig4.getRunInstanceCount(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(jobConfig4.isRunOnAllAgents(), Matchers.is(false));
    }

    @Test
    public void shouldResetJobRunTypeCorrectly() {
        HashMap map1 = new HashMap();
        map1.put(RUN_TYPE, RUN_MULTIPLE_INSTANCE);
        map1.put(RUN_INSTANCE_COUNT, "10");
        JobConfig jobConfig = new JobConfig();
        jobConfig.setConfigAttributes(map1);
        Assert.assertThat(jobConfig.getRunInstanceCountValue(), Matchers.is(10));
        Assert.assertThat(jobConfig.isRunMultipleInstanceType(), Matchers.is(true));
        Assert.assertThat(jobConfig.isRunOnAllAgents(), Matchers.is(false));
        // should not reset value when correct key not present
        HashMap map2 = new HashMap();
        jobConfig.setConfigAttributes(map2);
        Assert.assertThat(jobConfig.getRunInstanceCountValue(), Matchers.is(10));
        Assert.assertThat(jobConfig.isRunMultipleInstanceType(), Matchers.is(true));
        Assert.assertThat(jobConfig.isRunOnAllAgents(), Matchers.is(false));
        // reset value for same job config
        HashMap map3 = new HashMap();
        map3.put(RUN_TYPE, RUN_SINGLE_INSTANCE);
        jobConfig.setConfigAttributes(map3);
        Assert.assertThat(jobConfig.isRunMultipleInstanceType(), Matchers.is(false));
        Assert.assertThat(jobConfig.getRunInstanceCount(), Matchers.is(Matchers.nullValue()));
        Assert.assertThat(jobConfig.isRunOnAllAgents(), Matchers.is(false));
    }

    @Test
    public void shouldPopulateArtifactPlansFromAttributeMap() {
        HashMap map = new HashMap();
        HashMap valueHashMap = new HashMap();
        valueHashMap.put("src", "dest");
        valueHashMap.put("src1", "dest1");
        map.put(ARTIFACT_CONFIGS, valueHashMap);
        ArtifactConfigs mockArtifactConfigs = Mockito.mock(ArtifactConfigs.class);
        JobConfig jobConfig = new JobConfig(new CaseInsensitiveString("job-name"), new ResourceConfigs(), mockArtifactConfigs);
        jobConfig.setConfigAttributes(map);
        Mockito.verify(mockArtifactConfigs).setConfigAttributes(valueHashMap);
    }

    @Test
    public void shouldValidateThatTheTimeoutIsAValidNumber() {
        JobConfig job = new JobConfig("job");
        job.setTimeout("5.5");
        job.validate(ConfigSaveValidationContext.forChain(new BasicCruiseConfig()));
        Assert.assertThat(job.errors().isEmpty(), Matchers.is(true));
    }

    @Test
    public void shouldMarkJobInvalidIfTimeoutIsNotAValidNumber() {
        JobConfig job = new JobConfig("job");
        job.setTimeout("5.5MN");
        job.validate(ConfigSaveValidationContext.forChain(new BasicCruiseConfig()));
        Assert.assertThat(job.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(job.errors().on(TIMEOUT), Matchers.is("Timeout should be a valid number as it represents number of minutes"));
    }

    @Test
    public void shouldReturnTimeoutType() {
        JobConfig job = new JobConfig("job");
        Assert.assertThat(job.getTimeoutType(), Matchers.is(DEFAULT_TIMEOUT));
        job.setTimeout("0");
        Assert.assertThat(job.getTimeoutType(), Matchers.is(NEVER_TIMEOUT));
        job.setTimeout("10");
        Assert.assertThat(job.getTimeoutType(), Matchers.is(OVERRIDE_TIMEOUT));
    }

    @Test
    public void shouldReturnRunTypeCorrectly() {
        JobConfig job = new JobConfig("job");
        Assert.assertThat(job.getRunType(), Matchers.is(RUN_SINGLE_INSTANCE));
        job.setRunOnAllAgents(true);
        Assert.assertThat(job.getRunType(), Matchers.is(RUN_ON_ALL_AGENTS));
        job.setRunOnAllAgents(false);
        job.setRunInstanceCount(10);
        Assert.assertThat(job.getRunType(), Matchers.is(RUN_MULTIPLE_INSTANCE));
    }

    @Test
    public void shouldErrorOutWhenTimeoutIsANegativeNumber() {
        JobConfig jobConfig = new JobConfig("job");
        jobConfig.setTimeout("-1");
        jobConfig.validate(ConfigSaveValidationContext.forChain(new BasicCruiseConfig()));
        Assert.assertThat(jobConfig.errors().isEmpty(), Matchers.is(false));
        Assert.assertThat(jobConfig.errors().on(TIMEOUT), Matchers.is("Timeout cannot be a negative number as it represents number of minutes"));
    }

    @Test
    public void shouldValidateTree() {
        ResourceConfigs resourceConfigs = Mockito.mock(ResourceConfigs.class);
        Mockito.when(resourceConfigs.iterator()).thenReturn(new ResourceConfigs().iterator());
        ArtifactConfigs artifactConfigs = Mockito.mock(ArtifactConfigs.class);
        ArtifactPropertiesConfig properties = Mockito.mock(ArtifactPropertiesConfig.class);
        Tasks tasks = Mockito.mock(Tasks.class);
        Tabs tabs = Mockito.mock(Tabs.class);
        EnvironmentVariablesConfig variables = Mockito.mock(EnvironmentVariablesConfig.class);
        Mockito.when(tasks.validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class))).thenReturn(true);
        Mockito.when(resourceConfigs.validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class))).thenReturn(true);
        Mockito.when(properties.validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class))).thenReturn(true);
        Mockito.when(artifactConfigs.validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class))).thenReturn(true);
        Mockito.when(tabs.validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class))).thenReturn(true);
        Mockito.when(variables.validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class))).thenReturn(true);
        JobConfig jobConfig = new JobConfig(new CaseInsensitiveString("job"), resourceConfigs, artifactConfigs, tasks);
        jobConfig.setTabs(tabs);
        jobConfig.setProperties(properties);
        jobConfig.setVariables(variables);
        PipelineConfigSaveValidationContext context = PipelineConfigSaveValidationContext.forChain(true, "group", new PipelineConfig(), new StageConfig(), jobConfig);
        Assert.assertThat(jobConfig.validateTree(context), Matchers.is(true));
        ArgumentCaptor<PipelineConfigSaveValidationContext> captor = ArgumentCaptor.forClass(PipelineConfigSaveValidationContext.class);
        Mockito.verify(tasks).validateTree(captor.capture());
        PipelineConfigSaveValidationContext childContext = captor.getValue();
        Assert.assertThat(childContext.getParent(), Matchers.is(jobConfig));
        Mockito.verify(resourceConfigs).validateTree(childContext);
        Mockito.verify(properties).validateTree(childContext);
        Mockito.verify(artifactConfigs).validateTree(childContext);
        Mockito.verify(tabs).validateTree(childContext);
        Mockito.verify(variables).validateTree(childContext);
    }

    @Test
    public void shouldFailValidationIfAnyDescendentIsInvalid() {
        ResourceConfigs resourceConfigs = Mockito.mock(ResourceConfigs.class);
        Mockito.when(resourceConfigs.iterator()).thenReturn(new ResourceConfigs().iterator());
        ArtifactConfigs artifactConfigs = Mockito.mock(ArtifactConfigs.class);
        ArtifactPropertiesConfig properties = Mockito.mock(ArtifactPropertiesConfig.class);
        Tasks tasks = Mockito.mock(Tasks.class);
        Tabs tabs = Mockito.mock(Tabs.class);
        EnvironmentVariablesConfig variables = Mockito.mock(EnvironmentVariablesConfig.class);
        Mockito.when(tasks.validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class))).thenReturn(false);
        Mockito.when(resourceConfigs.validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class))).thenReturn(false);
        Mockito.when(properties.validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class))).thenReturn(false);
        Mockito.when(artifactConfigs.validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class))).thenReturn(false);
        Mockito.when(tabs.validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class))).thenReturn(false);
        Mockito.when(variables.validateTree(ArgumentMatchers.any(PipelineConfigSaveValidationContext.class))).thenReturn(false);
        JobConfig jobConfig = new JobConfig(new CaseInsensitiveString("job"), resourceConfigs, artifactConfigs, tasks);
        jobConfig.setTabs(tabs);
        jobConfig.setProperties(properties);
        jobConfig.setVariables(variables);
        PipelineConfigSaveValidationContext context = PipelineConfigSaveValidationContext.forChain(true, "group", new PipelineConfig(), new StageConfig(), jobConfig);
        Assert.assertThat(jobConfig.validateTree(context), Matchers.is(false));
        ArgumentCaptor<PipelineConfigSaveValidationContext> captor = ArgumentCaptor.forClass(PipelineConfigSaveValidationContext.class);
        Mockito.verify(tasks).validateTree(captor.capture());
        PipelineConfigSaveValidationContext childContext = captor.getValue();
        Assert.assertThat(childContext.getParent(), Matchers.is(jobConfig));
        Mockito.verify(resourceConfigs).validateTree(childContext);
        Mockito.verify(properties).validateTree(childContext);
        Mockito.verify(artifactConfigs).validateTree(childContext);
        Mockito.verify(tabs).validateTree(childContext);
        Mockito.verify(variables).validateTree(childContext);
    }

    @Test
    public void shouldValidateAgainstSettingRunOnAllAgentsForAJobAssignedToElasticAgent() {
        JobConfig jobConfig = new JobConfig(new CaseInsensitiveString("test"));
        jobConfig.setRunOnAllAgents(true);
        jobConfig.setElasticProfileId("ubuntu-dev");
        jobConfig.validate(ConfigSaveValidationContext.forChain(new BasicCruiseConfig()));
        ConfigErrors configErrors = jobConfig.errors();
        Assert.assertThat(configErrors.isEmpty(), Matchers.is(false));
        Assert.assertThat(configErrors.on(RUN_TYPE), Matchers.is("Job cannot be set to 'run on all agents' when assigned to an elastic agent"));
    }

    @Test
    public void shouldEncryptSecurePropertiesForOnlyFetchExternalArtifactTask() {
        JobConfig jobConfig = new JobConfig(new CaseInsensitiveString("job"));
        FetchPluggableArtifactTask mockFetchExternalArtifactTask = Mockito.mock(FetchPluggableArtifactTask.class);
        jobConfig.addTask(mockFetchExternalArtifactTask);
        jobConfig.encryptSecureProperties(new BasicCruiseConfig(), new PipelineConfig(), jobConfig);
        Mockito.verify(mockFetchExternalArtifactTask).encryptSecureProperties(ArgumentMatchers.any(CruiseConfig.class), ArgumentMatchers.any(PipelineConfig.class), ArgumentMatchers.any(FetchPluggableArtifactTask.class));
    }
}

