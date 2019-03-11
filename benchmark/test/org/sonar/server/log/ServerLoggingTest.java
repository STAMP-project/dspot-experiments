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
package org.sonar.server.log;


import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.io.File;
import java.io.IOException;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.utils.log.LogTester;
import org.sonar.db.Database;
import org.sonar.process.logging.LogLevelConfig;
import org.sonar.process.logging.LogbackHelper;


@RunWith(DataProviderRunner.class)
public class ServerLoggingTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final String rootLoggerName = RandomStringUtils.randomAlphabetic(20);

    private LogbackHelper logbackHelper = Mockito.spy(new LogbackHelper());

    private MapSettings settings = new MapSettings();

    private final ServerProcessLogging serverProcessLogging = Mockito.mock(ServerProcessLogging.class);

    private final Database database = Mockito.mock(Database.class);

    private ServerLogging underTest = new ServerLogging(logbackHelper, settings.asConfig(), serverProcessLogging, database);

    @Rule
    public LogTester logTester = new LogTester();

    @Test
    public void getLogsDir() throws IOException {
        File dir = temp.newFolder();
        settings.setProperty(PATH_LOGS.getKey(), dir.getAbsolutePath());
        assertThat(underTest.getLogsDir()).isEqualTo(dir);
    }

    @Test
    public void getRootLoggerLevel() {
        logTester.setLevel(TRACE);
        assertThat(underTest.getRootLoggerLevel()).isEqualTo(TRACE);
    }

    @Test
    public void changeLevel_to_trace_enables_db_logging() {
        LogLevelConfig logLevelConfig = LogLevelConfig.newBuilder(rootLoggerName).build();
        Mockito.when(serverProcessLogging.getLogLevelConfig()).thenReturn(logLevelConfig);
        Mockito.reset(database);
        underTest.changeLevel(INFO);
        Mockito.verify(database).enableSqlLogging(false);
        Mockito.reset(database);
        underTest.changeLevel(DEBUG);
        Mockito.verify(database).enableSqlLogging(false);
        Mockito.reset(database);
        underTest.changeLevel(TRACE);
        Mockito.verify(database).enableSqlLogging(true);
    }

    @Test
    public void changeLevel_fails_with_IAE_when_level_is_ERROR() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("ERROR log level is not supported (allowed levels are [TRACE, DEBUG, INFO])");
        underTest.changeLevel(ERROR);
    }

    @Test
    public void changeLevel_fails_with_IAE_when_level_is_WARN() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("WARN log level is not supported (allowed levels are [TRACE, DEBUG, INFO])");
        underTest.changeLevel(WARN);
    }
}

