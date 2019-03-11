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


import FetchTask.ARTIFACT_ORIGIN;
import FetchTask.DEST;
import FetchTask.IS_SOURCE_A_FILE;
import FetchTask.JOB;
import FetchTask.PIPELINE_NAME;
import FetchTask.SRC;
import FetchTask.STAGE;
import com.thoughtworks.go.config.materials.MaterialConfigs;
import com.thoughtworks.go.config.remote.FileConfigOrigin;
import com.thoughtworks.go.config.remote.RepoConfigOrigin;
import com.thoughtworks.go.domain.CruiseConfig;
import com.thoughtworks.go.domain.PipelineConfig;
import com.thoughtworks.go.util.DataStructureUtils;
import com.thoughtworks.go.util.ReflectionUtil;
import java.io.File;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;


public class FetchTaskTest {
    private PipelineConfig downstream;

    private PipelineConfig upstream;

    private PipelineConfig uppestStream;

    private CruiseConfig config;

    @Test
    public void shouldImplementAbstractFetchArtifact() {
        Assert.assertTrue(((new FetchTask()) instanceof AbstractFetchTask));
    }

    @Test
    public void validate_shouldErrorWhenReferencingConfigRepositoryPipelineFromFilePipeline() {
        uppestStream.setOrigin(new RepoConfigOrigin());
        downstream.setOrigin(new FileConfigOrigin());
        FetchTask task = new FetchTask(new CaseInsensitiveString("uppest_stream/upstream"), new CaseInsensitiveString("uppest-stage2"), new CaseInsensitiveString("uppest-job2"), "src", "dest");
        StageConfig stage = downstream.getStage(new CaseInsensitiveString("stage"));
        task.validate(ConfigSaveValidationContext.forChain(config, new BasicPipelineConfigs(), downstream, stage, stage.getJobs().first()));
        Assert.assertThat(task.errors().isEmpty(), is(false));
        Assert.assertThat(task.errors().on(ARTIFACT_ORIGIN), startsWith("\"downstream :: stage :: job\" tries to fetch artifact from job \"uppest_stream :: uppest-stage2 :: uppest-job2\" which is defined in"));
    }

    @Test
    public void validate_shouldNotErrorWhenReferencingFilePipelineFromConfigRepositoryPipeline() {
        uppestStream.setOrigin(new FileConfigOrigin());
        downstream.setOrigin(new RepoConfigOrigin());
        FetchTask task = new FetchTask(new CaseInsensitiveString("uppest_stream/upstream"), new CaseInsensitiveString("uppest-stage2"), new CaseInsensitiveString("uppest-job2"), "src", "dest");
        task.validate(ConfigSaveValidationContext.forChain(config, new BasicPipelineConfigs(), downstream, downstream.getStage(new CaseInsensitiveString("stage"))));
        Assert.assertThat(task.errors().isEmpty(), is(true));
    }

    @Test
    public void validate_shouldNotErrorWhenReferencingConfigRepositoryPipelineFromConfigRepositoryPipeline() {
        uppestStream.setOrigin(new RepoConfigOrigin());
        downstream.setOrigin(new RepoConfigOrigin());
        FetchTask task = new FetchTask(new CaseInsensitiveString("uppest_stream/upstream"), new CaseInsensitiveString("uppest-stage2"), new CaseInsensitiveString("uppest-job2"), "src", "dest");
        task.validate(ConfigSaveValidationContext.forChain(config, new BasicPipelineConfigs(), downstream, downstream.getStage(new CaseInsensitiveString("stage"))));
        Assert.assertThat(task.errors().isEmpty(), is(true));
    }

    @Test
    public void validate_shouldNotErrorWhenReferencingFilePipelineFromFilePipeline() {
        uppestStream.setOrigin(new FileConfigOrigin());
        downstream.setOrigin(new FileConfigOrigin());
        FetchTask task = new FetchTask(new CaseInsensitiveString("uppest_stream/upstream"), new CaseInsensitiveString("uppest-stage2"), new CaseInsensitiveString("uppest-job2"), "src", "dest");
        task.validate(ConfigSaveValidationContext.forChain(config, new BasicPipelineConfigs(), downstream, downstream.getStage(new CaseInsensitiveString("stage"))));
        Assert.assertThat(task.errors().isEmpty(), is(true));
    }

    @Test
    public void shouldPassValidationWhenSrcAndDestDirectoryAreInsideAgentSandbox() {
        StageConfig stage = upstream.getFirstStageConfig();
        JobConfig job = stage.getJobs().get(0);
        FetchTask task = new FetchTask(upstream.name(), stage.name(), job.name(), "src", "dest");
        task.validate(ConfigSaveValidationContext.forChain(upstream, stage, job));
        Assert.assertThat(task.errors().isEmpty(), is(true));
    }

    @Test
    public void shouldFailValidationWhenSrcDirectoryIsOutsideAgentSandbox() {
        validateAndAssertIncorrectPath("/src", true, "dest", SRC);
        validateAndAssertIncorrectPath("../src", true, "dest", SRC);
        validateAndAssertIncorrectPath("..", true, "dest", SRC);
        validateAndAssertIncorrectPath("first-level/../../../going-back", true, "dest", SRC);
    }

    @Test
    public void shouldFailValidationWhenSrcFileIsOutsideAgentSandbox() {
        validateAndAssertIncorrectPath("/src/junk.txt", false, "dest", SRC);
        validateAndAssertIncorrectPath("../junk.txt", false, "dest", SRC);
        validateAndAssertIncorrectPath("first-level/../../../going-back/file.txt", false, "dest", SRC);
    }

    @Test
    public void shouldFailValidationWhenDestDirectoryIsOutsideAgentSandbox() {
        validateAndAssertIncorrectPath("src", false, "/dest", DEST);
        validateAndAssertIncorrectPath("src", false, "../dest", DEST);
        validateAndAssertIncorrectPath("src", false, "..", DEST);
        validateAndAssertIncorrectPath("src", false, "first-level/../../../going-back", DEST);
    }

