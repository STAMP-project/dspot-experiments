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
package com.thoughtworks.go.server.service;


import RunOnAllAgents.CounterBasedJobNameGenerator;
import Username.ANONYMOUS;
import com.thoughtworks.go.config.materials.MaterialConfigs;
import com.thoughtworks.go.domain.buildcause.BuildCause;
import com.thoughtworks.go.helper.InstanceFactory;
import com.thoughtworks.go.util.Clock;
import com.thoughtworks.go.util.DataStructureUtils;
import com.thoughtworks.go.util.TimeProvider;
import com.thoughtworks.go.utils.Timeout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static Approval.SUCCESS;
import static com.thoughtworks.go.helper.ModificationsMother.modifyOneFile;


public class InstanceFactoryTest {
    private InstanceFactory instanceFactory;

    private Clock clock;

    @Test
    public void shouldSetTheConfigVersionOnSchedulingAStage() throws Exception {
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("foo-pipeline", "foo-stage", "foo-job");
        DefaultSchedulingContext schedulingContext = new DefaultSchedulingContext("loser");
        String md5 = "foo-md5";
        Stage actualStage = instanceFactory.createStageInstance(pipelineConfig, new CaseInsensitiveString("foo-stage"), schedulingContext, md5, clock);
        Assert.assertThat(actualStage.getConfigVersion(), is(md5));
    }

    @Test
    public void shouldThrowStageNotFoundExceptionWhenStageDoesNotExist() {
        PipelineConfig pipelineConfig = new PipelineConfig(new CaseInsensitiveString("cruise"), new MaterialConfigs(), new StageConfig(new CaseInsensitiveString("first"), new JobConfigs()));
        try {
            instanceFactory.createStageInstance(pipelineConfig, new CaseInsensitiveString("doesNotExist"), new DefaultSchedulingContext(), "md5", clock);
            Assert.fail("Found the stage doesNotExist but, well, it doesn't");
        } catch (StageNotFoundException expected) {
            Assert.assertThat(expected.getMessage(), is("Stage 'doesNotExist' not found in pipeline 'cruise'"));
        }
    }

    @Test
    public void shouldCreateAStageInstanceThroughInstanceFactory() {
        PipelineConfig pipelineConfig = new PipelineConfig(new CaseInsensitiveString("cruise"), new MaterialConfigs(), new StageConfig(new CaseInsensitiveString("first"), new JobConfigs(new JobConfig("job1"), new JobConfig("job2"))));
        Stage actualStage = instanceFactory.createStageInstance(pipelineConfig, new CaseInsensitiveString("first"), new DefaultSchedulingContext(), "md5", clock);
        JobInstances jobInstances = new JobInstances();
        jobInstances.add(new JobInstance("job1", clock));
        jobInstances.add(new JobInstance("job2", clock));
        Stage expectedStage = new Stage("first", jobInstances, "Unknown", null, SUCCESS, clock);
        Assert.assertThat(actualStage, is(expectedStage));
    }

    @Test
    public void shouldCreatePipelineInstanceWithEnvironmentVariablesOverriddenAccordingToScope() {
        StageConfig stageConfig = StageConfigMother.custom("stage", "foo", "bar");
        JobConfig fooConfig = stageConfig.jobConfigByConfigName(new CaseInsensitiveString("foo"));
        fooConfig.addVariable("foo", "foo");
        JobConfig barConfig = stageConfig.jobConfigByConfigName(new CaseInsensitiveString("bar"));
        barConfig.addVariable("foo", "bar");
        MaterialConfigs materialConfigs = MaterialConfigsMother.defaultMaterialConfigs();
        PipelineConfig pipelineConfig = new PipelineConfig(new CaseInsensitiveString("pipeline"), materialConfigs, stageConfig);
        DefaultSchedulingContext context = new DefaultSchedulingContext("anonymous");
        Pipeline instance = instanceFactory.createPipelineInstance(pipelineConfig, ModificationsMother.forceBuild(pipelineConfig), context, "some-md5", new TimeProvider());
        Assert.assertThat(instance.findStage("stage").findJob("foo").getPlan().getVariables(), is(new EnvironmentVariables(Arrays.asList(new EnvironmentVariable("foo", "foo")))));
        Assert.assertThat(instance.findStage("stage").findJob("bar").getPlan().getVariables(), is(new EnvironmentVariables(Arrays.asList(new EnvironmentVariable("foo", "bar")))));
    }

    @Test
    public void shouldOverridePipelineEnvironmentVariablesFromBuildCauseForLabel() {
        StageConfig stageConfig = StageConfigMother.custom("stage", "foo", "bar");
        MaterialConfigs materialConfigs = MaterialConfigsMother.defaultMaterialConfigs();
        DefaultSchedulingContext context = new DefaultSchedulingContext("anonymous");
        PipelineConfig pipelineConfig = new PipelineConfig(new CaseInsensitiveString("pipeline"), materialConfigs, stageConfig);
        pipelineConfig.addEnvironmentVariable("VAR", "value");
        pipelineConfig.setLabelTemplate("${ENV:VAR}");
        BuildCause buildCause = ModificationsMother.forceBuild(pipelineConfig);
        EnvironmentVariables overriddenVars = buildCause.getVariables();
        overriddenVars.add("VAR", "overriddenValue");
        buildCause.setVariables(overriddenVars);
        Pipeline instance = instanceFactory.createPipelineInstance(pipelineConfig, buildCause, context, "some-md5", new TimeProvider());
        instance.updateCounter(1);
        Assert.assertThat(instance.getLabel(), is("overriddenValue"));
    }

