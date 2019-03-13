/**
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.logging.java;


import LogLevel.DEBUG;
import LogLevel.ERROR;
import LogLevel.INFO;
import LogLevel.OFF;
import LogLevel.TRACE;
import LogLevel.WARN;
import LoggingSystem.ROOT_LOGGER_NAME;
import LoggingSystemProperties.PID_KEY;
import java.io.File;
import java.io.FileFilter;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.logging.AbstractLoggingSystemTests;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggerConfiguration;
import org.springframework.boot.testsupport.rule.OutputCapture;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;


/**
 * Tests for {@link JavaLoggingSystem}.
 *
 * @author Dave Syer
 * @author Phillip Webb
 * @author Ben Hale
 */
public class JavaLoggingSystemTests extends AbstractLoggingSystemTests {
    private static final FileFilter SPRING_LOG_FILTER = ( pathname) -> pathname.getName().startsWith("spring.log");

    private final JavaLoggingSystem loggingSystem = new JavaLoggingSystem(getClass().getClassLoader());

    @Rule
    public OutputCapture output = new OutputCapture();

    private Logger logger;

    private Locale defaultLocale;

    @Test
    public void noFile() {
        this.loggingSystem.beforeInitialize();
        this.logger.info("Hidden");
        this.loggingSystem.initialize(null, null, null);
        this.logger.info("Hello world");
        String output = this.output.toString().trim();
        assertThat(output).contains("Hello world").doesNotContain("Hidden");
        assertThat(new File(((tmpDir()) + "/spring.log")).exists()).isFalse();
    }

    @Test
    public void withFile() {
        File temp = new File(tmpDir());
        File[] logFiles = temp.listFiles(JavaLoggingSystemTests.SPRING_LOG_FILTER);
        for (File file : logFiles) {
            file.delete();
        }
        this.loggingSystem.beforeInitialize();
        this.logger.info("Hidden");
        this.loggingSystem.initialize(null, null, getLogFile(null, tmpDir()));
        this.logger.info("Hello world");
        String output = this.output.toString().trim();
        assertThat(output).contains("Hello world").doesNotContain("Hidden");
        assertThat(temp.listFiles(JavaLoggingSystemTests.SPRING_LOG_FILTER).length).isGreaterThan(0);
    }

    @Test
    public void testCustomFormatter() {
        this.loggingSystem.beforeInitialize();
        this.loggingSystem.initialize(null, null, null);
        this.logger.info("Hello world");
        String output = this.output.toString().trim();
        assertThat(output).contains("Hello world").contains("???? INFO [");
    }

    @Test
    public void testSystemPropertyInitializesFormat() {
        System.setProperty(PID_KEY, "1234");
        this.loggingSystem.beforeInitialize();
        this.loggingSystem.initialize(null, ("classpath:" + (ClassUtils.addResourcePathToPackagePath(getClass(), "logging.properties"))), null);
        this.logger.info("Hello world");
        this.logger.info("Hello world");
        String output = this.output.toString().trim();
        assertThat(output).contains("Hello world").contains("1234 INFO [");
    }

    @Test
    public void testNonDefaultConfigLocation() {
        this.loggingSystem.beforeInitialize();
        this.loggingSystem.initialize(null, "classpath:logging-nondefault.properties", null);
        this.logger.info("Hello world");
        String output = this.output.toString().trim();
        assertThat(output).contains("INFO: Hello");
    }

    @Test
    public void testNonexistentConfigLocation() {
        this.loggingSystem.beforeInitialize();
        assertThatIllegalStateException().isThrownBy(() -> this.loggingSystem.initialize(null, "classpath:logging-nonexistent.properties", null));
    }

    @Test
    public void getSupportedLevels() {
        assertThat(this.loggingSystem.getSupportedLogLevels()).isEqualTo(EnumSet.of(TRACE, DEBUG, INFO, WARN, ERROR, OFF));
    }

    @Test
    public void setLevel() {
        this.loggingSystem.beforeInitialize();
        this.loggingSystem.initialize(null, null, null);
        this.logger.fine("Hello");
        this.loggingSystem.setLogLevel("org.springframework.boot", DEBUG);
        this.logger.fine("Hello");
        assertThat(StringUtils.countOccurrencesOf(this.output.toString(), "Hello")).isEqualTo(1);
    }

    @Test
    public void setLevelToNull() {
        this.loggingSystem.beforeInitialize();
        this.loggingSystem.initialize(null, null, null);
        this.logger.fine("Hello");
        this.loggingSystem.setLogLevel("org.springframework.boot", DEBUG);
        this.logger.fine("Hello");
        this.loggingSystem.setLogLevel("org.springframework.boot", null);
        this.logger.fine("Hello");
        assertThat(StringUtils.countOccurrencesOf(this.output.toString(), "Hello")).isEqualTo(1);
    }

    @Test
    public void getLoggingConfigurations() {
        this.loggingSystem.beforeInitialize();
        this.loggingSystem.initialize(null, null, null);
        this.loggingSystem.setLogLevel(getClass().getName(), DEBUG);
        List<LoggerConfiguration> configurations = this.loggingSystem.getLoggerConfigurations();
        assertThat(configurations).isNotEmpty();
        assertThat(configurations.get(0).getName()).isEqualTo(ROOT_LOGGER_NAME);
    }

    @Test
    public void getLoggingConfiguration() {
        this.loggingSystem.beforeInitialize();
        this.loggingSystem.initialize(null, null, null);
        this.loggingSystem.setLogLevel(getClass().getName(), DEBUG);
        LoggerConfiguration configuration = this.loggingSystem.getLoggerConfiguration(getClass().getName());
        assertThat(configuration).isEqualTo(new LoggerConfiguration(getClass().getName(), LogLevel.DEBUG, LogLevel.DEBUG));
    }
}

