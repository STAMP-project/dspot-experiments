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
package com.google.devtools.build.lib.blackbox.framework;


import com.google.common.collect.Lists;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test of {@link ProcessRunner}
 */
@RunWith(JUnit4.class)
public final class ProcessRunnerTest {
    private static ExecutorService executorService;

    private Path directory;

    private Path path;

    @Test
    public void testSuccess() throws Exception {
        Files.write(path, /* exit code */
        /* output */
        /* error */
        ProcessRunnerTest.createScriptText(0, "Hello!", null));
        ProcessParameters parameters = createBuilder().build();
        ProcessResult result = new ProcessRunner(parameters, ProcessRunnerTest.executorService).runSynchronously();
        assertThat(result.exitCode()).isEqualTo(0);
        assertThat(result.outString()).isEqualTo("Hello!");
        assertThat(result.errString()).isEmpty();
    }

    @Test
    public void testFailureWithCode() throws Exception {
        Files.write(path, /* exit code */
        /* output */
        /* error */
        ProcessRunnerTest.createScriptText(124, null, "Failure"));
        ProcessParameters parameters = createBuilder().setExpectedExitCode(124).setExpectedEmptyError(false).build();
        ProcessResult result = new ProcessRunner(parameters, ProcessRunnerTest.executorService).runSynchronously();
        assertThat(result.exitCode()).isEqualTo(124);
        assertThat(result.outString()).isEmpty();
        assertThat(result.errString()).isEqualTo("Failure");
    }

    @Test
    public void testFailure() throws Exception {
        Files.write(path, /* exit code */
        /* output */
        /* error */
        ProcessRunnerTest.createScriptText(124, null, "Failure"));
        ProcessParameters parameters = createBuilder().setExpectedToFail(true).setExpectedEmptyError(false).build();
        ProcessResult result = new ProcessRunner(parameters, ProcessRunnerTest.executorService).runSynchronously();
        assertThat(result.exitCode()).isEqualTo(124);
        assertThat(result.outString()).isEmpty();
        assertThat(result.errString()).isEqualTo("Failure");
    }

    @Test
    public void testTimeout() throws Exception {
        // Windows script to sleep 5 seconds, so that we can test timeout.
        // This script finds PowerShell using %systemroot% variable, which we assume is always
        // defined. It passes some standard parameters like input and output formats,
        // important part is the Command parameter, which actually calls Sleep from PowerShell.
        String windowsScript = "%systemroot%\\system32\\cmd.exe /C \"start /I /B powershell" + (" -Version 3.0 -NoLogo -Sta -NoProfile -InputFormat Text -OutputFormat Text" + " -NonInteractive -Command \"\"&PowerShell Sleep 5\"");
        Files.write(path, Collections.singleton((ProcessRunnerTest.isWindows() ? windowsScript : "read smthg")));
        ProcessParameters parameters = createBuilder().setExpectedExitCode((-1)).setExpectedEmptyError(false).setTimeoutMillis(100).build();
        try {
            new ProcessRunner(parameters, ProcessRunnerTest.executorService).runSynchronously();
            assertThat(false).isTrue();
        } catch (TimeoutException e) {
            // ignore
        }
    }

    @Test
    public void testRedirect() throws Exception {
        Files.write(path, /* exit code */
        /* output */
        /* error */
        ProcessRunnerTest.createScriptText(12, Lists.newArrayList("Info", "Multi", "line"), Collections.singletonList("Failure")));
        Path out = directory.resolve("out.txt");
        Path err = directory.resolve("err.txt");
        try {
            ProcessParameters parameters = createBuilder().setExpectedExitCode(12).setExpectedEmptyError(false).setRedirectOutput(out).setRedirectError(err).build();
            ProcessResult result = new ProcessRunner(parameters, ProcessRunnerTest.executorService).runSynchronously();
            assertThat(result.exitCode()).isEqualTo(12);
            assertThat(result.outString()).isEqualTo("Info\nMulti\nline");
            assertThat(result.errString()).isEqualTo("Failure");
        } finally {
            Files.delete(out);
            Files.delete(err);
        }
    }
}

