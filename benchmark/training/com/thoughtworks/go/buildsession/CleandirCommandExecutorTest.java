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


import com.thoughtworks.go.domain.BuildCommand;
import com.thoughtworks.go.domain.JobResult;
import java.io.File;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class CleandirCommandExecutorTest extends BuildSessionBasedTestCase {
    @Test
    public void cleanDirWithoutAllows() throws IOException {
        runBuild(BuildCommand.mkdirs("foo/baz"), JobResult.Passed);
        Assert.assertTrue(new File(sandbox, "foo/file1").createNewFile());
        Assert.assertTrue(new File(sandbox, "file2").createNewFile());
        runBuild(BuildCommand.cleandir(""), JobResult.Passed);
        Assert.assertThat(sandbox.exists(), Matchers.is(true));
        Assert.assertThat(sandbox.listFiles().length, Matchers.is(0));
    }

    @Test
    public void cleanDirWithAllows() throws IOException {
        runBuild(BuildCommand.mkdirs("bar/foo/baz"), JobResult.Passed);
        runBuild(BuildCommand.mkdirs("bar/foo2"), JobResult.Passed);
        Assert.assertTrue(new File(sandbox, "bar/foo/file1").createNewFile());
        Assert.assertTrue(new File(sandbox, "bar/file2").createNewFile());
        Assert.assertTrue(new File(sandbox, "file3").createNewFile());
        runBuild(BuildCommand.cleandir("bar", "file2", "foo2"), JobResult.Passed);
        Assert.assertThat(new File(sandbox, "bar").isDirectory(), Matchers.is(true));
        Assert.assertThat(new File(sandbox, "file3").exists(), Matchers.is(true));
        Assert.assertThat(new File(sandbox, "bar").listFiles(), Matchers.arrayContainingInAnyOrder(new File(sandbox, "bar/file2"), new File(sandbox, "bar/foo2")));
    }

    @Test
    public void cleanDirWithAllowsAndWorkingDir() throws IOException {
        runBuild(BuildCommand.mkdirs("bar/foo/baz"), JobResult.Passed);
        runBuild(BuildCommand.mkdirs("bar/foo2"), JobResult.Passed);
        Assert.assertTrue(new File(sandbox, "bar/foo/file1").createNewFile());
        Assert.assertTrue(new File(sandbox, "bar/file2").createNewFile());
        Assert.assertTrue(new File(sandbox, "file3").createNewFile());
        runBuild(BuildCommand.cleandir("", "file2", "foo2").setWorkingDirectory("bar"), JobResult.Passed);
        Assert.assertThat(new File(sandbox, "bar").isDirectory(), Matchers.is(true));
        Assert.assertThat(new File(sandbox, "file3").exists(), Matchers.is(true));
        Assert.assertThat(new File(sandbox, "bar").listFiles(), Matchers.arrayContainingInAnyOrder(new File(sandbox, "bar/file2"), new File(sandbox, "bar/foo2")));
    }
}

