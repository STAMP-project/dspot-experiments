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
package com.google.devtools.build.lib.shell;


import CommandResult.EMPTY_OUTPUT;
import com.google.devtools.build.lib.testutil.MoreAsserts;
import java.time.Duration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link CommandResult}.
 */
@RunWith(JUnit4.class)
public final class CommandResultTest {
    @Test
    public void testBuilder_WithNoStderr() {
        Exception e = MoreAsserts.assertThrows(IllegalStateException.class, () -> CommandResult.builder().setStdoutStream(EMPTY_OUTPUT).setTerminationStatus(new TerminationStatus(0, false)).build());
        assertThat(e).hasMessageThat().contains("stderrStream");
    }

    @Test
    public void testBuilder_WithNoStdout() {
        Exception e = MoreAsserts.assertThrows(IllegalStateException.class, () -> CommandResult.builder().setStderrStream(EMPTY_OUTPUT).setTerminationStatus(new TerminationStatus(0, false)).build());
        assertThat(e).hasMessageThat().contains("stdoutStream");
    }

    @Test
    public void testBuilder_WithNoTerminationStatus() {
        Exception e = MoreAsserts.assertThrows(IllegalStateException.class, () -> CommandResult.builder().setStdoutStream(EMPTY_OUTPUT).setStderrStream(EMPTY_OUTPUT).build());
        assertThat(e).hasMessageThat().contains("terminationStatus");
    }

    @Test
    public void testBuilder_WithNoExecutionTime() {
        CommandResult commandResult = CommandResult.builder().setStdoutStream(EMPTY_OUTPUT).setStderrStream(EMPTY_OUTPUT).setTerminationStatus(new TerminationStatus(0, false)).build();
        assertThat(commandResult.getWallExecutionTime()).isEmpty();
        assertThat(commandResult.getUserExecutionTime()).isEmpty();
        assertThat(commandResult.getSystemExecutionTime()).isEmpty();
    }

    @Test
    public void testBuilder_WithExecutionTime() {
        CommandResult commandResult = CommandResult.builder().setStdoutStream(EMPTY_OUTPUT).setStderrStream(EMPTY_OUTPUT).setTerminationStatus(new TerminationStatus(0, false)).setWallExecutionTime(Duration.ofMillis(1929)).setUserExecutionTime(Duration.ofMillis(1492)).setSystemExecutionTime(Duration.ofMillis(1787)).build();
        assertThat(commandResult.getWallExecutionTime()).hasValue(Duration.ofMillis(1929));
        assertThat(commandResult.getUserExecutionTime()).hasValue(Duration.ofMillis(1492));
        assertThat(commandResult.getSystemExecutionTime()).hasValue(Duration.ofMillis(1787));
    }
}

