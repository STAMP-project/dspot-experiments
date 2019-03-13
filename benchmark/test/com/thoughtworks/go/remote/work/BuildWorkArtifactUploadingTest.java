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
package com.thoughtworks.go.remote.work;


import com.thoughtworks.go.agent.testhelpers.FakeBuildRepositoryRemote;
import com.thoughtworks.go.config.materials.svn.SvnMaterial;
import com.thoughtworks.go.domain.builder.Builder;
import com.thoughtworks.go.domain.builder.NullBuilder;
import com.thoughtworks.go.matchers.UploadEntry;
import com.thoughtworks.go.plugin.access.artifact.ArtifactExtension;
import com.thoughtworks.go.plugin.access.packagematerial.PackageRepositoryExtension;
import com.thoughtworks.go.plugin.access.pluggabletask.TaskExtension;
import com.thoughtworks.go.plugin.access.scm.SCMExtension;
import com.thoughtworks.go.plugin.infra.PluginRequestProcessorRegistry;
import com.thoughtworks.go.remote.AgentIdentifier;
import com.thoughtworks.go.util.FileUtil;
import com.thoughtworks.go.util.SystemEnvironment;
import com.thoughtworks.go.util.SystemUtil;
import com.thoughtworks.go.util.URLService;
import com.thoughtworks.go.util.ZipUtil;
import com.thoughtworks.go.util.command.CruiseControlException;
import com.thoughtworks.go.util.command.EnvironmentVariableContext;
import com.thoughtworks.go.utils.SvnRepoFixture;
import com.thoughtworks.go.work.DefaultGoPublisher;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;

import static AgentRuntimeStatus.Idle;
import static ArtifactPlanType.file;


public class BuildWorkArtifactUploadingTest {
    private static final String JOB_NAME = "one";

    private static final String STAGE_NAME = "first";

    private static final String PIPELINE_NAME = "cruise";

    private static final String AGENT_UUID = "uuid";

    private EnvironmentVariableContext environmentVariableContext;

    private SvnMaterial svnMaterial;

    private SvnRepoFixture svnRepoFixture;

    private SystemEnvironment systemEnvironment = new SystemEnvironment();

    File buildWorkingDirectory;

    @Mock
    private PackageRepositoryExtension packageRepositoryExtension;

    @Mock
    private SCMExtension scmExtension;

    @Mock
    private TaskExtension taskExtension;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private PluginRequestProcessorRegistry pluginRequestProcessorRegistry;

    @Test
    public void shouldUploadEachMatchedFile() throws Exception {
        List<ArtifactPlan> artifactPlans = new ArrayList<>();
        artifactPlans.add(new ArtifactPlan(file, "**/*.png", "mypic"));
        BuildAssignment buildAssigment = createAssignment(artifactPlans, new String[]{ "logs/pic/fail.png", "logs/pic/pass.png", "README" });
        BuildWork work = new BuildWork(buildAssigment, systemEnvironment.consoleLogCharset());
        GoArtifactsManipulatorStub manipulator = new GoArtifactsManipulatorStub();
        AgentIdentifier agentIdentifier = new AgentIdentifier("somename", "127.0.0.1", BuildWorkArtifactUploadingTest.AGENT_UUID);
        work.doWork(environmentVariableContext, new AgentWorkContext(agentIdentifier, new FakeBuildRepositoryRemote(), manipulator, new com.thoughtworks.go.server.service.AgentRuntimeInfo(agentIdentifier, Idle, SystemUtil.currentWorkingDirectory(), "cookie", false), packageRepositoryExtension, scmExtension, taskExtension, null, pluginRequestProcessorRegistry));
        List<UploadEntry> entries = manipulator.uploadEntries();
        Assert.assertThat(entries, uploadFileToDestination(new File(((buildWorkingDirectory.getPath()) + "/logs/pic/pass.png")), "mypic/logs/pic"));
        Assert.assertThat(entries, uploadFileToDestination(new File(((buildWorkingDirectory.getPath()) + "/logs/pic/fail.png")), "mypic/logs/pic"));
    }

