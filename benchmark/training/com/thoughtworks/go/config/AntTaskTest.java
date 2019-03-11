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


public class AntTaskTest {
    private AntTask antTask;

    @Test
    public void shouldRetainDoubleQuotesInDescription() {
        antTask.setTarget("\"foo bar\" baz \u2014debug");
        Assert.assertThat(antTask.describe(), Matchers.is("ant \"foo bar\" baz \u2014debug"));
    }

    @Test
    public void shouldRetainSingleQuotesInDescription() {
        antTask.setTarget("'foo bar' baz ?debug");
        Assert.assertThat(antTask.describe(), Matchers.is("ant 'foo bar' baz ?debug"));
    }

    @Test
    public void shouldNotSetTargetOnBuilderWhenNotSet() throws Exception {
        Assert.assertThat(antTask.arguments(), Matchers.is(""));
    }

    @Test
    public void shouldSetTargetOnBuilderWhenAvailable() throws Exception {
        String target = "target";
        antTask.setTarget(target);
        Assert.assertThat(antTask.arguments(), Matchers.is(target));
    }

    @Test
    public void shouldSetBuildFileWhenAvailable() throws Exception {
        String target = "target";
        String buildXml = "build.xml";
        antTask.setBuildFile(buildXml);
        antTask.setTarget(target);
        Assert.assertThat(antTask.arguments(), Matchers.is(((("-f \"" + buildXml) + "\" ") + target)));
        String distBuildXml = "build/dist.xml";
        antTask.setBuildFile(distBuildXml);
        Assert.assertThat(antTask.arguments(), Matchers.is(((("-f \"" + distBuildXml) + "\" ") + target)));
    }

    @Test
    public void describeTest() throws Exception {
        antTask.setBuildFile("build.xml");
        antTask.setTarget("test");
        antTask.setWorkingDirectory("lib");
        Assert.assertThat(antTask.describe(), Matchers.is("ant -f \"build.xml\" test (workingDirectory: lib)"));
    }

    @Test
    public void shouldReturnCommandAndWorkingDir() {
        antTask.setWorkingDirectory("lib");
        Assert.assertThat(antTask.command(), Matchers.is("ant"));
        Assert.assertThat(antTask.workingDirectory(), Matchers.is("lib"));
    }

    @Test
    public void shouldGiveArgumentsIncludingBuildfileAndTarget() {
        AntTask task = new AntTask();
        task.setBuildFile("build/build.xml");
        task.setTarget("compile");
        Assert.assertThat(task.arguments(), Matchers.is("-f \"build/build.xml\" compile"));
    }
}

