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
package com.google.devtools.build.lib.windows;


import com.google.common.collect.ImmutableList;
import com.google.devtools.build.lib.testutil.TestSpec;
import com.google.devtools.build.lib.util.OS;
import com.google.devtools.build.lib.windows.jni.WindowsProcesses;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link WindowsProcesses}.
 */
@RunWith(JUnit4.class)
@TestSpec(localOnly = true, supportedOs = OS.WINDOWS)
public class WindowsProcessesTest {
    private String mockSubprocess;

    private String mockBinary;

    private long process;

    @Test
    public void testDoesNotQuoteSimpleArg() throws Exception {
        assertThat(WindowsProcesses.quoteCommandLine(ImmutableList.of("x", "a"))).isEqualTo("x a");
    }

    @Test
    public void testQuotesEmptyArg() throws Exception {
        assertThat(WindowsProcesses.quoteCommandLine(ImmutableList.of("x", ""))).isEqualTo("x \"\"");
    }

    @Test
    public void testQuotesArgWithSpace() throws Exception {
        assertThat(WindowsProcesses.quoteCommandLine(ImmutableList.of("x", "a b"))).isEqualTo("x \"a b\"");
    }

    @Test
    public void testDoesNotQuoteArgWithBackslash() throws Exception {
        assertThat(WindowsProcesses.quoteCommandLine(ImmutableList.of("x", "a\\b"))).isEqualTo("x a\\b");
    }

    @Test
    public void testDoesNotQuoteArgWithSingleQuote() throws Exception {
        assertThat(WindowsProcesses.quoteCommandLine(ImmutableList.of("x", "a'b"))).isEqualTo("x a'b");
    }

    @Test
    public void testDoesNotQuoteArgWithDoubleQuote() throws Exception {
        assertThat(WindowsProcesses.quoteCommandLine(ImmutableList.of("x", "a\"b"))).isEqualTo("x a\\\"b");
    }

    @Test
    public void testSmoke() throws Exception {
        process = WindowsProcesses.createProcess(mockBinary, mockArgs("Ia5", "Oa"), null, null, null, null);
        assertNoProcessError();
        byte[] input = "HELLO".getBytes(StandardCharsets.UTF_8);
        byte[] output = new byte[5];
        WindowsProcesses.writeStdin(process, input, 0, 5);
        assertNoProcessError();
        readStdout(output, 0, 5);
        assertNoStreamError(WindowsProcesses.getStdout(process));
        assertThat(new String(output, StandardCharsets.UTF_8)).isEqualTo("HELLO");
    }