    @Test
    public void shouldUploadMatchedFolder() throws Exception {
        List<ArtifactPlan> artifactPlans = new ArrayList<>();
        artifactPlans.add(new ArtifactPlan(file, "**/*", "mypic"));
        BuildAssignment buildAssigment = createAssignment(artifactPlans, new String[]{ "logs/pic/fail.png", "logs/pic/pass.png", "README" });
        BuildWork work = new BuildWork(buildAssigment, systemEnvironment.consoleLogCharset());
        GoArtifactsManipulatorStub manipulator = new GoArtifactsManipulatorStub();
        AgentIdentifier agentIdentifier = new AgentIdentifier("somename", "127.0.0.1", BuildWorkArtifactUploadingTest.AGENT_UUID);
        work.doWork(environmentVariableContext, new AgentWorkContext(agentIdentifier, new FakeBuildRepositoryRemote(), manipulator, new com.thoughtworks.go.server.service.AgentRuntimeInfo(agentIdentifier, Idle, SystemUtil.currentWorkingDirectory(), "cookie", false), packageRepositoryExtension, scmExtension, taskExtension, null, pluginRequestProcessorRegistry));
        List<UploadEntry> entries = manipulator.uploadEntries();
        Assert.assertThat(entries, Matchers.not(uploadFileToDestination(new File(((buildWorkingDirectory.getPath()) + "/logs/pic/fail.png")), "mypic/logs/pic")));
        Assert.assertThat(entries, Matchers.not(uploadFileToDestination(new File(((buildWorkingDirectory.getPath()) + "/logs/pic/pass.png")), "mypic/logs/pic")));
        Assert.assertThat(entries, uploadFileToDestination(new File(((buildWorkingDirectory.getPath()) + "/logs/pic")), "mypic/logs"));
        Assert.assertThat(entries, uploadFileToDestination(new File(((buildWorkingDirectory.getPath()) + "/README")), "mypic"));
    }

    @Test
    public void shouldNotUploadFileContainingFolderAgain() throws Exception {
        List<ArtifactPlan> artifactPlans = new ArrayList<>();
        artifactPlans.add(new ArtifactPlan(file, "logs/pic/*.png", "mypic"));
        BuildAssignment buildAssigment = createAssignment(artifactPlans, new String[]{ "logs/pic/fail.png", "logs/pic/pass.png", "README" });
        BuildWork work = new BuildWork(buildAssigment, systemEnvironment.consoleLogCharset());
        GoArtifactsManipulatorStub manipulator = new GoArtifactsManipulatorStub();
        AgentIdentifier agentIdentifier = new AgentIdentifier("somename", "127.0.0.1", BuildWorkArtifactUploadingTest.AGENT_UUID);
        work.doWork(environmentVariableContext, new AgentWorkContext(agentIdentifier, new FakeBuildRepositoryRemote(), manipulator, new com.thoughtworks.go.server.service.AgentRuntimeInfo(agentIdentifier, Idle, SystemUtil.currentWorkingDirectory(), "cookie", false), packageRepositoryExtension, scmExtension, taskExtension, null, pluginRequestProcessorRegistry));
        List<UploadEntry> entries = manipulator.uploadEntries();
        Assert.assertThat(entries, uploadFileToDestination(new File(((buildWorkingDirectory.getPath()) + "/logs/pic/pass.png")), "mypic"));
        Assert.assertThat(entries, uploadFileToDestination(new File(((buildWorkingDirectory.getPath()) + "/logs/pic/fail.png")), "mypic"));
        Assert.assertThat(entries, Matchers.not(uploadFileToDestination(new File(((buildWorkingDirectory.getPath()) + "/logs/pic")), "mypic")));
    }

