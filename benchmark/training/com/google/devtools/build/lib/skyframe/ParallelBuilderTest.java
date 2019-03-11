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
package com.google.devtools.build.lib.skyframe;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.Runnables;
import com.google.devtools.build.lib.actions.Action;
import com.google.devtools.build.lib.actions.ActionExecutedEvent;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.BuildFailedException;
import com.google.devtools.build.lib.actions.cache.ActionCache;
import com.google.devtools.build.lib.actions.util.TestAction;
import com.google.devtools.build.lib.events.Event;
import com.google.devtools.build.lib.events.EventHandler;
import com.google.devtools.build.lib.events.EventKind;
import com.google.devtools.build.lib.testutil.BlazeTestUtils;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.testutil.Suite;
import com.google.devtools.build.lib.testutil.TestSpec;
import com.google.devtools.build.lib.testutil.TestUtils;
import com.google.devtools.build.lib.vfs.FileStatus;
import com.google.devtools.build.lib.vfs.FileSystem;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.inmemoryfs.InMemoryFileSystem;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test suite for ParallelBuilder.
 */
@TestSpec(size = Suite.MEDIUM_TESTS)
@RunWith(JUnit4.class)
public class ParallelBuilderTest extends TimestampBuilderTestCase {
    private static final Logger logger = Logger.getLogger(ParallelBuilderTest.class.getName());

    protected ActionCache cache;

    protected static final int DEFAULT_NUM_JOBS = 100;

    private volatile boolean runningFooAction;

    private volatile boolean runningBarAction;

    /**
     * Intercepts actionExecuted events, ordinarily written to the master log, for
     * use locally within this test suite.
     */
    public static class ActionEventRecorder {
        private final List<ActionExecutedEvent> actionExecutedEvents = new ArrayList<>();

        @Subscribe
        public void actionExecuted(ActionExecutedEvent event) {
            actionExecutedEvents.add(event);
        }
    }

    @Test
    public void testReportsActionExecutedEvent() throws Exception {
        Artifact pear = createDerivedArtifact("pear");
        ParallelBuilderTest.ActionEventRecorder recorder = new ParallelBuilderTest.ActionEventRecorder();
        eventBus.register(recorder);
        Action action = registerAction(new TestAction(Runnables.doNothing(), TimestampBuilderTestCase.emptySet, ParallelBuilderTest.asSet(pear)));
        buildArtifacts(createBuilder(ParallelBuilderTest.DEFAULT_NUM_JOBS, true), pear);
        assertThat(recorder.actionExecutedEvents).hasSize(1);
        assertThat(recorder.actionExecutedEvents.get(0).getAction()).isEqualTo(action);
    }

    @Test
    public void testRunsInParallel() throws Exception {
        runsInParallelWithBuilder(createBuilder(ParallelBuilderTest.DEFAULT_NUM_JOBS, false));
    }

    /**
     * Test that we can recover properly after a failed build.
     */
    @Test
    public void testFailureRecovery() throws Exception {
        // [action] -> foo
        Artifact foo = createDerivedArtifact("foo");
        Callable<Void> makeFoo = new Callable<Void>() {
            @Override
            public Void call() throws IOException {
                throw new IOException("building 'foo' is supposed to fail");
            }
        };
        registerAction(new TestAction(makeFoo, Artifact.NO_ARTIFACTS, ImmutableList.of(foo)));
        // [action] -> bar
        Artifact bar = createDerivedArtifact("bar");
        registerAction(new TestAction(TestAction.NO_EFFECT, TimestampBuilderTestCase.emptySet, ImmutableList.of(bar)));
        // Don't fail fast when we encounter the error
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        // test that building 'foo' fails
        try {
            buildArtifacts(foo);
            Assert.fail("building 'foo' was supposed to fail!");
        } catch (BuildFailedException e) {
            if (!(e.getMessage().contains("building 'foo' is supposed to fail"))) {
                throw e;
            }
            // Make sure the reporter reported the error message.
            assertContainsEvent("building 'foo' is supposed to fail");
        }
        // test that a subsequent build of 'bar' succeeds
        buildArtifacts(bar);
    }

