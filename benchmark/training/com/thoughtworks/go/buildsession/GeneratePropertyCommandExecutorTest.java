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
import com.thoughtworks.go.domain.BuildCommand;
import java.io.File;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class GeneratePropertyCommandExecutorTest extends BuildSessionBasedTestCase {
    private static final String TEST_PROPERTY = "test_property";

    @Test
    public void shouldReportFailureWhenArtifactFileDoesNotExist() throws IOException {
        runBuild(BuildCommand.generateProperty(GeneratePropertyCommandExecutorTest.TEST_PROPERTY, "not-exists.xml", "//src"), Passed);
        Assert.assertThat(console.output(), Matchers.containsString("Failed to create property"));
        Assert.assertThat(console.output(), Matchers.containsString(new File(sandbox, "not-exists.xml").getAbsolutePath()));
        Assert.assertThat(artifactsRepository.propertyValue(GeneratePropertyCommandExecutorTest.TEST_PROPERTY), Matchers.nullValue());
    }

    @Test
    public void shouldReportNotingMatchedWhenNoNodeCanMatch() throws IOException {
        createSrcFile("xmlfile");
        runBuild(BuildCommand.generateProperty(GeneratePropertyCommandExecutorTest.TEST_PROPERTY, "xmlfile", "//HTML"), Passed);
        Assert.assertThat(console.output(), Matchers.containsString("Failed to create property"));
        Assert.assertThat(console.output(), Matchers.containsString("Nothing matched xpath \"//HTML\""));
        Assert.assertThat(artifactsRepository.propertyValue(GeneratePropertyCommandExecutorTest.TEST_PROPERTY), Matchers.nullValue());
    }

    @Test
    public void shouldReportNotingMatchedWhenXPATHisNotValid() throws IOException {
        createSrcFile("xmlfile");
        runBuild(BuildCommand.generateProperty(GeneratePropertyCommandExecutorTest.TEST_PROPERTY, "xmlfile", "////////HTML"), Passed);
        Assert.assertThat(console.output(), Matchers.containsString("Failed to create property"));
        Assert.assertThat(console.output(), Matchers.containsString("Illegal xpath: \"////////HTML\""));
        Assert.assertThat(artifactsRepository.propertyValue(GeneratePropertyCommandExecutorTest.TEST_PROPERTY), Matchers.nullValue());
    }

    @Test
    public void shouldReportPropertyIsCreated() throws Exception {
        createSrcFile("xmlfile");
        runBuild(BuildCommand.generateProperty(GeneratePropertyCommandExecutorTest.TEST_PROPERTY, "xmlfile", "//buildplan/@name"), Passed);
        Assert.assertThat(console.output(), Matchers.containsString((("Property " + (GeneratePropertyCommandExecutorTest.TEST_PROPERTY)) + " = test created")));
        Assert.assertThat(artifactsRepository.propertyValue(GeneratePropertyCommandExecutorTest.TEST_PROPERTY), Matchers.is("test"));
    }

    @Test
    public void shouldReportFirstMatchedProperty() throws Exception {
        createSrcFile("xmlfile");
        runBuild(BuildCommand.generateProperty(GeneratePropertyCommandExecutorTest.TEST_PROPERTY, "xmlfile", "//artifact/@src"), Passed);
        Assert.assertThat(console.output(), Matchers.containsString((("Property " + (GeneratePropertyCommandExecutorTest.TEST_PROPERTY)) + " = target\\connectfour.jar created")));
        Assert.assertThat(artifactsRepository.propertyValue(GeneratePropertyCommandExecutorTest.TEST_PROPERTY), Matchers.is("target\\connectfour.jar"));
    }
}