    @Test
    public void shouldUploadFolderWhenMatchedWithWildCards() throws Exception {
        List<ArtifactPlan> artifactPlans = new ArrayList<>();
        artifactPlans.add(new ArtifactPlan(file, "logs/pic-*", "mypic"));
        BuildAssignment buildAssigment = createAssignment(artifactPlans, new String[]{ "logs/pic-1/fail.png", "logs/pic-1/pass.png", "logs/pic-2/cancel.png", "logs/pic-2/complete.png", "README" });
        BuildWork work = new BuildWork(buildAssigment, systemEnvironment.consoleLogCharset());
        GoArtifactsManipulatorStub manipulator = new GoArtifactsManipulatorStub();
        AgentIdentifier agentIdentifier = new AgentIdentifier("somename", "127.0.0.1", BuildWorkArtifactUploadingTest.AGENT_UUID);
        work.doWork(environmentVariableContext, new AgentWorkContext(agentIdentifier, new FakeBuildRepositoryRemote(), manipulator, new com.thoughtworks.go.server.service.AgentRuntimeInfo(agentIdentifier, Idle, SystemUtil.currentWorkingDirectory(), "cookie", false), packageRepositoryExtension, scmExtension, taskExtension, null, pluginRequestProcessorRegistry));
        List<UploadEntry> entries = manipulator.uploadEntries();
        Assert.assertThat(entries, Matchers.not(uploadFileToDestination(new File(((buildWorkingDirectory.getPath()) + "/logs/pic-1/pass.png")), "mypic")));
        Assert.assertThat(entries, Matchers.not(uploadFileToDestination(new File(((buildWorkingDirectory.getPath()) + "/logs/pic-1/fail.png")), "mypic")));
        Assert.assertThat(entries, Matchers.not(uploadFileToDestination(new File(((buildWorkingDirectory.getPath()) + "/logs/pic-2/cancel.png")), "mypic")));
        Assert.assertThat(entries, Matchers.not(uploadFileToDestination(new File(((buildWorkingDirectory.getPath()) + "/logs/pic-2/complete.png")), "mypic")));
        Assert.assertThat(entries, uploadFileToDestination(new File(((buildWorkingDirectory.getPath()) + "/logs/pic-1")), "mypic"));
        Assert.assertThat(entries, uploadFileToDestination(new File(((buildWorkingDirectory.getPath()) + "/logs/pic-2")), "mypic"));
    }

    @Test
    public void shouldUploadFolderWhenDirectMatch() throws Exception {
        List<ArtifactPlan> artifactPlans = new ArrayList<>();
        artifactPlans.add(new ArtifactPlan(file, "logs/pic-1", "mypic"));
        BuildAssignment buildAssigment = createAssignment(artifactPlans, new String[]{ "logs/pic-1/fail.png", "logs/pic-1/pass.png", "logs/pic-2/cancel.png", "logs/pic-2/complete.png", "README" });
        BuildWork work = new BuildWork(buildAssigment, systemEnvironment.consoleLogCharset());
        GoArtifactsManipulatorStub manipulator = new GoArtifactsManipulatorStub();
        AgentIdentifier agentIdentifier = new AgentIdentifier("somename", "127.0.0.1", BuildWorkArtifactUploadingTest.AGENT_UUID);
        work.doWork(environmentVariableContext, new AgentWorkContext(agentIdentifier, new FakeBuildRepositoryRemote(), manipulator, new com.thoughtworks.go.server.service.AgentRuntimeInfo(agentIdentifier, Idle, SystemUtil.currentWorkingDirectory(), "cookie", false), packageRepositoryExtension, scmExtension, taskExtension, null, pluginRequestProcessorRegistry));
        List<UploadEntry> entries = manipulator.uploadEntries();
        Assert.assertThat(entries, uploadFileToDestination(new File(((buildWorkingDirectory.getPath()) + "/logs/pic-1")), "mypic"));
    }

    @Test
    public void shouldUploadFolderWhenTrimedPathDirectMatch() throws Exception {
        List<ArtifactPlan> artifactPlans = new ArrayList<>();
        artifactPlans.add(new ArtifactPlan(file, "logs/pic-1 ", "mypic"));
        BuildAssignment buildAssigment = createAssignment(artifactPlans, new String[]{ "logs/pic-1/fail.png", "logs/pic-1/pass.png", "logs/pic-2/cancel.png", "logs/pic-2/complete.png", "README" });
        BuildWork work = new BuildWork(buildAssigment, systemEnvironment.consoleLogCharset());
        GoArtifactsManipulatorStub manipulator = new GoArtifactsManipulatorStub();
        AgentIdentifier agentIdentifier = new AgentIdentifier("somename", "127.0.0.1", BuildWorkArtifactUploadingTest.AGENT_UUID);
        work.doWork(environmentVariableContext, new AgentWorkContext(agentIdentifier, new FakeBuildRepositoryRemote(), manipulator, new com.thoughtworks.go.server.service.AgentRuntimeInfo(agentIdentifier, Idle, SystemUtil.currentWorkingDirectory(), "cookie", false), packageRepositoryExtension, scmExtension, taskExtension, null, pluginRequestProcessorRegistry));
        List<UploadEntry> entries = manipulator.uploadEntries();
        Assert.assertThat(entries, uploadFileToDestination(new File(((buildWorkingDirectory.getPath()) + "/logs/pic-1")), "mypic"));
    }

