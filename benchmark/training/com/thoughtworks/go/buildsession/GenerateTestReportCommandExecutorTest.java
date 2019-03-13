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
import TestReportGenerator.FAILED_TEST_COUNT;
import TestReportGenerator.IGNORED_TEST_COUNT;
import TestReportGenerator.TEST_TIME;
import TestReportGenerator.TOTAL_TEST_COUNT;
import com.thoughtworks.go.domain.ArtifactsRepositoryStub;
import com.thoughtworks.go.domain.exception.ArtifactPublishingException;
import com.thoughtworks.go.util.TestUtils;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class GenerateTestReportCommandExecutorTest extends BuildSessionBasedTestCase {
    private File testFolder;

    @Test
    public void generateReportForNUnit() throws ArtifactPublishingException, IOException {
        TestUtils.copyAndClose(source("TestResult.xml"), target("test-result.xml"));
        runBuild(BuildCommand.generateTestReport(Collections.singletonList("test-reports/test-result.xml"), "test-out"), Passed);
        Assert.assertThat(artifactsRepository.getFileUploaded().size(), Matchers.is(1));
        Assert.assertThat(artifactsRepository.getFileUploaded().get(0).destPath, Matchers.is("test-out"));
        Assert.assertThat(artifactsRepository.propertyValue(TOTAL_TEST_COUNT), Matchers.is("206"));
        Assert.assertThat(artifactsRepository.propertyValue(FAILED_TEST_COUNT), Matchers.is("0"));
        Assert.assertThat(artifactsRepository.propertyValue(IGNORED_TEST_COUNT), Matchers.is("0"));
        Assert.assertThat(artifactsRepository.propertyValue(TEST_TIME), Matchers.is("NaN"));
    }
}

