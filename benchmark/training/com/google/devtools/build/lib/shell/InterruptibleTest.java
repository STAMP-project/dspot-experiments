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
package com.google.devtools.build.lib.shell;


import Command.CONTINUE_SUBPROCESS_ON_INTERRUPT;
import Command.NO_INPUT;
import com.google.devtools.build.lib.util.OS;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests of the interaction of Thread.interrupt and Command.execute.
 *
 * Read http://www.ibm.com/developerworks/java/library/j-jtp05236/
 * for background material.
 *
 * NOTE: This test is dependent on thread timings.  Under extreme machine load
 * it's possible that this test could fail spuriously or intermittently.  In
 * that case, adjust the timing constants to increase the tolerance.
 */
@RunWith(JUnit4.class)
public class InterruptibleTest {
    private final Thread mainThread = Thread.currentThread();

    // Interrupt main thread after 1 second.  Hopefully by then /bin/sleep
    // should be running.
    private final Thread interrupter = new Thread() {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);// 1 sec

            } catch (InterruptedException e) {
                throw new IllegalStateException("Unexpected interrupt!");
            }
            mainThread.interrupt();
        }
    };

    private Command command;

    /**
     * Test that interrupting a thread in an "uninterruptible" Command.execute marks the thread as
     * interrupted, and does not terminate the subprocess.
     */
    @Test
    public void testUninterruptibleCommandRunsToCompletion() throws Exception {
        Assume.assumeTrue(((OS.getCurrent()) != (OS.WINDOWS)));
        CommandResult result = command.executeAsync(NO_INPUT, CONTINUE_SUBPROCESS_ON_INTERRUPT).get();
        assertThat(result.getTerminationStatus().success()).isTrue();
        assertThat(result.getStderr()).isEmpty();
        assertThat(result.getStdout()).isEmpty();
        // The interrupter thread should have exited about 1000ms ago.
        assertWithMessage("Interrupter thread is still alive!").that(interrupter.isAlive()).isFalse();
        // The interrupter thread should have set the main thread's interrupt flag.
        assertWithMessage("Main thread was not interrupted during command execution!").that(mainThread.isInterrupted()).isTrue();
    }

    /**
     * Test that interrupting a thread in an "interruptible" Command.execute does terminate the
     * subprocess, and also marks the thread as interrupted.
     */
    @Test
    public void testInterruptibleCommandRunsToCompletion() throws Exception {
        Assume.assumeTrue(((OS.getCurrent()) != (OS.WINDOWS)));
        try {
            command.execute();
            Assert.fail();
        } catch (AbnormalTerminationException expected) {
            assertThat(expected).hasMessageThat().isEqualTo("Process terminated by signal 15");
            assertThat(expected.getResult().getTerminationStatus().exited()).isFalse();
        }
        // The interrupter thread should have set the main thread's interrupt flag.
        assertWithMessage("Main thread was not interrupted during command execution!").that(mainThread.isInterrupted()).isTrue();
    }
}