    @Test
    public void validate_withinTemplates_shouldPopulateErrorOnSrcFileOrSrcDirOrDestIfIsNotAValidFilePathPattern() {
        String dest = "..";
        String src = "..";
        PipelineTemplateConfig template = PipelineTemplateConfigMother.createTemplate("template-1");
        StageConfig stage = template.get(0);
        JobConfig job = stage.getJobs().get(0);
        FetchTask task = new FetchTask(template.name(), stage.name(), job.name(), src, dest);
        ValidationContext context = ConfigSaveValidationContext.forChain(config, template, stage, job);
        task.validate(context);
        Assert.assertThat(task.errors().isEmpty(), is(false));
        String messageForSrc = String.format("Task of job '%s' in stage '%s' of template '%s' has src path '%s' which is outside the working directory.", job.name(), stage.name(), template.name(), src);
        Assert.assertThat(task.errors().on(SRC), is(messageForSrc));
        String messageForDest = String.format("Task of job '%s' in stage '%s' of template '%s' has dest path '%s' which is outside the working directory.", job.name(), stage.name(), template.name(), dest);
        Assert.assertThat(task.errors().on(DEST), is(messageForDest));
    }

    @Test
    public void validate_shouldPopulateErrorOnSrcFileOrSrcDirOrDestIfIsNotAValidFilePathPattern() {
        FetchTask task = new FetchTask(new CaseInsensitiveString(""), new CaseInsensitiveString(""), new CaseInsensitiveString(""), "", "");
        task.validateAttributes(ConfigSaveValidationContext.forChain(config, new BasicPipelineConfigs(), downstream, downstream.getStage(new CaseInsensitiveString("stage")), downstream.getStage(new CaseInsensitiveString("stage")).getJobs().get(0)));
        Assert.assertThat(task.errors().isEmpty(), is(false));
        Assert.assertThat(task.errors().on(SRC), is("Should provide either srcdir or srcfile"));
    }

    @Test
    public void validate_shouldNotTryAndValidateWhenWithinTemplate() throws Exception {
        FetchTask task = new FetchTask(new CaseInsensitiveString("dummy"), new CaseInsensitiveString("stage"), new CaseInsensitiveString("job"), "src", "dest");
        task.validate(ConfigSaveValidationContext.forChain(config, new TemplatesConfig(), downstream.getStage(new CaseInsensitiveString("stage"))));
        Assert.assertThat(task.errors().isEmpty(), is(true));
    }

    @Test
    public void validate_shouldValidateBlankStageAndJobWhenWithinTemplate() throws Exception {
        FetchTask task = new FetchTask(new CaseInsensitiveString("dummy"), new CaseInsensitiveString(""), new CaseInsensitiveString(""), "src", "dest");
        task.validate(ConfigSaveValidationContext.forChain(config, new TemplatesConfig(), downstream.getStage(new CaseInsensitiveString("stage"))));
        Assert.assertThat(task.errors().isEmpty(), is(false));
        Assert.assertThat(task.errors().on(STAGE), is("Stage is a required field."));
        Assert.assertThat(task.errors().on(JOB), is("Job is a required field."));
    }

    @Test
    public void shouldPopulateErrorsIfFetchArtifactFromPipelineThatIsNotDependency() {
        FetchTask task = new FetchTask(new CaseInsensitiveString("dummy"), new CaseInsensitiveString("stage"), new CaseInsensitiveString("job"), "src", "dest");
        task.validate(ConfigSaveValidationContext.forChain(config, new BasicPipelineConfigs(), downstream, downstream.getStage(new CaseInsensitiveString("stage"))));
        Assert.assertThat(task.errors().isEmpty(), is(false));
        Assert.assertThat(task.errors().on(PIPELINE_NAME), is(("Pipeline \"downstream\" tries to fetch artifact from pipeline " + "\"dummy\" which is not an upstream pipeline")));
    }

    @Test
    public void shouldPopulateErrorsIfFetchArtifactDoesNotHaveStageAndOrJobDefined() {
        FetchTask task = new FetchTask(new CaseInsensitiveString(""), new CaseInsensitiveString(""), new CaseInsensitiveString(""), "src", "dest");
        task.validate(ConfigSaveValidationContext.forChain(config, new BasicPipelineConfigs(), downstream, new StageConfig(), new JobConfig()));
        Assert.assertThat(task.errors().isEmpty(), is(false));
        Assert.assertThat(task.errors().on(STAGE), is("Stage is a required field."));
        Assert.assertThat(task.errors().on(JOB), is("Job is a required field."));
    }

    @Test
    public void shouldBeValidWhenFetchArtifactIsFromAnyAncestorStage_onTheUpstreamPipeline() {
        FetchTask task = new FetchTask(new CaseInsensitiveString("uppest_stream/upstream"), new CaseInsensitiveString("uppest-stage2"), new CaseInsensitiveString("uppest-job2"), "src", "dest");
        task.validate(ConfigSaveValidationContext.forChain(config, new BasicPipelineConfigs(), downstream, downstream.getStage(new CaseInsensitiveString("stage"))));
        Assert.assertThat(task.errors().isEmpty(), is(true));
    }

    @Test
    public void shouldBeValidWhenFetchArtifactIsFromAnyAncestorStage_s_predecessorStage__onTheUpstreamPipeline() {
        FetchTask task = new FetchTask(new CaseInsensitiveString("uppest_stream/upstream"), new CaseInsensitiveString("uppest-stage1"), new CaseInsensitiveString("uppest-job1"), "src", "dest");
        task.validate(ConfigSaveValidationContext.forChain(config, new BasicPipelineConfigs(), downstream, downstream.getStage(new CaseInsensitiveString("stage"))));
        Assert.assertThat(task.errors().isEmpty(), is(true));
    }

