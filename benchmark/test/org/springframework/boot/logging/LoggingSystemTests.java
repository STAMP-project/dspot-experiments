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
package org.springframework.boot.logging;


import LoggingSystem.NONE;
import LoggingSystem.SYSTEM_PROPERTY;
import org.junit.Test;
import org.springframework.boot.logging.LoggingSystem.NoOpLoggingSystem;


/**
 * Tests for {@link LoggingSystem}.
 *
 * @author Andy Wilkinson
 */
public class LoggingSystemTests {
    @Test
    public void loggingSystemCanBeDisabled() {
        System.setProperty(SYSTEM_PROPERTY, NONE);
        LoggingSystem loggingSystem = LoggingSystem.get(getClass().getClassLoader());
        assertThat(loggingSystem).isInstanceOf(NoOpLoggingSystem.class);
    }

    @Test
    public void getLoggerConfigurationIsUnsupported() {
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> new org.springframework.boot.logging.StubLoggingSystem().getLoggerConfiguration("test-logger-name"));
    }

    @Test
    public void listLoggerConfigurationsIsUnsupported() {
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> new org.springframework.boot.logging.StubLoggingSystem().getLoggerConfigurations());
    }

    private static final class StubLoggingSystem extends LoggingSystem {
        @Override
        public void beforeInitialize() {
            // Stub implementation
        }

        @Override
        public void setLogLevel(String loggerName, LogLevel level) {
            // Stub implementation
        }
    }
}