    @Test
    public void shouldSchedulePipelineWithFirstStage() {
        StageConfig stageOneConfig = StageConfigMother.stageConfig("dev", BuildPlanMother.withBuildPlans("functional", "unit"));
        StageConfig stageTwoConfig = StageConfigMother.stageConfig("qa", BuildPlanMother.withBuildPlans("suiteOne", "suiteTwo"));
        MaterialConfigs materialConfigs = MaterialConfigsMother.defaultMaterialConfigs();
        PipelineConfig pipelineConfig = new PipelineConfig(new CaseInsensitiveString("mingle"), materialConfigs, stageOneConfig, stageTwoConfig);
        BuildCause buildCause = BuildCause.createManualForced(modifyOneFile(pipelineConfig), ANONYMOUS);
        Pipeline pipeline = instanceFactory.createPipelineInstance(pipelineConfig, buildCause, new DefaultSchedulingContext("test"), "some-md5", new TimeProvider());
        Assert.assertThat(pipeline.getName(), is("mingle"));
        Assert.assertThat(pipeline.getStages().size(), is(1));
        Assert.assertThat(pipeline.getStages().get(0).getName(), is("dev"));
        Assert.assertThat(pipeline.getStages().get(0).getJobInstances().get(0).getName(), is("functional"));
    }

    @Test
    public void shouldSetAutoApprovalOnStageInstance() {
        StageConfig stageConfig = StageConfigMother.custom("test", Approval.automaticApproval());
        Stage instance = instanceFactory.createStageInstance(stageConfig, new DefaultSchedulingContext("anyone"), "md5", new TimeProvider());
        Assert.assertThat(instance.getApprovalType(), is(GoConstants.APPROVAL_SUCCESS));
    }

    @Test
    public void shouldSetManualApprovalOnStageInstance() {
        StageConfig stageConfig = StageConfigMother.custom("test", Approval.manualApproval());
        Stage instance = instanceFactory.createStageInstance(stageConfig, new DefaultSchedulingContext("anyone"), "md5", new TimeProvider());
        Assert.assertThat(instance.getApprovalType(), is(GoConstants.APPROVAL_MANUAL));
    }

    @Test
    public void shouldSetFetchMaterialsFlagOnStageInstance() throws Exception {
        StageConfig stageConfig = StageConfigMother.custom("test", Approval.automaticApproval());
        stageConfig.setFetchMaterials(false);
        Stage instance = instanceFactory.createStageInstance(stageConfig, new DefaultSchedulingContext("anyone"), "md5", new TimeProvider());
        Assert.assertThat(instance.shouldFetchMaterials(), is(false));
    }

    @Test
    public void shouldClear_DatabaseIds_State_and_Result_ForJobObjectHierarchy() {
        Date old = new DateTime().minusDays(2).toDate();
        JobInstance rails = jobInstance(old, "rails", 7, 10);
        JobInstance java = jobInstance(old, "java", 12, 22);
        Stage stage = stage(9, rails, java);
        Assert.assertThat(stage.hasRerunJobs(), is(false));
        Stage newStage = instanceFactory.createStageForRerunOfJobs(stage, DataStructureUtils.a("rails"), new DefaultSchedulingContext("loser", new Agents()), StageConfigMother.custom("dev", "rails", "java"), new TimeProvider(), "md5");
        Assert.assertThat(stage.hasRerunJobs(), is(false));
        Assert.assertThat(newStage.getId(), is((-1L)));
        Assert.assertThat(newStage.getJobInstances().size(), is(2));
        Assert.assertThat(newStage.isLatestRun(), is(true));
        JobInstance newRails = newStage.getJobInstances().getByName("rails");
        assertNewJob(old, newRails);
        JobInstance newJava = newStage.getJobInstances().getByName("java");
        assertCopiedJob(newJava, 12L);
    }

    @Test
    public void should_MaintainRerunOfReferences_InCaseOfMultipleCopyForRerunOperations() {
        Date old = new DateTime().minusDays(2).toDate();
        JobInstance rails = jobInstance(old, "rails", 7, 10);
        JobInstance java = jobInstance(old, "java", 12, 22);
        Stage stage = stage(9, rails, java);
        stage.setCounter(2);
        Stage newStage = instanceFactory.createStageForRerunOfJobs(stage, DataStructureUtils.a("rails"), new DefaultSchedulingContext("loser", new Agents()), StageConfigMother.custom("dev", "rails", "java"), new TimeProvider(), "md5");
        newStage.setCounter(3);
        Assert.assertThat(newStage.getId(), is((-1L)));
        Assert.assertThat(newStage.getJobInstances().size(), is(2));
        Assert.assertThat(newStage.isLatestRun(), is(true));
        Assert.assertThat(newStage.getRerunOfCounter(), is(2));
        JobInstance newJava = newStage.getJobInstances().getByName("java");
        assertCopiedJob(newJava, 12L);
        // set id, to assert if original ends up pointing to copied job's id
        newJava.setId(18L);
        newStage = instanceFactory.createStageForRerunOfJobs(newStage, DataStructureUtils.a("rails"), new DefaultSchedulingContext("loser", new Agents()), StageConfigMother.custom("dev", "rails", "java"), new TimeProvider(), "md5");
        newStage.setCounter(4);
        Assert.assertThat(newStage.getId(), is((-1L)));
        Assert.assertThat(newStage.getJobInstances().size(), is(2));
        Assert.assertThat(newStage.isLatestRun(), is(true));
        Assert.assertThat(newStage.getRerunOfCounter(), is(2));
        newJava = newStage.getJobInstances().getByName("java");
        assertCopiedJob(newJava, 12L);
    }

    @Test
    public void shouldCloneStageForGivenJobsWithLatestMd5() {
        TimeProvider timeProvider = new TimeProvider() {
            @Override
            public Date currentTime() {
                return new Date();
            }

            public DateTime currentDateTime() {
                throw new UnsupportedOperationException("Not implemented");
            }

            public DateTime timeoutTime(Timeout timeout) {
                throw new UnsupportedOperationException("Not implemented");
            }
        };
        JobInstance firstJob = new JobInstance("first-job", timeProvider);
        JobInstance secondJob = new JobInstance("second-job", timeProvider);
        JobInstances jobInstances = new JobInstances(firstJob, secondJob);
        Stage stage = StageMother.custom("test", jobInstances);
        Stage clonedStage = instanceFactory.createStageForRerunOfJobs(stage, Arrays.asList("first-job"), new DefaultSchedulingContext("loser", new Agents()), StageConfigMother.custom("test", "first-job", "second-job"), new TimeProvider(), "latest");
        Assert.assertThat(clonedStage.getConfigVersion(), is("latest"));
    }

