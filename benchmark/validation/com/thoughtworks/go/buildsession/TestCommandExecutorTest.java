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


import JobResult.Passed;
import com.thoughtworks.go.domain.JobResult;
import java.io.File;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestCommandExecutorTest extends BuildSessionBasedTestCase {
    @Test
    public void testDirExists() throws IOException {
        runBuild(test("-d", ""), JobResult.Passed);
        runBuild(test("-d", "dir"), JobResult.Failed);
        runBuild(test("-d", "file"), JobResult.Failed);
        Assert.assertTrue(new File(sandbox, "file").createNewFile());
        Assert.assertTrue(new File(sandbox, "dir").mkdir());
        runBuild(test("-d", "file"), JobResult.Failed);
        runBuild(test("-d", "dir"), JobResult.Passed);
    }

    @Test
    public void testDirNotExists() throws IOException {
        runBuild(test("-nd", ""), JobResult.Failed);
        runBuild(test("-nd", "dir"), JobResult.Passed);
        runBuild(test("-nd", "file"), JobResult.Passed);
        Assert.assertTrue(new File(sandbox, "file").createNewFile());
        Assert.assertTrue(new File(sandbox, "dir").mkdir());
        runBuild(test("-nd", "file"), JobResult.Passed);
        runBuild(test("-nd", "dir"), JobResult.Failed);
    }

    @Test
    public void testFileExists() throws IOException {
        runBuild(test("-f", ""), JobResult.Failed);
        runBuild(test("-f", "file"), JobResult.Failed);
        File file = new File(sandbox, "file");
        Assert.assertTrue(file.createNewFile());
        runBuild(test("-f", "file"), JobResult.Passed);
    }

    @Test
    public void testFileNotExists() throws IOException {
        runBuild(test("-nf", ""), JobResult.Passed);
        runBuild(test("-nf", "file"), JobResult.Passed);
        File file = new File(sandbox, "file");
        Assert.assertTrue(file.createNewFile());
        runBuild(test("-nf", "file"), JobResult.Failed);
    }

    @Test
    public void testEqWithCommandOutput() throws IOException {
        runBuild(test("-eq", "foo", echo("foo")), JobResult.Passed);
        runBuild(test("-eq", "bar", echo("foo")), JobResult.Failed);
        Assert.assertThat(console.lineCount(), Matchers.is(0));
    }

    @Test
    public void testNotEqWithCommandOutput() throws IOException {
        runBuild(test("-neq", "foo", echo("foo")), JobResult.Failed);
        runBuild(test("-neq", "bar", echo("foo")), JobResult.Passed);
        Assert.assertThat(console.lineCount(), Matchers.is(0));
    }

    @Test
    public void testCommandOutputContainsString() throws IOException {
        runBuild(test("-in", "foo", echo("foo bar")), JobResult.Passed);
        runBuild(test("-in", "foo", echo("bar")), JobResult.Failed);
        Assert.assertThat(console.lineCount(), Matchers.is(0));
    }

    @Test
    public void testCommandOutputDoesNotContainsString() throws IOException {
        runBuild(test("-nin", "foo", echo("foo bar")), JobResult.Failed);
        runBuild(test("-nin", "foo", echo("bar")), JobResult.Passed);
        Assert.assertThat(console.lineCount(), Matchers.is(0));
    }

    @Test
    public void mkdirWithWorkingDir() {
        runBuild(mkdirs("foo").setWorkingDirectory("bar"), JobResult.Passed);
        Assert.assertThat(new File(sandbox, "bar/foo").isDirectory(), Matchers.is(true));
        Assert.assertThat(new File(sandbox, "foo").isDirectory(), Matchers.is(false));
    }

    @Test
    public void shouldNotFailBuildWhenTestCommandFail() {
        runBuild(echo("foo").setTest(fail("")), JobResult.Passed);
        Assert.assertThat(statusReporter.singleResult(), Matchers.is(JobResult.Passed));
    }

    @Test
    public void shouldNotFailBuildWhenComposedTestCommandFail() {
        runBuild(echo("foo").setTest(compose(echo(""), fail(""))), JobResult.Passed);
        Assert.assertThat(statusReporter.singleResult(), Matchers.is(Passed));
    }

    @Test
    public void shouldNotFailBuildWhenTestEqWithComposedCommandOutputFail() {
        runBuild(echo("foo").setTest(test("-eq", "42", compose(fail("42")))), JobResult.Passed);
        Assert.assertThat(statusReporter.singleResult(), Matchers.is(JobResult.Passed));
        Assert.assertThat(console.output(), Matchers.containsString("foo"));
    }
}

