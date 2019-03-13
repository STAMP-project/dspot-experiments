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
package com.thoughtworks.go.util;


import java.io.File;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class FilenameUtilTest {
    @Test
    public void shouldReturnFalseIfGivenFolderIsAbsolute() {
        Assert.assertThat(FilenameUtil.isNormalizedPathOutsideWorkingDir("c:\\foo"), Matchers.is(false));
    }

    @Test
    public void shouldReturnFalseIfGivenFolderIsAbsoluteUnderLinux() {
        Assert.assertThat(FilenameUtil.isNormalizedPathOutsideWorkingDir("/tmp"), Matchers.is(false));
    }

    @Test
    public void shouldReturnFalseIfGivenFolderWithRelativeTakesYouOutOfSandbox() {
        Assert.assertThat(FilenameUtil.isNormalizedPathOutsideWorkingDir("../tmp"), Matchers.is(false));
        Assert.assertThat(FilenameUtil.isNormalizedPathOutsideWorkingDir("tmp/../../../pavan"), Matchers.is(false));
    }

    @Test
    public void shouldReturnTrueIfGivenFolderWithRelativeKeepsYouInsideSandbox() {
        Assert.assertThat(FilenameUtil.isNormalizedPathOutsideWorkingDir("tmp/../home/cruise"), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseEvenIfAnAbsolutePathKeepsYouInsideSandbox() {
        File file = new File("somethingInsideCurrentFolder");
        Assert.assertThat(FilenameUtil.isNormalizedPathOutsideWorkingDir(file.getAbsolutePath()), Matchers.is(false));
    }

    @Test
    public void shouldReturnFalseIfDirectoryNameIsSameAsParentDirectoryButNotASubdirectory() throws Exception {
        Assert.assertFalse(FilenameUtil.isNormalizedDirectoryPathInsideNormalizedParentDirectory("config", "artifacts/config"));
    }

    @Test
    public void shouldReturnTrueIfDirectoryIsSubdirectoryOfParent() throws Exception {
        Assert.assertTrue(FilenameUtil.isNormalizedDirectoryPathInsideNormalizedParentDirectory("artifacts", "artifacts/config"));
    }
}

