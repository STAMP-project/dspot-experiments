/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal.cli.shell;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.geode.management.internal.cli.LogWrapper;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.rules.TemporaryFolder;

import static Gfsh.LINE_SEPARATOR;
import static GfshConfig.DEFAULT_INIT_FILE_NAME;


/**
 * Unit tests for supplying an init file to Gfsh.
 * </P>
 * Makes use of reflection to reset private static variables on some classes to replace loggers that
 * would otherwise clutter the console.
 */
public class GfshInitFileJUnitTest {
    private static final String INIT_FILE_NAME = DEFAULT_INIT_FILE_NAME;

    private static final boolean APPEND = true;

    private static final int BANNER_LINES = 1;

    private static final int INIT_FILE_CITATION_LINES = 1;

    private static String saveLog4j2Config;

    private static Logger julLogger;

    private static Handler[] saveHandlers;

    private ByteArrayOutputStream sysout = new ByteArrayOutputStream();

    private String gfshHistoryFileName;

    private LogWrapper gfshFileLogger;

    private GfshInitFileJUnitTest.JUnitLoggerHandler junitLoggerHandler;

    @ClassRule
    public static TemporaryFolder temporaryFolder_Config = new TemporaryFolder();

    @Rule
    public TemporaryFolder temporaryFolder_CurrentDirectory = new TemporaryFolder();

    @Rule
    public RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    @Test
    public void testInitFile_NotProvided() throws Exception {
        /* String historyFileName, String defaultPrompt, int historySize, String logDir, Level logLevel,
        Integer logLimit, Integer logCount, String initFileName
         */
        GfshConfig gfshConfig = new GfshConfig(this.gfshHistoryFileName, "", 0, temporaryFolder_CurrentDirectory.getRoot().getAbsolutePath(), null, null, null, null);
        Assert.assertNull(GfshInitFileJUnitTest.INIT_FILE_NAME, gfshConfig.getInitFileName());
        /* boolean launchShell, String[] args, GfshConfig gfshConfig */
        Gfsh gfsh = Gfsh.getInstance(false, new String[]{  }, gfshConfig);
        int actualStatus = gfsh.getLastExecutionStatus();
        int expectedStatus = 0;
        Assert.assertEquals("Status 0==success", expectedStatus, actualStatus);
        int expectedLogCount = GfshInitFileJUnitTest.BANNER_LINES;
        Assert.assertEquals("Log records written", expectedLogCount, this.junitLoggerHandler.getLog().size());
        for (LogRecord logRecord : this.junitLoggerHandler.getLog()) {
            Assert.assertNull("No exceptions in log", logRecord.getThrown());
        }
    }

    @Test
    public void testInitFile_NotFound() throws Exception {
        // Construct the file name but not the file
        String initFileName = ((temporaryFolder_CurrentDirectory.getRoot().getAbsolutePath()) + (File.separator)) + (GfshInitFileJUnitTest.INIT_FILE_NAME);
        /* String historyFileName, String defaultPrompt, int historySize, String logDir, Level logLevel,
        Integer logLimit, Integer logCount, String initFileName
         */
        GfshConfig gfshConfig = new GfshConfig(this.gfshHistoryFileName, "", 0, temporaryFolder_CurrentDirectory.getRoot().getAbsolutePath(), null, null, null, initFileName);
        Assert.assertNotNull(GfshInitFileJUnitTest.INIT_FILE_NAME, gfshConfig.getInitFileName());
        /* boolean launchShell, String[] args, GfshConfig gfshConfig */
        Gfsh gfsh = Gfsh.getInstance(false, new String[]{  }, gfshConfig);
        int actualStatus = gfsh.getLastExecutionStatus();
        int expectedStatus = 0;
        Assert.assertNotEquals("Status <0==failure", expectedStatus, actualStatus);
        int expectedLogCount = ((GfshInitFileJUnitTest.BANNER_LINES) + (GfshInitFileJUnitTest.INIT_FILE_CITATION_LINES)) + 1;
        Assert.assertEquals("Log records written", expectedLogCount, this.junitLoggerHandler.getLog().size());
        Throwable exception = null;
        for (LogRecord logRecord : this.junitLoggerHandler.getLog()) {
            if ((logRecord.getThrown()) != null) {
                exception = logRecord.getThrown();
                break;
            }
        }
        Assert.assertNotNull("Exceptions in log", exception);
    }

