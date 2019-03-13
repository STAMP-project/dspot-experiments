/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.exec;


import ActionInputPrefetcher.NONE;
import BlazeTestStatus.FLAKY;
import Status.NON_ZERO_EXIT;
import Status.SUCCESS;
import TestLogHelper.HEADER_DELIMITER;
import TestStatus.FAILED;
import TestStatus.PASSED;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MoreCollectors;
import com.google.devtools.build.lib.actions.ActionContext;
import com.google.devtools.build.lib.actions.ActionExecutionContext;
import com.google.devtools.build.lib.actions.ActionKeyContext;
import com.google.devtools.build.lib.actions.Spawn;
import com.google.devtools.build.lib.actions.SpawnActionContext;
import com.google.devtools.build.lib.actions.SpawnResult;
import com.google.devtools.build.lib.analysis.test.TestResult;
import com.google.devtools.build.lib.analysis.test.TestRunnerAction;
import com.google.devtools.build.lib.analysis.util.BuildViewTestCase;
import com.google.devtools.build.lib.clock.BlazeClock;
import com.google.devtools.build.lib.clock.Clock;
import com.google.devtools.build.lib.events.ExtendedEventHandler;
import com.google.devtools.build.lib.events.StoredEventHandler;
import com.google.devtools.build.lib.exec.TestStrategy.TestOutputFormat;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.util.OS;
import com.google.devtools.build.lib.util.io.FileOutErr;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.common.options.Options;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static ExecutionOptions.DEFAULTS;


/**
 * Unit tests for {@link StandaloneTestStrategy}.
 */
@RunWith(JUnit4.class)
public final class StandaloneTestStrategyTest extends BuildViewTestCase {
    private static class TestedStandaloneTestStrategy extends StandaloneTestStrategy {
        TestResult postedResult = null;

        public TestedStandaloneTestStrategy(ExecutionOptions executionOptions, BinTools binTools, Path tmpDirRoot) {
            super(executionOptions, binTools, tmpDirRoot);
        }

        @Override
        protected void postTestResult(ActionExecutionContext actionExecutionContext, TestResult result) {
            postedResult = result;
        }
    }

    private class FakeActionExecutionContext extends ActionExecutionContext {
        private final SpawnActionContext spawnActionContext;

        public FakeActionExecutionContext(FileOutErr fileOutErr, SpawnActionContext spawnActionContext) {
            /* executor= */
            /* actionInputFileCache= */
            /* metadataHandler= */
            /* eventHandler= */
            /* clientEnv= */
            /* topLevelFilesets= */
            /* artifactExpander= */
            /* actionFileSystem= */
            /* skyframeDepsResult= */
            super(null, null, NONE, new ActionKeyContext(), null, fileOutErr, null, ImmutableMap.of(), ImmutableMap.of(), null, null, null);
            this.spawnActionContext = spawnActionContext;
        }

        @Override
        public Clock getClock() {
            return BlazeClock.instance();
        }

        @Override
        public <T extends ActionContext> T getContext(Class<? extends T> type) {
            return SpawnActionContext.class.equals(type) ? type.cast(spawnActionContext) : null;
        }

        @Override
        public ExtendedEventHandler getEventHandler() {
            return storedEvents;
        }

        @Override
        public Path getExecRoot() {
            return outputBase.getRelative("execroot");
        }

        @Override
        public ActionExecutionContext withFileOutErr(FileOutErr fileOutErr) {
            return new StandaloneTestStrategyTest.FakeActionExecutionContext(fileOutErr, spawnActionContext);
        }
    }

    @Mock
    private SpawnActionContext spawnActionContext;

    private StoredEventHandler storedEvents = new StoredEventHandler();

