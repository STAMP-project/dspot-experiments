/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.util;


import Shell.ShellCommandExecutor;
import com.google.common.base.Supplier;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.Timeout;

import static org.apache.hadoop.util.Shell.Shell.WINDOWS;
import static org.apache.hadoop.util.Shell.Shell.isSetsidAvailable;


public class TestShell extends Assert {
    /**
     * Set the timeout for every test
     */
    @Rule
    public Timeout testTimeout = new Timeout(30000);

    @Rule
    public TestName methodName = new TestName();

    private File rootTestDir = GenericTestUtils.getTestDir();

    /**
     * A filename generated uniquely for each test method. The file
     * itself is neither created nor deleted during test setup/teardown.
     */
    private File methodDir;

    private static class Command extends Shell.Shell {
        private int runCount = 0;

        private Command(long interval) {
            super(interval);
        }

        @Override
        protected String[] getExecString() {
            // There is no /bin/echo equivalent on Windows so just launch it as a
            // shell built-in.
            // 
            return WINDOWS ? new String[]{ "cmd.exe", "/c", "echo", "hello" } : new String[]{ "echo", "hello" };
        }

        @Override
        protected void parseExecResult(BufferedReader lines) throws IOException {
            ++(runCount);
        }

        public int getRunCount() {
            return runCount;
        }
    }

    @Test
    public void testInterval() throws IOException {
        testInterval(((Long.MIN_VALUE) / 60000));// test a negative interval

        testInterval(0L);// test a zero interval

        testInterval(10L);// interval equal to 10mins

        testInterval((((Time.now()) / 60000) + 60));// test a very big interval

    }

    @Test
    public void testShellCommandExecutorToString() throws Throwable {
        Shell.Shell.ShellCommandExecutor sce = new Shell.Shell.ShellCommandExecutor(new String[]{ "ls", "..", "arg 2" });
        String command = sce.toString();
        assertInString(command, "ls");
        assertInString(command, " .. ");
        assertInString(command, "\"arg 2\"");
    }

