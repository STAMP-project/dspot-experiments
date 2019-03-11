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
import com.thoughtworks.go.config.CaseInsensitiveString;
import com.thoughtworks.go.config.PipelineConfig;
import com.thoughtworks.go.config.materials.MaterialConfigs;
import com.thoughtworks.go.config.materials.Materials;
import com.thoughtworks.go.config.materials.dependency.DependencyMaterial;
import com.thoughtworks.go.config.materials.mercurial.HgMaterial;
import com.thoughtworks.go.config.materials.perforce.P4Material;
import com.thoughtworks.go.config.materials.svn.SvnMaterial;
import com.thoughtworks.go.domain.materials.perforce.P4Client;
import com.thoughtworks.go.domain.materials.perforce.P4Fixture;
import com.thoughtworks.go.domain.materials.svn.SvnCommand;
import com.thoughtworks.go.helper.HgTestRepo;
import com.thoughtworks.go.plugin.access.packagematerial.PackageRepositoryExtension;
import com.thoughtworks.go.plugin.access.pluggabletask.TaskExtension;
import com.thoughtworks.go.plugin.access.scm.SCMExtension;
import com.thoughtworks.go.remote.AgentIdentifier;
import com.thoughtworks.go.util.SystemEnvironment;
import com.thoughtworks.go.util.SystemUtil;
import com.thoughtworks.go.util.command.EnvironmentVariableContext;
import com.thoughtworks.go.utils.SvnRepoFixture;
import java.io.File;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;

import static AgentRuntimeStatus.Idle;


public class BuildWorkEnvironmentVariablesTest {
    private static final String JOB_NAME = "one";

    private static final String STAGE_NAME = "first";

    private static final String PIPELINE_NAME = "cruise";

    private static final String AGENT_UUID = "uuid";

    private static final String TRIGGERED_BY_USER = "approver";

    private File dir;

    private PipelineConfig pipelineConfig;

    private EnvironmentVariableContext environmentVariableContext;

    private SvnCommand command;

    private HgTestRepo hgTestRepo;

    private HgMaterial hgMaterial;

    private SvnMaterial svnMaterial;

    private DependencyMaterial dependencyMaterial;

    private DependencyMaterial dependencyMaterialWithName;

    private SvnRepoFixture svnRepoFixture;

    @Mock
    private PackageRepositoryExtension packageRepositoryExtension;

    @Mock
    private SCMExtension scmExtension;

    @Mock
    private TaskExtension taskExtension;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private P4Material p4Material;

    private P4Fixture p4Fixture;

    private P4Client p4Client;

    private SystemEnvironment systemEnvironment = new SystemEnvironment();