    @Test
    public void should_NOT_BeValidWhenFetchArtifactIsFromAnyAncestorStage_s_successorStage_onTheUpstreamPipeline() {
        FetchTask task = new FetchTask(new CaseInsensitiveString("uppest_stream/upstream"), new CaseInsensitiveString("uppest-stage3"), new CaseInsensitiveString("uppest-job3"), "src", "dest");
        StageConfig stage = downstream.getStage(new CaseInsensitiveString("stage"));
        task.validate(ConfigSaveValidationContext.forChain(config, new BasicPipelineConfigs(), downstream, stage, stage.getJobs().first()));
        Assert.assertThat(task.errors().isEmpty(), is(false));
        Assert.assertThat(task.errors().on(STAGE), is("\"downstream :: stage :: job\" tries to fetch artifact from stage \"uppest_stream :: uppest-stage3\" which does not complete before \"downstream\" pipeline\'s dependencies."));
    }

    @Test
    public void should_NOT_BeValidWhen_pathFromAncestor_isInvalid_becauseRefferedPipelineIsNotAnAncestor() {
        FetchTask task = new FetchTask(new CaseInsensitiveString("random_pipeline/upstream"), new CaseInsensitiveString("random-stage1"), new CaseInsensitiveString("random-job1"), "src", "dest");
        task.validate(ConfigSaveValidationContext.forChain(config, new BasicPipelineConfigs(), downstream, downstream.getStage(new CaseInsensitiveString("stage"))));
        Assert.assertThat(task.errors().isEmpty(), is(false));
        Assert.assertThat(task.errors().on(PIPELINE_NAME), is("Pipeline named 'random_pipeline' exists, but is not an ancestor of 'downstream' as declared in 'random_pipeline/upstream'."));
    }

    @Test
    public void should_NOT_BeValidWhen_NO_pathFromAncestorIsGiven_butAncestorPipelineIsBeingFetchedFrom() {
        FetchTask task = new FetchTask(null, new CaseInsensitiveString("uppest-stage3"), new CaseInsensitiveString("uppest-job3"), "src", "dest");
        StageConfig stage = downstream.getStage(new CaseInsensitiveString("stage"));
        task.validate(ConfigSaveValidationContext.forChain(config, new BasicPipelineConfigs(), downstream, stage, stage.getJobs().get(0)));
        Assert.assertThat(task.errors().isEmpty(), is(false));
        Assert.assertThat(task.errors().on(STAGE), is("\"downstream :: stage :: job\" tries to fetch artifact from stage \"downstream :: uppest-stage3\" which does not exist."));
    }

    @Test
    public void should_BeValidWhen_hasAnAlternatePathToAncestor() {
        PipelineConfig upstreamsPeer = config.pipelineConfigByName(new CaseInsensitiveString("upstreams_peer"));
        upstreamsPeer.setMaterialConfigs(new MaterialConfigs(MaterialConfigsMother.dependencyMaterialConfig("uppest_stream", "uppest-stage1")));
        upstreamsPeer.add(StageConfigMother.stageConfig("peer-stage", new JobConfigs(new JobConfig("peer-job"))));
        downstream = config.pipelineConfigByName(new CaseInsensitiveString("downstream"));
        downstream.setMaterialConfigs(new MaterialConfigs(MaterialConfigsMother.dependencyMaterialConfig("upstream", "up-stage1"), MaterialConfigsMother.dependencyMaterialConfig("upstreams_peer", "peer-stage")));
        FetchTask task = new FetchTask(new CaseInsensitiveString("uppest_stream/upstream"), new CaseInsensitiveString("uppest-stage1"), new CaseInsensitiveString("uppest-job1"), "src", "dest");
        task.validate(ConfigSaveValidationContext.forChain(config, new BasicPipelineConfigs(), downstream, downstream.getStage(new CaseInsensitiveString("stage"))));
        Assert.assertThat(task.errors().isEmpty(), is(true));
        task = new FetchTask(new CaseInsensitiveString("uppest_stream/upstreams_peer"), new CaseInsensitiveString("uppest-stage1"), new CaseInsensitiveString("uppest-job1"), "src", "dest");
        task.validate(ConfigSaveValidationContext.forChain(config, new BasicPipelineConfigs(), downstream, downstream.getStage(new CaseInsensitiveString("stage"))));
        Assert.assertThat(task.errors().isEmpty(), is(true));
    }

    @Test
    public void should_NOT_BeValidWhen_ImmediateParentDeclaredInPathFromAncestor_isNotAParentPipeline() {
        PipelineConfig upstreamsPeer = config.pipelineConfigByName(new CaseInsensitiveString("upstreams_peer"));
        upstreamsPeer.setMaterialConfigs(new MaterialConfigs(MaterialConfigsMother.dependencyMaterialConfig("uppest_stream", "uppest-stage1")));
        upstreamsPeer.add(StageConfigMother.stageConfig("peer-stage", new JobConfigs(new JobConfig("peer-job"))));
        downstream = config.pipelineConfigByName(new CaseInsensitiveString("downstream"));
        downstream.setMaterialConfigs(new MaterialConfigs(MaterialConfigsMother.dependencyMaterialConfig("upstream", "up-stage1"), MaterialConfigsMother.dependencyMaterialConfig("upstreams_peer", "peer-stage")));
        FetchTask task = new FetchTask(new CaseInsensitiveString("upstream/uppest_stream"), new CaseInsensitiveString("up-stage1"), new CaseInsensitiveString("up-job1"), "src", "dest");
        StageConfig stage = downstream.getStage(new CaseInsensitiveString("stage"));
        task.validate(ConfigSaveValidationContext.forChain(config, new BasicPipelineConfigs(), downstream, stage, stage.getJobs().get(0)));
        Assert.assertThat(task.errors().isEmpty(), is(false));
        Assert.assertThat(task.errors().on(PIPELINE_NAME), is("Pipeline named 'uppest_stream' exists, but is not an ancestor of 'downstream' as declared in 'upstream/uppest_stream'."));
    }

