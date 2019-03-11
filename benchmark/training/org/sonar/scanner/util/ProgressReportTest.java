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
package org.sonar.scanner.util;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.sonar.api.utils.log.LogTester;


public class ProgressReportTest {
    private static final String THREAD_NAME = "progress";

    @Rule
    public TestRule safeguardTimeout = new DisableOnDebug(Timeout.seconds(60));

    @Rule
    public LogTester logTester = new LogTester();

    private ProgressReport underTest = new ProgressReport(ProgressReportTest.THREAD_NAME, 1);

    @Test
    public void stop_thread_on_stop() {
        underTest.start("start");
        assertThat(ProgressReportTest.isThreadAlive(ProgressReportTest.THREAD_NAME)).isTrue();
        underTest.stop("stop");
        assertThat(ProgressReportTest.isThreadAlive(ProgressReportTest.THREAD_NAME)).isFalse();
    }

    @Test
    public void do_not_block_app() {
        underTest.start("start");
        assertThat(ProgressReportTest.isDaemon(ProgressReportTest.THREAD_NAME)).isTrue();
        underTest.stop("stop");
    }

    @Test
    public void do_log() {
        underTest.start("start");
        underTest.message("Some message");
        boolean logged = false;
        while (!logged) {
            logged = logTester.logs().contains("Some message");
        } 
        underTest.stop("stop");
    }
}