    @Test
    public void testUpdateCacheError() throws Exception {
        FileSystem fs = new InMemoryFileSystem() {
            @Override
            public FileStatus statIfFound(Path path, boolean followSymlinks) throws IOException {
                final FileStatus stat = super.statIfFound(path, followSymlinks);
                if (path.toString().endsWith("/out/foo")) {
                    return new FileStatus() {
                        private final FileStatus original = stat;

                        @Override
                        public boolean isSymbolicLink() {
                            return original.isSymbolicLink();
                        }

                        @Override
                        public boolean isFile() {
                            return original.isFile();
                        }

                        @Override
                        public boolean isDirectory() {
                            return original.isDirectory();
                        }

                        @Override
                        public boolean isSpecialFile() {
                            return original.isSpecialFile();
                        }

                        @Override
                        public long getSize() throws IOException {
                            return original.getSize();
                        }

                        @Override
                        public long getNodeId() throws IOException {
                            return original.getNodeId();
                        }

                        @Override
                        public long getLastModifiedTime() throws IOException {
                            throw new IOException();
                        }

                        @Override
                        public long getLastChangeTime() throws IOException {
                            throw new IOException();
                        }
                    };
                }
                return stat;
            }
        };
        Artifact foo = createDerivedArtifact(fs, "foo");
        registerAction(new TestAction(TestAction.NO_EFFECT, TimestampBuilderTestCase.emptySet, ImmutableList.of(foo)));
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        try {
            buildArtifacts(foo);
            Assert.fail("Expected to fail");
        } catch (BuildFailedException e) {
            assertContainsEvent("not all outputs were created or valid");
        }
    }

    @Test
    public void testNullBuild() throws Exception {
        // BuildTool.setupLogging(Level.FINEST);
        ParallelBuilderTest.logger.fine("Testing null build...");
        buildArtifacts();
    }

    /**
     * Test a randomly-generated complex dependency graph.
     */
    @Test
    public void testSmallRandomStressTest() throws Exception {
        final int numTrials = 1;
        final int numArtifacts = 30;
        final int randomSeed = 42;
        ParallelBuilderTest.StressTest test = new ParallelBuilderTest.StressTest(numArtifacts, numTrials, randomSeed);
        test.runStressTest();
    }

    private static enum BuildKind {

        Clean,
        Incremental,
        Nop;}

    /**
     * Sets up and manages stress tests of arbitrary size.
     */
    protected class StressTest {
        final int numArtifacts;

        final int numTrials;

        Random random;

        Artifact[] artifacts;

        public StressTest(int numArtifacts, int numTrials, int randomSeed) {
            this.numTrials = numTrials;
            this.numArtifacts = numArtifacts;
            this.random = new Random(randomSeed);
        }

        public void runStressTest() throws Exception {
            for (int trial = 0; trial < (numTrials); trial++) {
                List<TimestampBuilderTestCase.Counter> counters = buildRandomActionGraph(trial);
                // do a clean build
                ParallelBuilderTest.logger.fine((("Testing clean build... (trial " + trial) + ")"));
                Artifact[] buildTargets = chooseRandomBuild();
                buildArtifacts(buildTargets);
                doSanityChecks(buildTargets, counters, ParallelBuilderTest.BuildKind.Clean);
                resetCounters(counters);
                // Do an incremental build.
                // 
                // BuildTool creates new instances of the Builder for each build request. It may rely on
                // that fact (that its state will be discarded after each build request) - thus
                // test should use same approach and ensure that a new instance is used each time.
                ParallelBuilderTest.logger.fine("Testing incremental build...");
                buildTargets = chooseRandomBuild();
                buildArtifacts(buildTargets);
                doSanityChecks(buildTargets, counters, ParallelBuilderTest.BuildKind.Incremental);
                resetCounters(counters);
                // do a do-nothing build
                ParallelBuilderTest.logger.fine("Testing do-nothing rebuild...");
                buildArtifacts(buildTargets);
                doSanityChecks(buildTargets, counters, ParallelBuilderTest.BuildKind.Nop);
                // resetCounters(counters);
            }
        }

