/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.actuate.logging;


import LogLevel.DEBUG;
import LogLevel.ERROR;
import LogLevel.FATAL;
import LogLevel.INFO;
import LogLevel.OFF;
import LogLevel.TRACE;
import LogLevel.WARN;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.actuate.logging.LoggersEndpoint.LoggerLevels;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;


/**
 * Tests for {@link LoggersEndpoint}.
 *
 * @author Ben Hale
 * @author Andy Wilkinson
 */
public class LoggersEndpointTests {
    private final LoggingSystem loggingSystem = Mockito.mock(LoggingSystem.class);

    @Test
    @SuppressWarnings("unchecked")
    public void loggersShouldReturnLoggerConfigurations() {
        BDDMockito.given(this.loggingSystem.getLoggerConfigurations()).willReturn(Collections.singletonList(new org.springframework.boot.logging.LoggerConfiguration("ROOT", null, LogLevel.DEBUG)));
        BDDMockito.given(this.loggingSystem.getSupportedLogLevels()).willReturn(EnumSet.allOf(LogLevel.class));
        Map<String, Object> result = loggers();
        Map<String, LoggerLevels> loggers = ((Map<String, LoggerLevels>) (result.get("loggers")));
        Set<LogLevel> levels = ((Set<LogLevel>) (result.get("levels")));
        LoggerLevels rootLevels = loggers.get("ROOT");
        assertThat(rootLevels.getConfiguredLevel()).isNull();
        assertThat(rootLevels.getEffectiveLevel()).isEqualTo("DEBUG");
        assertThat(levels).containsExactly(OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE);
    }

    @Test
    public void loggerLevelsWhenNameSpecifiedShouldReturnLevels() {
        BDDMockito.given(this.loggingSystem.getLoggerConfiguration("ROOT")).willReturn(new org.springframework.boot.logging.LoggerConfiguration("ROOT", null, LogLevel.DEBUG));
        LoggerLevels levels = loggerLevels("ROOT");
        assertThat(levels.getConfiguredLevel()).isNull();
        assertThat(levels.getEffectiveLevel()).isEqualTo("DEBUG");
    }

    @Test
    public void configureLogLevelShouldSetLevelOnLoggingSystem() {
        new LoggersEndpoint(this.loggingSystem).configureLogLevel("ROOT", DEBUG);
        Mockito.verify(this.loggingSystem).setLogLevel("ROOT", DEBUG);
    }

    @Test
    public void configureLogLevelWithNullSetsLevelOnLoggingSystemToNull() {
        configureLogLevel("ROOT", null);
        Mockito.verify(this.loggingSystem).setLogLevel("ROOT", null);
    }
}

