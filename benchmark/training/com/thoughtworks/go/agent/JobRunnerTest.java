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
package com.thoughtworks.go.agent;


import com.thoughtworks.go.remote.AgentIdentifier;
import com.thoughtworks.go.remote.AgentInstruction;
import com.thoughtworks.go.remote.work.BuildWork;
import com.thoughtworks.go.server.service.UpstreamPipelineResolver;
import com.thoughtworks.go.work.FakeWork;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static AgentRuntimeStatus.Idle;


public class JobRunnerTest {
    private static final String SERVER_URL = "somewhere-does-not-matter";

    private static final String JOB_PLAN_NAME = "run-ant";

    private JobRunner runner;

    private FakeWork work;

    private List<String> consoleOut;

    private List<Enum> statesAndResult;

    private List<Property> properties;

    private BuildWork buildWork;

    private AgentIdentifier agentIdentifier;

    private UpstreamPipelineResolver resolver;

    @Test
    public void shouldDoNothingWhenJobIsNotCancelled() {
        runner.setWork(work);
        runner.handleInstruction(new AgentInstruction(false), new com.thoughtworks.go.server.service.AgentRuntimeInfo(agentIdentifier, Idle, currentWorkingDirectory(), "cookie", false));
        Assert.assertThat(work.getCallCount(), Matchers.is(0));
    }

    @Test
    public void shouldCancelOncePerJob() {
        runner.setWork(work);
        runner.handleInstruction(new AgentInstruction(true), new com.thoughtworks.go.server.service.AgentRuntimeInfo(agentIdentifier, Idle, currentWorkingDirectory(), "cookie", false));
        Assert.assertThat(work.getCallCount(), Matchers.is(1));
        runner.handleInstruction(new AgentInstruction(true), new com.thoughtworks.go.server.service.AgentRuntimeInfo(agentIdentifier, Idle, currentWorkingDirectory(), "cookie", false));
        Assert.assertThat(work.getCallCount(), Matchers.is(1));
    }

    @Test
    public void shouldReturnTrueOnGetJobIsCancelledWhenJobIsCancelled() {
        Assert.assertThat(runner.isJobCancelled(), Matchers.is(false));
        runner.handleInstruction(new AgentInstruction(true), new com.thoughtworks.go.server.service.AgentRuntimeInfo(agentIdentifier, Idle, currentWorkingDirectory(), "cookie", false));
        Assert.assertThat(runner.isJobCancelled(), Matchers.is(true));
    }
}

