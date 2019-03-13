/**
 * Copyright 2016 The Bazel Authors. All rights reserved.
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


import BlazeTestStatus.FAILED;
import BlazeTestStatus.PASSED;
import ExitCode.SUCCESS;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.devtools.build.lib.actions.Action;
import com.google.devtools.build.lib.actions.ActionExecutionMetadata;
import com.google.devtools.build.lib.actions.ActionLookupData;
import com.google.devtools.build.lib.actions.ActionOwner;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.ArtifactRoot;
import com.google.devtools.build.lib.analysis.ConfiguredTarget;
import com.google.devtools.build.lib.bazel.repository.downloader.DownloadProgressEvent;
import com.google.devtools.build.lib.buildeventstream.BuildEventTransport;
import com.google.devtools.build.lib.buildtool.BuildResult;
import com.google.devtools.build.lib.buildtool.buildevent.TestFilteringCompleteEvent;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.events.ExtendedEventHandler.FetchProgress;
import com.google.devtools.build.lib.packages.AspectDescriptor;
import com.google.devtools.build.lib.skyframe.PackageProgressReceiver;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.ManualClock;
import com.google.devtools.build.lib.util.io.LoggingTerminalWriter;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import com.google.devtools.build.lib.vfs.Root;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests {@link ExperimentalStateTracker}.
 */
@RunWith(JUnit4.class)
public class ExperimentalStateTrackerTest extends FoundationTestCase {
    @Test
    public void testLoadingActivity() throws IOException {
        // During loading phase, state and activity, as reported by the PackageProgressReceiver,
        // should be visible in the progress bar.
        String state = "42 packages loaded";
        String activity = "currently loading //src/foo/bar and 17 more";
        PackageProgressReceiver progress = Mockito.mock(PackageProgressReceiver.class);
        Mockito.when(progress.progressState()).thenReturn(new com.google.devtools.build.lib.util.Pair<String, String>(state, activity));
        ManualClock clock = new ManualClock();
        ExperimentalStateTracker stateTracker = new ExperimentalStateTracker(clock);
        stateTracker.loadingStarted(new com.google.devtools.build.lib.skyframe.LoadingPhaseStartedEvent(progress));
        LoggingTerminalWriter terminalWriter = /* discardHighlight= */
        new LoggingTerminalWriter(true);
        stateTracker.writeProgressBar(terminalWriter);
        String output = terminalWriter.getTranscript();
        assertWithMessage(("Output should indicate that we are in the loading phase, but was:\n" + output)).that(output.contains("Loading")).isTrue();
        assertWithMessage(((("Output should contain loading state '" + state) + "\', but was:\n") + output)).that(output.contains(state)).isTrue();
        assertWithMessage(((("Output should contain loading state '" + activity) + "\', but was:\n") + output)).that(output.contains(activity)).isTrue();
    }

    @Test
    public void testActionVisible() throws IOException {
        // If there is only one action running, it should be visible
        // somewhere in the progress bar, and also the short version thereof.
        String message = "Building foo";
        ManualClock clock = new ManualClock();
        clock.advanceMillis(120000);
        ExperimentalStateTracker stateTracker = new ExperimentalStateTracker(clock);
        stateTracker.actionStarted(new com.google.devtools.build.lib.actions.ActionStartedEvent(mockAction(message, "bar/foo"), 123456789));
        LoggingTerminalWriter terminalWriter = /* discardHighlight= */
        new LoggingTerminalWriter(true);
        stateTracker.writeProgressBar(terminalWriter);
        String output = terminalWriter.getTranscript();
        assertWithMessage(((("Action message '" + message) + "' should be present in output: ") + output)).that(output.contains(message)).isTrue();
        terminalWriter = new LoggingTerminalWriter();
        /* shortVersion= */
        stateTracker.writeProgressBar(terminalWriter, true);
        output = terminalWriter.getTranscript();
        assertWithMessage(((("Action message '" + message) + "' should be present in short output: ") + output)).that(output.contains(message)).isTrue();
    }

    @Test
    public void testCompletedActionNotShown() throws IOException {
        // Completed actions should not be reported in the progress bar, nor in the
        // short progress bar.
        String messageFast = "Running quick action";
        String messageSlow = "Running slow action";
        ManualClock clock = new ManualClock();
        clock.advanceMillis(120000);
        Action fastAction = mockAction(messageFast, "foo/fast");
        Action slowAction = mockAction(messageSlow, "bar/slow");
        ExperimentalStateTracker stateTracker = new ExperimentalStateTracker(clock);
        stateTracker.actionStarted(new com.google.devtools.build.lib.actions.ActionStartedEvent(fastAction, 123456789));
        stateTracker.actionStarted(new com.google.devtools.build.lib.actions.ActionStartedEvent(slowAction, 123456999));
        stateTracker.actionCompletion(new com.google.devtools.build.lib.actions.ActionCompletionEvent(20, fastAction, Mockito.mock(ActionLookupData.class)));
        LoggingTerminalWriter terminalWriter = /* discardHighlight= */
        new LoggingTerminalWriter(true);
        stateTracker.writeProgressBar(terminalWriter);
        String output = terminalWriter.getTranscript();
        assertWithMessage(((("Completed action '" + messageFast) + "' should not be present in output: ") + output)).that(output.contains(messageFast)).isFalse();
        assertWithMessage(((("Only running action '" + messageSlow) + "' should be present in output: ") + output)).that(output.contains(messageSlow)).isTrue();
        terminalWriter = new LoggingTerminalWriter();
        /* shortVersion= */
        stateTracker.writeProgressBar(terminalWriter, true);
        output = terminalWriter.getTranscript();
        assertWithMessage(((("Completed action '" + messageFast) + "' should not be present in short output: ") + output)).that(output.contains(messageFast)).isFalse();
        assertWithMessage(((("Only running action '" + messageSlow) + "' should be present in short output: ") + output)).that(output.contains(messageSlow)).isTrue();
    }

