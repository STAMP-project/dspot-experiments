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


import BlazeModule.ModuleEnvironment;
import ExitCode.BLAZE_INTERNAL_ERROR;
import ExitCode.INTERRUPTED;
import ExitCode.NO_TESTS_FOUND;
import ExitCode.SUCCESS;
import OutErr.SYSTEM_OUT_ERR;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.SettableFuture;
import com.google.devtools.build.lib.util.AbruptExitException;
import com.google.devtools.build.lib.util.ExitCode;
import com.google.devtools.common.options.Option;
import com.google.devtools.common.options.OptionDocumentationCategory;
import com.google.devtools.common.options.OptionEffectTag;
import com.google.devtools.common.options.OptionsBase;
import com.google.devtools.common.options.OptionsParser;
import com.google.devtools.common.options.OptionsParsingResult;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests of CommandEnvironment's command-interrupting exit functionality.
 */
@RunWith(JUnit4.class)
public final class CommandInterruptionTest {
    /**
     * Options class to pass configuration to our dummy wait command.
     */
    public static class WaitOptions extends OptionsBase {
        public WaitOptions() {
        }

        @Option(name = "expect_interruption", documentationCategory = OptionDocumentationCategory.UNCATEGORIZED, effectTags = { OptionEffectTag.NO_OP }, defaultValue = "false")
        public boolean expectInterruption;
    }

    /**
     * Command which retrieves an exit code off the queue and returns it, or INTERRUPTED if
     * interrupted more than --expect_interruptions times while waiting.
     */
    @Command(name = "snooze", shortDescription = "", help = "", options = { CommandInterruptionTest.WaitOptions.class })
    private static final class WaitForCompletionCommand implements BlazeCommand {
        private final AtomicBoolean isTestShuttingDown;

        private final AtomicReference<SettableFuture<CommandInterruptionTest.CommandState>> commandStateHandoff;

        public WaitForCompletionCommand(AtomicBoolean isTestShuttingDown) {
            this.isTestShuttingDown = isTestShuttingDown;
            this.commandStateHandoff = new AtomicReference<>();
        }

        @Override
        public BlazeCommandResult exec(CommandEnvironment env, OptionsParsingResult options) {
            CommandInterruptionTest.CommandState commandState = new CommandInterruptionTest.CommandState(env, options.getOptions(CommandInterruptionTest.WaitOptions.class).expectInterruption, isTestShuttingDown);
            commandStateHandoff.getAndSet(null).set(commandState);
            return BlazeCommandResult.exitCode(commandState.waitForExitCodeFromTest());
        }

        @Override
        public void editOptions(OptionsParser optionsParser) {
        }

        /**
         * Runs an instance of this command on the given executor, waits for it to start and returns a
         * CommandState which can be used to control and assert on the status of that command.
         */
        public CommandInterruptionTest.CommandState runIn(ExecutorService executor, BlazeCommandDispatcher dispatcher, boolean expectInterruption) throws InterruptedException, ExecutionException {
            SettableFuture<CommandInterruptionTest.CommandState> newHandoff = SettableFuture.create();
            if (!(commandStateHandoff.compareAndSet(null, newHandoff))) {
                throw new AssertionError("Another command is already starting at this time?!");
            }
            // static analysis wants us to check future return values
            @SuppressWarnings("unused")
            Future<?> ignoredCommandResult = executor.submit(new CommandInterruptionTest.RunCommandThroughDispatcher(dispatcher, newHandoff, expectInterruption));
            return newHandoff.get();
        }
    }

    /**
     * Callable to run the above command on a different thread.
     */
    private static final class RunCommandThroughDispatcher implements Callable<Integer> {
        private final BlazeCommandDispatcher dispatcher;

        private final SettableFuture<CommandInterruptionTest.CommandState> commandStateHandoff;

        private final boolean expectInterruption;

        public RunCommandThroughDispatcher(BlazeCommandDispatcher dispatcher, SettableFuture<CommandInterruptionTest.CommandState> commandStateHandoff, boolean expectInterruption) {
            this.dispatcher = dispatcher;
            this.commandStateHandoff = commandStateHandoff;
            this.expectInterruption = expectInterruption;
        }

