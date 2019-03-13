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
package com.thoughtworks.go.agent.launcher;


import AgentBootstrapperArgs.SERVER_URL;
import com.googlecode.junit.ext.checkers.OSChecker;
import com.thoughtworks.cruise.agent.common.launcher.AgentLaunchDescriptor;
import com.thoughtworks.cruise.agent.common.launcher.AgentLauncher;
import com.thoughtworks.go.CurrentGoCDVersion;
import com.thoughtworks.go.agent.ServerUrlGenerator;
import com.thoughtworks.go.agent.testhelper.FakeGoServer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;


public class AgentLauncherImplTest {
    @Rule
    public FakeGoServer server = new FakeGoServer();

    public static final OSChecker OS_CHECKER = new OSChecker(OSChecker.WINDOWS);

    @Test
    public void shouldPassLauncherVersionToAgent() throws IOException, InterruptedException {
        final List<String> actualVersion = new ArrayList<>();
        final AgentLauncher launcher = new AgentLauncherImpl(new AgentLauncherImpl.AgentProcessParentRunner() {
            public int run(String launcherVersion, String launcherMd5, ServerUrlGenerator urlConstructor, Map<String, String> environmentVariables, Map context) {
                actualVersion.add(launcherVersion);
                return 0;
            }
        });
        TEST_AGENT_LAUNCHER.copyTo(AGENT_LAUNCHER_JAR);
        launcher.launch(launchDescriptor());
        Assert.assertThat(actualVersion.size(), Matchers.is(1));
        Assert.assertThat(actualVersion.get(0), Matchers.is(CurrentGoCDVersion.getInstance().fullVersion()));
    }

    @Test
    public void shouldNotThrowException_instedReturnAppropriateErrorCode_whenSomethingGoesWrongInLaunch() {
        AgentLaunchDescriptor launchDesc = Mockito.mock(AgentLaunchDescriptor.class);
        Mockito.when(((String) (launchDesc.context().get(SERVER_URL)))).thenThrow(new RuntimeException("Ouch!"));
        try {
            Assert.assertThat(new AgentLauncherImpl().launch(launchDesc), Matchers.is((-273)));
        } catch (Exception e) {
            Assert.fail("should not have blown up, because it directly interfaces with bootstrapper");
        }
    }

    @Test
    public void shouldDownloadLauncherJarIfLocalCopyIsStale() throws IOException {
        // because new invocation will take care of pulling latest agent down, and will then operate on it with the latest launcher -jj
        File staleJar = randomFile(AGENT_LAUNCHER_JAR);
        long original = staleJar.length();
        new AgentLauncherImpl().launch(launchDescriptor());
        Assert.assertThat(staleJar.length(), Matchers.not(original));
    }

    @Test
    public void shouldDownload_AgentJar_IfTheCurrentJarIsStale() throws Exception {
        if (!(AgentLauncherImplTest.OS_CHECKER.satisfy())) {
            TEST_AGENT_LAUNCHER.copyTo(AGENT_LAUNCHER_JAR);
            File staleJar = randomFile(AGENT_BINARY_JAR);
            long original = staleJar.length();
            new AgentLauncherImpl().launch(launchDescriptor());
            Assert.assertThat(staleJar.length(), Matchers.not(original));
        }
    }

    @Test
    public void should_NOT_Download_AgentJar_IfTheCurrentJarIsUpToDate() throws Exception {
        if (!(AgentLauncherImplTest.OS_CHECKER.satisfy())) {
            TEST_AGENT_LAUNCHER.copyTo(AGENT_LAUNCHER_JAR);
            TEST_AGENT.copyTo(AGENT_BINARY_JAR);
            Assert.assertTrue(AGENT_BINARY_JAR.setLastModified(0));
            new AgentLauncherImpl().launch(launchDescriptor());
            Assert.assertThat(AGENT_BINARY_JAR.lastModified(), Matchers.is(0L));
        }
    }

    @Test
    public void should_NOT_Download_TfsImplJar_IfTheCurrentJarIsUpToDate() throws Exception {
        if (!(AgentLauncherImplTest.OS_CHECKER.satisfy())) {
            TEST_AGENT_LAUNCHER.copyTo(AGENT_LAUNCHER_JAR);
            TEST_AGENT.copyTo(AGENT_BINARY_JAR);
            TEST_TFS_IMPL.copyTo(TFS_IMPL_JAR);
            Assert.assertTrue(TFS_IMPL_JAR.setLastModified(0));
            new AgentLauncherImpl().launch(launchDescriptor());
            Assert.assertThat(TFS_IMPL_JAR.lastModified(), Matchers.is(0L));
        }
    }

    @Test
    public void shouldDownloadLauncherJarIfLocalCopyIsStale_butShouldReturnWithoutDownloadingOrLaunchingAgent() throws Exception {
        File launcher = randomFile(AGENT_LAUNCHER_JAR);
        long original = launcher.length();
        File agentFile = randomFile(AGENT_BINARY_JAR);
        long originalAgentLength = agentFile.length();
        new AgentLauncherImpl().launch(launchDescriptor());
        Assert.assertThat(launcher.length(), Matchers.not(original));
        Assert.assertThat(agentFile.length(), Matchers.is(originalAgentLength));
    }
}