    @Test
    public void testOldestActionVisible() throws IOException {
        // The earliest-started action is always visible somehow in the progress bar
        // and its short version.
        String messageOld = "Running the first-started action";
        ManualClock clock = new ManualClock();
        clock.advanceMillis(120000);
        ExperimentalStateTracker stateTracker = new ExperimentalStateTracker(clock);
        stateTracker.actionStarted(new com.google.devtools.build.lib.actions.ActionStartedEvent(mockAction(messageOld, "bar/foo"), 123456789));
        for (int i = 0; i < 30; i++) {
            stateTracker.actionStarted(new com.google.devtools.build.lib.actions.ActionStartedEvent(mockAction(("Other action " + i), ("some/other/actions/number" + i)), (123456790 + i)));
        }
        LoggingTerminalWriter terminalWriter = /* discardHighlight= */
        new LoggingTerminalWriter(true);
        stateTracker.writeProgressBar(terminalWriter);
        String output = terminalWriter.getTranscript();
        assertWithMessage(((("Longest running action '" + messageOld) + "' should be visible in output: ") + output)).that(output.contains(messageOld)).isTrue();
        terminalWriter = /* discardHighlight= */
        new LoggingTerminalWriter(true);
        /* shortVersion= */
        stateTracker.writeProgressBar(terminalWriter, true);
        output = terminalWriter.getTranscript();
        assertWithMessage(((("Longest running action '" + messageOld) + "' should be visible in short output: ") + output)).that(output.contains(messageOld)).isTrue();
    }

