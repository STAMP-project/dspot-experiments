/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.runtime;


import AnsiTerminalPrinter.Mode.ERROR;
import AnsiTerminalPrinter.Mode.INFO;
import com.google.devtools.build.lib.exec.TestStrategy.TestSummaryFormat;
import com.google.devtools.build.lib.util.io.AnsiTerminalPrinter;
import com.google.devtools.build.lib.view.test.TestStatus.BlazeTestStatus;
import com.google.devtools.common.options.OptionsParsingResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for {@link TerminalTestResultNotifier}.
 */
@RunWith(JUnit4.class)
public final class TerminalTestResultNotifierTest {
    private static final String ALL_TEST_CASES_PASSED_BUT_TARGET_FAILED_DISCLAIMER = "however note that at least one target failed";

    private final OptionsParsingResult optionsParsingResult = Mockito.mock(OptionsParsingResult.class);

    private final AnsiTerminalPrinter ansiTerminalPrinter = Mockito.mock(AnsiTerminalPrinter.class);

    private BlazeTestStatus targetStatus;

    private int numFailedTestCases;

    private int numTotalTestCases;

    private TestSummaryFormat testSummaryFormat;

    @Test
    public void testCaseOption_allPass() throws Exception {
        testSummaryFormat = TestSummaryFormat.TESTCASE;
        numFailedTestCases = 0;
        numTotalTestCases = 10;
        targetStatus = BlazeTestStatus.PASSED;
        printTestCaseSummary();
        String printed = getPrintedMessage();
        assertThat(printed).contains(TerminalTestResultNotifierTest.info("10 passing"));
        assertThat(printed).contains("0 failing");
        assertThat(printed).contains("out of 10 test cases");
        assertThat(printed).doesNotContain(TerminalTestResultNotifierTest.ALL_TEST_CASES_PASSED_BUT_TARGET_FAILED_DISCLAIMER);
        assertThat(printed).doesNotContain(ERROR.toString());
    }

    @Test
    public void testCaseOption_allPassButTargetFails() throws Exception {
        testSummaryFormat = TestSummaryFormat.TESTCASE;
        numFailedTestCases = 0;
        numTotalTestCases = 10;
        targetStatus = BlazeTestStatus.FAILED;
        printTestCaseSummary();
        String printed = getPrintedMessage();
        assertThat(printed).contains(TerminalTestResultNotifierTest.info("10 passing"));
        assertThat(printed).contains("0 failing");
        assertThat(printed).contains("out of 10 test cases");
        assertThat(printed).contains(TerminalTestResultNotifierTest.ALL_TEST_CASES_PASSED_BUT_TARGET_FAILED_DISCLAIMER);
        assertThat(printed).doesNotContain(ERROR.toString());
    }

    @Test
    public void testCaseOption_someFail() throws Exception {
        testSummaryFormat = TestSummaryFormat.TESTCASE;
        numFailedTestCases = 2;
        numTotalTestCases = 10;
        targetStatus = BlazeTestStatus.FAILED;
        printTestCaseSummary();
        String printed = getPrintedMessage();
        assertThat(printed).contains(TerminalTestResultNotifierTest.info("8 passing"));
        assertThat(printed).contains(TerminalTestResultNotifierTest.error("2 failing"));
        assertThat(printed).contains("out of 10 test cases");
        assertThat(printed).doesNotContain(TerminalTestResultNotifierTest.ALL_TEST_CASES_PASSED_BUT_TARGET_FAILED_DISCLAIMER);
    }

    @Test
    public void testCaseOption_allFail() throws Exception {
        testSummaryFormat = TestSummaryFormat.TESTCASE;
        numFailedTestCases = 10;
        numTotalTestCases = 10;
        targetStatus = BlazeTestStatus.FAILED;
        printTestCaseSummary();
        String printed = getPrintedMessage();
        assertThat(printed).contains("0 passing");
        assertThat(printed).contains(TerminalTestResultNotifierTest.error("10 failing"));
        assertThat(printed).contains("out of 10 test cases");
        assertThat(printed).doesNotContain(TerminalTestResultNotifierTest.ALL_TEST_CASES_PASSED_BUT_TARGET_FAILED_DISCLAIMER);
        assertThat(printed).doesNotContain(INFO.toString());
    }

