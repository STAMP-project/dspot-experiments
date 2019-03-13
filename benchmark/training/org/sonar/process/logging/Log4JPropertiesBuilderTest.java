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
import ProcessId.APP;
import ProcessId.COMPUTE_ENGINE;
import ProcessId.ELASTICSEARCH;
import ProcessId.WEB_SERVER;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.io.File;
import java.util.Properties;
import java.util.Random;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.sonar.process.MessageException;


@RunWith(DataProviderRunner.class)
public class Log4JPropertiesBuilderTest {
    private static final String ROLLING_POLICY_PROPERTY = "sonar.log.rollingPolicy";

    private static final String PROPERTY_MAX_FILES = "sonar.log.maxFiles";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private final RootLoggerConfig esRootLoggerConfig = RootLoggerConfig.newRootLoggerConfigBuilder().setProcessId(ELASTICSEARCH).build();

    private final Log4JPropertiesBuilder underTest = Log4JPropertiesBuilderTest.newLog4JPropertiesBuilder();

    @Test
    public void constructor_fails_with_NPE_if_Props_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Props can't be null");
        new Log4JPropertiesBuilder(null);
    }

    @Test
    public void constructor_sets_status_to_ERROR() {
        Properties properties = underTest.get();
        assertThat(properties.getProperty("status")).isEqualTo("ERROR");
    }

    @Test
    public void getRootLoggerName_returns_rootLogger() {
        assertThat(underTest.getRootLoggerName()).isEqualTo("rootLogger");
    }

    @Test
    public void get_always_returns_a_new_object() {
        Properties previous = underTest.get();
        for (int i = 0; i < (2 + (new Random().nextInt(5))); i++) {
            Properties properties = underTest.get();
            assertThat(properties).isNotSameAs(previous);
            previous = properties;
        }
    }

    @Test
    public void buildLogPattern_puts_process_key_as_process_id() {
        String pattern = underTest.buildLogPattern(RootLoggerConfig.newRootLoggerConfigBuilder().setProcessId(ELASTICSEARCH).build());
        assertThat(pattern).isEqualTo("%d{yyyy.MM.dd HH:mm:ss} %-5level es[][%logger{1.}] %msg%n");
    }

    @Test
    public void buildLogPattern_puts_threadIdFieldPattern_from_RootLoggerConfig_non_null() {
        String threadIdFieldPattern = RandomStringUtils.randomAlphabetic(5);
        String pattern = underTest.buildLogPattern(RootLoggerConfig.newRootLoggerConfigBuilder().setProcessId(APP).setThreadIdFieldPattern(threadIdFieldPattern).build());
        assertThat(pattern).isEqualTo((("%d{yyyy.MM.dd HH:mm:ss} %-5level app[" + threadIdFieldPattern) + "][%logger{1.}] %msg%n"));
    }

    @Test
    public void buildLogPattern_does_not_put_threadIdFieldPattern_from_RootLoggerConfig_is_null() {
        String pattern = underTest.buildLogPattern(RootLoggerConfig.newRootLoggerConfigBuilder().setProcessId(COMPUTE_ENGINE).build());
        assertThat(pattern).isEqualTo("%d{yyyy.MM.dd HH:mm:ss} %-5level ce[][%logger{1.}] %msg%n");
    }

    @Test
    public void buildLogPattern_does_not_put_threadIdFieldPattern_from_RootLoggerConfig_is_empty() {
        String pattern = underTest.buildLogPattern(RootLoggerConfig.newRootLoggerConfigBuilder().setProcessId(WEB_SERVER).setThreadIdFieldPattern("").build());
        assertThat(pattern).isEqualTo("%d{yyyy.MM.dd HH:mm:ss} %-5level web[][%logger{1.}] %msg%n");
    }

    @Test
    public void configureGlobalFileLog_sets_properties_for_daily_time_rolling_policy_with_max_7_files_for_empty_props() throws Exception {
        File logDir = temporaryFolder.newFolder();
        String logPattern = randomAlphanumeric(15);
        underTest.configureGlobalFileLog(esRootLoggerConfig, logDir, logPattern);
        verifyTimeRollingPolicy(underTest, logDir, logPattern, "yyyy-MM-dd", 7);
    }

    @Test
    public void time_rolling_policy_has_large_max_files_if_property_is_zero() throws Exception {
        File logDir = temporaryFolder.newFolder();
        String logPattern = "foo";
        Log4JPropertiesBuilder underTest = Log4JPropertiesBuilderTest.newLog4JPropertiesBuilder(Log4JPropertiesBuilderTest.ROLLING_POLICY_PROPERTY, "time:yyyy-MM-dd", Log4JPropertiesBuilderTest.PROPERTY_MAX_FILES, "0");
        underTest.configureGlobalFileLog(esRootLoggerConfig, logDir, logPattern);
        verifyTimeRollingPolicy(underTest, logDir, logPattern, "yyyy-MM-dd", 100000);
    }

    @Test
    public void time_rolling_policy_has_large_max_files_if_property_is_negative() throws Exception {
        File logDir = temporaryFolder.newFolder();
        String logPattern = "foo";
        Log4JPropertiesBuilder underTest = Log4JPropertiesBuilderTest.newLog4JPropertiesBuilder(Log4JPropertiesBuilderTest.ROLLING_POLICY_PROPERTY, "time:yyyy-MM-dd", Log4JPropertiesBuilderTest.PROPERTY_MAX_FILES, "-2");
        underTest.configureGlobalFileLog(esRootLoggerConfig, logDir, logPattern);
        verifyTimeRollingPolicy(underTest, logDir, logPattern, "yyyy-MM-dd", 100000);
    }

    @Test
    public void size_rolling_policy_has_large_max_files_if_property_is_zero() throws Exception {
        File logDir = temporaryFolder.newFolder();
        String logPattern = "foo";
        String sizePattern = "1KB";
        Log4JPropertiesBuilder underTest = Log4JPropertiesBuilderTest.newLog4JPropertiesBuilder(Log4JPropertiesBuilderTest.ROLLING_POLICY_PROPERTY, ("size:" + sizePattern), Log4JPropertiesBuilderTest.PROPERTY_MAX_FILES, "0");
        underTest.configureGlobalFileLog(esRootLoggerConfig, logDir, logPattern);
        verifySizeRollingPolicy(underTest, logDir, logPattern, sizePattern, 100000);
    }

    @Test
    public void size_rolling_policy_has_large_max_files_if_property_is_negative() throws Exception {
        File logDir = temporaryFolder.newFolder();
        String logPattern = "foo";
        String sizePattern = "1KB";
        Log4JPropertiesBuilder underTest = Log4JPropertiesBuilderTest.newLog4JPropertiesBuilder(Log4JPropertiesBuilderTest.ROLLING_POLICY_PROPERTY, ("size:" + sizePattern), Log4JPropertiesBuilderTest.PROPERTY_MAX_FILES, "-2");
        underTest.configureGlobalFileLog(esRootLoggerConfig, logDir, logPattern);
        verifySizeRollingPolicy(underTest, logDir, logPattern, sizePattern, 100000);
    }

    @Test
    public void configureGlobalFileLog_throws_MessageException_when_property_is_not_supported() throws Exception {
        File logDir = temporaryFolder.newFolder();
        String logPattern = randomAlphanumeric(15);
        String invalidPropertyValue = randomAlphanumeric(3);
        Log4JPropertiesBuilder underTest = Log4JPropertiesBuilderTest.newLog4JPropertiesBuilder(Log4JPropertiesBuilderTest.ROLLING_POLICY_PROPERTY, invalidPropertyValue);
        expectedException.expect(MessageException.class);
        expectedException.expectMessage(((("Unsupported value for property " + (Log4JPropertiesBuilderTest.ROLLING_POLICY_PROPERTY)) + ": ") + invalidPropertyValue));
        underTest.configureGlobalFileLog(esRootLoggerConfig, logDir, logPattern);
    }

    @Test
    public void configureGlobalFileLog_sets_properties_for_time_rolling_policy_with_max_7_files_when_property_starts_with_time_colon() throws Exception {
        File logDir = temporaryFolder.newFolder();
        String logPattern = randomAlphanumeric(15);
        String timePattern = randomAlphanumeric(6);
        Log4JPropertiesBuilder underTest = Log4JPropertiesBuilderTest.newLog4JPropertiesBuilder(Log4JPropertiesBuilderTest.ROLLING_POLICY_PROPERTY, ("time:" + timePattern));
        underTest.configureGlobalFileLog(esRootLoggerConfig, logDir, logPattern);
        verifyTimeRollingPolicy(underTest, logDir, logPattern, timePattern, 7);
    }

    @Test
    public void configureGlobalFileLog_sets_properties_for_time_rolling_policy_when_property_starts_with_time_colon_and_specified_max_number_of_files() throws Exception {
        File logDir = temporaryFolder.newFolder();
        String logPattern = randomAlphanumeric(15);
        String timePattern = randomAlphanumeric(6);
        int maxFile = 1 + (new Random().nextInt(10));
        Log4JPropertiesBuilder underTest = Log4JPropertiesBuilderTest.newLog4JPropertiesBuilder(Log4JPropertiesBuilderTest.ROLLING_POLICY_PROPERTY, ("time:" + timePattern), Log4JPropertiesBuilderTest.PROPERTY_MAX_FILES, String.valueOf(maxFile));
        underTest.configureGlobalFileLog(esRootLoggerConfig, logDir, logPattern);
        verifyTimeRollingPolicy(underTest, logDir, logPattern, timePattern, maxFile);
    }

    @Test
    public void configureGlobalFileLog_sets_properties_for_size_rolling_policy_with_max_7_files_when_property_starts_with_size_colon() throws Exception {
        File logDir = temporaryFolder.newFolder();
        String logPattern = randomAlphanumeric(15);
        String sizePattern = randomAlphanumeric(6);
        Log4JPropertiesBuilder underTest = Log4JPropertiesBuilderTest.newLog4JPropertiesBuilder(Log4JPropertiesBuilderTest.ROLLING_POLICY_PROPERTY, ("size:" + sizePattern));
        underTest.configureGlobalFileLog(esRootLoggerConfig, logDir, logPattern);
        verifySizeRollingPolicy(underTest, logDir, logPattern, sizePattern, 7);
    }

    @Test
    public void configureGlobalFileLog_sets_properties_for_size_rolling_policy_when_property_starts_with_size_colon_and_specified_max_number_of_files() throws Exception {
        File logDir = temporaryFolder.newFolder();
        String logPattern = randomAlphanumeric(15);
        String sizePattern = randomAlphanumeric(6);
        int maxFile = 1 + (new Random().nextInt(10));
        Log4JPropertiesBuilder underTest = Log4JPropertiesBuilderTest.newLog4JPropertiesBuilder(Log4JPropertiesBuilderTest.ROLLING_POLICY_PROPERTY, ("size:" + sizePattern), Log4JPropertiesBuilderTest.PROPERTY_MAX_FILES, String.valueOf(maxFile));
        underTest.configureGlobalFileLog(esRootLoggerConfig, logDir, logPattern);
        verifySizeRollingPolicy(underTest, logDir, logPattern, sizePattern, maxFile);
    }

    @Test
    public void configureGlobalFileLog_sets_properties_for_no_rolling_policy_when_property_is_none() throws Exception {
        File logDir = temporaryFolder.newFolder();
        String logPattern = randomAlphanumeric(15);
        Log4JPropertiesBuilder underTest = Log4JPropertiesBuilderTest.newLog4JPropertiesBuilder(Log4JPropertiesBuilderTest.ROLLING_POLICY_PROPERTY, "none");
        underTest.configureGlobalFileLog(esRootLoggerConfig, logDir, logPattern);
        verifyPropertiesForConfigureGlobalFileLog(underTest.get(), "appender.file_es.type", "File", "appender.file_es.name", "file_es", "appender.file_es.fileName", new File(logDir, "es.log").getAbsolutePath(), "appender.file_es.layout.type", "PatternLayout", "appender.file_es.layout.pattern", logPattern, "rootLogger.appenderRef.file_es.ref", "file_es");
    }

    @Test
    public void apply_fails_with_IAE_if_LogLevelConfig_does_not_have_rootLoggerName_of_Log4J() {
        Log4JPropertiesBuilder underTest = Log4JPropertiesBuilderTest.newLog4JPropertiesBuilder();
        LogLevelConfig logLevelConfig = LogLevelConfig.newBuilder(randomAlphanumeric(2)).build();
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Value of LogLevelConfig#rootLoggerName must be \"rootLogger\"");
        underTest.apply(logLevelConfig);
    }

    @Test
    public void apply_fails_with_IAE_if_global_property_has_unsupported_level() {
        LogLevelConfig config = newLogLevelConfig().rootLevelFor(WEB_SERVER).build();
        Log4JPropertiesBuilder underTest = Log4JPropertiesBuilderTest.newLog4JPropertiesBuilder("sonar.log.level", "ERROR");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("log level ERROR in property sonar.log.level is not a supported value (allowed levels are [TRACE, DEBUG, INFO])");
        underTest.apply(config);
    }

    @Test
    public void apply_fails_with_IAE_if_process_property_has_unsupported_level() {
        LogLevelConfig config = newLogLevelConfig().rootLevelFor(WEB_SERVER).build();
        Log4JPropertiesBuilder underTest = Log4JPropertiesBuilderTest.newLog4JPropertiesBuilder("sonar.log.level.web", "ERROR");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("log level ERROR in property sonar.log.level.web is not a supported value (allowed levels are [TRACE, DEBUG, INFO])");
        underTest.apply(config);
    }

    @Test
    public void apply_sets_root_logger_to_INFO_if_no_property_is_set() {
        LogLevelConfig config = newLogLevelConfig().rootLevelFor(WEB_SERVER).build();
        underTest.apply(config);
        verifyRootLoggerLevel(underTest, INFO);
    }

    @Test
    public void apply_sets_root_logger_to_global_property_if_set() {
        LogLevelConfig config = newLogLevelConfig().rootLevelFor(WEB_SERVER).build();
        Log4JPropertiesBuilder underTest = Log4JPropertiesBuilderTest.newLog4JPropertiesBuilder("sonar.log.level", "TRACE");
        underTest.apply(config);
        verifyRootLoggerLevel(underTest, TRACE);
    }

    @Test
    public void apply_sets_root_logger_to_process_property_if_set() {
        LogLevelConfig config = newLogLevelConfig().rootLevelFor(WEB_SERVER).build();
        Log4JPropertiesBuilder underTest = Log4JPropertiesBuilderTest.newLog4JPropertiesBuilder("sonar.log.level.web", "DEBUG");
        underTest.apply(config);
        verifyRootLoggerLevel(underTest, DEBUG);
    }

    @Test
    public void apply_sets_root_logger_to_process_property_over_global_property_if_both_set() {
        LogLevelConfig config = newLogLevelConfig().rootLevelFor(WEB_SERVER).build();
        Log4JPropertiesBuilder underTest = Log4JPropertiesBuilderTest.newLog4JPropertiesBuilder("sonar.log.level", "DEBUG", "sonar.log.level.web", "TRACE");
        underTest.apply(config);
        verifyRootLoggerLevel(underTest, TRACE);
    }

    @Test
    public void apply_sets_domain_property_over_process_and_global_property_if_all_set() {
        LogLevelConfig config = newLogLevelConfig().levelByDomain("foo", WEB_SERVER, ES).build();
        Log4JPropertiesBuilder underTest = Log4JPropertiesBuilderTest.newLog4JPropertiesBuilder("sonar.log.level", "DEBUG", "sonar.log.level.web", "DEBUG", "sonar.log.level.web.es", "TRACE");
        underTest.apply(config);
        verifyLoggerProperties(underTest.get(), "foo", TRACE);
    }

    @Test
    public void apply_sets_domain_property_over_process_property_if_both_set() {
        LogLevelConfig config = newLogLevelConfig().levelByDomain("foo", WEB_SERVER, ES).build();
        Log4JPropertiesBuilder underTest = Log4JPropertiesBuilderTest.newLog4JPropertiesBuilder("sonar.log.level.web", "DEBUG", "sonar.log.level.web.es", "TRACE");
        underTest.apply(config);
        verifyLoggerProperties(underTest.get(), "foo", TRACE);
    }

    @Test
    public void apply_sets_domain_property_over_global_property_if_both_set() {
        LogLevelConfig config = newLogLevelConfig().levelByDomain("foo", WEB_SERVER, ES).build();
        Log4JPropertiesBuilder underTest = Log4JPropertiesBuilderTest.newLog4JPropertiesBuilder("sonar.log.level", "DEBUG", "sonar.log.level.web.es", "TRACE");
        underTest.apply(config);
        verifyLoggerProperties(underTest.get(), "foo", TRACE);
    }

    @Test
    public void apply_fails_with_IAE_if_domain_property_has_unsupported_level() {
        LogLevelConfig config = newLogLevelConfig().levelByDomain("foo", WEB_SERVER, JMX).build();
        Log4JPropertiesBuilder underTest = Log4JPropertiesBuilderTest.newLog4JPropertiesBuilder("sonar.log.level.web.jmx", "ERROR");
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("log level ERROR in property sonar.log.level.web.jmx is not a supported value (allowed levels are [TRACE, DEBUG, INFO])");
        underTest.apply(config);
    }

    @Test
    public void apply_set_level_to_OFF_if_sonar_global_level_is_not_set() {
        underTest.apply(newLogLevelConfig().offUnlessTrace("fii").build());
        verifyLoggerProperties(underTest.get(), "fii", OFF);
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
    public void apply_does_not_create_loggers_property_if_only_root_level_is_defined() {
        LogLevelConfig logLevelConfig = newLogLevelConfig().rootLevelFor(APP).build();
        underTest.apply(logLevelConfig);
        assertThat(underTest.get().getProperty("loggers")).isNull();
    }

    @Test
    public void apply_creates_loggers_property_with_logger_names_ordered_but_root() {
        LogLevelConfig config = newLogLevelConfig().rootLevelFor(WEB_SERVER).levelByDomain("foo", WEB_SERVER, JMX).levelByDomain("bar", COMPUTE_ENGINE, ES).immutableLevel("doh", ERROR).immutableLevel("pif", TRACE).offUnlessTrace("fii").build();
        underTest.apply(config);
        assertThat(underTest.get().getProperty("loggers")).isEqualTo("bar,doh,fii,foo,pif");
    }

    @Test
    public void apply_does_not_set_level_if_sonar_global_level_is_TRACE() {
        Log4JPropertiesBuilder underTest = Log4JPropertiesBuilderTest.newLog4JPropertiesBuilder("sonar.log.level", TRACE.toString());
        underTest.apply(newLogLevelConfig().offUnlessTrace("fii").build());
        verifyNoLoggerProperties(underTest.get(), "fii");
    }
}

