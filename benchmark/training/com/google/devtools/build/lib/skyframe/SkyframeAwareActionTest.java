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


import EvaluationProgressReceiver.NullEvaluationProgressReceiver;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.graph.ImmutableGraph;
import com.google.devtools.build.lib.actions.AbstractAction;
import com.google.devtools.build.lib.actions.ActionExecutionContext;
import com.google.devtools.build.lib.actions.ActionExecutionException;
import com.google.devtools.build.lib.actions.ActionKeyContext;
import com.google.devtools.build.lib.actions.ActionResult;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.Executor;
import com.google.devtools.build.lib.actions.FileStateValue;
import com.google.devtools.build.lib.actions.util.ActionsTestUtil;
import com.google.devtools.build.lib.testutil.FoundationTestCase;
import com.google.devtools.build.lib.util.Fingerprint;
import com.google.devtools.build.skyframe.EvaluationProgressReceiver;
import com.google.devtools.build.skyframe.EvaluationProgressReceiver.EvaluationState;
import com.google.devtools.build.skyframe.SkyFunction.Environment;
import com.google.devtools.build.skyframe.SkyKey;
import com.google.devtools.build.skyframe.SkyValue;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link SkyframeAwareAction}.
 */
@RunWith(JUnit4.class)
public class SkyframeAwareActionTest extends TimestampBuilderTestCase {
    private Builder builder;

    private Executor executor;

    private SkyframeAwareActionTest.TrackingEvaluationProgressReceiver progressReceiver;

    private static final class TrackingEvaluationProgressReceiver extends EvaluationProgressReceiver.NullEvaluationProgressReceiver {
        public static final class InvalidatedKey {
            public final SkyKey skyKey;

            public final InvalidationState state;

            InvalidatedKey(SkyKey skyKey, InvalidationState state) {
                this.skyKey = skyKey;
                this.state = state;
            }

            @Override
            public boolean equals(Object obj) {
                return ((obj instanceof SkyframeAwareActionTest.TrackingEvaluationProgressReceiver.InvalidatedKey) && (this.skyKey.equals(((SkyframeAwareActionTest.TrackingEvaluationProgressReceiver.InvalidatedKey) (obj)).skyKey))) && (this.state.equals(((SkyframeAwareActionTest.TrackingEvaluationProgressReceiver.InvalidatedKey) (obj)).state));
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(skyKey, state);
            }
        }

        private static final class EvaluatedEntry {
            public final SkyKey skyKey;

            final EvaluationSuccessState successState;

            public final EvaluationState state;

            EvaluatedEntry(SkyKey skyKey, EvaluationSuccessState successState, EvaluationState state) {
                this.skyKey = skyKey;
                this.successState = successState;
                this.state = state;
            }

            @Override
            public boolean equals(Object obj) {
                return (((obj instanceof SkyframeAwareActionTest.TrackingEvaluationProgressReceiver.EvaluatedEntry) && (this.skyKey.equals(((SkyframeAwareActionTest.TrackingEvaluationProgressReceiver.EvaluatedEntry) (obj)).skyKey))) && (this.successState.equals(((SkyframeAwareActionTest.TrackingEvaluationProgressReceiver.EvaluatedEntry) (obj)).successState))) && (this.state.equals(((SkyframeAwareActionTest.TrackingEvaluationProgressReceiver.EvaluatedEntry) (obj)).state));
            }

            @Override
            public int hashCode() {
                return Objects.hashCode(skyKey, successState, state);
            }
        }

        public final Set<SkyframeAwareActionTest.TrackingEvaluationProgressReceiver.InvalidatedKey> invalidated = Sets.newConcurrentHashSet();

        public final Set<SkyKey> enqueued = Sets.newConcurrentHashSet();

        public final Set<SkyframeAwareActionTest.TrackingEvaluationProgressReceiver.EvaluatedEntry> evaluated = Sets.newConcurrentHashSet();

        public void reset() {
            invalidated.clear();
            enqueued.clear();
            evaluated.clear();
        }

        public boolean wasInvalidated(SkyKey skyKey) {
            for (SkyframeAwareActionTest.TrackingEvaluationProgressReceiver.InvalidatedKey e : invalidated) {
                if (e.skyKey.equals(skyKey)) {
                    return true;
                }
            }
            return false;
        }

