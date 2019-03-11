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


import BlazeTestStatus.BLAZE_HALTED_BEFORE_TESTING;
import BlazeTestStatus.FAILED;
import BlazeTestStatus.FAILED_TO_BUILD;
import BlazeTestStatus.FLAKY;
import BlazeTestStatus.INCOMPLETE;
import BlazeTestStatus.PASSED;
import FailedTestCasesStatus.FULL;
import FailedTestCasesStatus.NOT_AVAILABLE;
import FailedTestCasesStatus.PARTIAL;
import TestCase.Status.ERROR;
import TestSummary.Builder;
import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.util.io.AnsiTerminalPrinter;
import com.google.devtools.build.lib.vfs.FileSystem;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.view.test.TestStatus.BlazeTestStatus;
import com.google.devtools.build.lib.view.test.TestStatus.TestCase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.AdditionalMatchers;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class TestSummaryTest {
    private static final String ANY_STRING = ".*?";

    private static final String PATH = "package";

    private static final String TARGET_NAME = "name";

    private ConfiguredTarget stubTarget;

    private static final ImmutableList<Long> SMALL_TIMING = ImmutableList.of(1L, 2L, 3L, 4L);

    private static final int CACHED = TestSummaryTest.SMALL_TIMING.size();

    private static final int NOT_CACHED = 0;

    private FileSystem fs;

    private Builder basicBuilder;

    @Test
    public void testShouldProperlyTestLabels() throws Exception {
        ConfiguredTarget target = target("somepath", "MyTarget");
        String expectedString = ((TestSummaryTest.ANY_STRING) + "//somepath:MyTarget") + (TestSummaryTest.ANY_STRING);
        AnsiTerminalPrinter terminalPrinter = Mockito.mock(AnsiTerminalPrinter.class);
        TestSummary summaryStatus = TestSummaryTest.createTestSummary(target, PASSED, TestSummaryTest.CACHED);
        TestSummaryPrinter.print(summaryStatus, terminalPrinter, Path::getPathString, true, false);
        terminalPrinter.print(AdditionalMatchers.find(expectedString));
    }

    @Test
    public void testShouldPrintPassedStatus() throws Exception {
        String expectedString = ((((TestSummaryTest.ANY_STRING) + "INFO") + (TestSummaryTest.ANY_STRING)) + (BlazeTestStatus.PASSED)) + (TestSummaryTest.ANY_STRING);
        AnsiTerminalPrinter terminalPrinter = Mockito.mock(AnsiTerminalPrinter.class);
        TestSummary summary = TestSummaryTest.createTestSummary(stubTarget, PASSED, TestSummaryTest.NOT_CACHED);
        TestSummaryPrinter.print(summary, terminalPrinter, Path::getPathString, true, false);
        Mockito.verify(terminalPrinter).print(AdditionalMatchers.find(expectedString));
    }

    @Test
    public void testShouldPrintFailedStatus() throws Exception {
        String expectedString = ((((TestSummaryTest.ANY_STRING) + "ERROR") + (TestSummaryTest.ANY_STRING)) + (BlazeTestStatus.FAILED)) + (TestSummaryTest.ANY_STRING);
        AnsiTerminalPrinter terminalPrinter = Mockito.mock(AnsiTerminalPrinter.class);
        TestSummary summary = TestSummaryTest.createTestSummary(stubTarget, FAILED, TestSummaryTest.NOT_CACHED);
        TestSummaryPrinter.print(summary, terminalPrinter, Path::getPathString, true, false);
        terminalPrinter.print(AdditionalMatchers.find(expectedString));
    }

    @Test
    public void testShouldNotPrintFailedToBuildStatus() throws Exception {
        assertShouldNotPrint(FAILED_TO_BUILD);
    }

    @Test
    public void testShouldNotPrintHaltedStatus() throws Exception {
        assertShouldNotPrint(BLAZE_HALTED_BEFORE_TESTING);
    }

    @Test
    public void testShouldPrintCachedStatus() throws Exception {
        String expectedString = ((TestSummaryTest.ANY_STRING) + "\\(cached") + (TestSummaryTest.ANY_STRING);
        AnsiTerminalPrinter terminalPrinter = Mockito.mock(AnsiTerminalPrinter.class);
        TestSummary summary = TestSummaryTest.createTestSummary(stubTarget, PASSED, TestSummaryTest.CACHED);
        TestSummaryPrinter.print(summary, terminalPrinter, Path::getPathString, true, false);
        terminalPrinter.print(AdditionalMatchers.find(expectedString));
    }

    @Test
    public void testPartialCachedStatus() throws Exception {
        String expectedString = ((TestSummaryTest.ANY_STRING) + "\\(3/4 cached") + (TestSummaryTest.ANY_STRING);
        AnsiTerminalPrinter terminalPrinter = Mockito.mock(AnsiTerminalPrinter.class);
        TestSummary summary = TestSummaryTest.createTestSummary(stubTarget, PASSED, ((TestSummaryTest.CACHED) - 1));
        TestSummaryPrinter.print(summary, terminalPrinter, Path::getPathString, true, false);
        terminalPrinter.print(AdditionalMatchers.find(expectedString));
    }

    @Test
    public void testIncompleteCached() throws Exception {
        AnsiTerminalPrinter terminalPrinter = Mockito.mock(AnsiTerminalPrinter.class);
        TestSummary summary = TestSummaryTest.createTestSummary(stubTarget, INCOMPLETE, ((TestSummaryTest.CACHED) - 1));
        TestSummaryPrinter.print(summary, terminalPrinter, Path::getPathString, true, false);
        Mockito.verify(terminalPrinter).print(AdditionalMatchers.not(ArgumentMatchers.contains("cached")));
    }

    @Test
    public void testShouldPrintUncachedStatus() throws Exception {
        AnsiTerminalPrinter terminalPrinter = Mockito.mock(AnsiTerminalPrinter.class);
        TestSummary summary = TestSummaryTest.createTestSummary(stubTarget, PASSED, TestSummaryTest.NOT_CACHED);
        TestSummaryPrinter.print(summary, terminalPrinter, Path::getPathString, true, false);
        Mockito.verify(terminalPrinter).print(AdditionalMatchers.not(ArgumentMatchers.contains("cached")));
    }

    @Test
    public void testNoTiming() throws Exception {
        String expectedString = (((TestSummaryTest.ANY_STRING) + "INFO") + (TestSummaryTest.ANY_STRING)) + (BlazeTestStatus.PASSED);
        AnsiTerminalPrinter terminalPrinter = Mockito.mock(AnsiTerminalPrinter.class);
        TestSummary summary = TestSummaryTest.createTestSummary(stubTarget, PASSED, TestSummaryTest.NOT_CACHED);
        TestSummaryPrinter.print(summary, terminalPrinter, Path::getPathString, true, false);
        terminalPrinter.print(AdditionalMatchers.find(expectedString));
    }

    @Test
    public void testBuilder() throws Exception {
        // No need to copy if built twice in a row; no direct setters on the object.
        TestSummary summary = basicBuilder.build();
        TestSummary sameSummary = basicBuilder.build();
        assertThat(sameSummary).isSameAs(summary);
        basicBuilder.addTestTimes(ImmutableList.of(40L));
        TestSummary summaryCopy = basicBuilder.build();
        assertThat(summaryCopy.getTarget()).isEqualTo(summary.getTarget());
        assertThat(summaryCopy.getStatus()).isEqualTo(summary.getStatus());
        assertThat(summaryCopy.numCached()).isEqualTo(summary.numCached());
        assertThat(summaryCopy).isNotSameAs(summary);
        assertThat(summary.totalRuns()).isEqualTo(0);
        assertThat(summaryCopy.totalRuns()).isEqualTo(1);
        // Check that the builder can add a new warning to the copy,
        // despite the immutability of the original.
        basicBuilder.addTestTimes(ImmutableList.of(60L));
        TestSummary fiftyCached = basicBuilder.setNumCached(50).build();
        assertThat(fiftyCached.getStatus()).isEqualTo(summary.getStatus());
        assertThat(fiftyCached.numCached()).isEqualTo(50);
        assertThat(fiftyCached.totalRuns()).isEqualTo(2);
        TestSummary sixtyCached = basicBuilder.setNumCached(60).build();
        assertThat(sixtyCached.numCached()).isEqualTo(60);
        assertThat(fiftyCached.numCached()).isEqualTo(50);
        TestSummary failedCacheTemplate = TestSummary.newBuilderFromExisting(fiftyCached).setStatus(FAILED).build();
        assertThat(failedCacheTemplate.numCached()).isEqualTo(50);
        assertThat(failedCacheTemplate.getStatus()).isEqualTo(FAILED);
        assertThat(failedCacheTemplate.getTotalTestCases()).isEqualTo(fiftyCached.getTotalTestCases());
    }

    @Test
    public void testSingleTime() throws Exception {
        String expectedString = (((((TestSummaryTest.ANY_STRING) + "INFO") + (TestSummaryTest.ANY_STRING)) + (BlazeTestStatus.PASSED)) + (TestSummaryTest.ANY_STRING)) + "in 3.4s";
        AnsiTerminalPrinter terminalPrinter = Mockito.mock(AnsiTerminalPrinter.class);
        TestSummary summary = basicBuilder.addTestTimes(ImmutableList.of(3412L)).build();
        TestSummaryPrinter.print(summary, terminalPrinter, Path::getPathString, true, false);
        terminalPrinter.print(AdditionalMatchers.find(expectedString));
    }

    @Test
    public void testNoTime() throws Exception {
        // The last part matches anything not containing "in".
        String expectedString = ((((TestSummaryTest.ANY_STRING) + "INFO") + (TestSummaryTest.ANY_STRING)) + (BlazeTestStatus.PASSED)) + "(?!in)*";
        AnsiTerminalPrinter terminalPrinter = Mockito.mock(AnsiTerminalPrinter.class);
        TestSummary summary = basicBuilder.addTestTimes(ImmutableList.of(3412L)).build();
        TestSummaryPrinter.print(summary, terminalPrinter, Path::getPathString, false, false);
        terminalPrinter.print(AdditionalMatchers.find(expectedString));
    }

    @Test
    public void testMultipleTimes() throws Exception {
        String expectedString = ((((((TestSummaryTest.ANY_STRING) + "INFO") + (TestSummaryTest.ANY_STRING)) + (BlazeTestStatus.PASSED)) + (TestSummaryTest.ANY_STRING)) + "\n  Stats over 3 runs: max = 3.0s, min = 1.0s, ") + "avg = 2.0s, dev = 0.8s";
        AnsiTerminalPrinter terminalPrinter = Mockito.mock(AnsiTerminalPrinter.class);
        TestSummary summary = basicBuilder.addTestTimes(ImmutableList.of(1000L, 2000L, 3000L)).build();
        TestSummaryPrinter.print(summary, terminalPrinter, Path::getPathString, true, false);
        terminalPrinter.print(AdditionalMatchers.find(expectedString));
    }

    @Test
    public void testCoverageDataReferences() throws Exception {
        List<Path> paths = getPathList("/cov1.dat", "/cov2.dat", "/cov3.dat", "/cov4.dat");
        FileSystemUtils.writeContentAsLatin1(paths.get(1), "something");
        FileSystemUtils.writeContentAsLatin1(paths.get(3), "");
        FileSystemUtils.writeContentAsLatin1(paths.get(3), "something else");
        TestSummary summary = basicBuilder.addCoverageFiles(paths).build();
        AnsiTerminalPrinter terminalPrinter = Mockito.mock(AnsiTerminalPrinter.class);
        TestSummaryPrinter.print(summary, terminalPrinter, Path::getPathString, true, false);
        Mockito.verify(terminalPrinter).print(AdditionalMatchers.find(((((TestSummaryTest.ANY_STRING) + "INFO") + (TestSummaryTest.ANY_STRING)) + (BlazeTestStatus.PASSED))));
        Mockito.verify(terminalPrinter).print(AdditionalMatchers.find("  /cov2.dat"));
        Mockito.verify(terminalPrinter).print(AdditionalMatchers.find("  /cov4.dat"));
    }

    @Test
    public void testFlakyAttempts() throws Exception {
        String expectedString = (((((TestSummaryTest.ANY_STRING) + "WARNING") + (TestSummaryTest.ANY_STRING)) + (BlazeTestStatus.FLAKY)) + (TestSummaryTest.ANY_STRING)) + ", failed in 2 out of 3";
        AnsiTerminalPrinter terminalPrinter = Mockito.mock(AnsiTerminalPrinter.class);
        TestSummary summary = basicBuilder.setStatus(FLAKY).addPassedLogs(getPathList("/a")).addFailedLogs(getPathList("/b", "/c")).build();
        TestSummaryPrinter.print(summary, terminalPrinter, Path::getPathString, true, false);
        terminalPrinter.print(AdditionalMatchers.find(expectedString));
    }

    @Test
    public void testNumberOfFailedRuns() throws Exception {
        String expectedString = (((((TestSummaryTest.ANY_STRING) + "ERROR") + (TestSummaryTest.ANY_STRING)) + (BlazeTestStatus.FAILED)) + (TestSummaryTest.ANY_STRING)) + "in 2 out of 3";
        AnsiTerminalPrinter terminalPrinter = Mockito.mock(AnsiTerminalPrinter.class);
        TestSummary summary = basicBuilder.setStatus(FAILED).addPassedLogs(getPathList("/a")).addFailedLogs(getPathList("/b", "/c")).build();
        TestSummaryPrinter.print(summary, terminalPrinter, Path::getPathString, true, false);
        terminalPrinter.print(AdditionalMatchers.find(expectedString));
    }

    @Test
    public void testFileNamesNotShown() throws Exception {
        List<TestCase> emptyDetails = ImmutableList.of();
        TestSummary summary = basicBuilder.setStatus(FAILED).addPassedLogs(getPathList("/apple")).addFailedLogs(getPathList("/pear")).addCoverageFiles(getPathList("/maracuja")).addFailedTestCases(emptyDetails, FULL).build();
        // Check that only //package:name is printed.
        AnsiTerminalPrinter printer = Mockito.mock(AnsiTerminalPrinter.class);
        TestSummaryPrinter.print(summary, printer, Path::getPathString, true, true);
        Mockito.verify(printer).print(ArgumentMatchers.contains("//package:name"));
    }

    @Test
    public void testMessageShownWhenTestCasesMissing() throws Exception {
        ImmutableList<TestCase> emptyList = ImmutableList.of();
        TestSummary summary = createTestSummaryWithDetails(FAILED, emptyList, NOT_AVAILABLE);
        AnsiTerminalPrinter printer = Mockito.mock(AnsiTerminalPrinter.class);
        TestSummaryPrinter.print(summary, printer, Path::getPathString, true, true);
        Mockito.verify(printer).print(ArgumentMatchers.contains("//package:name"));
        Mockito.verify(printer).print(ArgumentMatchers.contains("not available"));
    }

    @Test
    public void testMessageShownForPartialResults() throws Exception {
        ImmutableList<TestCase> testCases = ImmutableList.of(newDetail("orange", TestCase.Status.FAILED, 1500L));
        TestSummary summary = createTestSummaryWithDetails(FAILED, testCases, PARTIAL);
        AnsiTerminalPrinter printer = Mockito.mock(AnsiTerminalPrinter.class);
        TestSummaryPrinter.print(summary, printer, Path::getPathString, true, true);
        Mockito.verify(printer).print(ArgumentMatchers.contains("//package:name"));
        Mockito.verify(printer).print(AdditionalMatchers.find("FAILED.*orange"));
        Mockito.verify(printer).print(ArgumentMatchers.contains("incomplete"));
    }

    @Test
    public void testTestCaseNamesShownWhenNeeded() throws Exception {
        TestCase detailPassed = newDetail("strawberry", TestCase.Status.PASSED, 1000L);
        TestCase detailFailed = newDetail("orange", TestCase.Status.FAILED, 1500L);
        TestSummary summaryPassed = createTestSummaryWithDetails(PASSED, Arrays.asList(detailPassed));
        TestSummary summaryFailed = createTestSummaryWithDetails(FAILED, Arrays.asList(detailPassed, detailFailed));
        assertThat(summaryFailed.getStatus()).isEqualTo(FAILED);
        AnsiTerminalPrinter printerPassed = Mockito.mock(AnsiTerminalPrinter.class);
        TestSummaryPrinter.print(summaryPassed, printerPassed, Path::getPathString, true, true);
        Mockito.verify(printerPassed).print(ArgumentMatchers.contains("//package:name"));
        AnsiTerminalPrinter printerFailed = Mockito.mock(AnsiTerminalPrinter.class);
        TestSummaryPrinter.print(summaryFailed, printerFailed, Path::getPathString, true, true);
        Mockito.verify(printerFailed).print(ArgumentMatchers.contains("//package:name"));
        Mockito.verify(printerFailed).print(AdditionalMatchers.find("FAILED.*orange *\\(1\\.5"));
    }

    @Test
    public void testTestCaseNamesOrdered() throws Exception {
        TestCase[] details = new TestCase[]{ newDetail("apple", TestCase.Status.FAILED, 1000L), newDetail("banana", TestCase.Status.FAILED, 1000L), newDetail("cranberry", TestCase.Status.FAILED, 1000L) };
        // The exceedingly dumb approach: writing all the permutations down manually
        // is simply easier than any way of generating them.
        int[][] permutations = new int[][]{ new int[]{ 0, 1, 2 }, new int[]{ 0, 2, 1 }, new int[]{ 1, 0, 2 }, new int[]{ 1, 2, 0 }, new int[]{ 2, 0, 1 }, new int[]{ 2, 1, 0 } };
        for (int[] permutation : permutations) {
            List<TestCase> permutatedDetails = new ArrayList<>();
            for (int element : permutation) {
                permutatedDetails.add(details[element]);
            }
            TestSummary summary = createTestSummaryWithDetails(FAILED, permutatedDetails);
            // A mock that checks the ordering of method calls
            AnsiTerminalPrinter printer = Mockito.mock(AnsiTerminalPrinter.class);
            TestSummaryPrinter.print(summary, printer, Path::getPathString, true, true);
            InOrder order = Mockito.inOrder(printer);
            order.verify(printer).print(ArgumentMatchers.contains("//package:name"));
            order.verify(printer).print(AdditionalMatchers.find("FAILED.*apple"));
            order.verify(printer).print(AdditionalMatchers.find("FAILED.*banana"));
            order.verify(printer).print(AdditionalMatchers.find("FAILED.*cranberry"));
        }
    }

    @Test
    public void testCachedResultsFirstInSort() throws Exception {
        TestSummary summaryFailedCached = createTestSummary(FAILED, TestSummaryTest.CACHED);
        TestSummary summaryFailedNotCached = createTestSummary(FAILED, TestSummaryTest.NOT_CACHED);
        TestSummary summaryPassedCached = createTestSummary(PASSED, TestSummaryTest.CACHED);
        TestSummary summaryPassedNotCached = createTestSummary(PASSED, TestSummaryTest.NOT_CACHED);
        // This way we can make the test independent from the sort order of FAILEd
        // and PASSED.
        assertThat(summaryFailedCached.compareTo(summaryPassedNotCached)).isLessThan(0);
        assertThat(summaryPassedCached.compareTo(summaryFailedNotCached)).isLessThan(0);
    }

    @Test
    public void testCollectingFailedDetails() throws Exception {
        TestCase rootCase = TestCase.newBuilder().setName("tests").setRunDurationMillis(5000L).addChild(newDetail("apple", TestCase.Status.FAILED, 1000L)).addChild(newDetail("banana", TestCase.Status.PASSED, 1000L)).addChild(newDetail("cherry", ERROR, 1000L)).build();
        TestSummary summary = getTemplateBuilder().collectFailedTests(rootCase).setStatus(FAILED).build();
        AnsiTerminalPrinter printer = Mockito.mock(AnsiTerminalPrinter.class);
        TestSummaryPrinter.print(summary, printer, Path::getPathString, true, true);
        Mockito.verify(printer).print(ArgumentMatchers.contains("//package:name"));
        Mockito.verify(printer).print(AdditionalMatchers.find("FAILED.*apple"));
        Mockito.verify(printer).print(AdditionalMatchers.find("ERROR.*cherry"));
    }

    @Test
    public void countTotalTestCases() throws Exception {
        TestCase rootCase = TestCase.newBuilder().setName("tests").setRunDurationMillis(5000L).addChild(newDetail("apple", TestCase.Status.FAILED, 1000L)).addChild(newDetail("banana", TestCase.Status.PASSED, 1000L)).addChild(newDetail("cherry", ERROR, 1000L)).build();
        TestSummary summary = getTemplateBuilder().countTotalTestCases(rootCase).setStatus(FAILED).build();
        assertThat(summary.getTotalTestCases()).isEqualTo(3);
    }

    @Test
    public void countTotalTestCasesInNestedTree() throws Exception {
        TestCase aCase = TestCase.newBuilder().setName("tests-1").setRunDurationMillis(5000L).addChild(newDetail("apple", TestCase.Status.FAILED, 1000L)).addChild(newDetail("banana", TestCase.Status.PASSED, 1000L)).addChild(newDetail("cherry", ERROR, 1000L)).build();
        TestCase anotherCase = TestCase.newBuilder().setName("tests-2").setRunDurationMillis(5000L).addChild(newDetail("apple", TestCase.Status.FAILED, 1000L)).addChild(newDetail("banana", TestCase.Status.PASSED, 1000L)).addChild(newDetail("cherry", ERROR, 1000L)).build();
        TestCase rootCase = TestCase.newBuilder().setName("tests").addChild(aCase).addChild(anotherCase).build();
        TestSummary summary = getTemplateBuilder().countTotalTestCases(rootCase).setStatus(FAILED).build();
        assertThat(summary.getTotalTestCases()).isEqualTo(6);
    }
}