    @Test
    public void testPingpong() throws Exception {
        List<String> args = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            args.add("Ia3");
            args.add("Oa");
        }
        process = WindowsProcesses.createProcess(mockBinary, mockArgs(args.toArray(new String[]{  })), null, null, null, null);
        for (int i = 0; i < 100; i++) {
            byte[] input = String.format("%03d", i).getBytes(StandardCharsets.UTF_8);
            assertThat(input.length).isEqualTo(3);
            assertThat(WindowsProcesses.writeStdin(process, input, 0, 3)).isEqualTo(3);
            byte[] output = new byte[3];
            assertThat(readStdout(output, 0, 3)).isEqualTo(3);
            assertThat(Integer.parseInt(new String(output, StandardCharsets.UTF_8))).isEqualTo(i);
        }
    }

    @Test
    public void testExitCode() throws Exception {
        process = WindowsProcesses.createProcess(mockBinary, mockArgs("X42"), null, null, null, null);
        assertThat(WindowsProcesses.waitFor(process, (-1))).isEqualTo(0);
        assertThat(WindowsProcesses.getExitCode(process)).isEqualTo(42);
        assertNoProcessError();
    }

    @Test
    public void testPartialRead() throws Exception {
        process = WindowsProcesses.createProcess(mockBinary, mockArgs("O-HELLO"), null, null, null, null);
        byte[] one = new byte[2];
        byte[] two = new byte[3];
        assertThat(readStdout(one, 0, 2)).isEqualTo(2);
        assertNoStreamError(WindowsProcesses.getStdout(process));
        assertThat(readStdout(two, 0, 3)).isEqualTo(3);
        assertNoStreamError(WindowsProcesses.getStdout(process));
        assertThat(new String(one, StandardCharsets.UTF_8)).isEqualTo("HE");
        assertThat(new String(two, StandardCharsets.UTF_8)).isEqualTo("LLO");
    }

    @Test
    public void testArrayOutOfBounds() throws Exception {
        process = WindowsProcesses.createProcess(mockBinary, mockArgs("O-oob"), null, null, null, null);
        byte[] buf = new byte[3];
        assertThat(readStdout(buf, (-1), 3)).isEqualTo((-1));
        assertThat(readStdout(buf, 0, 5)).isEqualTo((-1));
        assertThat(readStdout(buf, 4, 1)).isEqualTo((-1));
        assertThat(readStdout(buf, 2, (-1))).isEqualTo((-1));
        assertThat(readStdout(buf, Integer.MAX_VALUE, 2)).isEqualTo((-1));
        assertThat(readStdout(buf, 2, Integer.MAX_VALUE)).isEqualTo((-1));
        assertThat(readStderr(buf, (-1), 3)).isEqualTo((-1));
        assertThat(readStderr(buf, 0, 5)).isEqualTo((-1));
        assertThat(readStderr(buf, 4, 1)).isEqualTo((-1));
        assertThat(readStderr(buf, 2, (-1))).isEqualTo((-1));
        assertThat(readStderr(buf, Integer.MAX_VALUE, 2)).isEqualTo((-1));
        assertThat(readStderr(buf, 2, Integer.MAX_VALUE)).isEqualTo((-1));
        assertThat(WindowsProcesses.writeStdin(process, buf, (-1), 3)).isEqualTo((-1));
        assertThat(WindowsProcesses.writeStdin(process, buf, 0, 5)).isEqualTo((-1));
        assertThat(WindowsProcesses.writeStdin(process, buf, 4, 1)).isEqualTo((-1));
        assertThat(WindowsProcesses.writeStdin(process, buf, 2, (-1))).isEqualTo((-1));
        assertThat(WindowsProcesses.writeStdin(process, buf, Integer.MAX_VALUE, 2)).isEqualTo((-1));
        assertThat(WindowsProcesses.writeStdin(process, buf, 2, Integer.MAX_VALUE)).isEqualTo((-1));
        assertThat(readStdout(buf, 0, 3)).isEqualTo(3);
        assertThat(new String(buf, StandardCharsets.UTF_8)).isEqualTo("oob");
    }

    @Test
    public void testOffsetedOps() throws Exception {
        process = WindowsProcesses.createProcess(mockBinary, mockArgs("Ia3", "Oa"), null, null, null, null);
        byte[] input = "01234".getBytes(StandardCharsets.UTF_8);
        byte[] output = "abcde".getBytes(StandardCharsets.UTF_8);
        assertThat(WindowsProcesses.writeStdin(process, input, 1, 3)).isEqualTo(3);
        assertNoProcessError();
        int rv = readStdout(output, 1, 3);
        assertNoProcessError();
        assertThat(rv).isEqualTo(3);
        assertThat(new String(output, StandardCharsets.UTF_8)).isEqualTo("a123e");
    }

    @Test
    public void testParallelStdoutAndStderr() throws Exception {
        process = WindowsProcesses.createProcess(mockBinary, mockArgs("O-out1", "E-err1", "O-out2", "E-err2", "E-err3", "O-out3", "E-err4", "O-out4"), null, null, null, null);
        assertNoProcessError();
        byte[] buf = new byte[4];
        assertThat(readStdout(buf, 0, 4)).isEqualTo(4);
        assertThat(new String(buf, StandardCharsets.UTF_8)).isEqualTo("out1");
        assertThat(readStderr(buf, 0, 4)).isEqualTo(4);
        assertThat(new String(buf, StandardCharsets.UTF_8)).isEqualTo("err1");
        assertThat(readStderr(buf, 0, 4)).isEqualTo(4);
        assertThat(new String(buf, StandardCharsets.UTF_8)).isEqualTo("err2");
        assertThat(readStdout(buf, 0, 4)).isEqualTo(4);
        assertThat(new String(buf, StandardCharsets.UTF_8)).isEqualTo("out2");
        assertThat(readStdout(buf, 0, 4)).isEqualTo(4);
        assertThat(new String(buf, StandardCharsets.UTF_8)).isEqualTo("out3");
        assertThat(readStderr(buf, 0, 4)).isEqualTo(4);
        assertThat(new String(buf, StandardCharsets.UTF_8)).isEqualTo("err3");
        assertThat(readStderr(buf, 0, 4)).isEqualTo(4);
        assertThat(new String(buf, StandardCharsets.UTF_8)).isEqualTo("err4");
        assertThat(readStdout(buf, 0, 4)).isEqualTo(4);
        assertThat(new String(buf, StandardCharsets.UTF_8)).isEqualTo("out4");
    }

    @Test
    public void testExecutableNotFound() throws Exception {
        process = WindowsProcesses.createProcess("ThisExecutableDoesNotExist", "TheseArgsDontMatter", null, null, null, null);
        assertThat(WindowsProcesses.processGetLastError(process)).contains("The system cannot find the file specified.");
        byte[] buf = new byte[1];
        assertThat(readStdout(buf, 0, 1)).isEqualTo(0);
    }

    @Test
    public void testReadingAndWritingAfterTermination() throws Exception {
        process = WindowsProcesses.createProcess(mockBinary, mockArgs("X42"), null, null, null, null);
        byte[] buf = new byte[1];
        assertThat(readStdout(buf, 0, 1)).isEqualTo(0);
        assertThat(readStderr(buf, 0, 1)).isEqualTo(0);
        assertThat(WindowsProcesses.writeStdin(process, buf, 0, 1)).isEqualTo((-1));
    }

    @Test
    public void testNewEnvironmentVariables() throws Exception {
        byte[] data = "ONE=one\u0000TWO=twotwo\u0000\u0000".getBytes(StandardCharsets.UTF_16LE);
        process = WindowsProcesses.createProcess(mockBinary, mockArgs("O$ONE", "O$TWO"), data, null, null, null);
        assertNoProcessError();
        byte[] buf = new byte[3];
        assertThat(readStdout(buf, 0, 3)).isEqualTo(3);
        assertThat(new String(buf, StandardCharsets.UTF_8)).isEqualTo("one");
        buf = new byte[6];
        assertThat(readStdout(buf, 0, 6)).isEqualTo(6);
        assertThat(new String(buf, StandardCharsets.UTF_8)).isEqualTo("twotwo");
    }

    @Test
    public void testNoZeroInEnvBuffer() throws Exception {
        byte[] data = "clown".getBytes(StandardCharsets.UTF_16LE);
        process = WindowsProcesses.createProcess(mockBinary, mockArgs(), data, null, null, null);
        assertThat(WindowsProcesses.processGetLastError(process)).isNotEmpty();
    }

    @Test
    public void testMissingFinalDoubleZeroInEnvBuffer() throws Exception {
        byte[] data = "FOO=bar\u0000".getBytes(StandardCharsets.UTF_16LE);
        process = WindowsProcesses.createProcess(mockBinary, mockArgs(), data, null, null, null);
        assertThat(WindowsProcesses.processGetLastError(process)).isNotEmpty();
    }

    @Test
    public void testOneByteEnvBuffer() throws Exception {
        byte[] data = "a".getBytes(StandardCharsets.UTF_16LE);
        process = WindowsProcesses.createProcess(mockBinary, mockArgs(), data, null, null, null);
        assertThat(WindowsProcesses.processGetLastError(process)).isNotEmpty();
    }

    @Test
    public void testOneZeroEnvBuffer() throws Exception {
        byte[] data = "\u0000".getBytes(StandardCharsets.UTF_16LE);
        process = WindowsProcesses.createProcess(mockBinary, mockArgs(), data, null, null, null);
        assertThat(WindowsProcesses.processGetLastError(process)).isNotEmpty();
    }

    @Test
    public void testTwoZerosInEnvBuffer() throws Exception {
        byte[] data = "\u0000\u0000".getBytes(StandardCharsets.UTF_16LE);
        process = WindowsProcesses.createProcess(mockBinary, mockArgs(), data, null, null, null);
        assertThat(WindowsProcesses.processGetLastError(process)).isEmpty();
    }

    @Test
    public void testRedirect() throws Exception {
        String stdoutFile = (System.getenv("TEST_TMPDIR")) + "\\stdout_redirect";
        String stderrFile = (System.getenv("TEST_TMPDIR")) + "\\stderr_redirect";
        process = WindowsProcesses.createProcess(mockBinary, mockArgs("O-one", "E-two"), null, null, stdoutFile, stderrFile);
        assertThat(process).isGreaterThan(0L);
        assertNoProcessError();
        assertThat(WindowsProcesses.waitFor(process, (-1))).isEqualTo(0);
        WindowsProcesses.getExitCode(process);
        assertNoProcessError();
        byte[] stdout = Files.readAllBytes(Paths.get(stdoutFile));
        byte[] stderr = Files.readAllBytes(Paths.get(stderrFile));
        assertThat(new String(stdout, StandardCharsets.UTF_8)).isEqualTo("one");
        assertThat(new String(stderr, StandardCharsets.UTF_8)).isEqualTo("two");
    }

    @Test
    public void testRedirectToSameFile() throws Exception {
        String file = (System.getenv("TEST_TMPDIR")) + "\\captured_";
        process = WindowsProcesses.createProcess(mockBinary, mockArgs("O-one", "E-two"), null, null, file, file);
        assertThat(process).isGreaterThan(0L);
        assertNoProcessError();
        assertThat(WindowsProcesses.waitFor(process, (-1))).isEqualTo(0);
        WindowsProcesses.getExitCode(process);
        assertNoProcessError();
        byte[] bytes = Files.readAllBytes(Paths.get(file));
        assertThat(new String(bytes, StandardCharsets.UTF_8)).isEqualTo("onetwo");
    }

    @Test
    public void testReadingFromRedirectedStreams() throws Exception {
        String stdoutFile = (System.getenv("TEST_TMPDIR")) + "\\captured_stdout";
        String stderrFile = (System.getenv("TEST_TMPDIR")) + "\\captured_stderr";
        process = WindowsProcesses.createProcess(mockBinary, mockArgs("O-one", "E-two"), null, null, stdoutFile, stderrFile);
        assertNoProcessError();
        byte[] buf = new byte[1];
        assertThat(readStdout(buf, 0, 1)).isEqualTo(0);
        assertThat(readStderr(buf, 0, 1)).isEqualTo(0);
        WindowsProcesses.waitFor(process, (-1));
    }

    @Test
    public void testRedirectedErrorStream() throws Exception {
        process = WindowsProcesses.createProcess(mockBinary, mockArgs("O-one", "E-two"), null, null, null, null, true);
        assertNoProcessError();
        byte[] buf = new byte[6];
        assertThat(readStdout(buf, 0, 3)).isEqualTo(3);
        assertThat(readStdout(buf, 3, 3)).isEqualTo(3);
        assertThat(new String(buf, StandardCharsets.UTF_8)).isEqualTo("onetwo");
        assertThat(readStderr(buf, 0, 1)).isEqualTo(0);
        WindowsProcesses.waitFor(process, (-1));
    }

    @Test
    public void testAppendToExistingFile() throws Exception {
        String stdoutFile = (System.getenv("TEST_TMPDIR")) + "\\stdout_atef";
        String stderrFile = (System.getenv("TEST_TMPDIR")) + "\\stderr_atef";
        Path stdout = Paths.get(stdoutFile);
        Path stderr = Paths.get(stderrFile);
        Files.write(stdout, "out1".getBytes(StandardCharsets.UTF_8));
        Files.write(stderr, "err1".getBytes(StandardCharsets.UTF_8));
        process = WindowsProcesses.createProcess(mockBinary, mockArgs("O-out2", "E-err2"), null, null, stdoutFile, stderrFile);
        assertNoProcessError();
        WindowsProcesses.waitFor(process, (-1));
        WindowsProcesses.getExitCode(process);
        assertNoProcessError();
        byte[] stdoutBytes = Files.readAllBytes(Paths.get(stdoutFile));
        byte[] stderrBytes = Files.readAllBytes(Paths.get(stderrFile));
        assertThat(new String(stdoutBytes, StandardCharsets.UTF_8)).isEqualTo("out1out2");
        assertThat(new String(stderrBytes, StandardCharsets.UTF_8)).isEqualTo("err1err2");
    }

    @Test
    public void testCwd() throws Exception {
        String dir1 = (System.getenv("TEST_TMPDIR")) + "/dir1";
        new File(dir1).mkdir();
        process = WindowsProcesses.createProcess(mockBinary, mockArgs("O."), null, dir1, null, null);
        assertNoProcessError();
        byte[] buf = new byte[1024];// Windows MAX_PATH is 260, but whatever

        int len = readStdout(buf, 0, 1024);
        assertNoProcessError();
        assertThat(new String(buf, 0, len, StandardCharsets.UTF_8).replace("\\", "/")).isEqualTo(dir1);
    }

    @Test
    public void testTimeout() throws Exception {
        process = WindowsProcesses.createProcess(mockBinary, mockArgs("W5", "X0"), null, null, null, null);
        assertThat(WindowsProcesses.waitFor(process, 1000)).isEqualTo(1);
    }
}