        public SkyframeAwareActionTest.TrackingEvaluationProgressReceiver.EvaluatedEntry getEvalutedEntry(SkyKey forKey) {
            for (SkyframeAwareActionTest.TrackingEvaluationProgressReceiver.EvaluatedEntry e : evaluated) {
                if (e.skyKey.equals(forKey)) {
                    return e;
                }
            }
            return null;
        }

        @Override
        public void invalidated(SkyKey skyKey, InvalidationState state) {
            invalidated.add(new SkyframeAwareActionTest.TrackingEvaluationProgressReceiver.InvalidatedKey(skyKey, state));
        }

        @Override
        public void enqueueing(SkyKey skyKey) {
            enqueued.add(skyKey);
        }

        @Override
        public void evaluated(SkyKey skyKey, @Nullable
        SkyValue value, Supplier<EvaluationSuccessState> evaluationSuccessState, EvaluationState state) {
            evaluated.add(new SkyframeAwareActionTest.TrackingEvaluationProgressReceiver.EvaluatedEntry(skyKey, evaluationSuccessState.get(), state));
        }
    }

    /**
     * A mock action that counts how many times it was executed.
     */
    private static class ExecutionCountingAction extends AbstractAction {
        private final AtomicInteger executionCounter;

        ExecutionCountingAction(Artifact input, Artifact output, AtomicInteger executionCounter) {
            super(ActionsTestUtil.NULL_ACTION_OWNER, ImmutableList.of(input), ImmutableList.of(output));
            this.executionCounter = executionCounter;
        }

        @Override
        public ActionResult execute(ActionExecutionContext actionExecutionContext) throws ActionExecutionException, InterruptedException {
            executionCounter.incrementAndGet();
            // This action first reads its input file (there can be only one). For the purpose of these
            // tests we assume that the input file is short, maybe just 10 bytes long.
            byte[] input = new byte[10];
            int inputLen = 0;
            try (InputStream in = Iterables.getOnlyElement(getInputs()).getPath().getInputStream()) {
                inputLen = in.read(input);
            } catch (IOException e) {
                throw new ActionExecutionException(e, this, false);
            }
            // This action then writes the contents of the input to the (only) output file, and appends an
            // extra "x" character too.
            try (OutputStream out = getPrimaryOutput().getPath().getOutputStream()) {
                out.write(input, 0, inputLen);
                out.write('x');
            } catch (IOException e) {
                throw new ActionExecutionException(e, this, false);
            }
            return ActionResult.EMPTY;
        }

        @Override
        public String getMnemonic() {
            return null;
        }

        @Override
        protected void computeKey(ActionKeyContext actionKeyContext, Fingerprint fp) {
            fp.addString(getPrimaryOutput().getExecPathString());
            fp.addInt(executionCounter.get());
        }
    }

    private static class ExecutionCountingCacheBypassingAction extends SkyframeAwareActionTest.ExecutionCountingAction {
        ExecutionCountingCacheBypassingAction(Artifact input, Artifact output, AtomicInteger executionCounter) {
            super(input, output, executionCounter);
        }

        @Override
        public boolean executeUnconditionally() {
            return true;
        }

        @Override
        public boolean isVolatile() {
            return true;
        }
    }

    /**
     * A mock skyframe-aware action that counts how many times it was executed.
     */
    private static class SkyframeAwareExecutionCountingAction extends SkyframeAwareActionTest.ExecutionCountingCacheBypassingAction implements SkyframeAwareAction {
        private final SkyKey actionDepKey;

        SkyframeAwareExecutionCountingAction(Artifact input, Artifact output, AtomicInteger executionCounter, SkyKey actionDepKey) {
            super(input, output, executionCounter);
            this.actionDepKey = actionDepKey;
        }

        @Override
        public Object establishSkyframeDependencies(Environment env) throws ExceptionBase, InterruptedException {
            // Establish some Skyframe dependency. A real action would then use this to compute and
            // cache data for the execute(...) method.
            env.getValue(actionDepKey);
            return null;
        }