    @Test
    public void shouldAddEnvironmentVariablesPresentInTheScheduleContextToJobPlan() {
        JobConfig jobConfig = new JobConfig("foo");
        EnvironmentVariablesConfig variablesConfig = new EnvironmentVariablesConfig();
        variablesConfig.add("blahVar", "blahVal");
        SchedulingContext context = new DefaultSchedulingContext("Loser");
        context = context.overrideEnvironmentVariables(variablesConfig);
        JobPlan plan = instanceFactory.createJobPlan(jobConfig, context);
        Assert.assertThat(plan.getVariables(), hasItem(new EnvironmentVariable("blahVar", "blahVal")));
    }

    @Test
    public void shouldOverrideEnvironmentVariablesPresentInTheScheduleContextToJobPlan() {
        EnvironmentVariablesConfig variablesConfig = new EnvironmentVariablesConfig();
        variablesConfig.add("blahVar", "blahVal");
        variablesConfig.add("differentVar", "differentVal");
        JobConfig jobConfig = new JobConfig("foo");
        jobConfig.setVariables(variablesConfig);
        EnvironmentVariablesConfig overridenConfig = new EnvironmentVariablesConfig();
        overridenConfig.add("blahVar", "originalVal");
        overridenConfig.add("secondVar", "secondVal");
        SchedulingContext context = new DefaultSchedulingContext();
        context = context.overrideEnvironmentVariables(overridenConfig);
        JobPlan plan = instanceFactory.createJobPlan(jobConfig, context);
        Assert.assertThat(plan.getVariables().size(), is(3));
        Assert.assertThat(plan.getVariables(), hasItem(new EnvironmentVariable("blahVar", "blahVal")));
        Assert.assertThat(plan.getVariables(), hasItem(new EnvironmentVariable("secondVar", "secondVal")));
        Assert.assertThat(plan.getVariables(), hasItem(new EnvironmentVariable("differentVar", "differentVal")));
    }

    @Test
    public void shouldAddEnvironmentVariablesToJobPlan() {
        EnvironmentVariablesConfig variablesConfig = new EnvironmentVariablesConfig();
        variablesConfig.add("blahVar", "blahVal");
        JobConfig jobConfig = new JobConfig("foo");
        jobConfig.setVariables(variablesConfig);
        SchedulingContext context = new DefaultSchedulingContext();
        JobPlan plan = instanceFactory.createJobPlan(jobConfig, context);
        Assert.assertThat(plan.getVariables(), hasItem(new EnvironmentVariable("blahVar", "blahVal")));
    }

    @Test
    public void shouldCreateJobPlan() {
        ResourceConfigs resourceConfigs = new ResourceConfigs();
        ArtifactConfigs artifactConfigs = new ArtifactConfigs();
        JobConfig jobConfig = new JobConfig(new CaseInsensitiveString("test"), resourceConfigs, artifactConfigs);
        JobPlan plan = instanceFactory.createJobPlan(jobConfig, new DefaultSchedulingContext());
        Assert.assertThat(plan, is(new DefaultJobPlan(new Resources(resourceConfigs), ArtifactPlan.toArtifactPlans(artifactConfigs), new ArrayList(), (-1), new JobIdentifier(), null, new EnvironmentVariables(), new EnvironmentVariables(), null)));
    }

    @Test
    public void shouldReturnBuildInstance() {
        ArtifactConfigs artifactConfigs = new ArtifactConfigs();
        JobConfig jobConfig = new JobConfig(new CaseInsensitiveString("test"), null, artifactConfigs);
        RunOnAllAgents.CounterBasedJobNameGenerator jobNameGenerator = new RunOnAllAgents.CounterBasedJobNameGenerator(CaseInsensitiveString.str(jobConfig.name()));
        JobInstances jobs = instanceFactory.createJobInstance(new CaseInsensitiveString("stage_foo"), jobConfig, new DefaultSchedulingContext(), new TimeProvider(), jobNameGenerator);
        JobInstance jobInstance = jobs.first();
        Assert.assertThat(jobConfig.name(), is(new CaseInsensitiveString(jobInstance.getName())));
        Assert.assertThat(jobInstance.getState(), is(JobState.Scheduled));
        Assert.assertThat(jobInstance.getScheduledDate(), is(notNullValue()));
    }

    @Test
    public void shouldUseRightNameGenerator() {
        StageConfig stageConfig = StageConfigMother.custom("dev", "rails", "java", "html");
        JobConfig railsConfig = stageConfig.getJobs().getJob(new CaseInsensitiveString("rails"));
        railsConfig.setRunOnAllAgents(true);
        railsConfig.addResourceConfig("foobar");
        JobConfig javaConfig = stageConfig.getJobs().getJob(new CaseInsensitiveString("java"));
        javaConfig.setRunInstanceCount(2);
        AgentConfig agent1 = new AgentConfig("abcd1234", "host", "127.0.0.2", new ResourceConfigs(new ResourceConfig("foobar")));
        AgentConfig agent2 = new AgentConfig("1234abcd", "ghost", "192.168.1.2", new ResourceConfigs(new ResourceConfig("baz"), new ResourceConfig("foobar")));
        AgentConfig agent3 = new AgentConfig("7890abdc", "lost", "10.4.3.55", new ResourceConfigs(new ResourceConfig("crapyagent")));
        DefaultSchedulingContext schedulingContext = new DefaultSchedulingContext("loser", new Agents(agent1, agent2, agent3));
        Stage stageInstance = instanceFactory.createStageInstance(stageConfig, schedulingContext, "md5", clock);
        JobInstances jobInstances = stageInstance.getJobInstances();
        Assert.assertThat(jobInstances.size(), is(5));
        assertRunOnAllAgentsJobInstance(jobInstances.get(0), "rails-runOnAll-1");
        assertRunOnAllAgentsJobInstance(jobInstances.get(1), "rails-runOnAll-2");
        assertRunMultipleJobInstance(jobInstances.get(2), "java-runInstance-1");
        assertRunMultipleJobInstance(jobInstances.get(3), "java-runInstance-2");
        assertSimpleJobInstance(jobInstances.get(4), "html");
    }

