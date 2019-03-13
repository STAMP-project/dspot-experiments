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
package org.apache.flink.client.cli;


import java.util.Collections;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the RUN command.
 */
public class CliFrontendRunTest extends CliFrontendTestBase {
    @Test
    public void testRun() throws Exception {
        final Configuration configuration = getConfiguration();
        // test without parallelism
        {
            String[] parameters = new String[]{ "-v", CliFrontendTestUtils.getTestJarPath() };
            CliFrontendRunTest.verifyCliFrontend(CliFrontendTestBase.getCli(configuration), parameters, 1, true, false);
        }
        // test configure parallelism
        {
            String[] parameters = new String[]{ "-v", "-p", "42", CliFrontendTestUtils.getTestJarPath() };
            CliFrontendRunTest.verifyCliFrontend(CliFrontendTestBase.getCli(configuration), parameters, 42, true, false);
        }
        // test configure sysout logging
        {
            String[] parameters = new String[]{ "-p", "2", "-q", CliFrontendTestUtils.getTestJarPath() };
            CliFrontendRunTest.verifyCliFrontend(CliFrontendTestBase.getCli(configuration), parameters, 2, false, false);
        }
        // test detached mode
        {
            String[] parameters = new String[]{ "-p", "2", "-d", CliFrontendTestUtils.getTestJarPath() };
            CliFrontendRunTest.verifyCliFrontend(CliFrontendTestBase.getCli(configuration), parameters, 2, true, true);
        }
        // test configure savepoint path (no ignore flag)
        {
            String[] parameters = new String[]{ "-s", "expectedSavepointPath", CliFrontendTestUtils.getTestJarPath() };
            RunOptions options = CliFrontendParser.parseRunCommand(parameters);
            SavepointRestoreSettings savepointSettings = options.getSavepointRestoreSettings();
            Assert.assertTrue(savepointSettings.restoreSavepoint());
            Assert.assertEquals("expectedSavepointPath", savepointSettings.getRestorePath());
            Assert.assertFalse(savepointSettings.allowNonRestoredState());
        }
        // test configure savepoint path (with ignore flag)
        {
            String[] parameters = new String[]{ "-s", "expectedSavepointPath", "-n", CliFrontendTestUtils.getTestJarPath() };
            RunOptions options = CliFrontendParser.parseRunCommand(parameters);
            SavepointRestoreSettings savepointSettings = options.getSavepointRestoreSettings();
            Assert.assertTrue(savepointSettings.restoreSavepoint());
            Assert.assertEquals("expectedSavepointPath", savepointSettings.getRestorePath());
            Assert.assertTrue(savepointSettings.allowNonRestoredState());
        }
        // test jar arguments
        {
            String[] parameters = new String[]{ CliFrontendTestUtils.getTestJarPath(), "-arg1", "value1", "justavalue", "--arg2", "value2" };
            RunOptions options = CliFrontendParser.parseRunCommand(parameters);
            Assert.assertEquals("-arg1", options.getProgramArgs()[0]);
            Assert.assertEquals("value1", options.getProgramArgs()[1]);
            Assert.assertEquals("justavalue", options.getProgramArgs()[2]);
            Assert.assertEquals("--arg2", options.getProgramArgs()[3]);
            Assert.assertEquals("value2", options.getProgramArgs()[4]);
        }
    }

    @Test(expected = CliArgsException.class)
    public void testUnrecognizedOption() throws Exception {
        // test unrecognized option
        String[] parameters = new String[]{ "-v", "-l", "-a", "some", "program", "arguments" };
        Configuration configuration = getConfiguration();
        CliFrontend testFrontend = new CliFrontend(configuration, Collections.singletonList(CliFrontendTestBase.getCli(configuration)));
        testFrontend.run(parameters);
    }

    @Test(expected = CliArgsException.class)
    public void testInvalidParallelismOption() throws Exception {
        // test configure parallelism with non integer value
        String[] parameters = new String[]{ "-v", "-p", "text", CliFrontendTestUtils.getTestJarPath() };
        Configuration configuration = getConfiguration();
        CliFrontend testFrontend = new CliFrontend(configuration, Collections.singletonList(CliFrontendTestBase.getCli(configuration)));
        testFrontend.run(parameters);
    }

    @Test(expected = CliArgsException.class)
    public void testParallelismWithOverflow() throws Exception {
        // test configure parallelism with overflow integer value
        String[] parameters = new String[]{ "-v", "-p", "475871387138", CliFrontendTestUtils.getTestJarPath() };
        Configuration configuration = new Configuration();
        CliFrontend testFrontend = new CliFrontend(configuration, Collections.singletonList(CliFrontendTestBase.getCli(configuration)));
        testFrontend.run(parameters);
    }

    private static final class RunTestingCliFrontend extends CliFrontend {
        private final int expectedParallelism;

        private final boolean sysoutLogging;

        private final boolean isDetached;

        private RunTestingCliFrontend(AbstractCustomCommandLine<?> cli, int expectedParallelism, boolean logging, boolean isDetached) throws Exception {
            super(cli.getConfiguration(), Collections.singletonList(cli));
            this.expectedParallelism = expectedParallelism;
            this.sysoutLogging = logging;
            this.isDetached = isDetached;
        }

        @Override
        protected void executeProgram(PackagedProgram program, ClusterClient client, int parallelism) {
            Assert.assertEquals(isDetached, client.isDetached());
            Assert.assertEquals(sysoutLogging, client.getPrintStatusDuringExecution());
            Assert.assertEquals(expectedParallelism, parallelism);
        }
    }
}

