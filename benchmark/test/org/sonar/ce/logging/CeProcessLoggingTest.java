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
package org.sonar.ce.logging;


import Level.DEBUG;
import Level.INFO;
import Level.OFF;
import Level.TRACE;
import Logger.ROOT_LOGGER_NAME;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import java.io.File;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.sonar.process.Props;


public class CeProcessLoggingTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private File logDir;

    private Props props = new Props(new Properties());

    private CeProcessLogging underTest = new CeProcessLogging();

    @Test
    public void do_not_log_to_console() {
        LoggerContext ctx = underTest.configure(props);
        Logger root = ctx.getLogger(ROOT_LOGGER_NAME);
        Appender appender = root.getAppender("CONSOLE");
        assertThat(appender).isNull();
    }

    @Test
    public void startup_logger_prints_to_only_to_system_out() {
        LoggerContext ctx = underTest.configure(props);
        Logger startup = ctx.getLogger("startup");
        assertThat(startup.isAdditive()).isFalse();
        Appender appender = startup.getAppender("CONSOLE");
        assertThat(appender).isInstanceOf(ConsoleAppender.class);
        ConsoleAppender<ILoggingEvent> consoleAppender = ((ConsoleAppender<ILoggingEvent>) (appender));
        assertThat(consoleAppender.getTarget()).isEqualTo("System.out");
        assertThat(consoleAppender.getEncoder()).isInstanceOf(PatternLayoutEncoder.class);
        PatternLayoutEncoder patternEncoder = ((PatternLayoutEncoder) (consoleAppender.getEncoder()));
        assertThat(patternEncoder.getPattern()).isEqualTo("%d{yyyy.MM.dd HH:mm:ss} %-5level app[][%logger{20}] %msg%n");
    }

    @Test
    public void log_to_ce_file() {
        LoggerContext ctx = underTest.configure(props);
        Logger root = ctx.getLogger(ROOT_LOGGER_NAME);
        Appender<ILoggingEvent> appender = root.getAppender("file_ce");
        assertThat(appender).isInstanceOf(FileAppender.class);
        FileAppender fileAppender = ((FileAppender) (appender));
        assertThat(fileAppender.getFile()).isEqualTo(new File(logDir, "ce.log").getAbsolutePath());
        assertThat(fileAppender.getEncoder()).isInstanceOf(PatternLayoutEncoder.class);
        PatternLayoutEncoder encoder = ((PatternLayoutEncoder) (fileAppender.getEncoder()));
        assertThat(encoder.getPattern()).isEqualTo("%d{yyyy.MM.dd HH:mm:ss} %-5level ce[%X{ceTaskUuid}][%logger{20}] %msg%n");
    }

    @Test
    public void default_level_for_root_logger_is_INFO() {
        LoggerContext ctx = underTest.configure(props);
        verifyRootLogLevel(ctx, INFO);
    }

    @Test
    public void root_logger_level_changes_with_global_property() {
        props.set("sonar.log.level", "TRACE");
        LoggerContext ctx = underTest.configure(props);
        verifyRootLogLevel(ctx, TRACE);
    }

    @Test
    public void root_logger_level_changes_with_ce_property() {
        props.set("sonar.log.level.ce", "TRACE");
        LoggerContext ctx = underTest.configure(props);
        verifyRootLogLevel(ctx, TRACE);
    }

    @Test
    public void root_logger_level_is_configured_from_ce_property_over_global_property() {
        props.set("sonar.log.level", "TRACE");
        props.set("sonar.log.level.ce", "DEBUG");
        LoggerContext ctx = underTest.configure(props);
        verifyRootLogLevel(ctx, DEBUG);
    }

    @Test
    public void root_logger_level_changes_with_ce_property_and_is_case_insensitive() {
        props.set("sonar.log.level.ce", "debug");
        LoggerContext ctx = underTest.configure(props);
        verifyRootLogLevel(ctx, DEBUG);
    }

    @Test
    public void sql_logger_level_changes_with_global_property_and_is_case_insensitive() {
        props.set("sonar.log.level", "InFO");
        LoggerContext ctx = underTest.configure(props);
        verifySqlLogLevel(ctx, INFO);
    }

    @Test
    public void sql_logger_level_changes_with_ce_property_and_is_case_insensitive() {
        props.set("sonar.log.level.ce", "TrACe");
        LoggerContext ctx = underTest.configure(props);
        verifySqlLogLevel(ctx, TRACE);
    }

    @Test
    public void sql_logger_level_changes_with_ce_sql_property_and_is_case_insensitive() {
        props.set("sonar.log.level.ce.sql", "debug");
        LoggerContext ctx = underTest.configure(props);
        verifySqlLogLevel(ctx, DEBUG);
    }

    @Test
    public void sql_logger_level_is_configured_from_ce_sql_property_over_ce_property() {
        props.set("sonar.log.level.ce.sql", "debug");
        props.set("sonar.log.level.ce", "TRACE");
        LoggerContext ctx = underTest.configure(props);
        verifySqlLogLevel(ctx, DEBUG);
    }

    @Test
    public void sql_logger_level_is_configured_from_ce_sql_property_over_global_property() {
        props.set("sonar.log.level.ce.sql", "debug");
        props.set("sonar.log.level", "TRACE");
        LoggerContext ctx = underTest.configure(props);
        verifySqlLogLevel(ctx, DEBUG);
    }

    @Test
    public void sql_logger_level_is_configured_from_ce_property_over_global_property() {
        props.set("sonar.log.level.ce", "debug");
        props.set("sonar.log.level", "TRACE");
        LoggerContext ctx = underTest.configure(props);
        verifySqlLogLevel(ctx, DEBUG);
    }

    @Test
    public void es_logger_level_changes_with_global_property_and_is_case_insensitive() {
        props.set("sonar.log.level", "InFO");
        LoggerContext ctx = underTest.configure(props);
        verifyEsLogLevel(ctx, INFO);
    }

    @Test
    public void es_logger_level_changes_with_ce_property_and_is_case_insensitive() {
        props.set("sonar.log.level.ce", "TrACe");
        LoggerContext ctx = underTest.configure(props);
        verifyEsLogLevel(ctx, TRACE);
    }

    @Test
    public void es_logger_level_changes_with_ce_es_property_and_is_case_insensitive() {
        props.set("sonar.log.level.ce.es", "debug");
        LoggerContext ctx = underTest.configure(props);
        verifyEsLogLevel(ctx, DEBUG);
    }

    @Test
    public void es_logger_level_is_configured_from_ce_es_property_over_ce_property() {
        props.set("sonar.log.level.ce.es", "debug");
        props.set("sonar.log.level.ce", "TRACE");
        LoggerContext ctx = underTest.configure(props);
        verifyEsLogLevel(ctx, DEBUG);
    }

    @Test
    public void es_logger_level_is_configured_from_ce_es_property_over_global_property() {
        props.set("sonar.log.level.ce.es", "debug");
        props.set("sonar.log.level", "TRACE");
        LoggerContext ctx = underTest.configure(props);
        verifyEsLogLevel(ctx, DEBUG);
    }

    @Test
    public void es_logger_level_is_configured_from_ce_property_over_global_property() {
        props.set("sonar.log.level.ce", "debug");
        props.set("sonar.log.level", "TRACE");
        LoggerContext ctx = underTest.configure(props);
        verifyEsLogLevel(ctx, DEBUG);
    }

    @Test
    public void jmx_logger_level_changes_with_global_property_and_is_case_insensitive() {
        props.set("sonar.log.level", "InFO");
        LoggerContext ctx = underTest.configure(props);
        verifyJmxLogLevel(ctx, INFO);
    }

    @Test
    public void jmx_logger_level_changes_with_jmx_property_and_is_case_insensitive() {
        props.set("sonar.log.level.ce", "TrACe");
        LoggerContext ctx = underTest.configure(props);
        verifyJmxLogLevel(ctx, TRACE);
    }

    @Test
    public void jmx_logger_level_changes_with_ce_jmx_property_and_is_case_insensitive() {
        props.set("sonar.log.level.ce.jmx", "debug");
        LoggerContext ctx = underTest.configure(props);
        verifyJmxLogLevel(ctx, DEBUG);
    }

    @Test
    public void jmx_logger_level_is_configured_from_ce_jmx_property_over_ce_property() {
        props.set("sonar.log.level.ce.jmx", "debug");
        props.set("sonar.log.level.ce", "TRACE");
        LoggerContext ctx = underTest.configure(props);
        verifyJmxLogLevel(ctx, DEBUG);
    }

    @Test
    public void jmx_logger_level_is_configured_from_ce_jmx_property_over_global_property() {
        props.set("sonar.log.level.ce.jmx", "debug");
        props.set("sonar.log.level", "TRACE");
        LoggerContext ctx = underTest.configure(props);
        verifyJmxLogLevel(ctx, DEBUG);
    }

    @Test
    public void jmx_logger_level_is_configured_from_ce_property_over_global_property() {
        props.set("sonar.log.level.ce", "debug");
        props.set("sonar.log.level", "TRACE");
        LoggerContext ctx = underTest.configure(props);
        verifyJmxLogLevel(ctx, DEBUG);
    }

    @Test
    public void root_logger_level_defaults_to_INFO_if_ce_property_has_invalid_value() {
        props.set("sonar.log.level.ce", "DodoDouh!");
        LoggerContext ctx = underTest.configure(props);
        verifyRootLogLevel(ctx, INFO);
    }

    @Test
    public void sql_logger_level_defaults_to_INFO_if_ce_sql_property_has_invalid_value() {
        props.set("sonar.log.level.ce.sql", "DodoDouh!");
        LoggerContext ctx = underTest.configure(props);
        verifySqlLogLevel(ctx, INFO);
    }

    @Test
    public void es_logger_level_defaults_to_INFO_if_ce_es_property_has_invalid_value() {
        props.set("sonar.log.level.ce.es", "DodoDouh!");
        LoggerContext ctx = underTest.configure(props);
        verifyEsLogLevel(ctx, INFO);
    }

    @Test
    public void jmx_loggers_level_defaults_to_INFO_if_ce_jmx_property_has_invalid_value() {
        props.set("sonar.log.level.ce.jmx", "DodoDouh!");
        LoggerContext ctx = underTest.configure(props);
        verifyJmxLogLevel(ctx, INFO);
    }

    @Test
    public void fail_with_IAE_if_global_property_unsupported_level() {
        props.set("sonar.log.level", "ERROR");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("log level ERROR in property sonar.log.level is not a supported value (allowed levels are [TRACE, DEBUG, INFO])");
        underTest.configure(props);
    }

    @Test
    public void fail_with_IAE_if_ce_property_unsupported_level() {
        props.set("sonar.log.level.ce", "ERROR");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("log level ERROR in property sonar.log.level.ce is not a supported value (allowed levels are [TRACE, DEBUG, INFO])");
        underTest.configure(props);
    }

    @Test
    public void fail_with_IAE_if_ce_sql_property_unsupported_level() {
        props.set("sonar.log.level.ce.sql", "ERROR");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("log level ERROR in property sonar.log.level.ce.sql is not a supported value (allowed levels are [TRACE, DEBUG, INFO])");
        underTest.configure(props);
    }

    @Test
    public void fail_with_IAE_if_ce_es_property_unsupported_level() {
        props.set("sonar.log.level.ce.es", "ERROR");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("log level ERROR in property sonar.log.level.ce.es is not a supported value (allowed levels are [TRACE, DEBUG, INFO])");
        underTest.configure(props);
    }

    @Test
    public void fail_with_IAE_if_ce_jmx_property_unsupported_level() {
        props.set("sonar.log.level.ce.jmx", "ERROR");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("log level ERROR in property sonar.log.level.ce.jmx is not a supported value (allowed levels are [TRACE, DEBUG, INFO])");
        underTest.configure(props);
    }

    @Test
    public void configure_defines_hardcoded_levels() {
        LoggerContext context = underTest.configure(props);
        verifyImmutableLogLevels(context);
    }

    @Test
    public void configure_defines_hardcoded_levels_unchanged_by_global_property() {
        props.set("sonar.log.level", "TRACE");
        LoggerContext context = underTest.configure(props);
        verifyImmutableLogLevels(context);
    }

    @Test
    public void configure_defines_hardcoded_levels_unchanged_by_ce_property() {
        props.set("sonar.log.level.ce", "TRACE");
        LoggerContext context = underTest.configure(props);
        verifyImmutableLogLevels(context);
    }

    @Test
    public void configure_turns_off_some_MsSQL_driver_logger() {
        LoggerContext context = underTest.configure(props);
        Stream.of("com.microsoft.sqlserver.jdbc.internals", "com.microsoft.sqlserver.jdbc.ResultSet", "com.microsoft.sqlserver.jdbc.Statement", "com.microsoft.sqlserver.jdbc.Connection").forEach(( loggerName) -> assertThat(context.getLogger(loggerName).getLevel()).isEqualTo(OFF));
    }
}