        @Override
        public ImmutableGraph<SkyKey> getSkyframeDependenciesForRewinding(SkyKey self) {
            throw new UnsupportedOperationException();
        }
    }

    private interface ExecutionCountingActionFactory {
        SkyframeAwareActionTest.ExecutionCountingAction create(Artifact input, Artifact output, AtomicInteger executionCounter);
    }

    private enum ChangeArtifact {

        DONT_CHANGE,
        CHANGE_MTIME() {
            @Override
            boolean changeMtime() {
                return true;
            }
        },
        CHANGE_MTIME_AND_CONTENT() {
            @Override
            boolean changeMtime() {
                return true;
            }

            @Override
            boolean changeContent() {
                return true;
            }
        };
        boolean changeMtime() {
            return false;
        }

        boolean changeContent() {
            return false;
        }
    }

    private enum ExpectActionIs {

        NOT_DIRTIED() {
            @Override
            boolean actuallyClean() {
                return true;
            }
        },
        DIRTIED_BUT_VERIFIED_CLEAN() {
            @Override
            boolean dirtied() {
                return true;
            }

            @Override
            boolean actuallyClean() {
                return true;
            }
        },
        // REBUILT_BUT_ACTION_CACHE_HIT,
        // This would be a bug, symptom of a skyframe-aware action that doesn't bypass the action cache
        // and is incorrectly regarded as an action cache hit when its inputs stayed the same but its
        // "skyframe dependencies" changed.
        REEXECUTED() {
            @Override
            boolean dirtied() {
                return true;
            }

            @Override
            boolean reexecuted() {
                return true;
            }
        };
        boolean dirtied() {
            return false;
        }

        boolean actuallyClean() {
            return false;
        }

        boolean reexecuted() {
            return false;
        }
    }

    @Test
    public void testCacheCheckingActionWithContentChangingInput() throws Exception {
        /* unconditionalExecution */
        assertActionWithContentChangingInput(false);
    }

    @Test
    public void testCacheBypassingActionWithContentChangingInput() throws Exception {
        /* unconditionalExecution */
        assertActionWithContentChangingInput(true);
    }

    @Test
    public void testCacheCheckingActionWithMtimeChangingInput() throws Exception {
        /* unconditionalExecution */
        assertActionWithMtimeChangingInput(false);
    }

    @Test
    public void testCacheBypassingActionWithMtimeChangingInput() throws Exception {
        /* unconditionalExecution */
        assertActionWithMtimeChangingInput(true);
    }

    @Test
    public void testCacheCheckingActionWithNonChangingInput() throws Exception {
        /* unconditionalExecution */
        assertActionWithNonChangingInput(false);
    }

    @Test
    public void testCacheBypassingActionWithNonChangingInput() throws Exception {
        /* unconditionalExecution */
        assertActionWithNonChangingInput(true);
    }

    @Test
    public void testActionWithNonChangingInputButChangingSkyframeDeps() throws Exception {
        assertActionWithMaybeChangingInputAndChangingSkyframeDeps(SkyframeAwareActionTest.ChangeArtifact.DONT_CHANGE);
    }

    @Test
    public void testActionWithChangingInputMtimeAndChangingSkyframeDeps() throws Exception {
        assertActionWithMaybeChangingInputAndChangingSkyframeDeps(SkyframeAwareActionTest.ChangeArtifact.CHANGE_MTIME);
    }

    @Test
    public void testActionWithChangingInputAndChangingSkyframeDeps() throws Exception {
        assertActionWithMaybeChangingInputAndChangingSkyframeDeps(SkyframeAwareActionTest.ChangeArtifact.CHANGE_MTIME_AND_CONTENT);
    }