    /* SingleJobInstance */
    @Test
    public void shouldCreateASingleJobIfRunOnAllAgentsIsFalse() throws Exception {
        JobConfig jobConfig = new JobConfig("foo");
        SchedulingContext context = Mockito.mock(com.thoughtworks.go.domain.SchedulingContext.class);
        Mockito.when(context.getEnvironmentVariablesConfig()).thenReturn(new EnvironmentVariablesConfig());
        Mockito.when(context.overrideEnvironmentVariables(ArgumentMatchers.any(com.thoughtworks.go.domain.EnvironmentVariablesConfig.class))).thenReturn(context);
        RunOnAllAgents.CounterBasedJobNameGenerator jobNameGenerator = new RunOnAllAgents.CounterBasedJobNameGenerator(CaseInsensitiveString.str(jobConfig.name()));
        JobInstances jobs = instanceFactory.createJobInstance(new CaseInsensitiveString("someStage"), jobConfig, new DefaultSchedulingContext(), new TimeProvider(), jobNameGenerator);
        Assert.assertThat(jobs.toArray(), hasItemInArray(hasProperty("name", is("foo"))));
        Assert.assertThat(jobs.toArray(), hasItemInArray(hasProperty("agentUuid", nullValue())));
        Assert.assertThat(jobs.toArray(), hasItemInArray(hasProperty("runOnAllAgents", is(false))));
        Assert.assertThat(jobs.size(), is(1));
    }

    @Test
    public void shouldNotRerun_WhenJobConfigDoesNotExistAnymore_ForSingleInstanceJob() {
        Date old = new DateTime().minusDays(2).toDate();
        JobInstance rails = jobInstance(old, "rails", 7, 10);
        JobInstance java = jobInstance(old, "java", 12, 22);
        Stage stage = stage(9, rails, java);
        Stage newStage = null;
        CannotRerunJobException exception = null;
        try {
            newStage = instanceFactory.createStageForRerunOfJobs(stage, DataStructureUtils.a("rails"), new DefaultSchedulingContext("loser", new Agents()), StageConfigMother.custom("dev", "java"), new TimeProvider(), "md5");
            Assert.fail("should not schedule when job config does not exist anymore");
        } catch (CannotRerunJobException e) {
            exception = e;
        }
        Assert.assertThat(exception.getJobName(), is("rails"));
        Assert.assertThat(newStage, is(nullValue()));
    }

    @Test
    public void shouldClearAgentAssignment_ForSingleInstanceJobType() {
        Date old = new DateTime().minusDays(2).toDate();
        JobInstance rails = jobInstance(old, "rails", 7, 10);
        JobInstance java = jobInstance(old, "java", 12, 22);
        Stage stage = stage(9, rails, java);
        Stage newStage = instanceFactory.createStageForRerunOfJobs(stage, DataStructureUtils.a("rails"), new DefaultSchedulingContext("loser", new Agents()), StageConfigMother.custom("dev", "rails", "java"), new TimeProvider(), "md5");
        Assert.assertThat(newStage.getJobInstances().getByName("rails").getAgentUuid(), is(nullValue()));
        Assert.assertThat(newStage.getJobInstances().getByName("java").getAgentUuid(), is(not(nullValue())));
    }

    @Test
    public void shouldNotRerun_WhenJobConfigIsChangedToRunMultipleInstance_ForSingleJobInstance() {
        Date old = new DateTime().minusDays(2).toDate();
        StageConfig stageConfig = StageConfigMother.custom("dev", "rails", "java");
        JobConfig railsConfig = stageConfig.getJobs().getJob(new CaseInsensitiveString("rails"));
        DefaultSchedulingContext schedulingContext = new DefaultSchedulingContext("loser", new Agents());
        JobInstances jobs = instanceFactory.createJobInstance(new CaseInsensitiveString("dev"), railsConfig, schedulingContext, new TimeProvider(), null);
        Stage stage = createStageInstance(old, jobs);
        Stage newStage = null;
        railsConfig.setRunInstanceCount(10);
        CannotRerunJobException exception = null;
        try {
            newStage = instanceFactory.createStageForRerunOfJobs(stage, DataStructureUtils.a("rails"), schedulingContext, stageConfig, new TimeProvider(), "md5");
            Assert.fail("should not schedule since job config changed to run multiple instance");
        } catch (CannotRerunJobException e) {
            exception = e;
        }
        Assert.assertThat(exception.getJobName(), is("rails"));
        Assert.assertThat(exception.getInformation(), is("Run configuration for job has been changed to 'run multiple instance'."));
        Assert.assertThat(newStage, is(nullValue()));
    }

    /* RunOnAllAgents tests */
    @Test
    public void shouldCreateAJobForEachAgentIfRunOnAllAgentsIsTrue() throws Exception {
        Agents agents = new Agents();
        agents.add(new AgentConfig("uuid1"));
        agents.add(new AgentConfig("uuid2"));
        JobConfig jobConfig = new JobConfig("foo");
        jobConfig.setRunOnAllAgents(true);
        SchedulingContext context = Mockito.mock(com.thoughtworks.go.domain.SchedulingContext.class);
        Mockito.when(context.getApprovedBy()).thenReturn("chris");
        Mockito.when(context.findAgentsMatching(new ResourceConfigs())).thenReturn(agents);
        Mockito.when(context.getEnvironmentVariablesConfig()).thenReturn(new EnvironmentVariablesConfig());
        Mockito.when(context.overrideEnvironmentVariables(ArgumentMatchers.any(com.thoughtworks.go.domain.EnvironmentVariablesConfig.class))).thenReturn(context);
        RunOnAllAgents.CounterBasedJobNameGenerator jobNameGenerator = new RunOnAllAgents.CounterBasedJobNameGenerator(CaseInsensitiveString.str(jobConfig.name()));
        JobInstances jobs = instanceFactory.createJobInstance(new CaseInsensitiveString("stageName"), jobConfig, context, new TimeProvider(), jobNameGenerator);
        Assert.assertThat(jobs.toArray(), hasItemInArray(hasProperty("name", is("foo-runOnAll-1"))));
        Assert.assertThat(jobs.toArray(), hasItemInArray(hasProperty("agentUuid", is("uuid1"))));
        Assert.assertThat(jobs.toArray(), hasItemInArray(hasProperty("runOnAllAgents", is(true))));
        Assert.assertThat(jobs.toArray(), hasItemInArray(hasProperty("name", is("foo-runOnAll-1"))));
        Assert.assertThat(jobs.toArray(), hasItemInArray(hasProperty("agentUuid", is("uuid2"))));
        Assert.assertThat(jobs.toArray(), hasItemInArray(hasProperty("runOnAllAgents", is(true))));
        Assert.assertThat(jobs.size(), is(2));
    }