    @Test
    public void testRunTestOnce() throws Exception {
        ExecutionOptions executionOptions = DEFAULTS;
        Path tmpDirRoot = TestStrategy.getTmpRoot(rootDirectory, outputBase, executionOptions);
        BinTools binTools = BinTools.forUnitTesting(directories, analysisMock.getEmbeddedTools());
        StandaloneTestStrategyTest.TestedStandaloneTestStrategy standaloneTestStrategy = new StandaloneTestStrategyTest.TestedStandaloneTestStrategy(executionOptions, binTools, tmpDirRoot);
        // setup a test action
        scratch.file("standalone/simple_test.sh", "this does not get executed, it is mocked out");
        scratch.file("standalone/BUILD", "sh_test(", "    name = \"simple_test\",", "    size = \"small\",", "    srcs = [\"simple_test.sh\"],", ")");
        TestRunnerAction testRunnerAction = getTestAction("//standalone:simple_test");
        SpawnResult expectedSpawnResult = new SpawnResult.Builder().setStatus(SUCCESS).setWallTime(Duration.ofMillis(10)).setRunnerName("test").build();
        Mockito.when(spawnActionContext.exec(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(ImmutableList.of(expectedSpawnResult));
        ActionExecutionContext actionExecutionContext = new StandaloneTestStrategyTest.FakeActionExecutionContext(createTempOutErr(tmpDirRoot), spawnActionContext);
        // actual StandaloneTestStrategy execution
        List<SpawnResult> spawnResults = execute(testRunnerAction, actionExecutionContext, standaloneTestStrategy);
        assertThat(spawnResults).contains(expectedSpawnResult);
        TestResult result = standaloneTestStrategy.postedResult;
        assertThat(result).isNotNull();
        assertThat(result.isCached()).isFalse();
        assertThat(result.getTestAction()).isSameAs(testRunnerAction);
        assertThat(result.getData().getTestPassed()).isTrue();
        assertThat(result.getData().getRemotelyCached()).isFalse();
        assertThat(result.getData().getIsRemoteStrategy()).isFalse();
        assertThat(result.getData().getRunDurationMillis()).isEqualTo(10);
        assertThat(result.getData().getTestTimesList()).containsExactly(10L);
        TestAttempt attempt = storedEvents.getPosts().stream().filter(TestAttempt.class::isInstance).map(TestAttempt.class::cast).collect(MoreCollectors.onlyElement());
        assertThat(attempt.getExecutionInfo().getStrategy()).isEqualTo("test");
        assertThat(attempt.getExecutionInfo().getHostname()).isEqualTo("");
    }

    @Test
    public void testRunFlakyTest() throws Exception {
        ExecutionOptions executionOptions = Options.getDefaults(ExecutionOptions.class);
        // TODO(ulfjack): Update this test for split xml generation.
        executionOptions.splitXmlGeneration = false;
        Path tmpDirRoot = TestStrategy.getTmpRoot(rootDirectory, outputBase, executionOptions);
        BinTools binTools = BinTools.forUnitTesting(directories, analysisMock.getEmbeddedTools());
        StandaloneTestStrategyTest.TestedStandaloneTestStrategy standaloneTestStrategy = new StandaloneTestStrategyTest.TestedStandaloneTestStrategy(executionOptions, binTools, tmpDirRoot);
        // setup a test action
        scratch.file("standalone/simple_test.sh", "this does not get executed, it is mocked out");
        scratch.file("standalone/BUILD", "sh_test(", "    name = \"simple_test\",", "    size = \"small\",", "    srcs = [\"simple_test.sh\"],", "    flaky = True,", ")");
        TestRunnerAction testRunnerAction = getTestAction("//standalone:simple_test");
        SpawnResult failSpawnResult = new SpawnResult.Builder().setStatus(NON_ZERO_EXIT).setExitCode(1).setWallTime(Duration.ofMillis(10)).setRunnerName("test").build();
        SpawnResult passSpawnResult = new SpawnResult.Builder().setStatus(SUCCESS).setWallTime(Duration.ofMillis(15)).setRunnerName("test").build();
        Mockito.when(spawnActionContext.exec(ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(new SpawnExecException("test failed", failSpawnResult, false)).thenReturn(ImmutableList.of(passSpawnResult));
        ActionExecutionContext actionExecutionContext = new StandaloneTestStrategyTest.FakeActionExecutionContext(createTempOutErr(tmpDirRoot), spawnActionContext);
        // actual StandaloneTestStrategy execution
        List<SpawnResult> spawnResults = execute(testRunnerAction, actionExecutionContext, standaloneTestStrategy);
        assertThat(spawnResults).containsExactly(failSpawnResult, passSpawnResult).inOrder();
        TestResult result = standaloneTestStrategy.postedResult;
        assertThat(result).isNotNull();
        assertThat(result.isCached()).isFalse();
        assertThat(result.getTestAction()).isSameAs(testRunnerAction);
        assertThat(result.getData().getStatus()).isEqualTo(FLAKY);
        assertThat(result.getData().getTestPassed()).isTrue();
        assertThat(result.getData().getRemotelyCached()).isFalse();
        assertThat(result.getData().getIsRemoteStrategy()).isFalse();
        assertThat(result.getData().getRunDurationMillis()).isEqualTo(15L);
        assertThat(result.getData().getTestTimesList()).containsExactly(10L, 15L);
        List<TestAttempt> attempts = storedEvents.getPosts().stream().filter(TestAttempt.class::isInstance).map(TestAttempt.class::cast).collect(ImmutableList.toImmutableList(ImmutableList));
        assertThat(attempts).hasSize(2);
        TestAttempt failedAttempt = attempts.get(0);
        assertThat(failedAttempt.getExecutionInfo().getStrategy()).isEqualTo("test");
        assertThat(failedAttempt.getExecutionInfo().getHostname()).isEqualTo("");
        assertThat(failedAttempt.getStatus()).isEqualTo(FAILED);
        assertThat(failedAttempt.getExecutionInfo().getCachedRemotely()).isFalse();
        TestAttempt okAttempt = attempts.get(1);
        assertThat(okAttempt.getStatus()).isEqualTo(PASSED);
        assertThat(okAttempt.getExecutionInfo().getStrategy()).isEqualTo("test");
        assertThat(okAttempt.getExecutionInfo().getHostname()).isEqualTo("");
    }

    @Test
    public void testRunTestRemotely() throws Exception {
        ExecutionOptions executionOptions = DEFAULTS;
        Path tmpDirRoot = TestStrategy.getTmpRoot(rootDirectory, outputBase, executionOptions);
        BinTools binTools = BinTools.forUnitTesting(directories, analysisMock.getEmbeddedTools());
        StandaloneTestStrategyTest.TestedStandaloneTestStrategy standaloneTestStrategy = new StandaloneTestStrategyTest.TestedStandaloneTestStrategy(executionOptions, binTools, tmpDirRoot);
        // setup a test action
        scratch.file("standalone/simple_test.sh", "this does not get executed, it is mocked out");
        scratch.file("standalone/BUILD", "sh_test(", "    name = \"simple_test\",", "    size = \"small\",", "    srcs = [\"simple_test.sh\"],", ")");
        TestRunnerAction testRunnerAction = getTestAction("//standalone:simple_test");
        SpawnResult expectedSpawnResult = new SpawnResult.Builder().setStatus(SUCCESS).setWallTime(Duration.ofMillis(10)).setRunnerName("remote").setExecutorHostname("a-remote-host").build();
        Mockito.when(spawnActionContext.exec(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(ImmutableList.of(expectedSpawnResult));
        ActionExecutionContext actionExecutionContext = new StandaloneTestStrategyTest.FakeActionExecutionContext(createTempOutErr(tmpDirRoot), spawnActionContext);
        // actual StandaloneTestStrategy execution
        List<SpawnResult> spawnResults = execute(testRunnerAction, actionExecutionContext, standaloneTestStrategy);
        assertThat(spawnResults).contains(expectedSpawnResult);
        TestResult result = standaloneTestStrategy.postedResult;
        assertThat(result).isNotNull();
        assertThat(result.isCached()).isFalse();
        assertThat(result.getTestAction()).isSameAs(testRunnerAction);
        assertThat(result.getData().getTestPassed()).isTrue();
        assertThat(result.getData().getRemotelyCached()).isFalse();
        assertThat(result.getData().getIsRemoteStrategy()).isTrue();
        assertThat(result.getData().getRunDurationMillis()).isEqualTo(10);
        assertThat(result.getData().getTestTimesList()).containsExactly(10L);
        TestAttempt attempt = storedEvents.getPosts().stream().filter(TestAttempt.class::isInstance).map(TestAttempt.class::cast).collect(MoreCollectors.onlyElement());
        assertThat(attempt.getStatus()).isEqualTo(PASSED);
        assertThat(attempt.getExecutionInfo().getStrategy()).isEqualTo("remote");
        assertThat(attempt.getExecutionInfo().getHostname()).isEqualTo("a-remote-host");
    }

    @Test
    public void testRunRemotelyCachedTest() throws Exception {
        ExecutionOptions executionOptions = DEFAULTS;
        Path tmpDirRoot = TestStrategy.getTmpRoot(rootDirectory, outputBase, executionOptions);
        BinTools binTools = BinTools.forUnitTesting(directories, analysisMock.getEmbeddedTools());
        StandaloneTestStrategyTest.TestedStandaloneTestStrategy standaloneTestStrategy = new StandaloneTestStrategyTest.TestedStandaloneTestStrategy(executionOptions, binTools, tmpDirRoot);
        // setup a test action
        scratch.file("standalone/simple_test.sh", "this does not get executed, it is mocked out");
        scratch.file("standalone/BUILD", "sh_test(", "    name = \"simple_test\",", "    size = \"small\",", "    srcs = [\"simple_test.sh\"],", ")");
        TestRunnerAction testRunnerAction = getTestAction("//standalone:simple_test");
        SpawnResult expectedSpawnResult = new SpawnResult.Builder().setStatus(SUCCESS).setCacheHit(true).setWallTime(Duration.ofMillis(10)).setRunnerName("remote cache").build();
        Mockito.when(spawnActionContext.exec(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(ImmutableList.of(expectedSpawnResult));
        ActionExecutionContext actionExecutionContext = new StandaloneTestStrategyTest.FakeActionExecutionContext(createTempOutErr(tmpDirRoot), spawnActionContext);
        // actual StandaloneTestStrategy execution
        List<SpawnResult> spawnResults = execute(testRunnerAction, actionExecutionContext, standaloneTestStrategy);
        // check that the rigged SpawnResult was returned
        assertThat(spawnResults).contains(expectedSpawnResult);
        TestResult result = standaloneTestStrategy.postedResult;
        assertThat(result).isNotNull();
        assertThat(result.isCached()).isFalse();
        assertThat(result.getTestAction()).isSameAs(testRunnerAction);
        assertThat(result.getData().getTestPassed()).isTrue();
        assertThat(result.getData().getRemotelyCached()).isTrue();
        assertThat(result.getData().getIsRemoteStrategy()).isFalse();
        assertThat(result.getData().getRunDurationMillis()).isEqualTo(10);
        assertThat(result.getData().getTestTimesList()).containsExactly(10L);
        TestAttempt attempt = storedEvents.getPosts().stream().filter(TestAttempt.class::isInstance).map(TestAttempt.class::cast).collect(MoreCollectors.onlyElement());
        assertThat(attempt.getExecutionInfo().getStrategy()).isEqualTo("remote cache");
        assertThat(attempt.getExecutionInfo().getHostname()).isEqualTo("");
    }

    @Test
    public void testThatTestLogAndOutputAreReturned() throws Exception {
        ExecutionOptions executionOptions = Options.getDefaults(ExecutionOptions.class);
        executionOptions.testOutput = TestOutputFormat.ERRORS;
        Path tmpDirRoot = TestStrategy.getTmpRoot(rootDirectory, outputBase, executionOptions);
        BinTools binTools = BinTools.forUnitTesting(directories, analysisMock.getEmbeddedTools());
        StandaloneTestStrategyTest.TestedStandaloneTestStrategy standaloneTestStrategy = new StandaloneTestStrategyTest.TestedStandaloneTestStrategy(executionOptions, binTools, tmpDirRoot);
        // setup a test action
        scratch.file("standalone/failing_test.sh", "this does not get executed, it is mocked out");
        scratch.file("standalone/BUILD", "sh_test(", "    name = \"failing_test\",", "    size = \"small\",", "    srcs = [\"failing_test.sh\"],", ")");
        TestRunnerAction testRunnerAction = getTestAction("//standalone:failing_test");
        SpawnResult expectedSpawnResult = new SpawnResult.Builder().setStatus(NON_ZERO_EXIT).setExitCode(1).setRunnerName("test").build();
        Mockito.when(spawnActionContext.exec(ArgumentMatchers.any(), ArgumentMatchers.any())).thenAnswer(( invocation) -> {
            Spawn spawn = invocation.getArgumentAt(0, .class);
            if ((spawn.getOutputFiles().size()) != 1) {
                FileOutErr outErr = invocation.getArgumentAt(1, .class).getFileOutErr();
                try (OutputStream stream = outErr.getOutputStream()) {
                    stream.write("This will not appear in the test output: bla\n".getBytes(StandardCharsets.UTF_8));
                    stream.write((TestLogHelper.HEADER_DELIMITER + "\n").getBytes(StandardCharsets.UTF_8));
                    stream.write("This will appear in the test output: foo\n".getBytes(StandardCharsets.UTF_8));
                }
                throw /* forciblyRunRemotely= */
                /* catastrophe= */
                new SpawnExecException("Failure!!", expectedSpawnResult, false, false);
            } else {
                return ImmutableList.of(new SpawnResult.Builder().setStatus(Status.SUCCESS).setRunnerName("test").build());
            }
        });
        FileOutErr outErr = createTempOutErr(tmpDirRoot);
        ActionExecutionContext actionExecutionContext = new StandaloneTestStrategyTest.FakeActionExecutionContext(outErr, spawnActionContext);
        // actual StandaloneTestStrategy execution
        List<SpawnResult> spawnResults = execute(testRunnerAction, actionExecutionContext, standaloneTestStrategy);
        // check that the rigged SpawnResult was returned
        assertThat(spawnResults).contains(expectedSpawnResult);
        // check that the test log contains all the output
        try {
            String logData = FileSystemUtils.readContent(testRunnerAction.getTestLog().getPath(), StandardCharsets.UTF_8);
            assertThat(logData).contains("bla");
            assertThat(logData).contains(HEADER_DELIMITER);
            assertThat(logData).contains("foo");
        } catch (IOException e) {
            Assert.fail(("Test log missing: " + (testRunnerAction.getTestLog().getPath())));
        }
        // check that the test stdout contains all the expected output
        outErr.close();// Create the output files.

        try {
            String outData = FileSystemUtils.readContent(outErr.getOutputPath(), StandardCharsets.UTF_8);
            assertThat(outData).contains("==================== Test output for //standalone:failing_test:");
            assertThat(outData).doesNotContain("bla");
            assertThat(outData).doesNotContain(HEADER_DELIMITER);
            assertThat(outData).contains("foo");
            assertThat(outData).contains("================================================================================");
        } catch (IOException e) {
            Assert.fail(("Test stdout file missing: " + (outErr.getOutputPath())));
        }
        assertThat(outErr.getErrorPath().exists()).isFalse();
    }

    @Test
    public void testThatTestLogAndOutputAreReturnedWithSplitXmlGeneration() throws Exception {
        ExecutionOptions executionOptions = Options.getDefaults(ExecutionOptions.class);
        executionOptions.testOutput = TestOutputFormat.ERRORS;
        executionOptions.splitXmlGeneration = true;
        Path tmpDirRoot = TestStrategy.getTmpRoot(rootDirectory, outputBase, executionOptions);
        BinTools binTools = BinTools.forUnitTesting(directories, analysisMock.getEmbeddedTools());
        StandaloneTestStrategyTest.TestedStandaloneTestStrategy standaloneTestStrategy = new StandaloneTestStrategyTest.TestedStandaloneTestStrategy(executionOptions, binTools, tmpDirRoot);
        // setup a test action
        scratch.file("standalone/failing_test.sh", "this does not get executed, it is mocked out");
        scratch.file("standalone/BUILD", "sh_test(", "    name = \"failing_test\",", "    size = \"small\",", "    srcs = [\"failing_test.sh\"],", ")");
        TestRunnerAction testRunnerAction = getTestAction("//standalone:failing_test");
        SpawnResult testSpawnResult = new SpawnResult.Builder().setStatus(NON_ZERO_EXIT).setExitCode(1).setRunnerName("test").build();
        SpawnResult xmlGeneratorSpawnResult = new SpawnResult.Builder().setStatus(SUCCESS).setRunnerName("test").build();
        List<FileOutErr> called = new ArrayList<>();
        Mockito.when(spawnActionContext.exec(ArgumentMatchers.any(), ArgumentMatchers.any())).thenAnswer(( invocation) -> {
            Spawn spawn = invocation.getArgumentAt(0, .class);
            FileOutErr outErr = invocation.getArgumentAt(1, .class).getFileOutErr();
            called.add(outErr);
            if ((spawn.getOutputFiles().size()) != 1) {
                try (OutputStream stream = outErr.getOutputStream()) {
                    stream.write("This will not appear in the test output: bla\n".getBytes(StandardCharsets.UTF_8));
                    stream.write((TestLogHelper.HEADER_DELIMITER + "\n").getBytes(StandardCharsets.UTF_8));
                    stream.write("This will appear in the test output: foo\n".getBytes(StandardCharsets.UTF_8));
                }
                throw /* forciblyRunRemotely= */
                /* catastrophe= */
                new SpawnExecException("Failure!!", testSpawnResult, false, false);
            } else {
                String testName = ((OS.getCurrent()) == OS.WINDOWS) ? "standalone/failing_test.exe" : "standalone/failing_test";
                assertThat(spawn.getEnvironment()).containsEntry("TEST_BINARY", testName);
                return ImmutableList.of(xmlGeneratorSpawnResult);
            }
        });
        FileOutErr outErr = createTempOutErr(tmpDirRoot);
        ActionExecutionContext actionExecutionContext = new StandaloneTestStrategyTest.FakeActionExecutionContext(outErr, spawnActionContext);
        // actual StandaloneTestStrategy execution
        List<SpawnResult> spawnResults = execute(testRunnerAction, actionExecutionContext, standaloneTestStrategy);
        // check that the rigged SpawnResult was returned
        assertThat(spawnResults).containsExactly(testSpawnResult, xmlGeneratorSpawnResult);
        // check that the test log contains all the output
        String logData = FileSystemUtils.readContent(testRunnerAction.getTestLog().getPath(), StandardCharsets.UTF_8);
        assertThat(logData).contains("bla");
        assertThat(logData).contains(HEADER_DELIMITER);
        assertThat(logData).contains("foo");
        // check that the test stdout contains all the expected output
        outErr.close();// Create the output files.

        String outData = FileSystemUtils.readContent(outErr.getOutputPath(), StandardCharsets.UTF_8);
        assertThat(outData).contains("==================== Test output for //standalone:failing_test:");
        assertThat(outData).doesNotContain("bla");
        assertThat(outData).doesNotContain(HEADER_DELIMITER);
        assertThat(outData).contains("foo");
        assertThat(outData).contains("================================================================================");
        assertThat(outErr.getErrorPath().exists()).isFalse();
        assertThat(called).hasSize(2);
        assertThat(called).containsNoDuplicates();
    }

    @Test
    public void testEmptyOutputCreatesEmptyLogFile() throws Exception {
        ExecutionOptions executionOptions = Options.getDefaults(ExecutionOptions.class);
        executionOptions.testOutput = TestOutputFormat.ALL;
        Path tmpDirRoot = TestStrategy.getTmpRoot(rootDirectory, outputBase, executionOptions);
        BinTools binTools = BinTools.forUnitTesting(directories, analysisMock.getEmbeddedTools());
        StandaloneTestStrategyTest.TestedStandaloneTestStrategy standaloneTestStrategy = new StandaloneTestStrategyTest.TestedStandaloneTestStrategy(executionOptions, binTools, tmpDirRoot);
        // setup a test action
        scratch.file("standalone/empty_test.sh", "this does not get executed, it is mocked out");
        scratch.file("standalone/BUILD", "sh_test(", "    name = \"empty_test\",", "    size = \"small\",", "    srcs = [\"empty_test.sh\"],", ")");
        TestRunnerAction testRunnerAction = getTestAction("//standalone:empty_test");
        SpawnResult expectedSpawnResult = new SpawnResult.Builder().setStatus(SUCCESS).setRunnerName("test").build();
        Mockito.when(spawnActionContext.exec(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(ImmutableList.of(expectedSpawnResult));
        FileOutErr outErr = createTempOutErr(tmpDirRoot);
        ActionExecutionContext actionExecutionContext = new StandaloneTestStrategyTest.FakeActionExecutionContext(outErr, spawnActionContext);
        // actual StandaloneTestStrategy execution
        List<SpawnResult> spawnResults = execute(testRunnerAction, actionExecutionContext, standaloneTestStrategy);
        // check that the rigged SpawnResult was returned
        assertThat(spawnResults).contains(expectedSpawnResult);
        // check that the test log contains all the output
        try {
            String logData = FileSystemUtils.readContent(testRunnerAction.getTestLog().getPath(), StandardCharsets.UTF_8);
            assertThat(logData).isEmpty();
        } catch (IOException e) {
            Assert.fail(("Test log missing: " + (testRunnerAction.getTestLog().getPath())));
        }
        // check that the test stdout contains all the expected output
        outErr.close();// Create the output files.

        try {
            String outData = FileSystemUtils.readContent(outErr.getOutputPath(), StandardCharsets.UTF_8);
            String emptyOutput = "==================== Test output for //standalone:empty_test:(\\s)*" + "================================================================================(\\s)*";
            assertThat(outData).matches(emptyOutput);
        } catch (IOException e) {
            Assert.fail(("Test stdout file missing: " + (outErr.getOutputPath())));
        }
        assertThat(outErr.getErrorPath().exists()).isFalse();
    }
}

