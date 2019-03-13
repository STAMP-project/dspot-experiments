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
package org.springframework.boot.logging.log4j2;


import LogLevel.DEBUG;
import LoggingSystem.ROOT_LOGGER_NAME;
import LoggingSystemProperties.EXCEPTION_CONVERSION_WORD;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.logging.AbstractLoggingSystemTests;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggerConfiguration;
import org.springframework.boot.testsupport.assertj.Matched;
import org.springframework.boot.testsupport.rule.OutputCapture;
import org.springframework.util.StringUtils;

import static java.util.logging.Logger.getLogger;


/**
 * Tests for {@link Log4J2LoggingSystem}.
 *
 * @author Daniel Fullarton
 * @author Phillip Webb
 * @author Andy Wilkinson
 * @author Ben Hale
 */
public class Log4J2LoggingSystemTests extends AbstractLoggingSystemTests {
    @Rule
    public OutputCapture output = new OutputCapture();

    private final Log4J2LoggingSystemTests.TestLog4J2LoggingSystem loggingSystem = new Log4J2LoggingSystemTests.TestLog4J2LoggingSystem();

    private Logger logger;

    @Test
    public void noFile() {
        beforeInitialize();
        this.logger.info("Hidden");
        initialize(null, null, null);
        this.logger.info("Hello world");
        String output = this.output.toString().trim();
        Configuration configuration = this.loggingSystem.getConfiguration();
        assertThat(output).contains("Hello world").doesNotContain("Hidden");
        assertThat(new File(((tmpDir()) + "/spring.log")).exists()).isFalse();
        assertThat(configuration.getConfigurationSource().getFile()).isNotNull();
    }

    @Test
    public void withFile() {
        beforeInitialize();
        this.logger.info("Hidden");
        this.loggingSystem.initialize(null, null, getLogFile(null, tmpDir()));
        this.logger.info("Hello world");
        String output = this.output.toString().trim();
        Configuration configuration = this.loggingSystem.getConfiguration();
        assertThat(output).contains("Hello world").doesNotContain("Hidden");
        assertThat(new File(((tmpDir()) + "/spring.log")).exists()).isTrue();
        assertThat(configuration.getConfigurationSource().getFile()).isNotNull();
    }

    @Test
    public void testNonDefaultConfigLocation() {
        beforeInitialize();
        this.loggingSystem.initialize(null, "classpath:log4j2-nondefault.xml", getLogFile(((tmpDir()) + "/tmp.log"), null));
        this.logger.info("Hello world");
        String output = this.output.toString().trim();
        Configuration configuration = this.loggingSystem.getConfiguration();
        assertThat(output).contains("Hello world").contains(((tmpDir()) + "/tmp.log"));
        assertThat(new File(((tmpDir()) + "/tmp.log")).exists()).isFalse();
        assertThat(configuration.getConfigurationSource().getFile().getAbsolutePath()).contains("log4j2-nondefault.xml");
        assertThat(configuration.getWatchManager().getIntervalSeconds()).isEqualTo(30);
    }

    @Test
    public void testNonexistentConfigLocation() {
        beforeInitialize();
        assertThatIllegalStateException().isThrownBy(() -> this.loggingSystem.initialize(null, "classpath:log4j2-nonexistent.xml", null));
    }

    @Test
    public void getSupportedLevels() {
        assertThat(getSupportedLogLevels()).isEqualTo(EnumSet.allOf(LogLevel.class));
    }

    @Test
    public void setLevel() {
        beforeInitialize();
        initialize(null, null, null);
        this.logger.debug("Hello");
        this.loggingSystem.setLogLevel("org.springframework.boot", DEBUG);
        this.logger.debug("Hello");
        assertThat(StringUtils.countOccurrencesOf(this.output.toString(), "Hello")).isEqualTo(1);
    }

    @Test
    public void setLevelToNull() {
        beforeInitialize();
        initialize(null, null, null);
        this.logger.debug("Hello");
        this.loggingSystem.setLogLevel("org.springframework.boot", DEBUG);
        this.logger.debug("Hello");
        setLogLevel("org.springframework.boot", null);
        this.logger.debug("Hello");
        assertThat(StringUtils.countOccurrencesOf(this.output.toString(), "Hello")).isEqualTo(1);
    }

    @Test
    public void getLoggingConfigurations() {
        beforeInitialize();
        initialize(null, null, null);
        this.loggingSystem.setLogLevel(getClass().getName(), DEBUG);
        List<LoggerConfiguration> configurations = getLoggerConfigurations();
        assertThat(configurations).isNotEmpty();
        assertThat(configurations.get(0).getName()).isEqualTo(ROOT_LOGGER_NAME);
    }

    @Test
    public void getLoggingConfiguration() {
        beforeInitialize();
        initialize(null, null, null);
        this.loggingSystem.setLogLevel(getClass().getName(), DEBUG);
        LoggerConfiguration configuration = getLoggerConfiguration(getClass().getName());
        assertThat(configuration).isEqualTo(new LoggerConfiguration(getClass().getName(), LogLevel.DEBUG, LogLevel.DEBUG));
    }