    @Test
    public void should_NOT_BeValidWhen_ImmediateParentDeclaredInPathFromAncestor_isNotAParentPipeline_PipelineConfigValidationContext() {
        PipelineConfig upstreamsPeer = config.pipelineConfigByName(new CaseInsensitiveString("upstreams_peer"));
        upstreamsPeer.setMaterialConfigs(new MaterialConfigs(MaterialConfigsMother.dependencyMaterialConfig("uppest_stream", "uppest-stage1")));
        upstreamsPeer.add(StageConfigMother.stageConfig("peer-stage", new JobConfigs(new JobConfig("peer-job"))));
        downstream = config.pipelineConfigByName(new CaseInsensitiveString("downstream"));
        downstream.setMaterialConfigs(new MaterialConfigs(MaterialConfigsMother.dependencyMaterialConfig("upstream", "up-stage1"), MaterialConfigsMother.dependencyMaterialConfig("upstreams_peer", "peer-stage")));
        FetchTask task = new FetchTask(new CaseInsensitiveString("upstream/uppest_stream"), new CaseInsensitiveString("up-stage1"), new CaseInsensitiveString("up-job1"), "src", "dest");
        StageConfig stage = downstream.getStage(new CaseInsensitiveString("stage"));
        task.validateTree(PipelineConfigSaveValidationContext.forChain(true, "group", config, downstream, stage, stage.getJobs().get(0)));
        Assert.assertThat(task.errors().isEmpty(), is(false));
        Assert.assertThat(task.errors().on(PIPELINE_NAME), is("Pipeline named 'uppest_stream' exists, but is not an ancestor of 'downstream' as declared in 'upstream/uppest_stream'."));
    }

    @Test
    public void should_NOT_BeValidWhen_stageMayNotHaveRunViaTheGivenPath_evenThoughItMayHaveActuallyRunAccordingToAnAlternatePath() {
        // TODO: Please fix this if someone cares about this corner case working -jj
        PipelineConfig upstreamsPeer = config.pipelineConfigByName(new CaseInsensitiveString("upstreams_peer"));
        upstreamsPeer.setMaterialConfigs(new MaterialConfigs(MaterialConfigsMother.dependencyMaterialConfig("uppest_stream", "uppest-stage1")));
        upstreamsPeer.add(StageConfigMother.stageConfig("peer-stage", new JobConfigs(new JobConfig("peer-job"))));
        downstream = config.pipelineConfigByName(new CaseInsensitiveString("downstream"));
        downstream.setMaterialConfigs(new MaterialConfigs(MaterialConfigsMother.dependencyMaterialConfig("upstream", "up-stage1"), MaterialConfigsMother.dependencyMaterialConfig("upstreams_peer", "peer-stage")));
        FetchTask task = new FetchTask(new CaseInsensitiveString("uppest_stream/upstreams_peer"), new CaseInsensitiveString("uppest-stage1"), new CaseInsensitiveString("uppest-job1"), "src", "dest");
        task.validate(ConfigSaveValidationContext.forChain(config, new BasicPipelineConfigs(), downstream, downstream.getStage(new CaseInsensitiveString("stage")), downstream.getStage(new CaseInsensitiveString("stage")).getJobs().first()));
        Assert.assertThat(task.errors().isEmpty(), is(true));
        task = new FetchTask(new CaseInsensitiveString("uppest_stream/upstreams_peer"), new CaseInsensitiveString("uppest-stage2"), new CaseInsensitiveString("uppest-job2"), "src", "dest");
        task.validate(ConfigSaveValidationContext.forChain(config, new BasicPipelineConfigs(), downstream, downstream.getStage(new CaseInsensitiveString("stage")), downstream.getStage(new CaseInsensitiveString("stage")).getJobs().first()));
        Assert.assertThat(task.errors().isEmpty(), is(false));
        Assert.assertThat(task.errors().on(STAGE), is("\"downstream :: stage :: job\" tries to fetch artifact from stage \"uppest_stream :: uppest-stage2\" which does not complete before \"downstream\" pipeline\'s dependencies."));
    }

    @Test
    public void shouldFailWhenFetchArtifactIsFromAnyStage_AFTER_theDependencyStageOnTheUpstreamPipeline() {
        FetchTask task = new FetchTask(new CaseInsensitiveString("upstream"), new CaseInsensitiveString("up-stage2"), new CaseInsensitiveString("up-job2"), "src", "dest");
        task.validate(ConfigSaveValidationContext.forChain(config, new BasicPipelineConfigs(), downstream, downstream.getStage(new CaseInsensitiveString("stage")), downstream.getStage(new CaseInsensitiveString("stage")).getJobs().first()));
        Assert.assertThat(task.errors().isEmpty(), is(false));
        Assert.assertThat(task.errors().on(STAGE), is("\"downstream :: stage :: job\" tries to fetch artifact from stage \"upstream :: up-stage2\" which does not complete before \"downstream\" pipeline\'s dependencies."));
    }

    @Test
    public void shouldPopulateErrorIfFetchArtifactFromDependentPipelineButStageDoesNotExist() {
        FetchTask task = new FetchTask(new CaseInsensitiveString("upstream"), new CaseInsensitiveString("stage-does-not-exist"), new CaseInsensitiveString("job"), "src", "dest");
        task.validate(ConfigSaveValidationContext.forChain(config, new BasicPipelineConfigs(), downstream, downstream.getStage(new CaseInsensitiveString("stage")), downstream.getStage(new CaseInsensitiveString("stage")), downstream.getStage(new CaseInsensitiveString("stage")).getJobs().get(0)));
        Assert.assertThat(task.errors().isEmpty(), is(false));
        Assert.assertThat(task.errors().on(STAGE), is(("\"downstream :: stage :: job\" tries to fetch artifact from stage " + "\"upstream :: stage-does-not-exist\" which does not exist.")));
    }

