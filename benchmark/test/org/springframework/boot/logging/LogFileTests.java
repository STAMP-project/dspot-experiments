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
package org.springframework.boot.logging;


import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;
import org.springframework.core.env.PropertyResolver;


/**
 * Tests for {@link LogFile}.
 *
 * @author Phillip Webb
 */
public class LogFileTests {
    @Test
    public void noProperties() {
        PropertyResolver resolver = getPropertyResolver(Collections.emptyMap());
        LogFile logFile = LogFile.get(resolver);
        assertThat(logFile).isNull();
    }

    @Test
    public void loggingFile() {
        PropertyResolver resolver = getPropertyResolver(Collections.singletonMap("logging.file.name", "log.file"));
        testLoggingFile(resolver);
    }

    @Test
    @Deprecated
    public void loggingFileWithDeprecatedProperties() {
        PropertyResolver resolver = getPropertyResolver(Collections.singletonMap("logging.file", "log.file"));
        testLoggingFile(resolver);
    }

    @Test
    public void loggingPath() {
        PropertyResolver resolver = getPropertyResolver(Collections.singletonMap("logging.file.path", "logpath"));
        testLoggingPath(resolver);
    }

    @Test
    @Deprecated
    public void loggingPathWithDeprecatedProperties() {
        PropertyResolver resolver = getPropertyResolver(Collections.singletonMap("logging.path", "logpath"));
        testLoggingPath(resolver);
    }

    @Test
    public void loggingFileAndPath() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("logging.file.name", "log.file");
        properties.put("logging.file.path", "logpath");
        PropertyResolver resolver = getPropertyResolver(properties);
        testLoggingFileAndPath(resolver);
    }

    @Test
    @Deprecated
    public void loggingFileAndPathWithDeprecatedProperties() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("logging.file", "log.file");
        properties.put("logging.path", "logpath");
        PropertyResolver resolver = getPropertyResolver(properties);
        testLoggingFileAndPath(resolver);
    }
}