        @Override
        public Integer call() throws Exception {
            int result;
            try {
                result = dispatcher.exec(ImmutableList.of("snooze", (expectInterruption ? "--expect_interruption" : "--noexpect_interruption")), "CommandInterruptionTest", SYSTEM_OUT_ERR).getExitCode().getNumericExitCode();
            } catch (Exception throwable) {
                if (commandStateHandoff.isDone()) {
                    commandStateHandoff.get().completeWithFailure(throwable);
                } else {
                    commandStateHandoff.setException(new IllegalStateException("The command failed with an exception before WaitForCompletionCommand started.", throwable));
                }
                throw throwable;
            }
            if (commandStateHandoff.isDone()) {
                commandStateHandoff.get().completeWithExitCode(result);
            } else {
                commandStateHandoff.setException(new IllegalStateException((("The command failed with exit code " + result) + " before WaitForCompletionCommand started.")));
            }
            return result;
        }
    }

    /**
     * A remote control allowing the test to control and assert on the WaitForCompletionCommand.
     */
    private static final class CommandState {
        private final SettableFuture<Integer> result;

        private final CommandEnvironment commandEnvironment;

        private final Thread thread;

        private final BlockingQueue<ExitCode> exitCodeQueue;

        private final AtomicBoolean isTestShuttingDown;

        private boolean expectInterruption;

        private final CyclicBarrier barrier;

        private static final ExitCode SENTINEL = ExitCode.createInfrastructureFailure((-1), "GO TO THE BARRIER");

        public CommandState(CommandEnvironment commandEnvironment, boolean expectInterruption, AtomicBoolean isTestShuttingDown) {
            this.result = SettableFuture.create();
            this.commandEnvironment = commandEnvironment;
            this.thread = Thread.currentThread();
            this.exitCodeQueue = new ArrayBlockingQueue<ExitCode>(1);
            this.isTestShuttingDown = isTestShuttingDown;
            this.expectInterruption = expectInterruption;
            this.barrier = new CyclicBarrier(2);
        }

        // command side
        /**
         * Marks the Future associated with this CommandState completed with the given exit code, then
         * waits at the barrier for the test thread to catch up.
         */
        private void completeWithExitCode(int exitCode) {
            result.set(exitCode);
            if (!(isTestShuttingDown.get())) {
                // Wait at the barrier for the test to assert on status, unless the test is shutting down.
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException ex) {
                    // this is fine, we're only doing this for the test thread's benefit anyway
                }
            }
        }

