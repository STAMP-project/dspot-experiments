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
package org.sonar.process.logging;


import Level.DEBUG;
import Level.ERROR;
import Level.INFO;
import Level.OFF;
import Level.TRACE;
import LogDomain.ES;
import LogDomain.JMX;
import LogbackHelper.RollingPolicy;
import ProcessId.APP;
import ProcessId.COMPUTE_ENGINE;
import ProcessId.ELASTICSEARCH;
import ProcessId.WEB_SERVER;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import com.google.common.collect.ImmutableList;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.sonar.process.MessageException;
import org.sonar.process.Props;

import static org.slf4j.Logger.ROOT_LOGGER_NAME;


@RunWith(DataProviderRunner.class)
public class LogbackHelperTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Props props = new Props(new Properties());

    private LogbackHelper underTest = new LogbackHelper();

    @Test
    public void getRootContext() {
        assertThat(underTest.getRootContext()).isNotNull();
    }

    @Test
    public void buildLogPattern_puts_process_key_as_process_id() {
        String pattern = underTest.buildLogPattern(RootLoggerConfig.newRootLoggerConfigBuilder().setProcessId(ELASTICSEARCH).build());
        assertThat(pattern).isEqualTo("%d{yyyy.MM.dd HH:mm:ss} %-5level es[][%logger{20}] %msg%n");
    }

    @Test
    public void buildLogPattern_puts_threadIdFieldPattern_from_RootLoggerConfig_non_null() {
        String threadIdFieldPattern = RandomStringUtils.randomAlphabetic(5);
        String pattern = underTest.buildLogPattern(RootLoggerConfig.newRootLoggerConfigBuilder().setProcessId(APP).setThreadIdFieldPattern(threadIdFieldPattern).build());
        assertThat(pattern).isEqualTo((("%d{yyyy.MM.dd HH:mm:ss} %-5level app[" + threadIdFieldPattern) + "][%logger{20}] %msg%n"));
    }

    @Test
    public void buildLogPattern_does_not_put_threadIdFieldPattern_from_RootLoggerConfig_is_null() {
        String pattern = underTest.buildLogPattern(RootLoggerConfig.newRootLoggerConfigBuilder().setProcessId(COMPUTE_ENGINE).build());
        assertThat(pattern).isEqualTo("%d{yyyy.MM.dd HH:mm:ss} %-5level ce[][%logger{20}] %msg%n");
    }

    @Test
    public void buildLogPattern_does_not_put_threadIdFieldPattern_from_RootLoggerConfig_is_empty() {
        String pattern = underTest.buildLogPattern(RootLoggerConfig.newRootLoggerConfigBuilder().setProcessId(WEB_SERVER).setThreadIdFieldPattern("").build());
        assertThat(pattern).isEqualTo("%d{yyyy.MM.dd HH:mm:ss} %-5level web[][%logger{20}] %msg%n");
    }

    @Test
    public void enableJulChangePropagation() {
        LoggerContext ctx = underTest.getRootContext();
        int countListeners = ctx.getCopyOfListenerList().size();
        LoggerContextListener propagator = underTest.enableJulChangePropagation(ctx);
        assertThat(ctx.getCopyOfListenerList().size()).isEqualTo((countListeners + 1));
        ctx.removeListener(propagator);
    }

    @Test
    public void verify_jul_initialization() {
        LoggerContext ctx = underTest.getRootContext();
        String logbackRootLoggerName = underTest.getRootLoggerName();
        LogLevelConfig config = LogLevelConfig.newBuilder(logbackRootLoggerName).levelByDomain(logbackRootLoggerName, WEB_SERVER, JMX).build();
        props.set("sonar.log.level.web", "TRACE");
        underTest.apply(config, props);
        LogbackHelperTest.MemoryAppender memoryAppender = new LogbackHelperTest.MemoryAppender();
        start();
        underTest.getRootContext().getLogger(logbackRootLoggerName).addAppender(memoryAppender);
        Logger julLogger = Logger.getLogger("com.ms.sqlserver.jdbc.DTV");
        julLogger.finest("Message1");
        julLogger.finer("Message1");
        julLogger.fine("Message1");
        julLogger.info("Message1");
        julLogger.warning("Message1");
        julLogger.severe("Message1");
        // JUL bridge has not been initialized, nothing in logs
        assertThat(memoryAppender.getLogs()).hasSize(0);
        // Enabling JUL bridge
        LoggerContextListener propagator = underTest.enableJulChangePropagation(ctx);
        julLogger.finest("Message2");
        julLogger.finer("Message2");
        julLogger.fine("Message2");
        julLogger.info("Message2");
        julLogger.warning("Message2");
        julLogger.severe("Message2");
        assertThat(julLogger.isLoggable(Level.FINEST)).isTrue();
        assertThat(julLogger.isLoggable(Level.FINER)).isTrue();
        assertThat(julLogger.isLoggable(Level.FINE)).isTrue();
        assertThat(julLogger.isLoggable(Level.INFO)).isTrue();
        assertThat(julLogger.isLoggable(Level.SEVERE)).isTrue();
        assertThat(julLogger.isLoggable(Level.WARNING)).isTrue();
        // We are expecting messages from info to severe
        assertThat(memoryAppender.getLogs()).hasSize(6);
        memoryAppender.clear();
        ctx.getLogger(logbackRootLoggerName).setLevel(INFO);
        julLogger.finest("Message3");
        julLogger.finer("Message3");
        julLogger.fine("Message3");
        julLogger.info("Message3");
        julLogger.warning("Message3");
        julLogger.severe("Message3");
        // We are expecting messages from finest to severe in TRACE mode
        assertThat(memoryAppender.getLogs()).hasSize(3);
        memoryAppender.clear();
        stop();
        ctx.removeListener(propagator);
    }

    @Test
    public void newConsoleAppender() {
        LoggerContext ctx = underTest.getRootContext();
        ConsoleAppender<?> appender = underTest.newConsoleAppender(ctx, "MY_APPENDER", "%msg%n");
        assertThat(appender.getName()).isEqualTo("MY_APPENDER");
        assertThat(appender.getContext()).isSameAs(ctx);
        assertThat(appender.isStarted()).isTrue();
        assertThat(getPattern()).isEqualTo("%msg%n");
        assertThat(appender.getCopyOfAttachedFiltersList()).isEmpty();
    }

    @Test
    public void createRollingPolicy_defaults() {
        LoggerContext ctx = underTest.getRootContext();
        LogbackHelper.RollingPolicy policy = underTest.createRollingPolicy(ctx, props, "sonar");
        FileAppender appender = policy.createAppender("SONAR_FILE");
        assertThat(appender).isInstanceOf(RollingFileAppender.class);
        // max 5 daily files
        RollingFileAppender fileAppender = ((RollingFileAppender) (appender));
        TimeBasedRollingPolicy triggeringPolicy = ((TimeBasedRollingPolicy) (fileAppender.getTriggeringPolicy()));
        assertThat(triggeringPolicy.getMaxHistory()).isEqualTo(7);
        assertThat(triggeringPolicy.getFileNamePattern()).endsWith("sonar.%d{yyyy-MM-dd}.log");
    }

    @Test
    public void createRollingPolicy_none() {
        props.set("sonar.log.rollingPolicy", "none");
        LoggerContext ctx = underTest.getRootContext();
        LogbackHelper.RollingPolicy policy = underTest.createRollingPolicy(ctx, props, "sonar");
        Appender appender = policy.createAppender("SONAR_FILE");
        assertThat(appender).isNotInstanceOf(RollingFileAppender.class).isInstanceOf(FileAppender.class);
    }

    @Test
    public void createRollingPolicy_size() throws Exception {
        props.set("sonar.log.rollingPolicy", "size:1MB");
        props.set("sonar.log.maxFiles", "20");
        LoggerContext ctx = underTest.getRootContext();
        LogbackHelper.RollingPolicy policy = underTest.createRollingPolicy(ctx, props, "sonar");
        Appender appender = policy.createAppender("SONAR_FILE");
        assertThat(appender).isInstanceOf(RollingFileAppender.class);
        // max 20 files of 1Mb
        RollingFileAppender fileAppender = ((RollingFileAppender) (appender));
        FixedWindowRollingPolicy rollingPolicy = ((FixedWindowRollingPolicy) (fileAppender.getRollingPolicy()));
        assertThat(rollingPolicy.getMaxIndex()).isEqualTo(20);
        assertThat(rollingPolicy.getFileNamePattern()).endsWith("sonar.%i.log");
        SizeBasedTriggeringPolicy triggeringPolicy = ((SizeBasedTriggeringPolicy) (fileAppender.getTriggeringPolicy()));
        FileSize maxFileSize = ((FileSize) (FieldUtils.readField(triggeringPolicy, "maxFileSize", true)));
        assertThat(maxFileSize.getSize()).isEqualTo((1024L * 1024));
    }

    @Test
    public void createRollingPolicy_time() {
        props.set("sonar.log.rollingPolicy", "time:yyyy-MM");
        props.set("sonar.log.maxFiles", "20");
        LoggerContext ctx = underTest.getRootContext();
        LogbackHelper.RollingPolicy policy = underTest.createRollingPolicy(ctx, props, "sonar");
        RollingFileAppender appender = ((RollingFileAppender) (policy.createAppender("SONAR_FILE")));
        // max 5 monthly files
        TimeBasedRollingPolicy triggeringPolicy = ((TimeBasedRollingPolicy) (appender.getTriggeringPolicy()));
        assertThat(triggeringPolicy.getMaxHistory()).isEqualTo(20);
        assertThat(triggeringPolicy.getFileNamePattern()).endsWith("sonar.%d{yyyy-MM}.log");
    }

    @Test
    public void createRollingPolicy_fail_if_unknown_policy() {
        props.set("sonar.log.rollingPolicy", "unknown:foo");
        try {
            LoggerContext ctx = underTest.getRootContext();
            underTest.createRollingPolicy(ctx, props, "sonar");
            Assert.fail();
        } catch (MessageException e) {
            assertThat(e).hasMessage("Unsupported value for property sonar.log.rollingPolicy: unknown:foo");
        }
    }

    @Test
    public void apply_fails_with_IAE_if_LogLevelConfig_does_not_have_ROOT_LOGGER_NAME_of_LogBack() {
        LogLevelConfig logLevelConfig = LogLevelConfig.newBuilder(randomAlphanumeric(2)).build();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Value of LogLevelConfig#rootLoggerName must be \"ROOT\"");
        underTest.apply(logLevelConfig, props);
    }

    @Test
    public void apply_fails_with_IAE_if_global_property_has_unsupported_level() {
        LogLevelConfig config = newLogLevelConfig().rootLevelFor(WEB_SERVER).build();
        props.set("sonar.log.level", "ERROR");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("log level ERROR in property sonar.log.level is not a supported value (allowed levels are [TRACE, DEBUG, INFO])");
        underTest.apply(config, props);
    }

    @Test
    public void apply_fails_with_IAE_if_process_property_has_unsupported_level() {
        LogLevelConfig config = newLogLevelConfig().rootLevelFor(WEB_SERVER).build();
        props.set("sonar.log.level.web", "ERROR");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("log level ERROR in property sonar.log.level.web is not a supported value (allowed levels are [TRACE, DEBUG, INFO])");
        underTest.apply(config, props);
    }

    @Test
    public void apply_sets_logger_to_INFO_if_no_property_is_set() {
        LogLevelConfig config = newLogLevelConfig().rootLevelFor(WEB_SERVER).build();
        LoggerContext context = underTest.apply(config, props);
        assertThat(context.getLogger(ROOT_LOGGER_NAME).getLevel()).isEqualTo(INFO);
    }

    @Test
    public void apply_sets_logger_to_globlal_property_if_set() {
        LogLevelConfig config = newLogLevelConfig().rootLevelFor(WEB_SERVER).build();
        props.set("sonar.log.level", "TRACE");
        LoggerContext context = underTest.apply(config, props);
        assertThat(context.getLogger(ROOT_LOGGER_NAME).getLevel()).isEqualTo(TRACE);
    }

    @Test
    public void apply_sets_logger_to_process_property_if_set() {
        LogLevelConfig config = newLogLevelConfig().rootLevelFor(WEB_SERVER).build();
        props.set("sonar.log.level.web", "DEBUG");
        LoggerContext context = underTest.apply(config, props);
        assertThat(context.getLogger(ROOT_LOGGER_NAME).getLevel()).isEqualTo(DEBUG);
    }

    @Test
    public void apply_sets_logger_to_process_property_over_global_property_if_both_set() {
        LogLevelConfig config = newLogLevelConfig().rootLevelFor(WEB_SERVER).build();
        props.set("sonar.log.level", "DEBUG");
        props.set("sonar.log.level.web", "TRACE");
        LoggerContext context = underTest.apply(config, props);
        assertThat(context.getLogger(ROOT_LOGGER_NAME).getLevel()).isEqualTo(TRACE);
    }

    @Test
    public void apply_sets_domain_property_over_process_and_global_property_if_all_set() {
        LogLevelConfig config = newLogLevelConfig().levelByDomain("foo", WEB_SERVER, ES).build();
        props.set("sonar.log.level", "DEBUG");
        props.set("sonar.log.level.web", "DEBUG");
        props.set("sonar.log.level.web.es", "TRACE");
        LoggerContext context = underTest.apply(config, props);
        assertThat(context.getLogger("foo").getLevel()).isEqualTo(TRACE);
    }

    @Test
    public void apply_sets_domain_property_over_process_property_if_both_set() {
        LogLevelConfig config = newLogLevelConfig().levelByDomain("foo", WEB_SERVER, ES).build();
        props.set("sonar.log.level.web", "DEBUG");
        props.set("sonar.log.level.web.es", "TRACE");
        LoggerContext context = underTest.apply(config, props);
        assertThat(context.getLogger("foo").getLevel()).isEqualTo(TRACE);
    }

    @Test
    public void apply_sets_domain_property_over_global_property_if_both_set() {
        LogLevelConfig config = newLogLevelConfig().levelByDomain("foo", WEB_SERVER, ES).build();
        props.set("sonar.log.level", "DEBUG");
        props.set("sonar.log.level.web.es", "TRACE");
        LoggerContext context = underTest.apply(config, props);
        assertThat(context.getLogger("foo").getLevel()).isEqualTo(TRACE);
    }

    @Test
    public void apply_fails_with_IAE_if_domain_property_has_unsupported_level() {
        LogLevelConfig config = newLogLevelConfig().levelByDomain("foo", WEB_SERVER, JMX).build();
        props.set("sonar.log.level.web.jmx", "ERROR");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("log level ERROR in property sonar.log.level.web.jmx is not a supported value (allowed levels are [TRACE, DEBUG, INFO])");
        underTest.apply(config, props);
    }

    @Test
    public void changeRoot_sets_level_of_ROOT_and_all_loggers_with_a_config_but_the_hardcoded_one() {
        LogLevelConfig config = newLogLevelConfig().rootLevelFor(WEB_SERVER).levelByDomain("foo", WEB_SERVER, JMX).levelByDomain("bar", COMPUTE_ENGINE, ES).immutableLevel("doh", ERROR).immutableLevel("pif", TRACE).build();
        LoggerContext context = underTest.apply(config, props);
        assertThat(context.getLogger(ROOT_LOGGER_NAME).getLevel()).isEqualTo(INFO);
        assertThat(context.getLogger("foo").getLevel()).isEqualTo(INFO);
        assertThat(context.getLogger("bar").getLevel()).isEqualTo(INFO);
        assertThat(context.getLogger("doh").getLevel()).isEqualTo(ERROR);
        assertThat(context.getLogger("pif").getLevel()).isEqualTo(TRACE);
        underTest.changeRoot(config, DEBUG);
        assertThat(context.getLogger(ROOT_LOGGER_NAME).getLevel()).isEqualTo(DEBUG);
        assertThat(context.getLogger("foo").getLevel()).isEqualTo(DEBUG);
        assertThat(context.getLogger("bar").getLevel()).isEqualTo(DEBUG);
        assertThat(context.getLogger("doh").getLevel()).isEqualTo(ERROR);
        assertThat(context.getLogger("pif").getLevel()).isEqualTo(TRACE);
    }

    @Test
    public void apply_set_level_to_OFF_if_sonar_global_level_is_not_set() {
        LoggerContext context = underTest.apply(newLogLevelConfig().offUnlessTrace("fii").build(), new Props(new Properties()));
        assertThat(context.getLogger("fii").getLevel()).isEqualTo(OFF);
    }

    @Test
    public void apply_set_level_to_OFF_if_sonar_global_level_is_INFO() {
        setLevelToOff(INFO);
    }

    @Test
    public void apply_set_level_to_OFF_if_sonar_global_level_is_DEBUG() {
        setLevelToOff(DEBUG);
    }

    @Test
    public void apply_does_not_set_level_if_sonar_global_level_is_TRACE() {
        Properties properties = new Properties();
        properties.setProperty("sonar.log.level", TRACE.toString());
        assertThat(underTest.getRootContext().getLogger("fii").getLevel()).isNull();
        LoggerContext context = underTest.apply(newLogLevelConfig().offUnlessTrace("fii").build(), new Props(properties));
        assertThat(context.getLogger("fii").getLevel()).isNull();
    }

    public static class MemoryAppender extends AppenderBase<ILoggingEvent> {
        private static final List<ILoggingEvent> LOGS = new ArrayList<>();

        @Override
        protected void append(ILoggingEvent eventObject) {
            LogbackHelperTest.MemoryAppender.LOGS.add(eventObject);
        }

        public List<ILoggingEvent> getLogs() {
            return ImmutableList.copyOf(LogbackHelperTest.MemoryAppender.LOGS);
        }

        public void clear() {
            LogbackHelperTest.MemoryAppender.LOGS.clear();
        }
    }
}

