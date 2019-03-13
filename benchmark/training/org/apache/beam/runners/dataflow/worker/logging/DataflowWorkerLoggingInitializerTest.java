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
package org.apache.beam.runners.dataflow.worker.logging;


import DataflowWorkerLoggingOptions.Level.DEBUG;
import DataflowWorkerLoggingOptions.Level.ERROR;
import DataflowWorkerLoggingOptions.Level.INFO;
import DataflowWorkerLoggingOptions.Level.WARN;
import java.io.IOException;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.apache.beam.runners.dataflow.options.DataflowWorkerLoggingOptions;
import org.apache.beam.runners.dataflow.options.DataflowWorkerLoggingOptions.WorkerLogLevelOverrides;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.testing.RestoreSystemProperties;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.LoggerFactory;


/**
 * Unit tests for {@link DataflowWorkerLoggingInitializer}.
 *
 * <p>Tests which validate written log messages should assume that other background tasks may
 * concurrently be writing log messages, since registered log handlers are global. Therefore it is
 * not safe to assert on log counts or whether the retrieved log collection is empty.
 */
@RunWith(JUnit4.class)
public class DataflowWorkerLoggingInitializerTest {
    @Rule
    public TemporaryFolder logFolder = new TemporaryFolder();

    @Rule
    public RestoreSystemProperties restoreProperties = new RestoreSystemProperties();

    // Should match {@link DataflowWorkerLoggingInitializer#FILEPATH_PROPERTY}
    private static final String LOGPATH_PROPERTY = "dataflow.worker.logging.filepath";

    @Test
    public void testWithDefaults() {
        DataflowWorkerLoggingOptions options = PipelineOptionsFactory.as(DataflowWorkerLoggingOptions.class);
        DataflowWorkerLoggingInitializer.configure(options);
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        Assert.assertEquals(1, rootLogger.getHandlers().length);
        Assert.assertEquals(Level.INFO, rootLogger.getLevel());
        assertIsDataflowWorkerLoggingHandler(rootLogger.getHandlers()[0], Level.ALL);
    }

    @Test
    public void testWithConfigurationOverride() {
        DataflowWorkerLoggingOptions options = PipelineOptionsFactory.as(DataflowWorkerLoggingOptions.class);
        options.setDefaultWorkerLogLevel(WARN);
        DataflowWorkerLoggingInitializer.configure(options);
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        Assert.assertEquals(1, rootLogger.getHandlers().length);
        Assert.assertEquals(Level.WARNING, rootLogger.getLevel());
        assertIsDataflowWorkerLoggingHandler(rootLogger.getHandlers()[0], Level.ALL);
    }

    @Test
    public void testWithCustomLogLevels() {
        DataflowWorkerLoggingOptions options = PipelineOptionsFactory.as(DataflowWorkerLoggingOptions.class);
        options.setWorkerLogLevelOverrides(new WorkerLogLevelOverrides().addOverrideForName("A", DEBUG).addOverrideForName("B", ERROR));
        DataflowWorkerLoggingInitializer.configure(options);
        Logger aLogger = LogManager.getLogManager().getLogger("A");
        Assert.assertEquals(0, aLogger.getHandlers().length);
        Assert.assertEquals(Level.FINE, aLogger.getLevel());
        Assert.assertTrue(aLogger.getUseParentHandlers());
        Logger bLogger = LogManager.getLogManager().getLogger("B");
        Assert.assertEquals(Level.SEVERE, bLogger.getLevel());
        Assert.assertEquals(0, bLogger.getHandlers().length);
        Assert.assertTrue(aLogger.getUseParentHandlers());
    }

    @Test
    public void testStrictGlobalFilterAndRelaxedOverride() throws IOException {
        DataflowWorkerLoggingOptions options = PipelineOptionsFactory.as(DataflowWorkerLoggingOptions.class);
        options.setDefaultWorkerLogLevel(ERROR);
        options.setWorkerLogLevelOverrides(new WorkerLogLevelOverrides().addOverrideForName("A", INFO));
        DataflowWorkerLoggingInitializer.configure(options);
        LogManager.getLogManager().getLogger("A").info("foobar");
        verifyLogOutput("foobar");
    }