        /**
         * Construct a random action graph, and initialize the file system
         * so that all of the input files exist and none of the output files
         * exist.
         */
        public List<TimestampBuilderTestCase.Counter> buildRandomActionGraph(int actionGraphNumber) throws IOException {
            List<TimestampBuilderTestCase.Counter> counters = new ArrayList<>(numArtifacts);
            artifacts = new Artifact[numArtifacts];
            for (int i = 0; i < (numArtifacts); i++) {
                artifacts[i] = createDerivedArtifact(((("file" + actionGraphNumber) + "-") + i));
            }
            int numOutputs;
            for (int i = 0; i < (artifacts.length); i += numOutputs) {
                int numInputs = random.nextInt(3);
                numOutputs = 1 + (random.nextInt(2));
                if ((i + numOutputs) >= (artifacts.length)) {
                    numOutputs = (artifacts.length) - i;
                }
                Collection<Artifact> inputs = new ArrayList<>(numInputs);
                for (int j = 0; j < numInputs; j++) {
                    if (i != 0) {
                        int inputNum = random.nextInt(i);
                        inputs.add(artifacts[inputNum]);
                    }
                }
                Collection<Artifact> outputs = new ArrayList<>(numOutputs);
                for (int j = 0; j < numOutputs; j++) {
                    outputs.add(artifacts[(i + j)]);
                }
                counters.add(createActionCounter(inputs, outputs));
                if (inputs.isEmpty()) {
                    // source files -- create them
                    for (Artifact output : outputs) {
                        BlazeTestUtils.makeEmptyFile(output.getPath());
                    }
                } else {
                    // generated files -- delete them
                    for (Artifact output : outputs) {
                        try {
                            output.getPath().delete();
                        } catch (FileNotFoundException e) {
                            // ok
                        }
                    }
                }
            }
            return counters;
        }

        /**
         * Choose a random set of targets to build.
         */
        public Artifact[] chooseRandomBuild() {
            Artifact[] buildTargets;
            switch (random.nextInt(4)) {
                case 0 :
                    // build the final output target
                    ParallelBuilderTest.logger.fine("Building final output target.");
                    buildTargets = new Artifact[]{ artifacts[((numArtifacts) - 1)] };
                    break;
                case 1 :
                    {
                        // build all the targets (in random order);
                        ParallelBuilderTest.logger.fine("Building all the targets.");
                        List<Artifact> targets = Lists.newArrayList(artifacts);
                        Collections.shuffle(targets, random);
                        buildTargets = targets.toArray(new Artifact[numArtifacts]);
                        break;
                    }
                case 2 :
                    // build a random target
                    ParallelBuilderTest.logger.fine("Building a random target.");
                    buildTargets = new Artifact[]{ artifacts[random.nextInt(numArtifacts)] };
                    break;
                case 3 :
                    {
                        // build a random subset of targets
                        ParallelBuilderTest.logger.fine("Building a random subset of targets.");
                        List<Artifact> targets = Lists.newArrayList(artifacts);
                        Collections.shuffle(targets, random);
                        List<Artifact> targetSubset = new ArrayList<>();
                        int numTargetsToTest = random.nextInt(numArtifacts);
                        ParallelBuilderTest.logger.fine(("numTargetsToTest = " + numTargetsToTest));
                        Iterator<Artifact> iterator = targets.iterator();
                        for (int i = 0; i < numTargetsToTest; i++) {
                            targetSubset.add(iterator.next());
                        }
                        buildTargets = targetSubset.toArray(new Artifact[numTargetsToTest]);
                        break;
                    }
                default :
                    throw new IllegalStateException();
            }
            return buildTargets;
        }

        public void doSanityChecks(Artifact[] targets, List<TimestampBuilderTestCase.Counter> counters, ParallelBuilderTest.BuildKind kind) {
            // Check that we really did build all the targets.
            for (Artifact file : targets) {
                assertThat(file.getPath().exists()).isTrue();
            }
            // Check that each action was executed the right number of times
            for (TimestampBuilderTestCase.Counter counter : counters) {
                switch (kind) {
                    case Clean :
                        // assert counter.count == 1;
                        // break;
                    case Incremental :
                        assert ((counter.count) == 0) || ((counter.count) == 1);
                        break;
                    case Nop :
                        assert (counter.count) == 0;
                        break;
                }
            }
        }

        private void resetCounters(List<TimestampBuilderTestCase.Counter> counters) {
            for (TimestampBuilderTestCase.Counter counter : counters) {
                counter.count = 0;
            }
        }
    }

