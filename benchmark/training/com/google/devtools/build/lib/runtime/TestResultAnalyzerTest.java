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


import TestSummary.Builder;
import com.google.devtools.build.lib.analysis.test.TestResult;
import com.google.devtools.build.lib.analysis.test.TestRunnerAction;
import com.google.devtools.build.lib.testutil.Suite;
import com.google.devtools.build.lib.testutil.TestSpec;
import com.google.devtools.build.lib.view.test.TestStatus.TestResultData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@TestSpec(size = Suite.SMALL_TESTS)
@RunWith(JUnit4.class)
public class TestResultAnalyzerTest {
    private TestResultAnalyzer underTest;

    @Test
    public void testIncrementalAnalyzeSetsActionRanTrueWhenThereAreNonCachedResults() {
        TestSummary.Builder summaryBuilder = makeTestSummaryBuilder();
        assertThat(summaryBuilder.peek().actionRan()).isFalse();
        TestResultData testResultData = TestResultData.newBuilder().setRemotelyCached(false).build();
        TestResult result = /* cached= */
        new TestResult(Mockito.mock(TestRunnerAction.class), testResultData, false);
        TestSummary.Builder newSummaryBuilder = underTest.incrementalAnalyze(summaryBuilder, result);
        assertThat(newSummaryBuilder.peek().actionRan()).isTrue();
    }

    @Test
    public void testIncrementalAnalyzeSetsActionRanFalseForLocallyCachedTests() {
        TestSummary.Builder summaryBuilder = makeTestSummaryBuilder();
        assertThat(summaryBuilder.peek().actionRan()).isFalse();
        TestResultData testResultData = TestResultData.newBuilder().setRemotelyCached(false).build();
        TestResult result = /* cached= */
        new TestResult(Mockito.mock(TestRunnerAction.class), testResultData, true);
        TestSummary.Builder newSummaryBuilder = underTest.incrementalAnalyze(summaryBuilder, result);
        assertThat(newSummaryBuilder.peek().actionRan()).isFalse();
    }

    @Test
    public void testIncrementalAnalyzeSetsActionRanFalseForRemotelyCachedTests() {
        TestSummary.Builder summaryBuilder = makeTestSummaryBuilder();
        assertThat(summaryBuilder.peek().actionRan()).isFalse();
        TestResultData testResultData = TestResultData.newBuilder().setRemotelyCached(true).build();
        TestResult result = /* cached= */
        new TestResult(Mockito.mock(TestRunnerAction.class), testResultData, false);
        TestSummary.Builder newSummaryBuilder = underTest.incrementalAnalyze(summaryBuilder, result);
        assertThat(newSummaryBuilder.peek().actionRan()).isFalse();
    }

    @Test
    public void testIncrementalAnalyzeKeepsActionRanTrueWhenAlreadyTrueAndNewCachedResults() {
        TestSummary.Builder summaryBuilder = makeTestSummaryBuilder().setActionRan(true);
        TestResultData testResultData = TestResultData.newBuilder().setRemotelyCached(true).build();
        TestResult result = /* cached= */
        new TestResult(Mockito.mock(TestRunnerAction.class), testResultData, true);
        TestSummary.Builder newSummaryBuilder = underTest.incrementalAnalyze(summaryBuilder, result);
        assertThat(newSummaryBuilder.peek().actionRan()).isTrue();
    }
}

