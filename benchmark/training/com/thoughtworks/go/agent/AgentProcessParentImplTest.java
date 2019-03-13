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


import Level.ERROR;
import com.googlecode.junit.ext.checkers.OSChecker;
import com.thoughtworks.go.agent.testhelper.FakeGoServer;
import com.thoughtworks.go.util.LogFixture;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static AgentProcessParentImpl.GO_AGENT_STDERR_LOG;
import static AgentProcessParentImpl.GO_AGENT_STDOUT_LOG;


public class AgentProcessParentImplTest {
    @Rule
    public FakeGoServer server = new FakeGoServer();

    private static final OSChecker OS_CHECKER = new OSChecker(OSChecker.WINDOWS);

    private final File stderrLog = new File("logs", GO_AGENT_STDERR_LOG);

    private final File stdoutLog = new File("logs", GO_AGENT_STDOUT_LOG);

    @Test
    public void shouldStartSubprocessWithCommandLine() throws IOException, InterruptedException {
        final List<String> cmd = new ArrayList<>();
        String expectedAgentMd5 = TEST_AGENT.getMd5();
        String expectedAgentPluginsMd5 = TEST_AGENT_PLUGINS.getMd5();
        String expectedTfsMd5 = TEST_TFS_IMPL.getMd5();
        AgentProcessParentImpl bootstrapper = createBootstrapper(cmd);
        int returnCode = bootstrapper.run("launcher_version", "bar", getURLGenerator(), new HashMap(), context());
        Assert.assertThat(returnCode, Matchers.is(42));
        Assert.assertThat(cmd.toArray(new String[]{  }), Matchers.equalTo(new String[]{ ((((System.getProperty("java.home")) + (System.getProperty("file.separator"))) + "bin") + (System.getProperty("file.separator"))) + "java", "-Dagent.plugins.md5=" + expectedAgentPluginsMd5, "-Dagent.binary.md5=" + expectedAgentMd5, "-Dagent.launcher.md5=bar", "-Dagent.tfs.md5=" + expectedTfsMd5, "-jar", "agent.jar", "-serverUrl", ("https://localhost:" + (server.getSecurePort())) + "/go/", "-sslVerificationMode", "NONE", "-rootCertFile", "/path/to/cert.pem" }));
    }

    @Test
    public void shouldAddAnyExtraPropertiesFoundToTheAgentInvocation() throws IOException, InterruptedException {
        final List<String> cmd = new ArrayList<>();
        String expectedAgentMd5 = TEST_AGENT.getMd5();
        String expectedAgentPluginsMd5 = TEST_AGENT_PLUGINS.getMd5();
        String expectedTfsMd5 = TEST_TFS_IMPL.getMd5();
        server.setExtraPropertiesHeaderValue("extra.property=value1%20with%20space extra%20property%20with%20space=value2%20with%20space");
        AgentProcessParentImpl bootstrapper = createBootstrapper(cmd);
        int returnCode = bootstrapper.run("launcher_version", "bar", getURLGenerator(), new HashMap(), context());
        Assert.assertThat(returnCode, Matchers.is(42));
        Assert.assertThat(cmd.toArray(new String[]{  }), Matchers.equalTo(new String[]{ ((((System.getProperty("java.home")) + (System.getProperty("file.separator"))) + "bin") + (System.getProperty("file.separator"))) + "java", "-Dextra.property=value1 with space", "-Dextra property with space=value2 with space", "-Dagent.plugins.md5=" + expectedAgentPluginsMd5, "-Dagent.binary.md5=" + expectedAgentMd5, "-Dagent.launcher.md5=bar", "-Dagent.tfs.md5=" + expectedTfsMd5, "-jar", "agent.jar", "-serverUrl", ("https://localhost:" + (server.getSecurePort())) + "/go/", "-sslVerificationMode", "NONE", "-rootCertFile", "/path/to/cert.pem" }));
    }

