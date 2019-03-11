/**
 * Copyright 2018 The Bazel Authors. All rights reserved.
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
package com.google.devtools.build.lib.dynamic;


import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.actions.ActionExecutionContext;
import com.google.devtools.build.lib.actions.ActionInput;
import com.google.devtools.build.lib.actions.ActionKeyContext;
import com.google.devtools.build.lib.actions.Artifact;
import com.google.devtools.build.lib.actions.ExecException;
import com.google.devtools.build.lib.actions.ExecutionStrategy;
import com.google.devtools.build.lib.actions.SandboxedSpawnActionContext;
import com.google.devtools.build.lib.actions.Spawn;
import com.google.devtools.build.lib.actions.SpawnActionContext;
import com.google.devtools.build.lib.actions.SpawnResult;
import com.google.devtools.build.lib.actions.UserExecException;
import com.google.devtools.build.lib.exec.ExecutionPolicy;
import com.google.devtools.build.lib.testutil.TestThread;
import com.google.devtools.build.lib.util.io.FileOutErr;
import com.google.devtools.build.lib.vfs.FileSystem;
import com.google.devtools.build.lib.vfs.FileSystemUtils;
import com.google.devtools.build.lib.vfs.Path;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link DynamicSpawnStrategy}.
 */
@RunWith(JUnit4.class)
public class DynamicSpawnStrategyTest {
    protected FileSystem fileSystem;

    protected Path testRoot;

    private ExecutorService executorService;

    private DynamicSpawnStrategyTest.MockLocalSpawnStrategy localStrategy;

    private DynamicSpawnStrategyTest.MockRemoteSpawnStrategy remoteStrategy;

    private SpawnActionContext dynamicSpawnStrategy;

    private Artifact inputArtifact;

    private Artifact outputArtifact;

    private FileOutErr outErr;

    private ActionExecutionContext actionExecutionContext;

    private DynamicExecutionOptions options;

    private final ActionKeyContext actionKeyContext = new ActionKeyContext();

    abstract static class MockSpawnStrategy implements SandboxedSpawnActionContext {
        private final Path testRoot;

        private final int delayMs;

        private volatile Spawn executedSpawn;

        private CountDownLatch succeeded = new CountDownLatch(1);

        private boolean failsDuringExecution;

        private CountDownLatch beforeExecutionWaitFor;

        private Callable<List<SpawnResult>> execute;

        public MockSpawnStrategy(Path testRoot, int delayMs) {
            this.testRoot = testRoot;
            this.delayMs = delayMs;
        }

        @Override
        public List<SpawnResult> exec(Spawn spawn, ActionExecutionContext actionExecutionContext) throws ExecException, InterruptedException {
            return exec(spawn, actionExecutionContext, null);
        }

        @Override
        public boolean canExec(Spawn spawn) {
            return true;
        }

