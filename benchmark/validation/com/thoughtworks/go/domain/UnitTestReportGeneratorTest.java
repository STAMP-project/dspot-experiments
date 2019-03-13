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
package com.thoughtworks.go.domain;


import com.thoughtworks.go.domain.exception.ArtifactPublishingException;
import com.thoughtworks.go.work.DefaultGoPublisher;
import java.io.File;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class UnitTestReportGeneratorTest {
    private File testFolder;

    private UnitTestReportGenerator.UnitTestReportGenerator generator;

    private DefaultGoPublisher publisher;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldGenerateReportForNUnit() throws ArtifactPublishingException, IOException {
        copyAndClose(source("TestResult.xml"), target("test-result.xml"));
        final Properties properties = generator.generate(testFolder.listFiles(), "testoutput");
        Assert.assertThat(testFolder.listFiles().length, Matchers.is(2));
        Mockito.verify(publisher).upload(ArgumentMatchers.any(File.class), ArgumentMatchers.any(String.class));
        Mockito.verify(publisher).setProperty(new Property(TOTAL_TEST_COUNT, "206"));
        Mockito.verify(publisher).setProperty(new Property(FAILED_TEST_COUNT, "0"));
        Mockito.verify(publisher).setProperty(new Property(IGNORED_TEST_COUNT, "0"));
        Mockito.verify(publisher).setProperty(new Property(TEST_TIME, "NaN"));
    }

    @Test
    public void shouldGenerateReportForNUnitXmlWithByteOrderMark() throws ArtifactPublishingException, IOException {
        copyAndClose(source("NunitTestResultWithByteOrderMark.xml"), target("test-result.xml"));
        generator.generate(testFolder.listFiles(), "testoutput");
        Assert.assertThat(testFolder.listFiles().length, Matchers.is(2));
        Mockito.verify(publisher).upload(ArgumentMatchers.any(File.class), ArgumentMatchers.any(String.class));
        Mockito.verify(publisher).setProperty(new Property(TOTAL_TEST_COUNT, "18"));
        Mockito.verify(publisher).setProperty(new Property(FAILED_TEST_COUNT, "0"));
        Mockito.verify(publisher).setProperty(new Property(IGNORED_TEST_COUNT, "0"));
        Mockito.verify(publisher).setProperty(new Property(TEST_TIME, "1.570"));
    }

    @Test
    public void shouldNotGenerateAnyReportIfNoTestResultsWereFound() throws ArtifactPublishingException, IOException {
        generator.generate(testFolder.listFiles(), "testoutput");
        expectZeroedProperties();
    }

    @Test
    public void shouldNotGenerateAnyReportIfTestResultIsEmpty() throws ArtifactPublishingException, IOException {
        copyAndClose(source("empty.xml"), target("empty.xml"));
        generator.generate(testFolder.listFiles(), "testoutput");
        Mockito.verify(publisher).consumeLine("Ignoring file empty.xml - it is not a recognised test file.");
        Mockito.verify(publisher).setProperty(new Property(TOTAL_TEST_COUNT, "0"));
        Mockito.verify(publisher).setProperty(new Property(FAILED_TEST_COUNT, "0"));
        Mockito.verify(publisher).setProperty(new Property(IGNORED_TEST_COUNT, "0"));
        Mockito.verify(publisher).setProperty(new Property(TEST_TIME, "0.000"));
        Mockito.verify(publisher).upload(ArgumentMatchers.any(File.class), ArgumentMatchers.any(String.class));
    }

    @Test
    public void shouldNotGenerateAnyReportIfTestReportIsInvalid() throws ArtifactPublishingException, IOException {
        copyAndClose(source("InvalidTestResult.xml"), target("Invalid.xml"));
        generator.generate(testFolder.listFiles(), "testoutput");
        Mockito.verify(publisher).consumeLine("Ignoring file Invalid.xml - it is not a recognised test file.");
        Mockito.verify(publisher).setProperty(new Property(TOTAL_TEST_COUNT, "0"));
        Mockito.verify(publisher).setProperty(new Property(FAILED_TEST_COUNT, "0"));
        Mockito.verify(publisher).setProperty(new Property(IGNORED_TEST_COUNT, "0"));
        Mockito.verify(publisher).setProperty(new Property(TEST_TIME, "0.000"));
        Mockito.verify(publisher).upload(ArgumentMatchers.any(File.class), ArgumentMatchers.any(String.class));
    }

    // This is bug #2319
    @Test
    public void shouldStillUploadResultsIfReportIsIllegalBug2319() throws ArtifactPublishingException, IOException {
        copyAndClose(source("xml_samples/Coverage.xml"), target("Coverage.xml"));
        generator.generate(testFolder.listFiles(), "testoutput");
        Mockito.verify(publisher).consumeLine("Ignoring file Coverage.xml - it is not a recognised test file.");
        Mockito.verify(publisher).upload(ArgumentMatchers.any(File.class), ArgumentMatchers.any(String.class));
        Mockito.verify(publisher).setProperty(new Property(TOTAL_TEST_COUNT, "0"));
        Mockito.verify(publisher).setProperty(new Property(FAILED_TEST_COUNT, "0"));
        Mockito.verify(publisher).setProperty(new Property(IGNORED_TEST_COUNT, "0"));
        Mockito.verify(publisher).setProperty(new Property(TEST_TIME, "0.000"));
    }

    @Test
    public void shouldGenerateReportForJUnitAlso() throws ArtifactPublishingException, IOException {
        copyAndClose(source("SerializableProjectConfigUtilTest.xml"), target("AgentTest.xml"));
        generator.generate(testFolder.listFiles(), "testoutput");
        Mockito.verify(publisher).upload(ArgumentMatchers.any(File.class), ArgumentMatchers.any(String.class));
        Mockito.verify(publisher).setProperty(new Property(TOTAL_TEST_COUNT, "1"));
        Mockito.verify(publisher).setProperty(new Property(FAILED_TEST_COUNT, "0"));
        Mockito.verify(publisher).setProperty(new Property(IGNORED_TEST_COUNT, "0"));
        Mockito.verify(publisher).setProperty(new Property(TEST_TIME, "0.456"));
    }

    @Test
    public void shouldGenerateReportForJUnitWithMultipleFiles() throws ArtifactPublishingException, IOException {
        copyAndClose(source("UnitTestReportGeneratorTest.xml"), target("UnitTestReportGeneratorTest.xml"));
        copyAndClose(source("SerializableProjectConfigUtilTest.xml"), target("SerializableProjectConfigUtilTest.xml"));
        generator.generate(testFolder.listFiles(), "testoutput");
        Mockito.verify(publisher).upload(ArgumentMatchers.any(File.class), ArgumentMatchers.any(String.class));
        Mockito.verify(publisher).setProperty(new Property(TOTAL_TEST_COUNT, "5"));
        Mockito.verify(publisher).setProperty(new Property(FAILED_TEST_COUNT, "3"));
        Mockito.verify(publisher).setProperty(new Property(IGNORED_TEST_COUNT, "0"));
        Mockito.verify(publisher).setProperty(new Property(TEST_TIME, "1.286"));
    }

    @Test
    public void shouldGenerateReportForNUnitGivenMutipleInputFiles() throws ArtifactPublishingException, IOException {
        copyAndClose(source("TestReport-Integration.xml"), target("test-result1.xml"));
        copyAndClose(source("TestReport-Unit.xml"), target("test-result2.xml"));
        generator.generate(testFolder.listFiles(), "testoutput");
        Mockito.verify(publisher).upload(ArgumentMatchers.any(File.class), ArgumentMatchers.any(String.class));
        Mockito.verify(publisher).setProperty(new Property(TOTAL_TEST_COUNT, "2762"));
        Mockito.verify(publisher).setProperty(new Property(FAILED_TEST_COUNT, "0"));
        Mockito.verify(publisher).setProperty(new Property(IGNORED_TEST_COUNT, "120"));
        Mockito.verify(publisher).setProperty(new Property(TEST_TIME, "221.766"));
    }

    @Test
    public void shouldGenerateReportForXmlFilesRecursivelyInAFolder() throws ArtifactPublishingException, IOException {
        File reports = new File(testFolder.getAbsoluteFile(), "reports");
        reports.mkdir();
        File module = new File(reports, "module");
        module.mkdir();
        copyAndClose(source("xml_samples/Coverage.xml"), target("reports/module/Coverage.xml"));
        copyAndClose(source("xml_samples/TestResult.xml"), target("reports/TestResult.xml"));
        generator.generate(testFolder.listFiles(), "testoutput");
        Mockito.verify(publisher).consumeLine("Ignoring file Coverage.xml - it is not a recognised test file.");
        Mockito.verify(publisher).upload(ArgumentMatchers.any(File.class), ArgumentMatchers.any(String.class));
        Mockito.verify(publisher).setProperty(new Property(TOTAL_TEST_COUNT, "204"));
        Mockito.verify(publisher).setProperty(new Property(FAILED_TEST_COUNT, "0"));
        Mockito.verify(publisher).setProperty(new Property(IGNORED_TEST_COUNT, "6"));
        Mockito.verify(publisher).setProperty(new Property(TEST_TIME, "80.231"));
    }
}