    // Regression test for bug fixed in CL 3548332: builder was not waiting for
    // all its subprocesses to terminate.
    @Test
    public void testWaitsForSubprocesses() throws Exception {
        final Semaphore semaphore = new Semaphore(1);
        final boolean[] finished = new boolean[]{ false };
        semaphore.acquireUninterruptibly();// t=0: semaphore acquired

        // This arrangement ensures that the "bar" action tries to run for about
        // 100ms after the "foo" action has completed (failed).
        // [action] -> foo
        Artifact foo = createDerivedArtifact("foo");
        Callable<Void> makeFoo = new Callable<Void>() {
            @Override
            public Void call() throws IOException {
                semaphore.acquireUninterruptibly();// t=2: semaphore re-acquired

                throw new IOException("foo action failed");
            }
        };
        registerAction(new TestAction(makeFoo, Artifact.NO_ARTIFACTS, ImmutableList.of(foo)));
        // [action] -> bar
        Artifact bar = createDerivedArtifact("bar");
        Runnable makeBar = new Runnable() {
            @Override
            public void run() {
                semaphore.release();// t=1: semaphore released

                try {
                    Thread.sleep(100);// 100ms

                } catch (InterruptedException e) {
                    // This might happen (though not necessarily).  The
                    // ParallelBuilder interrupts all its workers at the first sign
                    // of trouble.
                }
                finished[0] = true;
            }
        };
        registerAction(new TestAction(makeBar, TimestampBuilderTestCase.emptySet, ParallelBuilderTest.asSet(bar)));
        // Don't fail fast when we encounter the error
        reporter.removeHandler(FoundationTestCase.failFastHandler);
        try {
            buildArtifacts(foo, bar);
            Assert.fail();
        } catch (BuildFailedException e) {
            assertThat(e).hasMessageThat().contains("TestAction failed due to exception: foo action failed");
            assertContainsEvent("TestAction failed due to exception: foo action failed");
        }
        assertWithMessage("bar action not finished, yet buildArtifacts has completed.").that(finished[0]).isTrue();
    }

    @Test
    public void testCyclicActionGraph() throws Exception {
        // foo -> [action] -> bar
        // bar -> [action] -> baz
        // baz -> [action] -> foo
        Artifact foo = createDerivedArtifact("foo");
        Artifact bar = createDerivedArtifact("bar");
        Artifact baz = createDerivedArtifact("baz");
        try {
            registerAction(new TestAction(TestAction.NO_EFFECT, ParallelBuilderTest.asSet(foo), ParallelBuilderTest.asSet(bar)));
            registerAction(new TestAction(TestAction.NO_EFFECT, ParallelBuilderTest.asSet(bar), ParallelBuilderTest.asSet(baz)));
            registerAction(new TestAction(TestAction.NO_EFFECT, ParallelBuilderTest.asSet(baz), ParallelBuilderTest.asSet(foo)));
            buildArtifacts(foo);
            Assert.fail("Builder failed to detect cyclic action graph");
        } catch (BuildFailedException e) {
            assertThat(e).hasMessageThat().isEqualTo(TimestampBuilderTestCase.CYCLE_MSG);
        }
    }

    @Test
    public void testSelfCyclicActionGraph() throws Exception {
        // foo -> [action] -> foo
        Artifact foo = createDerivedArtifact("foo");
        try {
            registerAction(new TestAction(TestAction.NO_EFFECT, ParallelBuilderTest.asSet(foo), ParallelBuilderTest.asSet(foo)));
            buildArtifacts(foo);
            Assert.fail("Builder failed to detect cyclic action graph");
        } catch (BuildFailedException e) {
            assertThat(e).hasMessageThat().isEqualTo(TimestampBuilderTestCase.CYCLE_MSG);
        }
    }

    @Test
    public void testCycleInActionGraphBelowTwoActions() throws Exception {
        // bar -> [action] -> foo1
        // bar -> [action] -> foo2
        // baz -> [action] -> bar
        // bar -> [action] -> baz
        Artifact foo1 = createDerivedArtifact("foo1");
        Artifact foo2 = createDerivedArtifact("foo2");
        Artifact bar = createDerivedArtifact("bar");
        Artifact baz = createDerivedArtifact("baz");
        try {
            registerAction(new TestAction(TestAction.NO_EFFECT, ParallelBuilderTest.asSet(bar), ParallelBuilderTest.asSet(foo1)));
            registerAction(new TestAction(TestAction.NO_EFFECT, ParallelBuilderTest.asSet(bar), ParallelBuilderTest.asSet(foo2)));
            registerAction(new TestAction(TestAction.NO_EFFECT, ParallelBuilderTest.asSet(baz), ParallelBuilderTest.asSet(bar)));
            registerAction(new TestAction(TestAction.NO_EFFECT, ParallelBuilderTest.asSet(bar), ParallelBuilderTest.asSet(baz)));
            buildArtifacts(foo1, foo2);
            Assert.fail("Builder failed to detect cyclic action graph");
        } catch (BuildFailedException e) {
            assertThat(e).hasMessageThat().isEqualTo(TimestampBuilderTestCase.CYCLE_MSG);
        }
    }

