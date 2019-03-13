/**
 * Copyright 2012-2017 the original author or authors.
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


import LoggingSystemProperties.CONSOLE_LOG_PATTERN;
import LoggingSystemProperties.FILE_LOG_PATTERN;
import LoggingSystemProperties.PID_KEY;
import java.util.Set;
import org.junit.Test;


/**
 * Tests for {@link LoggingSystemProperties}.
 *
 * @author Andy Wilkinson
 */
public class LoggingSystemPropertiesTests {
    private Set<Object> systemPropertyNames;

    @Test
    public void pidIsSet() {
        apply(null);
        assertThat(System.getProperty(PID_KEY)).isNotNull();
    }

    @Test
    public void consoleLogPatternIsSet() {
        apply(null);
        assertThat(System.getProperty(CONSOLE_LOG_PATTERN)).isEqualTo("console pattern");
    }

    @Test
    public void fileLogPatternIsSet() {
        apply(null);
        assertThat(System.getProperty(FILE_LOG_PATTERN)).isEqualTo("file pattern");
    }

    @Test
    public void consoleLogPatternCanReferencePid() {
        apply(null);
        assertThat(System.getProperty(CONSOLE_LOG_PATTERN)).matches("[0-9]+");
    }

    @Test
    public void fileLogPatternCanReferencePid() {
        apply(null);
        assertThat(System.getProperty(FILE_LOG_PATTERN)).matches("[0-9]+");
    }
}

