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
package org.sonar.application;


import Level.DEBUG;
import Level.INFO;
import Level.TRACE;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.slf4j.LoggerFactory;
import org.sonar.application.config.AppSettings;
import org.sonar.application.config.TestAppSettings;
import org.sonar.application.process.StreamGobbler;

import static org.slf4j.Logger.ROOT_LOGGER_NAME;


public class AppLoggingTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private File logDir;

    private AppSettings settings = new TestAppSettings();

    private AppLogging underTest = new AppLogging(settings);

    @Test
    public void no_writing_to_sonar_log_file_when_running_from_sonar_script() {
        emulateRunFromSonarScript();
        LoggerContext ctx = underTest.configure();
        ctx.getLoggerList().forEach(AppLoggingTest::verifyNoFileAppender);
    }

    @Test
    public void root_logger_only_writes_to_console_with_formatting_when_running_from_sonar_script() {
        emulateRunFromSonarScript();
        LoggerContext ctx = underTest.configure();
        Logger rootLogger = ctx.getLogger(ROOT_LOGGER_NAME);
        ConsoleAppender<ILoggingEvent> consoleAppender = ((ConsoleAppender<ILoggingEvent>) (rootLogger.getAppender("APP_CONSOLE")));
        verifyAppFormattedLogEncoder(consoleAppender.getEncoder());
        assertThat(rootLogger.iteratorForAppenders()).hasSize(1);
    }

    @Test
    public void gobbler_logger_writes_to_console_without_formatting_when_running_from_sonar_script() {
        emulateRunFromSonarScript();
        LoggerContext ctx = underTest.configure();
        Logger gobblerLogger = ctx.getLogger(StreamGobbler.LOGGER_GOBBLER);
        verifyGobblerConsoleAppender(gobblerLogger);
        assertThat(gobblerLogger.iteratorForAppenders()).hasSize(1);
    }

    @Test
    public void root_logger_writes_to_console_with_formatting_and_to_sonar_log_file_when_running_from_command_line() {
        emulateRunFromCommandLine(false);
        LoggerContext ctx = underTest.configure();
        Logger rootLogger = ctx.getLogger(ROOT_LOGGER_NAME);
        verifyAppConsoleAppender(rootLogger.getAppender("APP_CONSOLE"));
        verifySonarLogFileAppender(rootLogger.getAppender("file_sonar"));
        assertThat(rootLogger.iteratorForAppenders()).hasSize(2);
        // verify no other logger writes to sonar.log
        ctx.getLoggerList().stream().filter(( logger) -> !(org.slf4j.Logger.ROOT_LOGGER_NAME.equals(logger.getName()))).forEach(AppLoggingTest::verifyNoFileAppender);
    }

    @Test
    public void gobbler_logger_writes_to_console_without_formatting_when_running_from_command_line() {
        emulateRunFromCommandLine(false);
        LoggerContext ctx = underTest.configure();
        Logger gobblerLogger = ctx.getLogger(StreamGobbler.LOGGER_GOBBLER);
        verifyGobblerConsoleAppender(gobblerLogger);
        assertThat(gobblerLogger.iteratorForAppenders()).hasSize(1);
    }

    @Test
    public void root_logger_writes_to_console_with_formatting_and_to_sonar_log_file_when_running_from_ITs() {
        emulateRunFromCommandLine(true);
        LoggerContext ctx = underTest.configure();
        Logger rootLogger = ctx.getLogger(ROOT_LOGGER_NAME);
        verifyAppConsoleAppender(rootLogger.getAppender("APP_CONSOLE"));
        verifySonarLogFileAppender(rootLogger.getAppender("file_sonar"));
        assertThat(rootLogger.iteratorForAppenders()).hasSize(2);
        ctx.getLoggerList().stream().filter(( logger) -> !(org.slf4j.Logger.ROOT_LOGGER_NAME.equals(logger.getName()))).forEach(AppLoggingTest::verifyNoFileAppender);
    }

    @Test
    public void gobbler_logger_writes_to_console_without_formatting_when_running_from_ITs() {
        emulateRunFromCommandLine(true);
        LoggerContext ctx = underTest.configure();
        Logger gobblerLogger = ctx.getLogger(StreamGobbler.LOGGER_GOBBLER);
        verifyGobblerConsoleAppender(gobblerLogger);
        assertThat(gobblerLogger.iteratorForAppenders()).hasSize(1);
    }

    @Test
    public void configure_no_rotation_on_sonar_file() {
        settings.getProps().set("sonar.log.rollingPolicy", "none");
        LoggerContext ctx = underTest.configure();
        Logger rootLogger = ctx.getLogger(ROOT_LOGGER_NAME);
        Appender<ILoggingEvent> appender = rootLogger.getAppender("file_sonar");
        assertThat(appender).isNotInstanceOf(RollingFileAppender.class).isInstanceOf(FileAppender.class);
    }

    @Test
    public void default_level_for_root_logger_is_INFO() {
        LoggerContext ctx = underTest.configure();
        verifyRootLogLevel(ctx, INFO);
    }

    @Test
    public void root_logger_level_changes_with_global_property() {
        settings.getProps().set("sonar.log.level", "TRACE");
        LoggerContext ctx = underTest.configure();
        verifyRootLogLevel(ctx, TRACE);
    }

    @Test
    public void root_logger_level_changes_with_app_property() {
        settings.getProps().set("sonar.log.level.app", "TRACE");
        LoggerContext ctx = underTest.configure();
        verifyRootLogLevel(ctx, TRACE);
    }

    @Test
    public void root_logger_level_is_configured_from_app_property_over_global_property() {
        settings.getProps().set("sonar.log.level", "TRACE");
        settings.getProps().set("sonar.log.level.app", "DEBUG");
        LoggerContext ctx = underTest.configure();
        verifyRootLogLevel(ctx, DEBUG);
    }

    @Test
    public void root_logger_level_changes_with_app_property_and_is_case_insensitive() {
        settings.getProps().set("sonar.log.level.app", "debug");
        LoggerContext ctx = underTest.configure();
        verifyRootLogLevel(ctx, DEBUG);
    }

    @Test
    public void default_to_INFO_if_app_property_has_invalid_value() {
        settings.getProps().set("sonar.log.level.app", "DodoDouh!");
        LoggerContext ctx = underTest.configure();
        verifyRootLogLevel(ctx, INFO);
    }

    @Test
    public void fail_with_IAE_if_global_property_unsupported_level() {
        settings.getProps().set("sonar.log.level", "ERROR");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("log level ERROR in property sonar.log.level is not a supported value (allowed levels are [TRACE, DEBUG, INFO])");
        underTest.configure();
    }

    @Test
    public void fail_with_IAE_if_app_property_unsupported_level() {
        settings.getProps().set("sonar.log.level.app", "ERROR");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("log level ERROR in property sonar.log.level.app is not a supported value (allowed levels are [TRACE, DEBUG, INFO])");
        underTest.configure();
    }

    @Test
    public void no_info_log_from_hazelcast() {
        settings.getProps().set(CLUSTER_ENABLED.getKey(), "true");
        underTest.configure();
        assertThat(LoggerFactory.getLogger("com.hazelcast").isInfoEnabled()).isEqualTo(false);
    }
}

