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
package com.thoughtworks.go.buildsession;


import Level.DEBUG;
import com.googlecode.junit.ext.JunitExtRunner;
import com.googlecode.junit.ext.RunIf;
import com.googlecode.junit.ext.checkers.OSChecker;
import com.thoughtworks.go.domain.JobResult;
import com.thoughtworks.go.junitext.EnhancedOSChecker;
import com.thoughtworks.go.util.LogFixture;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(JunitExtRunner.class)
public class ExecCommandExecutorTest extends BuildSessionBasedTestCase {
    @Test
    public void execExecuteExternalCommandAndConnectOutputToBuildConsole() {
        runBuild(exec("echo", "foo"), JobResult.Passed);
        Assert.assertThat(console.lastLine(), Matchers.is("foo"));
    }

    @Test
    public void execShouldFailIfWorkingDirectoryNotExists() {
        runBuild(exec("echo", "should not show").setWorkingDirectory("not-exists"), JobResult.Failed);
        Assert.assertThat(console.lineCount(), Matchers.is(1));
        Assert.assertThat(console.firstLine(), Matchers.containsString("not-exists\" is not a directory!"));
    }

    @Test
    public void execUseSystemEnvironmentVariables() {
        runBuild(execEchoEnv(pathSystemEnvName()), JobResult.Passed);
        Assert.assertThat(console.output(), Matchers.is(System.getenv(pathSystemEnvName())));
    }

    @Test
    public void execUsePresetEnvs() {
        BuildSession buildSession = newBuildSession();
        buildSession.setEnv("GO_SERVER_URL", "https://far.far.away/go");
        runBuild(buildSession, execEchoEnv("GO_SERVER_URL"), JobResult.Passed);
        Assert.assertThat(console.output(), Matchers.is("https://far.far.away/go"));
    }

    @Test
    public void execUseExportedEnv() throws IOException {
        runBuild(compose(export("foo", "bar", false), execEchoEnv("foo")), JobResult.Passed);
        Assert.assertThat(console.lastLine(), Matchers.is("bar"));
    }

    @Test
    public void execUseExportedEnvWithOverridden() throws Exception {
        runBuild(compose(export("answer", "2", false), export("answer", "42", false), execEchoEnv("answer")), JobResult.Passed);
        Assert.assertThat(console.lastLine(), Matchers.is("42"));
    }

    @Test
    public void execUseOverriddenSystemEnvValue() throws Exception {
        runBuild(compose(export(pathSystemEnvName(), "/foo/bar", false), execEchoEnv(pathSystemEnvName())), JobResult.Passed);
        Assert.assertThat(console.lastLine(), Matchers.is("/foo/bar"));
    }

    @Test
    @RunIf(value = EnhancedOSChecker.class, arguments = { DO_NOT_RUN_ON, WINDOWS })
    public void execExecuteNotExistExternalCommandOnUnix() {
        runBuild(exec("not-not-not-exist"), JobResult.Failed);
        Assert.assertThat(console.output(), printedAppsMissingInfoOnUnix("not-not-not-exist"));
    }

    @Test
    @RunIf(value = EnhancedOSChecker.class, arguments = { EnhancedOSChecker.WINDOWS })
    public void execExecuteNotExistExternalCommandOnWindows() {
        runBuild(exec("not-not-not-exist"), JobResult.Failed);
        Assert.assertThat(console.output(), printedAppsMissingInfoOnWindows("not-not-not-exist"));
    }

    @Test
    @RunIf(value = EnhancedOSChecker.class, arguments = { DO_NOT_RUN_ON, OSChecker.WINDOWS })
    public void shouldNotLeakSecretsToConsoleLog() {
        runBuild(compose(secret("topsecret"), exec("not-not-not-exist", "topsecret")), JobResult.Failed);
        Assert.assertThat(console.output(), Matchers.containsString("not-not-not-exist ******"));
        Assert.assertThat(console.output(), Matchers.not(Matchers.containsString("topsecret")));
    }

    @Test
    @RunIf(value = EnhancedOSChecker.class, arguments = { DO_NOT_RUN_ON, OSChecker.WINDOWS })
    public void shouldNotLeakSecretsToLog() {
        try (LogFixture logFixture = LogFixture.logFixtureFor(ExecCommandExecutor.class, DEBUG)) {
            runBuild(compose(secret("topsecret"), exec("not-not-not-exist", "topsecret")), JobResult.Failed);
            String logs = logFixture.getLog();
            Assert.assertThat(logs, Matchers.containsString("not-not-not-exist ******"));
            Assert.assertThat(logs, Matchers.not(Matchers.containsString("topsecret")));
        }
    }
}

