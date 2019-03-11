/**
 * ***********************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ************************GO-LICENSE-END**********************************
 */
package com.thoughtworks.go.util;


import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TempFilesTest {
    TempFiles files;

    private Properties original;

    @Test
    public void shouldRecordFilesThatAreCreated() throws IOException {
        File created = files.createFile("foo");
        Assert.assertThat(created.exists(), Matchers.is(true));
        files.cleanUp();
        Assert.assertThat(created.exists(), Matchers.is(false));
    }

    @Test
    public void shouldRecordFoldersThatAreCreated() {
        File dir = files.mkdir("foo");
        Assert.assertThat(dir.exists(), Matchers.is(true));
        files.cleanUp();
        Assert.assertThat(dir.exists(), Matchers.is(false));
    }

    @Test
    public void shouldDeleteNonEmptyFolders() throws IOException {
        File dir = files.mkdir("foo");
        Assert.assertThat(dir.exists(), Matchers.is(true));
        File file = new File(dir, "foo");
        file.createNewFile();
        files.cleanUp();
        Assert.assertThat(dir.exists(), Matchers.is(false));
    }

    @Test
    public void shouldForgetFolders() throws IOException {
        File file = files.mkdir("foo");
        files.cleanUp();
        files.cleanUp();
    }

    @Test
    public void shouldCreateFilesInTempDirectory() throws IOException {
        File file = files.createFile("foo");
        File parentFile = file.getParentFile();
        Assert.assertThat(parentFile.getName(), Matchers.is("cruise"));
        Assert.assertThat(parentFile.getParentFile(), Matchers.is(tmpDir()));
    }

    @Test
    public void shouldCreateDirsInTempDirectory() throws IOException {
        File dir = files.mkdir("foo");
        File parentFile = dir.getParentFile();
        Assert.assertThat(parentFile.getName(), Matchers.is("cruise"));
        Assert.assertThat(parentFile.getParentFile(), Matchers.is(tmpDir()));
    }

    @Test
    public void shouldCreateUniqueFilesEveryTime() throws IOException {
        TestingClock clock = new TestingClock();
        files.setClock(clock);
        File file1 = files.createUniqueFile("foo");
        File file2 = files.createUniqueFile("foo");
        Assert.assertThat(file1, Matchers.not(file2));
    }

    @Test
    public void shouldCreateUniqueFilesParentDirectoryIfDoesNotExist() throws IOException {
        String newTmpDir = ((original.getProperty("java.io.tmpdir")) + "/") + (UUID.randomUUID());
        System.setProperty("java.io.tmpdir", newTmpDir);
        File file = files.createUniqueFile("foo");
        Assert.assertThat(file.getParentFile().exists(), Matchers.is(true));
    }

    @Test
    public void shouldCreateUniqueFolders() throws IOException {
        TestingClock clock = new TestingClock();
        files.setClock(clock);
        File file1 = files.createUniqueFolder("foo");
        clock.addSeconds(1);
        File file2 = files.createUniqueFolder("foo");
        Assert.assertThat(file2, Matchers.not(file1));
    }

    @Test
    public void willNotDeleteParentDirectoriesIfPathologicalFilesGetCreated() throws IOException {
        File file1 = files.createFile("foo/bar/baz.zip");
        Assert.assertThat(file1.exists(), Matchers.is(true));
        files.cleanUp();
        Assert.assertThat(file1.exists(), Matchers.is(false));
        Assert.assertThat(new File(new File(tmpDir(), "cruise"), "foo").exists(), Matchers.is(true));
    }
}

