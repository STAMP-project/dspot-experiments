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
package com.thoughtworks.go.buildsession;


import com.thoughtworks.go.domain.JobResult;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class BuildSessionTest extends BuildSessionBasedTestCase {
    @Test
    public void resolveRelativeDir() throws IOException {
        BuildSession buildSession = newBuildSession();
        Assert.assertThat(buildSession.resolveRelativeDir("foo"), Matchers.is(new File(sandbox, "foo")));
        Assert.assertThat(buildSession.resolveRelativeDir("foo", "bar"), Matchers.is(new File(sandbox, "foo/bar")));
        Assert.assertThat(buildSession.resolveRelativeDir("", "bar"), Matchers.is(new File(sandbox, "bar")));
    }

    @Test
    public void echoCommandAppendContentToConsole() {
        runBuild(echo("o1o2"), JobResult.Passed);
        Assert.assertThat(console.asList(), Matchers.is(Collections.singletonList("o1o2")));
    }

    @Test
    public void testReportCurrentStatus() {
        runBuild(compose(reportCurrentStatus(Preparing), reportCurrentStatus(Building), reportCurrentStatus(Completing)), JobResult.Passed);
        Assert.assertThat(statusReporter.status(), Matchers.is(Arrays.asList(Preparing, Building, Completing, Completed)));
    }

    @Test
    public void testReportCompleting() {
        runBuild(reportCompleting(), JobResult.Passed);
        Assert.assertThat(statusReporter.results(), Matchers.is(Arrays.asList(JobResult.Passed, JobResult.Passed)));
    }

    @Test
    public void resultShouldBeFailedWhenCommandFailed() {
        runBuild(fail("force build failure"), JobResult.Failed);
        Assert.assertThat(statusReporter.singleResult(), Matchers.is(JobResult.Failed));
    }

    @Test
    public void forceBuildFailWithMessage() {
        runBuild(fail("force failure"), JobResult.Failed);
        Assert.assertThat(console.output(), Matchers.is("force failure"));
    }

    @Test
    public void composeRunAllSubCommands() {
        runBuild(compose(echo("hello"), echo("world")), JobResult.Passed);
        Assert.assertThat(console.asList(), Matchers.is(Arrays.asList("hello", "world")));
    }

    @Test
    public void shouldNotRunCommandWithRunIfFailedIfBuildIsPassing() {
        runBuild(compose(echo("on pass"), echo("on failure").runIf("failed")), JobResult.Passed);
        Assert.assertThat(console.asList(), Matchers.is(Collections.singletonList("on pass")));
    }

    @Test
    public void shouldRunCommandWithRunIfFailedIfBuildIsFailed() {
        runBuild(compose(fail("force failure"), echo("on failure").runIf("failed")), JobResult.Failed);
        Assert.assertThat(console.lastLine(), Matchers.is("on failure"));
    }

    @Test
    public void shouldRunCommandWithRunIfAnyRegardlessOfBuildResult() {
        runBuild(compose(echo("foo"), echo("on passing").runIf("any"), fail("force failure"), echo("on failure").runIf("any")), JobResult.Failed);
        Assert.assertThat(console.asList(), Matchers.is(Arrays.asList("foo", "on passing", "force failure", "on failure")));
    }

    @Test
    public void echoWithBuildVariableSubstitution() {
        runBuild(echo("hello ${test.foo}"), JobResult.Passed);
        Assert.assertThat(console.lastLine(), Matchers.is("hello ${test.foo}"));
        buildVariables.put("test.foo", "world");
        runBuild(echo("hello ${test.foo}"), JobResult.Passed);
        Assert.assertThat(console.lastLine(), Matchers.is("hello world"));
    }
}