    @Test
    public void shouldFailWhenDoesNotFindAnyMatchingAgents() throws Exception {
        JobConfig jobConfig = new JobConfig("foo");
        jobConfig.setRunOnAllAgents(true);
        SchedulingContext context = Mockito.mock(com.thoughtworks.go.domain.SchedulingContext.class);
        Mockito.when(context.getApprovedBy()).thenReturn("chris");
        Mockito.when(context.findAgentsMatching(new ResourceConfigs())).thenReturn(new ArrayList());
        Mockito.when(context.getEnvironmentVariablesConfig()).thenReturn(new EnvironmentVariablesConfig());
        Mockito.when(context.overrideEnvironmentVariables(ArgumentMatchers.any(com.thoughtworks.go.domain.EnvironmentVariablesConfig.class))).thenReturn(context);
        try {
            RunOnAllAgents.CounterBasedJobNameGenerator jobNameGenerator = new RunOnAllAgents.CounterBasedJobNameGenerator(CaseInsensitiveString.str(jobConfig.name()));
            instanceFactory.createJobInstance(new CaseInsensitiveString("myStage"), jobConfig, new DefaultSchedulingContext(), new TimeProvider(), jobNameGenerator);
            Assert.fail("should have failed as no agents matched");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), is("Could not find matching agents to run job [foo] of stage [myStage]."));
        }
    }

    @Test
    public void shouldFailWhenNoAgentsmatchAJob() throws Exception {
        DefaultSchedulingContext context = new DefaultSchedulingContext("raghu/vinay", new Agents());
        JobConfig fooJob = new JobConfig(new CaseInsensitiveString("foo"), new ResourceConfigs(), new ArtifactConfigs());
        fooJob.setRunOnAllAgents(true);
        StageConfig stageConfig = new StageConfig(new CaseInsensitiveString("blah-stage"), new JobConfigs(fooJob, new JobConfig(new CaseInsensitiveString("bar"), new ResourceConfigs(), new ArtifactConfigs())));
        try {
            instanceFactory.createStageInstance(stageConfig, context, "md5", new TimeProvider());
            Assert.fail("expected exception but not thrown");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), is("Could not find matching agents to run job [foo] of stage [blah-stage]."));
        }
    }

    @Test
    public void shouldBomb_ForRerun_OfASingleInstanceJobType_WhichWasEarlierRunOnAll_WithTwoRunOnAllInstancesSelectedForRerun() {
        Date old = new DateTime().minusDays(2).toDate();
        StageConfig stageConfig = StageConfigMother.custom("dev", "rails", "java");
        JobConfig railsConfig = stageConfig.getJobs().getJob(new CaseInsensitiveString("rails"));
        railsConfig.setRunOnAllAgents(true);
        railsConfig.addResourceConfig("foobar");
        DefaultSchedulingContext schedulingContext = new DefaultSchedulingContext("loser", new Agents(new AgentConfig("abcd1234", "host", "127.0.0.2", new ResourceConfigs(new ResourceConfig("foobar"))), new AgentConfig("1234abcd", "ghost", "192.168.1.2", new ResourceConfigs(new ResourceConfig("baz"), new ResourceConfig("foobar"))), new AgentConfig("7890abdc", "lost", "10.4.3.55", new ResourceConfigs(new ResourceConfig("crapyagent")))));
        RunOnAllAgents.CounterBasedJobNameGenerator jobNameGenerator = new RunOnAllAgents.CounterBasedJobNameGenerator(CaseInsensitiveString.str(railsConfig.name()));
        JobInstances jobs = instanceFactory.createJobInstance(new CaseInsensitiveString("dev"), railsConfig, schedulingContext, new TimeProvider(), jobNameGenerator);
        Stage stage = createStageInstance(old, jobs);
        railsConfig.setRunOnAllAgents(false);
        try {
            instanceFactory.createStageForRerunOfJobs(stage, DataStructureUtils.a("rails-runOnAll-1", "rails-runOnAll-2"), schedulingContext, stageConfig, new TimeProvider(), "md5");
            Assert.fail("should have failed when multiple run on all agents jobs are selected when job-config does not have run on all flag anymore");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), is("Cannot schedule multiple instances of job named 'rails'."));
        }
    }

    @Test
    public void should_NOT_ClearAgentAssignment_ForRerun_OfASingleInstanceJobType_WhichWasEarlierRunOnAll() {
        Date old = new DateTime().minusDays(2).toDate();
        StageConfig stageConfig = StageConfigMother.custom("dev", "rails", "java");
        JobConfig railsConfig = stageConfig.getJobs().getJob(new CaseInsensitiveString("rails"));
        railsConfig.setRunOnAllAgents(true);
        railsConfig.addResourceConfig("foobar");
        DefaultSchedulingContext schedulingContext = new DefaultSchedulingContext("loser", new Agents(new AgentConfig("abcd1234", "host", "127.0.0.2", new ResourceConfigs(new ResourceConfig("foobar"))), new AgentConfig("1234abcd", "ghost", "192.168.1.2", new ResourceConfigs(new ResourceConfig("baz"), new ResourceConfig("foobar"))), new AgentConfig("7890abdc", "lost", "10.4.3.55", new ResourceConfigs(new ResourceConfig("crapyagent")))));
        RunOnAllAgents.CounterBasedJobNameGenerator jobNameGenerator = new RunOnAllAgents.CounterBasedJobNameGenerator(CaseInsensitiveString.str(railsConfig.name()));
        JobInstances jobs = instanceFactory.createJobInstance(new CaseInsensitiveString("dev"), railsConfig, schedulingContext, new TimeProvider(), jobNameGenerator);
        Stage stage = createStageInstance(old, jobs);
        railsConfig.setRunOnAllAgents(false);
        Stage newStage = instanceFactory.createStageForRerunOfJobs(stage, DataStructureUtils.a("rails-runOnAll-1"), schedulingContext, stageConfig, new TimeProvider(), "md5");
        Assert.assertThat(newStage.getJobInstances().size(), is(3));
        JobInstance newRailsJob = newStage.getJobInstances().getByName("rails");
        assertNewJob(old, newRailsJob);
        Assert.assertThat(newRailsJob.getAgentUuid(), is("abcd1234"));
        JobInstance copiedRailsJob = newStage.getJobInstances().getByName("rails-runOnAll-2");
        assertCopiedJob(copiedRailsJob, 102L);
        Assert.assertThat(copiedRailsJob.getAgentUuid(), is("1234abcd"));
        JobInstance copiedJavaJob = newStage.getJobInstances().getByName("java");
        assertCopiedJob(copiedJavaJob, 12L);
        Assert.assertThat(copiedJavaJob.getAgentUuid(), is(not(nullValue())));
    }

    @Test
    public void shouldClearAgentAssignment_ForRunOnAllAgentsJobType() {
        Date old = new DateTime().minusDays(2).toDate();
        JobInstance rails = jobInstance(old, "rails", 7, 10);
        JobInstance java = jobInstance(old, "java", 12, 22);
        Stage stage = stage(9, rails, java);
        StageConfig stageConfig = StageConfigMother.custom("dev", "rails", "java");
        JobConfig railsConfig = stageConfig.getJobs().getJob(new CaseInsensitiveString("rails"));
        railsConfig.setRunOnAllAgents(true);
        railsConfig.addResourceConfig("foobar");
        Stage newStage = instanceFactory.createStageForRerunOfJobs(stage, DataStructureUtils.a("rails"), new DefaultSchedulingContext("loser", new Agents(new AgentConfig("abcd1234", "host", "127.0.0.2", new ResourceConfigs(new ResourceConfig("foobar"))), new AgentConfig("1234abcd", "ghost", "192.168.1.2", new ResourceConfigs(new ResourceConfig("baz"), new ResourceConfig("foobar"))), new AgentConfig("7890abdc", "lost", "10.4.3.55", new ResourceConfigs(new ResourceConfig("crapyagent"))))), stageConfig, new TimeProvider(), "md5");
        Assert.assertThat(newStage.getJobInstances().size(), is(3));
        JobInstance newRailsFirstJob = newStage.getJobInstances().getByName("rails-runOnAll-1");
        assertNewJob(old, newRailsFirstJob);
        Assert.assertThat(newRailsFirstJob.getAgentUuid(), is("abcd1234"));
        JobInstance newRailsSecondJob = newStage.getJobInstances().getByName("rails-runOnAll-2");
        assertNewJob(old, newRailsSecondJob);
        Assert.assertThat(newRailsSecondJob.getAgentUuid(), is("1234abcd"));
        JobInstance copiedJavaJob = newStage.getJobInstances().getByName("java");
        assertCopiedJob(copiedJavaJob, 12L);
        Assert.assertThat(copiedJavaJob.getAgentUuid(), is(not(nullValue())));
    }

    @Test
    public void shouldNotRerun_WhenJobConfigDoesNotExistAnymore_ForRunOnAllAgentsJobInstance() {
        Date old = new DateTime().minusDays(2).toDate();
        StageConfig stageConfig = StageConfigMother.custom("dev", "rails", "java");
        JobConfig railsConfig = stageConfig.getJobs().getJob(new CaseInsensitiveString("rails"));
        railsConfig.setRunOnAllAgents(true);
        railsConfig.addResourceConfig("foobar");
        DefaultSchedulingContext schedulingContext = new DefaultSchedulingContext("loser", new Agents(new AgentConfig("abcd1234", "host", "127.0.0.2", new ResourceConfigs(new ResourceConfig("foobar"))), new AgentConfig("1234abcd", "ghost", "192.168.1.2", new ResourceConfigs(new ResourceConfig("baz"), new ResourceConfig("foobar"))), new AgentConfig("7890abdc", "lost", "10.4.3.55", new ResourceConfigs(new ResourceConfig("crapyagent")))));
        RunOnAllAgents.CounterBasedJobNameGenerator jobNameGenerator = new RunOnAllAgents.CounterBasedJobNameGenerator(CaseInsensitiveString.str(railsConfig.name()));
        JobInstances jobs = instanceFactory.createJobInstance(new CaseInsensitiveString("dev"), railsConfig, schedulingContext, new TimeProvider(), jobNameGenerator);
        Stage stage = createStageInstance(old, jobs);
        Stage newStage = null;
        CannotRerunJobException exception = null;
        try {
            newStage = instanceFactory.createStageForRerunOfJobs(stage, DataStructureUtils.a("rails-runOnAll-1"), new DefaultSchedulingContext("loser", new Agents()), StageConfigMother.custom("dev", "java"), new TimeProvider(), "md5");
            Assert.fail("should not schedule when job config does not exist anymore");
        } catch (CannotRerunJobException e) {
            exception = e;
        }
        Assert.assertThat(exception.getJobName(), is("rails"));
        Assert.assertThat(newStage, is(nullValue()));
    }

    @Test
    public void shouldNotRerun_WhenJobConfigIsChangedToRunMultipleInstance_ForRunOnAllAgentsJobInstance() {
        Date old = new DateTime().minusDays(2).toDate();
        StageConfig stageConfig = StageConfigMother.custom("dev", "rails", "java");
        JobConfig railsConfig = stageConfig.getJobs().getJob(new CaseInsensitiveString("rails"));
        railsConfig.setRunOnAllAgents(true);
        railsConfig.addResourceConfig("foobar");
        AgentConfig agent1 = new AgentConfig("abcd1234", "host", "127.0.0.2", new ResourceConfigs(new ResourceConfig("foobar")));
        AgentConfig agent2 = new AgentConfig("1234abcd", "ghost", "192.168.1.2", new ResourceConfigs(new ResourceConfig("baz"), new ResourceConfig("foobar")));
        AgentConfig agent3 = new AgentConfig("7890abdc", "lost", "10.4.3.55", new ResourceConfigs(new ResourceConfig("crapyagent")));
        DefaultSchedulingContext schedulingContext = new DefaultSchedulingContext("loser", new Agents(agent1, agent2, agent3));
        RunOnAllAgents.CounterBasedJobNameGenerator jobNameGenerator = new RunOnAllAgents.CounterBasedJobNameGenerator(CaseInsensitiveString.str(railsConfig.name()));
        JobInstances jobs = instanceFactory.createJobInstance(new CaseInsensitiveString("dev"), railsConfig, schedulingContext, new TimeProvider(), jobNameGenerator);
        Stage stage = createStageInstance(old, jobs);
        Stage newStage = null;
        railsConfig.setRunOnAllAgents(false);
        railsConfig.setRunInstanceCount(10);
        CannotRerunJobException exception = null;
        try {
            newStage = instanceFactory.createStageForRerunOfJobs(stage, DataStructureUtils.a("rails-runOnAll-1"), schedulingContext, stageConfig, new TimeProvider(), "md5");
            Assert.fail("should not schedule since job config changed to run multiple instance");
        } catch (CannotRerunJobException e) {
            exception = e;
        }
        Assert.assertThat(exception.getJobName(), is("rails"));
        Assert.assertThat(exception.getInformation(), is("Run configuration for job has been changed to 'run multiple instance'."));
        Assert.assertThat(newStage, is(nullValue()));
    }

    /* RunMultipleInstance tests */
    @Test
    public void shouldCreateJobInstancesCorrectly_RunMultipleInstance() {
        Date old = new DateTime().minusDays(2).toDate();
        StageConfig stageConfig = StageConfigMother.custom("dev", "rails", "java");
        JobConfig railsConfig = stageConfig.getJobs().getJob(new CaseInsensitiveString("rails"));
        railsConfig.setRunInstanceCount(3);
        DefaultSchedulingContext schedulingContext = new DefaultSchedulingContext("loser", new Agents());
        RunMultipleInstance.CounterBasedJobNameGenerator jobNameGenerator = new RunMultipleInstance.CounterBasedJobNameGenerator(CaseInsensitiveString.str(railsConfig.name()));
        JobInstances jobs = instanceFactory.createJobInstance(new CaseInsensitiveString("dev"), railsConfig, schedulingContext, new TimeProvider(), jobNameGenerator);
        Assert.assertThat(jobs.get(0).getName(), is("rails-runInstance-1"));
        assertEnvironmentVariable(jobs.get(0), 0, "GO_JOB_RUN_INDEX", "1");
        assertEnvironmentVariable(jobs.get(0), 1, "GO_JOB_RUN_COUNT", "3");
        Assert.assertThat(jobs.get(1).getName(), is("rails-runInstance-2"));
        assertEnvironmentVariable(jobs.get(1), 0, "GO_JOB_RUN_INDEX", "2");
        assertEnvironmentVariable(jobs.get(1), 1, "GO_JOB_RUN_COUNT", "3");
        Assert.assertThat(jobs.get(2).getName(), is("rails-runInstance-3"));
        assertEnvironmentVariable(jobs.get(2), 0, "GO_JOB_RUN_INDEX", "3");
        assertEnvironmentVariable(jobs.get(2), 1, "GO_JOB_RUN_COUNT", "3");
        Stage stage = createStageInstance(old, jobs);
        JobInstances jobInstances = stage.getJobInstances();
        Assert.assertThat(jobInstances.size(), is(4));
        assertRunMultipleJobInstance(jobInstances.get(0), "rails-runInstance-1");
        assertRunMultipleJobInstance(jobInstances.get(1), "rails-runInstance-2");
        assertRunMultipleJobInstance(jobInstances.get(2), "rails-runInstance-3");
        Assert.assertThat(jobInstances.get(3).getName(), is("java"));
    }

    @Test
    public void shouldCreateJobInstancesCorrectly_RunMultipleInstance_Rerun() {
        Date old = new DateTime().minusDays(2).toDate();
        StageConfig stageConfig = StageConfigMother.custom("dev", "rails", "java");
        JobConfig railsConfig = stageConfig.getJobs().getJob(new CaseInsensitiveString("rails"));
        railsConfig.setRunInstanceCount(3);
        DefaultSchedulingContext schedulingContext = new DefaultSchedulingContext("loser", new Agents());
        RunMultipleInstance.CounterBasedJobNameGenerator jobNameGenerator = new RunMultipleInstance.CounterBasedJobNameGenerator(CaseInsensitiveString.str(railsConfig.name()));
        JobInstances jobs = instanceFactory.createJobInstance(new CaseInsensitiveString("dev"), railsConfig, schedulingContext, new TimeProvider(), jobNameGenerator);
        Assert.assertThat(jobs.get(0).getName(), is("rails-runInstance-1"));
        assertEnvironmentVariable(jobs.get(0), 0, "GO_JOB_RUN_INDEX", "1");
        assertEnvironmentVariable(jobs.get(0), 1, "GO_JOB_RUN_COUNT", "3");
        Assert.assertThat(jobs.get(1).getName(), is("rails-runInstance-2"));
        assertEnvironmentVariable(jobs.get(1), 0, "GO_JOB_RUN_INDEX", "2");
        assertEnvironmentVariable(jobs.get(1), 1, "GO_JOB_RUN_COUNT", "3");
        Assert.assertThat(jobs.get(2).getName(), is("rails-runInstance-3"));
        assertEnvironmentVariable(jobs.get(2), 0, "GO_JOB_RUN_INDEX", "3");
        assertEnvironmentVariable(jobs.get(2), 1, "GO_JOB_RUN_COUNT", "3");
        Stage stage = createStageInstance(old, jobs);
        Stage stageForRerun = instanceFactory.createStageForRerunOfJobs(stage, DataStructureUtils.a("rails-runInstance-1", "rails-runInstance-2"), schedulingContext, stageConfig, clock, "md5");
        JobInstances jobsForRerun = stageForRerun.getJobInstances();
        Assert.assertThat(jobsForRerun.get(0).getName(), is("rails-runInstance-3"));
        assertEnvironmentVariable(jobsForRerun.get(0), 0, "GO_JOB_RUN_INDEX", "3");
        assertEnvironmentVariable(jobsForRerun.get(0), 1, "GO_JOB_RUN_COUNT", "3");
        Assert.assertThat(jobsForRerun.get(2).getName(), is("rails-runInstance-1"));
        assertEnvironmentVariable(jobsForRerun.get(2), 0, "GO_JOB_RUN_INDEX", "1");
        assertEnvironmentVariable(jobsForRerun.get(2), 1, "GO_JOB_RUN_COUNT", "3");
        Assert.assertThat(jobsForRerun.get(3).getName(), is("rails-runInstance-2"));
        assertEnvironmentVariable(jobsForRerun.get(3), 0, "GO_JOB_RUN_INDEX", "2");
        assertEnvironmentVariable(jobsForRerun.get(3), 1, "GO_JOB_RUN_COUNT", "3");
        Assert.assertThat(jobsForRerun.size(), is(4));
        assertRunMultipleJobInstance(jobsForRerun.get(0), "rails-runInstance-3");
        Assert.assertThat(jobsForRerun.get(1).getName(), is("java"));
        assertReRunMultipleJobInstance(jobsForRerun.get(2), "rails-runInstance-1");
        assertReRunMultipleJobInstance(jobsForRerun.get(3), "rails-runInstance-2");
    }

    @Test
    public void shouldNotRerun_WhenJobConfigDoesNotExistAnymore_ForRunMultipleInstance() {
        Date old = new DateTime().minusDays(2).toDate();
        StageConfig stageConfig = StageConfigMother.custom("dev", "rails", "java");
        JobConfig railsConfig = stageConfig.getJobs().getJob(new CaseInsensitiveString("rails"));
        railsConfig.setRunInstanceCount(3);
        DefaultSchedulingContext schedulingContext = new DefaultSchedulingContext("loser", new Agents());
        RunMultipleInstance.CounterBasedJobNameGenerator jobNameGenerator = new RunMultipleInstance.CounterBasedJobNameGenerator(CaseInsensitiveString.str(railsConfig.name()));
        JobInstances jobs = instanceFactory.createJobInstance(new CaseInsensitiveString("dev"), railsConfig, schedulingContext, new TimeProvider(), jobNameGenerator);
        Stage stage = createStageInstance(old, jobs);
        Stage newStage = null;
        CannotRerunJobException exception = null;
        try {
            newStage = instanceFactory.createStageForRerunOfJobs(stage, DataStructureUtils.a("rails-runInstance-1"), schedulingContext, StageConfigMother.custom("dev", "java"), new TimeProvider(), "md5");
            Assert.fail("should not schedule when job config does not exist anymore");
        } catch (CannotRerunJobException e) {
            exception = e;
        }
        Assert.assertThat(exception.getJobName(), is("rails"));
        Assert.assertThat(exception.getInformation(), is("Configuration for job doesn't exist."));
        Assert.assertThat(newStage, is(nullValue()));
    }

    @Test
    public void shouldNotRerun_WhenJobRunConfigIsChanged_ForRunMultipleInstance() {
        Date old = new DateTime().minusDays(2).toDate();
        StageConfig stageConfig = StageConfigMother.custom("dev", "rails", "java");
        JobConfig railsConfig = stageConfig.getJobs().getJob(new CaseInsensitiveString("rails"));
        railsConfig.setRunInstanceCount(3);
        DefaultSchedulingContext schedulingContext = new DefaultSchedulingContext("loser", new Agents());
        RunMultipleInstance.CounterBasedJobNameGenerator jobNameGenerator = new RunMultipleInstance.CounterBasedJobNameGenerator(CaseInsensitiveString.str(railsConfig.name()));
        JobInstances jobs = instanceFactory.createJobInstance(new CaseInsensitiveString("dev"), railsConfig, schedulingContext, new TimeProvider(), jobNameGenerator);
        Stage stage = createStageInstance(old, jobs);
        Stage newStage = null;
        railsConfig.setRunOnAllAgents(true);
        railsConfig.setRunInstanceCount(0);
        CannotRerunJobException exception = null;
        try {
            newStage = instanceFactory.createStageForRerunOfJobs(stage, DataStructureUtils.a("rails-runInstance-1"), schedulingContext, stageConfig, new TimeProvider(), "md5");
            Assert.fail("should not schedule since job config changed to run multiple instance");
        } catch (CannotRerunJobException e) {
            exception = e;
        }
        Assert.assertThat(exception.getJobName(), is("rails"));
        Assert.assertThat(exception.getInformation(), is("Run configuration for job has been changed to 'run on all agents'."));
        Assert.assertThat(newStage, is(nullValue()));
        railsConfig.setRunOnAllAgents(false);
        try {
            newStage = instanceFactory.createStageForRerunOfJobs(stage, DataStructureUtils.a("rails-runInstance-1"), schedulingContext, stageConfig, new TimeProvider(), "md5");
            Assert.fail("should not schedule since job config changed to run multiple instance");
        } catch (CannotRerunJobException e) {
            exception = e;
        }
        Assert.assertThat(exception.getJobName(), is("rails"));
        Assert.assertThat(exception.getInformation(), is("Run configuration for job has been changed to 'simple'."));
        Assert.assertThat(newStage, is(nullValue()));
    }
}