        /**
         * Marks the Future associated with this CommandState as having failed with the given exit code,
         * then waits at the barrier for the test thread to catch up.
         */
        private void completeWithFailure(Throwable throwable) {
            result.setException(throwable);
            if (!(isTestShuttingDown.get())) {
                // Wait at the barrier for the test to assert on status, unless the test is shutting down.
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException ex) {
                    // this is fine, we're only doing this for the test thread's benefit anyway
                }
            }
        }

        /**
         * Waits for an exit code to come from the test, either INTERRUPTED via thread interruption, or
         * a test-specified exit code via requestExitWith(). If expectInterruption was set,
         * a single interruption will be ignored.
         */
        private ExitCode waitForExitCodeFromTest() {
            while (true) {
                ExitCode exitCode = null;
                try {
                    exitCode = exitCodeQueue.take();
                    if (Thread.interrupted()) {
                        // the interruption and the exit code delivery may have come in simultaneously, which
                        // may result in a successful return from the queue with interrupted() set.
                        throw new InterruptedException();
                    }
                } catch (InterruptedException ex) {
                    if ((!(expectInterruption)) || (isTestShuttingDown.get())) {
                        // This is not an expected interruption (possibly because the test is shutting down and
                        // it's the executor's please stop interruption) so give up.
                        return ExitCode.INTERRUPTED;
                    }
                    // Otherwise, that was an expected interruption, so return to looking for exit codes.
                    // But we only expect one, so the next one will be fatal.
                    expectInterruption = false;
                    // We fall through the catch here in case we received an interruption and an exit code at
                    // the same time.
                }
                if (CommandInterruptionTest.CommandState.SENTINEL.equals(exitCode)) {
                    // The test just wants us to go wait at the barrier for an assertion.
                    try {
                        barrier.await();
                    } catch (InterruptedException | BrokenBarrierException impossible) {
                        // This should not happen in normal use, but if it does, exit gracefully so
                        // BlazeCommandDispatcher has a chance to clean up. Use the SENTINEL value to avoid
                        // accidentally passing any tests that might have been looking for INTERRUPTED.
                        return CommandInterruptionTest.CommandState.SENTINEL;
                    }
                    continue;
                } else
                    if (exitCode != null) {
                        return exitCode;
                    }

            } 
        }

        // test side
        /**
         * Gets the ModuleEnvironment modules will see when executing this command.
         */
        public ModuleEnvironment getModuleEnvironment() {
            return commandEnvironment.getBlazeModuleEnvironment();
        }

        /**
         * Sends an exit code to the command, which will then return with it if it is still running.
         */
        public void requestExitWith(ExitCode exitCode) {
            exitCodeQueue.offer(exitCode);
        }

        /**
         * Sends an interrupt directly to the command's thread.
         */
        public void interrupt() {
            thread.interrupt();
        }

        /**
         * Waits for the command to reach a stopping point to check if it has finished or not.
         */
        private void synchronizeWithCommand() throws InterruptedException, BrokenBarrierException {
            // If the future is already done, no need to wait at the barrier - we already know the state.
            if (result.isDone()) {
                // But if the command thread is waiting on the barrier, tell it to stop doing so.
                barrier.reset();
                return;
            }
            // Offer the sentinel to the queue - if the command is still waiting and it sees the sentinel,
            // it will go to the barrier.
            exitCodeQueue.offer(CommandInterruptionTest.CommandState.SENTINEL);
            // Then wait for the command to finish processing.
            barrier.await();
        }

        /**
         * Asserts that the command finished and returned the given ExitCode.
         */
        public void assertFinishedWith(ExitCode exitCode) throws InterruptedException, BrokenBarrierException, ExecutionException {
            synchronizeWithCommand();
            assertWithMessage("The command should have been finished, but it was not.").that(result.isDone()).isTrue();
            // TODO(mstaib): replace with Futures.getDone when Bazel uses Guava 20.0
            assertThat(result.get()).isEqualTo(exitCode.getNumericExitCode());
        }

        /**
         * Asserts that the command has not finished yet.
         */
        public void assertNotFinishedYet() throws InterruptedException, BrokenBarrierException, ExecutionException {
            synchronizeWithCommand();
            if (result.isDone()) {
                try {
                    throw new AssertionError(("The command should not have been finished, but it finished with exit code " + (result.get())));
                } catch (Throwable ex) {
                    throw new AssertionError("The command should not have been finished, but it threw", ex);
                }
            }
        }

        /**
         * Asserts that both commands were executed on the same thread.
         */
        public void assertOnSameThreadAs(CommandInterruptionTest.CommandState other) {
            assertThat(thread).isSameAs(other.thread);
        }
    }

    private ExecutorService executor;

    private AtomicBoolean isTestShuttingDown;

    private BlazeCommandDispatcher dispatcher;

    private CommandInterruptionTest.WaitForCompletionCommand snooze;

    // These tests are basically testing the functionality of the dummy command.
    @Test
    public void sendingExitCodeToTestCommandResultsInExitWithThatStatus() throws Exception {
        CommandInterruptionTest.CommandState command = /* expectInterruption= */
        snooze.runIn(executor, dispatcher, false);
        command.requestExitWith(SUCCESS);
        command.assertFinishedWith(SUCCESS);
    }

    @Test
    public void interruptingTestCommandMakesItExitWithInterruptedStatus() throws Exception {
        CommandInterruptionTest.CommandState command = /* expectInterruption= */
        snooze.runIn(executor, dispatcher, false);
        command.interrupt();
        command.assertFinishedWith(INTERRUPTED);
    }

    @Test
    public void commandIgnoresFirstInterruptionWhenExpectingInterruption() throws Exception {
        CommandInterruptionTest.CommandState command = /* expectInterruption= */
        snooze.runIn(executor, dispatcher, true);
        command.interrupt();
        command.assertNotFinishedYet();
        command.requestExitWith(SUCCESS);
        command.assertFinishedWith(SUCCESS);
    }

    @Test
    public void commandExitsWithInterruptedAfterInterruptionCountExceeded() throws Exception {
        CommandInterruptionTest.CommandState command = /* expectInterruption= */
        snooze.runIn(executor, dispatcher, true);
        command.interrupt();
        command.assertNotFinishedYet();
        command.interrupt();
        command.assertFinishedWith(INTERRUPTED);
    }

    // These tests get into the meat of actual abrupt exits.
    @Test
    public void exitForbidsNullException() throws Exception {
        CommandInterruptionTest.CommandState command = /* expectInterruption= */
        snooze.runIn(executor, dispatcher, false);
        try {
            command.getModuleEnvironment().exit(null);
            throw new AssertionError("It shouldn't be allowed to pass null to exit()!");
        } catch (NullPointerException expected) {
            // Good!
        }
        command.assertNotFinishedYet();
        command.requestExitWith(SUCCESS);
    }

    @Test
    public void exitForbidsNullExitCode() throws Exception {
        CommandInterruptionTest.CommandState command = /* expectInterruption= */
        snooze.runIn(executor, dispatcher, false);
        try {
            command.getModuleEnvironment().exit(new AbruptExitException("", null));
            throw new AssertionError("It shouldn't be allowed to pass an AbruptExitException with null ExitCode to exit()!");
        } catch (NullPointerException expected) {
            // Good!
        }
        command.assertNotFinishedYet();
        command.requestExitWith(SUCCESS);
    }

    @Test
    public void callingExitOnceInterruptsAndOverridesExitCode() throws Exception {
        CommandInterruptionTest.CommandState command = /* expectInterruption= */
        snooze.runIn(executor, dispatcher, false);
        command.getModuleEnvironment().exit(new AbruptExitException("", ExitCode.NO_TESTS_FOUND));
        command.assertFinishedWith(NO_TESTS_FOUND);
    }

    @Test
    public void callingExitSecondTimeNeitherInterruptsNorReOverridesExitCode() throws Exception {
        CommandInterruptionTest.CommandState command = /* expectInterruption= */
        snooze.runIn(executor, dispatcher, true);
        command.getModuleEnvironment().exit(new AbruptExitException("", ExitCode.NO_TESTS_FOUND));
        command.assertNotFinishedYet();
        command.getModuleEnvironment().exit(new AbruptExitException("", ExitCode.ANALYSIS_FAILURE));
        command.assertNotFinishedYet();
        command.requestExitWith(SUCCESS);
        command.assertFinishedWith(NO_TESTS_FOUND);
    }

    @Test
    public void abruptExitCodesDontOverrideInfrastructureFailures() throws Exception {
        CommandInterruptionTest.CommandState command = /* expectInterruption= */
        snooze.runIn(executor, dispatcher, true);
        command.getModuleEnvironment().exit(new AbruptExitException("", ExitCode.NO_TESTS_FOUND));
        command.assertNotFinishedYet();
        command.requestExitWith(BLAZE_INTERNAL_ERROR);
        command.assertFinishedWith(BLAZE_INTERNAL_ERROR);
    }

    @Test
    public void callingExitAfterCommandCompletesDoesNothing() throws Exception {
        CommandInterruptionTest.CommandState firstCommand = /* expectInterruption= */
        snooze.runIn(executor, dispatcher, false);
        firstCommand.requestExitWith(SUCCESS);
        firstCommand.assertFinishedWith(SUCCESS);
        CommandInterruptionTest.CommandState newCommandOnSameThread = /* expectInterruption= */
        snooze.runIn(executor, dispatcher, false);
        firstCommand.assertOnSameThreadAs(newCommandOnSameThread);
        firstCommand.getModuleEnvironment().exit(new AbruptExitException("", ExitCode.RUN_FAILURE));
        newCommandOnSameThread.assertNotFinishedYet();
        newCommandOnSameThread.requestExitWith(SUCCESS);
    }
}

