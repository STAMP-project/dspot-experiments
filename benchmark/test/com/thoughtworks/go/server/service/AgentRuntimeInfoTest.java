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


import AgentRuntimeStatus.Idle;
import AgentRuntimeStatus.Unknown;
import com.thoughtworks.go.config.AgentConfig;
import com.thoughtworks.go.domain.AgentRuntimeStatus;
import com.thoughtworks.go.remote.AgentIdentifier;
import com.thoughtworks.go.util.SystemUtil;
import com.thoughtworks.go.websocket.MessageEncoding;
import java.io.File;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class AgentRuntimeInfoTest {
    private static final int OLD_IDX = 0;

    private static final int NEW_IDX = 1;

    private File pipelinesFolder;

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    @Test(expected = Exception.class)
    public void should() throws Exception {
        AgentRuntimeInfo.fromServer(new AgentConfig("uuid", "localhost", "127.0.0.1"), false, "", 0L, "linux", false);
    }

    @Test
    public void shouldUsingIdleWhenRegistrationRequestIsFromLocalAgent() {
        AgentRuntimeInfo agentRuntimeInfo = AgentRuntimeInfo.fromServer(new AgentConfig("uuid", "localhost", "127.0.0.1"), false, "/var/lib", 0L, "linux", false);
        Assert.assertThat(agentRuntimeInfo.getRuntimeStatus(), Matchers.is(AgentRuntimeStatus.Idle));
    }

    @Test
    public void shouldBeUnknownWhenRegistrationRequestIsFromLocalAgent() {
        AgentRuntimeInfo agentRuntimeInfo = AgentRuntimeInfo.fromServer(new AgentConfig("uuid", "localhost", "176.19.4.1"), false, "/var/lib", 0L, "linux", false);
        Assert.assertThat(agentRuntimeInfo.getRuntimeStatus(), Matchers.is(Unknown));
    }

    @Test
    public void shouldUsingIdleWhenRegistrationRequestIsFromAlreadyRegisteredAgent() {
        AgentRuntimeInfo agentRuntimeInfo = AgentRuntimeInfo.fromServer(new AgentConfig("uuid", "localhost", "176.19.4.1"), true, "/var/lib", 0L, "linux", false);
        Assert.assertThat(agentRuntimeInfo.getRuntimeStatus(), Matchers.is(Idle));
    }

    @Test
    public void shouldNotMatchRuntimeInfosWithDifferentOperatingSystems() {
        AgentRuntimeInfo linux = AgentRuntimeInfo.fromServer(new AgentConfig("uuid", "localhost", "176.19.4.1"), true, "/var/lib", 0L, "linux", false);
        AgentRuntimeInfo osx = AgentRuntimeInfo.fromServer(new AgentConfig("uuid", "localhost", "176.19.4.1"), true, "/var/lib", 0L, "foo bar", false);
        Assert.assertThat(linux, Matchers.is(Matchers.not(osx)));
    }

    @Test
    public void shouldInitializeTheFreeSpaceAtAgentSide() {
        AgentIdentifier id = new AgentConfig("uuid", "localhost", "176.19.4.1").getAgentIdentifier();
        AgentRuntimeInfo agentRuntimeInfo = new AgentRuntimeInfo(id, AgentRuntimeStatus.Idle, SystemUtil.currentWorkingDirectory(), "cookie", false);
        Assert.assertThat(agentRuntimeInfo.getUsableSpace(), Matchers.is(Matchers.not(0L)));
    }

    @Test
    public void shouldNotBeLowDiskSpaceForMissingAgent() {
        Assert.assertThat(AgentRuntimeInfo.initialState(new AgentConfig("uuid")).isLowDiskSpace(10L), Matchers.is(false));
    }

    @Test
    public void shouldReturnTrueIfUsableSpaceLessThanLimit() {
        AgentRuntimeInfo agentRuntimeInfo = AgentRuntimeInfo.initialState(new AgentConfig("uuid"));
        agentRuntimeInfo.setUsableSpace(10L);
        Assert.assertThat(agentRuntimeInfo.isLowDiskSpace(20L), Matchers.is(true));
    }

    @Test
    public void shouldHaveRelevantFieldsInDebugString() throws Exception {
        AgentRuntimeInfo agentRuntimeInfo = new AgentRuntimeInfo(new AgentIdentifier("localhost", "127.0.0.1", "uuid"), AgentRuntimeStatus.Idle, SystemUtil.currentWorkingDirectory(), "cookie", false);
        Assert.assertThat(agentRuntimeInfo.agentInfoDebugString(), Matchers.is("Agent [localhost, 127.0.0.1, uuid, cookie]"));
    }

    @Test
    public void shouldHaveBeautifulPhigureLikeDisplayString() throws Exception {
        AgentRuntimeInfo agentRuntimeInfo = new AgentRuntimeInfo(new AgentIdentifier("localhost", "127.0.0.1", "uuid"), AgentRuntimeStatus.Idle, SystemUtil.currentWorkingDirectory(), "cookie", false);
        agentRuntimeInfo.setLocation("/nim/appan/mane");
        Assert.assertThat(agentRuntimeInfo.agentInfoForDisplay(), Matchers.is("Agent located at [localhost, 127.0.0.1, /nim/appan/mane]"));
    }

    @Test
    public void shouldTellIfHasCookie() throws Exception {
        Assert.assertThat(new AgentRuntimeInfo(new AgentIdentifier("localhost", "127.0.0.1", "uuid"), AgentRuntimeStatus.Idle, SystemUtil.currentWorkingDirectory(), "cookie", false).hasDuplicateCookie("cookie"), Matchers.is(false));
        Assert.assertThat(new AgentRuntimeInfo(new AgentIdentifier("localhost", "127.0.0.1", "uuid"), AgentRuntimeStatus.Idle, SystemUtil.currentWorkingDirectory(), "cookie", false).hasDuplicateCookie("different"), Matchers.is(true));
        Assert.assertThat(new AgentRuntimeInfo(new AgentIdentifier("localhost", "127.0.0.1", "uuid"), AgentRuntimeStatus.Idle, SystemUtil.currentWorkingDirectory(), null, false).hasDuplicateCookie("cookie"), Matchers.is(false));
        Assert.assertThat(hasDuplicateCookie(null), Matchers.is(false));
    }

    @Test
    public void shouldUpdateSelfForAnIdleAgent() {
        AgentRuntimeInfo agentRuntimeInfo = new AgentRuntimeInfo(new AgentIdentifier("localhost", "127.0.0.1", "uuid"), AgentRuntimeStatus.Idle, SystemUtil.currentWorkingDirectory(), null, false);
        AgentRuntimeInfo newRuntimeInfo = new AgentRuntimeInfo(new AgentIdentifier("go02", "10.10.10.1", "uuid"), AgentRuntimeStatus.Idle, SystemUtil.currentWorkingDirectory(), "cookie", false);
        newRuntimeInfo.setBuildingInfo(new AgentBuildingInfo("Idle", ""));
        newRuntimeInfo.setLocation("home");
        newRuntimeInfo.setUsableSpace(10L);
        newRuntimeInfo.setOperatingSystem("Linux");
        agentRuntimeInfo.updateSelf(newRuntimeInfo);
        Assert.assertThat(agentRuntimeInfo.getBuildingInfo(), Matchers.is(newRuntimeInfo.getBuildingInfo()));
        Assert.assertThat(agentRuntimeInfo.getLocation(), Matchers.is(newRuntimeInfo.getLocation()));
        Assert.assertThat(agentRuntimeInfo.getUsableSpace(), Matchers.is(newRuntimeInfo.getUsableSpace()));
        Assert.assertThat(agentRuntimeInfo.getOperatingSystem(), Matchers.is(newRuntimeInfo.getOperatingSystem()));
    }

    @Test
    public void dataMapEncodingAndDecoding() {
        AgentRuntimeInfo info = new AgentRuntimeInfo(new AgentIdentifier("go02", "10.10.10.1", "uuid"), AgentRuntimeStatus.Idle, SystemUtil.currentWorkingDirectory(), "cookie", true);
        AgentRuntimeInfo clonedInfo = MessageEncoding.decodeData(MessageEncoding.encodeData(info), AgentRuntimeInfo.class);
        Assert.assertThat(clonedInfo, Matchers.is(info));
    }
}