    @Test
    public void shouldPopulateErrorIfFetchArtifactFromDependentPipelineButJobNotExist() {
        FetchTask task = new FetchTask(new CaseInsensitiveString("upstream"), new CaseInsensitiveString("stage"), new CaseInsensitiveString("job-does-not-exist"), "src", "dest");
        StageConfig stage = downstream.getStage(new CaseInsensitiveString("stage"));
        task.validate(ConfigSaveValidationContext.forChain(config, new BasicPipelineConfigs(), downstream, stage, stage.getJobs().first()));
        Assert.assertThat(task.errors().isEmpty(), is(false));
        Assert.assertThat(task.errors().on(JOB), is(("\"downstream :: stage :: job\" tries to fetch artifact from job " + "\"upstream :: stage :: job-does-not-exist\" which does not exist.")));
    }

    @Test
    public void shouldBeValidIfFetchArtifactUsingADependantPipeline() {
        FetchTask task = new FetchTask(new CaseInsensitiveString("upstream"), new CaseInsensitiveString("up-stage1"), new CaseInsensitiveString("up-job1"), "src", "dest");
        task.validate(ConfigSaveValidationContext.forChain(config, downstream, downstream.getStage(new CaseInsensitiveString("stage"))));
        Assert.assertThat(task.errors().isEmpty(), is(true));
    }

    @Test
    public void shouldBeValidIfFetchArtifactUsingAStageBeforeCurrentInTheSamePipeline() {
        FetchTask task = new FetchTask(new CaseInsensitiveString("upstream"), new CaseInsensitiveString("stage"), new CaseInsensitiveString("job"), "src", "dest");
        task.validate(ConfigSaveValidationContext.forChain(config, upstream, upstream.getStage(new CaseInsensitiveString("up-stage1"))));
        Assert.assertThat(task.errors().isEmpty(), is(true));
    }

    @Test
    public void shouldBeValidIfFetchArtifactDoesNotSpecifyPipeline() {
        FetchTask task = new FetchTask(new CaseInsensitiveString("stage"), new CaseInsensitiveString("job"), "src", "dest");
        task.validate(ConfigSaveValidationContext.forChain(config, upstream, upstream.getStage(new CaseInsensitiveString("up-stage1"))));
        Assert.assertThat(task.errors().isEmpty(), is(true));
    }

    // The Fetch Task is now case insensitive to job names . This behavior has changed from before migration of validations
    // to inside Config Objects for clicky admin. It was earlier Case SENSITIVE. This was done to address #4970
    // -Jake
    @Test
    public void shouldPopulateErrorsIfFetchArtifactUsingJobNameWithDifferentCase() {
        FetchTask task = new FetchTask(new CaseInsensitiveString("upstream"), new CaseInsensitiveString("stage"), new CaseInsensitiveString("JOB"), "src", "dest");
        task.validate(ConfigSaveValidationContext.forChain(config, new BasicPipelineConfigs(), downstream, downstream.getStage(new CaseInsensitiveString("stage"))));
        Assert.assertThat(task.errors().isEmpty(), is(true));
    }

    @Test
    public void shouldPopulateErrorIfSrcFileAndSrcDirBothAreDefined() {
        FetchTask task = new FetchTask(new CaseInsensitiveString("upstream"), new CaseInsensitiveString("stage"), new CaseInsensitiveString("job"), "src_file", "dest");
        task.setSrcdir("src_dir");
        task.validate(ConfigSaveValidationContext.forChain(config, upstream, upstream.getStage(new CaseInsensitiveString("stage"))));
        Assert.assertThat(task.errors().isEmpty(), is(false));
        Assert.assertThat(task.errors().on(SRC), is("Only one of srcfile or srcdir is allowed at a time"));
    }

    @Test
    public void shouldPopulateErrorIfBothSrcFileAndSrcDirAreNotDefined() {
        FetchTask task = new FetchTask(new CaseInsensitiveString("upstream"), new CaseInsensitiveString("stage"), new CaseInsensitiveString("job"), "src_file", "dest");
        task.setSrcfile(null);
        task.validate(ConfigSaveValidationContext.forChain(config, upstream, upstream.getStage(new CaseInsensitiveString("stage"))));
        Assert.assertThat(task.errors().isEmpty(), is(false));
        Assert.assertThat(task.errors().on(SRC), is("Should provide either srcdir or srcfile"));
    }

    @Test
    public void shouldPopulateErrorOnSrcFileOrSrcDirOrDestIfIsNotAValidFilePathPattern() {
        FetchTask task = new FetchTask(new CaseInsensitiveString("upstream"), new CaseInsensitiveString("stage"), new CaseInsensitiveString("job"), "..", "..");
        StageConfig stage = upstream.getStage(new CaseInsensitiveString("stage"));
        ValidationContext context = ConfigSaveValidationContext.forChain(config, new BasicPipelineConfigs(), upstream, stage, stage.getJobs().first());
        task.validate(context);
        Assert.assertThat(task.errors().isEmpty(), is(false));
        Assert.assertThat(task.errors().on(SRC), is("Task of job 'job' in stage 'stage' of pipeline 'upstream' has src path '..' which is outside the working directory."));
        Assert.assertThat(task.errors().on(DEST), is("Task of job 'job' in stage 'stage' of pipeline 'upstream' has dest path '..' which is outside the working directory."));
        task.setSrcfile(null);
        task.setSrcdir("..");
        task.validate(context);
        Assert.assertThat(task.errors().on(SRC), is("Task of job 'job' in stage 'stage' of pipeline 'upstream' has src path '..' which is outside the working directory."));
    }

    @Test
    public void shouldIndicateSourceIsAFileBasedOnValuePopulated() {
        FetchTask fetchTask = new FetchTask();
        fetchTask.setSrcfile("a.txt");
        Assert.assertThat(fetchTask.isSourceAFile(), is(true));
        FetchTask fetchTaskWithDir = new FetchTask();
        fetchTaskWithDir.setSrcdir("/a/b");
        Assert.assertThat(fetchTaskWithDir.isSourceAFile(), is(false));
    }