    @Test
    public void shouldStartSubprocess_withOverriddenArgs() throws IOException, InterruptedException {
        final List<String> cmd = new ArrayList<>();
        AgentProcessParentImpl bootstrapper = createBootstrapper(cmd);
        int returnCode = bootstrapper.run("launcher_version", "bar", getURLGenerator(), m(AgentProcessParentImpl.AGENT_STARTUP_ARGS, "foo bar  baz with%20some%20space"), context());
        String expectedAgentMd5 = TEST_AGENT.getMd5();
        String expectedAgentPluginsMd5 = TEST_AGENT_PLUGINS.getMd5();
        String expectedTfsMd5 = TEST_TFS_IMPL.getMd5();
        Assert.assertThat(returnCode, Matchers.is(42));
        Assert.assertThat(cmd.toArray(new String[]{  }), Matchers.equalTo(new String[]{ ((((System.getProperty("java.home")) + (System.getProperty("file.separator"))) + "bin") + (System.getProperty("file.separator"))) + "java", "foo", "bar", "baz", "with some space", "-Dagent.plugins.md5=" + expectedAgentPluginsMd5, "-Dagent.binary.md5=" + expectedAgentMd5, "-Dagent.launcher.md5=bar", "-Dagent.tfs.md5=" + expectedTfsMd5, "-jar", "agent.jar", "-serverUrl", ("https://localhost:" + (server.getSecurePort())) + "/go/", "-sslVerificationMode", "NONE", "-rootCertFile", "/path/to/cert.pem" }));
    }

    @Test
    public void shouldLogInterruptOnAgentProcess() throws InterruptedException {
        final List<String> cmd = new ArrayList<>();
        try (LogFixture logFixture = logFixtureFor(AgentProcessParentImpl.class, Level.DEBUG)) {
            Process subProcess = mockProcess();
            Mockito.when(subProcess.waitFor()).thenThrow(new InterruptedException("bang bang!"));
            AgentProcessParentImpl bootstrapper = createBootstrapper(cmd, subProcess);
            int returnCode = bootstrapper.run("bootstrapper_version", "bar", getURLGenerator(), new HashMap(), context());
            Assert.assertThat(returnCode, Matchers.is(0));
            Assert.assertThat(logFixture.contains(ERROR, "Agent was interrupted. Terminating agent and respawning. java.lang.InterruptedException: bang bang!"), Matchers.is(true));
            Mockito.verify(subProcess).destroy();
        }
    }

