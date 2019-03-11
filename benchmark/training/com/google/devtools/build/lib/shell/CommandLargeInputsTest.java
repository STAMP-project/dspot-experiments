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


import Command.KILL_SUBPROCESS_ON_INTERRUPT;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests the command class with large inputs
 */
@RunWith(JUnit4.class)
public class CommandLargeInputsTest {
    @Test
    public void testCatRandomBinaryToOutputStream() throws Exception {
        final Command command = new Command(new String[]{ "cat" });
        byte[] randomBytes = getRandomBytes();
        ByteArrayInputStream in = new ByteArrayInputStream(randomBytes);
        CommandResult result = command.executeAsync(in, KILL_SUBPROCESS_ON_INTERRUPT).get();
        assertThat(result.getTerminationStatus().getRawExitCode()).isEqualTo(0);
        TestUtil.assertArrayEquals(randomBytes, result.getStdout());
        assertThat(result.getStderr()).isEmpty();
    }

    @Test
    public void testCatRandomBinaryToErrorStream() throws Exception {
        final Command command = new Command(new String[]{ "/bin/sh", "-c", "cat >&2" });
        byte[] randomBytes = getRandomBytes();
        ByteArrayInputStream in = new ByteArrayInputStream(randomBytes);
        CommandResult result = command.executeAsync(in, KILL_SUBPROCESS_ON_INTERRUPT).get();
        assertThat(result.getTerminationStatus().getRawExitCode()).isEqualTo(0);
        TestUtil.assertArrayEquals(randomBytes, result.getStderr());
        assertThat(result.getStdout()).isEmpty();
    }

    @Test
    public void testCatRandomBinaryFromInputStreamToOutputStream() throws Exception {
        final Command command = new Command(new String[]{ "cat" });
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        byte[] randomBytes = getRandomBytes();
        ByteArrayInputStream in = new ByteArrayInputStream(randomBytes);
        CommandResult result = command.executeAsync(in, out, err, KILL_SUBPROCESS_ON_INTERRUPT).get();
        assertThat(result.getTerminationStatus().getRawExitCode()).isEqualTo(0);
        assertThat(err.toByteArray()).isEmpty();
        TestUtil.assertArrayEquals(randomBytes, out.toByteArray());
        assertOutAndErrNotAvailable(result);
    }

    @Test
    public void testCatRandomBinaryFromInputStreamToErrorStream() throws Exception {
        final Command command = new Command(new String[]{ "/bin/sh", "-c", "cat >&2" });
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        byte[] randomBytes = getRandomBytes();
        ByteArrayInputStream in = new ByteArrayInputStream(randomBytes);
        CommandResult result = command.executeAsync(in, out, err, KILL_SUBPROCESS_ON_INTERRUPT).get();
        assertThat(result.getTerminationStatus().getRawExitCode()).isEqualTo(0);
        assertThat(out.toByteArray()).isEmpty();
        TestUtil.assertArrayEquals(randomBytes, err.toByteArray());
        assertOutAndErrNotAvailable(result);
    }

    @Test
    public void testStdoutInterleavedWithStdErr() throws Exception {
        final Command command = new Command(new String[]{ "/bin/bash", "-c", "for i in $( seq 0 999); do (echo OUT$i >&1) && (echo ERR$i  >&2); done" });
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        command.execute(out, err);
        StringBuilder expectedOut = new StringBuilder();
        StringBuilder expectedErr = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            expectedOut.append("OUT").append(i).append("\n");
            expectedErr.append("ERR").append(i).append("\n");
        }
        assertThat(out.toString("UTF-8")).isEqualTo(expectedOut.toString());
        assertThat(err.toString("UTF-8")).isEqualTo(expectedErr.toString());
    }

    @Test
    public void testCatAllByteValues() throws Exception {
        final Command command = new Command(new String[]{ "cat" });
        byte[] allByteValues = getAllByteValues();
        ByteArrayInputStream in = new ByteArrayInputStream(allByteValues);
        CommandResult result = command.executeAsync(in, KILL_SUBPROCESS_ON_INTERRUPT).get();
        assertThat(result.getTerminationStatus().getRawExitCode()).isEqualTo(0);
        assertThat(result.getStderr()).isEmpty();
        TestUtil.assertArrayEquals(allByteValues, result.getStdout());
    }
}