    @Test
    public void shouldFailBuildWhenNothingMatched() throws Exception {
        List<ArtifactPlan> artifactPlans = new ArrayList<>();
        artifactPlans.add(new ArtifactPlan(file, "logs/picture", "mypic"));
        BuildAssignment buildAssigment = createAssignment(artifactPlans, new String[]{ "logs/pic-1/fail.png", "logs/pic-1/pass.png", "logs/pic-2/cancel.png", "logs/pic-2/complete.png", "README" });
        BuildWork work = new BuildWork(buildAssigment, systemEnvironment.consoleLogCharset());
        GoArtifactsManipulatorStub manipulator = new GoArtifactsManipulatorStub();
        BuildRepositoryRemoteStub repository = new BuildRepositoryRemoteStub();
        AgentIdentifier agentIdentifier = new AgentIdentifier("somename", "127.0.0.1", BuildWorkArtifactUploadingTest.AGENT_UUID);
        work.doWork(environmentVariableContext, new AgentWorkContext(agentIdentifier, repository, manipulator, new com.thoughtworks.go.server.service.AgentRuntimeInfo(agentIdentifier, Idle, SystemUtil.currentWorkingDirectory(), "cookie", false), packageRepositoryExtension, scmExtension, taskExtension, null, pluginRequestProcessorRegistry));
        List<UploadEntry> entries = manipulator.uploadEntries();
        Assert.assertThat(entries.size(), Matchers.is(0));
        Assert.assertThat(repository.states, containsResult(JobState.Building));
        Assert.assertThat(repository.states, containsResult(JobState.Completing));
        Assert.assertThat(repository.results, containsResult(JobResult.Failed));
        Assert.assertThat(manipulator.consoleOut(), printedRuleDoesNotMatchFailure(buildWorkingDirectory.getPath(), "logs/picture"));
    }

    @Test
    public void shouldFailBuildWhenSourceDirectoryDoesNotExist() throws Exception {
        List<ArtifactPlan> artifactPlans = new ArrayList<>();
        artifactPlans.add(new ArtifactPlan(file, "not-Exist-Folder", "mypic"));
        BuildAssignment buildAssigment = createAssignment(artifactPlans, new String[]{ "logs/pic-1/fail.png", "logs/pic-1/pass.png", "logs/pic-2/cancel.png", "logs/pic-2/complete.png", "README" });
        BuildWork work = new BuildWork(buildAssigment, systemEnvironment.consoleLogCharset());
        GoArtifactsManipulatorStub manipulator = new GoArtifactsManipulatorStub();
        BuildRepositoryRemoteStub repository = new BuildRepositoryRemoteStub();
        AgentIdentifier agentIdentifier = new AgentIdentifier("somename", "127.0.0.1", BuildWorkArtifactUploadingTest.AGENT_UUID);
        work.doWork(environmentVariableContext, new AgentWorkContext(agentIdentifier, repository, manipulator, new com.thoughtworks.go.server.service.AgentRuntimeInfo(agentIdentifier, Idle, SystemUtil.currentWorkingDirectory(), "cookie", false), packageRepositoryExtension, scmExtension, taskExtension, null, pluginRequestProcessorRegistry));
        List<UploadEntry> entries = manipulator.uploadEntries();
        Assert.assertThat(entries.size(), Matchers.is(0));
        Assert.assertThat(repository.states, containsResult(JobState.Building));
        Assert.assertThat(repository.states, containsResult(JobState.Completing));
        Assert.assertThat(repository.results, containsResult(JobResult.Failed));
        Assert.assertThat(manipulator.consoleOut(), printedRuleDoesNotMatchFailure(buildWorkingDirectory.getPath(), "not-Exist-Folder"));
    }