    @Test
    public void setLevelOfUnconfiguredLoggerDoesNotAffectRootConfiguration() {
        beforeInitialize();
        initialize(null, null, null);
        LogManager.getRootLogger().debug("Hello");
        this.loggingSystem.setLogLevel("foo.bar.baz", DEBUG);
        LogManager.getRootLogger().debug("Hello");
        assertThat(this.output.toString()).doesNotContain("Hello");
    }

    @Test
    public void loggingThatUsesJulIsCaptured() {
        beforeInitialize();
        initialize(null, null, null);
        java.util.logging.Logger julLogger = getLogger(getClass().getName());
        julLogger.severe("Hello world");
        String output = this.output.toString().trim();
        assertThat(output).contains("Hello world");
    }

    @Test
    public void configLocationsWithNoExtraDependencies() {
        assertThat(getStandardConfigLocations()).contains("log4j2.xml");
    }

    @Test
    public void configLocationsWithJacksonDatabind() {
        this.loggingSystem.availableClasses(ObjectMapper.class.getName());
        assertThat(getStandardConfigLocations()).contains("log4j2.json", "log4j2.jsn", "log4j2.xml");
    }

    @Test
    public void configLocationsWithJacksonDataformatYaml() {
        this.loggingSystem.availableClasses("com.fasterxml.jackson.dataformat.yaml.YAMLParser");
        assertThat(getStandardConfigLocations()).contains("log4j2.yaml", "log4j2.yml", "log4j2.xml");
    }

    @Test
    public void configLocationsWithJacksonDatabindAndDataformatYaml() {
        this.loggingSystem.availableClasses("com.fasterxml.jackson.dataformat.yaml.YAMLParser", ObjectMapper.class.getName());
        assertThat(getStandardConfigLocations()).contains("log4j2.yaml", "log4j2.yml", "log4j2.json", "log4j2.jsn", "log4j2.xml");
    }

    @Test
    public void springConfigLocations() {
        String[] locations = getSpringConfigLocations(this.loggingSystem);
        assertThat(locations).isEqualTo(new String[]{ "log4j2-spring.xml" });
    }

    @Test
    public void exceptionsIncludeClassPackaging() {
        beforeInitialize();
        this.loggingSystem.initialize(null, null, getLogFile(null, tmpDir()));
        Matcher<String> expectedOutput = Matchers.containsString("[junit-");
        this.output.expect(expectedOutput);
        this.logger.warn("Expected exception", new RuntimeException("Expected"));
        String fileContents = contentOf(new File(((tmpDir()) + "/spring.log")));
        assertThat(fileContents).is(Matched.by(expectedOutput));
    }

    @Test
    public void beforeInitializeFilterDisablesErrorLogging() {
        beforeInitialize();
        assertThat(this.logger.isErrorEnabled()).isFalse();
        this.loggingSystem.initialize(null, null, getLogFile(null, tmpDir()));
    }

    @Test
    public void customExceptionConversionWord() {
        System.setProperty(EXCEPTION_CONVERSION_WORD, "%ex");
        try {
            beforeInitialize();
            this.logger.info("Hidden");
            this.loggingSystem.initialize(null, null, getLogFile(null, tmpDir()));
            Matcher<String> expectedOutput = Matchers.allOf(Matchers.containsString("java.lang.RuntimeException: Expected"), Matchers.not(Matchers.containsString("Wrapped by:")));
            this.output.expect(expectedOutput);
            this.logger.warn("Expected exception", new RuntimeException("Expected", new RuntimeException("Cause")));
            String fileContents = contentOf(new File(((tmpDir()) + "/spring.log")));
            assertThat(fileContents).is(Matched.by(expectedOutput));
        } finally {
            System.clearProperty(EXCEPTION_CONVERSION_WORD);
        }
    }

    @Test
    public void initializationIsOnlyPerformedOnceUntilCleanedUp() {
        LoggerContext loggerContext = ((LoggerContext) (LogManager.getContext(false)));
        PropertyChangeListener listener = Mockito.mock(PropertyChangeListener.class);
        loggerContext.addPropertyChangeListener(listener);
        beforeInitialize();
        initialize(null, null, null);
        beforeInitialize();
        initialize(null, null, null);
        beforeInitialize();
        initialize(null, null, null);
        Mockito.verify(listener, Mockito.times(2)).propertyChange(ArgumentMatchers.any(PropertyChangeEvent.class));
        cleanUp();
        beforeInitialize();
        initialize(null, null, null);
        Mockito.verify(listener, Mockito.times(4)).propertyChange(ArgumentMatchers.any(PropertyChangeEvent.class));
    }

    private static class TestLog4J2LoggingSystem extends Log4J2LoggingSystem {
        private List<String> availableClasses = new ArrayList<>();

        TestLog4J2LoggingSystem() {
            super(Log4J2LoggingSystemTests.TestLog4J2LoggingSystem.class.getClassLoader());
        }

        public Configuration getConfiguration() {
            return ((LoggerContext) (LogManager.getContext(false))).getConfiguration();
        }

        @Override
        protected boolean isClassAvailable(String className) {
            return this.availableClasses.contains(className);
        }

        private void availableClasses(String... classNames) {
            Collections.addAll(this.availableClasses, classNames);
        }
    }
}