        @Override
        public List<SpawnResult> exec(Spawn spawn, ActionExecutionContext actionExecutionContext, AtomicReference<Class<? extends SpawnActionContext>> writeOutputFiles) throws ExecException, InterruptedException {
            executedSpawn = spawn;
            if ((beforeExecutionWaitFor) != null) {
                beforeExecutionWaitFor.countDown();
                beforeExecutionWaitFor.await();
            }
            if ((delayMs) > 0) {
                Thread.sleep(delayMs);
            }
            List<SpawnResult> spawnResults = ImmutableList.of();
            if ((execute) != null) {
                try {
                    spawnResults = execute.call();
                } catch (ExecException | InterruptedException e) {
                    throw e;
                } catch (Exception e) {
                    throwIfUnchecked(e);
                    throw new IllegalStateException(e);
                }
            }
            if (failsDuringExecution) {
                try {
                    FileSystemUtils.appendIsoLatin1(actionExecutionContext.getFileOutErr().getOutputPath(), ("action failed with " + (getClass().getSimpleName())));
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
                throw new UserExecException(((getClass().getSimpleName()) + " failed to execute the Spawn"));
            }
            if ((writeOutputFiles != null) && (!(writeOutputFiles.compareAndSet(null, getClass())))) {
                throw new InterruptedException(((getClass()) + " could not acquire barrier"));
            } else {
                for (ActionInput output : spawn.getOutputFiles()) {
                    try {
                        FileSystemUtils.writeIsoLatin1(testRoot.getRelative(output.getExecPath()), getClass().getSimpleName());
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
            try {
                FileSystemUtils.appendIsoLatin1(actionExecutionContext.getFileOutErr().getOutputPath(), ("output files written with " + (getClass().getSimpleName())));
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            succeeded.countDown();
            return spawnResults;
        }

        public Spawn getExecutedSpawn() {
            return executedSpawn;
        }

        boolean succeeded() {
            return (succeeded.getCount()) == 0;
        }

        CountDownLatch getSucceededLatch() {
            return succeeded;
        }

        public void failsDuringExecution() {
            failsDuringExecution = true;
        }

        public void beforeExecutionWaitFor(CountDownLatch countDownLatch) {
            beforeExecutionWaitFor = countDownLatch;
        }

        void setExecute(Callable<List<SpawnResult>> execute) {
            this.execute = execute;
        }
    }

    @ExecutionStrategy(name = { "mock-remote" }, contextType = SpawnActionContext.class)
    private static class MockRemoteSpawnStrategy extends DynamicSpawnStrategyTest.MockSpawnStrategy {
        public MockRemoteSpawnStrategy(Path testRoot, int delayMs) {
            super(testRoot, delayMs);
        }
    }

    @ExecutionStrategy(name = { "mock-local" }, contextType = SpawnActionContext.class)
    private static class MockLocalSpawnStrategy extends DynamicSpawnStrategyTest.MockSpawnStrategy {
        public MockLocalSpawnStrategy(Path testRoot, int delayMs) {
            super(testRoot, delayMs);
        }
    }

    private static class DynamicSpawnStrategyUnderTest extends DynamicSpawnStrategy {
        public DynamicSpawnStrategyUnderTest(ExecutorService executorService, DynamicExecutionOptions options, Function<Spawn, ExecutionPolicy> executionPolicy) {
            super(executorService, options, executionPolicy);
        }
    }

    @Test
    public void nonRemotableSpawnRunsLocally() throws Exception {
        Spawn spawn = getSpawnForTest(true, false);
        createSpawnStrategy(0, 0);
        dynamicSpawnStrategy.exec(spawn, actionExecutionContext);
        assertThat(localStrategy.getExecutedSpawn()).isEqualTo(spawn);
        assertThat(localStrategy.succeeded()).isTrue();
        assertThat(remoteStrategy.getExecutedSpawn()).isNull();
        assertThat(remoteStrategy.succeeded()).isFalse();
        assertThat(outErr.outAsLatin1()).contains("output files written with MockLocalSpawnStrategy");
        assertThat(outErr.outAsLatin1()).doesNotContain("MockRemoteSpawnStrategy");
    }

    @Test
    public void nonLocallyExecutableSpawnRunsRemotely() throws Exception {
        Spawn spawn = getSpawnForTest(false, true);
        createSpawnStrategy(0, 0);
        dynamicSpawnStrategy.exec(spawn, actionExecutionContext);
        assertThat(localStrategy.getExecutedSpawn()).isNull();
        assertThat(localStrategy.succeeded()).isFalse();
        assertThat(remoteStrategy.getExecutedSpawn()).isEqualTo(spawn);
        assertThat(remoteStrategy.succeeded()).isTrue();
        assertThat(outErr.outAsLatin1()).contains("output files written with MockRemoteSpawnStrategy");
        assertThat(outErr.outAsLatin1()).doesNotContain("MockLocalSpawnStrategy");
    }

    @Test
    public void actionSucceedsIfLocalExecutionSucceedsEvenIfRemoteFailsLater() throws Exception {
        Spawn spawn = getSpawnForTest(false, false);
        createSpawnStrategy(0, 2000);
        CountDownLatch countDownLatch = new CountDownLatch(2);
        localStrategy.beforeExecutionWaitFor(countDownLatch);
        remoteStrategy.beforeExecutionWaitFor(countDownLatch);
        remoteStrategy.failsDuringExecution();
        dynamicSpawnStrategy.exec(spawn, actionExecutionContext);
        assertThat(localStrategy.getExecutedSpawn()).isEqualTo(spawn);
        assertThat(localStrategy.succeeded()).isTrue();
        assertThat(remoteStrategy.getExecutedSpawn()).isEqualTo(spawn);
        assertThat(remoteStrategy.succeeded()).isFalse();
        assertThat(outErr.outAsLatin1()).contains("output files written with MockLocalSpawnStrategy");
        assertThat(outErr.outAsLatin1()).doesNotContain("MockRemoteSpawnStrategy");
    }

    @Test
    public void actionSucceedsIfRemoteExecutionSucceedsEvenIfLocalFailsLater() throws Exception {
        Spawn spawn = getSpawnForTest(false, false);
        createSpawnStrategy(2000, 0);
        CountDownLatch countDownLatch = new CountDownLatch(2);
        localStrategy.beforeExecutionWaitFor(countDownLatch);
        localStrategy.failsDuringExecution();
        remoteStrategy.beforeExecutionWaitFor(countDownLatch);
        dynamicSpawnStrategy.exec(spawn, actionExecutionContext);
        assertThat(localStrategy.getExecutedSpawn()).isEqualTo(spawn);
        assertThat(localStrategy.succeeded()).isFalse();
        assertThat(remoteStrategy.getExecutedSpawn()).isEqualTo(spawn);
        assertThat(remoteStrategy.succeeded()).isTrue();
        assertThat(outErr.outAsLatin1()).contains("output files written with MockRemoteSpawnStrategy");
        assertThat(outErr.outAsLatin1()).doesNotContain("MockLocalSpawnStrategy");
    }

    @Test
    public void actionFailsIfLocalFailsImmediatelyEvenIfRemoteSucceedsLater() throws Exception {
        Spawn spawn = getSpawnForTest(false, false);
        createSpawnStrategy(0, 2000);
        CountDownLatch countDownLatch = new CountDownLatch(2);
        localStrategy.beforeExecutionWaitFor(countDownLatch);
        localStrategy.failsDuringExecution();
        remoteStrategy.beforeExecutionWaitFor(countDownLatch);
        try {
            dynamicSpawnStrategy.exec(spawn, actionExecutionContext);
            Assert.fail("Expected dynamicSpawnStrategy to throw an ExecException");
        } catch (ExecException e) {
            assertThat(e).hasMessageThat().matches("MockLocalSpawnStrategy failed to execute the Spawn");
        }
        assertThat(localStrategy.getExecutedSpawn()).isEqualTo(spawn);
        assertThat(localStrategy.succeeded()).isFalse();
        assertThat(remoteStrategy.getExecutedSpawn()).isEqualTo(spawn);
        assertThat(remoteStrategy.succeeded()).isFalse();
        assertThat(outErr.outAsLatin1()).contains("action failed with MockLocalSpawnStrategy");
        assertThat(outErr.outAsLatin1()).doesNotContain("MockRemoteSpawnStrategy");
    }

    @Test
    public void actionFailsIfRemoteFailsImmediatelyEvenIfLocalSucceedsLater() throws Exception {
        Spawn spawn = getSpawnForTest(false, false);
        createSpawnStrategy(2000, 0);
        CountDownLatch countDownLatch = new CountDownLatch(2);
        localStrategy.beforeExecutionWaitFor(countDownLatch);
        remoteStrategy.beforeExecutionWaitFor(countDownLatch);
        remoteStrategy.failsDuringExecution();
        try {
            dynamicSpawnStrategy.exec(spawn, actionExecutionContext);
            Assert.fail("Expected dynamicSpawnStrategy to throw an ExecException");
        } catch (ExecException e) {
            assertThat(e).hasMessageThat().matches("MockRemoteSpawnStrategy failed to execute the Spawn");
        }
        assertThat(localStrategy.getExecutedSpawn()).isEqualTo(spawn);
        assertThat(localStrategy.succeeded()).isFalse();
        assertThat(remoteStrategy.getExecutedSpawn()).isEqualTo(spawn);
        assertThat(remoteStrategy.succeeded()).isFalse();
        assertThat(outErr.outAsLatin1()).contains("action failed with MockRemoteSpawnStrategy");
        assertThat(outErr.outAsLatin1()).doesNotContain("MockLocalSpawnStrategy");
    }

    @Test
    public void actionFailsIfLocalAndRemoteFail() throws Exception {
        Spawn spawn = getSpawnForTest(false, false);
        createSpawnStrategy(0, 0);
        CountDownLatch countDownLatch = new CountDownLatch(2);
        localStrategy.beforeExecutionWaitFor(countDownLatch);
        remoteStrategy.beforeExecutionWaitFor(countDownLatch);
        localStrategy.failsDuringExecution();
        remoteStrategy.failsDuringExecution();
        try {
            dynamicSpawnStrategy.exec(spawn, actionExecutionContext);
            Assert.fail("Expected dynamicSpawnStrategy to throw an ExecException");
        } catch (ExecException e) {
            assertThat(e).hasMessageThat().matches("Mock(Local|Remote)SpawnStrategy failed to execute the Spawn");
        }
        assertThat(localStrategy.getExecutedSpawn()).isEqualTo(spawn);
        assertThat(localStrategy.succeeded()).isFalse();
        assertThat(remoteStrategy.getExecutedSpawn()).isEqualTo(spawn);
        assertThat(remoteStrategy.succeeded()).isFalse();
    }

    @Test
    public void noDeadlockWithSingleThreadedExecutor() throws Exception {
        final Spawn spawn = /* forceLocal= */
        /* forceRemote= */
        getSpawnForTest(false, false);
        // Replace the executorService with a single threaded one.
        executorService = Executors.newSingleThreadExecutor();
        /* localDelay= */
        /* remoteDelay= */
        createSpawnStrategy(0, 0);
        dynamicSpawnStrategy.exec(spawn, actionExecutionContext);
        assertThat(localStrategy.getExecutedSpawn()).isEqualTo(spawn);
        assertThat(localStrategy.succeeded()).isTrue();
        /**
         * The single-threaded executorService#invokeAny does not comply to the contract where
         * the callables are *always* called sequentially. In this case, both spawns will start
         * executing, but the local one will always succeed as it's the first to be called. The remote
         * one will then be cancelled, or is null if the local one completes before the remote one
         * starts.
         *
         * See the documentation of {@link BoundedExectorService#invokeAny(Collection)}, specifically:
         * "The following is less efficient (it goes on submitting tasks even if there is some task
         * already finished), but quite straight-forward.".
         */
        assertThat(remoteStrategy.getExecutedSpawn()).isAnyOf(spawn, null);
        assertThat(remoteStrategy.succeeded()).isFalse();
    }

    @Test
    public void interruptDuringExecutionDoesActuallyInterruptTheExecution() throws Exception {
        final Spawn spawn = getSpawnForTest(false, false);
        createSpawnStrategy(60000, 60000);
        CountDownLatch countDownLatch = new CountDownLatch(2);
        localStrategy.beforeExecutionWaitFor(countDownLatch);
        remoteStrategy.beforeExecutionWaitFor(countDownLatch);
        TestThread testThread = new TestThread() {
            @Override
            public void runTest() throws Exception {
                try {
                    dynamicSpawnStrategy.exec(spawn, actionExecutionContext);
                } catch (InterruptedException e) {
                    // This is expected.
                }
            }
        };
        testThread.start();
        countDownLatch.await(5, TimeUnit.SECONDS);
        testThread.interrupt();
        testThread.joinAndAssertState(5000);
        assertThat(outErr.getOutputPath().exists()).isFalse();
        assertThat(outErr.getErrorPath().exists()).isFalse();
    }

    @Test
    public void strategyWaitsForBothSpawnsToFinish() throws Exception {
        strategyWaitsForBothSpawnsToFinish(false, false);
    }

    @Test
    public void strategyWaitsForBothSpawnsToFinishEvenIfInterrupted() throws Exception {
        strategyWaitsForBothSpawnsToFinish(true, false);
    }

    @Test
    public void strategyWaitsForBothSpawnsToFinishOnFailure() throws Exception {
        strategyWaitsForBothSpawnsToFinish(false, true);
    }

    @Test
    public void strategyWaitsForBothSpawnsToFinishOnFailureEvenIfInterrupted() throws Exception {
        strategyWaitsForBothSpawnsToFinish(true, true);
    }

    @Test
    public void strategyPropagatesFasterLocalException() throws Exception {
        strategyPropagatesException(true);
    }

    @Test
    public void strategyPropagatesFasterRemoteException() throws Exception {
        strategyPropagatesException(false);
    }
}