    @Test
    public void testActionWithNonChangingInputAndNonChangingSkyframeDeps() throws Exception {
        final SkyKey skyframeDep = FileStateValue.key(createSkyframeDepOfAction());
        // Assert that an action-cache-check-bypassing action is executed only once if neither its input
        // nor its Skyframe dependency changes between builds.
        assertActionExecutions(new SkyframeAwareActionTest.ExecutionCountingActionFactory() {
            @Override
            public SkyframeAwareActionTest.ExecutionCountingAction create(Artifact input, Artifact output, AtomicInteger executionCounter) {
                return new SkyframeAwareActionTest.SkyframeAwareExecutionCountingAction(input, output, executionCounter, skyframeDep);
            }
        }, SkyframeAwareActionTest.ChangeArtifact.DONT_CHANGE, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // Invalidate the dependency but leave its value up-to-date, so the action should not
                // be rebuilt.
                differencer.invalidate(ImmutableList.of(skyframeDep));
                return null;
            }
        }, SkyframeAwareActionTest.ExpectActionIs.DIRTIED_BUT_VERIFIED_CLEAN);
    }

    private abstract static class SingleOutputAction extends AbstractAction {
        SingleOutputAction(@Nullable
        Artifact input, Artifact output) {
            super(ActionsTestUtil.NULL_ACTION_OWNER, (input == null ? ImmutableList.<Artifact>of() : ImmutableList.of(input)), ImmutableList.of(output));
        }

        protected static final class Buffer {
            final int size;

            final byte[] data;

            Buffer(byte[] data, int size) {
                this.data = data;
                this.size = size;
            }
        }

        protected SkyframeAwareActionTest.SingleOutputAction.Buffer readInput() throws ActionExecutionException {
            byte[] input = new byte[100];
            int inputLen = 0;
            try (InputStream in = getPrimaryInput().getPath().getInputStream()) {
                inputLen = in.read(input, 0, input.length);
            } catch (IOException e) {
                throw new ActionExecutionException(e, this, false);
            }
            return new SkyframeAwareActionTest.SingleOutputAction.Buffer(input, inputLen);
        }

        protected void writeOutput(@Nullable
        SkyframeAwareActionTest.SingleOutputAction.Buffer buf, String data) throws ActionExecutionException {
            try (OutputStream out = getPrimaryOutput().getPath().getOutputStream()) {
                if (buf != null) {
                    out.write(buf.data, 0, buf.size);
                }
                out.write(data.getBytes(StandardCharsets.UTF_8), 0, data.length());
            } catch (IOException e) {
                throw new ActionExecutionException(e, this, false);
            }
        }

        @Override
        public String getMnemonic() {
            return "MockActionMnemonic";
        }

        @Override
        protected void computeKey(ActionKeyContext actionKeyContext, Fingerprint fp) {
            fp.addInt(42);
        }
    }

    private abstract static class SingleOutputSkyframeAwareAction extends SkyframeAwareActionTest.SingleOutputAction implements SkyframeAwareAction {
        SingleOutputSkyframeAwareAction(@Nullable
        Artifact input, Artifact output) {
            super(input, output);
        }

        @Override
        public boolean executeUnconditionally() {
            return true;
        }

        @Override
        public boolean isVolatile() {
            return true;
        }
    }

    /**
     * Regression test to avoid a potential race condition in {@link ActionExecutionFunction}.
     *
     * <p>The test ensures that when ActionExecutionFunction executes a Skyframe-aware action
     * (implementor of {@link SkyframeAwareAction}), ActionExecutionFunction first requests the inputs
     * of the action and ensures they are built before requesting any of its Skyframe dependencies.
     *
     * <p>This strict ordering is very important to avoid the race condition, which could arise if the
     * compute method were too eager to request all dependencies: request input files but even if some
     * are missing, request also the skyframe-dependencies. The race is described in this method's
     * body.
     */
    @Test
    public void testRaceConditionBetweenInputAcquisitionAndSkyframeDeps() throws Exception {
        // Sequence of events on threads A and B, showing SkyFunctions and requested SkyKeys, leading
        // to an InconsistentFilesystemException:
        // 
        // _______________[Thread A]_________________|_______________[Thread B]_________________
        // ActionExecutionFunction(gen2_action:      | idle
        // genfiles/gen1 -> genfiles/foo/bar/gen2) |
        // ARTIFACT:genfiles/gen1                    |
        // MOCK_VALUE:dummy_argument                 |
        // env.valuesMissing():yes ==> return        |
        // |
        // ArtifactFunction(genfiles/gen1)           | MockFunction()
        // CONFIGURED_TARGET://foo:gen1              | FILE:genfiles/foo
        // ACTION_EXECUTION:gen1_action              | env.valuesMissing():yes ==> return
        // env.valuesMissing():yes ==> return        |
        // | FileFunction(genfiles/foo)
        // ActionExecutionFunction(gen1_action)      | FILE:genfiles
        // ARTIFACT:genfiles/gen0                    | env.valuesMissing():yes ==> return
        // env.valuesMissing():yes ==> return        |
        // | FileFunction(genfiles)
        // ArtifactFunction(genfiles/gen0)           | FILE_STATE:genfiles
        // CONFIGURED_TARGET://foo:gen0              | env.valuesMissing():yes ==> return
        // ACTION_EXECUTION:gen0_action              |
        // env.valuesMissing():yes ==> return        | FileStateFunction(genfiles)
        // | stat genfiles
        // ActionExecutionFunction(gen0_action)      | return FileStateValue:non-existent
        // create output directory: genfiles         |
        // working                                   | FileFunction(genfiles/foo)
        // | FILE:genfiles
        // | FILE_STATE:genfiles/foo
        // | env.valuesMissing():yes ==> return
        // |
        // | FileStateFunction(genfiles/foo)
        // | stat genfiles/foo
        // | return FileStateValue:non-existent
        // |
        // done, created genfiles/gen0               | FileFunction(genfiles/foo)
        // return ActionExecutionValue(gen0_action)  | FILE:genfiles
        // | FILE_STATE:genfiles/foo
        // ArtifactFunction(genfiles/gen0)           | return FileValue(genfiles/foo:non-existent)
        // CONFIGURED_TARGET://foo:gen0              |
        // ACTION_EXECUTION:gen0_action              | MockFunction()
        // return ArtifactSkyKey(genfiles/gen0)      | FILE:genfiles/foo
        // | FILE:genfiles/foo/bar/gen1
        // ActionExecutionFunction(gen1_action)      | env.valuesMissing():yes ==> return
        // ARTIFACT:genfiles/gen0                    |
        // create output directory: genfiles/foo/bar | FileFunction(genfiles/foo/bar/gen1)
        // done, created genfiles/foo/bar/gen1       | FILE:genfiles/foo/bar
        // return ActionExecutionValue(gen1_action)  | env.valuesMissing():yes ==> return
        // |
        // idle                                      | FileFunction(genfiles/foo/bar)
        // | FILE:genfiles/foo
        // | FILE_STATE:genfiles/foo/bar
        // | env.valuesMissing():yes ==> return
        // |
        // | FileStateFunction(genfiles/foo/bar)
        // | stat genfiles/foo/bar
        // | return FileStateValue:directory
        // |
        // | FileFunction(genfiles/foo/bar)
        // | FILE:genfiles/foo
        // | FILE_STATE:genfiles/foo/bar
        // | throw InconsistentFilesystemException:
        // |     genfiles/foo doesn't exist but
        // |     genfiles/foo/bar does!
        Artifact genFile1 = createDerivedArtifact("foo/bar/gen1.txt");
        Artifact genFile2 = createDerivedArtifact("gen2.txt");
        registerAction(new SkyframeAwareActionTest.SingleOutputAction(null, genFile1) {
            @Override
            public ActionResult execute(ActionExecutionContext actionExecutionContext) throws ActionExecutionException, InterruptedException {
                writeOutput(null, "gen1");
                return ActionResult.EMPTY;
            }
        });
        registerAction(new SkyframeAwareActionTest.SingleOutputSkyframeAwareAction(genFile1, genFile2) {
            @Override
            public Object establishSkyframeDependencies(Environment env) throws ExceptionBase {
                assertThat(env.valuesMissing()).isFalse();
                return null;
            }

            @Override
            public ImmutableGraph<SkyKey> getSkyframeDependenciesForRewinding(SkyKey self) {
                throw new UnsupportedOperationException();
            }

            @Override
            public ActionResult execute(ActionExecutionContext actionExecutionContext) throws ActionExecutionException, InterruptedException {
                writeOutput(readInput(), "gen2");
                return ActionResult.EMPTY;
            }
        });
        builder.buildArtifacts(reporter, ImmutableSet.of(genFile2), null, null, null, null, null, executor, null, null, options, null, null);
    }
}