    @Test
    public void shouldReturnSrcFileWhenSrcFileIsNotEmpty() throws Exception {
        FetchTask fetchTask = new FetchTask();
        fetchTask.setSrcfile("a.jar");
        Assert.assertThat(fetchTask.getSrc(), is("a.jar"));
    }

    @Test
    public void shouldReturnSrcDirWhenSrcDirIsNotEmpty() throws Exception {
        FetchTask fetchTask = new FetchTask();
        fetchTask.setSrcdir("folder");
        Assert.assertThat(fetchTask.getSrc(), is("folder"));
    }

    @Test
    public void shouldNormalizeDest() throws Exception {
        FetchTask fetchTask = new FetchTask(new CaseInsensitiveString("mingle"), new CaseInsensitiveString("dev"), new CaseInsensitiveString("windows-3"), "cruise-output/console.log", "dest\\subfolder");
        Assert.assertThat(fetchTask.getDest(), is("dest/subfolder"));
    }

    @Test
    public void shouldNormalizeSrcFile() throws Exception {
        FetchTask fetchTask = new FetchTask(new CaseInsensitiveString("mingle"), new CaseInsensitiveString("dev"), new CaseInsensitiveString("windows-3"), "cruise-output\\console.log", "dest\\subfolder");
        Assert.assertThat(fetchTask.getSrc(), is("cruise-output/console.log"));
    }

    @Test
    public void shouldNormalizeSrcDir() throws Exception {
        FetchTask fetchTask = new FetchTask(new CaseInsensitiveString("mingle"), new CaseInsensitiveString("dev"), new CaseInsensitiveString("windows-3"), "", "dest\\subfolder");
        fetchTask.setSrcdir("testfolder\\subfolder");
        Assert.assertThat(fetchTask.getSrc(), is("testfolder/subfolder"));
    }

    @Test
    public void describeTestForSrcFile() throws Exception {
        FetchTask fetchTask = new FetchTask(new CaseInsensitiveString("mingle"), new CaseInsensitiveString("dev"), new CaseInsensitiveString("windows-3"), "cruise.zip", "dest\\subfolder");
        Assert.assertThat(fetchTask.describe(), is("fetch artifact [cruise.zip] => [dest/subfolder] from [mingle/dev/windows-3]"));
    }

    @Test
    public void describeTestForSrcDir() throws Exception {
        FetchTask fetchTask = new FetchTask(new CaseInsensitiveString("mingle"), new CaseInsensitiveString("dev"), new CaseInsensitiveString("windows-3"), "", "dest\\subfolder");
        fetchTask.setSrcdir("cruise-output");
        Assert.assertThat(fetchTask.describe(), is("fetch artifact [cruise-output] => [dest/subfolder] from [mingle/dev/windows-3]"));
    }

    @Test
    public void describeTestForSrcDirAndSrcFile() throws Exception {
        FetchTask fetchTask = new FetchTask(new CaseInsensitiveString("mingle"), new CaseInsensitiveString("dev"), new CaseInsensitiveString("windows-3"), "cruise.zip", "dest\\subfolder");
        fetchTask.setSrcdir("cruise-output");
        Assert.assertThat(fetchTask.describe(), is("fetch artifact [cruise.zip] => [dest/subfolder] from [mingle/dev/windows-3]"));
    }

    @Test
    public void shouldUpdateItsAttributesFromAttributeMap() throws Exception {
        FetchTask fetchTask = new FetchTask();
        fetchTask.setConfigAttributes(DataStructureUtils.m(PIPELINE_NAME, "pipeline_foo", STAGE, "stage_bar", JOB, "job_baz", SRC, "src_file", DEST, "dest_dir", IS_SOURCE_A_FILE, "1"));
        Assert.assertThat(fetchTask.getTargetPipelineName(), is(new CaseInsensitiveString("pipeline_foo")));
        Assert.assertThat(fetchTask.getStage(), is(new CaseInsensitiveString("stage_bar")));
        Assert.assertThat(fetchTask.getJob().toString(), is("job_baz"));
        Assert.assertThat(fetchTask.getSrcfile(), is("src_file"));
        Assert.assertThat(fetchTask.getSrcdir(), is(nullValue()));
        Assert.assertThat(fetchTask.getDest(), is("dest_dir"));
        fetchTask.setConfigAttributes(DataStructureUtils.m(PIPELINE_NAME, "", STAGE, "", JOB, "", SRC, "", IS_SOURCE_A_FILE, "1", DEST, ""));
        Assert.assertThat(fetchTask.getTargetPipelineName(), is(new CaseInsensitiveString("")));
        Assert.assertThat(fetchTask.getStage(), is(new CaseInsensitiveString("")));
        Assert.assertThat(fetchTask.getJob().toString(), is(""));
        Assert.assertThat(fetchTask.getSrcfile(), is(nullValue()));
        Assert.assertThat(fetchTask.getSrcdir(), is(nullValue()));
        Assert.assertThat(fetchTask.getDest(), is(""));
    }

    @Test
    public void shouldSetSrcFileToNullIfSrcDirIsDefined() throws Exception {
        FetchTask fetchTask = new FetchTask();
        fetchTask.setConfigAttributes(DataStructureUtils.m(PIPELINE_NAME, "pipeline_foo", STAGE, "stage_bar", JOB, "job_baz", IS_SOURCE_A_FILE, "0", SRC, "src_dir", DEST, "dest_dir"));
        Assert.assertThat(fetchTask.getSrcfile(), is(nullValue()));
        Assert.assertThat(fetchTask.getSrcdir(), is("src_dir"));
    }

