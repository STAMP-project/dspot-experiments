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
package com.thoughtworks.go.agent.bootstrapper;


import AgentBootstrapperArgs.SslMode;
import com.googlecode.junit.ext.checkers.OSChecker;
import com.thoughtworks.go.agent.testhelper.FakeGoServer;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class AgentBootstrapperFunctionalTest {
    @Rule
    public FakeGoServer server = new FakeGoServer();

    public static final OSChecker OS_CHECKER = new OSChecker(OSChecker.WINDOWS);

    @Test
    public void shouldCheckout_Bundled_agentLauncher() throws IOException {
        try {
            AGENT_LAUNCHER_JAR.delete();
            new AgentBootstrapper().validate();
            Assert.assertEquals("agent launcher from default files", FileUtils.readFileToString(AGENT_LAUNCHER_JAR, StandardCharsets.UTF_8).trim());
        } finally {
            AGENT_LAUNCHER_JAR.delete();
        }
    }

    @Test
    public void shouldLoadAndBootstrapJarUsingAgentBootstrapCode_specifiedInAgentManifestFile() throws Exception {
        if (!(AgentBootstrapperFunctionalTest.OS_CHECKER.satisfy())) {
            PrintStream err = System.err;
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                System.setErr(new PrintStream(os));
                File agentJar = new File("agent.jar");
                agentJar.delete();
                go(false, new com.thoughtworks.go.agent.common.AgentBootstrapperArgs(new URL(((("http://" + ("localhost" + ":")) + (server.getPort())) + "/go")), null, SslMode.NONE));
                agentJar.delete();
                Assert.assertThat(new String(os.toByteArray()), Matchers.containsString("Hello World Fellas!"));
            } finally {
                System.setErr(err);
            }
        }
    }

    @Test
    public void shouldDownloadJarIfItDoesNotExist() throws Exception {
        if (!(AgentBootstrapperFunctionalTest.OS_CHECKER.satisfy())) {
            File agentJar = new File("agent.jar");
            agentJar.delete();
            go(false, new com.thoughtworks.go.agent.common.AgentBootstrapperArgs(new URL(((("http://" + ("localhost" + ":")) + (server.getPort())) + "/go")), null, SslMode.NONE));
            Assert.assertTrue("No agent downloaded", agentJar.exists());
            agentJar.delete();
        }
    }

    @Test
    public void shouldDownloadJarIfTheCurrentOneIsWrong() throws Exception {
        if (!(AgentBootstrapperFunctionalTest.OS_CHECKER.satisfy())) {
            File agentJar = new File("agent.jar");
            agentJar.delete();
            createRandomFile(agentJar);
            long original = agentJar.length();
            go(false, new com.thoughtworks.go.agent.common.AgentBootstrapperArgs(new URL(((("http://" + ("localhost" + ":")) + (server.getPort())) + "/go")), null, SslMode.NONE));
            Assert.assertThat(agentJar.length(), Matchers.not(original));
            agentJar.delete();
        }
    }
}