    @Test
    public void testSampleSize() throws IOException {
        // Verify that the number of actions shown in the progress bar can be set as sample size.
        ManualClock clock = new ManualClock();
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(123));
        ExperimentalStateTracker stateTracker = new ExperimentalStateTracker(clock);
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(2));
        // Start 10 actions (numbered 0 to 9).
        for (int i = 0; i < 10; i++) {
            clock.advanceMillis(TimeUnit.SECONDS.toMillis(1));
            Action action = mockAction((("Performing action A" + i) + "."), (("action_A" + i) + ".out"));
            stateTracker.actionStarted(new com.google.devtools.build.lib.actions.ActionStartedEvent(action, clock.nanoTime()));
        }
        // For various sample sizes verify the progress bar
        for (int i = 1; i < 11; i++) {
            stateTracker.setSampleSize(i);
            LoggingTerminalWriter terminalWriter = /* discardHighlight= */
            new LoggingTerminalWriter(true);
            stateTracker.writeProgressBar(terminalWriter);
            String output = terminalWriter.getTranscript();
            assertWithMessage(((("Action " + (i - 1)) + " should still be shown in the output: '") + output)).that(output.contains((("A" + (i - 1)) + "."))).isTrue();
            assertWithMessage(((("Action " + i) + " should not be shown in the output: ") + output)).that(output.contains((("A" + i) + "."))).isFalse();
            if (i < 10) {
                assertWithMessage(("Ellipsis symbol should be shown in output: " + output)).that(output.contains("...")).isTrue();
            } else {
                assertWithMessage(("Ellipsis symbol should not be shown in output: " + output)).that(output.contains("...")).isFalse();
            }
        }
    }

    @Test
    public void testTimesShown() throws IOException {
        // For sufficiently long running actions, the time that has passed since their start is shown.
        // In the short version of the progress bar, this should be true at least for the oldest action.
        ManualClock clock = new ManualClock();
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(123));
        ExperimentalStateTracker stateTracker = new ExperimentalStateTracker(clock);
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(2));
        stateTracker.actionStarted(new com.google.devtools.build.lib.actions.ActionStartedEvent(mockAction("First action", "foo"), clock.nanoTime()));
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(7));
        stateTracker.actionStarted(new com.google.devtools.build.lib.actions.ActionStartedEvent(mockAction("Second action", "bar"), clock.nanoTime()));
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(20));
        LoggingTerminalWriter terminalWriter = /* discardHighlight= */
        new LoggingTerminalWriter(true);
        stateTracker.writeProgressBar(terminalWriter);
        String output = terminalWriter.getTranscript();
        assertWithMessage(("Runtime of first action should be visible in output: " + output)).that(output.contains("27s")).isTrue();
        assertWithMessage(("Runtime of second action should be visible in output: " + output)).that(output.contains("20s")).isTrue();
        terminalWriter = /* discardHighlight= */
        new LoggingTerminalWriter(true);
        /* shortVersion= */
        stateTracker.writeProgressBar(terminalWriter, true);
        output = terminalWriter.getTranscript();
        assertWithMessage(("Runtime of first action should be visible in short output: " + output)).that(output.contains("27s")).isTrue();
    }

    @Test
    public void initialProgressBarTimeIndependent() {
        ManualClock clock = new ManualClock();
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(123));
        ExperimentalStateTracker stateTracker = new ExperimentalStateTracker(clock);
        assertWithMessage("Initial progress status should be time independent").that(stateTracker.progressBarTimeDependent()).isFalse();
    }

    @Test
    public void runningActionTimeIndependent() {
        ManualClock clock = new ManualClock();
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(123));
        ExperimentalStateTracker stateTracker = new ExperimentalStateTracker(clock);
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(1));
        stateTracker.actionStarted(new com.google.devtools.build.lib.actions.ActionStartedEvent(mockAction("Some action", "foo"), clock.nanoTime()));
        assertWithMessage("Progress bar showing a running action should be time dependent").that(stateTracker.progressBarTimeDependent()).isTrue();
    }

    @Test
    public void testCountVisible() throws Exception {
        // The test count should be visible in the status bar, as well as the short status bar
        ManualClock clock = new ManualClock();
        ExperimentalStateTracker stateTracker = new ExperimentalStateTracker(clock);
        TestFilteringCompleteEvent filteringComplete = Mockito.mock(TestFilteringCompleteEvent.class);
        Label labelA = Label.parseAbsolute("//foo/bar:baz", ImmutableMap.of());
        ConfiguredTarget targetA = Mockito.mock(ConfiguredTarget.class);
        Mockito.when(targetA.getLabel()).thenReturn(labelA);
        ConfiguredTarget targetB = Mockito.mock(ConfiguredTarget.class);
        Mockito.when(filteringComplete.getTestTargets()).thenReturn(ImmutableSet.of(targetA, targetB));
        TestSummary testSummary = Mockito.mock(TestSummary.class);
        Mockito.when(testSummary.getTarget()).thenReturn(targetA);
        Mockito.when(testSummary.getLabel()).thenReturn(labelA);
        stateTracker.testFilteringComplete(filteringComplete);
        stateTracker.testSummary(testSummary);
        LoggingTerminalWriter terminalWriter = /* discardHighlight= */
        new LoggingTerminalWriter(true);
        stateTracker.writeProgressBar(terminalWriter);
        String output = terminalWriter.getTranscript();
        assertWithMessage(("Test count should be visible in output: " + output)).that(output.contains(" 1 / 2 tests")).isTrue();
        terminalWriter = /* discardHighlight= */
        new LoggingTerminalWriter(true);
        /* shortVersion= */
        stateTracker.writeProgressBar(terminalWriter, true);
        output = terminalWriter.getTranscript();
        assertWithMessage(("Test count should be visible in short output: " + output)).that(output.contains(" 1 / 2 tests")).isTrue();
    }

    @Test
    public void testPassedVisible() throws Exception {
        // The last test should still be visible in the long status bar, and colored as ok if it passed.
        ManualClock clock = new ManualClock();
        ExperimentalStateTracker stateTracker = new ExperimentalStateTracker(clock);
        TestFilteringCompleteEvent filteringComplete = Mockito.mock(TestFilteringCompleteEvent.class);
        Label labelA = Label.parseAbsolute("//foo/bar:baz", ImmutableMap.of());
        ConfiguredTarget targetA = Mockito.mock(ConfiguredTarget.class);
        Mockito.when(targetA.getLabel()).thenReturn(labelA);
        ConfiguredTarget targetB = Mockito.mock(ConfiguredTarget.class);
        Mockito.when(filteringComplete.getTestTargets()).thenReturn(ImmutableSet.of(targetA, targetB));
        TestSummary testSummary = Mockito.mock(TestSummary.class);
        Mockito.when(testSummary.getStatus()).thenReturn(PASSED);
        Mockito.when(testSummary.getTarget()).thenReturn(targetA);
        Mockito.when(testSummary.getLabel()).thenReturn(labelA);
        stateTracker.testFilteringComplete(filteringComplete);
        stateTracker.testSummary(testSummary);
        LoggingTerminalWriter terminalWriter = new LoggingTerminalWriter();
        stateTracker.writeProgressBar(terminalWriter);
        String output = terminalWriter.getTranscript();
        String expected = (LoggingTerminalWriter.OK) + labelA;
        assertWithMessage(((("Sequence '" + expected) + "' should be present in colored progress bar: ") + output)).that(output.contains(expected)).isTrue();
    }

    @Test
    public void testFailedVisible() throws Exception {
        // The last test should still be visible in the long status bar, and colored as fail if it
        // did not pass.
        ManualClock clock = new ManualClock();
        ExperimentalStateTracker stateTracker = new ExperimentalStateTracker(clock);
        TestFilteringCompleteEvent filteringComplete = Mockito.mock(TestFilteringCompleteEvent.class);
        Label labelA = Label.parseAbsolute("//foo/bar:baz", ImmutableMap.of());
        ConfiguredTarget targetA = Mockito.mock(ConfiguredTarget.class);
        Mockito.when(targetA.getLabel()).thenReturn(labelA);
        ConfiguredTarget targetB = Mockito.mock(ConfiguredTarget.class);
        Mockito.when(filteringComplete.getTestTargets()).thenReturn(ImmutableSet.of(targetA, targetB));
        TestSummary testSummary = Mockito.mock(TestSummary.class);
        Mockito.when(testSummary.getStatus()).thenReturn(FAILED);
        Mockito.when(testSummary.getTarget()).thenReturn(targetA);
        Mockito.when(testSummary.getLabel()).thenReturn(labelA);
        stateTracker.testFilteringComplete(filteringComplete);
        stateTracker.testSummary(testSummary);
        LoggingTerminalWriter terminalWriter = new LoggingTerminalWriter();
        stateTracker.writeProgressBar(terminalWriter);
        String output = terminalWriter.getTranscript();
        String expected = (LoggingTerminalWriter.FAIL) + labelA;
        assertWithMessage(((("Sequence '" + expected) + "' should be present in colored progress bar: ") + output)).that(output.contains(expected)).isTrue();
    }

    @Test
    public void testSensibleShortening() throws Exception {
        // Verify that in the typical case, we shorten the progress message by shortening
        // the path implicit in it, that can also be extracted from the label. In particular,
        // the parts
        ManualClock clock = new ManualClock();
        ExperimentalStateTracker stateTracker = new ExperimentalStateTracker(clock, 70);
        Action action = mockAction("Building some/very/very/long/path/for/some/library/directory/foo.jar (42 source files)", "some/very/very/long/path/for/some/library/directory/foo.jar");
        Label label = Label.parseAbsolute("//some/very/very/long/path/for/some/library/directory:libfoo", ImmutableMap.of());
        ActionOwner owner = ActionOwner.create(label, ImmutableList.<AspectDescriptor>of(), null, null, null, "fedcba", null, null, null);
        Mockito.when(action.getOwner()).thenReturn(owner);
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(3));
        stateTracker.actionStarted(new com.google.devtools.build.lib.actions.ActionStartedEvent(action, clock.nanoTime()));
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(5));
        LoggingTerminalWriter terminalWriter = /* discardHighlight= */
        new LoggingTerminalWriter(true);
        stateTracker.writeProgressBar(terminalWriter);
        String output = terminalWriter.getTranscript();
        assertWithMessage(("Progress bar should contain \'Building \', but was:\n" + output)).that(output.contains("Building ")).isTrue();
        assertWithMessage(("Progress bar should contain \'foo.jar (42 source files)\', but was:\n" + output)).that(output.contains("foo.jar (42 source files)")).isTrue();
    }

    @Test
    public void testActionStrategyVisible() throws Exception {
        // verify that, if a strategy was reported for a shown action, it is visible
        // in the progress bar.
        String strategy = "verySpecialStrategy";
        String primaryOutput = "some/path/to/a/file";
        ManualClock clock = new ManualClock();
        Path path = outputBase.getRelative(PathFragment.create(primaryOutput));
        Artifact artifact = new Artifact(path, ArtifactRoot.asSourceRoot(Root.fromPath(outputBase)));
        ActionExecutionMetadata actionMetadata = Mockito.mock(ActionExecutionMetadata.class);
        Mockito.when(actionMetadata.getOwner()).thenReturn(Mockito.mock(ActionOwner.class));
        Mockito.when(actionMetadata.getPrimaryOutput()).thenReturn(artifact);
        ExperimentalStateTracker stateTracker = new ExperimentalStateTracker(clock);
        stateTracker.actionStarted(new com.google.devtools.build.lib.actions.ActionStartedEvent(mockAction("Some random action", primaryOutput), clock.nanoTime()));
        stateTracker.runningAction(new com.google.devtools.build.lib.actions.RunningActionEvent(actionMetadata, strategy));
        LoggingTerminalWriter terminalWriter = /* discardHighlight= */
        new LoggingTerminalWriter(true);
        stateTracker.writeProgressBar(terminalWriter);
        String output = terminalWriter.getTranscript();
        assertWithMessage(((("Output should mention strategy '" + strategy) + "', but was: ") + output)).that(output.contains(strategy)).isTrue();
    }

    @Test
    public void testOutputLength() throws Exception {
        for (int i = 0; i < 3; i++) {
            doTestOutputLength(true, i);
            doTestOutputLength(false, i);
        }
    }

    @Test
    public void testStatusShown() throws Exception {
        // Verify that for non-executing actions, at least the first 3 characters of the
        // status are shown.
        // Also verify that the number of running actions is reported correctly, if there is
        // more than one active action and not all are running.
        ManualClock clock = new ManualClock();
        clock.advanceMillis(120000);
        ExperimentalStateTracker stateTracker = new ExperimentalStateTracker(clock);
        Action actionFoo = mockAction("Building foo", "foo/foo");
        ActionOwner ownerFoo = Mockito.mock(ActionOwner.class);
        Mockito.when(actionFoo.getOwner()).thenReturn(ownerFoo);
        Action actionBar = mockAction("Building bar", "bar/bar");
        ActionOwner ownerBar = Mockito.mock(ActionOwner.class);
        Mockito.when(actionBar.getOwner()).thenReturn(ownerBar);
        LoggingTerminalWriter terminalWriter;
        String output;
        // Action foo being analyzed.
        stateTracker.actionStarted(new com.google.devtools.build.lib.actions.ActionStartedEvent(actionFoo, 123456700));
        stateTracker.analyzingAction(new com.google.devtools.build.lib.actions.AnalyzingActionEvent(actionFoo));
        terminalWriter = /* discardHighlight= */
        new LoggingTerminalWriter(true);
        stateTracker.writeProgressBar(terminalWriter);
        output = terminalWriter.getTranscript();
        assertWithMessage(("Action foo being analyzed should be visible in output:\n" + output)).that(((output.contains("ana")) || (output.contains("Ana")))).isTrue();
        // Then action bar gets scheduled.
        stateTracker.actionStarted(new com.google.devtools.build.lib.actions.ActionStartedEvent(actionBar, 123456701));
        stateTracker.schedulingAction(new com.google.devtools.build.lib.actions.SchedulingActionEvent(actionBar, "bar-sandbox"));
        terminalWriter = /* discardHighlight= */
        new LoggingTerminalWriter(true);
        stateTracker.writeProgressBar(terminalWriter);
        output = terminalWriter.getTranscript();
        assertWithMessage(("Action bar being scheduled should be visible in output:\n" + output)).that(((output.contains("sch")) || (output.contains("Sch")))).isTrue();
        assertWithMessage(("Action foo being analyzed should still be visible in output:\n" + output)).that(((output.contains("ana")) || (output.contains("Ana")))).isTrue();
        assertWithMessage(("Indication at no actions are running is missing in output:\n" + output)).that(output.contains("0 running")).isTrue();
        assertWithMessage(("Total number of actions expected  in output:\n" + output)).that(output.contains("2 actions")).isTrue();
        // Then foo starts.
        stateTracker.runningAction(new com.google.devtools.build.lib.actions.RunningActionEvent(actionFoo, "xyz-sandbox"));
        stateTracker.writeProgressBar(terminalWriter);
        terminalWriter = /* discardHighlight= */
        new LoggingTerminalWriter(true);
        stateTracker.writeProgressBar(terminalWriter);
        output = terminalWriter.getTranscript();
        assertWithMessage(("Action foo\'s xyz-sandbox strategy should be shown in output:\n" + output)).that(output.contains("xyz-sandbox")).isTrue();
        assertWithMessage(("Action foo should no longer be analyzed in output:\n" + output)).that(((output.contains("ana")) || (output.contains("Ana")))).isFalse();
        assertWithMessage(("Action bar being scheduled should still be visible in output:\n" + output)).that(((output.contains("sch")) || (output.contains("Sch")))).isTrue();
        assertWithMessage(("Indication at one action is running is missing in output:\n" + output)).that(output.contains("1 running")).isTrue();
        assertWithMessage(("Total number of actions expected  in output:\n" + output)).that(output.contains("2 actions")).isTrue();
    }

    @Test
    public void testTimerReset() throws Exception {
        // Verify that a change in an action state (e.g., from scheduling to executing) resets
        // the time associated with that action.
        ManualClock clock = new ManualClock();
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(123));
        ExperimentalStateTracker stateTracker = new ExperimentalStateTracker(clock);
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(2));
        LoggingTerminalWriter terminalWriter;
        String output;
        Action actionFoo = mockAction("Building foo", "foo/foo");
        ActionOwner ownerFoo = Mockito.mock(ActionOwner.class);
        Mockito.when(actionFoo.getOwner()).thenReturn(ownerFoo);
        Action actionBar = mockAction("Building bar", "bar/bar");
        ActionOwner ownerBar = Mockito.mock(ActionOwner.class);
        Mockito.when(actionBar.getOwner()).thenReturn(ownerBar);
        stateTracker.actionStarted(new com.google.devtools.build.lib.actions.ActionStartedEvent(actionFoo, clock.nanoTime()));
        stateTracker.runningAction(new com.google.devtools.build.lib.actions.RunningActionEvent(actionFoo, "foo-sandbox"));
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(7));
        stateTracker.actionStarted(new com.google.devtools.build.lib.actions.ActionStartedEvent(actionBar, clock.nanoTime()));
        stateTracker.schedulingAction(new com.google.devtools.build.lib.actions.SchedulingActionEvent(actionBar, "bar-sandbox"));
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(21));
        terminalWriter = /* discardHighlight= */
        new LoggingTerminalWriter(true);
        stateTracker.writeProgressBar(terminalWriter);
        output = terminalWriter.getTranscript();
        assertWithMessage(("Runtime of first action should be visible in output: " + output)).that(output.contains("28s")).isTrue();
        assertWithMessage(("Scheduling time of second action should be visible in output: " + output)).that(output.contains("21s")).isTrue();
        stateTracker.runningAction(new com.google.devtools.build.lib.actions.RunningActionEvent(actionBar, "bar-sandbox"));
        terminalWriter = /* discardHighlight= */
        new LoggingTerminalWriter(true);
        stateTracker.writeProgressBar(terminalWriter);
        output = terminalWriter.getTranscript();
        assertWithMessage(("Runtime of first action should still be visible in output: " + output)).that(output.contains("28s")).isTrue();
        assertWithMessage(("Time of second action should no longer be visible in output: " + output)).that(output.contains("21s")).isFalse();
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(30));
        terminalWriter = /* discardHighlight= */
        new LoggingTerminalWriter(true);
        stateTracker.writeProgressBar(terminalWriter);
        output = terminalWriter.getTranscript();
        assertWithMessage(("New runtime of first action should be visible in output: " + output)).that(output.contains("58s")).isTrue();
        assertWithMessage(("Runtime of second action should be visible in output: " + output)).that(output.contains("30s")).isTrue();
    }

    @Test
    public void testEarlyStatusHandledGracefully() throws Exception {
        // On the event bus, events sometimes are sent out of order; verify that we handle an
        // early message that an action is running gracefully.
        ManualClock clock = new ManualClock();
        ExperimentalStateTracker stateTracker = new ExperimentalStateTracker(clock);
        Action actionFoo = mockAction("Building foo", "foo/foo");
        ActionOwner ownerFoo = Mockito.mock(ActionOwner.class);
        Mockito.when(actionFoo.getOwner()).thenReturn(ownerFoo);
        LoggingTerminalWriter terminalWriter;
        String output;
        // Early status announcement
        stateTracker.runningAction(new com.google.devtools.build.lib.actions.RunningActionEvent(actionFoo, "foo-sandbox"));
        // Here we don't expect any particular output, just some description; in particular, we do
        // not expect the state tracker to hit an internal error.
        terminalWriter = /* discardHighlight= */
        new LoggingTerminalWriter(true);
        stateTracker.writeProgressBar(terminalWriter);
        output = terminalWriter.getTranscript();
        assertWithMessage("Expected at least some status bar").that(((output.length()) != 0)).isTrue();
        // Action actually started
        stateTracker.actionStarted(new com.google.devtools.build.lib.actions.ActionStartedEvent(actionFoo, clock.nanoTime()));
        terminalWriter = /* discardHighlight= */
        new LoggingTerminalWriter(true);
        stateTracker.writeProgressBar(terminalWriter);
        output = terminalWriter.getTranscript();
        assertWithMessage(("Even a strategy announced early should be shown in output:\n" + output)).that(output.contains("foo-sandbox")).isTrue();
    }

    @Test
    public void testExecutingActionsFirst() throws Exception {
        // Verify that executing actions, even if started late, are visible.
        ManualClock clock = new ManualClock();
        ExperimentalStateTracker stateTracker = new ExperimentalStateTracker(clock);
        clock.advanceMillis(120000);
        for (int i = 0; i < 30; i++) {
            Action action = mockAction(("Takes long to schedule number " + i), ("long/startup" + i));
            ActionOwner owner = Mockito.mock(ActionOwner.class);
            Mockito.when(action.getOwner()).thenReturn(owner);
            stateTracker.actionStarted(new com.google.devtools.build.lib.actions.ActionStartedEvent(action, (123456789 + i)));
            stateTracker.schedulingAction(new com.google.devtools.build.lib.actions.SchedulingActionEvent(action, "xyz-sandbox"));
        }
        for (int i = 0; i < 3; i++) {
            Action action = mockAction(("quickstart" + i), ("pkg/quickstart" + i));
            ActionOwner owner = Mockito.mock(ActionOwner.class);
            Mockito.when(action.getOwner()).thenReturn(owner);
            stateTracker.actionStarted(new com.google.devtools.build.lib.actions.ActionStartedEvent(action, (123457000 + i)));
            stateTracker.runningAction(new com.google.devtools.build.lib.actions.RunningActionEvent(action, "xyz-sandbox"));
            LoggingTerminalWriter terminalWriter = /* discardHighlight= */
            new LoggingTerminalWriter(true);
            stateTracker.writeProgressBar(terminalWriter);
            String output = terminalWriter.getTranscript();
            assertWithMessage(((("Action quickstart" + i) + " should be visible in output:\n") + output)).that(output.contains(("quickstart" + i))).isTrue();
            assertWithMessage(("Number of running actions should be indicated in output:\n" + output)).that(output.contains((("" + (i + 1)) + " running"))).isTrue();
        }
    }

    @Test
    public void testAggregation() throws Exception {
        // Assert that actions for the same test are aggregated so that an action afterwards
        // is still shown.
        ManualClock clock = new ManualClock();
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(1234));
        ExperimentalStateTracker stateTracker = new ExperimentalStateTracker(clock, 80);
        Label labelFooTest = Label.parseAbsolute("//foo/bar:footest", ImmutableMap.of());
        ConfiguredTarget targetFooTest = Mockito.mock(ConfiguredTarget.class);
        Mockito.when(targetFooTest.getLabel()).thenReturn(labelFooTest);
        ActionOwner fooOwner = ActionOwner.create(labelFooTest, ImmutableList.<AspectDescriptor>of(), null, null, null, "abcdef", null, null, null);
        Label labelBarTest = Label.parseAbsolute("//baz:bartest", ImmutableMap.of());
        ConfiguredTarget targetBarTest = Mockito.mock(ConfiguredTarget.class);
        Mockito.when(targetBarTest.getLabel()).thenReturn(labelBarTest);
        TestFilteringCompleteEvent filteringComplete = Mockito.mock(TestFilteringCompleteEvent.class);
        Mockito.when(filteringComplete.getTestTargets()).thenReturn(ImmutableSet.of(targetFooTest, targetBarTest));
        ActionOwner barOwner = ActionOwner.create(labelBarTest, ImmutableList.<AspectDescriptor>of(), null, null, null, "fedcba", null, null, null);
        stateTracker.testFilteringComplete(filteringComplete);
        // First produce 10 actions for footest...
        for (int i = 0; i < 10; i++) {
            clock.advanceMillis(TimeUnit.SECONDS.toMillis(1));
            Action action = mockAction(("Testing foo, shard " + i), ("testlog_foo_" + i));
            Mockito.when(action.getOwner()).thenReturn(fooOwner);
            stateTracker.actionStarted(new com.google.devtools.build.lib.actions.ActionStartedEvent(action, clock.nanoTime()));
        }
        // ...then produce 10 actions for bartest...
        for (int i = 0; i < 10; i++) {
            clock.advanceMillis(TimeUnit.SECONDS.toMillis(1));
            Action action = mockAction(("Testing bar, shard " + i), ("testlog_bar_" + i));
            Mockito.when(action.getOwner()).thenReturn(barOwner);
            stateTracker.actionStarted(new com.google.devtools.build.lib.actions.ActionStartedEvent(action, clock.nanoTime()));
        }
        // ...and finally a completely unrelated action
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(1));
        stateTracker.actionStarted(new com.google.devtools.build.lib.actions.ActionStartedEvent(mockAction("Other action", "other/action"), clock.nanoTime()));
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(1));
        LoggingTerminalWriter terminalWriter = /* discardHighlight= */
        new LoggingTerminalWriter(true);
        stateTracker.writeProgressBar(terminalWriter);
        String output = terminalWriter.getTranscript();
        assertWithMessage(("Progress bar should contain \':footest\', but was:\n" + output)).that(output.contains(":footest")).isTrue();
        assertWithMessage(("Progress bar should contain \':bartest\', but was:\n" + output)).that(output.contains(":bartest")).isTrue();
        assertWithMessage(("Progress bar should contain \'Other action\', but was:\n" + output)).that(output.contains("Other action")).isTrue();
    }

    @Test
    public void testSuffix() throws Exception {
        MatcherAssert.assertThat(ExperimentalStateTracker.suffix("foobar", 3)).isEqualTo("bar");
        MatcherAssert.assertThat(ExperimentalStateTracker.suffix("foo", (-2))).isEmpty();
        MatcherAssert.assertThat(ExperimentalStateTracker.suffix("foobar", 200)).isEqualTo("foobar");
    }

    @Test
    public void testDownloadShown() throws Exception {
        // Verify that, whenever a single download is running in loading face, it is shown in the status
        // bar.
        ManualClock clock = new ManualClock();
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(1234));
        ExperimentalStateTracker stateTracker = new ExperimentalStateTracker(clock, 80);
        URL url = new URL("http://example.org/first/dep");
        stateTracker.buildStarted(null);
        stateTracker.downloadProgress(new DownloadProgressEvent(url));
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(6));
        LoggingTerminalWriter terminalWriter = /* discardHighlight= */
        new LoggingTerminalWriter(true);
        stateTracker.writeProgressBar(terminalWriter);
        String output = terminalWriter.getTranscript();
        assertWithMessage(((("Progress bar should contain '" + (url.toString())) + "\', but was:\n") + output)).that(output.contains(url.toString())).isTrue();
        assertWithMessage(("Progress bar should contain \'6s\', but was:\n" + output)).that(output.contains("6s")).isTrue();
        // Progress on the pending download should be reported appropriately
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(1));
        stateTracker.downloadProgress(new DownloadProgressEvent(url, 256));
        terminalWriter = /* discardHighlight= */
        new LoggingTerminalWriter(true);
        stateTracker.writeProgressBar(terminalWriter);
        output = terminalWriter.getTranscript();
        assertWithMessage(((("Progress bar should contain '" + (url.toString())) + "\', but was:\n") + output)).that(output.contains(url.toString())).isTrue();
        assertWithMessage(("Progress bar should contain \'7s\', but was:\n" + output)).that(output.contains("7s")).isTrue();
        assertWithMessage(("Progress bar should contain \'256\', but was:\n" + output)).that(output.contains("256")).isTrue();
        // After finishing the download, it should no longer be reported.
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(1));
        stateTracker.downloadProgress(new DownloadProgressEvent(url, 256, true));
        terminalWriter = /* discardHighlight= */
        new LoggingTerminalWriter(true);
        stateTracker.writeProgressBar(terminalWriter);
        output = terminalWriter.getTranscript();
        assertWithMessage(("Progress bar should not contain url, but was:\n" + output)).that(output.contains("example.org")).isFalse();
    }

    @Test
    public void testDownloadOutputLength() throws Exception {
        // Verify that URLs are shortened in a reasonable way, if the terminal is not wide enough
        // Also verify that the length is respected, even if only a download sample is shown.
        ManualClock clock = new ManualClock();
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(1234));
        ExperimentalStateTracker stateTracker = new ExperimentalStateTracker(clock, 60);
        URL url = new URL("http://example.org/some/really/very/very/long/path/filename.tar.gz");
        stateTracker.buildStarted(null);
        stateTracker.downloadProgress(new DownloadProgressEvent(url));
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(6));
        for (int i = 0; i < 10; i++) {
            stateTracker.downloadProgress(new DownloadProgressEvent(new URL((("http://otherhost.example/another/also/length/path/to/another/download" + i) + ".zip"))));
            clock.advanceMillis(TimeUnit.SECONDS.toMillis(1));
        }
        LoggingTerminalWriter terminalWriter = /* discardHighlight= */
        new LoggingTerminalWriter(true);
        stateTracker.writeProgressBar(terminalWriter);
        String output = terminalWriter.getTranscript();
        assertWithMessage(("Only lines with at most 60 chars should be present in the output:\n" + output)).that(((longestLine(output)) <= 60)).isTrue();
        assertWithMessage(("Output still should contain the filename, but was:\n" + output)).that(output.contains("filename.tar.gz")).isTrue();
        assertWithMessage(("Output still should contain the host name, but was:\n" + output)).that(output.contains("example.org")).isTrue();
    }

    @Test
    public void testMultipleBuildEventProtocolTransports() throws Exception {
        // Verify that all announced transports are present in the progress bar
        // and that as transports are closed they disappear from the progress bar.
        // Verify that the wait duration is displayed.
        // Verify that after all transports have been closed, the build status is displayed.
        ManualClock clock = new ManualClock();
        BuildEventTransport transport1 = newBepTransport("BuildEventTransport1");
        BuildEventTransport transport2 = newBepTransport("BuildEventTransport2");
        BuildEventTransport transport3 = newBepTransport("BuildEventTransport3");
        BuildResult buildResult = new BuildResult(clock.currentTimeMillis());
        buildResult.setExitCondition(SUCCESS);
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(1));
        buildResult.setStopTime(clock.currentTimeMillis());
        ExperimentalStateTracker stateTracker = new ExperimentalStateTracker(clock, 80);
        stateTracker.buildStarted(null);
        stateTracker.buildEventTransportsAnnounced(new com.google.devtools.build.lib.buildeventstream.AnnounceBuildEventTransportsEvent(ImmutableList.of(transport1, transport2)));
        stateTracker.buildEventTransportsAnnounced(new com.google.devtools.build.lib.buildeventstream.AnnounceBuildEventTransportsEvent(ImmutableList.of(transport3)));
        stateTracker.buildComplete(new com.google.devtools.build.lib.buildtool.buildevent.BuildCompleteEvent(buildResult));
        LoggingTerminalWriter terminalWriter = new LoggingTerminalWriter(true);
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(1));
        stateTracker.writeProgressBar(terminalWriter);
        String output = terminalWriter.getTranscript();
        MatcherAssert.assertThat(output, CoreMatchers.containsString("1s"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("BuildEventTransport1"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("BuildEventTransport2"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("BuildEventTransport3"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("success"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("complete"));
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(1));
        stateTracker.buildEventTransportClosed(new com.google.devtools.build.lib.buildeventstream.BuildEventTransportClosedEvent(transport1));
        terminalWriter = new LoggingTerminalWriter(true);
        stateTracker.writeProgressBar(terminalWriter);
        output = terminalWriter.getTranscript();
        MatcherAssert.assertThat(output, CoreMatchers.containsString("2s"));
        MatcherAssert.assertThat(output, CoreMatchers.not(CoreMatchers.containsString("BuildEventTransport1")));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("BuildEventTransport2"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("BuildEventTransport3"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("success"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("complete"));
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(1));
        stateTracker.buildEventTransportClosed(new com.google.devtools.build.lib.buildeventstream.BuildEventTransportClosedEvent(transport3));
        terminalWriter = new LoggingTerminalWriter(true);
        stateTracker.writeProgressBar(terminalWriter);
        output = terminalWriter.getTranscript();
        MatcherAssert.assertThat(output, CoreMatchers.containsString("3s"));
        MatcherAssert.assertThat(output, CoreMatchers.not(CoreMatchers.containsString("BuildEventTransport1")));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("BuildEventTransport2"));
        MatcherAssert.assertThat(output, CoreMatchers.not(CoreMatchers.containsString("BuildEventTransport3")));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("success"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("complete"));
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(1));
        stateTracker.buildEventTransportClosed(new com.google.devtools.build.lib.buildeventstream.BuildEventTransportClosedEvent(transport2));
        terminalWriter = new LoggingTerminalWriter(true);
        stateTracker.writeProgressBar(terminalWriter);
        output = terminalWriter.getTranscript();
        MatcherAssert.assertThat(output, CoreMatchers.not(CoreMatchers.containsString("3s")));
        MatcherAssert.assertThat(output, CoreMatchers.not(CoreMatchers.containsString("BuildEventTransport1")));
        MatcherAssert.assertThat(output, CoreMatchers.not(CoreMatchers.containsString("BuildEventTransport2")));
        MatcherAssert.assertThat(output, CoreMatchers.not(CoreMatchers.containsString("BuildEventTransport3")));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("success"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("complete"));
        MatcherAssert.assertThat(output.split("\\n")).hasLength(1);
    }

    @Test
    public void testBuildEventTransportsOnNarrowTerminal() throws IOException {
        // Verify that the progress bar contains useful information on a 60-character terminal.
        // - Too long names should be shortened to reasonably long prefixes of the name.
        ManualClock clock = new ManualClock();
        BuildEventTransport transport1 = newBepTransport(Strings.repeat("A", 61));
        BuildEventTransport transport2 = newBepTransport("BuildEventTransport");
        BuildResult buildResult = new BuildResult(clock.currentTimeMillis());
        buildResult.setExitCondition(SUCCESS);
        LoggingTerminalWriter terminalWriter = new LoggingTerminalWriter(true);
        ExperimentalStateTracker stateTracker = new ExperimentalStateTracker(clock, 60);
        stateTracker.buildStarted(null);
        stateTracker.buildEventTransportsAnnounced(new com.google.devtools.build.lib.buildeventstream.AnnounceBuildEventTransportsEvent(ImmutableList.of(transport1, transport2)));
        stateTracker.buildComplete(new com.google.devtools.build.lib.buildtool.buildevent.BuildCompleteEvent(buildResult));
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(1));
        stateTracker.writeProgressBar(terminalWriter);
        String output = terminalWriter.getTranscript();
        MatcherAssert.assertThat(longestLine(output)).isAtMost(60);
        MatcherAssert.assertThat(output, CoreMatchers.containsString("1s"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString(((Strings.repeat("A", 30)) + "...")));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("BuildEventTransport"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("success"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("complete"));
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(1));
        stateTracker.buildEventTransportClosed(new com.google.devtools.build.lib.buildeventstream.BuildEventTransportClosedEvent(transport2));
        terminalWriter = new LoggingTerminalWriter(true);
        stateTracker.writeProgressBar(terminalWriter);
        output = terminalWriter.getTranscript();
        MatcherAssert.assertThat(longestLine(output)).isAtMost(60);
        MatcherAssert.assertThat(output, CoreMatchers.containsString("2s"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString(((Strings.repeat("A", 30)) + "...")));
        MatcherAssert.assertThat(output, CoreMatchers.not(CoreMatchers.containsString("BuildEventTransport")));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("success"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("complete"));
        MatcherAssert.assertThat(output.split("\\n")).hasLength(2);
    }

    @Test
    public void testTotalFetchesReported() throws IOException {
        ManualClock clock = new ManualClock();
        ExperimentalStateTracker stateTracker = new ExperimentalStateTracker(clock, 80);
        stateTracker.buildStarted(null);
        for (int i = 0; i < 30; i++) {
            stateTracker.downloadProgress(new ExperimentalStateTrackerTest.FetchEvent(("@repoFoo" + i)));
        }
        clock.advanceMillis(TimeUnit.SECONDS.toMillis(7));
        LoggingTerminalWriter terminalWriter = new LoggingTerminalWriter(true);
        stateTracker.writeProgressBar(terminalWriter);
        String output = terminalWriter.getTranscript();
        MatcherAssert.assertThat(output, CoreMatchers.containsString("@repoFoo"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("7s"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("30 fetches"));
    }

    private static class FetchEvent implements FetchProgress {
        private final String id;

        FetchEvent(String id) {
            this.id = id;
        }

        @Override
        public String getResourceIdentifier() {
            return id;
        }

        @Override
        public String getProgress() {
            return "working...";
        }

        @Override
        public boolean isFinished() {
            return false;
        }
    }
}