    @Test
    public void shouldSetSrcFileToNullWhenSrcDirIsUpdated() {
        FetchTask fetchTask = new FetchTask(new CaseInsensitiveString("pname"), new CaseInsensitiveString("sname"), new CaseInsensitiveString("jname"), "sfile", "dest");
        fetchTask.setConfigAttributes(DataStructureUtils.m(PIPELINE_NAME, "pipeline_foo", STAGE, "stage_bar", JOB, "job_baz", IS_SOURCE_A_FILE, "0", SRC, "src_dir", DEST, "dest_dir"));
        Assert.assertThat(fetchTask.getSrcfile(), is(nullValue()));
        Assert.assertThat(fetchTask.getSrcdir(), is("src_dir"));
    }

    @Test
    public void shouldNotUpdateItsAttributesFromAttributeMapWhenKeysNotPresent() throws Exception {
        FetchTask fetchTask = new FetchTask();
        fetchTask.setConfigAttributes(DataStructureUtils.m(PIPELINE_NAME, "pipeline_foo", STAGE, "stage_bar", JOB, "job_baz", SRC, "src_file", IS_SOURCE_A_FILE, "1", SRC, "src_file", DEST, "dest_dir"));
        fetchTask.setConfigAttributes(DataStructureUtils.m());
        Assert.assertThat(fetchTask.getTargetPipelineName(), is(new CaseInsensitiveString("pipeline_foo")));
        Assert.assertThat(fetchTask.getStage(), is(new CaseInsensitiveString("stage_bar")));
        Assert.assertThat(fetchTask.getJob().toString(), is("job_baz"));
        Assert.assertThat(fetchTask.getSrcfile(), is("src_file"));
        Assert.assertThat(fetchTask.getSrcdir(), is(nullValue()));
        Assert.assertThat(fetchTask.getDest(), is("dest_dir"));
    }

    @Test
    public void shouldUpdateDestToNullIfDestIsEmptyInAttributeMap_SoThatItDoesNotGetSerializedInXml() {
        FetchTask fetchTask = new FetchTask(new CaseInsensitiveString("mingle"), new CaseInsensitiveString("dev"), new CaseInsensitiveString("one"), "", "dest");
        fetchTask.setConfigAttributes(DataStructureUtils.m(DEST, ""));
        Assert.assertThat(fetchTask, is(new FetchTask(new CaseInsensitiveString("mingle"), new CaseInsensitiveString("dev"), new CaseInsensitiveString("one"), "", null)));
    }

    @Test
    public void shouldPopulateAllFieldsInReturnedPropertiesForDisplay() {
        FetchTask fetchTask = new FetchTask(new CaseInsensitiveString("foo-pipeline"), new CaseInsensitiveString("bar-stage"), new CaseInsensitiveString("baz-job"), "quux.c", "bang-file");
        Assert.assertThat(fetchTask.getPropertiesForDisplay(), JUnitMatchers.hasItems(new TaskProperty("Pipeline Name", "foo-pipeline", "pipeline_name"), new TaskProperty("Stage Name", "bar-stage", "stage_name"), new TaskProperty("Job Name", "baz-job", "job_name"), new TaskProperty("Source File", "quux.c", "source_file"), new TaskProperty("Destination", "bang-file", "destination")));
        Assert.assertThat(fetchTask.getPropertiesForDisplay().size(), is(5));
        fetchTask = new FetchTask(new CaseInsensitiveString("foo-pipeline"), new CaseInsensitiveString("bar-stage"), new CaseInsensitiveString("baz-job"), null, "bang-file");
        fetchTask.setSrcdir("foo/src");
        Assert.assertThat(fetchTask.getPropertiesForDisplay(), JUnitMatchers.hasItems(new TaskProperty("Pipeline Name", "foo-pipeline", "pipeline_name"), new TaskProperty("Stage Name", "bar-stage", "stage_name"), new TaskProperty("Job Name", "baz-job", "job_name"), new TaskProperty("Source Directory", "foo/src", "source_directory"), new TaskProperty("Destination", "bang-file", "destination")));
        Assert.assertThat(fetchTask.getPropertiesForDisplay().size(), is(5));
        fetchTask = new FetchTask(new CaseInsensitiveString(null), new CaseInsensitiveString("bar-stage"), new CaseInsensitiveString("baz-job"), null, null);
        Assert.assertThat(fetchTask.getPropertiesForDisplay(), JUnitMatchers.hasItems(new TaskProperty("Stage Name", "bar-stage", "stage_name"), new TaskProperty("Job Name", "baz-job", "job_name")));
        Assert.assertThat(fetchTask.getPropertiesForDisplay().size(), is(2));
    }

    @Test
    public void shouldCreateChecksumPath() {
        FetchTask fetchTask = new FetchTask(new CaseInsensitiveString("up-pipeline"), new CaseInsensitiveString("bar-stage"), new CaseInsensitiveString("baz-job"), "quux.c", "bang-file");
        String checksumPath = fetchTask.checksumPath();
        Assert.assertThat(checksumPath, is("up-pipeline_bar-stage_baz-job_md5.checksum"));
    }

    @Test
    public void shouldCreateArtifactDest() {
        FetchTask fetchTask = new FetchTask(new CaseInsensitiveString("up-pipeline"), new CaseInsensitiveString("bar-stage"), new CaseInsensitiveString("baz-job"), "quux.c", "dest-dir");
        File artifactDest = fetchTask.artifactDest("foo-pipeline", "file-name-in-dest");
        Assert.assertThat(artifactDest, is(new File("pipelines/foo-pipeline/dest-dir/file-name-in-dest")));
    }

    @Test
    public void shouldNotPopulatePropertyForPipelineWhenPipelineIsNull() {
        FetchTask fetchTask = new FetchTask(null, new CaseInsensitiveString("bar-stage"), new CaseInsensitiveString("baz-job"), "quux.c", "bang-file");
        // is null when no pipeline name is specified in config xml (manual entry)
        ReflectionUtil.setField(fetchTask, "pipelineName", null);
        Assert.assertThat(fetchTask.getPropertiesForDisplay(), JUnitMatchers.hasItems(new TaskProperty("Stage Name", "bar-stage", "stage_name"), new TaskProperty("Job Name", "baz-job", "job_name"), new TaskProperty("Source File", "quux.c", "source_file"), new TaskProperty("Destination", "bang-file", "destination")));
        Assert.assertThat(fetchTask.getPropertiesForDisplay().size(), is(4));
    }