    @Test
    public void testInitFile_Empty() throws Exception {
        File initFile = temporaryFolder_CurrentDirectory.newFile(GfshInitFileJUnitTest.INIT_FILE_NAME);
        /* String historyFileName, String defaultPrompt, int historySize, String logDir, Level logLevel,
        Integer logLimit, Integer logCount, String initFileName
         */
        GfshConfig gfshConfig = new GfshConfig(this.gfshHistoryFileName, "", 0, temporaryFolder_CurrentDirectory.getRoot().getAbsolutePath(), null, null, null, initFile.getAbsolutePath());
        Assert.assertNotNull(GfshInitFileJUnitTest.INIT_FILE_NAME, gfshConfig.getInitFileName());
        /* boolean launchShell, String[] args, GfshConfig gfshConfig */
        Gfsh gfsh = Gfsh.getInstance(false, new String[]{  }, gfshConfig);
        int actualStatus = gfsh.getLastExecutionStatus();
        int expectedStatus = 0;
        Assert.assertEquals("Status 0==success", expectedStatus, actualStatus);
        int expectedLogCount = ((GfshInitFileJUnitTest.BANNER_LINES) + (GfshInitFileJUnitTest.INIT_FILE_CITATION_LINES)) + 1;
        Assert.assertEquals("Log records written", expectedLogCount, this.junitLoggerHandler.getLog().size());
        for (LogRecord logRecord : this.junitLoggerHandler.getLog()) {
            Assert.assertNull("No exceptions in log", logRecord.getThrown());
        }
    }

    @Test
    public void testInitFile_OneGoodCommand() throws Exception {
        File initFile = temporaryFolder_CurrentDirectory.newFile(GfshInitFileJUnitTest.INIT_FILE_NAME);
        FileUtils.writeStringToFile(initFile, ("echo --string=hello" + (LINE_SEPARATOR)), GfshInitFileJUnitTest.APPEND);
        /* String historyFileName, String defaultPrompt, int historySize, String logDir, Level logLevel,
        Integer logLimit, Integer logCount, String initFileName
         */
        GfshConfig gfshConfig = new GfshConfig(this.gfshHistoryFileName, "", 0, temporaryFolder_CurrentDirectory.getRoot().getAbsolutePath(), null, null, null, initFile.getAbsolutePath());
        Assert.assertNotNull(GfshInitFileJUnitTest.INIT_FILE_NAME, gfshConfig.getInitFileName());
        /* boolean launchShell, String[] args, GfshConfig gfshConfig */
        Gfsh gfsh = Gfsh.getInstance(false, new String[]{  }, gfshConfig);
        int actualStatus = gfsh.getLastExecutionStatus();
        int expectedStatus = 0;
        Assert.assertEquals("Status 0==success", expectedStatus, actualStatus);
        int expectedLogCount = ((GfshInitFileJUnitTest.BANNER_LINES) + (GfshInitFileJUnitTest.INIT_FILE_CITATION_LINES)) + 1;
        Assert.assertEquals("Log records written", expectedLogCount, this.junitLoggerHandler.getLog().size());
        for (LogRecord logRecord : this.junitLoggerHandler.getLog()) {
            Assert.assertNull("No exceptions in log", logRecord.getThrown());
        }
    }

    @Test
    public void testInitFile_TwoGoodCommands() throws Exception {
        File initFile = temporaryFolder_CurrentDirectory.newFile(GfshInitFileJUnitTest.INIT_FILE_NAME);
        FileUtils.writeStringToFile(initFile, ("echo --string=hello" + (LINE_SEPARATOR)), GfshInitFileJUnitTest.APPEND);
        FileUtils.writeStringToFile(initFile, ("echo --string=goodbye" + (LINE_SEPARATOR)), GfshInitFileJUnitTest.APPEND);
        /* String historyFileName, String defaultPrompt, int historySize, String logDir, Level logLevel,
        Integer logLimit, Integer logCount, String initFileName
         */
        GfshConfig gfshConfig = new GfshConfig(this.gfshHistoryFileName, "", 0, temporaryFolder_CurrentDirectory.getRoot().getAbsolutePath(), null, null, null, initFile.getAbsolutePath());
        Assert.assertNotNull(GfshInitFileJUnitTest.INIT_FILE_NAME, gfshConfig.getInitFileName());
        /* boolean launchShell, String[] args, GfshConfig gfshConfig */
        Gfsh gfsh = Gfsh.getInstance(false, new String[]{  }, gfshConfig);
        int actualStatus = gfsh.getLastExecutionStatus();
        int expectedStatus = 0;
        Assert.assertEquals("Status 0==success", expectedStatus, actualStatus);
        int expectedLogCount = ((GfshInitFileJUnitTest.BANNER_LINES) + (GfshInitFileJUnitTest.INIT_FILE_CITATION_LINES)) + 1;
        Assert.assertEquals("Log records written", expectedLogCount, this.junitLoggerHandler.getLog().size());
        for (LogRecord logRecord : this.junitLoggerHandler.getLog()) {
            Assert.assertNull("No exceptions in log", logRecord.getThrown());
        }
    }

