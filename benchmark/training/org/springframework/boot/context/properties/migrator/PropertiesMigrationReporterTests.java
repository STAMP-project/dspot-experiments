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
package org.springframework.boot.context.properties.migrator;


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;
import org.springframework.boot.configurationmetadata.SimpleConfigurationMetadataRepository;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.mock.env.MockEnvironment;


/**
 * Tests for {@link PropertiesMigrationReporter}.
 *
 * @author Stephane Nicoll
 */
public class PropertiesMigrationReporterTests {
    private ConfigurableEnvironment environment = new MockEnvironment();

    @Test
    public void reportIsNullWithNoMatchingKeys() {
        String report = createWarningReport(new SimpleConfigurationMetadataRepository());
        assertThat(report).isNull();
    }

    @Test
    public void replacementKeysAreRemapped() throws IOException {
        MutablePropertySources propertySources = this.environment.getPropertySources();
        PropertySource<?> one = loadPropertySource("one", "config/config-error.properties");
        PropertySource<?> two = loadPropertySource("two", "config/config-warnings.properties");
        propertySources.addFirst(one);
        propertySources.addAfter("one", two);
        assertThat(propertySources).hasSize(3);
        createAnalyzer(loadRepository("metadata/sample-metadata.json")).getReport();
        assertThat(mapToNames(propertySources)).containsExactly("one", "migrate-two", "two", "mockProperties");
        assertMappedProperty(propertySources.get("migrate-two"), "test.two", "another", getOrigin(two, "wrong.two"));
    }

    @Test
    public void warningReport() throws IOException {
        this.environment.getPropertySources().addFirst(loadPropertySource("test", "config/config-warnings.properties"));
        this.environment.getPropertySources().addFirst(loadPropertySource("ignore", "config/config-error.properties"));
        String report = createWarningReport(loadRepository("metadata/sample-metadata.json"));
        assertThat(report).isNotNull();
        assertThat(report).containsSubsequence("Property source 'test'", "wrong.four.test", "Line: 5", "test.four.test", "wrong.two", "Line: 2", "test.two");
        assertThat(report).doesNotContain("wrong.one");
    }

    @Test
    public void errorReport() throws IOException {
        this.environment.getPropertySources().addFirst(loadPropertySource("test1", "config/config-warnings.properties"));
        this.environment.getPropertySources().addFirst(loadPropertySource("test2", "config/config-error.properties"));
        String report = createErrorReport(loadRepository("metadata/sample-metadata.json"));
        assertThat(report).isNotNull();
        assertThat(report).containsSubsequence("Property source 'test2'", "wrong.one", "Line: 2", "This is no longer supported.");
        assertThat(report).doesNotContain("wrong.four.test").doesNotContain("wrong.two");
    }

    @Test
    public void errorReportNoReplacement() throws IOException {
        this.environment.getPropertySources().addFirst(loadPropertySource("first", "config/config-error-no-replacement.properties"));
        this.environment.getPropertySources().addFirst(loadPropertySource("second", "config/config-error.properties"));
        String report = createErrorReport(loadRepository("metadata/sample-metadata.json"));
        assertThat(report).isNotNull();
        assertThat(report).containsSubsequence("Property source 'first'", "wrong.three", "Line: 6", "none", "Property source 'second'", "wrong.one", "Line: 2", "This is no longer supported.");
        assertThat(report).doesNotContain("null").doesNotContain("server.port").doesNotContain("debug");
    }

    @Test
    public void durationTypeIsHandledTransparently() {
        MutablePropertySources propertySources = this.environment.getPropertySources();
        Map<String, Object> content = new LinkedHashMap<>();
        content.put("test.cache-seconds", 50);
        content.put("test.time-to-live-ms", 1234L);
        content.put("test.ttl", 5678L);
        propertySources.addFirst(new MapPropertySource("test", content));
        assertThat(propertySources).hasSize(2);
        String report = createWarningReport(loadRepository("metadata/type-conversion-metadata.json"));
        assertThat(report).contains("Property source 'test'", "test.cache-seconds", "test.cache", "test.time-to-live-ms", "test.time-to-live", "test.ttl", "test.mapped.ttl");
        assertThat(mapToNames(propertySources)).containsExactly("migrate-test", "test", "mockProperties");
        PropertySource<?> propertySource = propertySources.get("migrate-test");
        assertMappedProperty(propertySource, "test.cache", 50, null);
        assertMappedProperty(propertySource, "test.time-to-live", 1234L, null);
        assertMappedProperty(propertySource, "test.mapped.ttl", 5678L, null);
    }

    @Test
    public void reasonIsProvidedIfPropertyCouldNotBeRenamed() throws IOException {
        this.environment.getPropertySources().addFirst(loadPropertySource("test", "config/config-error-no-compatible-type.properties"));
        String report = createErrorReport(loadRepository("metadata/type-conversion-metadata.json"));
        assertThat(report).isNotNull();
        assertThat(report).containsSubsequence("Property source 'test'", "wrong.inconvertible", "Line: 1", ("Reason: Replacement key " + "'test.inconvertible' uses an incompatible target type"));
    }

    @Test
    public void invalidReplacementHandled() throws IOException {
        this.environment.getPropertySources().addFirst(loadPropertySource("first", "config/config-error-invalid-replacement.properties"));
        String report = createErrorReport(loadRepository("metadata/sample-metadata-invalid-replacement.json"));
        assertThat(report).isNotNull();
        assertThat(report).containsSubsequence("Property source 'first'", "deprecated.six.test", "Line: 1", "Reason", "No metadata found for replacement key 'does.not.exist'");
        assertThat(report).doesNotContain("null");
    }
}

