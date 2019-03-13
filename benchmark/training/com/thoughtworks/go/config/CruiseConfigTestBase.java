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
package com.thoughtworks.go.config;


import GoConfigGraphWalker.Handler;
import ScmMaterialConfig.FOLDER;
import com.thoughtworks.go.config.materials.PluggableSCMMaterialConfig;
import com.thoughtworks.go.config.materials.git.GitMaterialConfig;
import com.thoughtworks.go.config.materials.mercurial.HgMaterialConfig;
import com.thoughtworks.go.config.materials.perforce.P4MaterialConfig;
import com.thoughtworks.go.config.materials.svn.SvnMaterialConfig;
import com.thoughtworks.go.config.merge.MergeEnvironmentConfig;
import com.thoughtworks.go.config.merge.MergePipelineConfigs;
import com.thoughtworks.go.domain.CruiseConfig;
import com.thoughtworks.go.domain.config.Configuration;
import com.thoughtworks.go.domain.config.ConfigurationKey;
import com.thoughtworks.go.domain.config.ConfigurationValue;
import com.thoughtworks.go.domain.materials.MaterialConfig;
import com.thoughtworks.go.domain.packagerepository.PackageDefinitionMother;
import com.thoughtworks.go.domain.packagerepository.PackageRepositoryMother;
import com.thoughtworks.go.domain.scm.SCM;
import com.thoughtworks.go.domain.scm.SCMMother;
import com.thoughtworks.go.helper.BasicPipelineConfigs;
import com.thoughtworks.go.helper.GoConfigMother;
import com.thoughtworks.go.helper.MaterialConfigsMother;
import com.thoughtworks.go.helper.PartialConfigMother;
import com.thoughtworks.go.helper.PipelineConfigMother;
import com.thoughtworks.go.security.GoCipher;
import com.thoughtworks.go.util.ReflectionUtil;
import com.thoughtworks.go.util.command.UrlArgument;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public abstract class CruiseConfigTestBase {
    public GoConfigMother goConfigMother;

    protected BasicPipelineConfigs pipelines;

    protected CruiseConfig cruiseConfig;

    @Test
    public void cloneForValidationShouldKeepProposedPartials() {
        cruiseConfig.setPartials(Arrays.asList(createPartial()));
        Assert.assertThat(cruiseConfig.getPartials().size(), is(1));
        cruiseConfig = cruiseConfig.cloneForValidation();
        Assert.assertThat(cruiseConfig.getPartials().size(), is(1));
    }

    @Test
    public void shouldLoadPasswordForGivenMaterialFingerprint() {
        MaterialConfig svnConfig = new SvnMaterialConfig("url", "loser", "boozer", true);
        PipelineConfig one = PipelineConfigMother.pipelineConfig("one", svnConfig, new JobConfigs(new JobConfig("job")));
        cruiseConfig.addPipeline("group-1", one);
        P4MaterialConfig p4One = new P4MaterialConfig("server_and_port", "outside_the_window");
        p4One.setPassword("abcdef");
        PipelineConfig two = PipelineConfigMother.pipelineConfig("two", p4One, new JobConfigs(new JobConfig("job")));
        cruiseConfig.addPipeline("group-2", two);
        P4MaterialConfig p4Two = new P4MaterialConfig("port_and_server", "inside_yourself");
        p4Two.setPassword("fedcba");
        PipelineConfig three = PipelineConfigMother.pipelineConfig("three", p4Two, new JobConfigs(new JobConfig("job")));
        cruiseConfig.addPipeline("group-3", three);
        Assert.assertThat(cruiseConfig.materialConfigFor(svnConfig.getFingerprint()), is(svnConfig));
        Assert.assertThat(cruiseConfig.materialConfigFor(p4One.getFingerprint()), is(p4One));
        Assert.assertThat(cruiseConfig.materialConfigFor(p4Two.getFingerprint()), is(p4Two));
        Assert.assertThat(cruiseConfig.materialConfigFor("some_crazy_fingerprint"), is(nullValue()));
    }

    @Test
    public void canFindMaterialConfigForUnderGivenPipelineWithMaterialFingerprint() {
        MaterialConfig fullClone = new GitMaterialConfig("url", "master", false);
        PipelineConfig one = PipelineConfigMother.pipelineConfig("one", fullClone, new JobConfigs(new JobConfig("job")));
        cruiseConfig.addPipeline("group-1", one);
        MaterialConfig shallowClone = new GitMaterialConfig("url", "master", true);
        PipelineConfig two = PipelineConfigMother.pipelineConfig("two", shallowClone, new JobConfigs(new JobConfig("job")));
        cruiseConfig.addPipeline("group-2", two);
        MaterialConfig others = new GitMaterialConfig("bar", "master", true);
        PipelineConfig three = PipelineConfigMother.pipelineConfig("three", others, new JobConfigs(new JobConfig("job")));
        cruiseConfig.addPipeline("group-3", three);
        String fingerprint = new GitMaterialConfig("url", "master").getFingerprint();
        Assert.assertThat(isShallowClone(), is(false));
        Assert.assertThat(isShallowClone(), is(true));
        Assert.assertThat(cruiseConfig.materialConfigFor(three.name(), fingerprint), is(nullValue()));
    }

    @Test
    public void shouldFindAllAgentResources() {
        cruiseConfig.agents().add(new AgentConfig("uuid", "host1", "127.0.0.1", new ResourceConfigs("from-agent")));
        Assert.assertThat(cruiseConfig.getAllResources(), hasItem(new ResourceConfig("from-agent")));
    }

    @Test
    public void shouldFindBuildPlanWithStages() throws Exception {
        try {
            cruiseConfig.jobConfigByName("cetaceans", "whales", "right whale", true);
            Assert.fail("Expected not to find right whale in stage whales in pipeline cetaceans");
        } catch (RuntimeException ex) {
            // ignore
        }
        addPipeline("cetaceans", "whales", jobConfig("whale"));
        try {
            cruiseConfig.jobConfigByName("cetaceans", "whales", "dolphin", true);
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), is("Job [dolphin] is not found in pipeline [cetaceans] stage [whales]."));
        }
        try {
            cruiseConfig.jobConfigByName("primates", "whales", "dolphin", true);
            Assert.fail("Expected not to find primates in stage whales in pipeline cetaceans");
        } catch (RuntimeException ex) {
            // ignore
        }
        JobConfig plan = jobConfig("baboon");
        addPipeline("primates", "apes", plan);
        Assert.assertThat(cruiseConfig.jobConfigByName("primates", "apes", "baboon", true), is(plan));
    }

    @Test
    public void shouldFindNextStage() {
        addPipelineWithStages("mingle", "dev", jobConfig("ut"), jobConfig("ft"));
        Assert.assertThat(cruiseConfig.hasNextStage(new CaseInsensitiveString("mingle"), new CaseInsensitiveString("dev")), is(true));
        StageConfig nextStage = cruiseConfig.nextStage(new CaseInsensitiveString("mingle"), new CaseInsensitiveString("dev"));
        Assert.assertThat(nextStage.name(), is(new CaseInsensitiveString("dev2")));
        Assert.assertThat(cruiseConfig.hasNextStage(new CaseInsensitiveString("mingle"), nextStage.name()), is(false));
    }

    @Test
    public void shouldFindPreviousStage() {
        addPipelineWithStages("mingle", "dev", jobConfig("ut"), jobConfig("ft"));
        Assert.assertThat(cruiseConfig.hasPreviousStage(new CaseInsensitiveString("mingle"), new CaseInsensitiveString("dev2")), is(true));
        StageConfig previousStage = cruiseConfig.previousStage(new CaseInsensitiveString("mingle"), new CaseInsensitiveString("dev2"));
        Assert.assertThat(previousStage.name(), is(new CaseInsensitiveString("dev")));
        Assert.assertThat(cruiseConfig.hasPreviousStage(new CaseInsensitiveString("mingle"), previousStage.name()), is(false));
    }

    @Test
    public void shouldKnowWhenBuildPlanNotInConfigFile() {
        pipelines.add(PipelineConfigMother.createPipelineConfig("pipeline", "stage", "build1", "build2"));
        Assert.assertThat(cruiseConfig.hasBuildPlan(new CaseInsensitiveString("pipeline"), new CaseInsensitiveString("stage"), "build1", true), is(true));
        Assert.assertThat(cruiseConfig.hasBuildPlan(new CaseInsensitiveString("pipeline"), new CaseInsensitiveString("stage"), "build2", true), is(true));
        Assert.assertThat(cruiseConfig.hasBuildPlan(new CaseInsensitiveString("pipeline"), new CaseInsensitiveString("stage"), "build3", true), is(false));
    }

    @Test
    public void shouldTellIfSMTPIsEnabled() {
        Assert.assertThat(cruiseConfig.isSmtpEnabled(), is(false));
        MailHost mailHost = new MailHost("abc", 12, "admin", "p", true, true, "anc@mail.com", "anc@mail.com");
        cruiseConfig.setServerConfig(new ServerConfig(null, mailHost, null, null));
        cruiseConfig.server().updateMailHost(mailHost);
        Assert.assertThat(cruiseConfig.isSmtpEnabled(), is(true));
    }

    @Test
    public void shouldReturnAMapOfAllTemplateNamesWithAssociatedPipelines() {
        PipelineTemplateConfig template = template("first_template");
        PipelineConfig pipelineConfig1 = PipelineConfigMother.pipelineConfig("first");
        pipelineConfig1.clear();
        pipelineConfig1.setTemplateName(new CaseInsensitiveString("first_template"));
        pipelineConfig1.usingTemplate(template);
        PipelineConfig pipelineConfig2 = PipelineConfigMother.pipelineConfig("second");
        pipelineConfig2.clear();
        pipelineConfig2.setTemplateName(new CaseInsensitiveString("FIRST_template"));
        pipelineConfig2.usingTemplate(template);
        PipelineConfig pipelineConfigWithoutTemplate = PipelineConfigMother.pipelineConfig("third");
        BasicPipelineConfigs pipelineConfigs = new BasicPipelineConfigs(pipelineConfig1, pipelineConfig2, pipelineConfigWithoutTemplate);
        pipelineConfigs.setOrigin(new FileConfigOrigin());
        CruiseConfig cruiseConfig = createCruiseConfig(pipelineConfigs);
        cruiseConfig.addTemplate(template);
        SecurityConfig securityConfig = new SecurityConfig(false);
        securityConfig.adminsConfig().add(new AdminUser(new CaseInsensitiveString("root")));
        cruiseConfig.server().useSecurity(securityConfig);
        Map<CaseInsensitiveString, Map<CaseInsensitiveString, Authorization>> allTemplatesWithAssociatedPipelines = cruiseConfig.templatesWithAssociatedPipelines();
        Assert.assertThat(allTemplatesWithAssociatedPipelines.size(), is(1));
        HashMap<CaseInsensitiveString, Map<CaseInsensitiveString, Authorization>> expectedTemplatesMap = new HashMap<>();
        expectedTemplatesMap.put(new CaseInsensitiveString("first_template"), new HashMap());
        expectedTemplatesMap.get(new CaseInsensitiveString("first_template")).put(new CaseInsensitiveString("first"), new Authorization());
        expectedTemplatesMap.get(new CaseInsensitiveString("first_template")).put(new CaseInsensitiveString("second"), new Authorization());
        Assert.assertThat(allTemplatesWithAssociatedPipelines, is(expectedTemplatesMap));
    }

    @Test
    public void shouldThrowExceptionWhenThereIsNoGroup() {
        CruiseConfig config = createCruiseConfig();
        try {
            config.isInFirstGroup(new CaseInsensitiveString("any-pipeline"));
            Assert.fail("should throw exception when there is no group");
        } catch (Exception e) {
            Assert.assertThat(e.getMessage(), is("No pipeline group defined yet!"));
        }
    }

    @Test
    public void shouldOfferAllTasksToVisitors() throws Exception {
        CruiseConfig config = createCruiseConfig();
        Task task1 = new ExecTask("ls", "-a", "");
        Task task2 = new AntTask();
        setupJobWithTasks(config, task1, task2);
        final List<Task> tasksVisited = new ArrayList<>();
        config.accept(new TaskConfigVisitor() {
            public void visit(PipelineConfig pipelineConfig, StageConfig stageConfig, JobConfig jobConfig, Task task) {
                tasksVisited.add(task);
            }
        });
        Assert.assertThat(tasksVisited.size(), is(2));
        Assert.assertThat(tasksVisited.get(0), is(task1));
        Assert.assertThat(tasksVisited.get(1), is(task2));
    }

    @Test
    public void shouldOfferNothingToVisitorsIfThereAreNoTasks() throws Exception {
        CruiseConfig config = createCruiseConfig();
        setupJobWithTasks(config, new NullTask());
        final List<Task> tasksVisited = new ArrayList<>();
        config.accept(new TaskConfigVisitor() {
            public void visit(PipelineConfig pipelineConfig, StageConfig stageConfig, JobConfig jobConfig, Task task) {
                tasksVisited.add(task);
            }
        });
        Assert.assertThat(tasksVisited.size(), is(0));
    }

    @Test
    public void shouldReturnTrueIfThereAreTwoPipelineGroups() throws Exception {
        CruiseConfig config = goConfigMother.cruiseConfigWithTwoPipelineGroups();
        Assert.assertThat("shouldReturnTrueIfThereAreTwoPipelineGroups", config.hasMultiplePipelineGroups(), is(true));
    }

    @Test
    public void shouldReturnFalseIfThereIsOnePipelineGroup() throws Exception {
        CruiseConfig config = goConfigMother.cruiseConfigWithOnePipelineGroup();
        Assert.assertThat("shouldReturnFalseIfThereIsOnePipelineGroup", config.hasMultiplePipelineGroups(), is(false));
    }

    @Test
    public void shouldFindDownstreamPipelines() throws Exception {
        CruiseConfig config = goConfigMother.defaultCruiseConfig();
        goConfigMother.addPipeline(config, "pipeline-1", "stage-1", "job-1");
        PipelineConfig pipeline2 = goConfigMother.addPipeline(config, "pipeline-2", "stage-2", "job-2");
        PipelineConfig pipeline3 = goConfigMother.addPipeline(config, "pipeline-3", "stage-3", "job-3");
        goConfigMother.setDependencyOn(config, pipeline2, "pipeline-1", "stage-1");
        goConfigMother.setDependencyOn(config, pipeline3, "pipeline-1", "stage-1");
        Iterable<PipelineConfig> downstream = config.getDownstreamPipelines("pipeline-1");
        Assert.assertThat(downstream, hasItem(pipeline2));
        Assert.assertThat(downstream, hasItem(pipeline3));
    }

    @Test
    public void shouldReturnFalseForEmptyCruiseConfig() throws Exception {
        CruiseConfig config = createCruiseConfig();
        Assert.assertThat("shouldReturnFalseForEmptyCruiseConfig", config.hasMultiplePipelineGroups(), is(false));
    }

    @Test
    public void shouldReturnFalseIfNoMailHost() throws Exception {
        Assert.assertThat(createCruiseConfig().isMailHostConfigured(), is(false));
    }

    @Test
    public void shouldReturnTrueIfMailHostIsConfigured() throws Exception {
        MailHost mailHost = new MailHost("hostName", 1234, "user", "pass", true, true, "from", "admin@local.com");
        Assert.assertThat(GoConfigMother.cruiseConfigWithMailHost(mailHost).isMailHostConfigured(), is(true));
    }

    @Test
    public void shouldNotLockAPipelineWhenItIsAddedToAnEnvironment() throws Exception {
        CruiseConfig config = GoConfigMother.configWithPipelines("pipeline-1");
        EnvironmentConfig env = config.addEnvironment("environment");
        env.addPipeline(new CaseInsensitiveString("pipeline-1"));
        Assert.assertThat(config.isPipelineLockable("pipeline-1"), is(false));
    }

    @Test
    public void shouldBeAbleToExplicitlyLockAPipeline() throws Exception {
        CruiseConfig config = GoConfigMother.configWithPipelines("pipeline-1");
        PipelineConfig pipelineConfig = config.pipelineConfigByName(new CaseInsensitiveString("pipeline-1"));
        pipelineConfig.lockExplicitly();
        Assert.assertThat(config.isPipelineLockable("pipeline-1"), is(true));
    }

    @Test
    public void shouldCollectAllTheErrorsInTheChildren() {
        CruiseConfig config = GoConfigMother.configWithPipelines("pipeline-1");
        shouldCollectAllTheErrorsInTheChildrenHelper(config);
    }

    @Test
    public void getAllErrors_shouldCollectAllErrorsInTheChildren() {
        CruiseConfig config = GoConfigMother.configWithPipelines("pipeline-1");
        SecurityAuthConfig ldapConfig = new SecurityAuthConfig("ldap", "cd.go.authorization.ldap");
        ldapConfig.errors().add("uri", "invalid ldap uri");
        ldapConfig.errors().add("searchBase", "invalid search base");
        config.server().security().securityAuthConfigs().add(ldapConfig);
        PipelineConfig pipelineConfig = config.pipelineConfigByName(new CaseInsensitiveString("pipeline-1"));
        pipelineConfig.errors().add("base", "Some base errors");
        P4MaterialConfig p4MaterialConfig = new P4MaterialConfig("localhost:1999", "view");
        pipelineConfig.addMaterialConfig(p4MaterialConfig);
        p4MaterialConfig.errors().add("materialName", "material name does not follow pattern");
        StageConfig stage = pipelineConfig.first();
        stage.errors().add("role", "Roles must be proper");
        List<ConfigErrors> allErrors = config.getAllErrors();
        Assert.assertThat(allErrors.size(), is(4));
        Assert.assertThat(allErrors.get(0).on("uri"), is("invalid ldap uri"));
        Assert.assertThat(allErrors.get(0).on("searchBase"), is("invalid search base"));
        Assert.assertThat(allErrors.get(1).on("base"), is("Some base errors"));
        Assert.assertThat(allErrors.get(2).on("role"), is("Roles must be proper"));
        Assert.assertThat(allErrors.get(3).on("materialName"), is("material name does not follow pattern"));
    }

    @Test
    public void getAllErrors_shouldIgnoreErrorsOnElementToBeSkipped() {
        CruiseConfig config = GoConfigMother.configWithPipelines("pipeline-1");
        SecurityAuthConfig ldapConfig = new SecurityAuthConfig("ldap", "cd.go.authorization.ldap");
        ldapConfig.errors().add("uri", "invalid ldap uri");
        ldapConfig.errors().add("searchBase", "invalid search base");
        config.server().security().securityAuthConfigs().add(ldapConfig);
        PipelineConfig pipelineConfig = config.pipelineConfigByName(new CaseInsensitiveString("pipeline-1"));
        pipelineConfig.errors().add("base", "Some base errors");
        P4MaterialConfig p4MaterialConfig = new P4MaterialConfig("localhost:1999", "view");
        pipelineConfig.addMaterialConfig(p4MaterialConfig);
        p4MaterialConfig.errors().add("materialName", "material name does not follow pattern");
        StageConfig stage = pipelineConfig.first();
        stage.errors().add("role", "Roles must be proper");
        List<ConfigErrors> allErrors = config.getAllErrorsExceptFor(p4MaterialConfig);
        Assert.assertThat(allErrors.size(), is(3));
        Assert.assertThat(allErrors.get(0).on("uri"), is("invalid ldap uri"));
        Assert.assertThat(allErrors.get(0).on("searchBase"), is("invalid search base"));
        Assert.assertThat(allErrors.get(1).on("base"), is("Some base errors"));
        Assert.assertThat(allErrors.get(2).on("role"), is("Roles must be proper"));
    }

    @Test
    public void getAllErrors_shouldRetainAllErrorsWhenNoSubjectGiven() {
        CruiseConfig config = GoConfigMother.configWithPipelines("pipeline-1");
        SecurityAuthConfig ldapConfig = new SecurityAuthConfig("ldap", "cd.go.authorization.ldap");
        ldapConfig.errors().add("uri", "invalid ldap uri");
        ldapConfig.errors().add("searchBase", "invalid search base");
        config.server().security().securityAuthConfigs().add(ldapConfig);
        PipelineConfig pipelineConfig = config.pipelineConfigByName(new CaseInsensitiveString("pipeline-1"));
        pipelineConfig.errors().add("base", "Some base errors");
        P4MaterialConfig p4MaterialConfig = new P4MaterialConfig("localhost:1999", "view");
        pipelineConfig.addMaterialConfig(p4MaterialConfig);
        p4MaterialConfig.errors().add("materialName", "material name does not follow pattern");
        StageConfig stage = pipelineConfig.first();
        stage.errors().add("role", "Roles must be proper");
        List<ConfigErrors> allErrors = config.getAllErrorsExceptFor(null);
        Assert.assertThat(allErrors.size(), is(4));
    }

    @Test
    public void shouldBuildTheValidationContextForAnOnCancelTask() {
        CruiseConfig config = GoConfigMother.configWithPipelines("pipeline-1");
        PipelineConfig pipelineConfig = config.pipelineConfigByName(new CaseInsensitiveString("pipeline-1"));
        StageConfig stageConfig = pipelineConfig.get(0);
        JobConfig jobConfig = stageConfig.getJobs().get(0);
        ExecTask execTask = new ExecTask("ls", "-la", "dir");
        Task mockTask = Mockito.mock(Task.class);
        Mockito.when(mockTask.errors()).thenReturn(new ConfigErrors());
        execTask.setCancelTask(mockTask);
        jobConfig.addTask(execTask);
        config.validateAfterPreprocess();
        Mockito.verify(mockTask).validate(ConfigSaveValidationContext.forChain(config, config.getGroups(), config.getGroups().findGroup("defaultGroup"), pipelineConfig, stageConfig, stageConfig.getJobs(), jobConfig, jobConfig.getTasks(), execTask, execTask.onCancelConfig()));
    }

    @Test
    public void shouldNotConsiderEqualObjectsAsSame() {
        CruiseConfigTestBase.MyValidatable foo = new CruiseConfigTestBase.AlwaysEqualMyValidatable();
        CruiseConfigTestBase.MyValidatable bar = new CruiseConfigTestBase.AlwaysEqualMyValidatable();
        foo.innerValidatable = bar;
        GoConfigGraphWalker.Handler handler = Mockito.mock(Handler.class);
        new GoConfigGraphWalker(foo).walk(handler);
        Mockito.verify(handler).handle(ArgumentMatchers.same(foo), ArgumentMatchers.any(ValidationContext.class));
        Mockito.verify(handler).handle(ArgumentMatchers.same(bar), ArgumentMatchers.any(ValidationContext.class));
    }

    @Test
    public void shouldIgnoreConstantFieldsWhileCollectingErrorsToAvoidPotentialCycles() {
        CruiseConfig config = GoConfigMother.configWithPipelines("pipeline-1");
        List<ConfigErrors> allErrors = config.validateAfterPreprocess();
        Assert.assertThat(allErrors.size(), is(0));
    }

    @Test
    public void shouldErrorOutWhenDependsOnItself() throws Exception {
        CruiseConfig cruiseConfig = createCruiseConfig();
        PipelineConfig pipelineConfig = goConfigMother.addPipeline(cruiseConfig, "pipeline1", "stage", "build");
        goConfigMother.addStageToPipeline(cruiseConfig, "pipeline1", "ft", "build");
        goConfigMother.setDependencyOn(cruiseConfig, pipelineConfig, "pipeline1", "ft");
        cruiseConfig.validate(null);
        ConfigErrors errors = pipelineConfig.materialConfigs().errors();
        Assert.assertThat(errors.on("base"), is("Circular dependency: pipeline1 <- pipeline1"));
    }

    @Test
    public void shouldNotDuplicateErrorWhenPipelineDoesnotExist() throws Exception {
        CruiseConfig cruiseConfig = createCruiseConfig();
        PipelineConfig pipelineConfig = goConfigMother.addPipeline(cruiseConfig, "pipeline1", "stage", "build");
        PipelineConfig pipelineConfig2 = goConfigMother.addPipeline(cruiseConfig, "pipeline2", "stage", "build");
        goConfigMother.addStageToPipeline(cruiseConfig, "pipeline1", "ft", "build");
        goConfigMother.setDependencyOn(cruiseConfig, pipelineConfig2, "pipeline1", "ft");
        goConfigMother.setDependencyOn(cruiseConfig, pipelineConfig, "invalid", "invalid");
        cruiseConfig.validate(null);
        List<ConfigErrors> allErrors = cruiseConfig.getAllErrors();
        List<String> errors = new ArrayList<>();
        for (ConfigErrors allError : allErrors) {
            errors.addAll(allError.getAllOn("base"));
        }
        Assert.assertThat(errors.size(), is(1));
        Assert.assertThat(errors.get(0), is("Pipeline \"invalid\" does not exist. It is used from pipeline \"pipeline1\"."));
    }

    @Test
    public void shouldErrorOutWhenTwoPipelinesDependsOnEachOther() throws Exception {
        CruiseConfig cruiseConfig = createCruiseConfig();
        PipelineConfig pipeline1 = goConfigMother.addPipeline(cruiseConfig, "pipeline1", "stage", "build");
        PipelineConfig pipeline2 = goConfigMother.addPipeline(cruiseConfig, "pipeline2", "stage", "build");
        goConfigMother.setDependencyOn(cruiseConfig, pipeline2, "pipeline1", "stage");
        goConfigMother.setDependencyOn(cruiseConfig, pipeline1, "pipeline2", "stage");
        cruiseConfig.validate(null);
        Assert.assertThat(pipeline1.materialConfigs().errors().isEmpty(), is(false));
        Assert.assertThat(pipeline2.materialConfigs().errors().isEmpty(), is(false));
    }

    @Test
    public void shouldAddPipelineWithoutValidationInAnExistingGroup() {
        CruiseConfig cruiseConfig = createCruiseConfig();
        PipelineConfig pipeline1 = PipelineConfigMother.pipelineConfig("first");
        PipelineConfig pipeline2 = PipelineConfigMother.pipelineConfig("first");
        cruiseConfig.addPipelineWithoutValidation("first-group", pipeline1);
        Assert.assertThat(cruiseConfig.getGroups().size(), is(1));
        Assert.assertThat(cruiseConfig.findGroup("first-group").get(0), is(pipeline1));
        cruiseConfig.addPipelineWithoutValidation("first-group", pipeline2);
        Assert.assertThat(cruiseConfig.findGroup("first-group").get(0), is(pipeline1));
        Assert.assertThat(cruiseConfig.findGroup("first-group").get(1), is(pipeline2));
    }

    @Test
    public void shouldErrorOutWhenThreePipelinesFormACycle() throws Exception {
        CruiseConfig cruiseConfig = createCruiseConfig();
        PipelineConfig pipeline1 = goConfigMother.addPipeline(cruiseConfig, "pipeline1", "stage", "build");
        SvnMaterialConfig material = ((SvnMaterialConfig) (pipeline1.materialConfigs().get(0)));
        material.setConfigAttributes(Collections.singletonMap(FOLDER, "svn_dir"));
        P4MaterialConfig p4MaterialConfig = new P4MaterialConfig("localhost:1999", "view");
        p4MaterialConfig.setConfigAttributes(Collections.singletonMap(FOLDER, "p4_folder"));
        pipeline1.addMaterialConfig(p4MaterialConfig);
        PipelineConfig pipeline2 = goConfigMother.addPipeline(cruiseConfig, "pipeline3", "stage", "build");
        PipelineConfig pipeline3 = goConfigMother.addPipeline(cruiseConfig, "pipeline2", "stage", "build");
        goConfigMother.setDependencyOn(cruiseConfig, pipeline3, "pipeline3", "stage");
        goConfigMother.setDependencyOn(cruiseConfig, pipeline2, "pipeline1", "stage");
        goConfigMother.setDependencyOn(cruiseConfig, pipeline1, "pipeline2", "stage");
        cruiseConfig.validate(null);
        Assert.assertThat(pipeline1.materialConfigs().errors().isEmpty(), is(false));
        Assert.assertThat(pipeline2.materialConfigs().errors().isEmpty(), is(false));
        Assert.assertThat(pipeline3.materialConfigs().errors().isEmpty(), is(false));
    }

    @Test
    public void shouldAllowCleanupOfNonExistentStages() {
        CruiseConfig cruiseConfig = createCruiseConfig();
        Assert.assertThat(cruiseConfig.isArtifactCleanupProhibited("foo", "bar"), is(false));
        PipelineConfig pipelineConfig = PipelineConfigMother.createPipelineConfig("foo-pipeline", "bar-stage", "baz-job");
        cruiseConfig.addPipeline("defaultGrp", pipelineConfig);
        Assert.assertThat(cruiseConfig.isArtifactCleanupProhibited("foo-pipeline", "baz-stage"), is(false));
        Assert.assertThat(cruiseConfig.isArtifactCleanupProhibited("foo-pipeline", "bar-stage"), is(false));
        ReflectionUtil.setField(pipelineConfig.getFirstStageConfig(), "artifactCleanupProhibited", true);
        Assert.assertThat(cruiseConfig.isArtifactCleanupProhibited("foo-pipeline", "bar-stage"), is(true));
        Assert.assertThat(cruiseConfig.isArtifactCleanupProhibited("fOO-pipeLINE", "BaR-StagE"), is(true));
    }

    @Test
    public void shouldReturnDefaultGroupNameIfNoGroupNameIsSpecified() {
        CruiseConfig cfg = createCruiseConfig();
        Assert.assertThat(cfg.sanitizedGroupName(null), is(BasicPipelineConfigs.DEFAULT_GROUP));
        cfg.addPipeline("grp1", PipelineConfigMother.pipelineConfig("foo"));
        Assert.assertThat(cfg.sanitizedGroupName(null), is(BasicPipelineConfigs.DEFAULT_GROUP));
    }

    @Test
    public void shouldReturnSelectedGroupNameIfGroupNameIsSpecified() {
        CruiseConfig cfg = createCruiseConfig();
        cfg.addPipeline("grp1", PipelineConfigMother.pipelineConfig("foo"));
        Assert.assertThat(cfg.sanitizedGroupName("gr1"), is("gr1"));
    }

    @Test
    public void shouldAddPackageRepository() throws Exception {
        PackageRepository packageRepository = new PackageRepository();
        cruiseConfig.savePackageRepository(packageRepository);
        Assert.assertThat(cruiseConfig.getPackageRepositories().size(), is(1));
        Assert.assertThat(cruiseConfig.getPackageRepositories().get(0), is(packageRepository));
        Assert.assertThat(cruiseConfig.getPackageRepositories().get(0).getId(), is(notNullValue()));
    }

    @Test
    public void shouldUpdatePackageRepository() throws Exception {
        PackageRepository packageRepository = new PackageRepository();
        packageRepository.setName("old");
        cruiseConfig.savePackageRepository(packageRepository);
        packageRepository.setName("new");
        cruiseConfig.savePackageRepository(packageRepository);
        Assert.assertThat(cruiseConfig.getPackageRepositories().size(), is(1));
        Assert.assertThat(cruiseConfig.getPackageRepositories().get(0), is(packageRepository));
        Assert.assertThat(cruiseConfig.getPackageRepositories().get(0).getId(), is(notNullValue()));
        Assert.assertThat(cruiseConfig.getPackageRepositories().get(0).getName(), is("new"));
    }

    @Test
    public void shouldAddPackageDefinitionToGivenRepository() throws Exception {
        String repoId = "repo-id";
        PackageRepository packageRepository = PackageRepositoryMother.create(repoId, "repo-name", "plugin-id", "1.0", new Configuration());
        PackageDefinition existing = PackageDefinitionMother.create("pkg-1", "pkg1-name", new Configuration(), packageRepository);
        packageRepository.setPackages(new Packages(existing));
        cruiseConfig.setPackageRepositories(new PackageRepositories(packageRepository));
        Configuration configuration = new Configuration();
        configuration.add(new com.thoughtworks.go.domain.config.ConfigurationProperty(new ConfigurationKey("key"), new ConfigurationValue("value")));
        configuration.add(new com.thoughtworks.go.domain.config.ConfigurationProperty(new ConfigurationKey("key-with-no-value"), new ConfigurationValue("")));
        PackageDefinition packageDefinition = PackageDefinitionMother.create(null, "pkg2-name", configuration, packageRepository);
        cruiseConfig.savePackageDefinition(packageDefinition);
        Assert.assertThat(cruiseConfig.getPackageRepositories().size(), is(1));
        Assert.assertThat(cruiseConfig.getPackageRepositories().get(0).getId(), is(repoId));
        Assert.assertThat(cruiseConfig.getPackageRepositories().get(0).getPackages().size(), is(2));
        Assert.assertThat(cruiseConfig.getPackageRepositories().get(0).getPackages().get(0).getId(), is(existing.getId()));
        PackageDefinition createdPkgDef = cruiseConfig.getPackageRepositories().get(0).getPackages().get(1);
        Assert.assertThat(createdPkgDef.getId(), is(notNullValue()));
        Assert.assertThat(createdPkgDef.getConfiguration().getProperty("key"), is(Matchers.notNullValue()));
        Assert.assertThat(createdPkgDef.getConfiguration().getProperty("key-with-no-value"), is(nullValue()));
    }

    @Test
    public void shouldClearPackageRepositoryConfigurationsWhichAreEmptyWithNoErrors() throws Exception {
        PackageRepository packageRepository = Mockito.mock(com.thoughtworks.go.domain.packagerepository.PackageRepository.class);
        Mockito.when(packageRepository.isNew()).thenReturn(true);
        cruiseConfig.savePackageRepository(packageRepository);
        Mockito.verify(packageRepository).clearEmptyConfigurations();
    }

    @Test
    public void shouldRemovePackageRepositoryById() throws Exception {
        PackageRepository packageRepository = PackageRepositoryMother.create(null, "repo", "pid", "1.3", new Configuration());
        cruiseConfig.savePackageRepository(packageRepository);
        cruiseConfig.removePackageRepository(packageRepository.getId());
        Assert.assertThat(cruiseConfig.getPackageRepositories().find(packageRepository.getId()), is(Matchers.nullValue()));
    }

    @Test
    public void shouldDecideIfRepoCanBeDeleted_BasedOnPackageRepositoryBeingUsedByPipelines() throws Exception {
        PackageRepository repo1 = PackageRepositoryMother.create(null, "repo1", "plugin", "1.3", new Configuration());
        PackageRepository repo2 = PackageRepositoryMother.create(null, "repo2", "plugin", "1.3", new Configuration());
        PackageDefinition packageDefinition = PackageDefinitionMother.create("package", repo2);
        repo2.addPackage(packageDefinition);
        PipelineConfig pipeline = PipelineConfigMother.pipelineConfig("pipeline");
        pipeline.addMaterialConfig(new com.thoughtworks.go.config.materials.PackageMaterialConfig(new CaseInsensitiveString("p1"), packageDefinition.getId(), packageDefinition));
        cruiseConfig.addPipeline("existing_group", pipeline);
        cruiseConfig.savePackageRepository(repo1);
        cruiseConfig.savePackageRepository(repo2);
        Assert.assertThat(cruiseConfig.canDeletePackageRepository(repo1), is(true));
        Assert.assertThat(cruiseConfig.canDeletePackageRepository(repo2), is(false));
    }

    @Test
    public void shouldDecideIfPluggableSCMMaterialCanBeDeleted_BasedOnPluggableSCMMaterialBeingUsedByPipelines() throws Exception {
        SCM scmConfigOne = SCMMother.create("scm-id-1");
        SCM scmConfigTwo = SCMMother.create("scm-id-2");
        cruiseConfig.getSCMs().addAll(Arrays.asList(scmConfigOne, scmConfigTwo));
        PipelineConfig pipeline = PipelineConfigMother.pipelineConfig("pipeline");
        pipeline.addMaterialConfig(new PluggableSCMMaterialConfig(null, scmConfigOne, null, null));
        cruiseConfig.addPipeline("existing_group", pipeline);
        Assert.assertThat(cruiseConfig.canDeletePluggableSCMMaterial(scmConfigOne), is(false));
        Assert.assertThat(cruiseConfig.canDeletePluggableSCMMaterial(scmConfigTwo), is(true));
    }

    @Test
    public void shouldReturnConfigRepos() {
        Assert.assertNotNull(cruiseConfig.getConfigRepos());
    }

    @Test
    public void shouldReturnTrueWhenHasGroup() {
        Assert.assertThat(cruiseConfig.hasPipelineGroup("existing_group"), is(true));
    }

    @Test
    public void shouldReturnFalseWhenDoesNotHaveGroup() {
        Assert.assertThat(cruiseConfig.hasPipelineGroup("non_existing_group"), is(false));
    }

    @Test
    public void getAllLocalPipelines_shouldReturnPipelinesOnlyFromMainPart() {
        PipelineConfig pipe1 = PipelineConfigMother.pipelineConfig("pipe1");
        pipelines = new BasicPipelineConfigs("group_main", new Authorization(), pipe1);
        BasicCruiseConfig mainCruiseConfig = new BasicCruiseConfig(pipelines);
        cruiseConfig = new BasicCruiseConfig(mainCruiseConfig, PartialConfigMother.withPipeline("pipe2"));
        Assert.assertThat(cruiseConfig.getAllLocalPipelineConfigs(false).size(), is(1));
        Assert.assertThat(cruiseConfig.getAllLocalPipelineConfigs(false), hasItem(pipe1));
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
    public void shouldAddPipelineToMain() {
        pipelines = new BasicPipelineConfigs("group_main", new Authorization(), PipelineConfigMother.pipelineConfig("pipe1"));
        pipelines.setOrigin(new FileConfigOrigin());
        BasicCruiseConfig mainCruiseConfig = new BasicCruiseConfig(pipelines);
        cruiseConfig = new BasicCruiseConfig(mainCruiseConfig, PartialConfigMother.withPipeline("pipe2"));
        cruiseConfig.addPipeline("group_main", PipelineConfigMother.pipelineConfig("pipe3"));
        Assert.assertThat(mainCruiseConfig.hasPipelineNamed(new CaseInsensitiveString("pipe3")), is(true));
        Assert.assertThat(cruiseConfig.hasPipelineNamed(new CaseInsensitiveString("pipe3")), is(true));
    }

    @Test
    public void shouldGetAllPipelineNamesFromAllParts() {
        pipelines = new BasicPipelineConfigs("group_main", new Authorization(), PipelineConfigMother.pipelineConfig("pipe1"));
        BasicCruiseConfig mainCruiseConfig = new BasicCruiseConfig(pipelines);
        cruiseConfig = new BasicCruiseConfig(mainCruiseConfig, PartialConfigMother.withPipelineInGroup("pipe2", "g2"), PartialConfigMother.withPipelineInGroup("pipe3", "g3"));
        Assert.assertThat(cruiseConfig.getAllPipelineNames(), hasItem(new CaseInsensitiveString("pipe1")));
        Assert.assertThat(cruiseConfig.getAllPipelineNames(), hasItem(new CaseInsensitiveString("pipe2")));
        Assert.assertThat(cruiseConfig.getAllPipelineNames(), hasItem(new CaseInsensitiveString("pipe3")));
    }

    @Test
    public void shouldGetJobConfigByName() {
        goConfigMother.addPipeline(cruiseConfig, "cruise", "dev", "linux-firefox");
        JobConfig job = cruiseConfig.jobConfigByName("cruise", "dev", "linux-firefox", true);
        Assert.assertNotNull(job);
    }

    @Test
    public void shouldReturnAllUniqueSchedulableScmMaterials() {
        final MaterialConfig svnMaterialConfig = new SvnMaterialConfig("http://svn_url_1", "username", "password", false);
        svnMaterialConfig.setAutoUpdate(false);
        final MaterialConfig svnMaterialConfigWithAutoUpdate = new SvnMaterialConfig("http://svn_url_2", "username", "password", false);
        svnMaterialConfigWithAutoUpdate.setAutoUpdate(true);
        final MaterialConfig hgMaterialConfig = new HgMaterialConfig("http://hg_url", null);
        hgMaterialConfig.setAutoUpdate(false);
        final MaterialConfig gitMaterialConfig = new GitMaterialConfig("http://git_url");
        gitMaterialConfig.setAutoUpdate(false);
        final MaterialConfig tfsMaterialConfig = new com.thoughtworks.go.config.materials.tfs.TfsMaterialConfig(Mockito.mock(GoCipher.class), new UrlArgument("http://tfs_url"), "username", "domain", "password", "project_path");
        tfsMaterialConfig.setAutoUpdate(false);
        final MaterialConfig p4MaterialConfig = new P4MaterialConfig("http://p4_url", "view", "username");
        p4MaterialConfig.setAutoUpdate(false);
        final MaterialConfig dependencyMaterialConfig = MaterialConfigsMother.dependencyMaterialConfig();
        final PluggableSCMMaterialConfig pluggableSCMMaterialConfig = MaterialConfigsMother.pluggableSCMMaterialConfig("scm-id-1", null, null);
        pluggableSCMMaterialConfig.getSCMConfig().setAutoUpdate(false);
        final PipelineConfig p1 = PipelineConfigMother.pipelineConfig("pipeline1", new com.thoughtworks.go.config.materials.MaterialConfigs(svnMaterialConfig), new JobConfigs(new JobConfig(new CaseInsensitiveString("jobName"))));
        final PipelineConfig p2 = PipelineConfigMother.pipelineConfig("pipeline2", new com.thoughtworks.go.config.materials.MaterialConfigs(svnMaterialConfig, gitMaterialConfig), new JobConfigs(new JobConfig(new CaseInsensitiveString("jobName"))));
        final PipelineConfig p3 = PipelineConfigMother.pipelineConfig("pipeline3", new com.thoughtworks.go.config.materials.MaterialConfigs(hgMaterialConfig, dependencyMaterialConfig), new JobConfigs(new JobConfig(new CaseInsensitiveString("jobName"))));
        final PipelineConfig p4 = PipelineConfigMother.pipelineConfig("pipeline4", new com.thoughtworks.go.config.materials.MaterialConfigs(p4MaterialConfig, pluggableSCMMaterialConfig), new JobConfigs(new JobConfig(new CaseInsensitiveString("jobName"))));
        final PipelineConfig p5 = PipelineConfigMother.pipelineConfig("pipeline5", new com.thoughtworks.go.config.materials.MaterialConfigs(svnMaterialConfigWithAutoUpdate, tfsMaterialConfig), new JobConfigs(new JobConfig(new CaseInsensitiveString("jobName"))));
        cruiseConfig.getGroups().add(new BasicPipelineConfigs(p1, p2, p3, p4, p5));
        final Set<MaterialConfig> materials = cruiseConfig.getAllUniquePostCommitSchedulableMaterials();
        Assert.assertThat(materials.size(), is(6));
        Assert.assertThat(materials, hasItems(svnMaterialConfig, hgMaterialConfig, gitMaterialConfig, tfsMaterialConfig, p4MaterialConfig, pluggableSCMMaterialConfig));
        Assert.assertThat(materials, not(hasItem(svnMaterialConfigWithAutoUpdate)));
    }

    @Test
    public void getAllUniquePostCommitSchedulableMaterials_shouldReturnMaterialsWithAutoUpdateFalse() {
        GitMaterialConfig gitAutoMaterial = MaterialConfigsMother.gitMaterialConfig("url");
        PipelineConfig pipelineAuto = PipelineConfigMother.pipelineConfig("pipelineAuto", new com.thoughtworks.go.config.materials.MaterialConfigs(gitAutoMaterial));
        GitMaterialConfig gitNonAutoMaterial = new GitMaterialConfig(new UrlArgument("other-url"), "master", "dest", false, null, false, null, new CaseInsensitiveString("git"), false);
        PipelineConfig pipelineTriggerable = PipelineConfigMother.pipelineConfig("pipelineTriggerable", new com.thoughtworks.go.config.materials.MaterialConfigs(gitNonAutoMaterial));
        PipelineConfigs defaultGroup = PipelineConfigMother.createGroup("defaultGroup", pipelineAuto, pipelineTriggerable);
        cruiseConfig.getGroups().add(defaultGroup);
        Set<MaterialConfig> materials = cruiseConfig.getAllUniquePostCommitSchedulableMaterials();
        Assert.assertThat(materials.size(), is(1));
        Assert.assertThat(materials, hasItem(gitNonAutoMaterial));
    }

    @Test
    public void getAllUniquePostCommitSchedulableMaterials_shouldReturnMaterialsWithAutoUpdateFalseAndConfigRepos() {
        GitMaterialConfig gitAutoMaterial = MaterialConfigsMother.gitMaterialConfig("url");
        PipelineConfig pipelineAuto = PipelineConfigMother.pipelineConfig("pipelineAuto", new com.thoughtworks.go.config.materials.MaterialConfigs(gitAutoMaterial));
        GitMaterialConfig gitNonAutoMaterial = new GitMaterialConfig(new UrlArgument("other-url"), "master", "dest", false, null, false, null, new CaseInsensitiveString("git"), false);
        PipelineConfig pipelineTriggerable = PipelineConfigMother.pipelineConfig("pipelineTriggerable", new com.thoughtworks.go.config.materials.MaterialConfigs(gitNonAutoMaterial));
        PipelineConfigs defaultGroup = PipelineConfigMother.createGroup("defaultGroup", pipelineAuto, pipelineTriggerable);
        cruiseConfig = new BasicCruiseConfig(defaultGroup);
        ConfigReposConfig reposConfig = new ConfigReposConfig();
        GitMaterialConfig configRepoMaterial = new GitMaterialConfig("http://git");
        reposConfig.add(new ConfigRepoConfig(configRepoMaterial, "myplug"));
        cruiseConfig.setConfigRepos(reposConfig);
        PipelineGroups pipelineGroups = new PipelineGroups(defaultGroup);
        Set<MaterialConfig> materials = cruiseConfig.getAllUniquePostCommitSchedulableMaterials();
        Assert.assertThat(materials.size(), is(2));
        Assert.assertThat(materials, hasItem(gitNonAutoMaterial));
        Assert.assertThat(materials, hasItem(configRepoMaterial));
    }

    @Test
    public void shouldCheckCyclicDependency() throws Exception {
        PipelineConfig p1 = PipelineConfigMother.createPipelineConfig("p1", "s1", "j1");
        PipelineConfig p2 = PipelineConfigMother.createPipelineConfig("p2", "s2", "j1");
        p2.addMaterialConfig(new com.thoughtworks.go.config.materials.dependency.DependencyMaterialConfig(new CaseInsensitiveString("p1"), new CaseInsensitiveString("s1")));
        PipelineConfig p3 = PipelineConfigMother.createPipelineConfig("p3", "s3", "j1");
        p3.addMaterialConfig(new com.thoughtworks.go.config.materials.dependency.DependencyMaterialConfig(new CaseInsensitiveString("p2"), new CaseInsensitiveString("s2")));
        p1.addMaterialConfig(new com.thoughtworks.go.config.materials.dependency.DependencyMaterialConfig(new CaseInsensitiveString("p3"), new CaseInsensitiveString("s3")));
        pipelines.addAll(Arrays.asList(p1, p2, p3));
        BasicCruiseConfig mainCruiseConfig = new BasicCruiseConfig(pipelines);
        ConfigReposConfig reposConfig = new ConfigReposConfig();
        GitMaterialConfig configRepo = new GitMaterialConfig("http://git");
        reposConfig.add(new ConfigRepoConfig(configRepo, "myplug"));
        mainCruiseConfig.setConfigRepos(reposConfig);
        PartialConfig partialConfig = PartialConfigMother.withPipeline("pipe2");
        cruiseConfig = new BasicCruiseConfig(mainCruiseConfig, partialConfig);
        cruiseConfig.validate(Mockito.mock(ValidationContext.class));
        List<ConfigErrors> allErrors = cruiseConfig.getAllErrors();
        Assert.assertThat(allErrors.get(0).on("base"), is("Circular dependency: p1 <- p2 <- p3 <- p1"));
    }

    // UI-like scenarios
    @Test
    public void shouldCreateEmptyEnvironmentConfigForEditsWithUIOrigin_WhenFileHasNoEnvironment_AndForEdit() {
        BasicCruiseConfig mainCruiseConfig = new BasicCruiseConfig(pipelines);
        PartialConfig partialConfig = PartialConfigMother.withEnvironment("remoteEnv");
        partialConfig.setOrigins(new RepoConfigOrigin());
        cruiseConfig = new BasicCruiseConfig(mainCruiseConfig, true, partialConfig);
        Assert.assertThat(cruiseConfig.getEnvironments().size(), is(1));
        Assert.assertThat(((cruiseConfig.getEnvironments().get(0)) instanceof MergeEnvironmentConfig), is(true));
        Assert.assertThat(cruiseConfig.getEnvironments().get(0).name(), is(new CaseInsensitiveString("remoteEnv")));
        MergeEnvironmentConfig mergedEnv = ((MergeEnvironmentConfig) (cruiseConfig.getEnvironments().get(0)));
        Assert.assertThat(mergedEnv.size(), is(2));
    }

    @Test
    public void shouldCreateEmptyEnvironmentConfigForEditsWithUIOrigin_WhenFileHasNoEnvironmentAnd2RemoteParts_AndForEdit() {
        BasicCruiseConfig mainCruiseConfig = new BasicCruiseConfig(pipelines);
        PartialConfig partialConfig1 = PartialConfigMother.withEnvironment("remoteEnv");
        partialConfig1.setOrigins(new RepoConfigOrigin());
        PartialConfig partialConfig2 = PartialConfigMother.withEnvironment("remoteEnv");
        partialConfig2.setOrigins(new RepoConfigOrigin());
        cruiseConfig = new BasicCruiseConfig(mainCruiseConfig, true, partialConfig1, partialConfig2);
        Assert.assertThat(cruiseConfig.getEnvironments().size(), is(1));
        Assert.assertThat(((cruiseConfig.getEnvironments().get(0)) instanceof MergeEnvironmentConfig), is(true));
        Assert.assertThat(cruiseConfig.getEnvironments().get(0).name(), is(new CaseInsensitiveString("remoteEnv")));
        MergeEnvironmentConfig mergedEnv = ((MergeEnvironmentConfig) (cruiseConfig.getEnvironments().get(0)));
        Assert.assertThat(mergedEnv.size(), is(3));
    }

    @Test
    public void shouldNotCreateMergeEnvironmentConfig_WhenFileHasNoEnvironment_AndNotForEdit() {
        BasicCruiseConfig mainCruiseConfig = new BasicCruiseConfig(pipelines);
        PartialConfig partialConfig = PartialConfigMother.withEnvironment("remoteEnv");
        partialConfig.setOrigins(new RepoConfigOrigin());
        cruiseConfig = new BasicCruiseConfig(mainCruiseConfig, false, partialConfig);
        Assert.assertThat(cruiseConfig.getEnvironments().size(), is(1));
        Assert.assertThat(((cruiseConfig.getEnvironments().get(0)) instanceof MergeEnvironmentConfig), is(false));
        Assert.assertThat(cruiseConfig.getEnvironments().get(0).name(), is(new CaseInsensitiveString("remoteEnv")));
        Assert.assertThat(cruiseConfig.getEnvironments().get(0).isLocal(), is(false));
    }

    @Test
    public void shouldNotCreateEmptyEnvironmentConfigForEditsWithUIOrigin_WhenFileHasEnvironment_AndForEdit() {
        BasicCruiseConfig mainCruiseConfig = new BasicCruiseConfig(pipelines);
        mainCruiseConfig.addEnvironment(new BasicEnvironmentConfig(new CaseInsensitiveString("Env")));
        mainCruiseConfig.setOrigins(new FileConfigOrigin());
        PartialConfig partialConfig = PartialConfigMother.withEnvironment("Env");
        partialConfig.setOrigins(new RepoConfigOrigin());
        cruiseConfig = new BasicCruiseConfig(mainCruiseConfig, true, partialConfig);
        Assert.assertThat(cruiseConfig.getEnvironments().size(), is(1));
        Assert.assertThat(((cruiseConfig.getEnvironments().get(0)) instanceof MergeEnvironmentConfig), is(true));
        Assert.assertThat(cruiseConfig.getEnvironments().get(0).name(), is(new CaseInsensitiveString("Env")));
        MergeEnvironmentConfig mergedEnv = ((MergeEnvironmentConfig) (cruiseConfig.getEnvironments().get(0)));
        Assert.assertThat(mergedEnv.size(), is(2));
        Assert.assertThat(mergedEnv.get(0).isLocal(), is(true));
        Assert.assertThat(mergedEnv.get(1).isLocal(), is(false));
    }

    @Test
    public void shouldModifyEmptyEnvironmentConfigWithUIOrigin() {
        BasicCruiseConfig mainCruiseConfig = new BasicCruiseConfig(pipelines);
        PartialConfig partialConfig = PartialConfigMother.withEnvironment("remoteEnv");
        partialConfig.setOrigins(new RepoConfigOrigin());
        cruiseConfig = new BasicCruiseConfig(mainCruiseConfig, true, partialConfig);
        cruiseConfig.getEnvironments().get(0).addAgent("agent");
        MergeEnvironmentConfig mergedEnv = ((MergeEnvironmentConfig) (cruiseConfig.getEnvironments().get(0)));
        Assert.assertThat(mergedEnv.getFirstEditablePart().getAgents(), hasItem(new EnvironmentAgentConfig("agent")));
    }

    @Test
    public void shouldModifyEnvironmentConfigWithFileOrigin() {
        BasicCruiseConfig mainCruiseConfig = new BasicCruiseConfig(pipelines);
        BasicEnvironmentConfig envInFile = new BasicEnvironmentConfig(new CaseInsensitiveString("Env"));
        mainCruiseConfig.addEnvironment(envInFile);
        mainCruiseConfig.setOrigins(new FileConfigOrigin());
        PartialConfig partialConfig = PartialConfigMother.withEnvironment("Env");
        partialConfig.setOrigins(new RepoConfigOrigin());
        cruiseConfig = new BasicCruiseConfig(mainCruiseConfig, true, partialConfig);
        cruiseConfig.getEnvironments().get(0).addAgent("agent");
        Assert.assertThat(envInFile.getAgents(), hasItem(new EnvironmentAgentConfig("agent")));
    }

    @Test
    public void shouldAddAuthorizationToPipelinesConfigForEditsWithUIOrigin_WhenFileHasNoPipelineGroupYet_AndForEdit() {
        BasicCruiseConfig mainCruiseConfig = new BasicCruiseConfig();
        // only remotely defined group
        PartialConfig partialConfig = PartialConfigMother.withPipelineInGroup("pipe1", "group1");
        partialConfig.setOrigins(new RepoConfigOrigin());
        cruiseConfig = new BasicCruiseConfig(mainCruiseConfig, true, partialConfig);
        Assert.assertThat(cruiseConfig.getGroups().size(), is(1));
        Assert.assertThat(((cruiseConfig.getGroups().get(0)) instanceof MergePipelineConfigs), is(true));
        Assert.assertThat(cruiseConfig.getGroups().get(0).getGroup(), is("group1"));
        MergePipelineConfigs mergedEnv = ((MergePipelineConfigs) (cruiseConfig.getGroups().get(0)));
        Assert.assertThat(mergedEnv.getLocal().getOrigin(), is(new UIConfigOrigin()));
        Authorization authorization = new Authorization(new AdminsConfig(new AdminUser(new CaseInsensitiveString("firstTemplate-admin"))));
        cruiseConfig.getGroups().get(0).setAuthorization(authorization);
        Assert.assertThat(mergedEnv.getLocal().getAuthorization(), is(authorization));
    }

    private static class MyValidatable implements Validatable {
        public Validatable innerValidatable;

        public void validate(ValidationContext validationContext) {
        }

        public ConfigErrors errors() {
            return new ConfigErrors();
        }

        public void addError(String fieldName, String message) {
        }
    }

    private static class AlwaysEqualMyValidatable extends CruiseConfigTestBase.MyValidatable {
        @Override
        public final int hashCode() {
            return 42;
        }

        @Override
        public final boolean equals(Object obj) {
            return true;
        }
    }
}