    // if it fails with timeout, that means stderr was not flushed -jj
    @Test(timeout = 10 * 1000)
    public void shouldLogErrorStreamOfSubprocess() throws IOException, InterruptedException {
        final List<String> cmd = new ArrayList<>();
        Process subProcess = mockProcess();
        String stdErrMsg = "Mr. Agent writes to stderr!";
        Mockito.when(subProcess.getErrorStream()).thenReturn(new ByteArrayInputStream(stdErrMsg.getBytes()));
        String stdOutMsg = "Mr. Agent writes to stdout!";
        Mockito.when(subProcess.getInputStream()).thenReturn(new ByteArrayInputStream(stdOutMsg.getBytes()));
        Mockito.when(subProcess.waitFor()).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return 42;
            }
        });
        AgentProcessParentImpl bootstrapper = createBootstrapper(cmd, subProcess);
        int returnCode = bootstrapper.run("bootstrapper_version", "bar", getURLGenerator(), new HashMap(), context());
        Assert.assertThat(returnCode, Matchers.is(42));
        Assert.assertThat(FileUtils.readFileToString(stderrLog, StandardCharsets.UTF_8).contains(stdErrMsg), Matchers.is(true));
        Assert.assertThat(FileUtils.readFileToString(stdoutLog, StandardCharsets.UTF_8).contains(stdOutMsg), Matchers.is(true));
    }

    @Test
    public void shouldLogFailureToStartSubprocess() throws InterruptedException {
        final List<String> cmd = new ArrayList<>();
        try (LogFixture logFixture = logFixtureFor(AgentProcessParentImpl.class, Level.DEBUG)) {
            AgentProcessParentImpl bootstrapper = new AgentProcessParentImpl() {
                @Override
                Process invoke(String[] command) throws IOException {
                    cmd.addAll(Arrays.asList(command));
                    throw new RuntimeException("something failed!");
                }
            };
            int returnCode = bootstrapper.run("bootstrapper_version", "bar", getURLGenerator(), new HashMap(), context());
            Assert.assertThat(returnCode, Matchers.is((-373)));
            Assert.assertThat(logFixture.contains(ERROR, (("Exception while executing command: " + (StringUtils.join(cmd, " "))) + " - java.lang.RuntimeException: something failed!")), Matchers.is(true));
        }
    }

    @Test
    public void shouldClose_STDIN_and_STDOUT_ofSubprocess() throws InterruptedException {
        final List<String> cmd = new ArrayList<>();
        final OutputStream stdin = Mockito.mock(OutputStream.class);
        Process subProcess = mockProcess(new ByteArrayInputStream(new byte[0]), new ByteArrayInputStream(new byte[0]), stdin);
        Mockito.when(subProcess.waitFor()).thenAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Mockito.verify(stdin).close();
                return 21;
            }
        });
        AgentProcessParentImpl bootstrapper = createBootstrapper(cmd, subProcess);
        int returnCode = bootstrapper.run("bootstrapper_version", "bar", getURLGenerator(), new HashMap(), context());
        Assert.assertThat(returnCode, Matchers.is(21));
    }

    @Test
    public void shouldNotDownloadPluginsZipIfPresent() throws Exception {
        if (!(AgentProcessParentImplTest.OS_CHECKER.satisfy())) {
            TEST_AGENT_PLUGINS.copyTo(AGENT_PLUGINS_ZIP);
            AGENT_PLUGINS_ZIP.setLastModified(((System.currentTimeMillis()) - (10 * 1000)));
            long expectedModifiedDate = AGENT_PLUGINS_ZIP.lastModified();
            AgentProcessParentImpl bootstrapper = createBootstrapper(new ArrayList<>());
            bootstrapper.run("launcher_version", "bar", getURLGenerator(), m(AgentProcessParentImpl.AGENT_STARTUP_ARGS, "foo bar  baz with%20some%20space"), context());
            Assert.assertThat(Downloader.AGENT_PLUGINS_ZIP.lastModified(), Matchers.is(expectedModifiedDate));
        }
    }

    @Test
    public void shouldDownloadPluginsZipIfMissing() throws Exception {
        if (!(AgentProcessParentImplTest.OS_CHECKER.satisfy())) {
            File stalePluginZip = randomFile(AGENT_PLUGINS_ZIP);
            long original = stalePluginZip.length();
            AgentProcessParentImpl bootstrapper = createBootstrapper(new ArrayList<>());
            bootstrapper.run("launcher_version", "bar", getURLGenerator(), m(AgentProcessParentImpl.AGENT_STARTUP_ARGS, "foo bar  baz with%20some%20space"), context());
            Assert.assertThat(stalePluginZip.length(), Matchers.not(original));
        }
    }

    @Test
    public void shouldDownload_TfsImplJar_IfTheCurrentJarIsStale() throws Exception {
        if (!(AgentProcessParentImplTest.OS_CHECKER.satisfy())) {
            File staleFile = randomFile(TFS_IMPL_JAR);
            long original = staleFile.length();
            AgentProcessParentImpl bootstrapper = createBootstrapper(new ArrayList<>());
            bootstrapper.run("launcher_version", "bar", getURLGenerator(), m(AgentProcessParentImpl.AGENT_STARTUP_ARGS, "foo bar  baz with%20some%20space"), context());
            Assert.assertThat(staleFile.length(), Matchers.not(original));
        }
    }
}