    @Test
    public void testInitFile_OneBadCommand() throws Exception {
        File initFile = temporaryFolder_CurrentDirectory.newFile(GfshInitFileJUnitTest.INIT_FILE_NAME);
        FileUtils.writeStringToFile(initFile, ("fail" + (LINE_SEPARATOR)), GfshInitFileJUnitTest.APPEND);
        /* String historyFileName, String defaultPrompt, int historySize, String logDir, Level logLevel,
        Integer logLimit, Integer logCount, String initFileName
         */
        GfshConfig gfshConfig = new GfshConfig(this.gfshHistoryFileName, "", 0, temporaryFolder_CurrentDirectory.getRoot().getAbsolutePath(), null, null, null, initFile.getAbsolutePath());
        Assert.assertNotNull(GfshInitFileJUnitTest.INIT_FILE_NAME, gfshConfig.getInitFileName());
        /* boolean launchShell, String[] args, GfshConfig gfshConfig */
        Gfsh gfsh = Gfsh.getInstance(false, new String[]{  }, gfshConfig);
        int actualStatus = gfsh.getLastExecutionStatus();
        int expectedStatus = 0;
        Assert.assertNotEquals("Status <0==failure", expectedStatus, actualStatus);
        // after upgrading to Spring-shell 1.2, the bad command exception is logged as well
        int expectedLogCount = ((GfshInitFileJUnitTest.BANNER_LINES) + (GfshInitFileJUnitTest.INIT_FILE_CITATION_LINES)) + 2;
        Assert.assertEquals("Log records written", expectedLogCount, this.junitLoggerHandler.getLog().size());
    }

    @Test
    public void testInitFile_TwoBadCommands() throws Exception {
        File initFile = temporaryFolder_CurrentDirectory.newFile(GfshInitFileJUnitTest.INIT_FILE_NAME);
        FileUtils.writeStringToFile(initFile, ("fail" + (LINE_SEPARATOR)), GfshInitFileJUnitTest.APPEND);
        FileUtils.writeStringToFile(initFile, ("fail" + (LINE_SEPARATOR)), GfshInitFileJUnitTest.APPEND);
        /* String historyFileName, String defaultPrompt, int historySize, String logDir, Level logLevel,
        Integer logLimit, Integer logCount, String initFileName
         */
        GfshConfig gfshConfig = new GfshConfig(this.gfshHistoryFileName, "", 0, temporaryFolder_CurrentDirectory.getRoot().getAbsolutePath(), null, null, null, initFile.getAbsolutePath());
        Assert.assertNotNull(GfshInitFileJUnitTest.INIT_FILE_NAME, gfshConfig.getInitFileName());
        /* boolean launchShell, String[] args, GfshConfig gfshConfig */
        Gfsh gfsh = Gfsh.getInstance(false, new String[]{  }, gfshConfig);
        int actualStatus = gfsh.getLastExecutionStatus();
        int expectedStatus = 0;
        Assert.assertNotEquals("Status <0==failure", expectedStatus, actualStatus);
        // after upgrading to Spring-shell 1.2, the bad command exception is logged as well
        int expectedLogCount = ((GfshInitFileJUnitTest.BANNER_LINES) + (GfshInitFileJUnitTest.INIT_FILE_CITATION_LINES)) + 2;
        Assert.assertEquals("Log records written", expectedLogCount, this.junitLoggerHandler.getLog().size());
    }

    @Test
    public void testInitFile_BadAndGoodCommands() throws Exception {
        File initFile = temporaryFolder_CurrentDirectory.newFile(GfshInitFileJUnitTest.INIT_FILE_NAME);
        FileUtils.writeStringToFile(initFile, ("fail" + (LINE_SEPARATOR)), GfshInitFileJUnitTest.APPEND);
        FileUtils.writeStringToFile(initFile, ("echo --string=goodbye" + (LINE_SEPARATOR)), GfshInitFileJUnitTest.APPEND);
        /* String historyFileName, String defaultPrompt, int historySize, String logDir, Level logLevel,
        Integer logLimit, Integer logCount, String initFileName
         */
        GfshConfig gfshConfig = new GfshConfig(this.gfshHistoryFileName, "", 0, temporaryFolder_CurrentDirectory.getRoot().getAbsolutePath(), null, null, null, initFile.getAbsolutePath());
        Assert.assertNotNull(GfshInitFileJUnitTest.INIT_FILE_NAME, gfshConfig.getInitFileName());
        /* boolean launchShell, String[] args, GfshConfig gfshConfig */
        Gfsh gfsh = Gfsh.getInstance(false, new String[]{  }, gfshConfig);
        int actualStatus = gfsh.getLastExecutionStatus();
        int expectedStatus = 0;
        Assert.assertNotEquals("Status <0==failure", expectedStatus, actualStatus);
        // after upgrading to Spring-shell 1.2, the bad command exception is logged as well
        int expectedLogCount = ((GfshInitFileJUnitTest.BANNER_LINES) + (GfshInitFileJUnitTest.INIT_FILE_CITATION_LINES)) + 2;
        Assert.assertEquals("Log records written", expectedLogCount, this.junitLoggerHandler.getLog().size());
    }

    /**
     * Log handler for testing. Capture logged messages for later inspection.
     *
     * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
     */
    private static class JUnitLoggerHandler extends Handler {
        private List<LogRecord> log;

        public JUnitLoggerHandler() {
            log = new ArrayList<>();
        }

        public List<LogRecord> getLog() {
            return log;
        }

        @Override
        public void publish(LogRecord record) {
            log.add(record);
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {
        }
    }
}

