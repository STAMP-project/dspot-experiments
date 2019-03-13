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
package com.thoughtworks.go.remote.work;


import com.thoughtworks.go.domain.AgentRuntimeStatus;
import com.thoughtworks.go.plugin.access.packagematerial.PackageRepositoryExtension;
import com.thoughtworks.go.plugin.access.pluggabletask.TaskExtension;
import com.thoughtworks.go.plugin.access.scm.SCMExtension;
import com.thoughtworks.go.remote.AgentIdentifier;
import com.thoughtworks.go.server.service.AgentRuntimeInfo;
import com.thoughtworks.go.util.SystemUtil;
import com.thoughtworks.go.util.command.EnvironmentVariableContext;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class AgentStatusReportTest {
    private AgentIdentifier agentIdentifier;

    private EnvironmentVariableContext environmentVariableContext;

    private GoArtifactsManipulatorStub artifactManipulator;

    private BuildRepositoryRemoteStub buildRepository;

    private AgentRuntimeInfo agentRuntimeInfo;

    private PackageRepositoryExtension packageRepositoryExtension;

    private SCMExtension scmExtension;

    private TaskExtension taskExtension;

    @Test
    public void shouldReportIdleWhenAgentRunningNoWork() {
        NoWork work = new NoWork();
        work.doWork(environmentVariableContext, new AgentWorkContext(agentIdentifier, buildRepository, artifactManipulator, agentRuntimeInfo, packageRepositoryExtension, scmExtension, taskExtension, null, null));
        Assert.assertThat(agentRuntimeInfo, Matchers.is(new AgentRuntimeInfo(agentIdentifier, AgentRuntimeStatus.Idle, SystemUtil.currentWorkingDirectory(), "cookie", false)));
    }

    @Test
    public void shouldReportIdleWhenAgentCancelledNoWork() {
        NoWork work = new NoWork();
        work.cancel(environmentVariableContext, agentRuntimeInfo);
        Assert.assertThat(agentRuntimeInfo, Matchers.is(new AgentRuntimeInfo(agentIdentifier, AgentRuntimeStatus.Idle, SystemUtil.currentWorkingDirectory(), "cookie", false)));
    }

    @Test
    public void shouldReportIdleWhenAgentRunningDeniedWork() {
        Work work = new DeniedAgentWork("uuid");
        work.doWork(environmentVariableContext, new AgentWorkContext(agentIdentifier, buildRepository, artifactManipulator, agentRuntimeInfo, packageRepositoryExtension, scmExtension, taskExtension, null, null));
        Assert.assertThat(agentRuntimeInfo, Matchers.is(new AgentRuntimeInfo(agentIdentifier, AgentRuntimeStatus.Idle, SystemUtil.currentWorkingDirectory(), "cookie", false)));
    }

    @Test
    public void shouldReportIdleWhenAgentCancelledDeniedWork() {
        Work work = new DeniedAgentWork("uuid");
        work.cancel(environmentVariableContext, agentRuntimeInfo);
        Assert.assertThat(agentRuntimeInfo, Matchers.is(new AgentRuntimeInfo(agentIdentifier, AgentRuntimeStatus.Idle, SystemUtil.currentWorkingDirectory(), "cookie", false)));
    }

    @Test
    public void shouldNotChangeWhenAgentRunningUnregisteredAgentWork() {
        Work work = new UnregisteredAgentWork("uuid");
        try {
            work.doWork(environmentVariableContext, new AgentWorkContext(agentIdentifier, buildRepository, artifactManipulator, agentRuntimeInfo, packageRepositoryExtension, scmExtension, taskExtension, null, null));
        } catch (Exception e) {
        }
        Assert.assertThat(agentRuntimeInfo, Matchers.is(new AgentRuntimeInfo(agentIdentifier, AgentRuntimeStatus.Idle, SystemUtil.currentWorkingDirectory(), "cookie", false)));
    }

    @Test
    public void shouldNotChangeIdleWhenAgentCancelledUnregisteredAgentWork() {
        Work work = new UnregisteredAgentWork("uuid");
        try {
            work.cancel(environmentVariableContext, agentRuntimeInfo);
        } catch (Exception e) {
        }
        Assert.assertThat(agentRuntimeInfo, Matchers.is(new AgentRuntimeInfo(agentIdentifier, AgentRuntimeStatus.Idle, SystemUtil.currentWorkingDirectory(), "cookie", false)));
    }
}