    @Test
    public void shouldFailBuildWhenNothingMatchedUsingMatcherStartDotStart() throws Exception {
        List<ArtifactPlan> artifactPlans = new ArrayList<>();
        artifactPlans.add(new ArtifactPlan(file, "target/pkg/*.*", "MYDEST"));
        BuildAssignment buildAssigment = createAssignment(artifactPlans, new String[]{ "target/pkg/" });
        BuildWork work = new BuildWork(buildAssigment, systemEnvironment.consoleLogCharset());
        GoArtifactsManipulatorStub manipulator = new GoArtifactsManipulatorStub();
        BuildRepositoryRemoteStub repository = new BuildRepositoryRemoteStub();
        AgentIdentifier agentIdentifier = new AgentIdentifier("somename", "127.0.0.1", BuildWorkArtifactUploadingTest.AGENT_UUID);
        work.doWork(environmentVariableContext, new AgentWorkContext(agentIdentifier, repository, manipulator, new com.thoughtworks.go.server.service.AgentRuntimeInfo(agentIdentifier, Idle, SystemUtil.currentWorkingDirectory(), "cookie", false), packageRepositoryExtension, scmExtension, taskExtension, null, pluginRequestProcessorRegistry));
        List<UploadEntry> entries = manipulator.uploadEntries();
        Assert.assertThat(entries.size(), Matchers.is(0));
        Assert.assertThat(repository.states, containsResult(JobState.Building));
        Assert.assertThat(repository.states, containsResult(JobState.Completing));
        Assert.assertThat(repository.results, containsResult(JobResult.Failed));
        Assert.assertThat(manipulator.consoleOut(), printedRuleDoesNotMatchFailure(buildWorkingDirectory.getPath(), "target/pkg/*.*"));
    }

    @Test
    public void shouldReportUploadFailuresWhenTheyHappen() throws Exception {
        List<ArtifactPlan> artifactPlans = new ArrayList<>();
        artifactPlans.add(new ArtifactPlan(file, "**/*.png", "mypic"));
        BuildAssignment buildAssigment = createAssignment(artifactPlans, new String[]{ "logs/pic/pass.png", "logs/pic-1/pass.png" });
        BuildWork work = new BuildWork(buildAssigment, systemEnvironment.consoleLogCharset());
        GoArtifactsManipulatorStub manipulator = new GoArtifactsManipulatorStub(new ArrayList(), new ArrayList(), new HttpServiceStub(), new URLService(), new BuildWorkArtifactUploadingTest.ZipUtilThatRunsOutOfMemory());
        AgentIdentifier agentIdentifier = new AgentIdentifier("somename", "127.0.0.1", BuildWorkArtifactUploadingTest.AGENT_UUID);
        work.doWork(environmentVariableContext, new AgentWorkContext(agentIdentifier, new FakeBuildRepositoryRemote(), manipulator, new com.thoughtworks.go.server.service.AgentRuntimeInfo(agentIdentifier, Idle, SystemUtil.currentWorkingDirectory(), "cookie", false), packageRepositoryExtension, scmExtension, taskExtension, null, pluginRequestProcessorRegistry));
        List<UploadEntry> entries = manipulator.uploadEntries();
        Assert.assertThat(entries.isEmpty(), Matchers.is(true));
        Assert.assertThat(manipulator.consoleOut(), Matchers.containsString("Failed to upload [**/*.png]"));
    }

    private class ZipUtilThatRunsOutOfMemory extends ZipUtil {
        public File zip(File source, File destFile, int level) {
            throw new OutOfMemoryError("#2824");
        }
    }

    public class CreateFileBuilder extends Builder {
        private final String[] files;

        public CreateFileBuilder(String[] files) {
            super(new RunIfConfigs(), new NullBuilder(), "");
            this.files = files;
        }

        public void build(DefaultGoPublisher publisher, EnvironmentVariableContext environmentVariableContext, TaskExtension taskExtension, ArtifactExtension artifactExtension, PluginRequestProcessorRegistry pluginRequestProcessorRegistry, String consoleLogCharset) throws CruiseControlException {
            try {
                FileUtil.createFilesByPath(buildWorkingDirectory, files);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}