    @Test
    public void shouldSetUpEnvironmentContextCorrectly() throws Exception {
        new SystemEnvironment().setProperty("serviceUrl", "some_random_place");
        Materials materials = new Materials(svnMaterial);
        EnvironmentVariableContext environmentVariableContext = doWorkWithMaterials(materials);
        Assert.assertThat(environmentVariableContext.getProperty("GO_REVISION"), Matchers.is("3"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_SERVER_URL"), Matchers.is("some_random_place"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_PIPELINE_NAME"), Matchers.is(BuildWorkEnvironmentVariablesTest.PIPELINE_NAME));
        Assert.assertThat(environmentVariableContext.getProperty("GO_PIPELINE_LABEL"), Matchers.is("1"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_STAGE_NAME"), Matchers.is(BuildWorkEnvironmentVariablesTest.STAGE_NAME));
        Assert.assertThat(environmentVariableContext.getProperty("GO_STAGE_COUNTER"), Matchers.is("1"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_JOB_NAME"), Matchers.is(BuildWorkEnvironmentVariablesTest.JOB_NAME));
        Assert.assertThat(environmentVariableContext.getProperty("GO_TRIGGER_USER"), Matchers.is(BuildWorkEnvironmentVariablesTest.TRIGGERED_BY_USER));
    }

    @Test
    public void shouldSetUpP4ClientEnvironmentVariableEnvironmentContextCorrectly() {
        new SystemEnvironment().setProperty("serviceUrl", "some_random_place");
        BuildWork work = getBuildWorkWithP4MaterialRevision(p4Material);
        EnvironmentVariableContext environmentVariableContext = new EnvironmentVariableContext();
        AgentIdentifier agentIdentifier = new AgentIdentifier("somename", "127.0.0.1", BuildWorkEnvironmentVariablesTest.AGENT_UUID);
        work.doWork(environmentVariableContext, new AgentWorkContext(agentIdentifier, new FakeBuildRepositoryRemote(), new GoArtifactsManipulatorStub(), new com.thoughtworks.go.server.service.AgentRuntimeInfo(agentIdentifier, Idle, SystemUtil.currentWorkingDirectory(), "cookie", false), packageRepositoryExtension, scmExtension, taskExtension, null, null));
        Assert.assertThat(environmentVariableContext.getProperty("GO_REVISION"), Matchers.is("10"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_SERVER_URL"), Matchers.is("some_random_place"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_TRIGGER_USER"), Matchers.is(BuildWorkEnvironmentVariablesTest.TRIGGERED_BY_USER));
        Assert.assertThat(environmentVariableContext.getProperty("GO_P4_CLIENT"), Matchers.is(p4Material.clientName(dir)));
    }

    @Test
    public void shouldMergeEnvironmentVariablesFromInitialContext() throws IOException {
        pipelineConfig.setMaterialConfigs(new MaterialConfigs());
        BuildAssignment buildAssignment = createAssignment(new EnvironmentVariableContext("foo", "bar"));
        BuildWork work = new BuildWork(buildAssignment, systemEnvironment.consoleLogCharset());
        EnvironmentVariableContext environmentContext = new EnvironmentVariableContext();
        AgentIdentifier agentIdentifier = new AgentIdentifier("somename", "127.0.0.1", BuildWorkEnvironmentVariablesTest.AGENT_UUID);
        work.doWork(environmentContext, new AgentWorkContext(agentIdentifier, new FakeBuildRepositoryRemote(), new GoArtifactsManipulatorStub(), new com.thoughtworks.go.server.service.AgentRuntimeInfo(agentIdentifier, Idle, SystemUtil.currentWorkingDirectory(), "cookie", false), packageRepositoryExtension, scmExtension, taskExtension, null, null));
        assertEnvironmentContext(environmentContext, "foo", Matchers.is("bar"));
    }

    @Test
    public void shouldSetupEnvironmentVariableForDependencyMaterial() throws IOException {
        EnvironmentVariableContext environmentVariableContext = doWorkWithMaterials(new Materials());
        Assert.assertThat(("Properties: \n" + (environmentVariableContext.getProperties())), environmentVariableContext.getProperty("GO_DEPENDENCY_LOCATOR_UPSTREAM1"), Matchers.is("upstream1/0/first/1"));
        Assert.assertThat(("Properties: \n" + (environmentVariableContext.getProperties())), environmentVariableContext.getProperty("GO_DEPENDENCY_LABEL_UPSTREAM1"), Matchers.is("upstream1-label"));
    }

    @Test
    public void shouldSetupEnvironmentVariableUsingDependencyMaterialName() throws IOException {
        EnvironmentVariableContext environmentVariableContext = doWorkWithMaterials(new Materials());
        Assert.assertThat(("Properties: \n" + (environmentVariableContext.getProperties())), environmentVariableContext.getProperty("GO_DEPENDENCY_LOCATOR_DEPENDENCY_MATERIAL_NAME"), Matchers.is("upstream2/0/first/1"));
        Assert.assertThat(("Properties: \n" + (environmentVariableContext.getProperties())), environmentVariableContext.getProperty("GO_DEPENDENCY_LABEL_DEPENDENCY_MATERIAL_NAME"), Matchers.is("upstream2-label"));
    }

    @Test
    public void shouldUseSvnMaterialNameIfPresent() throws IOException {
        svnMaterial.setName(new CaseInsensitiveString("Cruise"));
        pipelineConfig.setMaterialConfigs(new MaterialConfigs(svnMaterial.config()));
        BuildAssignment buildAssigment = createAssignment(null);
        BuildWork work = new BuildWork(buildAssigment, systemEnvironment.consoleLogCharset());
        EnvironmentVariableContext environmentVariableContext = new EnvironmentVariableContext();
        new SystemEnvironment().setProperty("serviceUrl", "some_random_place");
        AgentIdentifier agentIdentifier = new AgentIdentifier("somename", "127.0.0.1", BuildWorkEnvironmentVariablesTest.AGENT_UUID);
        work.doWork(environmentVariableContext, new AgentWorkContext(agentIdentifier, new FakeBuildRepositoryRemote(), new GoArtifactsManipulatorStub(), new com.thoughtworks.go.server.service.AgentRuntimeInfo(agentIdentifier, Idle, SystemUtil.currentWorkingDirectory(), "cookie", false), packageRepositoryExtension, scmExtension, taskExtension, null, null));
        Assert.assertThat(environmentVariableContext.getProperty("GO_REVISION_CRUISE"), Matchers.is("3"));
    }

    @Test
    public void shouldSetUpRevisionIntoEnvironmentContextCorrectlyForMutipleMaterial() throws IOException {
        svnMaterial.setFolder("svn-Dir");
        EnvironmentVariableContext environmentVariableContext = doWorkWithMaterials(new Materials(svnMaterial, hgMaterial));
        Assert.assertThat(environmentVariableContext.getProperty("GO_REVISION_SVN_DIR"), Matchers.is("3"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_REVISION_HG_DIR"), Matchers.is("ca3ebb67f527c0ad7ed26b789056823d8b9af23f"));
    }

    @Test
    public void shouldOutputEnvironmentVariablesIntoConsoleOut() throws IOException {
        BuildAssignment buildAssigment = createAssignment(null);
        BuildWork work = new BuildWork(buildAssigment, systemEnvironment.consoleLogCharset());
        GoArtifactsManipulatorStub manipulator = new GoArtifactsManipulatorStub();
        new SystemEnvironment().setProperty("serviceUrl", "some_random_place");
        AgentIdentifier agentIdentifier = new AgentIdentifier("somename", "127.0.0.1", BuildWorkEnvironmentVariablesTest.AGENT_UUID);
        work.doWork(environmentVariableContext, new AgentWorkContext(agentIdentifier, new FakeBuildRepositoryRemote(), manipulator, new com.thoughtworks.go.server.service.AgentRuntimeInfo(agentIdentifier, Idle, SystemUtil.currentWorkingDirectory(), "cookie", false), packageRepositoryExtension, scmExtension, taskExtension, null, null));
        Assert.assertThat(manipulator.consoleOut(), printedEnvVariable("GO_SERVER_URL", "some_random_place"));
        Assert.assertThat(manipulator.consoleOut(), printedEnvVariable("GO_PIPELINE_NAME", BuildWorkEnvironmentVariablesTest.PIPELINE_NAME));
        Assert.assertThat(manipulator.consoleOut(), printedEnvVariable("GO_PIPELINE_COUNTER", 1));
        Assert.assertThat(manipulator.consoleOut(), printedEnvVariable("GO_PIPELINE_LABEL", 1));
        Assert.assertThat(manipulator.consoleOut(), printedEnvVariable("GO_STAGE_NAME", BuildWorkEnvironmentVariablesTest.STAGE_NAME));
        Assert.assertThat(manipulator.consoleOut(), printedEnvVariable("GO_STAGE_COUNTER", 1));
        Assert.assertThat(manipulator.consoleOut(), printedEnvVariable("GO_JOB_NAME", BuildWorkEnvironmentVariablesTest.JOB_NAME));
        Assert.assertThat(manipulator.consoleOut(), printedEnvVariable("GO_REVISION", 3));
        Assert.assertThat(manipulator.consoleOut(), printedEnvVariable("GO_TRIGGER_USER", BuildWorkEnvironmentVariablesTest.TRIGGERED_BY_USER));
    }

    @Test
    public void shouldSetEnvironmentVariableForSvnExternal() throws IOException {
        svnRepoFixture.createExternals(svnRepoFixture.getEnd2EndRepoUrl());
        command = new SvnCommand(null, svnRepoFixture.getEnd2EndRepoUrl(), null, null, true);
        svnMaterial = SvnMaterial.createSvnMaterialWithMock(command);
        svnMaterial.setFolder("svn-Dir");
        EnvironmentVariableContext environmentVariableContext = doWorkWithMaterials(new Materials(svnMaterial));
        Assert.assertThat(environmentVariableContext.getProperty("GO_REVISION_SVN_DIR"), Matchers.is("4"));
        Assert.assertThat(environmentVariableContext.getProperty("GO_REVISION_SVN_DIR_EXTERNAL"), Matchers.is("4"));
    }
}