    @Test
    public void testSystemOutToLogger() throws Throwable {
        System.out.println("afterInitialization");
        verifyLogOutput("afterInitialization");
    }

    @Test
    public void testSystemErrToLogger() throws Throwable {
        System.err.println("afterInitialization");
        verifyLogOutput("afterInitialization");
    }

    @Test
    public void testSystemOutRespectsFilterConfig() throws IOException {
        DataflowWorkerLoggingOptions options = PipelineOptionsFactory.as(DataflowWorkerLoggingOptions.class);
        options.setDefaultWorkerLogLevel(ERROR);
        DataflowWorkerLoggingInitializer.configure(options);
        System.out.println("sys.out");
        System.err.println("sys.err");
        List<String> actualLines = retrieveLogLines();
        Assert.assertThat(actualLines, Matchers.not(Matchers.hasItem(Matchers.containsString("sys.out"))));
        Assert.assertThat(actualLines, Matchers.hasItem(Matchers.containsString("sys.err")));
    }

    @Test
    public void testSystemOutLevelOverrides() throws IOException {
        DataflowWorkerLoggingOptions options = PipelineOptionsFactory.as(DataflowWorkerLoggingOptions.class);
        options.setWorkerSystemOutMessageLevel(WARN);
        DataflowWorkerLoggingInitializer.configure(options.as(DataflowWorkerLoggingOptions.class));
        System.out.println("foobar");
        verifyLogOutput("WARN");
    }

    @Test
    public void testSystemOutCustomLogLevel() throws IOException {
        DataflowWorkerLoggingOptions options = PipelineOptionsFactory.as(DataflowWorkerLoggingOptions.class);
        options.setWorkerLogLevelOverrides(new WorkerLogLevelOverrides().addOverrideForName("System.out", ERROR));
        DataflowWorkerLoggingInitializer.configure(options);
        System.out.println("sys.out");
        List<String> actualLines = retrieveLogLines();
        // N.B.: It's not safe to assert that actualLines is "empty" since the logging framework is
        // global and logs may be concurrently written by other infrastructure.
        Assert.assertThat(actualLines, Matchers.not(Matchers.hasItem(Matchers.containsString("sys.out"))));
    }

    @Test
    public void testSystemErrLevelOverrides() throws IOException {
        DataflowWorkerLoggingOptions options = PipelineOptionsFactory.as(DataflowWorkerLoggingOptions.class);
        options.setWorkerSystemErrMessageLevel(WARN);
        DataflowWorkerLoggingInitializer.configure(options.as(DataflowWorkerLoggingOptions.class));
        System.err.println("foobar");
        verifyLogOutput("WARN");
    }

    /**
     * Verify we can handle additional user logging configuration. Specifically, ensure that we
     * gracefully handle adding an additional log handler which forwards to stdout.
     */
    @Test
    public void testUserHandlerForwardsStdOut() throws Throwable {
        registerRootLogHandler(new DataflowWorkerLoggingInitializerTest.StdOutLogHandler());
        org.slf4j.Logger log = LoggerFactory.getLogger(DataflowWorkerLoggingInitializerTest.class);
        log.info("foobar");
        verifyLogOutput("foobar");
    }

    @Test
    public void testLoggingHandlersAreDifferent() {
        Assert.assertThat(DataflowWorkerLoggingInitializer.getLoggingHandler(), Matchers.not(DataflowWorkerLoggingInitializer.getSdkLoggingHandler()));
        Assert.assertThat(DataflowWorkerLoggingInitializer.DEFAULT_RUNNER_LOGGING_LOCATION, Matchers.not(DataflowWorkerLoggingInitializer.DEFAULT_SDK_LOGGING_LOCATION));
        Assert.assertThat(DataflowWorkerLoggingInitializer.RUNNER_FILEPATH_PROPERTY, Matchers.not(DataflowWorkerLoggingInitializer.SDK_FILEPATH_PROPERTY));
    }

    static class StdOutLogHandler extends Handler {
        @Override
        public void publish(LogRecord record) {
            System.out.println(record.getMessage());
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }
    }
}