    @Test
    public void shouldNotFailValidationIfUpstreamExists_PipelineConfigSave() {
        PipelineConfig upstream = new PipelineConfig(new CaseInsensitiveString("upstream-pipeline"), new MaterialConfigs(), new StageConfig(new CaseInsensitiveString("upstream-stage"), new JobConfigs(new JobConfig(new CaseInsensitiveString("upstream-job")))));
        JobConfig job = new JobConfig(new CaseInsensitiveString("downstream-job"));
        FetchTask fetchTask = new FetchTask(new CaseInsensitiveString("upstream-pipeline"), new CaseInsensitiveString("upstream-stage"), new CaseInsensitiveString("upstream-job"), "quux.c", "bang-file");
        job.addTask(fetchTask);
        PipelineConfig downstream = new PipelineConfig(new CaseInsensitiveString("downstream-pipeline"), new MaterialConfigs(new com.thoughtworks.go.config.materials.dependency.DependencyMaterialConfig(upstream.name(), upstream.getFirstStageConfig().name())), new StageConfig(new CaseInsensitiveString("downstream-stage"), new JobConfigs(job)));
        fetchTask.validateTree(PipelineConfigSaveValidationContext.forChain(true, "group", new BasicCruiseConfig(new BasicPipelineConfigs(upstream, downstream)), downstream, downstream.getFirstStageConfig(), downstream.getFirstStageConfig().getJobs().first()));
        Assert.assertThat(fetchTask.errors().isEmpty(), is(true));
    }

    @Test
    public void shouldFailValidationIfFetchArtifactPipelineIsNotAMaterial_PipelineConfigSave() {
        PipelineConfig upstream = new PipelineConfig(new CaseInsensitiveString("upstream-pipeline"), new MaterialConfigs(), new StageConfig(new CaseInsensitiveString("upstream-stage"), new JobConfigs(new JobConfig(new CaseInsensitiveString("upstream-job")))));
        JobConfig job = new JobConfig(new CaseInsensitiveString("downstream-job"));
        FetchTask fetchTask = new FetchTask(new CaseInsensitiveString("upstream-pipeline"), new CaseInsensitiveString("upstream-stage"), new CaseInsensitiveString("upstream-job"), "quux.c", "bang-file");
        job.addTask(fetchTask);
        PipelineConfig downstream = new PipelineConfig(new CaseInsensitiveString("downstream-pipeline"), new MaterialConfigs(), new StageConfig(new CaseInsensitiveString("downstream-stage"), new JobConfigs(job)));
        fetchTask.validateTree(PipelineConfigSaveValidationContext.forChain(true, "group", new BasicCruiseConfig(new BasicPipelineConfigs(upstream, downstream)), downstream, downstream.getFirstStageConfig(), downstream.getFirstStageConfig().getJobs().first()));
        Assert.assertThat(fetchTask.errors().isEmpty(), is(false));
        Assert.assertThat(fetchTask.errors().on(PIPELINE_NAME), is("Pipeline \"downstream-pipeline\" tries to fetch artifact from pipeline \"upstream-pipeline\" which is not an upstream pipeline"));
    }

    @Test
    public void shouldFailValidationIfFetchArtifactPipelineAndStageExistsButJobDoesNot_PipelineConfigSave() {
        PipelineConfig upstream = new PipelineConfig(new CaseInsensitiveString("upstream-pipeline"), new MaterialConfigs(), new StageConfig(new CaseInsensitiveString("upstream-stage"), new JobConfigs(new JobConfig(new CaseInsensitiveString("upstream-job")))));
        JobConfig job = new JobConfig(new CaseInsensitiveString("downstream-job"));
        FetchTask fetchTask = new FetchTask(new CaseInsensitiveString("upstream-pipeline"), new CaseInsensitiveString("upstream-stage"), new CaseInsensitiveString("some-random-job"), "quux.c", "bang-file");
        job.addTask(fetchTask);
        PipelineConfig downstream = new PipelineConfig(new CaseInsensitiveString("downstream-pipeline"), new MaterialConfigs(new com.thoughtworks.go.config.materials.dependency.DependencyMaterialConfig(upstream.name(), upstream.getFirstStageConfig().name())), new StageConfig(new CaseInsensitiveString("downstream-stage"), new JobConfigs(job)));
        fetchTask.validateTree(PipelineConfigSaveValidationContext.forChain(true, "group", new BasicCruiseConfig(new BasicPipelineConfigs(upstream, downstream)), downstream, downstream.getFirstStageConfig(), downstream.getFirstStageConfig().getJobs().first()));
        Assert.assertThat(fetchTask.errors().isEmpty(), is(false));
        Assert.assertThat(fetchTask.errors().on(JOB), is("\"downstream-pipeline :: downstream-stage :: downstream-job\" tries to fetch artifact from job \"upstream-pipeline :: upstream-stage :: some-random-job\" which does not exist."));
    }

    @Test
    public void shouldPassValidationWhenFetchingFromAnInstanceOfRunOnAllJob() {
        StageConfig stage = upstream.getFirstStageConfig();
        JobConfig job = stage.getJobs().get(0);
        job.setRunOnAllAgents(true);
        FetchTask task = new FetchTask(upstream.name(), stage.name(), new CaseInsensitiveString(((job.name()) + "-runOnAll-1")), "src", "dest");
        task.validate(ConfigSaveValidationContext.forChain(config, new BasicPipelineConfigs(), upstream, stage, job));
        Assert.assertThat(task.errors().on(JOB), is(Matchers.nullValue()));
    }
}