    @Test
    public void detailedOption_allPass() throws Exception {
        testSummaryFormat = TestSummaryFormat.DETAILED;
        numFailedTestCases = 0;
        numTotalTestCases = 10;
        targetStatus = BlazeTestStatus.PASSED;
        printTestCaseSummary();
        String printed = getPrintedMessage();
        assertThat(printed).contains(TerminalTestResultNotifierTest.info("10 passing"));
        assertThat(printed).contains("0 failing");
        assertThat(printed).contains("out of 10 test cases");
        assertThat(printed).doesNotContain(TerminalTestResultNotifierTest.ALL_TEST_CASES_PASSED_BUT_TARGET_FAILED_DISCLAIMER);
        assertThat(printed).doesNotContain(ERROR.toString());
    }

    @Test
    public void detailedOption_allPassButTargetFails() throws Exception {
        testSummaryFormat = TestSummaryFormat.DETAILED;
        numFailedTestCases = 0;
        numTotalTestCases = 10;
        targetStatus = BlazeTestStatus.FAILED;
        printTestCaseSummary();
        String printed = getPrintedMessage();
        assertThat(printed).contains(TerminalTestResultNotifierTest.info("10 passing"));
        assertThat(printed).contains("0 failing");
        assertThat(printed).contains("out of 10 test cases");
        assertThat(printed).contains(TerminalTestResultNotifierTest.ALL_TEST_CASES_PASSED_BUT_TARGET_FAILED_DISCLAIMER);
        assertThat(printed).doesNotContain(ERROR.toString());
    }

    @Test
    public void detailedOption_someFail() throws Exception {
        testSummaryFormat = TestSummaryFormat.DETAILED;
        numFailedTestCases = 2;
        numTotalTestCases = 10;
        targetStatus = BlazeTestStatus.FAILED;
        printTestCaseSummary();
        String printed = getPrintedMessage();
        assertThat(printed).contains(TerminalTestResultNotifierTest.info("8 passing"));
        assertThat(printed).contains(TerminalTestResultNotifierTest.error("2 failing"));
        assertThat(printed).contains("out of 10 test cases");
        assertThat(printed).doesNotContain(TerminalTestResultNotifierTest.ALL_TEST_CASES_PASSED_BUT_TARGET_FAILED_DISCLAIMER);
    }

    @Test
    public void detailedOption_allFail() throws Exception {
        testSummaryFormat = TestSummaryFormat.DETAILED;
        numFailedTestCases = 10;
        numTotalTestCases = 10;
        targetStatus = BlazeTestStatus.FAILED;
        printTestCaseSummary();
        String printed = getPrintedMessage();
        assertThat(printed).contains("0 passing");
        assertThat(printed).contains(TerminalTestResultNotifierTest.error("10 failing"));
        assertThat(printed).contains("out of 10 test cases");
        assertThat(printed).doesNotContain(TerminalTestResultNotifierTest.ALL_TEST_CASES_PASSED_BUT_TARGET_FAILED_DISCLAIMER);
        assertThat(printed).doesNotContain(INFO.toString());
    }

    @Test
    public void shortOption_noSummaryPrinted() throws Exception {
        testSummaryFormat = TestSummaryFormat.SHORT;
        numFailedTestCases = 2;
        numTotalTestCases = 10;
        targetStatus = BlazeTestStatus.FAILED;
        printTestCaseSummary();
        verifyNoSummaryPrinted();
    }

    @Test
    public void terseOption_noSummaryPrinted() throws Exception {
        testSummaryFormat = TestSummaryFormat.TERSE;
        numFailedTestCases = 2;
        numTotalTestCases = 10;
        targetStatus = BlazeTestStatus.FAILED;
        printTestCaseSummary();
        verifyNoSummaryPrinted();
    }

    @Test
    public void noneOption_noSummaryPrinted() throws Exception {
        testSummaryFormat = TestSummaryFormat.NONE;
        numFailedTestCases = 2;
        numTotalTestCases = 10;
        targetStatus = BlazeTestStatus.FAILED;
        printTestCaseSummary();
        verifyNoSummaryPrinted();
    }
}

