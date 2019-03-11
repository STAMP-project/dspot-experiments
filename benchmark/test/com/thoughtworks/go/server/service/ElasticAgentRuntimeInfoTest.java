/**
 * Copyright 2016 ThoughtWorks, Inc.
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


import AgentRuntimeStatus.Idle;
import AgentStatus.Building;
import com.thoughtworks.go.domain.AgentRuntimeStatus;
import com.thoughtworks.go.remote.AgentIdentifier;
import com.thoughtworks.go.util.SystemEnvironment;
import com.thoughtworks.go.websocket.MessageEncoding;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ElasticAgentRuntimeInfoTest {
    @Test
    public void shouldUpdateSelfForAnIdleAgent() throws Exception {
        ElasticAgentRuntimeInfo agentRuntimeInfo = new ElasticAgentRuntimeInfo(new AgentIdentifier("localhost", "127.0.0.1", "uuid"), AgentRuntimeStatus.Idle, "/foo/one", null, "42", "go.cd.elastic-agent-plugin.docker");
        ElasticAgentRuntimeInfo newRuntimeInfo = new ElasticAgentRuntimeInfo(new AgentIdentifier("go02", "10.10.10.1", "uuid"), Building.getRuntimeStatus(), "/foo/two", "cookie", "42", "go.cd.elastic-agent-plugin.docker");
        agentRuntimeInfo.updateSelf(newRuntimeInfo);
        Assert.assertThat(agentRuntimeInfo.getBuildingInfo(), Matchers.is(newRuntimeInfo.getBuildingInfo()));
        Assert.assertThat(agentRuntimeInfo.getLocation(), Matchers.is(newRuntimeInfo.getLocation()));
        Assert.assertThat(agentRuntimeInfo.getUsableSpace(), Matchers.is(newRuntimeInfo.getUsableSpace()));
        Assert.assertThat(agentRuntimeInfo.getOperatingSystem(), Matchers.is(newRuntimeInfo.getOperatingSystem()));
        Assert.assertThat(agentRuntimeInfo.getElasticAgentId(), Matchers.is(newRuntimeInfo.getElasticAgentId()));
        Assert.assertThat(agentRuntimeInfo.getElasticPluginId(), Matchers.is(newRuntimeInfo.getElasticPluginId()));
    }

    @Test
    public void dataMapEncodingAndDecoding() {
        AgentRuntimeInfo info = new ElasticAgentRuntimeInfo(new AgentIdentifier("localhost", "127.0.0.1", "uuid"), AgentRuntimeStatus.Idle, "/foo/one", null, "42", "go.cd.elastic-agent-plugin.docker");
        AgentRuntimeInfo clonedInfo = MessageEncoding.decodeData(MessageEncoding.encodeData(info), AgentRuntimeInfo.class);
        Assert.assertThat(clonedInfo, Matchers.is(info));
    }

    @Test
    public void shouldRefreshOperatingSystemOfAgent() throws Exception {
        AgentIdentifier identifier = new AgentIdentifier("local.in", "127.0.0.1", "uuid-1");
        AgentRuntimeInfo runtimeInfo = ElasticAgentRuntimeInfo.fromAgent(identifier, Idle, "/tmp/foo", false);
        String os = new SystemEnvironment().getOperatingSystemCompleteName();
        Assert.assertThat(runtimeInfo.getOperatingSystem(), Matchers.is(os));
    }

    @Test
    public void shouldRefreshUsableSpaceOfAgent() throws Exception {
        AgentIdentifier identifier = new AgentIdentifier("local.in", "127.0.0.1", "uuid-1");
        String workingDirectory = FileUtils.getTempDirectory().getAbsolutePath();
        AgentRuntimeInfo runtimeInfo = ElasticAgentRuntimeInfo.fromAgent(identifier, Idle, workingDirectory, false);
        long space = ElasticAgentRuntimeInfo.usableSpace(workingDirectory);
        Assert.assertThat(runtimeInfo.getUsableSpace(), Matchers.is(space));
    }
}

