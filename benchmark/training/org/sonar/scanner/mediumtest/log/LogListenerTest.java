/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.scanner.mediumtest.log;


import com.google.common.collect.ImmutableMap;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.sonar.batch.bootstrapper.LogOutput;
import org.sonar.batch.bootstrapper.LogOutput.Level;
import org.sonar.scanner.mediumtest.ScannerMediumTester;
import org.sonar.xoo.XooPlugin;


public class LogListenerTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Pattern simpleTimePattern = Pattern.compile("\\d{2}:\\d{2}:\\d{2}");

    private List<LogListenerTest.LogEvent> logOutput;

    private StringBuilder logOutputStr;

    private ByteArrayOutputStream stdOutTarget;

    private ByteArrayOutputStream stdErrTarget;

    private static PrintStream savedStdOut;

    private static PrintStream savedStdErr;

    @Rule
    public ScannerMediumTester tester = new ScannerMediumTester().registerPlugin("xoo", new XooPlugin()).addDefaultQProfile("xoo", "Sonar Way").setLogOutput(new LogListenerTest.SimpleLogListener());

    private File baseDir;

    private ImmutableMap.Builder<String, String> builder;

    @Test
    public void testChangeLogForAnalysis() throws IOException, InterruptedException {
        File srcDir = new File(baseDir, "src");
        srcDir.mkdir();
        File xooFile = new File(srcDir, "sample.xoo");
        FileUtils.write(xooFile, "Sample xoo\ncontent");
        tester.newAnalysis().properties(builder.put("sonar.sources", "src").put("sonar.verbose", "true").build()).execute();
        for (LogListenerTest.LogEvent e : logOutput) {
            LogListenerTest.savedStdOut.println(((("[captured]" + (e.level)) + " ") + (e.msg)));
        }
        // only done in DEBUG during analysis
        assertThat(logOutputStr.toString()).contains("Post-jobs : ");
    }

    @Test
    public void testNoStdLog() throws IOException {
        File srcDir = new File(baseDir, "src");
        srcDir.mkdir();
        File xooFile = new File(srcDir, "sample.xoo");
        FileUtils.write(xooFile, "Sample xoo\ncontent");
        tester.newAnalysis().properties(builder.put("sonar.sources", "src").build()).execute();
        assertNoStdOutput();
        assertThat(logOutput).isNotEmpty();
        synchronized(logOutput) {
            for (LogListenerTest.LogEvent e : logOutput) {
                LogListenerTest.savedStdOut.println(((("[captured]" + (e.level)) + " ") + (e.msg)));
            }
        }
    }

    @Test
    public void testNoFormattedMsgs() throws IOException {
        File srcDir = new File(baseDir, "src");
        srcDir.mkdir();
        File xooFile = new File(srcDir, "sample.xoo");
        FileUtils.write(xooFile, "Sample xoo\ncontent");
        tester.newAnalysis().properties(builder.put("sonar.sources", "src").build()).execute();
        assertNoStdOutput();
        synchronized(logOutput) {
            for (LogListenerTest.LogEvent e : logOutput) {
                assertMsgClean(e.msg);
                LogListenerTest.savedStdOut.println(((("[captured]" + (e.level)) + " ") + (e.msg)));
            }
        }
    }

    // SONAR-7540
    @Test
    public void testStackTrace() throws IOException {
        File srcDir = new File(baseDir, "src");
        srcDir.mkdir();
        File xooFile = new File(srcDir, "sample.xoo");
        FileUtils.write(xooFile, "Sample xoo\ncontent");
        File xooFileMeasure = new File(srcDir, "sample.xoo.measures");
        FileUtils.write(xooFileMeasure, "foo:bar");
        try {
            tester.newAnalysis().properties(builder.put("sonar.sources", "src").build()).execute();
            fail("Expected exception");
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("Error processing line 1");
        }
        assertNoStdOutput();
        synchronized(logOutput) {
            for (LogListenerTest.LogEvent e : logOutput) {
                if ((e.level) == (Level.ERROR)) {
                    assertThat(e.msg).contains("Error processing line 1 of file", (("src" + (File.separator)) + "sample.xoo.measures"), "java.lang.IllegalStateException: Unknow metric with key: foo", "at org.sonar.xoo.lang.MeasureSensor.saveMeasure");
                }
            }
        }
    }

    private class SimpleLogListener implements LogOutput {
        @Override
        public void log(String msg, Level level) {
            logOutput.add(new LogListenerTest.LogEvent(msg, level));
            logOutputStr.append(msg).append("\n");
        }
    }

    private static class LogEvent {
        String msg;

        Level level;

        LogEvent(String msg, LogOutput.Level level) {
            this.msg = msg;
            this.level = level;
        }
    }
}

