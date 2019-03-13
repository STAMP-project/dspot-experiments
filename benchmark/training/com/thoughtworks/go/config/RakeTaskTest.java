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
package com.thoughtworks.go.config;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class RakeTaskTest {
    @Test
    public void shouldReturnEmptyStringForDefault() throws Exception {
        RakeTask rakeTask = new RakeTask();
        Assert.assertThat(rakeTask.arguments(), Matchers.is(""));
    }

    @Test
    public void shouldContainBuildFileWhenDefined() throws Exception {
        RakeTask rakeTask = new RakeTask();
        rakeTask.setBuildFile("myrakefile.rb");
        Assert.assertThat(rakeTask.arguments(), Matchers.is("-f \"myrakefile.rb\""));
    }

    @Test
    public void shouldContainBuildFileAndTargetWhenBothDefined() throws Exception {
        RakeTask rakeTask = new RakeTask();
        rakeTask.setBuildFile("myrakefile.rb");
        rakeTask.setTarget("db:migrate VERSION=0");
        Assert.assertThat(rakeTask.arguments(), Matchers.is("-f \"myrakefile.rb\" db:migrate VERSION=0"));
    }

    @Test
    public void shouldUseRakeFileFromAnyDirectoryUnderRoot() throws Exception {
        RakeTask rakeTask = new RakeTask();
        String rakeFile = "build/myrakefile.rb";
        rakeTask.setBuildFile(rakeFile);
        rakeTask.setTarget("db:migrate VERSION=0");
        Assert.assertThat(rakeTask.arguments(), Matchers.is((("-f \"" + rakeFile) + "\" db:migrate VERSION=0")));
    }

    @Test
    public void describeTest() throws Exception {
        RakeTask rakeTask = new RakeTask();
        rakeTask.setBuildFile("myrakefile.rb");
        rakeTask.setTarget("db:migrate VERSION=0");
        rakeTask.setWorkingDirectory("lib");
        Assert.assertThat(rakeTask.describe(), Matchers.is("rake -f \"myrakefile.rb\" db:migrate VERSION=0 (workingDirectory: lib)"));
    }

    @Test
    public void shouldShowCommandName() {
        Assert.assertThat(new RakeTask().command(), Matchers.is("rake"));
    }

    @Test
    public void shouldGiveArgumentsForRakeTask() {
        RakeTask rakeTask = new RakeTask();
        rakeTask.setBuildFile("myrakefile.rb");
        rakeTask.setTarget("db:migrate VERSION=0");
        Assert.assertThat(rakeTask.arguments(), Matchers.is("-f \"myrakefile.rb\" db:migrate VERSION=0"));
    }
}

