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
package com.thoughtworks.go.agent.common;


import AgentBootstrapperArgs.SslMode.NONE;
import AgentCLI.SystemExitter;
import java.io.ByteArrayOutputStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class AgentCLITest {
    private ByteArrayOutputStream errorStream;

    private AgentCLI agentCLI;

    private SystemExitter exitter;

    @Test
    public void shouldDieIfNoArguments() {
        try {
            agentCLI.parse();
            Assert.fail("Was expecting an exception!");
        } catch (AgentCLITest.ExitException e) {
            Assert.assertThat(e.getStatus(), Matchers.is(1));
            Assert.assertThat(errorStream.toString(), Matchers.containsString("The following option is required: [-serverUrl]"));
            Assert.assertThat(errorStream.toString(), Matchers.containsString("Usage: java -jar agent-bootstrapper.jar"));
        }
    }

    @Test
    public void serverURLMustBeAValidURL() throws Exception {
        try {
            agentCLI.parse("-serverUrl", "foobar");
            Assert.fail("Was expecting an exception!");
        } catch (AgentCLITest.ExitException e) {
            Assert.assertThat(e.getStatus(), Matchers.is(1));
            Assert.assertThat(errorStream.toString(), Matchers.containsString("-serverUrl is not a valid url"));
            Assert.assertThat(errorStream.toString(), Matchers.containsString("Usage: java -jar agent-bootstrapper.jar"));
        }
    }

    @Test
    public void serverURLMustBeSSL() throws Exception {
        try {
            agentCLI.parse("-serverUrl", "http://go.example.com:8154/go");
            Assert.fail("Was expecting an exception!");
        } catch (AgentCLITest.ExitException e) {
            Assert.assertThat(e.getStatus(), Matchers.is(1));
            Assert.assertThat(errorStream.toString(), Matchers.containsString("serverUrl must be an HTTPS url and must begin with https://"));
            Assert.assertThat(errorStream.toString(), Matchers.containsString("Usage: java -jar agent-bootstrapper.jar"));
        }
    }

    @Test
    public void shouldPassIfCorrectArgumentsAreProvided() throws Exception {
        AgentBootstrapperArgs agentBootstrapperArgs = agentCLI.parse("-serverUrl", "https://go.example.com:8154/go", "-sslVerificationMode", "NONE");
        Assert.assertThat(agentBootstrapperArgs.getServerUrl().toString(), Matchers.is("https://go.example.com:8154/go"));
        Assert.assertThat(agentBootstrapperArgs.getSslMode(), Matchers.is(NONE));
    }

    @Test
    public void shouldRaisExceptionWhenInvalidSslModeIsPassed() throws Exception {
        try {
            agentCLI.parse("-serverUrl", "https://go.example.com:8154/go", "-sslVerificationMode", "FOOBAR");
            Assert.fail("Was expecting an exception!");
        } catch (AgentCLITest.ExitException e) {
            Assert.assertThat(e.getStatus(), Matchers.is(1));
            Assert.assertThat(errorStream.toString(), Matchers.containsString("Invalid value for -sslVerificationMode parameter. Allowed values:[FULL, NONE, NO_VERIFY_HOST]"));
            Assert.assertThat(errorStream.toString(), Matchers.containsString("Usage: java -jar agent-bootstrapper.jar"));
        }
    }

    @Test
    public void shouldDefaultsTheSslModeToNONEWhenNotSpecified() throws Exception {
        AgentBootstrapperArgs agentBootstrapperArgs = agentCLI.parse("-serverUrl", "https://go.example.com/go");
        Assert.assertThat(agentBootstrapperArgs.getSslMode(), Matchers.is(NONE));
    }

    @Test
    public void printsHelpAndExitsWith0() throws Exception {
        try {
            agentCLI.parse("-help");
            Assert.fail("Was expecting an exception!");
        } catch (AgentCLITest.ExitException e) {
            Assert.assertThat(e.getStatus(), Matchers.is(0));
        }
    }

    class ExitException extends RuntimeException {
        private final int status;

        public ExitException(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }
}