    @Test
    public void testCyclicActionGraphWithTail() throws Exception {
        // bar -> [action] -> foo
        // baz -> [action] -> bar
        // bat, foo -> [action] -> baz
        Artifact foo = createDerivedArtifact("foo");
        Artifact bar = createDerivedArtifact("bar");
        Artifact baz = createDerivedArtifact("baz");
        Artifact bat = createDerivedArtifact("bat");
        try {
            registerAction(new TestAction(TestAction.NO_EFFECT, ParallelBuilderTest.asSet(bar), ParallelBuilderTest.asSet(foo)));
            registerAction(new TestAction(TestAction.NO_EFFECT, ParallelBuilderTest.asSet(baz), ParallelBuilderTest.asSet(bar)));
            registerAction(new TestAction(TestAction.NO_EFFECT, ParallelBuilderTest.asSet(bat, foo), ParallelBuilderTest.asSet(baz)));
            registerAction(new TestAction(TestAction.NO_EFFECT, ImmutableSet.<Artifact>of(), ParallelBuilderTest.asSet(bat)));
            buildArtifacts(foo);
            Assert.fail("Builder failed to detect cyclic action graph");
        } catch (BuildFailedException e) {
            assertThat(e).hasMessageThat().isEqualTo(TimestampBuilderTestCase.CYCLE_MSG);
        }
    }

    @Test
    public void testDuplicatedInput() throws Exception {
        // <null> -> [action] -> foo
        // (foo, foo) -> [action] -> bar
        Artifact foo = createDerivedArtifact("foo");
        Artifact bar = createDerivedArtifact("bar");
        registerAction(new TestAction(TestAction.NO_EFFECT, ParallelBuilderTest.<Artifact>asSet(), ParallelBuilderTest.asSet(foo)));
        registerAction(new TestAction(TestAction.NO_EFFECT, Lists.<Artifact>newArrayList(foo, foo), ParallelBuilderTest.asSet(bar)));
        buildArtifacts(bar);
    }

    @Test
    public void testNoNewJobsAreRunAfterFirstFailure() throws Exception {
        assertNoNewJobsAreRunAfterFirstFailure(false, false);
    }

    @Test
    public void testNoNewJobsAreRunAfterCatastrophe() throws Exception {
        assertNoNewJobsAreRunAfterFirstFailure(true, true);
    }

    @Test
    public void testProgressReporting() throws Exception {
        // Build three artifacts in 3 separate actions (baz depends on bar and bar
        // depends on foo.  Make sure progress is reported at the beginning of all
        // three actions.
        List<Artifact> sourceFiles = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            sourceFiles.add(createInputFile(("file" + i)));
        }
        Artifact foo = createDerivedArtifact("foo");
        Artifact bar = createDerivedArtifact("bar");
        Artifact baz = createDerivedArtifact("baz");
        bar.getPath().delete();
        baz.getPath().delete();
        final List<String> messages = new ArrayList<>();
        EventHandler handler = new EventHandler() {
            @Override
            public void handle(Event event) {
                EventKind k = event.getKind();
                if ((k == (EventKind.START)) || (k == (EventKind.FINISH))) {
                    // Remove the tmpDir as this is user specific and the assert would
                    // fail below.
                    messages.add((((event.getMessage().replaceFirst(TestUtils.tmpDir(), "")) + " ") + (event.getKind())));
                }
            }
        };
        reporter.addHandler(handler);
        reporter.addHandler(new com.google.devtools.build.lib.events.PrintingEventHandler(EventKind.ALL_EVENTS));
        registerAction(new TestAction(TestAction.NO_EFFECT, sourceFiles, ParallelBuilderTest.asSet(foo)));
        registerAction(new TestAction(TestAction.NO_EFFECT, ParallelBuilderTest.asSet(foo), ParallelBuilderTest.asSet(bar)));
        registerAction(new TestAction(TestAction.NO_EFFECT, ParallelBuilderTest.asSet(bar), ParallelBuilderTest.asSet(baz)));
        buildArtifacts(baz);
        // Check that the percentages increase non-linearly, because foo has 10 input files
        List<String> expectedMessages = Lists.newArrayList(" Test foo START", " Test foo FINISH", " Test bar START", " Test bar FINISH", " Test baz START", " Test baz FINISH");
        assertThat(messages).containsAllIn(expectedMessages);
        // Now do an incremental rebuild of bar and baz,
        // and check the incremental progress percentages.
        messages.clear();
        bar.getPath().delete();
        baz.getPath().delete();
        // This uses a new builder instance so that we refetch timestamps from
        // (in-memory) file system, rather than using cached entries.
        buildArtifacts(baz);
        expectedMessages = Lists.newArrayList(" Test bar START", " Test bar FINISH", " Test baz START", " Test baz FINISH");
        assertThat(messages).containsAllIn(expectedMessages);
    }
}