    @Test
    public void testShellCommandTimeout() throws Throwable {
        Assume.assumeFalse(WINDOWS);
        String rootDir = rootTestDir.getAbsolutePath();
        File shellFile = new File(rootDir, "timeout.sh");
        String timeoutCommand = "sleep 4; echo \"hello\"";
        Shell.Shell.ShellCommandExecutor shexc;
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(shellFile))) {
            writer.println(timeoutCommand);
            writer.close();
        }
        FileUtil.setExecutable(shellFile, true);
        shexc = new Shell.Shell.ShellCommandExecutor(new String[]{ shellFile.getAbsolutePath() }, null, null, 100);
        try {
            shexc.execute();
        } catch (Exception e) {
            // When timing out exception is thrown.
        }
        shellFile.delete();
        Assert.assertTrue("Script did not timeout", shexc.isTimedOut());
    }

    @Test
    public void testEnvVarsWithInheritance() throws Exception {
        Assume.assumeFalse(WINDOWS);
        testEnvHelper(true);
    }

    @Test
    public void testEnvVarsWithoutInheritance() throws Exception {
        Assume.assumeFalse(WINDOWS);
        testEnvHelper(false);
    }

    @Test
    public void testShellCommandTimerLeak() throws Exception {
        String[] quickCommand = new String[]{ "/bin/sleep", "100" };
        int timersBefore = TestShell.countTimerThreads();
        System.err.println(("before: " + timersBefore));
        for (int i = 0; i < 10; i++) {
            Shell.Shell.ShellCommandExecutor shexec = new Shell.Shell.ShellCommandExecutor(quickCommand, null, null, 1);
            try {
                shexec.execute();
                Assert.fail("Bad command should throw exception");
            } catch (Exception e) {
                // expected
            }
        }
        Thread.sleep(1000);
        int timersAfter = TestShell.countTimerThreads();
        System.err.println(("after: " + timersAfter));
        Assert.assertEquals(timersBefore, timersAfter);
    }

    @Test
    public void testGetCheckProcessIsAliveCommand() throws Exception {
        String anyPid = "9999";
        String[] checkProcessAliveCommand = getCheckProcessIsAliveCommand(anyPid);
        String[] expectedCommand;
        if (WINDOWS) {
            expectedCommand = new String[]{ getWinUtilsPath(), "task", "isAlive", anyPid };
        } else
            if (isSetsidAvailable) {
                expectedCommand = new String[]{ "bash", "-c", ("kill -0 -- -'" + anyPid) + "'" };
            } else {
                expectedCommand = new String[]{ "bash", "-c", ("kill -0 '" + anyPid) + "'" };
            }

        Assert.assertArrayEquals(expectedCommand, checkProcessAliveCommand);
    }

    @Test
    public void testGetSignalKillCommand() throws Exception {
        String anyPid = "9999";
        int anySignal = 9;
        String[] checkProcessAliveCommand = getSignalKillCommand(anySignal, anyPid);
        String[] expectedCommand;
        if (WINDOWS) {
            expectedCommand = new String[]{ getWinUtilsPath(), "task", "kill", anyPid };
        } else
            if (isSetsidAvailable) {
                expectedCommand = new String[]{ "bash", "-c", ("kill -9 -- -'" + anyPid) + "'" };
            } else {
                expectedCommand = new String[]{ "bash", "-c", ("kill -9 '" + anyPid) + "'" };
            }

        Assert.assertArrayEquals(expectedCommand, checkProcessAliveCommand);
    }

    @Test
    public void testHadoopHomeUnset() throws Throwable {
        assertHomeResolveFailed(null, "unset");
    }

    @Test
    public void testHadoopHomeEmpty() throws Throwable {
        assertHomeResolveFailed("", E_HADOOP_PROPS_EMPTY);
    }

    @Test
    public void testHadoopHomeEmptyDoubleQuotes() throws Throwable {
        assertHomeResolveFailed("\"\"", E_HADOOP_PROPS_EMPTY);
    }

    @Test
    public void testHadoopHomeEmptySingleQuote() throws Throwable {
        assertHomeResolveFailed("\"", E_HADOOP_PROPS_EMPTY);
    }

    @Test
    public void testHadoopHomeValid() throws Throwable {
        File f = checkHadoopHomeInner(rootTestDir.getCanonicalPath());
        Assert.assertEquals(rootTestDir, f);
    }

    @Test
    public void testHadoopHomeValidQuoted() throws Throwable {
        File f = checkHadoopHomeInner((('"' + (rootTestDir.getCanonicalPath())) + '"'));
        Assert.assertEquals(rootTestDir, f);
    }

    @Test
    public void testHadoopHomeNoDir() throws Throwable {
        assertHomeResolveFailed(methodDir.getCanonicalPath(), E_DOES_NOT_EXIST);
    }

    @Test
    public void testHadoopHomeNotADir() throws Throwable {
        File touched = touch(methodDir);
        try {
            assertHomeResolveFailed(touched.getCanonicalPath(), E_NOT_DIRECTORY);
        } finally {
            FileUtils.deleteQuietly(touched);
        }
    }

    @Test
    public void testHadoopHomeRelative() throws Throwable {
        assertHomeResolveFailed("./target", E_IS_RELATIVE);
    }

    @Test
    public void testBinDirMissing() throws Throwable {
        FileNotFoundException ex = assertWinutilsResolveFailed(methodDir, E_DOES_NOT_EXIST);
        assertInString(ex.toString(), "Hadoop bin directory");
    }

    @Test
    public void testHadoopBinNotADir() throws Throwable {
        File bin = new File(methodDir, "bin");
        touch(bin);
        try {
            assertWinutilsResolveFailed(methodDir, E_NOT_DIRECTORY);
        } finally {
            FileUtils.deleteQuietly(methodDir);
        }
    }

    @Test
    public void testBinWinUtilsFound() throws Throwable {
        try {
            File bin = new File(methodDir, "bin");
            File winutils = new File(bin, WINUTILS_EXE);
            touch(winutils);
            Assert.assertEquals(winutils.getCanonicalPath(), getQualifiedBinInner(methodDir, WINUTILS_EXE).getCanonicalPath());
        } finally {
            FileUtils.deleteQuietly(methodDir);
        }
    }

    @Test
    public void testBinWinUtilsNotAFile() throws Throwable {
        try {
            File bin = new File(methodDir, "bin");
            File winutils = new File(bin, WINUTILS_EXE);
            winutils.mkdirs();
            assertWinutilsResolveFailed(methodDir, E_NOT_EXECUTABLE_FILE);
        } finally {
            FileUtils.deleteDirectory(methodDir);
        }
    }

    /**
     * This test takes advantage of the invariant winutils path is valid
     * or access to it will raise an exception holds on Linux, and without
     * any winutils binary even if HADOOP_HOME points to a real hadoop
     * directory, the exception reporting can be validated
     */
    @Test
    public void testNoWinutilsOnUnix() throws Throwable {
        Assume.assumeFalse(WINDOWS);
        try {
            getWinUtilsFile();
        } catch (FileNotFoundException ex) {
            assertExContains(ex, E_NOT_A_WINDOWS_SYSTEM);
        }
        try {
            getWinUtilsPath();
        } catch (RuntimeException ex) {
            assertExContains(ex, E_NOT_A_WINDOWS_SYSTEM);
            if (((ex.getCause()) == null) || (!((ex.getCause()) instanceof FileNotFoundException))) {
                throw ex;
            }
        }
    }

    @Test
    public void testBashQuote() {
        Assert.assertEquals("'foobar'", Shell.Shell.bashQuote("foobar"));
        Assert.assertEquals("\'foo\'\\\'\'bar\'", Shell.Shell.bashQuote("foo'bar"));
        Assert.assertEquals("\'\'\\\'\'foo\'\\\'\'bar\'\\\'\'\'", Shell.Shell.bashQuote("'foo'bar'"));
    }

    @Test(timeout = 120000)
    public void testDestroyAllShellProcesses() throws Throwable {
        Assume.assumeFalse(WINDOWS);
        StringBuffer sleepCommand = new StringBuffer();
        sleepCommand.append("sleep 200");
        String[] shellCmd = new String[]{ "bash", "-c", sleepCommand.toString() };
        final ShellCommandExecutor shexc1 = new ShellCommandExecutor(shellCmd);
        final ShellCommandExecutor shexc2 = new ShellCommandExecutor(shellCmd);
        Thread shellThread1 = new Thread() {
            @Override
            public void run() {
                try {
                    shexc1.execute();
                } catch (IOException ioe) {
                    // ignore IOException from thread interrupt
                }
            }
        };
        Thread shellThread2 = new Thread() {
            @Override
            public void run() {
                try {
                    shexc2.execute();
                } catch (IOException ioe) {
                    // ignore IOException from thread interrupt
                }
            }
        };
        shellThread1.start();
        shellThread2.start();
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return (shexc1.getProcess()) != null;
            }
        }, 10, 10000);
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return (shexc2.getProcess()) != null;
            }
        }, 10, 10000);
        Shell.Shell.destroyAllShellProcesses();
        shexc1.getProcess().waitFor();
        shexc2.getProcess().waitFor();
    }

    @Test
    public void testIsJavaVersionAtLeast() {
        Assert.assertTrue(Shell.Shell.isJavaVersionAtLeast(8));
    }

    @Test
    public void testIsBashSupported() throws InterruptedIOException {
        Assume.assumeTrue("Bash is not supported", Shell.Shell.checkIsBashSupported());
    }
}

