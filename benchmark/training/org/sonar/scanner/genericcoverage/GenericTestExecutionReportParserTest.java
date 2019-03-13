/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.scanner.genericcoverage;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.test.MutableTestPlan;
import org.sonar.api.utils.MessageException;
import org.sonar.scanner.deprecated.test.TestPlanBuilder;


public class GenericTestExecutionReportParserTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private TestPlanBuilder testPlanBuilder;

    private DefaultInputFile fileWithBranches;

    private DefaultInputFile emptyFile;

    private SensorContextTester context;

    private MutableTestPlan testPlan;

    @Test
    public void ut_empty_file() throws Exception {
        addFileToFs(emptyFile);
        GenericTestExecutionReportParser parser = parseReportFile("unittest.xml");
        assertThat(parser.numberOfMatchedFiles()).isEqualTo(1);
        assertThat(parser.numberOfUnknownFiles()).isEqualTo(1);
        assertThat(parser.firstUnknownFiles()).hasSize(1);
    }

    @Test
    public void file_with_unittests() throws Exception {
        addFileToFs(fileWithBranches);
        GenericTestExecutionReportParser parser = parseReportFile("unittest2.xml");
        assertThat(parser.numberOfMatchedFiles()).isEqualTo(1);
        Mockito.verify(testPlan).addTestCase("test1");
        Mockito.verify(testPlan).addTestCase("test2");
        Mockito.verify(testPlan).addTestCase("test3");
    }

    @Test(expected = MessageException.class)
    public void unittest_invalid_root_node_name() throws Exception {
        parseUnitTestReport("<mycoverage version=\"1\"></mycoverage>");
    }

    @Test(expected = MessageException.class)
    public void unittest_invalid_report_version() throws Exception {
        parseUnitTestReport("<unitTest version=\"2\"></unitTest>");
    }

    @Test(expected = MessageException.class)
    public void unittest_duration_in_testCase_should_be_a_number() throws Exception {
        addFileToFs(setupFile("file1"));
        parseUnitTestReport(("<unitTest version=\"1\"><file path=\"file1\">" + "<testCase name=\"test1\" duration=\"aaa\"/></file></unitTest>"));
    }

    @Test(expected = MessageException.class)
    public void unittest_failure_should_have_a_message() throws Exception {
        addFileToFs(setupFile("file1"));
        parseUnitTestReport(("<unitTest version=\"1\"><file path=\"file1\">" + "<testCase name=\"test1\" duration=\"2\"><failure /></testCase></file></unitTest>"));
    }

    @Test(expected = MessageException.class)
    public void unittest_error_should_have_a_message() throws Exception {
        addFileToFs(setupFile("file1"));
        parseUnitTestReport(("<unitTest version=\"1\"><file path=\"file1\">" + "<testCase name=\"test1\" duration=\"2\"><error /></testCase></file></unitTest>"));
    }

    @Test(expected = MessageException.class)
    public void unittest_skipped_should_have_a_message() throws Exception {
        addFileToFs(setupFile("file1"));
        parseUnitTestReport(("<unitTest version=\"1\"><file path=\"file1\">" + "<testCase name=\"test1\" duration=\"2\"><skipped notmessage=\"\"/></testCase></file></unitTest>"));
    }

    @Test(expected = MessageException.class)
    public void unittest_duration_in_testCase_should_not_be_negative() throws Exception {
        addFileToFs(setupFile("file1"));
        parseUnitTestReport(("<unitTest version=\"1\"><file path=\"file1\">" + "<testCase name=\"test1\" duration=\"-5\"/></file></unitTest>"));
    }
}

